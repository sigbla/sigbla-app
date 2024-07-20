#!/usr/bin/env kscript

// Cases:
// table[label1, label2, label3, labelX, int|long|row] = value
// table[label1, label2, label3, labelX][int|long|row] = value
// table[int|long|row][label1, label2, label3, labelX] = value
// table[header|column, int|long|row] = value
// table[header|column][int|long|row] = value
// table[int|long|row][header|column] = value
// table[cell] = value

// + invoke assignment pattern

// + these read cases with index relations

// table[label1, label2, label3, labelX, index relation, int|long]
// table[label1, label2, label3, labelX][index relation, int|long]
// table[index relation, int|long][label1, label2, label3, labelX]
// table[header|column, index relation, int|long]
// table[header|column][index relation, int|long]
// table[index relation, int|long][header|column]

// Values:
// Cell, Unit, Boolean, String, Int, Long, Float, Double, BigInteger, BigDecimal, Number, LocalDate, LocalTime, LocalDateTime, ZonedDateTime

fun generateForX(type: String, cellGenerator: String, cellValue: String = "cell") {
    println("@Test")
    println("fun `$type accessors`() {")

    println(cellGenerator)
    println()
    println("val table = Table[\"\${this.javaClass.simpleName} \${object {}.javaClass.enclosingMethod.name}\"]")

    for (i in 1..5) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }
        // table[label1, label2, label3, labelX, int] = value
        println()
        println("table[${headers.joinToString()}, Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}, Int.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}, Int.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        // table[label1, label2, label3, labelX, long] = value
        println()
        println("table[${headers.joinToString()}, Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}, Long.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}, Long.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        // table[label1, label2, label3, labelX, row] = value
        println()
        println("table[${headers.joinToString()}, table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}, table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}, table[Long.MAX_VALUE]](cell)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}, table[Long.MAX_VALUE]] = { this(cell) }")
        println("assertEquals($cellValue, table[${headers.joinToString()}, table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        // table[label1, label2, label3, labelX][int] = value
        println()
        println("table[${headers.joinToString()}][Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}][Int.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}][Int.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        // table[label1, label2, label3, labelX][long] = value
        println()
        println("table[${headers.joinToString()}][Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}][Long.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}][Long.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        // table[label1, label2, label3, labelX][row] = value
        println()
        println("table[${headers.joinToString()}][table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}][table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}][table[Long.MAX_VALUE]](cell)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[${headers.joinToString()}][table[Long.MAX_VALUE]] = { this(cell) }")
        println("assertEquals($cellValue, table[${headers.joinToString()}][table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        // table[int][label1, label2, label3, labelX] = value
        println()
        println("table[Int.MAX_VALUE][${headers.joinToString()}] = cell")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][${headers.joinToString()}].value)")
        println("clear(table)")

        println()
        println("table[Int.MAX_VALUE][${headers.joinToString()}](cell)")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][${headers.joinToString()}].value)")
        println("clear(table)")

        println()
        println("table[Int.MAX_VALUE][${headers.joinToString()}] = { this(cell) }")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][${headers.joinToString()}].value)")
        println("clear(table)")

        // table[long][label1, label2, label3, labelX] = value
        println()
        println("table[Long.MAX_VALUE][${headers.joinToString()}] = cell")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][${headers.joinToString()}].value)")
        println("clear(table)")

        println()
        println("table[Long.MAX_VALUE][${headers.joinToString()}](cell)")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][${headers.joinToString()}].value)")
        println("clear(table)")

        println()
        println("table[Long.MAX_VALUE][${headers.joinToString()}] = { this(cell) }")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][${headers.joinToString()}].value)")
        println("clear(table)")

        // table[row][label1, label2, label3, labelX] = value
        println()
        println("table[table[Long.MAX_VALUE]][${headers.joinToString()}] = cell")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][${headers.joinToString()}].value)")
        println("clear(table)")

        println()
        println("table[table[Long.MAX_VALUE]][${headers.joinToString()}](cell)")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][${headers.joinToString()}].value)")
        println("clear(table)")

        println()
        println("table[table[Long.MAX_VALUE]][${headers.joinToString()}] = { this(cell) }")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][${headers.joinToString()}].value)")
        println("clear(table)")
    }

    for (i in 5..6) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }
        // table[header, int] = value
        println()
        println("table[Header[${headers.joinToString()}], Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}], Int.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}], Int.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        // table[header, long] = value
        println()
        println("table[Header[${headers.joinToString()}], Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}], Long.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}], Long.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        // table[header, row] = value
        println()
        println("table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]](cell)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]] = { this(cell) }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        // table[column, int] = value
        println()
        println("table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        // table[column, long] = value
        println()
        println("table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        // table[column, row] = value
        println()
        println("table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]](cell)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]] = { this(cell) }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        // table[header][int] = value
        println()
        println("table[Header[${headers.joinToString()}]][Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}]][Int.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}]][Int.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        // table[header][long] = value
        println()
        println("table[Header[${headers.joinToString()}]][Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}]][Long.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}]][Long.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        // table[header][row] = value
        println()
        println("table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]](cell)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]] = { this(cell) }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        // table[column][int] = value
        println()
        println("table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Int.MAX_VALUE].value)")
        println("clear(table)")

        // table[column][long] = value
        println()
        println("table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE](cell)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = { this(cell) }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Long.MAX_VALUE].value)")
        println("clear(table)")

        // table[column][row] = value
        println()
        println("table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]](cell)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]] = { this(cell) }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]].value)")
        println("clear(table)")

        // table[int][header] = value
        println()
        println("table[Int.MAX_VALUE][Header[${headers.joinToString()}]] = cell")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("clear(table)")

        println()
        println("table[Int.MAX_VALUE][Header[${headers.joinToString()}]](cell)")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("clear(table)")

        println()
        println("table[Int.MAX_VALUE][Header[${headers.joinToString()}]] = { this(cell) }")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("clear(table)")

        // table[long][header] = value
        println()
        println("table[Long.MAX_VALUE][Header[${headers.joinToString()}]] = cell")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("clear(table)")

        println()
        println("table[Long.MAX_VALUE][Header[${headers.joinToString()}]](cell)")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("clear(table)")

        println()
        println("table[Long.MAX_VALUE][Header[${headers.joinToString()}]] = { this(cell) }")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("clear(table)")

        // table[row][header] = value
        println()
        println("table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]] = cell")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]].value)")
        println("clear(table)")

        println()
        println("table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]](cell)")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]].value)")
        println("clear(table)")

        println()
        println("table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]] = { this(cell) }")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]].value)")
        println("clear(table)")

        // table[int][column] = value
        println()
        println("table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]] = cell")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("clear(table)")

        println()
        println("table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]](cell)")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("clear(table)")

        println()
        println("table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]] = { this(cell) }")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("clear(table)")

        // table[long][column] = value
        println()
        println("table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]] = cell")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("clear(table)")

        println()
        println("table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]](cell)")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("clear(table)")

        println()
        println("table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]] = { this(cell) }")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("clear(table)")

        // table[row][column] = value
        println()
        println("table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]] = cell")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]].value)")
        println("clear(table)")

        println()
        println("table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]](cell)")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]].value)")
        println("clear(table)")

        println()
        println("table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]] = { this(cell) }")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]].value)")
        println("clear(table)")
    }

    // table[cell] = value
    println()
    println("table[table[\"L1\", Int.MAX_VALUE]] = cell")
    println("assertEquals($cellValue, table[table[\"L1\", Int.MAX_VALUE]].value)")
    println("clear(table)")

    println()
    println("table[table[\"L1\", Int.MAX_VALUE]](cell)")
    println("assertEquals($cellValue, table[table[\"L1\", Int.MAX_VALUE]].value)")
    println("clear(table)")

    println()
    println("table[table[\"L1\", Int.MAX_VALUE]] = { this(cell) }")
    println("assertEquals($cellValue, table[table[\"L1\", Int.MAX_VALUE]].value)")
    println("clear(table)")

    println("}")
    println()
}

