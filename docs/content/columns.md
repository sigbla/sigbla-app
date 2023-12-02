# Columns

You will have seen columns in action already from the previous chapters, but there's more to cover so let's get started.

When creating a new table it will have no columns defined by default. This is different from a traditional spreadsheet,
which comes with predefined columns labeled "A", "B", "C", and so on. As you start creating columns, they are added
in the order you define them. You can rearrange this with functions we will cover later, but let's start with a
quick example.

## Creating columns

``` kotlin
val table = Table["MyTable"]

val columnA = table["A"]
val columnB = table["B"]

print(table)
```

Here we created two columns named A and B, assigned to the columnA and columnB variables. If you run this, you'll
notice that the print function doesn't print anything. While the columns were created, because they don't have any
cells in them they are in what's known as a prenatal state. All fresh columns with no data start in this state.

Let's modify the example slightly and try again:

``` kotlin
val table = Table["MyTable"]

val columnA = table["A"]
val columnB = table["B"]

columnB[0] = "Cell B0"

print(table)

// Output:
//         |B
// 0       |Cell B0
```

As expected, we can now see the content of column B, with a cell at row 0. Column A still doesn't have any data, so
is still hidden in its prenatal state.

Because we created column A first, by doing `table["A"]`, when we put data into it, it will show as the first column.
This is the case also when assigning data to it last, as shown next. It's the order they are created in that matters.

``` kotlin
val table = Table["MyTable"]

val columnA = table["A"]
val columnB = table["B"]

columnB[0] = "Cell B0"
columnA[0] = "Cell A0"

print(table)

// Output:
//         |A       |B
// 0       |Cell A0 |Cell B0
```

## Column and ColumnHeader

A table may contain any number of columns, and each column contains one or more column labels, as represented by a
column header. Each column header must be unique within a table.

You can't create a `Column` instance directly, but instead do that through `table[column header]` as seen earlier. A
column is equal to another column if they point to the same table and contain the same header. As such, columns are
linked to tables via the `Column` class.

The column header is contained within the `ColumnHeader` class. You can create `ColumnHeader` instances through its
constructor, or obtain a reference to it via the `Column` instance. A `ColumnHeader` is equal to another `ColumnHeader`
instance if they have equal labels.

Columns are ordered by their order value, and can be sorted according to this. As we'll see later, we can move columns
around, which would also update their order values.

``` kotlin
val table1 = Table["MyTable1"]
val table2 = Table["MyTable2"]

fun assert(value: Boolean) {
    if (!value) { throw AssertionError() }
}

// The below is the same as doing table["L1", L1"], but here
// using the ColumnHeader constructor directly, before passing
// that to our table:
val columnHeader1 = ColumnHeader("L1", "L2")
val columnT1C1 = table1[columnHeader1]

// Because column headers just contain labels, we can use
// them across several tables, and they will return us
// columns with the same column labels:
val columnT2C1 = table2[columnHeader1]

// We can also pass a column, even from a different table,
// which is shorthand for table[column.header]
assert(columnT2C1 == table2[columnT1C1])

// Two separate instances of ColumnHeader are equal if they
// contain the same labels in the same order:
assert(columnHeader1 == ColumnHeader("L1", "L2"))
assert(columnHeader1 == columnT1C1.header)
assert(columnT1C1.header == columnT2C1.header)
assert(columnHeader1 != ColumnHeader("L3", "L4"))

// Two columns are not equal if they belong to different
// tables, even if they have the same column header:
assert(columnT1C1 == table1["L1", "L2"])
assert(columnT1C1 != columnT2C1)

val columnT1C2 = table1["L3", "L4"]

// Columns contain an order field, which outline their
// order relative to other columns in the same table
assert(columnT1C1.order < columnT1C2.order)

// Note you can also get the table connected to
// a column from the column instance:
assert(columnT1C1.table == table1)

// It's also worth noting that a table is only equal to itself:
assert(table1 == Table.fromRegistry("MyTable1"))
assert(table2 == Table.fromRegistry("MyTable2"))
assert(table2 != Table.fromRegistry("MyTable1"))
```

## Navigating between columns

Columns come with functions to navigate left or right as seen in the next example. The parameter specifies how many
columns to move left or right, and a `null` would be returned if you move beyond the edge of the table.

``` kotlin
val table = Table[null]

table["A", 0] = "A0"
table["B", 0] = "B0"
table["C", 0] = "C0"

val columnA = table["B"] left 1
val columnC = table["B"] right 1

// This will return column C
table["A"] right 2

// This will return null because we move past the edge
table["B"] left 2

// This will return the column provided
table["B"] left 0

// A negative offset is also possible, which makes a left a right and vice versa
table["B"] left -1
table["B"] right -1
```

## Clearing and removing columns

Clearing and removing columns from a table are two different operations. Clearing a column will clear all the data
from the column, but keep the column itself. Removing it will both clear any data, and remove the column itself.

