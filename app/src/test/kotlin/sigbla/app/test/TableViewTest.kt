/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import sigbla.app.*
import sigbla.app.exceptions.InvalidRowException
import sigbla.app.exceptions.InvalidTableViewException
import java.io.File
import kotlin.test.assertFailsWith

class TableViewTest {
    @After
    fun cleanup() {
        TableView.names.forEach { TableView.delete(it) }
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `registry test`() {
        val t1 = TableView[object {}.javaClass.enclosingMethod.name]
        val t2 = TableView.fromRegistry(t1.name!!)
        assertEquals(t1, t2)
        assertTrue(t1 === t2)

        TableView.delete(t1.name!!)

        assertFailsWith(InvalidTableViewException::class) {
            TableView.fromRegistry(t1.name!!)
        }

        val t3 = TableView.fromRegistry(t1.name!!) {
            TableView[t1.name]
        }

        assertNotEquals(t1, t3)
        assertFalse(t1 === t3)
        assertEquals(t1.name, t3.name)

        assertEquals(1, TableView.names.size)
        assertEquals(t1.name, TableView.names.first())
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
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
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
    }

    @Test
    fun `column view params`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
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

        tv1["A"][CellClasses] = (tv1[CellClasses] - "a") - "d"
        tv1["A"][CellTopics] = (tv1[CellTopics] - "b") - "e"
        assertEquals(emptySet<String>(), tv1["A"][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].toSet())

        tv1["A"][CellClasses] = tv1[CellClasses] + "a"
        tv1["A"][CellTopics] = tv1[CellTopics] + "b"
        assertEquals(setOf("a"), tv1["A"][CellClasses].toSet())
        assertEquals(setOf("b"), tv1["A"][CellTopics].toSet())

        tv1["A"][CellWidth] = null
        tv1["A"][CellClasses] = null
        tv1["A"][CellTopics] = null

        assertEquals(UnitCellWidth::class, tv1["A"][CellWidth]::class)
        assertEquals(emptySet<String>(), tv1["A"][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1["A"][CellTopics].toSet())
    }

    @Test
    fun `row view params`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
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

        tv1[1][CellClasses] = (tv1[CellClasses] - "a") - "d"
        tv1[1][CellTopics] = (tv1[CellTopics] - "b") - "e"
        assertEquals(emptySet<String>(), tv1[1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1[1][CellTopics].toSet())

        tv1[1][CellClasses] = tv1[CellClasses] + "a"
        tv1[1][CellTopics] = tv1[CellTopics] + "b"
        assertEquals(setOf("a"), tv1[1][CellClasses].toSet())
        assertEquals(setOf("b"), tv1[1][CellTopics].toSet())

        tv1[1][CellHeight] = null
        tv1[1][CellClasses] = null
        tv1[1][CellTopics] = null

        assertEquals(UnitCellHeight::class, tv1[1][CellHeight]::class)
        assertEquals(emptySet<String>(), tv1[1][CellClasses].toSet())
        assertEquals(emptySet<String>(), tv1[1][CellTopics].toSet())
    }

    @Test
    fun `derived view params`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
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
    }

