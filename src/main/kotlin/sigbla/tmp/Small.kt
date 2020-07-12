package sigbla.tmp

import sigbla.app.IndexRelation.*
import sigbla.app.timeseries.*
import sigbla.app.Table.Companion.on
import sigbla.app.*
import sigbla.app.Table.Companion.onTest
import java.math.BigDecimal
import java.math.BigInteger

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.ThreadLocalRandom

fun main() {
    //val table = Table.newTable("Table A")
    val table = Table.newTable("test")

    for (col in listOf("A", "B", "C", "D", "E", "F")) {
        val withExtra = ThreadLocalRandom.current().nextBoolean()
        for (row in 0..10000) {
            if (withExtra)
                table[col, "E1", row] = "$col $row"
            else
                table[col, row] = "$col $row"
        }
    }

    val tableView = TableView.newTableView(table)

    tableView.show()

    println("END")
}
