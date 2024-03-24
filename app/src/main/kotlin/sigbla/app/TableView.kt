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
import kotlin.collections.LinkedHashMap
import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.ThreadLocalRandom

// A table view is associated with one table, and holds metadata related on how to view a table.
// This includes among other things column widths, row heights, individual cell dimensions, styling, etc..

// TODO Add transformers outside just CellView?

// TODO Would be good to have something like on<INACTIVE>(tableView) { .. } with something like
//      on<INACTIVE>(tableView) {
//        timeout = ...
//        events { .. }
//      }
//      This would allow for views with no clients to be cleaned up..
//      Might want on<NO_CLIENT>(tableView) { .. } rather than INACTIVE?
//      Allow for setting the time out value.. on<NO_CLIENT>(tableView) { timeout = .. }

private val EMPTY_IMMUTABLE_STRING_SET = immutableSetOf<String>()

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
    onRegistry: Boolean = true,
    internal val tableViewRef: RefHolder<TableViewRef>,
    internal val eventProcessor: TableViewEventProcessor = TableViewEventProcessor()
) : Iterable<DerivedCellView> {
    internal constructor(name: String?, table: Table?) : this(name, null, tableViewRef = RefHolder(TableViewRef(table = table)))
    internal constructor(table: Table) : this(table.name, table)
    internal constructor(name: String?) : this(name, if (name == null) null else Registry.getTable(name))

    init {
        if (name != null && onRegistry) Registry.setView(name, this)
    }

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

            val table = originalTable?.makeClone() ?: BaseTable(name = null, source = null, onRegistry = false)

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

    operator fun set(cellHeight: CellHeight.Companion, height: Long) {
        setCellHeight(height)
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Number) {
        when (height) {
            is Int -> setCellHeight(height.toLong())
            is Long -> setCellHeight(height)
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

    operator fun set(cellWidth: CellWidth.Companion, width: Long) {
        setCellWidth(width)
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Number) {
        when (width) {
            is Int -> setCellWidth(width.toLong())
            is Long -> setCellWidth(width)
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

    operator fun set(cellClasses: CellClasses.Companion, classes: String) {
        setCellClasses(immutableSetOf(classes))
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: Collection<String>) {
        setCellClasses(classes.toImmutableSet())
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

    operator fun set(cellTopics: CellTopics.Companion, topics: String) {
        setCellTopics(immutableSetOf(topics))
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: Collection<String>) {
        setCellTopics(topics.toImmutableSet())
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

    operator fun get(resources: Resources.Companion): Resources {
        val ref = tableViewRef.get()
        return Resources(this, ref.resources)
    }

    operator fun set(resources: Resources.Companion, resource: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>) {
        setResources(PHashMap<String, Pair<Long, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>().put(resource.first, Resources.RESOURCE_COUNTER.getAndIncrement() to resource.second))
    }

    operator fun set(resources: Resources.Companion, newResources: Collection<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>) {
        setResources(newResources.fold(PHashMap()) { acc, r -> acc.put(r.first, Resources.RESOURCE_COUNTER.getAndIncrement() to r.second)})
    }

    operator fun set(resources: Resources.Companion, newResources: Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>) {
        setResources(newResources.entries.fold(PHashMap()) { acc, r -> acc.put(r.key, Resources.RESOURCE_COUNTER.getAndIncrement() to r.value)})
    }

    operator fun set(resources: Resources.Companion, newResources: Resources?) {
        setResources(newResources?._resources)
    }

    private fun setResources(resources: PMap<String, Pair<Long, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>?) {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                it.copy(
                    resources = resources ?: PHashMap(),
                    version = it.version + 1L
                )
            }

            if (!eventProcessor.haveListeners()) return

            val old = makeClone(ref = oldRef)
            val new = makeClone(ref = newRef)

            eventProcessor.publish(listOf(TableViewListenerEvent<Resources>(old[Resources], new[Resources])) as List<TableViewListenerEvent<Any>>)
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

    operator fun set(cell: Cell<*>, view: CellView?) {
        this[cell.column][cell.index] = view
    }

    operator fun set(cell: Cell<*>, function: CellView.() -> Unit) {
        this[cell.column][cell.index].function()
    }

    // -----

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
                TableViewListenerEvent(oldColumnView[ColumnTransformer], newColumnView[ColumnTransformer])
            ))
        }
    }

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
                TableViewListenerEvent(oldRowView[RowTransformer], newRowView[RowTransformer])
            ))
        }
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

    operator fun set(vararg header: String, view: ColumnView?) {
        this[Header(*header)] = view
    }

    // -----

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

    operator fun invoke(function: TableView.() -> Any?): Any? {
        return when (val value = function()) {
            is CellHeight<*, *> -> { this[CellHeight] = value; value }
            is CellWidth<*, *> -> { this[CellWidth] = value; value }
            is CellClasses<*> -> { this[CellClasses] = value; value }
            is CellTopics<*> -> { this[CellTopics] = value; value }
            is TableTransformer<*> -> { this[TableTransformer] = value; value }
            is Resources -> { this[Resources] = value; value }
            is Unit -> { /* no assignment */ Unit }
            is Function1<*, *> -> { invoke(value as TableView.() -> Any?) }
            is Table -> { this[Table] = value; value }
            is TableView -> {
                batch(this) {
                    this[CellHeight] = value[CellHeight]
                    this[CellWidth] = value[CellWidth]
                    this[CellClasses] = value[CellClasses]
                    this[CellTopics] = value[CellTopics]
                    this[TableTransformer] = value[TableTransformer]
                    this[Resources] = value[Resources]
                    this[Table] = value[Table]
                }
                null
            }
            null -> {
                batch(this) {
                    this[CellHeight] = null
                    this[CellWidth] = null
                    this[CellClasses] = null
                    this[CellTopics] = null
                    this[TableTransformer] = null
                    this[Resources] = null
                    this[Table] = null
                }
                null
            }
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }
    }

    internal fun makeClone(name: String? = this.name, onRegistry: Boolean = false, ref: TableViewRef = tableViewRef.get()) = TableView(name, this, onRegistry, RefHolder(ref))

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
        operator fun get(name: String?): TableView = TableView(name)

        operator fun get(table: Table): TableView = TableView(table)

        operator fun get(name: String, table: Table): TableView = TableView(name, table)

        fun fromRegistry(name: String): TableView = Registry.getView(name) ?: throw InvalidTableViewException("No table view by name $name")

        fun fromRegistry(name: String, init: (String) -> TableView): TableView = Registry.getView(name, init)

        fun remove(name: String) = Registry.shutdownView(fromRegistry(name), true)

        val names: SortedSet<String> get() = Registry.viewNames

        val views: Set<TableView> get() = Registry.views

        // TODO Consider a operator get/set(resources: Resources, ..) on this level as well to allow for global resources

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