fun generateForCell() {
    generateForX("cell", """
        val cell = Table[null].let { 
            it["A", 0] = "A0"
            it["A", 0]
        }
    """.trimIndent(), "cell.value")
    generateForX("null cell", "val cell: Cell<*>? = null", "Unit")
}

fun generateForString() {
    generateForX("string", "val cell = \"string\"")
    generateForX("null string", "val cell: String? = null", "Unit")
}

fun generateForInt() {
    generateForX("int", "val cell = Int.MAX_VALUE", "cell.toLong()")
    generateForX("null int", "val cell: Int? = null", "Unit")
}

fun generateForLong() {
    generateForX("long", "val cell = Long.MAX_VALUE")
    generateForX("null long", "val cell: Long? = null", "Unit")
}

fun generateForFloat() {
    generateForX("float", "val cell = Float.MAX_VALUE", "cell.toDouble()")
    generateForX("null float", "val cell: Float? = null", "Unit")
}

fun generateForDouble() {
    generateForX("double", "val cell = Double.MAX_VALUE")
    generateForX("null double", "val cell: Double? = null", "Unit")
}

fun generateForBigInteger() {
    generateForX("bigint", "val cell = BigInteger.TEN")
    generateForX("null bigint", "val cell: BigInteger? = null", "Unit")
}

fun generateForBigDecimal() {
    generateForX("bigdecimal", "val cell = BigDecimal.TEN")
    generateForX("null bigdecimal", "val cell: BigDecimal? = null", "Unit")
}

fun generateForNumber() {
    generateForX("number", "val cell = 123 as Number", "cell.toLong()")
    generateForX("null number", "val cell: Number? = null", "Unit")
}

fun generateForLocalDate() {
    generateForX("localdate", "val cell = LocalDate.now()")
    generateForX("null localdate", "val cell: LocalDate? = null", "Unit")
}

fun generateForLocalTime() {
    generateForX("localtime", "val cell = LocalTime.now()")
    generateForX("null localtime", "val cell: LocalTime? = null", "Unit")
}

fun generateForLocalDateTime() {
    generateForX("localdatetime", "val cell = LocalDateTime.now()")
    generateForX("null localdatetime", "val cell: LocalDateTime? = null", "Unit")
}

fun generateForZonedDateTime() {
    generateForX("zoneddatetime", "val cell = ZonedDateTime.now()")
    generateForX("null zoneddatetime", "val cell: ZonedDateTime? = null", "Unit")
}

fun generateForTemporal() {
    generateForX("temporal", "val cell = ZonedDateTime.now() as Temporal")
    generateForX("null temporal", "val cell: Temporal? = null", "Unit")
}

fun generateForBool() {
    generateForX("boolean", "val cell = true")
    generateForX("null boolean", "val cell: Boolean? = null", "Unit")
}

fun generateForUnit() {
    generateForX("unit", "val cell = Unit")
    generateForX("null unit", "val cell: Unit? = null", "Unit")
}

generateForCell()
generateForString()
generateForInt()
generateForLong()
generateForFloat()
generateForDouble()
generateForBigInteger()
generateForBigDecimal()
generateForNumber()
generateForLocalDate()
generateForLocalTime()
generateForLocalDateTime()
generateForZonedDateTime()
generateForTemporal()
generateForBool()
generateForUnit()