package sigbla.tmp

import sigbla.app.Table

fun main() {
    val table = Table["listener loop"]

    val ref = table.onAny {
        allowLoop = true

        events {
            println(joinToString())
            forEach {
                if ((table["A", 1].value as Number).toLong() < 1000)
                    table["A", 1] = table["A", 1] + 1
            }
        }
    }

    println("Ref: $ref")

    println("Pre 1")
    table["A", 1] = 1
    println("Pre 2")
    table["B", 1] = 1
    println("Pre 3")
    table["A", 2] = 1
    println("Pre 4")
    table["B", 2] = 1

    println("----")
    println(table["A", 1])

    println("END")
}
