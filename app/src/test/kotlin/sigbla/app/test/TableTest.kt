/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.AfterClass
import sigbla.app.exceptions.*
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.assertFailsWith

class TableTest {
    @Test
    fun `registry test`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val t2 = Table[t1.name]

        t1["A", 1] = "A1"

        assertEquals(t1, t2)
        assertTrue(t1 === t2)

        assertTrue(Table.names.contains(t1.name))
        assertTrue(Table.tables.mapNotNull { it.name }.contains(t1.name))
        assertFalse(t1.closed)

        remove(t1)

        assertFalse(Table.names.contains(t1.name))
        assertFalse(Table.tables.mapNotNull { it.name }.contains(t1.name))
        assertTrue(t1.closed)
        assertTrue(t2.closed)

        assertEquals("A1", t1["A", 1].value)
        assertEquals("A1", t2["A", 1].value)

        assertFailsWith<InvalidRefException> {
            t1["A", 1] = "A1"
        }

        assertFailsWith(InvalidTableException::class) {
            Table[t1.name!!, { name -> throw InvalidTableException("No table by name $name") }]
        }
        assertFalse(Table.names.contains(t1.name))

        val t3 = Table[t1.name!!, { Table[null] }]

        assertTrue(Table.names.contains(t1.name))
        assertTrue(Table.tables.contains(t3))

        t3["A", 1] = "A1"

        assertNotEquals(t1, t3)
        assertFalse(t1 === t3)
        assertEquals(t1.name, t3.name)

        remove(t3)

        assertFailsWith(InvalidTableException::class) {
            Table[t3.name!!, { name -> throw InvalidTableException("No table by name $name") }]
        }

        val t4 = Table[t3.name!!, { name -> Table["$name extra"] }]

        assertTrue(Table.names.contains(t4.name))
        assertTrue(Table.tables.contains(t4))

        assertTrue(Table.names.contains(t4.name + " extra"))
        val t5 = Table[t4.name + " extra", { name -> throw InvalidTableException("No table by name $name") }]

        assertTrue(Table.tables.contains(t5))

