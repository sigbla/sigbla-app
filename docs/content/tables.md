# Tables

At the core of Sigbla we find the `Table`. This is where we store the majority of data used by Sigbla apps, only pulling
data out of the table when we're doing processing on it.

With the use of subscriptions, we might not even need to pull the data out of the table when processing it, but rather
simply let the data come to us. This allows us to react to data updates, and ensure that whatever processing we need to
perform is always done in sync with its input. If the input is updated, processing will kick off automatically.

We'll save subscriptions for a later chapter. Let's instead focus on how data in a table is structured and how we would
access it.

## Using tables

Much like any spreadsheet, a table is a collection of cells. These cells are placed within a column and a row based grid.
The row in `Table` is comparable to a row in any spreadsheet. It's simply an index, and the table uses `Long` as the
index type.

Through convenience methods, you can also address a particular row using the `Int` type, assuming the index fits in that
type. Internally however, all row indexes are of type `Long`.

Columns are also similar to any other spreadsheet, as they are described with strings of text. However, contrary to what
you might be used to, we are free to assign any string for a column. In other words, we are not forced to use "A", "B",
"C" and so on as column labels.

Let's summarize the above with an examples:

``` kotlin
import sigbla.app.*

// We create a new table without any name by passing null as the name parameter
val table = Table[null]

// We assign values to cells in the table
table["A", -100] = "Hidden value"
table["Another column", 200L] = "Visible value"
```

You'll notice in the example that we assign two strings to two separate cells. The first cell is in a column named A at
a negative row index. Negative row indexes are like any other row index, but if we view the table in the UI (using a
`TableView`) only those rows from zero onwards are visible. This is helpful as a hidden area for temporary or
intermediate values, for example.

The second assignment clearly illustrates that columns can have names we couldn't use in traditional spreadsheets, and
also show how we can use a `Long` as the type for the row.

Moving on from this, another feature of columns in Sigbla is that they can have nested labels. This is helpful when we
want to describe more complex data, especially data that can be assumed hierarchical.

``` kotlin
table["Label A", "Label A1", 0] = 1000
table["Label A", "Label A2", 0] = 2000
```

There's no hardcoded limit to how many nested labels a column can have, and convenience methods allow for easy access.
The below examples all point to the same cell.

``` kotlin
table["A", 0] = "Cell A0"
table["A"][0] = "Cell A0"
table[0]["A"] = "Cell A0"
```

Using the first approach (`table["A", 0]`) you can address columns up to 5 labels deep.
Should you need more you can use the `vararg` options (second and third approach), or you can make use of the
`Header` class directly.

``` kotlin
// This works
table["L1", "L2", "L3", "L4", "L5", 1] = "Cell value"

// This does NOT work
table["L1", "L2", "L3", "L4", "L5", "L6", 1] = "Cell value"

// These make use of varargs
table["L1", "L2", "L3", "L4", "L5", "L6"][1] = "Cell value"
table[1]["L1", "L2", "L3", "L4", "L5", "L6"] = "Cell value"

// Using the Header class directly is also an option
table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][1] = "Cell value"
table[1][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = "Cell value"
```

Being able to do `table["A"][0]` and `table[0]["A"]` gives us a clue to how we read or address multiple cells in one
go. Let's say we want to print all the cells in column A:

``` kotlin
table["A", 0] = "Cell A0"
table["A", 1] = "Cell A1"

table["A"].forEach {
    println(it)
}
```

Or all the cells in row 0:

``` kotlin
table["A", 0] = "Cell A0"
table["B", 0] = "Cell B0"

table[0].forEach {
    println(it)
}
```

What we're doing above is to obtain a reference to a `Column` or a `Row` instance through `table["A"]` and `table[0]`.
These can also be used as references past back into a table, even a different table than the one they were obtained from.

``` kotlin
val t1 = Table[null]
val t2 = Table[null]

val column = t1["A"]
val row = t1[1]

t2[column][row] = "Cell value"
t2[row][column] = "Cell value"

// The above is shorthand for this:
t2[column.header][row.index] = "Cell value"
t2[row.index][column.header] = "Cell value"
```

It's also possible to address an area of cells using what's known as a `CellRange`, like so:

``` kotlin
table["A", 0] = "Cell A0"
table["A", 1] = "Cell A1"
table["B", 0] = "Cell B0"
table["B", 1] = "Cell B1"

(table["A", 0]..table["B", 1]).forEach {
    println(it)
}

// Output:
// Cell A0
// Cell A1
// Cell B0
// Cell B1

```

You'll notice the output starts at `["A", 0]` and ends at `["B", 1]`, just like we specified. And it follows column A
from row 0 to row 1 before moving on to column B. It goes down to row 1 because we asked it to go to `["B", 1]` which
happens to be at row 1.

Doing this in reverse order would be as simple as switching the first and last cells in the range:

``` kotlin
(table["B", 1]..table["A", 0]).forEach {
    println(it)
}

// Output:
// Cell B1
// Cell B0
// Cell A1
// Cell A0
```

If we wanted this to first do `["A", 0]` and then `["B", 0]`, instead of `["A", 0]` followed by `["A", 1]`, we can do
this by making use of the `CellOrder` option:

``` kotlin
(table["A", 0]..table["B", 1] by CellOrder.ROW).forEach {
    println(it)
}

// Output:
// Cell A0
// Cell B0
// Cell A1
// Cell B1
```