class CellView(
    val columnView: ColumnView,
    val index: Long
) : Iterable<DerivedCellView> {
    operator fun get(cellHeight: CellHeight.Companion): CellHeight<CellView, *> {
        val ref = tableView.tableViewRef.get()
        return when (val height = ref.cellViews[Pair(columnView.header, index)]?.cellHeight) {
            is Long -> PixelCellHeight(this, height)
            else -> UnitCellHeight(this)
        }
    }

    // TODO Ensure cell height and width is rendered (currently ignored)
    operator fun set(cellHeight: CellHeight.Companion, height: Long) {
        setCellHeight(height)
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Number) {
        when (height) {
            is Int -> setCellHeight(height.toLong())
            is Long -> setCellHeight(height)
            else -> throw InvalidCellHeightException("Unsupported type: ${height::class}")
        }
    }

    operator fun set(cellHeight: CellHeight.Companion, height: CellHeight<*, *>?) {
        setCellHeight(height?.asLong)
    }

    private fun setCellHeight(height: Long?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)
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

            val old = oldView[columnView.header][index][CellHeight]
            val new = newView[columnView.header][index][CellHeight]

            columnView.tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellHeight<CellView, *>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellWidth: CellWidth.Companion): CellWidth<CellView, *> {
        val ref = tableView.tableViewRef.get()
        return when (val width = ref.cellViews[Pair(columnView.header, index)]?.cellWidth) {
            is Long -> PixelCellWidth(this, width)
            else -> UnitCellWidth(this)
        }
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Long) {
        setCellWidth(width)
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Number) {
        when (width) {
            is Int -> setCellWidth(width.toLong())
            is Long -> setCellWidth(width)
            else -> throw InvalidCellWidthException("Unsupported type: ${width::class}")
        }
    }

    operator fun set(cellWidth: CellWidth.Companion, width: CellWidth<*, *>?) {
        setCellWidth(width?.asLong)
    }

    private fun setCellWidth(width: Long?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)
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

            val old = oldView[columnView.header][index][CellWidth]
            val new = newView[columnView.header][index][CellWidth]

            columnView.tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellWidth<CellView, *>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellClasses: CellClasses.Companion): CellClasses<CellView> {
        val ref = tableView.tableViewRef.get()
        return CellClasses(this, ref.cellViews[Pair(columnView.header, index)]?.cellClasses ?: immutableSetOf())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: String) {
        setCellClasses(immutableSetOf(classes))
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: Collection<String>) {
        setCellClasses(classes.toImmutableSet())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: CellClasses<*>?) {
        setCellClasses(classes?._classes)
    }

    private fun setCellClasses(classes: PSet<String>?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellClasses = if (classes?.isEmpty() == true) null else classes) ?: ViewMeta(cellClasses = if (classes?.isEmpty() == true) null else classes)

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.header][index][CellClasses]
            val new = newView[columnView.header][index][CellClasses]

            columnView.tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellClasses<CellView>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellTopics: CellTopics.Companion): CellTopics<CellView> {
        val ref = tableView.tableViewRef.get()
        return CellTopics(this, ref.cellViews[Pair(columnView.header, index)]?.cellTopics ?: immutableSetOf())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: String) {
        setCellTopics(immutableSetOf(topics))
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: Collection<String>) {
        setCellTopics(topics.toImmutableSet())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: CellTopics<*>?) {
        setCellTopics(topics?._topics)
    }

    private fun setCellTopics(topics: PSet<String>?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellTopics = if (topics?.isEmpty() == true) null else topics) ?: ViewMeta(cellTopics = if (topics?.isEmpty() == true) null else topics)

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.header][index][CellTopics]
            val new = newView[columnView.header][index][CellTopics]

            columnView.tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellTopics<CellView>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cell: CellTransformer.Companion): CellTransformer<*> {
        val ref = tableView.tableViewRef.get()
        val function = ref.cellTransformers[Pair(columnView.header, index)] ?: return UnitCellTransformer(this)
        return FunctionCellTransformer(this, function)
    }

    operator fun set(cell: CellTransformer.Companion, cellTransformer: CellTransformer<*>?) {
        setCellTransformer(cellTransformer?.function as? (Cell<*>.() -> Unit)?)
    }

    operator fun set(cell: CellTransformer.Companion, cellTransformer: Cell<*>.() -> Unit) {
        setCellTransformer(cellTransformer)
    }

    private fun setCellTransformer(transformer: (Cell<*>.() -> Unit)?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)

                it.copy(
                    cellTransformers = if (transformer == null) it.cellTransformers.remove(key) else it.cellTransformers.put(key, transformer),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.header][index][CellTransformer]
            val new = newView[columnView.header][index][CellTransformer]

            columnView.tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellTransformer<*>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun invoke(function: CellView.() -> Any?): Any? {
        return when (val value = function()) {
            is CellView -> { tableView[this] = value; value }
            is CellHeight<*, *> -> { tableView[this][CellHeight] = value; value }
            is CellWidth<*, *> -> { tableView[this][CellWidth] = value; value }
            is CellClasses<*> -> { tableView[this][CellClasses] = value; value }
            is CellTopics<*> -> { tableView[this][CellTopics] = value; value }
            is CellTransformer<*> -> { tableView[this][CellTransformer] = value; value }
            is Unit -> { /* no assignment */ Unit }
            is Function1<*, *> -> { invoke(value as CellView.() -> Any?) }
            null -> { tableView[this] = null; null }
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }
    }

    val tableView: TableView
        get() = columnView.tableView

    val cell: Cell<*>?
        get() = tableView[Table]?.let { return it[columnView.header][index] }

    val derived: DerivedCellView
        get() = createDerivedCellViewFromRef(this.tableView.tableViewRef.get(), columnView, index)

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableView.tableViewRef.get()
        if (ref.table == null || ref.table.tableRef.get().columnCells.get(this.columnView.header)?.get(this.index) == null)
            return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val derivedCellView = createDerivedCellViewFromRef(ref, columnView, index)

        return listOf(derivedCellView).iterator()
    }

    override fun toString() = "CellView[${columnView.header.labels.joinToString(limit = 30)}, $index]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellView

        if (columnView != other.columnView) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = columnView.hashCode()
        result = 31 * result + index.hashCode()
        return result
    }
}

