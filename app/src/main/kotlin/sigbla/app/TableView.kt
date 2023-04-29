package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableViewException
import sigbla.app.internals.Registry
import sigbla.app.internals.TableViewEventProcessor
import sigbla.app.internals.refAction
import java.util.*
import java.util.Collections.emptySortedSet
import java.util.concurrent.atomic.AtomicReference
import com.github.andrewoma.dexx.kollection.toImmutableSet
import com.github.andrewoma.dexx.collection.Map as PMap
import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.kollection.ImmutableSet as PSet

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
//      on<TableView>(cellRangeView):
//      on<TableView>(cellView):
//      .
//      on<ColumnView>(tableView):
//      on<ColumnView>(columnView):
//      on<ColumnView>(rowView):
//      on<ColumnView>(cellRangeView):
//      on<ColumnView>(cellView):
//      .
//      on<RowView>(tableView):
//      on<RowView>(columnView):
//      on<RowView>(rowView):
//      on<RowView>(cellRangeView):
//      on<RowView>(cellView):
//      .
//      on<CellRangeView>(tableView):
//      on<CellRangeView>(columnView):
//      on<CellRangeView>(rowView):
//      on<CellRangeView>(cellRangeView):
//      on<CellRangeView>(cellView):
//      .
//      on<CellView>(tableView):
//      on<CellView>(columnView):
//      on<CellView>(rowView):
//      on<CellView>(cellRangeView):
//      on<CellView>(cellView):
//      .
//      Not sure we want these below?
//      on<Cell>(tableView) or on<O,N>(tableView):
//      on<Cell>(columnView) or on<O,N>(columnView):
//      on<Cell>(rowView) or on<O,N>(rowView):
//      on<Cell>(cellView) or on<O,N>(cellView):

const val DEFAULT_CELL_HEIGHT = 20L
const val DEFAULT_CELL_WIDTH = 100L

internal data class ViewMeta(
    val cellHeight: Long? = null,
    val cellWidth: Long? = null,
    val cellClasses: PSet<String>? = null,
    val cellTopics: PSet<String>? = null
)

internal data class TableViewRef(
    val defaultCellView: ViewMeta = ViewMeta(),
    val columnViews: PMap<ColumnHeader, ViewMeta> = PHashMap(),
    val rowViews: PMap<Long, ViewMeta> = PHashMap(),
    val cellViews: PMap<Pair<ColumnHeader, Long>, ViewMeta> = PHashMap(),
    val table: Table? = null,
    val version: Long = Long.MIN_VALUE,
)

