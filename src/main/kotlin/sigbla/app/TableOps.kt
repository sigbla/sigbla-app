package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.internals.Registry
import sigbla.app.internals.refAction
import java.util.*
import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.collection.TreeMap as PTreeMap
import kotlin.reflect.KClass

// TODO Refactor TableOps.kt into various files, like for column ops, rows ops, events, etc..?

// TODO Implement something similar for moving/copying rows around, like move(t[1] after t[2]), etc
// TODO Implement something for moving rows around within a column, like move(t["A", 1] after t["A", 2]), etc?

private fun publishColumnMoveEvents(
    left: Column,
    right: Column,
    newRight: Column,
    order: ColumnActionOrder,
    t1OldRef: TableRef,
    t1NewRef: TableRef,
    t2OldRef: TableRef,
    t2NewRef: TableRef
) {
    if (!left.table.eventProcessor.haveListeners()
        && !right.table.eventProcessor.haveListeners()
        && !newRight.table.eventProcessor.haveListeners()) return

    fun prepareEvents(column: Column, oldRef: TableRef, newRef: TableRef): Pair<Table, MutableList<TableListenerEvent<Any, Any>>> {
        // We need to do this in order to disconnect the columns from the original table
        val oldTable = column.table.makeClone(ref = oldRef)
        val newTable = column.table.makeClone(ref = newRef)

        val oldRef = oldTable.tableRef.get()
        val newRef = newTable.tableRef.get()

        val indexes1 = oldRef.columnCells[column.columnHeader]?.keys() ?: emptySet()
        val indexes2 = newRef.columnCells[column.columnHeader]?.keys() ?: emptySet()
        val indexes = indexes1 union indexes2

        // Get columns anchored to old and new ref
        val oldColumn = BaseColumn(
            oldTable,
            column.columnHeader,
            oldRef.columns[column.columnHeader]?.columnOrder ?: column.columnOrder
        )
        val newColumn = BaseColumn(
            newTable,
            column.columnHeader,
            newRef.columns[column.columnHeader]?.columnOrder ?: column.columnOrder
        )

        val events = indexes.map { TableListenerEvent(oldColumn[it], newColumn[it]) as TableListenerEvent<Any, Any> }

        return column.table to events.toMutableList()
    }

    fun publishEvents(vararg tableEvents: Pair<Table, MutableList<TableListenerEvent<Any, Any>>>) {
        val groupedEvents = IdentityHashMap<Table, MutableList<TableListenerEvent<Any, Any>>>()

        tableEvents.forEach {
            groupedEvents.compute(it.first) { _, v -> v?.apply { v.addAll(it.second) } ?: it.second }
        }

        groupedEvents.forEach { (t, e) -> t.eventProcessor.publish(e) }
    }

    // There's a lot of options for moving things around, while also renaming the columns they are moved to.
    // Depending on the way this is done, events are needed to reflect all the changes. Below the event
    // model is laid out. In short: We don't want to emit more events than strictly needed, which means
    // to only emit enough events to allow a listener to recreate the change on their end just from the events.

    // Note: Because the events only concern themselves with the columns being moved, the order change
    //       might impact the order of other columns. The events in isolation do not reflect the change
    //       to the order index of other columns, but give access to the updated table as a whole.

    // Cases:

    // TODO: Optimise below when cases..?

    when {
        // move(T1["A"] to T1["A"], "A")        -> No-op move, will produce events for that column
        order == ColumnActionOrder.TO && left.table === right.table && left.columnHeader == newRight.columnHeader && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
            )
        }

        // move(T1["A"] to T1["A"], "B")        -> A is renamed to B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (A value, A order)
        order == ColumnActionOrder.TO && left.table === right.table && left.columnHeader == right.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T1["B"], "B")        -> A takes the place of B, named B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T2["B"], "B")        -> A takes the place of B, named B, in T2. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] to T1["B"], "A")        -> A takes the place of B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, B order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (unit value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T2["B"], "A")        -> A takes the place of B, named A. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T2["B"] (B value, B order) -> T2["B"] (unit value, B order)
        //                                                      T2["A"] (A value, A order) -> T2["A"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] to T1["B"], "C")        -> A takes the place of B, named C, removing any existing C
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (unit value, B order),
        //                                                      T1["C"] (C value, C order) -> T1["C"] (A value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && left.columnHeader != right.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t1OldRef, t1NewRef),
                prepareEvents(newRight, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T2["B"], "C")        -> A takes the place of B, named C, removing any existing C. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T2["B"] (B value, B order) -> T2["B"] (unit value, B order),
        //                                                      T2["C"] (C value, C order) -> T2["C"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] after T1["B"], "A")     -> A moved after B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table === right.table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] after T2["B"], "A")     -> A moved after B, named A. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["A"] (A value, A order) -> T2["A"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table !== right.table && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] after T1["B"], "B")     -> A moved after B, named B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.AFTER && left.table === right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] after T2["B"], "B")     -> A moved after B, named B. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.AFTER && left.table !== right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] after T1["B"], "C")     -> A moved after B, named C, removing any existing C
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T1["C"] (C value, C value) -> T1["C"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table === right.table && left.columnHeader != newRight.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] after T2["B"], "C")     -> A moved after B, named C, removing any existing C. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["C"] (C value, C value) -> T2["C"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table !== right.table && left.columnHeader != newRight.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] before T1["B"], "A")    -> A moved before B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table === right.table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] before T2["B"], "A")    -> A moved before B, named A. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["A"] (A value, A order) -> T2["A"] (T1["A"] value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] before T1["B"], "B")    -> A moved before B, named B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                      ->              T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.BEFORE && left.table === right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] before T2["B"], "B")    -> A moved before B, named B. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                      ->              T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] before T1["B"], "C")    -> A moved before B, named C, removing any existing C
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T1["C"] (C value, C value) -> T1["C"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table === right.table && left.columnHeader != newRight.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] before T2["B"], "C")    -> A moved before B, named C, removing any existing C. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["C"] (C value, C value) -> T2["C"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && left.columnHeader != newRight.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // Should never happen
        else -> throw UnsupportedOperationException()
    }
}

