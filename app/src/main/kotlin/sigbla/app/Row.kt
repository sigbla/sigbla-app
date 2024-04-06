/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidRowException
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.min
import kotlin.math.max

class Row internal constructor(val table: Table, val indexRelation: IndexRelation, val index: Long) : Comparable<Row>, Iterable<Cell<*>> {
    val headers: Sequence<Header>
        get() = table.headers

    val columns: Sequence<Column>
        get() = table.columns

    operator fun get(header: Header): Cell<*> = table[header][indexRelation, index]

    operator fun get(column: Column): Cell<*> = table[column][indexRelation, index]

    operator fun get(vararg columnHeader: String): Cell<*> = get(Header(*columnHeader))

    operator fun set(header: Header, value: Cell<*>?) = table[header].set(this, value)
    operator fun set(header: Header, value: Boolean?) = table[header].set(this, value)
    operator fun set(header: Header, value: String?) = table[header].set(this, value)
    operator fun set(header: Header, value: Double?) = table[header].set(this, value)
    operator fun set(header: Header, value: Float?) = table[header].set(this, value)
    operator fun set(header: Header, value: Long?) = table[header].set(this, value)
    operator fun set(header: Header, value: Int?) = table[header].set(this, value)
    operator fun set(header: Header, value: BigInteger?) = table[header].set(this, value)
    operator fun set(header: Header, value: BigDecimal?) = table[header].set(this, value)
    operator fun set(header: Header, value: Number?) = table[header].set(this, value)
    operator fun set(header: Header, value: Unit?) = table[header].set(this, value)
    operator fun set(header: Header, init: Cell<*>.() -> Unit) = table[header][this].init()

    operator fun set(column: Column, value: Cell<*>?) = table[column].set(this, value)
    operator fun set(column: Column, value: Boolean?) = table[column].set(this, value)
    operator fun set(column: Column, value: String?) = table[column].set(this, value)
    operator fun set(column: Column, value: Double?) = table[column].set(this, value)
    operator fun set(column: Column, value: Float?) = table[column].set(this, value)
    operator fun set(column: Column, value: Long?) = table[column].set(this, value)
    operator fun set(column: Column, value: Int?) = table[column].set(this, value)
    operator fun set(column: Column, value: BigInteger?) = table[column].set(this, value)
    operator fun set(column: Column, value: BigDecimal?) = table[column].set(this, value)
    operator fun set(column: Column, value: Number?) = table[column].set(this, value)
    operator fun set(column: Column, value: Unit?) = table[column].set(this, value)
    operator fun set(column: Column, init: Cell<*>.() -> Unit) = table[column][this].init()

    operator fun set(vararg header: String, value: Cell<*>?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: Boolean?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: String?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: Double?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: Float?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: Long?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: Int?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: BigInteger?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: BigDecimal?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: Number?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, value: Unit?) = table[Header(*header)].set(this, value)
    operator fun set(vararg header: String, init: Cell<*>.() -> Unit) = table[Header(*header)][this].init()

    operator fun rangeTo(other: Row): RowRange {
        return RowRange(this, other)
    }

    operator fun contains(that: Number): Boolean = any { that in it }
    operator fun contains(that: Boolean): Boolean = any { that in it }
    operator fun contains(that: String): Boolean = any { that in it }
    operator fun contains(that: Cell<*>?): Boolean = any { that in it }

    override fun iterator(): Iterator<Cell<*>> = iterator(table, table.tableRef.get())

