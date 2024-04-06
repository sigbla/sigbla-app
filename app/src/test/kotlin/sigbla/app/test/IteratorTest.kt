/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.AfterClass

class IteratorTest {
    @Test
    fun `table cell iterator`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        assertFalse(t1.iterator().hasNext())

        t1["A", 1] = "A1"

        val itr1 = t1.iterator()

        t1["B", 1] = "B1"

        assertTrue(itr1.hasNext())
        assertEquals(listOf("A1"), itr1.asSequence().mapNotNull { it.asString }.toList())

        assertTrue(t1.iterator().hasNext())
        assertEquals(listOf("A1", "B1"), t1.iterator().asSequence().mapNotNull { it.asString }.toList())

        remove(t1["A"])

        assertTrue(t1.iterator().hasNext())
        assertEquals(listOf("B1"), t1.iterator().asSequence().mapNotNull { it.asString }.toList())

        remove(t1["B"])

        assertFalse(t1.iterator().hasNext())

        t1["A", 1] = "A1"

        assertTrue(t1.iterator().hasNext())
        assertEquals(listOf("A1"), t1.iterator().asSequence().mapNotNull { it.asString }.toList())
    }

    @Test
    fun `column cell iterator`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val c1 = t1["A"]

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"

        val itr1 = c1.iterator()

        t1["B", 1] = "B1"

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())

        remove(t1["A"])

        assertTrue(itr1.hasNext())
        assertEquals(listOf("A1"), itr1.asSequence().mapNotNull { it.asString }.toList())

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())
    }

    @Test
    fun `row cell iterator`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val r1 = t1[1]

        assertFalse(r1.iterator().hasNext())

        t1["A", 1] = "A1"

        val itr1 = r1.iterator()

        t1["B", 1] = "B1"

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf("A1", "B1"), r1.iterator().asSequence().mapNotNull { it.asString }.toList())

        remove(t1["A"])

        assertTrue(itr1.hasNext())
        assertEquals(listOf("A1"), itr1.asSequence().mapNotNull { it.asString }.toList())

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf("B1"), r1.iterator().asSequence().mapNotNull { it.asString }.toList())

        remove(t1["B"])

        assertFalse(r1.iterator().hasNext())

        t1["A", 1] = "A1"

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf("A1"), r1.iterator().asSequence().mapNotNull { it.asString }.toList())
    }

    @Test
    fun `cellrange cell iterator`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val c1 = t1["A", 1]..t1["B", 1]

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"
        t1["B", 1] = "B1"

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A1", "B1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())

        remove(t1["A"])

        assertFalse(c1.iterator().hasNext())

        remove(t1["B"])

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"

        assertFalse(c1.iterator().hasNext())

        t1["B", 1] = "B1"

        val itr1 = c1.iterator()

        t1["AB", 1] = "AB1"
        move(t1["AB"] before t1["B"])

        assertTrue(itr1.hasNext())
        assertEquals(listOf("A1", "B1"), itr1.asSequence().mapNotNull { it.asString }.toList())

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A1", "AB1", "B1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())
    }

    @Test
    fun `cell cell iterator`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val c1 = t1["A", 1]

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"
        t1["B", 1] = "B1"

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())

        remove(t1["A"])

        assertFalse(c1.iterator().hasNext())

        remove(t1["B"])

        assertFalse(c1.iterator().hasNext())

        t1["B", 1] = "B1"

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())
    }

    @Test
    fun `cells cell iterator`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val c1 = t1["A", 1] or t1["B", 1]

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"

        val itr1 = c1.iterator()

        t1["B", 1] = "B1"

        assertTrue(itr1.hasNext())
        assertEquals(listOf("A1"), itr1.asSequence().mapNotNull { it.asString }.toList())

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A1", "B1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())

        remove(t1["A"])

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("B1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())

        remove(t1["B"])

        assertFalse(c1.iterator().hasNext())

        t1["B", 1] = "B1"

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("B1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())

        t1["A", 1] = "A1"

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A1", "B1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())
    }

    @Test
    fun `cells all sources iterator`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val c1 = t1["A", 1] or t1["A"] or t1[1] or t1["A", 1]..t1["B", 1] or t1 or listOf(t1["A", 1], t1["B", 1])

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"

        val itr1 = c1.iterator()

        t1["B", 1] = "B1"

        assertTrue(itr1.hasNext())
        assertEquals(listOf("A1", "A1", "A1", "A1", "A1", "A1"), itr1.asSequence().mapNotNull { it.asString }.toList())

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A1", "A1", "A1", "B1", "A1", "B1", "A1", "B1", "A1", "B1"), c1.iterator().asSequence().mapNotNull { it.asString }.toList())
    }

    @Test
    fun `columnrange cell iterator`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val c1 = t1["A"]..t1["B"]

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"
        t1["B", 1] = "B1"

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A", "B"), c1.iterator().asSequence().mapNotNull { it.header[0] }.toList())

        remove(t1["A"])

        assertFalse(c1.iterator().hasNext())

        remove(t1["B"])

        assertFalse(c1.iterator().hasNext())

        t1["A", 1] = "A1"

        assertFalse(c1.iterator().hasNext())

        t1["B", 1] = "B1"

        val itr1 = c1.iterator()

        t1["AB", 1] = "AB1"
        move(t1["AB"] before t1["B"])

        assertTrue(itr1.hasNext())
        assertEquals(listOf("A", "B"), itr1.asSequence().mapNotNull { it.header[0] }.toList())

        assertTrue(c1.iterator().hasNext())
        assertEquals(listOf("A", "AB", "B"), c1.iterator().asSequence().mapNotNull { it.header[0] }.toList())
    }

    @Test
    fun `rowrange cell iterator`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val r1 = t1[1]..t1[2]

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf(1L, 2L), r1.iterator().asSequence().map { it.index }.toList())

        t1["A", 1] = "A1"
        t1["B", 2] = "B2"

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf(1L, 2L), r1.iterator().asSequence().map { it.index }.toList())

        remove(t1["A"])

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf(1L, 2L), r1.iterator().asSequence().map { it.index }.toList())

        remove(t1["B"])

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf(1L, 2L), r1.iterator().asSequence().map { it.index }.toList())

        t1["A", 1] = "A1"

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf(1L, 2L), r1.iterator().asSequence().map { it.index }.toList())

        t1["AB", 1] = "AB1"

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf(1L, 2L), r1.iterator().asSequence().map { it.index }.toList())

        t1["B", 2] = "B2"

        assertTrue(r1.iterator().hasNext())
        assertEquals(listOf(1L, 2L), r1.iterator().asSequence().map { it.index }.toList())
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}
