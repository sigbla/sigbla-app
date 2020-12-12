package sigbla.tmp

import sigbla.app.Table
import sigbla.app.TableView
import sigbla.app.sum

fun main() {
    val table = Table["test"]

    for (col in listOf("A", "B")) {
        for (row in 0..100) {
            if (col == "A")
                table[col, row] = row.toDouble()
            else
                table[col, row] = row.toBigDecimal()
        }
    }

    // TODO: table["Sum", 0] = sum(..)
    val cell = table["Sum", 0]
    // TODO table[cell] = sum(table["A"]..table["B"])
    table[cell] = sum(table["A", 0]..table["B", 100])

    val tableView = TableView[table]

    tableView.show()

    println("END")
}
