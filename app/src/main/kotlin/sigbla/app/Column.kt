/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.IndexRelation.*
import sigbla.app.exceptions.InvalidRowException
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class Header internal constructor(vararg labels: String) : Comparable<Header> {
    val labels: List<String> = Collections.unmodifiableList(labels.asList())

    internal constructor(labels: List<String>) : this(*labels.toTypedArray())

    init {
        labels.forEach {
            // This can happen because we allow a list in the secondary constructor
            if (it == null) throw InvalidColumnException("Null values not allowed")
        }
    }

    operator fun get(index: Int): String? {
        return when {
            index < labels.size -> labels[index]
            else -> null
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Header)
            this.labels == other.labels
        else
            false
    }

    override fun hashCode() = labels.hashCode()

    override fun toString() = "Header[${labels.joinToString(limit = 30)}]"

    override fun compareTo(other: Header): Int {
        if (this.labels.size != other.labels.size) return this.labels.size.compareTo(other.labels.size)

        for (i in 0 until this.labels.size) {
            val cmp = this.labels[i].compareTo(other.labels[i])

            if (cmp != 0) return cmp
        }

        return 0
    }

    operator fun component1() = this[0]
    operator fun component2() = this[1]
    operator fun component3() = this[2]
    operator fun component4() = this[3]
    operator fun component5() = this[4]

    companion object {
        operator fun get(vararg labels: String) = Header(*labels)
        operator fun get(labels: List<String>) = Header(labels)
    }
}

// TODO Should the be sealed rather than abstract? Or just a normal class with no BaseColumn?
abstract class Column internal constructor(
    val table: Table,
    val header: Header,
    val order: Long
) : Comparable<Column>, Iterable<Cell<*>> {
    val indexes: Sequence<Long>
        get() = table.tableRef.get().columnCells[header]?.keys()?.sorted()?.asSequence()
            ?: throw InvalidColumnException("Unable to find column $header")

    abstract operator fun get(indexRelation: IndexRelation, index: Long): Cell<*>

    abstract operator fun set(index: Long, value: Cell<*>?)

    infix fun at(index: Long) = get(AT, index)

    infix fun atOrBefore(index: Long) = get(AT_OR_BEFORE, index)

    infix fun atOrAfter(index: Long) = get(AT_OR_AFTER, index)

    infix fun before(index: Long) = get(BEFORE, index)

    infix fun after(index: Long) = get(AFTER, index)

    infix fun at(index: Int) = get(AT, index)

    infix fun atOrBefore(index: Int) = get(AT_OR_BEFORE, index)

    infix fun atOrAfter(index: Int) = get(AT_OR_AFTER, index)

    infix fun before(index: Int) = get(BEFORE, index)

    infix fun after(index: Int) = get(AFTER, index)

    infix fun left(offset: Int): Column? {
        if (offset == 0) return this
        else if (offset < 0) return right(offset.absoluteValue)

        val header = table.tableRef.get().headers
            .takeWhile { it.first != this.header }
            .fold(LinkedList<Header>()) { acc, (header, _) -> acc.add(0, header); acc }
            .drop(offset - 1).firstOrNull() ?: return null

        return table[header]
    }

    infix fun right(offset: Int): Column? {
        if (offset == 0) return this
        else if (offset < 0) return left(offset.absoluteValue)

        val (header, _) = table.tableRef.get().headers
            .dropWhile { it.first != this.header }
            .drop(offset)
            .firstOrNull() ?: return null

        return table[header]
    }

    operator fun get(index: Long) = get(AT, index)

    operator fun get(index: Int) = get(AT, index.toLong())

    operator fun get(indexRelation: IndexRelation, index: Int) = get(indexRelation, index.toLong())

    operator fun get(row: Row) = get(row.indexRelation, row.index)

    // ---

    operator fun set(index: Long, value: Boolean) = set(index, value.toCell(this, index))

    operator fun set(index: Long, value: String) = set(index, value.toCell(this, index))

    operator fun set(index: Long, value: Double) = set(index, value.toCell(this, index))

    operator fun set(index: Long, value: Long) = set(index, value.toCell(this, index))

    operator fun set(index: Long, value: BigInteger) = set(index, value.toCell(this, index))

    operator fun set(index: Long, value: BigDecimal) = set(index, value.toCell(this, index))

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

    operator fun set(index: Long, init: Cell<*>.() -> Unit) = this[index].init()

    // ---

    operator fun set(index: Int, cell: Cell<*>?) = set(index.toLong(), cell)

    operator fun set(index: Int, value: Boolean) = set(index.toLong(), value)

    operator fun set(index: Int, value: String) = set(index.toLong(), value)

    operator fun set(index: Int, value: Double) = set(index.toLong(), value)

    operator fun set(index: Int, value: Long) = set(index.toLong(), value)

    operator fun set(index: Int, value: BigInteger) = set(index.toLong(), value)

    operator fun set(index: Int, value: BigDecimal) = set(index.toLong(), value)

    operator fun set(index: Int, value: Number) = set(index.toLong(), value)

    operator fun set(index: Int, init: Cell<*>.() -> Unit) = this[index].init()

    // ---

    operator fun set(row: Row, cell: Cell<*>?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, cell)
    }

    operator fun set(row: Row, value: Boolean) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: String) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: Double) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: Long) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: BigInteger) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: BigDecimal) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: Number) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, init: Cell<*>.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[row.indexRelation, row.index].init()
    }

    // ---

    // TODO: Look at adding a add(..) function.
    //       Add would just insert a cell at first available location.
    //       Maybe that can be a plus operator function?

    operator fun rangeTo(other: Column): ColumnRange {
        return ColumnRange(this, other)
    }

    operator fun contains(that: Number): Boolean = any { that in it }
    operator fun contains(that: Boolean): Boolean = any { that in it }
    operator fun contains(that: String): Boolean = any { that in it }
    operator fun contains(that: Cell<*>?): Boolean = any { that in it }

    override fun compareTo(other: Column): Int {
        return order.compareTo(other.order)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Column

        if (table != other.table) return false
        if (header != other.header) return false

        return true
    }

    override fun hashCode() = header.hashCode()

    override fun toString() = "Column[${header.labels.joinToString(limit = 30)}]"
}