private fun publishTableMoveEvents(
    left: Column,
    table: Table,
    newRight: Column,
    t1OldRef: TableRef,
    t1NewRef: TableRef,
    t2OldRef: TableRef,
    t2NewRef: TableRef
) {
    if (!left.table.eventProcessor.haveListeners()
        && !table.eventProcessor.haveListeners()
        && !newRight.table.eventProcessor.haveListeners()) return

    fun prepareEvents(column: Column, oldRef: TableRef, newRef: TableRef): Pair<Table, MutableList<TableListenerEvent<Any, Any>>> {
        // We need to do this in order to disconnect the columns from the original table
        val oldTable = column.table.makeClone(ref = oldRef)
        val newTable = column.table.makeClone(ref = newRef)

        val oldRef = oldTable.tableRef.get()
        val newRef = newTable.tableRef.get()

        val indexes1 = oldRef.columnCells[column.columnHeader]?.keys() ?: emptySet()
        val indexes2 = newRef.columnCells[column.columnHeader]?.keys() ?: emptySet()
        val indexes = indexes1 union indexes2

        // Get columns anchored to old and new ref
        val oldColumn = BaseColumn(
            oldTable,
            column.columnHeader,
            oldRef.columns[column.columnHeader]?.columnOrder ?: column.columnOrder
        )
        val newColumn = BaseColumn(
            newTable,
            column.columnHeader,
            newRef.columns[column.columnHeader]?.columnOrder ?: column.columnOrder
        )

        val events = indexes.map { TableListenerEvent(oldColumn[it], newColumn[it]) as TableListenerEvent<Any, Any> }

        return column.table to events.toMutableList()
    }

    fun publishEvents(vararg tableEvents: Pair<Table, MutableList<TableListenerEvent<Any, Any>>>) {
        val groupedEvents = IdentityHashMap<Table, MutableList<TableListenerEvent<Any, Any>>>()

        tableEvents.forEach {
            groupedEvents.compute(it.first) { _, v -> v?.apply { v.addAll(it.second) } ?: it.second }
        }

        groupedEvents.forEach { (t, e) -> t.eventProcessor.publish(e) }
    }

    // There's a lot of options for moving things around, while also renaming the columns they are moved to.
    // Depending on the way this is done, events are needed to reflect all the changes. Below the event
    // model is laid out. In short: We don't want to emit more events than strictly needed, which means
    // to only emit enough events to allow a listener to recreate the change on their end just from the events.

    // Note: Because the events only concern themselves with the columns being moved, the order change
    //       might impact the order of other columns. The events in isolation do not reflect the change
    //       to the order index of other columns, but give access to the updated table as a whole.

    // Cases:

    // TODO: Optimise below when cases..?

    when {
        // move(T1["A"] to T1, "A")        -> A is moved to end of T1
        //                                 -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order)
        left.table === table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T1, "B")        -> A is moved to end of T1 and renamed to B
        //                                 -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                 T1["B"] (B value, B order) -> T1["B"] (A value, new B order)
        left.table === table && left.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T2, "A")        -> A takes the place of B, named A. A removed from T1
        //                                 -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                 T2["A"] (A value, A order) -> T2["A"] (A value, new A order)
        left.table !== table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] to T2, "B")        -> A takes the place of B, named B, in T2. A removed from T1
        //                                 -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                 T2["B"] (B value, B order) -> T2["B"] (A value, new B order)
        left.table !== table && left.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // Should never happen
        else -> throw UnsupportedOperationException()
    }
}

