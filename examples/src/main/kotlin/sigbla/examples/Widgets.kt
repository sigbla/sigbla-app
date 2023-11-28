/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import sigbla.app.*
import sigbla.widgets.*

fun main() {
    val table = Table["Widgets"]
    val tableView = TableView[table]

    table["B", 0] = 0

    tableView["A", 0] = button("Click me!") {
        println("Button click!")
        table["B", 0] = table["B", 0] + 1
        text = table["B", 0].toString()
    }

    tableView["A", 1] {
        checkBox("Check me") {
            println("Checkbox action: ${checked}")
            text = if (checked) "I'm checked! =)" else "I'm not checked :("
        }
    }

    fun radioMaker(index: Int, otherIndex: Int, selected: Boolean?) {
        tableView["A", index] {
            radio(if (selected == null) "I'm a radio button" else if (selected) "I'm selected! =)" else "I'm not selected :(") {
                println("Radio action: ${this.selected}")
                text = if (this.selected) "I'm selected! =)" else "I'm not selected :("

                if (this.selected) radioMaker(otherIndex, index, false)
            }
        }
    }

    radioMaker(3, 4, null)
    radioMaker(4, 3, null)

    tableView["A", 6] = textField {
        println("Text field action: ${text}")
        if (!text.startsWith("*")) text = "* $text"
    }

    move(table["A"] before table["B"])

    val url = show(tableView)
    println(url)

    println("END")
}