class BaseColumn internal constructor(
    table: Table,
    header: Header,
    columnOrder: Long = table.tableRef.get().columns[header]?.columnOrder ?: table.tableRef.get().columnCounter.getAndIncrement()
) : Column(
    table,
    header,
    columnOrder
) {
    // TODO At an optimized version for get(index)
    override fun get(indexRelation: IndexRelation, index: Long): Cell<*> {
        return getCellRaw(table.tableRef.get(), header, index, indexRelation)?.let {
            it.first.toCell(this, it.second)
        } ?: UnitCell(this, index)
    }

    override fun set(index: Long, value: Cell<*>?) {
        if (value is UnitCell || value == null) {
            clear(index)
            return
        }

        val cellValue = value.toCellValue()

        synchronized(table.eventProcessor) {
            val (oldRef, newRef) = table.tableRef.refAction {
                val meta = it.columns[this.header] ?: throw InvalidColumnException("Unable to find column meta for header ${this.header}")
                val values = it.columnCells[this.header] ?: throw InvalidColumnException("Unable to find column cells for header ${this.header}")

                it.copy(
                    columns = if (meta.prenatal) it.columns.put(
                        this.header,
                        meta.copy(prenatal = false)
                    ) else it.columns,
                    columnCells = it.columnCells.put(this.header, values.put(index, cellValue)),
                    version = it.version + 1L
                )
            }

            if (!table.eventProcessor.haveListeners()) return

            val oldTable = this.table.makeClone(ref = oldRef)
            val newTable = this.table.makeClone(ref = newRef)

            // TODO This might create a column if it doesn't exist, which we don't want (see events in table ops)
            val old = oldTable[this.header][index]
            val new = newTable[this.header][index]

            table.eventProcessor.publish(
                listOf(
                    TableListenerEvent(
                        old,
                        new
                    )
                ) as List<TableListenerEvent<Any, Any>>
            )
        }
    }

    private fun clear(index: Long): Cell<*> {
        synchronized(table.eventProcessor) {
            val (oldRef, newRef) = table.tableRef.refAction {
                val meta = it.columns[this.header] ?: throw InvalidColumnException("Unable to find column meta for header ${this.header}")
                val values = it.columnCells[this.header] ?: throw InvalidColumnException("Unable to find column cells for header ${this.header}")

                it.copy(
                    columns = if (meta.prenatal) it.columns.put(
                        this.header,
                        meta.copy(prenatal = false)
                    ) else it.columns,
                    columnCells = it.columnCells.put(this.header, values.remove(index)),
                    version = it.version + 1L
                )
            }

            val oldTable = this.table.makeClone(ref = oldRef)
            val newTable = this.table.makeClone(ref = newRef)

            val old = oldTable[this.header][index]
            // TODO This will create a column if it doesn't exist, which we don't want (see events in table ops)
            val new = newTable[this.header][index] // This will be a unit cell, but with new table and column refs

            if (!table.eventProcessor.haveListeners()) return old

            table.eventProcessor.publish(
                listOf(
                    TableListenerEvent(
                        old,
                        new
                    )
                ) as List<TableListenerEvent<Any, Any>>
            )

            return old
        }
    }

    override fun iterator(): Iterator<Cell<*>> {
        val values = table.tableRef.get().columnCells[this.header] ?: throw InvalidColumnException("Unable to find column cells for header ${this.header}")
        return values.asSequence().map { it.component2().toCell(this, it.component1()) }.iterator()
    }
}

