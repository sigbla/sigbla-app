# View subscriptions

Subscribing to events on a table view (and related view classes) doesn't from an API point of view feel very different
from subscribing to events on a table. You'd use functions like `on` and `off`, with comparable patterns for events
with access to old and new views, etc.

But while table subscription is all about cells and the data they hold, view subscription is all about metadata and
the various types of metadata we can store on views.

## Metadata types and view hierarchy

Whenever metadata is updated on a view, if we subscribe to it, we get told about it. Below we list all the various
metadata types currently supported on a view.

| Type              | Subtypes                                         | Assignable on                            |
|-------------------|--------------------------------------------------|------------------------------------------|
| CellClasses       | N/A                                              | TableView, ColumnView, RowView, CellView |
| CellHeight        | UnitCellHeight, PixelCellHeight                  | TableView, RowView, CellView             |
| CellTopics        | N/A                                              | TableView, ColumnView, RowView, CellView |
| TableTransformer  | UnitTableTransformer, FunctionTableTransformer   | TableView                                |
| ColumnTransformer | UnitColumnTransformer, FunctionColumnTransformer | ColumnView                               |
| RowTransformer    | UnitRowTransformer, FunctionRowTransformer       | RowView                                  |
| CellTransformer   | UnitCellTransformer, FunctionCellTransformer     | CellView                                 |
| CellWidth         | UnitCellWidth, PixelCellWidth                    | TableView, ColumnView, CellView          |
| Resources         | N/A                                              | TableView                                |
| Table             | SourceTable <sup>*</sup>                         | TableView                                |

<sup>*</sup> SourceTable is not a subtype of Table, but instead a container of the table for event purposes.

With the above table in mind, if you have a table view, and you do `on(tableView) { .. }` and thereafter assign a
`CellHeight` to a `CellView`, this event will bubble up and trigger your listener. If instead you had a cell view and
did `on(cellView) { .. }` and thereafter assigned a `CellHeight` to a `RowView`, your listener would not trigger.

When a listener triggers will relate to the view hierarchy. A `TableView` is considered the top most root, with
`ColumnView` and `RowView` following, and finally `CellView` following those. Because the types are assignable on
various different targets, you get a more complex event sourcing situation than when compared to table subscriptions.

Hence, if you have a listener on a `RowView`, and an event is triggered on a `CellView` that relates to this row view,
that would trigger the listener on that row view. This, even if the metadata assigned was on the cell view and not on
the particular row view. Same for a related column view, and ultimately the table view. This is shown in the next example:

``` kotlin
val tableView = TableView[null]
val rowView = tableView[0]
val cellView = rowView["A"]

on(tableView) events {
    println("Have events on tableView: ${this.toList()}")
}

on(rowView) events {
    println("Have events on rowView: ${this.toList()}")
}

on(cellView) events {
    println("Have events on cellView: ${this.toList()}")
}

cellView[CellHeight] = 100

// Output:
// Have events on tableView: [TableViewListenerEvent(oldValue=, newValue=100)]
// Have events on rowView: [TableViewListenerEvent(oldValue=, newValue=100)]
// Have events on cellView: [TableViewListenerEvent(oldValue=, newValue=100)]

rowView[CellHeight] = 90

// Output:
// Have events on tableView: [TableViewListenerEvent(oldValue=, newValue=90)]
// Have events on rowView: [TableViewListenerEvent(oldValue=, newValue=90)]

tableView[CellHeight] = 80

// Output:
// Have events on tableView: [TableViewListenerEvent(oldValue=, newValue=80)]
```

## Filtering events by type

You might not be interested in all types when adding a listener, and can filter by type with `on<type>(source) { .. }`.
The particular type must be one of the types listed above, otherwise no event will ever be fired. There is no concept of
an old and new type like we have for tables, where we can do `on<old type, new type>(source) { .. }`, because a
`CellHeight` will always be a `CellHeight`, etc. It's not like on a table, where a cell might go from holding a `String`
to holding a `Long`.

Most of the types shown in the above table need some generic types defined. For example, if you only want to listen
to events for `CellHeight` you'd need to do `on<CellHeight<Any, Any>>(..)`. Trying to do just `on<CellHeight>(..)`
would result in a compilation error.

The inner types, here `<Any, Any>`, are not taken into account when filtering. It is recommended that you leave these
as `Any`, but if you are 100% certain about the types you receive you may define them more narrowly. If you get it
wrong you'll get a runtime class cast exception.

## Event source and related utilities

Because various types can be assigned to various targets, figuring out what the source of the event was wouldn't always
be obvious. A `CellHeight` can as seen above be assigned on many targets, such as `TableView`, `RowView` and `CellView`.

To aid in pinpointing the source, the types used in events all contain a source field. Let's look at an example of this:

``` kotlin
val tableView = TableView[null]
val rowView = tableView[0]
val cellView = rowView["A"]

on<CellHeight<Any, Any>>(tableView) events {
    forEach {
        val eventSource = it.newValue.source
        println("Got event from source type: ${eventSource::class.simpleName}")
    }
}

cellView[CellHeight] = 100

// Output:
// Got event from source type: CellView

rowView[CellHeight] = 90

// Output:
// Got event from source type: RowView

tableView[CellHeight] = 80

// Output:
// Got event from source type: TableView
```

The above example allows us to do `newValue.source`, because we specified that the `newValue` type must be a `CellHeight`
when doing `on<CellHeight<Any, Any>>(tableView)`, and `CellHeight` contains a source property. If we only did
`on(tableView)` we wouldn't have that luxury, as the type of `newValue` (and `oldValue`) would then be `Any`.

If you use event values without types, like `on(source)` without generics, there are a few utility functions that help
ease some typical operations, such as `sourceFromViewEventRelated`:

``` kotlin
val tableView = TableView[null]
val rowView = tableView[0]
val cellView = rowView["A"]

on(tableView) events {
    forEach {
        val eventSource = sourceFromViewEventRelated(it.newValue)
        println("Got event from source type: ${eventSource::class.simpleName}")
    }
}

cellView[CellHeight] = 100

// Output:
// Got event from source type: CellView

rowView[CellHeight] = 90

// Output:
// Got event from source type: RowView

tableView[CellHeight] = 80

// Output:
// Got event from source type: TableView
```

Other utility functions include `tableViewFromViewRelated`, returning the underlying `TableView`,
`columnViewFromViewRelated`, returning the underlying `ColumnView` (or null if not available), and
`indexFromViewRelated`, returning the underlying index value (or null if not available). These three utility functions
all allow for the full range of view related types to be used as input, not just event related types.

## Other event related functionality

Just like with events on a table, various other types of utility properties are available:

``` kotlin
val tableView = TableView[null]

on(tableView) {
    // Source is the source used in on(source)
    source

    events {
        // newView and oldView follow similar functionality
        // as newTable and oldTable do for table subscriptions
        newView
        oldView
    }
}
```

`oldView` and `newView` are clones of the view as it existed before and after the event took place. If you have
chained listeners, they will see updates to these just like chained table listeners see updates to `oldTable` and
`newTable`.

You also have options like `skipHistory`, `order`, and `name` on a view listener, with the same meaning as defined
for table listeners.
