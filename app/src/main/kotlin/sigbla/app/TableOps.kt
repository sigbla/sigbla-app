package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.Registry
import sigbla.app.internals.load1
import sigbla.app.internals.refAction
import sigbla.app.internals.save1
import java.io.File
import java.util.*
import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.collection.TreeMap as PTreeMap
import kotlin.reflect.KClass

// TODO Refactor TableOps.kt into various files, like for column ops, rows ops, events, etc..?

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

        val indexes1 = oldRef.columnCells[column.header]?.keys() ?: emptySet()
        val indexes2 = newRef.columnCells[column.header]?.keys() ?: emptySet()
        val indexes = indexes1 union indexes2

        // Get columns anchored to old and new ref
        val oldColumn = BaseColumn(
            oldTable,
            column.header,
            oldRef.columns[column.header]?.columnOrder ?: column.order
        )
        val newColumn = BaseColumn(
            newTable,
            column.header,
            newRef.columns[column.header]?.columnOrder ?: column.order
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
        order == ColumnActionOrder.TO && left.table === right.table && left.header == newRight.header && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
            )
        }

        // move(T1["A"] to T1["A"], "B")        -> A is renamed to B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (A value, A order)
        order == ColumnActionOrder.TO && left.table === right.table && left.header == right.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T1["B"], "B")        -> A takes the place of B, named B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T2["B"], "B")        -> A takes the place of B, named B, in T2. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] to T1["B"], "A")        -> A takes the place of B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, B order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (unit value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && left.header == newRight.header
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
        order == ColumnActionOrder.TO && left.table !== right.table && right.header != newRight.header
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
        order == ColumnActionOrder.TO && left.table === right.table && left.header != right.header && right.header != newRight.header
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
        order == ColumnActionOrder.TO && left.table !== right.table && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] after T1["B"], "A")     -> A moved after B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table === right.table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] after T2["B"], "A")     -> A moved after B, named A. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["A"] (A value, A order) -> T2["A"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table !== right.table && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] after T1["B"], "B")     -> A moved after B, named B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.AFTER && left.table === right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] after T2["B"], "B")     -> A moved after B, named B. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.AFTER && left.table !== right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] after T1["B"], "C")     -> A moved after B, named C, removing any existing C
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T1["C"] (C value, C value) -> T1["C"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table === right.table && left.header != newRight.header && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] after T2["B"], "C")     -> A moved after B, named C, removing any existing C. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["C"] (C value, C value) -> T2["C"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table !== right.table && left.header != newRight.header && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] before T1["B"], "A")    -> A moved before B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table === right.table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] before T2["B"], "A")    -> A moved before B, named A. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["A"] (A value, A order) -> T2["A"] (T1["A"] value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] before T1["B"], "B")    -> A moved before B, named B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                      ->              T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.BEFORE && left.table === right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] before T2["B"], "B")    -> A moved before B, named B. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                      ->              T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] before T1["B"], "C")    -> A moved before B, named C, removing any existing C
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T1["C"] (C value, C value) -> T1["C"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table === right.table && left.header != newRight.header && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] before T2["B"], "C")    -> A moved before B, named C, removing any existing C. A removed from T1
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order)
        //                                                      T2["C"] (C value, C value) -> T2["C"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && left.header != newRight.header && right.header != newRight.header
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

        val indexes1 = oldRef.columnCells[column.header]?.keys() ?: emptySet()
        val indexes2 = newRef.columnCells[column.header]?.keys() ?: emptySet()
        val indexes = indexes1 union indexes2

        // Get columns anchored to old and new ref
        val oldColumn = BaseColumn(
            oldTable,
            column.header,
            oldRef.columns[column.header]?.columnOrder ?: column.order
        )
        val newColumn = BaseColumn(
            newTable,
            column.header,
            newRef.columns[column.header]?.columnOrder ?: column.order
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
        left.table === table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T1, "B")        -> A is moved to end of T1 and renamed to B
        //                                 -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                 T1["B"] (B value, B order) -> T1["B"] (A value, new B order)
        left.table === table && left.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t1OldRef, t1NewRef)
            )
        }

        // move(T1["A"] to T2, "A")        -> A is moved to end of T2
        //                                 -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                 T2["A"] (A value, A order) -> T2["A"] (A value, new A order)
        left.table !== table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(newRight, t2OldRef, t2NewRef)
            )
        }

        // move(T1["A"] to T2, "B")        -> A is moved to end of T2 and renamed to B. A removed from T1
        //                                 -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                 T2["B"] (B value, B order) -> T2["B"] (A value, new B order)
        left.table !== table && left.header != newRight.header
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

        val indexes1 = oldRef.columnCells[column.header]?.keys() ?: emptySet()
        val indexes2 = newRef.columnCells[column.header]?.keys() ?: emptySet()
        val indexes = indexes1 union indexes2

        // Get columns anchored to old and new ref
        val oldColumn = BaseColumn(
            oldTable,
            column.header,
            oldRef.columns[column.header]?.columnOrder ?: column.order
        )
        val newColumn = BaseColumn(
            newTable,
            column.header,
            newRef.columns[column.header]?.columnOrder ?: column.order
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
        order == ColumnActionOrder.TO && left.table === right.table && left.header == newRight.header && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef),
            )
        }

        // copy(T1["A"] to T1["A"], "B")        -> A is renamed to B
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (unit value, A order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (A value, A order)
        order == ColumnActionOrder.TO && left.table === right.table && left.header == right.header
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef),
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T1["B"], "B")        -> A takes the place of B, named B
        //                                      -> Event model: T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T2["B"], "B")        -> A takes the place of B, named B, in T2
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T1["B"], "A")        -> A takes the place of B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, B order),
        //                                                      T1["B"] (B value, B order) -> T1["B"] (unit value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef),
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T2["B"], "A")        -> A takes the place of B, named A
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (unit value, B order)
        //                                                      T2["A"] (A value, A order) -> T2["A"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef),
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T1["B"], "C")        -> A takes the place of B, named C, removing any existing C
        //                                      -> Event model: T1["B"] (B value, B order) -> T1["B"] (unit value, B order),
        //                                                      T1["C"] (C value, C order) -> T1["C"] (A value, B order)
        order == ColumnActionOrder.TO && left.table === right.table && left.header != right.header && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef),
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T2["B"], "C")        -> A takes the place of B, named C, removing any existing C
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (unit value, B order),
        //                                                      T2["C"] (C value, C order) -> T2["C"] (A value, B order)
        order == ColumnActionOrder.TO && left.table !== right.table && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef),
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T1["B"], "A")     -> A copied after B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table === right.table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T2["B"], "A")     -> A copied after B, named A
        //                                      -> Event model: T2["A"] (A value, A order) -> T2["A"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table !== right.table && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T1["B"], "B")     -> A copied after B, named B
        //                                      -> Event model: T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.AFTER && left.table === right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T2["B"], "B")     -> A copied after B, named B
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.AFTER && left.table !== right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T1["B"], "C")     -> A copied after B, named C, removing any existing C
        //                                      -> Event model: T1["C"] (C value, C value) -> T1["C"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table === right.table && left.header != newRight.header && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] after T2["B"], "C")     -> A copied after B, named C, removing any existing C
        //                                      -> Event model: T2["C"] (C value, C value) -> T2["C"] (A value, new A order after B)
        order == ColumnActionOrder.AFTER && left.table !== right.table && left.header != newRight.header && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T1["B"], "A")    -> A copied before B, named A
        //                                      -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table === right.table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T2["B"], "A")    -> A copied before B, named A
        //                                      -> Event model: T2["A"] (A value, A order) -> T2["A"] (T1["A"] value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T1["B"], "B")    -> A copied before B, named B
        //                                      -> Event model: T1["B"] (B value, B order) -> T1["B"] (A value, B order)
        order == ColumnActionOrder.BEFORE && left.table === right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T2["B"], "B")    -> A copied before B, named B
        //                                      -> Event model: T2["B"] (B value, B order) -> T2["B"] (A value, B order)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && right.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T1["B"], "C")    -> A copied before B, named C, removing any existing C
        //                                      -> Event model: T1["C"] (C value, C value) -> T1["C"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table === right.table && left.header != newRight.header && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] before T2["B"], "C")    -> A copied before B, named C, removing any existing C
        //                                      -> Event model: T2["C"] (C value, C value) -> T2["C"] (A value, new A order before B)
        order == ColumnActionOrder.BEFORE && left.table !== right.table && left.header != newRight.header && right.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // Should never happen
        else -> throw UnsupportedOperationException()
    }
}

