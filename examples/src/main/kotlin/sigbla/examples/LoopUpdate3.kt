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
        tableView[x.toString(), y][CellTransformer] = {
            val black = this.value == true

            this.table[this] = div {
                style = if (black)
                    "background-color: black; width: 100%; height: 100%"
                else
                    "background-color: white; width: 100%; height: 100%"
            }
        }
        table[x.toString(), y] = black
    }

    val maxHeaders = 99
    val maxRows = 99

    for (x in 0 until maxHeaders) {
        for (y in 0 until maxRows) {
            cell(x, y, x % 2 == 0)
            //cell(x, y, ThreadLocalRandom.current().nextBoolean())
        }
    }

    val url = show(tableView)
    println(url)

    batch(tableView) {
        tableView[CellWidth] = 10
        tableView[CellHeight] = 10
    }

    Thread.sleep(15000)

    var sleepTime = 100L

    while (true) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            val x = ThreadLocalRandom.current().nextInt(0, maxHeaders)
            val y = ThreadLocalRandom.current().nextInt(0, maxHeaders)
            copy(table[x.toString()] to table[y.toString()])
        } else {
            val x = ThreadLocalRandom.current().nextInt(0, maxRows)
            val y = ThreadLocalRandom.current().nextInt(0, maxRows)
            copy(table[x] to table[y])
        }

        // TODO Some UI bug observed consistently when this speeds up and eventually overflow the UI buffer.
        //      Issue observed is that the first column isn't shown. Seen in Firefox and Chrome.
        Thread.sleep(sleepTime--)

        if (sleepTime <= 0) sleepTime = 100L
    }

    println("END")
}
