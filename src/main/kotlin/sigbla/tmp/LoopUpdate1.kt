package sigbla.tmp

import sigbla.app.*

fun main() {
    val table = Table.newTable("test")
    val tableView = TableView.newTableView(table)
    tableView.show()

    Thread.sleep(15000)

    var i = 0L

    while (true) {
        Thread.sleep(100)
        println("Update..")
        table["A", i % 10] = i++
    }
}
