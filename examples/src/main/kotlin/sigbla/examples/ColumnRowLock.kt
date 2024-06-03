package sigbla.examples

import sigbla.app.*

fun main() {
    TableView[Port] = 8080

    val table = Table[null]
    val tableView = TableView[table]

    for (label in listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T")) {
        for (index in 0..1000) {
            table[label, index] = "$label $index"
        }
    }

    tableView["D"][Position] = Position.Left
    tableView["G"][Position] = Position.Left

    tableView["E"][Position] = Position.Right
    tableView["B"][Position] = Position.Right

    tableView[10][Position] = Position.Top
    tableView[200][Position] = Position.Top

    tableView[100][Position] = Position.Bottom
    tableView[2][Position] = Position.Bottom

    //println(show(tableView, ref = "column-row-lock", config = spaciousViewConfig()))
    println(show(tableView, ref = "column-row-lock", config = compactViewConfig()))
}