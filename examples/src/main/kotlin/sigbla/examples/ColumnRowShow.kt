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

    // Define the columns and rows we want visible
    tableView["B"][Visibility] = Visibility.Show
    tableView["D"][Visibility] = Visibility.Show
    tableView[2][Visibility] = Visibility.Show
    tableView[3][Visibility] = Visibility.Show

    // Reverse default visibility behavior by setting columnVisibilityBehavior and rowVisibilityBehavior to Show
    //println(show(tableView, ref = "column-row-show", config = compactViewConfig(columnVisibilityBehavior = Visibility.Show, rowVisibilityBehavior = Visibility.Show)))
    println(show(tableView, ref = "column-row-show", config = spaciousViewConfig(columnVisibilityBehavior = Visibility.Show, rowVisibilityBehavior = Visibility.Show)))
}