/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.After
import org.junit.Test
import java.io.File
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ThreadLocalRandom
import kotlin.io.path.*
import org.junit.Assert.*

class TableStorageTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    private fun deleteFolder(folder: Path) {
        folder.listDirectoryEntries().forEach {
            it.deleteExisting()
        }
        folder.deleteExisting()
    }

    @Test
    fun `basic storage test - empty table - no compress`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}")

        val table1 = Table["Storage Test"]

        print(table1)

        save(table1 to tmpFile, compress = false)

        println()

        val table2 = Table[null]

        load(tmpFile to table2)

        println()

        print(table2)

        val it1 = table1.iterator()
        val it2 = table2.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        deleteFolder(tmpFolder)
    }

    @Test
    fun `basic storage test - empty table - with compress`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}")

        val table1 = Table["Storage Test"]

        print(table1)

        save(table1 to tmpFile, compress = true)

        println()

        val table2 = Table[null]

        load(tmpFile to table2)

        println()

        print(table2)

        val it1 = table1.iterator()
        val it2 = table2.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        deleteFolder(tmpFolder)
    }

    @Test
    fun `basic storage test - simple table - no compress`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}")

        val table1 = Table["Storage Test"]

        table1["A"][0] = 100
        table1["A", "B"][0] = 200
        table1["A", "C"][0] = 250
        table1["A", "B", "C"][0] = 300
        table1["B"]
        table1["D", "E", 0] = 400

        print(table1)

        save(table1 to tmpFile, compress = false)

        println()

        val table2 = Table[null]

        val table1Columns = listOf(
            table1["A"],
            table1["A", "B"],
            table1["A", "C"],
            table1["A", "B", "C"],
            table1["B"],
            table1["D", "E"]
        ).iterator()

        load(tmpFile to table2) {
            println("$this -> ${this.joinToString { "${it.index}:${it.value}" }}")
            val table1Column = table1Columns.next()
            assertEquals(table1Column.header, header)
            assertEquals(table1Column[0], this[0])
            return@load this
        }

        assertFalse(table1Columns.hasNext())

        println()

        print(table2)

        val it1 = table1.iterator()
        val it2 = table2.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertEquals(it1.next(), it2.next())
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        deleteFolder(tmpFolder)
    }

    @Test
    fun `basic storage test - simple table - with compress`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}")

        val table1 = Table["Storage Test"]

        table1["A"][0] = 100
        table1["A", "B"][0] = 200
        table1["A", "C"][0] = 250
        table1["A", "B", "C"][0] = 300
        table1["B"]
        table1["D", "E", 0] = 400

        print(table1)

        save(table1 to tmpFile, compress = true)

        println()

        val table2 = Table[null]

        val table1Columns = listOf(
            table1["A"],
            table1["A", "B"],
            table1["A", "C"],
            table1["A", "B", "C"],
            table1["B"],
            table1["D", "E"]
        ).iterator()

        load(tmpFile to table2) {
            println("$this -> ${this.joinToString { "${it.index}:${it.value}" }}")
            val table1Column = table1Columns.next()
            assertEquals(table1Column.header, header)
            assertEquals(table1Column[0], this[0])
            return@load this
        }

        assertFalse(table1Columns.hasNext())

        println()

        print(table2)

        val it1 = table1.iterator()
        val it2 = table2.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertEquals(it1.next(), it2.next())
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        deleteFolder(tmpFolder)
    }

    @Test
    fun `big fuzzy table storage test`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}.sigt")

        for (t in 1..100) {
            val table1 = Table["Storage Test"]

            println("Generating table..")

            for (c in 1..ThreadLocalRandom.current().nextInt(1, 2000)) {
                val headers = generateSequence {
                    val r = ThreadLocalRandom.current().nextInt(0, 21)
                    if (r == 0) null else if (r == 1) "" else {
                        val ba = ByteArray(r-1)
                        ThreadLocalRandom.current().nextBytes(ba)
                        String(ba)
                    }
                }.toList().take(49)

                if (headers.isEmpty()) continue
                val column = table1[Header(headers)]

                if (ThreadLocalRandom.current().nextBoolean()) {
                    val range = when (ThreadLocalRandom.current().nextInt(0, 13)) {
                        0 -> (Int.MIN_VALUE.toLong() - 100)..(Int.MIN_VALUE.toLong())
                        1 -> (Int.MIN_VALUE.toLong() - 100)..(Int.MIN_VALUE.toLong() + 1)
                        2 -> -2000L..-1L
                        3 -> -2000L..0L
                        4 -> -2000L..1L
                        5 -> -1000L..1000L
                        6 -> -1L..2000L
                        7 -> 0L..2000L
                        8 -> 1L..2000L
                        9 -> (Int.MAX_VALUE.toLong())..(Int.MAX_VALUE.toLong() + 100)
                        10 -> (Int.MAX_VALUE.toLong() + 1)..(Int.MAX_VALUE.toLong() + 100)
                        11 -> Long.MIN_VALUE..(Long.MIN_VALUE + 100)
                        12 -> (Long.MAX_VALUE-100)..Long.MAX_VALUE
                        else -> throw IllegalArgumentException()
                    }

                    for (r in range) {
                        val type = ThreadLocalRandom.current().nextInt(0, 7)
                        when (type) {
                            0 -> { /* skip */ }
                            1 -> column[r] = ThreadLocalRandom.current().nextLong().toString() + " " + ThreadLocalRandom.current().nextBoolean()
                            2 -> column[r] = ThreadLocalRandom.current().nextLong()
                            3 -> column[r] = ThreadLocalRandom.current().nextDouble()
                            4 -> column[r] = BigInteger.valueOf(ThreadLocalRandom.current().nextLong()).multiply(BigInteger.valueOf(ThreadLocalRandom.current().nextLong()))
                            5 -> column[r] = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()).multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()))
                            6 -> column[r] = if (ThreadLocalRandom.current().nextBoolean()) div {
                                val text = ThreadLocalRandom.current().nextLong().toString() + " " + ThreadLocalRandom.current().nextBoolean()
                                +text
                            } else div(classes = "with-class") {
                                val text = ThreadLocalRandom.current().nextLong().toString() + " " + ThreadLocalRandom.current().nextBoolean()
                                +text
                            }
                        }
                    }
                }
            }

            println("Storing table..")

            save(table1 to tmpFile, compress = ThreadLocalRandom.current().nextBoolean())

            println("File size is ${tmpFile.length()}")
            assertTrue(tmpFile.length() > 0)

            val table2 = Table[null]

            println("Loading table..")

            load(tmpFile to table2)

            println("Comparing tables")

            val it1 = table1.iterator()
            val it2 = table2.iterator()

            while (it1.hasNext() && it2.hasNext()) {
                val c1 = it1.next()
                val c2 = it2.next()
                assertEquals(c1.column.header, c2.column.header)
                assertEquals(c1.index, c2.index)
                assertEquals(c1, c2)
            }

            assertFalse(it1.hasNext())
            assertFalse(it2.hasNext())
        }

        deleteFolder(tmpFolder)
    }
}