private fun publishTableCopyEvents(
    left: Column,
    table: Table,
    newRight: Column,
    oldRef: TableRef,
    newRef: TableRef
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

        val indexes1 = oldRef.columnCells[column.header]?.keys() ?: emptySet()
        val indexes2 = newRef.columnCells[column.header]?.keys() ?: emptySet()
        val indexes = indexes1 union indexes2

        // Get columns anchored to old and new ref
        val oldColumn = BaseColumn(
            oldTable,
            column.header,
            oldRef.columns[column.header]?.columnOrder ?: column.order
        )
        val newColumn = BaseColumn(
            newTable,
            column.header,
            newRef.columns[column.header]?.columnOrder ?: column.order
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
        // copy(T1["A"] to T1, "A")        -> A is moved to end of T1
        //                                 -> Event model: T1["A"] (A value, A order) -> T1["A"] (A value, new A order)
        left.table === table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T1, "B")        -> A is copied to end of T1 and renamed to B
        //                                 -> Event model: T1["B"] (B value, B order) -> T1["B"] (A value, new B order)
        left.table === table && left.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T2, "A")        -> A is copied to end of T2
        //                                 -> Event model: T2["A"] (A value, A order) -> T2["A"] (A value, new A order)
        left.table !== table && left.header == newRight.header
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // copy(T1["A"] to T2, "B")        -> A is copied to end of T2 and renamed to B
        //                                 -> Event model: T2["B"] (B value, B order) -> T2["B"] (A value, new B order)
        left.table !== table && left.header != newRight.header
        -> {
            publishEvents(
                prepareEvents(newRight, oldRef, newRef)
            )
        }

        // Should never happen
        else -> throw UnsupportedOperationException()
    }
}

