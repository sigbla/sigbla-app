package sigbla.app.test

import org.junit.After
import org.junit.Assert
import org.junit.Test
import sigbla.app.*

class TableRowCopyMove {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `replace rows within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 2] = "A2"
        t["A", 3] = "A3"

        t["B", 0] = "B0"
        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A1", "A2", "A3"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B1", "B2", "B3"), valueOf<Any>(t["B"]).toList())

        // No-op
        move(t[0] to t[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A1", "A2", "A3"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B1", "B2", "B3"), valueOf<Any>(t["B"]).toList())

        // Move to in between
        move(t[0] to t[2])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A1", "A0", "A3"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B1", "B0", "B3"), valueOf<Any>(t["B"]).toList())
        // TODO indexOf test

        // Move to last
        move(t[1] to t[4])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A3", "A1"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B3", "B1"), valueOf<Any>(t["B"]).toList())

        // Move to first
        move(t[3] to t[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A3", "A0", "A1"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B3", "B0", "B1"), valueOf<Any>(t["B"]).toList())
    }

    @Test
    fun `replace rows between tables with rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        t2["A", 0] = "A0_1"
        t2["A", 1] = "A1_1"
        t2["A", 2] = "A2_1"
        t2["A", 3] = "A3_1"

        t2["B", 0] = "B0_1"
        t2["B", 1] = "B1_1"
        t2["B", 2] = "B2_1"
        t2["B", 3] = "B3_1"

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_1", "A1_1", "A2_1", "A3_1"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_1", "B1_1", "B2_1", "B3_1"), valueOf<Any>(t2["B"]).toList())

        // Move to in between
        move(t1[0] to t2[2])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_1", "A1_1", "A0_0", "A3_1"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_1", "B1_1", "B0_0", "B3_1"), valueOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Move to last
        move(t1[1] to t2[4])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_1", "A1_1", "A0_0", "A3_1", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_1", "B1_1", "B0_0", "B3_1", "B1_0"), valueOf<Any>(t2["B"]).toList())

        // Move to first
        move(t1[3] to t2[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A2_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B2_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A3_0", "A1_1", "A0_0", "A3_1", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B3_0", "B1_1", "B0_0", "B3_1", "B1_0"), valueOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `replace rows between tables without rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        // Move to in between
        move(t1[0] to t2[2])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_0"), valueOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Move to last
        move(t1[1] to t2[4])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0"), valueOf<Any>(t2["B"]).toList())

        // Move to first
        move(t1[3] to t2[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A2_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B2_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A3_0", "A0_0", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B3_0", "B0_0", "B1_0"), valueOf<Any>(t2["B"]).toList())

        // Move last
        move(t1[2] to t2[5])

        Assert.assertEquals(emptyList<List<String>>(), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(emptyList<String>(), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(emptyList<String>(), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A3_0", "A0_0", "A1_0", "A2_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B3_0", "B0_0", "B1_0", "B2_0"), valueOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `overwrite rows within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 2] = "A2"
        t["A", 3] = "A3"

        t["B", 0] = "B0"
        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A1", "A2", "A3"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B1", "B2", "B3"), valueOf<Any>(t["B"]).toList())

        // No-op
        copy(t[0] to t[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A1", "A2", "A3"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B1", "B2", "B3"), valueOf<Any>(t["B"]).toList())

        // Copy to in between
        copy(t[0] to t[2])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A1", "A0", "A3"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B1", "B0", "B3"), valueOf<Any>(t["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t[1] to t[4])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A1", "A0", "A3", "A1"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B1", "B0", "B3", "B1"), valueOf<Any>(t["B"]).toList())

        // Copy to first
        copy(t[3] to t[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A3", "A1", "A0", "A3", "A1"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B3", "B1", "B0", "B3", "B1"), valueOf<Any>(t["B"]).toList())
    }

    @Test
    fun `overwrite rows between tables with rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        t2["A", 0] = "A0_1"
        t2["A", 1] = "A1_1"
        t2["A", 2] = "A2_1"
        t2["A", 3] = "A3_1"

        t2["B", 0] = "B0_1"
        t2["B", 1] = "B1_1"
        t2["B", 2] = "B2_1"
        t2["B", 3] = "B3_1"

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_1", "A1_1", "A2_1", "A3_1"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_1", "B1_1", "B2_1", "B3_1"), valueOf<Any>(t2["B"]).toList())

        // Copy to in between
        copy(t1[0] to t2[2])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_1", "A1_1", "A0_0", "A3_1"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_1", "B1_1", "B0_0", "B3_1"), valueOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t1[1] to t2[4])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_1", "A1_1", "A0_0", "A3_1", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_1", "B1_1", "B0_0", "B3_1", "B1_0"), valueOf<Any>(t2["B"]).toList())

        // Copy to first
        copy(t1[3] to t2[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A3_0", "A1_1", "A0_0", "A3_1", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B3_0", "B1_1", "B0_0", "B3_1", "B1_0"), valueOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `overwrite rows between tables without rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        // Copy to in between
        copy(t1[0] to t2[2])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_0"), valueOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t1[1] to t2[4])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0"), valueOf<Any>(t2["B"]).toList())

        // Copy to first
        copy(t1[3] to t2[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A3_0", "A0_0", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B3_0", "B0_0", "B1_0"), valueOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `copy rows within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 2] = "A2"
        t["A", 3] = "A3"

        t["B", 0] = "B0"
        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A1", "A2", "A3"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B1", "B2", "B3"), valueOf<Any>(t["B"]).toList())

        // Copy to after
        copy(t[0] after t[2])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A1", "A2", "A0", "A3"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B1", "B2", "B0", "B3"), valueOf<Any>(t["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t[1] after t[4])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A0", "A1", "A2", "A0", "A3", "A1"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B0", "B1", "B2", "B0", "B3", "B1"), valueOf<Any>(t["B"]).toList())

        // Copy to first
        copy(t[2] before t[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A2", "A0", "A1", "A2", "A0", "A3", "A1"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B2", "B0", "B1", "B2", "B0", "B3", "B1"), valueOf<Any>(t["B"]).toList())

        // Copy to before
        copy(t[4] before t[1])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t).toList().map { it.header })
        Assert.assertEquals(listOf("A2", "A0", "A3", "A1", "A2", "A0", "A3", "A1"), valueOf<Any>(t["A"]).toList())
        Assert.assertEquals(listOf("B2", "B0", "B3", "B1", "B2", "B0", "B3", "B1"), valueOf<Any>(t["B"]).toList())
    }

    @Test
    fun `copy rows to different table with rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        t2["A", 0] = "A0_1"
        t2["A", 1] = "A1_1"
        t2["A", 2] = "A2_1"
        t2["A", 3] = "A3_1"

        t2["B", 0] = "B0_1"
        t2["B", 1] = "B1_1"
        t2["B", 2] = "B2_1"
        t2["B", 3] = "B3_1"

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_1", "A1_1", "A2_1", "A3_1"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_1", "B1_1", "B2_1", "B3_1"), valueOf<Any>(t2["B"]).toList())

        // Copy to after
        copy(t1[0] after t2[2])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_1", "A1_1", "A2_1", "A0_0", "A3_1"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_1", "B1_1", "B2_1", "B0_0", "B3_1"), valueOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t1[1] after t2[4])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_1", "A1_1", "A2_1", "A0_0", "A3_1", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_1", "B1_1", "B2_1", "B0_0", "B3_1", "B1_0"), valueOf<Any>(t2["B"]).toList())

        // Copy to first
        copy(t1[3] before t2[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A3_0", "A0_1", "A1_1", "A2_1", "A0_0", "A3_1", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B3_0", "B0_1", "B1_1", "B2_1", "B0_0", "B3_1", "B1_0"), valueOf<Any>(t2["B"]).toList())

        // Copy to before
        copy(t1[2] before t2[1])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A3_0", "A0_1", "A2_0", "A1_1", "A2_1", "A0_0", "A3_1", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B3_0", "B0_1", "B2_0", "B1_1", "B2_1", "B0_0", "B3_1", "B1_0"), valueOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `copy rows to different table without rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf<Any>(), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf<Any>(), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf<Any>(), valueOf<Any>(t2["B"]).toList())

        // Copy to after
        copy(t1[0] after t2[2])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_0"), valueOf<Any>(t2["B"]).toList())
        Assert.assertEquals(listOf(3L), indexOf(t2["A"]).toList())
        Assert.assertEquals(listOf(3L), indexOf(t2["B"]).toList())

        // Copy to last
        copy(t1[1] after t2[4])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0"), valueOf<Any>(t2["B"]).toList())
        Assert.assertEquals(listOf(3L, 5L), indexOf(t2["A"]).toList())
        Assert.assertEquals(listOf(3L, 5L), indexOf(t2["B"]).toList())

        // Copy to first
        copy(t1[3] before t2[0])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A3_0", "A0_0", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B3_0", "B0_0", "B1_0"), valueOf<Any>(t2["B"]).toList())
        Assert.assertEquals(listOf(-1L, 3L, 5L), indexOf(t2["A"]).toList())
        Assert.assertEquals(listOf(-1L, 3L, 5L), indexOf(t2["B"]).toList())

        // Copy to before
        copy(t1[2] before t2[1])

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t1).toList().map { it.header })
        Assert.assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valueOf<Any>(t1["A"]).toList())
        Assert.assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valueOf<Any>(t1["B"]).toList())

        Assert.assertEquals(listOf(listOf("A"), listOf("B")), headerOf(t2).toList().map { it.header })
        Assert.assertEquals(listOf("A3_0", "A2_0", "A0_0", "A1_0"), valueOf<Any>(t2["A"]).toList())
        Assert.assertEquals(listOf("B3_0", "B2_0", "B0_0", "B1_0"), valueOf<Any>(t2["B"]).toList())
        Assert.assertEquals(listOf(-2L, 0L, 3L, 5L), indexOf(t2["A"]).toList())
        Assert.assertEquals(listOf(-2L, 0L, 3L, 5L), indexOf(t2["B"]).toList())
    }
}