// TODO Consider if we need a DerivedTableView as well?
class TableView internal constructor(
    val name: String?,
    onRegistry: Boolean = true,
    internal val tableViewRef: AtomicReference<TableViewRef>,
    internal val eventProcessor: TableViewEventProcessor = TableViewEventProcessor()
) : Iterable<DerivedCellView> {
    internal constructor(name: String?, table: Table?) : this(name, tableViewRef = AtomicReference(TableViewRef(table = table)))
    internal constructor(table: Table) : this(table.name, table)
    internal constructor(name: String) : this(name, Registry.getTable(name))

    init {
        if (name != null && onRegistry) Registry.setView(name, this)
    }

    // TODO This or the operator function should go?
    val tableView: TableView
        get() = this

    operator fun get(tableView: TableView.Companion) = this

    // TODO? val materializedTable (and materializedColumn/Row elsewhere?)

    // TODO Change to get/set operator: view[Table] = table
    var table
        get() = tableViewRef.get().table
        set(table) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    table = table,
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    val columnViews: Sequence<ColumnView>
        get() = tableViewRef.get()
            .columnViews
            .keys()
            .asSequence()
            .map { ColumnView(this, it) }

    val rowViews: Sequence<RowView>
        get() = tableViewRef.get()
            .rowViews
            .keys()
            .asSequence()
            .map { RowView(this, it) }

    // Note: cellViews return the defined CellViews, while the TableView iterator
    // returns the calculated cell views for current cells
    val cellViews: Sequence<CellView>
        get() = tableViewRef.get()
            .cellViews
            .keys()
            .asSequence()
            .map {
                CellView(ColumnView(this, it.first), it.second)
            }

    // TODO should use get/set operators
    var cellHeight: Long?
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.defaultCellView.cellHeight
        }
        set(height) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    defaultCellView = it.defaultCellView.copy(
                        cellHeight = height
                    ),
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    // TODO should use get/set operators
    var cellWidth: Long?
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.defaultCellView.cellWidth
        }
        set(width) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    defaultCellView = it.defaultCellView.copy(
                        cellWidth = width
                    ),
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    // TODO should use get/set operators
    var cellClasses: SortedSet<String>
        get() {
            val ref = tableView.tableViewRef.get()
            return Collections.unmodifiableSortedSet(ref.defaultCellView.cellClasses?.toSortedSet() ?: emptySortedSet())
        }
        set(classes) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    defaultCellView = it.defaultCellView.copy(
                        cellClasses = classes.toImmutableSet()
                    )
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    // TODO should use get/set operators
    var cellTopics: SortedSet<String>
        get() {
            val ref = tableView.tableViewRef.get()
            return Collections.unmodifiableSortedSet(ref.defaultCellView.cellTopics?.toSortedSet() ?: emptySortedSet())
        }
        set(topics) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    defaultCellView = it.defaultCellView.copy(
                        cellTopics = topics.toImmutableSet()
                    )
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    operator fun get(columnHeader: ColumnHeader) = ColumnView(this, columnHeader)

    operator fun get(vararg header: String): ColumnView = get(
        ColumnHeader(
            *header
        )
    )

    operator fun get(index: Long) = RowView(this, index)

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

    operator fun get(derivedCellView: DerivedCellView) = this[derivedCellView.columnView][derivedCellView.index].derived

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

    operator fun get(columnHeader: ColumnHeader, index: Long): CellView = CellView(get(columnHeader), index)

    // -----

    operator fun set(companion: Companion, tableView: TableView) {
        TODO("Not yet implemented")
    }

    // -----

    operator fun set(cell: Cell<*>, view: CellView?) {
        this[cell.column][cell.index] = view
    }

    // -----

    operator fun set(cellView: CellView, view: CellView?) {
        this[cellView.columnView][cellView.index] = view
    }

    // -----

    operator fun set(columnHeader: ColumnHeader, view: ColumnView?) {
        val (oldRef, newRef) = tableViewRef.refAction {
            val viewMeta = if (view == null) null else view.tableView.tableViewRef.get().columnViews[view.columnHeader]

            it.copy(
                columnViews = if (viewMeta != null)
                    it.columnViews.put(columnHeader, viewMeta)
                else
                    it.columnViews.remove(columnHeader),
                version = it.version + 1L
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[columnHeader]
        val new = newView[columnHeader]

        eventProcessor.publish(listOf(TableViewListenerEvent<ColumnView>(old, new)) as List<TableViewListenerEvent<Any>>)
    }

    operator fun set(column: Column, view: ColumnView?) = set(column.columnHeader, view)

    operator fun set(columnView: ColumnView, view: ColumnView?) = set(columnView.columnHeader, view)

    // -----

    operator fun set(row: Long, view: RowView?) {
        val (oldRef, newRef) = tableViewRef.refAction {
            val viewMeta = if (view == null) null else view.tableView.tableViewRef.get().rowViews[view.index]

            it.copy(
                rowViews = if (viewMeta != null)
                    it.rowViews.put(row, viewMeta)
                else
                    it.rowViews.remove(row),
                version = it.version + 1L
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[row]
        val new = newView[row]

        eventProcessor.publish(listOf(TableViewListenerEvent<RowView>(old, new)) as List<TableViewListenerEvent<Any>>)
    }

    operator fun set(rowView: RowView, view: RowView?) = set(rowView.index, view)

    operator fun set(row: Row, view: RowView?) = set(row.index, view)

    // -----

    operator fun set(columnHeader: ColumnHeader, row: Long, view: CellView?) {
        val cell = Pair(columnHeader, row)
        val (oldRef, newRef) = tableViewRef.refAction {
            val viewMeta = if (view == null) null else view.tableView.tableViewRef.get().cellViews[cell]

            it.copy(
                cellViews = if (viewMeta != null)
                    it.cellViews.put(cell, viewMeta)
                else
                    it.cellViews.remove(cell),
                version = it.version + 1L
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[columnHeader][row]
        val new = newView[columnHeader][row]

        eventProcessor.publish(listOf(TableViewListenerEvent(old, new)))
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

    internal fun makeClone(name: String? = this.name, onRegistry: Boolean = false, ref: TableViewRef = tableViewRef.get()) = TableView(name, onRegistry, AtomicReference(ref))

    companion object {
        operator fun get(name: String): TableView = TableView(name)

        operator fun get(table: Table): TableView = TableView(table)

        operator fun get(name: String, table: Table): TableView = TableView(name, table)

        fun fromRegistry(name: String): TableView = Registry.getView(name) ?: throw InvalidTableViewException("No table view by name $name")

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

/* TODO Introduce a way to make atomic updates across several properties, with
        view = {
            it[..] = ..
            it[..] = ..
        }
*/

class CellView(
    val columnView: ColumnView,
    val index: Long
) : Iterable<DerivedCellView> {
    var cellHeight: Long?
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.cellViews[Pair(columnView.columnHeader, index)]?.cellHeight
        }
        set(height) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.columnHeader, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellHeight = height) ?: ViewMeta(cellHeight = height)

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.columnHeader][index]
            val new = newView[columnView.columnHeader][index]

            columnView.tableView.eventProcessor.publish(listOf(TableViewListenerEvent(old, new)))
        }

    var cellWidth: Long?
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.cellViews[Pair(columnView.columnHeader, index)]?.cellWidth
        }
        set(width) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.columnHeader, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellWidth = width) ?: ViewMeta(cellWidth = width)

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.columnHeader][index]
            val new = newView[columnView.columnHeader][index]

            columnView.tableView.eventProcessor.publish(listOf(TableViewListenerEvent(old, new)))
        }

    var cellClasses: SortedSet<String>
        get() {
            val ref = tableView.tableViewRef.get()
            return Collections.unmodifiableSortedSet(ref.cellViews[Pair(columnView.columnHeader, index)]?.cellClasses?.toSortedSet() ?: emptySortedSet())
        }
        set(classes) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.columnHeader, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellClasses = classes.toImmutableSet()) ?: ViewMeta(cellClasses = classes.toImmutableSet())

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.columnHeader][index]
            val new = newView[columnView.columnHeader][index]

            columnView.tableView.eventProcessor.publish(listOf(TableViewListenerEvent(old, new)))
        }

    var cellTopics: SortedSet<String>
        get() {
            val ref = tableView.tableViewRef.get()
            return Collections.unmodifiableSortedSet(ref.cellViews[Pair(columnView.columnHeader, index)]?.cellTopics?.toSortedSet() ?: emptySortedSet())
        }
        set(topics) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.columnHeader, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellTopics = topics.toImmutableSet()) ?: ViewMeta(cellTopics = topics.toImmutableSet())

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.columnHeader][index]
            val new = newView[columnView.columnHeader][index]

            columnView.tableView.eventProcessor.publish(listOf(TableViewListenerEvent(old, new)))
        }

    val tableView: TableView
        get() = columnView.tableView

    val cell: Cell<*>?
        get() = tableView.table?.let { return it[columnView.columnHeader][index] }

    val derived: DerivedCellView
        get() = createDerivedCellViewFromRef(this.tableView.tableViewRef.get(), columnView, index)

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableView.tableViewRef.get()
        if (ref.table == null)
            return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val derivedCellView = createDerivedCellViewFromRef(ref, columnView, index)

        return listOf(derivedCellView).iterator()
    }

    // TODO toString, hashCode, equals
}