private fun publishRowMoveEvents(
    left: Row,
    right: Row,
    order: RowActionOrder,
    t1OldRef: TableRef,
    t1NewRef: TableRef,
    t2OldRef: TableRef,
    t2NewRef: TableRef
) {
    if (!left.table.eventProcessor.haveListeners()
        && !right.table.eventProcessor.haveListeners()) return

    fun prepareEvents(row: Row, oldRef: TableRef, newRef: TableRef): Pair<Table, MutableList<TableListenerEvent<Any, Any>>> {
        // We need to do this in order to disconnect the columns from the original table
        val oldTable = row.table.makeClone(ref = oldRef)
        val newTable = row.table.makeClone(ref = newRef)

        val oldRef = oldTable.tableRef.get()
        val newRef = newTable.tableRef.get()

        val columnHeaders = oldRef.headers.fold(LinkedHashMap<ColumnHeader, ColumnMeta>()) { acc, pair ->
            acc[pair.first] = pair.second
            acc
        }.apply {
            newRef.headers.filter { !this.containsKey(it.first) }.forEach { this[it.first] = it.second }
        }.entries.sortedWith { a, b ->
            if (a.value.columnOrder == b.value.columnOrder) a.key.compareTo(b.key)
            else a.value.columnOrder.compareTo(b.value.columnOrder)
        }

        // Get columns anchored to old and new ref
        val columns = columnHeaders.map { (columnHeader, columnMeta) ->
            val oldColumn = BaseColumn(
                oldTable,
                columnHeader,
                columnMeta.columnOrder
            )
            val newColumn = BaseColumn(
                newTable,
                columnHeader,
                columnMeta.columnOrder
            )

            Pair(oldColumn, newColumn)
        }

        val events = columns.map {
            val old = it.first[row]
            val new = it.second[old.index]
            TableListenerEvent(old, new) as TableListenerEvent<Any, Any>
        }

        return row.table to events.toMutableList()
    }

    fun prepareEventsAfter(right: Row, indexes: SortedSet<Long>, oldRef: TableRef, newRef: TableRef): Pair<Table, MutableList<TableListenerEvent<Any, Any>>> {
        // We need to do this in order to disconnect the columns from the original table
        val oldTable = right.table.makeClone(ref = oldRef)
        val newTable = right.table.makeClone(ref = newRef)

        val oldRef = oldTable.tableRef.get()
        val newRef = newTable.tableRef.get()

        val columnHeaders = oldRef.headers.fold(LinkedHashMap<ColumnHeader, ColumnMeta>()) { acc, pair ->
            acc[pair.first] = pair.second
            acc
        }.apply {
            newRef.headers.filter { !this.containsKey(it.first) }.forEach { this[it.first] = it.second }
        }.entries.sortedWith { a, b ->
            if (a.value.columnOrder == b.value.columnOrder) a.key.compareTo(b.key)
            else a.value.columnOrder.compareTo(b.value.columnOrder)
        }

        // Get columns anchored to old and new ref
        val columns = columnHeaders.map { (columnHeader, columnMeta) ->
            val oldColumn = BaseColumn(
                oldTable,
                columnHeader,
                columnMeta.columnOrder
            )
            val newColumn = BaseColumn(
                newTable,
                columnHeader,
                columnMeta.columnOrder
            )

            Pair(oldColumn, newColumn)
        }

        val events = columns.map { oldNewColumn ->
            val oldColumn = oldNewColumn.first
            val newColumn = oldNewColumn.second

            indexes.map { index ->
                val old = oldColumn[index]
                val new = newColumn[index]
                TableListenerEvent(old, new) as TableListenerEvent<Any, Any>
            }
        }.flatten()

        return right.table to events.toMutableList()
    }

    fun prepareEventsBefore(right: Row, indexes: SortedSet<Long>, oldRef: TableRef, newRef: TableRef): Pair<Table, MutableList<TableListenerEvent<Any, Any>>> {
        // We need to do this in order to disconnect the columns from the original table
        val oldTable = right.table.makeClone(ref = oldRef)
        val newTable = right.table.makeClone(ref = newRef)

        val oldRef = oldTable.tableRef.get()
        val newRef = newTable.tableRef.get()

        val columnHeaders = oldRef.headers.fold(LinkedHashMap<ColumnHeader, ColumnMeta>()) { acc, pair ->
            acc[pair.first] = pair.second
            acc
        }.apply {
            newRef.headers.filter { !this.containsKey(it.first) }.forEach { this[it.first] = it.second }
        }.entries.sortedWith { a, b ->
            if (a.value.columnOrder == b.value.columnOrder) a.key.compareTo(b.key)
            else a.value.columnOrder.compareTo(b.value.columnOrder)
        }

        // Get columns anchored to old and new ref
        val columns = columnHeaders.map { (columnHeader, columnMeta) ->
            val oldColumn = BaseColumn(
                oldTable,
                columnHeader,
                columnMeta.columnOrder
            )
            val newColumn = BaseColumn(
                newTable,
                columnHeader,
                columnMeta.columnOrder
            )

            Pair(oldColumn, newColumn)
        }

        val events = columns.map { oldNewColumn ->
            val oldColumn = oldNewColumn.first
            val newColumn = oldNewColumn.second

            indexes.map { index ->
                val old = oldColumn[index]
                val new = newColumn[index]
                TableListenerEvent(old, new) as TableListenerEvent<Any, Any>
            }
        }.flatten()

        return right.table to events.toMutableList()
    }

    fun publishEvents(vararg tableEvents: Pair<Table, MutableList<TableListenerEvent<Any, Any>>>) {
        val groupedEvents = IdentityHashMap<Table, MutableList<TableListenerEvent<Any, Any>>>()

        tableEvents.forEach {
            groupedEvents.compute(it.first) { _, v -> v?.apply { v.addAll(it.second) } ?: it.second }
        }

        groupedEvents.forEach { (t, e) -> t.eventProcessor.publish(e) }
    }

    when {
        // move(T1[1] to T1[1])        -> No-op move, will produce events for that row
        order == RowActionOrder.TO && left.table === right.table && left == right
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef)
            )
        }

        // move(T1[1] to T1[2])        -> Row 1 replaces row 2
        //                             -> Event model: T1[1] (1 value, 1 order) -> T1[1] (unit value, A order),
        //                                             T1[2] (2 value, 2 order) -> T1[2] (1 value, 1 order)
        order == RowActionOrder.TO && left.table === right.table && left != right
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t1OldRef, t1NewRef)
            )
        }

        // move(T1[1] to T2[2])        -> Row 1 replaces row 2, in T2. Row 1 removed from T1
        //                             -> Event model: T1[1] (1 value, 1 order) -> T1[1] (unit value, 1 order),
        //                                             T2[2] (2 value, 2 order) -> T2[2] (1 value, 1 order)
        order == RowActionOrder.TO && left.table !== right.table
        -> {
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEvents(right, t2OldRef, t2NewRef)
            )
        }

        // move(T1[1] after T1[2])     -> Row 1 moved after row 2, named row 3
        //                             -> Event model: T1[1] (1 value, 1 order) -> T1[1] (unit value, 1 order),
        //                                             T1[3] (3 value, 3 order) -> T1[3] (1 value, 1 order),
        //                                             T1[4] (4 value, 4 order) -> T1[4] (3 value, 3 order),
        //                                             ...,
        //                                             T1[N] (N value, N order) -> T1[N] (N-1 value, N-1 order)
        order == RowActionOrder.AFTER && left.table === right.table
        -> {
            val t2Indexes = (t2OldRef.indexes.filter { it > right.index }.toSet() union t2NewRef.indexes.filter { it > right.index }.toSet()).toSortedSet()
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef).let { (table, events) ->
                    // Remove any event overlap
                    events.removeIf { t2Indexes.contains(it.oldValue.index) }
                    Pair(table, events)
                },
                prepareEventsAfter(right, t2Indexes, t2OldRef, t2NewRef)
            )
        }

        // move(T1[1] after T2[2])     -> Row 1 moved after row 2, in T2, named row 3. Row 1 removed from T1
        //                             -> Event model: T1[1] (1 value, 1 order) -> T1[1] (unit value, 1 order),
        //                                             T2[3] (3 value, 3 order) -> T2[3] (1 value, 1 order),
        //                                             T2[4] (4 value, 4 order) -> T2[4] (3 value, 3 order),
        //                                             ...,
        //                                             T2[N] (N value, N order) -> T2[N] (N-1 value, N-1 order)
        order == RowActionOrder.AFTER && left.table !== right.table
        -> {
            val t2Indexes = (t2OldRef.indexes.filter { it > right.index }.toSet() union t2NewRef.indexes.filter { it > right.index }.toSet()).toSortedSet()
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEventsAfter(right, t2Indexes, t2OldRef, t2NewRef)
            )
        }

        // move(T1[5] before T1[3])    -> Row 5 moved before row 3, named row 2
        //                             -> Event model: T1[5] (5 value, 5 order) -> T1[5] (unit value, 5 order),
        //                                             T1[2] (2 value, 2 order) -> T1[2] (5 value, 5 order),
        //                                             T1[1] (1 value, 1 order) -> T1[1] (2 value, 2 order),
        //                                             T1[0] (0 value, 0 order) -> T1[0] (1 value, 1 order),
        //                                             T1[-1] (-1 value, -1 order) -> T1[-1] (N-1 value, N-1 order)
        //                                             ...,
        //                                             T1[N] (N value, N order) -> T1[N] (N+1 value, N+1 order)
        order == RowActionOrder.BEFORE && left.table === right.table
        -> {
            val t2Indexes = (t2OldRef.indexes.filter { it < right.index }.toSet() union t2NewRef.indexes.filter { it < right.index }.toSet()).toSortedSet()
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef).let { (table, events) ->
                    // Remove any event overlap
                    events.removeIf { t2Indexes.contains(it.oldValue.index) }
                    Pair(table, events)
                },
                prepareEventsBefore(right, t2Indexes, t2OldRef, t2NewRef)
            )
        }

        // move(T1[5] before T2[3])    -> Row 5 moved before row 3, in T2, named row 2. Row 1 removed from T1
        //                             -> Event model: T1[5] (5 value, 5 order) -> T1[5] (unit value, 5 order),
        //                                             T2[2] (2 value, 2 order) -> T2[2] (5 value, 5 order),
        //                                             T2[1] (1 value, 1 order) -> T2[1] (2 value, 2 order),
        //                                             T2[0] (0 value, 0 order) -> T2[0] (1 value, 1 order),
        //                                             T2[-1] (-1 value, -1 order) -> T2[-1] (N-1 value, N-1 order)
        //                                             ...,
        //                                              T2[N] (N value, N order) -> T2[N] (N+1 value, N+1 order)
        order == RowActionOrder.BEFORE && left.table !== right.table
        -> {
            val t2Indexes = (t2OldRef.indexes.filter { it < right.index }.toSet() union t2NewRef.indexes.filter { it < right.index }.toSet()).toSortedSet()
            publishEvents(
                prepareEvents(left, t1OldRef, t1NewRef),
                prepareEventsBefore(right, t2Indexes, t2OldRef, t2NewRef)
            )
        }
    }
}

