package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.Registry
import sigbla.app.internals.TableViewEventProcessor
import sigbla.app.internals.refAction
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import com.github.andrewoma.dexx.collection.Map as PMap
import com.github.andrewoma.dexx.collection.HashMap as PHashMap

// A table view is associated with one table, and holds metadata related on how to view a table.
// This includes among other things column widths, row heights, individual cell dimensions, styling, etc..

// TODO Introduce a concept of a view transformer. As with views, this can be on column, row or cell.
//      Some priority ordering might be needed between these, but the transformer would take a value
//      and convert this into another value. This only affects the view, and is done on demand (i.e.,
//      when the cell is being rendered), and is used to perform things like formatting etc..
//      .
//      Look at having this in the form of view["column"] = { .. } where this is given a cell and
//      expected to return a cell.
//      .
//      Should also allow for view[row number] = { .. } and view[headers.., row] = { .. }, and
//      view[cell] = { .. }, etc..
//      .
//      Also need a way to obtain a transformed cell, maybe with view[cell] returning cell?
//      Probably should return a special type of cell associated with the view, as this would
//      also allow for things like move(view[..] before|after|to view[..]) type ops.. like CellView
//      But, this should also work for RowView, ColumnView, etc.. maybe have a TableViewOps function
//      called materialize, which takes these and returns a table/column/row/cell transformed
//      according to the view transformations, ex: materialize(view) returns table or
//      materialize(view["column"]) returns a column, etc...
//      .
//      Potentially reuse DestinationOsmosis on a table clone. That way we can make any changes we
//      want to the clone, which impacts the view only.. On a change a new table clone is used..
//      (Maybe have ViewBuilder extend DestinationOsmosis? - not a bad idea as it allows h/w to be adjusted too)

// TODO Event model:
//      .
//      on<TableView>(tableView):
//      on<TableView>(columnView):
//      on<TableView>(rowView):
//      on<TableView>(cellView):
//      .
//      on<ColumnView>(tableView):
//      on<ColumnView>(columnView):
//      on<ColumnView>(rowView):
//      on<ColumnView>(cellView):
//      .
//      on<RowView>(tableView):
//      on<RowView>(columnView):
//      on<RowView>(rowView):
//      on<RowView>(cellView):
//      .
//      on<CellView>(tableView):
//      on<CellView>(columnView):
//      on<CellView>(rowView):
//      on<CellView>(cellView):
//      .
//      on<Cell>(tableView) or on<O,N>(tableView):
//      on<Cell>(columnView) or on<O,N>(columnView):
//      on<Cell>(rowView) or on<O,N>(rowView):
//      on<Cell>(cellView) or on<O,N>(cellView):

private const val STANDARD_CELL_HEIGHT = 20L
private const val STANDARD_CELL_WIDTH = 100L

