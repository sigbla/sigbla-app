package sigbla.tmp

import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom

fun main() {
    val table = Table["Print me"]

    for (col in listOf("A", "B", "C", "D", "E", "F")) {
        val withExtra = ThreadLocalRandom.current().nextBoolean()
        for (row in 0..10) {
            if (withExtra)
                table[col, "E1", row] = "!$col$row!"
            else
                table[col, row] = "!$col$row!"
        }
    }

    print(table)
}
