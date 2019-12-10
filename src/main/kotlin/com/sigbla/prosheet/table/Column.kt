package com.sigbla.prosheet.table

import com.sigbla.prosheet.exceptions.InvalidTableException
import com.sigbla.prosheet.exceptions.ReadOnlyColumnException
import com.sigbla.prosheet.table.IndexRelation.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentNavigableMap
import java.util.function.Consumer
import kotlin.math.max

// TODO: data class?
class ColumnHeader(vararg header: String) : Comparable<ColumnHeader> {
    // TODO: Make sure this list is immutable
    val header: List<String> = header.asList();

    constructor(header: List<String>) : this(*header.toTypedArray())

    operator fun get(index: Int): String {
        return when {
            index < header.size -> header[index]
            else -> ""
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ColumnHeader)
            this.header == other.header
        else
            false
    }

    override fun hashCode(): Int {
        return this.header.hashCode()
    }

    override fun toString(): String {
        return this.header.toString()
    }

    override fun compareTo(other: ColumnHeader): Int {
        for (i in 0 until max(this.header.size, other.header.size)) {
            val thisItem = if (i < this.header.size) this.header[i] else ""
            val otherItem = if (i < other.header.size) other.header[i] else ""
            val cmp = thisItem.compareTo(otherItem)

            if (cmp != 0) return cmp
        }

        return 0
    }

    operator fun component1() = this[1]
    operator fun component2() = this[2]
    operator fun component3() = this[3]
    operator fun component4() = this[4]
    operator fun component5() = this[5]
}

// TODO We'd like various utility functions like sum, etc, like defined on CollectionsKt..
// kotlin.collections.Iterable<kotlin.Int>.sum(): kotlin.Int
// But these should be defined in some math package maybe?
abstract class Column(val table: Table, val columnHeader: ColumnHeader) : Iterable<Cell<*>> {
    abstract operator fun get(indexRelation: IndexRelation, index: Long): Cell<*>

    abstract operator fun set(index: Long, cell: Cell<*>)

    operator fun get(index: Long) = get(AT, index)

    operator fun get(index: Int) = get(AT, index.toLong())

    operator fun get(indexRelation: IndexRelation, index: Int) = get(indexRelation, index.toLong())

    operator fun set(index: Long, value: String) = set(index, value.toCell(index))

    operator fun set(index: Long, value: Double) = set(index, value.toCell(index))

    operator fun set(index: Long, value: Long) = set(index, value.toCell(index))

    operator fun set(index: Long, value: BigInteger) = set(index, value.toCell(index))

    operator fun set(index: Long, value: BigDecimal) = set(index, value.toCell(index))

    operator fun set(index: Long, value: Number) {
        when (value) {
            is Int -> set(index, value.toLong())
            is Long -> set(index, value)
            is Float -> set(index, value.toDouble())
            is Double -> set(index, value)
            is BigInteger -> set(index, value)
            is BigDecimal -> set(index, value)
            else -> set(index, value.toLong())
        }
    }

    operator fun set(index: Int, cell: Cell<*>) = set(index.toLong(), cell)

    operator fun set(index: Int, value: String) = set(index.toLong(), value)

    operator fun set(index: Int, value: Double) = set(index.toLong(), value)

    operator fun set(index: Int, value: Long) = set(index.toLong(), value)

    operator fun set(index: Int, value: BigInteger) = set(index.toLong(), value)

    operator fun set(index: Int, value: BigDecimal) = set(index.toLong(), value)

    operator fun set(index: Int, value: Number) {
        when (value) {
            is Int -> set(index, value.toLong())
            is Long -> set(index, value)
            is Float -> set(index, value.toDouble())
            is Double -> set(index, value)
            is BigInteger -> set(index, value)
            is BigDecimal -> set(index, value)
            else -> set(index, value.toLong())
        }
    }

    abstract fun remove(index: Long): Cell<*>

    fun remove(index: Int) = remove(index.toLong())

    fun rename(newName: ColumnHeader) = table.rename(columnHeader, newName)

    fun rename(vararg newName: String) = table.rename(columnHeader, *newName)

    abstract fun clear()

    // TODO: Maybe?
    //internal abstract fun close()

    fun between(fromIndex: Int, toIndex: Int, descendingOrder: Boolean = false) = between(fromIndex.toLong(), toIndex.toLong(), descendingOrder)

