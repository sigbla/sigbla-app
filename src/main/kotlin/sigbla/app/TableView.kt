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

// A table view is associated with one table, and holds metadata related on how to view a table.
// This includes among other things column widths, row heights, individual cell dimensions, styling, etc..

// TODO Introduce a concept of a view transformer. As with views, this can be on column, row or cell.
//      Some priority ordering might be needed between these, but the transformer would take a value
//      and convert this into another value. This only affects the view, and is done on demand (i.e.,
//      when the cell is being rendered), and is used to perform things like formatting etc..
//
//      Look at having this in the form of view["column"] = { .. } where this is given a cell and
//      expected to return a cell.
//
//      Should also allow for view[row number] = { .. } and view[headers.., row] = { .. }, and
//      view[cell] = { .. }, etc..
//
//      Also need a way to obtain a transformed cell, maybe with view[cell] returning cell?
//      Probably should return a special type of cell associated with the view, as this would
//      also allow for things like move(view[..] before|after|to view[..]) type ops.. like CellView
//      But, this should also work for RowView, ColumnView, etc.. maybe have a TableViewOps function
//      called materialize, which takes these and returns a table/column/row/cell transformed
//      according to the view transformations, ex: materialize(view) returns table or
//      materialize(view["column"]) returns a column, etc...

// TODO Events on a table view should be much like on a table, with view.onAny or view.on<ColumnView>.
//      Note that we only have one generic type, as we can't have one type of view convert to another.
//
//      A listener can, as with table, call back into the table view to change the view if wanted. And we
//      use ordering to fire off listeners in the right order. This allows for the final UI listener
//      to fire after any earlier modifications. A loop detector is also included.

abstract class TableView(val name: String?) : Iterable<Area<*>> {
    abstract var table: Table?

    internal abstract val tableViewRef: AtomicReference<TableViewRef>

    internal abstract val eventProcessor: TableViewEventProcessor

    abstract operator fun get(type: DEFAULT_COLUMN_VIEW): DefaultColumnView

    abstract operator fun set(type: DEFAULT_COLUMN_VIEW, init: DefaultColumnViewBuilder.() -> Unit)

    abstract operator fun set(type: DEFAULT_COLUMN_VIEW, defaultColumnView: DefaultColumnView)

    abstract operator fun get(type: DEFAULT_ROW_VIEW): DefaultRowView

    abstract operator fun set(type: DEFAULT_ROW_VIEW, init: DefaultRowViewBuilder.() -> Unit)

    abstract operator fun set(type: DEFAULT_ROW_VIEW, defaultRowView: DefaultRowView)

    // TODO Does a set/get with DEFAULT_CELL_VIEW make sense?

    abstract operator fun get(columnHeader: ColumnHeader): ColumnView

    operator fun get(column: Column) = get(column.columnHeader)

    operator fun get(columnView: ColumnView) = get(columnView.columnHeader)

    abstract operator fun get(row: Long): RowView

    operator fun get(row: Row) = get(row.index)

    operator fun get(rowView: RowView) = get(rowView.index)

    abstract operator fun get(columnHeader: ColumnHeader, row: Long): CellView

    operator fun get(column: Column, row: Long) = get(column.columnHeader, row)

    operator fun get(cell: Cell<*>) = get(cell.column, cell.index)

    operator fun get(cellView: CellView) = get(cellView.columnHeader, cellView.index)

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
            // TODO Should this really be here? Table?
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

    internal abstract fun makeClone(name: String? = this.name, onRegistry: Boolean = false, ref: TableViewRef = tableViewRef.get()!!): TableView

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
    val columnViews: PMap<ColumnHeader, ColumnViewMeta> = PHashMap(),
    val rowViews: PMap<Long, RowViewMeta> = PHashMap(),
    val cellViews: PMap<Pair<ColumnHeader, Long>, CellViewMeta> = PHashMap(),
    val table: Table? = null
)

