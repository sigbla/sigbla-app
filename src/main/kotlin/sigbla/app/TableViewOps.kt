package sigbla.app

import sigbla.app.internals.refAction

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

    eventProcessor.publish(listOf(
        TableViewListenerEvent(
            Area(old.tableView, old),
            Area(new.tableView, new)
        )
    ) as List<TableViewListenerEvent<Any>>)
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

    eventProcessor.publish(listOf(
        TableViewListenerEvent(
            Area(old.tableView, old),
            Area(new.tableView, new)
        )
    ) as List<TableViewListenerEvent<Any>>)
}

fun remove(cell: CellView) {
    val tableViewRef = cell.tableView.tableViewRef
    val columnHeader = cell.columnHeader
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

    eventProcessor.publish(listOf(
        TableViewListenerEvent(
            Area(old.tableView, old),
            Area(new.tableView, new)
        )
    ) as List<TableViewListenerEvent<Any>>)
}
