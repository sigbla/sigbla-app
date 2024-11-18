/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples.charts

import sigbla.app.*
import sigbla.charts.*

fun main() {
    TableView[Port] = 8080

    val table = Table["BubbleChart"]
    val tableView = TableView[table]

    val url = show(tableView, ref = "bubble-chart", config = compactViewConfig(title = "Bubble chart"))
    println(url)

    table["Dataset", 1] = "Dataset 1"
    table["X", 1] = 100
    table["Y", 1] = 110
    table["R", 1] = 20

    table["Dataset", 2] = "Dataset 1"
    table["X", 2] = 110
    table["Y", 2] = 120
    table["R", 2] = 10

    table["Dataset", 3] = "Dataset 1"
    table["X", 3] = 90
    table["Y", 3] = 125
    table["R", 3] = 15

    table["Dataset", 4] = "Dataset 2"
    table["X", 4] = 80
    table["Y", 4] = 210
    table["R", 4] = 25

    table["Dataset", 5] = "Dataset 2"
    table["X", 5] = 210
    table["Y", 5] = 220
    table["R", 5] = 5

    table["Dataset", 6] = "Dataset 2"
    table["X", 6] = 90
    table["Y", 6] = 25
    table["R", 6] = 11

    tableView[0][CellHeight] = 250
    tableView["Chart"][CellWidth] = 550

    val title = Table[null]["Title", 0].let {
        it("Bubble chart")
        it.table[it]
    }

    val dataset1 = table["X", 1]..table["R", 3] by CellOrder.ROW
    val dataset2 = table["X", 4]..table["R", 6] by CellOrder.ROW

    tableView["Chart", 0] = bubble(
        title, "Dataset 1" to dataset1, "Dataset 2" to dataset2
    ) {
        val options = this.options ?: ChartModel.Options()
        val scales = options.scales ?: ChartModel.Options.Scales()
        val xScale = scales.values?.getOrDefault("x", ChartModel.Options.Scales.Scale()) ?: ChartModel.Options.Scales.Scale()
        val yScale = scales.values?.getOrDefault("y", ChartModel.Options.Scales.Scale()) ?: ChartModel.Options.Scales.Scale()

        xScale.min = Numeric(0)
        xScale.max = Numeric(250)

        yScale.min = Numeric(0)
        yScale.max = Numeric(250)

        scales.values = (scales.values ?: mapOf()).toMutableMap().apply {
            this["x"] = xScale
            this["y"] = yScale
        }

        options.scales = scales
        this.options = options
    }
}