internal fun createDerivedCellViewFromRef(ref: TableViewRef, columnView: ColumnView, index: Long): DerivedCellView {
    val cellViewMeta = ref.cellViews[Pair(columnView.columnHeader, index)]
    val defaultCellViewMeta = ref.defaultCellView
    val columnViewMeta = ref.columnViews[columnView.columnHeader]
    val rowViewMeta = ref.rowViews[index]

    val height = cellViewMeta?.cellHeight
        ?: rowViewMeta?.cellHeight
        ?: defaultCellViewMeta.cellHeight
        ?: DEFAULT_CELL_HEIGHT

    val width = cellViewMeta?.cellWidth
        ?: columnViewMeta?.cellWidth
        ?: defaultCellViewMeta.cellWidth
        ?: DEFAULT_CELL_WIDTH

    val classes = Collections.unmodifiableSortedSet((
            (cellViewMeta?.cellClasses?.toMutableSet() ?: mutableSetOf())
                    + (columnViewMeta?.cellClasses?.toMutableSet() ?: mutableSetOf())
                    + (rowViewMeta?.cellClasses?.toMutableSet() ?: mutableSetOf())
                    + (defaultCellViewMeta.cellClasses?.toMutableSet() ?: mutableSetOf())
            ).toSortedSet())

    val topics = Collections.unmodifiableSortedSet((
            (cellViewMeta?.cellTopics?.toMutableSet() ?: mutableSetOf())
                    + (columnViewMeta?.cellTopics?.toMutableSet() ?: mutableSetOf())
                    + (rowViewMeta?.cellTopics?.toMutableSet() ?: mutableSetOf())
                    + (defaultCellViewMeta.cellTopics?.toMutableSet() ?: mutableSetOf())
            ).toSortedSet())

    return DerivedCellView(columnView, index, height, width, classes, topics)
}

