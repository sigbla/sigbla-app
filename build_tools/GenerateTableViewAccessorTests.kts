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

// + invoke assignment pattern

// ColumnView value:
// tableView[label1, label2, label3, labelX] = value
// tableView[header|column|columnview] = value

// + the ColumnView.() -> Any? assignment pattern

// RowView value:
// tableView[int|long|row|rowview] = value

// + the RowView.() -> Any? assignment pattern

fun generateForCellView() {
    println("@Test")
    println("fun `cellview accessors`() {")
    println("val table = Table[\"\${this.javaClass.simpleName} \${object {}.javaClass.enclosingMethod.name}\"]")

    println("""
        val row = table[Long.MIN_VALUE]
        
        val tableView = TableView[table]
        val rowView = tableView[row]

        tableView["L1", 1][CellHeight] = 1000
        tableView["L1", 1][CellWidth] = 2000
        tableView["L1", 1][CellClasses] = "cc-1"
        tableView["L1", 1][CellTopics] = "ct-1"
        val ct: Cell<*>.() -> Unit = {}
        tableView["L1", 1][CellTransformer] = ct

        val sourceCellView = tableView["L1", 1]

        fun compare(cellView: CellView) {
            assertEquals(1000L, sourceCellView[CellHeight].height)
            assertEquals(2000L, sourceCellView[CellWidth].width)
            assertEquals(setOf("cc-1"), sourceCellView[CellClasses].classes)
            assertEquals(setOf("ct-1"), sourceCellView[CellTopics].topics)
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
        println("tableView[${headers.joinToString()}, Int.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[${headers.joinToString()}, Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, long] = value
        println()
        println("tableView[${headers.joinToString()}, Long.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[${headers.joinToString()}, Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, row] = value
        println()
        println("tableView[${headers.joinToString()}, row] = { this(sourceCellView) }")
        println("compare(tableView[${headers.joinToString()}, row])")

        // tableView[label1, label2, label3, labelX, rowview] = value
        println()
        println("tableView[${headers.joinToString()}, rowView] = { this(sourceCellView) }")
        println("compare(tableView[${headers.joinToString()}, rowView])")

        // tableView[label1, label2, label3, labelX][int] = value
        println()
        println("tableView[${headers.joinToString()}][Int.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[${headers.joinToString()}][Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][long] = value
        println()
        println("tableView[${headers.joinToString()}][Long.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[${headers.joinToString()}][Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][row] = value
        println()
        println("tableView[${headers.joinToString()}][row] = { this(sourceCellView) }")
        println("compare(tableView[${headers.joinToString()}][row])")

        // tableView[label1, label2, label3, labelX][rowview] = value
        println()
        println("tableView[${headers.joinToString()}][rowView] = { this(sourceCellView) }")
        println("compare(tableView[${headers.joinToString()}][rowView])")

        // tableView[int][label1, label2, label3, labelX] = value
        println()
        println("tableView[Int.MAX_VALUE][${headers.joinToString()}] = { this(sourceCellView) }")
        println("compare(tableView[Int.MAX_VALUE][${headers.joinToString()}])")

        // tableView[long][label1, label2, label3, labelX] = value
        println()
        println("tableView[Long.MAX_VALUE][${headers.joinToString()}] = { this(sourceCellView) }")
        println("compare(tableView[Long.MAX_VALUE][${headers.joinToString()}])")

        // tableView[row][label1, label2, label3, labelX] = value
        println()
        println("tableView[row][${headers.joinToString()}] = { this(sourceCellView) }")
        println("compare(tableView[row][${headers.joinToString()}])")

        // tableView[rowview][label1, label2, label3, labelX] = value
        println()
        println("tableView[rowView][${headers.joinToString()}] = { this(sourceCellView) }")
        println("compare(tableView[rowView][${headers.joinToString()}])")
    }

    for (i in 1..5) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[label1, label2, label3, labelX, int] = value
        println()
        println("tableView[${headers.joinToString()}, Int.MAX_VALUE](sourceCellView)")
        println("compare(tableView[${headers.joinToString()}, Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, long] = value
        println()
        println("tableView[${headers.joinToString()}, Long.MAX_VALUE](sourceCellView)")
        println("compare(tableView[${headers.joinToString()}, Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX, row] = value
        println()
        println("tableView[${headers.joinToString()}, row](sourceCellView)")
        println("compare(tableView[${headers.joinToString()}, row])")

        // tableView[label1, label2, label3, labelX, rowview] = value
        println()
        println("tableView[${headers.joinToString()}, rowView](sourceCellView)")
        println("compare(tableView[${headers.joinToString()}, rowView])")

        // tableView[label1, label2, label3, labelX][int] = value
        println()
        println("tableView[${headers.joinToString()}][Int.MAX_VALUE](sourceCellView)")
        println("compare(tableView[${headers.joinToString()}][Int.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][long] = value
        println()
        println("tableView[${headers.joinToString()}][Long.MAX_VALUE](sourceCellView)")
        println("compare(tableView[${headers.joinToString()}][Long.MAX_VALUE])")

        // tableView[label1, label2, label3, labelX][row] = value
        println()
        println("tableView[${headers.joinToString()}][row](sourceCellView)")
        println("compare(tableView[${headers.joinToString()}][row])")

        // tableView[label1, label2, label3, labelX][rowview] = value
        println()
        println("tableView[${headers.joinToString()}][rowView](sourceCellView)")
        println("compare(tableView[${headers.joinToString()}][rowView])")

        // tableView[int][label1, label2, label3, labelX] = value
        println()
        println("tableView[Int.MAX_VALUE][${headers.joinToString()}](sourceCellView)")
        println("compare(tableView[Int.MAX_VALUE][${headers.joinToString()}])")

        // tableView[long][label1, label2, label3, labelX] = value
        println()
        println("tableView[Long.MAX_VALUE][${headers.joinToString()}](sourceCellView)")
        println("compare(tableView[Long.MAX_VALUE][${headers.joinToString()}])")

        // tableView[row][label1, label2, label3, labelX] = value
        println()
        println("tableView[row][${headers.joinToString()}](sourceCellView)")
        println("compare(tableView[row][${headers.joinToString()}])")

        // tableView[rowview][label1, label2, label3, labelX] = value
        println()
        println("tableView[rowView][${headers.joinToString()}](sourceCellView)")
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
        println("tableView[Header[${headers.joinToString()}], Int.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[Header[${headers.joinToString()}], Int.MAX_VALUE])")

        // tableView[header, long] = value
        println()
        println("tableView[Header[${headers.joinToString()}], Long.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[Header[${headers.joinToString()}], Long.MAX_VALUE])")

        // tableView[header, row] = value
        println()
        println("tableView[Header[${headers.joinToString()}], row] = { this(sourceCellView) }")
        println("compare(tableView[Header[${headers.joinToString()}], row])")

        // tableView[header, rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}], rowView] = { this(sourceCellView) }")
        println("compare(tableView[Header[${headers.joinToString()}], rowView])")

        // tableView[header][int] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE])")

        // tableView[header][long] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE])")

        // tableView[header][row] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][row] = { this(sourceCellView) }")
        println("compare(tableView[Header[${headers.joinToString()}]][row])")

        // tableView[header][rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][rowView] = { this(sourceCellView) }")
        println("compare(tableView[Header[${headers.joinToString()}]][rowView])")

        // tableView[int][header] = value
        println()
        println("tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]] = { this(sourceCellView) }")
        println("compare(tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[long][header] = value
        println()
        println("tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]] = { this(sourceCellView) }")
        println("compare(tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[row][header] = value
        println()
        println("tableView[row][Header[${headers.joinToString()}]] = { this(sourceCellView) }")
        println("compare(tableView[row][Header[${headers.joinToString()}]])")

        // tableView[rowview][header] = value
        println()
        println("tableView[rowView][Header[${headers.joinToString()}]] = { this(sourceCellView) }")
        println("compare(tableView[rowView][Header[${headers.joinToString()}]])")

        // tableView[column, int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[column, long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[column, row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], row] = { this(sourceCellView) }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], row])")

        // tableView[column, rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], rowView] = { this(sourceCellView) }")
        println("compare(tableView[table[Header[${headers.joinToString()}]], rowView])")

        // tableView[column][int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[column][long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[column][row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][row] = { this(sourceCellView) }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][row])")

        // tableView[column][rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][rowView] = { this(sourceCellView) }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][column] = value
        println()
        println("tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]] = { this(sourceCellView) }")
        println("compare(tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[long][column] = value
        println()
        println("tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]] = { this(sourceCellView) }")
        println("compare(tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[row][column] = value
        println()
        println("tableView[row][table[Header[${headers.joinToString()}]]] = { this(sourceCellView) }")
        println("compare(tableView[row][table[Header[${headers.joinToString()}]]])")

        // tableView[rowview][column] = value
        println()
        println("tableView[rowView][table[Header[${headers.joinToString()}]]] = { this(sourceCellView) }")
        println("compare(tableView[rowView][table[Header[${headers.joinToString()}]]])")

        // tableView[columnView, int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[columnView, long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[columnView, row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], row] = { this(sourceCellView) }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], row])")

        // tableView[columnView, rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], rowView] = { this(sourceCellView) }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], rowView])")

        // tableView[columnView][int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[columnView][long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = { this(sourceCellView) }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[columnView][row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][row] = { this(sourceCellView) }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][row])")

        // tableView[columnView][rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][rowView] = { this(sourceCellView) }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][columnView] = value
        println()
        println("tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]] = { this(sourceCellView) }")
        println("compare(tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[long][columnView] = value
        println()
        println("tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]] = { this(sourceCellView) }")
        println("compare(tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[row][columnView] = value
        println()
        println("tableView[row][tableView[Header[${headers.joinToString()}]]] = { this(sourceCellView) }")
        println("compare(tableView[row][tableView[Header[${headers.joinToString()}]]])")

        // tableView[rowview][columnView] = value
        println()
        println("tableView[rowView][tableView[Header[${headers.joinToString()}]]] = { this(sourceCellView) }")
        println("compare(tableView[rowView][tableView[Header[${headers.joinToString()}]]])")

    }

    for (i in 5..6) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[header, int] = value
        println()
        println("tableView[Header[${headers.joinToString()}], Int.MAX_VALUE](sourceCellView)")
        println("compare(tableView[Header[${headers.joinToString()}], Int.MAX_VALUE])")

        // tableView[header, long] = value
        println()
        println("tableView[Header[${headers.joinToString()}], Long.MAX_VALUE](sourceCellView)")
        println("compare(tableView[Header[${headers.joinToString()}], Long.MAX_VALUE])")

        // tableView[header, row] = value
        println()
        println("tableView[Header[${headers.joinToString()}], row](sourceCellView)")
        println("compare(tableView[Header[${headers.joinToString()}], row])")

        // tableView[header, rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}], rowView](sourceCellView)")
        println("compare(tableView[Header[${headers.joinToString()}], rowView])")

        // tableView[header][int] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE](sourceCellView)")
        println("compare(tableView[Header[${headers.joinToString()}]][Int.MAX_VALUE])")

        // tableView[header][long] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE](sourceCellView)")
        println("compare(tableView[Header[${headers.joinToString()}]][Long.MAX_VALUE])")

        // tableView[header][row] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][row](sourceCellView)")
        println("compare(tableView[Header[${headers.joinToString()}]][row])")

        // tableView[header][rowview] = value
        println()
        println("tableView[Header[${headers.joinToString()}]][rowView](sourceCellView)")
        println("compare(tableView[Header[${headers.joinToString()}]][rowView])")

        // tableView[int][header] = value
        println()
        println("tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]](sourceCellView)")
        println("compare(tableView[Int.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[long][header] = value
        println()
        println("tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]](sourceCellView)")
        println("compare(tableView[Long.MAX_VALUE][Header[${headers.joinToString()}]])")

        // tableView[row][header] = value
        println()
        println("tableView[row][Header[${headers.joinToString()}]](sourceCellView)")
        println("compare(tableView[row][Header[${headers.joinToString()}]])")

        // tableView[rowview][header] = value
        println()
        println("tableView[rowView][Header[${headers.joinToString()}]](sourceCellView)")
        println("compare(tableView[rowView][Header[${headers.joinToString()}]])")

        // tableView[column, int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE](sourceCellView)")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[column, long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE](sourceCellView)")
        println("compare(tableView[table[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[column, row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], row](sourceCellView)")
        println("compare(tableView[table[Header[${headers.joinToString()}]], row])")

        // tableView[column, rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]], rowView](sourceCellView)")
        println("compare(tableView[table[Header[${headers.joinToString()}]], rowView])")

        // tableView[column][int] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE](sourceCellView)")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[column][long] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE](sourceCellView)")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[column][row] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][row](sourceCellView)")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][row])")

        // tableView[column][rowview] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]][rowView](sourceCellView)")
        println("compare(tableView[table[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][column] = value
        println()
        println("tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]](sourceCellView)")
        println("compare(tableView[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[long][column] = value
        println()
        println("tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]](sourceCellView)")
        println("compare(tableView[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]])")

        // tableView[row][column] = value
        println()
        println("tableView[row][table[Header[${headers.joinToString()}]]](sourceCellView)")
        println("compare(tableView[row][table[Header[${headers.joinToString()}]]])")

        // tableView[rowview][column] = value
        println()
        println("tableView[rowView][table[Header[${headers.joinToString()}]]](sourceCellView)")
        println("compare(tableView[rowView][table[Header[${headers.joinToString()}]]])")

        // tableView[columnView, int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE](sourceCellView)")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Int.MAX_VALUE])")

        // tableView[columnView, long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE](sourceCellView)")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], Long.MAX_VALUE])")

        // tableView[columnView, row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], row](sourceCellView)")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], row])")

        // tableView[columnView, rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]], rowView](sourceCellView)")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]], rowView])")

        // tableView[columnView][int] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE](sourceCellView)")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Int.MAX_VALUE])")

        // tableView[columnView][long] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE](sourceCellView)")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][Long.MAX_VALUE])")

        // tableView[columnView][row] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][row](sourceCellView)")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][row])")

        // tableView[columnView][rowview] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]][rowView](sourceCellView)")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]][rowView])")

        // tableView[int][columnView] = value
        println()
        println("tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]](sourceCellView)")
        println("compare(tableView[Int.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[long][columnView] = value
        println()
        println("tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]](sourceCellView)")
        println("compare(tableView[Long.MAX_VALUE][tableView[Header[${headers.joinToString()}]]])")

        // tableView[row][columnView] = value
        println()
        println("tableView[row][tableView[Header[${headers.joinToString()}]]](sourceCellView)")
        println("compare(tableView[row][tableView[Header[${headers.joinToString()}]]])")

        // tableView[rowview][columnView] = value
        println()
        println("tableView[rowView][tableView[Header[${headers.joinToString()}]]](sourceCellView)")
        println("compare(tableView[rowView][tableView[Header[${headers.joinToString()}]]])")

    }

    println("""
            tableView[tableView["L1", 1000]] = sourceCellView
            compare(tableView[tableView["L1", 1000]])

            tableView[tableView["L1", 1001]] = { this(sourceCellView) }
            compare(tableView[tableView["L1", 1001]])

            tableView[tableView["L1", 1002]](sourceCellView)
            compare(tableView[tableView["L1", 1002]])

            tableView[table["L1", 2000]] = sourceCellView
            compare(tableView[table["L1", 2000]])

            tableView[table["L1", 2001]] = { this(sourceCellView) }
            compare(tableView[table["L1", 2001]])

            tableView[table["L1", 2002]](sourceCellView)
            compare(tableView[table["L1", 2002]])
    """.trimIndent())

    println("}")
    println()
}

fun generateForColumnView() {
    println("@Test")
    println("fun `columnview accessors`() {")
    println("val table = Table[\"\${this.javaClass.simpleName} \${object {}.javaClass.enclosingMethod.name}\"]")

    println("""
        
        val tableView = TableView[table]

        tableView["A"][CellWidth] = 2000
        tableView["A"][CellClasses] = "cc-1"
        tableView["A"][CellTopics] = "ct-1"
        val ct: Column.() -> Unit = {}
        tableView["A"][ColumnTransformer] = ct

        val sourceColumnView = tableView["A"]

        fun compare(columnView: ColumnView) {
            assertEquals(2000L, sourceColumnView[CellWidth].width)
            assertEquals(setOf("cc-1"), sourceColumnView[CellClasses].classes)
            assertEquals(setOf("ct-1"), sourceColumnView[CellTopics].topics)
            assertEquals(ct, sourceColumnView[ColumnTransformer].function)

            assertEquals(sourceColumnView[CellWidth].width, columnView[CellWidth].width)
            assertEquals(sourceColumnView[CellClasses].classes, columnView[CellClasses].classes)
            assertEquals(sourceColumnView[CellTopics].topics, columnView[CellTopics].topics)
            assertEquals(sourceColumnView[ColumnTransformer].function, columnView[ColumnTransformer].function)

            clear(columnView)
        }
    """.trimIndent())

    for (i in 1..5) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[label1, label2, label3, labelX] = value
        println()
        println("tableView[${headers.joinToString()}] = sourceColumnView")
        println("compare(tableView[${headers.joinToString()}])")
    }

    for (i in 1..5) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[label1, label2, label3, labelX] = value
        println()
        println("tableView[${headers.joinToString()}] = { this { sourceColumnView } }")
        println("compare(tableView[${headers.joinToString()}])")
    }

    for (i in 1..5) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[label1, label2, label3, labelX] = value
        println()
        println("tableView[${headers.joinToString()}] { sourceColumnView }")
        println("compare(tableView[${headers.joinToString()}])")
    }

    for (i in 5..6) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[header] = value
        println()
        println("tableView[Header[${headers.joinToString()}]] = sourceColumnView")
        println("compare(tableView[Header[${headers.joinToString()}]])")

        // tableView[column] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]] = sourceColumnView")
        println("compare(tableView[table[Header[${headers.joinToString()}]]])")

        // tableView[columnView] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]] = sourceColumnView")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]])")
    }

    for (i in 5..6) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[header] = value
        println()
        println("tableView[Header[${headers.joinToString()}]] = { this { sourceColumnView } }")
        println("compare(tableView[Header[${headers.joinToString()}]])")

        // tableView[column] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]] = { this { sourceColumnView } }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]])")

        // tableView[columnView] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]] = { this { sourceColumnView } }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]])")
    }

    for (i in 5..6) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }

        // tableView[header] = value
        println()
        println("tableView[Header[${headers.joinToString()}]] { sourceColumnView }")
        println("compare(tableView[Header[${headers.joinToString()}]])")

        // tableView[column] = value
        println()
        println("tableView[table[Header[${headers.joinToString()}]]] { sourceColumnView }")
        println("compare(tableView[table[Header[${headers.joinToString()}]]])")

        // tableView[columnView] = value
        println()
        println("tableView[tableView[Header[${headers.joinToString()}]]] { sourceColumnView }")
        println("compare(tableView[tableView[Header[${headers.joinToString()}]]])")
    }

    println("}")
    println()
}

fun generateForRowView() {
    println("@Test")
    println("fun `rowview accessors`() {")
    println("val table = Table[\"\${this.javaClass.simpleName} \${object {}.javaClass.enclosingMethod.name}\"]")

    println("""
        
        val tableView = TableView[table]

        tableView[1][CellHeight] = 1000
        tableView[1][CellClasses] = "cc-1"
        tableView[1][CellTopics] = "ct-1"
        val ct: Row.() -> Unit = {}
        tableView[1][RowTransformer] = ct

        val sourceRowView = tableView[1]

        fun compare(rowView: RowView) {
            assertEquals(1000L, sourceRowView[CellHeight].height)
            assertEquals(setOf("cc-1"), sourceRowView[CellClasses].classes)
            assertEquals(setOf("ct-1"), sourceRowView[CellTopics].topics)
            assertEquals(ct, sourceRowView[RowTransformer].function)

            assertEquals(sourceRowView[CellHeight].height, rowView[CellHeight].height)
            assertEquals(sourceRowView[CellClasses].classes, rowView[CellClasses].classes)
            assertEquals(sourceRowView[CellTopics].topics, rowView[CellTopics].topics)
            assertEquals(sourceRowView[RowTransformer].function, rowView[RowTransformer].function)

            clear(rowView)
        }
    """.trimIndent())

    // tableView[int] = value
    println()
    println("tableView[Int.MAX_VALUE] = sourceRowView")
    println("compare(tableView[Int.MAX_VALUE])")

    // tableView[long] = value
    println()
    println("tableView[Long.MAX_VALUE] = sourceRowView")
    println("compare(tableView[Long.MAX_VALUE])")

    // tableView[row] = value
    println()
    println("tableView[table[2000]] = sourceRowView")
    println("compare(tableView[table[2000]])")

    // tableView[rowview] = value
    println()
    println("tableView[tableView[3000]] = sourceRowView")
    println("compare(tableView[tableView[3000]])")

    // tableView[int] = value
    println()
    println("tableView[Int.MAX_VALUE] = { this { sourceRowView } }")
    println("compare(tableView[Int.MAX_VALUE])")

    // tableView[long] = value
    println()
    println("tableView[Long.MAX_VALUE] = { this { sourceRowView } }")
    println("compare(tableView[Long.MAX_VALUE])")

    // tableView[row] = value
    println()
    println("tableView[table[2000]] = { this { sourceRowView } }")
    println("compare(tableView[table[2000]])")

    // tableView[rowview] = value
    println()
    println("tableView[tableView[3000]] = { this { sourceRowView } }")
    println("compare(tableView[tableView[3000]])")

    // tableView[int] = value
    println()
    println("tableView[Int.MAX_VALUE] { sourceRowView }")
    println("compare(tableView[Int.MAX_VALUE])")

    // tableView[long] = value
    println()
    println("tableView[Long.MAX_VALUE] { sourceRowView }")
    println("compare(tableView[Long.MAX_VALUE])")

    // tableView[row] = value
    println()
    println("tableView[table[2000]] { sourceRowView }")
    println("compare(tableView[table[2000]])")

    // tableView[rowview] = value
    println()
    println("tableView[tableView[3000]] { sourceRowView }")
    println("compare(tableView[tableView[3000]])")

    println("}")
    println()
}

generateForCellView()
generateForColumnView()
generateForRowView()
