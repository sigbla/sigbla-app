/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import sigbla.app.*

fun main() {
    TableView[Port] = 8080

    val table = Table[null]
    val tableView = TableView[table]

    for (label in listOf("A", "B", "C", "D")) {
        for (index in 0..5) {
            table[label, index] = "$label $index"
        }
    }

    tableView["B"][Visibility] = Visibility.Hide
    tableView[2][Visibility] = Visibility.Hide

    println(show(tableView, ref = "column-row-hide"))
}
