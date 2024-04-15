/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import kotlinx.html.style
import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom

fun main() {
    TableView[Port] = 8080

    //val table = Table["Table A"]
    val table = Table["test"]

    for (col in listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T")) {
        val withExtra = col != "G" && ThreadLocalRandom.current().nextBoolean()
        for (row in 0..100) {
            if (withExtra)
                table[col, "E1", row] = "$col $row"
            else
                table[col, row] = "$col $row"
        }
    }

    move(table["A"] after table["B"])
    copy(table["F"] to table, "F2")

    println(columnsOf(table))

    table["G", 0] = div("myCustomClass1 myCustomClass2") {
        style = "color: red"
        +"This is red text"
    }

    println("G 0 div value: " + table["G", 0])

    val tableView = TableView[table]

    on(table) {
        events {
            newTable
            oldTable
        }
    }

    on(tableView) {
        events {
            newView
            oldView
        }
    }

    /*
    val listener: TableViewEventReceiver<TableView, Any>.() -> Unit = {
        events {
            newView
            oldView
        }
    }
    on(tableView, listener)
    on(tableView) = listener
     */

    val url = show(tableView, config = spaciousViewConfig(title = "Small example"))
    println(url)

    println("END")
}
