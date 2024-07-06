/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.*
import sigbla.app.internals.RefHolder
import sigbla.app.internals.Registry
import sigbla.app.internals.TableViewEventProcessor
import sigbla.app.pds.collection.Map as PMap
import sigbla.app.pds.collection.HashMap as PHashMap
import sigbla.app.pds.kollection.toImmutableSet
import sigbla.app.pds.kollection.immutableSetOf
import sigbla.app.pds.kollection.ImmutableSet as PSet
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.ThreadLocalRandom

// A table view is associated with one table, and holds metadata related on how to view a table.
// This includes among other things column widths, row heights, individual cell dimensions, styling, etc..

// TODO Would be good to have something like on<INACTIVE>(tableView) { .. } with something like
//      on<INACTIVE>(tableView) {
//        timeout = ...
//        events { .. }
//      }
//      This would allow for views with no clients to be cleaned up..
//      Might want on<NO_CLIENT>(tableView) { .. } rather than INACTIVE?
//      Allow for setting the time out value.. on<NO_CLIENT>(tableView) { timeout = .. }

internal val EMPTY_IMMUTABLE_STRING_SET = immutableSetOf<String>()

const val DEFAULT_CELL_HEIGHT = 20L
const val DEFAULT_CELL_WIDTH = 100L

internal data class ViewMeta(
    val cellHeight: Long? = null,
    val cellWidth: Long? = null,
    val cellClasses: PSet<String>? = null,
    val cellTopics: PSet<String>? = null,
    val positionValue: Position.Value? = null,
    val visibilityValue: Visibility.Value? = null
)

internal data class TableViewRef(
    val defaultCellView: ViewMeta = ViewMeta(),
    val columnViews: PMap<Header, ViewMeta> = PHashMap(),
    val rowViews: PMap<Long, ViewMeta> = PHashMap(),
    val cellViews: PMap<Pair<Header, Long>, ViewMeta> = PHashMap(),

    val resources: PMap<String, Pair<Long, suspend PipelineContext<*, ApplicationCall>.() -> Unit>> = PHashMap(),

    val tableTransformer: (Table.() -> Unit)? = null,
    val columnTransformers: PMap<Header, Column.() -> Unit> = PHashMap(),
    val rowTransformers: PMap<Long, Row.() -> Unit> = PHashMap(),
    val cellTransformers: PMap<Pair<Header, Long>, Cell<*>.() -> Unit> = PHashMap(),

    val table: Table? = null,

    val version: Long = Long.MIN_VALUE
)

