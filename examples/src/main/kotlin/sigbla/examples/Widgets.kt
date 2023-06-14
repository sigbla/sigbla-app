package sigbla.examples

import sigbla.app.*
import sigbla.widgets.*

fun main() {
    val table = Table["Widgets"]
    val tableView = TableView[table]

    table["A", 0] = 0

    fun buttonMaker() {
        tableView["A", 0] {
            button(table["A", 0].toString()) {
                println("Pre 2 ${this.columnView.columnHeader}:${this.index} !")
                table["A", 0] = table["A", 0] + 1
                buttonMaker()
                println("Post 2 ${this.columnView.columnHeader}:${this.index} !")
            }
        }
    }

    tableView["A", 0] {
        button("Click me!") {
            println("Pre 1 ${this.columnView.columnHeader}:${this.index} !")
            buttonMaker()
            println("Post 1 ${this.columnView.columnHeader}:${this.index} !")
        }
    }

    show(tableView)

    println("END")
}