    /*
    @Test
    fun `table view swaps`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

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
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        tv1[CellHeight] {
            100
        }
        tv1[CellWidth] {
            200
        }
        tv1[CellClasses] {
            "300"
        }
        tv1[CellTopics] {
            "400"
        }

        assertEquals(100L, tv1[CellHeight].height)
        assertEquals(200L, tv1[CellWidth].width)
        assertEquals(listOf("300"), tv1[CellClasses].classes)
        assertEquals(listOf("400"), tv1[CellTopics].topics)

        tv1[CellClasses] {
            setOf("500", "600")
        }
        tv1[CellTopics] {
            setOf("700", "800")
        }

        assertEquals(listOf("500", "600"), tv1[CellClasses].classes)
        assertEquals(listOf("700", "800"), tv1[CellTopics].topics)

        tv1[CellHeight] { }
        tv1[CellWidth] { }
        tv1[CellClasses] { }
        tv1[CellTopics] { }

        assertEquals(100L, tv1[CellHeight].height)
        assertEquals(200L, tv1[CellWidth].width)
        assertEquals(listOf("500", "600"), tv1[CellClasses].classes)
        assertEquals(listOf("700", "800"), tv1[CellTopics].topics)

        tv1[CellHeight] { null }
        tv1[CellWidth] { null }
        tv1[CellClasses] { null }
        tv1[CellTopics] { null }

        assertEquals(Unit, tv1[CellHeight].height)
        assertEquals(Unit, tv1[CellWidth].width)
        assertEquals(emptyList<String>(), tv1[CellClasses].classes)
        assertEquals(emptyList<String>(), tv1[CellTopics].topics)
    }

    @Test
    fun `tableview invoke 2`() {
        /*
        TODO

        See cellview invoke 2 and similar..
        But to make that work, we can't do batching with tv { .. } (or table { .. } to keep consistency),
        and would need to introduce batch(tv) { .. } as the batching API approach..
         */
    }

    @Test
    fun `columnview invoke 1`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]
        // TODO Add CellTransformer when supported

        tv1["A"][CellWidth] {
            200
        }
        tv1["A"][CellClasses] {
            "300"
        }
        tv1["A"][CellTopics] {
            "400"
        }

        assertEquals(200L, tv1["A"][CellWidth].width)
        assertEquals(listOf("300"), tv1["A"][CellClasses].classes)
        assertEquals(listOf("400"), tv1["A"][CellTopics].topics)

        tv1["A"][CellClasses] {
            setOf("500", "600")
        }
        tv1["A"][CellTopics] {
            setOf("700", "800")
        }

        assertEquals(listOf("500", "600"), tv1["A"][CellClasses].classes)
        assertEquals(listOf("700", "800"), tv1["A"][CellTopics].topics)

        tv1["A"][CellWidth] { }
        tv1["A"][CellClasses] { }
        tv1["A"][CellTopics] { }

        assertEquals(200L, tv1["A"][CellWidth].width)
        assertEquals(listOf("500", "600"), tv1["A"][CellClasses].classes)
        assertEquals(listOf("700", "800"), tv1["A"][CellTopics].topics)

        tv1["A"][CellWidth] { null }
        tv1["A"][CellClasses] { null }
        tv1["A"][CellTopics] { null }

        assertEquals(Unit, tv1["A"][CellWidth].width)
        assertEquals(emptyList<String>(), tv1["A"][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1["A"][CellTopics].topics)
    }

    @Test
    fun `columnview invoke 2`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]
        // TODO Add CellTransformer when supported

        tv1["A"] {
            tv1["B"][CellWidth]
        }
        tv1["A"] {
            tv1["B"][CellClasses]
        }
        tv1["A"] {
            tv1["B"][CellTopics]
        }
        tv1["A"] { }

        assertEquals(Unit, tv1["A"][CellWidth].width)
        assertEquals(emptyList<String>(), tv1["A"][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1["A"][CellTopics].topics)

        tv1["B"][CellWidth] {
            200
        }
        tv1["B"][CellClasses] {
            "300"
        }
        tv1["B"][CellTopics] {
            "400"
        }

        tv1["A"] {
            tv1["B"][CellWidth]
        }
        tv1["A"] {
            tv1["B"][CellClasses]
        }
        tv1["A"] {
            tv1["B"][CellTopics]
        }

        assertEquals(200L, tv1["A"][CellWidth].width)
        assertEquals(listOf("300"), tv1["A"][CellClasses].classes)
        assertEquals(listOf("400"), tv1["A"][CellTopics].topics)

        tv1["A"] { }

        assertEquals(200L, tv1["A"][CellWidth].width)
        assertEquals(listOf("300"), tv1["A"][CellClasses].classes)
        assertEquals(listOf("400"), tv1["A"][CellTopics].topics)

        tv1["A"] { null }

        assertEquals(Unit, tv1["A"][CellWidth].width)
        assertEquals(emptyList<String>(), tv1["A"][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1["A"][CellTopics].topics)

        tv1["A"] { tv1["B"] }

        assertEquals(200L, tv1["A"][CellWidth].width)
        assertEquals(listOf("300"), tv1["A"][CellClasses].classes)
        assertEquals(listOf("400"), tv1["A"][CellTopics].topics)
    }

    @Test
    fun `rowview invoke 1`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]
        // TODO Add CellTransformer when supported

        tv1[1][CellHeight] {
            200
        }
        tv1[1][CellClasses] {
            "300"
        }
        tv1[1][CellTopics] {
            "400"
        }

        assertEquals(200L, tv1[1][CellHeight].height)
        assertEquals(listOf("300"), tv1[1][CellClasses].classes)
        assertEquals(listOf("400"), tv1[1][CellTopics].topics)

        tv1[1][CellClasses] {
            setOf("500", "600")
        }
        tv1[1][CellTopics] {
            setOf("700", "800")
        }

        assertEquals(listOf("500", "600"), tv1[1][CellClasses].classes)
        assertEquals(listOf("700", "800"), tv1[1][CellTopics].topics)

        tv1[1][CellHeight] { }
        tv1[1][CellClasses] { }
        tv1[1][CellTopics] { }

        assertEquals(200L, tv1[1][CellHeight].height)
        assertEquals(listOf("500", "600"), tv1[1][CellClasses].classes)
        assertEquals(listOf("700", "800"), tv1[1][CellTopics].topics)

        tv1[1][CellHeight] { null }
        tv1[1][CellClasses] { null }
        tv1[1][CellTopics] { null }

        assertEquals(Unit, tv1[1][CellHeight].height)
        assertEquals(emptyList<String>(), tv1[1][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1[1][CellTopics].topics)
    }

    @Test
    fun `rowview invoke 2`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]
        // TODO Add CellTransformer when supported

        tv1[1] {
            tv1[2][CellHeight]
        }
        tv1[1][CellClasses] {
            tv1[2][CellClasses]
        }
        tv1[1][CellTopics] {
            tv1[2][CellTopics]
        }
        tv1[1] { }

        assertEquals(Unit, tv1[1][CellHeight].height)
        assertEquals(emptyList<String>(), tv1[1][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1[1][CellTopics].topics)

        tv1[2][CellHeight] {
            200
        }
        tv1[2][CellClasses] {
            "300"
        }
        tv1[2][CellTopics] {
            "400"
        }
        tv1[1] {
            tv1[2][CellHeight]
        }
        tv1[1][CellClasses] {
            tv1[2][CellClasses]
        }
        tv1[1][CellTopics] {
            tv1[2][CellTopics]
        }

        assertEquals(200L, tv1[1][CellHeight].height)
        assertEquals(listOf("300"), tv1[1][CellClasses].classes)
        assertEquals(listOf("400"), tv1[1][CellTopics].topics)

        tv1[1] { }

        assertEquals(200L, tv1[1][CellHeight].height)
        assertEquals(listOf("300"), tv1[1][CellClasses].classes)
        assertEquals(listOf("400"), tv1[1][CellTopics].topics)

        tv1[1] { null }

        assertEquals(Unit, tv1[1][CellHeight].height)
        assertEquals(emptyList<String>(), tv1[1][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1[1][CellTopics].topics)

        tv1[1] { tv1[2] }

        assertEquals(200L, tv1[1][CellHeight].height)
        assertEquals(listOf("300"), tv1[1][CellClasses].classes)
        assertEquals(listOf("400"), tv1[1][CellTopics].topics)
    }

    @Test
    fun `cellview invoke 1`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]
        val ct: Cell<*>.() -> Any? = {}

        tv1["A", 1][CellHeight] {
            100
        }
        tv1["A", 1][CellWidth] {
            200
        }
        tv1["A", 1][CellClasses] {
            "300"
        }
        tv1["A", 1][CellTopics] {
            "400"
        }
        tv1["A", 1][CellTransformer] {
            ct
        }

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(listOf("300"), tv1["A", 1][CellClasses].classes)
        assertEquals(listOf("400"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        tv1["A", 1][CellClasses] {
            setOf("500", "600")
        }
        tv1["A", 1][CellTopics] {
            setOf("700", "800")
        }

        assertEquals(listOf("500", "600"), tv1["A", 1][CellClasses].classes)
        assertEquals(listOf("700", "800"), tv1["A", 1][CellTopics].topics)

        tv1["A", 1][CellHeight] { }
        tv1["A", 1][CellWidth] { }
        tv1["A", 1][CellClasses] { }
        tv1["A", 1][CellTopics] { }
        tv1["A", 1][CellTransformer] { }

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(listOf("500", "600"), tv1["A", 1][CellClasses].classes)
        assertEquals(listOf("700", "800"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        tv1["A", 1][CellHeight] { null }
        tv1["A", 1][CellWidth] { null }
        tv1["A", 1][CellClasses] { null }
        tv1["A", 1][CellTopics] { null }
        tv1["A", 1][CellTransformer] { null }

        assertEquals(Unit, tv1["A", 1][CellHeight].height)
        assertEquals(Unit, tv1["A", 1][CellWidth].width)
        assertEquals(emptyList<String>(), tv1["A", 1][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1["A", 1][CellTopics].topics)
        assertEquals(Unit, tv1["A", 1][CellTransformer].function)
    }

    @Test
    fun `cellview invoke 2`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]
        val ct: Cell<*>.() -> Any? = {}

        tv1["A", 1] {
            tv1["B", 1][CellHeight]
        }
        tv1["A", 1] {
            tv1["B", 1][CellWidth]
        }
        tv1["A", 1] {
            tv1["B", 1][CellClasses]
        }
        tv1["A", 1] {
            tv1["B", 1][CellTopics]
        }
        tv1["A", 1] {
            tv1["B", 1][CellTransformer]
        }
        tv1["A", 1] { }

        assertEquals(Unit, tv1["A", 1][CellHeight].height)
        assertEquals(Unit, tv1["A", 1][CellWidth].width)
        assertEquals(emptyList<String>(), tv1["A", 1][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1["A", 1][CellTopics].topics)
        assertEquals(Unit, tv1["A", 1][CellTransformer].function)

        tv1["B", 1][CellHeight] {
            100
        }
        tv1["B", 1][CellWidth] {
            200
        }
        tv1["B", 1][CellClasses] {
            "300"
        }
        tv1["B", 1][CellTopics] {
            "400"
        }
        tv1["B", 1][CellTransformer] {
            ct
        }

        tv1["A", 1] {
            tv1["B", 1][CellHeight]
        }
        tv1["A", 1] {
            tv1["B", 1][CellWidth]
        }
        tv1["A", 1] {
            tv1["B", 1][CellClasses]
        }
        tv1["A", 1] {
            tv1["B", 1][CellTopics]
        }
        tv1["A", 1] {
            tv1["B", 1][CellTransformer]
        }

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(listOf("300"), tv1["A", 1][CellClasses].classes)
        assertEquals(listOf("400"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        tv1["A", 1] { }

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(listOf("300"), tv1["A", 1][CellClasses].classes)
        assertEquals(listOf("400"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        tv1["A", 1] { null }

        assertEquals(Unit, tv1["A", 1][CellHeight].height)
        assertEquals(Unit, tv1["A", 1][CellWidth].width)
        assertEquals(emptyList<String>(), tv1["A", 1][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1["A", 1][CellTopics].topics)
        assertEquals(Unit, tv1["A", 1][CellTransformer].function)

        tv1["A", 1] { tv1["B", 1] }

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(listOf("300"), tv1["A", 1][CellClasses].classes)
        assertEquals(listOf("400"), tv1["A", 1][CellTopics].topics)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)
    }

    @Test
    fun `tableview resources`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

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

        tv1[Resources] { /* no assignment */ }
        assertEquals(mapOf("foo/bar" to handler1), tv1[Resources].resources)

        tv1[Resources] { mapOf("fiz/buz" to handler2) }
        assertEquals(mapOf("fiz/buz" to handler2), tv1[Resources].resources)

        tv1[Resources] { "fiz/buz" to handler1 }
        assertEquals(mapOf("fiz/buz" to handler1), tv1[Resources].resources)

        tv1[Resources] { listOf("foo/bar" to handler2) }
        assertEquals(mapOf("foo/bar" to handler2), tv1[Resources].resources)

        tv1[Resources] { resources1 }
        assertEquals(emptyMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(), tv1[Resources].resources)

        tv1[Resources] { resources2 }
        assertEquals(mapOf("foo/bar" to handler1), tv1[Resources].resources)
    }

    @Test
    fun `tableview js and css resources`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        assertTrue(jsHandlers.isEmpty())
        assertTrue(cssHandlers.isEmpty())

        fun getHandler1(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText(text = "Response 1")
            }
        }

        tv1[Resources] {
            this + ("other/resource" to getHandler1())
        }

        assertTrue(jsHandlers.isEmpty())
        assertTrue(cssHandlers.isEmpty())

        tv1[Resources] {
            this + ("js/resource.js" to jsResource("/js/resource.js"))
        }

        assertEquals(1, jsHandlers.size)
        assertTrue(cssHandlers.isEmpty())

        tv1[Resources] {
            this + ("css/resource.css" to cssResource("/css/resource.css"))
        }

        assertEquals(1, jsHandlers.size)
        assertEquals(1, cssHandlers.size)

        tv1[Resources] {
            val tmpFile = File.createTempFile("tmpJsFile", ".js")
            tmpFile.deleteOnExit()
            this.javaClass.getResourceAsStream("/js/resource.js").buffered().transferTo(tmpFile.outputStream())
            this + ("js/resource2.js" to jsFile(tmpFile))
        }

        assertEquals(2, jsHandlers.size)
        assertEquals(1, cssHandlers.size)

        tv1[Resources] {
            val tmpFile = File.createTempFile("tmpCssFile", ".css")
            tmpFile.deleteOnExit()
            this.javaClass.getResourceAsStream("/css/resource.css").buffered().transferTo(tmpFile.outputStream())
            this + ("css/resource2.css" to cssFile(tmpFile))
        }

        assertEquals(2, jsHandlers.size)
        assertEquals(2, cssHandlers.size)
    }

    @Test
    fun `only valid row index relation getters and setters`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
        val tv1 = TableView[t1]

        for (ir in IndexRelation.entries) {
            // TableView
            if (ir == IndexRelation.AT) {
                tv1[t1[ir, 0]] = tv1[t1[ir, 0]]
                tv1[t1[ir, 0]] = { tv1[t1[ir, 0]] }
                tv1[t1[ir, 0]] { tv1[t1[ir, 0]] }
                assertEquals(RowView::class, tv1[t1[ir, 0]]::class)
            } else {
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]] }
                val validRow = t1[IndexRelation.AT, 0]
                val rowView = tv1[validRow]
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]] = rowView }
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]] = { rowView } }
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]] { rowView } }
            }

            // ColumnView
            if (ir == IndexRelation.AT) {
                tv1["A"][t1[ir, 0]] = tv1["A"][t1[ir, 0]]
                tv1["A"][t1[ir, 0]] = { tv1["A"][t1[ir, 0]] }
                tv1["A"][t1[ir, 0]] { tv1["A"][t1[ir, 0]] }
                assertEquals(CellView::class, tv1["A"][t1[ir, 0]]::class)
            } else {
                assertFailsWith<InvalidRowException> { tv1["A"][t1[ir, 0]] }
                val validRow = t1[IndexRelation.AT, 0]
                val cellView = tv1["A"][validRow]
                assertFailsWith<InvalidRowException> { tv1["A"][t1[ir, 0]] = cellView }
                assertFailsWith<InvalidRowException> { tv1["A"][t1[ir, 0]] = { cellView } }
                assertFailsWith<InvalidRowException> { tv1["A"][t1[ir, 0]] { cellView } }
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
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]]["A"] = { cellView } }
                assertFailsWith<InvalidRowException> { tv1[t1[ir, 0]]["A"] { cellView } }
            }
        }
    }

    @Test
    fun `clear columnview`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        tv1["A"][CellClasses] = "cc-1"
        tv1["A"][CellTopics] = "ct-1"
        tv1["A"][CellWidth] = 1000

        var count = 0

        on(tv1["A"], skipHistory = true) events {
            count += count()

            assertEquals(listOf("cc-1"), oldView["A"][CellClasses].classes)
            assertEquals(listOf("ct-1"), oldView["A"][CellTopics].topics)
            assertEquals(1000L, oldView["A"][CellWidth].width)

            assertEquals(emptyList<String>(), newView["A"][CellClasses].classes)
            assertEquals(emptyList<String>(), newView["A"][CellTopics].topics)
            assertEquals(Unit, newView["A"][CellWidth].width)
        }

        assertEquals(0, count)

        assertEquals(listOf("cc-1"), tv1["A"][CellClasses].classes)
        assertEquals(listOf("ct-1"), tv1["A"][CellTopics].topics)
        assertEquals(1000L, tv1["A"][CellWidth].width)

        clear(tv1["A"])

        assertEquals(emptyList<String>(), tv1["A"][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1["A"][CellTopics].topics)
        assertEquals(Unit, tv1["A"][CellWidth].width)

        assertEquals(3, count)
    }

    @Test
    fun `clear rowview`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        tv1[1][CellClasses] = "cc-1"
        tv1[1][CellTopics] = "ct-1"
        tv1[1][CellHeight] = 1000

        var count = 0

        on(tv1[1], skipHistory = true) events {
            count += count()

            assertEquals(listOf("cc-1"), oldView[1][CellClasses].classes)
            assertEquals(listOf("ct-1"), oldView[1][CellTopics].topics)
            assertEquals(1000L, oldView[1][CellHeight].height)

            assertEquals(emptyList<String>(), newView[1][CellClasses].classes)
            assertEquals(emptyList<String>(), newView[1][CellTopics].topics)
            assertEquals(Unit, newView[1][CellHeight].height)
        }

        assertEquals(0, count)

        assertEquals(listOf("cc-1"), tv1[1][CellClasses].classes)
        assertEquals(listOf("ct-1"), tv1[1][CellTopics].topics)
        assertEquals(1000L, tv1[1][CellHeight].height)

        clear(tv1[1])

        assertEquals(emptyList<String>(), tv1[1][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1[1][CellTopics].topics)
        assertEquals(Unit, tv1[1][CellHeight].height)

        assertEquals(3, count)
    }

    @Test
    fun `clear cellview`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        tv1["A", 1][CellHeight] = 1000
        tv1["A", 1][CellWidth] = 2000
        tv1["A", 1][CellClasses] = "cc-1"
        tv1["A", 1][CellTopics] = "ct-1"
        val ct: Cell<*>.() -> Any? = {}
        tv1["A", 1][CellTransformer] = ct

        var count = 0

        on(tv1["A", 1], skipHistory = true) events {
            count += count()

            assertEquals(listOf("cc-1"), oldView["A", 1][CellClasses].classes)
            assertEquals(listOf("ct-1"), oldView["A", 1][CellTopics].topics)
            assertEquals(1000L, oldView["A", 1][CellHeight].height)
            assertEquals(2000L, oldView["A", 1][CellWidth].width)
            assertEquals(ct, oldView["A", 1][CellTransformer].function)

            assertEquals(emptyList<String>(), newView["A", 1][CellClasses].classes)
            assertEquals(emptyList<String>(), newView["A", 1][CellTopics].topics)
            assertEquals(Unit, newView["A", 1][CellHeight].height)
            assertEquals(Unit, newView["A", 1][CellWidth].width)
            assertEquals(Unit, newView["A", 1][CellTransformer].function)
        }

        assertEquals(0, count)

        assertEquals(listOf("cc-1"), tv1["A", 1][CellClasses].classes)
        assertEquals(listOf("ct-1"), tv1["A", 1][CellTopics].topics)
        assertEquals(1000L, tv1["A", 1][CellHeight].height)
        assertEquals(2000L, tv1["A", 1][CellWidth].width)
        assertEquals(ct, tv1["A", 1][CellTransformer].function)

        clear(tv1["A", 1])

        assertEquals(emptyList<String>(), tv1["A", 1][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1["A", 1][CellTopics].topics)
        assertEquals(Unit, tv1["A", 1][CellHeight].height)
        assertEquals(Unit, tv1["A", 1][CellWidth].width)
        assertEquals(Unit, tv1["A", 1][CellTransformer].function)

        assertEquals(5, count)
    }

    // TODO See TableTest for inspiration
}
