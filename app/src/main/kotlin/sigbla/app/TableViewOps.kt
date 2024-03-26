/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import io.ktor.server.engine.*
import sigbla.app.exceptions.InvalidTableViewException
import sigbla.app.internals.Registry
import sigbla.app.internals.SigblaBackend
import sigbla.app.internals.load1
import sigbla.app.internals.save1
import java.io.File
import java.net.URL
import kotlin.reflect.KClass

// TODO Consider a reset(tableView) as well to remove all styling?

fun remove(tableView: TableView) = Registry.shutdownView(tableView, true)

// ---

fun clear(tableView: TableView): Unit { tableView() }

fun clear(columnView: ColumnView): Unit { columnView() }

fun clear(rowView: RowView): Unit { rowView() }

fun clear(cellView: CellView): Unit { cellView() }

// ---

fun clone(tableView: TableView): TableView = tableView.makeClone()

fun clone(tableView: TableView, name: String): TableView = tableView.makeClone(name, true)

fun show(
    tableView: TableView,
    ref: String = tableView.name ?: throw InvalidTableViewException("No name on table view"),
    urlGenerator: (engine: ApplicationEngine, view: TableView, ref: String) -> URL = { engine, _, ref ->
        val type = engine.environment.connectors.first().type.name
        val host = engine.environment.connectors.first().host
        val port = engine.environment.connectors.first().port
        URL("$type://$host:$port/t/${ref}/")
    }
): URL = SigblaBackend.openView(tableView, ref, urlGenerator)

fun show(table: Table): URL = show(TableView[table])
fun show(table: Table, ref: String): URL = show(TableView[table], ref)
fun show(
    table: Table,
    ref: String,
    urlGenerator: (engine: ApplicationEngine, view: TableView, ref: String) -> URL
): URL = show(TableView[table], ref, urlGenerator)

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
        ) {
            this.map {
                TableViewListenerEvent(relatedFromViewRelated(oldView, it.oldValue), relatedFromViewRelated(newView, it.newValue))
            }
        }
        else -> TableViewEventReceiver(
            tableView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.map {
                TableViewListenerEvent(relatedFromViewRelated(oldView, it.oldValue), relatedFromViewRelated(newView, it.newValue))
            }.filter {
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
        ) {
            this.map {
                TableViewListenerEvent(relatedFromViewRelated(oldView, it.oldValue), relatedFromViewRelated(newView, it.newValue))
            }
        }
        else -> TableViewEventReceiver(
            columnView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.map {
                TableViewListenerEvent(relatedFromViewRelated(oldView, it.oldValue), relatedFromViewRelated(newView, it.newValue))
            }.filter {
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
        ) {
            this.map {
                TableViewListenerEvent(relatedFromViewRelated(oldView, it.oldValue), relatedFromViewRelated(newView, it.newValue))
            }
        }
        else -> TableViewEventReceiver(
            rowView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.map {
                TableViewListenerEvent(relatedFromViewRelated(oldView, it.oldValue), relatedFromViewRelated(newView, it.newValue))
            }.filter {
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
        ) {
            this.map {
                TableViewListenerEvent(relatedFromViewRelated(oldView, it.oldValue), relatedFromViewRelated(newView, it.newValue))
            }
        }
        else -> TableViewEventReceiver(
            cellView,
            type,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.map {
                TableViewListenerEvent(relatedFromViewRelated(oldView, it.oldValue), relatedFromViewRelated(newView, it.newValue))
            }.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return cellView.tableView.eventProcessor.subscribe(cellView, eventReceiver, init)
}

// ---

// TODO Something like Cells for views too?

// ---

fun off(reference: TableViewListenerReference) = reference.unsubscribe()

fun off(tableViewEventReceiver: TableViewEventReceiver<*, *>) = off(tableViewEventReceiver.reference)

// ---

fun <R> batch(tableView: TableView, batch: TableView.() -> R): R {
    synchronized(tableView.eventProcessor) {
        if (tableView.eventProcessor.pauseEvents()) {
            try {
                tableView.tableViewRef.useLocal()
                val r = tableView.batch()
                tableView.eventProcessor.publish(true)
                tableView.tableViewRef.commitLocal()
                return r
            } finally {
                tableView.eventProcessor.clearBuffer()
                tableView.tableViewRef.clearLocal()
            }
        } else {
            return tableView.batch()
        }
    }
}

// ---

fun load(
    tableView: TableView,
    extension: String = "sigv"
) = load1(
    File(tableView.name ?: throw InvalidTableViewException("No table view name")) to tableView,
    extension
)

fun save(
    tableView: TableView,
    extension: String = "sigv",
    compress: Boolean = true
) = save1(
    tableView to File(tableView.name ?: throw InvalidTableViewException("No table view name")),
    extension,
    compress
)

fun load(
    resources: Pair<File, TableView>,
    extension: String = "sigv",
) = load1(resources, extension)

fun save(
    resources: Pair<TableView, File>,
    extension: String = "sigv",
    compress: Boolean = true
) = save1(resources, extension, compress)

@JvmName("loadString")
fun load(
    resources: Pair<String, TableView>,
    extension: String = "sigv"
) = load1(
    resources.let { File(it.first) to it.second },
    extension
)

@JvmName("saveString")
fun save(
    resources: Pair<TableView, String>,
    extension: String = "sigv",
    compress: Boolean = true
) = save1(
    resources.let { it.first to File(it.second) },
    extension,
    compress
)
