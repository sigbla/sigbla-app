package sigbla.tmp

import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.style
import sigbla.app.IndexRelation.*
import sigbla.app.timeseries.*
import sigbla.app.Table.Companion.on
import sigbla.app.*
import sigbla.app.Table.Companion.onAny
import java.math.BigDecimal
import java.math.BigInteger

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun main() {
    //val table = Table["Table A"]
    val table = Table["test"]

    table["Column A"][0] = "A0"

    table["Column A"][1] = 1000
    table["Column A", 1] = 1000

    (table["C"][1]..table["D"][10]).onAny {
        name = "A"

        if (table["E"][1].isNumeric())
            table["E"][1] = table["E"][1] + 1
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

    val tickerHeaders = table.headers.filter { it[2] == "Ticker" }

    val prices = tickerHeaders.map { header -> header to table[header][AT_OR_BEFORE, 1000] }.toMap()

    table["A"][AT, LocalDate.now(), LocalTime.MAX, ZoneId.systemDefault()]

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

    val compare1 = table["A"][1] > 5
    val compare2 = table["A"][1] < table["A"][2]
    val compare3 = table["A"][1] == table["A", 3]

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
    range.map { it.value }.filterIsInstance<BigInteger>().size

    table["A"].asSequence().forEach {
        println("table[\"A\"].asSequence().forEach $it")
    }

    table.iterator().forEach {
        println("table iterator forEach: ${it.column} ${it.index} $it")
    }
    table.asSequence().forEach {
        println("table asSequence forEach: ${it.column} ${it.index} $it")
    }

    // TODO
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

    on<Any, Any>(table) {
        name = "B"
        allowLoop = true

        println("Subscribe 1a")
        events {
            newTable[source] //.forEach {
//                it.value
//            }
            oldTable[source] //.forEach {
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
//    on<Any, Any>(r) {
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
                if (it.column.columnHeader[0] == "Sums") return@forEach
                // TODO Implement plusAssign and similar: https://kotlinlang.org/docs/reference/operator-overloading.html
                //source["Sums", 0] += it
                source["Sums", 0] = source["Sums", 0] + it
                println("Sum: ${source["Sums", 0]}")
            }
        }
    }

    table["Sums", 0].on<Any, Number> {
        name = "F"

        events {
            forEach {
                println("New sum: ${it.newValue}")
            }
        }
    }

    table["A", 1] = null

    onAny(table) {
        name = "G"

        events {
            forEach {
                it.newValue.value
            }
        }
    }

    // TODO Introduce an off function as well?
    // table.off(listenerRef)
    // table.off("Name")
    // table.off(table["A"][1])

    table.on<Any, Number> {
        name = "H"

        events {
            println("Subscribe 2: ${count()}")
        }
    }
    table.on<Any, String> {
        name = "I"

        events {
            println("Subscribe 3: ${count()}")
        }
    }
    table.on<String, Number> {
        name = "J"

        events {
            println("Subscribe 4: ${count()}")
        }
    }
    table.onAny {
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
    table["DST", 0] = {
        table.on<Any, Number> {
            name = "L"

            skipHistory = true
            destination.table[destination] = "Init"

            events {
                val c = count {
                    it.newValue.column.columnHeader != destination.column.columnHeader && it.newValue.index != destination.index
                }

                if (c > 0) destination.table[destination] = c
            }
        }
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

    table["DIV", 1] = {
        destination.table[destination] = div {
            +"Pre Event"
        }

        table.onAny {
            name = "M"

            skipHistory = true
            events {
                destination.table[destination] = div {
                    +"Post Event"
                }
            }
        }
    }

    tableView.show()

    println("END")
}
