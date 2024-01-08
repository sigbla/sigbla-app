# Ranges

Throughout the earlier chapters you've seen ranges in action already. A range is something like `table["A"]..table["B"]`,
which in this case gives us a `ColumnRange` on `table`, starting at column A and ending at column B. Any columns between
these two points are included in the range.

There are 3 different range types: `ColumnRange`, as above, `RowRange` for rows, and `CellRange` for cells. Any range
is created by putting a `..` between the start and end point. A range needs to have a start and end point that belong
to the same table.

Ranges can provide an iterator, with the iterator returning columns, rows, or cells from the starting point to the end
point in the order they were defined.

Let's look at some examples next:

``` kotlin
val t = Table[null]

t["A", 0] = "A0"
t["A", 1] = "A1"
t["B", 0] = "B0"
t["B", 1] = "B1"
t["C", 0] = "C0"
t["C", 1] = "C1"

val columnRange = t["A"]..t["B"]
val rowRange = t[0]..t[1]
val cellRange = t["A", 0]..t["C", 1]

columnRange.forEach { println(it) }

// Output:
// Column[A]
// Column[B]

rowRange.forEach { println(it) }

// Output:
// Row[at 0]
// Row[at 1]

cellRange.forEach { println(it) }

// Output:
// A0
// A1
// B0
// B1
// C0
// C1
```

## Reversing a range

Getting a range reversed is as simple as swapping the start and end points:

``` kotlin
val t = Table[null]

t["A", 0] = "A0"
t["A", 1] = "A1"
t["B", 0] = "B0"
t["B", 1] = "B1"
t["C", 0] = "C0"
t["C", 1] = "C1"

val columnRange = t["B"]..t["A"]
val rowRange = t[1]..t[0]
val cellRange = t["C", 1]..t["A", 0]

columnRange.forEach { println(it) }

// Output:
// Column[B]
// Column[A]

rowRange.forEach { println(it) }

// Output:
// Row[at 1]
// Row[at 0]

cellRange.forEach { println(it) }

// Output:
// C1
// C0
// B1
// B0
// A1
// A0
```

## Cell range by column or row

You'll notice above that the cell range follows the column before moving to the next column. It's also possible to
iterate by one row before moving on to the next row as shown next:

``` kotlin
val t = Table[null]

t["A", 0] = "A0"
t["A", 1] = "A1"
t["B", 0] = "B0"
t["B", 1] = "B1"
t["C", 0] = "C0"
t["C", 1] = "C1"

// This is the default behavior
val cellRangeByColumn = t["A", 0]..t["C", 1] by CellOrder.COLUMN

// This changes the range to iterate by row instead
val cellRangeByRow = t["A", 0]..t["C", 1] by CellOrder.ROW

cellRangeByColumn.forEach { println(it) }

// Output:
// A0
// A1
// B0
// B1
// C0
// C1

cellRangeByRow.forEach { println(it) }

// Output:
// A0
// B0
// C0
// A1
// B1
// C1
```

## Other range functions

Ranges provide more than just an iterator, they also come with the ability to check if they are empty and if a particular
column, row, or cell is contained within them.

Row ranges are never empty. This is because, unlike columns, all rows always exist as a predefined range of indexes.
Column ranges might be empty, depending on the current state of the columns they contain. Hence, cell ranges might also
be empty, as cells are contained in columns, and cells themselves might be empty as well.

Let's see this in action:

``` kotlin
val t = Table[null]

t["A", 0] = "A0"
t["A", 1] = "A1"
t["B", 0] = "B0"
t["B", 1] = "B1"
t["C", 0] = "C0"
t["C", 1] = "C1"

val rowRange = t[0]..t[1]

// This is never true
println("rowRange.isEmpty: ${rowRange.isEmpty()}")

// True because row range contains this row
println("rowRange.contains t[0]: ${t[0] in rowRange}")

// False because row falls outside row range
println("rowRange.contains t[2]: ${t[2] in rowRange}")

// Output:
// rowRange.isEmpty: false
// rowRange.contains t[0]: true
// rowRange.contains t[2]: false

val columnRange = t["B"]..t["D"]

// This is false in this case because we have column B and C in the range
println("columnRange.isEmpty: ${columnRange.isEmpty()}")

// True because column B isn't prenatal
println("columnRange contains B: ${t["B"] in columnRange}")

// False because column D is prenatal
println("columnRange contains D: ${t["D"] in columnRange}")

// Output:
// columnRange.isEmpty: false
// columnRange contains B: true
// columnRange contains D: false

// Removing column B and C will make the column range empty:
remove(t["B"])
remove(t["C"])
println("columnRange.isEmpty: ${columnRange.isEmpty()}")

// Output:
// columnRange.isEmpty: true
```

Finally, it's worth noting that if columns are moved within the same table, and these are used as a start or end point
on a column range, then this will impact the next iterator created from that column range. Existing iterators are not
impacted by this.

It's also possible to check if a column range from one table contains a column from another table. It might, because
only the column header is used as part of this check. The same will apply for a row, where only the row index is used
to check if the row is contained within the row range. Cell ranges inherit this behavior as well when checking if a cell
from another table is contained within a cell range.

Cell ranges also allow for another check, which is, if a particular value is contained within the cell range. This would
refer to the value within cells, as shown next:

``` kotlin
val t = Table[null]

t["A", 0] = "A0"
t["A", 1] = "A1"
t["B", 0] = "B0"
t["B", 1] = "B1"
t["C", 0] = "C0"
t["C", 1] = "C1"

val cellRange = t["A", 0]..t["C", 1]

// True
println(t["A", 1] in cellRange)

// False
println(t["A", 2] in cellRange)

// True
println("A1" in cellRange)

// False
println("A3" in cellRange)
```
