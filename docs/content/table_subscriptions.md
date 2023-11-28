# Table subscriptions

You should now have a good understanding of how we access cells and assign and read values from them. Let's move on to
events and how we can subscribe to them.

Subscriptions are how we can be notified of updates to cell data. If we have a subscription to a cell that has changed,
the event listener will be notified about this. From that we can perform further processing, and update other cells.
It's even possible to support circular references, but let's not get ahead of ourselves..

## Subscribing to events

How do we add a listener? Starting with a simple example, here we're implementing a basic sum function that takes two
cells and stores the sum of these in a third cell.

``` kotlin
val table = Table["MySum"]

on(table["A", 0] or table["A", 1]) events {
    if (table["A", 0].isNumeric && table["A", 1].isNumeric) {
        table["Sum", 2] = table["A", 0] + table["A", 1]
    }
}

table["A", 0] = 100
table["A", 1] = 123.123

val url = show(table)
println(url)
```

Starting with the `on` function, this takes cell references and executes our event listener. We can pass any type of
cell reference, such as `Table` (for listening to any table change), `Column` (for listening to a change within that
column), `Row` (for listening to changes in that row), and so on, as the first parameter to the `on` function.

However, in the above example, we're seeing something slightly different:

`on(table["A", 0] or table["A", 1])`

This is, as it indicates, allowing us to subscribe to multiple sources within one listener. This is not limited to
just cells, but could also have been, for example, `on(table["A"] or table["C"])`. Any combination is supported, as
long as they are within the same table.

Zooming out a bit, we find `on(source) events { .. }`. The code within the `{ .. }` block will be executed once per
change to the defined source. If we have sources that overlap, such as `on(table["A"] or table[1])`, where the two
sources share the cell as the intersection between column A and row 1, you need to be aware that the code within
`{ .. }` will be executed multiple times. For `on(table["A"] or table[1])` it would, if we were to assign to
`table["A", 1]`, result in two events. However, there are ways you can remove duplicate events if that's
something you don't want, and will show how later.

Before we look at the logic within our event listener, it's worth knowing that `on(source) events { .. }` is shorthand
for `on(source) { events { .. } }`. The purpose of the outer code block is to allow for initialization logic, which is
guaranteed to execute after the event listener has been added to the event processor, but before any events are
processed by the inner events code block.

We can expand our example to look at an example of us initializing some properties:

``` kotlin
on(table["A", 0] or table["A", 1]) {
    name = "My sum listener"
    order = 0
    skipHistory = false
    allowLoop = false

    events {
        if (table["A", 0].isNumeric && table["A", 1].isNumeric) {
            table["Sum", 2] = table["A", 0] + table["A", 1]
        }
    }
}
```

Here we are assigning four properties related to the event listener. The  `name` property is for your benefit, allowing
you to assign a name of your choice to the listener reference. If you have multiple listeners on a table, the `order`
property allows you to define in what order to execute these. Listeners with a lower order value are executed before
those with a higher value. If two listeners have the same `order`, they are executed in the order they were added.

Next we find `skipHistory`, which, if set to `true`, will cause the `events { .. }` code block to only be executed when
there are changes to the table. If `false` it will be executed once when adding the listener, and thereafter on any
changes.

Let's illustrate the behavior of `skipHistory` with two examples:

``` kotlin
table["A", 1] = "Value 1"

on(table["A", 1]) {
    skipHistory = false

    println("In init")

    events {
        println(table["A", 1])
    }
}

table["A", 1] = "Value 2"

// Output:
// In init
// Value 1
// Value 2
```

``` kotlin
table["A", 1] = "Value 1"

on(table["A", 1]) {
    skipHistory = true

    println("In init")

    events {
        println(table["A", 1])
    }
}

table["A", 1] = "Value 2"

// Output:
// In init
// Value 2
```

You will see that, when `skipHistory` is `true`, we don't get "Value 1" printed to the console, because only changes
following that are included.

Finally, what about `allowLoop` and the earlier statement about allowing for circular references?

In traditional spreadsheets, if you create a circular reference, directly or indirectly, where the calculations in a
cell end up depending on itself, you'll get an error. The dependency graph needs to be what's known as
directed acyclic graph.

The same is true in Sigbla, assuming `allowLoop` is `false`. But, if you want to, you have the option to allow for this.
You'll need to then take care to, at some point, break the loop, unless you want it to run forever. The point of having
this as an option is to ensure that, if you by accident create a loop, Sigbla will throw an exception should a loop
be detected. This protects you from applications that never return from a listener, unless you explicitly ask for it.

What would an example of such a loop be?

``` kotlin
on(table["A", 1]) {
    allowLoop = true

    events {
        println(table["A", 1])
        if (table["A", 1].isNumeric && table["A", 1] < 5) {
            table["A", 1] = table["A", 1] + 1
        }
    }
}

table["A", 1] = 1

println("Cell value is: ${table["A", 1]}")

// Output:
// 1
// 2
// 3
// 4
// 5
// Cell value is: 5
```

From this we can see that, when we assign 1 to the cell, the listener keeps changing the cell to what it currently is
plus 1. This reassignment triggers a new event, causing the process to repeat, as long as the value is below 5.

