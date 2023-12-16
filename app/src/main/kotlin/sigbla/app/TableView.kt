/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableViewException
import sigbla.app.exceptions.InvalidCellHeightException
import sigbla.app.exceptions.InvalidCellWidthException
import sigbla.app.exceptions.InvalidValueException
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
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import sigbla.app.internals.RefHolder
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
    val columnViews: PMap<ColumnHeader, ViewMeta> = PHashMap(),
    val rowViews: PMap<Long, ViewMeta> = PHashMap(),
    val cellViews: PMap<Pair<ColumnHeader, Long>, ViewMeta> = PHashMap(),

    val resources: PMap<String, Pair<Long, suspend PipelineContext<*, ApplicationCall>.() -> Unit>> = PHashMap(),
    val cellTransformers: PMap<Pair<ColumnHeader, Long>, Cell<*>.() -> Any?> = PHashMap(),
    val table: Table? = null,

    val version: Long = Long.MIN_VALUE,
)

// TODO Consider if we need a DerivedTableView as well?
class TableView internal constructor(
    val name: String?,
    onRegistry: Boolean = true,
    internal val tableViewRef: RefHolder<TableViewRef>,
    internal val eventProcessor: TableViewEventProcessor = TableViewEventProcessor()
) : Iterable<DerivedCellView> {
    internal constructor(name: String?, table: Table?) : this(name, tableViewRef = RefHolder(TableViewRef(table = table)))
    internal constructor(table: Table) : this(table.name, table)
    internal constructor(name: String?) : this(name, if (name == null) null else Registry.getTable(name))

    init {
        if (name != null && onRegistry) Registry.setView(name, this)
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

    operator fun get(table: Table.Companion): Table {
        val ref = tableViewRef.get()
        val table = ref.table?.let { it.makeClone() } ?: BaseTable(name = null, source = null, onRegistry = false)

        // TODO Look into making this lazy? Or at least cache it
        ref.cellTransformers.forEach {
            val key = it.component1()
            val init = it.component2()
            table[key.first][key.second] = init
        }

        return table
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
        setCellHeight(height?.toLong())
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
        setCellWidth(width?.toLong())
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

    operator fun get(cell: Cell<*>): CellView = this[cell.column.header][cell.index]

    operator fun get(cellView: CellView) = this[cellView.columnView][cellView.index]

    operator fun get(derivedCellView: DerivedCellView) = this[derivedCellView.columnView][derivedCellView.index].derived

    // -----

    operator fun get(column: Column): ColumnView = this[column.header]

    operator fun get(columnView: ColumnView): ColumnView = this[columnView.header]

    // -----

    operator fun get(row: Row) = this[row.index]

    operator fun get(rowView: RowView) = this[rowView.index]

    // -----

    operator fun get(index: Long, column: Column): CellView = this[column, index]
    operator fun get(index: Long, columnHeader: ColumnHeader): CellView = this[columnHeader, index]

    // -----

    operator fun get(column: Column, index: Long): CellView = this[column.header, index]

    operator fun get(columnView: ColumnView, index: Long): CellView = this[columnView.header, index]

    operator fun get(columnHeader: ColumnHeader, index: Long): CellView = CellView(get(columnHeader), index)

    // -----

    operator fun set(cell: Cell<*>, view: CellView?) {
        this[cell.column][cell.index] = view
    }

    operator fun set(cell: Cell<*>, function: CellView.() -> Any?) {
        this[cell.column][cell.index] { function() }
    }

    // -----

    operator fun set(cellView: CellView, view: CellView?) {
        this[cellView.columnView][cellView.index] = view
    }

    operator fun set(cellView: CellView, function: CellView.() -> Any?) {
        this[cellView.columnView][cellView.index] { function() }
    }

    // -----

    operator fun set(columnHeader: ColumnHeader, view: ColumnView?) {
        synchronized(eventProcessor) {
            val (oldRef, newRef) = tableViewRef.refAction {
                val viewMeta = if (view == null) null else view.tableView.tableViewRef.get().columnViews[view.header]

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

            val oldColumnView = oldView[columnHeader]
            val newColumnView = newView[columnHeader]

            eventProcessor.publish(listOf(
                TableViewListenerEvent(oldColumnView[CellClasses], newColumnView[CellClasses]),
                TableViewListenerEvent(oldColumnView[CellTopics], newColumnView[CellTopics]),
                TableViewListenerEvent(oldColumnView[CellWidth], newColumnView[CellWidth])
            ))
        }
    }

    operator fun set(column: Column, view: ColumnView?) = set(column.header, view)

    operator fun set(columnView: ColumnView, view: ColumnView?) = set(columnView.header, view)

    // -----

    operator fun set(row: Long, view: RowView?) {
        synchronized(eventProcessor) {
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

            val oldRowView = oldView[row]
            val newRowView = newView[row]


            eventProcessor.publish(listOf(
                TableViewListenerEvent(oldRowView[CellClasses], newRowView[CellClasses]),
                TableViewListenerEvent(oldRowView[CellHeight], newRowView[CellHeight]),
                TableViewListenerEvent(oldRowView[CellTopics], newRowView[CellTopics])
            ))
        }
    }

    operator fun set(rowView: RowView, view: RowView?) = set(rowView.index, view)

    operator fun set(row: Row, view: RowView?) = set(row.index, view)

    // -----

    operator fun set(columnHeader: ColumnHeader, index: Int, view: CellView?) {
        this[columnHeader, index.toLong()] = view
    }

    operator fun set(columnHeader: ColumnHeader, index: Long, view: CellView?) {
        synchronized(eventProcessor) {
            val cell = Pair(columnHeader, index)
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

            val oldCellView = oldView[columnHeader][index]
            val newCellView = newView[columnHeader][index]

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

    operator fun set(columnHeader: ColumnHeader, index: Int, function: CellView.() -> Any?) {
        this[columnHeader][index] { function() }
    }

    operator fun set(columnHeader: ColumnHeader, index: Long, function: CellView.() -> Any?) {
        this[columnHeader][index] { function() }
    }

    // -----

    operator fun set(vararg header: String, view: ColumnView?) {
        this[ColumnHeader(*header)] = view
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

    operator fun set(header1: String, index: Long, function: CellView.() -> Any?) {
        this[header1][index] { function() }
    }

    operator fun set(header1: String, header2: String, index: Long, function: CellView.() -> Any?) {
        this[header1, header2][index] { function() }
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, function: CellView.() -> Any?) {
        this[header1, header2, header3][index] { function() }
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, function: CellView.() -> Any?) {
        this[header1, header2, header3, header4][index] { function() }
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, function: CellView.() -> Any?) {
        this[header1, header2, header3, header4, header5][index] { function() }
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

    operator fun set(header1: String, index: Int, function: CellView.() -> Any?) {
        this[header1][index] { function() }
    }

    operator fun set(header1: String, header2: String, index: Int, function: CellView.() -> Any?) {
        this[header1, header2][index] { function() }
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, function: CellView.() -> Any?) {
        this[header1, header2, header3][index] { function() }
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, function: CellView.() -> Any?) {
        this[header1, header2, header3, header4][index] { function() }
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, function: CellView.() -> Any?) {
        this[header1, header2, header3, header4, header5][index] { function() }
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

    operator fun <R> invoke(batch: TableView.() -> R): R {
        synchronized(eventProcessor) {
            if (eventProcessor.pauseEvents()) {
                try {
                    tableViewRef.useLocal()
                    val r = this.batch()
                    eventProcessor.publish(true)
                    tableViewRef.commitLocal()
                    return r
                } finally {
                    eventProcessor.clearBuffer()
                    tableViewRef.clearLocal()
                }
            } else {
                return this.batch()
            }
        }
    }

    internal fun makeClone(name: String? = this.name, onRegistry: Boolean = false, ref: TableViewRef = tableViewRef.get()) = TableView(name, onRegistry, RefHolder(ref))

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

        val names: SortedSet<String> get() = Registry.viewNames()

        fun delete(name: String) = Registry.deleteView(name)

        // TODO Consider a operator get/set(resources: Resources, ..) on this level as well to allow for global resources

        operator fun get(port: Port): Int = Port.port

        operator fun set(_port: Port, port: Int) {
            Port.port = port
        }
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
        setCellHeight(height?.toLong())
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
        setCellWidth(width?.toLong())
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
        setCellTransformer(if (cellTransformer == null) null else cellTransformer.function as (Cell<*>.() -> Any?)?)
    }

    operator fun set(cell: CellTransformer.Companion, cellTransformer: Cell<*>.() -> Any?) {
        setCellTransformer(cellTransformer)
    }

    private fun setCellTransformer(init: (Cell<*>.() -> Any?)?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)

                it.copy(
                    cellTransformers = if (init == null) it.cellTransformers.remove(key) else it.cellTransformers.put(key, init),
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

    operator fun <T> invoke(function: CellView.() -> T): T {
        return when (val value = function()) {
            is CellHeight<*, *> -> { tableView[this][CellHeight] = value; value }
            is CellWidth<*, *> -> { tableView[this][CellWidth] = value; value }
            is CellClasses<*> -> { tableView[this][CellClasses] = value; value }
            is CellTopics<*> -> { tableView[this][CellTopics] = value; value }
            is Unit -> { /* no assignment */ Unit as T }
            is Function1<*, *> -> { invoke(value as CellView.() -> T?) as T }
            // TODO CellTransformer?
            // TODO null?
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
        if (ref.table == null)
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
    // TODO Consider if this and other Derived.. instances should operate on clones
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
    // TODO: This should use a ref to ensure snapshot across multiple derived cell views
    val cell: Cell<*> = tableView[Table].let { it[columnView.header][index] }

    val cellClasses: CellClasses<DerivedCellView> by lazy {
        CellClasses(this, classes)
    }

    val cellTopics: CellTopics<DerivedCellView> by lazy {
        CellTopics(this, topics)
    }

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
    val header: ColumnHeader
) : Iterable<DerivedCellView> {
    init {
        // Best efforts to help columns appear in the order they are referenced,
        // but ultimately this is controlled by the underlying table.
        if (header !== emptyColumnHeader) tableView.tableViewRef.get().table?.get(header)
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
        setCellWidth(width?.toLong())
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

    // TODO Does this need to care about index relations?
    operator fun get(row: Row) = get(row.index)

    operator fun get(rowView: RowView) = get(rowView.index)

    operator fun set(index: Long, view: CellView?) { tableView[header, index] = view }

    operator fun set(index: Int, view: CellView?) { this[index.toLong()] = view }

    operator fun set(row: Row, view: CellView?) { this[row.index] = view }

    operator fun set(rowView: RowView, view: CellView?) { this[rowView.index] = view }

    operator fun set(index: Long, function: CellView.() -> Any?) { this[index] { function() } }

    operator fun set(index: Int, function: CellView.() -> Any?) { this[index] { function() } }

    operator fun set(row: Row, function: CellView.() -> Any?) { this[row.index] { function() } }

    operator fun set(rowView: RowView, function: CellView.() -> Any?) { this[rowView.index] { function() } }

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

        val values = tableRef.columnCells[header] ?: throw InvalidColumnException(header)
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

    val header: ColumnHeader
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
        setCellHeight(height?.toLong())
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

    operator fun get(columnView: ColumnView): CellView = tableView[columnView.header, index]

    operator fun get(column: Column): CellView = tableView[column.header, index]

    operator fun set(vararg header: String, view: CellView?) { tableView[ColumnHeader(*header), index] = view }

    operator fun set(columnHeader: ColumnHeader, view: CellView?) { tableView[columnHeader, index] = view }

    operator fun set(columnView: ColumnView, view: CellView?) { tableView[columnView.header, index] = view }

    operator fun set(column: Column, view: CellView?) { tableView[column.header, index] = view }

    operator fun set(vararg header: String, function: CellView.() -> Any?) { tableView[ColumnHeader(*header), index] { function() } }

    operator fun set(columnHeader: ColumnHeader, function: CellView.() -> Any?) { tableView[columnHeader, index] { function() } }

    operator fun set(columnView: ColumnView, function: CellView.() -> Any?) { tableView[columnView.header, index] { function() } }

    operator fun set(column: Column, function: CellView.() -> Any?) { tableView[column.header, index] { function() } }

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

    // TODO Make these match Cell
    open fun isNumeric(): Boolean = false
    open fun toLong(): Long? = null

    operator fun plus(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Int -> plus(that.toLong() ?: 0L)
            is Long -> plus(that.toLong() ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at $source")
        }
    }

    operator fun plus(that: Number): Number {
        return when (that) {
            is Int -> plus(that.toLong())
            is Long -> plus(that.toLong())
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun plus(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun plus(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun minus(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Int -> minus(that.toLong() ?: 0L)
            is Long -> minus(that.toLong() ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at $source")
        }
    }

    operator fun minus(that: Number): Number {
        return when (that) {
            is Int -> minus(that.toLong())
            is Long -> minus(that.toLong())
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun minus(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun minus(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun times(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Int -> times(that.toLong() ?: 0L)
            is Long -> times(that.toLong() ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at $source")
        }
    }

    operator fun times(that: Number): Number {
        return when (that) {
            is Int -> minus(that.toLong())
            is Long -> minus(that.toLong())
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun times(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun times(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun div(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Int -> div(that.toLong() ?: 0L)
            is Long -> div(that.toLong() ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at $source")
        }
    }

    operator fun div(that: Number): Number {
        return when (that) {
            is Int -> div(that.toLong())
            is Long -> div(that.toLong())
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun div(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun div(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun rem(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Int -> rem(that.toLong() ?: 0L)
            is Long -> rem(that.toLong() ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at $source")
        }
    }

    operator fun rem(that: Number): Number {
        return when (that) {
            is Int -> rem(that.toLong())
            is Long -> rem(that.toLong())
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun rem(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun rem(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun <T> invoke(function: CellHeight<*,*>.() -> T): T {
        val value = this.function()
        val longValue = when(value) {
            is Unit -> /* no assignment */ return Unit as T
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

    override fun equals(other: Any?): Boolean {
        val height = this.height

        return when {
            other is CellHeight<*, *> -> height == other.height
            other is Number -> if (height is Number) {
                when (val v = this - other) {
                    is Int -> v == 0
                    is Long -> v == 0L
                    else -> throw InvalidCellHeightException("Unsupported type: ${v::class}")
                }
            } else false
            else -> height == other
        }
    }

    override fun hashCode() = Objects.hash(this.height)

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
    override fun isNumeric() = true

    override fun toLong() = height

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

    // TODO Make these match Cell
    open fun isNumeric(): Boolean = false
    open fun toLong(): Long? = null

    operator fun plus(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Int -> plus(that.toLong() ?: 0L)
            is Long -> plus(that.toLong() ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at $source")
        }
    }

    operator fun plus(that: Number): Number {
        return when (that) {
            is Int -> plus(that.toLong())
            is Long -> plus(that.toLong())
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun plus(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun plus(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun minus(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Int -> minus(that.toLong() ?: 0L)
            is Long -> minus(that.toLong() ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at $source")
        }
    }

    operator fun minus(that: Number): Number {
        return when (that) {
            is Int -> minus(that.toLong())
            is Long -> minus(that.toLong())
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun minus(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun minus(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun times(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Int -> times(that.toLong() ?: 0L)
            is Long -> times(that.toLong() ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at $source")
        }
    }

    operator fun times(that: Number): Number {
        return when (that) {
            is Int -> minus(that.toLong())
            is Long -> minus(that.toLong())
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun times(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun times(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun div(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Int -> div(that.toLong() ?: 0L)
            is Long -> div(that.toLong() ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at $source")
        }
    }

    operator fun div(that: Number): Number {
        return when (that) {
            is Int -> div(that.toLong())
            is Long -> div(that.toLong())
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun div(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun div(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun rem(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Int -> rem(that.toLong() ?: 0L)
            is Long -> rem(that.toLong() ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at $source")
        }
    }

    operator fun rem(that: Number): Number {
        return when (that) {
            is Int -> rem(that.toLong())
            is Long -> rem(that.toLong())
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun rem(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun rem(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun <T> invoke(function: CellWidth<*,*>.() -> T): T {
        val value = this.function()
        val longValue = when(value) {
            is Unit -> /* no assignment */ return Unit as T
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

    // TODO Add functionality to make this symmetric?
    override fun equals(other: Any?): Boolean {
        val height = this.width

        return when (other) {
            is CellWidth<*, *> -> height == other.width
            is Number -> if (height is Number) {
                when (val v = this - other) {
                    is Int -> v == 0
                    is Long -> v == 0L
                    else -> throw InvalidCellWidthException("Unsupported type: ${v::class}")
                }
            } else false

            else -> height == other
        }
    }

    override fun hashCode() = Objects.hash(this.width)

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
    override fun isNumeric() = true

    override fun toLong() = width

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
    val classes: List<String> by lazy {
        _classes.sorted().toList()
    }

    operator fun plus(topic: String): CellClasses<S> = CellClasses(source, _classes + topic)
    operator fun plus(topics: Collection<String>): CellClasses<S> = CellClasses(source, topics.fold(this._classes) { acc, topic -> acc + topic })
    operator fun minus(topic: String): CellClasses<S> = CellClasses(source, _classes - topic)
    operator fun minus(topics: Collection<String>): CellClasses<S> = CellClasses(source, topics.fold(this._classes) { acc, topic -> acc - topic })
    override fun iterator(): Iterator<String> = classes.iterator()

    // TODO Change these and similar to be CellClasses<*>.() -> Any?
    operator fun <T> invoke(function: CellClasses<*>.() -> T): T {
        val value = this.function()
        val classes = when(value) {
            is Unit -> /* no assignment */ return Unit as T
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

    override fun equals(other: Any?): Boolean {
        // TODO Add functionality to make this symmetric, i.e. "some string" == some CellClasses containing just "some string" must return true, etc..
        return when (other) {
            is CellClasses<*> -> _classes == other._classes
            is String -> _classes.contains(other) && _classes.size == 1
            is Iterable<*> -> other.toSet().let { _classes.containsAll(it) && _classes.size == it.size }
            else -> _classes == other
        }
    }

    override fun hashCode() = Objects.hash(this._classes)

    override fun toString() = "CellClasses[${classes.joinToString(limit = 30)}]"

    companion object
}

class CellTopics<S> internal constructor(
    val source: S,
    internal val _topics: PSet<String>
) : Iterable<String> {
    val topics: List<String> by lazy {
        _topics.sorted().toList()
    }

    operator fun plus(topic: String): CellTopics<S> = CellTopics(source, _topics + topic)
    operator fun plus(topics: Collection<String>): CellTopics<S> = CellTopics(source, topics.fold(this._topics) { acc, topic -> acc + topic })
    operator fun minus(topic: String): CellTopics<S> = CellTopics(source, _topics - topic)
    operator fun minus(topics: Collection<String>): CellTopics<S> = CellTopics(source, topics.fold(this._topics) { acc, topic -> acc - topic })
    override fun iterator(): Iterator<String> = topics.iterator()

    operator fun <T> invoke(function: CellTopics<*>.() -> T): T {
        val value = this.function()
        val topics = when(value) {
            is Unit -> /* no assignment */ return Unit as T
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

    override fun equals(other: Any?): Boolean {
        // TODO Add functionality to make this symmetric, i.e. "some string" == some CellTopics containing just "some string" must return true, etc..
        return when (other) {
            is CellTopics<*> -> _topics == other._topics
            is String -> _topics.contains(other) && _topics.size == 1
            is Iterable<*> -> other.toSet().let { _topics.containsAll(it) && _topics.size == it.size }
            else -> _topics == other
        }
    }

    override fun hashCode() = Objects.hash(this._topics)

    override fun toString() = "CellTopics[${topics.joinToString(limit = 30)}]"

    companion object
}

// TODO Should introduce a generic type S like else where..
sealed class CellTransformer<T> {
    abstract val source: CellView
    abstract val function: T

    companion object
}

class UnitCellTransformer internal constructor(
    override val source: CellView
): CellTransformer<Unit>() {
    override val function = Unit

    // TODO Equals, hashCode, toString?
}

class FunctionCellTransformer internal constructor(
    override val source: CellView,
    override val function: Cell<*>.() -> Any?
): CellTransformer<Cell<*>.() -> Any?>() {
    // TODO Equals, hashCode, toString?
}

// TODO Should introduce a generic type S like else where..?
class Resources internal constructor(
    val source: TableView,
    internal val _resources: PMap<String, Pair<Long, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>
): Iterable<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>> {
    val resources: Map<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> by lazy {
        _resources.sortedBy { it.component2().first }.map { it.component1() to it.component2().second }.toMap(LinkedHashMap())
    }

    operator fun plus(resource: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>): Resources = Resources(source, _resources.put(resource.first, RESOURCE_COUNTER.getAndIncrement() to resource.second))
    operator fun plus(resources: Collection<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>): Resources = Resources(source, resources.fold(_resources) {acc, resource -> acc.put(resource.first, RESOURCE_COUNTER.getAndIncrement() to resource.second)})
    operator fun minus(resource: String): Resources = Resources(source, _resources.remove(resource))
    operator fun minus(resource: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>): Resources = this - resource.first
    operator fun minus(resources: Collection<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>>): Resources = Resources(source, resources.fold(_resources) {acc, resource -> acc.remove(resource.first)})
    override fun iterator(): Iterator<Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>> = resources.map { Pair(it.key, it.value) }.iterator()

    operator fun <T> invoke(function: Resources.() -> T): T {
        val value = this.function()
        val resources = when(value) {
            is Unit -> /* no assignment */ return Unit as T
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

    override fun equals(other: Any?): Boolean {
        // TODO Add functionality to make this symmetric, i.e. "some string" == some CellTopics containing just "some string" must return true, etc..
        return when (other) {
            is Resources -> _resources.all { other._resources[it.component1()]?.second == it.component2().second } && _resources.size() == other._resources.size()
            is Map<*,*> -> _resources.all { other[it.component1()] == it.component2() } && _resources.size() == other.size
            is Pair<*,*> -> other.first is String && _resources[other.first as String] == other.second && _resources.size() == 1
            is Iterable<*> -> {
                val otherList = other.toList()
                if (otherList.size != _resources.size()) return false
                val otherMap = otherList.filterIsInstance<Pair<*, *>>().fold(mutableMapOf<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit>()) {
                    acc, pair -> acc[pair.first as String] = pair.second as suspend PipelineContext<*, ApplicationCall>.() -> Unit; acc
                }
                if (otherMap.size != otherList.size) return false
                if (otherMap.size != _resources.size()) return false
                _resources.all { otherMap[it.component1()] == it.component2() }
            }
            else -> _resources == other
        }
    }

    override fun hashCode() = Objects.hash(this._resources.map { it.component1() to it.component2().second })

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
