/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.After
import sigbla.app.exceptions.InvalidTableException
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.assertFailsWith

class TableTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `registry test`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
        val t2 = Table.fromRegistry(t1.name!!)
        assertEquals(t1, t2)
        assertTrue(t1 === t2)

        Table.delete(t1.name!!)

        assertFailsWith(InvalidTableException::class) {
            Table.fromRegistry(t1.name!!)
        }

        val t3 = Table.fromRegistry(t1.name!!) {
            Table[t1.name]
        }

        assertNotEquals(t1, t3)
        assertFalse(t1 === t3)
        assertEquals(t1.name, t3.name)

        assertEquals(1, Table.names.size)
        assertEquals(t1.name, Table.names.first())
    }

    @Test
    fun `cell range`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        for (c in listOf("D", "A", "B", "C", "E")) {
            for (r in -100..100) {
                t1[c][r] = "$c$r"
            }
        }

        assertEquals(listOf("A1", "A2", "B1", "B2"), (t1["A", 1]..t1["B", 2]).map { it.toString() }.toList())
        assertEquals(listOf("A1", "A2", "B1", "B2"), (t1["A", 1]..t1["B", 2] by CellOrder.COLUMN).map { it.toString() }.toList())
        assertEquals(listOf("A1", "B1", "A2", "B2"), (t1["A", 1]..t1["B", 2] by CellOrder.ROW).map { it.toString() }.toList())

        assertEquals(listOf("B2", "B1", "A2", "A1"), (t1["B", 2]..t1["A", 1]).map { it.toString() }.toList())
        assertEquals(listOf("B2", "B1", "A2", "A1"), (t1["B", 2]..t1["A", 1] by CellOrder.COLUMN).map { it.toString() }.toList())
        assertEquals(listOf("B2", "A2", "B1", "A1"), (t1["B", 2]..t1["A", 1] by CellOrder.ROW).map { it.toString() }.toList())

        // This introduces D between A and B
        move(t1["D"] after t1["A"])

        assertEquals(listOf("A1", "A2", "D1", "D2", "B1", "B2"), (t1["A", 1]..t1["B", 2]).map { it.toString() }.toList())
        assertEquals(listOf("A1", "A2", "D1", "D2", "B1", "B2"), (t1["A", 1]..t1["B", 2] by CellOrder.COLUMN).map { it.toString() }.toList())
        assertEquals(listOf("A1", "D1", "B1", "A2", "D2", "B2"), (t1["A", 1]..t1["B", 2] by CellOrder.ROW).map { it.toString() }.toList())

        assertEquals(listOf("B2", "B1", "D2", "D1", "A2", "A1"), (t1["B", 2]..t1["A", 1]).map { it.toString() }.toList())
        assertEquals(listOf("B2", "B1", "D2", "D1", "A2", "A1"), (t1["B", 2]..t1["A", 1] by CellOrder.COLUMN).map { it.toString() }.toList())
        assertEquals(listOf("B2", "D2", "A2", "B1", "D1", "A1"), (t1["B", 2]..t1["A", 1] by CellOrder.ROW).map { it.toString() }.toList())

        // This removes D, and returns us to the original sequences, because order is defined by the range
        move(t1["A"] after t1["B"])

        assertEquals(listOf("A1", "A2", "B1", "B2"), (t1["A", 1]..t1["B", 2]).map { it.toString() }.toList())
        assertEquals(listOf("A1", "A2", "B1", "B2"), (t1["A", 1]..t1["B", 2] by CellOrder.COLUMN).map { it.toString() }.toList())
        assertEquals(listOf("A1", "B1", "A2", "B2"), (t1["A", 1]..t1["B", 2] by CellOrder.ROW).map { it.toString() }.toList())

        assertEquals(listOf("B2", "B1", "A2", "A1"), (t1["B", 2]..t1["A", 1]).map { it.toString() }.toList())
        assertEquals(listOf("B2", "B1", "A2", "A1"), (t1["B", 2]..t1["A", 1] by CellOrder.COLUMN).map { it.toString() }.toList())
        assertEquals(listOf("B2", "A2", "B1", "A1"), (t1["B", 2]..t1["A", 1] by CellOrder.ROW).map { it.toString() }.toList())
    }

    @Test
    fun `column range`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        for (c in listOf("D", "A", "B", "C")) {
            for (r in -100..100) {
                t1[c][r] = "$c$r"
            }
        }

        assertEquals(listOf("[A]", "[B]", "[C]"), (t1["A"]..t1["C"]).map { it.toString() }.toList())
        assertEquals(listOf("[C]", "[B]", "[A]"), (t1["C"]..t1["A"]).map { it.toString() }.toList())

        // This introduces D between A and B
        move(t1["D"] after t1["A"])

        assertEquals(listOf("[A]", "[D]", "[B]", "[C]"), (t1["A"]..t1["C"]).map { it.toString() }.toList())
        assertEquals(listOf("[C]", "[B]", "[D]", "[A]"), (t1["C"]..t1["A"]).map { it.toString() }.toList())

        // This removes D and B
        move(t1["A"] after t1["C"])

        assertEquals(listOf("[A]", "[C]"), (t1["A"]..t1["C"]).map { it.toString() }.toList())
        assertEquals(listOf("[C]", "[A]"), (t1["C"]..t1["A"]).map { it.toString() }.toList())
    }

    @Test
    fun `row range`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        for (c in listOf("D", "A", "B", "C")) {
            for (r in -100..100) {
                t1[c][r] = "$c$r"
            }
        }

        assertEquals(listOf("-1", "0", "1", "2"), (t1[-1]..t1[2]).map { it.toString() }.toList())
        assertEquals(listOf("2", "1", "0", "-1"), (t1[2]..t1[-1]).map { it.toString() }.toList())
    }

    @Test
    fun `clone table values`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r] = "$c$r A1"
            }
        }

        val t2 = clone(t1, "tableClone2")

        for (c in listOf("A", "B", "C")) {
            for (r in 1..100) {
                t2[c][r] = "$c$r B1"
            }
        }

        for (c in listOf("D")) {
            for (r in 1..100) {
                t1[c][r] = "$c$r A2"
            }
        }

        for (c in listOf("A", "B", "C")) {
            for (r in 1..100) {
                assertEquals("$c$r A1", valueOf<Any>(t1[c][r]))
            }
        }

        for (c in listOf("A", "B", "C")) {
            for (r in 1..100) {
                assertEquals("$c$r B1", valueOf<Any>(t2[c][r]))
            }
        }

        for (c in listOf("D")) {
            for (r in 1..100) {
                assertEquals("$c$r A2", valueOf<Any>(t1[c][r]))
            }
        }

        for (c in listOf("D")) {
            for (r in 1..100) {
                assertEquals("$c$r A1", valueOf<Any>(t2[c][r]))
            }
        }
    }

    @Test
    fun `compare cell to values`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]
        t["Long", 0] = 100L
        t["Double", 0] = 100.0
        t["BigInteger", 0] = BigInteger("100")
        t["BigDecimal", 0] = BigDecimal("100")

        // Contains
        assertTrue(100L in t["Long", 0])
        assertTrue(100.0 in t["Double", 0])
        assertTrue(BigInteger("100") in t["BigInteger", 0])
        assertTrue(BigDecimal("100") in t["BigDecimal", 0])

        // Long case
        assertTrue(t["Long", 0] > 0L)
        assertTrue(0L < t["Long", 0])

        assertTrue(t["Long", 0] >= 100L)
        assertTrue(100L <= t["Long", 0])

        assertTrue(t["Long", 0] <= 100L)
        assertTrue(100L >= t["Long", 0])

        assertTrue(t["Long", 0] < 200L)
        assertTrue(200L > t["Long", 0])

        assertTrue(t["Double", 0] > 0L)
        assertTrue(0L < t["Double", 0])

        assertTrue(t["Double", 0] >= 100L)
        assertTrue(100L <= t["Double", 0])

        assertTrue(t["Double", 0] <= 100L)
        assertTrue(100L >= t["Double", 0])

        assertTrue(t["Double", 0] < 200L)
        assertTrue(200L > t["Double", 0])

        assertTrue(t["BigInteger", 0] > 0L)
        assertTrue(0L < t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] >= 100L)
        assertTrue(100L <= t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] <= 100L)
        assertTrue(100L >= t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] < 200L)
        assertTrue(200L > t["BigInteger", 0])

        assertTrue(t["BigDecimal", 0] > 0L)
        assertTrue(0L < t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] >= 100L)
        assertTrue(100L <= t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] <= 100L)
        assertTrue(100L >= t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] < 200L)
        assertTrue(200L > t["BigDecimal", 0])

        // Double case
        assertTrue(t["Long", 0] > 0.0)
        assertTrue(0.0 < t["Long", 0])

        assertTrue(t["Long", 0] >= 100.0)
        assertTrue(100.0 <= t["Long", 0])

        assertTrue(t["Long", 0] <= 100.0)
        assertTrue(100.0 >= t["Long", 0])

        assertTrue(t["Long", 0] < 200.0)
        assertTrue(200.0 > t["Long", 0])

        assertTrue(t["Double", 0] > 0.0)
        assertTrue(0.0 < t["Double", 0])

        assertTrue(t["Double", 0] >= 100.0)
        assertTrue(100.0 <= t["Double", 0])

        assertTrue(t["Double", 0] <= 100.0)
        assertTrue(100.0 >= t["Double", 0])

        assertTrue(t["Double", 0] < 200.0)
        assertTrue(200.0 > t["Double", 0])

        assertTrue(t["BigInteger", 0] > 0.0)
        assertTrue(0.0 < t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] >= 100.0)
        assertTrue(100.0 <= t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] <= 100.0)
        assertTrue(100.0 >= t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] < 200.0)
        assertTrue(200.0 > t["BigInteger", 0])

        assertTrue(t["BigDecimal", 0] > 0.0)
        assertTrue(0.0 < t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] >= 100.0)
        assertTrue(100.0 <= t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] <= 100.0)
        assertTrue(100.0 >= t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] < 200.0)
        assertTrue(200.0 > t["BigDecimal", 0])

        // BigInteger case
        assertTrue(t["Long", 0] > BigInteger("0"))
        assertTrue(BigInteger("0") < t["Long", 0])

        assertTrue(t["Long", 0] >= BigInteger("100"))
        assertTrue(BigInteger("100") <= t["Long", 0])

        assertTrue(t["Long", 0] <= BigInteger("100"))
        assertTrue(BigInteger("100") >= t["Long", 0])

        assertTrue(t["Long", 0] < BigInteger("200"))
        assertTrue(BigInteger("200") > t["Long", 0])

        assertTrue(t["Double", 0] > BigInteger("0"))
        assertTrue(BigInteger("0") < t["Double", 0])

        assertTrue(t["Double", 0] >= BigInteger("100"))
        assertTrue(BigInteger("100") <= t["Double", 0])

        assertTrue(t["Double", 0] <= BigInteger("100"))
        assertTrue(BigInteger("100") >= t["Double", 0])

        assertTrue(t["Double", 0] < BigInteger("200"))
        assertTrue(BigInteger("200") > t["Double", 0])

        assertTrue(t["BigInteger", 0] > BigInteger("0"))
        assertTrue(BigInteger("0") < t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] >= BigInteger("100"))
        assertTrue(BigInteger("100") <= t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] <= BigInteger("100"))
        assertTrue(BigInteger("100") >= t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] < BigInteger("200"))
        assertTrue(BigInteger("200") > t["BigInteger", 0])

        assertTrue(t["BigDecimal", 0] > BigInteger("0"))
        assertTrue(BigInteger("0") < t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] >= BigInteger("100"))
        assertTrue(BigInteger("100") <= t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] <= BigInteger("100"))
        assertTrue(BigInteger("100") >= t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] < BigInteger("200"))
        assertTrue(BigInteger("200") > t["BigDecimal", 0])

        // BigDecimal case
        assertTrue(t["Long", 0] > BigDecimal("0.0"))
        assertTrue(BigDecimal("0.0") < t["Long", 0])

        assertTrue(t["Long", 0] >= BigDecimal("100.0"))
        assertTrue(BigDecimal("100.0") <= t["Long", 0])

        assertTrue(t["Long", 0] <= BigDecimal("100.0"))
        assertTrue(BigDecimal("100.0") >= t["Long", 0])

        assertTrue(t["Long", 0] < BigDecimal("200.0"))
        assertTrue(BigDecimal("200.0") > t["Long", 0])

        assertTrue(t["Double", 0] > BigDecimal("0.0"))
        assertTrue(BigDecimal("0.0") < t["Double", 0])

        assertTrue(t["Double", 0] >= BigDecimal("100.0"))
        assertTrue(BigDecimal("100.0") <= t["Double", 0])

        assertTrue(t["Double", 0] <= BigDecimal("100.0"))
        assertTrue(BigDecimal("100.0") >= t["Double", 0])

        assertTrue(t["Double", 0] < BigDecimal("200.0"))
        assertTrue(BigDecimal("200.0") > t["Double", 0])

        assertTrue(t["BigInteger", 0] > BigDecimal("0.0"))
        assertTrue(BigDecimal("0.0") < t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] >= BigDecimal("100.0"))
        assertTrue(BigDecimal("100.0") <= t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] <= BigDecimal("100.0"))
        assertTrue(BigDecimal("100.0") >= t["BigInteger", 0])

        assertTrue(t["BigInteger", 0] < BigDecimal("200.0"))
        assertTrue(BigDecimal("200.0") > t["BigInteger", 0])

        assertTrue(t["BigDecimal", 0] > BigDecimal("0.0"))
        assertTrue(BigDecimal("0.0") < t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] >= BigDecimal("100.0"))
        assertTrue(BigDecimal("100.0") <= t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] <= BigDecimal("100.0"))
        assertTrue(BigDecimal("100.0") >= t["BigDecimal", 0])

        assertTrue(t["BigDecimal", 0] < BigDecimal("200.0"))
        assertTrue(BigDecimal("200.0") > t["BigDecimal", 0])
    }

    @Test
    fun `table iterator`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 2] = "A2"
        t["A", 3] = "A3"

        t["B", 0] = "B0"
        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        assertEquals(listOf("A0", "A1", "A2", "A3", "B0", "B1", "B2", "B3"), t.map { valueOf<Any>(it) })
        assertEquals(listOf("A0", "A1", "A2", "A3", "B0", "B1", "B2", "B3"), t.iterator().asSequence().map { valueOf<Any>(it) }.toList())
    }

    @Test
    fun `column iterator`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 2] = "A2"
        t["A", 3] = "A3"

        t["B", 0] = "B0"
        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        assertEquals(listOf("A0", "A1", "A2", "A3"), t["A"].map { valueOf<Any>(it) })
        assertEquals(listOf("B0", "B1", "B2", "B3"), t["B"].iterator().asSequence().map { valueOf<Any>(it) }.toList())
    }

    @Test
    fun `row iterator`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"
        t["C", 0] = "C0"
        t["D", 0] = "D0"

        t["A", 1] = "A1"
        t["B", 1] = "B1"
        t["C", 1] = "C1"
        t["D", 1] = "D1"

        assertEquals(listOf("A0", "B0", "C0", "D0"), t[0].map { valueOf<Any>(it) })
        assertEquals(listOf("A1", "B1", "C1", "D1"), t[1].iterator().asSequence().map { valueOf<Any>(it) }.toList())
    }

    @Test
    fun `cell iterator`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"
        t["C", 0] = "C0"
        t["D", 0] = "D0"

        t["A", 1] = "A1"
        t["B", 1] = "B1"
        t["C", 1] = "C1"
        t["D", 1] = "D1"

        assertEquals(listOf("A0"), t["A", 0].map { valueOf<Any>(it) })
        assertEquals(listOf("A1"), t["A", 1].iterator().asSequence().map { valueOf<Any>(it) }.toList())
    }

    @Test
    fun `table clear`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"
        t["C", 0] = "C0"
        t["D", 0] = "D0"

        t["A", 1] = "A1"
        t["B", 1] = "B1"
        t["C", 1] = "C1"
        t["D", 1] = "D1"

        clear(t)

        assertFalse(t.iterator().hasNext())
        assertEquals(listOf("[A]", "[B]", "[C]", "[D]"), t.headers.map { it.toString() }.toList())
        assertFalse(t.indexes.iterator().hasNext())

        remove(t["A"])
        remove(t["B"])
        remove(t["C"])
        remove(t["D"])

        assertFalse(t.iterator().hasNext())
        assertFalse(t.headers.iterator().hasNext())
        assertFalse(t.indexes.iterator().hasNext())
    }

    @Test
    fun `column clear`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"
        t["C", 0] = "C0"
        t["D", 0] = "D0"

        t["A", 1] = "A1"
        t["B", 1] = "B1"
        t["C", 1] = "C1"
        t["D", 1] = "D1"

        clear(t["B"])

        assertFalse(t["B"].iterator().hasNext())
        assertTrue(t.iterator().hasNext())
        assertEquals(listOf("[A]", "[B]", "[C]", "[D]"), t.headers.map { it.toString() }.toList())
        assertTrue(t.indexes.iterator().hasNext())
        assertEquals(2, t["A"].indexes.count())
        assertEquals(0, t["B"].indexes.count())
    }

    @Test
    fun `row clear`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"
        t["C", 0] = "C0"
        t["D", 0] = "D0"

        t["A", 1] = "A1"
        t["B", 1] = "B1"
        t["C", 1] = "C1"
        t["D", 1] = "D1"

        clear(t[1])

        assertFalse(t[1].iterator().hasNext())
        assertTrue(t.iterator().hasNext())
        assertEquals(listOf("[A]", "[B]", "[C]", "[D]"), t.headers.map { it.toString() }.toList())
        assertTrue(t.indexes.iterator().hasNext())

        assertEquals(listOf(0L), t.indexes.toList())
    }

    @Test
    fun `valuesOf sequence`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"
        t["C", 0] = "C0"
        t["D", 0] = "D0"

        t["A", 1] = "A1"
        t["B", 1] = "B1"
        t["C", 1] = "C1"
        t["D", 1] = "D1"

        valuesOf<Any>(t[0]).apply {
            assertEquals(listOf("A0", "B0", "C0", "D0"), toList())
            assertEquals(listOf("A0", "B0", "C0", "D0"), toList())
        }

        valuesOf<Any>(t[1]).apply {
            assertEquals(listOf("A1", "B1", "C1", "D1"), toList())
            assertEquals(listOf("A1", "B1", "C1", "D1"), toList())
        }

        valuesOf<Any>(t["A"]).apply {
            assertEquals(listOf("A0", "A1"), toList())
            assertEquals(listOf("A0", "A1"), toList())
        }

        valuesOf<Any>(t["B"]).apply {
            assertEquals(listOf("B0", "B1"), toList())
            assertEquals(listOf("B0", "B1"), toList())
        }

        valuesOf<Any>(t["A", 0]..t["D", 1]).apply {
            assertEquals(listOf("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1"), toList())
            assertEquals(listOf("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1"), toList())
        }

        valuesOf<Any>(t).apply {
            assertEquals(listOf("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1"), toList())
            assertEquals(listOf("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1"), toList())
        }
    }

    @Test
    fun `headersOf sequence`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"
        t["C", 0] = "C0"
        t["D", 0] = "D0"

        headersOf(t).apply {
            assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), map { it.labels }.toList())
            assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), map { it.labels }.toList())
        }
    }

    @Test
    fun `table headers`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"

        val headers1 = t.headers

        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())

        t["C", 0] = "C0"
        t["D", 0] = "D0"

        val headers2 = t.headers

        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headers2.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headers2.map { it.labels }.toList())
    }

    @Test
    fun `table indexes`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 3] = "A3"
        t["A", 4] = "A4"

        t["B", 0] = "B0"
        t["B", 2] = "B2"
        t["B", 4] = "B4"

        val indexes1 = t.indexes

        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexes1.toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexes1.toList())

        t["C", 5] = "C5"

        val indexes2 = t.indexes

        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexes1.toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexes1.toList())

        assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexes2.toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexes2.toList())
    }

    @Test
    fun `index relation order`() {
        val relations = IndexRelation.values().sorted()

        // BEFORE, AT_OR_BEFORE, AT, AT_OR_AFTER, AFTER
        assertEquals(5, relations.size)
        assertEquals(IndexRelation.BEFORE, relations[0])
        assertEquals(IndexRelation.AT_OR_BEFORE, relations[1])
        assertEquals(IndexRelation.AT, relations[2])
        assertEquals(IndexRelation.AT_OR_AFTER, relations[3])
        assertEquals(IndexRelation.AFTER, relations[4])
    }

    @Test
    fun `index relation get`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        assertEquals(UnitCell::class, (t["A"] before -100)::class)
        assertEquals(UnitCell::class, (t["A"] atOrBefore -100)::class)
        assertEquals(UnitCell::class, (t["A"] at -100)::class)
        assertEquals(UnitCell::class, (t["A"] atOrAfter -100)::class)
        assertEquals(UnitCell::class, (t["A"] after -100)::class)

        assertEquals(UnitCell::class, (t["A"] before 0)::class)
        assertEquals(UnitCell::class, (t["A"] atOrBefore 0)::class)
        assertEquals(UnitCell::class, (t["A"] at 0)::class)
        assertEquals(UnitCell::class, (t["A"] atOrAfter 0)::class)
        assertEquals(UnitCell::class, (t["A"] after 0)::class)

        assertEquals(UnitCell::class, (t["A"] before 100)::class)
        assertEquals(UnitCell::class, (t["A"] atOrBefore 100)::class)
        assertEquals(UnitCell::class, (t["A"] at 100)::class)
        assertEquals(UnitCell::class, (t["A"] atOrAfter 100)::class)
        assertEquals(UnitCell::class, (t["A"] after 100)::class)

        assertEquals(-100L, (t["A"] before -100).index)
        assertEquals(-100L, (t["A"] atOrBefore -100).index)
        assertEquals(-100L, (t["A"] at -100).index)
        assertEquals(-100L, (t["A"] atOrAfter -100).index)
        assertEquals(-100L, (t["A"] after -100).index)

        assertEquals(0L, (t["A"] before 0).index)
        assertEquals(0L, (t["A"] atOrBefore 0).index)
        assertEquals(0L, (t["A"] at 0).index)
        assertEquals(0L, (t["A"] atOrAfter 0).index)
        assertEquals(0L, (t["A"] after 0).index)

        assertEquals(100L, (t["A"] before 100).index)
        assertEquals(100L, (t["A"] atOrBefore 100).index)
        assertEquals(100L, (t["A"] at 100).index)
        assertEquals(100L, (t["A"] atOrAfter 100).index)
        assertEquals(100L, (t["A"] after 100).index)

        assertEquals("A", (t["A"] before -100).column.header[0])
        assertEquals("A", (t["A"] atOrBefore -100).column.header[0])
        assertEquals("A", (t["A"] at -100).column.header[0])
        assertEquals("A", (t["A"] atOrAfter -100).column.header[0])
        assertEquals("A", (t["A"] after -100).column.header[0])

        assertEquals("A", (t["A"] before 0).column.header[0])
        assertEquals("A", (t["A"] atOrBefore 0).column.header[0])
        assertEquals("A", (t["A"] at 0).column.header[0])
        assertEquals("A", (t["A"] atOrAfter 0).column.header[0])
        assertEquals("A", (t["A"] after 0).column.header[0])

        assertEquals("A", (t["A"] before 100).column.header[0])
        assertEquals("A", (t["A"] atOrBefore 100).column.header[0])
        assertEquals("A", (t["A"] at 100).column.header[0])
        assertEquals("A", (t["A"] atOrAfter 100).column.header[0])
        assertEquals("A", (t["A"] after 100).column.header[0])

        t["A", -100] = "A-100"
        t["A", 0] = "A0"
        t["A", 100] = "A100"

        assertEquals(UnitCell::class, (t["A"] before -100)::class)
        assertEquals(StringCell::class, (t["A"] atOrBefore -100)::class)
        assertEquals(StringCell::class, (t["A"] at -100)::class)
        assertEquals(StringCell::class, (t["A"] atOrAfter -100)::class)
        assertEquals(StringCell::class, (t["A"] after -100)::class)

        assertEquals(StringCell::class, (t["A"] before 0)::class)
        assertEquals(StringCell::class, (t["A"] atOrBefore 0)::class)
        assertEquals(StringCell::class, (t["A"] at 0)::class)
        assertEquals(StringCell::class, (t["A"] atOrAfter 0)::class)
        assertEquals(StringCell::class, (t["A"] after 0)::class)

        assertEquals(StringCell::class, (t["A"] before 100)::class)
        assertEquals(StringCell::class, (t["A"] atOrBefore 100)::class)
        assertEquals(StringCell::class, (t["A"] at 100)::class)
        assertEquals(StringCell::class, (t["A"] atOrAfter 100)::class)
        assertEquals(UnitCell::class, (t["A"] after 100)::class)

        assertEquals(-101L, (t["A"] before -101).index)

        assertEquals(-101L, (t["A"] atOrBefore -101).index)
        assertEquals(-101L, (t["A"] at -101).index)
        assertEquals(-100L, (t["A"] atOrAfter -101).index)
        assertEquals(-100L, (t["A"] after -101).index)

        assertEquals(-100L, (t["A"] before -100).index)
        assertEquals(-100L, (t["A"] atOrBefore -100).index)
        assertEquals(-100L, (t["A"] at -100).index)
        assertEquals(-100L, (t["A"] atOrAfter -100).index)
        assertEquals(0L, (t["A"] after -100).index)

        assertEquals(-100L, (t["A"] before -1).index)
        assertEquals(-100L, (t["A"] atOrBefore -1).index)
        assertEquals(-1L, (t["A"] at -1).index)
        assertEquals(0L, (t["A"] atOrAfter -1).index)
        assertEquals(0L, (t["A"] after -1).index)

        assertEquals(-100L, (t["A"] before 0).index)
        assertEquals(0L, (t["A"] atOrBefore 0).index)
        assertEquals(0L, (t["A"] at 0).index)
        assertEquals(0L, (t["A"] atOrAfter 0).index)
        assertEquals(100L, (t["A"] after 0).index)

        assertEquals(0L, (t["A"] before 1).index)
        assertEquals(0L, (t["A"] atOrBefore 1).index)
        assertEquals(1L, (t["A"] at 1).index)
        assertEquals(100L, (t["A"] atOrAfter 1).index)
        assertEquals(100L, (t["A"] after 1).index)

        assertEquals(0L, (t["A"] before 100).index)
        assertEquals(100L, (t["A"] atOrBefore 100).index)
        assertEquals(100L, (t["A"] at 100).index)
        assertEquals(100L, (t["A"] atOrAfter 100).index)
        assertEquals(100L, (t["A"] after 100).index)

        assertEquals(101L, (t["A"] after 101).index)

        assertEquals("A", (t["A"] before -101).column.header[0])

        assertEquals("A", (t["A"] atOrBefore -101).column.header[0])
        assertEquals("A", (t["A"] at -101).column.header[0])
        assertEquals("A", (t["A"] atOrAfter -101).column.header[0])
        assertEquals("A", (t["A"] after -101).column.header[0])

        assertEquals("A", (t["A"] before -100).column.header[0])
        assertEquals("A", (t["A"] atOrBefore -100).column.header[0])
        assertEquals("A", (t["A"] at -100).column.header[0])
        assertEquals("A", (t["A"] atOrAfter -100).column.header[0])
        assertEquals("A", (t["A"] after -100).column.header[0])

        assertEquals("A", (t["A"] before -1).column.header[0])
        assertEquals("A", (t["A"] atOrBefore -1).column.header[0])
        assertEquals("A", (t["A"] at -1).column.header[0])
        assertEquals("A", (t["A"] atOrAfter -1).column.header[0])
        assertEquals("A", (t["A"] after -1).column.header[0])

        assertEquals("A", (t["A"] before 0).column.header[0])
        assertEquals("A", (t["A"] atOrBefore 0).column.header[0])
        assertEquals("A", (t["A"] at 0).column.header[0])
        assertEquals("A", (t["A"] atOrAfter 0).column.header[0])
        assertEquals("A", (t["A"] after 0).column.header[0])

        assertEquals("A", (t["A"] before 1).column.header[0])
        assertEquals("A", (t["A"] atOrBefore 1).column.header[0])
        assertEquals("A", (t["A"] at 1).column.header[0])
        assertEquals("A", (t["A"] atOrAfter 1).column.header[0])
        assertEquals("A", (t["A"] after 1).column.header[0])

        assertEquals("A", (t["A"] before 100).column.header[0])
        assertEquals("A", (t["A"] atOrBefore 100).column.header[0])
        assertEquals("A", (t["A"] at 100).column.header[0])
        assertEquals("A", (t["A"] atOrAfter 100).column.header[0])
        assertEquals("A", (t["A"] after 100).column.header[0])

        assertEquals("A", (t["A"] after 101).column.header[0])
    }

    @Test
    fun `cell invoke`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A"][1L] = BigDecimal.ONE
        assertEquals(t["A"][1L], t["A"][2L] { BigDecimal.ONE })

        assertEquals(t["A", 1L], t["A", 2L])

        t["A"][1L] = BigInteger.TWO
        assertEquals(t["A"][1L], t["A"][2L] { BigInteger.TWO })

        assertEquals(t["A", 1L], t["A", 2L])

        t["A"][1L] = 3.0
        assertEquals(t["A"][1L], t["A"][2L] { 3.0 })

        assertEquals(t["A", 1L], t["A", 2L])

        t["A"][1L] = 4L
        assertEquals(t["A"][1L], t["A"][2L] { 4L })

        assertEquals(t["A", 1L], t["A", 2L])

        t["A"][1L] = 5 as Number
        assertEquals(t["A"][1L], t["A"][2L] { 5 as Number })

        assertEquals(t["A", 1L], t["A", 2L])

        t["A"][1L] = "6"
        assertEquals(t["A"][1L], t["A"][2L] { "6" })

        assertEquals(t["A", 1L], t["A", 2L])

        t["B", 3L] = "Cell"
        t["A"][1L] = t["B", 3L]
        assertEquals(t["A"][1L], t["A"][2L] { t["B", 3L] })

        assertEquals(t["A", 1L], t["A", 2L])

        assertEquals(Unit, t["A"][2L] { Unit })

        assertEquals(t["A", 1L], t["A", 2L])

        t["A"][1L] = null
        assertNull(t["A"][2L] { null })

        assertEquals(t["A", 1L], t["A", 2L])

        for (i in 1..10) t["B", i] = i

        t["A"][1L] = sum(t["B",1]..t["B", 10])
        t["A"][2L] { sum(t["B",1]..t["B", 10]) }

        assertEquals(t["A", 1L], t["A", 2L])

        assertEquals(55L, t["A", 2L].value)

        t["B", 1] = 2

        assertEquals(56L, t["A", 2L].value)
        assertEquals(t["A", 1L], t["A", 2L])
    }

    @Test
    fun `equality checks`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        t1["A", 1] = "A1"
        t1["A", 2] = "A2"
        t1["B", 1] = "B1"
        t1["B", 2] = "B2"

        val t2 = clone(t1)

        assertEquals(t1, Table.fromRegistry(t1.name!!))
        assertTrue(t1 === Table.fromRegistry(t1.name!!))
        assertNotEquals(t1, t2)
        assertNotEquals(t2, Table.fromRegistry(t1.name!!))

        assertEquals(t1["A"], Table.fromRegistry(t1.name!!)["A"])
        assertNotEquals(t1["A"], t1["B"])
        assertNotEquals(t1["A"], t2["A"])

        assertEquals(t1["A"].header, t2["A"].header)
        assertNotEquals(t1["A"].header, t1["B"].header)

        assertEquals(t1[1], Table.fromRegistry(t1.name!!)[1])
        assertNotEquals(t1[1], t1[2])
        assertNotEquals(t1[1], t2[1])
        assertEquals(t1[1].index, t2[1].index)
    }

    @Test
    fun `batching commit`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        val t2 = t1 {
            t1["A", 1] = "A1"
            t1["A", 2] = "A2"
            t1["B", 1] = "B1"
            t1["B", 2] = "B2"

            return@t1 clone(t1)
        }

        assertEquals("A1", t1["A", 1].value)
        assertEquals("A2", t1["A", 2].value)
        assertEquals("B1", t1["B", 1].value)
        assertEquals("B2", t1["B", 2].value)

        val it1 = t1.iterator()
        val it2 = t2.iterator()

        assertTrue(it1.hasNext())
        assertTrue(it2.hasNext())

        while (it1.hasNext() && it2.hasNext()) {
            assertEquals(it1.next(), it2.next())
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())
    }

    @Test
    fun `nested batching`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var count = 0

        val t2 = t1 {
            on(this) events {
                count += count()
            }

            this {
                assertTrue(this === this@t1)
                assertTrue(this === t1)
                assertTrue(this == this@t1)
                assertTrue(this == t1)

                t1["A", 1] = "A1"
                t1["A", 2] = "A2"
                t1["B", 1] = "B1"
                t1["B", 2] = "B2"
            }

            assertEquals(0, count)

            return@t1 clone(t1)
        }

        assertEquals(4, count)

        assertEquals("A1", t1["A", 1].value)
        assertEquals("A2", t1["A", 2].value)
        assertEquals("B1", t1["B", 1].value)
        assertEquals("B2", t1["B", 2].value)

        val it1 = t1.iterator()
        val it2 = t2.iterator()

        assertTrue(it1.hasNext())
        assertTrue(it2.hasNext())

        while (it1.hasNext() && it2.hasNext()) {
            assertEquals(it1.next(), it2.next())
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())
    }

    @Test
    fun `batching abort`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
        var t2: Table? = null

        assertFailsWith(RuntimeException::class) {
            t1 {
                t1["A", 1] = "A1"
                t1["A", 2] = "A2"
                t1["B", 1] = "B1"
                t1["B", 2] = "B2"

                t2 = clone(t1)

                throw RuntimeException()
            }
        }

        assertNotNull(t2)
        assertFalse(t1.iterator().hasNext())

        assertEquals("A1", t2!!["A", 1].value)
        assertEquals("A2", t2!!["A", 2].value)
        assertEquals("B1", t2!!["B", 1].value)
        assertEquals("B2", t2!!["B", 2].value)
    }

    @Test
    fun `batching threads`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        t1["A", 1] = "A1"
        t1["A", 2] = "A2"
        t1["B", 1] = "B1"
        t1["B", 2] = "B2"

        val flag1 = AtomicReference(true)
        val flag2 = AtomicReference(true)
        val flag3 = AtomicReference(true)
        val flag4 = AtomicReference(true)
        val flag5 = AtomicReference(true)

        val thread1 = thread {
            t1 {
                flag1.set(false)
                while(flag2.get()) Thread.sleep(1)

                assertEquals("A1", t1["A", 1].value)
                assertEquals("A2", this["A", 2].value)
                assertEquals("B1", t1["B", 1].value)
                assertEquals("B2", this["B", 2].value)

                this {
                    this["A", 1] = "A1 T"
                    t1["A", 2] = "A2 T"
                    this["B", 1] = "B1 T"
                    t1["B", 2] = "B2 T"
                }

                flag3.set(false)

                while(flag4.get()) Thread.sleep(1)
            }
        }

        while (flag1.get()) Thread.sleep(1)

        val thread2 = thread {
            t1 {
                assertEquals("A1 T", t1["A", 1].value)
                assertEquals("A2 T", t1["A", 2].value)
                assertEquals("B1 T", t1["B", 1].value)
                assertEquals("B2 T", t1["B", 2].value)

                flag5.set(false)
            }
        }

        Thread.sleep(100)

        flag2.set(false)
        while (flag3.get()) Thread.sleep(1)

        assertEquals("A1", t1["A", 1].value)
        assertEquals("A2", t1["A", 2].value)
        assertEquals("B1", t1["B", 1].value)
        assertEquals("B2", t1["B", 2].value)

        assertTrue(flag5.get())

        flag4.set(false)
        thread1.join()
        thread2.join()

        assertFalse(flag5.get())

        assertEquals("A1 T", t1["A", 1].value)
        assertEquals("A2 T", t1["A", 2].value)
        assertEquals("B1 T", t1["B", 1].value)
        assertEquals("B2 T", t1["B", 2].value)
    }

    @Test
    fun `table compact`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", -1] = "A-1"
        t["A", 2] = "A2"
        t["A", 4] = "A4"
        t["A", 6] = "A6"

        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 4] = "B4"

        var events: List<TableListenerEvent<out Any, out Any>> = emptyList()

        on(t, skipHistory = true) events {
            events = toList()
        }

        compact(t)

        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), t.indexes.toList())
        assertEquals(listOf("A-1", "A2", "A4", "A6"), t["A"].map { it.toString() }.toList())
        assertEquals(listOf("B1", "B2", "B4"), t["B"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 2L, 3L, 4L), t["A"].map { it.index }.toList())
        assertEquals(listOf(1L, 2L, 3L), t["B"].map { it.index }.toList())

        val eventsOnA = events.filter { it.newValue.column.header[0] == "A" }.sortedBy { it.newValue.index }

        assertEquals(7, eventsOnA.count())
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 6L), eventsOnA.map { it.oldValue.index })
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 6L), eventsOnA.map { it.newValue.index })
        assertEquals(listOf("A-1", "", "", "A2", "", "A4", "A6"), eventsOnA.map { it.oldValue.toString() })
        assertEquals(listOf("", "A-1", "", "A2", "A4", "A6", ""), eventsOnA.map { it.newValue.toString() })

        val eventsOnB = events.filter { it.newValue.column.header[0] == "B" }.sortedBy { it.newValue.index }

        assertEquals(5, eventsOnB.count())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), eventsOnB.map { it.oldValue.index })
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), eventsOnB.map { it.newValue.index })
        assertEquals(listOf("", "B1", "B2", "", "B4"), eventsOnB.map { it.oldValue.toString() })
        assertEquals(listOf("", "B1", "B2", "B4", ""), eventsOnB.map { it.newValue.toString() })
    }

    @Test
    fun `column left right`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"
        t["C", 0] = "C0"

        assertEquals(t["B"], t["B"] left 0)
        assertEquals(t["B"], t["B"] right 0)

        assertEquals(t["B"], t["C"] left 1)
        assertEquals(t["A"], t["C"] left 2)
        assertNull( t["C"] left 3)
        assertNull( t["C"] left 4)

        assertEquals(t["A"], t["B"] left 1)
        assertNull( t["B"] left 2)

        assertEquals(t["B"], t["A"] right 1)
        assertEquals(t["C"], t["A"] right 2)
        assertNull( t["A"] right 3)
        assertNull( t["A"] right 4)

        assertEquals(t["C"], t["B"] right 1)
        assertNull( t["B"] right 2)

        assertEquals(t["B"], t["C"] right -1)
        assertEquals(t["A"], t["C"] right -2)
        assertNull( t["C"] right -3)
        assertNull( t["C"] right -4)

        assertEquals(t["A"], t["B"] right -1)
        assertNull( t["B"] right -2)

        assertEquals(t["B"], t["A"] left -1)
        assertEquals(t["C"], t["A"] left -2)
        assertNull( t["A"] left -3)
        assertNull( t["A"] left -4)

        assertEquals(t["C"], t["B"] left -1)
        assertNull( t["B"] left -2)
    }

    @Test
    fun `column internal swap`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 2] = "A2"

        t["B", 0] = "B0"
        t["B", 1] = "B1"

        t["C", 0] = "C0"
        t["C", 1] = "C1"
        t["C", 3] = "C3"

        var events: List<TableListenerEvent<out Any, out Any>> = emptyList()

        on(t, skipHistory = true) events {
            events = toList()
        }

        swap(t["A"], t["C"])

        assertEquals(listOf("C0", "C1", "C3"), t["A"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 1L, 3L), t["A"].map { it.index }.toList())
        assertEquals(listOf("A", "A", "A"), t["A"].map { it.column.header[0] }.toList())

        assertEquals(listOf("A0", "A2"), t["C"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 2L), t["C"].map { it.index }.toList())
        assertEquals(listOf("C", "C"), t["C"].map { it.column.header[0] }.toList())

        assertEquals(listOf("B0", "B1"), t["B"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 1L), t["B"].map { it.index }.toList())
        assertEquals(listOf("B", "B"), t["B"].map { it.column.header[0] }.toList())

        assertEquals(listOf("A", "B", "C"), t.headers.map { it[0] }.toList())

        assertEquals(8, events.size)

        val eventsA = events.filter { it.newValue.column.header[0] == "A" }.sortedBy { it.newValue.index }
        val eventsC = events.filter { it.newValue.column.header[0] == "C" }.sortedBy { it.newValue.index }

        assertEquals(4, eventsA.size)
        assertEquals(4, eventsC.size)

        val oldA = listOf("A0", Unit, "A2", Unit)
        val newA = listOf("C0", "C1", Unit, "C3")

        for (i in 0..3) {
            assertEquals("A", eventsA[i].oldValue.column.header[0])
            assertEquals("A", eventsA[i].newValue.column.header[0])
            assertEquals(i.toLong(), eventsA[i].oldValue.index)
            assertEquals(i.toLong(), eventsA[i].newValue.index)
            assertEquals(oldA[i], valueOf(eventsA[i].oldValue))
            assertEquals(newA[i], valueOf(eventsA[i].newValue))
        }

        val oldC = listOf("C0", "C1", Unit, "C3")
        val newC = listOf("A0", Unit, "A2", Unit)

        for (i in 0..3) {
            assertEquals("C", eventsC[i].oldValue.column.header[0])
            assertEquals("C", eventsC[i].newValue.column.header[0])
            assertEquals(i.toLong(), eventsC[i].oldValue.index)
            assertEquals(i.toLong(), eventsC[i].newValue.index)
            assertEquals(oldC[i], valueOf(eventsC[i].oldValue))
            assertEquals(newC[i], valueOf(eventsC[i].newValue))
        }
    }

    @Test
    fun `column external swap`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0-1"
        t1["A", 2] = "A2-1"

        t1["B", 0] = "B0-1"
        t1["B", 1] = "B1-1"

        t1["C", 0] = "C0-1"
        t1["C", 1] = "C1-1"
        t1["C", 3] = "C3-1"

        t2["A", 0] = "A0-2"
        t2["A", 2] = "A2-2"

        t2["B", 0] = "B0-2"
        t2["B", 1] = "B1-2"

        t2["C", 0] = "C0-2"
        t2["C", 1] = "C1-2"
        t2["C", 3] = "C3-2"

        var events1: List<TableListenerEvent<out Any, out Any>> = emptyList()
        var events2: List<TableListenerEvent<out Any, out Any>> = emptyList()

        on(t1, skipHistory = true) events {
            events1 = toList()
        }

        on(t2, skipHistory = true) events {
            events2 = toList()
        }

        swap(t1["A"], t2["C"])

        assertEquals(listOf("C0-2", "C1-2", "C3-2"), t1["A"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 1L, 3L), t1["A"].map { it.index }.toList())
        assertEquals(listOf("A", "A", "A"), t1["A"].map { it.column.header[0] }.toList())

        assertEquals(listOf("A0-1", "A2-1"), t2["C"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 2L), t2["C"].map { it.index }.toList())
        assertEquals(listOf("C", "C"), t2["C"].map { it.column.header[0] }.toList())

        assertEquals(listOf("B0-1", "B1-1"), t1["B"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 1L), t1["B"].map { it.index }.toList())
        assertEquals(listOf("B", "B"), t1["B"].map { it.column.header[0] }.toList())

        assertEquals(listOf("B0-2", "B1-2"), t2["B"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 1L), t2["B"].map { it.index }.toList())
        assertEquals(listOf("B", "B"), t2["B"].map { it.column.header[0] }.toList())

        assertEquals(listOf("A", "B", "C"), t1.headers.map { it[0] }.toList())
        assertEquals(listOf("A", "B", "C"), t2.headers.map { it[0] }.toList())

        assertEquals(4, events1.size)
        assertEquals(4, events2.size)

        val eventsA = events1.filter { it.newValue.column.header[0] == "A" }.sortedBy { it.newValue.index }
        val eventsC = events2.filter { it.newValue.column.header[0] == "C" }.sortedBy { it.newValue.index }

        assertEquals(4, eventsA.size)
        assertEquals(4, eventsC.size)

        val oldA = listOf("A0-1", Unit, "A2-1", Unit)
        val newA = listOf("C0-2", "C1-2", Unit, "C3-2")

        for (i in 0..3) {
            assertEquals("A", eventsA[i].oldValue.column.header[0])
            assertEquals("A", eventsA[i].newValue.column.header[0])
            assertEquals(i.toLong(), eventsA[i].oldValue.index)
            assertEquals(i.toLong(), eventsA[i].newValue.index)
            assertEquals(oldA[i], valueOf(eventsA[i].oldValue))
            assertEquals(newA[i], valueOf(eventsA[i].newValue))
        }

        val oldC = listOf("C0-2", "C1-2", Unit, "C3-2")
        val newC = listOf("A0-1", Unit, "A2-1", Unit)

        for (i in 0..3) {
            assertEquals("C", eventsC[i].oldValue.column.header[0])
            assertEquals("C", eventsC[i].newValue.column.header[0])
            assertEquals(i.toLong(), eventsC[i].oldValue.index)
            assertEquals(i.toLong(), eventsC[i].newValue.index)
            assertEquals(oldC[i], valueOf(eventsC[i].oldValue))
            assertEquals(newC[i], valueOf(eventsC[i].newValue))
        }
    }

    @Test
    fun `row internal swap`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 2] = "A2"

        t["B", 1] = "B1"
        t["B", 2] = "B2"

        t["C", 0] = "C0"
        t["C", 1] = "C1"
        t["C", 3] = "C3"

        var events: List<TableListenerEvent<out Any, out Any>> = emptyList()

        on(t, skipHistory = true) events {
            events = toList()
        }

        swap(t[1], t[2])

        assertEquals(listOf("A0", "A2"), t["A"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 1L), t["A"].map { it.index }.toList())
        assertEquals(listOf("A", "A"), t["A"].map { it.column.header[0] }.toList())

        assertEquals(listOf("B2", "B1"), t["B"].map { it.toString() }.toList())
        assertEquals(listOf(1L, 2L), t["B"].map { it.index }.toList())
        assertEquals(listOf("B", "B"), t["B"].map { it.column.header[0] }.toList())

        assertEquals(listOf("C0", "C1", "C3"), t["C"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 2L, 3L), t["C"].map { it.index }.toList())
        assertEquals(listOf("C", "C", "C"), t["C"].map { it.column.header[0] }.toList())

        assertEquals(listOf("A", "B", "C"), t.headers.map { it[0] }.toList())

        assertEquals(6, events.size)

        val eventsA = events.filter { it.newValue.column.header[0] == "A" }.sortedBy { it.newValue.index }
        val eventsB = events.filter { it.newValue.column.header[0] == "B" }.sortedBy { it.newValue.index }
        val eventsC = events.filter { it.newValue.column.header[0] == "C" }.sortedBy { it.newValue.index }

        assertEquals(2, eventsA.size)
        assertEquals(2, eventsB.size)
        assertEquals(2, eventsC.size)

        val oldA = listOf(Unit, "A2")
        val newA = listOf("A2", Unit)

        for (i in 0..1) {
            assertEquals("A", eventsA[i].oldValue.column.header[0])
            assertEquals("A", eventsA[i].newValue.column.header[0])
            assertEquals(i.toLong() + 1, eventsA[i].oldValue.index)
            assertEquals(i.toLong() + 1, eventsA[i].newValue.index)
            assertEquals(oldA[i], valueOf(eventsA[i].oldValue))
            assertEquals(newA[i], valueOf(eventsA[i].newValue))
        }

        val oldB = listOf("B1", "B2")
        val newB = listOf("B2", "B1")

        for (i in 0..1) {
            assertEquals("B", eventsB[i].oldValue.column.header[0])
            assertEquals("B", eventsB[i].newValue.column.header[0])
            assertEquals(i.toLong() + 1, eventsB[i].oldValue.index)
            assertEquals(i.toLong() + 1, eventsB[i].newValue.index)
            assertEquals(oldB[i], valueOf(eventsB[i].oldValue))
            assertEquals(newB[i], valueOf(eventsB[i].newValue))
        }

        val oldC = listOf("C1", Unit)
        val newC = listOf(Unit, "C1")

        for (i in 0..1) {
            assertEquals("C", eventsC[i].oldValue.column.header[0])
            assertEquals("C", eventsC[i].newValue.column.header[0])
            assertEquals(i.toLong() + 1, eventsC[i].oldValue.index)
            assertEquals(i.toLong() + 1, eventsC[i].newValue.index)
            assertEquals(oldC[i], valueOf(eventsC[i].oldValue))
            assertEquals(newC[i], valueOf(eventsC[i].newValue))
        }
    }

    @Test
    fun `row external swap`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0-1"
        t1["A", 2] = "A2-1"

        t1["B", 1] = "B1-1"
        t1["B", 2] = "B2-1"

        t1["C", 0] = "C0-1"
        t1["C", 1] = "C1-1"
        t1["C", 3] = "C3-1"

        t2["A", 0] = "A0-2"
        t2["A", 2] = "A2-2"

        t2["B", 1] = "B1-2"
        t2["B", 2] = "B2-2"

        t2["C", 0] = "C0-2"
        t2["C", 1] = "C1-2"
        t2["C", 3] = "C3-2"

        var events1: List<TableListenerEvent<out Any, out Any>> = emptyList()
        var events2: List<TableListenerEvent<out Any, out Any>> = emptyList()

        on(t1, skipHistory = true) events {
            events1 = toList()
        }

        on(t2, skipHistory = true) events {
            events2 = toList()
        }

        swap(t1[1], t2[2])

        assertEquals(listOf("A0-1", "A2-2", "A2-1"), t1["A"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 1L, 2L), t1["A"].map { it.index }.toList())
        assertEquals(listOf("A", "A", "A"), t1["A"].map { it.column.header[0] }.toList())

        assertEquals(listOf("B2-2", "B2-1"), t1["B"].map { it.toString() }.toList())
        assertEquals(listOf(1L, 2L), t1["B"].map { it.index }.toList())
        assertEquals(listOf("B", "B"), t1["B"].map { it.column.header[0] }.toList())

        assertEquals(listOf("C0-1", "C3-1"), t1["C"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 3L), t1["C"].map { it.index }.toList())
        assertEquals(listOf("C", "C"), t1["C"].map { it.column.header[0] }.toList())

        assertEquals(listOf("A", "B", "C"), t1.headers.map { it[0] }.toList())

        assertEquals(listOf("A0-2"), t2["A"].map { it.toString() }.toList())
        assertEquals(listOf(0L), t2["A"].map { it.index }.toList())
        assertEquals(listOf("A"), t2["A"].map { it.column.header[0] }.toList())

        assertEquals(listOf("B1-2", "B1-1"), t2["B"].map { it.toString() }.toList())
        assertEquals(listOf(1L, 2L), t2["B"].map { it.index }.toList())
        assertEquals(listOf("B", "B"), t2["B"].map { it.column.header[0] }.toList())

        assertEquals(listOf("C0-2", "C1-2", "C1-1", "C3-2"), t2["C"].map { it.toString() }.toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), t2["C"].map { it.index }.toList())
        assertEquals(listOf("C", "C", "C", "C"), t2["C"].map { it.column.header[0] }.toList())

        assertEquals(listOf("A", "B", "C"), t2.headers.map { it[0] }.toList())

        assertEquals(3, events1.size)
        assertEquals(3, events2.size)

        val events1A = events1.filter { it.newValue.column.header[0] == "A" }.sortedBy { it.newValue.index }
        val events1B = events1.filter { it.newValue.column.header[0] == "B" }.sortedBy { it.newValue.index }
        val events1C = events1.filter { it.newValue.column.header[0] == "C" }.sortedBy { it.newValue.index }

        assertEquals(1, events1A.size)
        assertEquals(1, events1B.size)
        assertEquals(1, events1C.size)

        assertEquals("A", events1A[0].oldValue.column.header[0])
        assertEquals("A", events1A[0].newValue.column.header[0])
        assertEquals(1L, events1A[0].oldValue.index)
        assertEquals(1L, events1A[0].newValue.index)
        assertEquals(Unit, valueOf(events1A[0].oldValue))
        assertEquals("A2-2", valueOf(events1A[0].newValue))

        assertEquals("B", events1B[0].oldValue.column.header[0])
        assertEquals("B", events1B[0].newValue.column.header[0])
        assertEquals(1L, events1B[0].oldValue.index)
        assertEquals(1L, events1B[0].newValue.index)
        assertEquals("B1-1", valueOf(events1B[0].oldValue))
        assertEquals("B2-2", valueOf(events1B[0].newValue))

        assertEquals("C", events1C[0].oldValue.column.header[0])
        assertEquals("C", events1C[0].newValue.column.header[0])
        assertEquals(1L, events1C[0].oldValue.index)
        assertEquals(1L, events1C[0].newValue.index)
        assertEquals("C1-1", valueOf(events1C[0].oldValue))
        assertEquals(Unit, valueOf(events1C[0].newValue))

        val events2A = events2.filter { it.newValue.column.header[0] == "A" }.sortedBy { it.newValue.index }
        val events2B = events2.filter { it.newValue.column.header[0] == "B" }.sortedBy { it.newValue.index }
        val events2C = events2.filter { it.newValue.column.header[0] == "C" }.sortedBy { it.newValue.index }

        assertEquals(1, events2A.size)
        assertEquals(1, events2B.size)
        assertEquals(1, events2C.size)

        assertEquals("A", events2A[0].oldValue.column.header[0])
        assertEquals("A", events2A[0].newValue.column.header[0])
        assertEquals(2L, events2A[0].oldValue.index)
        assertEquals(2L, events2A[0].newValue.index)
        assertEquals("A2-2", valueOf(events2A[0].oldValue))
        assertEquals(Unit, valueOf(events2A[0].newValue))

        assertEquals("B", events2B[0].oldValue.column.header[0])
        assertEquals("B", events2B[0].newValue.column.header[0])
        assertEquals(2L, events2B[0].oldValue.index)
        assertEquals(2L, events2B[0].newValue.index)
        assertEquals("B2-2", valueOf(events2B[0].oldValue))
        assertEquals("B1-1", valueOf(events2B[0].newValue))

        assertEquals("C", events2C[0].oldValue.column.header[0])
        assertEquals("C", events2C[0].newValue.column.header[0])
        assertEquals(2L, events2C[0].oldValue.index)
        assertEquals(2L, events2C[0].newValue.index)
        assertEquals(Unit, valueOf(events2C[0].oldValue))
        assertEquals("C1-1", valueOf(events2C[0].newValue))
    }

    @Test
    fun `table column sort`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = 300
        t["A", 1] = 400
        t["A", 2] = 500

        t["B", 0] = 200
        t["B", 1] = 500
        t["B", 2] = 300

        t["C", 0] = 100
        t["C", 1] = 600
        t["C", 2] = 400

        var events: List<TableListenerEvent<out Any, out Any>> = emptyList()

        on(t, skipHistory = true) events {
            events = toList()
        }

        sort(t by t["A"]..t["A"]) { c1, c2 -> c1[0].compareTo(c2[0]) }

        assertEquals(listOf("A", "B", "C"), t.headers.map { it[0] }.toList())
        assertEquals(3, events.size)

        events = emptyList()

        sort(t, t["C"]..t["C"]) { c1, c2 -> c1[0].compareTo(c2[0]) }

        assertEquals(listOf("A", "B", "C"), t.headers.map { it[0] }.toList())
        assertEquals(3, events.size)

        events = emptyList()

        sort(t by t["A"]..t["B"]) { c1, c2 -> c1[0].compareTo(c2[0]) }

        assertEquals(listOf("B", "A", "C"), t.headers.map { it[0] }.toList())
        assertEquals(6, events.size)
        assertEquals(setOf("A", "B"), events.map { it.newValue.column.header[0] }.toSet())

        events = emptyList()

        sort(t by t["A"]..t["C"]) { c1, c2 -> c1[0].compareTo(c2[0]) }

        assertEquals(listOf("B", "C", "A"), t.headers.map { it[0] }.toList())
        assertEquals(6, events.size)
        assertEquals(setOf("A", "C"), events.map { it.newValue.column.header[0] }.toSet())

        events = emptyList()

        sort(t by t["B"]..t["A"]) { c1, c2 -> c1[0].compareTo(c2[0]) }

        assertEquals(listOf("C", "B", "A"), t.headers.map { it[0] }.toList())

        assertEquals(9, events.size)
        assertEquals(9, events.count { it.newValue.column.header == it.oldValue.column.header })
        assertEquals(9, events.count { it.newValue == it.oldValue })
        assertEquals(3, events.count { it.newValue.column.header[0] == "A" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "B" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "C" })
        assertEquals(listOf(100L, 600L, 400L, 200L, 500L, 300L, 300L, 400L, 500L), t.map { valueOf<Long>(it) }.toList())

        events = emptyList()

        // Reverse
        sort(t by t["A"]..t["C"]) { c1, c2 -> c1[0].compareTo(c2[0]) }

        assertEquals(listOf("A", "B", "C"), t.headers.map { it[0] }.toList())
        assertEquals(9, events.size)
        assertEquals(9, events.count { it.newValue.column.header == it.oldValue.column.header })
        assertEquals(9, events.count { it.newValue == it.oldValue })
        assertEquals(3, events.count { it.newValue.column.header[0] == "A" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "B" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "C" })
        assertEquals(listOf(300L, 400L, 500L, 200L, 500L, 300L, 100L, 600L, 400L), t.map { valueOf<Long>(it) }.toList())

        events = emptyList()

        // Double reverse
        sort(t by t["C"]..t["A"]) { c1, c2 -> c2[0].compareTo(c1[0]) }

        assertEquals(listOf("C", "B", "A"), t.headers.map { it[0] }.toList())
        assertEquals(9, events.size)
        assertEquals(9, events.count { it.newValue.column.header == it.oldValue.column.header })
        assertEquals(9, events.count { it.newValue == it.oldValue })
        assertEquals(3, events.count { it.newValue.column.header[0] == "A" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "B" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "C" })
        assertEquals(listOf(100L, 600L, 400L, 200L, 500L, 300L, 300L, 400L, 500L), t.map { valueOf<Long>(it) }.toList())
    }

    @Test
    fun `table row sort`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = 300
        t["A", 1] = 400
        t["A", 2] = 500

        t["B", 0] = 200
        t["B", 1] = 500
        t["B", 2] = 300

        t["C", 0] = 100
        t["C", 1] = 600
        t["C", 2] = 400

        var events: List<TableListenerEvent<out Any, out Any>> = emptyList()

        on(t, skipHistory = true) events {
            events = toList()
        }

        sort(t by t[0]..t[0]) { r1, r2 -> r1["B"].compareTo(r2["B"]) }

        assertEquals(listOf(200L, 500L, 300L), t["B"].map { valueOf<Long>(it) }.toList())
        assertEquals(3, events.size)

        events = emptyList()

        sort(t, t[2]..t[2]) { r1, r2 -> r1["B"].compareTo(r2["B"]) }

        assertEquals(listOf(200L, 500L, 300L), t["B"].map { valueOf<Long>(it) }.toList())
        assertEquals(3, events.size)

        events = emptyList()

        sort(t by t[3]..t[3]) { r1, r2 -> r1["B"].compareTo(r2["B"]) }

        assertEquals(listOf(200L, 500L, 300L), t["B"].map { valueOf<Long>(it) }.toList())
        assertEquals(3, events.size)

        events = emptyList()

        sort(t by t[1]..t[2]) { r1, r2 -> r1["B"].compareTo(r2["B"]) }

        assertEquals(listOf(200L, 300L, 500L), t["B"].map { valueOf<Long>(it) }.toList())
        assertEquals(listOf("A", "B", "C"), t.headers.map { it[0] }.toList())
        assertEquals(6, events.size)
        assertEquals(6, events.count { it.newValue.column.header == it.oldValue.column.header })
        assertEquals(6, events.count { it.newValue != it.oldValue })
        assertEquals(2, events.count { it.newValue.column.header[0] == "A" })
        assertEquals(2, events.count { it.newValue.column.header[0] == "B" })
        assertEquals(2, events.count { it.newValue.column.header[0] == "C" })

        events = emptyList()

        // Reverse
        sort(t by t[0]..t[1]) { r1, r2 -> r2["B"].compareTo(r1["B"]) }

        assertEquals(listOf(300L, 200L, 500L), t["B"].map { valueOf<Long>(it) }.toList())
        assertEquals(listOf("A", "B", "C"), t.headers.map { it[0] }.toList())
        assertEquals(6, events.size)
        assertEquals(6, events.count { it.newValue.column.header == it.oldValue.column.header })
        assertEquals(6, events.count { it.newValue != it.oldValue })
        assertEquals(2, events.count { it.newValue.column.header[0] == "A" })
        assertEquals(2, events.count { it.newValue.column.header[0] == "B" })
        assertEquals(2, events.count { it.newValue.column.header[0] == "C" })

        events = emptyList()

        // Double reverse
        sort(t by t[2]..t[0]) { r1, r2 -> r2["C"].compareTo(r1["C"]) }

        assertEquals(listOf("A", "B", "C"), t.headers.map { it[0] }.toList())
        assertEquals(9, events.size)
        assertEquals(9, events.count { it.newValue.column.header == it.oldValue.column.header })
        assertEquals(6, events.count { it.newValue != it.oldValue })
        assertEquals(3, events.count { it.newValue.column.header[0] == "A" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "B" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "C" })

        assertEquals(listOf(300L, 500L, 400L, 200L, 300L, 500L, 100L, 400L, 600L), t.map { valueOf<Long>(it) }.toList())
    }
}