If we set `allowLoop` to `false`, we'd instead get an error:

`Exception in thread "main" sigbla.app.exceptions.ListenerLoopException: Listener loop detected on table listener: ..`

It's worth being aware that the default values for `name` is `null`, `order` is `0`, and both `allowLoop` and
`skipHistory` are `false`. You can also define these as parameters within the `on` function:

`on(source, name = "..", order = 0, allowLoop = false, skipHistory = false)`

## Events and TableListenerEvent

We've mentioned events a few times earlier, say things like "subscribing to events", but so far, we haven't seen any
actual use of these events. Instead, what we've been doing so far is to execute code within the events { .. } code block
that directly references the table.

There's nothing wrong with that approach if that's how you want to do it, but there's also an opportunity to get the
changed data from events. Let's change our last example to do just this:

``` kotlin
on(table["A", 1]) {
    allowLoop = true

    events {
        forEach {
            val newCell = it.newValue

            if (newCell.isNumeric && newCell < 5) {
                newCell { newCell + 1 }
            }
        }
    }
}

table["A", 1] = 1

println("Cell value is: ${table["A", 1]}")

// Output:
// Cell value is: 1
```

Within the events code block, `this` points to a sequence of `TableListenerEvent`, and for each cell update we will
receive one instance of that. This allows us to process these events using features of sequences, such as `forEach`.

So, the above example says that, for each event, run our logic to increase the cell value. On the `TableListenerEvent`
instance we have access to `newValue` as seen above, and, as you might guess, `oldValue`. This allows you to observe the
state of the cell before and after it was updated.

But hang on a second! It says the output is "Cell value is: 1". What? Didn't we run the above event loop till we've
incremented the cell value to 5? What's going on?

We are indeed updating the value of `newCell` to itself + 1, but what is `newCell`? Well, we can see it is obtained from
`it.newValue`, and if you print or debug the value of that, you will see it being a cell at `["A", 1]` with the value 1.
What `it.newValue` isn't is a reference to `["A", 1]` on our table. Instead, it's a reference to a clone of our table
as it existed after it was updated. Similarly, `it.oldValue` will be a reference to a cell at `["A", 1]` belonging to
a table that is a clone of our table as it existed prior to the change. As we know from earlier chapters, table clones
are disconnected from the original table, so changing `newCell` like we do above then obviously will not alter the
source.

How can we fix this for our example?

When calling the `on` function like we do above, the `{ .. }` that we're giving it has a function type with receiver of
`TableEventReceiver<Cell<*>, Any, Any>.() -> Unit`. This is using generics, and we can tell that we're calling this on
an `on` function that takes a cell as a source because of the `Cell<*>`. Had we instead done `on(table) { .. }` then the
declaration would be `TableEventReceiver<Table, Any, Any>.() -> Unit`, and so on for the other `on` functions. We'll get
back to the role of the two `Any` types later, but for now we can say they declare the type of the old and new cell value.

So how can we fix our issue and instead update the cell in our table? You could of course go back to simply referencing
the table we want to update directly, like we did initially. But this creates a direct connection between the listener
implementation and the table it's used on. We can avoid that by instead obtaining a reference to the source cell, the
one we pass in to `on(cell) ..`.

There exists a property called `source`, declared within `TableEventReceiver`, that
points to the source used when calling the `on` function. In our example, `source` points to the cell, but had we for
example been using `on(table)` then `source` would point to the table. The type of the source hence changes according
to the generics used.

Let's use `source` to fix the issue:

``` kotlin
on(table["A", 1]) {
    allowLoop = true

    events {
        forEach {
            val newCell = it.newValue

            if (newCell.isNumeric && newCell < 5) {
                source { newCell + 1 }
            }
        }
    }
}

table["A", 1] = 1

println("Cell value is: ${table["A", 1]}")

// Output:
// Cell value is: 5
```

A small change from `newCell { newCell + 1 }` to `source { newCell + 1 }` fixes the problem, as we're now assigning the
value to the source cell as we wanted to.

It's worth noting that `source` is not available when doing the shorthand form `on(source) events { .. }`.

## Filtering events by type

We saw earlier examples of `TableEventReceiver<Cell<*>, Any, Any>.() -> Unit` and `TableEventReceiver<Table, Any, Any>.() -> Unit`
and didn't go into any details about the role of the two `Any` types seen there. Let's do that now.

There exists forms of `on` that accept types, where these types are used to filter what type of values we get events
for. These take the form of `on<old type, new type>(source) { .. }` where the type represents the type of the value
stored in the cell. This has the added benefit of giving us better control of the type safety within the listener.

Let's modify the example to only receive events when the cell is assigned numbers:

``` kotlin
on<Any, Number>(table["A", 1]) {
    allowLoop = true

    events {
        forEach {
            val newCell = it.newValue

            if (newCell < 5) {
                source { newCell + 1 }
            }
        }
    }
}

table["A", 1] = 1

println("Cell value is: ${table["A", 1]}")

// Output:
// Cell value is: 5
```

