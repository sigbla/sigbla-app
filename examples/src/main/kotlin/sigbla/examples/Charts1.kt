package sigbla.examples

import sigbla.app.*
import sigbla.charts.*

fun main() {
    val table = Table["Charts"]
    val tableView = TableView[table]

    // TODO
    tableView["A", 0][CellHeight] = 250
    tableView["A", 0][CellWidth] = 350
    tableView["B", 0][CellHeight] = 250
    tableView["B", 0][CellWidth] = 350

    tableView[0][CellHeight] = 250
    tableView["A"][CellWidth] = 350
    tableView["B"][CellWidth] = 350

    tableView["A", 0] {
        line(
            listOf("X 1", "X 2", "X 3"),
            "Series A" to listOf(2.0, 1.5, 1.0),
            "Series B" to listOf(3.0, 2.5, 1.7)
        )
    }

    tableView["B", 0] {
        bar(
            listOf("X 1", "X 2", "X 3"),
            "Series A" to listOf(2.0, 1.5, 1.0),
            "Series B" to listOf(3.0, 2.5, 1.7)
        )
    }

    show(tableView)

    println("END")
}