// TODO Should the be sealed rather than abstract?
abstract class TableView(val name: String?) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = this

    abstract var table: Table?

    // TODO? abstract val materializedTable (and materializedColumn/Row elsewhere?)

    internal abstract val tableViewRef: AtomicReference<TableViewRef>

    internal abstract val eventProcessor: TableViewEventProcessor

    abstract val columnViews: Sequence<ColumnView>

    abstract val rowViews: Sequence<RowView>

    // Note: cellViews return the defined CellViews, while the TableView iterator
    // returns the calculated cell views for current cells
    abstract val cellViews: Sequence<CellView>

    abstract var cellHeight: Long

    abstract var cellWidth: Long

    operator fun get(tableView: Companion) = this

    abstract operator fun get(header: ColumnHeader): ColumnView

    operator fun get(vararg header: String): ColumnView = get(
        ColumnHeader(
            *header
        )
    )

    abstract operator fun get(index: Long): RowView

    operator fun get(header1: String, index: Long): CellView = this[header1][index]

    operator fun get(header1: String, header2: String, index: Long): CellView = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Long): CellView = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Long): CellView = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long): CellView = this[header1, header2, header3, header4, header5][index]

    operator fun get(index: Int): RowView = get(index.toLong())

    operator fun get(header1: String, index: Int): CellView = this[header1][index]

    operator fun get(header1: String, header2: String, index: Int): CellView = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Int): CellView = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Int): CellView = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int): CellView = this[header1, header2, header3, header4, header5][index]

    // -----

    operator fun get(cell: Cell<*>): CellView = this[cell.column.columnHeader][cell.index]

    operator fun get(cellView: CellView) = this[cellView.columnView][cellView.index]

    // -----

    operator fun get(column: Column): ColumnView = this[column.columnHeader]

    operator fun get(columnView: ColumnView): ColumnView = this[columnView.columnHeader]

    // -----

    operator fun get(row: Row) = this[row.index]

    operator fun get(rowView: RowView) = this[rowView.index]

    // -----

    operator fun get(index: Long, column: Column): CellView = this[column, index]
    operator fun get(index: Long, columnHeader: ColumnHeader): CellView = this[columnHeader, index]

    // -----

    operator fun get(column: Column, index: Long): CellView = this[column.columnHeader, index]
    abstract operator fun get(columnHeader: ColumnHeader, index: Long): CellView

    // -----

    abstract operator fun set(companion: TableView.Companion, tableView: TableView)

    abstract operator fun set(companion: TableView.Companion, init: TableViewBuilder.() -> Unit)

    // -----

    operator fun set(cell: Cell<*>, view: CellView?) {
        this[cell.column][cell.index] = view
    }

    operator fun set(cell: Cell<*>, init: CellViewBuilder.() -> Unit) {
        this[cell.column][cell.index] = init
    }

    // -----

    operator fun set(cellView: CellView, view: CellView?) {
        this[cellView.columnView][cellView.index] = view
    }

    operator fun set(cellView: CellView, init: CellViewBuilder.() -> Unit) {
        this[cellView.columnView][cellView.index] = init
    }

    // -----

    abstract operator fun set(columnHeader: ColumnHeader, init: ColumnViewBuilder.() -> Unit)

    operator fun set(column: Column, init: ColumnViewBuilder.() -> Unit) = set(column.columnHeader, init)

    operator fun set(columnView: ColumnView, init: ColumnViewBuilder.() -> Unit) = set(columnView.columnHeader, init)

    abstract operator fun set(columnHeader: ColumnHeader, view: ColumnView?)

    operator fun set(column: Column, view: ColumnView?) = set(column.columnHeader, view)

    operator fun set(columnView: ColumnView, view: ColumnView?) = set(columnView.columnHeader, view)

    // -----

    abstract operator fun set(index: Long, init: RowViewBuilder.() -> Unit)

    operator fun set(rowView: RowView, init: RowViewBuilder.() -> Unit) = set(rowView.index, init)

    operator fun set(row: Row, init: RowViewBuilder.() -> Unit) = set(row.index, init)

    abstract operator fun set(index: Long, view: RowView?)

    operator fun set(rowView: RowView, view: RowView?) = set(rowView.index, view)

    operator fun set(row: Row, view: RowView?) = set(row.index, view)

    // -----

    abstract operator fun set(columnHeader: ColumnHeader, row: Long, init: CellViewBuilder.() -> Unit)

    abstract operator fun set(columnHeader: ColumnHeader, row: Long, view: CellView?)

    // -----

    operator fun set(vararg header: String, init: ColumnViewBuilder.() -> Unit) {
        this[ColumnHeader(*header)] = init
    }

    // -----

    operator fun set(header1: String, index: Long, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1)][index] = init
    }

    operator fun set(header1: String, header2: String, index: Long, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1, header2)][index] = init
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1, header2, header3)][index] = init
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1, header2, header3, header4)][index] = init
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1, header2, header3, header4, header5)][index] = init
    }

    // -----

    operator fun set(header1: String, index: Int, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1)][index] = init
    }

    operator fun set(header1: String, header2: String, index: Int, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1, header2)][index] = init
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1, header2, header3)][index] = init
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1, header2, header3, header4)][index] = init
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, init: CellViewBuilder.() -> Unit) {
        this[ColumnHeader(header1, header2, header3, header4, header5)][index] = init
    }

    // -----

    operator fun set(vararg header: String, view: ColumnView?) {
        this[ColumnHeader(*header)] = view
    }

    // -----

    operator fun set(vararg header: String, index: Long, view: CellView?) {
        this[ColumnHeader(*header)][index] = view
    }

    // -----

    operator fun set(vararg header: String, index: Int, view: CellView?) {
        this[ColumnHeader(*header)][index] = view
    }

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableViewRef.get()
        val table = ref.table
            ?: return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val tableIterator = table.iterator()

        return object : Iterator<DerivedCellView> {
            override fun hasNext() = tableIterator.hasNext()
            override fun next(): DerivedCellView {
                val cell = tableIterator.next()
                val columnView = ColumnView(this@TableView, cell.column.columnHeader)
                return createDerivedCellViewFromRef(ref, columnView, cell.index)
            }
        }
    }

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
    val defaultCellView: ViewMeta = ViewMeta(STANDARD_CELL_HEIGHT, STANDARD_CELL_WIDTH),
    val columnViews: PMap<ColumnHeader, ViewMeta> = PHashMap(),
    val rowViews: PMap<Long, ViewMeta> = PHashMap(),
    val cellViews: PMap<Pair<ColumnHeader, Long>, ViewMeta> = PHashMap(),
    val table: Table? = null
    // TODO Need version for event management like with TableRef
    // TODO Need sorting?
)

