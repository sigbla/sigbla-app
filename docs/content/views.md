# Views

We've seen `TableView` in action already, but we haven't yet explained it in much detail. We'll start doing that now.

The purpose of a table view is to describe how you'd want to display your table. As has been mentioned already,
separating your data from its view helps create more maintainable applications, and it also allows you to attach
several different views to the same data.

## Creating views

You can create a `TableView` by providing it a `Table`. This makes the created view belong to the provided table.
But you can also create views without tying it to a table, and then later on bind the view to a table. This also allows
you to change which table the view binds to later on, handy for when you want to swap out the viewed data.

Just like there's a table registry, there is a view registry. Overall, the APIs provided by `TableView` should feel
familiar as they follow the same patterns as those used on `Table`. A `TableView` however is still very different from
`Table`, as the view describes how to display data rather than holding data.

The boundary between what is data and what is metadata can sometimes be unclear. One might think that the order of
columns should be something a view could dictate. But the order of columns is defined in the table, as it is considered
part of the data structure, rather than metadata. This is similar to how the order of rows is defined by their index,
which is a property of cells on tables.

Let's continue with an example of some of the features of `TableView`:

``` kotlin
// Create a TableView that's not tied to any Table
val tableView = TableView[null]

// Set the width of column B
tableView["B"][CellWidth] = 100

// Set the height of row 1
tableView[1][CellHeight] = 10

// Set height and width on a particular cell
tableView["C", 2][CellHeight] = 20
tableView["C", 2][CellWidth] = 80
```

Cells can have a height and a width. These can either be defined for a particular cell or across all cells within a
column or a row. Rows don't have a width, so doing `tableView[1][CellWidth]` isn't possible. Similarly, columns don't
have a height, thus `tableView["A"][CellHeight]` also isn't possible.

The numbers provided above represent pixel height and width when displaying the table. Let's look at the type of these
when reading from a `TableView`:

``` kotlin
val tableView = TableView[null]

// Read from an empty table view:
val cellHeight1 = tableView[1][CellHeight]
val cellWidth1 = tableView["B"][CellWidth]

println(cellHeight1::class)
println(cellWidth1::class)

// Output:
// class sigbla.app.UnitCellHeight
// class sigbla.app.UnitCellWidth

// Add some values to these
tableView[1][CellHeight] = 10
tableView["B"][CellWidth] = 100

val cellHeight2 = tableView[1][CellHeight]
val cellWidth2 = tableView["B"][CellWidth]

println(cellHeight2::class)
println(cellWidth2::class)

// Output:
// class sigbla.app.PixelCellHeight
// class sigbla.app.PixelCellWidth
```

Much like we had UnitCell from an empty cell on a table, we find a similar pattern for a table view. When not defined,
the cell height is represented by the `UnitCellHeight`, but when set, we get a `PixelCellHeight`. We see `UnitCellWidth`
and `PixelCellWidth` for width.

What happens at the intersection between column B and row 1 above? Let's find out:

``` kotlin
val tableView = TableView[null]

tableView[1][CellHeight] = 10
tableView["B"][CellWidth] = 100

val cellView = tableView["B", 1]
val cellHeight = cellView[CellHeight]
val cellWidth = cellView[CellWidth]

println(cellHeight::class)
println(cellWidth::class)

// Output:
// class sigbla.app.UnitCellHeight
// class sigbla.app.UnitCellWidth
```

For the particular cell at `["B", 1]`, you'll notice that the height and width remain undefined, giving us `UnitCellHeight`
and `UnitCellWidth`, even after defining height and width on the relevant row and column. The height and width are defined
separately on a cell view, row view and column view. If we were to set the height and width on the cell itself, then our
`cellHeight` and `cellWidth` would become `PixelCellHeight` and `PixelCellWidth` instead:

``` kotlin
val tableView = TableView[null]

tableView["B", 1][CellHeight] = 10
tableView["B", 1][CellWidth] = 100

val cellView = tableView["B", 1]
val cellHeight = cellView[CellHeight]
val cellWidth = cellView[CellWidth]

println(cellHeight::class)
println(cellWidth::class)

// Output:
// class sigbla.app.PixelCellHeight
// class sigbla.app.PixelCellWidth
```