        assertTrue(t4.source === t5)
    }

    @Test
    fun `cell range`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        assertTrue((t1["F", 0]..t1["G", 0]).isEmpty())

        for (c in listOf("D", "A", "B", "C", "E")) {
            for (r in -100..100) {
                t1[c][r] = "$c$r"
            }
        }

        assertFalse((t1["A", 0]..t1["C", 0]).isEmpty())
        assertFalse((t1["E", 0]..t1["H", 0]).isEmpty())

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

        t1.forEach {
            assertTrue(it in t1["D", -100]..t1["E", 100])
        }

        assertFalse(t1["G", 0] in t1["D", -100]..t1["E", 100])
        assertFalse(t1["G", -1000] in t1["D", -100]..t1["E", 100])
        assertFalse(t1["G", 1000] in t1["D", -100]..t1["E", 100])
        assertFalse(null in t1["D", -100]..t1["E", 100])
        assertFalse("Not a value" in t1["D", -100]..t1["E", 100])
    }

    @Test
    fun `invalid cell range`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val t2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

        val c1 = t1["A", 0]
        val c2 = t2["A", 1]

        assertFailsWith<InvalidCellException> { c1..c2 }
    }

    @Test
    fun `column range`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (c in listOf("D", "A", "B", "C")) {
            for (r in -100..100) {
                t1[c][r] = "$c$r"
            }
        }

        assertTrue(t1["A"] in t1["A"]..t1["C"])
        assertTrue((t1["A"]..t1["C"]).contains(t1["B"]))
        assertFalse(t1["E"] in t1["A"]..t1["E"])

        assertFalse((t1["A"]..t1["C"]).isEmpty())
        assertTrue((t1["E"]..t1["F"]).isEmpty())

        assertEquals(listOf("[A]", "[B]", "[C]"), (t1["A"]..t1["C"]).map { it.header.labels.toString() }.toList())
        assertEquals(listOf("[C]", "[B]", "[A]"), (t1["C"]..t1["A"]).map { it.header.labels.toString() }.toList())

        // This introduces D between A and B
        move(t1["D"] after t1["A"])

        assertEquals(listOf("[A]", "[D]", "[B]", "[C]"), (t1["A"]..t1["C"]).map { it.header.labels.toString() }.toList())
        assertEquals(listOf("[C]", "[B]", "[D]", "[A]"), (t1["C"]..t1["A"]).map { it.header.labels.toString() }.toList())

        // This removes D and B
        move(t1["A"] after t1["C"])

        assertEquals(listOf("[A]", "[C]"), (t1["A"]..t1["C"]).map { it.header.labels.toString() }.toList())
        assertEquals(listOf("[C]", "[A]"), (t1["C"]..t1["A"]).map { it.header.labels.toString() }.toList())
    }

    @Test
    fun `invalid column range`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val t2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

        val c1 = t1["A"]
        val c2 = t2["B"]

        assertFailsWith<InvalidColumnException> { c1..c2 }
    }

    @Test
    fun `row range`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (c in listOf("D", "A", "B", "C")) {
            for (r in -100..100) {
                t1[c][r] = "$c$r"
            }
        }

        assertEquals(listOf("-1", "0", "1", "2"), (t1[-1]..t1[2]).map { it.index.toString() }.toList())
        assertEquals(listOf("2", "1", "0", "-1"), (t1[2]..t1[-1]).map { it.index.toString() }.toList())

        assertFalse((t1[0]..t1[0]).isEmpty())
        assertFalse((t1[0]..t1[10]).isEmpty())
        assertFalse((t1[10]..t1[0]).isEmpty())

        assertTrue((t1[0]..t1[0]).contains(t1[0]))
        assertTrue((t1[-1]..t1[0]).contains(t1[-1]))
        assertTrue((t1[-1]..t1[0]).contains(t1[0]))
        assertTrue((t1[1]..t1[-1]).contains(t1[1]))
        assertFalse((t1[10]..t1[0]).contains(t1[-1]))
        assertFalse((t1[0]..t1[10]).contains(t1[11]))
    }

    @Test
    fun `invalid row range`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val t2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

        val r1 = t1[0]
        val r2 = t2[10]

        assertFailsWith<InvalidRowException> { r1..r2 }

        for (type in IndexRelation.entries) {
            if (type == IndexRelation.AT) r1..t1[type, 10]
            else assertFailsWith<InvalidRowException> { r1..t1[type, 10] }
        }

        for (type in IndexRelation.entries) {
            if (type == IndexRelation.AT) t2[type, 0]..r2
            else assertFailsWith<InvalidRowException> { t2[type, 0]..r2 }
        }
    }

    @Test
    fun `clone table values`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r] = "$c$r A1"
            }
        }

        val t2 = clone(t1, "tableClone2")

        assertTrue(Table.tables.contains(t2))
        assertTrue(Table.names.contains("tableClone2"))

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
    fun `clone is not closed`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t1["A", 0] = "A0v1"

        remove(t1)

        assertTrue(t1.closed)

        val t2 = clone(t1)

        assertTrue(t1 === t2.source)
        assertTrue(t2.source?.closed == true)
        assertFalse(t2.closed)

        t2["A", 0] = "A0v2"

        assertEquals("A0v2", t2["A", 0].value)
    }

    @Test
    fun `compare cell to values`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        assertEquals(listOf("[A]", "[B]", "[C]", "[D]"), t.headers.map { it.labels.toString() }.toList())
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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        assertEquals(listOf("[A]", "[B]", "[C]", "[D]"), t.headers.map { it.labels.toString() }.toList())
        assertTrue(t.indexes.iterator().hasNext())
        assertEquals(2, t["A"].indexes.count())
        assertEquals(0, t["B"].indexes.count())
    }

    @Test
    fun `row clear`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        assertEquals(listOf("[A]", "[B]", "[C]", "[D]"), t.headers.map { it.labels.toString() }.toList())
        assertTrue(t.indexes.iterator().hasNext())

        assertEquals(listOf(0L), t.indexes.toList())
    }

    @Test
    fun `valuesOf sequence`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = "A0"
        t["B", 0] = "B0"

        val headers1 = t.headers
        val columns1 = t.columns

        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns1.map { it.header.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns1.map { it.header.labels }.toList())

        t["C", 0]
        t["D", 0]

        assertTrue(Header["A"] in t)
        assertTrue(Header["B"] in t)
        assertTrue(t["B"] in t)
        assertTrue(Header["C"] !in t)
        assertFalse(Header["D"] in t)
        assertFalse(t["D"] in t)

        val headers2 = t.headers
        val columns2 = t.columns

        assertEquals(listOf(listOf("A"), listOf("B")), headers2.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers2.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns2.map { it.header.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns2.map { it.header.labels }.toList())

        t["C", 0] = "C0"
        t["D", 0] = "D0"

        assertTrue(Header["A"] in t)
        assertTrue(Header["B"] in t)
        assertTrue(Header["C"] in t)
        assertTrue(Header["D"] in t)

        val headers3 = t.headers
        val columns3 = t.columns

        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns1.map { it.header.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns1.map { it.header.labels }.toList())

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headers3.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headers3.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), columns3.map { it.header.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), columns3.map { it.header.labels }.toList())

        var prevOrder: Long? = null
        for (column in columns1) {
            if (prevOrder != null) assertTrue(prevOrder < column.order)
            prevOrder = column.order
            assertEquals(t, column.table)
        }

        prevOrder = null
        for (column in columns2) {
            if (prevOrder != null) assertTrue(prevOrder < column.order)
            prevOrder = column.order
            assertEquals(t, column.table)
        }

        prevOrder = null
        for (column in columns3) {
            if (prevOrder != null) assertTrue(prevOrder < column.order)
            prevOrder = column.order
            assertEquals(t, column.table)
        }
    }

    @Test
    fun `row headers`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = "A0"
        t["B", 0] = "B0"

        val headers1 = t[0].headers
        val columns1 = t[0].columns

        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns1.map { it.header.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns1.map { it.header.labels }.toList())

        t["C", 0]
        t["D", 0]

        assertTrue(Header["A"] in t)
        assertTrue(Header["B"] in t)
        assertTrue(Header["C"] !in t)
        assertFalse(Header["D"] in t)

        val headers2 = t[0].headers
        val columns2 = t[0].columns

        assertEquals(listOf(listOf("A"), listOf("B")), headers2.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers2.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns2.map { it.header.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns2.map { it.header.labels }.toList())

        t["C", 0] = "C0"
        t["D", 0] = "D0"

        assertTrue(Header["A"] in t)
        assertTrue(Header["B"] in t)
        assertTrue(Header["C"] in t)
        assertFalse(Header["D"] !in t)

        val headers3 = t[0].headers
        val columns3 = t[0].columns

        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), headers1.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns1.map { it.header.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B")), columns1.map { it.header.labels }.toList())

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headers3.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headers3.map { it.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), columns3.map { it.header.labels }.toList())
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), columns3.map { it.header.labels }.toList())

        var prevOrder: Long? = null
        for (column in columns1) {
            if (prevOrder != null) assertTrue(prevOrder < column.order)
            prevOrder = column.order
            assertEquals(t, column.table)
        }

        prevOrder = null
        for (column in columns2) {
            if (prevOrder != null) assertTrue(prevOrder < column.order)
            prevOrder = column.order
            assertEquals(t, column.table)
        }

        prevOrder = null
        for (column in columns3) {
            if (prevOrder != null) assertTrue(prevOrder < column.order)
            prevOrder = column.order
            assertEquals(t, column.table)
        }
    }

    @Test
    fun `table indexes`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
    fun `column index relation get`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        assertEquals(UnitCell::class, (t["A"] before -100L)::class)
        assertEquals(UnitCell::class, (t["A"] atOrBefore -100L)::class)
        assertEquals(UnitCell::class, (t["A"] at -100L)::class)
        assertEquals(UnitCell::class, (t["A"] atOrAfter -100L)::class)
        assertEquals(UnitCell::class, (t["A"] after -100L)::class)

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

        assertEquals(0L, (t["A"] before 0L).index)
        assertEquals(0L, (t["A"] atOrBefore 0L).index)
        assertEquals(0L, (t["A"] at 0L).index)
        assertEquals(0L, (t["A"] atOrAfter 0L).index)
        assertEquals(0L, (t["A"] after 0L).index)

        assertEquals(100L, (t["A"] before 100).index)
        assertEquals(100L, (t["A"] atOrBefore 100).index)
        assertEquals(100L, (t["A"] at 100).index)
        assertEquals(100L, (t["A"] atOrAfter 100).index)
        assertEquals(100L, (t["A"] after 100).index)

        assertEquals("A", (t["A"] before -100L).column.header[0])
        assertEquals("A", (t["A"] atOrBefore -100L).column.header[0])
        assertEquals("A", (t["A"] at -100L).column.header[0])
        assertEquals("A", (t["A"] atOrAfter -100L).column.header[0])
        assertEquals("A", (t["A"] after -100L).column.header[0])

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

        assertEquals(StringCell::class, (t["A"] before 0L)::class)
        assertEquals(StringCell::class, (t["A"] atOrBefore 0L)::class)
        assertEquals(StringCell::class, (t["A"] at 0L)::class)
        assertEquals(StringCell::class, (t["A"] atOrAfter 0L)::class)
        assertEquals(StringCell::class, (t["A"] after 0L)::class)

        assertEquals(StringCell::class, (t["A"] before 100)::class)
        assertEquals(StringCell::class, (t["A"] atOrBefore 100)::class)
        assertEquals(StringCell::class, (t["A"] at 100)::class)
        assertEquals(StringCell::class, (t["A"] atOrAfter 100)::class)
        assertEquals(UnitCell::class, (t["A"] after 100)::class)

        assertEquals(-101L, (t["A"] before -101).index)

        assertEquals(-101L, (t["A"] atOrBefore -101L).index)
        assertEquals(-101L, (t["A"] at -101L).index)
        assertEquals(-100L, (t["A"] atOrAfter -101L).index)
        assertEquals(-100L, (t["A"] after -101L).index)

        assertEquals(-100L, (t["A"] before -100).index)
        assertEquals(-100L, (t["A"] atOrBefore -100).index)
        assertEquals(-100L, (t["A"] at -100).index)
        assertEquals(-100L, (t["A"] atOrAfter -100).index)
        assertEquals(0L, (t["A"] after -100).index)

        assertEquals(-100L, (t["A"] before -1L).index)
        assertEquals(-100L, (t["A"] atOrBefore -1L).index)
        assertEquals(-1L, (t["A"] at -1L).index)
        assertEquals(0L, (t["A"] atOrAfter -1L).index)
        assertEquals(0L, (t["A"] after -1L).index)

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

        assertEquals(0L, (t["A"] before 100L).index)
        assertEquals(100L, (t["A"] atOrBefore 100L).index)
        assertEquals(100L, (t["A"] at 100L).index)
        assertEquals(100L, (t["A"] atOrAfter 100L).index)
        assertEquals(100L, (t["A"] after 100L).index)

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

        assertEquals("A", (t["A"] before 1L).column.header[0])
        assertEquals("A", (t["A"] atOrBefore 1L).column.header[0])
        assertEquals("A", (t["A"] at 1L).column.header[0])
        assertEquals("A", (t["A"] atOrAfter 1L).column.header[0])
        assertEquals("A", (t["A"] after 1L).column.header[0])

        assertEquals("A", (t["A"] before 100).column.header[0])
        assertEquals("A", (t["A"] atOrBefore 100).column.header[0])
        assertEquals("A", (t["A"] at 100).column.header[0])
        assertEquals("A", (t["A"] atOrAfter 100).column.header[0])
        assertEquals("A", (t["A"] after 100).column.header[0])

        assertEquals("A", (t["A"] after 101L).column.header[0])
    }

    @Test
    fun `cell invoke`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A"][1L] = BigDecimal.ONE
        assertEquals(t["A"][1L].value, t["A"][2L](BigDecimal.ONE))

        assertEquals(t["A", 1L].value, t["A", 2L].value)

        t["A"][1L] = BigInteger.TWO
        assertEquals(t["A"][1L].value, t["A"][2L](BigInteger.TWO))

        assertEquals(t["A", 1L].value, t["A", 2L].value)

        t["A"][1L] = 3.0
        assertEquals(t["A"][1L].value, t["A"][2L](3.0))

        assertEquals(t["A", 1L].value, t["A", 2L].value)

        t["A"][1L] = 4L
        assertEquals(t["A"][1L].value, t["A"][2L](4L))

        assertEquals(t["A", 1L].value, t["A", 2L].value)

        t["A"][1L] = 5 as Number
        assertEquals(t["A"][1L].value, t["A"][2L](5L as Number))

        assertEquals(t["A", 1L].value, t["A", 2L].value)

        t["A"][1L] = "6"
        assertEquals(t["A"][1L].value, t["A"][2L]("6"))

        assertEquals(t["A", 1L].value, t["A", 2L].value)

        t["B", 3L] = "Cell"
        t["A"][1L] = t["B", 3L]
        assertEquals(t["A"][1L].value, (t["A"][2L](t["B", 3L]) as Cell<*>).value)

        assertEquals(t["A", 1L].value, t["A", 2L].value)

        assertEquals(Unit, t["A"][2L](Unit))

        assertNotEquals(t["A", 1L].value, t["A", 2L].value)

        t["A"][1L] = null

        assertEquals(t["A", 1L].value, t["A", 2L].value)

        for (i in 1..10) t["B", i] = i

        t["A"][1L] = sum(t["B",1]..t["B", 10])
        t["A"][2L](t["A"][1L])

        assertEquals(t["A", 1L].value, t["A", 2L].value)

        assertEquals(55L, t["A", 2L].value)

        t["B", 1] = 2

        assertEquals(55L, t["A", 2L].value)
        assertNotEquals(t["A", 1L].value, t["A", 2L].value)
    }

    @Test
    fun `equality checks`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t1["A", 1] = "A1"
        t1["A", 2] = "A2"
        t1["B", 1] = "B1"
        t1["B", 2] = "B2"

        val t2 = clone(t1)

        assertEquals(t1, Table[t1.name])
        assertTrue(t1 === Table[t1.name])
        assertNotEquals(t1, t2)
        assertNotEquals(t2, Table[t1.name])

        assertEquals(t1["A"], Table[t1.name]["A"])
        assertNotEquals(t1["A"], t1["B"])
        assertNotEquals(t1["A"], t2["A"])

        assertEquals(t1["A"].header, t2["A"].header)
        assertNotEquals(t1["A"].header, t1["B"].header)

        assertEquals(t1[1], Table[t1.name][1])
        assertNotEquals(t1[1], t1[2])
        assertNotEquals(t1[1], t2[1])
        assertEquals(t1[1].index, t2[1].index)
    }

    @Test
    fun `batching commit`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val t2 = batch(t1) {
            t1["A", 1] = "A1"
            t1["A", 2] = "A2"
            t1["B", 1] = "B1"
            t1["B", 2] = "B2"

            return@batch clone(t1)
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
            assertEquals(it1.next().value, it2.next().value)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())
    }

    @Test
    fun `nested batching`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var count = 0

        val t2 = batch(t1) {
            on(this) events {
                count += count()
            }

            batch(this) {
                assertTrue(this === this@batch)
                assertTrue(this === t1)
                assertTrue(this == this@batch)
                assertTrue(this == t1)

                t1["A", 1] = "A1"
                t1["A", 2] = "A2"
                t1["B", 1] = "B1"
                t1["B", 2] = "B2"
            }

            assertEquals(0, count)

            return@batch clone(t1)
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
            assertEquals(it1.next().value, it2.next().value)
        }

        assertFalse(it1.hasNext())
        assertFalse(it2.hasNext())
    }

    @Test
    fun `batching abort`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        var t2: Table? = null

        assertFailsWith(RuntimeException::class) {
            batch(t1) {
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
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
            batch(t1) {
                flag1.set(false)
                while(flag2.get()) Thread.sleep(1)

                assertEquals("A1", t1["A", 1].value)
                assertEquals("A2", this["A", 2].value)
                assertEquals("B1", t1["B", 1].value)
                assertEquals("B2", this["B", 2].value)

                batch(this) {
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
            batch(t1) {
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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val t2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val t2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

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
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        assertEquals(9, events.count { it.newValue.value == it.oldValue.value })
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
        assertEquals(9, events.count { it.newValue.value == it.oldValue.value })
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
        assertEquals(9, events.count { it.newValue.value == it.oldValue.value })
        assertEquals(3, events.count { it.newValue.column.header[0] == "A" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "B" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "C" })
        assertEquals(listOf(100L, 600L, 400L, 200L, 500L, 300L, 300L, 400L, 500L), t.map { valueOf<Long>(it) }.toList())
    }

    @Test
    fun `table row sort`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        assertEquals(6, events.count { it.newValue.value != it.oldValue.value })
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
        assertEquals(6, events.count { it.newValue.value != it.oldValue.value })
        assertEquals(2, events.count { it.newValue.column.header[0] == "A" })
        assertEquals(2, events.count { it.newValue.column.header[0] == "B" })
        assertEquals(2, events.count { it.newValue.column.header[0] == "C" })

        events = emptyList()

        // Double reverse
        sort(t by t[2]..t[0]) { r1, r2 -> r2["C"].compareTo(r1["C"]) }

        assertEquals(listOf("A", "B", "C"), t.headers.map { it[0] }.toList())
        assertEquals(9, events.size)
        assertEquals(9, events.count { it.newValue.column.header == it.oldValue.column.header })
        assertEquals(6, events.count { it.newValue.value != it.oldValue.value })
        assertEquals(3, events.count { it.newValue.column.header[0] == "A" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "B" })
        assertEquals(3, events.count { it.newValue.column.header[0] == "C" })

        assertEquals(listOf(300L, 500L, 400L, 200L, 300L, 500L, 100L, 400L, 600L), t.map { valueOf<Long>(it) }.toList())
    }

    @Test
    fun `table index relation infix`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", -100] = "A -100"
        t["A", 0] = "A 0"
        t["A", 100] = "A 100"

        assertEquals("Row[at 0]", (t at 0).toString())
        assertEquals("Row[at 0]", (t at 0L).toString())
        assertEquals("A 0", (t at 0)["A"].toString())
        assertEquals("A 0", (t at 0L)["A"].toString())

        assertEquals("Row[at or before 0]", (t atOrBefore 0).toString())
        assertEquals("Row[at or before 0]", (t atOrBefore 0L).toString())
        assertEquals("A 0", (t atOrBefore 0)["A"].toString())
        assertEquals("A 0", (t atOrBefore 0L)["A"].toString())

        assertEquals("Row[at or after 0]", (t atOrAfter 0).toString())
        assertEquals("Row[at or after 0]", (t atOrAfter 0L).toString())
        assertEquals("A 0", (t atOrAfter 0)["A"].toString())
        assertEquals("A 0", (t atOrAfter 0L)["A"].toString())

        assertEquals("Row[at or before -1]", (t atOrBefore -1).toString())
        assertEquals("Row[at or before -1]", (t atOrBefore -1L).toString())
        assertEquals("A -100", (t atOrBefore -1)["A"].toString())
        assertEquals("A -100", (t atOrBefore -1L)["A"].toString())

        assertEquals("Row[at or after 1]", (t atOrAfter 1).toString())
        assertEquals("Row[at or after 1]", (t atOrAfter 1L).toString())
        assertEquals("A 100", (t atOrAfter 1)["A"].toString())
        assertEquals("A 100", (t atOrAfter 1L)["A"].toString())

        assertEquals("Row[before 0]", (t before 0).toString())
        assertEquals("Row[before 0]", (t before 0L).toString())
        assertEquals("A -100", (t before 0)["A"].toString())
        assertEquals("A -100", (t before 0L)["A"].toString())

        assertEquals("Row[after 0]", (t after 0).toString())
        assertEquals("Row[after 0]", (t after 0L).toString())
        assertEquals("A 100", (t after 0)["A"].toString())
        assertEquals("A 100", (t after 0L)["A"].toString())
    }

    @Test
    fun `row index relation iterator`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = "A0"
        t["B", 1] = "B1"
        t["C", 2] = "C2"

        val rAt1 = t at 1
        val rAtOrBefore1 = t atOrBefore 1
        val rAtOrAfter1 = t atOrAfter 1
        val rBefore1 = t before 1
        val rAfter1 = t after 1

        assertEquals(listOf("B1"), rAt1.map { it.value }.toList())
        assertEquals(listOf("A0", "B1"), rAtOrBefore1.map { it.value }.toList())
        assertEquals(listOf("B1", "C2"), rAtOrAfter1.map { it.value }.toList())
        assertEquals(listOf("A0"), rBefore1.map { it.value }.toList())
        assertEquals(listOf("C2"), rAfter1.map { it.value }.toList())
    }

    @Test
    fun `row compare and sort`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val rAt0 = t at 0L
        val rAtOrBefore0 = t atOrBefore 0L
        val rAtOrAfter0 = t atOrAfter 0L
        val rBefore0 = t before 0L
        val rAfter0 = t after 0L

        val rAt1 = t at 1
        val rAtOrBefore1 = t atOrBefore 1
        val rAtOrAfter1 = t atOrAfter 1
        val rBefore1 = t before 1
        val rAfter1 = t after 1

        val rAt2 = t at 2L
        val rAtOrBefore2 = t atOrBefore 2L
        val rAtOrAfter2 = t atOrAfter 2L
        val rBefore2 = t before 2L
        val rAfter2 = t after 2L

        val unsorted = listOf(
            rAt0, rAtOrBefore0, rAtOrAfter0, rBefore0, rAfter0,
            rAt1, rAtOrBefore1, rAtOrAfter1, rBefore1, rAfter1,
            rAt2, rAtOrBefore2, rAtOrAfter2, rBefore2, rAfter2
        )

        val sorted1 = unsorted.shuffled().sorted().iterator()
        for (r in 0..2) {
            for (ir in IndexRelation.entries) {
                assertTrue(sorted1.hasNext())
                val row = sorted1.next()
                assertEquals(row.index, r.toLong())
                assertEquals(row.indexRelation, ir)
            }
        }
        assertFalse(sorted1.hasNext())

        t["A", -1] = "A0"
        t["B", 1] = "B1"
        t["C", 3] = "C2"

        val sorted2 = unsorted.shuffled().sorted().iterator()
        for (r in 0..2) {
            for (ir in IndexRelation.entries) {
                assertTrue(sorted2.hasNext())
                val row = sorted2.next()
                assertEquals(row.index, r.toLong())
                assertEquals(row.indexRelation, ir)
            }
        }
        assertFalse(sorted2.hasNext())
    }

    @Test
    fun `table set`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = "Cell"

        val values = listOf<Any>(t["A", 0], "String", 1.0, 2L, BigInteger.TEN, BigDecimal.valueOf(100), 1000 as Number, true)

        fun assign(row: Row, v: Any) {
            when (v) {
                is Cell<*> -> t["A", row] = v
                is String -> t["A", row] = v
                is Double -> t["A", row] = v
                is Long -> t["A", row] = v
                is BigInteger -> t["A", row] = v
                is BigDecimal -> t["A", row] = v
                is Number -> t["A", row] = v
                is Boolean -> t["A", row] = v
                else -> throw Exception()
            }
        }

        fun assignFunction(row: Row, v: Any) {
            when (v) {
                is Cell<*> -> t["A", row] = { this(v) }
                is String -> t["A", row] = { this(v) }
                is Double -> t["A", row] = { this(v) }
                is Long -> t["A", row] = { this(v) }
                is BigInteger -> t["A", row] = { this(v) }
                is BigDecimal -> t["A", row] = { this(v) }
                is Number -> t["A", row] = { this(v) }
                is Boolean -> t["A", row] = { this(v) }
                else -> throw Exception()
            }
        }

        var lastValue = t["A", 0].value

        for (value in values) {
            for (ir in IndexRelation.entries) {
                val row = t[ir, 0]

                if (ir == IndexRelation.AT) {
                    assign(row, value)
                    lastValue = t["A"][0].value
                } else {
                    assertFailsWith<InvalidRowException> { assign(row, value) }
                }

                // Assert no change on exception
                assertEquals(lastValue, t["A"][0].value)
            }
        }

        assertEquals(1, t["A"].count())

        for (value in values) {
            for (ir in IndexRelation.entries) {
                remove(t["A"])

                val row = t[ir, 0]

                assignFunction(row, value)

                if (value is Int)
                    assertEquals(value.toLong(), t["A"][0].value)
                else if (value is Cell<*>)
                    assertEquals(value.value, t["A"][0].value)
                else
                    assertEquals(value, t["A"][0].value)
            }
        }
    }

    @Test
    fun `column set`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = "Cell"

        val values = listOf<Any>(t["A", 0], "String", 1.0, 2L, BigInteger.TEN, BigDecimal.valueOf(100), 1000 as Number, true)

        fun assign(row: Row, v: Any) {
            when (v) {
                is Cell<*> -> t["A"][row] = v
                is String -> t["A"][row] = v
                is Double -> t["A"][row] = v
                is Long -> t["A"][row] = v
                is BigInteger -> t["A"][row] = v
                is BigDecimal -> t["A"][row] = v
                is Number -> t["A"][row] = v
                is Boolean -> t["A"][row] = v
                else -> throw Exception()
            }
        }

        fun assignFunction(row: Row, v: Any) {
            when (v) {
                is Cell<*> -> t["A"][row] = { this(v) }
                is String -> t["A"][row] = { this(v) }
                is Double -> t["A"][row] = { this(v) }
                is Long -> t["A"][row] = { this(v) }
                is BigInteger -> t["A"][row] = { this(v) }
                is BigDecimal -> t["A"][row] = { this(v) }
                is Number -> t["A"][row] = { this(v) }
                is Boolean -> t["A"][row] = { this(v) }
                else -> throw Exception()
            }
        }

        var lastValue = t["A", 0].value

        for (value in values) {
            for (ir in IndexRelation.entries) {
                val row = t[ir, 0]

                if (ir == IndexRelation.AT) {
                    assign(row, value)
                    lastValue = t["A"][0].value
                } else {
                    assertFailsWith<InvalidRowException> { assign(row, value) }
                }

                // Assert no change on exception
                assertEquals(lastValue, t["A"][0].value)
            }
        }

        assertEquals(1, t["A"].count())

        for (value in values) {
            for (ir in IndexRelation.entries) {
                remove(t["A"])

                val row = t[ir, 0]

                assignFunction(row, value)

                if (value is Int)
                    assertEquals(value.toLong(), t["A"][0].value)
                else if (value is Cell<*>)
                    assertEquals(value.value, t["A"][0].value)
                else
                    assertEquals(value, t["A"][0].value)
            }
        }
    }

    @Test
    fun `row set`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = "Cell"

        val values = listOf<Any>(t["A", 0], "String", 1.0, 2L, BigInteger.TEN, BigDecimal.valueOf(100), 1000 as Number, true)

        fun assign(row: Row, v: Any) {
            when (v) {
                is Cell<*> -> t[row]["A"] = v
                is String -> t[row]["A"] = v
                is Double -> t[row]["A"] = v
                is Long -> t[row]["A"] = v
                is BigInteger -> t[row]["A"] = v
                is BigDecimal -> t[row]["A"] = v
                is Number -> t[row]["A"] = v
                is Boolean -> t[row]["A"] = v
                else -> throw Exception()
            }
        }

        fun assignFunction(row: Row, v: Any) {
            when (v) {
                is Cell<*> -> t[row]["A"] = { this(v) }
                is String -> t[row]["A"] = { this(v) }
                is Double -> t[row]["A"] = { this(v) }
                is Long -> t[row]["A"] = { this(v) }
                is BigInteger -> t[row]["A"] = { this(v) }
                is BigDecimal -> t[row]["A"] = { this(v) }
                is Number -> t[row]["A"] = { this(v) }
                is Boolean -> t[row]["A"] = { this(v) }
                else -> throw Exception()
            }
        }

        var lastValue = t["A", 0].value

        for (value in values) {
            for (ir in IndexRelation.entries) {
                val row = t[ir, 0]

                if (ir == IndexRelation.AT) {
                    assign(row, value)
                    lastValue = t["A"][0].value
                } else {
                    assertFailsWith<InvalidRowException> { assign(row, value) }
                }

                // Assert no change on exception
                assertEquals(lastValue, t["A"][0].value)
            }
        }

        assertEquals(1, t["A"].count())

        for (value in values) {
            for (ir in IndexRelation.entries) {
                remove(t["A"])

                val row = t[ir, 0]

                assignFunction(row, value)

                if (value is Int)
                    assertEquals(value.toLong(), t["A"][0].value)
                else if (value is Cell<*>)
                    assertEquals(value.value, t["A"][0].value)
                else
                    assertEquals(value, t["A"][0].value)
            }
        }
    }

    @Test
    fun `non-columntocolumnaction move`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = "A1"
        t["B", 1] = "B1"

        move(t["A"], ColumnActionOrder.AFTER, t["B"])

        assertEquals(listOf(listOf("B"), listOf("A")), t.columns.map { it.header.labels }.toList())

        move(t["A"], ColumnActionOrder.BEFORE, t["B"])

        assertEquals(listOf(listOf("A"), listOf("B")), t.columns.map { it.header.labels }.toList())

        move(t["A"], ColumnActionOrder.TO, t["B"], "C", "D")

        assertEquals(listOf(listOf("C", "D")), t.columns.map { it.header.labels }.toList())

        move(t["C", "D"], ColumnActionOrder.TO, t["C", "D"], Header["A0", "A1"])

        assertEquals(listOf(listOf("A0", "A1")), t.columns.map { it.header.labels }.toList())
    }

    @Test
    fun `non-columntocolumnaction copy`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = "A1"
        t["B", 1] = "B1"

        copy(t["A"], ColumnActionOrder.AFTER, t["B"])

        assertEquals(listOf(listOf("B"), listOf("A")), t.columns.map { it.header.labels }.toList())

        copy(t["A"], ColumnActionOrder.BEFORE, t["B"])

        assertEquals(listOf(listOf("A"), listOf("B")), t.columns.map { it.header.labels }.toList())

        copy(t["A"], ColumnActionOrder.TO, t["B"], "C", "D")

        assertEquals(listOf(listOf("A"), listOf("C", "D")), t.columns.map { it.header.labels }.toList())

        copy(t["C", "D"], ColumnActionOrder.TO, t["C", "D"], Header["A0", "A1"])

        assertEquals(listOf(listOf("A"), listOf("A0", "A1")), t.columns.map { it.header.labels }.toList())
    }

    @Test
    fun `non-columntotableaction move`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val t2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

        t1["A", 1] = "A1"
        t2["B", 1] = "B1"

        move(t1["A"], t2)

        assertEquals(listOf(listOf("B"), listOf("A")), t2.columns.map { it.header.labels }.toList())

        move(t2["B"], t1, "C", "D")

        assertEquals(listOf(listOf("C", "D")), t1.columns.map { it.header.labels }.toList())

        move(t2["A"], t1, Header["A0", "A1"])

        assertEquals(listOf(listOf("C", "D"), listOf("A0", "A1")), t1.columns.map { it.header.labels }.toList())
    }

    @Test
    fun `non-columntotableaction copy`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val t2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

        t1["A", 1] = "A1"
        t2["B", 1] = "B1"

        copy(t1["A"], t2)

        assertEquals(listOf(listOf("B"), listOf("A")), t2.columns.map { it.header.labels }.toList())

        copy(t2["B"], t1, "C", "D")

        assertEquals(listOf(listOf("A"), listOf("C", "D")), t1.columns.map { it.header.labels }.toList())

        copy(t2["A"], t1, Header["A0", "A1"])

        assertEquals(listOf(listOf("A"), listOf("C", "D"), listOf("A0", "A1")), t1.columns.map { it.header.labels }.toList())
    }

    @Test
    fun `non-rowtorowaction move`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = "A1"
        t["A", 2] = "A2"

        move(t[1], RowActionOrder.AFTER, t[2])

        assertEquals(listOf(2L, 3L), t.indexes.toList())

        move(t[3], RowActionOrder.BEFORE, t[2])

        assertEquals(listOf(1L, 2L), t.indexes.toList())

        move(t[2], RowActionOrder.TO, t[1])

        assertEquals(listOf(1L), t.indexes.toList())
    }

    @Test
    fun `non-rowtorowaction copy`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = "A1"
        t["A", 2] = "A2"

        copy(t[1], RowActionOrder.AFTER, t[2])

        assertEquals(listOf(1L, 2L, 3L), t.indexes.toList())

        copy(t[3], RowActionOrder.BEFORE, t[2])

        assertEquals(listOf(0L, 1L, 2L, 3L), t.indexes.toList())

        copy(t[3], RowActionOrder.TO, t[0])

        assertEquals(listOf(0L, 1L, 2L, 3L), t.indexes.toList())
    }

    @Test
    fun `column rename`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = "A1"

        rename(t["A"], "B", "C")

        assertEquals(listOf(listOf("B", "C")), t.columns.map { it.header.labels }.toList())

        rename(t["B", "C"], Header["D"])

        assertEquals(listOf(listOf("D")), t.columns.map { it.header.labels }.toList())
    }

    @Test
    fun `table remove`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        assertTrue(Table.names.contains(t.name))

        remove(t)

        assertFalse(Table.names.contains(t.name))
    }

    @Test
    fun `column remove`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = "A1"

        assertTrue(t.columns.map { it.header }.contains(Header["A"]))

        remove(t["A"])

        assertFalse(t.columns.map { it.header }.contains(Header["A"]))
    }

    @Test
    fun `row remove`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = "A1"

        assertTrue(t.indexes.contains(1L))

        remove(t[1])

        assertFalse(t.indexes.contains(1L))
    }

    @Test
    fun `cell contains`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = 100
        t["A", 2] = true
        t["A", 3] = false
        t["A", 4] = "String A"
        t["A", 5] = "String B"
        t["A", 6] = "String B"

        assertTrue(100 in t["A", 1])
        assertTrue(100L in t["A", 1])
        assertFalse(200 in t["A", 1])
        assertFalse(200L in t["A", 1])

        assertTrue(true in t["A", 2])
        assertTrue(false in t["A", 3])
        assertFalse(true in t["A", 3])
        assertFalse(false in t["A", 2])

        assertTrue("String A" in t["A", 4])
        assertTrue("String B" in t["A", 5])
        assertFalse("String A" in t["A", 5])
        assertFalse("String B" in t["A", 4])

        assertTrue(Unit in t["A", 7])
        assertFalse(Unit in t["A", 6])

        assertTrue(t["A", 6] in t["A", 5])
        assertFalse(t["A", 3] in t["A", 5])
    }

    @Test
    fun `cell range contains`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = 100
        t["A", 2] = true
        t["A", 3] = false
        t["A", 4] = "String A"
        t["A", 5] = "String B"
        t["A", 6] = "String B"

        assertTrue(100 in t["A", 1]..t["A", 2])
        assertTrue(100L in t["A", 1]..t["A", 2])
        assertFalse(200 in t["A", 2]..t["A", 3])
        assertFalse(200L in t["A", 2]..t["A", 3])

        assertTrue(true in t["A", 2]..t["A", 3])
        assertTrue(false in t["A", 3]..t["A", 2])
        assertFalse(true in t["A", 4]..t["A", 5])
        assertFalse(false in t["A", 5]..t["A", 4])

        assertTrue("String A" in t["A", 4]..t["A", 5])
        assertTrue("String B" in t["A", 5]..t["A", 4])
        assertFalse("String A" in t["A", 1]..t["A", 3])
        assertFalse("String B" in t["A", 1]..t["A", 3])

        assertTrue(t["A", 6] in t["A", 5]..t["A", 6])
        assertFalse(t["A", 6] in t["A", 5]..t["A", 2])
    }

    @Test
    fun `column contains`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = 100
        t["A", 2] = true
        t["A", 3] = false
        t["A", 4] = "String A"
        t["A", 5] = "String B"
        t["A", 6] = "String B"

        assertTrue(100 in t["A"])
        assertTrue(100L in t["A"])
        assertFalse(200 in t["B"])
        assertFalse(200L in t["B"])

        assertTrue(true in t["A"])
        assertTrue(false in t["A"])
        assertFalse(true in t["B"])
        assertFalse(false in t["B"])

        assertTrue("String A" in t["A"])
        assertTrue("String B" in t["A"])
        assertFalse("String A" in t["B"])
        assertFalse("String B" in t["B"])

        assertTrue(t["A", 6] in t["A"])
        assertFalse(t["A", 3] in t["B"])
        assertFalse(null in t["B"])
    }

    @Test
    fun `column range contains`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = 100
        t["A", 2] = true
        t["A", 3] = false
        t["A", 4] = "String A"
        t["A", 5] = "String B"
        t["A", 6] = "String B"

        assertTrue(100 in t["A"]..t["A"])
        assertTrue(100L in t["A"]..t["A"])
        assertFalse(200 in t["B"]..t["A"])
        assertFalse(200L in t["B"]..t["A"])

        assertTrue(true in t["A"]..t["A"])
        assertTrue(false in t["A"]..t["A"])
        assertFalse(true in t["C"]..t["B"])
        assertFalse(false in t["C"]..t["B"])

        assertTrue("String A" in t["A"]..t["A"])
        assertTrue("String B" in t["A"]..t["A"])
        assertFalse("String A" in t["C"]..t["B"])
        assertFalse("String B" in t["C"]..t["B"])
    }

    @Test
    fun `row contains`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = 100
        t["B", 1] = true
        t["C", 1] = false
        t["D", 1] = "String A"
        t["E", 1] = "String B"
        t["F", 1] = "String B"

        assertTrue(100 in t[1])
        assertTrue(100L in t[1])
        assertFalse(200 in t[2])
        assertFalse(200L in t[2])

        assertTrue(true in t[1])
        assertTrue(false in t[1])
        assertFalse(true in t[2])
        assertFalse(false in t[2])

        assertTrue("String A" in t[1])
        assertTrue("String B" in t[1])
        assertFalse("String A" in t[2])
        assertFalse("String B" in t[2])

        assertTrue(t["F", 1] in t[1])
        assertFalse(t["C", 1] in t[2])
        assertFalse(null in t[2])
    }

    @Test
    fun `row range contains`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = 100
        t["B", 1] = true
        t["C", 1] = false
        t["D", 1] = "String A"
        t["E", 1] = "String B"
        t["F", 1] = "String B"

        assertTrue(100 in t[1]..t[1])
        assertTrue(100L in t[1]..t[1])
        assertFalse(200 in t[3]..t[2])
        assertFalse(200L in t[3]..t[2])

        assertTrue(true in t[1]..t[1])
        assertTrue(false in t[1]..t[1])
        assertFalse(true in t[3]..t[2])
        assertFalse(false in t[3]..t[2])

        assertTrue("String A" in t[0]..t[1])
        assertTrue("String B" in t[1]..t[0])
        assertFalse("String A" in t[3]..t[2])
        assertFalse("String B" in t[2]..t[3])
    }

    @Test
    fun `table contains`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = 100
        t["B", 2] = true
        t["C", 3] = false
        t["D", 4] = "String A"
        t["E", 5] = "String B"
        t["F", 6] = "String B"

        assertTrue(100 in t)
        assertTrue(100L in t)
        assertFalse(200 in t)
        assertFalse(200L in t)

        assertTrue(true in t)
        assertTrue(false in t)

        assertTrue("String A" in t)
        assertTrue("String B" in t)
        assertFalse("String C" in t)

        assertTrue(t["A", 1] in t)
        assertFalse(t["A", 3] in t)
    }

    @Test
    fun `invoke nulls`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0](BigDecimal.ONE)
        t["A", 1](BigInteger.TWO)
        t["A", 2](3.0)
        t["A", 3](4L)
        t["A", 4](5 as Number)
        t["A", 5]("6")
        t["A", 6](true)
        t["A", 7](t["A", 7])
        t["A", 8](Unit)

        assertEquals(7, t.count())

        assertEquals(BigDecimal.ONE, t["A", 0].also { it(null as BigDecimal?) }.value)
        assertEquals(BigInteger.TWO, t["A", 1].also { it(null as BigInteger?) }.value)
        assertEquals(3.0, t["A", 2].also { it(null as Double?) }.value)
        assertEquals(4L, t["A", 3].also { it(null as Long?) }.value)
        assertEquals(5L, t["A", 4].also { it(null as Number?) }.value)
        assertEquals("6", t["A", 5].also { it(null as String?) }.value)
        assertEquals(true, t["A", 6].also { it(null as Boolean?) }.value)
        assertEquals(Unit, t["A", 7].also { it(null as Cell<*>?) }.value)
        assertEquals(Unit, t["A", 8].also { it(null as Unit?) }.value)

        assertEquals(0, t.count())
    }

    @Test
    fun `unit cell as`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        assertNull(t["Val1", 0].asLong)
        assertNull(t["Val1", 0].asDouble)
        assertNull(t["Val1", 0].asBigInteger)
        assertNull(t["Val1", 0].asBigDecimal)
        assertNull(t["Val1", 0].asBigDecimal(MathContext.DECIMAL32))
        assertNull(t["Val1", 0].asNumber)
        assertNull(t["Val1", 0].asBoolean)
        assertNull(t["Val1", 0].asString)
    }

    @Test
    fun `boolean cell as`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["Val1", 0] = true

        assertNull(t["Val1", 0].asLong)
        assertNull(t["Val1", 0].asDouble)
        assertNull(t["Val1", 0].asBigInteger)
        assertNull(t["Val1", 0].asBigDecimal)
        assertNull(t["Val1", 0].asBigDecimal(MathContext.DECIMAL32))
        assertNull(t["Val1", 0].asNumber)
        assertEquals(true, t["Val1", 0].asBoolean)
        assertNull(t["Val1", 0].asString)
    }

    @Test
    fun `string cell as`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["Val1", 0] = "string A"

        assertNull(t["Val1", 0].asLong)
        assertNull(t["Val1", 0].asDouble)
        assertNull(t["Val1", 0].asBigInteger)
        assertNull(t["Val1", 0].asBigDecimal)
        assertNull(t["Val1", 0].asBigDecimal(MathContext.DECIMAL32))
        assertNull(t["Val1", 0].asNumber)
        assertNull(t["Val1", 0].asBoolean)
        assertEquals("string A", t["Val1", 0].asString)
    }

    @Test
    fun `recreate column from old ref`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val c = t["A"]

        c[1] = "A1"

        remove(c)

        assertFalse(c in t)

        c[1] = "A1"

        assertEquals("A1", c[1].value)

        remove(c)

        assertFalse(c in t)

        var eventCount = 0

        on(t) events {
            eventCount += count()
        }

        assertEquals(0, eventCount)

        c[1] = "A1"

        assertEquals("A1", c[1].value)

        assertEquals(1, eventCount)
    }

    @Test
    fun `no-op clear on removed column`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val c = t["A"]

        c[1] = "A1"

        remove(c)

        assertFalse(c in t)

        c[1] = "A1"

        remove(c)

        assertFalse(c in t)

        c[1] = null

        assertFalse(c in t)

        c[1] = "A1"

        var eventCount = 0

        on(t) events {
            eventCount += count()
        }

        assertEquals(1, eventCount)

        remove(c)

        assertEquals(2, eventCount)

        assertFalse(c in t)

        c[1] = null

        assertEquals(2, eventCount)
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}
