#!/usr/bin/env kscript

// Cases:

// CellView value:
// tableView[label1, label2, label3, labelX, int|long|row|rowview] = value
// tableView[label1, label2, label3, labelX][int|long|row|rowview] = value
// tableView[header|column|columnview, int|long|row|rowview] = value
// tableView[header|column|columnview][int|long|row|rowview] = value
// tableView[int|long|row|rowview][label1, label2, label3, labelX] = value
// tableView[int|long|row|rowview][header|column|columnview] = value
// tableView[cellview] = value
// tableView[cell] = value

// + the CellView.() -> Any? assignment pattern

// ColumnView value:
// tableView[label1, label2, label3, labelX] = value
// tableView[header|column|columnview] = value
// tableView[int|long|row|rowview][label1, label2, label3, labelX] = value
// tableView[int|long|row|rowview][header|column|columnview] = value

// + the ColumnView.() -> Any? assignment pattern

// RowView value:
// tableView[int|long|row|rowview] = value
// tableView[label1, label2, label3, labelX][int|long|row|rowview] = value
// tableView[header|column|columnview][int|long|row|rowview] = value

// + the RowView.() -> Any? assignment pattern

fun generateForCellView() {
    println("@Test")
    println("fun `cellview accessors`() {")

    println("""
        val table = Table[object {}.javaClass.enclosingMethod.name]
        val row = table[Long.MIN_VALUE]
        
        val tableView = TableView[table]
        val rowView = tableView[row]

        tableView["L1", 1][CellHeight] = 1000
        tableView["L1", 1][CellWidth] = 2000
        tableView["L1", 1][CellClasses] = "cc-1"
        tableView["L1", 1][CellTopics] = "ct-1"
        val ct: Cell<*>.() -> Any? = {}
        tableView["L1", 1][CellTransformer] = ct

        val sourceCellView = tableView["L1", 1]

        fun compare(cellView: CellView) {
            assertEquals(1000L, sourceCellView[CellHeight].height)
            assertEquals(2000L, sourceCellView[CellWidth].width)
            assertEquals(listOf("cc-1"), sourceCellView[CellClasses].classes)
            assertEquals(listOf("ct-1"), sourceCellView[CellTopics].topics)
            assertEquals(ct, sourceCellView[CellTransformer].function)

            assertEquals(sourceCellView[CellHeight].height, cellView[CellHeight].height)
            assertEquals(sourceCellView[CellWidth].width, cellView[CellWidth].width)
            assertEquals(sourceCellView[CellClasses].classes, cellView[CellClasses].classes)
            assertEquals(sourceCellView[CellTopics].topics, cellView[CellTopics].topics)
            assertEquals(sourceCellView[CellTransformer].function, cellView[CellTransformer].function)

            clear(cellView)
        }
    """.trimIndent())

    for (i in 1..5) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[label1, label2, label3, labelX, int] = value
        println()
        println("tableView[${headers.joinToString()}, Int.MAX_VALUE] = sourceCellView")
        println("compare(tableView[${headers.joinToString()}, Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, long] = value
        println()
        println("tableView[${headers.joinToString()}, Long.MAX_VALUE] = sourceCellView")
        println("compare(tableView[${headers.joinToString()}, Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, row] = value
        println()
        println("tableView[${headers.joinToString()}, row] = sourceCellView")
        println("compare(tableView[${headers.joinToString()}, row])")

        // tableView[label1, label2, label3, labelX, rowview] = value
        println()
        println("tableView[${headers.joinToString()}, rowView] = sourceCellView")
        println("compare(tableView[${headers.joinToString()}, rowView])")

        // tableView[label1, label2, label3, labelX][int] = value
        println()
        println("tableView[${headers.joinToString()}][Int.MAX_VALUE] = sourceCellView")
        println("compare(tableView[${headers.joinToString()}][Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][long] = value
        println()
        println("tableView[${headers.joinToString()}][Long.MAX_VALUE] = sourceCellView")
        println("compare(tableView[${headers.joinToString()}][Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][row] = value
        println()
        println("tableView[${headers.joinToString()}][row] = sourceCellView")
        println("compare(tableView[${headers.joinToString()}][row])")

        // tableView[label1, label2, label3, labelX][rowview] = value
        println()
        println("tableView[${headers.joinToString()}][rowView] = sourceCellView")
        println("compare(tableView[${headers.joinToString()}][rowView])")

        // tableView[int][label1, label2, label3, labelX] = value
        println()
        println("tableView[Int.MAX_VALUE][${headers.joinToString()}] = sourceCellView")
        println("compare(tableView[Int.MAX_VALUE][${headers.joinToString()}])")

        // tableView[long][label1, label2, label3, labelX] = value
        println()
        println("tableView[Long.MAX_VALUE][${headers.joinToString()}] = sourceCellView")
        println("compare(tableView[Long.MAX_VALUE][${headers.joinToString()}])")

        // tableView[row][label1, label2, label3, labelX] = value
        println()
        println("tableView[row][${headers.joinToString()}] = sourceCellView")
        println("compare(tableView[row][${headers.joinToString()}])")

        // tableView[rowview][label1, label2, label3, labelX] = value
        println()
        println("tableView[rowView][${headers.joinToString()}] = sourceCellView")
        println("compare(tableView[rowView][${headers.joinToString()}])")
    }

    for (i in 1..5) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[label1, label2, label3, labelX, int] = value
        println()
        println("tableView[${headers.joinToString()}, Int.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}, Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, long] = value
        println()
        println("tableView[${headers.joinToString()}, Long.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}, Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, row] = value
        println()
        println("tableView[${headers.joinToString()}, row] = { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}, row])")

        // tableView[label1, label2, label3, labelX, rowview] = value
        println()
        println("tableView[${headers.joinToString()}, rowView] = { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}, rowView])")

        // tableView[label1, label2, label3, labelX][int] = value
        println()
        println("tableView[${headers.joinToString()}][Int.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}][Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][long] = value
        println()
        println("tableView[${headers.joinToString()}][Long.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}][Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][row] = value
        println()
        println("tableView[${headers.joinToString()}][row] = { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}][row])")

        // tableView[label1, label2, label3, labelX][rowview] = value
        println()
        println("tableView[${headers.joinToString()}][rowView] = { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}][rowView])")

        // tableView[int][label1, label2, label3, labelX] = value
        println()
        println("tableView[Int.MAX_VALUE][${headers.joinToString()}] = { sourceCellView }")
        println("compare(tableView[Int.MAX_VALUE][${headers.joinToString()}])")

        // tableView[long][label1, label2, label3, labelX] = value
        println()
        println("tableView[Long.MAX_VALUE][${headers.joinToString()}] = { sourceCellView }")
        println("compare(tableView[Long.MAX_VALUE][${headers.joinToString()}])")

        // tableView[row][label1, label2, label3, labelX] = value
        println()
        println("tableView[row][${headers.joinToString()}] = { sourceCellView }")
        println("compare(tableView[row][${headers.joinToString()}])")

        // tableView[rowview][label1, label2, label3, labelX] = value
        println()
        println("tableView[rowView][${headers.joinToString()}] = { sourceCellView }")
        println("compare(tableView[rowView][${headers.joinToString()}])")
    }

    for (i in 1..5) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[label1, label2, label3, labelX, int] = value
        println()
        println("tableView[${headers.joinToString()}, Int.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}, Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, long] = value
        println()
        println("tableView[${headers.joinToString()}, Long.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}, Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, row] = value
        println()
        println("tableView[${headers.joinToString()}, row] { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}, row])")

        // tableView[label1, label2, label3, labelX, rowview] = value
        println()
        println("tableView[${headers.joinToString()}, rowView] { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}, rowView])")

        // tableView[label1, label2, label3, labelX][int] = value
        println()
        println("tableView[${headers.joinToString()}][Int.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}][Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][long] = value
        println()
        println("tableView[${headers.joinToString()}][Long.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}][Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][row] = value
        println()
        println("tableView[${headers.joinToString()}][row] { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}][row])")

        // tableView[label1, label2, label3, labelX][rowview] = value
        println()
        println("tableView[${headers.joinToString()}][rowView] { sourceCellView }")
        println("compare(tableView[${headers.joinToString()}][rowView])")

        // tableView[int][label1, label2, label3, labelX] = value
        println()
        println("tableView[Int.MAX_VALUE][${headers.joinToString()}] { sourceCellView }")
        println("compare(tableView[Int.MAX_VALUE][${headers.joinToString()}])")

        // tableView[long][label1, label2, label3, labelX] = value
        println()
        println("tableView[Long.MAX_VALUE][${headers.joinToString()}] { sourceCellView }")
        println("compare(tableView[Long.MAX_VALUE][${headers.joinToString()}])")

        // tableView[row][label1, label2, label3, labelX] = value
        println()
        println("tableView[row][${headers.joinToString()}] { sourceCellView }")
        println("compare(tableView[row][${headers.joinToString()}])")

        // tableView[rowview][label1, label2, label3, labelX] = value
        println()
        println("tableView[rowView][${headers.joinToString()}] { sourceCellView }")
        println("compare(tableView[rowView][${headers.joinToString()}])")
    }

    for (i in 5..6) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[header, int] = value
        println()
        println("tableView[Header[${headers.joinToString()}], Int.MAX_VALUE] = sourceCellView")
        println("compare(tableView[Header[${headers.joinToString()}], Int.MAX_VALUE])")

        // tableView[header, long] = value
        println()
        println("tableView[Header[${headers.joinToString()}], Long.MAX_VALUE] = sourceCellView")
        println("compare(tableView[Header[${headers.joinToString()}], Long.MAX_VALUE])")

        // tableView[header, row] = value
        println()
        println("tableView[Header[${headers.joinToString()}], row] = sourceCellView")
        println("compare(tableView[Header[${headers.joinToString()}], row])")

        // tableView[header, rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}], rowView] = sourceCellView")
        println("compare(tableView[Header[${headers.joinToString()}], rowView])")

        // tableView[header][int] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE] = sourceCellView")
        println("compare(tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE])")

        // tableView[header][long] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE] = sourceCellView")
        println("compare(tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE])")

        // tableView[header][row] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][row] = sourceCellView")
        println("compare(tableView[Header[${headers.joinToString()}]][row])")

        // tableView[header][rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][rowView] = sourceCellView")
        println("compare(tableView[Header[${headers.joinToString()}]][rowView])")

        // tableView[int][header] = value
        println()
        println("tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]] = sourceCellView")
        println("compare(tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[long][header] = value
        println()
        println("tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]] = sourceCellView")
        println("compare(tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[row][header] = value
        println()
        println("tableView[row][Header[${headers.joinToString()}]] = sourceCellView")
        println("compare(tableView[row][Header[${headers.joinToString()}]])")

        // tableView[rowview][header] = value
        println()
        println("tableView[rowView][Header[${headers.joinToString()}]] = sourceCellView")
        println("compare(tableView[rowView][Header[${headers.joinToString()}]])")

        // tableView[column, int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE] = sourceCellView")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[column, long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE] = sourceCellView")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[column, row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], row] = sourceCellView")
        println("compare(tableView[table[Header[${headers.joinToString()}]], row])")

        // tableView[column, rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], rowView] = sourceCellView")
        println("compare(tableView[table[Header[${headers.joinToString()}]], rowView])")

        // tableView[column][int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = sourceCellView")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[column][long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = sourceCellView")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[column][row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][row] = sourceCellView")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][row])")

        // tableView[column][rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][rowView] = sourceCellView")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][column] = value
        println()
        println("tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]] = sourceCellView")
        println("compare(tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[long][column] = value
        println()
        println("tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]] = sourceCellView")
        println("compare(tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[row][column] = value
        println()
        println("tableView[row][table[Header[${headers.joinToString()}]]] = sourceCellView")
        println("compare(tableView[row][table[Header[${headers.joinToString()}]]])")

        // tableView[rowview][column] = value
        println()
        println("tableView[rowView][table[Header[${headers.joinToString()}]]] = sourceCellView")
        println("compare(tableView[rowView][table[Header[${headers.joinToString()}]]])")

        // tableView[columnView, int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE] = sourceCellView")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[columnView, long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE] = sourceCellView")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[columnView, row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], row] = sourceCellView")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], row])")

        // tableView[columnView, rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], rowView] = sourceCellView")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], rowView])")

        // tableView[columnView][int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = sourceCellView")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[columnView][long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = sourceCellView")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[columnView][row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][row] = sourceCellView")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][row])")

        // tableView[columnView][rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][rowView] = sourceCellView")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][columnView] = value
        println()
        println("tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]] = sourceCellView")
        println("compare(tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[long][columnView] = value
        println()
        println("tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]] = sourceCellView")
        println("compare(tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[row][columnView] = value
        println()
        println("tableView[row][tableView[Header[${headers.joinToString()}]]] = sourceCellView")
        println("compare(tableView[row][tableView[Header[${headers.joinToString()}]]])")

        // tableView[rowview][columnView] = value
        println()
        println("tableView[rowView][tableView[Header[${headers.joinToString()}]]] = sourceCellView")
        println("compare(tableView[rowView][tableView[Header[${headers.joinToString()}]]])")

    }

    for (i in 5..6) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[header, int] = value
        println()
        println("tableView[Header[${headers.joinToString()}], Int.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}], Int.MAX_VALUE])")

        // tableView[header, long] = value
        println()
        println("tableView[Header[${headers.joinToString()}], Long.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}], Long.MAX_VALUE])")

        // tableView[header, row] = value
        println()
        println("tableView[Header[${headers.joinToString()}], row] = { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}], row])")

        // tableView[header, rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}], rowView] = { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}], rowView])")

        // tableView[header][int] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE])")

        // tableView[header][long] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE])")

        // tableView[header][row] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][row] = { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}]][row])")

        // tableView[header][rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][rowView] = { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}]][rowView])")

        // tableView[int][header] = value
        println()
        println("tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]] = { sourceCellView }")
        println("compare(tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[long][header] = value
        println()
        println("tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]] = { sourceCellView }")
        println("compare(tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[row][header] = value
        println()
        println("tableView[row][Header[${headers.joinToString()}]] = { sourceCellView }")
        println("compare(tableView[row][Header[${headers.joinToString()}]])")

        // tableView[rowview][header] = value
        println()
        println("tableView[rowView][Header[${headers.joinToString()}]] = { sourceCellView }")
        println("compare(tableView[rowView][Header[${headers.joinToString()}]])")

        // tableView[column, int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[column, long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[column, row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], row] = { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], row])")

        // tableView[column, rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], rowView] = { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], rowView])")

        // tableView[column][int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[column][long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[column][row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][row] = { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][row])")

        // tableView[column][rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][rowView] = { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][column] = value
        println()
        println("tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]] = { sourceCellView }")
        println("compare(tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[long][column] = value
        println()
        println("tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]] = { sourceCellView }")
        println("compare(tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[row][column] = value
        println()
        println("tableView[row][table[Header[${headers.joinToString()}]]] = { sourceCellView }")
        println("compare(tableView[row][table[Header[${headers.joinToString()}]]])")

        // tableView[rowview][column] = value
        println()
        println("tableView[rowView][table[Header[${headers.joinToString()}]]] = { sourceCellView }")
        println("compare(tableView[rowView][table[Header[${headers.joinToString()}]]])")

        // tableView[columnView, int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[columnView, long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[columnView, row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], row] = { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], row])")

        // tableView[columnView, rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], rowView] = { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], rowView])")

        // tableView[columnView][int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[columnView][long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[columnView][row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][row] = { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][row])")

        // tableView[columnView][rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][rowView] = { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][columnView] = value
        println()
        println("tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]] = { sourceCellView }")
        println("compare(tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[long][columnView] = value
        println()
        println("tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]] = { sourceCellView }")
        println("compare(tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[row][columnView] = value
        println()
        println("tableView[row][tableView[Header[${headers.joinToString()}]]] = { sourceCellView }")
        println("compare(tableView[row][tableView[Header[${headers.joinToString()}]]])")

        // tableView[rowview][columnView] = value
        println()
        println("tableView[rowView][tableView[Header[${headers.joinToString()}]]] = { sourceCellView }")
        println("compare(tableView[rowView][tableView[Header[${headers.joinToString()}]]])")

    }

    for (i in 5..6) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[header, int] = value
        println()
        println("tableView[Header[${headers.joinToString()}], Int.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}], Int.MAX_VALUE])")

        // tableView[header, long] = value
        println()
        println("tableView[Header[${headers.joinToString()}], Long.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}], Long.MAX_VALUE])")

        // tableView[header, row] = value
        println()
        println("tableView[Header[${headers.joinToString()}], row] { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}], row])")

        // tableView[header, rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}], rowView] { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}], rowView])")

        // tableView[header][int] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE])")

        // tableView[header][long] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE])")

        // tableView[header][row] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][row] { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}]][row])")

        // tableView[header][rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][rowView] { sourceCellView }")
        println("compare(tableView[Header[${headers.joinToString()}]][rowView])")

        // tableView[int][header] = value
        println()
        println("tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]] { sourceCellView }")
        println("compare(tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[long][header] = value
        println()
        println("tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]] { sourceCellView }")
        println("compare(tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[row][header] = value
        println()
        println("tableView[row][Header[${headers.joinToString()}]] { sourceCellView }")
        println("compare(tableView[row][Header[${headers.joinToString()}]])")

        // tableView[rowview][header] = value
        println()
        println("tableView[rowView][Header[${headers.joinToString()}]] { sourceCellView }")
        println("compare(tableView[rowView][Header[${headers.joinToString()}]])")

        // tableView[column, int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[column, long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[column, row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], row] { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], row])")

        // tableView[column, rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], rowView] { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], rowView])")

        // tableView[column][int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[column][long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[column][row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][row] { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][row])")

        // tableView[column][rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][rowView] { sourceCellView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][column] = value
        println()
        println("tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]] { sourceCellView }")
        println("compare(tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[long][column] = value
        println()
        println("tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]] { sourceCellView }")
        println("compare(tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[row][column] = value
        println()
        println("tableView[row][table[Header[${headers.joinToString()}]]] { sourceCellView }")
        println("compare(tableView[row][table[Header[${headers.joinToString()}]]])")

        // tableView[rowview][column] = value
        println()
        println("tableView[rowView][table[Header[${headers.joinToString()}]]] { sourceCellView }")
        println("compare(tableView[rowView][table[Header[${headers.joinToString()}]]])")

        // tableView[columnView, int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[columnView, long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[columnView, row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], row] { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], row])")

        // tableView[columnView, rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], rowView] { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], rowView])")

        // tableView[columnView][int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[columnView][long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE] { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[columnView][row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][row] { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][row])")

        // tableView[columnView][rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][rowView] { sourceCellView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][columnView] = value
        println()
        println("tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]] { sourceCellView }")
        println("compare(tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[long][columnView] = value
        println()
        println("tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]] { sourceCellView }")
        println("compare(tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[row][columnView] = value
        println()
        println("tableView[row][tableView[Header[${headers.joinToString()}]]] { sourceCellView }")
        println("compare(tableView[row][tableView[Header[${headers.joinToString()}]]])")

        // tableView[rowview][columnView] = value
        println()
        println("tableView[rowView][tableView[Header[${headers.joinToString()}]]] { sourceCellView }")
        println("compare(tableView[rowView][tableView[Header[${headers.joinToString()}]]])")

    }

    println("""
            tableView[tableView["L1", 1000]] = sourceCellView
            compare(tableView[tableView["L1", 1000]])

            tableView[tableView["L1", 1001]] = { sourceCellView }
            compare(tableView[tableView["L1", 1001]])

            tableView[tableView["L1", 1002]] { sourceCellView }
            compare(tableView[tableView["L1", 1002]])

            tableView[table["L1", 2000]] = sourceCellView
            compare(tableView[table["L1", 2000]])

            tableView[table["L1", 2001]] = { sourceCellView }
            compare(tableView[table["L1", 2001]])

            tableView[table["L1", 2002]] { sourceCellView }
            compare(tableView[table["L1", 2002]])
    """.trimIndent())

    println("}")
    println()
}

generateForCellView()