private fun publishColumnCopyEvents(
    left: Column,
    right: Column,
    newRight: Column,
    order: ColumnActionOrder,
    oldRef: TableRef,
    newRef: TableRef
) {
    if (!left.table.eventProcessor.haveListeners()
        && !right.table.eventProcessor.haveListeners()
        && !newRight.table.eventProcessor.haveListeners()) return

    fun prepareEvents(column: Column, oldRef: TableRef, newRef: TableRef): Pair<Table, MutableList<TableListenerEvent<Any, Any>>> {
        // We need to do this in order to disconnect the columns from the original table
        val oldTable = column.table.makeClone(ref = oldRef)
        val newTable = column.table.makeClone(ref = newRef)

        val oldRef = oldTable.tableRef.get()
        val newRef = newTable.tableRef.get()

        val indexes1 = oldRef.columnCells[column.columnHeader]?.keys() ?: emptySet()
        val indexes2 = newRef.columnCells[column.columnHeader]?.keys() ?: emptySet()
        val indexes = indexes1 union indexes2

        // Get columns anchored to old and new ref
        val oldColumn = BaseColumn(
            oldTable,
            column.columnHeader,
            oldRef.columns[column.columnHeader]?.columnOrder ?: column.columnOrder
        )
        val newColumn = BaseColumn(
            newTable,
            column.columnHeader,
            newRef.columns[column.columnHeader]?.columnOrder ?: column.columnOrder
        )

        val events = indexes.map { TableListenerEvent(oldColumn[it], newColumn[it]) as TableListenerEvent<Any, Any> }

        return column.table to events.toMutableList()
    }

    fun publishEvents(vararg tableEvents: Pair<Table, MutableList<TableListenerEvent<Any, Any>>>) {
        val groupedEvents = IdentityHashMap<Table, MutableList<TableListenerEvent<Any, Any>>>()

        tableEvents.forEach {
            groupedEvents.compute(it.first) { _, v -> v?.apply { v.addAll(it.second) } ?: it.second }
        }

        groupedEvents.forEach { (t, e) -> t.eventProcessor.publish(e) }
    }

    // There's a lot of options for copying things around, while also renaming the columns they are moved to.
    // Depending on the way this is done, events are needed to reflect all the changes. Below the event
    // model is laid out. In short: We don't want to emit more events than strictly needed, which means
    // to only emit enough events to allow a listener to recreate the change on their end just from the events.

    // Note: Because the events only concern themselves with the columns being moved, the order change
    //       might impact the order of other columns. The events in isolation do not reflect the change
    //       to the order index of other columns, but give access to the updated table as a whole.

    // Cases:

    // TODO: Optimise below when cases..?

    when {
        // copy(T1["A"] to T1["A"], "A")        -> No-op copy, will produce events for that column
        order == ColumnActionOrder.TO && left.table === right.table && left.columnHeader == newRight.columnHeader && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef),
            )
        }

        // copy(T1["A"] to T1["A"], "B")        -> A is renamed to B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (A value, A order)
        order == ColumnActionOrder.TO && left.table === right.table && left.columnHeader == right.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef),
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T1["B"], "B")        -> A takes the place of B, named B
        //                                      -> Event model: T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T2["B"], "B")        -> A takes the place of B, named B, in T2
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T1["B"], "A")        -> A takes the place of B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, B order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (unit value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef),
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T2["B"], "A")        -> A takes the place of B, named A
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (unit value, B order)
        //                                                      T2["A"] (A value, A order) -> T2["A"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef),
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T1["B"], "C")        -> A takes the place of B, named C, removing any existing C
        //                                      -> Event model: T1["B"] (B value, B order) -> T1["B"] (unit value, B order),
        //                                                      T1["C"] (C value, C order) -> T1["C"] (A value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && left.columnHeader != right.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef),
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T2["B"], "C")        -> A takes the place of B, named C, removing any existing C
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (unit value, B order),
        //                                                      T2["C"] (C value, C order) -> T2["C"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef),
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T1["B"], "A")     -> A copied after B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table === right.table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T2["B"], "A")     -> A copied after B, named A
        //                                      -> Event model: T2["A"] (A value, A order) -> T2["A"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table !== right.table && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T1["B"], "B")     -> A copied after B, named B
        //                                      -> Event model: T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.AFTER && left.table === right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T2["B"], "B")     -> A copied after B, named B
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.AFTER && left.table !== right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T1["B"], "C")     -> A copied after B, named C, removing any existing C
        //                                      -> Event model: T1["C"] (C value, C value) -> T1["C"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table === right.table && left.columnHeader != newRight.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T2["B"], "C")     -> A copied after B, named C, removing any existing C
        //                                      -> Event model: T2["C"] (C value, C value) -> T2["C"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table !== right.table && left.columnHeader != newRight.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T1["B"], "A")    -> A copied before B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table === right.table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T2["B"], "A")    -> A copied before B, named A
        //                                      -> Event model: T2["A"] (A value, A order) -> T2["A"] (T1["A"] value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && left.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T1["B"], "B")    -> A copied before B, named B
        //                                      -> Event model: T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.BEFORE && left.table === right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T2["B"], "B")    -> A copied before B, named B
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && right.columnHeader == newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T1["B"], "C")    -> A copied before B, named C, removing any existing C
        //                                      -> Event model: T1["C"] (C value, C value) -> T1["C"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table === right.table && left.columnHeader != newRight.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T2["B"], "C")    -> A copied before B, named C, removing any existing C
        //                                      -> Event model: T2["C"] (C value, C value) -> T2["C"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && left.columnHeader != newRight.columnHeader && right.columnHeader != newRight.columnHeader
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // Should never happen
        else -> throw UnsupportedOperationException()
    }
}

fun move(columnToColumnAction: ColumnToColumnAction, withName: ColumnHeader) {
    fun columnMove(left: ColumnHeader, right: ColumnHeader, order: ColumnActionOrder, withName: ColumnHeader, refUpdate: TableRef.() -> TableRef): (ref: TableRef) -> TableRef = { inRef ->
        val ref = inRef.refUpdate()

        val changedColumns = ref
            .columns
            .asSequence()
            .sortedBy { (_, meta) -> meta.columnOrder }
            .dropWhile { (header, _) -> header != right }
            .filter { (header, _) -> header != left }
            .let {
                if (order == ColumnActionOrder.AFTER) it.drop(1)
                else it
            }
            .map { it.component1() }

        val firstChangedColumn = changedColumns.firstOrNull()

        val unchangedColumns = ref
            .columns
            .asSequence()
            .filter { (columnHeader, _) -> columnHeader != left }
            .sortedBy { (_, meta) -> meta.columnOrder }
            .map { it.component1() }
            .takeWhile { firstChangedColumn == null || firstChangedColumn != it } as Sequence<ColumnHeader>

        val newColumn = sequenceOf(withName)

        val remainingColumns = changedColumns.filter { it != withName }.let { columns ->
            if (order == ColumnActionOrder.TO) columns.filter { it != right }
            else columns
        } as Sequence<ColumnHeader>

        val allColumns = unchangedColumns + newColumn + remainingColumns

        val columnOrders = allColumns zip ref
            .columns
            .asSequence()
            .map { it.component2().columnOrder }
            .sorted()

        // Use sequence of columnOrder as it already exists, and just reassign accordingly to the new sequence..
        val newColumnMap = columnOrders.fold(PHashMap<ColumnHeader, ColumnMeta>()) { acc, (columnHeader, columnOrder) ->
            val prenatal = ref.columns[columnHeader]?.prenatal ?: throw InvalidColumnException(columnHeader)
            acc.put(columnHeader, ColumnMeta(columnOrder, prenatal))
        }

        // TODO Find a more efficient approach
        val removeColumnCells = ref.columnCells.keys().filter { !newColumnMap.containsKey(it) }

        ref.copy(
            columns = newColumnMap,
            columnCells = if (removeColumnCells.isEmpty()) ref.columnCells else removeColumnCells.fold(ref.columnCells) { acc, column -> acc.remove(column) },
            version = ref.version + 1L
        )
    }

    val left = columnToColumnAction.left
    val right = columnToColumnAction.right
    val order = columnToColumnAction.order

    if (left.table === right.table) {
        // Internal move
        val newRight = BaseColumn(left.table, withName)
        val (oldRef, newRef) = left.table.tableRef.refAction(
            (::columnMove)(left.columnHeader, right.columnHeader, order, withName) {
                copy(
                    columns = this.columns.put(withName, ColumnMeta(newRight.columnOrder, this.columns[left.columnHeader]?.prenatal ?: throw InvalidColumnException(left))),
                    columnCells = this.columnCells.put(withName, this.columnCells[left.columnHeader] ?: PTreeMap())
                )
            }
        )

        publishColumnMoveEvents(left, right, newRight, order, oldRef, newRef, oldRef, newRef)
    } else {
        // Move between tables
        val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
            ref.copy(
                columns = ref.columns.remove(left.columnHeader),
                columnCells = ref.columnCells.remove(left.columnHeader),
                version = ref.version + 1L
            )
        }

        val newRight = BaseColumn(right.table, withName)
        val (oldRef2, newRef2) = right.table.tableRef.refAction(
            (::columnMove)(newRight.columnHeader, right.columnHeader, order, withName) {
                copy(
                    columns = this.columns.put(withName, ColumnMeta(newRight.columnOrder, oldRef1.columns[left.columnHeader]?.prenatal ?: throw InvalidColumnException(left))),
                    columnCells = this.columnCells.put(withName, oldRef1.columnCells[left.columnHeader] ?: PTreeMap())
                )
            }
        )

        publishColumnMoveEvents(left, right, newRight, order, oldRef1, newRef1, oldRef2, newRef2)
    }
}

