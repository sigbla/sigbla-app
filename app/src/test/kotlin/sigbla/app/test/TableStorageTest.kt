package sigbla.app.test

import sigbla.app.*
import org.junit.After
import org.junit.Test
import sigbla.app.internals.SerializationType
import sigbla.app.internals.SerializationUtils
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
    fun `type serialization`() {
        val check = mutableSetOf<Int>()
        for (i in 0..2500000) {
            val rnd = SerializationType.values().map { it.type }.toList().shuffled().first()
            check.add(rnd)

            when (rnd) {
                0 -> {
                    assertNull(SerializationUtils.toType(SerializationUtils.fromType(null)))
                }
                1 -> {
                    val v1 = ThreadLocalRandom.current().nextBoolean()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Boolean", v2?.javaClass.toString())
                }
                2 -> {
                    val v1 = ThreadLocalRandom.current().nextInt().toByte()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Byte", v2?.javaClass.toString())
                }
                3 -> {
                    assertFalse(true)
                }
                4 -> {
                    val v1 = ThreadLocalRandom.current().nextInt()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Integer", v2?.javaClass.toString())
                }
                5 -> {
                    val v1 = ThreadLocalRandom.current().nextLong()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Long", v2?.javaClass.toString())
                }
                6 -> {
                    assertFalse(true)
                }
                7 -> {
                    val v1 = ThreadLocalRandom.current().nextDouble()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Double", v2?.javaClass.toString())
                }
                8 -> {
                    assertFalse(true)
                }
                9 -> {
                    val ba = ByteArray(ThreadLocalRandom.current().nextInt(0, 1000))
                    ThreadLocalRandom.current().nextBytes(ba)
                    val v1 = String(ba)
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                10 -> {
                    val v1 = BigInteger(ThreadLocalRandom.current().nextLong().toString()).let {
                        if (ThreadLocalRandom.current().nextBoolean()) it.multiply(BigInteger.valueOf(ThreadLocalRandom.current().nextLong(1, 100))) else it
                    }
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                11 -> {
                    val v1 = BigDecimal(ThreadLocalRandom.current().nextDouble().toString()).let {
                        if (ThreadLocalRandom.current().nextBoolean()) it.multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1.0, 100.0))) else it
                    }
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                else -> throw UnsupportedOperationException()
            }
        }
        assertEquals(SerializationType.values().size, check.size)
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
            assertEquals(table1Column.columnHeader, columnHeader)
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
            assertEquals(table1Column.columnHeader, columnHeader)
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
    fun `big fuzzy storage test`() {
        val tmpFolder = Files.createTempDirectory("sigbla-test")
        val tmpFile = File(tmpFolder.toFile(),"test-${System.currentTimeMillis()}.sigt")

        for (t in 1..100) {
            val table1 = Table["Storage Test"]

            println("Generating table..")

            for (c in 1..ThreadLocalRandom.current().nextInt(1, 2000)) {
                val headers = generateSequence {
                    val r = ThreadLocalRandom.current().nextInt(0, 21)
                    if (r == 0) null else if (r == 1) "" else {
                        val ba = ByteArray(r)
                        ThreadLocalRandom.current().nextBytes(ba)
                        String(ba)
                    }
                }.toList().take(49)

                if (headers.isEmpty()) continue
                val column = table1[ColumnHeader(headers)]

                if (ThreadLocalRandom.current().nextBoolean()) {
                    val range = when (ThreadLocalRandom.current().nextInt(0, 11)) {
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
                        else -> throw IllegalArgumentException()
                    }

                    for (r in range) {
                        val type = ThreadLocalRandom.current().nextInt(0, 6)
                        when (type) {
                            0 -> { /* skip */ }
                            1 -> column[r] = ThreadLocalRandom.current().nextLong().toString() + " " + ThreadLocalRandom.current().nextBoolean()
                            2 -> column[r] = ThreadLocalRandom.current().nextLong()
                            3 -> column[r] = ThreadLocalRandom.current().nextDouble()
                            4 -> column[r] = BigInteger.valueOf(ThreadLocalRandom.current().nextLong()).multiply(BigInteger.valueOf(ThreadLocalRandom.current().nextLong()))
                            5 -> column[r] = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()).multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()))
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
                assertEquals(c1, c2)
            }

            assertFalse(it1.hasNext())
            assertFalse(it2.hasNext())
        }

        deleteFolder(tmpFolder)
    }
}