internal fun getCellRaw(ref: TableRef, header: Header, index: Long, indexRelation: IndexRelation): Pair<CellValue<*>, Long>? {
    val values = ref.columnCells[header] ?: return null

    fun firstBefore(): Pair<CellValue<*>, Long>? {
        val keys = values.asSortedMap().headMap(index).keys
        if (keys.isEmpty()) return null
        val index = keys.last()
        val value = values[index] ?: return null
        return Pair(value, index)
    }

    fun firstAfter(): Pair<CellValue<*>, Long>? {
        val keys = values.asSortedMap().tailMap(index + 1L).keys
        if (keys.isEmpty()) return null
        val index = keys.first()
        val value = values[index] ?: return null
        return Pair(value, index)
    }

    return when (indexRelation) {
        AT -> values[index]?.let { Pair(it, index) }
        BEFORE -> firstBefore()
        AFTER -> firstAfter()
        AT_OR_BEFORE -> values[index]?.let { Pair(it, index) } ?: getCellRaw(ref, header, index, BEFORE)
        AT_OR_AFTER -> values[index]?.let { Pair(it, index) } ?: getCellRaw(ref, header, index, AFTER)
    }
}

class ColumnRange(override val start: Column, override val endInclusive: Column) : ClosedRange<Column>, Iterable<Column> {
    init {
        if (start.table !== endInclusive.table) throw InvalidColumnException("ColumnRange must be within same table")
    }

    val table: Table
        get() = start.table

    override fun iterator(): Iterator<Column> {
        val ref = table.tableRef.get()

        // Because columns might move around, get the latest order.
        // It might also mean the column is no longer available
        val currentStart = ref.columns[start.header]?.columnOrder ?: return Collections.emptyIterator()
        val currentEnd = ref.columns[endInclusive.header]?.columnOrder ?: return Collections.emptyIterator()

        val minOrder = min(currentStart, currentEnd)
        val maxOrder = max(currentStart, currentEnd)

        return ref
            .headers
            .filter { it.second.columnOrder in minOrder..maxOrder }
            .let {
                if (currentStart > currentEnd) it.toList().reversed().asSequence() else it
            }
            .map { BaseColumn(table, it.first, it.second.columnOrder) }
            .iterator()
    }

    operator fun contains(that: Number): Boolean = any { that in it }
    operator fun contains(that: Boolean): Boolean = any { that in it }
    operator fun contains(that: String): Boolean = any { that in it }

    override fun contains(value: Column): Boolean {
        val ref = table.tableRef.get()

        val valueMeta = ref.columns[value.header] ?: return false
        if (valueMeta.prenatal) return false

        // Because columns might move around, get the latest order
        // It might also mean the column is no longer available
        val currentStart = ref.columns[start.header]?.columnOrder ?: return false
        val currentEnd = ref.columns[endInclusive.header]?.columnOrder ?: return false

        val minOrder = min(currentStart, currentEnd)
        val maxOrder = max(currentStart, currentEnd)

        return !(value.order < minOrder || value.order > maxOrder)
    }

    override fun isEmpty() = !iterator().hasNext()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColumnRange

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

// TODO Think about including a RELATIVE_TO option:
//      If I have a row relative to index 10, and I move row 10 to row 9, then my existing
//      Row instance relative to index 10 would then point to the new location at row 9.
//      The same would need to apply to other rows part of the cascading effect..
enum class IndexRelation(private val text: String) {
    BEFORE("before"), AT_OR_BEFORE("at or before"), AT("at"), AT_OR_AFTER("at or after"), AFTER("after");

    override fun toString() = text
}

infix fun Column.before(other: Column): ColumnToColumnAction {
    if (this == other)
        throw InvalidColumnException("Cannot move/copy column before itself: $this")

    return ColumnToColumnAction(
        this,
        other,
        ColumnActionOrder.BEFORE
    )
}

infix fun Column.after(other: Column): ColumnToColumnAction {
    if (this == other)
        throw InvalidColumnException("Cannot move/copy column after itself: $this")

    return ColumnToColumnAction(
        this,
        other,
        ColumnActionOrder.AFTER
    )
}

infix fun Column.to(other: Column): ColumnToColumnAction {
    return ColumnToColumnAction(
        this,
        other,
        ColumnActionOrder.TO
    )
}

infix fun Column.to(other: Table): ColumnToTableAction {
    return ColumnToTableAction(
        this,
        other
    )
}

class ColumnToTableAction internal constructor(val left: Column, val table: Table)

class ColumnToColumnAction internal constructor(val left: Column, val right: Column, val order: ColumnActionOrder)

enum class ColumnActionOrder { BEFORE, AFTER, TO }

internal val emptyHeader = Header()