internal fun createDerivedCellViewFromRef(ref: TableViewRef, columnView: ColumnView, index: Long): DerivedCellView {
    val columnHeader = columnView.header
    val cellViewMeta = ref.cellViews[Pair(columnHeader, index)]
    val defaultCellViewMeta = ref.defaultCellView
    val columnViewMeta = ref.columnViews[columnHeader]
    val rowViewMeta = ref.rowViews[index]

    val height = cellViewMeta?.cellHeight
        ?: rowViewMeta?.cellHeight
        ?: defaultCellViewMeta.cellHeight
        ?: DEFAULT_CELL_HEIGHT

    val width = cellViewMeta?.cellWidth
        ?: columnViewMeta?.cellWidth
        ?: defaultCellViewMeta.cellWidth
        ?: DEFAULT_CELL_WIDTH

    val classes = (cellViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (columnViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (rowViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET)

    val topics = (cellViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (columnViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (rowViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET)

    return DerivedCellView(columnView, index, height, width, classes, topics)
}

class DerivedCellView internal constructor(
    val columnView: ColumnView,
    val index: Long,
    val cellHeight: Long,
    val cellWidth: Long,
    classes: PSet<String>,
    topics: PSet<String>
) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = columnView.tableView

    val cellView: CellView
        get() = columnView[index]

    // Note: This is assigned on init to preserve the derived nature of the class
    val cell: Cell<*> = tableView[Table].let { it[columnView.header][index] }

    val cellClasses: CellClasses<DerivedCellView> by lazy {
        CellClasses(this, classes)
    }

    val cellTopics: CellTopics<DerivedCellView> by lazy {
        CellTopics(this, topics)
    }

    override fun iterator(): Iterator<DerivedCellView> = cellView.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DerivedCellView

        if (columnView != other.columnView) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = columnView.hashCode()
        result = 31 * result + index.hashCode()
        return result
    }

    override fun toString() = "DerivedCellView[${columnView.header.labels.joinToString(limit = 30)}, $index]"
}

class ColumnView internal constructor(
    val tableView: TableView,
    val header: Header
) : Iterable<DerivedCellView> {
    init {
        // Best efforts to help columns appear in the order they are referenced,
        // but ultimately this is controlled by the underlying table.
        // TODO Do != instead of !== to allow row header style control?
        if (header !== emptyHeader) tableView.tableViewRef.get().table?.get(header)
    }

    operator fun get(column: ColumnTransformer.Companion): ColumnTransformer<*> {
        val ref = tableView.tableViewRef.get()
        val function = ref.columnTransformers[header] ?: return UnitColumnTransformer(this)
        return FunctionColumnTransformer(this, function)
    }

    operator fun set(column: ColumnTransformer.Companion, columnTransformer: ColumnTransformer<*>?) {
        setColumnTransformer(columnTransformer?.function as? (Column.() -> Unit)?)
    }

    operator fun set(column: ColumnTransformer.Companion, columnTransformer: Column.() -> Unit) {
        setColumnTransformer(columnTransformer)
    }

    private fun setColumnTransformer(transformer: (Column.() -> Unit)?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                it.copy(
                    columnTransformers = if (transformer == null) it.columnTransformers.remove(header) else it.columnTransformers.put(header, transformer),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[header][ColumnTransformer]
            val new = newView[header][ColumnTransformer]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<ColumnTransformer<*>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellWidth: CellWidth.Companion): CellWidth<ColumnView, *> {
        val ref = tableView.tableViewRef.get()
        return when (val width = ref.columnViews[header]?.cellWidth) {
            is Long -> PixelCellWidth(this, width)
            else -> UnitCellWidth(this)
        }
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Long) {
        setCellWidth(width)
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Number) {
        when (width) {
            is Int -> setCellWidth(width.toLong())
            is Long -> setCellWidth(width)
            else -> throw InvalidCellWidthException("Unsupported type: ${width::class}")
        }
    }

    operator fun set(cellWidth: CellWidth.Companion, width: CellWidth<*, *>?) {
        setCellWidth(width?.asLong)
    }

    private fun setCellWidth(width: Long?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[header]
                val viewMeta = oldMeta?.copy(cellWidth = width) ?: ViewMeta(cellWidth = width)

                it.copy(
                    columnViews = it.columnViews.put(header, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[header][CellWidth]
            val new = newView[header][CellWidth]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellWidth<ColumnView, *>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellClasses: CellClasses.Companion): CellClasses<ColumnView> {
        val ref = tableView.tableViewRef.get()
        return CellClasses(this, ref.columnViews[header]?.cellClasses ?: immutableSetOf())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: String) {
        setCellClasses(immutableSetOf(classes))
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: Collection<String>) {
        setCellClasses(classes.toImmutableSet())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: CellClasses<*>?) {
        setCellClasses(classes?._classes)
    }

    private fun setCellClasses(classes: PSet<String>?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[header]
                val viewMeta = oldMeta?.copy(cellClasses = if (classes?.isEmpty() == true) null else classes) ?: ViewMeta(cellClasses = if (classes?.isEmpty() == true) null else classes)

                it.copy(
                    columnViews = it.columnViews.put(header, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[header][CellClasses]
            val new = newView[header][CellClasses]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellClasses<ColumnView>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellTopics: CellTopics.Companion): CellTopics<ColumnView> {
        val ref = tableView.tableViewRef.get()
        return CellTopics(this, ref.columnViews[header]?.cellTopics ?: immutableSetOf())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: String) {
        setCellTopics(immutableSetOf(topics))
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: Collection<String>) {
        setCellTopics(topics.toImmutableSet())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: CellTopics<*>?) {
        setCellTopics(topics?._topics)
    }

    private fun setCellTopics(topics: PSet<String>?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[header]
                val viewMeta = oldMeta?.copy(cellTopics = if (topics?.isEmpty() == true) null else topics) ?: ViewMeta(cellTopics = if (topics?.isEmpty() == true) null else topics)

                it.copy(
                    columnViews = it.columnViews.put(header, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[header][CellTopics]
            val new = newView[header][CellTopics]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellTopics<ColumnView>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun invoke(function: ColumnView.() -> Any?): Any? {
        return when (val value = function()) {
            is ColumnView -> { tableView[this] = value; value }
            is CellWidth<*, *> -> { tableView[this][CellWidth] = value; value }
            is CellClasses<*> -> { tableView[this][CellClasses] = value; value }
            is CellTopics<*> -> { tableView[this][CellTopics] = value; value }
            is ColumnTransformer<*> -> { tableView[this][ColumnTransformer] = value; value }
            is Unit -> { /* no assignment */ Unit }
            is Function1<*, *> -> { invoke(value as ColumnView.() -> Any?) }
            null -> { tableView[this] = null; null }
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }
    }

    // Note: cellViews return the defined CellViews, while the ColumnView iterator
    // returns the calculated cell views for current cells
    val cellViews: Sequence<CellView>
        get() = tableView.tableViewRef.get()
            .cellViews
            .keys()
            .filter { it.first == header }
            .asSequence()
            .map {
                CellView(ColumnView(this.tableView, it.first), it.second)
            }

    val derived: DerivedColumnView
        get() = createDerivedColumnView(this)

    operator fun get(index: Long): CellView = tableView[header, index]

    operator fun get(index: Int) = get(index.toLong())

    operator fun get(row: Row): CellView {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in get: $row")
        return get(row.index)
    }

    operator fun get(rowView: RowView) = get(rowView.index)

    operator fun set(index: Long, view: CellView?) { tableView[header, index] = view }

    operator fun set(index: Int, view: CellView?) { this[index.toLong()] = view }

    operator fun set(row: Row, view: CellView?) {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        this[row.index] = view
    }

    operator fun set(rowView: RowView, view: CellView?) { this[rowView.index] = view }

    operator fun set(index: Long, function: CellView.() -> Unit) { this[index].function() }

    operator fun set(index: Int, function: CellView.() -> Unit) { this[index].function() }

    operator fun set(row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[row].function()
    }

    operator fun set(rowView: RowView, function: CellView.() -> Unit) { this[rowView.index].function() }

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableView.tableViewRef.get()
        val table = ref.table
            ?: return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val tableRef = table.tableRef.get()
        val columnMeta = tableRef.columns[header]
            ?: return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val values = tableRef.columnCells[header] ?: throw InvalidColumnException("Unable to find column cells for header $header")
        val columnIterator = values.asSequence().map { it.component2().toCell(BaseColumn(table, header, columnMeta.columnOrder), it.component1()) }.iterator()

        return object : Iterator<DerivedCellView> {
            override fun hasNext() = columnIterator.hasNext()
            override fun next(): DerivedCellView {
                val cell = columnIterator.next()
                val columnView = ColumnView(this@ColumnView.tableView, cell.column.header)
                return createDerivedCellViewFromRef(ref, columnView, cell.index)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColumnView

        if (tableView != other.tableView) return false
        if (header != other.header) return false

        return true
    }

    override fun hashCode() = header.hashCode()

    override fun toString() = "ColumnView[${header.labels.joinToString(limit = 30)}]"
}

internal fun createDerivedColumnView(columnView: ColumnView): DerivedColumnView {
    val ref = columnView.tableView.tableViewRef.get()
    val defaultCellViewMeta = ref.defaultCellView
    val columnViewMeta = ref.columnViews[columnView.header]

    val width = columnViewMeta?.cellWidth
        ?: defaultCellViewMeta.cellWidth
        ?: DEFAULT_CELL_WIDTH

    val classes = (columnViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET)

    val topics = (columnViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET)

    return DerivedColumnView(columnView, width, classes, topics)
}

class DerivedColumnView internal constructor(
    val columnView: ColumnView,
    val cellWidth: Long,
    classes: PSet<String>,
    topics: PSet<String>
) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = columnView.tableView

    val header: Header
        get() = columnView.header

    val cellClasses: CellClasses<DerivedColumnView> by lazy {
        CellClasses(this, classes)
    }

    val cellTopics: CellTopics<DerivedColumnView> by lazy {
        CellTopics(this, topics)
    }

    override fun iterator() = columnView.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DerivedColumnView

        if (columnView != other.columnView) return false

        return true
    }

    override fun hashCode() = columnView.hashCode()

    override fun toString() = "DerivedColumnView[${columnView.header.labels.joinToString(limit = 30)}]"
}

class RowView internal constructor(
    val tableView: TableView,
    val index: Long
) : Iterable<DerivedCellView> {
    operator fun get(row: RowTransformer.Companion): RowTransformer<*> {
        val ref = tableView.tableViewRef.get()
        val function = ref.rowTransformers[index] ?: return UnitRowTransformer(this)
        return FunctionRowTransformer(this, function)
    }

    operator fun set(row: RowTransformer.Companion, rowTransformer: RowTransformer<*>?) {
        setRowTransformer(rowTransformer?.function as? (Row.() -> Unit)?)
    }

    operator fun set(row: RowTransformer.Companion, rowTransformer: Row.() -> Unit) {
        setRowTransformer(rowTransformer)
    }

    private fun setRowTransformer(transformer: (Row.() -> Unit)?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                it.copy(
                    rowTransformers = if (transformer == null) it.rowTransformers.remove(index) else it.rowTransformers.put(index, transformer),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index][RowTransformer]
            val new = newView[index][RowTransformer]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<RowTransformer<*>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellHeight: CellHeight.Companion): CellHeight<RowView, *> {
        val ref = tableView.tableViewRef.get()
        return when (val height = ref.rowViews[index]?.cellHeight) {
            is Long -> PixelCellHeight(this, height)
            else -> UnitCellHeight(this)
        }
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Long) {
        setCellHeight(height)
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Number) {
        when (height) {
            is Int -> setCellHeight(height.toLong())
            is Long -> setCellHeight(height)
            else -> throw InvalidCellHeightException("Unsupported type: ${height::class}")
        }
    }

    operator fun set(cellHeight: CellHeight.Companion, height: CellHeight<*, *>?) {
        setCellHeight(height?.asLong)
    }

    private fun setCellHeight(height: Long?) {
        synchronized(tableView.eventProcessor) {
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

            val old = oldView[index][CellHeight]
            val new = newView[index][CellHeight]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellHeight<RowView, *>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellClasses: CellClasses.Companion): CellClasses<RowView> {
        val ref = tableView.tableViewRef.get()
        return CellClasses(this, ref.rowViews[index]?.cellClasses ?: immutableSetOf())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: String) {
        setCellClasses(immutableSetOf(classes))
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: Collection<String>) {
        setCellClasses(classes.toImmutableSet())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: CellClasses<*>?) {
        setCellClasses(classes?._classes)
    }

    private fun setCellClasses(classes: PSet<String>?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.rowViews[index]
                val viewMeta = oldMeta?.copy(cellClasses = if (classes?.isEmpty() == true) null else classes) ?: ViewMeta(cellClasses = if (classes?.isEmpty() == true) null else classes)

                it.copy(
                    rowViews = it.rowViews.put(index, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index][CellClasses]
            val new = newView[index][CellClasses]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellClasses<RowView>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellTopics: CellTopics.Companion): CellTopics<RowView> {
        val ref = tableView.tableViewRef.get()
        return CellTopics(this, ref.rowViews[index]?.cellTopics ?: immutableSetOf())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: String) {
        setCellTopics(immutableSetOf(topics))
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: Collection<String>) {
        setCellTopics(topics.toImmutableSet())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: CellTopics<*>?) {
        setCellTopics(topics?._topics)
    }

    private fun setCellTopics(topics: PSet<String>?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.rowViews[index]
                val viewMeta = oldMeta?.copy(cellTopics = if (topics?.isEmpty() == true) null else topics) ?: ViewMeta(cellTopics = if (topics?.isEmpty() == true) null else topics)

                it.copy(
                    rowViews = it.rowViews.put(index, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index][CellTopics]
            val new = newView[index][CellTopics]

            tableView.eventProcessor.publish(listOf(TableViewListenerEvent<CellTopics<RowView>>(old, new)) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun invoke(function: RowView.() -> Any?): Any? {
        return when (val value = function()) {
            is RowView -> { tableView[this] = value; value }
            is CellHeight<*, *> -> { tableView[this][CellHeight] = value; value }
            is CellClasses<*> -> { tableView[this][CellClasses] = value; value }
            is CellTopics<*> -> { tableView[this][CellTopics] = value; value }
            is RowTransformer<*> -> { tableView[this][RowTransformer] = value; value }
            is Unit -> { /* no assignment */ Unit }
            is Function1<*, *> -> { invoke(value as RowView.() -> Any?) }
            null -> { tableView[this] = null; null }
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }
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

    operator fun get(vararg header: String): CellView = tableView[Header(*header), index]

    operator fun get(header: Header): CellView = tableView[header, index]

    operator fun get(columnView: ColumnView): CellView = tableView[columnView.header, index]

    operator fun get(column: Column): CellView = tableView[column.header, index]

    operator fun set(vararg header: String, view: CellView?) { tableView[Header(*header), index] = view }

    operator fun set(header: Header, view: CellView?) { tableView[header, index] = view }

    operator fun set(columnView: ColumnView, view: CellView?) { tableView[columnView.header, index] = view }

    operator fun set(column: Column, view: CellView?) { tableView[column.header, index] = view }

    operator fun set(vararg header: String, function: CellView.() -> Unit) { tableView[Header(*header), index].function() }

    operator fun set(header: Header, function: CellView.() -> Unit) { tableView[header, index].function() }

    operator fun set(columnView: ColumnView, function: CellView.() -> Unit) { tableView[columnView.header, index].function() }

    operator fun set(column: Column, function: CellView.() -> Unit) { tableView[column.header, index].function() }

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
                val columnView = ColumnView(this@RowView.tableView, cell.column.header)
                return createDerivedCellViewFromRef(ref, columnView, cell.index)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RowView

        if (tableView != other.tableView) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int = index.hashCode()

    override fun toString(): String = "RowView[$index]"
}

internal fun createDerivedRowView(rowView: RowView): DerivedRowView {
    val ref = rowView.tableView.tableViewRef.get()
    val defaultCellViewMeta = ref.defaultCellView
    val rowViewMeta = ref.rowViews[rowView.index]

    val height = rowViewMeta?.cellHeight
        ?: defaultCellViewMeta.cellHeight
        ?: DEFAULT_CELL_HEIGHT

    val classes = (rowViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET)

    val topics = (rowViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET)

    return DerivedRowView(rowView, height, classes, topics)
}

class DerivedRowView internal constructor(
    val rowView: RowView,
    val cellHeight: Long,
    classes: PSet<String>,
    topics: PSet<String>
) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = rowView.tableView

    val index: Long
        get() = rowView.index

    val cellClasses: CellClasses<DerivedRowView> by lazy {
        CellClasses(this, classes)
    }

    val cellTopics: CellTopics<DerivedRowView> by lazy {
        CellTopics(this, topics)
    }

    override fun iterator() = rowView.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DerivedRowView

        if (rowView != other.rowView) return false

        return true
    }

    override fun hashCode() = rowView.hashCode()

    override fun toString() = "DerivedRowView[${rowView.index}]"
}

sealed class CellHeight<S, T> {
    abstract val source: S
    abstract val height: T

    open val isNumeric: Boolean = false
    open val asLong: Long? = null

    operator fun plus(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> plus(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun plus(that: Number): Number {
        return when (that) {
            is Int -> plus(that)
            is Long -> plus(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun plus(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun plus(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun minus(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> minus(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun minus(that: Number): Number {
        return when (that) {
            is Int -> minus(that)
            is Long -> minus(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun minus(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun minus(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun times(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> times(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun times(that: Number): Number {
        return when (that) {
            is Int -> minus(that)
            is Long -> minus(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun times(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun times(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun div(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> div(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun div(that: Number): Number {
        return when (that) {
            is Int -> div(that)
            is Long -> div(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun div(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun div(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun rem(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> rem(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun rem(that: Number): Number {
        return when (that) {
            is Int -> rem(that)
            is Long -> rem(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun rem(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun rem(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun invoke(function: CellHeight<*,*>.() -> Any?): Any? {
        val value = this.function()
        val longValue = when(value) {
            is Unit -> /* no assignment */ return Unit
            is Int -> value.toLong()
            is Long -> value
            is CellHeight<*,*> -> when (val height = value.height) {
                is Number -> height.toLong()
                else -> null
            }
            null -> null
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }

        when (val source = source) {
            is TableView -> if (longValue == null) source[CellHeight] = null else source[CellHeight] = longValue
            is RowView -> if (longValue == null) source[CellHeight] = null else source[CellHeight] = longValue
            is CellView -> if (longValue == null) source[CellHeight] = null else source[CellHeight] = longValue
        }

        return value
    }

    operator fun contains(other: CellHeight<*, *>) = height == other.height
    operator fun contains(other: Int) = (height as? Long) == other.toLong()
    operator fun contains(other: Long) = (height as? Long) == other
    operator fun contains(other: Unit?) = height is Unit

    override fun hashCode() = Objects.hash(this.height)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellHeight<*, *>

        if (source != other.source) return false
        if (height != other.height) return false

        return true
    }

    companion object
}

class UnitCellHeight<S> internal constructor(
    override val source: S
) : CellHeight<S, Unit>() {
    override val height = Unit

    override fun toString() = "UnitCellHeight"
}

class PixelCellHeight<S> internal constructor(
    override val source: S,
    override val height: Long
) : CellHeight<S, Long>() {
    override val isNumeric = true

    override val asLong = height

    override fun plus(that: Int) = height + that

    override fun plus(that: Long) = height + that

    override fun minus(that: Int) = height - that

    override fun minus(that: Long) = height - that

    override fun times(that: Int) = height * that

    override fun times(that: Long) = height * that

    override fun div(that: Int) = height / that

    override fun div(that: Long) = height / that

    override fun rem(that: Int) = height % that

    override fun rem(that: Long) = height % that

    override fun toString() = "PixelCellHeight[$height]"
}

sealed class CellWidth<S, T> {
    abstract val source: S
    abstract val width: T

    open val isNumeric: Boolean = false
    open val asLong: Long? = null

    operator fun plus(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> plus(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun plus(that: Number): Number {
        return when (that) {
            is Int -> plus(that)
            is Long -> plus(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun plus(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun plus(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun minus(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> minus(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun minus(that: Number): Number {
        return when (that) {
            is Int -> minus(that)
            is Long -> minus(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun minus(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun minus(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun times(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> times(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun times(that: Number): Number {
        return when (that) {
            is Int -> minus(that)
            is Long -> minus(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun times(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun times(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun div(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> div(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun div(that: Number): Number {
        return when (that) {
            is Int -> div(that)
            is Long -> div(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun div(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun div(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun rem(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> rem(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun rem(that: Number): Number {
        return when (that) {
            is Int -> rem(that)
            is Long -> rem(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun rem(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun rem(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun invoke(function: CellWidth<*,*>.() -> Any?): Any? {
        val value = this.function()
        val longValue = when(value) {
            is Unit -> /* no assignment */ return Unit
            is Int -> value.toLong()
            is Long -> value
            is CellWidth<*,*> -> when (val width = value.width) {
                is Number -> width.toLong()
                else -> null
            }
            null -> null
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }

        when (val source = source) {
            is TableView -> if (longValue == null) source[CellWidth] = null else source[CellWidth] = longValue
            is ColumnView -> if (longValue == null) source[CellWidth] = null else source[CellWidth] = longValue
            is CellView -> if (longValue == null) source[CellWidth] = null else source[CellWidth] = longValue
        }

        return value
    }

    operator fun contains(other: CellWidth<*, *>) = width == other.width
    operator fun contains(other: Int) = (width as? Long) == other.toLong()
    operator fun contains(other: Long) = (width as? Long) == other
    operator fun contains(other: Unit?) = width is Unit

    override fun hashCode() = Objects.hash(this.width)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellWidth<*, *>

        if (source != other.source) return false
        if (width != other.width) return false

        return true
    }

    companion object
}

class UnitCellWidth<S> internal constructor(
    override val source: S
) : CellWidth<S, Unit>() {
    override val width = Unit

    override fun toString() = "UnitCellWidth"
}

class PixelCellWidth<S> internal constructor(
    override val source: S,
    override val width: Long
) : CellWidth<S, Long>() {
    override val isNumeric = true

    override val asLong = width

    override fun plus(that: Int) = width + that

    override fun plus(that: Long) = width + that

    override fun minus(that: Int) = width - that

    override fun minus(that: Long) = width - that

    override fun times(that: Int) = width * that

    override fun times(that: Long) = width * that

    override fun div(that: Int) = width / that

    override fun div(that: Long) = width / that

    override fun rem(that: Int) = width % that

    override fun rem(that: Long) = width % that

    override fun toString() = "PixelCellWidth[$width]"
}

class CellClasses<S> internal constructor(
    val source: S,
    internal val _classes: PSet<String>
) : Iterable<String> {
    val classes: SortedSet<String> by lazy {
        _classes.toSortedSet()
    }

    operator fun plus(topic: String): SortedSet<String> = (_classes + topic).toSortedSet()
    operator fun plus(topics: Collection<String>): SortedSet<String> = (topics.fold(this._classes) { acc, topic -> acc + topic }).toSortedSet()
    operator fun minus(topic: String): SortedSet<String> = (_classes - topic).toSortedSet()
    operator fun minus(topics: Collection<String>): SortedSet<String> = (topics.fold(this._classes) { acc, topic -> acc - topic }).toSortedSet()
    override fun iterator(): Iterator<String> = classes.iterator()

    operator fun invoke(function: CellClasses<*>.() -> Any?): Any? {
        val value = this.function()
        val classes = when(value) {
            is Unit -> /* no assignment */ return Unit
            is String -> setOf(value)
            is Collection<*> -> value as Collection<String>
            is CellClasses<*> -> value.classes
            null -> null
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }

        when (val source = source) {
            is TableView -> if (classes == null) source[CellClasses] = null else source[CellClasses] = classes
            is ColumnView -> if (classes == null) source[CellClasses] = null else source[CellClasses] = classes
            is RowView -> if (classes == null) source[CellClasses] = null else source[CellClasses] = classes
            is CellView -> if (classes == null) source[CellClasses] = null else source[CellClasses] = classes
        }

        return value
    }

    operator fun contains(other: CellClasses<*>) = _classes.containsAll(other._classes)
    operator fun contains(other: String) = _classes.contains(other)
    operator fun contains(other: Collection<*>) = _classes.containsAll(other)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellClasses<*>

        if (source != other.source) return false
        if (_classes != other._classes) return false

        return true
    }

    override fun hashCode() = Objects.hash(this._classes)

    override fun toString() = "CellClasses[${classes.joinToString(limit = 30)}]"

    companion object
}

class CellTopics<S> internal constructor(
    val source: S,
    internal val _topics: PSet<String>
) : Iterable<String> {
    val topics: SortedSet<String> by lazy {
        _topics.toSortedSet()
    }

    operator fun plus(topic: String): SortedSet<String> = (_topics + topic).toSortedSet()
    operator fun plus(topics: Collection<String>): SortedSet<String> = (topics.fold(this._topics) { acc, topic -> acc + topic }).toSortedSet()
    operator fun minus(topic: String): SortedSet<String> = (_topics - topic).toSortedSet()
    operator fun minus(topics: Collection<String>): SortedSet<String> = (topics.fold(this._topics) { acc, topic -> acc - topic }).toSortedSet()
    override fun iterator(): Iterator<String> = topics.iterator()

    operator fun invoke(function: CellTopics<*>.() -> Any?): Any? {
        val value = this.function()
        val topics = when(value) {
            is Unit -> /* no assignment */ return Unit
            is String -> setOf(value)
            is Collection<*> -> value as Collection<String>
            is CellTopics<*> -> value.topics
            null -> null
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }

        when (val source = source) {
            is TableView -> if (topics == null) source[CellTopics] = null else source[CellTopics] = topics
            is ColumnView -> if (topics == null) source[CellTopics] = null else source[CellTopics] = topics
            is RowView -> if (topics == null) source[CellTopics] = null else source[CellTopics] = topics
            is CellView -> if (topics == null) source[CellTopics] = null else source[CellTopics] = topics
        }

        return value
    }

    operator fun contains(other: CellTopics<*>) = _topics.containsAll(other._topics)
    operator fun contains(other: String) = _topics.contains(other)
    operator fun contains(other: Collection<*>) = _topics.containsAll(other)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellTopics<*>

        if (source != other.source) return false
        if (_topics != other._topics) return false

        return true
    }

    override fun hashCode() = Objects.hash(this._topics)

    override fun toString() = "CellTopics[${topics.joinToString(limit = 30)}]"

    companion object
}

sealed class Transformer<S, T>(val source: S, val function: T)

abstract class TableTransformer<T>(source: TableView, function: T): Transformer<TableView, T>(source, function) {
    operator fun invoke(function: TableTransformer<*>.() -> Any?): Any? {
        val value = this.function()
        val transformer = when(value) {
            is FunctionTableTransformer -> value.function
            is UnitTableTransformer -> null
            is Unit -> { /* no assignment */ return Unit }
            is Function1<*, *> -> value as Table.() -> Unit
            null -> null
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }

        if (transformer == null) source[TableTransformer] = null else source[TableTransformer] = transformer

        return value
    }

    operator fun contains(other: TableTransformer<*>) = function == other.function
    operator fun contains(other: Table.() -> Unit) = function == other
    operator fun contains(other: Unit?) = function is Unit

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

abstract class ColumnTransformer<T>(source: ColumnView, function: T): Transformer<ColumnView, T>(source, function) {
    operator fun invoke(function: ColumnTransformer<*>.() -> Any?): Any? {
        val value = this.function()
        val transformer = when(value) {
            is FunctionColumnTransformer -> value.function
            is UnitColumnTransformer -> null
            is Unit -> { /* no assignment */ return Unit }
            is Function1<*, *> -> value as Column.() -> Unit
            null -> null
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }

        if (transformer == null) source[ColumnTransformer] = null else source[ColumnTransformer] = transformer

        return value
    }

    operator fun contains(other: ColumnTransformer<*>) = function == other.function
    operator fun contains(other: Column.() -> Unit) = function == other
    operator fun contains(other: Unit?) = function is Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColumnTransformer<*>

        if (source != other.source) return false
        if (function != other.function) return false

        return true
    }

    override fun hashCode() = Objects.hashCode(this.function)

    companion object
}

class UnitColumnTransformer internal constructor(
    source: ColumnView
): ColumnTransformer<Unit>(source, Unit) {
    override fun toString() = "UnitColumnTransformer"
}

class FunctionColumnTransformer internal constructor(
    source: ColumnView,
    function: Column.() -> Unit
): ColumnTransformer<Column.() -> Unit>(source, function) {
    override fun toString() = "FunctionColumnTransformer[$function]"
}

abstract class RowTransformer<T>(source: RowView, function: T): Transformer<RowView, T>(source, function) {
    operator fun invoke(function: RowTransformer<*>.() -> Any?): Any? {
        val value = this.function()
        val transformer = when(value) {
            is FunctionRowTransformer -> value.function
            is UnitRowTransformer -> null
            is Unit -> { /* no assignment */ return Unit }
            is Function1<*, *> -> value as Row.() -> Unit
            null -> null
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }

        if (transformer == null) source[RowTransformer] = null else source[RowTransformer] = transformer

        return value
    }

    operator fun contains(other: RowTransformer<*>) = function == other.function
    operator fun contains(other: Row.() -> Unit) = function == other
    operator fun contains(other: Unit?) = function is Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RowTransformer<*>

        if (source != other.source) return false
        if (function != other.function) return false

        return true
    }

    override fun hashCode() = Objects.hashCode(this.function)

    companion object
}

class UnitRowTransformer internal constructor(
    source: RowView
): RowTransformer<Unit>(source, Unit) {
    override fun toString() = "UnitRowTransformer"
}

class FunctionRowTransformer internal constructor(
    source: RowView,
    function: Row.() -> Unit
): RowTransformer<Row.() -> Unit>(source, function) {
    override fun toString() = "FunctionRowTransformer[$function]"
}

abstract class CellTransformer<T>(source: CellView, function: T): Transformer<CellView, T>(source, function) {
    operator fun invoke(function: CellTransformer<*>.() -> Any?): Any? {
        val value = this.function()
        val transformer = when(value) {
            is FunctionCellTransformer -> value.function
            is UnitCellTransformer -> null
            is Unit -> { /* no assignment */ return Unit }
            is Function1<*, *> -> value as Cell<*>.() -> Unit
            null -> null
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }

        if (transformer == null) source[CellTransformer] = null else source[CellTransformer] = transformer

        return value
    }

    operator fun contains(other: CellTransformer<*>) = function == other.function
    operator fun contains(other: Cell<*>.() -> Unit) = function == other
    operator fun contains(other: Unit?) = function is Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellTransformer<*>

        if (source != other.source) return false
        if (function != other.function) return false

        return true
    }

    override fun hashCode() = Objects.hashCode(this.function)

    companion object
}

class UnitCellTransformer internal constructor(
    source: CellView
): CellTransformer<Unit>(source, Unit) {
    override fun toString() = "UnitCellTransformer"
}

class FunctionCellTransformer internal constructor(
    source: CellView,
    function: Cell<*>.() -> Unit
): CellTransformer<Cell<*>.() -> Unit>(source, function) {
    override fun toString() = "FunctionCellTransformer[$function]"
}

// TODO Should introduce a generic type S like else where..?
class Resources internal constructor(
    val source: TableView,
    internal val _resources: PMap<String, Pair<Long, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>
): Iterable<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>> {
    val resources: Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> by lazy {
        _resources.sortedBy { it.component2().first }.associateTo(LinkedHashMap()) { it.component1() to it.component2().second }
    }

    operator fun plus(resource: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>): Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> =
        _resources
            .put(resource.first, RESOURCE_COUNTER.getAndIncrement() to resource.second)
            .sortedBy { it.component2().first }
            .associateTo(LinkedHashMap()) { it.component1() to it.component2().second }

    operator fun plus(resources: Collection<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>): Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> =
        resources
            .fold(_resources) { acc, resource ->
                acc.put(
                    resource.first,
                    RESOURCE_COUNTER.getAndIncrement() to resource.second
                )
            }
            .sortedBy { it.component2().first }
            .associateTo(LinkedHashMap()) { it.component1() to it.component2().second }

    operator fun minus(resource: String): Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> =
        _resources
            .remove(resource)
            .sortedBy { it.component2().first }
            .associateTo(LinkedHashMap()) { it.component1() to it.component2().second }

    operator fun minus(resource: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>): Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> =
        (if (_resources[resource.first]?.second == resource.second) _resources.remove(resource.first) else _resources)
            .sortedBy { it.component2().first }
            .associateTo(LinkedHashMap()) { it.component1() to it.component2().second }

    operator fun minus(resources: Collection<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>): Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> =
        resources.fold(_resources) {acc, resource -> if (acc[resource.first]?.second == resource.second) acc.remove(resource.first) else acc}
            .sortedBy { it.component2().first }
            .associateTo(LinkedHashMap()) { it.component1() to it.component2().second }

    override fun iterator(): Iterator<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>> = resources.map { Pair(it.key, it.value) }.iterator()

    operator fun invoke(function: Resources.() -> Any?): Any? {
        val value = this.function()
        val resources = when(value) {
            is Unit -> /* no assignment */ return Unit
            is Map<*, *> -> value as Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>
            is Pair<*, *> -> mapOf(value as Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>)
            is Collection<*> -> (value as Collection<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>).toMap()
            is Resources -> value.resources
            null -> null
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }

        if (resources == null) source[Resources] = null else source[Resources] = resources

        return value
    }

    operator fun contains(other: Resources) = other._resources.all { _resources[it.component1()]?.second == it.component2().second }
    operator fun contains(other: Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>) = other.entries.all { _resources[it.key]?.second == it.value }
    operator fun contains(other: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>) = _resources[other.first]?.second == other.second
    operator fun contains(other: Collection<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>) = other.all { _resources[it.first]?.second == it.second }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Resources

        if (source != other.source) return false
        if (_resources != other._resources) return false

        return true
    }

    override fun hashCode() = Objects.hash(this.resources)

    override fun toString() = "Resources[${resources.keys.joinToString(limit = 30)}]"
    companion object {
        internal val RESOURCE_COUNTER = AtomicLong()
    }
}

// TODO Should introduce a generic type S like else where..
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

// TODO Consider an Index type which allows us to replace the index.html file served for a table,
//      or let that happen straight on tableView[Resources] ?
// TODO Add plusAssign and minusAssign to the companion object on Resources to allow for root resources ?
