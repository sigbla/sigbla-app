package com.sigbla.prosheet

import com.sigbla.prosheet.table.*
import com.sigbla.prosheet.table.IndexRelation.*
import com.sigbla.prosheet.timeseries.*
import com.sigbla.prosheet.math.*
import java.math.BigDecimal
import java.math.BigInteger

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun main() {
    val table = Table.newTable("Table A")

    table["A", "B"][1] = "Test"
    val cell1 = table["A", "B"][BEFORE, 1]
    val cell2 = table[AFTER, 1]["A", "B"]
    val cell3 = table["A", "B"][1]
    val cell4 = table[1]["A", "B"]

    table[1][ColumnHeader("A")] = "A"
    table[1]["A", "B"] = "A"

    val tickerHeaders = table.headers.filter { it[2] == "Ticker" }

    val prices = tickerHeaders.map { header -> Pair(header, table[header][AT_OR_BEFORE, 1000]) }.toMap()

    table["A"][AT, LocalDate.now(), LocalTime.MAX, ZoneId.systemDefault()]

    val size = 10_000_000L

    val start1 = System.currentTimeMillis()
    for (i in 1.toLong()..size) {
        table["B"][i] = i

        if (i % 1_000_000L == 0L) println(i)
    }
    val end1 = System.currentTimeMillis()

    println()

    val start2 = System.currentTimeMillis()
    var foo = 0L as Number
    for (i in 1.toLong()..size) {
        foo += table["B"][i]

        if (i % 1_000_000L == 0L) println(i)
    }
    val end2 = System.currentTimeMillis()

    println()
    println("Write: " + (end1-start1))
    println("Read: " + (end2-start2))

    //Thread.sleep(Long.MAX_VALUE)

    table["B"][1].isNumeric()
    table["B"][3] = table["B"][1] + table["B"][2]
    table[1]["B"] = table["A"][1]

    table["A", 1]
    table["A", "B", BEFORE, 1]

    val math1a = 1 + table["B", 10]
    val math2a = 1L + table["B", 10]
    val math3a = 1.0F + table["B", 10]
    val math4a = 1.0 + table["B", 10]
    val math5a = BigInteger.ONE + table["B", 10]
    val math6a = BigDecimal.ONE + table["B", 10]
    val math7a = table["B", 10] + 1 + table["B", 10] + 1

    val math1b = 1 - table["B", 10]
    val math2b = 1L - table["B", 10]
    val math3b = 1.0F - table["B", 10]
    val math4b = 1.0 - table["B", 10]
    val math5b = BigInteger.ONE - table["B", 10]
    val math6b = BigDecimal.ONE - table["B", 10]
    val math7b = table["B", 10] - 1 - table["B", 10] - 1

    val math1c = 1 * table["B", 10]
    val math2c = 1L * table["B", 10]
    val math3c = 1.0F * table["B", 10]
    val math4c = 1.0 * table["B", 10]
    val math5c = BigInteger.ONE * table["B", 10]
    val math6c = BigDecimal.ONE * table["B", 10]
    val math7c = table["B", 10] * 1 * table["B", 10] * 1

    val math1d = 1 / table["B", 10]
    val math2d = 1L / table["B", 10]
    val math3d = 1.0F / table["B", 10]
    val math4d = 1.0 / table["B", 10]
    val math5d = BigInteger.ONE / table["B", 10]
    val math6d = BigDecimal.ONE / table["B", 10]
    val math7d = table["B", 10] / 1 / table["B", 10] / 1

    val math1e = 1 % table["B", 10]
    val math2e = 1L % table["B", 10]
    val math3e = 1.0F % table["B", 10]
    val math4e = 1.0 % table["B", 10]
    val math5e = BigInteger.ONE % table["B", 10]
    val math6e = BigDecimal.ONE % table["B", 10]
    val math7e = table["B", 10] % 1 % table["B", 10] % 1

    //if (math1 > 1)
}
