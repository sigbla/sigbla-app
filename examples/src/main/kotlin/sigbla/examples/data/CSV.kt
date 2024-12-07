package sigbla.examples.data

import sigbla.app.*
import sigbla.data.*
import java.io.File
import java.io.StringWriter

fun main() {
    val table = Table["csv"]

    import(csv(File("examples/data/example1.csv").bufferedReader(), withHeader = true) to table) {
        remove(this["Index"].column)
    }

    rename(table["First Name"], "Name", "First")
    rename(table["Last Name"], "Name", "Last")

    val writer = StringWriter()
    export(table to csv(writer, withHeader = false))

    println("----")
    println(writer.toString())
    println("----")

    TableView[Port] = 8080
    println(show(table))
}