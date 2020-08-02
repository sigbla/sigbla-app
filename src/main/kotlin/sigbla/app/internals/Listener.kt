package sigbla.app.internals

import sigbla.app.*
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong

interface ListenerReference {
    fun unsubscribe()
}

// TODO Change to builder, and use priority..
data class ListenerConfiguration(var name: String? = null, var priority: Int = 0)
data class ListenerEvent<O, N>(val oldValue: Cell<O>, val newValue: Cell<N>)

class EventReceiver<F, O, N>(
    val source: F,
    private val typeFilter: Sequence<ListenerEvent<Any, Any>>.() -> Sequence<ListenerEvent<Any, Any>>
) {
    lateinit var reference: ListenerReference
        internal set

    var configuration: ListenerConfiguration =
        ListenerConfiguration()
        private set

    private var process: (Sequence<ListenerEvent<O, N>>.() -> Unit) = {}

    // TODO Below is just code for playing around, see use in Foo..
    fun configure(init: ListenerConfiguration.() -> Unit) {
        configuration = ListenerConfiguration().apply(init)
    }

    fun events(process: Sequence<ListenerEvent<out O, out N>>.() -> Unit) {
        this.process = process
    }

    internal operator fun invoke(events: Sequence<ListenerEvent<Any, Any>>) {
        (events.typeFilter() as Sequence<ListenerEvent<O, N>>).process()
    }
}

internal class TableEventProcessor {
    private class ListenerReferenceEvent<R>(
        val listenerReference: R,
        val listenerEvent: (event: Sequence<ListenerEvent<Any, Any>>) -> Unit
    )

    private val tableListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerTableRef>> = ConcurrentSkipListMap()
    private val columnListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerColumnRef>> = ConcurrentSkipListMap()
    private val rowListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerRowRef>> = ConcurrentSkipListMap()
    private val cellRangeListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerCellRangeRef>> = ConcurrentSkipListMap()
    private val cellListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerCellRef>> = ConcurrentSkipListMap()

    private data class ListenerTableRef(
        val id: Long,
        val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerTableRef>>,
        val table: Table
    ) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
        }
    }

    private data class ListenerColumnRef(
        val id: Long,
        val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerColumnRef>>,
        val column: Column
    ) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
        }
    }

    private data class ListenerRowRef(
        val id: Long,
        val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerRowRef>>,
        val row: Row
    ) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
        }
    }

    private data class ListenerCellRangeRef(
        val id: Long,
        val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerCellRangeRef>>,
        val cellRange: CellRange
    ) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
        }
    }

    private data class ListenerCellRef(
        val id: Long,
        val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerCellRef>>,
        val cell: Cell<*>
    ) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
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
            idGenerator.getAndIncrement(),
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
            tableListeners[listenerRef.id] = listenerRefEvent
        }
        return listenerRef
    }

    fun subscribe(
        column: Column,
        eventReceiver: EventReceiver<Column, Any, Any>,
        init: EventReceiver<Column, Any, Any>.() -> Unit
    ): ListenerReference {
        val listenerRef = ListenerColumnRef(
            idGenerator.getAndIncrement(),
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
            columnListeners[listenerRef.id] = listenerRefEvent
        }
        return listenerRef
    }

    fun subscribe(
        row: Row,
        eventReceiver: EventReceiver<Row, Any, Any>,
        init: EventReceiver<Row, Any, Any>.() -> Unit
    ): ListenerReference {
        val listenerRef = ListenerRowRef(
            idGenerator.getAndIncrement(),
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
            rowListeners[listenerRef.id] = listenerRefEvent
        }
        return listenerRef
    }

    fun subscribe(
        cellRange: CellRange,
        eventReceiver: EventReceiver<CellRange, Any, Any>,
        init: EventReceiver<CellRange, Any, Any>.() -> Unit
    ): ListenerReference {
        val listenerRef = ListenerCellRangeRef(
            idGenerator.getAndIncrement(),
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
            cellRangeListeners[listenerRef.id] = listenerRefEvent
        }
        return listenerRef
    }

    fun subscribe(
        cell: Cell<*>,
        eventReceiver: EventReceiver<Cell<*>, Any, Any>,
        init: EventReceiver<Cell<*>, Any, Any>.() -> Unit
    ): ListenerReference {
        val listenerRef = ListenerCellRef(
            idGenerator.getAndIncrement(),
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
            cellListeners[listenerRef.id] = listenerRefEvent
        }
        return listenerRef
    }

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
