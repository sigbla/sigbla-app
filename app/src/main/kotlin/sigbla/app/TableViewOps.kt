package sigbla.app

import sigbla.app.internals.SigblaBackend
import sigbla.app.internals.refAction
import kotlin.reflect.KClass

fun clear(columnView: ColumnView) {
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

fun clear(rowView: RowView) {
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

fun clear(cellView: CellView) {
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

// ---

fun clone(tableView: TableView): TableView = tableView.makeClone()

fun clone(tableView: TableView, name: String): TableView = tableView.makeClone(name, true)

// TODO Add a fun show(table: Table) that would automatically create a view or reuse existing view on same name
fun show(tableView: TableView) = SigblaBackend.openView(tableView)

// ---

inline fun <reified T> on(tableView: TableView, noinline init: TableViewEventReceiver<TableView, T>.() -> Unit): TableViewListenerReference {
    return on(tableView, T::class, init as TableViewEventReceiver<TableView, Any>.() -> Unit)
}

fun on(tableView: TableView, type: KClass<*> = Any::class, init: TableViewEventReceiver<TableView, Any>.() -> Unit): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<TableView, Any>(tableView) { this }
        else -> TableViewEventReceiver(tableView, type) {
            this.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return tableView.eventProcessor.subscribe(tableView, eventReceiver, init)
}

// ---

inline fun <reified T> on(columnView: ColumnView, noinline init: TableViewEventReceiver<ColumnView, T>.() -> Unit): TableViewListenerReference {
    return on(columnView, T::class, init as TableViewEventReceiver<ColumnView, Any>.() -> Unit)
}

fun on(columnView: ColumnView, type: KClass<*> = Any::class, init: TableViewEventReceiver<ColumnView, Any>.() -> Unit): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<ColumnView, Any>(columnView) { this }
        else -> TableViewEventReceiver(columnView, type) {
            this.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return columnView.tableView.eventProcessor.subscribe(columnView, eventReceiver, init)
}

// ---

inline fun <reified T> on(rowView: RowView, noinline init: TableViewEventReceiver<RowView, T>.() -> Unit): TableViewListenerReference {
    return on(rowView, T::class, init as TableViewEventReceiver<RowView, Any>.() -> Unit)
}

fun on(rowView: RowView, type: KClass<*> = Any::class, init: TableViewEventReceiver<RowView, Any>.() -> Unit): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<RowView, Any>(rowView) { this }
        else -> TableViewEventReceiver(rowView, type) {
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

inline fun <reified T> on(cellView: CellView, noinline init: TableViewEventReceiver<CellView, T>.() -> Unit): TableViewListenerReference {
    return on(cellView, T::class, init as TableViewEventReceiver<CellView, Any>.() -> Unit)
}

fun on(cellView: CellView, type: KClass<*> = Any::class, init: TableViewEventReceiver<CellView, Any>.() -> Unit): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<CellView, Any>(cellView) { this }
        else -> TableViewEventReceiver(cellView, type) {
            this.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return cellView.tableView.eventProcessor.subscribe(cellView, eventReceiver, init)
}

// ---

inline fun <reified T> on(derivedCellView: DerivedCellView, noinline init: TableViewEventReceiver<DerivedCellView, T>.() -> Unit): TableViewListenerReference {
    return on(derivedCellView, T::class, init as TableViewEventReceiver<DerivedCellView, Any>.() -> Unit)
}

fun on(derivedCellView: DerivedCellView, type: KClass<*> = Any::class, init: TableViewEventReceiver<DerivedCellView, Any>.() -> Unit): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<DerivedCellView, Any>(derivedCellView) { this }
        else -> TableViewEventReceiver(derivedCellView, type) {
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