private fun publishRowCopyEvents(
    left: Row,
    right: Row,
    order: RowActionOrder,
    oldRef: TableRef,
    newRef: TableRef
) {
    if (!left.table.eventProcessor.haveListeners()
        && !right.table.eventProcessor.haveListeners()) return

    fun prepareEvents(row: Row, oldRef: TableRef, newRef: TableRef): Pair<Table, MutableList<TableListenerEvent<Any, Any>>> {
        // We need to do this in order to disconnect the columns from the original table
        val oldTable = row.table.makeClone(ref = oldRef)
        val newTable = row.table.makeClone(ref = newRef)

        val oldRef = oldTable.tableRef.get()
        val newRef = newTable.tableRef.get()

        val columnHeaders = oldRef.headers.fold(LinkedHashMap<ColumnHeader, ColumnMeta>()) { acc, pair ->
            acc[pair.first] = pair.second
            acc
        }.apply {
            newRef.headers.filter { !this.containsKey(it.first) }.forEach { this[it.first] = it.second }
        }.entries.sortedWith { a, b ->
            if (a.value.columnOrder == b.value.columnOrder) a.key.compareTo(b.key)
            else a.value.columnOrder.compareTo(b.value.columnOrder)
        }

        // Get columns anchored to old and new ref
        val columns = columnHeaders.map { (columnHeader, columnMeta) ->
            val oldColumn = BaseColumn(
                oldTable,
                columnHeader,
                columnMeta.columnOrder
            )
            val newColumn = BaseColumn(
                newTable,
                columnHeader,
                columnMeta.columnOrder
            )

            Pair(oldColumn, newColumn)
        }

        val events = columns.map {
            val old = it.first[row]
            val new = it.second[old.index]
            TableListenerEvent(old, new) as TableListenerEvent<Any, Any>
        }

        return row.table to events.toMutableList()
    }

    fun prepareEventsAfter(right: Row, indexes: SortedSet<Long>, oldRef: TableRef, newRef: TableRef): Pair<Table, MutableList<TableListenerEvent<Any, Any>>> {
        // We need to do this in order to disconnect the columns from the original table
        val oldTable = right.table.makeClone(ref = oldRef)
        val newTable = right.table.makeClone(ref = newRef)

        val oldRef = oldTable.tableRef.get()
        val newRef = newTable.tableRef.get()

        val columnHeaders = oldRef.headers.fold(LinkedHashMap<ColumnHeader, ColumnMeta>()) { acc, pair ->
            acc[pair.first] = pair.second
            acc
        }.apply {
            newRef.headers.filter { !this.containsKey(it.first) }.forEach { this[it.first] = it.second }
        }.entries.sortedWith { a, b ->
            if (a.value.columnOrder == b.value.columnOrder) a.key.compareTo(b.key)
            else a.value.columnOrder.compareTo(b.value.columnOrder)
        }

        // Get columns anchored to old and new ref
        val columns = columnHeaders.map { (columnHeader, columnMeta) ->
            val oldColumn = BaseColumn(
                oldTable,
                columnHeader,
                columnMeta.columnOrder
            )
            val newColumn = BaseColumn(
                newTable,
                columnHeader,
                columnMeta.columnOrder
            )

            Pair(oldColumn, newColumn)
        }

        val events = columns.map { oldNewColumn ->
            val oldColumn = oldNewColumn.first
            val newColumn = oldNewColumn.second

            indexes.map { index ->
                val old = oldColumn[index]
                val new = newColumn[index]
                TableListenerEvent(old, new) as TableListenerEvent<Any, Any>
            }
        }.flatten()

        return right.table to events.toMutableList()
    }

    fun prepareEventsBefore(right: Row, indexes: SortedSet<Long>, oldRef: TableRef, newRef: TableRef): Pair<Table, MutableList<TableListenerEvent<Any, Any>>> {
        // We need to do this in order to disconnect the columns from the original table
        val oldTable = right.table.makeClone(ref = oldRef)
        val newTable = right.table.makeClone(ref = newRef)

        val oldRef = oldTable.tableRef.get()
        val newRef = newTable.tableRef.get()

        val columnHeaders = oldRef.headers.fold(LinkedHashMap<ColumnHeader, ColumnMeta>()) { acc, pair ->
            acc[pair.first] = pair.second
            acc
        }.apply {
            newRef.headers.filter { !this.containsKey(it.first) }.forEach { this[it.first] = it.second }
        }.entries.sortedWith { a, b ->
            if (a.value.columnOrder == b.value.columnOrder) a.key.compareTo(b.key)
            else a.value.columnOrder.compareTo(b.value.columnOrder)
        }

        // Get columns anchored to old and new ref
        val columns = columnHeaders.map { (columnHeader, columnMeta) ->
            val oldColumn = BaseColumn(
                oldTable,
                columnHeader,
                columnMeta.columnOrder
            )
            val newColumn = BaseColumn(
                newTable,
                columnHeader,
                columnMeta.columnOrder
            )

            Pair(oldColumn, newColumn)
        }

        val events = columns.map { oldNewColumn ->
            val oldColumn = oldNewColumn.first
            val newColumn = oldNewColumn.second

            indexes.map { index ->
                val old = oldColumn[index]
                val new = newColumn[index]
                TableListenerEvent(old, new) as TableListenerEvent<Any, Any>
            }
        }.flatten()

        return right.table to events.toMutableList()
    }

    fun publishEvents(vararg tableEvents: Pair<Table, MutableList<TableListenerEvent<Any, Any>>>) {
        val groupedEvents = IdentityHashMap<Table, MutableList<TableListenerEvent<Any, Any>>>()

        tableEvents.forEach {
            groupedEvents.compute(it.first) { _, v -> v?.apply { v.addAll(it.second) } ?: it.second }
        }

        groupedEvents.forEach { (t, e) -> t.eventProcessor.publish(e) }
    }

    when {
        // copy(T1[1] to T1[1])        -> No-op move, will produce events for that row
        order == RowActionOrder.TO && left.table === right.table && left == right
        -> {
            publishEvents(
                prepareEvents(left, oldRef, newRef)
            )
        }

        // copy(T1[1] to T1[2])        -> Row 1 is copied to row 2
        //                             -> Event model: T1[2] (2 value, 2 order) -> T1[2] (1 value, 1 order)
        order == RowActionOrder.TO && left.table === right.table && left != right
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1[1] to T2[2])        -> Row 1 is copied to row 2, in T2
        //                             -> Event model: T2[2] (2 value, 2 order) -> T2[2] (1 value, 1 order)
        order == RowActionOrder.TO && left.table !== right.table
        -> {
            publishEvents(
                prepareEvents(right, oldRef, newRef)
            )
        }

        // copy(T1[1] after T1[2])     -> Row 1 is copied to after row 2, named row 3
        //                             -> Event model: T1[3] (3 value, 3 order) -> T1[3] (1 value, 1 order),
        //                                             T1[4] (4 value, 4 order) -> T1[4] (3 value, 3 order),
        //                                             ...,
        //                                             T1[N] (N value, N order) -> T1[N] (N-1 value, N-1 order)
        order == RowActionOrder.AFTER && left.table === right.table
        -> {
            val indexes = (oldRef.indexes.filter { it > right.index }.toSet() union newRef.indexes.filter { it > right.index }.toSet()).toSortedSet()
            publishEvents(
                prepareEventsAfter(right, indexes, oldRef, newRef)
            )
        }

        // copy(T1[1] after T2[2])     -> Row 1 is copied to after row 2, in T2, named row 3
        //                             -> Event model: T2[3] (3 value, 3 order) -> T2[3] (1 value, 1 order),
        //                                             T2[4] (4 value, 4 order) -> T2[4] (3 value, 3 order),
        //                                             ...,
        //                                             T2[N] (N value, N order) -> T2[N] (N-1 value, N-1 order)
        order == RowActionOrder.AFTER && left.table !== right.table
        -> {
            val indexes = (oldRef.indexes.filter { it > right.index }.toSet() union newRef.indexes.filter { it > right.index }.toSet()).toSortedSet()
            publishEvents(
                prepareEventsAfter(right, indexes, oldRef, newRef)
            )
        }

        // copy(T1[5] before T1[3])    -> Row 5 is copied to before row 3, named row 2
        //                             -> Event model: T1[2] (2 value, 2 order) -> T1[2] (5 value, 5 order),
        //                                             T1[1] (1 value, 1 order) -> T1[1] (2 value, 2 order),
        //                                             T1[0] (0 value, 0 order) -> T1[0] (1 value, 1 order),
        //                                             T1[-1] (-1 value, -1 order) -> T1[-1] (N-1 value, N-1 order)
        //                                             ...,
        //                                             T1[N] (N value, N order) -> T1[N] (N+1 value, N+1 order)
        order == RowActionOrder.BEFORE && left.table === right.table
        -> {
            val indexes = (oldRef.indexes.filter { it < right.index }.toSet() union newRef.indexes.filter { it < right.index }.toSet()).toSortedSet()
            publishEvents(
                prepareEventsBefore(right, indexes, oldRef, newRef)
            )
        }

        // copy(T1[5] before T2[3])    -> Row 5 is copied to before row 3, in T2, named row 2
        //                             -> Event model: T2[2] (2 value, 2 order) -> T2[2] (5 value, 5 order),
        //                                             T2[1] (1 value, 1 order) -> T2[1] (2 value, 2 order),
        //                                             T2[0] (0 value, 0 order) -> T2[0] (1 value, 1 order),
        //                                             T2[-1] (-1 value, -1 order) -> T2[-1] (N-1 value, N-1 order)
        //                                             ...,
        //                                              T2[N] (N value, N order) -> T2[N] (N+1 value, N+1 order)
        order == RowActionOrder.BEFORE && left.table !== right.table
        -> {
            val indexes = (oldRef.indexes.filter { it < right.index }.toSet() union newRef.indexes.filter { it < right.index }.toSet()).toSortedSet()
            publishEvents(
                prepareEventsBefore(right, indexes, oldRef, newRef)
            )
        }
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

    synchronized(left.table.eventProcessor) {
        synchronized(right.table.eventProcessor) {
            if (left.table === right.table) {
                // Internal move
                val newRight = BaseColumn(left.table, withName)
                val (oldRef, newRef) = left.table.tableRef.refAction(
                    (::columnMove)(left.header, right.header, order, withName) {
                        copy(
                            columns = this.columns.put(withName, ColumnMeta(newRight.order, this.columns[left.header]?.prenatal ?: throw InvalidColumnException(left))),
                            columnCells = this.columnCells.put(withName, this.columnCells[left.header] ?: PTreeMap())
                        )
                    }
                )

                publishColumnMoveEvents(left, right, newRight, order, oldRef, newRef, oldRef, newRef)
            } else {
                // Move between tables
                val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
                    ref.copy(
                        columns = ref.columns.remove(left.header),
                        columnCells = ref.columnCells.remove(left.header),
                        version = ref.version + 1L
                    )
                }

                val newRight = BaseColumn(right.table, withName)
                val (oldRef2, newRef2) = right.table.tableRef.refAction(
                    (::columnMove)(newRight.header, right.header, order, withName) {
                        copy(
                            columns = this.columns.put(withName, ColumnMeta(newRight.order, oldRef1.columns[left.header]?.prenatal ?: throw InvalidColumnException(left))),
                            columnCells = this.columnCells.put(withName, oldRef1.columnCells[left.header] ?: PTreeMap())
                        )
                    }
                )

                publishColumnMoveEvents(left, right, newRight, order, oldRef1, newRef1, oldRef2, newRef2)
            }
        }
    }
}

