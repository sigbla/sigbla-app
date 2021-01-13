package sigbla.app

import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.Registry
import sigbla.app.internals.SigblaBackend
import sigbla.app.internals.TableViewEventProcessor
import sigbla.app.internals.refAction
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.NoSuchElementException
import kotlin.reflect.KClass
import com.github.andrewoma.dexx.collection.Map as PMap
import com.github.andrewoma.dexx.collection.HashMap as PHashMap

// A table view is associated with one table, and holds meta data related on how to view a table.
// This includes among other things column widths, row heights, individual cell dimensions, styling, etc..

// TODO Introduce a concept of a view transformer. As with views, this can be on column, row or cell.
//      Some priority ordering might be needed between these, but the transformer would take a value
//      and convert this into another value. This only affects the view, and is done on demand (i.e.,
//      when the cell is being rendered), and is used to perform things like formatting etc..
//
//      Look at having this in the form of view["column"] = { .. } where this is given a cell and
//      expected to return a cell.

// TODO Events on a table view should be much like on a table, with view.onAny or view.on<ColumnView>.
//      Note that we only have one generic type, as we can't have one type of view convert to another.
//
//      A listener can, as with table, call back into the table view to change the view if wanted. And we
//      use ordering to fire off listeners in the right order. This allows for the final UI listener
//      to fire after any earlier modifications. A loop detector is also included.

abstract class TableView(val name: String) : Iterable<Area<*>> {
    abstract var table: Table?

    internal abstract val tableViewRef: AtomicReference<TableViewRef>

    internal abstract val eventProcessor: TableViewEventProcessor

    abstract operator fun get(type: DEFAULT_COLUMN_VIEW): DefaultColumnView

    abstract operator fun set(type: DEFAULT_COLUMN_VIEW, init: DefaultColumnViewBuilder.() -> Unit)

    abstract operator fun set(type: DEFAULT_COLUMN_VIEW, defaultColumnView: DefaultColumnView)

    abstract operator fun get(type: DEFAULT_ROW_VIEW): DefaultRowView

    abstract operator fun set(type: DEFAULT_ROW_VIEW, init: DefaultRowViewBuilder.() -> Unit)

    abstract operator fun set(type: DEFAULT_ROW_VIEW, defaultRowView: DefaultRowView)

    abstract operator fun get(columnHeader: ColumnHeader): ColumnView

    operator fun get(column: Column) = get(column.columnHeader)

    abstract operator fun get(row: Long): RowView

    operator fun get(row: Row) = get(row.index)

    abstract operator fun get(columnHeader: ColumnHeader, row: Long): CellView

    operator fun get(column: Column, row: Long) = get(column.columnHeader, row)

    operator fun get(cell: Cell<*>) = get(cell.column, cell.index)

    abstract operator fun set(columnHeader: ColumnHeader, init: ColumnViewBuilder.() -> Unit)

    operator fun set(column: Column, init: ColumnViewBuilder.() -> Unit) = set(column.columnHeader, init)

    abstract operator fun set(columnHeader: ColumnHeader, columnView: ColumnView?)

    operator fun set(column: Column, columnView: ColumnView?) = set(column.columnHeader, columnView)

    abstract operator fun set(row: Long, init: RowViewBuilder.() -> Unit)

    abstract operator fun set(row: Long, rowView: RowView?)

    operator fun set(row: Row, rowView: RowView?) = set(row.index, rowView)

    abstract operator fun set(columnHeader: ColumnHeader, row: Long, init: CellViewBuilder.() -> Unit)

    operator fun set(column: Column, row: Long, init: CellViewBuilder.() -> Unit) = set(column.columnHeader, row, init)

    abstract operator fun set(columnHeader: ColumnHeader, row: Long, cellView: CellView?)

    operator fun set(column: Column, row: Long, cellView: CellView?) = set(column.columnHeader, row, cellView)

