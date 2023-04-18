package sigbla.tmp

import kotlinx.html.style
import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom

fun main() {
    val table = Table["test"]

    val tableView = TableView[table]

    fun cell(x: Int, y: Int, black: Boolean) {
        table[x.toString(), y] = div {
            if (black) style = "background-color: black; width: 100%; height: 100%"
            else "background-color: white; width: 100%; height: 100%"
        }
        tableView[x.toString(), y].cellClasses = if (black) sortedSetOf("black") else sortedSetOf("white")
    }

    for (x in 0..99) {
        for (y in 0..99) {
            cell(x, y, true)
        }
    }

    /*
    tableView[DEFAULT_COLUMN_VIEW] = {
        width = 10
    }
    tableView[DEFAULT_ROW_VIEW] = {
        height = 10
    }
     */
    tableView[TableView] = {
        cellWidth = 10
        cellHeight = 10
    }

    show(tableView)
    Thread.sleep(15000)

    while (true) {
        cell(
            ThreadLocalRandom.current().nextInt(0, 100),
            ThreadLocalRandom.current().nextInt(0, 100),
            ThreadLocalRandom.current().nextBoolean()
        )

        Thread.sleep(100)
    }

    println("END")
}
