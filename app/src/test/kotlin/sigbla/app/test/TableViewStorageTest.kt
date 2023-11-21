/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.After
import org.junit.Test
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.listDirectoryEntries
import org.junit.Assert.*
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.util.concurrent.ThreadLocalRandom

class TableViewStorageTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
        TableView.names.forEach { TableView.delete(it) }
    }

    private fun deleteFolder(folder: Path) {
        folder.listDirectoryEntries().forEach {
            it.deleteExisting()
        }
        folder.deleteExisting()
    }

    @Test
    fun `basic storage test - empty table view - no compress`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}")

        val tableView1 = TableView["Storage Test"]

        save(tableView1 to tmpFile, compress = false)

        val tableView2 = TableView[null]

        load(tmpFile to tableView2)

        val it1 = tableView1.columnViews.iterator()
        val it2 = tableView2.columnViews.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        val it3 = tableView1.rowViews.iterator()
        val it4 = tableView2.rowViews.iterator()

        while (it3.hasNext() && it4.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it3.hasNext())
        assertFalse(it4.hasNext())

        val it5 = tableView1.cellViews.iterator()
        val it6 = tableView2.cellViews.iterator()

        while (it5.hasNext() && it6.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it5.hasNext())
        assertFalse(it6.hasNext())

        deleteFolder(tmpFolder)
    }

    @Test
    fun `basic storage test - empty table view - with compress`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}")

        val tableView1 = TableView["Storage Test"]

        save(tableView1 to tmpFile, compress = true)

        val tableView2 = TableView[null]

        load(tmpFile to tableView2)

        val it1 = tableView1.columnViews.iterator()
        val it2 = tableView2.columnViews.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        val it3 = tableView1.rowViews.iterator()
        val it4 = tableView2.rowViews.iterator()

        while (it3.hasNext() && it4.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it3.hasNext())
        assertFalse(it4.hasNext())

        val it5 = tableView1.cellViews.iterator()
        val it6 = tableView2.cellViews.iterator()

        while (it5.hasNext() && it6.hasNext()) {
            assertFalse(true)
        }

        assertFalse(it5.hasNext())
        assertFalse(it6.hasNext())

        deleteFolder(tmpFolder)
    }

    @Test
    fun `basic storage test - simple table view - no compress`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}")

        val tableView1 = TableView["Storage Test"]

        tableView1[CellHeight] = 100
        tableView1[CellWidth] = 200
        tableView1[CellClasses] = listOf("A", "AB", "ABC")
        tableView1[CellTopics] = listOf("B", "BB", "BBB")

        tableView1["A"][CellWidth] = 200
        tableView1["A"][CellClasses] = listOf("A", "AB", "ABC")
        tableView1["A"][CellTopics] = listOf("B", "BB", "BBB")

        tableView1[1][CellHeight] = 100
        tableView1[1][CellClasses] = listOf("A", "AB", "ABC")
        tableView1[1][CellTopics] = listOf("B", "BB", "BBB")

        tableView1["A", 1][CellHeight] = 100
        tableView1["A", 1][CellWidth] = 200
        tableView1["A", 1][CellClasses] = listOf("A", "AB", "ABC")
        tableView1["A", 1][CellTopics] = listOf("B", "BB", "BBB")

        save(tableView1 to tmpFile, compress = false)

        val tableView2 = TableView[null]

        load(tmpFile to tableView2)

        assertEquals(tableView1[CellHeight], tableView2[CellHeight])
        assertEquals(tableView1[CellWidth], tableView2[CellWidth])
        assertEquals(tableView1[CellClasses], tableView2[CellClasses])
        assertEquals(tableView1[CellTopics], tableView2[CellTopics])

        val it1 = tableView1.columnViews.iterator()
        val it2 = tableView2.columnViews.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            val v1 = it1.next()
            val v2 = it2.next()

            assertEquals(v1[CellWidth], v2[CellWidth])
            assertEquals(v1[CellClasses], v2[CellClasses])
            assertEquals(v1[CellTopics], v2[CellTopics])
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        val it3 = tableView1.rowViews.iterator()
        val it4 = tableView2.rowViews.iterator()

        while (it3.hasNext() && it4.hasNext()) {
            val v1 = it3.next()
            val v2 = it4.next()

            assertEquals(v1[CellHeight], v2[CellHeight])
            assertEquals(v1[CellClasses], v2[CellClasses])
            assertEquals(v1[CellTopics], v2[CellTopics])
        }

        assertFalse(it3.hasNext())
        assertFalse(it4.hasNext())

        val it5 = tableView1.cellViews.iterator()
        val it6 = tableView2.cellViews.iterator()

        while (it5.hasNext() && it6.hasNext()) {
            val v1 = it5.next()
            val v2 = it6.next()

            assertEquals(v1[CellHeight], v2[CellHeight])
            assertEquals(v1[CellWidth], v2[CellWidth])
            assertEquals(v1[CellClasses], v2[CellClasses])
            assertEquals(v1[CellTopics], v2[CellTopics])
        }

        assertFalse(it5.hasNext())
        assertFalse(it6.hasNext())

        deleteFolder(tmpFolder)
    }

    @Test
    fun `basic storage test - simple table view - with compress`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}")

        val tableView1 = TableView["Storage Test"]

        tableView1[CellHeight] = 100
        tableView1[CellWidth] = 200
        tableView1[CellClasses] = listOf("A", "AB", "ABC")
        tableView1[CellTopics] = listOf("B", "BB", "BBB")

        tableView1["A"][CellWidth] = 200
        tableView1["A"][CellClasses] = listOf("A", "AB", "ABC")
        tableView1["A"][CellTopics] = listOf("B", "BB", "BBB")

        tableView1[1][CellHeight] = 100
        tableView1[1][CellClasses] = listOf("A", "AB", "ABC")
        tableView1[1][CellTopics] = listOf("B", "BB", "BBB")

        tableView1["A", 1][CellHeight] = 100
        tableView1["A", 1][CellWidth] = 200
        tableView1["A", 1][CellClasses] = listOf("A", "AB", "ABC")
        tableView1["A", 1][CellTopics] = listOf("B", "BB", "BBB")

        save(tableView1 to tmpFile, compress = true)

        val tableView2 = TableView[null]

        load(tmpFile to tableView2)

        assertEquals(tableView1[CellHeight], tableView2[CellHeight])
        assertEquals(tableView1[CellWidth], tableView2[CellWidth])
        assertEquals(tableView1[CellClasses], tableView2[CellClasses])
        assertEquals(tableView1[CellTopics], tableView2[CellTopics])

        val it1 = tableView1.columnViews.iterator()
        val it2 = tableView2.columnViews.iterator()

        while (it1.hasNext() && it2.hasNext()) {
            val v1 = it1.next()
            val v2 = it2.next()

            assertEquals(v1[CellWidth], v2[CellWidth])
            assertEquals(v1[CellClasses], v2[CellClasses])
            assertEquals(v1[CellTopics], v2[CellTopics])
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())

        val it3 = tableView1.rowViews.iterator()
        val it4 = tableView2.rowViews.iterator()

        while (it3.hasNext() && it4.hasNext()) {
            val v1 = it3.next()
            val v2 = it4.next()

            assertEquals(v1[CellHeight], v2[CellHeight])
            assertEquals(v1[CellClasses], v2[CellClasses])
            assertEquals(v1[CellTopics], v2[CellTopics])
        }

        assertFalse(it3.hasNext())
        assertFalse(it4.hasNext())

        val it5 = tableView1.cellViews.iterator()
        val it6 = tableView2.cellViews.iterator()

        while (it5.hasNext() && it6.hasNext()) {
            val v1 = it5.next()
            val v2 = it6.next()

            assertEquals(v1[CellHeight], v2[CellHeight])
            assertEquals(v1[CellWidth], v2[CellWidth])
            assertEquals(v1[CellClasses], v2[CellClasses])
            assertEquals(v1[CellTopics], v2[CellTopics])
        }

        assertFalse(it5.hasNext())
        assertFalse(it6.hasNext())

        deleteFolder(tmpFolder)
    }

    @Test
    fun `big fuzzy table view storage test`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}.sigv")

        for (t in 1..100) {
            val tableView1 = TableView["Storage Test"]

            println("Generating table view..")

            if (ThreadLocalRandom.current().nextBoolean()) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    if (ThreadLocalRandom.current().nextBoolean()) tableView1[CellHeight] = null
                    else tableView1[CellHeight] = ThreadLocalRandom.current().nextLong()
                }
                if (ThreadLocalRandom.current().nextBoolean()) {
                    if (ThreadLocalRandom.current().nextBoolean()) tableView1[CellWidth] = null
                    else tableView1[CellWidth] = ThreadLocalRandom.current().nextLong()
                }
                if (ThreadLocalRandom.current().nextBoolean()) {
                    if (ThreadLocalRandom.current().nextBoolean()) tableView1[CellClasses] = null
                    else tableView1[CellClasses] = ThreadLocalRandom.current().nextInt(0, 100).let {
                        val values = mutableSetOf<String>()
                        for (i in 0 until it) {
                            val r = ThreadLocalRandom.current().nextInt(1, 21)
                            val ba = ByteArray(r)
                            ThreadLocalRandom.current().nextBytes(ba)
                            values += String(ba)
                        }
                        values.toList()
                    }
                }
                if (ThreadLocalRandom.current().nextBoolean()) {
                    if (ThreadLocalRandom.current().nextBoolean()) tableView1[CellTopics] = null
                    else tableView1[CellTopics] = ThreadLocalRandom.current().nextInt(0, 100).let {
                        val values = mutableSetOf<String>()
                        for (i in 0 until it) {
                            val r = ThreadLocalRandom.current().nextInt(1, 21)
                            val ba = ByteArray(r)
                            ThreadLocalRandom.current().nextBytes(ba)
                            values += String(ba)
                        }
                        values.toList()
                    }
                }
            }

            for (c in 1..ThreadLocalRandom.current().nextInt(1, 500)) {
                val headers = generateSequence {
                    val r = ThreadLocalRandom.current().nextInt(0, 21)
                    if (r == 0) null else if (r == 1) "" else {
                        val ba = ByteArray(r)
                        ThreadLocalRandom.current().nextBytes(ba)
                        String(ba)
                    }
                }.toList().take(49)

                if (headers.isEmpty()) continue
                val column = tableView1[ColumnHeader(headers)]

                if (ThreadLocalRandom.current().nextBoolean()) {
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        if (ThreadLocalRandom.current().nextBoolean()) tableView1[column][CellWidth] = null
                        else tableView1[column][CellWidth] = ThreadLocalRandom.current().nextLong()
                    }
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        if (ThreadLocalRandom.current().nextBoolean()) tableView1[column][CellClasses] = null
                        else tableView1[column][CellClasses] = ThreadLocalRandom.current().nextInt(0, 10).let {
                            val values = mutableSetOf<String>()
                            for (i in 0 until it) {
                                val r = ThreadLocalRandom.current().nextInt(1, 21)
                                val ba = ByteArray(r)
                                ThreadLocalRandom.current().nextBytes(ba)
                                values += String(ba)
                            }
                            values.toList()
                        }
                    }
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        if (ThreadLocalRandom.current().nextBoolean()) tableView1[column][CellTopics] = null
                        else tableView1[column][CellTopics] = ThreadLocalRandom.current().nextInt(0, 10).let {
                            val values = mutableSetOf<String>()
                            for (i in 0 until it) {
                                val r = ThreadLocalRandom.current().nextInt(1, 21)
                                val ba = ByteArray(r)
                                ThreadLocalRandom.current().nextBytes(ba)
                                values += String(ba)
                            }
                            values.toList()
                        }
                    }
                }

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
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            if (ThreadLocalRandom.current().nextBoolean()) {
                                if (ThreadLocalRandom.current().nextBoolean()) tableView1[r][CellHeight] = null
                                else tableView1[r][CellHeight] = ThreadLocalRandom.current().nextLong()
                            }
                            if (ThreadLocalRandom.current().nextBoolean()) {
                                if (ThreadLocalRandom.current().nextBoolean()) tableView1[r][CellClasses] = null
                                else tableView1[r][CellClasses] = ThreadLocalRandom.current().nextInt(0, 10).let {
                                    val values = mutableSetOf<String>()
                                    for (i in 0 until it) {
                                        val r = ThreadLocalRandom.current().nextInt(1, 21)
                                        val ba = ByteArray(r)
                                        ThreadLocalRandom.current().nextBytes(ba)
                                        values += String(ba)
                                    }
                                    values.toList()
                                }
                            }
                            if (ThreadLocalRandom.current().nextBoolean()) {
                                if (ThreadLocalRandom.current().nextBoolean()) tableView1[r][CellTopics] = null
                                else tableView1[r][CellTopics] = ThreadLocalRandom.current().nextInt(0, 10).let {
                                    val values = mutableSetOf<String>()
                                    for (i in 0 until it) {
                                        val r = ThreadLocalRandom.current().nextInt(1, 21)
                                        val ba = ByteArray(r)
                                        ThreadLocalRandom.current().nextBytes(ba)
                                        values += String(ba)
                                    }
                                    values.toList()
                                }
                            }
                        }

                        if (ThreadLocalRandom.current().nextBoolean()) {
                            if (ThreadLocalRandom.current().nextBoolean()) {
                                if (ThreadLocalRandom.current().nextBoolean()) tableView1[column, r][CellHeight] = null
                                else tableView1[column, r][CellHeight] = ThreadLocalRandom.current().nextLong()
                            }
                            if (ThreadLocalRandom.current().nextBoolean()) {
                                if (ThreadLocalRandom.current().nextBoolean()) tableView1[column, r][CellWidth] = null
                                else tableView1[column, r][CellWidth] = ThreadLocalRandom.current().nextLong()
                            }
                            if (ThreadLocalRandom.current().nextBoolean()) {
                                if (ThreadLocalRandom.current().nextBoolean()) tableView1[column, r][CellClasses] = null
                                else tableView1[column, r][CellClasses] =
                                    ThreadLocalRandom.current().nextInt(0, 10).let {
                                        val values = mutableSetOf<String>()
                                        for (i in 0 until it) {
                                            val r = ThreadLocalRandom.current().nextInt(1, 21)
                                            val ba = ByteArray(r)
                                            ThreadLocalRandom.current().nextBytes(ba)
                                            values += String(ba)
                                        }
                                        values.toList()
                                    }
                            }
                            if (ThreadLocalRandom.current().nextBoolean()) {
                                if (ThreadLocalRandom.current().nextBoolean()) tableView1[column, r][CellTopics] = null
                                else tableView1[column, r][CellTopics] =
                                    ThreadLocalRandom.current().nextInt(0, 10).let {
                                        val values = mutableSetOf<String>()
                                        for (i in 0 until it) {
                                            val r = ThreadLocalRandom.current().nextInt(1, 21)
                                            val ba = ByteArray(r)
                                            ThreadLocalRandom.current().nextBytes(ba)
                                            values += String(ba)
                                        }
                                        values.toList()
                                    }
                            }
                        }
                    }
                }
            }

            println("Storing table view..")

            save(tableView1 to tmpFile, compress = ThreadLocalRandom.current().nextBoolean())

            println("File size is ${tmpFile.length()}")
            assertTrue(tmpFile.length() > 0)

            val tableView2 = TableView[null]

            println("Loading table view..")

            load(tmpFile to tableView2)

            println("Comparing table views")

            assertEquals(tableView1[CellHeight], tableView2[CellHeight])
            assertEquals(tableView1[CellWidth], tableView2[CellWidth])
            assertEquals(tableView1[CellClasses], tableView2[CellClasses])
            assertEquals(tableView1[CellTopics], tableView2[CellTopics])

            val it1 = tableView1.columnViews.sortedBy { it.header }.iterator()
            val it2 = tableView2.columnViews.sortedBy { it.header }.iterator()

            while (it1.hasNext() && it2.hasNext()) {
                val v1 = it1.next()
                val v2 = it2.next()

                assertEquals(v1.header, v2.header)
                assertEquals(v1[CellWidth], v2[CellWidth])
                assertEquals(v1[CellClasses], v2[CellClasses])
                assertEquals(v1[CellTopics], v2[CellTopics])
            }

            assertFalse(it1.hasNext())
            assertFalse(it2.hasNext())

            val it3 = tableView1.rowViews.sortedBy { it.index }.iterator()
            val it4 = tableView2.rowViews.sortedBy { it.index }.iterator()

            while (it3.hasNext() && it4.hasNext()) {
                val v1 = it3.next()
                val v2 = it4.next()

                assertEquals(v1.index, v2.index)
                assertEquals(v1[CellHeight], v2[CellHeight])
                assertEquals(v1[CellClasses], v2[CellClasses])
                assertEquals(v1[CellTopics], v2[CellTopics])
            }

            assertFalse(it3.hasNext())
            assertFalse(it4.hasNext())

            val it5 = tableView1.cellViews.sortedWith { o1, o2 ->
                val cmp = o1.columnView.header.compareTo(o2.columnView.header)
                if (cmp == 0) o1.index.compareTo(o2.index) else cmp
            }.iterator()
            val it6 = tableView2.cellViews.sortedWith { o1, o2 ->
                val cmp = o1.columnView.header.compareTo(o2.columnView.header)
                if (cmp == 0) o1.index.compareTo(o2.index) else cmp
            }.iterator()

            while (it5.hasNext() && it6.hasNext()) {
                val v1 = it5.next()
                val v2 = it6.next()

                assertEquals(v1.columnView.header, v2.columnView.header)
                assertEquals(v1.index, v2.index)
                assertEquals(v1[CellHeight], v2[CellHeight])
                assertEquals(v1[CellWidth], v2[CellWidth])
                assertEquals(v1[CellClasses], v2[CellClasses])
                assertEquals(v1[CellTopics], v2[CellTopics])
            }

            assertFalse(it5.hasNext())
            assertFalse(it6.hasNext())
        }

        deleteFolder(tmpFolder)
    }
}