This should give you a good understanding of how cells are addressed and accessed. It's also possible to do a column
range and a row range by using `table["A"]..table["B"]` or `table[0]..table[1]`.

## Storing tables to disk

You can save and load tables to disk, using the `save(..)` and `load(..)` functions.

The next example shows how we can use the save functions available to us:

``` kotlin
val table = Table["MyTable"]

table["A", 1] = 100
table["B", 2] = 200

// This will save the table in a file named "MyTable.sigt"
// placed in the current working directory.
save(table)

// This will save the table in a file named "MyTable2.sigt"
// placed in the current working directory.
save(table to "MyTable2")

// This will save the table in a file named "MyTable3.sigt"
// placed in a subfolder called "tables" relative to current
// working directory. Any required folder is automatically
// created.
save(table to "tables/MyTable3")

// You can also use a File object, the .sigt
// extension is automatically added if needed.
save(table to File("tables/extra/MyTable4"))
save(table to File("tables/extra/MyTable4.sigt"))

// Use a custom extension, saving the file as MyTable5.custom.
save(table to File("tables/MyTable5"), extension = "custom")

// Table content is compressed by default, but you can
// turn this off if needed with the compress parameter.
save(table, compress = false)
```

Moving on to load next:

``` kotlin
val table = Table["MyTable"]

// Like with save(table), this will attempt to load a table
// in the current working directory called "MyTable.sigt".
// The content is loaded into the provided table, allowing
// you to merge content with any existing content. The load
// function will automatically figure out if the content is
// compressed or not.

load(table)

print(table)
println()

// Note that clearing a table doesn't remove any columns.
// If you want to remove a column, use remove(table["column"])
clear(table)

// You might want to perform some operations or filtering on
// the data that is being loaded, which is possible like shown
// next, on a per column by column basis. Like with save, you
// can also explicitly give a file name and also an extension
// should you need to. 

load("MyTable" to table) {
    apply {
        filter { it.isNumeric }.forEach { it { it * 2 } }
    }
}

print(table)

// Output:
//        |A   |B
//    1   |100 |
//    2   |    |200
//
//        |A   |B
//    1   |200 |
//    2   |    |400
```

The `filter { it.isNumeric }.forEach { it { it * 2 } }` operation might not make much sense to you yet, but will
after you've covered the chapter on cells, so for now, just understand that it will multiply all cells that contain
a number with two and update the table value with the result.

## Table registry

If you've already created a table with `Table["name"]`, you can obtain a reference to this same table through the
registry by calling `Table.fromRegistry("name")`. However, if you redo `Table["name"]` a new table is created
that will replace the old table of the same name in the registry.

Also note that if a table is replaced in the registry, the old table will be closed, something you can check by looking
at the `closed` property on a table as shown below.

``` kotlin
val table1 = Table["name"]
val table2 = Table["name"]

println(table1.closed)

// Output:
// true

println(table2.closed)

// Output:
// false
```

A table that is closed can not be updated, and an `InvalidRefException` will be thrown if you try. You may still read
existing data from a closed table. If you clone it, as described next, you are able to modify the clone.

Note that you can also remove (and close) a table from the registry with either `remove(table)` or `Table.remove("name")`.

Tables with no name, created with `Table[null]`, are not put on the registry.

If you call `Table.fromRegistry("name")` and there is no table with that name in the registry, an `InvalidTableException`
will be thrown. If you want to provide a fallback generator that will create the table if not found, you may do so as
shown below.

``` kotlin
val table = Table.fromRegistry("name") {
    // Generate new table
    Table[null]
}
```

Be careful not to generate the new table with a name itself, like `Table["name"]` instead of `Table[null]` within the
generator, because that will put that table on the registry as well (unless that's what you really wanted). The
returned table will have the name provided to `fromRegistry`, and now be available within the registry as well.

The returned table when calling `Table.fromRegistry` with a generator, is, if the generator is used, a clone of the
table returned by the generator. It's therefore possible to have the generator return an existing table without this
causing any sort of naming conflict or other overlap.

## Cloning a table

Internally in tables, data is stored in what is known as "persistent data structures". This is not persistence, as in
storing to disk, but instead allows us to efficiently manage updates while still keeping previous versions of the table.

Using this we can create copies of a table, something you can do with the `clone(table)` function. It will return you
a new table with an exact copy of the data in the table you're cloning. It doesn't matter if the table is empty or
filled with millions of cells, the effort needed to provide a clone is always the same and equally fast.

The returned clone is completely separate from the original table and would not contain any event subscriptions or
view connections, and you can use it as any other table. By default, the cloned table will share the same name with
the original table, but is not put on the registry. If you give it a new name, with `clone(table, "new name")`, it will
be placed on the registry.

The original table remains as is, unchanged. It's also possible to obtain the original table from the clone through the
`table.source` property. This will be `null` if the table isn't a clone.

``` kotlin
val table1 = Table["MyTable"]

table1["A", 1] = 100
table1["B", 2] = 200

val table2 = clone(table1)

// Changing a value on table2 does not impact the original table

table2["A", 1] = 300

print(table1)

// Output:
//     |A   |B
// 1   |100 |
// 2   |    |200

// But as expected did change table2..

print(table2)

// Output:
//     |A   |B   
// 1   |300 |    
// 2   |    |200 
```