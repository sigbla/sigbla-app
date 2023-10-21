package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.min
import kotlin.math.max

// TODO Should the be sealed rather than abstract? Or just a normal class with no BaseRow?
abstract class Row : Comparable<Row>, Iterable<Cell<*>> {
    abstract val table: Table

    abstract val indexRelation: IndexRelation

    abstract val index: Long

    val headers: Sequence<ColumnHeader>
        get() = table.headers

    operator fun get(header: ColumnHeader): Cell<*> = table[header][indexRelation, index]

    operator fun get(column: Column): Cell<*> = table[column][indexRelation, index]

    operator fun get(vararg columnHeader: String): Cell<*> = get(
        ColumnHeader(
            *columnHeader
        )
    )

    operator fun set(header: ColumnHeader, value: Cell<*>?) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: String) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Double) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Float) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Long) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Int) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: BigInteger) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: BigDecimal) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Number) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, init: Cell<*>.() -> Any?) = table[header][index] { init() }

    operator fun set(column: Column, value: Cell<*>?) = table[column].set(index, value)
    operator fun set(column: Column, value: String) = table[column].set(index, value)
    operator fun set(column: Column, value: Double) = table[column].set(index, value)
    operator fun set(column: Column, value: Float) = table[column].set(index, value)
    operator fun set(column: Column, value: Long) = table[column].set(index, value)
    operator fun set(column: Column, value: Int) = table[column].set(index, value)
    operator fun set(column: Column, value: BigInteger) = table[column].set(index, value)
    operator fun set(column: Column, value: BigDecimal) = table[column].set(index, value)
    operator fun set(column: Column, value: Number) = table[column].set(index, value)
    operator fun set(column: Column, init: Cell<*>.() -> Any?) = table[column][index] { init() }

    operator fun set(vararg header: String, value: Cell<*>?) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: String) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Double) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Float) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Long) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Int) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: BigInteger) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: BigDecimal) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Number) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, init: Cell<*>.() -> Any?) = table[ColumnHeader(*header)][index] { init() }

    operator fun rangeTo(other: Row): RowRange {
        return RowRange(this, other)
    }

    override fun iterator(): Iterator<Cell<*>> {
        val ref = table.tableRef.get()
        val columnCellMap = ref.columnCells

        fun at(columnHeader: ColumnHeader): CellValue<*>? {
            val values = columnCellMap[columnHeader] ?: throw InvalidColumnException(columnHeader)
            return values[index]
        }

        fun firstBefore(columnHeader: ColumnHeader): CellValue<*>? {
            val values = columnCellMap[columnHeader] ?: throw InvalidColumnException(columnHeader)
            val keys = values.asSortedMap().headMap(index).keys
            if (keys.isEmpty()) return null
            return values[keys.last()]
        }

        fun firstAfter(columnHeader: ColumnHeader): CellValue<*>? {
            val values = columnCellMap[columnHeader] ?: throw InvalidColumnException(columnHeader)
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
                    it?.toCell(BaseColumn(table, columnHeader, columnMeta.columnOrder), index)
                }
            }
            .filterNotNull()
            .iterator()
    }

    override fun compareTo(other: Row): Int = when (index.compareTo(other.index)) {
        0 -> 0
        else -> indexRelation.compareTo(other.indexRelation)
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

    override fun toString() = index.toString()
}

class BaseRow internal constructor(override val table: Table, override val indexRelation: IndexRelation, override val index: Long) : Row()

class RowRange(override val start: Row, override val endInclusive: Row) : ClosedRange<Row>, Iterable<Row> {
    val table: Table
        get() = start.table

    override fun iterator(): Iterator<Row> {
        // TODO Revisit the below comment..
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

    override fun contains(value: Row): Boolean {
        if (value.index < min(start.index, endInclusive.index) || value.index > max(start.index, endInclusive.index)) {
            return false
        }

        return true
    }

    // TODO Consider what the return value here actually represents?
    override fun isEmpty() = false

    override fun toString(): String {
        return "$start..$endInclusive"
    }
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