## Derived cell views

Being able to define height and width on an individual cell allows us to override any settings defined on its column and
row. Height and width on a cell hence take precedence over those defined on more overarching structures.

You might not want to first read the width of a cell and then move on to the column if undefined when working out a
particular cell width, as that would be a bit tedious. You can instead make use of what's known as a `DerivedCellView`:

``` kotlin
val tableView = TableView[null]

tableView[1][CellHeight] = 30
tableView["B"][CellWidth] = 120

tableView["B", 1][CellHeight] = 40
tableView["B", 1][CellWidth] = 150

val cellView1 = tableView["B", 1]
val derivedCellView1 = cellView1.derived

println("Cell height at [\"B\", 1]: ${derivedCellView1.cellHeight}")
println("Cell width at [\"B\", 1]: ${derivedCellView1.cellWidth}")

// Output:
// Cell height at ["B", 1]: 40
// Cell width at ["B", 1]: 150

val derivedCellView2 = tableView["B", 2].derived

println("Cell height at [\"B\", 2]: ${derivedCellView2.cellHeight}")
println("Cell width at [\"B\", 2]: ${derivedCellView2.cellWidth}")

// Output:
// Cell height at ["B", 2]: 20
// Cell width at ["B", 2]: 120

val derivedCellView3 = tableView["A", 1].derived

println("Cell height at [\"A\", 1]: ${derivedCellView3.cellHeight}")
println("Cell width at [\"A\", 1]: ${derivedCellView3.cellWidth}")

// Output:
// Cell height at ["A", 1]: 30
// Cell width at ["A", 1]: 100

val derivedCellView4 = tableView["A", 2].derived

println("Cell height at [\"A\", 2]: ${derivedCellView4.cellHeight}")
println("Cell width at [\"A\", 2]: ${derivedCellView4.cellWidth}")

// Output:
// Cell height at ["A", 2]: 20
// Cell width at ["A", 2]: 100
```

The `DerivedCellView` instance provides us with access to all the settings applied when viewing a particular
cell by combining everything that is defined within the TableView and related entities. It's a fully immutable view
of these definitions as they existed when creating the instance.

``` kotlin
val derivedCellView = TableView[null]["A", 1].derived

val cell: Cell<*> = derivedCellView.cell
val cellHeight: Long = derivedCellView.cellHeight
val cellWidth: Long = derivedCellView.cellWidth
val cellView: CellView = derivedCellView.cellView
val columnView: ColumnView = derivedCellView.columnView
val index: Long = derivedCellView.index
val tableView: TableView = derivedCellView.tableView
val cellClasses: CellClasses<DerivedCellView> = derivedCellView.cellClasses
val cellTopics: CellTopics<DerivedCellView> = derivedCellView.cellTopics
```

Height and width are worked out by taking the most specific cell view values applicable to the relevant cell as outlined
next.

Rules for cell height:

* If the height is defined on the relevant `CellView`, use this
* Otherwise, if the height is defined on the relevant `RowView`, use this
* Otherwise, if the height is defined on the relevant `TableView`, use this
* Finally, if not defined anywhere, use a fixed default of 20 pixels

The fixed default is defined on `sigbla.app.DEFAULT_CELL_HEIGHT`, which means all cells must have a height.

For width, the rules are comparable:

* If the width is defined on the relevant `CellView`, use this
* Otherwise, if the width is defined on the relevant `ColumnView`, use this
* Otherwise, if the width is defined on the relevant `TableView`, use this
* Finally, if not defined anywhere, use a fixed default of 100 pixels

The fixed default is defined on `sigbla.app.DEFAULT_CELL_WIDTH`, which means all cells must have a width.

We've seen above how we can set the height and width on cell views, row views, and column views, but not on the
`TableView`. It should come as no surprise how this is defined:

``` kotlin
val tableView = TableView[null]

tableView[CellHeight] = 30
tableView[CellWidth] = 200
```

As with the index on a `Cell`, convenience functions allow you to define height and width using `Int`, but internally
they are all stored as a `Long`.

Coming back to the `DerivedCellView` properties, you'll notice you have access to the `Cell` it represents. The cell
returned includes any cell transformations as covered below.

There's also something called `CellClasses` and `CellTopics`, which we'll come back to in the chapter on view
extensions. While topics require a bit more context, we can say classes contain CSS classes we want to apply to the
cell itself when viewed in a web browser. These are different to the classes we applied to `WebContent` on a `WebCell`
as the `CellClasses` apply to the cell container itself, not its content.

We'll also get back to the generics used on `CellClasses` and `CellTopics` in the chapter on view subscriptions.

## Iterating over derived cell views

On `TableView`, `ColumnView`, `RowView`, `CellView` and so on, you're able to create iterators that give you all the derived cell
views on these. However, please note that these iterators only produce derived cell views for those that have
corresponding cells in the connected table.

The next example illustrates this:

``` kotlin
fun assert(value: Boolean) {
    if (!value) { throw AssertionError() }
}

val table1 = Table["T1"]
val table2 = Table["T2"]

val tableView = TableView[null]
tableView["A"][CellWidth] = 200
tableView[1][CellHeight] = 25

// This iterator does not give us any derived cell views
// because we don't have any underlying cells
val emptyIterator = tableView.iterator()
assert(!emptyIterator.hasNext())

table1["A", 1] = 100

// Associate the view with table1
tableView[Table] = table1

val iterator1 = tableView.iterator()
iterator1.forEach {
    println(it)
}

// Output:
// DerivedCellView(columnView=[A], index=1, cellHeight=25, cellWidth=200, tableView=TableView[null], cellView=[A]:1, cell=100, cellClasses=[], cellTopics=[])

// Swapping the table associated with the view will then
// create iterators based on the cells in table2
table2["B", 2] = 200
tableView[Table] = table2

val iterator2 = tableView.iterator()
iterator2.forEach {
    println(it)
}

// Output:
// DerivedCellView(columnView=[B], index=2, cellHeight=20, cellWidth=100, tableView=TableView[null], cellView=[B]:2, cell=200, cellClasses=[], cellTopics=[])
```

## Hiding columns and rows

You might want to hide some columns or rows. The next example shows how we can hide the first row and column from the table view:

``` kotlin
val t = Table["Hide"]

t["A", 0] = "A0"
t["A", 1] = "A1"
t["B", 0] = "B0"
t["B", 1] = "B1"
t["C", 0] = "C0"
t["C", 1] = "C1"

val tableView = TableView[t]

tableView["A"][Visibility] = Visibility.Hide
tableView[0][Visibility] = Visibility.Hide

val url = show(tableView)
println(url)
```

![Hide column and row](img/views_hide_column_row.png)

As can be seen, while column A and row 0 contains data, it is not shown in the view because they were hidden.

It's also possible to assign a `Visibility.Show` value, but the default behavior is to show any column and row that has
not been hidden with `Visibility.Hide`. If you wanted to change this default behavior to only show those columns or rows
that have explicitly been set to be visible you'd need to provide a customized view configuration to the show function.

We'll dive into the view configuration in more details later, but here's a quick preview:

``` kotlin
val t = Table["Show"]

t["A", 0] = "A0"
t["A", 1] = "A1"
t["B", 0] = "B0"
t["B", 1] = "B1"
t["C", 0] = "C0"
t["C", 1] = "C1"

val tableView = TableView[t]

tableView["A"][Visibility] = Visibility.Show
tableView[0][Visibility] = Visibility.Show

val url = show(tableView, config = compactViewConfig(
    defaultColumnVisibility = Visibility.Hide,
    defaultRowVisibility = Visibility.Hide
))

println(url)
```

![Show column and row](img/views_show_column_row.png)

You'll notice we're able to define the visibility behavior on either the column or row separately allowing for different
default behavior between them.

