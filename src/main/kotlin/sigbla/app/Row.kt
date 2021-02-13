package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.min
import kotlin.math.max
import kotlin.reflect.KClass

// TODO Add Iterable<Cell<*>> and other operator functions as we have on Column
abstract class Row : Comparable<Row>, Iterable<Cell<*>> {
    abstract val table: Table

    abstract val indexRelation: IndexRelation

    abstract val index: Long

    val headers: Collection<ColumnHeader>
        get() = table.headers

    operator fun get(header: ColumnHeader): Cell<*> = table[header][indexRelation, index]

    operator fun get(vararg columnHeader: String): Cell<*> = get(
        ColumnHeader(
            *columnHeader
        )
    )

    // TODO Unsure if it is correct to remove based on indexRelation. Needs to work relative to set below as well..
    // TODO Compare to column behaviour?
    // TODO Likely just remove the function, this goes to TalbeOps..
    fun remove(header: ColumnHeader) = table[header].remove(table[header][indexRelation, index].index)

    // TODO Likely just remove the function, this goes to TalbeOps..
    fun remove(vararg header: String) = remove(ColumnHeader(*header))

    operator fun set(header: ColumnHeader, value: Cell<*>?) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: String) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Double) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Float) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Long) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Int) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: BigInteger) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: BigDecimal) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Number) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(table[header][index]).init()

    operator fun set(vararg header: String, value: Cell<*>?) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: String) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Double) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Float) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Long) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Int) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: BigInteger) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: BigDecimal) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Number) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(table[ColumnHeader(*header)][index]).init()

    operator fun rangeTo(other: Row): RowRange {
        return RowRange(this, other)
    }

    override fun iterator(): Iterator<Cell<*>> {
        // TODO Should probably use a cloned table for all iterators, like with events
        val ref = table.tableRef.get()
        val columnCellMap = ref.columnCellMap

        fun at(column: Column): CellValue<*>? {
            val values = columnCellMap[column] ?: throw InvalidColumnException(column)
            return values[index]
        }

        fun firstBefore(column: Column): CellValue<*>? {
            val values = columnCellMap[column] ?: throw InvalidColumnException(column)
            val keys = values.asSortedMap().headMap(index).keys
            if (keys.isEmpty()) return null
            return values[keys.last()]
        }

        fun firstAfter(column: Column): CellValue<*>? {
            val values = columnCellMap[column] ?: throw InvalidColumnException(column)
            val keys = values.asSortedMap().tailMap(index + 1L).keys
            if (keys.isEmpty()) return null
            return values[keys.first()]
        }

        return ref
            .columnsMap
            .values()
            .asSequence()
            .sortedBy { it.columnOrder }
            .map { column ->
                when (indexRelation) {
                    IndexRelation.AT -> at(column)
                    IndexRelation.BEFORE -> firstBefore(column)
                    IndexRelation.AFTER -> firstAfter(column)
                    IndexRelation.AT_OR_BEFORE -> at(column) ?: firstBefore(column)
                    IndexRelation.AT_OR_AFTER -> at(column) ?: firstAfter(column)
                }.let { it?.toCell(column, index) ?: UnitCell(column, index) }
            }
            .iterator()
    }

    override fun compareTo(other: Row): Int {
        return index.compareTo(other.index)
    }

    override fun toString(): String {
        return index.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Row

        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        return index.hashCode()
    }
}

class BaseRow internal constructor(override val table: Table, override val indexRelation: IndexRelation, override val index: Long) : Row()

class RowRange(override val start: Row, override val endInclusive: Row) : ClosedRange<Row>, Iterable<Row> {
    val table: Table
        get() = start.table

    override fun iterator(): Iterator<Row> {
        // TODO This needs to use a ref to ensure a snapshot
        return if (start.index <= endInclusive.index) {
            ((start.index)..(endInclusive.index))
                .asSequence()
                .map { table[it] }
                .iterator()
        } else {
            ((endInclusive.index)..(start.index))
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

    override fun isEmpty() = false

    override fun toString(): String {
        return "$start..$endInclusive"
    }
}
