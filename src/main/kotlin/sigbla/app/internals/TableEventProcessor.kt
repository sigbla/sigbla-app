package sigbla.app.internals

import sigbla.app.*
import sigbla.app.exceptions.InvalidListenerException
import sigbla.app.exceptions.ListenerLoopException
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong

internal class TableEventProcessor {
    private class ListenerReferenceEvent<R>(
        val listenerReference: R,
        val listenerEvent: (event: Sequence<TableListenerEvent<Any, Any>>) -> Unit
    ) {
        var version = Long.MIN_VALUE
    }

    private class ListenerId(val order: Long) : Comparable<ListenerId> {
        val id: Long = idGenerator.getAndIncrement()

        override fun compareTo(other: ListenerId): Int {
            val cmp = this.order.compareTo(other.order)
            if (cmp == 0) return this.id.compareTo(other.id)
            return cmp
        }
    }

    private val tableListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableRef>> = ConcurrentSkipListMap()
    private val columnListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerColumnRef>> = ConcurrentSkipListMap()
    private val rowListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerRowRef>> = ConcurrentSkipListMap()
    private val cellRangeListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellRangeRef>> = ConcurrentSkipListMap()
    private val cellListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellRef>> = ConcurrentSkipListMap()

    private abstract class ListenerUnsubscribeRef : TableListenerReference() {
        protected var haveUnsubscribed = false
        var key: ListenerId? = null
            set(value) {
                field = value
                if (haveUnsubscribed) off(this)
            }
    }

