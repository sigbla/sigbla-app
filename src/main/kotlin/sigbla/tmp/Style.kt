package sigbla.tmp

import kotlinx.html.style
import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom

fun main() {
    //val table = Table.newTable("Table A")
    val table = Table.newTable("test")

    for (col in listOf("A", "B", "C", "D", "E", "F")) {
        val withExtra = ThreadLocalRandom.current().nextBoolean()
        for (row in 0..100) {
            if (withExtra)
                table[col, "E1", row] = "$col $row"
            else
                table[col, row] = "$col $row"
        }
    }

    table["G", 0] = div("myCustomClass1 myCustomClass2") {
        style = "color: red"
        +"This is red text"
    }

    val tableView = TableView.newTableView(table)

    tableView[DEFAULT_COLUMN_STYLE] = {
        width = 99
    }

    tableView[ColumnHeader("A")] = {
        width = 100
    }

    tableView.show()

    tableView.onAny {
        events {
            forEach {
                println("onAny: " + it)
            }
        }
    }

    tableView.on<DefaultColumnStyle> {
        events {
            forEach {
                println("on: " + it)
            }
        }
    }

    tableView[DEFAULT_COLUMN_STYLE] = {
        width = 98
    }

    println("END")
}
