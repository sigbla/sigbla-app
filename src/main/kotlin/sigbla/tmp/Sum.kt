package sigbla.tmp

import sigbla.app.Table
import sigbla.app.TableView
import sigbla.app.sum
import sigbla.app.valueOf

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

    val value1 = valueOf<Number>(table["A", 0]..table["B", 100]).toList()
    val value2 = valueOf<Number>(table["A"]).toList()
    println("Value1: $value1")
    println("Value2: $value2")

    table["Sum", 0] = sum(table["A", 0]..table["B", 100])
    table["Sum", 0L] = sum(table["A", 0]..table["B", 100])
    table["Sum"][0] = sum(table["A", 0]..table["B", 100])
    table["Sum"][0L] = sum(table["A", 0]..table["B", 100])
    table[table["Sum", 0]] = sum(table["A", 0]..table["B", 100])

    val tableView = TableView[table]

    tableView.show()

    println("END")
}
