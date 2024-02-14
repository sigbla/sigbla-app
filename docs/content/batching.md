# Batching

Sometimes you need to make multiple updates to a table, and only have other threads or event listeners see all those
changes in one go. You might have dependencies between multiple values, so you don't want anything to process those
values until you've had a chance to update all of them.

In the previous chapter on concurrency we saw how you could synchronize on the table so that no one could read from it
before you finished the update. This didn't cover listeners, so wouldn't work for all situations, something batching
solves.

## Atomic operations

Before moving on to the batching feature of tables, let's look at some operations that batch their updates. These
include functions like `move`, `copy`, and other functions that potentially mutate multiple table or table view data
points.

For example, if you move a column to another column, and that column contains several cells, all those cells are moved
atomically. Reader threads will either see the table as it existed prior to or after the operation. It will never see
some halfway through version of the table.

Let's look at this with an example:

``` kotlin
val table = Table[null]

table["A", 1] = "A1"
table["A", 2] = "A2"

on(table) {
    println("Listener init")
    events {
        println("Processing ${count()} events")
        forEach {
            println("Old value: ${it.oldValue.column}:${it.oldValue.index} = ${it.oldValue}")
            println("New value: ${it.newValue.column}:${it.newValue.index} = ${it.newValue}")
            println()
        }
    }
}

println("Before move")
move(table["A"] to table["B"])

// Output:
// Listener init
// Processing 2 events
// Old value: [A]:1 = 
// New value: [A]:1 = A1
//
// Old value: [A]:2 = 
// New value: [A]:2 = A2
//
// Before move
// Processing 4 events
// Old value: [A]:1 = A1
// New value: [A]:1 = 
//
// Old value: [A]:2 = A2
// New value: [A]:2 = 
//
// Old value: [B]:1 = 
// New value: [B]:1 = A1
//
// Old value: [B]:2 = 
// New value: [B]:2 = A2
```

A few things worth pointing out here:

* When the listener is added, we see that it receives 2 events. This means that the listener gets access to all the
  events needed to represent the full state of the table. The old value is blank because they are unit cells, while
  the new value represents the cells already filled in with values.
* Then we move column A to B, and the listener suddenly receives 4 events. These 4 events, as a batch, represent the
  state update performed by the move operation, first deleting the two existing cells before putting them into the
  new column.

If we were to implement a manual move operation, the listener would instead receive the 4 events as individual
updates, rather than all 4 at once. Depending on what the listener needs to do, that might work fine, but could also be
problematic if it needs to understand that all these cells moved together.

Let's see this manual move approach in action:

``` kotlin
val table = Table[null]

table["A", 1] = "A1"
table["A", 2] = "A2"

on(table) {
    println("Listener init")
    events {
        println("Processing ${count()} events")
        forEach {
            println("Old value: ${it.oldValue.column}:${it.oldValue.index} = ${it.oldValue}")
            println("New value: ${it.newValue.column}:${it.newValue.index} = ${it.newValue}")
            println()
        }
    }
}

println("Before move")
table["B", 1] = table["A", 1]
table["B", 2] = table["A", 2]
remove(table["A"])

// Output:
// Listener init
// Processing 2 events
// Old value: [A]:1 =
// New value: [A]:1 = A1
//
// Old value: [A]:2 =
// New value: [A]:2 = A2
//
// Before move
// Processing 1 events
// Old value: [B]:1 =
// New value: [B]:1 = A1
//
// Processing 1 events
// Old value: [B]:2 =
// New value: [B]:2 = A2
//
// Processing 2 events
// Old value: [A]:1 = A1
// New value: [A]:1 =
//
// Old value: [A]:2 = A2
// New value: [A]:2 =
```

As you'll notice, when performing the manual move, each update is individually shared with the listener.

## Batching table operations

Instead of receiving these operations as individual updates we can make use of table batching to group them together:

``` kotlin
val table = Table[null]

table["A", 1] = "A1"
table["A", 2] = "A2"

on(table) {
    println("Listener init")
    events {
        println("Processing ${count()} events")
        forEach {
            println("Old value: ${it.oldValue.column}:${it.oldValue.index} = ${it.oldValue}")
            println("New value: ${it.newValue.column}:${it.newValue.index} = ${it.newValue}")
            println()
        }
    }
}

println("Before move")

batch(table) {
    table["B", 1] = table["A", 1]
    table["B", 2] = table["A", 2]
    remove(table["A"])
}

// Output:
// Listener init
// Processing 2 events
// Old value: [A]:1 = 
// New value: [A]:1 = A1
//
// Old value: [A]:2 = 
// New value: [A]:2 = A2
//
// Before move
// Processing 4 events
// Old value: [B]:1 = 
// New value: [B]:1 = A1
//
// Old value: [B]:2 = 
// New value: [B]:2 = A2
//
// Old value: [A]:1 = A1
// New value: [A]:1 = 
//
// Old value: [A]:2 = A2
// New value: [A]:2 = 
```

As you can see from the output, we're now back to receiving 4 events at once during the manually implemented move operation.

The only change we did was to put these operations within `batch(table) { .. }`, which is how you initiate and define
what goes into the batch.

So what's going on?

Firstly, the operations put within the batch operate as a writer thread. That means the batch will synchronize against
any other thread trying to change the state of the table, and they'd need to wait for the batch to finish before they
can proceed.

Secondly, the thread that is running the batch will operate on its own view of the table. It's not quite comparable to
a clone because all existing event listeners are still included, something they wouldn't be on a table clone. It also
means that within the batch scope, you can see changes you so far made to the table. Only the "outside world" can not
yet see those changes.

Thirdly, all events are paused and rebased at the end of the batch. That means no listeners will trigger until after
we've done everything we want to do in the batch. Listeners otherwise work the same way, allowing for chaining and so on.

Finally, when the batch has finished, whatever updates it did are made available on the original table atomically. Any
thread reading from that table thereafter will see all updates performed as part of the batch. Should an exception
occur within the batch, all changes are discarded.

You'll notice we used the `table` reference within the batch:

``` kotlin
batch(table) {
    table["B", 1] = table["A", 1]
    table["B", 2] = table["A", 2]
    remove(table["A"])
}
```

We could instead have done like the below, which allows for more reusable batch functions, but is otherwise the same:

``` kotlin
batch(table) {
    this["B", 1] = this["A", 1]
    this["B", 2] = this["A", 2]
    remove(this["A"])
}
```

Another handy feature of batching is that we can return values from it. Imagine we wanted to create iterators for
two columns, and we need to ensure both column iterators are created for the same table state, without any risk
of another writer thread modifying something in between us creating those iterators.

One approach is of course to simply clone the table with `clone(table)` and then create iterators on the clone. This
works just fine and avoids us synchronizing, but you might want to combine creating the iterators with other batched
operations. Avoiding a clone also keeps those iterators connected to the original table. The next example shows this:

``` kotlin
val table = Table[null]

table["A", 1] = "A1"
table["A", 2] = "A2"
table["B", 1] = "B1"
table["B", 2] = "B2"

val (iterator1, iterator2) = batch(table) {
    this["A", 3] = "A3"
    this["B", 3] = "B3"

    listOf(this["A"].iterator(), this["B"].iterator())
}

iterator1.forEach {
    println(it)
}
iterator2.forEach {
    println(it)
}

// Output:
// A1
// A2
// A3
// B1
// B2
// B3
```

## Event rebasing

We said above that events are paused and rebased, but didn't define what rebasing means.

Firstly, rebasing refers to changing the table that events point to. We know it's possible to obtain a reference to
the table that `oldValue` and `newValue` point to when processing events. When rebasing, we ensure that `oldValue`
point to the table as it existed when the batch started. And similarly for `newValue`, we ensure that this points to
the table as it exists after all the batch operations are applied.

This is intuitively the behavior you'd expect, but is different to what you'd get if the events were only paused.

If events were only paused, the `oldValue` and `newValue` would point to intermediary tables as they existed halfway
through the batch. This would be both confusing and also contrary to the aim of batching, hence they are rebased.

A consequence of rebasing and pausing events is that you do not see duplicate events, or you might see some events that
look like a no-op event.

Here's an example of duplicates being removed:

``` kotlin
val table = Table[null]

on(table) events {
    forEach {
        println("Processing event: $it")
    }
}

batch(table) {
    this["A", 1] = "A1 v1"
    this["A", 1] = "A1 v2"
    this["A", 1] = "A1 v3"
}

// Output
// Processing event: TableListenerEvent(oldValue=, newValue=A1 v3)
```

You'll notice only the last update to the cell will be delivered to the listener. If we didn't remove the earlier
events you'd get 3 events, but, due to rebasing, they'd all contain the same last value assigned to that cell. It
hence makes sense to remove the earlier 2 events automatically.

Next, here's an example of no-op events:

``` kotlin
val table = Table[null]

table["A", 1] = "A1"

on(table, skipHistory = true) events {
    forEach {
        println("Processing event: $it")
    }
}

batch(table) {
    move(this["A"] to this["B"])
    move(this["B"] to this["C"])
}

// Output
// Processing event: TableListenerEvent(oldValue=A1, newValue=)
// Processing event: TableListenerEvent(oldValue=, newValue=)
// Processing event: TableListenerEvent(oldValue=, newValue=A1)
```

That second event above doesn't do anything, but is included because we touch column B during our move:

``` kotlin
batch(table) {
    move(this["A"] to this["B"])
    move(this["B"] to this["C"])
}
```

Even if it's a no-op, the event is included by default because a listener might benefit from seeing it. Even if they are
no-op events, they convey some information about what cells have been operated on. If you don't want no-op events in
your listener, simply filter them out:

``` kotlin
on(table, skipHistory = true) events {
    filter {
        it.oldValue != it.newValue
    }.forEach {
        println("Processing event: $it")
    }
}
```

## Batching between tables

There's nothing that prevents you from running a batch across multiple tables at the same time. It's fully supported and
a handy approach to ensuring consistent interaction between multiple tables:

``` kotlin
val t1 = Table[null]
val t2 = Table[null]

t1["A", 1] = 100
t1["A", 2] = 200

on(t1, skipHistory = true) events {
    println("Processing ${count()} events on T1")
    forEach {
        println("\t$it")
    }
}

on(t2) events {
    println("Processing ${count()} events on T2")
    forEach {
        println("\t$it")
    }
}

batch(t1) {
    batch(t2) {
        println("Batch start")

        t1["A"].forEach {
            t2[it] = it
        }

        t1["A", 1] = t1["A", 1] * 2
        t1["A", 2] = t1["A", 2] * 2

        println("Batch end")
    }
}

// Output:
// Batch start
// Batch end
// Processing 2 events on T2
//     TableListenerEvent(oldValue=, newValue=100)
//     TableListenerEvent(oldValue=, newValue=200)
// Processing 2 events on T1
//     TableListenerEvent(oldValue=100, newValue=200)
//     TableListenerEvent(oldValue=200, newValue=400)
```

It's also worth noting that if you batch on the same table within an existing batch on that table, this action is
flattened automatically. Doing a batch within a batch on the same table hence does nothing other than continuing the
current batch. That makes it safe to do batching within nested function calls, where the inner function call doesn't
need to know or care if a batch has already been started.

``` kotlin
val t1 = Table[null]

fun myUpdateFunction(table: Table) = batch(table) {
    this["A", 1] = this["A", 1] * 2
    this["A", 2] = this["A", 2] * 2
}

batch(t1) {
    this["A", 1] = 100
    this["A", 2] = 200

    myUpdateFunction(this)
}

print(t1)

// Output:
//     |A
// 1   |200
// 2   |400
```

## View batching

Table views support batching just like tables do. The approach is the same:

``` kotlin
val tableView = TableView[null]

on(tableView) events {
    println("Processing ${count()} events")
    forEach {
        println("\t${it.newValue::class.simpleName} on ${columnViewFromViewRelated(it.newValue)}:${indexFromViewRelated(it.newValue)} = ${it.newValue}")
    }
}

batch(tableView) {
    this[CellHeight] = 45
    this[CellHeight] = 40
    this[1][CellHeight] = 35
    this[1][CellHeight] = 30
    this["A"][CellWidth] = 140
    this["A"][CellWidth] = 130
}

// Output:
// Processing 3 events
//     PixelCellHeight on null:null = 40
//     PixelCellHeight on null:1 = 30
//     PixelCellWidth on [A]:null = 130
```