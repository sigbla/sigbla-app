package sigbla.examples

import sigbla.app.*

fun main() {
    val table = Table["listener loop"]

    val ref = on(table) {
        allowLoop = true

        events {
            println(joinToString())
            forEach {
                if (valueOf<Number>(table["A", 1])?.toLong() ?: 1000 < 1000)
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