fun move(columnToColumnAction: ColumnToColumnAction, vararg withName: String) = move(columnToColumnAction, ColumnHeader(*withName))

fun move(columnToColumnAction: ColumnToColumnAction) = move(columnToColumnAction, if (columnToColumnAction.order == ColumnActionOrder.TO) columnToColumnAction.right.columnHeader else columnToColumnAction.left.columnHeader)

fun move(left: Column, actionOrder: ColumnActionOrder, right: Column, withName: ColumnHeader) = move(ColumnToColumnAction(left, right, actionOrder), withName)

fun move(left: Column, actionOrder: ColumnActionOrder, right: Column, vararg withName: String) = move(ColumnToColumnAction(left, right, actionOrder), *withName)

fun move(left: Column, actionOrder: ColumnActionOrder, right: Column) = move(ColumnToColumnAction(left, right, actionOrder))

fun move(columnToTableAction: ColumnToTableAction, withName: ColumnHeader) {
    fun columnMove(withName: ColumnHeader, refUpdate: TableRef.() -> TableRef): (ref: TableRef) -> TableRef = { inRef ->
        val ref = inRef.refUpdate()

        // TODO Probably don't need any of this logic as just adding a new column at the end is enough..
        //      But we need to take care of case where we're moving existing column to end
        val otherColumns = ref
            .columns
            .asSequence()
            .filter { (header, _) -> header != withName }
            .sortedBy { (_, meta) -> meta.columnOrder }
            .map { it.component1() } as Sequence<ColumnHeader>

        val newColumn = sequenceOf(withName)

        val allColumns = otherColumns + newColumn

        val columnOrders = allColumns zip ref
            .columns
            .asSequence()
            .map { it.component2().columnOrder }
            .sorted()

        // Use sequence of columnOrder as it already exists, and just reassign accordingly to the new sequence..
        val newColumnMap = columnOrders.fold(PHashMap<ColumnHeader, ColumnMeta>()) { acc, (columnHeader, columnOrder) ->
            val prenatal = ref.columns[columnHeader]?.prenatal ?: throw InvalidColumnException(columnHeader)
            acc.put(columnHeader, ColumnMeta(columnOrder, prenatal))
        }

        ref.copy(
            columns = newColumnMap,
            version = ref.version + 1L // TODO: If logic above removed, keep this..
        )
    }

    val left = columnToTableAction.left
    val table = columnToTableAction.table

    if (left.table === table) {
        // Internal move
        val newRight = BaseColumn(table, withName)
        val (oldRef, newRef) = table.tableRef.refAction(
            (::columnMove)(withName) {
                copy(
                    columns = this.columns.remove(left.columnHeader).put(withName, ColumnMeta(newRight.columnOrder, this.columns[left.columnHeader]?.prenatal ?: throw InvalidColumnException(left))),
                    columnCells = this.columnCells.remove(left.columnHeader).put(withName, this.columnCells[left.columnHeader] ?: PTreeMap()),
                )
            }
        )

        publishTableMoveEvents(left, table, newRight, oldRef, newRef, oldRef, newRef)
    } else {
        // Move between tables
        val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
            ref.copy(
                columns = ref.columns.remove(left.columnHeader),
                columnCells = ref.columnCells.remove(left.columnHeader),
                version = ref.version + 1L
            )
        }

        val newRight = BaseColumn(table, withName)
        val (oldRef2, newRef2) = table.tableRef.refAction(
            (::columnMove)(withName) {
                copy(
                    columns = this.columns.put(withName, ColumnMeta(newRight.columnOrder, oldRef1.columns[left.columnHeader]?.prenatal ?: throw InvalidColumnException(left))),
                    columnCells = this.columnCells.put(withName, oldRef1.columnCells[left.columnHeader] ?: PTreeMap()),
                )
            }
        )

        publishTableMoveEvents(left, table, newRight, oldRef1, newRef1, oldRef2, newRef2)
    }
}

