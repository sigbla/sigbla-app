/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.junit.AfterClass
import org.junit.Assert.*
import org.junit.Test
import sigbla.app.*
import sigbla.app.exceptions.*
import java.io.File
import kotlin.test.assertFailsWith

class TableViewTest {
    @Test
    fun `registry test`() {
        val t1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}", Table[null]]
        val t2 = TableView.fromRegistry(t1.name!!)

        t1["A", 1][CellHeight] = 250

        assertEquals(t1, t2)
        assertTrue(t1 === t2)
        assertFalse(t1.closed)

        assertTrue(TableView.names.contains(t1.name!!))
        assertTrue(TableView.views.mapNotNull { it.name }.contains(t1.name!!))

        remove(t1)

        assertFalse(TableView.names.contains(t1.name!!))
        assertFalse(TableView.views.mapNotNull { it.name }.contains(t1.name!!))
        assertTrue(t1.closed)

        assertEquals(250L, t1["A", 1][CellHeight].height)

        assertFailsWith<InvalidRefException> {
            t1["A", 1][CellHeight] = 250
        }

        assertFailsWith(InvalidTableViewException::class) {
            TableView.fromRegistry(t1.name!!)
        }

        val t3 = TableView.fromRegistry(t1.name!!) {
            TableView[null]
        }

        assertTrue(TableView.names.contains(t1.name))
        assertTrue(TableView.views.contains(t3))

        t3["A", 1][CellHeight] = 250

        assertNotEquals(t1, t3)
        assertFalse(t1 === t3)
        assertEquals(t1.name, t3.name)

        TableView.remove(t3.name!!)

        assertFailsWith(InvalidTableViewException::class) {
            TableView.fromRegistry(t3.name!!)
        }

        val t4 = TableView.fromRegistry(t3.name!!) {
            TableView[t3.name!! + " extra"]
        }

        assertTrue(TableView.names.contains(t4.name))
        assertTrue(TableView.views.contains(t4))

        assertTrue(TableView.names.contains(t4.name + " extra"))
        val t5 = TableView.fromRegistry(t4.name + " extra")

        assertTrue(TableView.views.contains(t5))

