package sigbla.app.test

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import sigbla.app.CellClasses
import sigbla.app.CellHeight
import sigbla.app.CellTopics
import sigbla.app.CellWidth
import sigbla.app.DEFAULT_CELL_HEIGHT
import sigbla.app.DEFAULT_CELL_WIDTH
import sigbla.app.Table
import sigbla.app.TableView
import sigbla.app.UnitCellHeight
import sigbla.app.UnitCellWidth

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

    // TODO See TableTest for inspiration
}