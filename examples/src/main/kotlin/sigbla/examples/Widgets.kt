package sigbla.examples

import sigbla.app.*
import sigbla.widgets.*

fun main() {
    val table = Table["Widgets"]
    val tableView = TableView[table]

    table["B", 0] = 0

    tableView["A", 0] {
        button("Click me!") {
            println("Button click!")
            table["B", 0] = table["B", 0] + 1
            text = table["B", 0].toString()
        }
    }

    tableView["A", 1] {
        checkBox("Check me") {
            println("Action: ${checked}")
            text = if (checked) "I'm checked! =)" else "I'm not checked :("
        }
    }

    move(table["A"] before table["B"])

    show(tableView)

    println("END")
}