class BaseTableView internal constructor(
    name: String?,
    onRegistry: Boolean = true,
    override val tableViewRef: AtomicReference<TableViewRef>,
    override val eventProcessor: TableViewEventProcessor = TableViewEventProcessor()
) : TableView(name) {
    constructor(name: String?, table: Table?) : this(name, tableViewRef = AtomicReference(TableViewRef(table = table)))
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

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    override val columnViews: Sequence<ColumnView>
        get() = tableViewRef.get()
            .columnViews
            .keys()
            .asSequence()
            .map { ColumnView(this, it) }

    override val rowViews: Sequence<RowView>
        get() = tableViewRef.get()
            .rowViews
            .keys()
            .asSequence()
            .map { RowView(this, it) }

    override val cellViews: Sequence<CellView>
        get() = tableViewRef.get()
            .cellViews
            .keys()
            .asSequence()
            .map {
                CellView(ColumnView(this, it.first), it.second)
            }

    override var cellHeight: Long
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.defaultCellView.cellHeight ?: STANDARD_CELL_HEIGHT
        }
        set(height) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    defaultCellView = ViewMeta(height, it.defaultCellView.cellWidth)
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    override var cellWidth: Long
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.defaultCellView.cellWidth ?: STANDARD_CELL_WIDTH
        }
        set(width) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    defaultCellView = ViewMeta(it.defaultCellView.cellHeight, width)
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    private fun set(viewMeta: ViewMeta) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                defaultCellView = viewMeta
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(oldView, newView)) as List<TableViewListenerEvent<Any>>)
    }

    override fun get(columnHeader: ColumnHeader) = ColumnView(this, columnHeader)

    override fun get(index: Long) = RowView(this, index)

    override fun get(columnHeader: ColumnHeader, index: Long) = CellView(get(columnHeader), index)

    override fun set(companion: Companion, tableView: TableView) {
        TODO("Not yet implemented")
    }

    override fun set(companion: Companion, init: TableViewBuilder.() -> Unit) {
        val tableViewBuilder = TableViewBuilder()
        tableViewBuilder.init()
        set(tableViewBuilder.build())
    }

    override fun set(columnHeader: ColumnHeader, init: ColumnViewBuilder.() -> Unit) {
        val columnViewBuilder = ColumnViewBuilder()
        columnViewBuilder.init()
        set(columnHeader, columnViewBuilder.build())
    }

    override fun set(columnHeader: ColumnHeader, view: ColumnView?) {
        set(columnHeader, if (view != null) ViewMeta(null, view.cellWidth) else null)
    }

    private fun set(columnHeader: ColumnHeader, viewMeta: ViewMeta?) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                columnViews = if (viewMeta != null)
                    it.columnViews.put(columnHeader, viewMeta)
                else
                    it.columnViews.remove(columnHeader)
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[columnHeader]
        val new = newView[columnHeader]

        eventProcessor.publish(listOf(TableViewListenerEvent<ColumnView>(old, new)) as List<TableViewListenerEvent<Any>>)
    }

    override fun set(row: Long, init: RowViewBuilder.() -> Unit) {
        val rowViewBuilder = RowViewBuilder()
        rowViewBuilder.init()
        set(row, rowViewBuilder.build())
    }

    override fun set(row: Long, view: RowView?) {
        set(row, if (view != null) ViewMeta(view.cellHeight, null) else null)
    }

    private fun set(row: Long, viewMeta: ViewMeta?) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                rowViews = if (viewMeta != null)
                    it.rowViews.put(row, viewMeta)
                else
                    it.rowViews.remove(row)
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[row]
        val new = newView[row]

        eventProcessor.publish(listOf(TableViewListenerEvent<RowView>(old, new)) as List<TableViewListenerEvent<Any>>)
    }

    override fun set(columnHeader: ColumnHeader, row: Long, init: CellViewBuilder.() -> Unit) {
        val cellViewBuilder = CellViewBuilder()
        cellViewBuilder.init()
        set(columnHeader, row, cellViewBuilder.build())
    }

    override fun set(columnHeader: ColumnHeader, row: Long, view: CellView?) {
        set(columnHeader, row, if (view != null) ViewMeta(view.cellHeight, view.cellWidth) else null)
    }

    internal fun set(columnHeader: ColumnHeader, row: Long, viewMeta: ViewMeta?) {
        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                cellViews = if (viewMeta != null)
                    it.cellViews.put(Pair(columnHeader, row), viewMeta)
                else
                    it.cellViews.remove(Pair(columnHeader, row))
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[columnHeader][row]
        val new = newView[columnHeader][row]

        eventProcessor.publish(listOf(TableViewListenerEvent(old, new)))
    }

    override fun makeClone(name: String?, onRegistry: Boolean, ref: TableViewRef) = BaseTableView(name, onRegistry, AtomicReference(ref))
}

