/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue

fun main() {
    val table = Table["test"]
    val tableView = TableView[table]

    tableView[CellClasses] = "testClass"
    tableView[CellTopics] = sortedSetOf("test-event")

    val url = show(tableView)
    println(url)

    table["B", 0] = sum(table["A", 0]..table["A", 9])

    Thread.sleep(15000)

    var i = 0L

    while (true) {
        Thread.sleep(100)
        println("Update..")
        if (i++ % 10 == 0L)
            table["A", ThreadLocalRandom.current().nextInt().absoluteValue % 10] = Unit
        else
            table["A", ThreadLocalRandom.current().nextInt().absoluteValue % 10] = i
    }
}
