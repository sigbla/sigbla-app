/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import kotlinx.html.style
import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom

fun main() {
    val table = Table["test"]

    val tableView = TableView[table]

    fun cell(x: Int, y: Int, black: Boolean) {
        table[x.toString(), y] = div {
            if (black) style = "background-color: black; width: 100%; height: 100%"
            else "background-color: white; width: 100%; height: 100%"
        }
        tableView[x.toString(), y][CellClasses] = if (black) "black" else "white"
    }

    for (x in 0..99) {
        for (y in 0..99) {
            cell(x, y, true)
        }
    }

    batch(tableView) {
        tableView[CellWidth] = 10
        tableView[CellHeight] = 10
    }

    val url = show(tableView)
    println(url)

    Thread.sleep(15000)

    while (true) {
        batch(table) {
            for (i in 1..10) {
                cell(
                    ThreadLocalRandom.current().nextInt(0, 100),
                    ThreadLocalRandom.current().nextInt(0, 100),
                    ThreadLocalRandom.current().nextBoolean()
                )
            }
        }

        Thread.sleep(1000)
    }

    println("END")
}
