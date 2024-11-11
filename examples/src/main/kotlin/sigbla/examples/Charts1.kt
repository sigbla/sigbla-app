/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import sigbla.app.*
import sigbla.charts.*

fun main() {
    val table = Table["Charts"]
    val tableView = TableView[table]

    tableView[0][CellHeight] = 250
    tableView["A"][CellWidth] = 350
    tableView["B"][CellWidth] = 350

    tableView["A", 0] = line(
        listOf("X 1", "X 2", "X 3"),
        "Series A" to listOf(2.0, 1.5, 1.0),
        "Series B" to listOf(3.0, 2.5, 1.7)
    ) {
        data.labels = listOf("A", "B", "C")
        data.datasets = data.datasets.mapIndexed { index, d ->
            LineChartConfig.Data.Dataset(data.labels[index], d.data)
        }
    }

    tableView["B", 0] = bar(
        listOf("X 1", "X 2", "X 3"),
        "Series A" to listOf(2.0, 1.5, 1.0),
        "Series B" to listOf(3.0, 2.5, 1.7)
    )

    val url = show(tableView)
    println(url)

    println("END")
}
