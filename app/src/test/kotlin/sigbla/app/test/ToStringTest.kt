package sigbla.app.test

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.junit.After
import org.junit.Test
import sigbla.app.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToStringTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
        TableView.names.forEach { TableView.delete(it) }
    }

    @Test
    fun `column header`() {
        val table = Table[object {}.javaClass.enclosingMethod.name]

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            val ch1 = if (labels.isNotEmpty()) ColumnHeader(*labels.toTypedArray()) else null
            labels.add(UUID.randomUUID().toString())
            val ch2 = ColumnHeader(*labels.toTypedArray())

            if (ch1 != null)
                move(table[ch1] to table[ch2])
            else
                table[ch2, 0] = "0"

            assertEquals("ColumnHeader[${labels.joinToString(limit = 30)}]", ch2.toString())
            assertEquals("Column[${labels.joinToString(limit = 30)}]", table[ch2].toString())
        }
    }

    @Test
    fun `column range`() {
        val table = Table[object {}.javaClass.enclosingMethod.name]

        val range = table["A", "B", "C"]..table["D", "E", "F"]

        assertEquals("Column[A, B, C]..Column[D, E, F]", range.toString())
    }

    @Test
    fun `row index`() {
        val table = Table[object {}.javaClass.enclosingMethod.name]

        for (indexRelation in IndexRelation.entries) {
            val index = ThreadLocalRandom.current().nextLong()
            val row = table[indexRelation, index]

            assertEquals("Row[$indexRelation $index]", row.toString())
        }
    }

    @Test
    fun `row range`() {
        val table = Table[object {}.javaClass.enclosingMethod.name]

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
        val tableView = TableView[object {}.javaClass.enclosingMethod.name]

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            labels.add(UUID.randomUUID().toString())
            val ch = ColumnHeader(*labels.toTypedArray())

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
        val tableView = TableView[object {}.javaClass.enclosingMethod.name]

        val unitCellHeight = tableView[CellHeight].also { it <Int>{ 100 } }
        val pixelCellHeight = tableView[CellHeight]

        assertEquals("UnitCellHeight", unitCellHeight.toString())
        assertEquals("PixelCellHeight[100]", pixelCellHeight.toString())

        val unitCellWidth = tableView[CellWidth].also { it <Int>{ 100 } }
        val pixelCellWidth = tableView[CellWidth]

        assertEquals("UnitCellWidth", unitCellWidth.toString())
        assertEquals("PixelCellWidth[100]", pixelCellWidth.toString())

        val emptyCellClasses = tableView[CellClasses].also { it <List<String>>{ listOf("B", "A") } }
        val filledCellClasses = tableView[CellClasses]

        assertEquals("CellClasses[]", emptyCellClasses.toString())
        assertEquals("CellClasses[A, B]", filledCellClasses.toString())

        val emptyCellTopics = tableView[CellTopics].also { it <List<String>>{ listOf("B", "A") } }
        val filledCellTopics = tableView[CellTopics]

        assertEquals("CellTopics[]", emptyCellTopics.toString())
        assertEquals("CellTopics[A, B]", filledCellTopics.toString())

        fun getHandler(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText(text = "Response 1")
            }
        }

        val emptyResources = tableView[Resources].also { it <List<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>>{ listOf("B" to getHandler(), "A" to getHandler()) } }
        val filledResources = tableView[Resources]

        assertEquals("Resources[]", emptyResources.toString())
        // These are in order they are added
        assertEquals("Resources[B, A]", filledResources.toString())

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            labels.add(UUID.randomUUID().toString())

            tableView[CellClasses] = labels
            tableView[CellTopics] = labels
            tableView[Resources] = labels.map { it to getHandler() }

            assertEquals("CellClasses[${labels.sorted().joinToString(limit = 30)}]", tableView[CellClasses].toString())
            assertEquals("CellTopics[${labels.sorted().joinToString(limit = 30)}]", tableView[CellTopics].toString())
            assertEquals("Resources[${labels.joinToString(limit = 30)}]", tableView[Resources].toString())
        }
    }

    @Test
    fun events() {
        val name = object {}.javaClass.enclosingMethod.name

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

        assertEquals("TableListenerReference[${object {}.javaClass.enclosingMethod.name}, order=100, allowLoop=true]", tableListenerRef.toString())

        val tableViewListenerRef = on(tableView, name = tableView.name, skipHistory = true, order = 200, allowLoop = true) events {
            forEach {
                assertEquals("TableViewListenerEvent[SourceTable[TableView[$name view], null] -> SourceTable[TableView[$name view], Table[$name]]]", it.toString())
            }
        }

        tableView[Table] = table

        assertEquals("TableViewListenerReference[${object {}.javaClass.enclosingMethod.name} view, order=200, allowLoop=true]", tableViewListenerRef.toString())
    }
}