    fun between(fromIndex: Long, toIndex: Long, descendingOrder: Boolean = false): Iterator<Cell<*>> {
        return object : Iterator<Cell<*>> {
            private var currentIndex = if (descendingOrder) toIndex else fromIndex
            private var nextCell: Cell<*> = if (descendingOrder) UnitCell(toIndex) else UnitCell(fromIndex)

            override fun hasNext(): Boolean {
                if (nextCell is UnitCell) {
                    nextCell = get(if (descendingOrder) AT_OR_BEFORE else AT_OR_AFTER, currentIndex)
                }

                if (nextCell !is UnitCell && (nextCell.index > toIndex || nextCell.index < fromIndex)) {
                    nextCell = UnitCell(nextCell.index)
                }

                return nextCell !is UnitCell
            }

            override fun next(): Cell<*> {
                if (hasNext())
                    currentIndex = nextCell.index + if (descendingOrder) -1L else 1L
                else
                    throw NoSuchElementException()

                return nextCell
            }
        }
    }

    override fun forEach(action: Consumer<in Cell<*>>?) {
        if (action != null)
            iterator().forEachRemaining(action)
    }

    override fun iterator(): Iterator<Cell<*>> {
        return iterator(false)
    }

    fun iterator(descendingOrder: Boolean): Iterator<Cell<*>> = between(Long.MIN_VALUE, Long.MAX_VALUE, descendingOrder)

    override fun spliterator(): Spliterator<Cell<*>> {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.NONNULL)
    }

    // TODO Spliterator for decending order as well? Parallel?
}

class BaseColumn internal constructor(table: Table, columnHeader: ColumnHeader, private val indices: ConcurrentNavigableMap<Long, Int>) : Column(table, columnHeader) {
    internal val values = ConcurrentHashMap<Long, CellValue<*>>()

    override fun get(indexRelation: IndexRelation, index: Long): Cell<*> {
        return getCellRaw(index, indexRelation)?.toCell(index) ?: UnitCell(index)
    }

    override fun set(index: Long, cell: Cell<*>) {
        if (cell is UnitCell) {
            remove(index)
            return
        }

        setCellRaw(index, cell.toCellValue()) ?: UnitCell(index)
    }

    override fun remove(index: Long): Cell<*> {
        val old = values.remove(index)?.toCell(index) ?: UnitCell(index)

        if (old !is UnitCell) {
            indices.compute(index) { _, v ->
                when {
                    v == null -> null
                    v - 1 == 0 -> null
                    else -> v - 1
                }
            }
        }

        return old
    }

    override fun clear() {
        for (k in values.keys) {
            indices.compute(k) { _, v ->
                when {
                    v == null -> null
                    v - 1 == 0 -> null
                    else -> v - 1
                }
            }
        }
        values.clear()
    }

    private fun getCellRaw(index: Long, indexRelation: IndexRelation): CellValue<*>? {
        fun firstBefore(): CellValue<*>? {
            for (i in indices.headMap(index).keys.descendingIterator()) {
                if (values.containsKey(i)) {
                    return values[i]
                }
            }
            return null
        }

        fun firstAfter(): CellValue<*>? {
            for (i in indices.tailMap(index + 1L).keys.iterator()) {
                if (values.containsKey(i)) {
                    return values[i]
                }
            }
            return null
        }

        return when (indexRelation) {
            AT -> values[index]
            BEFORE -> firstBefore()
            AFTER -> firstAfter()
            AT_OR_BEFORE -> values.getOrElse(index) { getCellRaw(index, BEFORE) }
            AT_OR_AFTER -> values.getOrElse(index) { getCellRaw(index, AFTER) }
        }
    }

    private fun setCellRaw(index: Long, cellValue: CellValue<*>): CellValue<*>? {
        if (table.closed)
            throw InvalidTableException("Table is closed")

        val old = values.put(index, cellValue)

        if (old == null)
            indices.compute(index) { _, v -> if (v == null) 1 else v + 1 }

        return old
    }
}

class ReadOnlyRowColumn internal constructor(private val column: Column, private val index: Long) : Column(column.table, column.columnHeader) {
    override fun get(indexRelation: IndexRelation, index: Long): Cell<*> {
        if (index > this.index)
            return UnitCell(index);

        return column.get(indexRelation, index)
    }

    override fun set(index: Long, cell: Cell<*>) {
        throw ReadOnlyColumnException()
    }

    override fun remove(index: Long): Cell<*> {
        throw ReadOnlyColumnException()
    }

    override fun clear() {
        throw ReadOnlyColumnException()
    }
}

enum class IndexRelation {
    AT, AT_OR_BEFORE, AT_OR_AFTER, BEFORE, AFTER
}

