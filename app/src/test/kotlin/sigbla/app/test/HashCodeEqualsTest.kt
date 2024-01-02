/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
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
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class HashCodeEqualsTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
        TableView.names.forEach { TableView.delete(it) }
    }

    @Test
    fun `column header`() {
        val table1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val table2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            val ch1 = if (labels.isNotEmpty()) ColumnHeader(*labels.toTypedArray()) else null
            labels.add(UUID.randomUUID().toString())
            val ch2 = ColumnHeader(*labels.toTypedArray())

            if (ch1 != null) {
                move(table1[ch1] to table1[ch2])
                move(table2[ch1] to table2[ch2])
            } else {
                table1[ch2, 0] = "0"
                table2[ch2, 0] = "0"
            }

            assertNotEquals(table1[ch2], table2[ch2])
            assertEquals(table1[ch2].header, table2[ch2].header)

            assertEquals(table1[ch2].hashCode(), table2[ch2].hashCode())
            assertEquals(table1[ch2].header.hashCode(), table2[ch2].header.hashCode())

            if (ch1 != null) {
                assertNotEquals(table1[ch1].hashCode(), table2[ch2].hashCode())
                assertNotEquals(table1[ch1].header.hashCode(), table2[ch2].header.hashCode())
            }
        }
    }

    @Test
    fun `column range`() {
        val table1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val table2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        val range1a = table1["A", "B", "C"]..table1["D", "E", "F"]
        val range1b = table1["A", "B", "C"]..table1["D", "E", "F"]
        val range2 = table2["A", "B", "C"]..table2["D", "E", "F"]

        assertEquals(range1a, range1b)
        assertNotEquals(range1a, range2)

        assertEquals(range1a.hashCode(), range1b.hashCode())
        assertEquals(range1a.hashCode(), range2.hashCode())
    }

    @Test
    fun `row index`() {
        val table1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val table2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        for (indexRelation in IndexRelation.entries) {
            val index = ThreadLocalRandom.current().nextLong()
            val row1a = table1[indexRelation, index]
            val row1b = table1[indexRelation, index]
            val row2 = table2[indexRelation, index]

            assertEquals(row1a, row1b)
            assertNotEquals(row1a, row2)

            assertEquals(row1a.hashCode(), row1b.hashCode())
            assertEquals(row1a.hashCode(), row2.hashCode())
        }
    }

    @Test
    fun `row range`() {
        val table1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val table2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        for (i in 0..100) {
            val index1 = ThreadLocalRandom.current().nextLong()
            val index2 = ThreadLocalRandom.current().nextLong()

            val range1a = table1[index1]..table1[index2]
            val range1b = table1[index1]..table1[index2]
            val range2 = table2[index1]..table2[index2]

            assertEquals(range1a, range1b)
            assertNotEquals(range1a, range2)

            assertEquals(range1a.hashCode(), range1b.hashCode())
            assertEquals(range1a.hashCode(), range2.hashCode())
        }
    }

    @Test
    fun table() {
        val name = UUID.randomUUID().toString()

        val tableNull = Table[null]
        val tableName1 = Table[name]
        val tableName2 = clone(tableName1)

        assertNotEquals(tableNull, tableName1)
        assertNotEquals(tableNull, tableName2)
        assertNotEquals(tableName1, tableName2)

        assertNotEquals(tableNull.hashCode(), tableName1.hashCode())
        assertEquals(tableName1.hashCode(), tableName2.hashCode())
    }

    @Test
    fun `table view`() {
        val name = UUID.randomUUID().toString()

        val tableViewNull = TableView[null]
        val tableViewName1 = TableView[name]
        val tableViewName2 = clone(tableViewName1)

        assertNotEquals(tableViewNull, tableViewName1)
        assertNotEquals(tableViewNull, tableViewName2)
        assertNotEquals(tableViewName1, tableViewName2)

        assertNotEquals(tableViewNull.hashCode(), tableViewName1.hashCode())
        assertEquals(tableViewName1.hashCode(), tableViewName2.hashCode())
    }

    @Test
    fun `cell view, column view, row view, derived`() {
        val tableView1 = TableView[object {}.javaClass.enclosingMethod.name + " 1"]
        val tableView2 = TableView[object {}.javaClass.enclosingMethod.name + " 2"]

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            labels.add(UUID.randomUUID().toString())
            val ch = ColumnHeader(*labels.toTypedArray())

            val cellView1 = tableView1[ch][i]
            val columnView1 = tableView1[ch]
            val rowView1 = tableView1[i]

            val derivedCellView1 = cellView1.derived
            val derivedColumnView1 = columnView1.derived
            val derivedRowView1 = rowView1.derived

            val cellView2 = tableView2[ch][i]
            val columnView2 = tableView2[ch]
            val rowView2 = tableView2[i]

            val derivedCellView2 = cellView2.derived
            val derivedColumnView2 = columnView2.derived
            val derivedRowView2 = rowView2.derived

            assertNotEquals(cellView1, cellView2)
            assertNotEquals(columnView1, columnView2)
            assertNotEquals(rowView1, rowView2)
            assertNotEquals(derivedCellView1, derivedCellView2)
            assertNotEquals(derivedColumnView1, derivedColumnView2)
            assertNotEquals(derivedRowView1, derivedRowView2)

            assertEquals(cellView1.hashCode(), cellView2.hashCode())
            assertEquals(columnView1.hashCode(), columnView2.hashCode())
            assertEquals(rowView1.hashCode(), rowView2.hashCode())
            assertEquals(derivedCellView1.hashCode(), derivedCellView2.hashCode())
            assertEquals(derivedColumnView1.hashCode(), derivedColumnView2.hashCode())
            assertEquals(derivedRowView1.hashCode(), derivedRowView2.hashCode())
        }
    }

    @Test
    fun `meta classes`() {
        fun getHandler(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText(text = "Response 1")
            }
        }

        val handler1 = getHandler()
        val handler2 = getHandler()

        val tableView1 = TableView[object {}.javaClass.enclosingMethod.name + " 1"]

        val unitCellHeight1 = tableView1[CellHeight].also { it <Int>{ 100 } }
        val pixelCellHeight1 = tableView1[CellHeight]

        val unitCellWidth1 = tableView1[CellWidth].also { it <Int>{ 100 } }
        val pixelCellWidth1 = tableView1[CellWidth]

        val emptyCellClasses1 = tableView1[CellClasses].also { it <List<String>>{ listOf("B", "A") } }
        val filledCellClasses1 = tableView1[CellClasses]

        val emptyCellTopics1 = tableView1[CellTopics].also { it <List<String>>{ listOf("B", "A") } }
        val filledCellTopics1 = tableView1[CellTopics]

        val emptyResources1 = tableView1[Resources].also { it <List<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>>{ listOf("B" to handler1, "A" to handler2) } }
        val filledResources1 = tableView1[Resources]

        val tableView2 = TableView[object {}.javaClass.enclosingMethod.name + " 2"]

        val unitCellHeight2 = tableView2[CellHeight].also { it <Int>{ 100 } }
        val pixelCellHeight2 = tableView2[CellHeight]

        val unitCellWidth2 = tableView2[CellWidth].also { it <Int>{ 100 } }
        val pixelCellWidth2 = tableView2[CellWidth]

        val emptyCellClasses2 = tableView2[CellClasses].also { it <List<String>>{ listOf("B", "A") } }
        val filledCellClasses2 = tableView2[CellClasses]

        val emptyCellTopics2 = tableView2[CellTopics].also { it <List<String>>{ listOf("B", "A") } }
        val filledCellTopics2 = tableView2[CellTopics]

        val emptyResources2 = tableView2[Resources].also { it <List<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>>{ listOf("B" to handler1, "A" to handler2) } }
        val filledResources2 = tableView2[Resources]

        assertEquals(unitCellHeight1, unitCellHeight2)
        assertEquals(pixelCellHeight1, pixelCellHeight2)
        assertEquals(unitCellWidth1, unitCellWidth2)
        assertEquals(pixelCellWidth1, pixelCellWidth2)
        assertEquals(emptyCellClasses1, emptyCellClasses2)
        assertEquals(filledCellClasses1, filledCellClasses2)
        assertEquals(emptyCellTopics1, emptyCellTopics2)
        assertEquals(filledCellTopics1, filledCellTopics2)
        assertEquals(emptyResources1, emptyResources2)
        assertEquals(filledResources1, filledResources2)

        assertEquals(unitCellHeight1.hashCode(), unitCellHeight2.hashCode())
        assertEquals(pixelCellHeight1.hashCode(), pixelCellHeight2.hashCode())
        assertEquals(unitCellWidth1.hashCode(), unitCellWidth2.hashCode())
        assertEquals(pixelCellWidth1.hashCode(), pixelCellWidth2.hashCode())
        assertEquals(emptyCellClasses1.hashCode(), emptyCellClasses2.hashCode())
        assertEquals(filledCellClasses1.hashCode(), filledCellClasses2.hashCode())
        assertEquals(emptyCellTopics1.hashCode(), emptyCellTopics2.hashCode())
        assertEquals(filledCellTopics1.hashCode(), filledCellTopics2.hashCode())
        assertEquals(emptyResources1.hashCode(), emptyResources2.hashCode())
        assertEquals(filledResources1.hashCode(), filledResources2.hashCode())
    }

    @Test
    fun events() {
        val name = object {}.javaClass.enclosingMethod.name

        assertTrue(name.isNotEmpty())

        val table = Table[name]
        val tableView = TableView[table]

        table["A", 0] = "1"
        table["A", 1] = "1"

        var lastEvent1: TableListenerEvent<out Any, out Any>? = null

        on(table) events {
            forEach {
                if (lastEvent1 != null) {
                    assertNotEquals(lastEvent1, it)
                    assertNotEquals(lastEvent1.hashCode(), it.hashCode())
                }
                lastEvent1 = it
            }
        }

        table {
            table["A", 0] = "2"
            table["A", 1] = "2"
        }

        var lastEvent2: TableViewListenerEvent<out Any>? = null

        on(tableView) events {
            forEach {
                if (lastEvent2 != null) {
                    assertNotEquals(lastEvent2, it)
                    assertNotEquals(lastEvent2.hashCode(), it.hashCode())

                    // Testing SourceTable
                    assertNotEquals(lastEvent2!!.oldValue, it.oldValue)
                    assertNotEquals(lastEvent2!!.newValue, it.newValue)

                    if ((lastEvent2!!.oldValue as SourceTable).table != null) {
                        assertEquals(lastEvent2!!.oldValue.hashCode(), it.oldValue.hashCode())
                        assertEquals(lastEvent2!!.newValue.hashCode(), it.newValue.hashCode())
                    }
                }
                lastEvent2 = it
            }
        }

        tableView[Table] = table
        tableView[Table] = table
    }
}