fun move(columnToTableAction: ColumnToTableAction, vararg withName: String) = move(columnToTableAction, ColumnHeader(*withName))

fun move(columnToTableAction: ColumnToTableAction) = move(columnToTableAction, columnToTableAction.left.columnHeader)

fun move(left: Column, table: Table, withName: ColumnHeader) = move(ColumnToTableAction(left, table), withName)

fun move(left: Column, table: Table, vararg withName: String) = move(ColumnToTableAction(left, table), *withName)

fun move(left: Column, table: Table) = move(ColumnToTableAction(left, table), left.columnHeader)

fun copy(columnToColumnAction: ColumnToColumnAction, withName: ColumnHeader) {
    fun columnCopy(left: ColumnHeader, right: ColumnHeader, order: ColumnActionOrder, withName: ColumnHeader, refUpdate: TableRef.() -> TableRef): (ref: TableRef) -> TableRef = { inRef ->
        val ref = inRef.refUpdate()

        val changedColumns = ref
            .columns
            .asSequence()
            .sortedBy { (_, meta) -> meta.columnOrder }
            .dropWhile { (header, _) -> header != right }
            .filter { (header, _) -> header != withName || header != left}
            .let {
                if (order == ColumnActionOrder.AFTER) it.drop(1)
                else it
            }
            .map { it.component1() }

        val firstChangedColumn = changedColumns.firstOrNull()

        val unchangedColumns = ref
            .columns
            .asSequence()
            .filter { (header, _) -> header != withName || header != left }
            .sortedBy { (_, meta) -> meta.columnOrder }
            .map { it.component1() }
            .takeWhile { firstChangedColumn == null || firstChangedColumn != it } as Sequence<ColumnHeader>

        val newColumn = sequenceOf(withName)

        val remainingColumns = changedColumns.filter { it != withName }.let { columns ->
            if (order == ColumnActionOrder.TO) columns.filter { it != right }
            else columns
        } as Sequence<ColumnHeader>

        val allColumns = unchangedColumns + newColumn + remainingColumns

        val columnOrders = allColumns zip ref
            .columns
            .asSequence()
            .map { it.component2().columnOrder }
            .sorted()

        // Use sequence of columnOrder as it already exists, and just reassign accordingly to the new sequence..
        val newColumnMap = columnOrders.fold(PHashMap<ColumnHeader, ColumnMeta>()) { acc, (columnHeader, columnOrder) ->
            val prenatal = ref.columns[columnHeader]?.prenatal ?: throw InvalidColumnException(columnHeader)
            acc.put(columnHeader, ColumnMeta(columnOrder, prenatal))
        }

        // TODO Find a more efficient approach
        val removeColumnCells = ref.columnCells.keys().filter { !newColumnMap.containsKey(it) }

        ref.copy(
            columns = newColumnMap,
            columnCells = if (removeColumnCells.isEmpty()) ref.columnCells else removeColumnCells.fold(ref.columnCells) { acc, column -> acc.remove(column) },
            version = ref.version + 1L
        )
    }

    val left = columnToColumnAction.left
    val right = columnToColumnAction.right
    val order = columnToColumnAction.order

    if (left.table === right.table) {
        // Internal copy
        val newRight = BaseColumn(left.table, withName)
        val (oldRef, newRef) = left.table.tableRef.refAction(
            (::columnCopy)(left.columnHeader, right.columnHeader, order, withName) {
                copy(
                    columns = this.columns.put(withName, ColumnMeta(newRight.columnOrder, this.columns[left.columnHeader]?.prenatal ?: throw InvalidColumnException(left))),
                    columnCells = this.columnCells.put(withName, this.columnCells[left.columnHeader] ?: PTreeMap())
                )
            }
        )

        publishColumnCopyEvents(left, right, newRight, order, oldRef, newRef)
    } else {
        // Copy between tables
        val newRight = BaseColumn(right.table, withName)
        val (oldRef, newRef) = right.table.tableRef.refAction(
            (::columnCopy)(newRight.columnHeader, right.columnHeader, order, withName) {
                val leftRef = left.table.tableRef.get()
                copy(
                    columns = this.columns.put(withName, ColumnMeta(newRight.columnOrder, leftRef.columns[left.columnHeader]?.prenatal ?: throw InvalidColumnException(left))),
                    columnCells = this.columnCells.put(withName, leftRef.columnCells[left.columnHeader] ?: PTreeMap())
                )
            }
        )

        publishColumnCopyEvents(left, right, newRight, order, oldRef, newRef)
    }
}

fun copy(columnToColumnAction: ColumnToColumnAction, vararg withName: String) = copy(columnToColumnAction, ColumnHeader(*withName))

fun copy(columnToColumnAction: ColumnToColumnAction) = copy(columnToColumnAction, if (columnToColumnAction.order == ColumnActionOrder.TO) columnToColumnAction.right.columnHeader else columnToColumnAction.left.columnHeader)

fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column, withName: ColumnHeader) = copy(ColumnToColumnAction(left, right, actionOrder), withName)

fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column, vararg withName: String) = copy(ColumnToColumnAction(left, right, actionOrder), ColumnHeader(*withName))

fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column) = copy(ColumnToColumnAction(left, right, actionOrder))