## Locking columns and rows

It is often helpful to lock certain columns or rows so that they are always in view, even if we scroll within a table
that otherwise contains more information that what can be shown at once. You'd do that with `Position` and we'll start
with an example that locks the first column to the left side of the view:

``` kotlin
val table = Table["Lock"]
val tableView = TableView[table]

for (label in listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T")) {
    for (index in 0..1000) {
        table[label, index] = "$label $index"
    }
}

// Lock column A to left position
tableView["A"][Position] = Position.Left

val url = show(tableView)
println(url)
```

![Lock column to left position](img/views_position_left.png)

In a similar fashion you can lock the column to the right position with:

``` kotlin
tableView["A"][Position] = Position.Right
```

And rows can be locked to the top or bottom position with:

``` kotlin
tableView[1][Position] = Position.Top
tableView[2][Position] = Position.Bottom
```

When multiple columns are locked to the same position, they are ordered according to their table order, for example:

``` kotlin
tableView["A"][Position] = Position.Left
tableView["B"][Position] = Position.Left

tableView["C"][Position] = Position.Right
tableView["D"][Position] = Position.Right
```

![Lock multiple columns to left and right](img/views_multiple_column_positions.png)

And for rows it would look like this:

``` kotlin
tableView[1][Position] = Position.Top
tableView[2][Position] = Position.Top

tableView[3][Position] = Position.Bottom
tableView[4][Position] = Position.Bottom
```

![Lock multiple rows to top and bottom](img/views_multiple_row_positions.png)

If we lock both columns and rows at the same time, we would lock the intersecting cells in both the horizontal and
vertical position, meaning those cells would never scroll out of view:

``` kotlin
tableView["A"][Position] = Position.Left
tableView["B"][Position] = Position.Left

tableView[1][Position] = Position.Top
tableView[2][Position] = Position.Top
```

![Lock multiple columns and rows together](img/views_multiple_column_and_row_positions.png)

## Transformations

A table view allows us to define something called transformers. A transformer allows us to change how a view is presented.
It doesn't in any way change the original underlying table, but potentially transforms what cells to display and how.

Back when introducing `WebCell` in the chapter on cells, we said it was somewhat problematic to embed HTML content
directly into a table for the purposes of changing how the content is presented, as it ties the data with the view.
Transformers allow us to break this link.

There are transformers at different levels of the hierarchy. You can on a view have table, column, row, and cell transformers.
They also act in that order, with the table transformer running first, followed by column transformers, row transformers,
and finally cell transformers.

We can with a transformer take cell values and transform them into whatever we want, which could include HTML
content for the purpose of elevating or changing how data is viewed, again without this impacting the original cell data.

Here's an example that changes the text color to red if the number is negative, otherwise green:

``` kotlin
import sigbla.app.*
import kotlinx.html.*

fun main() {
    val table = Table["Transformers"]
    val tableView = TableView[table]

    table["A", 0] = 100
    table["A", 1] = -100

    table["A"].forEach {
        tableView[it][CellTransformer] = cellTransformer
    }

    val url = show(tableView)
    println(url)
}

val cellTransformer: Cell<*>.() -> Unit = {
    if (isNumeric) {
        table[this] = if (this > 0) div {
            p {
                style = "color: green; text-align: right; padding-right: 5px"
                +value.toString()
            }
        } else div {
            p {
                style = "color: red; text-align: right; padding-right: 5px"
                +value.toString()
            }
        }
    }
}
```

This is, as explained for the `WebCell` in the chapter on cells using `kotlinx.html` so an `import kotlinx.html.*` is
recommended to ensure all tags are available for ease of use.

![Cell transformers in action](img/views_cell_transformers.png)

The definition of a cell transformer is `CellTransformer<T>`, which is a class that comes with two concrete
implementations, `UnitCellTransformer` and `FunctionCellTransformer`. In the example above we defined a function to
perform the transformation, which would cause us to obtain a reference to a `FunctionCellTransformer` should we after
adding it do a `tableView[ref][CellTransformer]`, with ref being one of the possible references, such as a `CellView`.
If no transformer was defined on the reference, we'd obtain a `UnitCellTransformer`, indicating no transformation.

