package sigbla.app

import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.Registry
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentNavigableMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicInteger

// Look at this wrt Table, Column, Row: https://kotlinlang.org/docs/reference/operator-overloading.html

// TODO: Meta data / meta cells: Cells that are always in scope? Need something for the view, to load libs and stuff

abstract class Table(val name: String) {
    @Volatile
    var closed: Boolean = false
        internal set

    internal val columnCounter = AtomicInteger()

    abstract val headers: Collection<ColumnHeader>

    internal abstract val eventProcessor: TableEventProcessor

    abstract operator fun get(header: ColumnHeader): Column

    operator fun get(vararg header: String): Column = get(
        ColumnHeader(
            *header
        )
    )

    operator fun get(index: Long): Row = get(IndexRelation.AT, index)

    operator fun get(indexRelation: IndexRelation, index: Long): Row =
        BaseRow(this, indexRelation, index)

    operator fun get(header1: String, index: Long): Cell<*> = this[header1][index]

    operator fun get(header1: String, header2: String, index: Long): Cell<*> = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Long): Cell<*> = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Long): Cell<*> = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long): Cell<*> = this[header1, header2, header3, header4, header5][index]

    operator fun get(header1: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1][indexRelation, index]

    operator fun get(header1: String, header2: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2, header3][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2, header3, header4][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2, header3, header4, header5][indexRelation, index]

    operator fun get(index: Int): Row = get(IndexRelation.AT, index)

    operator fun get(indexRelation: IndexRelation, index: Int): Row =
        BaseRow(this, indexRelation, index.toLong())

    operator fun get(header1: String, index: Int): Cell<*> = this[header1][index]

    operator fun get(header1: String, header2: String, index: Int): Cell<*> = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Int): Cell<*> = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Int): Cell<*> = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int): Cell<*> = this[header1, header2, header3, header4, header5][index]

    operator fun get(header1: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1][indexRelation, index]

    operator fun get(header1: String, header2: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2, header3][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2, header3, header4][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2, header3, header4, header5][indexRelation, index]

    operator fun set(header1: String, index: Long, value: Cell<*>?) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: Cell<*>?) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: Cell<*>?) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: Cell<*>?) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: Cell<*>?) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: String) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: String) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: String) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: String) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: String) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: Long) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: Long) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: Long) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: Long) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: Long) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: Double) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: Double) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: Double) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: Double) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: Double) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: BigInteger) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: BigInteger) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: BigInteger) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: BigInteger) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: BigInteger) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: BigDecimal) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: BigDecimal) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: BigDecimal) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: BigDecimal) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: BigDecimal) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: Cell<*>) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: Cell<*>) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: Cell<*>) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: Cell<*>) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: Cell<*>) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: String) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: String) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: String) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: String) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: String) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: Long) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: Long) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: Long) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: Long) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: Long) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: Double) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: Double) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: Double) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: Double) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: Double) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: BigInteger) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: BigInteger) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: BigInteger) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: BigInteger) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: BigInteger) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: BigDecimal) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: BigDecimal) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: BigDecimal) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: BigDecimal) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: BigDecimal) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    abstract operator fun contains(header: ColumnHeader): Boolean

    fun contains(vararg header: String): Boolean = contains(ColumnHeader(*header))

    abstract fun remove(header: ColumnHeader)

    fun rename(existing: ColumnHeader, vararg newName: String) = rename(existing,
        ColumnHeader(*newName)
    )

    abstract fun rename(existing: ColumnHeader, newName: ColumnHeader)

    fun remove(vararg header: String) = remove(ColumnHeader(*header))

    fun remove(index: Long) = this.headers.forEach { c -> this[c].remove(index) }

    inline fun <reified O, reified N> subscribe(crossinline listener: (eventReceiver: ListenerEventReceiver<Table, O, N>) -> Unit): ListenerReference {
        return subscribeAny { receiver ->
            val events = receiver.events.filter {
                it.oldValue.value is O && it.newValue.value is N
            } as List<ListenerEvent<out O, out N>>
            if (events.isNotEmpty()) listener.invoke(ListenerEventReceiver(this, receiver.listenerReference, events))
        }
    }

    fun subscribeAny(listener: (eventReceiver: ListenerEventReceiver<Table, *, *>) -> Unit): ListenerReference {
        return eventProcessor.subscribe(this) {
            if (it.events.isNotEmpty()) listener.invoke(ListenerEventReceiver(this, it.listenerReference, it.events))
        }
    }

    companion object {
        fun newTable(name: String): Table =
            BaseTable(name)

        fun fromRegistry(name: String): Table = Registry.getTable(name) ?: throw InvalidTableException("No table by name $name")

        fun fromStorage(storage: Storage, name: String): Table {
            TODO()
        }

        fun fromStorageAs(storage: Storage, name: String, newName: String): Table {
            TODO()
        }

        fun fromStorageRange(storage: Storage, name: String, fromIndex: Long, toIndex: Long): Table {
            TODO()
        }

        fun fromStorageRangeAs(storage: Storage, name: String, fromIndex: Long, toIndex: Long, newName: String): Table {
            TODO()
        }

        fun registryTableNames(): SortedSet<String> = Registry.tableNames()

        fun deleteTable(name: String) = Registry.deleteTable(name)

        fun move(columnAction: ColumnAction): Unit = TODO()

        fun move(columnAction: ColumnAction, withName: ColumnHeader): Unit = TODO()

        fun move(columnAction: ColumnAction, vararg withName: String): Unit = TODO()

        fun move(left: Column, actionOrder: ColumnActionOrder, right: Column): Unit = TODO()

        fun move(left: Column, actionOrder: ColumnActionOrder, right: Column, withName: ColumnHeader): Unit = TODO()

        fun move(left: Column, actionOrder: ColumnActionOrder, right: Column, vararg withName: String): Unit = TODO()

        fun copy(columnAction: ColumnAction): Unit = TODO()

        fun copy(columnAction: ColumnAction, withName: ColumnHeader): Unit = TODO()

        fun copy(columnAction: ColumnAction, vararg withName: String): Unit = TODO()

        fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column): Unit = TODO()

        fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column, withName: ColumnHeader): Unit = TODO()

        fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column, vararg withName: String): Unit = TODO()

        /*
        inline fun <reified T> subscribe(table: Table, crossinline listener: (eventReceiver: ListenerEventReceiver<Table, T>) -> Unit): ListenerReference {
            return table.subscribe(listener)
        }

        fun subscribeAny(table: Table, listener: (eventReceiver: ListenerEventReceiver<Table, *>) -> Unit): ListenerReference {
            return table.subscribeAny(listener)
        }

        inline fun <reified T> subscribe(column: Column, crossinline listener: (eventReceiver: ListenerEventReceiver<Column, T>) -> Unit): ListenerReference {
            return column.subscribe(listener)
        }

        fun subscribeAny(column: Column, listener: (eventReceiver: ListenerEventReceiver<Column, *>) -> Unit): ListenerReference {
            return column.subscribeAny(listener)
        }

        inline fun <reified T> subscribe(row: Row, crossinline listener: (eventReceiver: ListenerEventReceiver<Row, T>) -> Unit): ListenerReference {
            return row.subscribe(listener)
        }

        fun subscribeAny(row: Row, listener: (eventReceiver: ListenerEventReceiver<Row, *>) -> Unit): ListenerReference {
            return row.subscribeAny(listener)
        }

        inline fun <reified T> subscribe(cellRange: CellRange, crossinline listener: (eventReceiver: ListenerEventReceiver<CellRange, T>) -> Unit): ListenerReference {
            return cellRange.subscribe(listener)
        }

        fun subscribeAny(cellRange: CellRange, listener: (eventReceiver: ListenerEventReceiver<CellRange, *>) -> Unit): ListenerReference {
            return cellRange.subscribeAny(listener)
        }

        inline fun <reified T> subscribe(cell: Cell<*>, crossinline listener: (eventReceiver: ListenerEventReceiver<Cell<*>, T>) -> Unit): ListenerReference {
            return cell.subscribe(listener)
        }

        fun subscribeAny(cell: Cell<*>, listener: (eventReceiver: ListenerEventReceiver<Cell<*>, *>) -> Unit): ListenerReference {
            return cell.subscribeAny(listener)
        }
         */
    }
}

class BaseTable internal constructor(
    name: String,
    internal val columns: ConcurrentMap<ColumnHeader, Column> = ConcurrentHashMap(),
    internal val indices: ConcurrentNavigableMap<Long, Int> = ConcurrentSkipListMap(),
    override val eventProcessor: TableEventProcessor = TableEventProcessor()
) : Table(name) {
    init {
        Registry.setTable(name, this)
    }

    override val headers: Collection<ColumnHeader>
        get() = columns
            .entries
            .sortedBy { it.value.columnOrder }
            .map { it.key }
            .toList()

    override fun get(header: ColumnHeader): Column = columns.computeIfAbsent(header) {
        if (closed)
            throw InvalidTableException("Table is closed")

        BaseColumn(this, header, indices)
    }

    override fun contains(header: ColumnHeader): Boolean = columns.containsKey(header)

    override fun remove(header: ColumnHeader) {
        columns.remove(header)?.clear()
    }

    override fun rename(existing: ColumnHeader, newName: ColumnHeader) {
        val column = columns[existing] ?: return

        columns.put(newName, column)?.clear()
        columns.remove(existing, column)
    }

    companion object
}
