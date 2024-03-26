/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.AfterClass
import java.math.BigDecimal
import java.math.BigInteger

class TableAccessorsTest {
    @Test
    fun `cell accessors`() {
        val cell = Table[null].let {
            it["A", 0] = "A0"
            it["A", 0]
        }

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.value, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.value, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.value, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.value, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.value, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.value, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.value, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.value, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.value, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.value, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.value, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.value, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.value, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.value, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.value, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.value, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.value, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.value, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.value, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.value, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.value, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.value, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.value, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.value, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.value, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.value, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.value, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.value, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.value, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell.value, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell.value, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.value, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    @Test
    fun `string accessors`() {
        val cell = "string"

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    @Test
    fun `int accessors`() {
        val cell = Int.MAX_VALUE

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    @Test
    fun `long accessors`() {
        val cell = Long.MAX_VALUE

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    @Test
    fun `float accessors`() {
        val cell = Float.MAX_VALUE

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell.toDouble(), table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toDouble(), table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    @Test
    fun `double accessors`() {
        val cell = Double.MAX_VALUE

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    @Test
    fun `bigint accessors`() {
        val cell = BigInteger.TEN

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    @Test
    fun `bigdecimal accessors`() {
        val cell = BigDecimal.TEN

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    @Test
    fun `number accessors`() {
        val cell = 123 as Number

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    @Test
    fun `boolean accessors`() {
        val cell = true

        val table = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]](cell)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]](cell)
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { this(cell) }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]](cell)
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)

        table[table["L1", Int.MAX_VALUE]] = { this(cell) }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
        clear(table)
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}
