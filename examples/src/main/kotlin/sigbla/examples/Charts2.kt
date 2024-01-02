/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import sigbla.app.*
import sigbla.charts.*
import java.util.concurrent.ThreadLocalRandom

fun main() {
    val table = Table["Charts"]
    val tableView = TableView[table]

    table["A", 0] = "Title"
    val title = table["A", 0]

    table["A", 2] = "X 1"
    table["A", 3] = "X 2"
    table["A", 4] = "X 3"
    val labels = table["A", 2]..table["A", 4]

    table["A", 6] = 2.0
    table["A", 7] = 1.5
    table["A", 8] = 1.0
    val dataset1 = table["A", 6]..table["A", 8]

    table["A", 10] = 3.0
    table["A", 11] = 2.5
    table["A", 12] = 1.7
    val dataset2 = table["A", 10]..table["A", 12]

    tableView[0][CellHeight] = 250
    tableView["B"][CellWidth] = 350
    tableView["C"][CellWidth] = 350

    val url = show(tableView)
    println(url)

    Thread.sleep(10000)

    tableView["B", 0] {
        line(
            //title,
            labels,
            "Series A" to dataset1,
            "Series B" to dataset2
        )
    }

    tableView["C", 0] {
        bar(
            title,
            labels,
            "Series A" to dataset1,
            "Series B" to dataset2
        )
    }

    Thread.sleep(10000)

    for (i in 1..10000) {
        Thread.sleep(1000)
        println("Updating..")
        table["A", 7] = ThreadLocalRandom.current().nextDouble(1.0, 2.0)
    }

    println("END")
}
