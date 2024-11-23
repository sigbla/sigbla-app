/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples.views

import sigbla.app.*

fun main() {
    TableView[Port] = 8080

    val table = Table[null]

    table["A", 0] = "A0"
    table["B", 0] = "B0"
    table["C", 0] = "C0"

    table["A", 2] = "A2"
    table["B", 2] = "B2"
    table["C", 2] = "C2"

    // We want the cell at D0 to span from D0 to D2 (row span)
    table["D", 0] = "D0 to D2"

    // We want the cell at A1 to span from A1 to C1 (column span)
    table["A", 1] = "A1 to C1"

    // Pick on of the two default view configs
    val viewConfig = spaciousViewConfig(title = "Demo of column and row span")
    //val viewConfig = compactViewConfig(title = "Demo of column and row span")

    val tableView = TableView[table]

    link(
        tableView["D", 0][CellHeight],
        tableView[0][CellHeight], tableView[1][CellHeight], tableView[2][CellHeight],
        config = viewConfig
    )

    link(
        tableView["A", 1][CellWidth],
        tableView["A"][CellWidth], tableView["B"][CellWidth], tableView["C"][CellWidth],
        config = viewConfig
    )

    val url = show(tableView, ref = "column-and-row-span", config = viewConfig)
    println(url)
}