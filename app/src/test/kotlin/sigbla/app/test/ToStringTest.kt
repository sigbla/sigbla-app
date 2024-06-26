/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.junit.AfterClass
import org.junit.Test
import sigbla.app.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToStringTest {
    @Test
    fun `column header`() {
        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            val ch1 = if (labels.isNotEmpty()) Header(*labels.toTypedArray()) else null
            labels.add(UUID.randomUUID().toString())
            val ch2 = Header(*labels.toTypedArray())

            if (ch1 != null)
                move(table[ch1] to table[ch2])
            else
                table[ch2, 0] = "0"

            assertEquals("Header[${labels.joinToString(limit = 30)}]", ch2.toString())
            assertEquals("Column[${labels.joinToString(limit = 30)}]", table[ch2].toString())
        }
    }

    @Test
    fun `column range`() {
        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val range = table["A", "B", "C"]..table["D", "E", "F"]

        assertEquals("Column[A, B, C]..Column[D, E, F]", range.toString())
    }

    @Test
    fun `row index`() {
        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (indexRelation in IndexRelation.entries) {
            val index = ThreadLocalRandom.current().nextLong()
            val row = table[indexRelation, index]

            assertEquals("Row[$indexRelation $index]", row.toString())
        }
    }

    @Test
    fun `row range`() {
        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (i in 0..100) {
            val index1 = ThreadLocalRandom.current().nextLong()
            val index2 = ThreadLocalRandom.current().nextLong()

            val range = table[index1]..table[index2]

            assertEquals("Row[at $index1]..Row[at $index2]", range.toString())
        }
    }

    @Test
    fun table() {
        val name = UUID.randomUUID().toString()

        val tableNull = Table[null]
        val tableName = Table[name]

        assertEquals("Table[null]", tableNull.toString())
        assertEquals("Table[$name]", tableName.toString())
    }

    @Test
    fun `table view`() {
        val name1 = UUID.randomUUID().toString()
        val name2 = UUID.randomUUID().toString()

        val tableViewNull = TableView[null]
        val tableViewName1 = TableView[name1]
        val tableViewName2 = TableView[Table[name2]]

        assertEquals("TableView[null]", tableViewNull.toString())
        assertEquals("TableView[$name1]", tableViewName1.toString())
        assertEquals("TableView[$name2]", tableViewName2.toString())
    }

    @Test
    fun `cell view, column view, row view, derived`() {
        val tableView = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            labels.add(UUID.randomUUID().toString())
            val ch = Header(*labels.toTypedArray())

            val cellView = tableView[ch][i]
            val columnView = tableView[ch]
            val rowView = tableView[i]

            val derivedCellView = cellView.derived
            val derivedColumnView = columnView.derived
            val derivedRowView = rowView.derived

            assertEquals("CellView[${labels.joinToString(limit = 30)}, $i]", cellView.toString())
            assertEquals("ColumnView[${labels.joinToString(limit = 30)}]", columnView.toString())
            assertEquals("RowView[$i]", rowView.toString())
            assertEquals("DerivedCellView[${labels.joinToString(limit = 30)}, $i]", derivedCellView.toString())
            assertEquals("DerivedColumnView[${labels.joinToString(limit = 30)}]", derivedColumnView.toString())
            assertEquals("DerivedRowView[$i]", derivedRowView.toString())
        }
    }

    @Test
    fun `meta classes`() {
        val tableView = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val unitCellHeight = tableView[CellHeight].also { it(100) }
        val pixelCellHeight = tableView[CellHeight]

        assertEquals("UnitCellHeight", unitCellHeight.toString())
        assertEquals("PixelCellHeight[100]", pixelCellHeight.toString())

        val unitCellWidth = tableView[CellWidth].also { it(100) }
        val pixelCellWidth = tableView[CellWidth]

        assertEquals("UnitCellWidth", unitCellWidth.toString())
        assertEquals("PixelCellWidth[100]", pixelCellWidth.toString())

        val emptyCellClasses = tableView[CellClasses].also { it(listOf("B", "A")) }
        val filledCellClasses = tableView[CellClasses]

        assertEquals("CellClasses[]", emptyCellClasses.toString())
        assertEquals("CellClasses[A, B]", filledCellClasses.toString())

        val emptyCellTopics = tableView[CellTopics].also { it(listOf("B", "A")) }
        val filledCellTopics = tableView[CellTopics]

        assertEquals("CellTopics[]", emptyCellTopics.toString())
        assertEquals("CellTopics[A, B]", filledCellTopics.toString())

        val tableTransformerFunction: Table.() -> Unit = { }
        val emptyTableTransformer = tableView[TableTransformer].also { it(tableTransformerFunction) }
        val filledTableTransformer = tableView[TableTransformer]

        assertEquals("UnitTableTransformer", emptyTableTransformer.toString())
        assertEquals("FunctionTableTransformer[sigbla.app.Table.() -> kotlin.Unit]", filledTableTransformer.toString())

        val columnTransformerFunction: Column.() -> Unit = { }
        val emptyColumnTransformer = tableView["A"][ColumnTransformer].also { it(columnTransformerFunction) }
        val filledColumnTransformer = tableView["A"][ColumnTransformer]

        assertEquals("UnitColumnTransformer", emptyColumnTransformer.toString())
        assertEquals("FunctionColumnTransformer[sigbla.app.Column.() -> kotlin.Unit]", filledColumnTransformer.toString())

        val rowTransformerFunction: Row.() -> Unit = { }
        val emptyRowTransformer = tableView[1][RowTransformer].also { it(rowTransformerFunction) }
        val filledRowTransformer = tableView[1][RowTransformer]

        assertEquals("UnitRowTransformer", emptyRowTransformer.toString())
        assertEquals("FunctionRowTransformer[sigbla.app.Row.() -> kotlin.Unit]", filledRowTransformer.toString())

        val cellTransformerFunction: Cell<*>.() -> Unit = { }
        val emptyCellTransformer = tableView["A", 1][CellTransformer].also { it(cellTransformerFunction) }
        val filledCellTransformer = tableView["A", 1][CellTransformer]

        assertEquals("UnitCellTransformer", emptyCellTransformer.toString())
        assertEquals("FunctionCellTransformer[sigbla.app.Cell<*>.() -> kotlin.Unit]", filledCellTransformer.toString())

        fun getHandler(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText(text = "Response 1")
            }
        }

        val emptyResource = tableView[Resource["//A/B"]].also { it(getHandler()) }
        val filledResource = tableView[Resource["/A/B"]]

        // Notice how leading slashes are removed
        assertEquals("UnitResource[A/B]", emptyResource.toString())
        assertEquals("HandlerResource[A/B]", filledResource.toString())

        val unitHorizontal = tableView["A"][Position].also { it(Position.Left) }
        val leftHorizontal = tableView["A"][Position].also { it(Position.Right) }
        val rightHorizontal = tableView["A"][Position]

        assertEquals("Horizontal", unitHorizontal.toString())
        assertEquals("Left", leftHorizontal.toString())
        assertEquals("Right", rightHorizontal.toString())

        val unitVertical = tableView[1][Position].also { it(Position.Top) }
        val topVertical = tableView[1][Position].also { it(Position.Bottom) }
        val bottomVertical = tableView[1][Position]

        assertEquals("Vertical", unitVertical.toString())
        assertEquals("Top", topVertical.toString())
        assertEquals("Bottom", bottomVertical.toString())

        val unitVisibility = tableView[1][Visibility].also { it(Visibility.Show) }
        val showVisibility = tableView[1][Visibility].also { it(Visibility.Hide) }
        val hideVisibility = tableView[1][Visibility]

        assertEquals("Undefined", unitVisibility.toString())
        assertEquals("Show", showVisibility.toString())
        assertEquals("Hide", hideVisibility.toString())

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            labels.add(UUID.randomUUID().toString())

            tableView[CellClasses] = labels
            tableView[CellTopics] = labels

            assertEquals("CellClasses[${labels.sorted().joinToString(limit = 30)}]", tableView[CellClasses].toString())
            assertEquals("CellTopics[${labels.sorted().joinToString(limit = 30)}]", tableView[CellTopics].toString())
        }
    }

    @Test
    fun events() {
        val name = "${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"

        assertTrue(name.isNotEmpty())

        val table = Table[name]
        // Need a different name, otherwise it will pick up the table automatically
        val tableView = TableView["$name view"]

        table["A", 0] = "1"

        val tableListenerRef = on(table, name = table.name, skipHistory = true, order = 100, allowLoop = true) events {
            forEach {
                assertEquals("TableListenerEvent[1 -> 2]", it.toString())
            }
        }

        table["A", 0] = "2"

        assertEquals("TableListenerReference[${"${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"}, order=100, allowLoop=true]", tableListenerRef.toString())

        val tableViewListenerRef = on(tableView, name = tableView.name, skipHistory = true, order = 200, allowLoop = true) events {
            forEach {
                assertEquals("TableViewListenerEvent[SourceTable[TableView[$name view], null] -> SourceTable[TableView[$name view], Table[$name]]]", it.toString())
            }
        }

        tableView[Table] = table

        assertEquals("TableViewListenerReference[${"${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"} view, order=200, allowLoop=true]", tableViewListenerRef.toString())
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