    operator fun set(cell: Cell<*>, cellView: CellView?) = set(cell.column, cell.index, cellView)

    abstract fun remove(columnHeader: ColumnHeader)

    fun remove(column: Column) = remove(column.columnHeader)

    abstract fun remove(row: Long)

    fun remove(row: Row) = remove(row.index)

    abstract fun remove(columnHeader: ColumnHeader, row: Long)

    fun remove(column: Column, row: Long) = remove(column.columnHeader, row)

    fun remove(cell: Cell<*>) = remove(cell.column, cell.index)

    fun show() = SigblaBackend.openView(this)

    // TODO Move this and other table view on functions out..
    inline fun <reified T> on(noinline init: TableViewEventReceiver<TableView, T>.() -> Unit): TableViewListenerReference {
        return on(T::class, init as TableViewEventReceiver<TableView, Any>.() -> Unit)
    }

    fun onAny(init: TableViewEventReceiver<TableView, Any>.() -> Unit): TableViewListenerReference {
        return on(Any::class, init)
    }

    fun on(type: KClass<*> = Any::class, init: TableViewEventReceiver<TableView, Any>.() -> Unit): TableViewListenerReference {
        val eventReceiver = when {
            type == Any::class -> TableViewEventReceiver<TableView, Any>(
                this
            ) { this }
            else -> TableViewEventReceiver(this) {
                this.filter {
                    type.isInstance(it.oldValue.value) || type.isInstance(it.newValue.value)
                }
            }
        }
        return eventProcessor.subscribe(this, eventReceiver, init)
    }

    override fun iterator(): Iterator<Area<*>> {
        val view = this

        return object : Iterator<Area<*>> {
            val ref = tableViewRef.get()
            val tableIterator = if (ref.table != null) listOf(ref.table).iterator() else emptyList<Table>().iterator()
            val defaultIterator = listOf(ref.defaultColumnView, ref.defaultRowView).iterator()
            val columnViewIterator = ref.columnViews.values().iterator()
            val rowViewIterator = ref.rowViews.values().iterator()
            val cellViewIterator = ref.cellViews.values().iterator()

            override fun hasNext(): Boolean {
                return tableIterator.hasNext() || defaultIterator.hasNext() || columnViewIterator.hasNext() || rowViewIterator.hasNext() || cellViewIterator.hasNext()
            }

            override fun next(): Area<*> {
                return when {
                    tableIterator.hasNext() -> Area(view, tableIterator.next())
                    defaultIterator.hasNext() -> Area(view, defaultIterator.next())
                    columnViewIterator.hasNext() -> Area(view, columnViewIterator.next())
                    rowViewIterator.hasNext() -> Area(view, rowViewIterator.next())
                    cellViewIterator.hasNext() -> Area(view, cellViewIterator.next())
                    else -> throw NoSuchElementException()
                }
            }
        }
    }

    abstract fun clone(): TableView

    abstract fun clone(name: String): TableView

    internal abstract fun makeClone(name: String = this.name, onRegistry: Boolean = false, ref: TableViewRef = tableViewRef.get()!!): TableView

    companion object {
        operator fun get(name: String): TableView = BaseTableView(name)

        operator fun get(table: Table): TableView = BaseTableView(table)

        operator fun get(name: String, table: Table): TableView = BaseTableView(name, table)

        fun fromRegistry(name: String): TableView = Registry.getView(name) ?: throw InvalidTableException("No table view by name $name")

        fun fromStorage(storage: Storage, name: String): TableView {
            TODO()
        }

        fun fromStorageAs(storage: Storage, name: String, newName: String): TableView {
            TODO()
        }

        fun fromStorageRange(storage: Storage, name: String, fromIndex: Long, toIndex: Long): TableView {
            TODO()
        }

        fun fromStorageRangeAs(storage: Storage, name: String, fromIndex: Long, toIndex: Long, newName: String): TableView {
            TODO()
        }

        val names: SortedSet<String> get() = Registry.viewNames()

        fun delete(name: String) = Registry.deleteView(name)
    }
}

