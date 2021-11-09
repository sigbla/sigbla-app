package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.After
import java.math.BigDecimal
import java.math.BigInteger

class TableTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
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
            assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), map { it.header }.toList())
            assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), map { it.header }.toList())
        }
    }

    @Test
    fun `table headers`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["B", 0] = "B0"

        val headers1 = t.headers

        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.header }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.header }.toList())

        t["C", 0] = "C0"
        t["D", 0] = "D0"

        val headers2 = t.headers

        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.header }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.header }.toList())

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headers2.map { it.header }.toList())
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headers2.map { it.header }.toList())
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
}