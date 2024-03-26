/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.AfterClass

class TableViewAccessorsTest {
    @Test
    fun `cellview accessors`() {
        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val row = table[Long.MIN_VALUE]

        val tableView = TableView[table]
        val rowView = tableView[row]

        tableView["L1", 1][CellHeight] = 1000
        tableView["L1", 1][CellWidth] = 2000
        tableView["L1", 1][CellClasses] = "cc-1"
        tableView["L1", 1][CellTopics] = "ct-1"
        val ct: Cell<*>.() -> Unit = {}
        tableView["L1", 1][CellTransformer] = ct

        val sourceCellView = tableView["L1", 1]

        fun compare(cellView: CellView) {
            assertEquals(1000L, sourceCellView[CellHeight].height)
            assertEquals(2000L, sourceCellView[CellWidth].width)
            assertEquals(setOf("cc-1"), sourceCellView[CellClasses].classes)
            assertEquals(setOf("ct-1"), sourceCellView[CellTopics].topics)
            assertEquals(ct, sourceCellView[CellTransformer].function)

            assertEquals(sourceCellView[CellHeight].height, cellView[CellHeight].height)
            assertEquals(sourceCellView[CellWidth].width, cellView[CellWidth].width)
            assertEquals(sourceCellView[CellClasses].classes, cellView[CellClasses].classes)
            assertEquals(sourceCellView[CellTopics].topics, cellView[CellTopics].topics)
            assertEquals(sourceCellView[CellTransformer].function, cellView[CellTransformer].function)

            clear(cellView)
        }

        tableView["L1", Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1", Int.MAX_VALUE])

        tableView["L1", Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1", Long.MAX_VALUE])

        tableView["L1", row] = sourceCellView
        compare(tableView["L1", row])

        tableView["L1", rowView] = sourceCellView
        compare(tableView["L1", rowView])

        tableView["L1"][Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1"][Int.MAX_VALUE])

        tableView["L1"][Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1"][Long.MAX_VALUE])

        tableView["L1"][row] = sourceCellView
        compare(tableView["L1"][row])

        tableView["L1"][rowView] = sourceCellView
        compare(tableView["L1"][rowView])

        tableView[Int.MAX_VALUE]["L1"] = sourceCellView
        compare(tableView[Int.MAX_VALUE]["L1"])

        tableView[Long.MAX_VALUE]["L1"] = sourceCellView
        compare(tableView[Long.MAX_VALUE]["L1"])

        tableView[row]["L1"] = sourceCellView
        compare(tableView[row]["L1"])

        tableView[rowView]["L1"] = sourceCellView
        compare(tableView[rowView]["L1"])

        tableView["L1", "L2", Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", Int.MAX_VALUE])

        tableView["L1", "L2", Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", Long.MAX_VALUE])

        tableView["L1", "L2", row] = sourceCellView
        compare(tableView["L1", "L2", row])

        tableView["L1", "L2", rowView] = sourceCellView
        compare(tableView["L1", "L2", rowView])

        tableView["L1", "L2"][Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2"][Int.MAX_VALUE])

        tableView["L1", "L2"][Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2"][Long.MAX_VALUE])

        tableView["L1", "L2"][row] = sourceCellView
        compare(tableView["L1", "L2"][row])

        tableView["L1", "L2"][rowView] = sourceCellView
        compare(tableView["L1", "L2"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2"] = sourceCellView
        compare(tableView[Int.MAX_VALUE]["L1", "L2"])

        tableView[Long.MAX_VALUE]["L1", "L2"] = sourceCellView
        compare(tableView[Long.MAX_VALUE]["L1", "L2"])

        tableView[row]["L1", "L2"] = sourceCellView
        compare(tableView[row]["L1", "L2"])

        tableView[rowView]["L1", "L2"] = sourceCellView
        compare(tableView[rowView]["L1", "L2"])

        tableView["L1", "L2", "L3", Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", row] = sourceCellView
        compare(tableView["L1", "L2", "L3", row])

        tableView["L1", "L2", "L3", rowView] = sourceCellView
        compare(tableView["L1", "L2", "L3", rowView])

        tableView["L1", "L2", "L3"][Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3"][Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3"][row] = sourceCellView
        compare(tableView["L1", "L2", "L3"][row])

        tableView["L1", "L2", "L3"][rowView] = sourceCellView
        compare(tableView["L1", "L2", "L3"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3"] = sourceCellView
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3"] = sourceCellView
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3"])

        tableView[row]["L1", "L2", "L3"] = sourceCellView
        compare(tableView[row]["L1", "L2", "L3"])

        tableView[rowView]["L1", "L2", "L3"] = sourceCellView
        compare(tableView[rowView]["L1", "L2", "L3"])

        tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", row] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", row])

        tableView["L1", "L2", "L3", "L4", rowView] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", rowView])

        tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][row] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4"][row])

        tableView["L1", "L2", "L3", "L4"][rowView] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = sourceCellView
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = sourceCellView
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[row]["L1", "L2", "L3", "L4"] = sourceCellView
        compare(tableView[row]["L1", "L2", "L3", "L4"])

        tableView[rowView]["L1", "L2", "L3", "L4"] = sourceCellView
        compare(tableView[rowView]["L1", "L2", "L3", "L4"])

        tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", row] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", "L5", row])

        tableView["L1", "L2", "L3", "L4", "L5", rowView] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", "L5", rowView])

        tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][row] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", "L5"][row])

        tableView["L1", "L2", "L3", "L4", "L5"][rowView] = sourceCellView
        compare(tableView["L1", "L2", "L3", "L4", "L5"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = sourceCellView
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = sourceCellView
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[row]["L1", "L2", "L3", "L4", "L5"] = sourceCellView
        compare(tableView[row]["L1", "L2", "L3", "L4", "L5"])

        tableView[rowView]["L1", "L2", "L3", "L4", "L5"] = sourceCellView
        compare(tableView[rowView]["L1", "L2", "L3", "L4", "L5"])

        tableView["L1", Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", Int.MAX_VALUE])

        tableView["L1", Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", Long.MAX_VALUE])

        tableView["L1", row] = { this(sourceCellView) }
        compare(tableView["L1", row])

        tableView["L1", rowView] = { this(sourceCellView) }
        compare(tableView["L1", rowView])

        tableView["L1"][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1"][Int.MAX_VALUE])

        tableView["L1"][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1"][Long.MAX_VALUE])

        tableView["L1"][row] = { this(sourceCellView) }
        compare(tableView["L1"][row])

        tableView["L1"][rowView] = { this(sourceCellView) }
        compare(tableView["L1"][rowView])

        tableView[Int.MAX_VALUE]["L1"] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE]["L1"])

        tableView[Long.MAX_VALUE]["L1"] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE]["L1"])

        tableView[row]["L1"] = { this(sourceCellView) }
        compare(tableView[row]["L1"])

        tableView[rowView]["L1"] = { this(sourceCellView) }
        compare(tableView[rowView]["L1"])

        tableView["L1", "L2", Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", Int.MAX_VALUE])

        tableView["L1", "L2", Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", Long.MAX_VALUE])

        tableView["L1", "L2", row] = { this(sourceCellView) }
        compare(tableView["L1", "L2", row])

        tableView["L1", "L2", rowView] = { this(sourceCellView) }
        compare(tableView["L1", "L2", rowView])

        tableView["L1", "L2"][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2"][Int.MAX_VALUE])

        tableView["L1", "L2"][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2"][Long.MAX_VALUE])

        tableView["L1", "L2"][row] = { this(sourceCellView) }
        compare(tableView["L1", "L2"][row])

        tableView["L1", "L2"][rowView] = { this(sourceCellView) }
        compare(tableView["L1", "L2"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2"] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE]["L1", "L2"])

        tableView[Long.MAX_VALUE]["L1", "L2"] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE]["L1", "L2"])

        tableView[row]["L1", "L2"] = { this(sourceCellView) }
        compare(tableView[row]["L1", "L2"])

        tableView[rowView]["L1", "L2"] = { this(sourceCellView) }
        compare(tableView[rowView]["L1", "L2"])

        tableView["L1", "L2", "L3", Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", row] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", row])

        tableView["L1", "L2", "L3", rowView] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", rowView])

        tableView["L1", "L2", "L3"][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3"][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3"][row] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3"][row])

        tableView["L1", "L2", "L3"][rowView] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3"])

        tableView[row]["L1", "L2", "L3"] = { this(sourceCellView) }
        compare(tableView[row]["L1", "L2", "L3"])

        tableView[rowView]["L1", "L2", "L3"] = { this(sourceCellView) }
        compare(tableView[rowView]["L1", "L2", "L3"])

        tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", row] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", row])

        tableView["L1", "L2", "L3", "L4", rowView] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", rowView])

        tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][row] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4"][row])

        tableView["L1", "L2", "L3", "L4"][rowView] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[row]["L1", "L2", "L3", "L4"] = { this(sourceCellView) }
        compare(tableView[row]["L1", "L2", "L3", "L4"])

        tableView[rowView]["L1", "L2", "L3", "L4"] = { this(sourceCellView) }
        compare(tableView[rowView]["L1", "L2", "L3", "L4"])

        tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", row] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", "L5", row])

        tableView["L1", "L2", "L3", "L4", "L5", rowView] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", "L5", rowView])

        tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][row] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][row])

        tableView["L1", "L2", "L3", "L4", "L5"][rowView] = { this(sourceCellView) }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[row]["L1", "L2", "L3", "L4", "L5"] = { this(sourceCellView) }
        compare(tableView[row]["L1", "L2", "L3", "L4", "L5"])

        tableView[rowView]["L1", "L2", "L3", "L4", "L5"] = { this(sourceCellView) }
        compare(tableView[rowView]["L1", "L2", "L3", "L4", "L5"])

        tableView["L1", Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1", Int.MAX_VALUE])

        tableView["L1", Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1", Long.MAX_VALUE])

        tableView["L1", row](sourceCellView)
        compare(tableView["L1", row])

        tableView["L1", rowView](sourceCellView)
        compare(tableView["L1", rowView])

        tableView["L1"][Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1"][Int.MAX_VALUE])

        tableView["L1"][Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1"][Long.MAX_VALUE])

        tableView["L1"][row](sourceCellView)
        compare(tableView["L1"][row])

        tableView["L1"][rowView](sourceCellView)
        compare(tableView["L1"][rowView])

        tableView[Int.MAX_VALUE]["L1"](sourceCellView)
        compare(tableView[Int.MAX_VALUE]["L1"])

        tableView[Long.MAX_VALUE]["L1"](sourceCellView)
        compare(tableView[Long.MAX_VALUE]["L1"])

        tableView[row]["L1"](sourceCellView)
        compare(tableView[row]["L1"])

        tableView[rowView]["L1"](sourceCellView)
        compare(tableView[rowView]["L1"])

        tableView["L1", "L2", Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", Int.MAX_VALUE])

        tableView["L1", "L2", Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", Long.MAX_VALUE])

        tableView["L1", "L2", row](sourceCellView)
        compare(tableView["L1", "L2", row])

        tableView["L1", "L2", rowView](sourceCellView)
        compare(tableView["L1", "L2", rowView])

        tableView["L1", "L2"][Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2"][Int.MAX_VALUE])

        tableView["L1", "L2"][Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2"][Long.MAX_VALUE])

        tableView["L1", "L2"][row](sourceCellView)
        compare(tableView["L1", "L2"][row])

        tableView["L1", "L2"][rowView](sourceCellView)
        compare(tableView["L1", "L2"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2"](sourceCellView)
        compare(tableView[Int.MAX_VALUE]["L1", "L2"])

        tableView[Long.MAX_VALUE]["L1", "L2"](sourceCellView)
        compare(tableView[Long.MAX_VALUE]["L1", "L2"])

        tableView[row]["L1", "L2"](sourceCellView)
        compare(tableView[row]["L1", "L2"])

        tableView[rowView]["L1", "L2"](sourceCellView)
        compare(tableView[rowView]["L1", "L2"])

        tableView["L1", "L2", "L3", Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", row](sourceCellView)
        compare(tableView["L1", "L2", "L3", row])

        tableView["L1", "L2", "L3", rowView](sourceCellView)
        compare(tableView["L1", "L2", "L3", rowView])

        tableView["L1", "L2", "L3"][Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3"][Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3"][row](sourceCellView)
        compare(tableView["L1", "L2", "L3"][row])

        tableView["L1", "L2", "L3"][rowView](sourceCellView)
        compare(tableView["L1", "L2", "L3"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3"](sourceCellView)
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3"](sourceCellView)
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3"])

        tableView[row]["L1", "L2", "L3"](sourceCellView)
        compare(tableView[row]["L1", "L2", "L3"])

        tableView[rowView]["L1", "L2", "L3"](sourceCellView)
        compare(tableView[rowView]["L1", "L2", "L3"])

        tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", row](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", row])

        tableView["L1", "L2", "L3", "L4", rowView](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", rowView])

        tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][row](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4"][row])

        tableView["L1", "L2", "L3", "L4"][rowView](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](sourceCellView)
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](sourceCellView)
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[row]["L1", "L2", "L3", "L4"](sourceCellView)
        compare(tableView[row]["L1", "L2", "L3", "L4"])

        tableView[rowView]["L1", "L2", "L3", "L4"](sourceCellView)
        compare(tableView[rowView]["L1", "L2", "L3", "L4"])

        tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", row](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", "L5", row])

        tableView["L1", "L2", "L3", "L4", "L5", rowView](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", "L5", rowView])

        tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][row](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", "L5"][row])

        tableView["L1", "L2", "L3", "L4", "L5"][rowView](sourceCellView)
        compare(tableView["L1", "L2", "L3", "L4", "L5"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](sourceCellView)
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](sourceCellView)
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[row]["L1", "L2", "L3", "L4", "L5"](sourceCellView)
        compare(tableView[row]["L1", "L2", "L3", "L4", "L5"])

        tableView[rowView]["L1", "L2", "L3", "L4", "L5"](sourceCellView)
        compare(tableView[rowView]["L1", "L2", "L3", "L4", "L5"])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], row] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][row] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = sourceCellView
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = sourceCellView
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5"]] = sourceCellView
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]] = sourceCellView
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceCellView
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceCellView
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceCellView
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceCellView
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceCellView
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceCellView
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceCellView
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceCellView
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView] = sourceCellView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = sourceCellView
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = sourceCellView
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = sourceCellView
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = sourceCellView
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView] = sourceCellView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceCellView
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceCellView
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceCellView
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceCellView
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView] = sourceCellView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceCellView
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceCellView
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceCellView
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceCellView
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], row] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][row] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5"]] = { this(sourceCellView) }
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]] = { this(sourceCellView) }
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(sourceCellView) }
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(sourceCellView) }
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(sourceCellView) }
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(sourceCellView) }
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView] = { this(sourceCellView) }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(sourceCellView) }
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(sourceCellView) }
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView] = { this(sourceCellView) }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(sourceCellView) }
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(sourceCellView) }
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView] = { this(sourceCellView) }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(sourceCellView) }
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(sourceCellView) }
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(sourceCellView) }
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(sourceCellView) }
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], row](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][row](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](sourceCellView)
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](sourceCellView)
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5"]](sourceCellView)
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]](sourceCellView)
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](sourceCellView)
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](sourceCellView)
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]](sourceCellView)
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]](sourceCellView)
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]](sourceCellView)
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]](sourceCellView)
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]](sourceCellView)
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]](sourceCellView)
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView](sourceCellView)
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](sourceCellView)
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](sourceCellView)
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]](sourceCellView)
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]](sourceCellView)
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView](sourceCellView)
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](sourceCellView)
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](sourceCellView)
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](sourceCellView)
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](sourceCellView)
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView](sourceCellView)
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](sourceCellView)
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](sourceCellView)
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](sourceCellView)
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](sourceCellView)
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])
        tableView[tableView["L1", 1000]] = sourceCellView
        compare(tableView[tableView["L1", 1000]])

        tableView[tableView["L1", 1001]] = { this(sourceCellView) }
        compare(tableView[tableView["L1", 1001]])

        tableView[tableView["L1", 1002]](sourceCellView)
        compare(tableView[tableView["L1", 1002]])

        tableView[table["L1", 2000]] = sourceCellView
        compare(tableView[table["L1", 2000]])

        tableView[table["L1", 2001]] = { this(sourceCellView) }
        compare(tableView[table["L1", 2001]])

        tableView[table["L1", 2002]](sourceCellView)
        compare(tableView[table["L1", 2002]])
    }

    @Test
    fun `columnview accessors`() {
        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val tableView = TableView[table]

        tableView["A"][CellWidth] = 2000
        tableView["A"][CellClasses] = "cc-1"
        tableView["A"][CellTopics] = "ct-1"
        val ct: Column.() -> Unit = {}
        tableView["A"][ColumnTransformer] = ct

        val sourceColumnView = tableView["A"]

        fun compare(columnView: ColumnView) {
            assertEquals(2000L, sourceColumnView[CellWidth].width)
            assertEquals(setOf("cc-1"), sourceColumnView[CellClasses].classes)
            assertEquals(setOf("ct-1"), sourceColumnView[CellTopics].topics)
            assertEquals(ct, sourceColumnView[ColumnTransformer].function)

            assertEquals(sourceColumnView[CellWidth].width, columnView[CellWidth].width)
            assertEquals(sourceColumnView[CellClasses].classes, columnView[CellClasses].classes)
            assertEquals(sourceColumnView[CellTopics].topics, columnView[CellTopics].topics)
            assertEquals(sourceColumnView[ColumnTransformer].function, columnView[ColumnTransformer].function)

            clear(columnView)
        }

        tableView["L1"] = sourceColumnView
        compare(tableView["L1"])

        tableView["L1", "L2"] = sourceColumnView
        compare(tableView["L1", "L2"])

        tableView["L1", "L2", "L3"] = sourceColumnView
        compare(tableView["L1", "L2", "L3"])

        tableView["L1", "L2", "L3", "L4"] = sourceColumnView
        compare(tableView["L1", "L2", "L3", "L4"])

        tableView["L1", "L2", "L3", "L4", "L5"] = sourceColumnView
        compare(tableView["L1", "L2", "L3", "L4", "L5"])

        tableView["L1"] = { this { sourceColumnView } }
        compare(tableView["L1"])

        tableView["L1", "L2"] = { this { sourceColumnView } }
        compare(tableView["L1", "L2"])

        tableView["L1", "L2", "L3"] = { this { sourceColumnView } }
        compare(tableView["L1", "L2", "L3"])

        tableView["L1", "L2", "L3", "L4"] = { this { sourceColumnView } }
        compare(tableView["L1", "L2", "L3", "L4"])

        tableView["L1", "L2", "L3", "L4", "L5"] = { this { sourceColumnView } }
        compare(tableView["L1", "L2", "L3", "L4", "L5"])

        tableView["L1"] { sourceColumnView }
        compare(tableView["L1"])

        tableView["L1", "L2"] { sourceColumnView }
        compare(tableView["L1", "L2"])

        tableView["L1", "L2", "L3"] { sourceColumnView }
        compare(tableView["L1", "L2", "L3"])

        tableView["L1", "L2", "L3", "L4"] { sourceColumnView }
        compare(tableView["L1", "L2", "L3", "L4"])

        tableView["L1", "L2", "L3", "L4", "L5"] { sourceColumnView }
        compare(tableView["L1", "L2", "L3", "L4", "L5"])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]] = sourceColumnView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceColumnView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = sourceColumnView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]] = sourceColumnView
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceColumnView
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = sourceColumnView
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]] = { this { sourceColumnView } }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this { sourceColumnView } }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = { this { sourceColumnView } }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this { sourceColumnView } }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this { sourceColumnView } }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this { sourceColumnView } }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]] { sourceColumnView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceColumnView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceColumnView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]] { sourceColumnView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceColumnView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceColumnView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])
    }

    @Test
    fun `rowview accessors`() {
        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val tableView = TableView[table]

        tableView[1][CellHeight] = 1000
        tableView[1][CellClasses] = "cc-1"
        tableView[1][CellTopics] = "ct-1"
        val ct: Row.() -> Unit = {}
        tableView[1][RowTransformer] = ct

        val sourceRowView = tableView[1]

        fun compare(rowView: RowView) {
            assertEquals(1000L, sourceRowView[CellHeight].height)
            assertEquals(setOf("cc-1"), sourceRowView[CellClasses].classes)
            assertEquals(setOf("ct-1"), sourceRowView[CellTopics].topics)
            assertEquals(ct, sourceRowView[RowTransformer].function)

            assertEquals(sourceRowView[CellHeight].height, rowView[CellHeight].height)
            assertEquals(sourceRowView[CellClasses].classes, rowView[CellClasses].classes)
            assertEquals(sourceRowView[CellTopics].topics, rowView[CellTopics].topics)
            assertEquals(sourceRowView[RowTransformer].function, rowView[RowTransformer].function)

            clear(rowView)
        }

        tableView[Int.MAX_VALUE] = sourceRowView
        compare(tableView[Int.MAX_VALUE])

        tableView[Long.MAX_VALUE] = sourceRowView
        compare(tableView[Long.MAX_VALUE])

        tableView[table[2000]] = sourceRowView
        compare(tableView[table[2000]])

        tableView[tableView[3000]] = sourceRowView
        compare(tableView[tableView[3000]])

        tableView[Int.MAX_VALUE] = { this { sourceRowView } }
        compare(tableView[Int.MAX_VALUE])

        tableView[Long.MAX_VALUE] = { this { sourceRowView } }
        compare(tableView[Long.MAX_VALUE])

        tableView[table[2000]] = { this { sourceRowView } }
        compare(tableView[table[2000]])

        tableView[tableView[3000]] = { this { sourceRowView } }
        compare(tableView[tableView[3000]])

        tableView[Int.MAX_VALUE] { sourceRowView }
        compare(tableView[Int.MAX_VALUE])

        tableView[Long.MAX_VALUE] { sourceRowView }
        compare(tableView[Long.MAX_VALUE])

        tableView[table[2000]] { sourceRowView }
        compare(tableView[table[2000]])

        tableView[tableView[3000]] { sourceRowView }
        compare(tableView[tableView[3000]])
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}