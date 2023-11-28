/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.style
import sigbla.app.IndexRelation.*
import sigbla.app.*
import java.math.BigDecimal
import java.math.BigInteger

fun main() {
    //val table = Table["Table A"]
    val table = Table["test"]

    table["Column A"][0] = "A0"

    table["Column A"][1] = 1000
    table["Column A", 1] = 1000

    on(table["C"][1]..table["D"][10]) {
        name = "A"

        if (table["E"][1].isNumeric)
            table["E"][1] = table["E"][1] + 1
    }

    on(table["A"] or table["B"] or table["C", 1]..table["D", 2]) {
        events {

        }
    }

    //val view = TableView[table]
    //view.show()

    table["A", "B"][1] = "Test"
    val cell1 = table["A", "B"][BEFORE, 1]
    val cell2 = table[AFTER, 1]["A", "B"]
    val cell3 = table["A", "B"][1]
    val cell4 = table[1]["A", "B"]

    table[1][ColumnHeader("A")] = "A"
    table[1]["A", "B"] = "A"

    val tickerHeaders = headersOf(table).filter { it[2] == "Ticker" }

    val prices = tickerHeaders.map { header -> header to table[header][AT_OR_BEFORE, 1000] }.toMap()

    // Not a good idea, removed timeseries package table["A"][AT, LocalDate.now(), LocalTime.MAX, ZoneId.systemDefault()]

    val size = 1_000_000L

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
    println("Write: " + (end1 - start1))
    println("Read: " + (end2 - start2))

    //Thread.sleep(Long.MAX_VALUE)

    table["B"][1].isNumeric
    table["B"][1].isText

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

    val compare1a = table["A"][1] > 5
    val compare2a = table["A"][1] < table["A"][2]
    val compare3a = table["A"][1] == table["A", 3]

    val compare1b = 5 < table["A"][1]
    val compare2b = 5 <= table["A"][1]
    val compare3b = 5 > table["A"][1]
    val compare4b = 5 >= table["A"][1]

    // TODO Can we get this working?
    //      Don't understand why this wouldn't be fine when c.equals(5) is..??
    //val compare4 = table["A"][1] == 5
    //val compare5b = 5 == table["A"][1]
    val c = table["A"][1]
    val compare5 = c.equals(5)
    val compare5b = c.compareTo(5) == 0
    val compare6a = 5 in table["A"][1]

    println("${table["A"][1]} > 5: ${table["A"][1] > 5}")

    println("${table["A"][1]} < ${table["A"][2]}: ${table["A"][1] < table["A"][2]}")
    table["A"][2] = "A"
    println("${table["A"][1]} < ${table["A"][2]}: ${table["A"][1] < table["A"][2]}")

    println("${table["A"][1]} == ${table["A", 3]}: ${table["A"][1] == table["A", 3]}")
    table["A"][3] = "A"
    println("${table["A"][1]} == ${table["A", 3]}: ${table["A"][1] == table["A", 3]}")

    for (c in table["A"][1]..table["A"][2]) {
        println("for c: $c")
    }

    table["A"][2] = "Table cell at A2"

    (table["A"][1]..table["A"][2]).asSequence()
    (table["A"][1]..table["A"][2]).contains(10)
    val range = (table["A"][1]..table["A"][2])
    range.forEach { println(it) }
    range.mapNotNull { valueOf<BigInteger>(it) }.size

    table["A"].asSequence().forEach {
        println("table[\"A\"].asSequence().forEach $it")
    }

    table.iterator().forEach {
        println("table iterator forEach: ${it.column} ${it.index} $it")
    }
    table.asSequence().forEach {
        println("table asSequence forEach: ${it.column} ${it.index} $it")
    }

    val inVal = 5 in table["A"][1]

    move(table["A"] before table["B"])
    move(table["A"] after table["B"])
    copy(table["A"] before table["B"], "C")
    move(table["A"], ColumnActionOrder.AFTER, table["B"])

    (columnsOf(table).first()..columnsOf(table).last()).forEach {
        println("Column range first to last: " + it.header)
    }

    println()
    println("-----------")
    println()

    (columnsOf(table).last()..columnsOf(table).first()).forEach {
        println("Column range last to first: " + it.header)
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

    val sumValue = valueOf<Number>(sum(table["A", 1]..table["A", 10]))
    val maxValue = valueOf<Number>(max(table["A", 1]..table["A", 10]))
    val minValue = valueOf<Number>(min(table["A", 1]..table["A", 10]))

    table[1].iterator().forEach {  }

    on(table) {
        name = "B"
        allowLoop = true

        println("Subscribe 1a")
        events {
//            newTable[Table] //.forEach {
//                it.value
//            }
//            oldTable[Table] //.forEach {
//                it.value
//            }
            forEach {
                if (it.newValue.index == 1L) this@on.source["A"][2] = "UPDATE"
            }
        }
    }

    on<Any, Any>(table["A"]) {
        name = "C"

        println("Subscribe 1b")
        events {
            newTable[source] //.forEach {
//                it.value
//            }
            oldTable[source] //.forEach {
//                it.value
//            }
            forEach {
                //source.table[it.newValue] = it.newValue * 2
                //if (it.newValue.index == 1L) source.table["A"][2] = "UPDATE"
            }
        }
    }

    on<Any, Any>(table["A", 0]..table["B", 100]) {
        name = "D"

        println("Subscribe 1c")
        events {
            newTable[source] //.forEach {
//                it.value
//            }
            oldTable[source] //.forEach {
//                it.value
//            }
            forEach {
                //if (it.newValue.index == 1L) source.table["A"][2] = "UPDATE"
            }
        }
    }

    // TODO
//    val columnRange = table["A"]..table["B"]
//    on<Any, Any>(columnRange) {
//        name = "E"
//
//        println("Subscribe 1d")
//        events {
//            newTable[source].forEach {
//                it.value
//            }
//            oldTable[source].forEach {
//                it.value
//            }
//            forEach {
//                if (it.newValue.index == 1L) source.table["A"][2] = "UPDATE"
//            }
//        }
//    }

    on<Any, Number>(table) {
        name = "Name"
        order = 10
        allowLoop = true

        source["Sums", 0] = 0

        events {
            map {
                it.newValue
            }.forEach {
                if (it.column.header[0] == "Sums") return@forEach
                // TODO Implement plusAssign and similar: https://kotlinlang.org/docs/reference/operator-overloading.html
                //source["Sums", 0] += it
                source["Sums", 0] = source["Sums", 0] + it
                println("Sum: ${source["Sums", 0]}")
            }
        }
    }

    on<Any, Number>(table["Sums", 0]) {
        name = "F"

        events {
            forEach {
                println("New sum: ${it.newValue}")
            }
        }
    }

    table["A", 1] = null

    on(table) {
        name = "G"

        events {
            forEach {
                valueOf<Any>(it.newValue)
            }
        }
    }

    // TODO Introduce an off function as well?
    // table.off(listenerRef)
    // table.off("Name")
    // table.off(table["A"][1])

    on<Any, Number>(table) {
        name = "H"

        events {
            println("Subscribe 2: ${count()}")
        }
    }
    on<Any, String>(table) {
        name = "I"

        events {
            println("Subscribe 3: ${count()}")
        }
    }
    on<String, Number>(table) {
        name = "J"

        events {
            println("Subscribe 4: ${count()}")
        }
    }
    on(table) {
        name = "K"

        events {
            println("Subscribe 5: ${count()}")
        }
    }

    table["A"][1] = "Hello subscription"
    table["A"][1] = null
    table["A"][1] = 1000

    for (i in 10..10000) {
        table["A"][1] = i
    }

    on<Any, Any>(table["A"]) {}
    on<Any, Any>(table["A"][1]..table["A"][2]) {}
    on<Any, Any>(table["A"][1]) {}

    // TODO Maybe a onColumn for when columns are added/moved/copied?

    val tableView = TableView[table]
    // TODO Maybe it is better if we could do tableView["A"] instead of tableView[table["A"]], and similar..
//    tableView[table["A"]] = columnStyle {
//        width = 100
//    }
//    tableView[table["A", "B"]] = columnStyle {}
//
//    tableView[table[1]] = rowStyle {}
//
//    tableView[table["A"][1]] = cellStyle {}

    // WIP
    table["DST", 0] {
        val destination = this
        on<Any, Number>(table) {
            name = "L"

            skipHistory = true
            destination.table[destination] = "Init"

            events {
                val c = count {
                    it.newValue.column.header != destination.column.header && it.newValue.index != destination.index
                }

                if (c > 0) destination.table[destination] = c
            }
        }

        Unit
    }

    //table["DST", 0] = sum(table["A", 1]..table["A", 10])

    table["DIV", 0] = div {
        div {
            span {
                style = "color: red"
                +"Some HTML text"
            }
        }
    }

    table["DIV", 1] {
        val destination = this
        this.table[this] = div {
            +"Pre Event"
        }

        on(table) {
            name = "M"

            skipHistory = true
            events {
                destination.table[destination] = div {
                    +"Post Event"
                }
            }
        }

        Unit
    }

    table[1]["A", "B"] = {

    }

    // TODO
    /*
    on(table["A"] or table["B", 1] or table["C", 1]..table["C", 10] or etc) calc {
        source["D", 0] = ...
        newTable[..] = ...
        oldTable[..] = ...
    }

    or rely on Table, Row, Column, CellRange, Cell having Iterable<Cell<*>> implementations
    on(table["A"], table["B", 1], table["C", 1]..table["C", 10], etc) calc {
        source["D", 0] = ...
        newTable[..] = ...
        oldTable[..] = ...
    }

    It's probably best to use infix or, as it allows for better type-safty, and we can
    also introduce infix and:

    or = trigger on any update on either of the defined inputs
    and = trigger when all defined inputs have updates

    Example:

    on((table["input", 1] or table["input", 2]) and table["button", 1]) ..
     */

    val url = show(tableView)
    println(url)

    println("END")
}
