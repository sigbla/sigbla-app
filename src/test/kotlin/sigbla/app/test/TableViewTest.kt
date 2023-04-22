package sigbla.app.test

import org.junit.After
import org.junit.Test
import sigbla.app.DEFAULT_CELL_HEIGHT
import sigbla.app.DEFAULT_CELL_WIDTH
import sigbla.app.Table
import sigbla.app.TableView
import kotlin.test.assertEquals

class TableViewTest {
    @After
    fun cleanup() {
        TableView.names.forEach { TableView.delete(it) }
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `table view builder init`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
        val tv1 = TableView[t1]

        assertEquals(null, tv1.cellHeight)
        assertEquals(null, tv1.cellWidth)

        tv1[TableView] = {
            assertEquals(null, cellHeight)
            assertEquals(null, cellWidth)
            cellHeight = DEFAULT_CELL_HEIGHT * 2
            cellWidth = DEFAULT_CELL_WIDTH * 2
        }

        assertEquals(DEFAULT_CELL_HEIGHT * 2, tv1.cellHeight)
        assertEquals(DEFAULT_CELL_WIDTH * 2, tv1.cellWidth)

        tv1.cellHeight = DEFAULT_CELL_HEIGHT * 3
        tv1.cellWidth = DEFAULT_CELL_WIDTH * 3

        tv1[TableView] = {
            assertEquals(DEFAULT_CELL_HEIGHT * 3, cellHeight)
            assertEquals(DEFAULT_CELL_WIDTH * 3, cellWidth)
            cellHeight = null
            cellWidth = null
        }

        assertEquals(null, tv1.cellHeight)
        assertEquals(null, tv1.cellWidth)
    }

    @Test
    fun `column view builder init`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
        val tv1 = TableView[t1]

        assertEquals(null, tv1["A"].cellWidth)

        tv1["A"] = {
            assertEquals(null, cellWidth)
            cellWidth = DEFAULT_CELL_WIDTH * 2
        }

        assertEquals(DEFAULT_CELL_WIDTH * 2, tv1["A"].cellWidth)

        tv1["A"].cellWidth = DEFAULT_CELL_WIDTH * 3

        tv1["A"] = {
            assertEquals(DEFAULT_CELL_WIDTH * 3, cellWidth)
            cellWidth = null
        }

        assertEquals(null, tv1["A"].cellWidth)
    }

    @Test
    fun `row view builder init`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]
        val tv1 = TableView[t1]

        assertEquals(null, tv1[1].cellHeight)

        tv1[1] = {
            assertEquals(null, cellHeight)
            cellHeight = DEFAULT_CELL_HEIGHT * 2
        }

        assertEquals(DEFAULT_CELL_HEIGHT * 2, tv1[1].cellHeight)

        tv1[1].cellHeight = DEFAULT_CELL_HEIGHT * 3

        tv1[1] = {
            assertEquals(DEFAULT_CELL_HEIGHT * 3, cellHeight)
            cellHeight = null
        }

        assertEquals(null, tv1[1].cellHeight)
    }

    // TODO See TableTest for inspiration
}