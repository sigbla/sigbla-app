package sigbla.app.internals

import sigbla.app.*
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong

internal class TableEventProcessor {
    private class ListenerReferenceEvent<R>(
        val listenerReference: R,
        val listenerEvent: (event: Sequence<ListenerEvent<Any, Any>>) -> Unit
    )

    private class ListenerId(val order: Long) : Comparable<ListenerId> {
        val id: Long = idGenerator.getAndIncrement()

        override fun compareTo(that: ListenerId): Int {
            val cmp = this.order.compareTo(that.order)
            if (cmp == 0) return this.id.compareTo(that.id)
            return cmp
        }
    }

    private val tableListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableRef>> = ConcurrentSkipListMap()
    private val columnListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerColumnRef>> = ConcurrentSkipListMap()
    private val rowListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerRowRef>> = ConcurrentSkipListMap()
    private val cellRangeListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellRangeRef>> = ConcurrentSkipListMap()
    private val cellListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellRef>> = ConcurrentSkipListMap()

    private data class ListenerTableRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableRef>>,
        val table: Table
    ) : ListenerReference {
        lateinit var key: ListenerId

        override fun unsubscribe() {
            listeners.remove(key)
        }
    }

    private data class ListenerColumnRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerColumnRef>>,
        val column: Column
    ) : ListenerReference {
        lateinit var key: ListenerId

        override fun unsubscribe() {
            listeners.remove(key)
        }
    }

    private data class ListenerRowRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerRowRef>>,
        val row: Row
    ) : ListenerReference {
        lateinit var key: ListenerId

        override fun unsubscribe() {
            listeners.remove(key)
        }
    }

    private data class ListenerCellRangeRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellRangeRef>>,
        val cellRange: CellRange
    ) : ListenerReference {
        lateinit var key: ListenerId

        override fun unsubscribe() {
            listeners.remove(key)
        }
    }

    private data class ListenerCellRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellRef>>,
        val cell: Cell<*>
    ) : ListenerReference {
        lateinit var key: ListenerId

        override fun unsubscribe() {
            listeners.remove(key)
        }
    }

    // TODO Consider a mechanism to pause processing, buffering up events till no longer paused.
    //      Can be useful when we know that a single or a few cells are going to be updated many times
    //      but we don't want to fire for each of these updates individually?
    //      Potential example: table { // ops in here }

    // TODO When a subscription is added, we should feed existing cells to the listener
    //      The idea being that is shouldn't matter what order data or listener is added, listener will always get the feed

    fun subscribe(
        table: Table,
        eventReceiver: EventReceiver<Table, Any, Any>,
        init: EventReceiver<Table, Any, Any>.() -> Unit
    ): ListenerReference {
        val listenerRef = ListenerTableRef(
            tableListeners,
            table
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            // TODO Tables, cellrange, and others, will need a asSequence that returns all their cells
            //listener.invoke(ListenerEventReceiver(table, listenerRef, table.asSequence().map to event..))
            tableListeners[ListenerId(eventReceiver.order)] = listenerRefEvent
        }
        return listenerRef
    }

    fun subscribe(
        column: Column,
        eventReceiver: EventReceiver<Column, Any, Any>,
        init: EventReceiver<Column, Any, Any>.() -> Unit
    ): ListenerReference {
        val listenerRef = ListenerColumnRef(
            columnListeners,
            column
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            columnListeners[ListenerId(eventReceiver.order)] = listenerRefEvent
        }
        return listenerRef
    }

    fun subscribe(
        row: Row,
        eventReceiver: EventReceiver<Row, Any, Any>,
        init: EventReceiver<Row, Any, Any>.() -> Unit
    ): ListenerReference {
        val listenerRef = ListenerRowRef(
            rowListeners,
            row
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            rowListeners[ListenerId(eventReceiver.order)] = listenerRefEvent
        }
        return listenerRef
    }

    fun subscribe(
        cellRange: CellRange,
        eventReceiver: EventReceiver<CellRange, Any, Any>,
        init: EventReceiver<CellRange, Any, Any>.() -> Unit
    ): ListenerReference {
        val listenerRef = ListenerCellRangeRef(
            cellRangeListeners,
            cellRange
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            cellRangeListeners[ListenerId(eventReceiver.order)] = listenerRefEvent
        }
        return listenerRef
    }

    fun subscribe(
        cell: Cell<*>,
        eventReceiver: EventReceiver<Cell<*>, Any, Any>,
        init: EventReceiver<Cell<*>, Any, Any>.() -> Unit
    ): ListenerReference {
        val listenerRef = ListenerCellRef(
            cellListeners,
            cell
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            cellListeners[ListenerId(eventReceiver.order)] = listenerRefEvent
        }
        return listenerRef
    }

    // TODO Not sure if this is worth having?
    internal fun haveListeners(): Boolean {
        return tableListeners.size > 0 ||
                columnListeners.size > 0 ||
                rowListeners.size > 0 ||
                cellRangeListeners.size > 0 ||
                cellListeners.size > 0
    }

    // TODO: Whenever something is published, the old and new cells provided should,
    //       if we navigate up to their column/table provide the old/new column or table
    //       as they existed at the point of the old/new value being created
    internal fun publish(cells: List<ListenerEvent<Any, Any>>) {
        val buffer = eventBuffer.get()

        if (buffer != null) {
            buffer.addAll(cells)
            return
        }

        eventBuffer.set(cells.toMutableList())

        try {
            while (true) {
                val batch = Collections.unmodifiableList(eventBuffer.get() ?: break)
                if (batch.isEmpty()) break

                eventBuffer.set(mutableListOf())

                tableListeners
                    .values
                    .forEach { listenerRef ->
                        synchronized(listenerRef) {
                            // TODO Remove .invoke ? Same below..
                            listenerRef.listenerEvent.invoke(batch.asSequence())
                        }
                    }

                columnListeners
                    .values
                    .forEach { listenerRef ->
                        val columnBatch = Collections.unmodifiableList(batch.filter {
                            return@filter it.newValue.column == listenerRef.listenerReference.column
                                    || it.newValue.column.columnHeader == listenerRef.listenerReference.column.columnHeader
                                    || it.oldValue.column == listenerRef.listenerReference.column
                                    || it.oldValue.column.columnHeader == listenerRef.listenerReference.column.columnHeader
                        })

                        if (columnBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                listenerRef.listenerEvent.invoke(columnBatch.asSequence())
                            }
                        }
                    }

                rowListeners
                    .values
                    .forEach { listenerRef ->
                        val rowBatch = Collections.unmodifiableList(batch.filter {
                            return@filter it.newValue.index == listenerRef.listenerReference.row.index
                                    || it.oldValue.index == listenerRef.listenerReference.row.index
                        })

                        if (rowBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                listenerRef.listenerEvent.invoke(rowBatch.asSequence())
                            }
                        }
                    }

                cellRangeListeners
                    .values
                    .forEach { listenerRef ->
                        val cellRangeBatch = Collections.unmodifiableList(batch.filter {
                            return@filter listenerRef.listenerReference.cellRange.contains(it.newValue)
                                    || listenerRef.listenerReference.cellRange.contains(it.oldValue)
                        })

                        if (cellRangeBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                listenerRef.listenerEvent.invoke(cellRangeBatch.asSequence())
                            }
                        }
                    }

                cellListeners
                    .values
                    .forEach { listenerRef ->
                        val cellBatch = Collections.unmodifiableList(batch.filter {
                            return@filter (listenerRef.listenerReference.cell.index == it.newValue.index
                                    && (listenerRef.listenerReference.cell.column == it.newValue.column
                                    || listenerRef.listenerReference.cell.column.columnHeader == it.newValue.column.columnHeader))
                                    || (listenerRef.listenerReference.cell.index == it.oldValue.index
                                    && (listenerRef.listenerReference.cell.column == it.oldValue.column
                                    || listenerRef.listenerReference.cell.column.columnHeader == it.oldValue.column.columnHeader))
                        })

                        if (cellBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                listenerRef.listenerEvent.invoke(cellBatch.asSequence())
                            }
                        }
                    }
            }
        } finally {
            eventBuffer.set(null)
        }
    }

    internal fun shutdown() {
        tableListeners.clear()
        columnListeners.clear()
        rowListeners.clear()
        cellRangeListeners.clear()
        cellListeners.clear()
    }

    companion object {
        private val idGenerator = AtomicLong()
        private val eventBuffer = ThreadLocal<MutableList<ListenerEvent<Any, Any>>?>()
    }
}