fun move(columnToColumnAction: ColumnToColumnAction, vararg withName: String) = move(columnToColumnAction, ColumnHeader(*withName))

fun move(columnToColumnAction: ColumnToColumnAction) = move(columnToColumnAction, if (columnToColumnAction.order == ColumnActionOrder.TO) columnToColumnAction.right.header else columnToColumnAction.left.header)

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

    synchronized(left.table.eventProcessor) {
        synchronized(table.eventProcessor) {
            if (left.table === table) {
                // Internal move
                val newRight = BaseColumn(table, withName)
                val (oldRef, newRef) = table.tableRef.refAction(
                    (::columnMove)(withName) {
                        copy(
                            columns = this.columns.remove(left.header).put(withName, ColumnMeta(newRight.order, this.columns[left.header]?.prenatal ?: throw InvalidColumnException(left))),
                            columnCells = this.columnCells.remove(left.header).put(withName, this.columnCells[left.header] ?: PTreeMap()),
                        )
                    }
                )

                publishTableMoveEvents(left, table, newRight, oldRef, newRef, oldRef, newRef)
            } else {
                // Move between tables
                val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
                    ref.copy(
                        columns = ref.columns.remove(left.header),
                        columnCells = ref.columnCells.remove(left.header),
                        version = ref.version + 1L
                    )
                }

                val newRight = BaseColumn(table, withName)
                val (oldRef2, newRef2) = table.tableRef.refAction(
                    (::columnMove)(withName) {
                        copy(
                            columns = this.columns.put(withName, ColumnMeta(newRight.order, oldRef1.columns[left.header]?.prenatal ?: throw InvalidColumnException(left))),
                            columnCells = this.columnCells.put(withName, oldRef1.columnCells[left.header] ?: PTreeMap()),
                        )
                    }
                )

                publishTableMoveEvents(left, table, newRight, oldRef1, newRef1, oldRef2, newRef2)
            }
        }
    }
}

fun move(columnToTableAction: ColumnToTableAction, vararg withName: String) = move(columnToTableAction, ColumnHeader(*withName))

fun move(columnToTableAction: ColumnToTableAction) = move(columnToTableAction, columnToTableAction.left.header)

fun move(left: Column, table: Table, withName: ColumnHeader) = move(ColumnToTableAction(left, table), withName)

fun move(left: Column, table: Table, vararg withName: String) = move(ColumnToTableAction(left, table), *withName)

fun move(left: Column, table: Table) = move(ColumnToTableAction(left, table), left.header)

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

    synchronized(left.table.eventProcessor) {
        synchronized(right.table.eventProcessor) {
            if (left.table === right.table) {
                // Internal copy
                val newRight = BaseColumn(left.table, withName)
                val (oldRef, newRef) = left.table.tableRef.refAction(
                    (::columnCopy)(left.header, right.header, order, withName) {
                        copy(
                            columns = this.columns.put(withName, ColumnMeta(newRight.order, this.columns[left.header]?.prenatal ?: throw InvalidColumnException(left))),
                            columnCells = this.columnCells.put(withName, this.columnCells[left.header] ?: PTreeMap())
                        )
                    }
                )

                publishColumnCopyEvents(left, right, newRight, order, oldRef, newRef)
            } else {
                // Copy between tables
                val newRight = BaseColumn(right.table, withName)
                val (oldRef, newRef) = right.table.tableRef.refAction(
                    (::columnCopy)(newRight.header, right.header, order, withName) {
                        val leftRef = left.table.tableRef.get()
                        copy(
                            columns = this.columns.put(withName, ColumnMeta(newRight.order, leftRef.columns[left.header]?.prenatal ?: throw InvalidColumnException(left))),
                            columnCells = this.columnCells.put(withName, leftRef.columnCells[left.header] ?: PTreeMap())
                        )
                    }
                )

                publishColumnCopyEvents(left, right, newRight, order, oldRef, newRef)
            }
        }
    }
}

