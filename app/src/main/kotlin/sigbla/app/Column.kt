package sigbla.app

import com.github.andrewoma.dexx.collection.TreeMap as PTreeMap
import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.IndexRelation.*
import sigbla.app.internals.refAction
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.math.max
import kotlin.math.min

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

// TODO Should the be sealed rather than abstract? Or just a normal class with no BaseColumn?
abstract class Column internal constructor(
    val table: Table,
    // TODO Rename to header and order?
    val columnHeader: ColumnHeader,
    val columnOrder: Long
) : Comparable<Column>, Iterable<Cell<*>> {
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

    operator fun get(index: Long) = get(AT, index)

    operator fun get(index: Int) = get(AT, index.toLong())

    operator fun get(indexRelation: IndexRelation, index: Int) = get(indexRelation, index.toLong())

    operator fun get(row: Row) = get(row.indexRelation, row.index)

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

    operator fun set(index: Int, value: Number) = set(index.toLong(), value)

    operator fun set(index: Long, init: Cell<*>.() -> Any?) = this[index] { init() }

    operator fun set(index: Int, init: Cell<*>.() -> Any?) = this[index] { init() }

    // TODO: Look at adding a add(..) function.
    //       Add would just insert a cell at first available location.
    //       Maybe that can be a plus operator function?

    // TODO: Move these out (remove, rename, clear) like with on
    abstract fun clear()

    operator fun rangeTo(other: Column): ColumnRange {
        return ColumnRange(this, other)
    }

    override fun compareTo(other: Column): Int {
        return columnOrder.compareTo(other.columnOrder)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Column

        if (columnHeader != other.columnHeader) return false

        return true
    }

    override fun hashCode() = columnHeader.hashCode()

    override fun toString() = columnHeader.toString()
}

class BaseColumn internal constructor(
    table: Table,
    columnHeader: ColumnHeader,
    columnOrder: Long = table.tableRef.get().columns[columnHeader]?.columnOrder ?: table.tableRef.get().columnCounter.getAndIncrement()
) : Column(
    table,
    columnHeader,
    columnOrder
) {
    override fun get(indexRelation: IndexRelation, index: Long): Cell<*> {
        return getCellRaw(table.tableRef.get(), columnHeader, index, indexRelation)?.first?.toCell(this, index) ?: UnitCell(
            this,
            index
        )
    }

    override fun set(index: Long, value: Cell<*>?) {
        if (value is UnitCell || value == null) {
            clear(index)
            return
        }

        val cellValue = value.toCellValue()

        synchronized(table.eventProcessor) {
            val (oldRef, newRef) = table.tableRef.refAction {
                val meta = it.columns[this.columnHeader] ?: throw InvalidColumnException(this)
                val values = it.columnCells[this.columnHeader] ?: throw InvalidColumnException(this)

                it.copy(
                    columns = if (meta.prenatal) it.columns.put(
                        this.columnHeader,
                        meta.copy(prenatal = false)
                    ) else it.columns,
                    columnCells = it.columnCells.put(this.columnHeader, values.put(index, cellValue)),
                    version = it.version + 1L
                )
            }

            if (!table.eventProcessor.haveListeners()) return

            val oldTable = this.table.makeClone(ref = oldRef)
            val newTable = this.table.makeClone(ref = newRef)

            // TODO This might create a column if it doesn't exist, which we don't want (see events in table ops)
            val old = oldTable[this.columnHeader][index]
            val new = newTable[this.columnHeader][index]

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
                val meta = it.columns[this.columnHeader] ?: throw InvalidColumnException(this)
                val values = it.columnCells[this.columnHeader] ?: throw InvalidColumnException(this)

                it.copy(
                    columns = if (meta.prenatal) it.columns.put(
                        this.columnHeader,
                        meta.copy(prenatal = false)
                    ) else it.columns,
                    columnCells = it.columnCells.put(this.columnHeader, values.remove(index)),
                    version = it.version + 1L
                )
            }

            val oldTable = this.table.makeClone(ref = oldRef)
            val newTable = this.table.makeClone(ref = newRef)

            val old = oldTable[this.columnHeader][index]
            // TODO This will create a column if it doesn't exist, which we don't want (see events in table ops)
            val new = newTable[this.columnHeader][index] // This will be a unit cell, but with new table and column refs

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

    override fun clear() {
        table.tableRef.updateAndGet {
            it.copy(
                columnCells = it.columnCells.put(this.columnHeader, PTreeMap()),
                version = it.version + 1L
            )
        }

        // TODO Event processor..
    }

    override fun iterator(): Iterator<Cell<*>> {
        val values = table.tableRef.get().columnCells[this.columnHeader] ?: throw InvalidColumnException(this)
        return values.asSequence().map { it.component2().toCell(this, it.component1()) }.iterator()
    }
}

internal fun getCellRaw(ref: TableRef, columnHeader: ColumnHeader, index: Long, indexRelation: IndexRelation): Pair<CellValue<*>, Long>? {
    val values = ref.columnCells[columnHeader] ?: return null

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
        AT_OR_BEFORE -> values[index]?.let { Pair(it, index) } ?: getCellRaw(ref, columnHeader, index, BEFORE)
        AT_OR_AFTER -> values[index]?.let { Pair(it, index) } ?: getCellRaw(ref, columnHeader, index, AFTER)
    }
}

class ColumnRange(override val start: Column, override val endInclusive: Column) : ClosedRange<Column>, Iterable<Column> {
    val table: Table
        get() = start.table

    override fun iterator(): Iterator<Column> {
        val ref = table.tableRef.get()

        // Because columns might move around, get the latest order
        val currentStart = ref.columns[start.columnHeader]?.columnOrder ?: throw InvalidColumnException("Unable to find column $start")
        val currentEnd = ref.columns[endInclusive.columnHeader]?.columnOrder ?: throw InvalidColumnException("Unable to find column $endInclusive")

        val minOrder = min(currentStart, currentEnd)
        val maxOrder = max(currentStart, currentEnd)

        return ref
            .headers
            .filter { !it.second.prenatal }
            .filter { it.second.columnOrder in minOrder..maxOrder }
            .map { BaseColumn(table, it.first, it.second.columnOrder) }
            .iterator()
    }

    override fun contains(value: Column): Boolean {
        // TODO Because columns might move around, get the latest order
        //      See iterator above..
        if (value.columnOrder < min(start.columnOrder, endInclusive.columnOrder) || value.columnOrder > max(start.columnOrder, endInclusive.columnOrder)) {
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

enum class IndexRelation {
    BEFORE, AT_OR_BEFORE, AT, AT_OR_AFTER, AFTER
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

internal val emptyColumnHeader = ColumnHeader()