/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.IndexRelation.*
import sigbla.app.exceptions.InvalidRowException
import sigbla.app.pds.collection.TreeMap as PTreeMap
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.Temporal
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class Header internal constructor(vararg labels: String) : Comparable<Header> {
    internal constructor(labels: List<String>) : this(*labels.toTypedArray())

    val labels: List<String> = Collections.unmodifiableList(labels.asList())

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

class Column internal constructor(
    val table: Table,
    val header: Header,
    val order: Long = table.tableRef.get().columns[header]?.columnOrder ?: table.tableRef.get().columnCounter.getAndIncrement()
) : Comparable<Column>, Iterable<Cell<*>> {
    val indexes: Sequence<Long>
        get() = table.tableRef.get().columnCells[header]?.keys()?.sorted()?.asSequence()
            ?: throw InvalidColumnException("Unable to find column $header")

    // TODO At an optimized version for get(index)?
    operator fun get(indexRelation: IndexRelation, index: Long): Cell<*> {
        return getCellRaw(table.tableRef.get(), header, index, indexRelation)?.let {
            it.first.toCell(this, it.second)
        } ?: UnitCell(this, index)
    }

    operator fun set(index: Long, value: Cell<*>?) {
        if (value is UnitCell || value == null) {
            clear(index)
            return
        }

        val cellValue = value.toCellValue()

        synchronized(table.eventProcessor) {
            val (oldRef, newRef) = table.tableRef.refAction {
                val columns = if (it.columns.containsKey(this.header)) it.columns
                else it.columns.put(this.header, ColumnMeta(it.columnCounter.getAndIncrement(), true))

                val columnCells = if (it.columnCells.containsKey(this.header)) it.columnCells
                else it.columnCells.put(this.header, PTreeMap())

                val meta = columns[this.header] ?: throw InvalidColumnException("Unable to find column meta for header ${this.header}")
                val values = columnCells[this.header] ?: throw InvalidColumnException("Unable to find column cells for header ${this.header}")

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

            val oldColumn = Column(oldTable, this.header)
            val newColumn = Column(newTable, this.header)

            val old = oldColumn[index]
            val new = newColumn[index]

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

    private fun clear(index: Long) {
        synchronized(table.eventProcessor) {
            val (oldRef, newRef) = table.tableRef.refAction {
                // Return early if column no longer exists
                val meta = it.columns[this.header] ?: return@refAction it
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

            if (oldRef.version == newRef.version) return
            if (!table.eventProcessor.haveListeners()) return

            val oldTable = this.table.makeClone(ref = oldRef)
            val newTable = this.table.makeClone(ref = newRef)

            val oldColumn = Column(oldTable, this.header)
            val newColumn = Column(newTable, this.header)

            val old = oldColumn[index]
            val new = newColumn[index] // This will be a unit cell, but with new table and column refs

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

    override fun iterator(): Iterator<Cell<*>> = iterator(table, table.tableRef.get())

    internal fun iterator(table: Table, ref: TableRef): Iterator<Cell<*>> {
        // Column might have been removed before we call iterator
        val meta = ref.columns[this.header] ?: return emptyList<Cell<*>>().iterator()
        if (meta.prenatal) return emptyList<Cell<*>>().iterator()

        // We want to throw this exception because ref should contain columnCells
        val values = ref.columnCells[this.header] ?: throw InvalidColumnException("Unable to find column cells for header ${this.header}")
        val column = Column(table, this.header, meta.columnOrder)
        return values.asSequence().map { it.component2().toCell(column, it.component1()) }.iterator()
    }

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

    operator fun set(index: Long, value: Boolean?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: String?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: Double?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: Long?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: BigInteger?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: BigDecimal?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: Number?) {
        when (value) {
            is Int -> set(index, value.toLong())
            is Long -> set(index, value)
            is Float -> set(index, value.toDouble())
            is Double -> set(index, value)
            is BigInteger -> set(index, value)
            is BigDecimal -> set(index, value)
            null -> clear(index)
            // Could be a byte or short, something that fits in a long
            else -> set(index, value.toLong())
        }
    }

    operator fun set(index: Long, value: LocalDate?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: LocalTime?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: LocalDateTime?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: ZonedDateTime?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, value: Unit?) = if (value == null) clear(index) else set(index, value.toCell(this, index))

    operator fun set(index: Long, init: Cell<*>.() -> Unit) = this[index].init()

    // ---

    operator fun set(index: Int, cell: Cell<*>?) = set(index.toLong(), cell)

    operator fun set(index: Int, value: Boolean?) = set(index.toLong(), value)

    operator fun set(index: Int, value: String?) = set(index.toLong(), value)

    operator fun set(index: Int, value: Double?) = set(index.toLong(), value)

    operator fun set(index: Int, value: Long?) = set(index.toLong(), value)

    operator fun set(index: Int, value: BigInteger?) = set(index.toLong(), value)

    operator fun set(index: Int, value: BigDecimal?) = set(index.toLong(), value)

    operator fun set(index: Int, value: Number?) = set(index.toLong(), value)

    operator fun set(index: Int, value: LocalDate?) = set(index.toLong(), value)

    operator fun set(index: Int, value: LocalTime?) = set(index.toLong(), value)

    operator fun set(index: Int, value: LocalDateTime?) = set(index.toLong(), value)

    operator fun set(index: Int, value: ZonedDateTime?) = set(index.toLong(), value)

    operator fun set(index: Int, value: Unit?) = set(index.toLong(), value)

    operator fun set(index: Int, init: Cell<*>.() -> Unit) = this[index].init()

    // ---

    operator fun set(row: Row, cell: Cell<*>?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, cell)
    }

    operator fun set(row: Row, value: Boolean?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: String?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: Double?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: Long?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: BigInteger?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: BigDecimal?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: Number?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: LocalDate?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: LocalTime?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: LocalDateTime?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: ZonedDateTime?) {
        if (row.indexRelation != AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, value)
    }

    operator fun set(row: Row, value: Unit?) {
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
    operator fun contains(that: Temporal): Boolean = any { that in it }
    operator fun contains(that: Boolean): Boolean = any { that in it }
    operator fun contains(that: String): Boolean = any { that in it }
    operator fun contains(that: Cell<*>): Boolean = any { that in it }

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

class ColumnRange internal constructor(override val start: Column, override val endInclusive: Column) : ClosedRange<Column>, Iterable<Column> {
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
            .map { Column(table, it.first, it.second.columnOrder) }
            .iterator()
    }

    operator fun contains(that: Number): Boolean = any { that in it }
    operator fun contains(that: Temporal): Boolean = any { that in it }
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

val EMPTY_HEADER = Header()
