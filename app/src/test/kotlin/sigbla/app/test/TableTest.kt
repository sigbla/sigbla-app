package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.After
import sigbla.app.exceptions.InvalidTableException
import java.math.BigDecimal
import java.math.BigInteger
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
}