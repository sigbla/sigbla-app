/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples.charts

import sigbla.app.*
import sigbla.charts.*

fun main() {
    TableView[Port] = 8080

    val table = Table["RadarChart"]
    val tableView = TableView[table]

    val url = show(tableView, ref = "radar-chart", config = compactViewConfig(title = "Radar chart"))
    println(url)

    tableView[0][CellHeight] = 250
    tableView["A"][CellWidth] = 350
    tableView["B"][CellWidth] = 350

    tableView["A", 0] = radar(
        null,
        listOf("X 1", "X 2", "X 3"),
        "Series A" to listOf(2.0, 1.5, 1.0)
    )

    tableView["B", 0] = radar(
        null,
        listOf("X 1", "X 2", "X 3"),
        "Series A" to listOf(2.0, 1.5, 1.0), "Series B" to listOf(3.0, 2.5, 1.7)
    ) {
        data!!.datasets!!.datasets.mapIndexed { index, dataset ->
            dataset.backgroundColor = Colors(
                listOf(Color.RGB(150, index * 100, 0), Color.RGB(255, index * 100, 150), Color.RGB(255, index * 100, 100))
            )
        }
    }
}