internal class ViewMeta(
    val cellHeight: Long?,
    val cellWidth: Long?
)

class TableViewBuilder(
    var cellHeight: Long? = null,
    var cellWidth: Long? = null,
    // TODO? var reader: (DestinationOsmosis<CellView>.() -> Unit)? = null
) {
    internal fun build(): ViewMeta = ViewMeta(cellHeight, cellWidth)
}

class ColumnViewBuilder(
    var cellWidth: Long? = null,
    // TODO? var reader: (DestinationOsmosis<CellView>.() -> Unit)? = null
) {
    internal fun build(): ViewMeta = ViewMeta(null, cellWidth)
}

class RowViewBuilder(
    var cellHeight: Long? = null,
    // TODO? var reader: (DestinationOsmosis<CellView>.() -> Unit)? = null
) {
    internal fun build(): ViewMeta = ViewMeta(cellHeight, null)
}

class CellViewBuilder(
    var cellHeight: Long? = null,
    var cellWidth: Long? = null,
    // TODO? var reader: (DestinationOsmosis<CellView>.() -> Unit)? = null
) {
    internal fun build(): ViewMeta = ViewMeta(cellHeight, cellWidth)
}

// TODO Add Iterable<DerivedCellView> for symmetry?
class CellView(
    val columnView: ColumnView,
    val index: Long
) {
    var cellHeight: Long
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.cellViews[Pair(columnView.columnHeader, index)]?.cellHeight ?: columnView.tableView[index].cellHeight
        }
        set(height) {
            tableView[this] = {
                cellHeight = height
            }
        }

    var cellWidth: Long
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.cellViews[Pair(columnView.columnHeader, index)]?.cellWidth ?: columnView.cellWidth
        }
        set(width) {
            tableView[this] = {
                cellWidth = width
            }
        }

    val tableView: TableView
        get() = columnView.tableView

    val cell: Cell<*>?
        get() = tableView.table?.let { return it[columnView.columnHeader][index] }

    val derived: DerivedCellView
        get() = createDerivedCellViewFromRef(this.tableView.tableViewRef.get(), columnView, index)
}

internal fun createDerivedCellViewFromRef(ref: TableViewRef, columnView: ColumnView, index: Long): DerivedCellView {
    val cellViewMeta = ref.cellViews[Pair(columnView.columnHeader, index)]
    val defaultCellView = ref.defaultCellView

    val height = cellViewMeta?.cellHeight
        ?: ref.rowViews[index]?.cellHeight
        ?: defaultCellView.cellHeight
        ?: STANDARD_CELL_HEIGHT

    val width = cellViewMeta?.cellWidth
        ?: ref.columnViews[columnView.columnHeader]?.cellWidth
        ?: defaultCellView.cellWidth
        ?: STANDARD_CELL_WIDTH

    return DerivedCellView(columnView, index, height, width)
}

// TODO We could also have a DerivedColumnView and a DerivedRowView
class DerivedCellView internal constructor(
    val columnView: ColumnView,
    val index: Long,
    val cellHeight: Long,
    val cellWidth: Long
) {
    val tableView: TableView
        get() = columnView.tableView

    val cell: Cell<*>?
        get() = tableView.table?.let { return it[columnView.columnHeader][index] }
}

