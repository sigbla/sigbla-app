package sigbla.tmp

import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue

fun main() {
    val table = Table["test"]
    val tableView = TableView[table]
    tableView.show()

    table["B", 0] = sum(table["A", 0]..table["A", 9])

    Thread.sleep(15000)

    var i = 0L

    while (true) {
        Thread.sleep(100)
        println("Update..")
        table["A", ThreadLocalRandom.current().nextInt().absoluteValue % 10] = i++
    }
}
