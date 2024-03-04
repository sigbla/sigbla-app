#!/usr/bin/env kscript

// Cases:
// table[label1, label2, label3, labelX, int|long|row] = value
// table[label1, label2, label3, labelX][int|long|row] = value
// table[int|long|row][label1, label2, label3, labelX] = value
// table[header|column, int|long|row] = value
// table[header|column][int|long|row] = value
// table[int|long|row][header|column] = value
// table[cell] = value

// + the Cell<*>.() -> Any? assignment pattern

// + these read cases with index relations

// table[label1, label2, label3, labelX, index relation, int|long]
// table[label1, label2, label3, labelX][index relation, int|long]
// table[index relation, int|long][label1, label2, label3, labelX]
// table[header|column, index relation, int|long]
// table[header|column][index relation, int|long]
// table[index relation, int|long][header|column]

// Values:
// Cell, String, Int, Long, Float, Double, BigInteger, BigDecimal, Number

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

        println()
        println("table[${headers.joinToString()}, Int.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[${headers.joinToString()}, Int.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Int.MAX_VALUE].value)")

        // table[label1, label2, label3, labelX, long] = value
        println()
        println("table[${headers.joinToString()}, Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[${headers.joinToString()}, Long.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[${headers.joinToString()}, Long.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}, Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}, IndexRelation.AT, Long.MAX_VALUE].value)")

        // table[label1, label2, label3, labelX, row] = value
        println()
        println("table[${headers.joinToString()}, table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}, table[Long.MAX_VALUE]].value)")

        println()
        println("table[${headers.joinToString()}, table[Long.MAX_VALUE]] { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}, table[Long.MAX_VALUE]].value)")

        println()
        println("table[${headers.joinToString()}, table[Long.MAX_VALUE]] = { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}, table[Long.MAX_VALUE]].value)")

        // table[label1, label2, label3, labelX][int] = value
        println()
        println("table[${headers.joinToString()}][Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[${headers.joinToString()}][Int.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[${headers.joinToString()}][Int.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Int.MAX_VALUE].value)")

        // table[label1, label2, label3, labelX][long] = value
        println()
        println("table[${headers.joinToString()}][Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[${headers.joinToString()}][Long.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[${headers.joinToString()}][Long.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[${headers.joinToString()}][IndexRelation.AT, Long.MAX_VALUE].value)")

        // table[label1, label2, label3, labelX][row] = value
        println()
        println("table[${headers.joinToString()}][table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[${headers.joinToString()}][table[Long.MAX_VALUE]].value)")

        println()
        println("table[${headers.joinToString()}][table[Long.MAX_VALUE]] { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}][table[Long.MAX_VALUE]].value)")

        println()
        println("table[${headers.joinToString()}][table[Long.MAX_VALUE]] = { cell }")
        println("assertEquals($cellValue, table[${headers.joinToString()}][table[Long.MAX_VALUE]].value)")

        // table[int][label1, label2, label3, labelX] = value
        println()
        println("table[Int.MAX_VALUE][${headers.joinToString()}] = cell")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][${headers.joinToString()}].value)")

        println()
        println("table[Int.MAX_VALUE][${headers.joinToString()}] { cell }")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][${headers.joinToString()}].value)")

        println()
        println("table[Int.MAX_VALUE][${headers.joinToString()}] = { cell }")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][${headers.joinToString()}].value)")

        // table[long][label1, label2, label3, labelX] = value
        println()
        println("table[Long.MAX_VALUE][${headers.joinToString()}] = cell")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][${headers.joinToString()}].value)")

        println()
        println("table[Long.MAX_VALUE][${headers.joinToString()}] { cell }")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][${headers.joinToString()}].value)")

        println()
        println("table[Long.MAX_VALUE][${headers.joinToString()}] = { cell }")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][${headers.joinToString()}].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][${headers.joinToString()}].value)")

        // table[row][label1, label2, label3, labelX] = value
        println()
        println("table[table[Long.MAX_VALUE]][${headers.joinToString()}] = cell")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][${headers.joinToString()}].value)")

        println()
        println("table[table[Long.MAX_VALUE]][${headers.joinToString()}] { cell }")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][${headers.joinToString()}].value)")

        println()
        println("table[table[Long.MAX_VALUE]][${headers.joinToString()}] = { cell }")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][${headers.joinToString()}].value)")
    }

    for (i in 5..6) {
        val headers = (1..i).fold(mutableListOf<String>()) { acc, i -> acc.apply { add("\"L$i\"") } }
        // table[header, int] = value
        println()
        println("table[Header[${headers.joinToString()}], Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[Header[${headers.joinToString()}], Int.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[Header[${headers.joinToString()}], Int.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Int.MAX_VALUE].value)")

        // table[header, long] = value
        println()
        println("table[Header[${headers.joinToString()}], Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[Header[${headers.joinToString()}], Long.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[Header[${headers.joinToString()}], Long.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], IndexRelation.AT, Long.MAX_VALUE].value)")

        // table[header, row] = value
        println()
        println("table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]].value)")

        println()
        println("table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]] { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]].value)")

        println()
        println("table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]] = { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}], table[Long.MAX_VALUE]].value)")

        // table[column, int] = value
        println()
        println("table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Int.MAX_VALUE].value)")

        // table[column, long] = value
        println()
        println("table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], IndexRelation.AT, Long.MAX_VALUE].value)")

        // table[column, row] = value
        println()
        println("table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]] { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]] = { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]], table[Long.MAX_VALUE]].value)")

        // table[header][int] = value
        println()
        println("table[Header[${headers.joinToString()}]][Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[Header[${headers.joinToString()}]][Int.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[Header[${headers.joinToString()}]][Int.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Int.MAX_VALUE].value)")

        // table[header][long] = value
        println()
        println("table[Header[${headers.joinToString()}]][Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[Header[${headers.joinToString()}]][Long.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[Header[${headers.joinToString()}]][Long.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][IndexRelation.AT, Long.MAX_VALUE].value)")

        // table[header][row] = value
        println()
        println("table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]].value)")

        println()
        println("table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]] { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]].value)")

        println()
        println("table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]] = { cell }")
        println("assertEquals($cellValue, table[Header[${headers.joinToString()}]][table[Long.MAX_VALUE]].value)")

        // table[column][int] = value
        println()
        println("table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Int.MAX_VALUE].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Int.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Int.MAX_VALUE].value)")

        // table[column][long] = value
        println()
        println("table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE] { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Long.MAX_VALUE].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE] = { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][Long.MAX_VALUE].value)")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][IndexRelation.AT, Long.MAX_VALUE].value)")

        // table[column][row] = value
        println()
        println("table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]] = cell")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]] { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]].value)")

        println()
        println("table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]] = { cell }")
        println("assertEquals($cellValue, table[table[Header[${headers.joinToString()}]]][table[Long.MAX_VALUE]].value)")

        // table[int][header] = value
        println()
        println("table[Int.MAX_VALUE][Header[${headers.joinToString()}]] = cell")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")

        println()
        println("table[Int.MAX_VALUE][Header[${headers.joinToString()}]] { cell }")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")

        println()
        println("table[Int.MAX_VALUE][Header[${headers.joinToString()}]] = { cell }")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][Header[${headers.joinToString()}]].value)")

        // table[long][header] = value
        println()
        println("table[Long.MAX_VALUE][Header[${headers.joinToString()}]] = cell")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")

        println()
        println("table[Long.MAX_VALUE][Header[${headers.joinToString()}]] { cell }")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")

        println()
        println("table[Long.MAX_VALUE][Header[${headers.joinToString()}]] = { cell }")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][Header[${headers.joinToString()}]].value)")

        // table[row][header] = value
        println()
        println("table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]] = cell")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]].value)")

        println()
        println("table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]] { cell }")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]].value)")

        println()
        println("table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]] = { cell }")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][Header[${headers.joinToString()}]].value)")

        // table[int][column] = value
        println()
        println("table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]] = cell")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")

        println()
        println("table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]] { cell }")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")

        println()
        println("table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]] = { cell }")
        println("assertEquals($cellValue, table[Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Int.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")

        // table[long][column] = value
        println()
        println("table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]] = cell")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")

        println()
        println("table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]] { cell }")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")

        println()
        println("table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]] = { cell }")
        println("assertEquals($cellValue, table[Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")
        println("assertEquals($cellValue, table[IndexRelation.AT, Long.MAX_VALUE][table[Header[${headers.joinToString()}]]].value)")

        // table[row][column] = value
        println()
        println("table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]] = cell")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]].value)")

        println()
        println("table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]] { cell }")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]].value)")

        println()
        println("table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]] = { cell }")
        println("assertEquals($cellValue, table[table[Long.MAX_VALUE]][table[Header[${headers.joinToString()}]]].value)")
    }

    // table[cell] = value
    println()
    println("table[table[\"L1\", Int.MAX_VALUE]] = cell")
    println("assertEquals($cellValue, table[table[\"L1\", Int.MAX_VALUE]].value)")

    println()
    println("table[table[\"L1\", Int.MAX_VALUE]] { cell }")
    println("assertEquals($cellValue, table[table[\"L1\", Int.MAX_VALUE]].value)")

    println()
    println("table[table[\"L1\", Int.MAX_VALUE]] = { cell }")
    println("assertEquals($cellValue, table[table[\"L1\", Int.MAX_VALUE]].value)")

    println("}")
    println()
}

fun generateForCell() {
    generateForX("cell", """
        val cell = Table[null].let { 
            it["A", 0] = "A0"
            it["A", 0]
        }
    """.trimIndent())
}

fun generateForString() {
    generateForX("string", "val cell = \"string\"")
}

fun generateForInt() {
    generateForX("int", "val cell = Int.MAX_VALUE", "cell.toLong()")
}

fun generateForLong() {
    generateForX("long", "val cell = Long.MAX_VALUE")
}

fun generateForFloat() {
    generateForX("float", "val cell = Float.MAX_VALUE", "cell.toDouble()")
}

fun generateForDouble() {
    generateForX("double", "val cell = Double.MAX_VALUE")
}

fun generateForBigInteger() {
    generateForX("bigint", "val cell = BigInteger.TEN")
}

fun generateForBigDecimal() {
    generateForX("bigdecimal", "val cell = BigDecimal.TEN")
}

fun generateForNumber() {
    generateForX("number", "val cell = 123 as Number", "cell.toLong()")
}

fun generateForBool() {
    generateForX("boolean", "val cell = true")
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
generateForBool()