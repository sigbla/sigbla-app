package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.After

class TableColumnCopyMove {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `move columns within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "First"
        t["B", 0] = "Middle"
        t["C", 0] = "Last"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t[0].map { valueOf<Any>(it) })

        // Move to in between
        move(t["A"] after t["B"])

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Middle", "First", "Last"), t[0].map { valueOf<Any>(it) })

        // Move to last
        move(t["B"] after t["C"])

        assertEquals(listOf(listOf("A"), listOf("C"), listOf("B")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Last", "Middle"), t[0].map { valueOf<Any>(it) })

        // Move to first
        move(t["C"] before t["A"], "C2")

        assertEquals(listOf(listOf("C2"), listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Last", "First", "Middle"), t[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `replace columns within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A0", 0] = "First"
        t["B", 0] = "Middle 1"
        t["C", 0] = "Middle 2"
        t["D", 0] = "Last"

        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), t[0].map { valueOf<Any>(it) })

        // No-op
        move(t["A0"] to t["A0"])

        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), t[0].map { valueOf<Any>(it) })

        // Rename
        move(t["A0"] to t["A0"], "A")

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), t[0].map { valueOf<Any>(it) })

        // Move to in between
        move(t["A"] to t["C"])

        assertEquals(listOf(listOf("B"), listOf("C"), listOf("D")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Middle 1", "First", "Last"), t[0].map { valueOf<Any>(it) })

        // Move to last
        move(t["B"] to t["D"], "D2")

        assertEquals(listOf(listOf("C"), listOf("D2")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle 1"), t[0].map { valueOf<Any>(it) })

        // Move to first
        t["E", 0] = "New First"
        move(t["E"] to t["C"], "A")

        assertEquals(listOf(listOf("A"), listOf("D2")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("New First", "Middle 1"), t[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `move columns between tables with columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First"
        t1["B", 0] = "Middle"
        t1["C", 0] = "Last"

        t2["T2", 0] = "T2 cell"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        // Move to after T2
        move(t1["A"] after t2["T2"])

        assertEquals(listOf(listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("T2 cell", "First"), t2[0].map { valueOf<Any>(it) })

        // Move to before T2
        move(t1["B"] before t2["T2"])

        assertEquals(listOf(listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B"), listOf("T2"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle", "T2 cell", "First"), t2[0].map { valueOf<Any>(it) })

        // Move to in between
        move(t1["C"] after t2["B"], "C2")

        assertEquals(emptyList<List<String>>(), headerOf(t1).toList().map { it.header })
        assertEquals(emptyList<List<String>>(), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B"), listOf("C2"), listOf("T2"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle", "Last", "T2 cell", "First"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `replace columns between tables with columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First 1"
        t1["B", 0] = "Middle 1"
        t1["C", 0] = "Last 1"

        t2["A", 0] = "First 2"
        t2["B", 0] = "Middle 2"
        t2["C", 0] = "Last 2"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("First 2", "Middle 2", "Last 2"), t2[0].map { valueOf<Any>(it) })

        // Move T1["A"] to T2
        move(t1["A"] to t2["A"])

        assertEquals(listOf(listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 2", "Last 2"), t2[0].map { valueOf<Any>(it) })

        // Move T1["C"] to T2 with new name
        move(t1["C"] to t2["C"], "C2")

        assertEquals(listOf(listOf("B")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("Middle 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 2", "Last 1"), t2[0].map { valueOf<Any>(it) })

        // Move T1["B"] to T2 with same name
        move(t1["B"] to t2["B"], "B")

        assertEquals(emptyList<List<String>>(), headerOf(t1).toList().map { it.header })
        assertEquals(emptyList<List<String>>(), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `move columns to end of same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "First"
        t["B", 0] = "Middle"
        t["C", 0] = "Last"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t[0].map { valueOf<Any>(it) })

        // Move first
        move(t["A"] to t)

        assertEquals(listOf(listOf("B"), listOf("C"), listOf("A")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Middle", "Last", "First"), t[0].map { valueOf<Any>(it) })

        // Move last
        move(t["C"] to t)

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Middle", "First", "Last"), t[0].map { valueOf<Any>(it) })

        // Move middle
        move(t["B"] to t, "B2")

        assertEquals(listOf(listOf("A"), listOf("C"), listOf("B2")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Last", "Middle"), t[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `move columns to end of different table with columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First"
        t1["B", 0] = "Middle"
        t1["C", 0] = "Last"

        t2["T2", 0] = "T2 cell"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        // Move middle to T2
        move(t1["B"] to t2)

        assertEquals(listOf(listOf("A"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("B")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("T2 cell", "Middle"), t2[0].map { valueOf<Any>(it) })

        // Move first to T2
        move(t1["A"] to t2)

        assertEquals(listOf(listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("B"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("T2 cell", "Middle", "First"), t2[0].map { valueOf<Any>(it) })

        // Move last to T2
        move(t1["C"] to t2)

        assertEquals(emptyList<List<String>>(), headerOf(t1).toList().map { it.header })
        assertEquals(emptyList<List<String>>(), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("B"), listOf("A"), listOf("C")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("T2 cell", "Middle", "First", "Last"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `move columns to end of different table without columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First"
        t1["B", 0] = "Middle"
        t1["C", 0] = "Last"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        // Move middle to T2
        move(t1["B"] to t2)

        assertEquals(listOf(listOf("A"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle"), t2[0].map { valueOf<Any>(it) })

        // Move first to T2
        move(t1["A"] to t2)

        assertEquals(listOf(listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle", "First"), t2[0].map { valueOf<Any>(it) })

        // Move last to T2
        move(t1["C"] to t2)

        assertEquals(emptyList<List<String>>(), headerOf(t1).toList().map { it.header })
        assertEquals(emptyList<List<String>>(), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle", "First", "Last"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `copy columns within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "First"
        t["B", 0] = "Middle"
        t["C", 0] = "Last"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t[0].map { valueOf<Any>(it) })

        // Internal copy with same name is just a move
        copy(t["A"] after t["B"])

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Middle", "First", "Last"), t[0].map { valueOf<Any>(it) })

        copy(t["A"] before t["B"])

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy A to A2 after B
        copy(t["A"] after t["B"], "A2")

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("A2"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "First", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy B to B2 last
        copy(t["B"] after t["C"], "B2")

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("A2"), listOf("C"), listOf("B2")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "First", "Last", "Middle"), t[0].map { valueOf<Any>(it) })

        // Copy C to C2 first
        copy(t["C"] before t["A"], "C2")

        assertEquals(listOf(listOf("C2"), listOf("A"), listOf("B"), listOf("A2"), listOf("C"), listOf("B2")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Last", "First", "Middle", "First", "Last", "Middle"), t[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `overwrite columns within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A0", 0] = "First"
        t["B", 0] = "Middle 1"
        t["C", 0] = "Middle 2"
        t["D", 0] = "Last"

        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), t[0].map { valueOf<Any>(it) })

        // No-op
        copy(t["A0"] to t["A0"])

        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), t[0].map { valueOf<Any>(it) })

        // Rename
        copy(t["A0"] to t["A0"], "A")

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy to in between
        copy(t["A"] to t["C"])

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle 1", "First", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy to last
        copy(t["B"] to t["D"], "D2")

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D2")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle 1", "First", "Middle 1"), t[0].map { valueOf<Any>(it) })

        // Copy to C, replace first
        t["E", 0] = "New First"
        copy(t["E"] to t["C"], "A")

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("D2"), listOf("E")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Middle 1", "New First", "Middle 1", "New First"), t[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `copy columns between tables with columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First"
        t1["B", 0] = "Middle"
        t1["C", 0] = "Last"

        t2["T2", 0] = "T2 cell"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        // Copy to after T2
        copy(t1["A"] after t2["T2"])

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("T2 cell", "First"), t2[0].map { valueOf<Any>(it) })

        // Copy to before T2
        copy(t1["B"] before t2["T2"], "B2")

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B2"), listOf("T2"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle", "T2 cell", "First"), t2[0].map { valueOf<Any>(it) })

        // Copy to in between
        copy(t1["C"] after t2["B2"], "C2")

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B2"), listOf("C2"), listOf("T2"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle", "Last", "T2 cell", "First"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `overwrite columns between tables with columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First 1"
        t1["B", 0] = "Middle 1"
        t1["C", 0] = "Last 1"

        t2["A", 0] = "First 2"
        t2["B", 0] = "Middle 2"
        t2["C", 0] = "Last 2"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("First 2", "Middle 2", "Last 2"), t2[0].map { valueOf<Any>(it) })

        // Copy T1["A"] to T2
        copy(t1["A"] to t2["A"])

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 2", "Last 2"), t2[0].map { valueOf<Any>(it) })

        // Copy T1["C"] to T2 with new name
        copy(t1["C"] to t2["C"], "C2")

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 2", "Last 1"), t2[0].map { valueOf<Any>(it) })

        // Copy T1["B"] to T2 with same name
        copy(t1["B"] to t2["B"], "B")

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `copy columns to end of same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "First"
        t["B", 0] = "Middle"
        t["C", 0] = "Last"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy first
        copy(t["A"] to t)

        assertEquals(listOf(listOf("B"), listOf("C"), listOf("A")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Middle", "Last", "First"), t[0].map { valueOf<Any>(it) })

        // Copy last
        copy(t["C"] to t)

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Middle", "First", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy middle
        copy(t["B"] to t, "B2")

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C"), listOf("B2")), headerOf(t).toList().map { it.header })
        assertEquals(listOf("Middle", "First", "Last", "Middle"), t[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `copy columns to end of different table with columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First"
        t1["B", 0] = "Middle"
        t1["C", 0] = "Last"

        t2["T2", 0] = "T2 cell"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        // Copy middle to T2
        copy(t1["B"] to t2)

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("B")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("T2 cell", "Middle"), t2[0].map { valueOf<Any>(it) })

        // Copy first to T2
        copy(t1["A"] to t2)

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("B"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("T2 cell", "Middle", "First"), t2[0].map { valueOf<Any>(it) })

        // Copy last to T2
        copy(t1["C"] to t2)

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("B"), listOf("A"), listOf("C")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("T2 cell", "Middle", "First", "Last"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `copy columns to end of different table without columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First"
        t1["B", 0] = "Middle"
        t1["C", 0] = "Last"

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        // Copy middle to T2
        copy(t1["B"] to t2)

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle"), t2[0].map { valueOf<Any>(it) })

        // Copy first to T2
        copy(t1["A"] to t2)

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B"), listOf("A")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle", "First"), t2[0].map { valueOf<Any>(it) })

        // Copy last to T2
        copy(t1["C"] to t2)

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headerOf(t1).toList().map { it.header })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headerOf(t2).toList().map { it.header })
        assertEquals(listOf("Middle", "First", "Last"), t2[0].map { valueOf<Any>(it) })
    }
}