fun copy(columnToTableAction: ColumnToTableAction, withName: ColumnHeader) {
    fun columnCopy(withName: ColumnHeader, refUpdate: TableRef.() -> TableRef): (ref: TableRef) -> TableRef = { inRef ->
        val ref = inRef.refUpdate()

        // TODO Probably don't need any of this logic as just adding a new column at the end is enough..
        val otherColumns = ref
            .columns
            .asSequence()
            .filter { (header, _) -> header != withName }
            .sortedBy { (_, meta) -> meta.columnOrder }
            .map { it.component1() } as Sequence<ColumnHeader>

        val newColumn = sequenceOf(withName)

        val allColumns = otherColumns + newColumn

        val columnOrders = allColumns zip ref
            .columns
            .asSequence()
            .map { it.component2().columnOrder }
            .sorted()

        // Use sequence of columnOrder as it already exists, and just reassign accordingly to the new sequence..
        val newColumnMap = columnOrders.fold(PHashMap<ColumnHeader, ColumnMeta>()) { acc, (columnHeader, columnOrder) ->
            val prenatal = ref.columns[columnHeader]?.prenatal ?: throw InvalidColumnException(columnHeader)
            acc.put(columnHeader, ColumnMeta(columnOrder, prenatal))
        }

        ref.copy(
            columns = newColumnMap,
            version = ref.version + 1L // TODO: If logic above removed, keep this..
        )
    }

    val left = columnToTableAction.left
    val table = columnToTableAction.table

    if (left.table === table) {
        // Internal copy
        val newLeft = BaseColumn(table, withName)
        val (oldRef, newRef) = table.tableRef.refAction(
            (::columnCopy)(withName) {
                copy(
                    columns = this.columns.put(withName, ColumnMeta(newLeft.columnOrder, this.columns[left.columnHeader]?.prenatal ?: throw InvalidColumnException(left))),
                    columnCells = this.columnCells.put(withName, this.columnCells[left.columnHeader] ?: PTreeMap()),
                )
            }
        )
        // TODO Events
    } else {
        // Copy between tables
        val newLeft = BaseColumn(table, withName)
        val (oldRef, newRef) = table.tableRef.refAction(
            (::columnCopy)(withName) {
                val leftRef = left.table.tableRef.get()
                copy(
                    columns = this.columns.put(withName, ColumnMeta(newLeft.columnOrder, leftRef.columns[left.columnHeader]?.prenatal ?: throw InvalidColumnException(left))),
                    columnCells = this.columnCells.put(withName, left.table.tableRef.get().columnCells[left.columnHeader] ?: PTreeMap()),
                )
            }
        )
        // TODO Events
    }
}

fun copy(columnToTableAction: ColumnToTableAction, vararg withName: String) = copy(columnToTableAction, ColumnHeader(*withName))

fun copy(columnToTableAction: ColumnToTableAction) = copy(columnToTableAction, columnToTableAction.left.columnHeader)

fun copy(left: Column, table: Table, withName: ColumnHeader) = copy(ColumnToTableAction(left, table), withName)

fun copy(left: Column, table: Table, vararg withName: String) = copy(ColumnToTableAction(left, table), *withName)

fun copy(left: Column, table: Table) = copy(ColumnToTableAction(left, table), left.columnHeader)

// ---

fun move(rowToRowAction: RowToRowAction) {
    val left = rowToRowAction.left
    val right = rowToRowAction.right
    val order = rowToRowAction.order

    if (order == RowActionOrder.TO) {
        if (left.table === right.table) {
            // Internal move
            val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                        acc.put(ccm.component1(), ccm.component2().remove(left.index).let {
                            val cell = ccm.component2().get(left.index)
                            if (cell != null) it.put(right.index, cell) else it
                        })
                    }
                )
            }

            // TODO Events
        } else {
            // Move between tables
            val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                        acc.put(ccm.component1(), ccm.component2().remove(left.index))
                    }
                )
            }

            val (oldRef2, newRef2) = right.table.tableRef.refAction { ref ->
                val columnsMap = oldRef1
                    .columns
                    .sortedBy { it.component2().columnOrder }
                    .fold(ref.columns) { acc, (c, cm) ->
                        if (acc.containsKey(c)) acc else acc.put(c, ColumnMeta(BaseColumn(right.table, c).columnOrder, cm.prenatal))
                    }

                val columnCellMap = oldRef1.columnCells.fold(ref.columnCells) { acc, ccm ->
                    val cell = ccm.component2().get(left.index)
                    if (cell != null) {
                        val columnHeader = ccm.component1()
                        acc.put(columnHeader, (acc.get(columnHeader) ?: PTreeMap()).put(right.index, cell))
                    } else acc
                }

                ref.copy(
                    columns = columnsMap,
                    columnCells = columnCellMap
                )
            }

            // TODO Events
        }
    } else {
        if (left.table === right.table) {
            // Internal move
            val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                        val newIndex = if (order == RowActionOrder.AFTER) right.index + 1 else right.index - 1

                        val withoutMoved = ccm.component2().remove(left.index)
                        val headCells = withoutMoved.to(newIndex, order != RowActionOrder.AFTER)
                        val tailCells = withoutMoved.from(newIndex, order == RowActionOrder.AFTER)

                        val cells = if (order == RowActionOrder.AFTER) {
                            tailCells.fold(headCells) { acc2, cell ->
                                // Shift down
                                acc2.put(cell.component1() + 1, cell.component2())
                            }
                        } else {
                            headCells.fold(tailCells) { acc2, cell ->
                                // Shift up
                                acc2.put(cell.component1() - 1, cell.component2())
                            }
                        }

                        acc.put(ccm.component1(), cells.let {
                            val cellValue = ccm.component2().get(left.index)
                            if (cellValue != null) it.put(newIndex, cellValue) else it
                        })
                    }
                )
            }

            // TODO Events
        } else {
            // Move between tables
            val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                        acc.put(ccm.component1(), ccm.component2().remove(left.index))
                    }
                )
            }

            val (oldRef2, newRef2) = right.table.tableRef.refAction { ref ->
                val columnsMap = oldRef1
                    .columns
                    .sortedBy { it.component2().columnOrder }
                    .fold(ref.columns) { acc, (c, cm) ->
                        if (acc.containsKey(c)) {
                            val existing = acc[c]!!
                            acc.put(c, existing.copy(prenatal = existing.prenatal && cm.prenatal))
                        } else acc.put(c, ColumnMeta(BaseColumn(right.table, c).columnOrder, cm.prenatal))
                    }

                val columnCellMap = oldRef1.columnCells.fold(ref.columnCells) { acc, ccm ->
                    val columnHeader = ccm.component1()

                    val cm = acc.get(columnHeader) ?: PTreeMap()

                    val newIndex = if (order == RowActionOrder.AFTER) right.index + 1 else right.index - 1

                    val headCells = cm.to(newIndex, order != RowActionOrder.AFTER)
                    val tailCells = cm.from(newIndex, order == RowActionOrder.AFTER)

                    val cells = if (order == RowActionOrder.AFTER) {
                        tailCells.fold(headCells) { acc2, cell ->
                            // Shift down
                            acc2.put(cell.component1() + 1, cell.component2())
                        }
                    } else {
                        headCells.fold(tailCells) { acc2, cell ->
                            // Shift up
                            acc2.put(cell.component1() - 1, cell.component2())
                        }
                    }

                    acc.put(columnHeader, cells.let {
                        val cellValue = ccm.component2().get(left.index)
                        if (cellValue != null) it.put(newIndex, cellValue) else it
                    })
                }

                ref.copy(
                    columns = columnsMap,
                    columnCells = columnCellMap
                )
            }

            // TODO Events
        }
    }
}

