# Rows

Unlike columns, you can't define labels on rows. Rows instead are indexed by a number of type `Long`. Through various
convenience functions we can also reference rows with an `Int`, but internally it is all of type `Long`.

## Creating rows

``` kotlin
val table = Table["MyTable"]

val row1 = table[1]
val row2 = table[2]

print(table)
```

Like we saw from creating columns in the previous chapter, creating rows by themselves doesn't, of course, add any cells
to the table. Printing it as done above doesn't give us any output.

While we have obtained two references, one to row1 and one to row2, that's all we've done. Rows, unlike columns, don't
have any prenatal state, and it's fair to say, because they're labeled with their index, that they already exist.

Let's modify the example and add some data:

``` kotlin
val table = Table["MyTable"]

val row1 = table[1]
val row2 = table[2]

row2["A"] = "Cell A2"

print(table)

// Output:
//         |A
// 2       |Cell A2
```

As expected, we can now see data in the cell at row 2, column A. You'll realize that what we've done is comparable
to doing `table[2]["A"] = "Cell A2"`, which is just another way of doing `table["A"][2] = "Cell A2"`, or even
`table["A", 2] = "Cell A2"`.

## Row and index

While a table can contain any number of columns, the number of rows it can hold is fixed between the lower and upper
bound of what we can represent with a `Long`. That should be sufficient for most use cases, but it's good to have a
large range to work with as we can space the content out. Rows with no cells aren't included when iterating over the
cells in a column, for example.

Like with `Column`, you can't create an instance of a `Row` directly. You'd do it like shown above, through the table.
A `Row` instance is therefore, like with the column, linked to the table it belongs to. A row is equal to another row
if it belongs to the same table, and points to the same index. There's one other factor that impacts equality of rows,
something known as "index relations", but we'll cover that later.

Also, like the `Column` contains a `Header`, the `Row` contains an `index`, and rows are naturally ordered by this
index, with no duplicate indexes.

``` kotlin
val table1 = Table["MyTable1"]
val table2 = Table["MyTable2"]

fun assert(value: Boolean) {
    if (!value) { throw AssertionError() }
}

val rowT1R1 = table1[1]
val rowT2R1 = table2[1]

// We can pass a row to a table, even from a different table:
assert(rowT2R1 == table2[rowT1R1])

// Two rows are not equal if they belong to different
// tables, even if they have the same index:
assert(rowT1R1 == table1[1])
assert(rowT1R1.index == rowT2R1.index)
assert(rowT1R1 != rowT2R1)

// Note you can also get the table connected to
// a row from the row instance:
assert(rowT1R1.table == table1)
```

## Moving rows

We saw in the chapter on columns that they could be moved and copied, after, before or to other columns.

If we have column A, B, and C, and moved A to after B, it would end up between B and C, without impacting the
labels of column A and C. For rows, if we move row 0 to after row 2, it will become row 3, with any existing row 3
then becoming row 4. Any existing row 4 then becomes row 5, and so on, cascading all the way down. Moving a row in the
other direction, like moving row 5 to before row 3, will cause any existing row 2 to become row 1, any existing row 1
to become row 0 and so on, again cascading all the way up.

Let's see this in action:

``` kotlin
val table = Table["MyTable"]

table["A", 0] = "A0"
table["A", 1] = "A1"
table["A", 2] = "A2"
table["A", 3] = "A3"
table["A", 4] = "A4"

print(table)

// Output:
//    |A
// 0  |A0
// 1  |A1
// 2  |A2
// 3  |A3
// 4  |A4

// Move row 1 to before row 0, which is row -1.
// If there had been a row at -1 already, it
// would have been pushed to -2.
move(table[1] before table[0])

print(table)

// Output:
//    |A
// -1 |A1
// 0  |A0
// 2  |A2
// 3  |A3
// 4  |A4

// Move row 0 to after row 2, making it row 3.
// You'll notice in the output how that pushes
// all the other rows down by one index value.
move(table[0] after table[2])

print(table)

// Output:
//    |A
// -1 |A1
// 2  |A2
// 3  |A0
// 4  |A3
// 5  |A4

// Move row -1 to row 3, replacing the existing row 3
move(table[-1] to table[3])

print(table)

// Output:
//    |A
// 2  |A2
// 3  |A1
// 4  |A3
// 5  |A4
```

You can also, as with columns, move rows between different tables by having a different source and destination table.

## Copying rows

Like with the `copy` functions for columns, we have a `copy` function for rows, also taking the same arguments as the
`move` function for rows, but of course, copying rather than moving. Let's modify the earlier example:

``` kotlin
val table = Table["MyTable"]

table["A", 0] = "A0"
table["A", 1] = "A1"
table["A", 2] = "A2"
table["A", 3] = "A3"
table["A", 4] = "A4"

print(table)

// Output:
//    |A
// 0  |A0
// 1  |A1
// 2  |A2
// 3  |A3
// 4  |A4

// Copy row 1 to before row 0, which is row -1.
// If there had been a row at -1 already, it
// would have been pushed to -2, like with move(..).
// Different from move, the source remains,
// increasing the row count by one new row.
copy(table[1] before table[0])

print(table)

// Output:
//    |A
// -1 |A1
// 0  |A0
// 1  |A1
// 2  |A2
// 3  |A3
// 4  |A4

// Copy row 0 to after row 2, making it row 3.
// You'll notice in the output how that pushes
// all the other rows down by one index value.
copy(table[0] after table[2])

print(table)

// Output:
//    |A
// -1 |A1
// 0  |A0
// 1  |A1
// 2  |A2
// 3  |A0
// 4  |A3
// 5  |A4

// Copy row -1 to row 3, replacing the existing row 3
copy(table[-1] to table[3])

print(table)

// Output:
//    |A  
// -1 |A1 
// 0  |A0 
// 1  |A1 
// 2  |A2 
// 3  |A1 
// 4  |A3 
// 5  |A4 
```

As with moving rows to before or after another row, it will cascade the existing rows up or down to make the copied row
fit in between existing rows.

## Index relations

As we've covered earlier, while columns have their order defined by you, rows come with a predefined natural order.
While you can, as we've seen, move and copy rows around, you can't suddenly have row 20 come before row 19. The order
of rows have a predefined structure, and we can utilize this structure to navigate between rows within a column.

Index relations help us do this, and we'll present a simple example here to get you started:

``` kotlin
val table = Table[null]

table["A", 10] = "A10"
table["A", 20] = "A20"
table["A", 30] = "A30"

println(table["A"] before 20)
println(table["A"] at 20)
println(table["A"] after 20)

// Output:
// A10
// A20
// A30

// You also have the option to use "at or before" and "at or after":
println(table["A"] atOrBefore 19)
println(table["A"] atOrBefore 20)
println(table["A"] atOrAfter 20)
println(table["A"] atOrAfter 21)

// Output:
// A10
// A20
// A20
// A30
```

This ability to navigate is particularly useful if you have gaps in your data, and lets you find the closest cell
without needing to know exactly what row it's located at.

Say we want to append a cell to the bottom of a column:

``` kotlin
fun addValue(column: Column, value: Any?) {
    when (val cell = column before Long.MAX_VALUE) {
        is UnitCell -> column[0] { value }
        else -> column[cell.index + 1] { value }
    }
}

val table = Table["MyTable"]

addValue(table["A"], "First value")
addValue(table["B"], 1000)
addValue(table["A"], "Second value")
addValue(table["B"], 2000)

print(table)

// Output:
//              |A            |B            
// 0            |First value  |1000         
// 1            |Second value |2000         
```

The two lines within the `when` statement, including `UnitCell` and how the value is assigned to the cell hasn't been
covered yet, but will be in the next chapter.

We can also use index relations on a row, letting us fetch the nearest cell across the whole row:

``` kotlin
val table = Table[null]

table["A", 0] = "A0"
table["A", 1] = "A1"
table["B", 0] = "B0"
table["C", 0] = "C0"
table["C", 1] = "C1"
table["C", 2] = "C2"

print(table)

// Output:
//    |A  |B  |C
// 0  |A0 |B0 |C0
// 1  |A1 |   |C1
// 2  |   |   |C2

val row = table before 2

row.forEach {
    println(it)
}

// Output:
// A1
// B0
// C1
```

The default value for index relations is `at`. That means that doing `table[1]` is the same as doing `table at 1`, or
for `table["A", 1]` the same as `table["A"] at 1`.

We hinted earlier that index relations can impact the equality of rows. Because the index relation is part of a `Row`
instance, two rows with different index relations will not be equal, even if they otherwise point to the same table
and index.

``` kotlin
val table = Table[null]

fun assert(value: Boolean) {
    if (!value) { throw AssertionError() }
}

val rowAt1 = table at 1
val rowBefore1 = table before 1

assert(rowAt1 != rowBefore1)
assert(rowAt1.table == rowBefore1.table)
assert(rowAt1.index == rowBefore1.index)
assert(rowAt1.indexRelation != rowBefore1.indexRelation)
```