    internal fun iterator(table: Table, ref: TableRef): Iterator<Cell<*>> {
        val columnCellMap = ref.columnCells

        fun at(header: Header): CellValue<*>? {
            // We want to throw this exception because ref should contain columnCells
            val values = columnCellMap[header] ?: throw InvalidColumnException("Unable to find column cells for header $header")
            return values[index]
        }

        fun firstBefore(header: Header): CellValue<*>? {
            // We want to throw this exception because ref should contain columnCells
            val values = columnCellMap[header] ?: throw InvalidColumnException("Unable to find column cells for header $header")
            val keys = values.asSortedMap().headMap(index).keys
            if (keys.isEmpty()) return null
            return values[keys.last()]
        }

        fun firstAfter(header: Header): CellValue<*>? {
            // We want to throw this exception because ref should contain columnCells
            val values = columnCellMap[header] ?: throw InvalidColumnException("Unable to find column cells for header $header")
            val keys = values.asSortedMap().tailMap(index + 1L).keys
            if (keys.isEmpty()) return null
            return values[keys.first()]
        }

        return ref
            .columns
            .asSequence()
            .filter { (_, columnMeta) -> !columnMeta.prenatal }
            .sortedBy { (_, columnMeta) -> columnMeta.columnOrder }
            .map { (columnHeader, columnMeta) ->
                when (indexRelation) {
                    IndexRelation.AT -> at(columnHeader)
                    IndexRelation.BEFORE -> firstBefore(columnHeader)
                    IndexRelation.AFTER -> firstAfter(columnHeader)
                    IndexRelation.AT_OR_BEFORE -> at(columnHeader) ?: firstBefore(columnHeader)
                    IndexRelation.AT_OR_AFTER -> at(columnHeader) ?: firstAfter(columnHeader)
                }.let {
                    it?.toCell(Column(table, columnHeader, columnMeta.columnOrder), index)
                }
            }
            .filterNotNull()
            .iterator()
    }

    override fun compareTo(other: Row): Int = when (val cmp = index.compareTo(other.index)) {
        0 -> indexRelation.compareTo(other.indexRelation)
        else -> cmp
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Row

        if (table != other.table) return false
        if (index != other.index) return false
        if (indexRelation != other.indexRelation) return false

        return true
    }

    override fun hashCode() = index.hashCode()

    override fun toString() = "Row[$indexRelation $index]"
}

class RowRange internal constructor(override val start: Row, override val endInclusive: Row) : ClosedRange<Row>, Iterable<Row> {
    init {
        if (start.table !== endInclusive.table) throw InvalidRowException("RowRange must be within same table")
        if (start.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in RowRange: $start")
        if (endInclusive.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in RowRange: $endInclusive")
    }

    val table: Table
        get() = start.table

    override fun iterator(): Iterator<Row> {
        // Note, a row range is fixed to the indexes used, so no need to worry about refs or prenatal
        return if (start.index <= endInclusive.index) {
            ((start.index)..(endInclusive.index))
                .asSequence()
                .map { table[it] }
                .iterator()
        } else {
            ((start.index) downTo (endInclusive.index))
                .asSequence()
                .map { table[it] }
                .iterator()
        }
    }

    operator fun contains(that: Number): Boolean = any { that in it }
    operator fun contains(that: Boolean): Boolean = any { that in it }
    operator fun contains(that: String): Boolean = any { that in it }

    override fun contains(value: Row): Boolean {
        return !(value.index < min(start.index, endInclusive.index) || value.index > max(start.index, endInclusive.index))
    }

    override fun isEmpty() = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RowRange

        if (start != other.start) return false
        if (endInclusive != other.endInclusive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + endInclusive.hashCode()
        return result
    }

    override fun toString() = "$start..$endInclusive"
}

infix fun Row.before(other: Row): RowToRowAction {
    return RowToRowAction(
        this,
        other,
        RowActionOrder.BEFORE
    )
}

infix fun Row.after(other: Row): RowToRowAction {
    return RowToRowAction(
        this,
        other,
        RowActionOrder.AFTER
    )
}

infix fun Row.to(other: Row): RowToRowAction {
    return RowToRowAction(
        this,
        other,
        RowActionOrder.TO
    )
}

class RowToRowAction internal constructor(val left: Row, val right: Row, val order: RowActionOrder)

// TODO Consider a RowToTableAction, which would be functionally the same as row to row

enum class RowActionOrder { BEFORE, AFTER, TO }
