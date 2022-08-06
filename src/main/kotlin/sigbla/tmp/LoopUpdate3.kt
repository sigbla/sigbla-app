package sigbla.tmp

import kotlinx.html.style
import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom

// TODO Check why view is different if reloaded in browser?
//  It looks like it's missing some cell updates while running..

fun main() {
    val table = Table["test"]

    fun cell(x: Int, y: Int, black: Boolean) {
        table[x.toString(), y] = div {
            if (black) style = "background-color: black; width: 100%; height: 100%"
            else "background-color: white; width: 100%; height: 100%"
        }
    }

    for (x in 0..99) {
        for (y in 0..99) {
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
        val x = ThreadLocalRandom.current().nextInt(0, 100)
        val y = ThreadLocalRandom.current().nextInt(0, 100)

        if (ThreadLocalRandom.current().nextBoolean()) {
            copy(table[x.toString()] to table[y.toString()])
        } else {
            copy(table[x] to table[y])
        }

        Thread.sleep(100)
    }

    println("END")
}