fun copy(columnToColumnAction: ColumnToColumnAction, vararg withName: String) = copy(columnToColumnAction, ColumnHeader(*withName))

fun copy(columnToColumnAction: ColumnToColumnAction) = copy(columnToColumnAction, if (columnToColumnAction.order == ColumnActionOrder.TO) columnToColumnAction.right.header else columnToColumnAction.left.header)

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

    synchronized(left.table.eventProcessor) {
        synchronized(table.eventProcessor) {
            if (left.table === table) {
                // Internal copy
                val newRight = BaseColumn(table, withName)
                val (oldRef, newRef) = table.tableRef.refAction(
                    (::columnCopy)(withName) {
                        copy(
                            columns = this.columns.put(withName, ColumnMeta(newRight.order, this.columns[left.header]?.prenatal ?: throw InvalidColumnException(left))),
                            columnCells = this.columnCells.put(withName, this.columnCells[left.header] ?: PTreeMap()),
                        )
                    }
                )

                publishTableCopyEvents(left, table, newRight, oldRef, newRef)
            } else {
                // Copy between tables
                val newRight = BaseColumn(table, withName)
                val (oldRef, newRef) = table.tableRef.refAction(
                    (::columnCopy)(withName) {
                        val leftRef = left.table.tableRef.get()
                        copy(
                            columns = this.columns.put(withName, ColumnMeta(newRight.order, leftRef.columns[left.header]?.prenatal ?: throw InvalidColumnException(left))),
                            columnCells = this.columnCells.put(withName, left.table.tableRef.get().columnCells[left.header] ?: PTreeMap()),
                        )
                    }
                )

                publishTableCopyEvents(left, table, newRight, oldRef, newRef)
            }
        }
    }
}

fun copy(columnToTableAction: ColumnToTableAction, vararg withName: String) = copy(columnToTableAction, ColumnHeader(*withName))

fun copy(columnToTableAction: ColumnToTableAction) = copy(columnToTableAction, columnToTableAction.left.header)

fun copy(left: Column, table: Table, withName: ColumnHeader) = copy(ColumnToTableAction(left, table), withName)

fun copy(left: Column, table: Table, vararg withName: String) = copy(ColumnToTableAction(left, table), *withName)

fun copy(left: Column, table: Table) = copy(ColumnToTableAction(left, table), left.header)

// ---

fun move(rowToRowAction: RowToRowAction) {
    val left = rowToRowAction.left
    val right = rowToRowAction.right
    val order = rowToRowAction.order

    // TODO Handle index relation on right side..?
    if (right.indexRelation != IndexRelation.AT) throw UnsupportedOperationException("Only supporting IndexRelation.AT on right side")

    synchronized(left.table.eventProcessor) {
        synchronized(right.table.eventProcessor) {
            if (order == RowActionOrder.TO) {
                if (left.table === right.table) {
                    // Internal move
                    val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                        ref.copy(
                            columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                                val columnHeader = ccm.component1()
                                getCellRaw(ref, columnHeader, left.index, left.indexRelation)?.let { (cell, index) ->
                                    acc.put(columnHeader, ccm.component2().remove(index).put(right.index, cell))
                                } ?: acc
                            },
                            version = ref.version + 1L
                        )
                    }

                    publishRowMoveEvents(left, right, order, oldRef, newRef, oldRef, newRef)
                } else {
                    // Move between tables
                    val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
                        ref.copy(
                            columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                                val columnHeader = ccm.component1()
                                getCellRaw(ref, columnHeader, left.index, left.indexRelation)?.let { (_, index) ->
                                    acc.put(columnHeader, ccm.component2().remove(index))
                                } ?: acc
                            },
                            version = ref.version + 1L
                        )
                    }

                    val (oldRef2, newRef2) = right.table.tableRef.refAction { ref ->
                        val columnsMap = oldRef1
                            .columns
                            .sortedBy { it.component2().columnOrder }
                            .fold(ref.columns) { acc, (c, cm) ->
                                if (acc.containsKey(c)) acc else acc.put(c, ColumnMeta(BaseColumn(right.table, c).order, cm.prenatal))
                            }

                        val columnCellMap = oldRef1.columnCells.fold(ref.columnCells) { acc, ccm ->
                            val columnHeader = ccm.component1()
                            getCellRaw(oldRef1, columnHeader, left.index, left.indexRelation)?.let { (cell, _) ->
                                acc.put(columnHeader, (acc.get(columnHeader) ?: PTreeMap()).put(right.index, cell))
                            } ?: acc
                        }

                        ref.copy(
                            columns = columnsMap,
                            columnCells = columnCellMap,
                            version = ref.version + 1L
                        )
                    }

                    publishRowMoveEvents(left, right, order, oldRef1, newRef1, oldRef2, newRef2)
                }
            } else {
                if (left.table === right.table) {
                    // Internal move
                    val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                        ref.copy(
                            columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                                val columnHeader = ccm.component1()
                                getCellRaw(ref, columnHeader, left.index, left.indexRelation)?.let { (cell, index) ->
                                    val newIndex = if (order == RowActionOrder.AFTER) right.index + 1 else right.index - 1

                                    val withoutMoved = ccm.component2().remove(index)
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

                                    acc.put(columnHeader, cells.put(newIndex, cell))
                                } ?: acc
                            },
                            version = ref.version + 1L
                        )
                    }

                    publishRowMoveEvents(left, right, order, oldRef, newRef, oldRef, newRef)
                } else {
                    // Move between tables
                    val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
                        ref.copy(
                            columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                                val columnHeader = ccm.component1()
                                getCellRaw(ref, columnHeader, left.index, left.indexRelation)?.let { (_, index) ->
                                    acc.put(columnHeader, ccm.component2().remove(index))
                                } ?: acc
                            },
                            version = ref.version + 1L
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
                                } else acc.put(c, ColumnMeta(BaseColumn(right.table, c).order, cm.prenatal))
                            }

                        val columnCellMap = oldRef1.columnCells.fold(ref.columnCells) { acc, ccm ->
                            val columnHeader = ccm.component1()
                            getCellRaw(oldRef1, columnHeader, left.index, left.indexRelation)?.let { (cell, _) ->
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

                                acc.put(columnHeader, cells.put(newIndex, cell))
                            } ?: acc
                        }

                        ref.copy(
                            columns = columnsMap,
                            columnCells = columnCellMap,
                            version = ref.version + 1L
                        )
                    }

                    publishRowMoveEvents(left, right, order, oldRef1, newRef1, oldRef2, newRef2)
                }
            }
        }
    }
}