internal data class TableViewRef(
    val defaultColumnView: DefaultColumnView,
    val defaultRowView: DefaultRowView,
    val columnViews: PMap<ColumnHeader, ColumnView> = PHashMap(),
    val rowViews: PMap<Long, RowView> = PHashMap(),
    val cellViews: PMap<Pair<ColumnHeader, Long>, CellView> = PHashMap(),
    val table: Table? = null
)

class BaseTableView internal constructor(
    name: String,
    table: Table?, // TODO Don't like how table is a param here when it's part of viewRef
    onRegistry: Boolean = true,
    tableViewRef: AtomicReference<TableViewRef>? = null,
    override val eventProcessor: TableViewEventProcessor = TableViewEventProcessor()
) : TableView(name) {
    constructor(table: Table) : this(table.name, table)
    constructor(name: String) : this(name, Registry.getTable(name))

    override val tableViewRef: AtomicReference<TableViewRef> = tableViewRef ?: AtomicReference(TableViewRef(
        DefaultColumnView(),
        DefaultRowView(),
        table = table
    ))

    init {
        if (onRegistry) Registry.setView(name, this)
    }

    override var table
        get() = tableViewRef.get().table
        set(table) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    table = table
                )
            }

            if (!eventProcessor.haveListeners()) return

            eventProcessor.publish(listOf(
                TableViewListenerEvent(
                    Area(this, oldRef.table),
                    Area(this, newRef.table)
                )
            ) as List<TableViewListenerEvent<Any>>)
        }

    override operator fun get(type: DEFAULT_COLUMN_VIEW): DefaultColumnView = tableViewRef.get().defaultColumnView

    override operator fun set(type: DEFAULT_COLUMN_VIEW, init: DefaultColumnViewBuilder.() -> Unit) {
        val defaultColumnViewBuilder = DefaultColumnViewBuilder()
        defaultColumnViewBuilder.init()
        set(type, defaultColumnViewBuilder.build())
    }

    override operator fun set(type: DEFAULT_COLUMN_VIEW, defaultColumnView: DefaultColumnView) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                defaultColumnView = defaultColumnView
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[DEFAULT_COLUMN_VIEW]
        val new = newView[DEFAULT_COLUMN_VIEW]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                Area(this, old),
                Area(this, new)
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    override operator fun get(type: DEFAULT_ROW_VIEW): DefaultRowView = tableViewRef.get().defaultRowView

    override operator fun set(type: DEFAULT_ROW_VIEW, init: DefaultRowViewBuilder.() -> Unit) {
        val defaultRowViewBuilder = DefaultRowViewBuilder()
        defaultRowViewBuilder.init()
        set(type, defaultRowViewBuilder.build())
    }

    override operator fun set(type: DEFAULT_ROW_VIEW, defaultRowView: DefaultRowView) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                defaultRowView = defaultRowView
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[DEFAULT_ROW_VIEW]
        val new = newView[DEFAULT_ROW_VIEW]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                Area(this, old),
                Area(this, new)
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    override fun get(columnHeader: ColumnHeader): ColumnView = tableViewRef.get().columnViews[columnHeader] ?: this[DEFAULT_COLUMN_VIEW].toColumnView(columnHeader)

    override fun get(row: Long): RowView = tableViewRef.get().rowViews[row] ?: this[DEFAULT_ROW_VIEW].toRowView(row)

    override fun get(columnHeader: ColumnHeader, row: Long): CellView = tableViewRef.get().cellViews[Pair(columnHeader, row)] ?: CellView(this[DEFAULT_ROW_VIEW].height, this[DEFAULT_COLUMN_VIEW].width, columnHeader, row)

    override fun set(columnHeader: ColumnHeader, init: ColumnViewBuilder.() -> Unit) {
        val columnViewBuilder = ColumnViewBuilder()
        columnViewBuilder.init()
        set(columnHeader, columnViewBuilder.build(columnHeader))
    }

    override fun set(columnHeader: ColumnHeader, columnView: ColumnView?) {
        if (columnView == null) {
            remove(columnHeader)
        } else {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    columnViews = it.columnViews.put(columnHeader, columnView.ensureView(columnHeader))
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val old = oldView[columnHeader]
            val new = newView[columnHeader]

            eventProcessor.publish(listOf(
                TableViewListenerEvent(
                    Area(this, old),
                    Area(this, new)
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    override fun set(row: Long, init: RowViewBuilder.() -> Unit) {
        val rowViewBuilder = RowViewBuilder()
        rowViewBuilder.init()
        set(row, rowViewBuilder.build(row))
    }

    override fun set(row: Long, rowView: RowView?) {
        if (rowView == null) {
            remove(row)
        } else {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    rowViews = it.rowViews.put(row, rowView.ensureView(row))
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val old = oldView[row]
            val new = newView[row]

            eventProcessor.publish(listOf(
                TableViewListenerEvent(
                    Area(this, old),
                    Area(this, new)
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    override fun set(columnHeader: ColumnHeader, row: Long, init: CellViewBuilder.() -> Unit) {
        val cellViewBuilder = CellViewBuilder()
        cellViewBuilder.init()
        set(columnHeader, row, cellViewBuilder.build(columnHeader, row))
    }

    override fun set(columnHeader: ColumnHeader, row: Long, cellView: CellView?) {
        if (cellView == null) {
            remove(columnHeader, row)
        } else {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    cellViews = it.cellViews.put(Pair(columnHeader, row), cellView.ensureView(columnHeader, row))
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val old = oldView[columnHeader, row]
            val new = newView[columnHeader, row]

            eventProcessor.publish(listOf(
                TableViewListenerEvent(
                    Area(this, old),
                    Area(this, new)
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    override fun remove(columnHeader: ColumnHeader) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                columnViews = it.columnViews.remove(columnHeader)
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[columnHeader]
        val new = newView[columnHeader]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                Area(this, old),
                Area(this, new)
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    override fun remove(row: Long) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                rowViews = it.rowViews.remove(row)
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[row]
        val new = newView[row]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                Area(this, old),
                Area(this, new)
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    override fun remove(columnHeader: ColumnHeader, row: Long) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                cellViews = it.cellViews.remove(Pair(columnHeader, row))
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[columnHeader, row]
        val new = newView[columnHeader, row]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                Area(this, old),
                Area(this, new)
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    override fun clone(): TableView {
        return makeClone()
    }

    override fun clone(name: String): TableView {
        return makeClone(name, true)
    }

    override fun makeClone(name: String, onRegistry: Boolean, ref: TableViewRef): TableView {
        val newViewRef = AtomicReference(ref)
        val tableViewClone = BaseTableView(name, ref.table, onRegistry, newViewRef)

        val newDefaultColumnView = ref.defaultColumnView
        val newDefaultRowView = ref.defaultRowView

        // TODO: Like with table clone, something more efficient than this would be nice
        val newColumnsViews = ref.columnViews.fold(PHashMap<ColumnHeader, ColumnView>()) { acc, chc ->
            acc.put(chc.component1(), ColumnView(chc.component2().width, chc.component1()))
        }

        // TODO: Like with table clone, something more efficient than this would be nice
        val newRowViews = ref.rowViews.fold(PHashMap<Long, RowView>()) { acc, lrs ->
            acc.put(lrs.component1(), RowView(lrs.component2().height, lrs.component1()))
        }

        // TODO: Like with table clone, something more efficient than this would be nice
        val newCellViews = ref.cellViews.fold(PHashMap<Pair<ColumnHeader, Long>, CellView>()) { acc, chlcs ->
            acc.put(chlcs.component1(), CellView(chlcs.component2().height, chlcs.component2().width, chlcs.component1().first, chlcs.component1().second))
        }

        newViewRef.set(TableViewRef(
            newDefaultColumnView,
            newDefaultRowView,
            newColumnsViews,
            newRowViews,
            newCellViews,
            ref.table
        ))

        return tableViewClone
    }
}

object DEFAULT_COLUMN_VIEW
object DEFAULT_ROW_VIEW

private const val STANDARD_WIDTH = 100L
private const val STANDARD_HEIGHT = 20L

class Area<T>(val view: TableView, val value: T?) {
    internal fun toUnitArea() = Area<Any>(view, null)

    override fun equals(other: Any?): Boolean {
        return if (other is Area<*>)
            this.value == other.value
        else false
    }

    override fun hashCode(): Int {
        return Objects.hash(this.value)
    }

    override fun toString(): String {
        return this.value.toString()
    }
}

// TODO We want to have on and onAny functions on the below view classes, so that we can subscribe to the columns/rows/cell they represent
class ColumnView internal constructor(val width: Long = STANDARD_WIDTH, val columnHeader: ColumnHeader) {
    internal fun ensureView(columnHeader: ColumnHeader) = ColumnView(width, columnHeader)

    override fun toString(): String {
        return "ColumnView(width=$width, columnHeader=$columnHeader)"
    }
}

class DefaultColumnView internal constructor(val width: Long = STANDARD_WIDTH) {
    internal fun toColumnView(columnHeader: ColumnHeader) = ColumnView(width, columnHeader)

    override fun toString(): String {
        return "DefaultColumnView(width=$width)"
    }
}

class RowView internal constructor(val height: Long = STANDARD_HEIGHT, val index: Long) {
    internal fun ensureView(index: Long) = RowView(height, index)

    override fun toString(): String {
        return "RowView(height=$height, index=$index)"
    }
}

class DefaultRowView internal constructor(val height: Long = STANDARD_HEIGHT) {
    internal fun toRowView(index: Long) = RowView(height, index)

    override fun toString(): String {
        return "DefaultRowView(height=$height)"
    }
}

class CellView internal constructor(val height: Long = STANDARD_HEIGHT, val width: Long = STANDARD_WIDTH, val columnHeader: ColumnHeader, val index: Long) {
    internal fun ensureView(columnHeader: ColumnHeader, index: Long) = CellView(height, width, columnHeader, index)

    override fun toString(): String {
        return "CellView(height=$height, width=$width, columnHeader=$columnHeader, index=$index)"
    }
}

class ColumnViewBuilder(var width: Long = STANDARD_WIDTH) {
    internal fun build(columnHeader: ColumnHeader): ColumnView = ColumnView(width, columnHeader)
}

class RowViewBuilder(var height: Long = STANDARD_HEIGHT) {
    internal fun build(index: Long): RowView = RowView(height, index)
}

class CellViewBuilder(var height: Long = STANDARD_HEIGHT, var width: Long = STANDARD_WIDTH) {
    internal fun build(columnHeader: ColumnHeader, index: Long): CellView = CellView(height, width, columnHeader, index)
}

class DefaultColumnViewBuilder(var width: Long = STANDARD_WIDTH) {
    internal fun build(): DefaultColumnView = DefaultColumnView(width)
}

class DefaultRowViewBuilder(var height: Long = STANDARD_HEIGHT) {
    internal fun build(): DefaultRowView = DefaultRowView(height)
}

internal class PositionedContent(
    val contentHeader: ColumnHeader,
    val contentRow: Long,
    val content: String,
    val h: Long,
    val w: Long,
    val z: Long? = null,
    val mt: Long? = null, // Margin top
    val ml: Long? = null, // Margin left
    val cw: Long? = null, // Container width
    val ch: Long? = null, // Container height
    val className: String = "",
    val x: Long?,
    val y: Long?
)

internal class Dimensions(val cornerX: Long, val cornerY: Long, val maxX: Long, val maxY: Long)