package sigbla.tmp

import kotlinx.html.style
import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom

fun main() {
    val table = Table["test"]

    fun cell(x: Int, y: Int, black: Boolean) {
        // TODO Change this to use a view transformer when that is available..
        table[x.toString(), y] = div {
            if (black) style = "background-color: black; width: 100%; height: 100%"
            else "background-color: white; width: 100%; height: 100%"
        }
    }

    val maxHeaders = 99
    val maxRows = 99

    for (x in 0 until maxHeaders) {
        for (y in 0 until maxRows) {
            cell(x, y, x % 2 == 0)
        }
    }

    val tableView = TableView[table]
    tableView.show()

    tableView[DEFAULT_COLUMN_VIEW] = {
        width = 10
    }
    tableView[DEFAULT_ROW_VIEW] = {
        height = 10
    }

    Thread.sleep(15000)

    while (true) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            val x = ThreadLocalRandom.current().nextInt(0, maxHeaders)
            val y = ThreadLocalRandom.current().nextInt(0, maxHeaders)
            copy(table[x.toString()] to table[y.toString()])
        } else {
            val x = ThreadLocalRandom.current().nextInt(0, maxRows)
            val y = ThreadLocalRandom.current().nextInt(0, maxRows)
            copy(table[x] to table[y])
        }

        Thread.sleep(100)
    }

    println("END")
}