The type T on FunctionCellTransformer is `Cell<*>.() -> Unit`, indicating that the function is given a cell on which it
can operate.

In the above example we used cell transformers, and we assigned these cell transformers directly to the cells we wanted
to transform. We might not know in advance what cells we want to transform, or we might want to apply a transformation
across many cells. The next example shows us doing this on the whole table:

``` kotlin
import sigbla.app.*
import kotlinx.html.*

fun main() {
    val table = Table["Transformers"]
    val tableView = TableView[table]

    table["A", 0] = 100
    table["A", 1] = -100

    tableView[TableTransformer] = tableTransformer

    val url = show(tableView)
    println(url)
}

val tableTransformer: Table.() -> Unit = {
    forEach { it.cellTransformer() }
}

val cellTransformer: Cell<*>.() -> Unit = {
    if (isNumeric) {
        table[this] = if (this > 0) div {
            p {
                style = "color: green; text-align: right; padding-right: 5px"
                +value.toString()
            }
        } else div {
            p {
                style = "color: red; text-align: right; padding-right: 5px"
                +value.toString()
            }
        }
    }
}
```

Using a column transformer, we can limit the transformation to just the defined column:

``` kotlin
val columnTransformer: Column.() -> Unit = {
    forEach { it.cellTransformer() }
}

tableView["A"][ColumnTransformer] = columnTransformer
```

And similarly with a row transformer on just the defined row:

``` kotlin
val rowTransformer: Row.() -> Unit = {
    forEach { it.cellTransformer() }
}

tableView[1][RowTransformer] = rowTransformer
```

When using `forEach` above we iterate over cells with values, but there's nothing stopping a transformer to operate
on cells that don't exist in the source table. This allows a transformer to fill in empty cells if that's what you need.

You can obtain a `Table` instance from a table view that apply all the transformations defined by calling
`tableView[Table]`:

``` kotlin
val table = Table["Transformers"]
val tableView = TableView[table]

table["A", 0] = 100
table["A", 1] = -100

table["A"].forEach {
    tableView[it][CellTransformer] = {
        if (isNumeric) this.table[this] = this * 2
    }
}

val transformedTable = tableView[Table]

print(transformedTable)

// Output
//      |A    
// 0    |200  
// 1    |-200 
```

The returned table, `transformedTable`, is a clone of the table tied to the view, here `table`, with the transformations
applied. If no table was tied to the view, then an empty table would have been returned.

This makes views somewhat useful outside just UIs, as they can be used to apply transformation for data purposes. In
any case it helps with debugging and testing UI transformations as you gain access to what the UI would receive.

## Saving, cloning and registry

Back in the chapter on tables we said tables can be saved and loaded to disk using the `save` and `load` functions. The
same is the case for a table view, with functions taking the same type of parameters and operating in a similar
manner. Cell transformers are considered source code, and belong to source code files, hence not saved with the `save`
function.

When saving a table, it will use the file extension `sigt`, while views will use `sigv` as their default extension.

The table view can also be cloned using the `clone` function. As we'll cover later, you can have listeners on a table
view, and these listeners are, as with the table, not cloned when cloning a view. The use of transformers on the
other hand is included in the cloned view, sharing the same instance references.

Also, much like on tables, table views also have a registry. The behaviour is comparable to that of tables.

## Interacting with the UI

By default, when doing `show(tableView)`, Sigbla will pick a random port. It's a random port because there's an
assumption that you might run multiple Sigbla apps at the same time, and a random port helps avoid port collisions.

However, you might want to control the port used yourself and there are two ways to do that. You can, as shown in the
example, do `TableView[Port] = <port number>`. Or, you can define an environment variable called `SIGBLA_PORT`.

The port can only be set once, with any subsequent attempts ignored. If the environment variable is defined, this will
take precedence over any port set by the source code.