        assertTrue(t4.source === t5)
    }

    @Test
    fun `clone tableview values`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                tv1[c][r][CellTransformer] = { this("$c$r A1") }
            }
        }

        val tv2 = clone(tv1, "tableViewClone2")

        assertTrue(TableView.views.contains(tv2))
        assertTrue(TableView.names.contains("tableViewClone2"))

        for (c in listOf("A", "B", "C")) {
            for (r in 1..100) {
                tv2[c][r][CellTransformer] = { this("$c$r B1") }
            }
        }

        for (c in listOf("D")) {
            for (r in 1..100) {
                tv1[c][r][CellTransformer] = { this("$c$r A2") }
            }
        }

        for (c in listOf("A", "B", "C")) {
            for (r in 1..100) {
                assertEquals("$c$r A1", valueOf<Any>(tv1[Table][c][r]))
            }
        }

        for (c in listOf("A", "B", "C")) {
            for (r in 1..100) {
                assertEquals("$c$r B1", valueOf<Any>(tv2[Table][c][r]))
            }
        }

        for (c in listOf("D")) {
            for (r in 1..100) {
                assertEquals("$c$r A2", valueOf<Any>(tv1[Table][c][r]))
            }
        }

        for (c in listOf("D")) {
            for (r in 1..100) {
                assertEquals("$c$r A1", valueOf<Any>(tv2[Table][c][r]))
            }
        }
    }

    @Test
    fun `clone is not closed`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        tv1["A", 0][CellTransformer] = { this("A0v1") }

        remove(tv1)

        assertTrue(tv1.closed)

        val tv2 = clone(tv1)

        assertTrue(tv1 === tv2.source)
        assertTrue(tv2.source?.closed == true)
        assertFalse(tv2.closed)

        tv2["A", 0][CellTransformer] = { this("A0v2") }

        assertEquals("A0v2", tv2[Table]["A", 0].value)
    }

    @Test
    fun `define host`() {
        TableView[Host] = "192.168.0.1"
        assertEquals("192.168.0.1", TableView[Host])

        TableView[Host] = "192.168.0.2"
        assertEquals("192.168.0.1", TableView[Host])
    }

    @Test
    fun `define port`() {
        TableView[Port] = 8088
        assertEquals(8088, TableView[Port])

        TableView[Port] = 9090
        assertEquals(8088, TableView[Port])
    }

    @Test
    fun `table view params`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val tv1 = TableView[t1]

        assertEquals(UnitCellHeight::class, tv1[CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv1[CellWidth]::class)
        assertEquals(CellClasses::class, tv1[CellClasses]::class)
        assertEquals(CellTopics::class, tv1[CellTopics]::class)

        assertEquals(emptySet<String>(), tv1[CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1[CellTopics].toSet())

        tv1[CellHeight] = DEFAULT_CELL_HEIGHT * 2
        tv1[CellWidth] = DEFAULT_CELL_WIDTH * 2
        tv1[CellClasses] = "a"
        tv1[CellTopics] = "b"

        assertEquals(DEFAULT_CELL_HEIGHT * 2, tv1[CellHeight].height)
        assertEquals(DEFAULT_CELL_WIDTH * 2, tv1[CellWidth].width)
        assertEquals(setOf("a"), tv1[CellClasses].toSet())
        assertEquals(setOf("b"), tv1[CellTopics].toSet())

        tv1[CellClasses] = tv1[CellClasses] + setOf("c", "d")
        tv1[CellTopics] = tv1[CellTopics] + setOf("e", "f")
        assertEquals(setOf("a", "c", "d"), tv1[CellClasses].toSet())
        assertEquals(setOf("b", "e", "f"), tv1[CellTopics].toSet())

        tv1[CellClasses] = tv1[CellClasses] - setOf("c")
        tv1[CellTopics] = tv1[CellTopics] - setOf("f")
        assertEquals(setOf("a", "d"), tv1[CellClasses].toSet())
        assertEquals(setOf("b", "e"), tv1[CellTopics].toSet())

        tv1[CellClasses] = tv1[CellClasses] - "a"
        tv1[CellTopics] = tv1[CellTopics] - "b"
        assertEquals(setOf("d"), tv1[CellClasses].toSet())
        assertEquals(setOf("e"), tv1[CellTopics].toSet())

        tv1[CellClasses] = tv1[CellClasses] + "a"
        tv1[CellTopics] = tv1[CellTopics] + "b"
        assertEquals(setOf("a", "d"), tv1[CellClasses].toSet())
        assertEquals(setOf("b", "e"), tv1[CellTopics].toSet())

        tv1[CellClasses] = (tv1[CellClasses] - "a") - "d"
        tv1[CellTopics] = (tv1[CellTopics] - "b") - "e"
        assertEquals(emptySet<String>(), tv1[CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1[CellTopics].toSet())

        tv1[CellClasses] = tv1[CellClasses] + "a"
        tv1[CellTopics] = tv1[CellTopics] + "b"
        assertEquals(setOf("a"), tv1[CellClasses].toSet())
        assertEquals(setOf("b"), tv1[CellTopics].toSet())

        tv1[CellHeight] = null
        tv1[CellWidth] = null
        tv1[CellClasses] = null
        tv1[CellTopics] = null

        assertEquals(UnitCellHeight::class, tv1[CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv1[CellWidth]::class)
        assertEquals(emptySet<String>(), tv1[CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1[CellTopics].toSet())

        tv1[CellHeight] = 100 as Number
        assertEquals(100L, tv1[CellHeight].height)
        tv1[CellHeight] = 200L as Number
        assertEquals(200L, tv1[CellHeight].height)
        assertFailsWith<InvalidCellHeightException> { tv1[CellHeight] = 300.0 }

        tv1[CellWidth] = 100 as Number
        assertEquals(100L, tv1[CellWidth].width)
        tv1[CellWidth] = 200L as Number
        assertEquals(200L, tv1[CellWidth].width)
        assertFailsWith<InvalidCellWidthException> { tv1[CellWidth] = 300.0 }
    }

    @Test
    fun `column view params`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val tv1 = TableView[t1]

        assertEquals(UnitCellWidth::class, tv1["A"][CellWidth]::class)
        assertEquals(CellClasses::class, tv1["A"][CellClasses]::class)
        assertEquals(CellTopics::class, tv1["A"][CellTopics]::class)

        assertEquals(emptySet<String>(), tv1["A"][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].toSet())

        tv1["A"][CellWidth] = DEFAULT_CELL_WIDTH * 2
        tv1["A"][CellClasses] = "a"
        tv1["A"][CellTopics] = "b"

        assertEquals(DEFAULT_CELL_WIDTH * 2, tv1["A"][CellWidth].width)
        assertEquals(setOf("a"), tv1["A"][CellClasses].toSet())
        assertEquals(setOf("b"), tv1["A"][CellTopics].toSet())

        tv1["A"][CellClasses] = tv1["A"][CellClasses] + setOf("c", "d")
        tv1["A"][CellTopics] = tv1["A"][CellTopics] + setOf("e", "f")
        assertEquals(setOf("a", "c", "d"), tv1["A"][CellClasses].toSet())
        assertEquals(setOf("b", "e", "f"), tv1["A"][CellTopics].toSet())

        tv1["A"][CellClasses] = tv1["A"][CellClasses] + tv1["A"][CellClasses]
        tv1["A"][CellTopics] = tv1["A"][CellTopics] + tv1["A"][CellTopics]
        assertEquals(setOf("a", "c", "d"), tv1["A"][CellClasses].toSet())
        assertEquals(setOf("b", "e", "f"), tv1["A"][CellTopics].toSet())

        tv1["A"][CellClasses] = tv1["A"][CellClasses] - tv1["A"][CellClasses]
        tv1["A"][CellTopics] = tv1["A"][CellTopics] - tv1["A"][CellTopics]
        assertEquals(emptySet<String>(), tv1["A"][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].toSet())

        tv1["A"][CellClasses] = listOf("a", "c", "d")
        tv1["A"][CellTopics] = listOf("b", "e", "f")

        tv1["A"][CellClasses] = tv1["A"][CellClasses] - setOf("c")
        tv1["A"][CellTopics] = tv1["A"][CellTopics] - setOf("f")
        assertEquals(setOf("a", "d"), tv1["A"][CellClasses].toSet())
        assertEquals(setOf("b", "e"), tv1["A"][CellTopics].toSet())

        tv1["A"][CellClasses] = tv1["A"][CellClasses] - "a"
        tv1["A"][CellTopics] = tv1["A"][CellTopics] - "b"
        assertEquals(setOf("d"), tv1["A"][CellClasses].toSet())
        assertEquals(setOf("e"), tv1["A"][CellTopics].toSet())

        tv1["A"][CellClasses] = tv1["A"][CellClasses] + "a"
        tv1["A"][CellTopics] = tv1["A"][CellTopics] + "b"
        assertEquals(setOf("a", "d"), tv1["A"][CellClasses].toSet())
        assertEquals(setOf("b", "e"), tv1["A"][CellTopics].toSet())

        tv1["A"][CellClasses] = (tv1["A"][CellClasses] - "a") - "d"
        tv1["A"][CellTopics] = (tv1["A"][CellTopics] - "b") - "e"
        assertEquals(emptySet<String>(), tv1["A"][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].toSet())

        tv1["A"][CellClasses] = tv1["A"][CellClasses] + "a"
        tv1["A"][CellTopics] = tv1["A"][CellTopics] + "b"
        assertEquals(setOf("a"), tv1["A"][CellClasses].toSet())
        assertEquals(setOf("b"), tv1["A"][CellTopics].toSet())

        tv1["A"][CellWidth] = null
        tv1["A"][CellClasses] = null
        tv1["A"][CellTopics] = null

        assertEquals(UnitCellWidth::class, tv1["A"][CellWidth]::class)
        assertEquals(emptySet<String>(), tv1["A"][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].toSet())

        tv1["A"][CellWidth] = 100 as Number
        assertEquals(100L, tv1["A"][CellWidth].width)
        tv1["A"][CellWidth] = 200L as Number
        assertEquals(200L, tv1["A"][CellWidth].width)
        assertFailsWith<InvalidCellWidthException> { tv1["A"][CellWidth] = 300.0 }
    }

    @Test
    fun `row view params`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val tv1 = TableView[t1]

        assertEquals(UnitCellHeight::class, tv1[1][CellHeight]::class)
        assertEquals(CellClasses::class, tv1[1][CellClasses]::class)
        assertEquals(CellTopics::class, tv1[1][CellTopics]::class)

        assertEquals(emptySet<String>(), tv1[1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1[1][CellTopics].toSet())

        tv1[1][CellHeight] = DEFAULT_CELL_HEIGHT * 2
        tv1[1][CellClasses] = "a"
        tv1[1][CellTopics] = "b"

        assertEquals(DEFAULT_CELL_HEIGHT * 2, tv1[1][CellHeight].height)
        assertEquals(setOf("a"), tv1[1][CellClasses].toSet())
        assertEquals(setOf("b"), tv1[1][CellTopics].toSet())

        tv1[1][CellClasses] = tv1[1][CellClasses] + setOf("c", "d")
        tv1[1][CellTopics] = tv1[1][CellTopics] + setOf("e", "f")
        assertEquals(setOf("a", "c", "d"), tv1[1][CellClasses].toSet())
        assertEquals(setOf("b", "e", "f"), tv1[1][CellTopics].toSet())

        tv1[1][CellClasses] = tv1[1][CellClasses] + tv1[1][CellClasses]
        tv1[1][CellTopics] = tv1[1][CellTopics] + tv1[1][CellTopics]
        assertEquals(setOf("a", "c", "d"), tv1[1][CellClasses].toSet())
        assertEquals(setOf("b", "e", "f"), tv1[1][CellTopics].toSet())

        tv1[1][CellClasses] = tv1[1][CellClasses] - tv1[1][CellClasses]
        tv1[1][CellTopics] = tv1[1][CellTopics] - tv1[1][CellTopics]
        assertEquals(emptySet<String>(), tv1[1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1[1][CellTopics].toSet())

        tv1[1][CellClasses] = listOf("a", "c", "d")
        tv1[1][CellTopics] = listOf("b", "e", "f")

        tv1[1][CellClasses] = tv1[1][CellClasses] - setOf("c")
        tv1[1][CellTopics] = tv1[1][CellTopics] - setOf("f")
        assertEquals(setOf("a", "d"), tv1[1][CellClasses].toSet())
        assertEquals(setOf("b", "e"), tv1[1][CellTopics].toSet())

        tv1[1][CellClasses] = tv1[1][CellClasses] - "a"
        tv1[1][CellTopics] = tv1[1][CellTopics] - "b"
        assertEquals(setOf("d"), tv1[1][CellClasses].toSet())
        assertEquals(setOf("e"), tv1[1][CellTopics].toSet())

        tv1[1][CellClasses] = tv1[1][CellClasses] + "a"
        tv1[1][CellTopics] = tv1[1][CellTopics] + "b"
        assertEquals(setOf("a", "d"), tv1[1][CellClasses].toSet())
        assertEquals(setOf("b", "e"), tv1[1][CellTopics].toSet())

        tv1[1][CellClasses] = (tv1[1][CellClasses] - "a") - "d"
        tv1[1][CellTopics] = (tv1[1][CellTopics] - "b") - "e"
        assertEquals(emptySet<String>(), tv1[1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1[1][CellTopics].toSet())

        tv1[1][CellClasses] = tv1[1][CellClasses] + "a"
        tv1[1][CellTopics] = tv1[1][CellTopics] + "b"
        assertEquals(setOf("a"), tv1[1][CellClasses].toSet())
        assertEquals(setOf("b"), tv1[1][CellTopics].toSet())

        tv1[1][CellHeight] = null
        tv1[1][CellClasses] = null
        tv1[1][CellTopics] = null

        assertEquals(UnitCellHeight::class, tv1[1][CellHeight]::class)
        assertEquals(emptySet<String>(), tv1[1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1[1][CellTopics].toSet())

        tv1[1][CellHeight] = 100 as Number
        assertEquals(100L, tv1[1][CellHeight].height)
        tv1[1][CellHeight] = 200L as Number
        assertEquals(200L, tv1[1][CellHeight].height)
        assertFailsWith<InvalidCellHeightException> { tv1[1][CellHeight] = 300.0 }
    }

    @Test
    fun `cell view params`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val tv1 = TableView[t1]

        assertEquals(UnitCellHeight::class, tv1["A"][1][CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv1["A"][1][CellWidth]::class)
        assertEquals(CellClasses::class, tv1["A"][1][CellClasses]::class)
        assertEquals(CellTopics::class, tv1["A"][1][CellTopics]::class)

        assertEquals(emptySet<String>(), tv1["A"][1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellHeight] = DEFAULT_CELL_HEIGHT * 2
        tv1["A"][1][CellWidth] = DEFAULT_CELL_WIDTH * 2
        tv1["A"][1][CellClasses] = "a"
        tv1["A"][1][CellTopics] = "b"

        assertEquals(DEFAULT_CELL_HEIGHT * 2, tv1["A"][1][CellHeight].height)
        assertEquals(DEFAULT_CELL_WIDTH * 2, tv1["A"][1][CellWidth].width)
        assertEquals(setOf("a"), tv1["A"][1][CellClasses].toSet())
        assertEquals(setOf("b"), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellClasses] = tv1["A"][1][CellClasses] + setOf("c", "d")
        tv1["A"][1][CellTopics] = tv1["A"][1][CellTopics] + setOf("e", "f")
        assertEquals(setOf("a", "c", "d"), tv1["A"][1][CellClasses].toSet())
        assertEquals(setOf("b", "e", "f"), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellClasses] = tv1["A"][1][CellClasses] + tv1["A"][1][CellClasses]
        tv1["A"][1][CellTopics] = tv1["A"][1][CellTopics] + tv1["A"][1][CellTopics]
        assertEquals(setOf("a", "c", "d"), tv1["A"][1][CellClasses].toSet())
        assertEquals(setOf("b", "e", "f"), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellClasses] = tv1["A"][1][CellClasses] - tv1["A"][1][CellClasses]
        tv1["A"][1][CellTopics] = tv1["A"][1][CellTopics] - tv1["A"][1][CellTopics]
        assertEquals(emptySet<String>(), tv1["A"][1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellClasses] = listOf("a", "c", "d")
        tv1["A"][1][CellTopics] = listOf("b", "e", "f")

        tv1["A"][1][CellClasses] = tv1["A"][1][CellClasses] - setOf("c")
        tv1["A"][1][CellTopics] = tv1["A"][1][CellTopics] - setOf("f")
        assertEquals(setOf("a", "d"), tv1["A"][1][CellClasses].toSet())
        assertEquals(setOf("b", "e"), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellClasses] = tv1["A"][1][CellClasses] - "a"
        tv1["A"][1][CellTopics] = tv1["A"][1][CellTopics] - "b"
        assertEquals(setOf("d"), tv1["A"][1][CellClasses].toSet())
        assertEquals(setOf("e"), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellClasses] = tv1["A"][1][CellClasses] + "a"
        tv1["A"][1][CellTopics] = tv1["A"][1][CellTopics] + "b"
        assertEquals(setOf("a", "d"), tv1["A"][1][CellClasses].toSet())
        assertEquals(setOf("b", "e"), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellClasses] = (tv1["A"][1][CellClasses] - "a") - "d"
        tv1["A"][1][CellTopics] = (tv1["A"][1][CellTopics] - "b") - "e"
        assertEquals(emptySet<String>(), tv1["A"][1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellClasses] = tv1["A"][1][CellClasses] + "a"
        tv1["A"][1][CellTopics] = tv1["A"][1][CellTopics] + "b"
        assertEquals(setOf("a"), tv1["A"][1][CellClasses].toSet())
        assertEquals(setOf("b"), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellHeight] = null
        tv1["A"][1][CellWidth] = null
        tv1["A"][1][CellClasses] = null
        tv1["A"][1][CellTopics] = null

        assertEquals(UnitCellHeight::class, tv1["A"][1][CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv1["A"][1][CellWidth]::class)
        assertEquals(emptySet<String>(), tv1["A"][1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][1][CellTopics].toSet())

        tv1["A"][1][CellHeight] = 100 as Number
        assertEquals(100L, tv1["A"][1][CellHeight].height)
        tv1["A"][1][CellHeight] = 200L as Number
        assertEquals(200L, tv1["A"][1][CellHeight].height)
        assertFailsWith<InvalidCellHeightException> { tv1["A"][1][CellHeight] = 300.0 }

        tv1["A"][1][CellWidth] = 100 as Number
        assertEquals(100L, tv1["A"][1][CellWidth].width)
        tv1["A"][1][CellWidth] = 200L as Number
        assertEquals(200L, tv1["A"][1][CellWidth].width)
        assertFailsWith<InvalidCellWidthException> { tv1["A"][1][CellWidth] = 300.0 }
    }

    @Test
    fun `derived view params`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val tv1 = TableView[t1]

        t1["A", 1] = 1
        t1["B", 1] = 2
        t1["A", 2] = 3
        t1["B", 2] = 4

        tv1[CellHeight] = 10
        tv1[CellWidth] = 20
        tv1[CellClasses] = "tv cc"
        tv1[CellTopics] = "tv ct"

        tv1["A"][CellWidth] = 30
        tv1["A"][CellClasses] = "tv_a cc"
        tv1["A"][CellTopics] = "tv_a ct"

        tv1[1][CellHeight] = 40
        tv1[1][CellClasses] = "tv_1 cc"
        tv1[1][CellTopics] = "tv_1 ct"

        assertEquals(30L, tv1["A"].derived.cellWidth)
        assertEquals(20L, tv1["B"].derived.cellWidth)

        assertEquals(setOf("tv cc", "tv_a cc"), tv1["A"].derived.cellClasses.toSet())
        assertEquals(setOf("tv cc"), tv1["B"].derived.cellClasses.toSet())

        assertEquals(setOf("tv ct", "tv_a ct"), tv1["A"].derived.cellTopics.toSet())
        assertEquals(setOf("tv ct"), tv1["B"].derived.cellTopics.toSet())

        assertEquals(40L, tv1[1].derived.cellHeight)
        assertEquals(10L, tv1[2].derived.cellHeight)

        assertEquals(setOf("tv cc", "tv_1 cc"), tv1[1].derived.cellClasses.toSet())
        assertEquals(setOf("tv cc"), tv1[2].derived.cellClasses.toSet())

        assertEquals(setOf("tv ct", "tv_1 ct"), tv1[1].derived.cellTopics.toSet())
        assertEquals(setOf("tv ct"), tv1[2].derived.cellTopics.toSet())

        assertEquals(40L, tv1["A", 1].derived.cellHeight)
        assertEquals(30L, tv1["A", 1].derived.cellWidth)
        assertEquals(10L, tv1["A", 2].derived.cellHeight)
        assertEquals(30L, tv1["A", 2].derived.cellWidth)

        assertEquals(40L, tv1["B", 1].derived.cellHeight)
        assertEquals(20L, tv1["B", 1].derived.cellWidth)
        assertEquals(10L, tv1["B", 2].derived.cellHeight)
        assertEquals(20L, tv1["B", 2].derived.cellWidth)

        assertEquals(setOf("tv cc", "tv_a cc", "tv_1 cc"), tv1["A", 1].derived.cellClasses.toSet())
        assertEquals(setOf("tv ct", "tv_a ct", "tv_1 ct"), tv1["A", 1].derived.cellTopics.toSet())
        assertEquals(setOf("tv cc", "tv_a cc"), tv1["A", 2].derived.cellClasses.toSet())
        assertEquals(setOf("tv ct", "tv_a ct"), tv1["A", 2].derived.cellTopics.toSet())

        assertEquals(setOf("tv cc", "tv_1 cc"), tv1["B", 1].derived.cellClasses.toSet())
        assertEquals(setOf("tv ct", "tv_1 ct"), tv1["B", 1].derived.cellTopics.toSet())
        assertEquals(setOf("tv cc"), tv1["B", 2].derived.cellClasses.toSet())
        assertEquals(setOf("tv ct"), tv1["B", 2].derived.cellTopics.toSet())

        assertEquals(tv1["A", 1].derived, tv1[tv1["A", 1].derived])
    }

    /*
    @Test
    fun `table view swaps`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val tv1 = TableView[t1]
        val tv2 = TableView["t2"]
        val tv3 = TableView["t3"]

        assertEquals(t1.name, tv1[Table].name)
        assertNotEquals(t1, tv1[Table])
        assertEquals(t1, tv1[Table].source)
        assertTrue(t1 === tv1[Table].source)

        assertNotEquals(t1.name, tv2[Table].name)
        tv2[Table] = t1
        assertEquals(t1.name, tv2[Table].name)
        assertNotEquals(t1, tv2[Table])
        assertEquals(t1, tv2[Table].source)
        assertTrue(t1 === tv2[Table].source)

        assertEquals(UnitCellHeight::class, tv2[CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv2[CellWidth]::class)
        assertEquals(emptySet<String>(), tv2[CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2[CellTopics].toSet())

        assertEquals(UnitCellWidth::class, tv2["A"][CellWidth]::class)
        assertEquals(emptySet<String>(), tv2["A"][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2["A"][CellTopics].toSet())

        assertEquals(UnitCellHeight::class, tv2[1][CellHeight]::class)
        assertEquals(emptySet<String>(), tv2[1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2[1][CellTopics].toSet())

        assertEquals(UnitCellHeight::class, tv2["B", 2][CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv2["B", 2][CellWidth]::class)
        assertEquals(emptySet<String>(), tv2["B", 2][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2["B", 2][CellTopics].toSet())

        tv1[CellHeight] = 100
        tv1[CellWidth] = 200
        tv1[CellClasses] = "cc1"
        tv1[CellTopics] = "ct1"

        tv1["A"][CellWidth] = 300
        tv1["A"][CellClasses] = "cc2"
        tv1["A"][CellTopics] = "ct2"

        tv1[1][CellHeight] = 400
        tv1[1][CellClasses] = "cc3"
        tv1[1][CellTopics] = "ct3"

        tv1["B", 2][CellHeight] = 500
        tv1["B", 2][CellWidth] = 600
        tv1["B", 2][CellClasses] = "cc4"
        tv1["B", 2][CellTopics] = "ct4"

        assertEquals(UnitCellHeight::class, tv2[CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv2[CellWidth]::class)
        assertEquals(emptySet<String>(), tv2[CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2[CellTopics].toSet())

        assertEquals(UnitCellWidth::class, tv2["A"][CellWidth]::class)
        assertEquals(emptySet<String>(), tv2["A"][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2["A"][CellTopics].toSet())

        assertEquals(UnitCellHeight::class, tv2[1][CellHeight]::class)
        assertEquals(emptySet<String>(), tv2[1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2[1][CellTopics].toSet())

        assertEquals(UnitCellHeight::class, tv2["B", 2][CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv2["B", 2][CellWidth]::class)
        assertEquals(emptySet<String>(), tv2["B", 2][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2["B", 2][CellTopics].toSet())

        tv2[Table] = null
        assertTrue(tv2[Table].toList().isEmpty())

        tv2[TableView] = tv1

        assertTrue(tv2[Table].toList().isEmpty())

        assertEquals(100L, tv2[CellHeight].height)
        assertEquals(200L, tv2[CellWidth].width)
        assertEquals(setOf("cc1"), tv2[CellClasses].toSet())
        assertEquals(setOf("ct1"), tv2[CellTopics].toSet())

        assertEquals(300L, tv2["A"][CellWidth].width)
        assertEquals(setOf("cc2"), tv2["A"][CellClasses].toSet())
        assertEquals(setOf("ct2"), tv2["A"][CellTopics].toSet())

        assertEquals(400L, tv2[1][CellHeight].height)
        assertEquals(setOf("cc3"), tv2[1][CellClasses].toSet())
        assertEquals(setOf("ct3"), tv2[1][CellTopics].toSet())

        assertEquals(500L, tv2["B", 2][CellHeight].height)
        assertEquals(600L, tv2["B", 2][CellWidth].width)
        assertEquals(setOf("cc4"), tv2["B", 2][CellClasses].toSet())
        assertEquals(setOf("ct4"), tv2["B", 2][CellTopics].toSet())

        tv2[TableView] = tv3

        assertEquals(UnitCellHeight::class, tv2[CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv2[CellWidth]::class)
        assertEquals(emptySet<String>(), tv2[CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2[CellTopics].toSet())

        assertEquals(UnitCellWidth::class, tv2["A"][CellWidth]::class)
        assertEquals(emptySet<String>(), tv2["A"][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2["A"][CellTopics].toSet())

        assertEquals(UnitCellHeight::class, tv2[1][CellHeight]::class)
        assertEquals(emptySet<String>(), tv2[1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2[1][CellTopics].toSet())

        assertEquals(UnitCellHeight::class, tv2["B", 2][CellHeight]::class)
        assertEquals(UnitCellWidth::class, tv2["B", 2][CellWidth]::class)
        assertEquals(emptySet<String>(), tv2["B", 2][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv2["B", 2][CellTopics].toSet())

        assertTrue(tv2[Table].toList().isEmpty())
    }
     */

    @Test
    fun `tableview invoke 1`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val tt: Table.() -> Unit = {}

        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            call.respondText(text = "Response 1")
        }

        tv1[CellHeight](100)
        tv1[CellWidth](200)
        tv1[CellClasses]("300")
        tv1[CellTopics]("400")
        tv1[Resources]("a" to handler)
        tv1[TableTransformer](tt)

        // TODO Consider impact of supporting:
        //      tv1[Table] { table }
        //      It would imply doing table1 { table2 },
        //      so would need invoke support on Table

        assertEquals(100L, tv1[CellHeight].height)
        assertEquals(200L, tv1[CellWidth].width)
        assertEquals(setOf("300"), tv1[CellClasses].classes)
        assertEquals(setOf("400"), tv1[CellTopics].topics)
        assertEquals(mapOf("a" to handler), tv1[Resources].resources)
        assertEquals(tt, tv1[TableTransformer].function)

        tv1[CellClasses](setOf("500", "600"))
        tv1[CellTopics](setOf("700", "800"))

        assertEquals(100L, tv1[CellHeight].height)
        assertEquals(200L, tv1[CellWidth].width)
        assertEquals(setOf("500", "600"), tv1[CellClasses].classes)
        assertEquals(setOf("700", "800"), tv1[CellTopics].topics)
        assertEquals(mapOf("a" to handler), tv1[Resources].resources)
        assertEquals(tt, tv1[TableTransformer].function)

        tv1[CellHeight](null as Unit?)
        tv1[CellWidth](null as Unit?)
        tv1[CellClasses](null as Unit?)
        tv1[CellTopics](null as Unit?)
        tv1[Resources](null as Unit?)
        tv1[TableTransformer](null as Unit?)

        assertEquals(Unit, tv1[CellHeight].height)
        assertEquals(Unit, tv1[CellWidth].width)
        assertEquals(emptySet<String>(), tv1[CellClasses].classes)
        assertEquals(emptySet<String>(), tv1[CellTopics].topics)
        assertEquals(
            emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(),
            tv1[Resources].resources
        )
        assertEquals(Unit, tv1[TableTransformer].function)
    }

    @Test
    fun `tableview invoke 2`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val tv2 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

        val tt: Table.() -> Unit = {}

        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            call.respondText(text = "Response 1")
        }

        tv1(tv2[CellHeight])
        tv1(tv2[CellWidth])
        tv1(tv2[CellClasses])
        tv1(tv2[CellTopics])
        tv1(tv2[Resources])
        tv1(tv2[TableTransformer])
        val t1 = tv1(tv2[Table]) as Table

        assertEquals(Unit, tv1[CellHeight].height)
        assertEquals(Unit, tv1[CellWidth].width)
        assertEquals(emptySet<String>(), tv1[CellClasses].classes)
        assertEquals(emptySet<String>(), tv1[CellTopics].topics)
        assertEquals(
            emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(),
            tv1[Resources].resources
        )
        assertEquals(Unit, tv1[TableTransformer].function)
        assertEquals(t1, tv1[Table].source)

        tv2[CellHeight](100)
        tv2[CellWidth](200)
        tv2[CellClasses]("300")
        tv2[CellTopics]("400")
        tv2[Resources]("a" to handler)
        tv2[TableTransformer](tt)
        tv2[Table] = t1

        tv1(tv2[CellHeight])
        tv1(tv2[CellWidth])
        tv1(tv2[CellClasses])
        tv1(tv2[CellTopics])
        tv1(tv2[Resources])
        tv1(tv2[TableTransformer])
        val t2 = tv1(tv2[Table]) as Table

        assertEquals(100L, tv1[CellHeight].height)
        assertEquals(200L, tv1[CellWidth].width)
        assertEquals(setOf("300"), tv1[CellClasses].classes)
        assertEquals(setOf("400"), tv1[CellTopics].topics)
        assertEquals(mapOf("a" to handler), tv1[Resources].resources)
        assertEquals(tt, tv1[TableTransformer].function)
        assertEquals(t2, tv1[Table].source)

        tv1(null as Unit?)

        assertEquals(Unit, tv1[CellHeight].height)
        assertEquals(Unit, tv1[CellWidth].width)
        assertEquals(emptySet<String>(), tv1[CellClasses].classes)
        assertEquals(emptySet<String>(), tv1[CellTopics].topics)
        assertEquals(
            emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(),
            tv1[Resources].resources
        )
        assertEquals(Unit, tv1[TableTransformer].function)
        assertEquals(null, tv1[Table].source)

        tv1(tv2)

        assertEquals(100L, tv1[CellHeight].height)
        assertEquals(200L, tv1[CellWidth].width)
        assertEquals(setOf("300"), tv1[CellClasses].classes)
        assertEquals(setOf("400"), tv1[CellTopics].topics)
        assertEquals(mapOf("a" to handler), tv1[Resources].resources)
        assertEquals(tt, tv1[TableTransformer].function)

        assertEquals(t2, tv1[Table].source)
    }

    @Test
    fun `columnview invoke 1`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val ct: Column.() -> Unit = {}

        tv1["A"][CellWidth](200)
        tv1["A"][CellClasses]("300")
        tv1["A"][CellTopics]("400")
        tv1["A"][ColumnTransformer](ct)

        assertEquals(200L, tv1["A"][CellWidth].width)
        assertEquals(setOf("300"), tv1["A"][CellClasses].classes)
        assertEquals(setOf("400"), tv1["A"][CellTopics].topics)
        assertEquals(ct, tv1["A"][ColumnTransformer].function)

        tv1["A"][CellClasses](setOf("500", "600"))
        tv1["A"][CellTopics](setOf("700", "800"))

        assertEquals(200L, tv1["A"][CellWidth].width)
        assertEquals(setOf("500", "600"), tv1["A"][CellClasses].classes)
        assertEquals(setOf("700", "800"), tv1["A"][CellTopics].topics)
        assertEquals(ct, tv1["A"][ColumnTransformer].function)

        tv1["A"][CellWidth](null as Unit?)
        tv1["A"][CellClasses](null as Unit?)
        tv1["A"][CellTopics](null as Unit?)
        tv1["A"][ColumnTransformer](null as Unit?)

        assertEquals(Unit, tv1["A"][CellWidth].width)
        assertEquals(emptySet<String>(), tv1["A"][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].topics)
        assertEquals(Unit, tv1["A"][ColumnTransformer].function)
    }

    @Test
    fun `columnview invoke 2`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val ct: Column.() -> Unit = {}

        tv1["A"](tv1["B"][CellWidth])
        tv1["A"](tv1["B"][CellClasses])
        tv1["A"](tv1["B"][CellTopics])
        tv1["A"](tv1["B"][ColumnTransformer])

        assertEquals(Unit, tv1["A"][CellWidth].width)
        assertEquals(emptySet<String>(), tv1["A"][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].topics)
        assertEquals(Unit, tv1["A"][ColumnTransformer].function)

        tv1["B"][CellWidth](200)
        tv1["B"][CellClasses]("300")
        tv1["B"][CellTopics]("400")
        tv1["B"][ColumnTransformer](ct)

        tv1["A"](tv1["B"][CellWidth])
        tv1["A"](tv1["B"][CellClasses])
        tv1["A"](tv1["B"][CellTopics])
        tv1["A"](tv1["B"][ColumnTransformer])

        assertEquals(200L, tv1["A"][CellWidth].width)
        assertEquals(setOf("300"), tv1["A"][CellClasses].classes)
        assertEquals(setOf("400"), tv1["A"][CellTopics].topics)
        assertEquals(ct, tv1["A"][ColumnTransformer].function)

        tv1["A"](null as Unit?)

        assertEquals(Unit, tv1["A"][CellWidth].width)
        assertEquals(emptySet<String>(), tv1["A"][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].topics)
        assertEquals(Unit, tv1["A"][ColumnTransformer].function)

        tv1["A"](tv1["B"])

        assertEquals(200L, tv1["A"][CellWidth].width)
        assertEquals(setOf("300"), tv1["A"][CellClasses].classes)
        assertEquals(setOf("400"), tv1["A"][CellTopics].topics)
        assertEquals(ct, tv1["A"][ColumnTransformer].function)
    }

    @Test
    fun `rowview invoke 1`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val rt: Row.() -> Unit = {}

        tv1[1][CellHeight](200)
        tv1[1][CellClasses]("300")
        tv1[1][CellTopics]("400")
        tv1[1][RowTransformer](rt)

        assertEquals(200L, tv1[1][CellHeight].height)
        assertEquals(setOf("300"), tv1[1][CellClasses].classes)
        assertEquals(setOf("400"), tv1[1][CellTopics].topics)
        assertEquals(rt, tv1[1][RowTransformer].function)

        tv1[1][CellClasses](setOf("500", "600"))
        tv1[1][CellTopics](setOf("700", "800"))

        assertEquals(200L, tv1[1][CellHeight].height)
        assertEquals(setOf("500", "600"), tv1[1][CellClasses].classes)
        assertEquals(setOf("700", "800"), tv1[1][CellTopics].topics)
        assertEquals(rt, tv1[1][RowTransformer].function)

        tv1[1][CellHeight](null as Unit?)
        tv1[1][CellClasses](null as Unit?)
        tv1[1][CellTopics](null as Unit?)
        tv1[1][RowTransformer](null as Unit?)

        assertEquals(Unit, tv1[1][CellHeight].height)
        assertEquals(emptySet<String>(), tv1[1][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1[1][CellTopics].topics)
        assertEquals(Unit, tv1[1][RowTransformer].function)
    }

    @Test
    fun `rowview invoke 2`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val rt: Row.() -> Unit = {}

        tv1[1](tv1[2][CellHeight])
        tv1[1](tv1[2][CellClasses])
        tv1[1](tv1[2][CellTopics])
        tv1[1](tv1[2][RowTransformer])

        assertEquals(Unit, tv1[1][CellHeight].height)
        assertEquals(emptySet<String>(), tv1[1][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1[1][CellTopics].topics)
        assertEquals(Unit, tv1[1][RowTransformer].function)

        tv1[2][CellHeight](200)
        tv1[2][CellClasses]("300")
        tv1[2][CellTopics]("400")
        tv1[2][RowTransformer](rt)

        tv1[1](tv1[2][CellHeight])
        tv1[1](tv1[2][CellClasses])
        tv1[1](tv1[2][CellTopics])
        tv1[1](tv1[2][RowTransformer])

        assertEquals(200L, tv1[1][CellHeight].height)
        assertEquals(setOf("300"), tv1[1][CellClasses].classes)
        assertEquals(setOf("400"), tv1[1][CellTopics].topics)
        assertEquals(rt, tv1[1][RowTransformer].function)

        tv1[1](null as Unit?)

        assertEquals(Unit, tv1[1][CellHeight].height)
        assertEquals(emptySet<String>(), tv1[1][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1[1][CellTopics].topics)
        assertEquals(Unit, tv1[1][RowTransformer].function)

        tv1[1](tv1[2])

        assertEquals(200L, tv1[1][CellHeight].height)
        assertEquals(setOf("300"), tv1[1][CellClasses].classes)
        assertEquals(setOf("400"), tv1[1][CellTopics].topics)
        assertEquals(rt, tv1[1][RowTransformer].function)
    }

    @Test
    fun `cellview invoke 1`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val ct: Cell<*>.() -> Unit = {}

        tv1["A", 1][CellHeight](100)
        tv1["A", 1][CellWidth](200)
        tv1["A", 1][CellClasses]("300")
        tv1["A", 1][CellTopics]("400")
        tv1["A", 1][CellTransformer](ct)

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(setOf("300"), tv1["A", 1][CellClasses].classes)
        assertEquals(setOf("400"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        tv1["A", 1][CellClasses](setOf("500", "600"))
        tv1["A", 1][CellTopics](setOf("700", "800"))

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(setOf("500", "600"), tv1["A", 1][CellClasses].classes)
        assertEquals(setOf("700", "800"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        tv1["A", 1][CellHeight](null as Unit?)
        tv1["A", 1][CellWidth](null as Unit?)
        tv1["A", 1][CellClasses](null as Unit?)
        tv1["A", 1][CellTopics](null as Unit?)
        tv1["A", 1][CellTransformer](null as Unit?)

        assertEquals(Unit, tv1["A", 1][CellHeight].height)
        assertEquals(Unit, tv1["A", 1][CellWidth].width)
        assertEquals(emptySet<String>(), tv1["A", 1][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1["A", 1][CellTopics].topics)
        assertEquals(Unit, tv1["A", 1][CellTransformer].function)
    }

    @Test
    fun `cellview invoke 2`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val ct: Cell<*>.() -> Unit = {}

        tv1["A", 1](tv1["B", 1][CellHeight])
        tv1["A", 1](tv1["B", 1][CellWidth])
        tv1["A", 1](tv1["B", 1][CellClasses])
        tv1["A", 1](tv1["B", 1][CellTopics])
        tv1["A", 1](tv1["B", 1][CellTransformer])

        assertEquals(Unit, tv1["A", 1][CellHeight].height)
        assertEquals(Unit, tv1["A", 1][CellWidth].width)
        assertEquals(emptySet<String>(), tv1["A", 1][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1["A", 1][CellTopics].topics)
        assertEquals(Unit, tv1["A", 1][CellTransformer].function)

        tv1["B", 1][CellHeight](100)
        tv1["B", 1][CellWidth](200)
        tv1["B", 1][CellClasses]("300")
        tv1["B", 1][CellTopics]("400")
        tv1["B", 1][CellTransformer](ct)

        tv1["A", 1](tv1["B", 1][CellHeight])
        tv1["A", 1](tv1["B", 1][CellWidth])
        tv1["A", 1](tv1["B", 1][CellClasses])
        tv1["A", 1](tv1["B", 1][CellTopics])
        tv1["A", 1](tv1["B", 1][CellTransformer])

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(setOf("300"), tv1["A", 1][CellClasses].classes)
        assertEquals(setOf("400"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(setOf("300"), tv1["A", 1][CellClasses].classes)
        assertEquals(setOf("400"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        tv1["A", 1](null as Unit?)

        assertEquals(Unit, tv1["A", 1][CellHeight].height)
        assertEquals(Unit, tv1["A", 1][CellWidth].width)
        assertEquals(emptySet<String>(), tv1["A", 1][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1["A", 1][CellTopics].topics)
        assertEquals(Unit, tv1["A", 1][CellTransformer].function)

        tv1["A", 1](tv1["B", 1])

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(setOf("300"), tv1["A", 1][CellClasses].classes)
        assertEquals(setOf("400"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)
    }

    @Test
    fun `tableview resources`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        fun getHandler1(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText(text = "Response 1")
            }
        }

        fun getHandler2(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText(text = "Response 2")
            }
        }

        val resources1 = tv1[Resources]
        assertTrue(resources1.resources.isEmpty())

        val handler1 = getHandler1()
        val handler2 = getHandler2()

        tv1[Resources] = "foo/bar" to handler1

        assertTrue(resources1.resources.isEmpty())
        val resources2 = tv1[Resources]
        assertEquals(mapOf("foo/bar" to handler1), resources2.resources)
        assertNotEquals(mapOf("foo/bar" to handler2), resources2.resources)

        tv1[Resources](mapOf("fiz/buz" to handler2))
        assertEquals(mapOf("fiz/buz" to handler2), tv1[Resources].resources)

        tv1[Resources]("fiz/buz" to handler1)
        assertEquals(mapOf("fiz/buz" to handler1), tv1[Resources].resources)

        tv1[Resources](listOf("foo/bar" to handler2))
        assertEquals(mapOf("foo/bar" to handler2), tv1[Resources].resources)

        tv1[Resources](resources1)
        assertEquals(
            emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(),
            tv1[Resources].resources
        )

        tv1[Resources](resources2)
        assertEquals(mapOf("foo/bar" to handler1), tv1[Resources].resources)
    }

    @Test
    fun `tableview js and css resources`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        assertTrue(jsHandlers.isEmpty())
        assertTrue(cssHandlers.isEmpty())

        fun getHandler1(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText(text = "Response 1")
            }
        }

        tv1[Resources](tv1[Resources] + ("other/resource" to getHandler1()))

        assertTrue(jsHandlers.isEmpty())
        assertTrue(cssHandlers.isEmpty())

        tv1[Resources](tv1[Resources] + ("js/resource.js" to jsResource("/js/resource.js")))

        assertEquals(1, jsHandlers.size)
        assertTrue(cssHandlers.isEmpty())

        tv1[Resources](tv1[Resources] + ("css/resource.css" to cssResource("/css/resource.css")))

        assertEquals(1, jsHandlers.size)
        assertEquals(1, cssHandlers.size)

        tv1[Resources].apply {
            val tmpFile = File.createTempFile("tmpJsFile", ".js")
            tmpFile.deleteOnExit()
            this.javaClass.getResourceAsStream("/js/resource.js").buffered().transferTo(tmpFile.outputStream())
            this(this + ("js/resource2.js" to jsFile(tmpFile)))
        }

        assertEquals(2, jsHandlers.size)
        assertEquals(1, cssHandlers.size)

        tv1[Resources].apply {
            val tmpFile = File.createTempFile("tmpCssFile", ".css")
            tmpFile.deleteOnExit()
            this.javaClass.getResourceAsStream("/css/resource.css").buffered().transferTo(tmpFile.outputStream())
            this(this + ("css/resource2.css" to cssFile(tmpFile)))
        }

        assertEquals(2, jsHandlers.size)
        assertEquals(2, cssHandlers.size)
    }

    @Test
    fun `only valid row index relation getters and setters`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val tv1 = TableView[t1]

        for (ir in IndexRelation.entries) {
            // TableView
            if (ir == IndexRelation.AT) {
                tv1[t1[ir, 0]] = tv1[t1[ir, 0]]
                tv1[t1[ir, 0]] = { tv1[t1[ir, 0]] }
                tv1[t1[ir, 0]](tv1[t1[ir, 0]])
                assertEquals(RowView::class, tv1[t1[ir, 0]]::class)

                tv1[Header["A"], t1[ir, 0]] = tv1[Header["A"], t1[0]]
                assertEquals(tv1[Header["A"], t1[0]], tv1[Header["A"], t1[ir, 0]])
            } else {
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]] }
                val validRow = t1[IndexRelation.AT, 0]
                val rowView = tv1[validRow]
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]] = rowView }
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]] = { this(rowView) } }
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]](rowView) }
                assertFailsWith<InvalidRowException> { tv1[Header["A"], t1[ir, 0]] }
                assertFailsWith<InvalidRowException> { tv1[Header["A"], t1[ir, 0]] = tv1[Header["A"], t1[0]] }
            }

            // ColumnView
            if (ir == IndexRelation.AT) {
                tv1["A"][t1[ir, 0]] = tv1["A"][t1[ir, 0]]
                tv1["A"][t1[ir, 0]] = { tv1["A"][t1[ir, 0]] }
                tv1["A"][t1[ir, 0]](tv1["A"][t1[ir, 0]])
                assertEquals(CellView::class, tv1["A"][t1[ir, 0]]::class)
            } else {
                assertFailsWith<InvalidRowException> { tv1["A"][t1[ir, 0]] }
                val validRow = t1[IndexRelation.AT, 0]
                val cellView = tv1["A"][validRow]
                assertFailsWith<InvalidRowException> { tv1["A"][t1[ir, 0]] = cellView }
                assertFailsWith<InvalidRowException> { tv1["A"][t1[ir, 0]] = { this(cellView) } }
                assertFailsWith<InvalidRowException> { tv1["A"][t1[ir, 0]](cellView) }
            }

            // RowView
            if (ir == IndexRelation.AT) {
                tv1[t1[ir, 0]]["A"] = tv1[t1[ir, 0]]["A"]
                tv1[t1[ir, 0]]["A"] = { tv1[t1[ir, 0]]["A"] }
                assertEquals(CellView::class, tv1[t1[ir, 0]]["A"]::class)
            } else {
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]]["A"] }
                val validRow = t1[IndexRelation.AT, 0]
                val cellView = tv1[validRow]["A"]

                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]]["A"] = cellView }
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]]["A"] = { this(cellView) } }
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]]["A"](cellView) }
            }
        }
    }

    @Test
    fun `clear tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val t = Table[null]
        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            call.respondText(text = "Response 1")
        }

        val tt: Table.() -> Unit = {}

        tv1[CellClasses] = "cc-1"
        tv1[CellTopics] = "ct-1"
        tv1[CellHeight] = 1000
        tv1[CellWidth] = 2000
        tv1[TableTransformer] = tt
        tv1[Resources] = "a" to handler
        tv1[Table] = t

        var count = 0

        on(tv1, skipHistory = true) events {
            count += count()

            assertEquals(setOf("cc-1"), oldView[CellClasses].classes)
            assertEquals(setOf("ct-1"), oldView[CellTopics].topics)
            assertEquals(1000L, oldView[CellHeight].height)
            assertEquals(2000L, oldView[CellWidth].width)
            assertEquals(tt, oldView[TableTransformer].function)
            assertEquals(mapOf("a" to handler), oldView[Resources].resources)
            assertEquals(t, oldView[Table].source)

            assertEquals(emptySet<String>(), newView[CellClasses].classes)
            assertEquals(emptySet<String>(), newView[CellTopics].topics)
            assertEquals(Unit, newView[CellHeight].height)
            assertEquals(Unit, newView[CellWidth].width)
            assertEquals(Unit, newView[TableTransformer].function)
            assertEquals(
                emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(),
                newView[Resources].resources
            )
            assertEquals(null, newView[Table].source)
        }

        assertEquals(0, count)

        assertEquals(setOf("cc-1"), tv1[CellClasses].classes)
        assertEquals(setOf("ct-1"), tv1[CellTopics].topics)
        assertEquals(1000L, tv1[CellHeight].height)
        assertEquals(2000L, tv1[CellWidth].width)
        assertEquals(tt, tv1[TableTransformer].function)
        assertEquals(mapOf("a" to handler), tv1[Resources].resources)
        assertEquals(t, tv1[Table].source)

        clear(tv1)

        assertEquals(emptySet<String>(), tv1[CellClasses].classes)
        assertEquals(emptySet<String>(), tv1[CellTopics].topics)
        assertEquals(Unit, tv1[CellHeight].height)
        assertEquals(Unit, tv1[CellWidth].width)
        assertEquals(Unit, tv1[TableTransformer].function)
        assertEquals(
            emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(),
            tv1[Resources].resources
        )
        assertEquals(null, tv1[Table].source)

        assertEquals(7, count)
    }

    @Test
    fun `clear columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ct: Column.() -> Unit = {}

        tv1["A"][CellClasses] = "cc-1"
        tv1["A"][CellTopics] = "ct-1"
        tv1["A"][CellWidth] = 1000
        tv1["A"][ColumnTransformer] = ct

        var count = 0

        on(tv1["A"], skipHistory = true) events {
            count += count()

            assertEquals(setOf("cc-1"), oldView["A"][CellClasses].classes)
            assertEquals(setOf("ct-1"), oldView["A"][CellTopics].topics)
            assertEquals(1000L, oldView["A"][CellWidth].width)
            assertEquals(ct, oldView["A"][ColumnTransformer].function)

            assertEquals(emptySet<String>(), newView["A"][CellClasses].classes)
            assertEquals(emptySet<String>(), newView["A"][CellTopics].topics)
            assertEquals(Unit, newView["A"][CellWidth].width)
            assertEquals(Unit, newView["A"][ColumnTransformer].function)
        }

        assertEquals(0, count)

        assertEquals(setOf("cc-1"), tv1["A"][CellClasses].classes)
        assertEquals(setOf("ct-1"), tv1["A"][CellTopics].topics)
        assertEquals(1000L, tv1["A"][CellWidth].width)
        assertEquals(ct, tv1["A"][ColumnTransformer].function)

        clear(tv1["A"])

        assertEquals(emptySet<String>(), tv1["A"][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].topics)
        assertEquals(Unit, tv1["A"][CellWidth].width)
        assertEquals(Unit, tv1["A"][ColumnTransformer].function)

        assertEquals(4, count)
    }

    @Test
    fun `clear rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val rt: Row.() -> Unit = {}

        tv1[1][CellClasses] = "cc-1"
        tv1[1][CellTopics] = "ct-1"
        tv1[1][CellHeight] = 1000
        tv1[1][RowTransformer] = rt

        var count = 0

        on(tv1[1], skipHistory = true) events {
            count += count()

            assertEquals(setOf("cc-1"), oldView[1][CellClasses].classes)
            assertEquals(setOf("ct-1"), oldView[1][CellTopics].topics)
            assertEquals(1000L, oldView[1][CellHeight].height)
            assertEquals(rt, oldView[1][RowTransformer].function)

            assertEquals(emptySet<String>(), newView[1][CellClasses].classes)
            assertEquals(emptySet<String>(), newView[1][CellTopics].topics)
            assertEquals(Unit, newView[1][CellHeight].height)
            assertEquals(Unit, newView[1][RowTransformer].function)
        }

        assertEquals(0, count)

        assertEquals(setOf("cc-1"), tv1[1][CellClasses].classes)
        assertEquals(setOf("ct-1"), tv1[1][CellTopics].topics)
        assertEquals(1000L, tv1[1][CellHeight].height)
        assertEquals(rt, tv1[1][RowTransformer].function)

        clear(tv1[1])

        assertEquals(emptySet<String>(), tv1[1][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1[1][CellTopics].topics)
        assertEquals(Unit, tv1[1][CellHeight].height)
        assertEquals(Unit, tv1[1][RowTransformer].function)

        assertEquals(4, count)
    }

    @Test
    fun `clear cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        tv1["A", 1][CellHeight] = 1000
        tv1["A", 1][CellWidth] = 2000
        tv1["A", 1][CellClasses] = "cc-1"
        tv1["A", 1][CellTopics] = "ct-1"
        val ct: Cell<*>.() -> Unit = {}
        tv1["A", 1][CellTransformer] = ct

        var count = 0

        on(tv1["A", 1], skipHistory = true) events {
            count += count()

            assertEquals(setOf("cc-1"), oldView["A", 1][CellClasses].classes)
            assertEquals(setOf("ct-1"), oldView["A", 1][CellTopics].topics)
            assertEquals(1000L, oldView["A", 1][CellHeight].height)
            assertEquals(2000L, oldView["A", 1][CellWidth].width)
            assertEquals(ct, oldView["A", 1][CellTransformer].function)

            assertEquals(emptySet<String>(), newView["A", 1][CellClasses].classes)
            assertEquals(emptySet<String>(), newView["A", 1][CellTopics].topics)
            assertEquals(Unit, newView["A", 1][CellHeight].height)
            assertEquals(Unit, newView["A", 1][CellWidth].width)
            assertEquals(Unit, newView["A", 1][CellTransformer].function)
        }

        assertEquals(0, count)

        assertEquals(setOf("cc-1"), tv1["A", 1][CellClasses].classes)
        assertEquals(setOf("ct-1"), tv1["A", 1][CellTopics].topics)
        assertEquals(1000L, tv1["A", 1][CellHeight].height)
        assertEquals(2000L, tv1["A", 1][CellWidth].width)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        clear(tv1["A", 1])

        assertEquals(emptySet<String>(), tv1["A", 1][CellClasses].classes)
        assertEquals(emptySet<String>(), tv1["A", 1][CellTopics].topics)
        assertEquals(Unit, tv1["A", 1][CellHeight].height)
        assertEquals(Unit, tv1["A", 1][CellWidth].width)
        assertEquals(Unit, tv1["A", 1][CellTransformer].function)

        assertEquals(5, count)
    }

    @Test
    fun `tableview iterator`() {
        val t1 = Table[null]
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val it1 = tv1.iterator()
        tv1[Table] = t1

        t1["A", 0] = "A0"

        val it2 = tv1.iterator()

        assertFalse(it1.hasNext())
        assertFailsWith<NoSuchElementException> { it1.next() }

        assertTrue(it2.hasNext())
        assertEquals(1, it2.asSequence().count())
    }

    @Test
    fun `cellview iterator`() {
        val t1 = Table[null]
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val it1 = tv1["A", 0].iterator()
        tv1[Table] = t1
        val it2 = tv1["A", 0].iterator()

        t1["A", 0] = "A0"

        val it3 = tv1["A", 0].iterator()

        assertFalse(it1.hasNext())
        assertFailsWith<NoSuchElementException> { it1.next() }

        assertFalse(it2.hasNext())
        assertFailsWith<NoSuchElementException> { it2.next() }

        assertTrue(it3.hasNext())
        assertEquals(1, it3.asSequence().count())
    }

    @Test
    fun `derivedcellview iterator`() {
        val t1 = Table[null]
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val it1 = tv1["A", 0].derived.iterator()
        tv1[Table] = t1
        val it2 = tv1["A", 0].derived.iterator()

        t1["A", 0] = "A0"

        val it3 = tv1["A", 0].derived.iterator()

        assertFalse(it1.hasNext())
        assertFailsWith<NoSuchElementException> { it1.next() }

        assertFalse(it2.hasNext())
        assertFailsWith<NoSuchElementException> { it2.next() }

        assertTrue(it3.hasNext())
        assertEquals(1, it3.asSequence().count())
    }

    @Test
    fun `columnview iterator`() {
        val t1 = Table[null]
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        // This will trigger path when ref.table is null
        val it1 = tv1["A"].iterator()

        // Create CV here before assigning table avoid creating column on t1
        val cv = tv1["A"]
        tv1[Table] = t1

        // This will trigger path when tableRef.columns[header] is null
        val it2 = cv.iterator()

        t1["A", 0] = "A0"

        val it3 = tv1["A"].iterator()

        assertFalse(it1.hasNext())
        assertFailsWith<NoSuchElementException> { it1.next() }

        assertFalse(it2.hasNext())
        assertFailsWith<NoSuchElementException> { it2.next() }

        assertTrue(it3.hasNext())
        assertEquals(1, it3.asSequence().count())
    }

    @Test
    fun `derivedcolumnview iterator`() {
        val t1 = Table[null]
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        // This will trigger path when ref.table is null
        val it1 = tv1["A"].derived.iterator()

        // Create DCV here before assigning table avoid creating column on t1
        val dcv = tv1["A"].derived
        tv1[Table] = t1

        // This will trigger path when tableRef.columns[header] is null
        val it2 = dcv.iterator()

        t1["A", 0] = "A0"

        val it3 = dcv.iterator()

        assertFalse(it1.hasNext())
        assertFailsWith<NoSuchElementException> { it1.next() }

        assertFalse(it2.hasNext())
        assertFailsWith<NoSuchElementException> { it2.next() }

        assertTrue(it3.hasNext())
        assertEquals(1, it3.asSequence().count())
    }

    @Test
    fun `rowview iterator`() {
        val t1 = Table[null]
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val it1 = tv1[0].iterator()
        tv1[Table] = t1

        t1["A", 0] = "A0"

        val it2 = tv1[0].iterator()

        assertFalse(it1.hasNext())
        assertFailsWith<NoSuchElementException> { it1.next() }

        assertTrue(it2.hasNext())
        assertEquals(1, it2.asSequence().count())
    }

    @Test
    fun `derivedrowview iterator`() {
        val t1 = Table[null]
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val it1 = tv1[0].derived.iterator()
        tv1[Table] = t1

        t1["A", 0] = "A0"

        val it2 = tv1[0].derived.iterator()

        assertFalse(it1.hasNext())
        assertFailsWith<NoSuchElementException> { it1.next() }

        assertTrue(it2.hasNext())
        assertEquals(1, it2.asSequence().count())
    }

    @Test
    fun `cellheight math`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val unitHeight = tv1[CellHeight]
        assertFalse(unitHeight.isNumeric)
        assertNull(unitHeight.asLong)

        tv1[CellHeight] = 1000

        val pixelHeight1 = tv1[CellHeight]
        assertTrue(pixelHeight1.isNumeric)
        assertEquals(1000L, pixelHeight1.asLong)

        tv1[CellHeight] = 200

        val pixelHeight2 = tv1[CellHeight]

        assertEquals(1001L, pixelHeight1 + 1)
        assertEquals(999L, pixelHeight1 - 1)
        assertEquals(2000L, pixelHeight1 * 2)
        assertEquals(500L, pixelHeight1 / 2)
        assertEquals(0L, pixelHeight1 % 2)

        assertEquals(1001L, pixelHeight1 + 1L)
        assertEquals(999L, pixelHeight1 - 1L)
        assertEquals(2000L, pixelHeight1 * 2L)
        assertEquals(500L, pixelHeight1 / 2L)
        assertEquals(0L, pixelHeight1 % 2L)

        assertEquals(1200L, pixelHeight1 + pixelHeight2)
        assertEquals(800L, pixelHeight1 - pixelHeight2)
        assertEquals(200000L, pixelHeight1 * pixelHeight2)
        assertEquals(5L, pixelHeight1 / pixelHeight2)
        assertEquals(0L, pixelHeight1 % pixelHeight2)

        assertFailsWith<InvalidCellHeightException> { pixelHeight1 + unitHeight }
        assertFailsWith<InvalidCellHeightException> { pixelHeight1 - unitHeight }
        assertFailsWith<InvalidCellHeightException> { pixelHeight1 * unitHeight }
        assertFailsWith<InvalidCellHeightException> { pixelHeight1 / unitHeight }
        assertFailsWith<InvalidCellHeightException> { pixelHeight1 % unitHeight }

        assertFailsWith<InvalidCellHeightException> { unitHeight + 1 }
        assertFailsWith<InvalidCellHeightException> { unitHeight - 1 }
        assertFailsWith<InvalidCellHeightException> { unitHeight * 1 }
        assertFailsWith<InvalidCellHeightException> { unitHeight / 1 }
        assertFailsWith<InvalidCellHeightException> { unitHeight % 1 }

        assertFailsWith<InvalidCellHeightException> { unitHeight + 1L }
        assertFailsWith<InvalidCellHeightException> { unitHeight - 1L }
        assertFailsWith<InvalidCellHeightException> { unitHeight * 1L }
        assertFailsWith<InvalidCellHeightException> { unitHeight / 1L }
        assertFailsWith<InvalidCellHeightException> { unitHeight % 1L }

        assertFailsWith<InvalidCellHeightException> { unitHeight + (1 as Number) }
        assertFailsWith<InvalidCellHeightException> { unitHeight - (1 as Number) }
        assertFailsWith<InvalidCellHeightException> { unitHeight * (1 as Number) }
        assertFailsWith<InvalidCellHeightException> { unitHeight / (1 as Number) }
        assertFailsWith<InvalidCellHeightException> { unitHeight % (1 as Number) }

        assertFailsWith<InvalidCellHeightException> { unitHeight + (1L as Number) }
        assertFailsWith<InvalidCellHeightException> { unitHeight - (1L as Number) }
        assertFailsWith<InvalidCellHeightException> { unitHeight * (1L as Number) }
        assertFailsWith<InvalidCellHeightException> { unitHeight / (1L as Number) }
        assertFailsWith<InvalidCellHeightException> { unitHeight % (1L as Number) }

        assertFailsWith<InvalidCellHeightException> { pixelHeight2 + 1.0 }
        assertFailsWith<InvalidCellHeightException> { pixelHeight2 - 1.0 }
        assertFailsWith<InvalidCellHeightException> { pixelHeight2 * 1.0 }
        assertFailsWith<InvalidCellHeightException> { pixelHeight2 / 1.0 }
        assertFailsWith<InvalidCellHeightException> { pixelHeight2 % 1.0 }
    }

    @Test
    fun `cellwidth math`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val unitWidth = tv1[CellWidth]
        assertFalse(unitWidth.isNumeric)
        assertNull(unitWidth.asLong)

        tv1[CellWidth] = 1000

        val pixelWidth1 = tv1[CellWidth]
        assertTrue(pixelWidth1.isNumeric)
        assertEquals(1000L, pixelWidth1.asLong)

        tv1[CellWidth] = 200

        val pixelWidth2 = tv1[CellWidth]

        assertEquals(1001L, pixelWidth1 + 1)
        assertEquals(999L, pixelWidth1 - 1)
        assertEquals(2000L, pixelWidth1 * 2)
        assertEquals(500L, pixelWidth1 / 2)
        assertEquals(0L, pixelWidth1 % 2)

        assertEquals(1001L, pixelWidth1 + 1L)
        assertEquals(999L, pixelWidth1 - 1L)
        assertEquals(2000L, pixelWidth1 * 2L)
        assertEquals(500L, pixelWidth1 / 2L)
        assertEquals(0L, pixelWidth1 % 2L)

        assertEquals(1200L, pixelWidth1 + pixelWidth2)
        assertEquals(800L, pixelWidth1 - pixelWidth2)
        assertEquals(200000L, pixelWidth1 * pixelWidth2)
        assertEquals(5L, pixelWidth1 / pixelWidth2)
        assertEquals(0L, pixelWidth1 % pixelWidth2)

        assertFailsWith<InvalidCellWidthException> { pixelWidth1 + unitWidth }
        assertFailsWith<InvalidCellWidthException> { pixelWidth1 - unitWidth }
        assertFailsWith<InvalidCellWidthException> { pixelWidth1 * unitWidth }
        assertFailsWith<InvalidCellWidthException> { pixelWidth1 / unitWidth }
        assertFailsWith<InvalidCellWidthException> { pixelWidth1 % unitWidth }

        assertFailsWith<InvalidCellWidthException> { unitWidth + 1 }
        assertFailsWith<InvalidCellWidthException> { unitWidth - 1 }
        assertFailsWith<InvalidCellWidthException> { unitWidth * 1 }
        assertFailsWith<InvalidCellWidthException> { unitWidth / 1 }
        assertFailsWith<InvalidCellWidthException> { unitWidth % 1 }

        assertFailsWith<InvalidCellWidthException> { unitWidth + 1L }
        assertFailsWith<InvalidCellWidthException> { unitWidth - 1L }
        assertFailsWith<InvalidCellWidthException> { unitWidth * 1L }
        assertFailsWith<InvalidCellWidthException> { unitWidth / 1L }
        assertFailsWith<InvalidCellWidthException> { unitWidth % 1L }

        assertFailsWith<InvalidCellWidthException> { unitWidth + (1 as Number) }
        assertFailsWith<InvalidCellWidthException> { unitWidth - (1 as Number) }
        assertFailsWith<InvalidCellWidthException> { unitWidth * (1 as Number) }
        assertFailsWith<InvalidCellWidthException> { unitWidth / (1 as Number) }
        assertFailsWith<InvalidCellWidthException> { unitWidth % (1 as Number) }

        assertFailsWith<InvalidCellWidthException> { unitWidth + (1L as Number) }
        assertFailsWith<InvalidCellWidthException> { unitWidth - (1L as Number) }
        assertFailsWith<InvalidCellWidthException> { unitWidth * (1L as Number) }
        assertFailsWith<InvalidCellWidthException> { unitWidth / (1L as Number) }
        assertFailsWith<InvalidCellWidthException> { unitWidth % (1L as Number) }

        assertFailsWith<InvalidCellWidthException> { pixelWidth2 + 1.0 }
        assertFailsWith<InvalidCellWidthException> { pixelWidth2 - 1.0 }
        assertFailsWith<InvalidCellWidthException> { pixelWidth2 * 1.0 }
        assertFailsWith<InvalidCellWidthException> { pixelWidth2 / 1.0 }
        assertFailsWith<InvalidCellWidthException> { pixelWidth2 % 1.0 }
    }

    @Test
    fun `resources plus minus`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        fun getHandler(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText("")
            }
        }

        val unitResource = tv1[Resources]
        assertEquals(emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(), unitResource.resources)

        val h1 = getHandler()
        tv1[Resources] = unitResource + ("h1" to h1)

        val r1 = tv1[Resources]
        assertEquals(mapOf("h1" to h1), r1.resources)

        val h2 = getHandler()
        val h3 = getHandler()
        tv1[Resources] = unitResource + listOf("h2" to h2, "h3" to h3)

        val r2 = tv1[Resources]
        assertEquals(mapOf("h2" to h2, "h3" to h3), r2.resources)
        assertEquals(listOf("h2" to h2, "h3" to h3), r2.iterator().asSequence().toList())

        tv1[Resources] = r1 + r2
        val r3 = tv1[Resources]
        assertEquals(mapOf("h1" to h1, "h2" to h2, "h3" to h3), r3.resources)

        tv1[Resources] = r2 - "h2"
        val r4 = tv1[Resources]
        assertEquals(mapOf("h3" to h3), r4.resources)

        tv1[Resources] = r2 - ("h2" to getHandler())
        val r5 = tv1[Resources]
        assertEquals(mapOf("h2" to h2, "h3" to h3), r5.resources)

        tv1[Resources] = r2 - ("h2" to h2)
        val r6 = tv1[Resources]
        assertEquals(mapOf("h3" to h3), r6.resources)

        tv1[Resources] = r2 - setOf("h2" to getHandler(), "h3" to h3)
        val r7 = tv1[Resources]
        assertEquals(mapOf("h2" to h2), r7.resources)
    }

    @Test
    fun `transformed table cache`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val tv = TableView[null]

        val tt1 = tv[Table]
        val tt2 = tv[Table]

        assertTrue(tt1 === tt2)

        tv[Table] = t1
        t1["A", 0] = "A0"

        val tt3 = tv[Table]
        val tt4 = tv[Table]

        assertFalse(tt2 === tt3)
        assertTrue(tt3 === tt4)

        assertEquals(Unit, tt1["A", 0].value)
        assertEquals(Unit, tt2["A", 0].value)
        assertEquals("A0", tt3["A", 0].value)
        assertEquals("A0", tt4["A", 0].value)

        tv["A", 0][CellTransformer] = { this("A0v2") }

        val tt5 = tv[Table]
        val tt6 = tv[Table]

        assertFalse(tt4 === tt5)
        assertTrue(tt5 === tt6)

        assertEquals("A0", tt3["A", 0].value)
        assertEquals("A0", tt4["A", 0].value)

        assertEquals("A0v2", tt5["A", 0].value)
        assertEquals("A0v2", tt6["A", 0].value)

        assertEquals("A0", t1["A", 0].value)

        val t2 = Table[null]
        t2["B", 0] = "B0"

        tv[Table] = t2

        val tt7 = tv[Table]
        val tt8 = tv[Table]

        assertFalse(tt6 === tt7)
        assertTrue(tt7 === tt8)

        assertEquals("A0v2", tt8["A", 0].value)
        assertEquals("B0", tt8["B", 0].value)

        tv[Table] = null

        val tt9 = tv[Table]
        val tt10 = tv[Table]

        assertFalse(tt8 === tt9)
        assertTrue(tt9 === tt10)

        assertEquals("A0v2", tt10["A", 0].value)
        assertEquals(Unit, tt10["B", 0].value)
    }

    @Test
    fun `transformers`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val tv = TableView[t]

        tv[TableTransformer] = {
            assertFalse(this == t)
            assertTrue(this.source == t)

            this["TableTransformer", 0] = "TableTransformer 0"
        }

        tv["ColumnTransformer"][ColumnTransformer] = {
            assertFalse(this.table == t)
            assertTrue(this.table.source == t)

            this[0] = "ColumnTransformer 0"
        }

        tv[0][RowTransformer] = {
            assertFalse(this.table == t)
            assertTrue(this.table.source == t)

            this["RowTransformer"] = "RowTransformer 0"
        }

        tv["CellTransformer", 0][CellTransformer] = {
            assertFalse(this.table == t)
            assertTrue(this.table.source == t)

            this("CellTransformer 0")
        }

        assertTrue("TableTransformer 0" in tv[Table]["TableTransformer"][0])
        assertTrue("ColumnTransformer 0" in tv[Table]["ColumnTransformer"][0])
        assertTrue("RowTransformer 0" in tv[Table]["RowTransformer"][0])
        assertTrue("CellTransformer 0" in tv[Table]["CellTransformer"][0])
    }

    @Test
    fun `cell height contains`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val unitCellHeight = tv1[CellHeight].also { it(100) }
        val filledCellHeight = tv1[CellHeight]

        assertTrue(unitCellHeight in unitCellHeight)
        assertTrue(filledCellHeight in filledCellHeight)
        assertFalse(unitCellHeight in filledCellHeight)
        assertFalse(filledCellHeight in unitCellHeight)
        assertTrue(100 in filledCellHeight)
        assertTrue(100L in filledCellHeight)
        assertFalse(100 in unitCellHeight)
        assertFalse(100L in unitCellHeight)
        assertTrue(Unit in unitCellHeight)
        assertTrue(null in unitCellHeight)
        assertFalse(Unit in filledCellHeight)
        assertFalse(null in filledCellHeight)
    }

    @Test
    fun `cell width contains`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val unitCellWidth = tv1[CellWidth].also { it(100) }
        val filledCellWidth = tv1[CellWidth]

        assertTrue(unitCellWidth in unitCellWidth)
        assertTrue(filledCellWidth in filledCellWidth)
        assertFalse(unitCellWidth in filledCellWidth)
        assertFalse(filledCellWidth in unitCellWidth)
        assertTrue(100 in filledCellWidth)
        assertTrue(100L in filledCellWidth)
        assertFalse(100 in unitCellWidth)
        assertFalse(100L in unitCellWidth)
        assertTrue(Unit in unitCellWidth)
        assertTrue(null in unitCellWidth)
        assertFalse(Unit in filledCellWidth)
        assertFalse(null in filledCellWidth)
    }

    @Test
    fun `cell classes contains`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val unitCellClasses = tv1[CellClasses].also { it(listOf("A", "B")) }
        val filledCellClasses = tv1[CellClasses]

        assertTrue(unitCellClasses in unitCellClasses)
        assertTrue(filledCellClasses in filledCellClasses)
        assertTrue(unitCellClasses in filledCellClasses)
        assertFalse(filledCellClasses in unitCellClasses)
        assertTrue("A" in filledCellClasses)
        assertTrue("B" in filledCellClasses)
        assertFalse("A" in unitCellClasses)
        assertFalse("B" in unitCellClasses)
        assertTrue(emptyList<String>() in unitCellClasses)
        assertTrue(listOf("A") in filledCellClasses)
        assertTrue(listOf("B") in filledCellClasses)
        assertTrue(setOf("A", "B") in filledCellClasses)
        assertFalse(setOf("A", "B", "C") in filledCellClasses)
    }

    @Test
    fun `cell topics contains`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val unitCellTopics = tv1[CellTopics].also { it(listOf("A", "B")) }
        val filledCellTopics = tv1[CellTopics]

        assertTrue(unitCellTopics in unitCellTopics)
        assertTrue(filledCellTopics in filledCellTopics)
        assertTrue(unitCellTopics in filledCellTopics)
        assertFalse(filledCellTopics in unitCellTopics)
        assertTrue("A" in filledCellTopics)
        assertTrue("B" in filledCellTopics)
        assertFalse("A" in unitCellTopics)
        assertFalse("B" in unitCellTopics)
        assertTrue(emptyList<String>() in unitCellTopics)
        assertTrue(listOf("A") in filledCellTopics)
        assertTrue(listOf("B") in filledCellTopics)
        assertTrue(setOf("A", "B") in filledCellTopics)
        assertFalse(setOf("A", "B", "C") in filledCellTopics)
    }

    @Test
    fun `table transformer contains`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val tt: Table.() -> Unit = {}
        val unitTableTransformer = tv1[TableTransformer].also { it(tt) }
        val filledTableTransformer = tv1[TableTransformer]

        assertTrue(unitTableTransformer in unitTableTransformer)
        assertTrue(Unit in unitTableTransformer)
        assertTrue(null in unitTableTransformer)
        assertFalse(tt in unitTableTransformer)

        assertTrue(filledTableTransformer in filledTableTransformer)
        assertTrue(tt in filledTableTransformer)
        assertFalse(Unit in filledTableTransformer)
        assertFalse(null in filledTableTransformer)
    }

    @Test
    fun `column transformer contains`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ct: Column.() -> Unit = {}
        val unitColumnTransformer = tv1["A"][ColumnTransformer].also { it(ct) }
        val filledColumnTransformer = tv1["A"][ColumnTransformer]

        assertTrue(unitColumnTransformer in unitColumnTransformer)
        assertTrue(Unit in unitColumnTransformer)
        assertTrue(null in unitColumnTransformer)
        assertFalse(ct in unitColumnTransformer)

        assertTrue(filledColumnTransformer in filledColumnTransformer)
        assertTrue(ct in filledColumnTransformer)
        assertFalse(Unit in filledColumnTransformer)
        assertFalse(null in filledColumnTransformer)
    }

    @Test
    fun `row transformer contains`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val rt: Row.() -> Unit = {}
        val unitRowTransformer = tv1[1][RowTransformer].also { it(rt) }
        val filledRowTransformer = tv1[1][RowTransformer]

        assertTrue(unitRowTransformer in unitRowTransformer)
        assertTrue(Unit in unitRowTransformer)
        assertTrue(null in unitRowTransformer)
        assertFalse(rt in unitRowTransformer)

        assertTrue(filledRowTransformer in filledRowTransformer)
        assertTrue(rt in filledRowTransformer)
        assertFalse(Unit in filledRowTransformer)
        assertFalse(null in filledRowTransformer)
    }

    @Test
    fun `cell transformer contains`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ct: Cell<*>.() -> Unit = {}
        val unitCellTransformer = tv1["A", 1][CellTransformer].also { it(ct) }
        val filledCellTransformer = tv1["A", 1][CellTransformer]

        assertTrue(unitCellTransformer in unitCellTransformer)
        assertTrue(Unit in unitCellTransformer)
        assertTrue(null in unitCellTransformer)
        assertFalse(ct in unitCellTransformer)

        assertTrue(filledCellTransformer in filledCellTransformer)
        assertTrue(ct in filledCellTransformer)
        assertFalse(Unit in filledCellTransformer)
        assertFalse(null in filledCellTransformer)
    }

    @Test
    fun `resources contains`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        fun getHandler(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText("")
            }
        }

        val handler1 = getHandler()
        val handler2 = getHandler()
        val handler3 = getHandler()

        val unitResource = tv1[Resources].also { it(listOf("a" to handler1, "b" to handler2)) }
        val filledResource = tv1[Resources]

        assertTrue(unitResource in unitResource)
        assertTrue(filledResource in filledResource)

        assertTrue(emptyMap() in unitResource)
        assertTrue(emptyMap() in filledResource)
        assertFalse(mapOf("a" to handler1) in unitResource)
        assertTrue(mapOf("a" to handler1) in filledResource)
        assertTrue(mapOf("b" to handler2) in filledResource)
        assertTrue(mapOf("a" to handler1, "b" to handler2) in filledResource)
        assertFalse(mapOf("c" to handler3) in filledResource)
        assertFalse(mapOf("a" to handler1, "c" to handler3) in filledResource)

        assertTrue("a" to handler1 in filledResource)
        assertFalse("b" to handler1 in filledResource)
        assertFalse("b" to handler2 in unitResource)

        assertTrue(listOf("a" to handler1, "b" to handler2) in filledResource)
        assertFalse(listOf("a" to handler1, "b" to handler2) in unitResource)
        assertFalse(listOf("c" to handler1, "b" to handler2) in filledResource)
    }

    @Test
    fun `cell classes invoke`() {
        val cc1 = TableView[null][CellClasses].let { it(listOf("D", "E")); it.source[CellClasses] }
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (cc in listOf(tv1[CellClasses], tv1["A"][CellClasses], tv1[1][CellClasses], tv1["A", 1][CellClasses])) {
            cc(listOf("A", "B"))

            when (val source = cc.source) {
                is TableView -> assertEquals(sortedSetOf("A", "B"), source[CellClasses].classes)
                is ColumnView -> assertEquals(sortedSetOf("A", "B"), source[CellClasses].classes)
                is RowView -> assertEquals(sortedSetOf("A", "B"), source[CellClasses].classes)
                is CellView -> assertEquals(sortedSetOf("A", "B"), source[CellClasses].classes)
            }

            cc("C")

            when (val source = cc.source) {
                is TableView -> assertEquals(sortedSetOf("C"), source[CellClasses].classes)
                is ColumnView -> assertEquals(sortedSetOf("C"), source[CellClasses].classes)
                is RowView -> assertEquals(sortedSetOf("C"), source[CellClasses].classes)
                is CellView -> assertEquals(sortedSetOf("C"), source[CellClasses].classes)
            }

            cc(cc1)

            when (val source = cc.source) {
                is TableView -> assertEquals(sortedSetOf("D", "E"), source[CellClasses].classes)
                is ColumnView -> assertEquals(sortedSetOf("D", "E"), source[CellClasses].classes)
                is RowView -> assertEquals(sortedSetOf("D", "E"), source[CellClasses].classes)
                is CellView -> assertEquals(sortedSetOf("D", "E"), source[CellClasses].classes)
            }

            cc(Unit)

            when (val source = cc.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
            }
        }

        for (cc in listOf(tv1[CellClasses], tv1["A"][CellClasses], tv1[1][CellClasses], tv1["A", 1][CellClasses])) {
            when (val source = cc.source) {
                is TableView -> source[CellClasses] = cc1
                is ColumnView -> source[CellClasses] = cc1
                is RowView -> source[CellClasses] = cc1
                is CellView -> source[CellClasses] = cc1
            }

            cc(null as Collection<String>?)

            when (val source = cc.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
            }

            cc(null as String?)

            when (val source = cc.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
            }

            cc(null as CellClasses<*>?)

            when (val source = cc.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
            }

            cc(null as Unit?)

            when (val source = cc.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellClasses].classes)
            }
        }
    }

    @Test
    fun `cell topics invoke`() {
        val cc1 = TableView[null][CellTopics].let { it(listOf("D", "E")); it.source[CellTopics] }
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (ct in listOf(tv1[CellTopics], tv1["A"][CellTopics], tv1[1][CellTopics], tv1["A", 1][CellTopics])) {
            ct(listOf("A", "B"))

            when (val source = ct.source) {
                is TableView -> assertEquals(sortedSetOf("A", "B"), source[CellTopics].topics)
                is ColumnView -> assertEquals(sortedSetOf("A", "B"), source[CellTopics].topics)
                is RowView -> assertEquals(sortedSetOf("A", "B"), source[CellTopics].topics)
                is CellView -> assertEquals(sortedSetOf("A", "B"), source[CellTopics].topics)
            }

            ct("C")

            when (val source = ct.source) {
                is TableView -> assertEquals(sortedSetOf("C"), source[CellTopics].topics)
                is ColumnView -> assertEquals(sortedSetOf("C"), source[CellTopics].topics)
                is RowView -> assertEquals(sortedSetOf("C"), source[CellTopics].topics)
                is CellView -> assertEquals(sortedSetOf("C"), source[CellTopics].topics)
            }

            ct(cc1)

            when (val source = ct.source) {
                is TableView -> assertEquals(sortedSetOf("D", "E"), source[CellTopics].topics)
                is ColumnView -> assertEquals(sortedSetOf("D", "E"), source[CellTopics].topics)
                is RowView -> assertEquals(sortedSetOf("D", "E"), source[CellTopics].topics)
                is CellView -> assertEquals(sortedSetOf("D", "E"), source[CellTopics].topics)
            }

            ct(Unit)

            when (val source = ct.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
            }
        }

        for (ct in listOf(tv1[CellTopics], tv1["A"][CellTopics], tv1[1][CellTopics], tv1["A", 1][CellTopics])) {
            when (val source = ct.source) {
                is TableView -> source[CellTopics] = cc1
                is ColumnView -> source[CellTopics] = cc1
                is RowView -> source[CellTopics] = cc1
                is CellView -> source[CellTopics] = cc1
            }

            ct(null as Collection<String>?)

            when (val source = ct.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
            }

            ct(null as String?)

            when (val source = ct.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
            }

            ct(null as CellTopics<*>?)

            when (val source = ct.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
            }

            ct(null as Unit?)

            when (val source = ct.source) {
                is TableView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is ColumnView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is RowView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
                is CellView -> assertEquals(sortedSetOf<String>(), source[CellTopics].topics)
            }
        }
    }

    @Test
    fun `cell height invoke`() {
        val ch1 = TableView[null][CellHeight].let { it(111); it.source[CellHeight] }
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (ch in listOf(tv1[CellHeight], tv1[1][CellHeight], tv1["A", 1][CellHeight])) {
            ch(222)

            when (val source = ch.source) {
                is TableView -> assertEquals(222L, source[CellHeight].height)
                is RowView -> assertEquals(222L, source[CellHeight].height)
                is CellView -> assertEquals(222L, source[CellHeight].height)
            }

            ch(ch1)

            when (val source = ch.source) {
                is TableView -> assertEquals(111L, source[CellHeight].height)
                is RowView -> assertEquals(111L, source[CellHeight].height)
                is CellView -> assertEquals(111L, source[CellHeight].height)
            }

            ch(Unit)

            when (val source = ch.source) {
                is TableView -> assertEquals(Unit, source[CellHeight].height)
                is RowView -> assertEquals(Unit, source[CellHeight].height)
                is CellView -> assertEquals(Unit, source[CellHeight].height)
            }
        }

        for (ch in listOf(tv1[CellHeight], tv1[1][CellHeight], tv1["A", 1][CellHeight])) {
            ch(null as Long?)

            when (val source = ch.source) {
                is TableView -> assertEquals(Unit, source[CellHeight].height)
                is RowView -> assertEquals(Unit, source[CellHeight].height)
                is CellView -> assertEquals(Unit, source[CellHeight].height)
            }

            ch(null as CellHeight<*, *>?)

            when (val source = ch.source) {
                is TableView -> assertEquals(Unit, source[CellHeight].height)
                is RowView -> assertEquals(Unit, source[CellHeight].height)
                is CellView -> assertEquals(Unit, source[CellHeight].height)
            }

            ch(null as Unit?)

            when (val source = ch.source) {
                is TableView -> assertEquals(Unit, source[CellHeight].height)
                is RowView -> assertEquals(Unit, source[CellHeight].height)
                is CellView -> assertEquals(Unit, source[CellHeight].height)
            }
        }
    }

    @Test
    fun `cell width invoke`() {
        val cw1 = TableView[null][CellWidth].let { it(111); it.source[CellWidth] }
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (cw in listOf(tv1[CellWidth], tv1["A"][CellWidth], tv1["A", 1][CellWidth])) {
            cw(222)

            when (val source = cw.source) {
                is TableView -> assertEquals(222L, source[CellWidth].width)
                is ColumnView -> assertEquals(222L, source[CellWidth].width)
                is CellView -> assertEquals(222L, source[CellWidth].width)
            }

            cw(cw1)

            when (val source = cw.source) {
                is TableView -> assertEquals(111L, source[CellWidth].width)
                is ColumnView -> assertEquals(111L, source[CellWidth].width)
                is CellView -> assertEquals(111L, source[CellWidth].width)
            }

            cw(Unit)

            when (val source = cw.source) {
                is TableView -> assertEquals(Unit, source[CellWidth].width)
                is ColumnView -> assertEquals(Unit, source[CellWidth].width)
                is CellView -> assertEquals(Unit, source[CellWidth].width)
            }
        }

        for (cw in listOf(tv1[CellWidth], tv1["A"][CellWidth], tv1["A", 1][CellWidth])) {
            cw(null as Long?)

            when (val source = cw.source) {
                is TableView -> assertEquals(Unit, source[CellWidth].width)
                is ColumnView -> assertEquals(Unit, source[CellWidth].width)
                is CellView -> assertEquals(Unit, source[CellWidth].width)
            }

            cw(null as CellWidth<*, *>?)

            when (val source = cw.source) {
                is TableView -> assertEquals(Unit, source[CellWidth].width)
                is ColumnView -> assertEquals(Unit, source[CellWidth].width)
                is CellView -> assertEquals(Unit, source[CellWidth].width)
            }

            cw(null as Unit?)

            when (val source = cw.source) {
                is TableView -> assertEquals(Unit, source[CellWidth].width)
                is ColumnView -> assertEquals(Unit, source[CellWidth].width)
                is CellView -> assertEquals(Unit, source[CellWidth].width)
            }
        }
    }

    @Test
    fun `table transformer invoke`() {
        val tt1: Table.() -> Unit = {}
        val tt2: Table.() -> Unit = {}
        val tt3 = TableView[null][TableTransformer].let { it(tt2); it.source[TableTransformer] }
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        tv1[TableTransformer](tt1)
        assertEquals(tt1, tv1[TableTransformer].function)

        tv1[TableTransformer](null as TableTransformer<*>?)
        assertEquals(Unit, tv1[TableTransformer].function)

        tv1[TableTransformer](tt1)
        assertEquals(tt1, tv1[TableTransformer].function)

        tv1[TableTransformer](null as (Table.() -> Unit)?)
        assertEquals(Unit, tv1[TableTransformer].function)

        tv1[TableTransformer](tt3)
        assertEquals(tt2, tv1[TableTransformer].function)

        tv1[TableTransformer](Unit)
        assertEquals(Unit, tv1[TableTransformer].function)

        tv1[TableTransformer](tt2)
        assertEquals(tt2, tv1[TableTransformer].function)

        tv1[TableTransformer](null as Unit?)
        assertEquals(Unit, tv1[TableTransformer].function)
    }

    @Test
    fun `column transformer invoke`() {
        val ct1: Column.() -> Unit = {}
        val ct2: Column.() -> Unit = {}
        val ct3 = TableView[null]["A"][ColumnTransformer].let { it(ct2); it.source[ColumnTransformer] }
        val cv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]["A"]

        cv1[ColumnTransformer](ct1)
        assertEquals(ct1, cv1[ColumnTransformer].function)

        cv1[ColumnTransformer](null as ColumnTransformer<*>?)
        assertEquals(Unit, cv1[ColumnTransformer].function)

        cv1[ColumnTransformer](ct1)
        assertEquals(ct1, cv1[ColumnTransformer].function)

        cv1[ColumnTransformer](null as (Column.() -> Unit)?)
        assertEquals(Unit, cv1[ColumnTransformer].function)

        cv1[ColumnTransformer](ct3)
        assertEquals(ct2, cv1[ColumnTransformer].function)

        cv1[ColumnTransformer](Unit)
        assertEquals(Unit, cv1[ColumnTransformer].function)

        cv1[ColumnTransformer](ct2)
        assertEquals(ct2, cv1[ColumnTransformer].function)

        cv1[ColumnTransformer](null as Unit?)
        assertEquals(Unit, cv1[ColumnTransformer].function)
    }

    @Test
    fun `row transformer invoke`() {
        val rt1: Row.() -> Unit = {}
        val rt2: Row.() -> Unit = {}
        val rt3 = TableView[null][1][RowTransformer].let { it(rt2); it.source[RowTransformer] }
        val rv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"][1]

        rv1[RowTransformer](rt1)
        assertEquals(rt1, rv1[RowTransformer].function)

        rv1[RowTransformer](null as RowTransformer<*>?)
        assertEquals(Unit, rv1[RowTransformer].function)

        rv1[RowTransformer](rt1)
        assertEquals(rt1, rv1[RowTransformer].function)

        rv1[RowTransformer](null as (Row.() -> Unit)?)
        assertEquals(Unit, rv1[RowTransformer].function)

        rv1[RowTransformer](rt3)
        assertEquals(rt2, rv1[RowTransformer].function)

        rv1[RowTransformer](Unit)
        assertEquals(Unit, rv1[RowTransformer].function)

        rv1[RowTransformer](rt2)
        assertEquals(rt2, rv1[RowTransformer].function)

        rv1[RowTransformer](null as Unit?)
        assertEquals(Unit, rv1[RowTransformer].function)
    }

    @Test
    fun `cell transformer invoke`() {
        val ct1: Cell<*>.() -> Unit = {}
        val ct2: Cell<*>.() -> Unit = {}
        val ct3 = TableView[null]["A", 1][CellTransformer].let { it(ct2); it.source[CellTransformer] }
        val cv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]["A", 1]

        cv1[CellTransformer](ct1)
        assertEquals(ct1, cv1[CellTransformer].function)

        cv1[CellTransformer](null as CellTransformer<*>?)
        assertEquals(Unit, cv1[CellTransformer].function)

        cv1[CellTransformer](ct1)
        assertEquals(ct1, cv1[CellTransformer].function)

        cv1[CellTransformer](null as (Cell<*>.() -> Unit)?)
        assertEquals(Unit, cv1[CellTransformer].function)

        cv1[CellTransformer](ct3)
        assertEquals(ct2, cv1[CellTransformer].function)

        cv1[CellTransformer](Unit)
        assertEquals(Unit, cv1[CellTransformer].function)

        cv1[CellTransformer](ct2)
        assertEquals(ct2, cv1[CellTransformer].function)

        cv1[CellTransformer](null as Unit?)
        assertEquals(Unit, cv1[CellTransformer].function)
    }

    @Test
    fun `resources invoke`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        fun getHandler(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText("")
            }
        }

        val handler1 = getHandler()
        val handler2 = getHandler()
        val handler3 = getHandler()

        val r1 = TableView[null][Resources].let { it("a" to handler1); it.source[Resources] }

        tv1[Resources](r1)
        assertEquals(mapOf("a" to handler1), tv1[Resources].resources)

        tv1[Resources](null as Resources?)
        assertEquals(emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(), tv1[Resources].resources)

        tv1[Resources](mapOf("b" to handler2))
        assertEquals(mapOf("b" to handler2), tv1[Resources].resources)

        tv1[Resources](null as Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>?)
        assertEquals(emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(), tv1[Resources].resources)

        tv1[Resources]("c" to handler3)
        assertEquals(mapOf("c" to handler3), tv1[Resources].resources)

        tv1[Resources](null as Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>?)
        assertEquals(emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(), tv1[Resources].resources)

        tv1[Resources](listOf("a" to handler1, "c" to handler3))
        assertEquals(mapOf("a" to handler1, "c" to handler3), tv1[Resources].resources)

        tv1[Resources](null as Collection<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>?)
        assertEquals(emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(), tv1[Resources].resources)

        tv1[Resources](listOf("a" to handler1, "c" to handler3))
        tv1[Resources](Unit)
        assertEquals(emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(), tv1[Resources].resources)

        tv1[Resources](listOf("a" to handler1, "c" to handler3))
        tv1[Resources](null as Unit?)
        assertEquals(emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(), tv1[Resources].resources)
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
            TableView.views.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}
