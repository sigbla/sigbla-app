/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.AfterClass
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
import sigbla.app.exceptions.InvalidStorageException
import java.util.*

class TableStorageTest {
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

        save(table1 to tmpFile, compress = false)

        val table2 = Table[null]

        load(tmpFile to table2)

        val it1 = table1.iterator()
        val it2 = table2.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        deleteFolder(tmpFolder)

        remove(table1)
    }

    @Test
    fun `basic storage test - empty table - with compress`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}")

        val table1 = Table["Storage Test"]

        save(table1 to tmpFile, compress = true)

        val table2 = Table[null]

        load(tmpFile to table2)

        val it1 = table1.iterator()
        val it2 = table2.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        deleteFolder(tmpFolder)

        remove(table1)
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

        save(table1 to tmpFile, compress = false)

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
            val table1Column = table1Columns.next()
            assertEquals(table1Column.header, header)
            assertEquals(table1Column[0].value, this[0].value)
        }

        assertFalse(table1Columns.hasNext())

        val it1 = table1.iterator()
        val it2 = table2.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertEquals(it1.next().value, it2.next().value)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        deleteFolder(tmpFolder)

        remove(table1)
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

        save(table1 to tmpFile, compress = true)

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
            val table1Column = table1Columns.next()
            assertEquals(table1Column.header, header)
            assertEquals(table1Column[0].value, this[0].value)
        }

        assertFalse(table1Columns.hasNext())

        val it1 = table1.iterator()
        val it2 = table2.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertEquals(it1.next().value, it2.next().value)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        deleteFolder(tmpFolder)

        remove(table1)
    }

    @Test
    fun `implied filename`() {
        val name = UUID.randomUUID().toString()
        val file = File("$name.sigt")

        assertFalse(file.exists())

        val table1 = Table[name]

        table1["A"][0] = 100
        table1["A", "B"][0] = 200
        table1["A", "C"][0] = 250
        table1["A", "B", "C"][0] = 300
        table1["B"]
        table1["D", "E", 0] = 400

        save(table1)

        val table2 = clone(table1, "Storage test")

        remove(table1)

        val table3 = Table[name]

        val table2Columns = listOf(
            table2["A"],
            table2["A", "B"],
            table2["A", "C"],
            table2["A", "B", "C"],
            table2["B"],
            table2["D", "E"]
        ).iterator()

        load(table3) {
            val table2Column = table2Columns.next()
            assertEquals(table2Column.header, header)
            assertEquals(table2Column[0].value, this[0].value)
        }

        assertTrue(file.exists())
        file.delete()

        assertFalse(table2Columns.hasNext())

        val it1 = table2.iterator()
        val it2 = table3.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertEquals(it1.next().value, it2.next().value)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        remove(table2)
        remove(table3)
    }

    @Test
    fun `string filename`() {
        val name = UUID.randomUUID().toString()
        val file = File("$name.sigt")

        assertFalse(file.exists())

        val table1 = Table[name]

        table1["A"][0] = 100
        table1["A", "B"][0] = 200
        table1["A", "C"][0] = 250
        table1["A", "B", "C"][0] = 300
        table1["B"]
        table1["D", "E", 0] = 400

        save(table1 to name)

        val table2 = clone(table1, "Storage test")

        remove(table1)

        val table3 = Table[name]

        val table2Columns = listOf(
            table2["A"],
            table2["A", "B"],
            table2["A", "C"],
            table2["A", "B", "C"],
            table2["B"],
            table2["D", "E"]
        ).iterator()

        load(name to table3) {
            val table2Column = table2Columns.next()
            assertEquals(table2Column.header, header)
            assertEquals(table2Column[0].value, this[0].value)
        }

        assertTrue(file.exists())
        file.delete()

        assertFalse(table2Columns.hasNext())

        val it1 = table2.iterator()
        val it2 = table3.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertEquals(it1.next().value, it2.next().value)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        remove(table2)
        remove(table3)
    }

    @Test
    fun `ensure magic check`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}.sigt")

        tmpFile.writeBytes(ByteArray(100) { 0 })

        try {
            load(tmpFile to Table[null])
            assertTrue(false)
        } catch (ex: InvalidStorageException) {
            assertEquals("Unsupported file type", ex.message)
        }

        deleteFolder(tmpFolder)
    }

    @Test
    fun `version unsupported`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}.sigt")

        tmpFile.writeBytes("519b1a02000000".chunked(2).map { it.toInt(16).toByte() }.toByteArray())

        try {
            load(tmpFile to Table[null])
            assertTrue(false)
        } catch (ex: InvalidStorageException) {
            assertEquals("Table file version 2 not supported, please upgrade Sigbla", ex.message)
        }

        deleteFolder(tmpFolder)
    }

    @Test
    fun `load with column filter`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(), "test-${System.currentTimeMillis()}")

        val table1 = Table["Storage Test"]

        table1["A", 0] = "A0"
        table1["A", 1] = "A1"
        table1["B", 0] = "B0"
        table1["B", 1] = "B1"
        table1["C", 0] = "C0"
        table1["C", 1] = "C1"

        save(table1 to tmpFile)

        val table2 = Table[null]

        load(tmpFile to table2) {
            assertEquals(table2, table.source)
            if (header[0] == "C") remove(this)
            if (header[0] == "B") this[1] = Unit
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(table2).map { it.labels }.toList())
        assertEquals(listOf("A0", "A1"), table2["A"].map { it.toString() })
        assertEquals(listOf("B0"), table2["B"].map { it.toString() })
        assertFalse(Header["C"] in table2)

        val table3 = Table[null]

        load(tmpFile to table3)

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(table3).map { it.labels }.toList())
        assertEquals(listOf("A0", "A1"), table3["A"].map { it.toString() })
        assertEquals(listOf("B0", "B1"), table3["B"].map { it.toString() })
        assertEquals(listOf("C0", "C1"), table3["C"].map { it.toString() })

        remove(table1)
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
                        val type = ThreadLocalRandom.current().nextInt(0, 8)
                        when (type) {
                            0 -> { /* skip */ }
                            1 -> column[r] = ThreadLocalRandom.current().nextLong().toString() + " " + ThreadLocalRandom.current().nextBoolean()
                            2 -> column[r] = ThreadLocalRandom.current().nextLong()
                            3 -> column[r] = ThreadLocalRandom.current().nextDouble()
                            4 -> column[r] = BigInteger.valueOf(ThreadLocalRandom.current().nextLong()).multiply(BigInteger.valueOf(ThreadLocalRandom.current().nextLong()))
                            5 -> column[r] = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()).multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()))
                            6 -> column[r] = ThreadLocalRandom.current().nextBoolean()
                            7 -> column[r] = if (ThreadLocalRandom.current().nextBoolean()) div {
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
                assertEquals(c1.value, c2.value)
            }

            assertFalse(it1.hasNext())
            assertFalse(it2.hasNext())

            remove(table1)
        }

        deleteFolder(tmpFolder)
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}