fun move(left: Row, actionOrder: RowActionOrder, right: Row) = move(RowToRowAction(left, right, actionOrder))

fun copy(rowToRowAction: RowToRowAction) {
    val left = rowToRowAction.left
    val right = rowToRowAction.right
    val order = rowToRowAction.order

    if (order == RowActionOrder.TO) {
        if (left.table === right.table) {
            // Internal copy
            val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                        acc.put(ccm.component1(), ccm.component2().let {
                            val cell = ccm.component2().get(left.index)
                            if (cell != null) it.put(right.index, cell) else it
                        })
                    }
                )
            }

            // TODO Events
        } else {
            // Copy between tables
            val (oldRef, newRef) = right.table.tableRef.refAction { ref ->
                val leftRef = left.table.tableRef.get()

                val columnsMap = leftRef
                    .columns
                    .sortedBy { it.component2().columnOrder }
                    .fold(ref.columns) { acc, (c, cm) ->
                        if (acc.containsKey(c)) acc else acc.put(c, ColumnMeta(BaseColumn(right.table, c).columnOrder, cm.prenatal))
                    }

                val columnCellMap = leftRef.columnCells.fold(ref.columnCells) { acc, ccm ->
                    val cell = ccm.component2().get(left.index)
                    if (cell != null) {
                        val columnHeader = ccm.component1()
                        acc.put(columnHeader, (acc.get(columnHeader) ?: PTreeMap()).put(right.index, cell))
                    } else acc
                }

                ref.copy(
                    columns = columnsMap,
                    columnCells = columnCellMap
                )
            }

            // TODO Events
        }
    } else {
        if (left.table === right.table) {
            // Internal copy
            val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                        val newIndex = if (order == RowActionOrder.AFTER) right.index + 1 else right.index - 1

                        val headCells = ccm.component2().to(newIndex, order != RowActionOrder.AFTER)
                        val tailCells = ccm.component2().from(newIndex, order == RowActionOrder.AFTER)

                        val cells = if (order == RowActionOrder.AFTER) {
                            tailCells.fold(headCells) { acc2, cell ->
                                // Shift down
                                acc2.put(cell.component1() + 1, cell.component2())
                            }
                        } else {
                            headCells.fold(tailCells) { acc2, cell ->
                                // Shift up
                                acc2.put(cell.component1() - 1, cell.component2())
                            }
                        }

                        acc.put(ccm.component1(), cells.let {
                            val cellValue = ccm.component2().get(left.index)
                            if (cellValue != null) it.put(newIndex, cellValue) else it
                        })
                    }
                )
            }

            // TODO Events
        } else {
            // Copy between tables
            val (oldRef, newRef) = right.table.tableRef.refAction { ref ->
                val leftRef = left.table.tableRef.get()

                val columnsMap = leftRef
                    .columns
                    .sortedBy { it.component2().columnOrder }
                    .fold(ref.columns) { acc, (c, cm) ->
                        if (acc.containsKey(c)) {
                            val existing = acc[c]!!
                            acc.put(c, existing.copy(prenatal = existing.prenatal && cm.prenatal))
                        } else acc.put(c, ColumnMeta(BaseColumn(right.table, c).columnOrder, cm.prenatal))
                    }

                val columnCellMap = leftRef.columnCells.fold(ref.columnCells) { acc, ccm ->
                    val columnHeader = ccm.component1()

                    val cm = acc.get(columnHeader) ?: PTreeMap()

                    val newIndex = if (order == RowActionOrder.AFTER) right.index + 1 else right.index - 1

                    val headCells = cm.to(newIndex, order != RowActionOrder.AFTER)
                    val tailCells = cm.from(newIndex, order == RowActionOrder.AFTER)

                    val cells = if (order == RowActionOrder.AFTER) {
                        tailCells.fold(headCells) { acc2, cell ->
                            // Shift down
                            acc2.put(cell.component1() + 1, cell.component2())
                        }
                    } else {
                        headCells.fold(tailCells) { acc2, cell ->
                            // Shift up
                            acc2.put(cell.component1() - 1, cell.component2())
                        }
                    }

                    acc.put(columnHeader, cells.let {
                        val cellValue = ccm.component2().get(left.index)
                        if (cellValue != null) it.put(newIndex, cellValue) else it
                    })
                }

                ref.copy(
                    columns = columnsMap,
                    columnCells = columnCellMap
                )
            }

            // TODO Events
        }
    }
}

