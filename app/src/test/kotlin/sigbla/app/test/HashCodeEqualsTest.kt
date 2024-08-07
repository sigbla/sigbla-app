/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.junit.AfterClass
import org.junit.Test
import sigbla.app.*
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class HashCodeEqualsTest {
    @Test
    fun `column header`() {
        val table1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val table2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            val ch1 = if (labels.isNotEmpty()) Header(*labels.toTypedArray()) else null
            labels.add(UUID.randomUUID().toString())
            val ch2 = Header(*labels.toTypedArray())

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
        val table1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val table2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

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
        val table1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val table2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

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
        val table1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val table2 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

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
        val tableView1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]
        val tableView2 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

        val labels = mutableListOf<String>()

        for (i in 1..40) {
            labels.add(UUID.randomUUID().toString())
            val ch = Header(*labels.toTypedArray())

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
        // Clean up any other Resources
        Resource.resources.forEach {
            Resource[it.key] = Unit
        }

        fun getHandler(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
            return {
                call.respondText(text = "Response 1")
            }
        }

        val handler1 = getHandler()
        val handler2 = getHandler()

        val tableView1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 1"]

        val unitCellHeight1 = tableView1[CellHeight].also { it(100) }
        val pixelCellHeight1 = tableView1[CellHeight]

        val unitCellWidth1 = tableView1[CellWidth].also { it(100) }
        val pixelCellWidth1 = tableView1[CellWidth]

        val emptyCellClasses1 = tableView1[CellClasses].also { it(listOf("B", "A")) }
        val filledCellClasses1 = tableView1[CellClasses]

        val emptyCellTopics1 = tableView1[CellTopics].also { it (listOf("B", "A")) }
        val filledCellTopics1 = tableView1[CellTopics]

        val tableTransformerFunction: Table.() -> Unit = { }
        val emptyTableTransformer1 = tableView1[TableTransformer].also { it(tableTransformerFunction) }
        val filledTableTransformer1 = tableView1[TableTransformer]

        val columnTransformerFunction: Column.() -> Unit = { }
        val emptyColumnTransformer1 = tableView1["A"][ColumnTransformer].also { it(columnTransformerFunction) }
        val filledColumnTransformer1 = tableView1["A"][ColumnTransformer]

        val rowTransformerFunction: Row.() -> Unit = { }
        val emptyRowTransformer1 = tableView1[1][RowTransformer].also { it(rowTransformerFunction) }
        val filledRowTransformer1 = tableView1[1][RowTransformer]

        val cellTransformerFunction: Cell<*>.() -> Unit = { }
        val emptyCellTransformer1 = tableView1["A", 1][CellTransformer].also { it(cellTransformerFunction) }
        val filledCellTransformer1 = tableView1["A", 1][CellTransformer]

        val emptyResources1 = tableView1[Resource["/A"]].also { it(handler1) }
        val filledResources1 = tableView1[Resource["/A"]]

        val unitHorizontal1 = tableView1["A"][Position].also { it(Position.Left) }
        val leftHorizontal1 = tableView1["A"][Position].also { it(Position.Right) }
        val rightHorizontal1 = tableView1["A"][Position]

        val unitVertical1 = tableView1[1][Position].also { it(Position.Top) }
        val topVertical1 = tableView1[1][Position].also { it(Position.Bottom) }
        val bottomVertical1 = tableView1[1][Position]

        val unitVisibility1 = tableView1[1][Visibility].also { it(Visibility.Show) }
        val showVisibility1 = tableView1[1][Visibility].also { it(Visibility.Hide) }
        val hideVisibility1 = tableView1[1][Visibility]

        val tableView2 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}" + " 2"]

        val unitCellHeight2 = tableView2[CellHeight].also { it(100) }
        val pixelCellHeight2 = tableView2[CellHeight]

        val unitCellWidth2 = tableView2[CellWidth].also { it(100) }
        val pixelCellWidth2 = tableView2[CellWidth]

        val emptyCellClasses2 = tableView2[CellClasses].also { it(listOf("B", "A")) }
        val filledCellClasses2 = tableView2[CellClasses]

        val emptyCellTopics2 = tableView2[CellTopics].also { it (listOf("B", "A")) }
        val filledCellTopics2 = tableView2[CellTopics]

        val emptyTableTransformer2 = tableView2[TableTransformer].also { it(tableTransformerFunction) }
        val filledTableTransformer2 = tableView2[TableTransformer]

        val emptyColumnTransformer2 = tableView2["A"][ColumnTransformer].also { it(columnTransformerFunction) }
        val filledColumnTransformer2 = tableView2["A"][ColumnTransformer]

        val emptyRowTransformer2 = tableView2[1][RowTransformer].also { it(rowTransformerFunction) }
        val filledRowTransformer2 = tableView2[1][RowTransformer]

        val emptyCellTransformer2 = tableView2["A", 1][CellTransformer].also { it(cellTransformerFunction) }
        val filledCellTransformer2 = tableView2["A", 1][CellTransformer]

        val emptyResources2 = tableView2[Resource["A"]].also { it(handler1) }
        val filledResources2 = tableView2[Resource["A"]]

        val unitHorizontal2 = tableView2["A"][Position].also { it(Position.Left) }
        val leftHorizontal2 = tableView2["A"][Position].also { it(Position.Right) }
        val rightHorizontal2 = tableView2["A"][Position]

        val unitVertical2 = tableView2[1][Position].also { it(Position.Top) }
        val topVertical2 = tableView2[1][Position].also { it(Position.Bottom) }
        val bottomVertical2 = tableView2[1][Position]

        val unitVisibility2 = tableView2[1][Visibility].also { it(Visibility.Show) }
        val showVisibility2 = tableView2[1][Visibility].also { it(Visibility.Hide) }
        val hideVisibility2 = tableView2[1][Visibility]

        assertEquals(tableView1[CellHeight], tableView1[CellHeight])
        assertNotEquals(unitCellHeight1, unitCellHeight2)
        assertEquals(unitCellHeight1.height, unitCellHeight2.height)
        assertTrue(unitCellHeight1 in unitCellHeight2)
        assertTrue(unitCellHeight2 in unitCellHeight1)

        assertNotEquals(pixelCellHeight1, pixelCellHeight2)
        assertEquals(pixelCellHeight1.height, pixelCellHeight2.height)
        assertTrue(pixelCellHeight1 in pixelCellHeight2)
        assertTrue(pixelCellHeight2 in pixelCellHeight1)

        assertTrue(Unit in unitCellHeight1)
        assertTrue(100 in pixelCellHeight1)
        assertTrue(100L in pixelCellHeight2)

        assertEquals(tableView1[CellWidth], tableView1[CellWidth])
        assertNotEquals(unitCellWidth1, unitCellWidth2)
        assertEquals(unitCellWidth1.width, unitCellWidth2.width)
        assertTrue(unitCellWidth1 in unitCellWidth2)
        assertTrue(unitCellWidth2 in unitCellWidth1)

        assertNotEquals(pixelCellWidth1, pixelCellWidth2)
        assertEquals(pixelCellWidth1.width, pixelCellWidth2.width)
        assertTrue(pixelCellWidth1 in pixelCellWidth2)
        assertTrue(pixelCellWidth2 in pixelCellWidth1)

        assertTrue(Unit in unitCellWidth1)
        assertTrue(100 in pixelCellWidth1)
        assertTrue(100L in pixelCellWidth2)

        assertEquals(tableView1[CellClasses], tableView1[CellClasses])
        assertNotEquals(emptyCellClasses1, emptyCellClasses2)
        assertEquals(emptyCellClasses1.classes, emptyCellClasses2.classes)
        assertTrue(emptyCellClasses1 in emptyCellClasses2)
        assertTrue(emptyCellClasses2 in emptyCellClasses1)

        assertNotEquals(filledCellClasses1, filledCellClasses2)
        assertEquals(filledCellClasses1.classes, filledCellClasses2.classes)
        assertTrue(filledCellClasses1 in filledCellClasses2)
        assertTrue(filledCellClasses2 in filledCellClasses1)

        assertTrue(emptyCellClasses1 in filledCellClasses1)
        assertTrue(filledCellClasses1 !in emptyCellClasses1)

        assertTrue("A" in filledCellClasses1)
        assertFalse("A" in emptyCellClasses1)
        assertFalse("C" in filledCellClasses1)
        assertTrue(setOf("A", "B") in filledCellClasses1)
        assertTrue(listOf("A", "B") in filledCellClasses1)
        assertFalse(setOf("A", "B", "C") in filledCellClasses1)
        assertFalse(listOf("A", "B", "C") in filledCellClasses1)

        assertEquals(tableView1[CellTopics], tableView1[CellTopics])
        assertNotEquals(emptyCellTopics1, emptyCellTopics2)
        assertEquals(emptyCellTopics1.topics, emptyCellTopics2.topics)
        assertTrue(emptyCellTopics1 in emptyCellTopics2)
        assertTrue(emptyCellTopics2 in emptyCellTopics1)

        assertNotEquals(filledCellTopics1, filledCellTopics2)
        assertEquals(filledCellTopics1.topics, filledCellTopics2.topics)
        assertTrue(filledCellTopics1 in filledCellTopics2)
        assertTrue(filledCellTopics2 in filledCellTopics1)

        assertTrue(emptyCellTopics1 in filledCellTopics1)
        assertTrue(filledCellTopics1 !in emptyCellTopics1)

        assertTrue("A" in filledCellTopics1)
        assertFalse("A" in emptyCellTopics1)
        assertFalse("C" in filledCellTopics1)
        assertTrue(setOf("A", "B") in filledCellTopics1)
        assertTrue(listOf("A", "B") in filledCellTopics1)
        assertFalse(setOf("A", "B", "C") in filledCellTopics1)
        assertFalse(listOf("A", "B", "C") in filledCellTopics1)

        assertEquals(tableView1[TableTransformer], tableView1[TableTransformer])
        assertNotEquals(emptyTableTransformer1, emptyTableTransformer2)
        assertEquals(emptyTableTransformer1.function, emptyTableTransformer2.function)
        assertTrue(emptyTableTransformer1 in emptyTableTransformer2)
        assertTrue(emptyTableTransformer2 in emptyTableTransformer1)
        assertFalse(emptyTableTransformer1 in filledTableTransformer1)
        assertTrue(Unit in emptyTableTransformer2)

        assertNotEquals(filledTableTransformer1, filledTableTransformer2)
        assertEquals(filledTableTransformer1.function, filledTableTransformer2.function)
        assertTrue(filledTableTransformer1 in filledTableTransformer2)
        assertTrue(filledTableTransformer2 in filledTableTransformer1)
        assertTrue(tableTransformerFunction in filledTableTransformer1)
        assertTrue((filledTableTransformer1.function as? Table.() -> Unit)!! in filledTableTransformer2)

        assertEquals(tableView1["A"][ColumnTransformer], tableView1["A"][ColumnTransformer])
        assertNotEquals(emptyColumnTransformer1, emptyColumnTransformer2)
        assertEquals(emptyColumnTransformer1.function, emptyColumnTransformer2.function)
        assertTrue(emptyColumnTransformer1 in emptyColumnTransformer2)
        assertTrue(emptyColumnTransformer2 in emptyColumnTransformer1)
        assertFalse(emptyColumnTransformer1 in filledColumnTransformer1)
        assertTrue(Unit in emptyColumnTransformer2)

        assertNotEquals(filledColumnTransformer1, filledColumnTransformer2)
        assertEquals(filledColumnTransformer1.function, filledColumnTransformer2.function)
        assertTrue(filledColumnTransformer1 in filledColumnTransformer2)
        assertTrue(filledColumnTransformer2 in filledColumnTransformer1)
        assertTrue(columnTransformerFunction in filledColumnTransformer1)
        assertTrue((filledColumnTransformer1.function as? Column.() -> Unit)!! in filledColumnTransformer2)

        assertEquals(tableView1[1][RowTransformer], tableView1[1][RowTransformer])
        assertNotEquals(emptyRowTransformer1, emptyRowTransformer2)
        assertEquals(emptyRowTransformer1.function, emptyRowTransformer2.function)
        assertTrue(emptyRowTransformer1 in emptyRowTransformer2)
        assertTrue(emptyRowTransformer2 in emptyRowTransformer1)
        assertFalse(emptyRowTransformer1 in filledRowTransformer1)
        assertTrue(Unit in emptyRowTransformer2)

        assertNotEquals(filledRowTransformer1, filledRowTransformer2)
        assertEquals(filledRowTransformer1.function, filledRowTransformer2.function)
        assertTrue(filledRowTransformer1 in filledRowTransformer2)
        assertTrue(filledRowTransformer2 in filledRowTransformer1)
        assertTrue(rowTransformerFunction in filledRowTransformer1)
        assertTrue((filledRowTransformer1.function as? Row.() -> Unit)!! in filledRowTransformer2)

        assertEquals(tableView1["A", 1][CellTransformer], tableView1["A", 1][CellTransformer])
        assertNotEquals(emptyCellTransformer1, emptyCellTransformer2)
        assertEquals(emptyCellTransformer1.function, emptyCellTransformer2.function)
        assertTrue(emptyCellTransformer1 in emptyCellTransformer2)
        assertTrue(emptyCellTransformer2 in emptyCellTransformer1)
        assertFalse(emptyCellTransformer1 in filledCellTransformer1)
        assertTrue(Unit in emptyCellTransformer2)

        assertNotEquals(filledCellTransformer1, filledCellTransformer2)
        assertEquals(filledCellTransformer1.function, filledCellTransformer2.function)
        assertTrue(filledCellTransformer1 in filledCellTransformer2)
        assertTrue(filledCellTransformer2 in filledCellTransformer1)
        assertTrue(cellTransformerFunction in filledCellTransformer1)
        assertTrue((filledCellTransformer1.function as? Cell<*>.() -> Unit)!! in filledCellTransformer2)

        assertNotEquals(emptyResources1, emptyResources2)
        assertEquals(emptyResources1.path, emptyResources2.path)
        assertEquals(emptyResources1.handler, emptyResources2.handler)
        assertTrue(emptyResources1 in emptyResources2)
        assertEquals(Resource["/test"], Resource["/test"])
        assertNotEquals<Resource<*, *>>(TableView[null][Resource["/test"]], Resource["/test"]) // Different source
        assertNotEquals<Resource<*, *>>(Resource["/test"].also { it(handler1) }, Resource["/test"]) // Different resource
        assertNotEquals<Resource<*, *>>(Resource["/test-2"], Resource["/test-3"]) // Different path
        assertNotEquals<Resource<*, *>>(Resource["/test-3"].also { it(handler2) }, Resource["/test-3"]) // Different handler

        assertNotEquals(filledResources1, filledResources2)
        assertEquals(filledResources1.path, filledResources2.path)
        assertEquals(filledResources1.handler, filledResources2.handler)

        assertFalse(emptyResources1 in filledResources1)
        assertTrue(filledCellClasses1 !in emptyCellClasses1)
        assertTrue(filledCellClasses1 in filledCellClasses2)

        assertFalse("A" to handler1 in emptyResources1)
        assertTrue("A" in emptyResources1)
        assertTrue("A" to handler1 in filledResources1)
        assertTrue(handler1 in filledResources1)
        assertFalse(handler2 in filledResources1)
        assertFalse("A" to handler2 in filledResources2)
        assertFalse("A" to handler2 in emptyResources2)
        assertEquals(mapOf("A" to handler1), tableView2.resources)

        assertEquals(tableView1["A"][Position], tableView1["A"][Position])
        assertNotEquals(unitHorizontal1, unitHorizontal2)
        assertEquals(unitHorizontal1.position, unitHorizontal2.position)
        assertTrue(unitHorizontal1 in unitHorizontal2)
        assertTrue(unitHorizontal2 in unitHorizontal1)

        assertNotEquals(leftHorizontal1, leftHorizontal2)
        assertEquals(leftHorizontal1.position, leftHorizontal2.position)
        assertTrue(leftHorizontal1 in leftHorizontal2)
        assertTrue(leftHorizontal2 in leftHorizontal1)

        assertEquals(tableView1[1][Position], tableView1[1][Position])
        assertNotEquals(topVertical1, topVertical2)
        assertEquals(topVertical1.position, topVertical2.position)
        assertTrue(topVertical1 in topVertical2)
        assertTrue(topVertical2 in topVertical1)

        assertTrue(Unit in unitHorizontal1)
        assertTrue(Position.Value.LEFT in leftHorizontal1)
        assertTrue(Position.Left in leftHorizontal2)
        assertTrue(Position.Value.TOP in topVertical1)
        assertTrue(Position.Top in topVertical2)

        assertEquals(tableView1[1][Visibility], tableView1[1][Visibility])
        assertNotEquals(unitVisibility1, unitVisibility2)
        assertEquals(unitVisibility1.visibility, unitVisibility2.visibility)
        assertTrue(unitVisibility1 in unitVisibility2)
        assertTrue(unitVisibility2 in unitVisibility1)

        assertNotEquals(showVisibility1, showVisibility2)
        assertEquals(showVisibility1.visibility, showVisibility2.visibility)
        assertTrue(showVisibility1 in showVisibility2)
        assertTrue(showVisibility2 in showVisibility1)

        assertNotEquals(hideVisibility1, hideVisibility2)
        assertEquals(hideVisibility1.visibility, hideVisibility2.visibility)
        assertTrue(hideVisibility1 in hideVisibility2)
        assertTrue(hideVisibility2 in hideVisibility1)

        assertTrue(Unit in unitVertical1)
        assertTrue(Visibility.Value.SHOW in showVisibility1)
        assertTrue(Visibility.Value.HIDE in hideVisibility1)

        assertEquals(unitCellHeight1.hashCode(), unitCellHeight2.hashCode())
        assertEquals(pixelCellHeight1.hashCode(), pixelCellHeight2.hashCode())
        assertEquals(unitCellWidth1.hashCode(), unitCellWidth2.hashCode())
        assertEquals(pixelCellWidth1.hashCode(), pixelCellWidth2.hashCode())
        assertEquals(emptyCellClasses1.hashCode(), emptyCellClasses2.hashCode())
        assertEquals(filledCellClasses1.hashCode(), filledCellClasses2.hashCode())
        assertEquals(emptyCellTopics1.hashCode(), emptyCellTopics2.hashCode())
        assertEquals(filledCellTopics1.hashCode(), filledCellTopics2.hashCode())
        assertEquals(emptyTableTransformer1.hashCode(), emptyTableTransformer2.hashCode())
        assertEquals(filledTableTransformer1.hashCode(), filledTableTransformer2.hashCode())
        assertEquals(emptyColumnTransformer1.hashCode(), emptyColumnTransformer2.hashCode())
        assertEquals(filledColumnTransformer1.hashCode(), filledColumnTransformer2.hashCode())
        assertEquals(emptyRowTransformer1.hashCode(), emptyRowTransformer2.hashCode())
        assertEquals(filledRowTransformer1.hashCode(), filledRowTransformer2.hashCode())
        assertEquals(emptyCellTransformer1.hashCode(), emptyCellTransformer2.hashCode())
        assertEquals(filledCellTransformer1.hashCode(), filledCellTransformer2.hashCode())
        assertEquals(emptyResources1.hashCode(), emptyResources2.hashCode())
        assertEquals(filledResources1.hashCode(), filledResources2.hashCode())
        assertEquals(unitHorizontal1.hashCode(), unitHorizontal2.hashCode())
        assertEquals(unitVertical1.hashCode(), unitVertical2.hashCode())
        assertEquals(unitVertical1.hashCode(), unitHorizontal1.hashCode())
        assertEquals(leftHorizontal1.hashCode(), leftHorizontal2.hashCode())
        assertEquals(rightHorizontal1.hashCode(), rightHorizontal2.hashCode())
        assertEquals(topVertical1.hashCode(), topVertical2.hashCode())
        assertEquals(bottomVertical1.hashCode(), bottomVertical2.hashCode())
        assertEquals(unitVisibility1.hashCode(), unitVisibility2.hashCode())
        assertEquals(showVisibility1.hashCode(), showVisibility2.hashCode())
        assertEquals(hideVisibility1.hashCode(), hideVisibility2.hashCode())
    }

    @Test
    fun events() {
        val name = "${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"

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

        batch(table) {
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

    @Test
    fun `values in cells`() {
        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["Unit", 0] = Unit
        table["Boolean", 0] = true
        table["String", 0] = "string"
        table["Long", 0] = 100L
        table["Double", 0] = 200.0
        table["BigInteger", 0] = BigInteger.TEN
        table["BigDecimal", 0] = BigDecimal.ONE
        table["LocalDate", 0] = LocalDate.now()
        table["LocalTime", 0] = LocalTime.now()
        table["LocalDateTime", 0] = LocalDateTime.now()
        table["ZonedDateTime", 0] = ZonedDateTime.now()

        val unit = table["Unit", 0]
        val boolean = table["Boolean", 0]
        val string = table["String", 0]
        val long = table["Long", 0]
        val double = table["Double", 0]
        val bigint = table["BigInteger", 0]
        val bigdec = table["BigDecimal", 0]
        val localdate = table["LocalDate", 0]
        val localtime = table["LocalTime", 0]
        val localdatetime = table["LocalDateTime", 0]
        val zoneddatetime = table["ZonedDateTime", 0]

        // Most of these are false because UnitCells aren't included in iterators
        assertFalse(unit in table)
        assertFalse(unit in table[0])
        assertFalse(unit in table["Unit"])
        assertTrue(unit in table["Unit", 0])

        assertTrue(boolean in table)
        assertTrue(true in table)
        assertTrue(boolean in table[0])
        assertTrue(true in table[0])
        assertTrue(boolean in table["Boolean"])
        assertTrue(true in table["Boolean"])
        assertTrue(boolean in table["Boolean", 0])
        assertTrue(true in table["Boolean", 0])

        assertTrue(string in table)
        assertTrue("string" in table)
        assertTrue(string in table[0])
        assertTrue("string" in table[0])
        assertTrue(string in table["String"])
        assertTrue("string" in table["String"])
        assertTrue(string in table["String", 0])
        assertTrue("string" in table["String", 0])

        assertTrue(long in table)
        assertTrue(100L in table)
        assertTrue(long in table[0])
        assertTrue(100L in table[0])
        assertTrue(long in table["Long"])
        assertTrue(100L in table["Long"])
        assertTrue(long in table["Long", 0])
        assertTrue(100L in table["Long", 0])

        assertTrue(100 in table)
        assertTrue(100 in table[0])
        assertTrue(100 in table["Long"])
        assertTrue(100 in table["Long", 0])

        assertTrue(double in table)
        assertTrue(200.0 in table)
        assertTrue(double in table[0])
        assertTrue(200.0 in table[0])
        assertTrue(double in table["Double"])
        assertTrue(200.0 in table["Double"])
        assertTrue(double in table["Double", 0])
        assertTrue(200.0 in table["Double", 0])

        assertTrue(200F in table)
        assertTrue(200F in table[0])
        assertTrue(200F in table["Double"])
        assertTrue(200F in table["Double", 0])

        assertTrue(bigint in table)
        assertTrue(BigInteger.TEN in table)
        assertTrue(bigint in table[0])
        assertTrue(BigInteger.TEN in table[0])
        assertTrue(bigint in table["BigInteger"])
        assertTrue(BigInteger.TEN in table["BigInteger"])
        assertTrue(bigint in table["BigInteger", 0])
        assertTrue(BigInteger.TEN in table["BigInteger", 0])

        assertTrue(10 in table)
        assertTrue(10 in table[0])
        assertTrue(10 in table["BigInteger"])
        assertTrue(10 in table["BigInteger", 0])

        assertTrue(bigdec in table)
        assertTrue(BigDecimal.ONE in table)
        assertTrue(bigdec in table[0])
        assertTrue(BigDecimal.ONE in table[0])
        assertTrue(bigdec in table["BigDecimal"])
        assertTrue(BigDecimal.ONE in table["BigDecimal"])
        assertTrue(bigdec in table["BigDecimal", 0])
        assertTrue(BigDecimal.ONE in table["BigDecimal", 0])

        assertTrue(1 in table)
        assertTrue(1 in table[0])
        assertTrue(1 in table["BigDecimal"])
        assertTrue(1 in table["BigDecimal", 0])

        assertTrue(localdate in table)
        assertTrue(localdate.value as LocalDate in table)
        assertTrue(localdate in table[0])
        assertTrue(localdate.value as LocalDate in table[0])
        assertTrue(localdate in table["LocalDate"])
        assertTrue(localdate.value as LocalDate in table["LocalDate"])
        assertTrue(localdate in table["LocalDate", 0])
        assertTrue(localdate.value as LocalDate in table["LocalDate", 0])

        assertTrue(localtime in table)
        assertTrue(localtime.value as LocalTime in table)
        assertTrue(localtime in table[0])
        assertTrue(localtime.value as LocalTime in table[0])
        assertTrue(localtime in table["LocalTime"])
        assertTrue(localtime.value as LocalTime in table["LocalTime"])
        assertTrue(localtime in table["LocalTime", 0])
        assertTrue(localtime.value as LocalTime in table["LocalTime", 0])

        assertTrue(localdatetime in table)
        assertTrue(localdatetime.value as LocalDateTime in table)
        assertTrue(localdatetime in table[0])
        assertTrue(localdatetime.value as LocalDateTime in table[0])
        assertTrue(localdatetime in table["LocalDateTime"])
        assertTrue(localdatetime.value as LocalDateTime in table["LocalDateTime"])
        assertTrue(localdatetime in table["LocalDateTime", 0])
        assertTrue(localdatetime.value as LocalDateTime in table["LocalDateTime", 0])

        assertTrue(zoneddatetime in table)
        assertTrue(zoneddatetime.value as ZonedDateTime in table)
        assertTrue(zoneddatetime in table[0])
        assertTrue(zoneddatetime.value as ZonedDateTime in table[0])
        assertTrue(zoneddatetime in table["ZonedDateTime"])
        assertTrue(zoneddatetime.value as ZonedDateTime in table["ZonedDateTime"])
        assertTrue(zoneddatetime in table["ZonedDateTime", 0])
        assertTrue(zoneddatetime.value as ZonedDateTime in table["ZonedDateTime", 0])
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
