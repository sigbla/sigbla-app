package sigbla.app

import com.github.andrewoma.dexx.collection.TreeMap as PTreeMap
import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.ReadOnlyColumnException
import sigbla.app.IndexRelation.*
import sigbla.app.internals.refAction
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

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

abstract class Column internal constructor(
    val table: Table,
    val columnHeader: ColumnHeader,
    internal val columnOrder: Int
) : Comparable<Column>, Iterable<Cell<*>> {
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

    operator fun set(index: Long, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[index]).init()

    operator fun set(index: Int, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[index]).init()

    // TODO: Look at adding a add(..) function and asSequence() function.
    //       Add would just insert a cell at first available location.

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

    inline fun <reified O, reified N> on(noinline init: TableEventReceiver<Column, O, N>.() -> Unit): TableListenerReference {
        return on(O::class, N::class, init as TableEventReceiver<Column, Any, Any>.() -> Unit)
    }

    fun onAny(init: TableEventReceiver<Column, Any, Any>.() -> Unit): TableListenerReference {
        return on(Any::class, Any::class, init)
    }

    fun on(old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Column, Any, Any>.() -> Unit): TableListenerReference {
        val eventReceiver = when {
            old == Any::class && new == Any::class -> TableEventReceiver<Column, Any, Any>(
                this
            ) { this }
            old == Any::class -> TableEventReceiver(this) {
                this.filter {
                    new.isInstance(
                        it.newValue.value
                    )
                }
            }
            new == Any::class -> TableEventReceiver(this) {
                this.filter {
                    old.isInstance(
                        it.oldValue.value
                    )
                }
            }
            else -> TableEventReceiver(this) {
                this.filter {
                    old.isInstance(it.oldValue.value) && new.isInstance(
                        it.newValue.value
                    )
                }
            }
        }
        return table.eventProcessor.subscribe(this, eventReceiver, init)
    }

    override fun compareTo(other: Column): Int {
        return columnOrder.compareTo(other.columnOrder)
    }

    override fun toString(): String {
        return columnHeader.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Column

        if (columnHeader != other.columnHeader) return false

        return true
    }

    override fun hashCode(): Int {
        return columnHeader.hashCode()
    }
}

class BaseColumn internal constructor(
    table: Table,
    columnHeader: ColumnHeader,
    private val tableRef: AtomicReference<TableRef>
) : Column(
    table,
    columnHeader,
    tableRef.get().columnsMap[columnHeader]?.columnOrder ?: table.columnCounter.getAndIncrement()
) {
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

        val cellValue = value.toCellValue()

        val (oldRef, newRef) = tableRef.refAction {
            val values = it.columnCellMap[this] ?: throw InvalidColumnException()

            it.copy(
                columnCellMap = it.columnCellMap.put(this, values.put(index, cellValue)),
                indicesMap = if (values[index] != null) it.indicesMap else it.indicesMap.put(index, it.indicesMap[index].let { v ->
                    when (v) {
                        null -> 1
                        else -> v + 1
                    }
                })
            )
        }

        if (!table.eventProcessor.haveListeners()) return

        val oldTable = this.table.makeClone(ref = oldRef)
        val newTable = this.table.makeClone(ref = newRef)

        val old = oldTable[this.columnHeader][index]
        val new = newTable[this.columnHeader][index]

        table.eventProcessor.publish(listOf(
            TableListenerEvent(
                old,
                new
            )
        ) as List<TableListenerEvent<Any, Any>>)
    }

    override fun remove(index: Long): Cell<*> {
        val (oldRef, newRef) = tableRef.refAction {
            val values = it.columnCellMap[this] ?: throw InvalidColumnException()

            it.copy(
                columnCellMap = it.columnCellMap.put(this, values.remove(index)),
                indicesMap = it.indicesMap.put(index, it.indicesMap[index].let { v ->
                    when {
                        v == null -> null
                        v - 1 == 0 -> null
                        else -> v - 1
                    }
                })
            )
        }

        val oldTable = this.table.makeClone(ref = oldRef)
        val newTable = this.table.makeClone(ref = newRef)

        val old = oldTable[this.columnHeader][index]
        val new = newTable[this.columnHeader][index] // This will be a unit cell, but with new table and column refs

        if (!table.eventProcessor.haveListeners()) return old

        table.eventProcessor.publish(listOf(
            TableListenerEvent(
                old,
                new
            )
        ) as List<TableListenerEvent<Any, Any>>)

        return old
    }

    override fun clear() {
        tableRef.updateAndGet {
            val values = it.columnCellMap[this] ?: throw InvalidColumnException()
            val indices = values.keys().fold(it.indicesMap) { acc, index ->
                acc.put(index, acc[index].let { v ->
                    when {
                        v == null -> null
                        v - 1 == 0 -> null
                        else -> v - 1
                    }
                })
            }

            it.copy(
                indicesMap = indices,
                columnCellMap = it.columnCellMap.put(this, PTreeMap())
            )
        }

        // TODO Event processor..
    }

    // TODO We need this on CellRange, Table, Row and similar.. (done?)
    override fun iterator(): Iterator<Cell<*>> {
        val values = tableRef.get().columnCellMap[this] ?: throw InvalidColumnException()
        return values.asSequence().map { it.component2().toCell(this, it.component1()) }.iterator()
    }

    private fun getCellRaw(index: Long, indexRelation: IndexRelation): CellValue<*>? {
        val ref = tableRef.get()
        val indices = ref.indicesMap.asSortedMap()
        val values = ref.columnCellMap[this] ?: throw InvalidColumnException()

        fun firstBefore(): CellValue<*>? {
            for (i in indices.headMap(index).keys.sortedDescending()) {
                if (values.containsKey(i)) {
                    return values[i]
                }
            }
            return null
        }

        fun firstAfter(): CellValue<*>? {
            for (i in indices.tailMap(index + 1L).keys) {
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
            AT_OR_BEFORE -> values[index] ?: run { getCellRaw(index, BEFORE) }
            AT_OR_AFTER -> values[index] ?: run { getCellRaw(index, AFTER) }
        }
    }
}

class ColumnRange(override val start: Column, override val endInclusive: Column) : ClosedRange<Column>, Iterable<Column> {
    val table: Table
        get() = start.table

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
        if (value.columnOrder < min(start.columnOrder, endInclusive.columnOrder) || value.columnOrder > max(start.columnOrder, endInclusive.columnOrder)) {
            return false
        }

        return true
    }

    override fun isEmpty() = false

    override fun toString(): String {
        return "$start..$endInclusive"
    }
}

// TODO Should we use on rather than at?
enum class IndexRelation {
    AT, AT_OR_BEFORE, AT_OR_AFTER, BEFORE, AFTER
}

class ColumnAction internal constructor(val left: Column, val right: Column, val order: ColumnActionOrder)

enum class ColumnActionOrder { BEFORE, AFTER }

internal val emptyColumnHeader = ColumnHeader()