fun copy(left: Row, actionOrder: RowActionOrder, right: Row) = copy(RowToRowAction(left, right, actionOrder))

// ---

fun rename(column: Column, withName: ColumnHeader): Unit = move(column to column, withName)

fun rename(column: Column, vararg withName: String): Unit = move(column to column, *withName)

fun remove(table: Table) = Registry.deleteTable(table)

fun remove(column: Column) {
    // TODO Column remove event
    column.table.tableRef.updateAndGet {
        it.copy(
            columns = it.columns.remove(column.columnHeader),
            columnCells = it.columnCells.remove(column.columnHeader),
            version = it.version + 1L
        )
    }
}

// Note: We don't have remove(row) because that would imply shifting rows below up, same for cells
// TODO When move(t[1] after|before t[2]) is available, should have remove(row) too

fun clear(table: Table): Unit = TODO()

fun clear(column: Column): Unit = TODO()

fun clear(row: Row): Unit = TODO()

fun clear(cell: Cell<*>) = cell `=` null

fun clone(table: Table): Table = table.makeClone()

fun clone(table: Table, withName: String?): Table = table.makeClone(withName, true)

// ---

inline fun <reified O, reified N> on(table: Table, noinline init: TableEventReceiver<Table, O, N>.() -> Unit): TableListenerReference {
    return on(table, O::class, N::class, init as TableEventReceiver<Table, Any, Any>.() -> Unit)
}

fun on(table: Table, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Table, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Table, Any, Any>(table) { this }
        old == Any::class -> TableEventReceiver(table) {
            this.filter {
                new.isInstance(it.newValue.value)
            }
        }
        new == Any::class -> TableEventReceiver(table) {
            this.filter {
                old.isInstance(it.oldValue.value)
            }
        }
        else -> TableEventReceiver(table) {
            this.filter {
                old.isInstance(it.oldValue.value) && new.isInstance(it.newValue.value)
            }
        }
    }
    return table.eventProcessor.subscribe(table, eventReceiver, init)
}

// ---

inline fun <reified O, reified N> on(column: Column, noinline init: TableEventReceiver<Column, O, N>.() -> Unit): TableListenerReference {
    return on(column, O::class, N::class, init as TableEventReceiver<Column, Any, Any>.() -> Unit)
}

fun on(column: Column, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Column, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Column, Any, Any>(column) { this }
        old == Any::class -> TableEventReceiver(column) {
            this.filter {
                new.isInstance(it.newValue.value)
            }
        }
        new == Any::class -> TableEventReceiver(column) {
            this.filter {
                old.isInstance(it.oldValue.value)
            }
        }
        else -> TableEventReceiver(column) {
            this.filter {
                old.isInstance(it.oldValue.value) && new.isInstance(it.newValue.value)
            }
        }
    }
    return column.table.eventProcessor.subscribe(column, eventReceiver, init)
}

// ---

inline fun <reified O, reified N> on(row: Row, noinline init: TableEventReceiver<Row, O, N>.() -> Unit): TableListenerReference {
    return on(row, O::class, N::class, init as TableEventReceiver<Row, Any, Any>.() -> Unit)
}

fun on(row: Row, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Row, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Row, Any, Any>(row) { this }
        old == Any::class -> TableEventReceiver(row) {
            this.filter {
                new.isInstance(it.newValue.value)
            }
        }
        new == Any::class -> TableEventReceiver(row) {
            this.filter {
                old.isInstance(it.oldValue.value)
            }
        }
        else -> TableEventReceiver(row) {
            this.filter {
                old.isInstance(it.oldValue.value) && new.isInstance(it.newValue.value)
            }
        }
    }
    return row.table.eventProcessor.subscribe(row, eventReceiver, init)
}

// ---

inline fun <reified O, reified N> on(cellRange: CellRange, noinline init: TableEventReceiver<CellRange, O, N>.() -> Unit): TableListenerReference {
    return on(cellRange, O::class, N::class, init as TableEventReceiver<CellRange, Any, Any>.() -> Unit)
}

fun on(cellRange: CellRange, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<CellRange, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<CellRange, Any, Any>(cellRange) { this }
        old == Any::class -> TableEventReceiver(cellRange) {
            this.filter {
                new.isInstance(it.newValue.value)
            }
        }
        new == Any::class -> TableEventReceiver(cellRange) {
            this.filter {
                old.isInstance(it.oldValue.value)
            }
        }
        else -> TableEventReceiver(cellRange) {
            this.filter {
                old.isInstance(it.oldValue.value) && new.isInstance(it.newValue.value)
            }
        }
    }
    return cellRange.start.column.table.eventProcessor.subscribe(cellRange, eventReceiver, init)
}

// ---

inline fun <reified O, reified N> on(cell: Cell<*>, noinline init: TableEventReceiver<Cell<*>, O, N>.() -> Unit): TableListenerReference {
    return on(cell, O::class, N::class, init as TableEventReceiver<Cell<*>, Any, Any>.() -> Unit)
}

fun on(cell: Cell<*>, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Cell<*>, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Cell<*>, Any, Any>(cell) { this }
        old == Any::class -> TableEventReceiver(cell) {
            this.filter {
                new.isInstance(it.newValue.value)
            }
        }
        new == Any::class -> TableEventReceiver(cell) {
            this.filter {
                old.isInstance(it.oldValue.value)
            }
        }
        else -> TableEventReceiver(cell) {
            this.filter {
                old.isInstance(it.oldValue.value) && new.isInstance(it.newValue.value)
            }
        }
    }
    return cell.column.table.eventProcessor.subscribe(cell, eventReceiver, init)
}

// ---

fun off(reference: TableListenerReference) = reference.unsubscribe()

fun off(tableEventReceiver: TableEventReceiver<*, *, *>) = off(tableEventReceiver.reference)
