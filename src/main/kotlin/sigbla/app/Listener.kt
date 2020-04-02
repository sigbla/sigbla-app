package sigbla.app

import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong

interface ListenerReference {
    fun unsubscribe()
}

data class ListenerEvent<O, N>(val oldValue: Cell<O>, val newValue: Cell<N>)
class ListenerEventReceiver<F, O, N>(val source: F, val listenerReference: ListenerReference, val events: List<ListenerEvent<out O, out N>>)

internal class TableEventProcessor {
    private class ListenerReferenceEvent<R>(val listenerReference: R, val listenerEvent: (event: List<ListenerEvent<*, *>>) -> Unit)

    private val tableListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerTableRef>> = ConcurrentSkipListMap()
    private val columnListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerColumnRef>> = ConcurrentSkipListMap()
    private val rowListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerRowRef>> = ConcurrentSkipListMap()
    private val cellRangeListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerCellRangeRef>> = ConcurrentSkipListMap()
    private val cellListeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerCellRef>> = ConcurrentSkipListMap()

    private data class ListenerTableRef(val id: Long, val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerTableRef>>, val table: Table) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
        }
    }

    private data class ListenerColumnRef(val id: Long, val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerColumnRef>>, val column: Column) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
        }
    }

    private data class ListenerRowRef(val id: Long, val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerRowRef>>, val row: Row) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
        }
    }

    private data class ListenerCellRangeRef(val id: Long, val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerCellRangeRef>>, val cellRange: CellRange) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
        }
    }

    private data class ListenerCellRef(val id: Long, val listeners: ConcurrentMap<Long, ListenerReferenceEvent<ListenerCellRef>>, val cell: Cell<*>) : ListenerReference {
        override fun unsubscribe() {
            listeners.remove(id)
        }
    }

    fun subscribe(table: Table, listener: (eventReceiver: ListenerEventReceiver<Table, *, *>) -> Unit): ListenerReference {
        val listenerRef = ListenerTableRef(idGenerator.getAndIncrement(), tableListeners, table)
        tableListeners[listenerRef.id] = ListenerReferenceEvent(listenerRef) {
            listener.invoke(ListenerEventReceiver(table, listenerRef, it))
        }
        return listenerRef
    }

    fun subscribe(column: Column, listener: (eventReceiver: ListenerEventReceiver<Column, *, *>) -> Unit): ListenerReference {
        val listenerRef = ListenerColumnRef(idGenerator.getAndIncrement(), columnListeners, column)
        columnListeners[listenerRef.id] = ListenerReferenceEvent(listenerRef) {
            listener.invoke(ListenerEventReceiver(column, listenerRef, it))
        }
        return listenerRef
    }

    fun subscribe(row: Row, listener: (eventReceiver: ListenerEventReceiver<Row, *, *>) -> Unit): ListenerReference {
        val listenerRef = ListenerRowRef(idGenerator.getAndIncrement(), rowListeners, row)
        rowListeners[listenerRef.id] = ListenerReferenceEvent(listenerRef) {
            listener.invoke(ListenerEventReceiver(row, listenerRef, it))
        }
        return listenerRef
    }

    fun subscribe(cellRange: CellRange, listener: (eventReceiver: ListenerEventReceiver<CellRange, *, *>) -> Unit): ListenerReference {
        val listenerRef = ListenerCellRangeRef(idGenerator.getAndIncrement(), cellRangeListeners, cellRange)
        cellRangeListeners[listenerRef.id] = ListenerReferenceEvent(listenerRef) {
            listener.invoke(ListenerEventReceiver(cellRange, listenerRef, it))
        }
        return listenerRef
    }

    fun subscribe(cell: Cell<*>, listener: (eventReceiver: ListenerEventReceiver<Cell<*>, *, *>) -> Unit): ListenerReference {
        val listenerRef = ListenerCellRef(idGenerator.getAndIncrement(), cellListeners, cell)
        cellListeners[listenerRef.id] = ListenerReferenceEvent(listenerRef) {
            listener.invoke(ListenerEventReceiver(cell, listenerRef, it))
        }
        return listenerRef
    }

    fun publish(cells: List<ListenerEvent<Cell<*>, Cell<*>>>) {
        val buffer = eventBuffer.get()

        if (buffer != null) {
            buffer.addAll(cells)
            return
        }

        eventBuffer.set(cells.toMutableList())

        try {
            while (true) {
                val batch = eventBuffer.get() ?: break
                if (batch.isEmpty()) break

                eventBuffer.set(mutableListOf())

                tableListeners
                    .values
                    .forEach { listenerRef ->
                        listenerRef.listenerEvent.invoke(batch)
                    }

                columnListeners
                    .values
                    .forEach { listenerRef ->
                        // TODO
                    }

                rowListeners
                    .values
                    .forEach { listenerRef ->
                        // TODO
                    }

                cellRangeListeners
                    .values
                    .forEach { listenerRef ->
                        // TODO
                    }

                cellListeners
                    .values
                    .forEach { listenerRef ->
                        // TODO
                    }
            }
        } finally {
            eventBuffer.set(null)
        }
    }

    fun shutdown() {
        tableListeners.clear()
        columnListeners.clear()
        rowListeners.clear()
        cellRangeListeners.clear()
        cellListeners.clear()
    }

    companion object {
        private val idGenerator = AtomicLong()
        private val eventBuffer = ThreadLocal<MutableList<ListenerEvent<Cell<*>, Cell<*>>>?>()
    }
}
