package sigbla.app

import sigbla.app.internals.SigblaBackend
import sigbla.app.internals.load1
import sigbla.app.internals.refAction
import sigbla.app.internals.save1
import java.io.File
import kotlin.reflect.KClass

fun clear(columnView: ColumnView) {
    synchronized(columnView.tableView.eventProcessor) {
        val tableViewRef = columnView.tableView.tableViewRef
        val columnHeader = columnView.columnHeader
        val eventProcessor = columnView.tableView.eventProcessor

        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                columnViews = it.columnViews.remove(columnHeader),
                version = it.version + 1L
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = columnView.tableView.makeClone(ref = oldRef)
        val newView = columnView.tableView.makeClone(ref = newRef)

        val old = oldView[columnView]
        val new = newView[columnView]

        eventProcessor.publish(listOf(TableViewListenerEvent<ColumnView>(old, new)) as List<TableViewListenerEvent<Any>>)
    }
}

fun clear(rowView: RowView) {
    synchronized(rowView.tableView.eventProcessor) {
        val tableViewRef = rowView.tableView.tableViewRef
        val eventProcessor = rowView.tableView.eventProcessor

        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                rowViews = it.rowViews.remove(rowView.index),
                version = it.version + 1L
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = rowView.tableView.makeClone(ref = oldRef)
        val newView = rowView.tableView.makeClone(ref = newRef)

        val old = oldView[rowView]
        val new = newView[rowView]

        eventProcessor.publish(listOf(TableViewListenerEvent<RowView>(old, new)) as List<TableViewListenerEvent<Any>>)
    }
}

