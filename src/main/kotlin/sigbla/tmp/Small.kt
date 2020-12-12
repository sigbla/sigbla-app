package sigbla.tmp

import kotlinx.html.style
import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom

fun main() {
    //val table = Table["Table A"]
    val table = Table["test"]

    for (col in listOf("A", "B", "C", "D", "E", "F")) {
        val withExtra = ThreadLocalRandom.current().nextBoolean()
        for (row in 0..1000) {
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

    println("G 0 div value: " + table["G", 0])

    val tableView = TableView[table]

    table.onAny {
        events {
            newTable
            oldTable
        }
    }

    tableView.onAny {
        events {
            newView
            oldView
        }
    }

    tableView.show()

    println("END")
}
