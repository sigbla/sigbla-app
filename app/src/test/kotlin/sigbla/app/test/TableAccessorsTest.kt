/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.After
import java.math.BigDecimal
import java.math.BigInteger

class TableAccessorsTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `cell accessors`() {
        val cell = Table[null].let {
            it["A", 0] = "A0"
            it["A", 0]
        }

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
    }

    @Test
    fun `string accessors`() {
        val cell = "string"

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
    }

    @Test
    fun `int accessors`() {
        val cell = Int.MAX_VALUE

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)
    }

    @Test
    fun `long accessors`() {
        val cell = Long.MAX_VALUE

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
    }

    @Test
    fun `float accessors`() {
        val cell = Float.MAX_VALUE

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell.toDouble(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell.toDouble(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toDouble(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell.toDouble(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell.toDouble(), table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell.toDouble(), table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell.toDouble(), table[table["L1", Int.MAX_VALUE]].value)
    }

    @Test
    fun `double accessors`() {
        val cell = Double.MAX_VALUE

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
    }

    @Test
    fun `bigint accessors`() {
        val cell = BigInteger.TEN

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
    }

    @Test
    fun `bigdecimal accessors`() {
        val cell = BigDecimal.TEN

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
    }

    @Test
    fun `number accessors`() {
        val cell = 123 as Number

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell.toLong(), table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell.toLong(), table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell.toLong(), table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell.toLong(), table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell.toLong(), table[table["L1", Int.MAX_VALUE]].value)
    }

    @Test
    fun `boolean accessors`() {
        val cell = true

        val table = Table[object {}.javaClass.enclosingMethod.name]

        table["L1", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", table[Long.MAX_VALUE]].value)

        table["L1"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table["L1"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Int.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[Long.MAX_VALUE]["L1"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table[table[Long.MAX_VALUE]]["L1"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1"].value)

        table["L1", "L2", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", table[Long.MAX_VALUE]].value)

        table["L1", "L2"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table["L1", "L2"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Int.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[Long.MAX_VALUE]["L1", "L2"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2"].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3"].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4"].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5", table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Int.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Int.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][Long.MAX_VALUE].value)
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][IndexRelation.AT, Long.MAX_VALUE].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table["L1", "L2", "L3", "L4", "L5"][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]]["L1", "L2", "L3", "L4", "L5"].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5"]]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]], table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Int.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][Long.MAX_VALUE].value)
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[Header["L1", "L2", "L3", "L4", "L5", "L6"]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Int.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Int.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][Long.MAX_VALUE].value)
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][IndexRelation.AT, Long.MAX_VALUE].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = cell
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]][table[Long.MAX_VALUE]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][Header["L1", "L2", "L3", "L4", "L5", "L6"]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Int.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)
        assertEquals(cell, table[IndexRelation.AT, Long.MAX_VALUE][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = cell
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]] = { cell }
        assertEquals(cell, table[table[Long.MAX_VALUE]][table[Header["L1", "L2", "L3", "L4", "L5", "L6"]]].value)

        table[table["L1", Int.MAX_VALUE]] = cell
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)

        table[table["L1", Int.MAX_VALUE]] = { cell }
        assertEquals(cell, table[table["L1", Int.MAX_VALUE]].value)
    }
}
