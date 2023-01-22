package sigbla.app

import sigbla.app.internals.SigblaBackend
import sigbla.app.internals.refAction
import kotlin.reflect.KClass

// TODO We still want a ColumnView and RowView so we can do tableView["A"/1] to get one,
//      and also tableView["A"][1] to get CellView (with short cuts for tableView["A", 1] like on Table)
//      Should also allow table["A"/1] = { .. } but this would be a CellViewBuilder..
fun remove(column: ColumnView) {
    val tableViewRef = column.tableView.tableViewRef
    val columnHeader = column.columnHeader
    val eventProcessor = column.tableView.eventProcessor

    val (oldRef, newRef) = tableViewRef.refAction {
        it.copy(
            columnViews = it.columnViews.remove(columnHeader)
        )
    }

    if (!eventProcessor.haveListeners()) return

    val oldView = column.tableView.makeClone(ref = oldRef)
    val newView = column.tableView.makeClone(ref = newRef)

    val old = oldView[column]
    val new = newView[column]

    eventProcessor.publish(listOf(TableViewListenerEvent<ColumnView>(old, new)) as List<TableViewListenerEvent<Any>>)
}

fun remove(row: RowView) {
    val tableViewRef = row.tableView.tableViewRef
    val eventProcessor = row.tableView.eventProcessor

    val (oldRef, newRef) = tableViewRef.refAction {
        it.copy(
            rowViews = it.rowViews.remove(row.index)
        )
    }

    if (!eventProcessor.haveListeners()) return

    val oldView = row.tableView.makeClone(ref = oldRef)
    val newView = row.tableView.makeClone(ref = newRef)

    val old = oldView[row]
    val new = newView[row]

    eventProcessor.publish(listOf(TableViewListenerEvent<RowView>(old, new)) as List<TableViewListenerEvent<Any>>)
}

fun remove(cell: CellView) {
    val tableViewRef = cell.tableView.tableViewRef
    val columnHeader = cell.columnView.columnHeader
    val index = cell.index
    val eventProcessor = cell.tableView.eventProcessor

    val (oldRef, newRef) = tableViewRef.refAction {
        it.copy(
            cellViews = it.cellViews.remove(Pair(columnHeader, index))
        )
    }

    if (!eventProcessor.haveListeners()) return

    val oldView = cell.tableView.makeClone(ref = oldRef)
    val newView = cell.tableView.makeClone(ref = newRef)

    val old = oldView[cell]
    val new = newView[cell]

    eventProcessor.publish(listOf(TableViewListenerEvent<CellView>(old, new)) as List<TableViewListenerEvent<Any>>)
}

// ---

fun clone(tableView: TableView): TableView = tableView.makeClone()

fun clone(tableView: TableView, name: String): TableView = tableView.makeClone(name, true)

// TODO Add a fun show(table: Table) that would automatically create a view or reuse existing view on same name
fun show(tableView: TableView) = SigblaBackend.openView(tableView)

// ---

// TODO You should be able to do on<DerivedCellView>(..) and get the derived cell views when table|column|row|cellView is applied
inline fun <reified T> on(tableView: TableView, noinline init: TableViewEventReceiver<TableView, T>.() -> Unit): TableViewListenerReference {
    return on(tableView, T::class, init as TableViewEventReceiver<TableView, Any>.() -> Unit)
}

fun on(tableView: TableView, type: KClass<*> = Any::class, init: TableViewEventReceiver<TableView, Any>.() -> Unit): TableViewListenerReference {
    val eventReceiver = when {
        type == Any::class -> TableViewEventReceiver<TableView, Any>(tableView) { this }
        else -> TableViewEventReceiver(tableView) {
            this.filter {
                type.isInstance(it.oldValue) || type.isInstance(it.newValue)
            }
        }
    }
    return tableView.eventProcessor.subscribe(tableView, eventReceiver, init)
}

// TODO Other on/off functions