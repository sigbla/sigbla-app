package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.exceptions.ReadOnlyColumnException
import sigbla.app.IndexRelation.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentNavigableMap
import kotlin.math.max

class ColumnHeader(vararg header: String) : Comparable<ColumnHeader> {
    val header: List<String> = Collections.unmodifiableList(header.asList())

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

abstract class Column(val table: Table, val columnHeader: ColumnHeader) : Comparable<Column>, Iterable<Cell<*>> {
    internal val columnOrder = table.columnCounter.getAndIncrement()

    abstract operator fun get(indexRelation: IndexRelation, index: Long): Cell<*>

    abstract operator fun set(index: Long, value: Cell<*>?)

    // TODO Not sure I like these yet.. maybe move this and other infix to special place?
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

    // TODO Look at if this is wanted and possible: table["A"] move|copy before|after table["B"] (withName ..)
    //infix fun move()

    operator fun get(index: Long) = get(AT, index)

    operator fun get(index: Int) = get(AT, index.toLong())

    operator fun get(indexRelation: IndexRelation, index: Int) = get(indexRelation, index.toLong())

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

    operator fun set(index: Int, cell: Cell<*>?) = set(index.toLong(), cell)

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

    infix fun before(other: Column): ColumnAction {
        if (this == other)
            throw InvalidColumnException("Cannot move column before itself: $this")

        return ColumnAction(
            this,
            other,
            ColumnActionOrder.BEFORE
        )
    }

    infix fun after(other: Column): ColumnAction {
        if (this == other)
            throw InvalidColumnException("Cannot move column after itself: $this")

        return ColumnAction(
            this,
            other,
            ColumnActionOrder.AFTER
        )
    }

    operator fun rangeTo(other: Column): ColumnRange {
        return ColumnRange(this, other)
    }

    inline fun <reified O, reified N> on(crossinline listener: (eventReceiver: ListenerEventReceiver<Column, O, N>) -> Unit): ListenerReference {
        return onAny { receiver ->
            val events = receiver.events.filter {
                it.oldValue.value is O && it.newValue.value is N
            } as Sequence<ListenerEvent<out O, out N>>
            //if (events.isNotEmpty()) listener.invoke(ListenerEventReceiver(this, receiver.listenerReference, events))
            listener.invoke(ListenerEventReceiver(receiver.listenerReference, this, events))
        }
    }

    fun onAny(listener: (eventReceiver: ListenerEventReceiver<Column, *, *>) -> Unit): ListenerReference {
        return table.eventProcessor.subscribe(this) {
            //if (it.events.isNotEmpty()) listener.invoke(ListenerEventReceiver(this, it.listenerReference, it.events))
            listener.invoke(ListenerEventReceiver(it.listenerReference, this, it.events))
        }
    }

    override fun compareTo(other: Column): Int {
        if (table != other.table)
            throw InvalidColumnException("Both columns must belong to same table")

        return columnOrder.compareTo(other.columnOrder)
    }

    override fun toString(): String {
        return columnHeader.toString()
    }
}

class BaseColumn internal constructor(
    table: Table, columnHeader: ColumnHeader,
    private val indices: ConcurrentNavigableMap<Long, Int>
) : Column(table, columnHeader) {
    internal val values = ConcurrentHashMap<Long, CellValue<*>>()

    override fun get(indexRelation: IndexRelation, index: Long): Cell<*> {
        return getCellRaw(index, indexRelation)?.toCell(this, index) ?: UnitCell(
            this,
            index
        )
    }

    override fun set(index: Long, value: Cell<*>?) {
        if (value is UnitCell || value == null) {
            remove(index)
            return
        }

        val old = setCellRaw(index, value.toCellValue())?.toCell(this, index) ?: UnitCell(this, index)
        val new = value.toCell(this, index)
        table.eventProcessor.publish(listOf(ListenerEvent(old, new)) as List<ListenerEvent<Cell<*>, Cell<*>>>)
    }

    override fun remove(index: Long): Cell<*> {
        val old = values.remove(index)?.toCell(this, index) ?: UnitCell(this, index)

        if (old !is UnitCell) {
            indices.compute(index) { _, v ->
                when {
                    v == null -> null
                    v - 1 == 0 -> null
                    else -> v - 1
                }
            }
        }

        val new: Cell<*> = UnitCell(this, index)
        table.eventProcessor.publish(listOf(ListenerEvent(old, new)) as List<ListenerEvent<Cell<*>, Cell<*>>>)

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

        // TODO Event processor..
    }

    // TODO We need this (and asSequence?) on CellRange, Table, Row and similar..
    override fun iterator(): Iterator<Cell<*>> {
        return values.asSequence().map { it.value.toCell(this, it.key) }.iterator()
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

class ColumnRange(override val start: Column, override val endInclusive: Column) : ClosedRange<Column>, Iterable<Column> {
    override fun iterator(): Iterator<Column> {
        return if (start.columnOrder <= endInclusive.columnOrder) {
            start
                .table
                .columns
                .dropWhile { it != start }
                .reversed()
                .dropWhile { it != endInclusive }
                .reversed()
                .iterator()
        } else {
            start
                .table
                .columns
                .dropWhile { it != endInclusive }
                .reversed()
                .dropWhile { it != start }
                .iterator()
        }
    }

    override fun contains(value: Column): Boolean {
        if (start.table != value.table) {
            return false
        }

        if (value.columnOrder < kotlin.math.min(start.columnOrder, endInclusive.columnOrder) || value.columnOrder > max(start.columnOrder, endInclusive.columnOrder)) {
            return false
        }

        return true
    }

    override fun isEmpty() = false
}

// TODO Maybe just remove this, or make it extend BaseColumn to fix iterator access issue?
class ReadOnlyRowColumn internal constructor(private val column: Column, private val index: Long) : Column(column.table, column.columnHeader) {
    override fun get(indexRelation: IndexRelation, index: Long): Cell<*> {
        if (index > this.index)
            return UnitCell(this, index);

        return column.get(indexRelation, index)
    }

    override fun set(index: Long, value: Cell<*>?) {
        throw ReadOnlyColumnException()
    }

    override fun remove(index: Long): Cell<*> {
        throw ReadOnlyColumnException()
    }

    override fun clear() {
        throw ReadOnlyColumnException()
    }

    override fun iterator(): Iterator<Cell<*>> {
        // This needs access to the storage..
        TODO("Not yet implemented")
    }
}

// TODO Should we use on rather than at?
enum class IndexRelation {
    AT, AT_OR_BEFORE, AT_OR_AFTER, BEFORE, AFTER
}

class ColumnAction internal constructor(val left: Column, val right: Column, val order: ColumnActionOrder)

enum class ColumnActionOrder { BEFORE, AFTER }

internal val emptyColumnHeader = ColumnHeader()