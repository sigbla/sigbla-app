package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.EventReceiver
import sigbla.app.internals.ListenerReference
import sigbla.app.internals.Registry
import sigbla.app.internals.TableEventProcessor
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentNavigableMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

// Look at this wrt Table, Column, Row: https://kotlinlang.org/docs/reference/operator-overloading.html

// TODO: Meta data / meta cells: Cells that are always in scope? Need something for the view, to load libs and stuff

// TODO: Need a table clear like we have on columns..

abstract class Table(val name: String) {
    @Volatile
    var closed: Boolean = false
        internal set

    internal val columnCounter = AtomicInteger()

    abstract val headers: Collection<ColumnHeader>

    abstract val columns: Collection<Column>

    internal abstract val columnsMap: ConcurrentMap<ColumnHeader, Column>

    internal abstract val indicesMap: ConcurrentNavigableMap<Long, Int>

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

    operator fun set(header1: String, index: Long, value: Number) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: Number) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: Number) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: Number) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: Number) {
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

    operator fun set(header1: String, index: Int, value: Number) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: Number) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: Number) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: Number) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: Number) {
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

    inline fun <reified O, reified N> on(noinline init: EventReceiver<Table, O, N>.() -> Unit): ListenerReference {
        return on(O::class, N::class, init as EventReceiver<Table, Any, Any>.() -> Unit)
    }

    fun onAny(init: EventReceiver<Table, Any, Any>.() -> Unit): ListenerReference {
        return on(Any::class, Any::class, init)
    }

    fun on(old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: EventReceiver<Table, Any, Any>.() -> Unit): ListenerReference {
        val eventReceiver = when {
            old == Any::class && new == Any::class -> EventReceiver<Table, Any, Any>(
                this
            ) { this }
            old == Any::class -> EventReceiver(this) {
                this.filter {
                    new.isInstance(
                        it.newValue.value
                    )
                }
            }
            new == Any::class -> EventReceiver(this) {
                this.filter {
                    old.isInstance(
                        it.oldValue.value
                    )
                }
            }
            else -> EventReceiver(this) {
                this.filter {
                    old.isInstance(it.oldValue.value) && new.isInstance(
                        it.newValue.value
                    )
                }
            }
        }
        return eventProcessor.subscribe(this, eventReceiver, init)
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

        inline fun <reified O, reified N> on(table: Table, noinline init: EventReceiver<Table, O, N>.() -> Unit): ListenerReference {
            return table.on(init)
        }

        fun onAny(table: Table, init: EventReceiver<Table, Any, Any>.() -> Unit): ListenerReference {
            return table.onAny(init)
        }

        fun on(table: Table, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: EventReceiver<Table, Any, Any>.() -> Unit): ListenerReference {
            return table.on(old, new, init)
        }

        // ---

        inline fun <reified O, reified N> on(column: Column, noinline init: EventReceiver<Column, O, N>.() -> Unit): ListenerReference {
            return column.on(init)
        }

        fun onAny(column: Column, init: EventReceiver<Column, Any, Any>.() -> Unit): ListenerReference {
            return column.onAny(init)
        }

        fun on(column: Column, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: EventReceiver<Column, Any, Any>.() -> Unit): ListenerReference {
            return column.on(old, new, init)
        }

        // ---

        inline fun <reified O, reified N> on(row: Row, noinline init: EventReceiver<Row, O, N>.() -> Unit): ListenerReference {
            return row.on(init)
        }

        fun onAny(row: Row, init: EventReceiver<Row, Any, Any>.() -> Unit): ListenerReference {
            return row.onAny(init)
        }

        fun on(row: Row, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: EventReceiver<Row, Any, Any>.() -> Unit): ListenerReference {
            return row.on(old, new, init)
        }

        // ---

        inline fun <reified O, reified N> on(cellRange: CellRange, noinline init: EventReceiver<CellRange, O, N>.() -> Unit): ListenerReference {
            return cellRange.on(init)
        }

        fun onAny(cellRange: CellRange, init: EventReceiver<CellRange, Any, Any>.() -> Unit): ListenerReference {
            return cellRange.onAny(init)
        }

        fun on(cellRange: CellRange, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: EventReceiver<CellRange, Any, Any>.() -> Unit): ListenerReference {
            return cellRange.on(old, new, init)
        }

        // ---

        inline fun <reified O, reified N> on(cell: Cell<*>, noinline init: EventReceiver<Cell<*>, O, N>.() -> Unit): ListenerReference {
            return cell.on(init)
        }

        fun onAny(cell: Cell<*>, init: EventReceiver<Cell<*>, Any, Any>.() -> Unit): ListenerReference {
            return cell.onAny(init)
        }

        fun on(cell: Cell<*>, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: EventReceiver<Cell<*>, Any, Any>.() -> Unit): ListenerReference {
            return cell.on(old, new, init)
        }
    }
}

class BaseTable internal constructor(
    name: String,
    override val columnsMap: ConcurrentMap<ColumnHeader, Column> = ConcurrentHashMap(),
    override val indicesMap: ConcurrentNavigableMap<Long, Int> = ConcurrentSkipListMap(),
    override val eventProcessor: TableEventProcessor = TableEventProcessor()
) : Table(name) {
    init {
        Registry.setTable(name, this)
    }

    override val headers: Collection<ColumnHeader>
        get() = columnsMap
            .entries
            .sortedBy { it.value.columnOrder }
            .map { it.key }
            .toList()

    override val columns: Collection<Column>
        get() = columnsMap
            .entries
            .sortedBy { it.value.columnOrder }
            .map { it.value }
            .toList()

    // TODO Column add event
    override fun get(header: ColumnHeader): Column = columnsMap.computeIfAbsent(header) {
        if (closed) throw InvalidTableException("Table is closed")
        if (header.header.isEmpty()) throw InvalidColumnException("Empty header")

        BaseColumn(this, header, indicesMap)
    }

    override fun contains(header: ColumnHeader): Boolean = columnsMap.containsKey(header)

    // TODO Column remove event
    override fun remove(header: ColumnHeader) {
        columnsMap.remove(header)?.clear()
    }

    // TODO Column rename event
    override fun rename(existing: ColumnHeader, newName: ColumnHeader) {
        val column = columnsMap[existing] ?: return

        columnsMap.put(newName, column)?.clear()
        columnsMap.remove(existing, column)
    }

    companion object
}