We've made two changes here. Firstly, we've changed the call to the `on` function to be `on<Any, Number>(..)`. That
ensures that we only receive events when the value in the cell is of type `Number`. We still pass in an `Any`, because
we want events no matter what the old value type was. The second change is that we are no longer checking if the newCell
is numeric, because it's guaranteed to be numeric, so there's no need to worry about that.

We can put any combination of type filters, assuming cell are able to hold the type. Using a type not supported would
simply result in no events ever being dispatched, as they would all be filtered out. Empty cells contain a Unit value,
so you can use `on<Unit, Any>(..)` if you for example want to listen to already empty cells that are then assigned to.

Let's look at another example where we want to ensure that cells previously containing a long would continue doing so,
even if the new value was a string. We could imagine this being helpful if we accept input in string form but want to
parse these into longs.

``` kotlin
// Init the value, so we start with a type of Long
table["A", 1] = 1

on<Long, String>(table["A", 1]) {
    // We still need to allow loops, since we're modifying the source
    allowLoop = true

    events {
        forEach {
            source.table[source] = it.newValue.value.toLong()
        }
    }
}

// This will trigger the event
table["A", 1] = "100"

println("Cell value type is: ${table["A", 1].value?.javaClass}")

// Output:
// Cell value type is: class java.lang.Long
```

You'll notice we're assigning the long value with `source.table[source] = it.newValue.value.toLong()`, but doing
`source { it.newValue.value.toLong() }` would also work if you prefer.

It might make sense to apply such an operation across several cells, so let's finish with an example listening to all
the cells in column A:

``` kotlin
on<Long, String>(table["A"]) {
    // We still need to allow loops, since we're modifying the source
    allowLoop = true

    events {
        forEach {
            source.table[it.newValue] = it.newValue.value.toLong()
        }
    }
}
```

## Overlapping event sources

It was mentioned earlier that if the source you're subscribing to contain overlapping sources, such as
`on(table["A"] or table[1])` then you'd receive duplicate events both on init and during updates:

``` kotlin
val table = Table[null]

table["A", 1] = "A1"

on(table["A"] or table[1]) events {
    forEach {
        println(it.newValue)
    }
}

// Output:
// A1
// A1
```

If you wanted to avoid that, you could filter the sequence to only receive distinct events:

``` kotlin
on(table["A"] or table[1]) events {
    distinctBy {
        Pair(it.newValue.column, it.newValue.index)
    }.forEach {
        println(it.newValue)
    }
}

// Output:
// A1
```

You want to use `distinctBy` rather than `distinct` because we want to filter based on the location of the cells
rather than their content.

## Unsubscribing

At some point you might want to remove a listener by unsubscribing, which you do with the `off` function. When calling
the `on` function it will return a listener reference of type `TableListenerReference` with access to properties such
as `name`, `order`, `allowLoop`, and `skipHistory`.

``` kotlin
val table = Table[null]

val listenerReference = on(table) { .. }

off(listenerReference)
```

It's also possible to unsubscribe from within the listener logic itself as well, which is useful if you want to create
listeners that are able to manage their own lifecycle:

``` kotlin
on(table) {
    off(this)
}
```

Calling `off` within the init section of a listener might not make much sense at first, but often makes more sense
if used in combination with event processing. Here's an example that will unsubscribe itself after seeing 100
events:

``` kotlin
on(table) {
    var counter = 0

    events {
        counter += count()
        if (counter >= 100) {
            off(this@on)
        } else forEach { 
            // Do something with events..
        }
    }
}
```

## Listener ordering

You should now have a good understanding of how to make use of event listeners. One thing we haven't yet covered is the
role of ordering listeners. Because the execution order of a listener is defined through the order property, we can chain
these together, and let one pick up from where the other left off. This is where the fact that `it.newValue` and
`it.oldValue` point to table clones start becoming useful.

Let's start with an example where we first want to ensure the type of the input is a long before the second listener
calculates the difference between the old and new value:

``` kotlin
val table = Table[null]

// Init the value, so we start with a type of Long
table["A", 1] = 1

on<Long, String>(table["A"], order = 0) events {
    forEach {
        newTable[it.newValue] = it.newValue.value.toLong()
    }
}

on<Number, Number>(table["A"]) {
    order = 1

    events {
        forEach {
            source.table["B", it.newValue.index] = it.newValue - it.oldValue
        }
    }
}

// This will trigger the event
table["A", 1] = "100"

print(table)

// Output:
//     |A   |B   
// 1   |100 |99  
```

While defining the order here isn't strictly necessary since we're adding them in the order we want anyway, it's good
to ensure the right order explicitly in more complex code bases. We're also seeing something new called `newTable`.

Within `events { .. }` two properties called `newTable` and `oldTable` are made available that give you references to,
as the name indicates, tables containing the updated and previous versions of the source table. These are clones of the
original table, but what's more interesting for us is that any changes done to them become available to listeners
further down the chain. That's how the string to long conversion is handed over to the second listener, allowing it to
calculate the difference. The second listener isn't otherwise aware of the existence of any listeners before or after
itself. For all it knows, the old and new values always were numbers.