``` kotlin
val table = Table["MyTable1"]

table["A", 0] = 100
table["B", 0] = 200

print(table)

// Output:
//     |A   |B
// 0   |100 |200

clear(table["A"])

print(table)

// Output:
//     |A   |B
// 0   |    |200

remove(table["A"])

print(table)

// Output
//     |B
// 0   |200

// You can also clear and remove the whole table.
// Removing a table deletes it from the registry.
clear(table)
remove(table)
```

## Moving columns

If we wanted column A to come after column B we can move it with the `move(..)` function.

``` kotlin
val table = Table["MyTable"]

val columnA = table["A"]
val columnB = table["B"]

columnA[0] = "Cell A0"
columnB[0] = "Cell B0"

move(columnA after columnB)

print(table)

// Output:
//         |B       |A
// 0       |Cell B0 |Cell A0
```

We have a lot of options for moving columns around. We can, as seen, move them after another column, but also before
or to another column. Moving to another column means removing the original column and replacing the column we're moving
to with the source column. Let's exemplify this:

``` kotlin
val table = Table["MyTable"]

table["A", 0] = "A0"
table["B", 0] = "B0"
table["C", 0] = "C0"

print(table)

// Output:
//    |A  |B  |C
// 0  |A0 |B0 |C0

// Moves column B to after column C
move(table["B"] after table["C"])

print(table)

// Output:
//    |A  |C  |B
// 0  |A0 |C0 |B0

// Moves column C to before column A
move(table["C"] before table["A"])

print(table)

// Output:
//    |C  |A  |B
// 0  |C0 |A0 |B0

// Moves column A to column B, renaming column A to B,
// and removing the existing column B in the process
move(table["A"] to table["B"])

print(table)

// Output:
//    |C  |B
// 0  |C0 |A0
```

You can also move columns between different tables, by simply providing different source and target table references.
You'd do that like seen with the move function, for example `move(table1["A"] after table2["B"])` and so on.

Finally, it's worth noting that you can rename the source reference while moving it by providing a new name to the move
function like so:

``` kotlin
val table = Table["MyTable"]

table["A", 0] = "A0"
table["B", 0] = "B0"
table["C", 0] = "C0"

// Rename column B after moving it
move(table["B"] after table["C"], "New name")

print(table)

// Output
//          |A        |C        |New name
// 0        |A0       |C0       |B0
```

You should think of this like working as if you first perform the move and then rename the moved column. If the new
name matches an existing column, that column will also be removed. This could then potentially impact 3 columns:

``` kotlin
val table = Table["MyTable"]

table["A", 0] = "A0"
table["B", 0] = "B0"
table["C", 0] = "C0"

move(table["A"] after table["C"], "D")

print(table)

// Output
//    |B  |C  |D
// 0  |B0 |C0 |A0

move(table["B"] to table["C"], "D")

print(table)

// Output
//    |D
// 0  |B0
```

Nested labels and use of the ColumnHeader class is also supported when providing a new name. Column references are not
supported as a new name parameter to avoid any confusion around the source or destination of the renamed column.

``` kotlin
val table = Table["MyTable"]

table["A", 0] = "A0"
table["B", 0] = "B0"

move(table["A"] after table["B"], "L1", "L2")

print(table)

// Output:
//    |B  |L1
//    |   |L2
// 0  |B0 |A0

// Could also do move(table["L1", "L2"] before table["B"], ColumnHeader("B"))
move(table["L1", "L2"] before table["B"], table["B"].header)

print(table)

// Output:
//    |B
// 0  |A0
```

## Copying columns

Just like there's a `move(..)` function, we also have a `copy(..)` function.

It takes the same parameters as the move function, but, as the name indicates, keeps the source column intact.

Note that, if we don't rename a column while copying it to the same table, it behaves like a move because it is
replacing the source column with the destination column having the same column header.

``` kotlin
val table = Table["MyTable"]

table["A", 0] = "A0"
table["B", 0] = "B0"

// This will move A to after B, because we are doing it within one table
copy(table["A"] after table["B"])

print(table)

// Output
//    |B  |A
// 0  |B0 |A0

// Here we get a duplicate of B, named C
copy(table["B"] after table["A"], "C")

print(table)

// Output:
//    |B  |A  |C
// 0  |B0 |A0 |B0

// Copy to a new table
copy(table["C"] to Table["NewTable"])

print(Table.fromRegistry("NewTable"))

// Output:
//    |C
// 0  |B0

// Finally, let's copy A to B, and rename it to C, removing the old C
copy(table["A"] to table["B"], "C")

print(table)

// Output:
//    |C  |A
// 0  |A0 |A0
```

This concludes our introduction to columns, and you should now be able to create and manipulate columns in a table.
The next chapter covers rows, which you'll find provides many of the same functions, while also outlining some key
differences.