fun move(left: Row, actionOrder: RowActionOrder, right: Row) = move(RowToRowAction(left, right, actionOrder))

fun copy(rowToRowAction: RowToRowAction) {
    val left = rowToRowAction.left
    val right = rowToRowAction.right
    val order = rowToRowAction.order

    // TODO Handle index relation on right side..?
    if (right.indexRelation != IndexRelation.AT) throw UnsupportedOperationException("Only supporting IndexRelation.AT on right side")

    synchronized(left.table.eventProcessor) {
        synchronized(right.table.eventProcessor) {
            if (order == RowActionOrder.TO) {
                if (left.table === right.table) {
                    // Internal copy
                    val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                        ref.copy(
                            columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                                val columnHeader = ccm.component1()
                                getCellRaw(ref, columnHeader, left.index, left.indexRelation)?.let { (cell, _) ->
                                    acc.put(columnHeader, ccm.component2().put(right.index, cell))
                                } ?: acc
                            },
                            version = ref.version + 1L
                        )
                    }

                    publishRowCopyEvents(left, right, order, oldRef, newRef)
                } else {
                    // Copy between tables
                    val (oldRef, newRef) = right.table.tableRef.refAction { ref ->
                        val leftRef = left.table.tableRef.get()

                        val columnsMap = leftRef
                            .columns
                            .sortedBy { it.component2().columnOrder }
                            .fold(ref.columns) { acc, (c, cm) ->
                                if (acc.containsKey(c)) acc else acc.put(c, ColumnMeta(BaseColumn(right.table, c).order, cm.prenatal))
                            }

                        val columnCellMap = leftRef.columnCells.fold(ref.columnCells) { acc, ccm ->
                            val columnHeader = ccm.component1()
                            getCellRaw(leftRef, columnHeader, left.index, left.indexRelation)?.let { (cell, _) ->
                                acc.put(columnHeader, (acc.get(columnHeader) ?: PTreeMap()).put(right.index, cell))
                            } ?: acc
                        }

                        ref.copy(
                            columns = columnsMap,
                            columnCells = columnCellMap,
                            version = ref.version + 1L
                        )
                    }

                    publishRowCopyEvents(left, right, order, oldRef, newRef)
                }
            } else {
                if (left.table === right.table) {
                    // Internal copy
                    val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                        ref.copy(
                            columnCells = ref.columnCells.fold(PHashMap()) { acc, ccm ->
                                val columnHeader = ccm.component1()
                                getCellRaw(ref, columnHeader, left.index, left.indexRelation)?.let { (cell, _) ->
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

                                    acc.put(columnHeader, cells.put(newIndex, cell))
                                } ?: acc
                            },
                            version = ref.version + 1L
                        )
                    }

                    publishRowCopyEvents(left, right, order, oldRef, newRef)
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
                                } else acc.put(c, ColumnMeta(BaseColumn(right.table, c).order, cm.prenatal))
                            }

                        val columnCellMap = leftRef.columnCells.fold(ref.columnCells) { acc, ccm ->
                            val columnHeader = ccm.component1()
                            getCellRaw(leftRef, columnHeader, left.index, left.indexRelation)?.let { (cell, _) ->
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

                                acc.put(columnHeader, cells.put(newIndex, cell))
                            } ?: acc
                        }

                        ref.copy(
                            columns = columnsMap,
                            columnCells = columnCellMap,
                            version = ref.version + 1L
                        )
                    }

                    publishRowCopyEvents(left, right, order, oldRef, newRef)
                }
            }
        }
    }
}

fun copy(left: Row, actionOrder: RowActionOrder, right: Row) = copy(RowToRowAction(left, right, actionOrder))

// TODO move/copy rowToTableAction ?

// ---

// TODO swap(row to row) / swap(column to column) ?

// ---

fun rename(column: Column, withName: ColumnHeader): Unit = move(column to column, withName)

fun rename(column: Column, vararg withName: String): Unit = move(column to column, *withName)

// TODO rename(table: Table, withName: String)..

fun remove(table: Table) = Registry.deleteTable(table)

fun remove(column: Column) = move(column to Table[null])

// TODO Add some ability to shift up or down, like remove(row, shift = UP), or something like that
fun remove(row: Row) = move(row to Table[null][0])

// TODO These can be more efficient, especially if there are no listeners
fun clear(table: Table): Unit = table.forEach { clear(it) }

fun clear(column: Column): Unit = column.forEach { clear(it) }

fun clear(row: Row): Unit = row.forEach { clear(it) }

fun clear(cell: Cell<*>) = cell { null }

fun clone(table: Table): Table = table.makeClone()

fun clone(table: Table, withName: String?): Table = table.makeClone(withName, true)

// ---

// TODO Introduce a listener for table ops, maybe like an enum TableOps.MOVE|COPY|CLEAR|SET etc..?
//      This will trigger in addition to the cell events, so that someone can subscribe to the operations

interface OnTable<O, N> {
    infix fun events(process: Sequence<TableListenerEvent<out O, out N>>.() -> Unit): TableListenerReference
}