// TODO Consider if we need a DerivedTableView as well?
class TableView internal constructor(
    val name: String?,
    val source: TableView?,
    internal val tableViewRef: RefHolder<TableViewRef>,
    internal val eventProcessor: TableViewEventProcessor = TableViewEventProcessor()
) : Iterable<DerivedCellView> {
    internal constructor(name: String?, table: Table?) : this(name, null, tableViewRef = RefHolder(TableViewRef(table = table)))
    internal constructor(table: Table) : this(table.name, table)
    internal constructor(name: String?) : this(name, if (name == null) null else Registry.getTable(name))

    val closed: Boolean get() = tableViewRef.closed

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

    val resources: Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>
        get() = tableViewRef.get()
            .resources
            .sortedBy { it.component2().first }
            .associate { it.component1() to it.component2().second }

    // TODO Consider if get(tableView: TableView) and set(..: TableView, ..) should be included for symmetry?

    /*
    // TODO?
    operator fun get(tableView: Companion) = this

    operator fun set(tableView: Companion, newTableView: TableView) {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                newTableView.tableViewRef.get().copy(
                    table = it.table,
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<TableView>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }
     */

    // TODO Consider if get(table: Table) and set(..: Table, ..) should be included for symmetry?

    private var transformedTableRef: Triple<Long, Long?, WeakReference<Table>>? = null

    operator fun get(table: Table.Companion): Table {
        synchronized(eventProcessor) {
            val ref = tableViewRef.get()
            val originalTable = ref.table

            if (transformedTableRef != null
                && transformedTableRef!!.first == ref.version
                && transformedTableRef!!.second == originalTable?.tableRef?.get()?.version) {
                val cachedTable = transformedTableRef!!.third.get()
                if (cachedTable != null) return cachedTable
            }

            val table = originalTable?.makeClone() ?: Table(name = null, source = null)

            ref.tableTransformer?.invoke(table)

            ref.columnTransformers.forEach {
                val header = it.component1()
                val init = it.component2()
                table[header].init()
            }

            ref.rowTransformers.forEach {
                val index = it.component1()
                val init = it.component2()
                table[index].init()
            }

            ref.cellTransformers.forEach {
                val key = it.component1()
                val init = it.component2()
                table[key.first][key.second].init()
            }

            transformedTableRef = Triple(ref.version, originalTable?.tableRef?.get()?.version, WeakReference(table))

            return table
        }
    }

    operator fun set(table: Table.Companion, newTable: Unit?) {
        set(table, null as Table?)
    }

    operator fun set(table: Table.Companion, newTable: Table?) {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    table = newTable,
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<SourceTable>(
                SourceTable(old, oldRef.table), SourceTable(new, newRef.table)
            )) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(table: TableTransformer.Companion): TableTransformer<*> {
        val ref = tableViewRef.get()
        val function = ref.tableTransformer ?: return UnitTableTransformer(this)
        return FunctionTableTransformer(this, function)
    }

    operator fun set(table: TableTransformer.Companion, tableTransformer: Unit?) {
        setTableTransformer(null)
    }

    operator fun set(table: TableTransformer.Companion, tableTransformer: TableTransformer<*>?) {
        setTableTransformer(tableTransformer?.function as? (Table.() -> Unit)?)
    }

    operator fun set(table: TableTransformer.Companion, tableTransformer: Table.() -> Unit) {
        setTableTransformer(tableTransformer)
    }

    private fun setTableTransformer(transformer: (Table.() -> Unit)?) {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    tableTransformer = transformer,
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val old = oldView[TableTransformer]
            val new = newView[TableTransformer]

            eventProcessor.publish(listOf(TableViewListenerEvent<TableTransformer<*>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellHeight: CellHeight.Companion): CellHeight<TableView, *> {
        val ref = tableViewRef.get()
        return when (val height = ref.defaultCellView.cellHeight) {
            is Long -> PixelCellHeight(this, height)
            else -> UnitCellHeight(this)
        }
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Unit?) {
        setCellHeight(null)
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Long?) {
        setCellHeight(height)
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Number?) {
        when (height) {
            is Int -> setCellHeight(height.toLong())
            is Long -> setCellHeight(height)
            null -> setCellHeight(null)
            else -> throw InvalidCellHeightException("Unsupported type: ${height::class}")
        }
    }

    operator fun set(cellHeight: CellHeight.Companion, height: CellHeight<*, *>?) {
        setCellHeight(height?.asLong)
    }

    private fun setCellHeight(height: Long?) {
        synchronized(eventProcessor) {
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

            eventProcessor.publish(listOf(TableViewListenerEvent<CellHeight<TableView, *>>(old[CellHeight], new[CellHeight])) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellWidth: CellWidth.Companion): CellWidth<TableView, *> {
        val ref = tableViewRef.get()
        return when (val width = ref.defaultCellView.cellWidth) {
            is Long -> PixelCellWidth(this, width)
            else -> UnitCellWidth(this)
        }
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Unit?) {
        setCellWidth(null)
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Long?) {
        setCellWidth(width)
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Number?) {
        when (width) {
            is Int -> setCellWidth(width.toLong())
            is Long -> setCellWidth(width)
            null -> setCellWidth(null)
            else -> throw InvalidCellWidthException("Unsupported type: ${width::class}")
        }
    }

    operator fun set(cellWidth: CellWidth.Companion, width: CellWidth<*, *>?) {
        setCellWidth(width?.asLong)
    }

    private fun setCellWidth(width: Long?) {
        synchronized(eventProcessor) {
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

            eventProcessor.publish(listOf(TableViewListenerEvent<CellWidth<TableView, *>>(old[CellWidth], new[CellWidth])) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellClasses: CellClasses.Companion): CellClasses<TableView> {
        val ref = tableViewRef.get()
        return CellClasses(this, ref.defaultCellView.cellClasses ?: immutableSetOf())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: Unit?) {
        setCellClasses(null)
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: String?) {
        if (classes == null) setCellClasses(null) else setCellClasses(immutableSetOf(classes))
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: Collection<String>?) {
        setCellClasses(classes?.toImmutableSet())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: CellClasses<*>?) {
        setCellClasses(classes?._classes)
    }

    private fun setCellClasses(classes: PSet<String>?) {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    defaultCellView = it.defaultCellView.copy(
                        cellClasses = if (classes?.isEmpty() == true) null else classes
                    ),
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<CellClasses<TableView>>(old[CellClasses], new[CellClasses])) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellTopics: CellTopics.Companion): CellTopics<TableView> {
        val ref = tableViewRef.get()
        return CellTopics(this, ref.defaultCellView.cellTopics ?: immutableSetOf())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: Unit?) {
        setCellTopics(null)
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: String?) {
        if (topics == null) setCellTopics(null) else setCellTopics(immutableSetOf(topics))
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: Collection<String>?) {
        setCellTopics(topics?.toImmutableSet())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: CellTopics<*>?) {
        setCellTopics(topics?._topics)
    }

    private fun setCellTopics(topics: PSet<String>?) {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    defaultCellView = it.defaultCellView.copy(
                        cellTopics = if (topics?.isEmpty() == true) null else topics
                    ),
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<CellTopics<TableView>>(old[CellTopics], new[CellTopics])) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(resource: Resource<*, *>): Resource<TableView, *> {
        val ref = tableViewRef.get()
        return when (val handler = ref.resources[resource.path]?.second) {
            null -> UnitResource(this, resource.path)
            else -> HandlerResource(this, resource.path, handler)
        }
    }

    operator fun set(resource: Resource<*, *>, handler: Unit?) {
        setResources(resource.path, null)
    }

    operator fun set(resource: Resource<*, *>, handler: (suspend PipelineContext<*, ApplicationCall>.() -> Unit)?) {
        setResources(resource.path, handler)
    }

    private fun setResources(path: String, handler: (suspend PipelineContext<*, ApplicationCall>.() -> Unit)?) {
        if (path.isEmpty()) throw InvalidValueException("TableView resource path cannot be empty")

        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    resources = if (handler == null) it.resources.remove(path)
                                else it.resources.put(path, Resource.RESOURCE_COUNTER.getAndIncrement() to handler),
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<Resource<*, *>>(old[Resource[path]], new[Resource[path]])) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(header: Header) = ColumnView(this, header)

    operator fun get(vararg header: String): ColumnView = get(
        Header(
            *header
        )
    )

    operator fun get(index: Long) = RowView(this, index)

    // -----

    operator fun get(header1: String, index: Long): CellView = this[header1][index]

    operator fun get(header1: String, header2: String, index: Long): CellView = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Long): CellView = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Long): CellView = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long): CellView = this[header1, header2, header3, header4, header5][index]

    // -----

    operator fun get(header1: String, row: Row): CellView = this[header1][row]

    operator fun get(header1: String, header2: String, row: Row): CellView = this[header1, header2][row]

    operator fun get(header1: String, header2: String, header3: String, row: Row): CellView = this[header1, header2, header3][row]

    operator fun get(header1: String, header2: String, header3: String, header4: String, row: Row): CellView = this[header1, header2, header3, header4][row]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row): CellView = this[header1, header2, header3, header4, header5][row]

    // -----

    operator fun get(header1: String, rowView: RowView): CellView = this[header1][rowView]

    operator fun get(header1: String, header2: String, rowView: RowView): CellView = this[header1, header2][rowView]

    operator fun get(header1: String, header2: String, header3: String, rowView: RowView): CellView = this[header1, header2, header3][rowView]

    operator fun get(header1: String, header2: String, header3: String, header4: String, rowView: RowView): CellView = this[header1, header2, header3, header4][rowView]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, rowView: RowView): CellView = this[header1, header2, header3, header4, header5][rowView]

    // -----

    operator fun get(index: Int): RowView = get(index.toLong())

    // -----

    operator fun get(header1: String, index: Int): CellView = this[header1][index]

    operator fun get(header1: String, header2: String, index: Int): CellView = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Int): CellView = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Int): CellView = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int): CellView = this[header1, header2, header3, header4, header5][index]

    // -----

    operator fun get(cell: Cell<*>): CellView = this[cell.column.header][cell.index]

    operator fun get(cellView: CellView) = this[cellView.columnView][cellView.index]

    operator fun get(derivedCellView: DerivedCellView) = this[derivedCellView.columnView][derivedCellView.index].derived

    // -----

    operator fun get(column: Column): ColumnView = this[column.header]

    operator fun get(columnView: ColumnView): ColumnView = this[columnView.header]

    // -----

    operator fun get(row: Row): RowView {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in get: $row")
        return this[row.index]
    }

    operator fun get(rowView: RowView) = this[rowView.index]

    // -----

    operator fun get(column: Column, index: Long): CellView = this[column.header, index]

    operator fun get(columnView: ColumnView, index: Long): CellView = this[columnView.header, index]

    operator fun get(header: Header, index: Long): CellView = CellView(get(header), index)

    // -----

    operator fun get(column: Column, rowView: RowView): CellView = this[column.header, rowView.index]

    operator fun get(columnView: ColumnView, rowView: RowView): CellView = this[columnView.header, rowView.index]

    operator fun get(header: Header, rowView: RowView): CellView = CellView(get(header), rowView.index)

    // -----

    operator fun get(column: Column, row: Row): CellView = this[column.header, row]

    operator fun get(columnView: ColumnView, row: Row): CellView = this[columnView.header, row]

    operator fun get(header: Header, row: Row): CellView {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in get: $row")
        return this[header, row.index]
    }

    // -----

    operator fun get(column: Column, index: Int): CellView = this[column.header, index]

    operator fun get(columnView: ColumnView, index: Int): CellView = this[columnView.header, index]

    operator fun get(header: Header, index: Int): CellView = CellView(get(header), index.toLong())

    // -----

    operator fun set(cell: Cell<*>, view: Unit?) {
        this[cell.column][cell.index] = view
    }

    operator fun set(cell: Cell<*>, view: CellView?) {
        this[cell.column][cell.index] = view
    }

    operator fun set(cell: Cell<*>, function: CellView.() -> Unit) {
        this[cell.column][cell.index].function()
    }

    // -----

    operator fun set(cellView: CellView, view: Unit?) {
        this[cellView.columnView][cellView.index] = view
    }

    operator fun set(cellView: CellView, view: CellView?) {
        this[cellView.columnView][cellView.index] = view
    }

    operator fun set(cellView: CellView, function: CellView.() -> Unit) {
        this[cellView.columnView][cellView.index].function()
    }

    // -----

    operator fun set(header: Header, view: ColumnView?) {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                val otherRef = if (view == null) null else view.tableView.tableViewRef.get()
                val viewMeta = if (otherRef == null) null else otherRef.columnViews[view!!.header]
                val transformer = if (otherRef == null) null else otherRef.columnTransformers[view!!.header]

                it.copy(
                    columnViews = if (viewMeta != null)
                        it.columnViews.put(header, viewMeta)
                    else
                        it.columnViews.remove(header),
                    columnTransformers = if (transformer != null)
                        it.columnTransformers.put(header, transformer)
                    else
                        it.columnTransformers.remove(header),
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val oldColumnView = oldView[header]
            val newColumnView = newView[header]

            eventProcessor.publish(listOf(
                TableViewListenerEvent(oldColumnView[CellClasses], newColumnView[CellClasses]),
                TableViewListenerEvent(oldColumnView[CellTopics], newColumnView[CellTopics]),
                TableViewListenerEvent(oldColumnView[CellWidth], newColumnView[CellWidth]),
                TableViewListenerEvent(oldColumnView[Position], newColumnView[Position]),
                TableViewListenerEvent(oldColumnView[Visibility], newColumnView[Visibility]),
                TableViewListenerEvent(oldColumnView[ColumnTransformer], newColumnView[ColumnTransformer])
            ))
        }
    }

    operator fun set(header: Header, view: Unit?) = set(header, null as ColumnView?)

    operator fun set(column: Column, view: Unit?) = set(column.header, null as ColumnView?)

    operator fun set(columnView: ColumnView, view: Unit?) = set(columnView.header, null as ColumnView?)

    operator fun set(column: Column, view: ColumnView?) = set(column.header, view)

    operator fun set(columnView: ColumnView, view: ColumnView?) = set(columnView.header, view)

    operator fun set(header: Header, function: ColumnView.() -> Unit) {
        this[header].function()
    }

    operator fun set(column: Column, function: ColumnView.() -> Unit) {
        this[column].function()
    }

    operator fun set(columnView: ColumnView, function: ColumnView.() -> Unit) {
        this[columnView].function()
    }

    // -----

    operator fun set(header1: String, function: ColumnView.() -> Unit) {
        this[header1].function()
    }

    operator fun set(header1: String, header2: String, function: ColumnView.() -> Unit) {
        this[header1, header2].function()
    }

    operator fun set(header1: String, header2: String, header3: String, function: ColumnView.() -> Unit) {
        this[header1, header2, header3].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, function: ColumnView.() -> Unit) {
        this[header1, header2, header3, header4].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, function: ColumnView.() -> Unit) {
        this[header1, header2, header3, header4, header5].function()
    }

    // -----

    operator fun set(index: Long, view: RowView?) {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                val otherRef = if (view == null) null else view.tableView.tableViewRef.get()
                val viewMeta = if (otherRef == null) null else otherRef.rowViews[view!!.index]
                val transformer = if (otherRef == null) null else otherRef.rowTransformers[view!!.index]

                it.copy(
                    rowViews = if (viewMeta != null)
                        it.rowViews.put(index, viewMeta)
                    else
                        it.rowViews.remove(index),
                    rowTransformers = if (transformer != null)
                        it.rowTransformers.put(index, transformer)
                    else
                        it.rowTransformers.remove(index),
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val oldRowView = oldView[index]
            val newRowView = newView[index]


            eventProcessor.publish(listOf(
                TableViewListenerEvent(oldRowView[CellClasses], newRowView[CellClasses]),
                TableViewListenerEvent(oldRowView[CellHeight], newRowView[CellHeight]),
                TableViewListenerEvent(oldRowView[CellTopics], newRowView[CellTopics]),
                TableViewListenerEvent(oldRowView[Position], newRowView[Position]),
                TableViewListenerEvent(oldRowView[Visibility], newRowView[Visibility]),
                TableViewListenerEvent(oldRowView[RowTransformer], newRowView[RowTransformer])
            ))
        }
    }

    operator fun set(index: Long, view: Unit?) = set(index, null as RowView?)

    operator fun set(index: Int, view: Unit?) = set(index.toLong(), null as RowView?)

    operator fun set(rowView: RowView, view: Unit?) = set(rowView.index, null as RowView?)

    operator fun set(row: Row, view: Unit?) {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, view)
    }

    operator fun set(index: Int, view: RowView?) = set(index.toLong(), view)

    operator fun set(rowView: RowView, view: RowView?) = set(rowView.index, view)

    operator fun set(row: Row, view: RowView?) {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        set(row.index, view)
    }

    operator fun set(index: Int, function: RowView.() -> Unit) {
        this[index].function()
    }

    operator fun set(index: Long, function: RowView.() -> Unit) {
        this[index].function()
    }

    operator fun set(rowView: RowView, function: RowView.() -> Unit) {
        this[rowView].function()
    }

    operator fun set(row: Row, function: RowView.() -> Unit) {
        this[row].function()
    }

    // -----

    operator fun set(column: Column, index: Int, view: Unit?) {
        this[column.header, index] = view
    }

    operator fun set(column: Column, row: Row, view: Unit?) {
        this[column.header, row] = view
    }

    operator fun set(column: Column, rowView: RowView, view: Unit?) {
        this[column.header, rowView] = view
    }

    operator fun set(column: Column, index: Long, view: Unit?) {
        this[column.header, index] = view
    }

    operator fun set(column: Column, index: Int, view: CellView?) {
        this[column.header, index] = view
    }

    operator fun set(column: Column, row: Row, view: CellView?) {
        this[column.header, row] = view
    }

    operator fun set(column: Column, rowView: RowView, view: CellView?) {
        this[column.header, rowView] = view
    }

    operator fun set(column: Column, index: Long, view: CellView?) {
        this[column.header, index] = view
    }

    // -----

    operator fun set(columnView: ColumnView, index: Int, view: Unit?) {
        this[columnView.header, index] = view
    }

    operator fun set(columnView: ColumnView, row: Row, view: Unit?) {
        this[columnView.header, row] = view
    }

    operator fun set(columnView: ColumnView, rowView: RowView, view: Unit?) {
        this[columnView.header, rowView] = view
    }

    operator fun set(columnView: ColumnView, index: Long, view: Unit?) {
        this[columnView.header, index] = view
    }

    operator fun set(columnView: ColumnView, index: Int, view: CellView?) {
        this[columnView.header, index] = view
    }

    operator fun set(columnView: ColumnView, row: Row, view: CellView?) {
        this[columnView.header, row] = view
    }

    operator fun set(columnView: ColumnView, rowView: RowView, view: CellView?) {
        this[columnView.header, rowView] = view
    }

    operator fun set(columnView: ColumnView, index: Long, view: CellView?) {
        this[columnView.header, index] = view
    }

    // -----

    operator fun set(header: Header, index: Int, view: Unit?) {
        this[header, index.toLong()] = null as CellView?
    }

    operator fun set(header: Header, row: Row, view: Unit?) {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        this[header, row.index] = null as CellView?
    }

    operator fun set(header: Header, rowView: RowView, view: Unit?) {
        this[header, rowView.index] = null as CellView?
    }

    operator fun set(header: Header, index: Long, view: Unit?) {
        this[header, index] = null as CellView?
    }

    operator fun set(header: Header, index: Int, view: CellView?) {
        this[header, index.toLong()] = view
    }

    operator fun set(header: Header, row: Row, view: CellView?) {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        this[header, row.index] = view
    }

    operator fun set(header: Header, rowView: RowView, view: CellView?) {
        this[header, rowView.index] = view
    }

    operator fun set(header: Header, index: Long, view: CellView?) {
        synchronized(eventProcessor) {
            val sourceCell = if (view == null) null else Pair(view.columnView.header, view.index)
            val cell = Pair(header, index)

            val (oldRef, newRef) = tableViewRef.refAction {
                val otherRef = if (view == null) null else view.tableView.tableViewRef.get()
                val viewMeta = if (otherRef == null) null else otherRef.cellViews[sourceCell!!]
                val transformer = if (otherRef == null) null else otherRef.cellTransformers[sourceCell!!]

                it.copy(
                    cellViews = if (viewMeta != null)
                        it.cellViews.put(cell, viewMeta)
                    else
                        it.cellViews.remove(cell),
                    cellTransformers = if (transformer != null)
                        it.cellTransformers.put(cell, transformer)
                    else
                        it.cellTransformers.remove(cell),
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val oldCellView = oldView[header][index]
            val newCellView = newView[header][index]

            eventProcessor.publish(listOf(
                TableViewListenerEvent(oldCellView[CellClasses], newCellView[CellClasses]),
                TableViewListenerEvent(oldCellView[CellHeight], newCellView[CellHeight]),
                TableViewListenerEvent(oldCellView[CellTopics], newCellView[CellTopics]),
                TableViewListenerEvent(oldCellView[CellTransformer], newCellView[CellTransformer]),
                TableViewListenerEvent(oldCellView[CellWidth], newCellView[CellWidth])
            ))
        }
    }

    // -----

    operator fun set(header: Header, index: Int, function: CellView.() -> Unit) {
        this[header][index].function()
    }

    operator fun set(header: Header, index: Long, function: CellView.() -> Unit) {
        this[header][index].function()
    }

    operator fun set(header: Header, row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[header][row].function()
    }

    operator fun set(header: Header, rowView: RowView, function: CellView.() -> Unit) {
        this[header][rowView].function()
    }

    // -----

    operator fun set(column: Column, index: Int, function: CellView.() -> Unit) {
        this[column][index].function()
    }

    operator fun set(column: Column, index: Long, function: CellView.() -> Unit) {
        this[column][index].function()
    }

    operator fun set(column: Column, row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[column][row].function()
    }

    operator fun set(column: Column, rowView: RowView, function: CellView.() -> Unit) {
        this[column][rowView].function()
    }

    // -----

    operator fun set(columnView: ColumnView, index: Int, function: CellView.() -> Unit) {
        this[columnView][index].function()
    }

    operator fun set(columnView: ColumnView, index: Long, function: CellView.() -> Unit) {
        this[columnView][index].function()
    }

    operator fun set(columnView: ColumnView, row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[columnView][row].function()
    }

    operator fun set(columnView: ColumnView, rowView: RowView, function: CellView.() -> Unit) {
        this[columnView][rowView].function()
    }

    // -----

    operator fun set(vararg header: String, view: Unit?) {
        this[Header(*header)] = view
    }

    operator fun set(vararg header: String, view: ColumnView?) {
        this[Header(*header)] = view
    }

    // -----

    operator fun set(header1: String, index: Long, view: Unit?) {
        this[header1][index] = view
    }

    operator fun set(header1: String, header2: String, index: Long, view: Unit?) {
        this[header1, header2][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, view: Unit?) {
        this[header1, header2, header3][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, view: Unit?) {
        this[header1, header2, header3, header4][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, view: Unit?) {
        this[header1, header2, header3, header4, header5][index] = view
    }

    operator fun set(header1: String, index: Long, view: CellView?) {
        this[header1][index] = view
    }

    operator fun set(header1: String, header2: String, index: Long, view: CellView?) {
        this[header1, header2][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, view: CellView?) {
        this[header1, header2, header3][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, view: CellView?) {
        this[header1, header2, header3, header4][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, view: CellView?) {
        this[header1, header2, header3, header4, header5][index] = view
    }

    // -----

    operator fun set(header1: String, row: Row, view: Unit?) {
        this[header1][row] = view
    }

    operator fun set(header1: String, header2: String, row: Row, view: Unit?) {
        this[header1, header2][row] = view
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, view: Unit?) {
        this[header1, header2, header3][row] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, view: Unit?) {
        this[header1, header2, header3, header4][row] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, view: Unit?) {
        this[header1, header2, header3, header4, header5][row] = view
    }

    operator fun set(header1: String, row: Row, view: CellView?) {
        this[header1][row] = view
    }

    operator fun set(header1: String, header2: String, row: Row, view: CellView?) {
        this[header1, header2][row] = view
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, view: CellView?) {
        this[header1, header2, header3][row] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, view: CellView?) {
        this[header1, header2, header3, header4][row] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, view: CellView?) {
        this[header1, header2, header3, header4, header5][row] = view
    }

    // -----

    operator fun set(header1: String, rowView: RowView, view: Unit?) {
        this[header1][rowView] = view
    }

    operator fun set(header1: String, header2: String, rowView: RowView, view: Unit?) {
        this[header1, header2][rowView] = view
    }

    operator fun set(header1: String, header2: String, header3: String, rowView: RowView, view: Unit?) {
        this[header1, header2, header3][rowView] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, rowView: RowView, view: Unit?) {
        this[header1, header2, header3, header4][rowView] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, rowView: RowView, view: Unit?) {
        this[header1, header2, header3, header4, header5][rowView] = view
    }

    operator fun set(header1: String, rowView: RowView, view: CellView?) {
        this[header1][rowView] = view
    }

    operator fun set(header1: String, header2: String, rowView: RowView, view: CellView?) {
        this[header1, header2][rowView] = view
    }

    operator fun set(header1: String, header2: String, header3: String, rowView: RowView, view: CellView?) {
        this[header1, header2, header3][rowView] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, rowView: RowView, view: CellView?) {
        this[header1, header2, header3, header4][rowView] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, rowView: RowView, view: CellView?) {
        this[header1, header2, header3, header4, header5][rowView] = view
    }

    // -----

    operator fun set(header1: String, index: Long, function: CellView.() -> Unit) {
        this[header1][index].function()
    }

    operator fun set(header1: String, header2: String, index: Long, function: CellView.() -> Unit) {
        this[header1, header2][index].function()
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, function: CellView.() -> Unit) {
        this[header1, header2, header3][index].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, function: CellView.() -> Unit) {
        this[header1, header2, header3, header4][index].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, function: CellView.() -> Unit) {
        this[header1, header2, header3, header4, header5][index].function()
    }

    // -----

    operator fun set(header1: String, row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[header1][row].function()
    }

    operator fun set(header1: String, header2: String, row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[header1, header2][row].function()
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[header1, header2, header3][row].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[header1, header2, header3, header4][row].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[header1, header2, header3, header4, header5][row].function()
    }

    // -----

    operator fun set(header1: String, rowView: RowView, function: CellView.() -> Unit) {
        this[header1][rowView].function()
    }

    operator fun set(header1: String, header2: String, rowView: RowView, function: CellView.() -> Unit) {
        this[header1, header2][rowView].function()
    }

    operator fun set(header1: String, header2: String, header3: String, rowView: RowView, function: CellView.() -> Unit) {
        this[header1, header2, header3][rowView].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, rowView: RowView, function: CellView.() -> Unit) {
        this[header1, header2, header3, header4][rowView].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, rowView: RowView, function: CellView.() -> Unit) {
        this[header1, header2, header3, header4, header5][rowView].function()
    }

    // -----

    operator fun set(header1: String, index: Int, view: Unit?) {
        this[header1][index] = view
    }

    operator fun set(header1: String, header2: String, index: Int, view: Unit?) {
        this[header1, header2][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, view: Unit?) {
        this[header1, header2, header3][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, view: Unit?) {
        this[header1, header2, header3, header4][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, view: Unit?) {
        this[header1, header2, header3, header4, header5][index] = view
    }

    operator fun set(header1: String, index: Int, view: CellView?) {
        this[header1][index] = view
    }

    operator fun set(header1: String, header2: String, index: Int, view: CellView?) {
        this[header1, header2][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, view: CellView?) {
        this[header1, header2, header3][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, view: CellView?) {
        this[header1, header2, header3, header4][index] = view
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, view: CellView?) {
        this[header1, header2, header3, header4, header5][index] = view
    }

    // -----

    operator fun set(header1: String, index: Int, function: CellView.() -> Unit) {
        this[header1][index].function()
    }

    operator fun set(header1: String, header2: String, index: Int, function: CellView.() -> Unit) {
        this[header1, header2][index].function()
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, function: CellView.() -> Unit) {
        this[header1, header2, header3][index].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, function: CellView.() -> Unit) {
        this[header1, header2, header3, header4][index].function()
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, function: CellView.() -> Unit) {
        this[header1, header2, header3, header4, header5][index].function()
    }

    // -----

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
                val columnView = ColumnView(this@TableView, cell.column.header)
                return createDerivedCellViewFromRef(ref, columnView, cell.index)
            }
        }
    }

    operator fun invoke(newValue: TableView?): TableView? {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                val otherRef = newValue?.tableViewRef?.get()

                it.copy(
                    defaultCellView = otherRef?.defaultCellView ?: ViewMeta(),
                    table = otherRef?.table,
                    tableTransformer = otherRef?.tableTransformer,
                    resources = otherRef?.resources ?: PHashMap()
                )
            }

            if (!eventProcessor.haveListeners()) return newValue

            val oldTableView = makeClone(ref = oldRef)
            val newTableView = makeClone(ref = newRef)

            val tableEvents = listOf(
                listOf(TableViewListenerEvent(SourceTable(oldTableView, oldRef.table), SourceTable(newTableView, newRef.table))),
                oldRef.resources
                    .sortedBy { it.component2().first }
                    .filter { !newRef.resources.containsKey(it.component1()) }
                    .map { TableViewListenerEvent(oldTableView[Resource[it.component1()]], newTableView[Resource[it.component1()]]) }
                    .toList(),
                newRef.resources
                    .sortedBy { it.component2().first }
                    .map { TableViewListenerEvent(oldTableView[Resource[it.component1()]], newTableView[Resource[it.component1()]]) }
                    .toList(),
                listOf(TableViewListenerEvent(oldTableView[CellClasses], newTableView[CellClasses])),
                listOf(TableViewListenerEvent(oldTableView[CellHeight], newTableView[CellHeight])),
                listOf(TableViewListenerEvent(oldTableView[CellTopics], newTableView[CellTopics])),
                listOf(TableViewListenerEvent(oldTableView[CellWidth], newTableView[CellWidth])),
                listOf(TableViewListenerEvent(oldTableView[TableTransformer], newTableView[TableTransformer]))
            ).flatten()

            eventProcessor.publish(tableEvents as List<TableViewListenerEvent<Any>>)

            return newValue
        }
    }

    operator fun invoke(newValue: CellHeight<*, *>?): CellHeight<*, *>? {
        this[CellHeight] = newValue
        return newValue
    }

    operator fun invoke(newValue: CellWidth<*, *>?): CellWidth<*, *>? {
        this[CellWidth] = newValue
        return newValue
    }

    operator fun invoke(newValue: CellClasses<*>?): CellClasses<*>? {
        this[CellClasses] = newValue
        return newValue
    }

    operator fun invoke(newValue: CellTopics<*>?): CellTopics<*>? {
        this[CellTopics] = newValue
        return newValue
    }

    operator fun invoke(newValue: TableTransformer<*>?): TableTransformer<*>? {
        this[TableTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: Resource<*, *>?): Resource<*, *>? {
        when (newValue) {
            is UnitResource<*> -> this[newValue] = Unit
            is HandlerResource<*> -> this[newValue] = newValue.handler
        }
        return newValue
    }

    operator fun invoke(newValue: Table?): Table? {
        this[Table] = newValue
        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        invoke(null as TableView?)
        return newValue
    }

    internal fun makeClone(name: String? = this.name, ref: TableViewRef = tableViewRef.get()) = TableView(name, this, RefHolder(ref))

    override fun toString() = "TableView[$name]"

    override fun equals(other: Any?): Boolean {
        // Implemented to ensure expected equality check to always just be a reference compare, that's what we want
        return this === other
    }

    override fun hashCode(): Int {
        // Clustered by name
        return Objects.hashCode(name)
    }

    companion object {
        operator fun get(name: String?): TableView {
            return if (name == null) TableView(null) // This will not be on the registry
            else get(name) { TableView(null) } // This will be on the registry
        }

        operator fun get(name: String, init: (name: String) -> TableView): TableView = Registry.getView(name, init)

        operator fun get(table: Table): TableView = get(table.name).apply { this[Table] = table }

        operator fun get(name: String, table: Table): TableView = get(name).apply { this[Table] = table }

        val names: SortedSet<String> get() = Registry.viewNames

        val views: Set<TableView> get() = Registry.views

        operator fun get(host: Host): String = Host.host

        operator fun set(_host: Host, host: String) {
            Host.host = host
        }

        operator fun get(port: Port): Int = Port.port

        operator fun set(_port: Port, port: Int) {
            Port.port = port
        }
    }
}

object Host {
    private var _host: String? = null

    init {
        val envHost = System.getenv("SIGBLA_HOST")
        (envHost ?: null)?.apply {
            _host = this
        }
    }

    internal var host: String
        get() {
            if (_host == null) _host = "127.0.0.1"
            return _host!!
        }
        set(host) {
            if (_host != null) return
            this._host = host
        }
}

object Port {
    private var _port: Int? = null

    init {
        val envPort = System.getenv("SIGBLA_PORT")
        (envPort ?: "").toIntOrNull()?.apply {
            _port = this
        }
    }

    internal var port: Int
        get() {
            if (_port == null) _port = ThreadLocalRandom.current().nextInt(1024, 65535)
            return _port!!
        }
        set(port) {
            if (_port != null) return
            this._port = port
        }
}

sealed class Transformer<S, T>(val source: S, val function: T)

abstract class TableTransformer<T>(source: TableView, function: T): Transformer<TableView, T>(source, function) {
    operator fun invoke(newValue: TableTransformer<*>?): TableTransformer<*>? {
        source[TableTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: (Table.() -> Unit)?): (Table.() -> Unit)? {
        if (newValue == null) source[TableTransformer] = Unit else source[TableTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        source[TableTransformer] = newValue
        return newValue
    }

    operator fun contains(other: TableTransformer<*>) = function == other.function
    operator fun contains(other: Table.() -> Unit) = function == other
    operator fun contains(other: Unit) = function == other

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TableTransformer<*>

        if (source != other.source) return false
        if (function != other.function) return false

        return true
    }

    override fun hashCode() = Objects.hashCode(this.function)

    companion object
}

class UnitTableTransformer internal constructor(
    source: TableView
): TableTransformer<Unit>(source, Unit) {
    override fun toString() = "UnitTableTransformer"
}

class FunctionTableTransformer internal constructor(
    source: TableView,
    function: Table.() -> Unit
): TableTransformer<Table.() -> Unit>(source, function) {
    override fun toString() = "FunctionTableTransformer[$function]"
}

abstract class Resource<S, T> internal constructor(
    val source: S,
    path: String,
    val handler: T
) {
    val path: String = path.trim().replace("^/+".toRegex(), "")

    operator fun invoke(newValue: (suspend PipelineContext<*, ApplicationCall>.() -> Unit)?): (suspend PipelineContext<*, ApplicationCall>.() -> Unit)? {
        when (source) {
            is TableView -> source[Resource[path]] = newValue
            is Companion -> Resource[path] = newValue
        }
        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        when (source) {
            is TableView -> source[Resource[path]] = newValue
            is Companion -> Resource[path] = newValue
        }
        return newValue
    }

    operator fun contains(other: String) = other == path
    operator fun contains(other: Unit) = other == handler
    operator fun contains(other: suspend PipelineContext<*, ApplicationCall>.() -> Unit) = other == handler
    operator fun contains(other: Resource<*, *>) = other.path == path && other.handler == handler
    operator fun contains(other: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>) = other.first == path && other.second == handler

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Resource<*, *>

        if (source != other.source) return false
        if (path != other.path) return false
        if (handler != other.handler) return false

        return true
    }

    override fun hashCode() = Objects.hash(this.path)

    companion object {
        internal val RESOURCE_COUNTER = AtomicLong(Long.MIN_VALUE)

        private val _resources: ConcurrentMap<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> = ConcurrentHashMap()

        val resources: Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>
            get() = _resources.entries.associate { it.component1() to it.component2() }

        operator fun get(path: String): Resource<Companion, *> {
            val cleanPath = path.trim().replace("^/+".toRegex(), "")
            return when (val handler = _resources[cleanPath]) {
                null -> UnitResource(this, cleanPath)
                else -> HandlerResource(this, cleanPath, handler)
            }
        }

        operator fun set(path: String, handler: (suspend PipelineContext<*, ApplicationCall>.() -> Unit)?) {
            val cleanPath = path.trim().replace("^/+".toRegex(), "")
            if (handler == null) _resources.remove(cleanPath)
            else _resources[cleanPath] = handler
        }

        operator fun set(path: String, handler: Unit?) {
            val cleanPath = path.trim().replace("^/+".toRegex(), "")
            _resources.remove(cleanPath)
        }
    }
}

class UnitResource<S> internal constructor(source: S, path: String): Resource<S, Unit>(source, path, Unit) {
    override fun toString() = "UnitResource[$path]"
}
class HandlerResource<S> internal constructor(
    source: S, path: String, handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit
): Resource<S, suspend PipelineContext<*, ApplicationCall>.() -> Unit>(source, path, handler) {
    override fun toString() = "HandlerResource[$path]"
}

// TODO Should introduce a generic type S like else where..
// TODO Instead of table being nullable, have UnitSource and TableSource?
class SourceTable internal constructor(
    val source: TableView,
    val table: Table?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SourceTable

        if (source != other.source) return false
        if (table != other.table) return false

        return true
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + (table?.hashCode() ?: 0)
        return result
    }

    override fun toString() = "SourceTable[$source, ${table?.toString() ?: "null"}]"
}

sealed class Position<S, T>(
    val source: S,
    val position: T
) {
    enum class Value { LEFT, RIGHT, TOP, BOTTOM }

    open val isValue: Boolean = false
    open val asValue: Value? = null

    operator fun contains(other: Value) = position == other
    operator fun contains(other: Unit) = position == other

    override fun hashCode() = Objects.hashCode(this.position)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position<*, *>

        if (source != other.source) return false
        if (position != other.position) return false

        return true
    }

    open class Horizontal<T> internal constructor(source: ColumnView, position: T) : Position<ColumnView, T>(source, position) {
        operator fun contains(other: Horizontal<*>) = position == other.position
        operator fun contains(other: HorizontalCompanion) = position == other.asValue

        operator fun invoke(newValue: Unit?): Unit? {
            source[Position] = newValue
            return newValue
        }

        operator fun invoke(newValue: Horizontal<*>?): Horizontal<*>? {
            source[Position] = newValue
            return newValue
        }

        operator fun invoke(newValue: HorizontalCompanion?): HorizontalCompanion? {
            source[Position] = newValue
            return newValue
        }

        override fun toString() = "Horizontal"
    }

    open class Vertical<T> internal constructor(source: RowView, position: T) : Position<RowView, T>(source, position) {
        operator fun contains(other: Vertical<*>) = position == other.position
        operator fun contains(other: VerticalCompanion) = position == other.asValue

        operator fun invoke(newValue: Unit?): Unit? {
            source[Position] = newValue
            return newValue
        }

        operator fun invoke(newValue: Vertical<*>?): Vertical<*>? {
            source[Position] = newValue
            return newValue
        }

        operator fun invoke(newValue: VerticalCompanion?): VerticalCompanion? {
            source[Position] = newValue
            return newValue
        }

        override fun toString() = "Vertical"
    }

    interface HorizontalCompanion {
        val asValue: Value
    }

    interface VerticalCompanion {
        val asValue: Value
    }

    class Left internal constructor(source: ColumnView) : Horizontal<Value>(source, Value.LEFT) {
        override val isValue: Boolean = true
        override val asValue: Value = position

        override fun toString() = "Left"

        companion object : HorizontalCompanion {
            override val asValue = Value.LEFT
        }
    }

    class Right internal constructor(source: ColumnView) : Horizontal<Value>(source, Value.RIGHT) {
        override val isValue: Boolean = true
        override val asValue: Value = position

        override fun toString() = "Right"

        companion object : HorizontalCompanion {
            override val asValue = Value.RIGHT
        }
    }

    class Top internal constructor(source: RowView) : Vertical<Value>(source, Value.TOP) {
        override val isValue: Boolean = true
        override val asValue: Value = position

        override fun toString() = "Top"

        companion object : VerticalCompanion {
            override val asValue = Value.TOP
        }
    }

    class Bottom internal constructor(source: RowView) : Vertical<Value>(source, Value.BOTTOM) {
        override val isValue: Boolean = true
        override val asValue: Value = position

        override fun toString() = "Bottom"

        companion object : VerticalCompanion {
            override val asValue = Value.BOTTOM
        }
    }

    companion object
}

// TODO Support tableView[-1][Visibility] and related to hide/show headers?
sealed class Visibility<S, T>(
    val source: S,
    val visibility: T
) {
    enum class Value { SHOW, HIDE }

    abstract val isValue: Boolean
    abstract val asValue: Value?

    operator fun contains(other: Visibility<*, *>) = visibility == other.visibility
    operator fun contains(other: VisibilityCompanion) = visibility == other.asValue
    operator fun contains(other: Value) = visibility == other
    operator fun contains(other: Unit) = visibility == other

    operator fun invoke(newValue: Unit?): Unit? {
        when (val source = source) {
            is ColumnView -> source[Visibility] = newValue
            is RowView -> source[Visibility] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: Visibility<*, *>?): Visibility<*, *>? {
        when (val source = source) {
            is ColumnView -> source[Visibility] = newValue
            is RowView -> source[Visibility] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: VisibilityCompanion?): VisibilityCompanion? {
        when (val source = source) {
            is ColumnView -> source[Visibility] = newValue
            is RowView -> source[Visibility] = newValue
        }

        return newValue
    }

    override fun hashCode() = Objects.hashCode(this.visibility)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Visibility<*, *>

        if (source != other.source) return false
        if (visibility != other.visibility) return false

        return true
    }

    interface VisibilityCompanion {
        val asValue: Value
    }

    class Undefined<S> internal constructor(source: S) : Visibility<S, Unit>(source, Unit) {
        override val isValue: Boolean = false
        override val asValue: Value? = null

        override fun toString() = "Undefined"
    }

    class Show<S> internal constructor(source: S) : Visibility<S, Value>(source, Value.SHOW) {
        override val isValue: Boolean = true
        override val asValue: Value = visibility

        override fun toString() = "Show"

        companion object : VisibilityCompanion {
            override val asValue = Value.SHOW
        }
    }

    class Hide<S> internal constructor(source: S) : Visibility<S, Value>(source, Value.HIDE) {
        override val isValue: Boolean = true
        override val asValue: Value = visibility

        override fun toString() = "Hide"

        companion object : VisibilityCompanion {
            override val asValue = Value.HIDE
        }
    }

    companion object
}

// TODO Add support for disabling marker, hide column and row headers (but hiding columns can be done with empty header/negative index)
data class ViewConfig(
    val title: String,

    val marginTop: Long,
    val marginBottom: Long,
    val marginLeft: Long,
    val marginRight: Long,

    val paddingTop: Long,
    val paddingBottom: Long,
    val paddingLeft: Long,
    val paddingRight: Long,

    val topSeparatorHeight: Long,
    val bottomSeparatorHeight: Long,
    val leftSeparatorWidth: Long,
    val rightSeparatorWidth: Long,

    val defaultColumnVisibility: Visibility.VisibilityCompanion,
    val defaultRowVisibility: Visibility.VisibilityCompanion,

    val tableHtml: suspend PipelineContext<*, ApplicationCall>.() -> Unit,
    val tableScript: suspend PipelineContext<*, ApplicationCall>.() -> Unit,
    val tableStyle: suspend PipelineContext<*, ApplicationCall>.() -> Unit
)

fun compactViewConfig(
    title: String = "Table",
    defaultColumnVisibility: Visibility.VisibilityCompanion = Visibility.Show,
    defaultRowVisibility: Visibility.VisibilityCompanion = Visibility.Show
): ViewConfig = ViewConfig(
    title = title,

    marginTop = 0,
    marginBottom = 0,
    marginLeft = 0,
    marginRight = 0,

    paddingTop = 0,
    paddingBottom = 0,
    paddingLeft = 0,
    paddingRight = 0,

    topSeparatorHeight = 2,
    bottomSeparatorHeight = 2,
    leftSeparatorWidth = 2,
    rightSeparatorWidth = 2,

    defaultColumnVisibility = defaultColumnVisibility,
    defaultRowVisibility = defaultRowVisibility,

    tableHtml = {
        call.respondText(ContentType.Text.Html, HttpStatusCode.OK) {
            this.javaClass.getResource("/table/table.html").readText().replace("\${title}", title)
        }
    },
    tableScript = staticResource("/table/table.js"),
    tableStyle = staticResource("/table/compact.css")
)

fun spaciousViewConfig(
    title: String = "Table",
    defaultColumnVisibility: Visibility.VisibilityCompanion = Visibility.Show,
    defaultRowVisibility: Visibility.VisibilityCompanion = Visibility.Show
): ViewConfig = ViewConfig(
    title = title,

    marginTop = 1,
    marginBottom = 1,
    marginLeft = 1,
    marginRight = 1,

    paddingTop = 1,
    paddingBottom = 1,
    paddingLeft = 1,
    paddingRight = 1,

    topSeparatorHeight = 3,
    bottomSeparatorHeight = 3,
    leftSeparatorWidth = 3,
    rightSeparatorWidth = 3,

    defaultColumnVisibility = defaultColumnVisibility,
    defaultRowVisibility = defaultRowVisibility,

    tableHtml = {
        call.respondText(ContentType.Text.Html, HttpStatusCode.OK) {
            this.javaClass.getResource("/table/table.html").readText().replace("\${title}", title)
        }
    },
    tableScript = staticResource("/table/table.js"),
    tableStyle = staticResource("/table/spacious.css")
)