    private class ListenerTableRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableRef>>,
        val table: Table
    ) : ListenerUnsubscribeRef() {
        var lazyName: String? = null
        var lazyOrder: Long? = null
        var lazyAllowLoop: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }

        override fun unsubscribe() {
            haveUnsubscribed = true
            listeners.remove(key ?: return)
        }
    }

    private class ListenerColumnRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerColumnRef>>,
        val column: Column
    ) : ListenerUnsubscribeRef() {
        var lazyName: String? = null
        var lazyOrder: Long? = null
        var lazyAllowLoop: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }

        override fun unsubscribe() {
            haveUnsubscribed = true
            listeners.remove(key ?: return)
        }
    }

    private class ListenerRowRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerRowRef>>,
        val row: Row
    ) : ListenerUnsubscribeRef() {
        var lazyName: String? = null
        var lazyOrder: Long? = null
        var lazyAllowLoop: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }

        override fun unsubscribe() {
            haveUnsubscribed = true
            listeners.remove(key ?: return)
        }
    }

    private class ListenerCellRangeRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellRangeRef>>,
        val cellRange: CellRange
    ) : ListenerUnsubscribeRef() {
        var lazyName: String? = null
        var lazyOrder: Long? = null
        var lazyAllowLoop: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }

        override fun unsubscribe() {
            haveUnsubscribed = true
            listeners.remove(key ?: return)
        }
    }

    private class ListenerCellRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellRef>>,
        val cell: Cell<*>
    ) : ListenerUnsubscribeRef() {
        var lazyName: String? = null
        var lazyOrder: Long? = null
        var lazyAllowLoop: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }

        override fun unsubscribe() {
            haveUnsubscribed = true
            listeners.remove(key ?: return)
        }
    }

    // TODO Consider a mechanism to pause processing, buffering up events till no longer paused.
    //      Can be useful when we know that a single or a few cells are going to be updated many times
    //      but we don't want to fire for each of these updates individually?
    //      Potential example: table { // ops in here }

    fun subscribe(
        table: Table,
        eventReceiver: TableEventReceiver<Table, Any, Any>,
        init: TableEventReceiver<Table, Any, Any>.() -> Unit
    ): TableListenerReference {
        val listenerRef = ListenerTableRef(
            tableListeners,
            table
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        listenerRef.lazyName = eventReceiver.name
        listenerRef.lazyOrder = eventReceiver.order
        listenerRef.lazyAllowLoop = eventReceiver.allowLoop

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            val key = ListenerId(eventReceiver.order)
            tableListeners[key] = listenerRefEvent
            listenerRef.key = key

            if (!eventReceiver.skipHistory) {
                val tc = clone(table)
                listenerRefEvent.version = tc.tableRef.get().version
                listenerRefEvent.listenerEvent(tc.asSequence().map {
                    // TODO UnitCell must point to separate table clone
                    TableListenerEvent(UnitCell(it.column, it.index), it) as TableListenerEvent<Any, Any>
                })
            }
        }
        return listenerRef
    }

    fun subscribe(
        column: Column,
        eventReceiver: TableEventReceiver<Column, Any, Any>,
        init: TableEventReceiver<Column, Any, Any>.() -> Unit
    ): TableListenerReference {
        val listenerRef = ListenerColumnRef(
            columnListeners,
            column
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        listenerRef.lazyName = eventReceiver.name
        listenerRef.lazyOrder = eventReceiver.order
        listenerRef.lazyAllowLoop = eventReceiver.allowLoop

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            val key = ListenerId(eventReceiver.order)
            columnListeners[key] = listenerRefEvent
            listenerRef.key = key

            if (!eventReceiver.skipHistory) {
                val tc = clone(column.table)
                listenerRefEvent.version = tc.tableRef.get().version
                listenerRefEvent.listenerEvent(tc[column].asSequence().map {
                    // TODO UnitCell must point to separate table clone
                    TableListenerEvent(UnitCell(it.column, it.index), it) as TableListenerEvent<Any, Any>
                })
            }
        }
        return listenerRef
    }

    fun subscribe(
        row: Row,
        eventReceiver: TableEventReceiver<Row, Any, Any>,
        init: TableEventReceiver<Row, Any, Any>.() -> Unit
    ): TableListenerReference {
        val listenerRef = ListenerRowRef(
            rowListeners,
            row
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        listenerRef.lazyName = eventReceiver.name
        listenerRef.lazyOrder = eventReceiver.order
        listenerRef.lazyAllowLoop = eventReceiver.allowLoop

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            val key = ListenerId(eventReceiver.order)
            rowListeners[key] = listenerRefEvent
            listenerRef.key = key

            // TODO + clone + skipHistory + version
            //listenerRefEvent.listenerEvent(row.asSequence().map {
            //    ListenerEvent(UnitCell(it.column, it.index), it) as ListenerEvent<Any, Any>
            //})
        }
        return listenerRef
    }

    fun subscribe(
        cellRange: CellRange,
        eventReceiver: TableEventReceiver<CellRange, Any, Any>,
        init: TableEventReceiver<CellRange, Any, Any>.() -> Unit
    ): TableListenerReference {
        val listenerRef = ListenerCellRangeRef(
            cellRangeListeners,
            cellRange
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        listenerRef.lazyName = eventReceiver.name
        listenerRef.lazyOrder = eventReceiver.order
        listenerRef.lazyAllowLoop = eventReceiver.allowLoop

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            val key = ListenerId(eventReceiver.order)
            cellRangeListeners[key] = listenerRefEvent
            listenerRef.key = key

            if (!eventReceiver.skipHistory) {
                val tc = clone(cellRange.table)
                listenerRefEvent.version = tc.tableRef.get().version
                listenerRefEvent.listenerEvent(tc[cellRange].asSequence().map {
                    // TODO UnitCell must point to separate table clone
                    TableListenerEvent(UnitCell(it.column, it.index), it) as TableListenerEvent<Any, Any>
                })
            }
        }
        return listenerRef
    }

    fun subscribe(
        cell: Cell<*>,
        eventReceiver: TableEventReceiver<Cell<*>, Any, Any>,
        init: TableEventReceiver<Cell<*>, Any, Any>.() -> Unit
    ): TableListenerReference {
        val listenerRef = ListenerCellRef(
            cellListeners,
            cell
        )

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        listenerRef.lazyName = eventReceiver.name
        listenerRef.lazyOrder = eventReceiver.order
        listenerRef.lazyAllowLoop = eventReceiver.allowLoop

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }
        synchronized(listenerRefEvent) {
            val key = ListenerId(eventReceiver.order)
            cellListeners[key] = listenerRefEvent
            listenerRef.key = key

            if (!eventReceiver.skipHistory) {
                val tc = clone(cell.table)
                listenerRefEvent.version = tc.tableRef.get().version
                listenerRefEvent.listenerEvent(tc[cell].asSequence().map {
                    // TODO UnitCell must point to separate table clone
                    TableListenerEvent(UnitCell(it.column, it.index), it) as TableListenerEvent<Any, Any>
                })
            }
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

    // TODO Look at changing cells and buffers to use seqs
    internal fun publish(cells: List<TableListenerEvent<Any, Any>>) {
        val buffer = eventBuffer.get()

        if (buffer != null) {
            buffer.addAll(cells)

            activeListener.get()?.let {
                activeListeners.get()?.add(it) ?: activeListeners.set(mutableSetOf(it))
            }

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
                            loopCheck(listenerRef.listenerReference)
                            listenerRef.listenerEvent(batch.asSequence().filter { it.newValue.table.tableRef.get().version > listenerRef.version })
                            // TODO Do we really need to clear this here and below?
                            if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
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
                        }.filter { it.newValue.table.tableRef.get().version > listenerRef.version })

                        if (columnBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                loopCheck(listenerRef.listenerReference)
                                listenerRef.listenerEvent(columnBatch.asSequence())
                                if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                            }
                        }
                    }

                rowListeners
                    .values
                    .forEach { listenerRef ->
                        val rowBatch = Collections.unmodifiableList(batch.filter {
                            return@filter it.newValue.index == listenerRef.listenerReference.row.index
                                    || it.oldValue.index == listenerRef.listenerReference.row.index
                        }.filter { it.newValue.table.tableRef.get().version > listenerRef.version })

                        if (rowBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                loopCheck(listenerRef.listenerReference)
                                listenerRef.listenerEvent(rowBatch.asSequence())
                                if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                            }
                        }
                    }

                cellRangeListeners
                    .values
                    .forEach { listenerRef ->
                        val cellRangeBatch = Collections.unmodifiableList(batch.filter {
                            return@filter listenerRef.listenerReference.cellRange.contains(it.newValue)
                                    || listenerRef.listenerReference.cellRange.contains(it.oldValue)
                        }.filter { it.newValue.table.tableRef.get().version > listenerRef.version })

                        if (cellRangeBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                loopCheck(listenerRef.listenerReference)
                                listenerRef.listenerEvent(cellRangeBatch.asSequence())
                                if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                            }
                        }
                    }

                // TODO rowRangeListeners..

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
                        }.filter { it.newValue.table.tableRef.get().version > listenerRef.version })

                        if (cellBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                loopCheck(listenerRef.listenerReference)
                                listenerRef.listenerEvent(cellBatch.asSequence())
                                if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                            }
                        }
                    }
            }
        } finally {
            eventBuffer.set(null)
            activeListener.remove()
            activeListeners.remove()
        }
    }

    private fun loopCheck(listenerReference: TableListenerReference) {
        if (listenerReference.allowLoop) return
        if (activeListeners.get()?.contains(listenerReference) == true) throw ListenerLoopException(listenerReference)
        activeListener.set(listenerReference)
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
        private val eventBuffer = ThreadLocal<MutableList<TableListenerEvent<Any, Any>>?>()
        private val activeListener = ThreadLocal<TableListenerReference?>()
        private val activeListeners = ThreadLocal<MutableSet<TableListenerReference>?>()
    }
}
