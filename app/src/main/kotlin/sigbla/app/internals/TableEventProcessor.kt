/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import sigbla.app.*
import sigbla.app.exceptions.InvalidListenerException
import sigbla.app.exceptions.InvalidValueException
import sigbla.app.exceptions.ListenerLoopException
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.LinkedHashMap

internal class TableEventProcessor {
    private var eventBuffer: MutableList<TableListenerEvent<Any, Any>>? = null

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
        var lazySkipHistory: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }
        override val skipHistory: Boolean by lazy { lazySkipHistory ?: throw InvalidListenerException() }

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
        var lazySkipHistory: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }
        override val skipHistory: Boolean by lazy { lazySkipHistory ?: throw InvalidListenerException() }

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
        var lazySkipHistory: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }
        override val skipHistory: Boolean by lazy { lazySkipHistory ?: throw InvalidListenerException() }

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
        var lazySkipHistory: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }
        override val skipHistory: Boolean by lazy { lazySkipHistory ?: throw InvalidListenerException() }

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
        var lazySkipHistory: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }
        override val skipHistory: Boolean by lazy { lazySkipHistory ?: throw InvalidListenerException() }

        override fun unsubscribe() {
            haveUnsubscribed = true
            listeners.remove(key ?: return)
        }
    }

    private class ListenerCellsRef(
        val cells: Cells
    ) : TableListenerReference() {
        var haveUnsubscribed = false

        var refs: List<TableListenerReference>? = null
            set(value) {
                field = value
                if (haveUnsubscribed) off(this)
            }

        var lazyName: String? = null
        var lazyOrder: Long? = null
        var lazyAllowLoop: Boolean? = null
        var lazySkipHistory: Boolean? = null

        override val name: String? by lazy { lazyName }
        override val order: Long by lazy { lazyOrder ?: throw InvalidListenerException() }
        override val allowLoop: Boolean by lazy { lazyAllowLoop ?: throw InvalidListenerException() }
        override val skipHistory: Boolean by lazy { lazySkipHistory ?: throw InvalidListenerException() }

        override fun unsubscribe() {
            haveUnsubscribed = true
            for (ref in refs ?: return) {
                ref.unsubscribe()
            }
        }
    }

    @Synchronized
    fun subscribe(
        table: Table,
        eventReceiver: TableEventReceiver<Table, Any, Any>,
        init: TableEventReceiver<Table, Any, Any>.() -> Unit,
        ref: TableRef = table.tableRef.get()
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
        listenerRef.lazySkipHistory = eventReceiver.skipHistory

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }

        val key = ListenerId(eventReceiver.order)
        tableListeners[key] = listenerRefEvent
        listenerRef.key = key

        if (!eventReceiver.skipHistory) {
            val oldTable = table.makeClone(ref = TableRef())
            val newTable = table.makeClone(ref = ref)
            listenerRefEvent.version = ref.version
            listenerRefEvent.listenerEvent(newTable.asSequence().map {
                val oldColumn = BaseColumn(
                    oldTable,
                    it.column.header,
                    ref.columns[it.column.header]?.columnOrder ?: it.column.order
                )
                TableListenerEvent(UnitCell(oldColumn, it.index), it) as TableListenerEvent<Any, Any>
            })
        }

        return listenerRef
    }

    @Synchronized
    fun subscribe(
        column: Column,
        eventReceiver: TableEventReceiver<Column, Any, Any>,
        init: TableEventReceiver<Column, Any, Any>.() -> Unit,
        ref: TableRef = column.table.tableRef.get()
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
        listenerRef.lazySkipHistory = eventReceiver.skipHistory

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }

        val key = ListenerId(eventReceiver.order)
        columnListeners[key] = listenerRefEvent
        listenerRef.key = key

        if (!eventReceiver.skipHistory) {
            val oldTable = column.table.makeClone(ref = TableRef())
            val newTable = column.table.makeClone(ref = ref)
            listenerRefEvent.version = ref.version
            listenerRefEvent.listenerEvent(newTable[column].asSequence().map {
                val oldColumn = BaseColumn(
                    oldTable,
                    it.column.header,
                    ref.columns[it.column.header]?.columnOrder ?: it.column.order
                )
                TableListenerEvent(UnitCell(oldColumn, it.index), it) as TableListenerEvent<Any, Any>
            })
        }

        return listenerRef
    }

    @Synchronized
    fun subscribe(
        row: Row,
        eventReceiver: TableEventReceiver<Row, Any, Any>,
        init: TableEventReceiver<Row, Any, Any>.() -> Unit,
        ref: TableRef = row.table.tableRef.get()
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
        listenerRef.lazySkipHistory = eventReceiver.skipHistory

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }

        val key = ListenerId(eventReceiver.order)
        rowListeners[key] = listenerRefEvent
        listenerRef.key = key

        if (!eventReceiver.skipHistory) {
            val oldTable = row.table.makeClone(ref = TableRef())
            val newTable = row.table.makeClone(ref = ref)
            listenerRefEvent.version = ref.version
            listenerRefEvent.listenerEvent(newTable[row].asSequence().map {
                val oldColumn = BaseColumn(
                    oldTable,
                    it.column.header,
                    ref.columns[it.column.header]?.columnOrder ?: it.column.order
                )
                // TODO While this will always be a UnitCell on the old table,
                //  still need to take into account the index relation..?
                TableListenerEvent(UnitCell(oldColumn, it.index), it) as TableListenerEvent<Any, Any>
            })
        }

        return listenerRef
    }

    @Synchronized
    fun subscribe(
        cellRange: CellRange,
        eventReceiver: TableEventReceiver<CellRange, Any, Any>,
        init: TableEventReceiver<CellRange, Any, Any>.() -> Unit,
        ref: TableRef = cellRange.table.tableRef.get()
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
        listenerRef.lazySkipHistory = eventReceiver.skipHistory

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }

        val key = ListenerId(eventReceiver.order)
        cellRangeListeners[key] = listenerRefEvent
        listenerRef.key = key

        if (!eventReceiver.skipHistory) {
            val oldTable = cellRange.table.makeClone(ref = TableRef())
            val newTable = cellRange.table.makeClone(ref = ref)
            listenerRefEvent.version = ref.version
            listenerRefEvent.listenerEvent(newTable[cellRange].asSequence().map {
                val oldColumn = BaseColumn(
                    oldTable,
                    it.column.header,
                    ref.columns[it.column.header]?.columnOrder ?: it.column.order
                )
                TableListenerEvent(UnitCell(oldColumn, it.index), it) as TableListenerEvent<Any, Any>
            })
        }

        return listenerRef
    }

    @Synchronized
    fun subscribe(
        cell: Cell<*>,
        eventReceiver: TableEventReceiver<Cell<*>, Any, Any>,
        init: TableEventReceiver<Cell<*>, Any, Any>.() -> Unit,
        ref: TableRef = cell.table.tableRef.get()
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
        listenerRef.lazySkipHistory = eventReceiver.skipHistory

        val listenerRefEvent =
            ListenerReferenceEvent(listenerRef) {
                eventReceiver(it)
            }

        val key = ListenerId(eventReceiver.order)
        cellListeners[key] = listenerRefEvent
        listenerRef.key = key

        if (!eventReceiver.skipHistory) {
            val oldTable = cell.table.makeClone(ref = TableRef())
            val newTable = cell.table.makeClone(ref = ref)
            listenerRefEvent.version = ref.version
            listenerRefEvent.listenerEvent(newTable[cell].asSequence()
                .filter { it !is UnitCell }
                .map {
                    val oldColumn = BaseColumn(
                        oldTable,
                        it.column.header,
                        ref.columns[it.column.header]?.columnOrder ?: it.column.order
                    )
                    TableListenerEvent(UnitCell(oldColumn, it.index), it) as TableListenerEvent<Any, Any>
                })
        }

        return listenerRef
    }

    @Synchronized
    fun subscribe(
        cells: Cells,
        eventReceiver: TableEventReceiver<Cells, Any, Any>,
        init: TableEventReceiver<Cells, Any, Any>.() -> Unit,
        ref: TableRef = cells.table.tableRef.get()
    ): TableListenerReference {
        val listenerRef = ListenerCellsRef(cells)

        eventReceiver.reference = listenerRef
        eventReceiver.init()

        listenerRef.lazyName = eventReceiver.name
        listenerRef.lazyOrder = eventReceiver.order
        listenerRef.lazyAllowLoop = eventReceiver.allowLoop
        listenerRef.lazySkipHistory = eventReceiver.skipHistory

        val refs = mutableListOf<TableListenerReference>()
        cells.sources.forEach {
            when (it) {
                is Cell<*> -> {
                    val cellReceiver = TableEventReceiver<Cell<*>, Any, Any>(it, listenerRef.name, listenerRef.order, listenerRef.allowLoop, true) { this }
                    cellReceiver.events {
                        eventReceiver(this as Sequence<TableListenerEvent<Any, Any>>)
                    }
                    refs.add(subscribe(it, cellReceiver, {}, ref))
                }
                is Row -> {
                    val rowReceiver = TableEventReceiver<Row, Any, Any>(it, listenerRef.name, listenerRef.order, listenerRef.allowLoop, true) { this }
                    rowReceiver.events {
                        eventReceiver(this as Sequence<TableListenerEvent<Any, Any>>)
                    }
                    refs.add(subscribe(it, rowReceiver, {}, ref))
                }
                is Column -> {
                    val columnReceiver = TableEventReceiver<Column, Any, Any>(it, listenerRef.name, listenerRef.order, listenerRef.allowLoop, true) { this }
                    columnReceiver.events {
                        eventReceiver(this as Sequence<TableListenerEvent<Any, Any>>)
                    }
                    refs.add(subscribe(it, columnReceiver, {}, ref))
                }
                is CellRange -> {
                    val cellRangeReceiver = TableEventReceiver<CellRange, Any, Any>(it, listenerRef.name, listenerRef.order, listenerRef.allowLoop, true) { this }
                    cellRangeReceiver.events {
                        eventReceiver(this as Sequence<TableListenerEvent<Any, Any>>)
                    }
                    refs.add(subscribe(it, cellRangeReceiver, {}, ref))
                }
                is Table -> {
                    val tableReceiver = TableEventReceiver<Table, Any, Any>(it, listenerRef.name, listenerRef.order, listenerRef.allowLoop, true) { this }
                    tableReceiver.events {
                        eventReceiver(this as Sequence<TableListenerEvent<Any, Any>>)
                    }
                    refs.add(subscribe(it, tableReceiver, {}, ref))
                }
                else -> throw InvalidValueException("Unsupported source type: ${it::class}")
            }
        }

        listenerRef.refs = refs

        if (!eventReceiver.skipHistory) {
            //val ref = cells.table.tableRef.get()
            val oldTable = cells.table.makeClone(ref = TableRef())
            val newTable = cells.table.makeClone(ref = ref)
            eventReceiver(cells.asSequence().map { newTable[it] }.map {
                val oldColumn = BaseColumn(
                    oldTable,
                    it.column.header,
                    ref.columns[it.column.header]?.columnOrder ?: it.column.order
                )
                TableListenerEvent(UnitCell(oldColumn, it.index), it) as TableListenerEvent<Any, Any>
            })
        }

        return listenerRef
    }

    internal fun haveListeners(): Boolean {
        return tableListeners.size > 0 ||
                columnListeners.size > 0 ||
                rowListeners.size > 0 ||
                cellRangeListeners.size > 0 ||
                cellListeners.size > 0
    }

    @Synchronized
    internal fun pauseEvents(): Boolean {
        if (eventBuffer != null) return false
        eventBuffer = mutableListOf()
        return true
    }

    @Synchronized
    internal fun clearBuffer() {
        eventBuffer = null
    }

    // TODO Look at changing cells and buffers to use seqs
    @Synchronized
    internal fun publish(cells: List<TableListenerEvent<Any, Any>>) {
        val buffer = eventBuffer

        if (buffer != null) {
            buffer.addAll(cells)

            activeListener.get()?.let {
                activeListeners.get()?.add(it) ?: activeListeners.set(mutableSetOf(it))
            }

            return
        }

        eventBuffer = cells.toMutableList()

        publish(false)
    }

    @Synchronized
    internal fun publish(rebase: Boolean) {
        if (rebase) {
            eventBuffer?.apply {
                if (this.isEmpty()) return@apply

                val oldTable = this.first().oldValue.table
                val newTable = this.last().newValue.table

                val locations = LinkedHashMap<Pair<ColumnHeader, Long>, Boolean>()

                this.forEach {
                    val location = it.newValue.column.header to it.newValue.index
                    if (locations[location] == true) locations.remove(location)
                    locations[location] = true
                }

                val updated = mutableListOf<TableListenerEvent<Any, Any>>()

                locations.keys.forEach {
                    updated.add(TableListenerEvent(oldTable[it.first, it.second], newTable[it.first, it.second]) as TableListenerEvent<Any, Any>)
                }

                eventBuffer = updated
            }
        }

        try {
            while (true) {
                val batch = Collections.unmodifiableList(eventBuffer ?: break)
                if (batch.isEmpty()) break

                eventBuffer = mutableListOf()

                tableListeners
                    .values
                    .forEach { listenerRef ->
                        loopCheck(listenerRef.listenerReference)
                        listenerRef.listenerEvent(batch.asSequence().filter { it.newValue.table.tableRef.get().version > listenerRef.version })
                        // TODO Do we really need to clear this here and below?
                        if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                    }

                columnListeners
                    .values
                    .forEach { listenerRef ->
                        val columnBatch = Collections.unmodifiableList(batch.filter {
                            return@filter it.newValue.column.header == listenerRef.listenerReference.column.header
                                    || it.oldValue.column.header == listenerRef.listenerReference.column.header
                        }.filter { it.newValue.table.tableRef.get().version > listenerRef.version })

                        if (columnBatch.isNotEmpty()) {
                            loopCheck(listenerRef.listenerReference)
                            listenerRef.listenerEvent(columnBatch.asSequence())
                            if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                        }
                    }

                rowListeners
                    .values
                    .forEach { listenerRef ->
                        val rowBatch = Collections.unmodifiableList(batch.filter {
                            // TODO This filtering will need to take into account the index relation
                            return@filter it.newValue.index == listenerRef.listenerReference.row.index
                                    || it.oldValue.index == listenerRef.listenerReference.row.index
                        }.filter { it.newValue.table.tableRef.get().version > listenerRef.version })

                        if (rowBatch.isNotEmpty()) {
                            loopCheck(listenerRef.listenerReference)
                            listenerRef.listenerEvent(rowBatch.asSequence())
                            if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
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
                            loopCheck(listenerRef.listenerReference)
                            listenerRef.listenerEvent(cellRangeBatch.asSequence())
                            if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                        }
                    }

                // TODO rowRangeListeners..? columnRange?

                cellListeners
                    .values
                    .forEach { listenerRef ->
                        val cellBatch = Collections.unmodifiableList(batch.filter {
                            return@filter (listenerRef.listenerReference.cell.index == it.newValue.index
                                            && listenerRef.listenerReference.cell.column.header == it.newValue.column.header)
                                        || (listenerRef.listenerReference.cell.index == it.oldValue.index
                                            && listenerRef.listenerReference.cell.column.header == it.oldValue.column.header)
                        }.filter { it.newValue.table.tableRef.get().version > listenerRef.version })

                        if (cellBatch.isNotEmpty()) {
                            loopCheck(listenerRef.listenerReference)
                            listenerRef.listenerEvent(cellBatch.asSequence())
                            if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                        }
                    }
            }
        } finally {
            eventBuffer = null
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
        private val activeListener = ThreadLocal<TableListenerReference?>()
        private val activeListeners = ThreadLocal<MutableSet<TableListenerReference>?>()
    }
}