You may also define the host using `TableView[Host] = "hostname or IP"` or with an environment variable called
`SIGBLA_HOST`. Just like with the port, this can only be set once. If not set, it will default to `127.0.0.1`.

```
TableView[Host] = "localhost"
TableView[Port] = 8080

println("Using host ${TableView[Host]}")
println("Using port ${TableView[Port]}")

val table = Table["Table"]

table["A", 0] = 100
table["B", 0] = "B0"

val tableView = TableView[table]
val url = show(tableView)
println(url)
```

Once you have the UI for a table opened in a browser, you are able to interact with some of the table view metadata.
Specifically, you are able to adjust the height and width as shown below.

![Resizing columns and rows from the UI](img/views_ui_resize.png)

This updates the metadata just as if you did it from the code, interacting with the particular column or row being
adjusted. Hence, any such activity will trigger events, and if you don't want the user to be able to adjust any row or
column of your choice, you can simply override the action from a listener, setting it to whatever value you pick. The
listener and event topics will be covered in later chapters.

## The cell marker

If you click on a cell it will become marked. When the marker is present, you may move it around with your allow keys,
and also use tab to move right or enter to move down. Whatever cell is marked will receive user keyboard input,
something that will be explored in more detail in the [view extensions](view_extensions.md) chapter.

Double clicking a cell will select the cell, and expose the cell content. An exposed cell will be able to receive all
input, including mouse events.

The below video shows us clicking on a cell to expose the marker, using arrow keys to move it right and finally double
clicking it to expose the underlying content.

![Interacting with the cell marker](img/views_cell_marker.png)

## View configuration and related properties

You might have noticed in the above example that the URL is localhost:8080/t/Table/. While we've covered how to set the
host and port, the Table in the URL is taken from the name of the table when it was created with `Table["Table"]`.
Also, the title of the HTML page is set to the same.

You might not want this, and might want to separate the URL from the HTML title, and even keep them different from the
name of the table in your code. Let's start with the URL.

You can define the last part of the URL by defining what's known as a `ref` as shown next:

``` kotlin
TableView[Host] = "localhost"
TableView[Port] = 8080

val table = Table["Table"]
val tableView = TableView[table]

val url = show(tableView, ref = "my-own-ref")
println(url)
```

Running this and the URL will now be localhost:8080/t/my-own-ref/ which is what we wanted. But if you open it you'll
also see that the title has been set to "my-own-ref". If you also wanted to define a title you will need to define
the view config. Here's an example of this:

``` kotlin
TableView[Host] = "localhost"
TableView[Port] = 8080

val table = Table["Table"]
val tableView = TableView[table]

val url = show(tableView, ref = "my-own-ref",
                config = compactViewConfig(title = "My own title"))
println(url)
```

You'll notice that in `show(tableView, ref = "my-own-ref", config = compactViewConfig(title = "My own title"))` we use
something called `compactViewConfig(..)` to generate an instance of `ViewConfig` on which we set the title. Within
`ViewConfig` there are many parameters we can define, and we'll dive into that in more details in later chapters when
looking at extending views.

However, for now, be aware that there's two default view configs provided, as defined by `compactViewConfig(..)` and
`spaciousViewConfig(..)` providing two different styles for viewing a table. Here's an example of that:

``` kotlin
TableView[Host] = "localhost"
TableView[Port] = 8080

val table = Table["Table"]
val tableView = TableView[table]

table["A", 0] = "A 0"
table["A", 1] = "A 1"
table["B", 0] = "B 0"
table["B", 1] = "B 1"

val url1 = show(tableView, ref = "compact", config = compactViewConfig())
val url2 = show(tableView, ref = "spacious", config = spaciousViewConfig())

println(url1)
println(url2)
```

While the table and table view is shared between them, the way they look in the browser is different.

Here's the compact variant next to the spacious variant:

![Compact vs spacious view configs](img/views_viewconfig_compact_vs_spacious.png)

Being able to pass in a `ViewConfig` gives you the ability to fully customize what HTML, CSS and JavaScript is used
by the frontend, enabling you to build whatever works for your needs.