class ColumnView internal constructor(
    val tableView: TableView,
    val columnHeader: ColumnHeader
) : Iterable<DerivedCellView> {
    var cellWidth: Long
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.columnViews[columnHeader]?.cellWidth ?: ref.defaultCellView.cellWidth ?: STANDARD_CELL_WIDTH
        }
        set(width) {
            tableView[this] = {
                cellWidth = width
            }
        }

    // Note: cellViews return the defined CellViews, while the TableView iterator
    // returns the calculated cell views for current cells
    val cellViews: Sequence<CellView>
        get() = tableView.tableViewRef.get()
            .cellViews
            .keys()
            .filter { it.first == columnHeader }
            .asSequence()
            .map {
                CellView(ColumnView(this.tableView, it.first), it.second)
            }

    operator fun get(index: Long): CellView = tableView[columnHeader, index]

    operator fun get(index: Int) = get(index.toLong())

    // TODO Does this need to care about index relations?
    operator fun get(row: Row) = get(row.index)

    operator fun set(index: Long, init: CellViewBuilder.() -> Unit) { tableView[columnHeader, index] = init }

    operator fun set(index: Int, init: CellViewBuilder.() -> Unit) { this[index.toLong()] = init }

    operator fun set(index: Long, view: CellView?) { tableView[columnHeader, index] = view }

    operator fun set(index: Int, view: CellView?) { this[index.toLong()] = view }

    operator fun set(row: Row, view: CellView?) = set(row.index, view)

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableView.tableViewRef.get()
        val table = ref.table
            ?: return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val tableRef = table.tableRef.get()
        val columnMeta = tableRef.columns[columnHeader]
            ?: return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        // TODO Any need to filter our prenatal, or those are empty columns anyway?

        val values = tableRef.columnCells[columnHeader] ?: throw InvalidColumnException(columnHeader)
        val columnIterator = values.asSequence().map { it.component2().toCell(BaseColumn(table, columnHeader, columnMeta.columnOrder), it.component1()) }.iterator()

        return object : Iterator<DerivedCellView> {
            override fun hasNext() = columnIterator.hasNext()
            override fun next(): DerivedCellView {
                val cell = columnIterator.next()
                val columnView = ColumnView(this@ColumnView.tableView, cell.column.columnHeader)
                return createDerivedCellViewFromRef(ref, columnView, cell.index)
            }
        }
    }
}

class RowView internal constructor(
    val tableView: TableView,
    val index: Long
) : Iterable<DerivedCellView> {
    var cellHeight: Long
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.rowViews[index]?.cellHeight ?: ref.defaultCellView.cellHeight ?: STANDARD_CELL_HEIGHT
        }
        set(height) {
            tableView[this] = {
                cellHeight = height
            }
        }

    // Note: cellViews return the defined CellViews, while the TableView iterator
    // returns the calculated cell views for current cells
    val cellViews: Sequence<CellView>
        get() = TODO()

    operator fun get(vararg header: String): CellView = tableView[ColumnHeader(*header), index]

    operator fun get(columnHeader: ColumnHeader): CellView = tableView[columnHeader, index]

    operator fun get(columnView: ColumnView): CellView = tableView[columnView.columnHeader, index]

    operator fun get(column: Column): CellView = tableView[column.columnHeader, index]

    operator fun set(vararg header: String, init: CellViewBuilder.() -> Unit) { tableView[ColumnHeader(*header), index] = init }

    operator fun set(columnHeader: ColumnHeader, init: CellViewBuilder.() -> Unit) { tableView[columnHeader, index] = init }

    operator fun set(columnView: ColumnView, init: CellViewBuilder.() -> Unit) { tableView[columnView.columnHeader, index] = init }

    operator fun set(column: Column, init: CellViewBuilder.() -> Unit) { tableView[column.columnHeader, index] = init }

    operator fun set(vararg header: String, view: CellView?) { tableView[ColumnHeader(*header), index] = view }

    operator fun set(columnHeader: ColumnHeader, view: CellView?) { tableView[columnHeader, index] = view }

    operator fun set(columnView: ColumnView, view: CellView?) { tableView[columnView.columnHeader, index] = view }

    operator fun set(column: Column, view: CellView?) { tableView[column.columnHeader, index] = view }

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableView.tableViewRef.get()
        val table = ref.table
            ?: return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val rowIterator = table[IndexRelation.AT, index].iterator()

        return object : Iterator<DerivedCellView> {
            override fun hasNext() = rowIterator.hasNext()
            override fun next(): DerivedCellView {
                val cell = rowIterator.next()
                val columnView = ColumnView(this@RowView.tableView, cell.column.columnHeader)
                return createDerivedCellViewFromRef(ref, columnView, cell.index)
            }
        }
    }
}
