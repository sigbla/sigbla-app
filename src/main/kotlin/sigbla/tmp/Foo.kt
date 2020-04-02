package sigbla.tmp

import sigbla.app.IndexRelation.*
import sigbla.app.timeseries.*
import sigbla.app.Table.Companion.move
import sigbla.app.Table.Companion.subscribe
import sigbla.app.*
import java.math.BigDecimal
import java.math.BigInteger

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun main() {
    val table = Table.newTable("Table A")

    table["A", "B"][1] = "Test"
    val cell1 = table["A", "B"][BEFORE, 1]
    val cell2 = table[AFTER, 1]["A", "B"]
    val cell3 = table["A", "B"][1]
    val cell4 = table[1]["A", "B"]

    table[1][ColumnHeader("A")] = "A"
    table[1]["A", "B"] = "A"

    val tickerHeaders = table.headers.filter { it[2] == "Ticker" }

    val prices = tickerHeaders.map { header -> Pair(header, table[header][AT_OR_BEFORE, 1000]) }.toMap()

    table["A"][AT, LocalDate.now(), LocalTime.MAX, ZoneId.systemDefault()]

    val size = 10_000L

    val start1 = System.currentTimeMillis()
    for (i in 1.toLong()..size) {
        table["B"][i] = i

        if (i % 1_000_000L == 0L) println(i)
    }
    val end1 = System.currentTimeMillis()

    println()

    val start2 = System.currentTimeMillis()
    var foo = 0L as Number
    for (i in 1.toLong()..size) {
        foo += table["B"][i]

        if (i % 1_000_000L == 0L) println(i)
    }
    val end2 = System.currentTimeMillis()

    println()
    println("Write: " + (end1-start1))
    println("Read: " + (end2-start2))

    //Thread.sleep(Long.MAX_VALUE)

    table["B"][1].isNumeric()
    table["B"][1].isText()

    table["B"][3] = table["B"][1] + table["B"][2]
    table[1]["B"] = table["A"][1]

    table["A", 1]
    table["A", "B", BEFORE, 1]
    table["A", "B"] before 1

    val math1a = 1 + table["B", 10]
    val math2a = 1L + table["B", 10]
    val math3a = 1.0F + table["B", 10]
    val math4a = 1.0 + table["B", 10]
    val math5a = BigInteger.ONE + table["B", 10]
    val math6a = BigDecimal.ONE + table["B", 10]
    val math7a = table["B", 10] + 1 + table["B", 10] + 1

    val math1b = 1 - table["B", 10]
    val math2b = 1L - table["B", 10]
    val math3b = 1.0F - table["B", 10]
    val math4b = 1.0 - table["B", 10]
    val math5b = BigInteger.ONE - table["B", 10]
    val math6b = BigDecimal.ONE - table["B", 10]
    val math7b = table["B", 10] - 1 - table["B", 10] - 1

    val math1c = 1 * table["B", 10]
    val math2c = 1L * table["B", 10]
    val math3c = 1.0F * table["B", 10]
    val math4c = 1.0 * table["B", 10]
    val math5c = BigInteger.ONE * table["B", 10]
    val math6c = BigDecimal.ONE * table["B", 10]
    val math7c = table["B", 10] * 1 * table["B", 10] * 1

    val math1d = 1 / table["B", 10]
    val math2d = 1L / table["B", 10]
    val math3d = 1.0F / table["B", 10]
    val math4d = 1.0 / table["B", 10]
    val math5d = BigInteger.ONE / table["B", 10]
    val math6d = BigDecimal.ONE / table["B", 10]
    val math7d = table["B", 10] / 1 / table["B", 10] / 1

    val math1e = 1 % table["B", 10]
    val math2e = 1L % table["B", 10]
    val math3e = 1.0F % table["B", 10]
    val math4e = 1.0 % table["B", 10]
    val math5e = BigInteger.ONE % table["B", 10]
    val math6e = BigDecimal.ONE % table["B", 10]
    val math7e = table["B", 10] % 1 % table["B", 10] % 1

    // TODO
    //val compare1 = table["A"][1] > 5
    //val compare2 = table["A"][1] < table["A"][2]
    //val compare3 = table["A"][1] == table["A", 3]

    // TODO
    /*
    for (c in table["A"][1]..table["A"][2]) {

    }
     */

    // TODO
    //(table["A"][1]..table["A"][2]).contains(10)
    //val range = (table["A"][1]..table["A"][2])
    //range.forEach { println(it) }
    //range.map { it.value }.filterIsInstance<BigInteger>().size
    //val inVal = 5 in table["A"][1]

    // TODO
    //Table.move(table["A"] before table["B"])
    //Table.move(table["A"] after table["B"])
    //Table.copy(table["A"] before table["B"], "C")
    //Table.move(table["A"], ColumnActionOrder.AFTER, table["B"])
    //move(table["A"] before table["B"])

    (table.columns.first()..table.columns.last()).forEach {
        println("Column range first to last: " + it.columnHeader)
    }

    println()
    println("-----------")
    println()

    (table.columns.last()..table.columns.first()).forEach {
        println("Column range last to first: " + it.columnHeader)
    }

    println()
    println("-----------")
    println()

    for (x in listOf("C", "D")) {
        for (y in listOf(10L, 11L)) {
            table[x][y] = "$x:$y"
        }
    }

    (table["C"][10]..table["D"][11]).forEach { println(it) }

    println()
    println("-----------")
    println()

    (table["D"][11]..table["C"][10]).forEach { println(it) }

    println()
    println("-----------")
    println()

    val value = table["A"] at 1

    subscribe<Any, Any>(table) { receiver ->
        println("Subscribe 1: ${receiver.events}")
        receiver.events.forEach {
            if (it.newValue.index == 1L) receiver.source["A"][2] = "UPDATE"
        }
    }
    table.subscribe<Any, Number> {
        println("Subscribe 2: ${it.events}")
    }
    table.subscribe<Any, String> {
        println("Subscribe 3: ${it.events}")
    }
    table.subscribe<String, Number> {
        println("Subscribe 4: ${it.events}")
    }
    table.subscribeAny {
        println("Subscribe any: ${it.events}")
    }

    table["A"][1] = "Hello subscription"
    table["A"][1] = null
    table["A"][1] = 1000

    subscribe<Any, Any>(table["A"]) {}
    subscribe<Any, Any>(table["A"][1]..table["A"][2]) {}
    subscribe<Any, Any>(table["A"][1]) {}

    println("END")
}