inline fun <reified O : Any, reified N : Any> on(
    table: Table,
    old: KClass<O> = O::class,
    new: KClass<N> = N::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<O, N> {
    override fun events(process: Sequence<TableListenerEvent<out O, out N>>.() -> Unit): TableListenerReference {
        return on(
            table,
            old,
            new,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    table: Table,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<Any, Any> {
    override fun events(process: Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit): TableListenerReference {
        return on(
            table,
            old,
            new,
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

inline fun <reified O, reified N> on(
    table: Table,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableEventReceiver<Table, O, N>.() -> Unit
): TableListenerReference {
    return on(
        table,
        O::class,
        N::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableEventReceiver<Table, Any, Any>.() -> Unit
    )
}

fun on(
    table: Table,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableEventReceiver<Table, Any, Any>.() -> Unit
): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Table, Any, Any>(
            table,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        old == Any::class -> TableEventReceiver(
            table,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        new == Any::class -> TableEventReceiver(
            table,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        else -> TableEventReceiver(
            table,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value) && new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
    }
    return table.eventProcessor.subscribe(table, eventReceiver, init)
}

// ---

inline fun <reified O : Any, reified N : Any> on(
    column: Column,
    old: KClass<O> = O::class,
    new: KClass<N> = N::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<O, N> {
    override fun events(process: Sequence<TableListenerEvent<out O, out N>>.() -> Unit): TableListenerReference {
        return on(
            column,
            old,
            new,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    column: Column,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<Any, Any> {
    override fun events(process: Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit): TableListenerReference {
        return on(
            column,
            old,
            new,
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

inline fun <reified O, reified N> on(
    column: Column,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableEventReceiver<Column, O, N>.() -> Unit
): TableListenerReference {
    return on(
        column,
        O::class,
        N::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableEventReceiver<Column, Any, Any>.() -> Unit
    )
}

fun on(
    column: Column,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableEventReceiver<Column, Any, Any>.() -> Unit
): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Column, Any, Any>(
            column,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        old == Any::class -> TableEventReceiver(
            column,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        new == Any::class -> TableEventReceiver(
            column,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        else -> TableEventReceiver(
            column,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value) && new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
    }
    return column.table.eventProcessor.subscribe(column, eventReceiver, init)
}

// ---

inline fun <reified O : Any, reified N : Any> on(
    row: Row,
    old: KClass<O> = O::class,
    new: KClass<N> = N::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<O, N> {
    override fun events(process: Sequence<TableListenerEvent<out O, out N>>.() -> Unit): TableListenerReference {
        return on(
            row,
            old,
            new,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    row: Row,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<Any, Any> {
    override fun events(process: Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit): TableListenerReference {
        return on(
            row,
            old,
            new,
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

inline fun <reified O, reified N> on(
    row: Row,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableEventReceiver<Row, O, N>.() -> Unit
): TableListenerReference {
    return on(
        row,
        O::class,
        N::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableEventReceiver<Row, Any, Any>.() -> Unit
    )
}

fun on(
    row: Row,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableEventReceiver<Row, Any, Any>.() -> Unit
): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Row, Any, Any>(
            row,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        old == Any::class -> TableEventReceiver(
            row,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        new == Any::class -> TableEventReceiver(
            row,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        else -> TableEventReceiver(
            row,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value) && new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
    }
    return row.table.eventProcessor.subscribe(row, eventReceiver, init)
}

// ---

inline fun <reified O : Any, reified N : Any> on(
    cellRange: CellRange,
    old: KClass<O> = O::class,
    new: KClass<N> = N::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<O, N> {
    override fun events(process: Sequence<TableListenerEvent<out O, out N>>.() -> Unit): TableListenerReference {
        return on(
            cellRange,
            old,
            new,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    cellRange: CellRange,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<Any, Any> {
    override fun events(process: Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit): TableListenerReference {
        return on(
            cellRange,
            old,
            new,
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

inline fun <reified O, reified N> on(
    cellRange: CellRange,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableEventReceiver<CellRange, O, N>.() -> Unit
): TableListenerReference {
    return on(
        cellRange,
        O::class,
        N::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableEventReceiver<CellRange, Any, Any>.() -> Unit
    )
}

fun on(
    cellRange: CellRange,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableEventReceiver<CellRange, Any, Any>.() -> Unit
): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<CellRange, Any, Any>(
            cellRange,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        old == Any::class -> TableEventReceiver(
            cellRange,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        new == Any::class -> TableEventReceiver(
            cellRange,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        else -> TableEventReceiver(
            cellRange,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value) && new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
    }
    return cellRange.start.column.table.eventProcessor.subscribe(cellRange, eventReceiver, init)
}

// ---

inline fun <reified O : Any, reified N : Any> on(
    cell: Cell<*>,
    old: KClass<O> = O::class,
    new: KClass<N> = N::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<O, N> {
    override fun events(process: Sequence<TableListenerEvent<out O, out N>>.() -> Unit): TableListenerReference {
        return on(
            cell,
            old,
            new,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    cell: Cell<*>,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<Any, Any> {
    override fun events(process: Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit): TableListenerReference {
        return on(
            cell,
            old,
            new,
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

inline fun <reified O, reified N> on(
    cell: Cell<*>,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableEventReceiver<Cell<*>, O, N>.() -> Unit
): TableListenerReference {
    return on(
        cell,
        O::class,
        N::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableEventReceiver<Cell<*>, Any, Any>.() -> Unit
    )
}

fun on(
    cell: Cell<*>,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableEventReceiver<Cell<*>, Any, Any>.() -> Unit
): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Cell<*>, Any, Any>(
            cell,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        old == Any::class -> TableEventReceiver(
            cell,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        new == Any::class -> TableEventReceiver(
            cell,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        else -> TableEventReceiver(
            cell,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value) && new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
    }
    return cell.column.table.eventProcessor.subscribe(cell, eventReceiver, init)
}

// ---

inline fun <reified O : Any, reified N : Any> on(
    cells: Cells,
    old: KClass<O> = O::class,
    new: KClass<N> = N::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<O, N> {
    override fun events(process: Sequence<TableListenerEvent<out O, out N>>.() -> Unit): TableListenerReference {
        return on(
            cells,
            old,
            new,
            name,
            order,
            allowLoop,
            skipHistory
        ) events process as Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit
    }
}

@JvmName("onAny")
fun on(
    cells: Cells,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false
) = object : OnTable<Any, Any> {
    override fun events(process: Sequence<TableListenerEvent<out Any, out Any>>.() -> Unit): TableListenerReference {
        return on(
            cells,
            old,
            new,
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

inline fun <reified O, reified N> on(
    cells: Cells,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    noinline init: TableEventReceiver<Cells, O, N>.() -> Unit
): TableListenerReference {
    return on(
        cells,
        O::class,
        N::class,
        name,
        order,
        allowLoop,
        skipHistory,
        init as TableEventReceiver<Cells, Any, Any>.() -> Unit
    )
}

fun on(
    cells: Cells,
    old: KClass<*> = Any::class,
    new: KClass<*> = Any::class,
    name: String? = null,
    order: Long = 0,
    allowLoop: Boolean = false,
    skipHistory: Boolean = false,
    init: TableEventReceiver<Cells, Any, Any>.() -> Unit
): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Cells, Any, Any>(
            cells,
            name,
            order,
            allowLoop,
            skipHistory
        ) { this }
        old == Any::class -> TableEventReceiver(
            cells,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        new == Any::class -> TableEventReceiver(
            cells,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
        else -> TableEventReceiver(
            cells,
            name,
            order,
            allowLoop,
            skipHistory
        ) {
            this.filter {
                old.isInstance(oldTable[it.oldValue].value) && new.isInstance(newTable[it.newValue].value)
            }.map {
                TableListenerEvent(oldTable[it.oldValue], newTable[it.newValue]) as TableListenerEvent<Any, Any>
            }
        }
    }
    return cells.table.eventProcessor.subscribe(cells, eventReceiver, init)
}

// ---

fun off(reference: TableListenerReference) = reference.unsubscribe()

fun off(tableEventReceiver: TableEventReceiver<*, *, *>) = off(tableEventReceiver.reference)

// ---

// TODO Also support CellRange in addition to Table, allowing for storing subsections?

fun load(
    table: Table,
    extension: String = "sigt",
    filter: Column.() -> Column? = { this }
) = load1(
    File(table.name ?: throw InvalidTableException("No table name")) to table,
    extension,
    filter
)

fun save(
    table: Table,
    extension: String = "sigt",
    compress: Boolean = true
) = save1(
    table to File(table.name ?: throw InvalidTableException("No table name")),
    extension,
    compress
)

fun load(
    resources: Pair<File, Table>,
    extension: String = "sigt",
    filter: Column.() -> Column? = { this }
) = load1(resources, extension, filter)

fun save(
    resources: Pair<Table, File>,
    extension: String = "sigt",
    compress: Boolean = true
) = save1(resources, extension, compress)

@JvmName("loadString")
fun load(
    resources: Pair<String, Table>,
    extension: String = "sigt",
    filter: Column.() -> Column? = { this }
) = load1(
    resources.let { File(it.first) to it.second },
    extension,
    filter
)

@JvmName("saveString")
fun save(
    resources: Pair<Table, String>,
    extension: String = "sigt",
    compress: Boolean = true
) = save1(
    resources.let { it.first to File(it.second) },
    extension,
    compress
)

// TODO Add functions for sorting, like sort(table by table["A"]) or sort(table by table[1]) etc..
//      Also look at supporting sort of a subsection of a table through cell ranges, columns, etc