// TODO We also need DerivedColumnView and a DerivedRowView
class DerivedCellView internal constructor(
    val columnView: ColumnView,
    val index: Long,
    val cellHeight: Long,
    val cellWidth: Long,
    val cellClasses: SortedSet<String>,
    val cellTopics: SortedSet<String>
) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = columnView.tableView

    val cellView: CellView
        get() = columnView[index]

    val cell: Cell<*>?
        get() = tableView.table?.let { return it[columnView.columnHeader][index] }

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableView.tableViewRef.get()
        if (ref.table == null)
            return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val derivedCellView = createDerivedCellViewFromRef(ref, columnView, index)

        return listOf(derivedCellView).iterator()
    }

    // TODO toString, hashCode, equals
}

class ColumnView internal constructor(
    val tableView: TableView,
    val columnHeader: ColumnHeader
) : Iterable<DerivedCellView> {
    var cellWidth: Long?
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.columnViews[columnHeader]?.cellWidth
        }
        set(width) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[columnHeader]
                val viewMeta = oldMeta?.copy(cellWidth = width) ?: ViewMeta(cellWidth = width)

                it.copy(
                    columnViews = it.columnViews.put(columnHeader, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[columnHeader]
            val new = newView[columnHeader]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<ColumnView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    var cellClasses: SortedSet<String>
        get() {
            val ref = tableView.tableViewRef.get()
            return Collections.unmodifiableSortedSet(ref.columnViews[columnHeader]?.cellClasses?.toSortedSet() ?: emptySortedSet())
        }
        set(classes) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[columnHeader]
                val viewMeta = oldMeta?.copy(cellClasses = classes.toImmutableSet()) ?: ViewMeta(cellClasses = classes.toImmutableSet())

                it.copy(
                    columnViews = it.columnViews.put(columnHeader, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[columnHeader]
            val new = newView[columnHeader]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<ColumnView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    var cellTopics: SortedSet<String>
        get() {
            val ref = tableView.tableViewRef.get()
            return Collections.unmodifiableSortedSet(ref.columnViews[columnHeader]?.cellTopics?.toSortedSet() ?: emptySortedSet())
        }
        set(topics) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[columnHeader]
                val viewMeta = oldMeta?.copy(cellTopics = topics.toImmutableSet()) ?: ViewMeta(cellTopics = topics.toImmutableSet())

                it.copy(
                    columnViews = it.columnViews.put(columnHeader, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[columnHeader]
            val new = newView[columnHeader]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<ColumnView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    // Note: cellViews return the defined CellViews, while the ColumnView iterator
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

    val derived: DerivedColumnView
        get() = createDerivedColumnView(this)

    operator fun get(index: Long): CellView = tableView[columnHeader, index]

    operator fun get(index: Int) = get(index.toLong())

    // TODO Does this need to care about index relations?
    operator fun get(row: Row) = get(row.index)

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColumnView

        if (columnHeader != other.columnHeader) return false

        return true
    }

    override fun hashCode() = columnHeader.hashCode()

    override fun toString() = columnHeader.toString()
}

internal fun createDerivedColumnView(columnView: ColumnView): DerivedColumnView {
    val ref = columnView.tableView.tableViewRef.get()
    val defaultCellViewMeta = ref.defaultCellView
    val columnViewMeta = ref.columnViews[columnView.columnHeader]

    val width = columnViewMeta?.cellWidth
        ?: defaultCellViewMeta.cellWidth
        ?: DEFAULT_CELL_WIDTH

    val classes = Collections.unmodifiableSortedSet((
            (columnViewMeta?.cellClasses?.toMutableSet() ?: mutableSetOf())
                    + (defaultCellViewMeta.cellClasses?.toMutableSet() ?: mutableSetOf())
            ).toSortedSet())

    val topics = Collections.unmodifiableSortedSet((
            (columnViewMeta?.cellTopics?.toMutableSet() ?: mutableSetOf())
                    + (defaultCellViewMeta.cellTopics?.toMutableSet() ?: mutableSetOf())
            ).toSortedSet())

    return DerivedColumnView(columnView, width, classes, topics)
}

class DerivedColumnView internal constructor(
    val columnView: ColumnView,
    val cellWidth: Long,
    val cellClasses: SortedSet<String>,
    val cellTopics: SortedSet<String>
) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = columnView.tableView

    val columnHeader: ColumnHeader
        get() = columnView.columnHeader

    override fun iterator() = columnView.iterator()

    // TODO toString, hashCode, equals
}

class RowView internal constructor(
    val tableView: TableView,
    val index: Long
) : Iterable<DerivedCellView> {
    var cellHeight: Long?
        get() {
            val ref = tableView.tableViewRef.get()
            return ref.rowViews[index]?.cellHeight
        }
        set(height) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.rowViews[index]
                val viewMeta = oldMeta?.copy(cellHeight = height) ?: ViewMeta(cellHeight = height)

                it.copy(
                    rowViews = it.rowViews.put(index, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index]
            val new = newView[index]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<RowView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    var cellClasses: SortedSet<String>
        get() {
            val ref = tableView.tableViewRef.get()
            return Collections.unmodifiableSortedSet(ref.rowViews[index]?.cellClasses?.toSortedSet() ?: emptySortedSet())
        }
        set(classes) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.rowViews[index]
                val viewMeta = oldMeta?.copy(cellClasses = classes.toImmutableSet()) ?: ViewMeta(cellClasses = classes.toImmutableSet())

                it.copy(
                    rowViews = it.rowViews.put(index, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index]
            val new = newView[index]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<RowView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    var cellTopics: SortedSet<String>
        get() {
            val ref = tableView.tableViewRef.get()
            return Collections.unmodifiableSortedSet(ref.rowViews[index]?.cellTopics?.toSortedSet() ?: emptySortedSet())
        }
        set(topics) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.rowViews[index]
                val viewMeta = oldMeta?.copy(cellTopics = topics.toImmutableSet()) ?: ViewMeta(cellTopics = topics.toImmutableSet())

                it.copy(
                    rowViews = it.rowViews.put(index, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index]
            val new = newView[index]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<RowView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }

    // Note: cellViews return the defined CellViews, while the RowView iterator
    // returns the calculated cell views for current cells
    val cellViews: Sequence<CellView>
        get() = tableView.tableViewRef.get()
            .cellViews
            .keys()
            .filter { it.second == index }
            .asSequence()
            .map {
                CellView(ColumnView(this.tableView, it.first), it.second)
            }

    val derived: DerivedRowView
        get() = createDerivedRowView(this)

    operator fun get(vararg header: String): CellView = tableView[ColumnHeader(*header), index]

    operator fun get(columnHeader: ColumnHeader): CellView = tableView[columnHeader, index]

    operator fun get(columnView: ColumnView): CellView = tableView[columnView.columnHeader, index]

    operator fun get(column: Column): CellView = tableView[column.columnHeader, index]

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RowView

        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int = index.hashCode()

    override fun toString(): String = index.toString()
}

internal fun createDerivedRowView(rowView: RowView): DerivedRowView {
    val ref = rowView.tableView.tableViewRef.get()
    val defaultCellViewMeta = ref.defaultCellView
    val rowViewMeta = ref.rowViews[rowView.index]

    val height = rowViewMeta?.cellHeight
        ?: defaultCellViewMeta.cellHeight
        ?: DEFAULT_CELL_HEIGHT

    val classes = Collections.unmodifiableSortedSet((
            (rowViewMeta?.cellClasses?.toMutableSet() ?: mutableSetOf())
                    + (defaultCellViewMeta.cellClasses?.toMutableSet() ?: mutableSetOf())
            ).toSortedSet())

    val topics = Collections.unmodifiableSortedSet((
            (rowViewMeta?.cellTopics?.toMutableSet() ?: mutableSetOf())
                    + (defaultCellViewMeta.cellTopics?.toMutableSet() ?: mutableSetOf())
            ).toSortedSet())

    return DerivedRowView(rowView, height, classes, topics)
}

class DerivedRowView internal constructor(
    val rowView: RowView,
    val cellHeight: Long,
    val cellClasses: SortedSet<String>,
    val cellTopics: SortedSet<String>
) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = rowView.tableView

    val index: Long
        get() = rowView.index

    override fun iterator() = rowView.iterator()

    // TODO toString, hashCode, equals
}
