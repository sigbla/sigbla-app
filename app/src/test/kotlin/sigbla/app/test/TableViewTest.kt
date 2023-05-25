package sigbla.app.test

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import sigbla.app.*

class TableViewTest {
    @After
    fun cleanup() {
        TableView.names.forEach { TableView.delete(it) }
        Table.names.forEach { Table.delete(it) }
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

    @Test
    fun `table view swaps`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        val tv1 = TableView[t1]
        val tv2 = TableView["t2"]
        val tv3 = TableView["t3"]

        assertEquals(t1, tv1[Table])

        assertNull(tv2[Table])

        tv2[Table] = t1
        assertEquals(t1, tv2[Table])

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
        assertNull(tv2[Table])

        tv2[TableView] = tv1

        assertNull(tv2[Table])

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

        assertNull(tv2[Table])
    }

    @Test
    fun `tableview invoke`() {
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
    fun `columnview invoke`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

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
    fun `rowview invoke`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

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
    fun `cellview invoke`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

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

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(listOf("300"), tv1["A", 1][CellClasses].classes)
        assertEquals(listOf("400"), tv1["A", 1][CellTopics].topics)

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

        assertEquals(100L, tv1["A", 1][CellHeight].height)
        assertEquals(200L, tv1["A", 1][CellWidth].width)
        assertEquals(listOf("500", "600"), tv1["A", 1][CellClasses].classes)
        assertEquals(listOf("700", "800"), tv1["A", 1][CellTopics].topics)

        tv1["A", 1][CellHeight] { null }
        tv1["A", 1][CellWidth] { null }
        tv1["A", 1][CellClasses] { null }
        tv1["A", 1][CellTopics] { null }

        assertEquals(Unit, tv1["A", 1][CellHeight].height)
        assertEquals(Unit, tv1["A", 1][CellWidth].width)
        assertEquals(emptyList<String>(), tv1["A", 1][CellClasses].classes)
        assertEquals(emptyList<String>(), tv1["A", 1][CellTopics].topics)
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

    // TODO See TableTest for inspiration
}