fun clear(cellView: CellView) {
    synchronized(cellView.tableView.eventProcessor) {
        val tableViewRef = cellView.tableView.tableViewRef
        val columnHeader = cellView.columnView.columnHeader
        val index = cellView.index
        val eventProcessor = cellView.tableView.eventProcessor

        val (oldRef, newRef) = tableViewRef.refAction {
            it.copy(
                cellViews = it.cellViews.remove(Pair(columnHeader, index)),
                version = it.version + 1L
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = cellView.tableView.makeClone(ref = oldRef)
        val newView = cellView.tableView.makeClone(ref = newRef)

        val old = oldView[cellView]
        val new = newView[cellView]

        eventProcessor.publish(listOf(TableViewListenerEvent<CellView>(old, new)) as List<TableViewListenerEvent<Any>>)
    }
}

// ---

fun clone(tableView: TableView): TableView = tableView.makeClone()

fun clone(tableView: TableView, name: String): TableView = tableView.makeClone(name, true)

// TODO Add a fun show(table: Table) that would automatically create a view or reuse existing view on same name
fun show(tableView: TableView) = SigblaBackend.openView(tableView)

// ---

interface OnTableView<T> {
    infix fun events(process: Sequence<TableViewListenerEvent<out T>>.() -> Unit): TableViewListenerReference
}

inline fun <reified T : Any> on(
    tableView: TableView,
    type: KClass<T> = T::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<T> {
    override fun events(process: Sequence<TableViewListenerEvent<out T>>.() -> Unit): TableViewListenerReference {
        return on(
            tableView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableViewListenerEvent<out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    tableView: TableView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<Any> {
    override fun events(process: Sequence<TableViewListenerEvent<out Any>>.() -> Unit): TableViewListenerReference {
        return on(
            tableView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            events {
                process(this)
            }
        }
    }
}

inline fun <reified T> on(
    tableView: TableView,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableViewEventReceiver<TableView, T>.() -> Unit
): TableViewListenerReference {
    return on(
        tableView,
        T::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableViewEventReceiver<TableView, Any>.() -> Unit
    )
}

fun on(
    tableView: TableView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableViewEventReceiver<TableView, Any>.() -> Unit
): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<TableView, Any>(
            tableView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        else -> TableViewEventReceiver(
            tableView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return tableView.eventProcessor.subscribe(tableView, eventReceiver, init)
}

// ---

inline fun <reified T : Any> on(
    columnView: ColumnView,
    type: KClass<T> = T::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<T> {
    override fun events(process: Sequence<TableViewListenerEvent<out T>>.() -> Unit): TableViewListenerReference {
        return on(
            columnView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableViewListenerEvent<out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    columnView: ColumnView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<Any> {
    override fun events(process: Sequence<TableViewListenerEvent<out Any>>.() -> Unit): TableViewListenerReference {
        return on(
            columnView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            events {
                process(this)
            }
        }
    }
}

inline fun <reified T> on(
    columnView: ColumnView,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableViewEventReceiver<ColumnView, T>.() -> Unit
): TableViewListenerReference {
    return on(
        columnView,
        T::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableViewEventReceiver<ColumnView, Any>.() -> Unit
    )
}

fun on(
    columnView: ColumnView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableViewEventReceiver<ColumnView, Any>.() -> Unit
): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<ColumnView, Any>(
            columnView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        else -> TableViewEventReceiver(
            columnView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return columnView.tableView.eventProcessor.subscribe(columnView, eventReceiver, init)
}

// ---

inline fun <reified T : Any> on(
    rowView: RowView,
    type: KClass<T> = T::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<T> {
    override fun events(process: Sequence<TableViewListenerEvent<out T>>.() -> Unit): TableViewListenerReference {
        return on(
            rowView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableViewListenerEvent<out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    rowView: RowView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<Any> {
    override fun events(process: Sequence<TableViewListenerEvent<out Any>>.() -> Unit): TableViewListenerReference {
        return on(
            rowView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            events {
                process(this)
            }
        }
    }
}

inline fun <reified T> on(
    rowView: RowView,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableViewEventReceiver<RowView, T>.() -> Unit
): TableViewListenerReference {
    return on(
        rowView,
        T::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableViewEventReceiver<RowView, Any>.() -> Unit
    )
}

fun on(
    rowView: RowView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableViewEventReceiver<RowView, Any>.() -> Unit
): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<RowView, Any>(
            rowView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        else -> TableViewEventReceiver(
            rowView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return rowView.tableView.eventProcessor.subscribe(rowView, eventReceiver, init)
}

// ---

// TODO CellRange on functions

// ---

inline fun <reified T : Any> on(
    cellView: CellView,
    type: KClass<T> = T::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<T> {
    override fun events(process: Sequence<TableViewListenerEvent<out T>>.() -> Unit): TableViewListenerReference {
        return on(
            cellView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableViewListenerEvent<out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    cellView: CellView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<Any> {
    override fun events(process: Sequence<TableViewListenerEvent<out Any>>.() -> Unit): TableViewListenerReference {
        return on(
            cellView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            events {
                process(this)
            }
        }
    }
}

inline fun <reified T> on(
    cellView: CellView,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableViewEventReceiver<CellView, T>.() -> Unit
): TableViewListenerReference {
    return on(
        cellView,
        T::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableViewEventReceiver<CellView, Any>.() -> Unit
    )
}

fun on(
    cellView: CellView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableViewEventReceiver<CellView, Any>.() -> Unit
): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<CellView, Any>(
            cellView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        else -> TableViewEventReceiver(
            cellView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return cellView.tableView.eventProcessor.subscribe(cellView, eventReceiver, init)
}

// ---

inline fun <reified T : Any> on(
    derivedCellView: DerivedCellView,
    type: KClass<T> = T::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<T> {
    override fun events(process: Sequence<TableViewListenerEvent<out T>>.() -> Unit): TableViewListenerReference {
        return on(
            derivedCellView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableViewListenerEvent<out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    derivedCellView: DerivedCellView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTableView<Any> {
    override fun events(process: Sequence<TableViewListenerEvent<out Any>>.() -> Unit): TableViewListenerReference {
        return on(
            derivedCellView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            events {
                process(this)
            }
        }
    }
}

inline fun <reified T> on(
    derivedCellView: DerivedCellView,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableViewEventReceiver<DerivedCellView, T>.() -> Unit
): TableViewListenerReference {
    return on(
        derivedCellView,
        T::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableViewEventReceiver<DerivedCellView, Any>.() -> Unit
    )
}

fun on(
    derivedCellView: DerivedCellView,
    type: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableViewEventReceiver<DerivedCellView, Any>.() -> Unit
): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<DerivedCellView, Any>(
            derivedCellView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        else -> TableViewEventReceiver(
            derivedCellView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return derivedCellView.tableView.eventProcessor.subscribe(derivedCellView, eventReceiver, init)
}

// ---

fun off(reference: TableViewListenerReference) = reference.unsubscribe()

fun off(tableViewEventReceiver: TableViewEventReceiver<*, *>) = off(tableViewEventReceiver.reference)

// ---

// TODO Allow File to be a String

// TODO Allow for just save(table), taking name from table, same for load

fun load(
    resources: Pair<File, TableView>,
    extension: String = "sigv",
) = load1(resources, extension)

fun save(
    resources: Pair<TableView, File>,
    extension: String = "sigv",
    compress: Boolean = true
) = save1(resources, extension, compress)
