/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.After

class TableViewAccessorsTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `cellview accessors`() {
        val table = Table[object {}.javaClass.enclosingMethod.name]
        val row = table[Long.MIN_VALUE]

        val tableView = TableView[table]
        val rowView = tableView[row]

        tableView["L1", 1][CellHeight] = 1000
        tableView["L1", 1][CellWidth] = 2000
        tableView["L1", 1][CellClasses] = "cc-1"
        tableView["L1", 1][CellTopics] = "ct-1"
        val ct: Cell<*>.() -> Any? = {}
        tableView["L1", 1][CellTransformer] = ct

        val sourceCellView = tableView["L1", 1]

        fun compare(cellView: CellView) {
            assertEquals(1000L, sourceCellView[CellHeight].height)
            assertEquals(2000L, sourceCellView[CellWidth].width)
            assertEquals(listOf("cc-1"), sourceCellView[CellClasses].classes)
            assertEquals(listOf("ct-1"), sourceCellView[CellTopics].topics)
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

        tableView["L1", Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", Int.MAX_VALUE])

        tableView["L1", Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", Long.MAX_VALUE])

        tableView["L1", row] = { sourceCellView }
        compare(tableView["L1", row])

        tableView["L1", rowView] = { sourceCellView }
        compare(tableView["L1", rowView])

        tableView["L1"][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1"][Int.MAX_VALUE])

        tableView["L1"][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1"][Long.MAX_VALUE])

        tableView["L1"][row] = { sourceCellView }
        compare(tableView["L1"][row])

        tableView["L1"][rowView] = { sourceCellView }
        compare(tableView["L1"][rowView])

        tableView[Int.MAX_VALUE]["L1"] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1"])

        tableView[Long.MAX_VALUE]["L1"] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1"])

        tableView[row]["L1"] = { sourceCellView }
        compare(tableView[row]["L1"])

        tableView[rowView]["L1"] = { sourceCellView }
        compare(tableView[rowView]["L1"])

        tableView["L1", "L2", Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", Int.MAX_VALUE])

        tableView["L1", "L2", Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", Long.MAX_VALUE])

        tableView["L1", "L2", row] = { sourceCellView }
        compare(tableView["L1", "L2", row])

        tableView["L1", "L2", rowView] = { sourceCellView }
        compare(tableView["L1", "L2", rowView])

        tableView["L1", "L2"][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2"][Int.MAX_VALUE])

        tableView["L1", "L2"][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2"][Long.MAX_VALUE])

        tableView["L1", "L2"][row] = { sourceCellView }
        compare(tableView["L1", "L2"][row])

        tableView["L1", "L2"][rowView] = { sourceCellView }
        compare(tableView["L1", "L2"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2"] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1", "L2"])

        tableView[Long.MAX_VALUE]["L1", "L2"] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1", "L2"])

        tableView[row]["L1", "L2"] = { sourceCellView }
        compare(tableView[row]["L1", "L2"])

        tableView[rowView]["L1", "L2"] = { sourceCellView }
        compare(tableView[rowView]["L1", "L2"])

        tableView["L1", "L2", "L3", Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", row] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", row])

        tableView["L1", "L2", "L3", rowView] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", rowView])

        tableView["L1", "L2", "L3"][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3"][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3"][row] = { sourceCellView }
        compare(tableView["L1", "L2", "L3"][row])

        tableView["L1", "L2", "L3"][rowView] = { sourceCellView }
        compare(tableView["L1", "L2", "L3"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3"] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3"] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3"])

        tableView[row]["L1", "L2", "L3"] = { sourceCellView }
        compare(tableView[row]["L1", "L2", "L3"])

        tableView[rowView]["L1", "L2", "L3"] = { sourceCellView }
        compare(tableView[rowView]["L1", "L2", "L3"])

        tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", row] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", row])

        tableView["L1", "L2", "L3", "L4", rowView] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", rowView])

        tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][row] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4"][row])

        tableView["L1", "L2", "L3", "L4"][rowView] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[row]["L1", "L2", "L3", "L4"] = { sourceCellView }
        compare(tableView[row]["L1", "L2", "L3", "L4"])

        tableView[rowView]["L1", "L2", "L3", "L4"] = { sourceCellView }
        compare(tableView[rowView]["L1", "L2", "L3", "L4"])

        tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", row] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5", row])

        tableView["L1", "L2", "L3", "L4", "L5", rowView] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5", rowView])

        tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][row] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][row])

        tableView["L1", "L2", "L3", "L4", "L5"][rowView] = { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[row]["L1", "L2", "L3", "L4", "L5"] = { sourceCellView }
        compare(tableView[row]["L1", "L2", "L3", "L4", "L5"])

        tableView[rowView]["L1", "L2", "L3", "L4", "L5"] = { sourceCellView }
        compare(tableView[rowView]["L1", "L2", "L3", "L4", "L5"])

        tableView["L1", Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", Int.MAX_VALUE])

        tableView["L1", Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", Long.MAX_VALUE])

        tableView["L1", row] { sourceCellView }
        compare(tableView["L1", row])

        tableView["L1", rowView] { sourceCellView }
        compare(tableView["L1", rowView])

        tableView["L1"][Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1"][Int.MAX_VALUE])

        tableView["L1"][Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1"][Long.MAX_VALUE])

        tableView["L1"][row] { sourceCellView }
        compare(tableView["L1"][row])

        tableView["L1"][rowView] { sourceCellView }
        compare(tableView["L1"][rowView])

        tableView[Int.MAX_VALUE]["L1"] { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1"])

        tableView[Long.MAX_VALUE]["L1"] { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1"])

        tableView[row]["L1"] { sourceCellView }
        compare(tableView[row]["L1"])

        tableView[rowView]["L1"] { sourceCellView }
        compare(tableView[rowView]["L1"])

        tableView["L1", "L2", Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", Int.MAX_VALUE])

        tableView["L1", "L2", Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", Long.MAX_VALUE])

        tableView["L1", "L2", row] { sourceCellView }
        compare(tableView["L1", "L2", row])

        tableView["L1", "L2", rowView] { sourceCellView }
        compare(tableView["L1", "L2", rowView])

        tableView["L1", "L2"][Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2"][Int.MAX_VALUE])

        tableView["L1", "L2"][Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2"][Long.MAX_VALUE])

        tableView["L1", "L2"][row] { sourceCellView }
        compare(tableView["L1", "L2"][row])

        tableView["L1", "L2"][rowView] { sourceCellView }
        compare(tableView["L1", "L2"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2"] { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1", "L2"])

        tableView[Long.MAX_VALUE]["L1", "L2"] { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1", "L2"])

        tableView[row]["L1", "L2"] { sourceCellView }
        compare(tableView[row]["L1", "L2"])

        tableView[rowView]["L1", "L2"] { sourceCellView }
        compare(tableView[rowView]["L1", "L2"])

        tableView["L1", "L2", "L3", Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", row] { sourceCellView }
        compare(tableView["L1", "L2", "L3", row])

        tableView["L1", "L2", "L3", rowView] { sourceCellView }
        compare(tableView["L1", "L2", "L3", rowView])

        tableView["L1", "L2", "L3"][Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3"][Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3"][row] { sourceCellView }
        compare(tableView["L1", "L2", "L3"][row])

        tableView["L1", "L2", "L3"][rowView] { sourceCellView }
        compare(tableView["L1", "L2", "L3"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3"] { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3"] { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3"])

        tableView[row]["L1", "L2", "L3"] { sourceCellView }
        compare(tableView[row]["L1", "L2", "L3"])

        tableView[rowView]["L1", "L2", "L3"] { sourceCellView }
        compare(tableView[rowView]["L1", "L2", "L3"])

        tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", row] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", row])

        tableView["L1", "L2", "L3", "L4", rowView] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", rowView])

        tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4"][row] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4"][row])

        tableView["L1", "L2", "L3", "L4"][rowView] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4"])

        tableView[row]["L1", "L2", "L3", "L4"] { sourceCellView }
        compare(tableView[row]["L1", "L2", "L3", "L4"])

        tableView[rowView]["L1", "L2", "L3", "L4"] { sourceCellView }
        compare(tableView[rowView]["L1", "L2", "L3", "L4"])

        tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5", row] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5", row])

        tableView["L1", "L2", "L3", "L4", "L5", rowView] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5", rowView])

        tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE])

        tableView["L1", "L2", "L3", "L4", "L5"][row] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][row])

        tableView["L1", "L2", "L3", "L4", "L5"][rowView] { sourceCellView }
        compare(tableView["L1", "L2", "L3", "L4", "L5"][rowView])

        tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { sourceCellView }
        compare(tableView[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { sourceCellView }
        compare(tableView[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"])

        tableView[row]["L1", "L2", "L3", "L4", "L5"] { sourceCellView }
        compare(tableView[row]["L1", "L2", "L3", "L4", "L5"])

        tableView[rowView]["L1", "L2", "L3", "L4", "L5"] { sourceCellView }
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

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], row] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][row] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5"]] = { sourceCellView }
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]] = { sourceCellView }
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { sourceCellView }
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { sourceCellView }
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = { sourceCellView }
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] = { sourceCellView }
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView] = { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { sourceCellView }
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { sourceCellView }
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView] = { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { sourceCellView }
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { sourceCellView }
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView] = { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { sourceCellView }
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { sourceCellView }
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { sourceCellView }
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { sourceCellView }
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], row] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][row] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { sourceCellView }
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { sourceCellView }
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5"]] { sourceCellView }
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]] { sourceCellView }
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceCellView }
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceCellView }
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceCellView }
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceCellView }
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceCellView }
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceCellView }
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceCellView }
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]] { sourceCellView }
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5"]]])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"], rowView])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][row])

        tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView] { sourceCellView }
        compare(tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]][rowView])

        tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { sourceCellView }
        compare(tableView[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { sourceCellView }
        compare(tableView[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { sourceCellView }
        compare(tableView[row][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { sourceCellView }
        compare(tableView[rowView][Header["L1", "L2", "L3", "L4", "L5", "L6"]])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView] { sourceCellView }
        compare(tableView[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceCellView }
        compare(tableView[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceCellView }
        compare(tableView[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceCellView }
        compare(tableView[row][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceCellView }
        compare(tableView[rowView][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]], rowView])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][row])

        tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView] { sourceCellView }
        compare(tableView[tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][rowView])

        tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceCellView }
        compare(tableView[Int.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceCellView }
        compare(tableView[Long.MAX_VALUE][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceCellView }
        compare(tableView[row][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])

        tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { sourceCellView }
        compare(tableView[rowView][tableView[Header["L1", "L2", "L3", "L4", "L5", "L6"]]])
        tableView[tableView["L1", 1000]] = sourceCellView
        compare(tableView[tableView["L1", 1000]])

        tableView[tableView["L1", 1001]] = { sourceCellView }
        compare(tableView[tableView["L1", 1001]])

        tableView[tableView["L1", 1002]] { sourceCellView }
        compare(tableView[tableView["L1", 1002]])

        tableView[table["L1", 2000]] = sourceCellView
        compare(tableView[table["L1", 2000]])

        tableView[table["L1", 2001]] = { sourceCellView }
        compare(tableView[table["L1", 2001]])

        tableView[table["L1", 2002]] { sourceCellView }
        compare(tableView[table["L1", 2002]])
    }
}