class BaseTableView internal constructor(
    name: String?,
    onRegistry: Boolean = true,
    override val tableViewRef: AtomicReference<TableViewRef>,
    override val eventProcessor: TableViewEventProcessor = TableViewEventProcessor()
) : TableView(name) {
    constructor(name: String?, table: Table?) : this(name, tableViewRef = AtomicReference(
        TableViewRef(
            DefaultColumnView(),
            DefaultRowView(),
            table = table
    )))
    constructor(table: Table) : this(table.name, table)
    constructor(name: String) : this(name, Registry.getTable(name))

    init {
        if (name != null && onRegistry) Registry.setView(name, this)
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
                    // TODO Probably clone the tables? Perhaps not, to give access to tables
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

    override fun get(columnHeader: ColumnHeader): ColumnView = tableViewRef.get().columnViews[columnHeader]?.toColumnView(this, columnHeader) ?: this[DEFAULT_COLUMN_VIEW].toColumnView(this, columnHeader)

    override fun get(row: Long): RowView = tableViewRef.get().rowViews[row]?.toRowView(this, row) ?: this[DEFAULT_ROW_VIEW].toRowView(this, row)

    override fun get(columnHeader: ColumnHeader, row: Long): CellView = tableViewRef.get().cellViews[Pair(columnHeader, row)]?.toCellView(this, columnHeader, row) ?: CellView(this, columnHeader, row, this[DEFAULT_ROW_VIEW].height, this[DEFAULT_COLUMN_VIEW].width)

    override fun set(columnHeader: ColumnHeader, init: ColumnViewBuilder.() -> Unit) {
        val columnViewBuilder = ColumnViewBuilder()
        columnViewBuilder.init()
        set(columnHeader, columnViewBuilder.build())
    }

    override fun set(columnHeader: ColumnHeader, columnView: ColumnView?) {
        set(columnHeader, if (columnView != null) ColumnViewMeta(columnView.width) else null)
    }

    private fun set(columnHeader: ColumnHeader, columnViewMeta: ColumnViewMeta?) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                columnViews = if (columnViewMeta != null)
                    it.columnViews.put(columnHeader, columnViewMeta)
                else
                    it.columnViews.remove(columnHeader)
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

    override fun set(row: Long, init: RowViewBuilder.() -> Unit) {
        val rowViewBuilder = RowViewBuilder()
        rowViewBuilder.init()
        set(row, rowViewBuilder.build())
    }

    override fun set(row: Long, rowView: RowView?) {
        set(row, if (rowView != null) RowViewMeta(rowView.height) else null)
    }

    private fun set(row: Long, rowViewMeta: RowViewMeta?) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                rowViews = if (rowViewMeta != null)
                    it.rowViews.put(row, rowViewMeta)
                else
                    it.rowViews.remove(row)
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

    override fun set(columnHeader: ColumnHeader, row: Long, init: CellViewBuilder.() -> Unit) {
        val cellViewBuilder = CellViewBuilder()
        cellViewBuilder.init()
        set(columnHeader, row, cellViewBuilder.build())
    }

    override fun set(columnHeader: ColumnHeader, row: Long, cellView: CellView?) {
        set(columnHeader, row, if (cellView != null) CellViewMeta(cellView.height, cellView.width) else null)
    }

    private fun set(columnHeader: ColumnHeader, row: Long, cellViewMeta: CellViewMeta?) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                cellViews = if (cellViewMeta != null)
                    it.cellViews.put(Pair(columnHeader, row), cellViewMeta)
                else
                    it.cellViews.remove(Pair(columnHeader, row))
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

    override fun makeClone(name: String?, onRegistry: Boolean, ref: TableViewRef) = BaseTableView(name, onRegistry, AtomicReference(ref))
}

object DEFAULT_COLUMN_VIEW
object DEFAULT_ROW_VIEW

private const val STANDARD_WIDTH = 100L
private const val STANDARD_HEIGHT = 20L

// TODO Consider removing Area class completely
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

internal class ColumnViewMeta(
    val width: Long
) {
    fun toColumnView(tableView: TableView, columnHeader: ColumnHeader) = ColumnView(tableView, columnHeader, width)
}

// TODO We want to have on and onAny functions on the below view classes, so that we can subscribe to the columns/rows/cell they represent
class ColumnView internal constructor(val tableView: TableView, val columnHeader: ColumnHeader, val width: Long = STANDARD_WIDTH) {
    override fun toString(): String {
        return "ColumnView(tableView=$tableView, columnHeader=$columnHeader, width=$width)"
    }
}

class DefaultColumnView internal constructor(val width: Long = STANDARD_WIDTH) {
    internal fun toColumnView(tableView: TableView, columnHeader: ColumnHeader) = ColumnView(tableView, columnHeader, width)

    override fun toString(): String {
        return "DefaultColumnView(width=$width)"
    }
}

internal class RowViewMeta(
    val height: Long
) {
    fun toRowView(tableView: TableView, index: Long) = RowView(tableView, index, height)
}

class RowView internal constructor(val tableView: TableView, val index: Long, val height: Long = STANDARD_HEIGHT) {
    override fun toString(): String {
        return "RowView(tableView=$tableView, index=$index, height=$height)"
    }
}

class DefaultRowView internal constructor(val height: Long = STANDARD_HEIGHT) {
    internal fun toRowView(tableView: TableView, index: Long) = RowView(tableView, index, height)

    override fun toString(): String {
        return "DefaultRowView(height=$height)"
    }
}

internal class CellViewMeta(
    val height: Long,
    val width: Long
) {
    fun toCellView(tableView: TableView, columnHeader: ColumnHeader, index: Long) = CellView(tableView, columnHeader, index, height, width)
}

class CellView internal constructor(val tableView: TableView, val columnHeader: ColumnHeader, val index: Long, val height: Long = STANDARD_HEIGHT, val width: Long = STANDARD_WIDTH) {
    override fun toString(): String {
        return "CellView(tableView=$tableView, columnHeader=$columnHeader, index=$index, height=$height, width=$width)"
    }
}

class ColumnViewBuilder(var width: Long = STANDARD_WIDTH) {
    internal fun build(): ColumnViewMeta = ColumnViewMeta(width)
}

class RowViewBuilder(var height: Long = STANDARD_HEIGHT) {
    internal fun build(): RowViewMeta = RowViewMeta(height)
}

class CellViewBuilder(var height: Long = STANDARD_HEIGHT, var width: Long = STANDARD_WIDTH) {
    internal fun build(): CellViewMeta = CellViewMeta(height, width)
}

class DefaultColumnViewBuilder(var width: Long = STANDARD_WIDTH) {
    internal fun build(): DefaultColumnView = DefaultColumnView(width)
}

class DefaultRowViewBuilder(var height: Long = STANDARD_HEIGHT) {
    internal fun build(): DefaultRowView = DefaultRowView(height)
}