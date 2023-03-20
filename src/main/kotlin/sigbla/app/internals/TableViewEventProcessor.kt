package sigbla.app.internals

import sigbla.app.*
import sigbla.app.exceptions.InvalidListenerException
import sigbla.app.exceptions.ListenerLoopException
import sigbla.app.exceptions.SigblaAppException
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong

internal class TableViewEventProcessor {
    private class ListenerReferenceEvent<R>(
        val listenerReference: R,
        val listenerEvent: (event: Sequence<TableViewListenerEvent<Any>>) -> Unit
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

    private val tableViewListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableViewRef>> = ConcurrentSkipListMap()
    private val columnViewListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerColumnViewRef>> = ConcurrentSkipListMap()
    private val rowViewListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerRowViewRef>> = ConcurrentSkipListMap()
    // TODO private val cellRangeViewListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellRangeViewRef>> = ConcurrentSkipListMap()
    private val cellViewListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellViewRef>> = ConcurrentSkipListMap()
    private val derivedCellViewListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerDerivedCellViewRef>> = ConcurrentSkipListMap()

    private abstract class ListenerUnsubscribeRef : TableViewListenerReference() {
        protected var haveUnsubscribed = false
        var key: ListenerId? = null
            set(value) {
                field = value
                if (haveUnsubscribed) unsubscribe()
            }
    }

    private class ListenerTableViewRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableViewRef>>,
        val tableView: TableView
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

    private class ListenerColumnViewRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableViewRef>>,
        val columnView: ColumnView
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

    private class ListenerRowViewRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableViewRef>>,
        val rowView: RowView
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

    // TODO CellRange

    private class ListenerCellViewRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableViewRef>>,
        val cellView: CellView
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

    private class ListenerDerivedCellViewRef(
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableViewRef>>,
        val derivedCellView: DerivedCellView
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

    fun subscribe(
        tableView: TableView,
        eventReceiver: TableViewEventReceiver<TableView, Any>,
        init: TableViewEventReceiver<TableView, Any>.() -> Unit
    ): TableViewListenerReference {
        val listenerRef = ListenerTableViewRef(
            tableViewListeners,
            tableView
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
            tableViewListeners[key] = listenerRefEvent
            listenerRef.key = key

            if (!eventReceiver.skipHistory) {
                val ref = tableView.tableViewRef.get()
                val oldTableView = tableView.makeClone(ref = TableViewRef())
                val newTableView = tableView.makeClone(ref = ref)
                listenerRefEvent.version = ref.version
                // TODO TableView for on<TableView>(..) ? Note that we also have this as a BaseTableView for now..
                val seq1 = (if (eventReceiver.type == Any::class || eventReceiver.type == ColumnView::class)
                    newTableView.columnViews.map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<ColumnView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq2 = (if (eventReceiver.type == Any::class || eventReceiver.type == RowView::class)
                    newTableView.rowViews.map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<RowView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq3 = (if (eventReceiver.type == Any::class || eventReceiver.type == CellView::class)
                    newTableView.cellViews.map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<CellView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq4 = (if (eventReceiver.type == Any::class || eventReceiver.type == DerivedCellView::class)
                    newTableView.asSequence().map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<DerivedCellView>()) as Sequence<TableViewListenerEvent<Any>>

                listenerRefEvent.listenerEvent(seq1 + seq2 + seq3 + seq4)
            }
        }
        return listenerRef
    }

    fun subscribe(
        columnView: ColumnView,
        eventReceiver: TableViewEventReceiver<ColumnView, Any>,
        init: TableViewEventReceiver<ColumnView, Any>.() -> Unit
    ): TableViewListenerReference {
        val listenerRef = ListenerColumnViewRef(
            tableViewListeners,
            columnView
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
            columnViewListeners[key] = listenerRefEvent
            listenerRef.key = key

            if (!eventReceiver.skipHistory) {
                val ref = columnView.tableView.tableViewRef.get()
                val oldTableView = columnView.tableView.makeClone(ref = TableViewRef())
                val newTableView = columnView.tableView.makeClone(ref = ref)
                listenerRefEvent.version = ref.version
                // TODO TableView for on<TableView>(..) ? Note that we also have this as a BaseTableView for now..
                val seq1 = (if (eventReceiver.type == Any::class || eventReceiver.type == ColumnView::class)
                    sequenceOf(newTableView[columnView]).map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<ColumnView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq2 = (if (eventReceiver.type == Any::class || eventReceiver.type == RowView::class)
                    newTableView.rowViews.map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<RowView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq3 = (if (eventReceiver.type == Any::class || eventReceiver.type == CellView::class)
                    newTableView[columnView].cellViews.map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<CellView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq4 = (if (eventReceiver.type == Any::class || eventReceiver.type == DerivedCellView::class)
                    newTableView[columnView].asSequence().map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<DerivedCellView>()) as Sequence<TableViewListenerEvent<Any>>

                listenerRefEvent.listenerEvent(seq1 + seq2 + seq3 + seq4)
            }
        }
        return listenerRef
    }

    fun subscribe(
        rowView: RowView,
        eventReceiver: TableViewEventReceiver<RowView, Any>,
        init: TableViewEventReceiver<RowView, Any>.() -> Unit
    ): TableViewListenerReference {
        val listenerRef = ListenerRowViewRef(
            tableViewListeners,
            rowView
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
            rowViewListeners[key] = listenerRefEvent
            listenerRef.key = key

            if (!eventReceiver.skipHistory) {
                val ref = rowView.tableView.tableViewRef.get()
                val oldTableView = rowView.tableView.makeClone(ref = TableViewRef())
                val newTableView = rowView.tableView.makeClone(ref = ref)
                listenerRefEvent.version = ref.version
                // TODO TableView for on<TableView>(..) ? Note that we also have this as a BaseTableView for now..
                val seq1 = (if (eventReceiver.type == Any::class || eventReceiver.type == ColumnView::class)
                    newTableView.columnViews.map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<ColumnView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq2 = (if (eventReceiver.type == Any::class || eventReceiver.type == RowView::class)
                    sequenceOf(newTableView[rowView]).map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<RowView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq3 = (if (eventReceiver.type == Any::class || eventReceiver.type == CellView::class)
                    newTableView[rowView].cellViews.map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<CellView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq4 = (if (eventReceiver.type == Any::class || eventReceiver.type == DerivedCellView::class)
                    newTableView[rowView].asSequence().map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<DerivedCellView>()) as Sequence<TableViewListenerEvent<Any>>

                listenerRefEvent.listenerEvent(seq1 + seq2 + seq3 + seq4)
            }
        }
        return listenerRef
    }

    // TODO CellRange

    fun subscribe(
        cellView: CellView,
        eventReceiver: TableViewEventReceiver<CellView, Any>,
        init: TableViewEventReceiver<CellView, Any>.() -> Unit
    ): TableViewListenerReference {
        val listenerRef = ListenerCellViewRef(
            tableViewListeners,
            cellView
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
            cellViewListeners[key] = listenerRefEvent
            listenerRef.key = key

            if (!eventReceiver.skipHistory) {
                val ref = cellView.tableView.tableViewRef.get()
                val oldTableView = cellView.tableView.makeClone(ref = TableViewRef())
                val newTableView = cellView.tableView.makeClone(ref = ref)
                listenerRefEvent.version = ref.version
                // TODO TableView for on<TableView>(..) ? Note that we also have this as a BaseTableView for now..
                val seq1 = (if (eventReceiver.type == Any::class || eventReceiver.type == ColumnView::class)
                    sequenceOf(newTableView[cellView.columnView]).map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<ColumnView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq2 = (if (eventReceiver.type == Any::class || eventReceiver.type == RowView::class)
                    sequenceOf(newTableView[cellView.index]).map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<RowView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq3 = (if (eventReceiver.type == Any::class || eventReceiver.type == CellView::class)
                    sequenceOf(newTableView[cellView]).map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<CellView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq4 = (if (eventReceiver.type == Any::class || eventReceiver.type == DerivedCellView::class)
                    newTableView[cellView].asSequence().map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<DerivedCellView>()) as Sequence<TableViewListenerEvent<Any>>

                listenerRefEvent.listenerEvent(seq1 + seq2 + seq3 + seq4)
            }
        }
        return listenerRef
    }

    fun subscribe(
        derivedCellView: DerivedCellView,
        eventReceiver: TableViewEventReceiver<DerivedCellView, Any>,
        init: TableViewEventReceiver<DerivedCellView, Any>.() -> Unit
    ): TableViewListenerReference {
        val listenerRef = ListenerDerivedCellViewRef(
            tableViewListeners,
            derivedCellView
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
            derivedCellViewListeners[key] = listenerRefEvent
            listenerRef.key = key

            if (!eventReceiver.skipHistory) {
                val ref = derivedCellView.tableView.tableViewRef.get()
                val oldTableView = derivedCellView.tableView.makeClone(ref = TableViewRef())
                val newTableView = derivedCellView.tableView.makeClone(ref = ref)
                listenerRefEvent.version = ref.version
                // TODO TableView for on<TableView>(..) ? Note that we also have this as a BaseTableView for now..
                val seq1 = (if (eventReceiver.type == Any::class || eventReceiver.type == ColumnView::class)
                    sequenceOf(newTableView[derivedCellView.columnView]).map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<ColumnView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq2 = (if (eventReceiver.type == Any::class || eventReceiver.type == RowView::class)
                    sequenceOf(newTableView[derivedCellView.index]).map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<RowView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq3 = (if (eventReceiver.type == Any::class || eventReceiver.type == CellView::class)
                    sequenceOf(newTableView[derivedCellView.cellView]).map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<CellView>()) as Sequence<TableViewListenerEvent<Any>>
                val seq4 = (if (eventReceiver.type == Any::class || eventReceiver.type == DerivedCellView::class)
                    newTableView[derivedCellView].asSequence().map {
                        TableViewListenerEvent(oldTableView[it], it)
                    } else emptySequence<DerivedCellView>()) as Sequence<TableViewListenerEvent<Any>>

                listenerRefEvent.listenerEvent(seq1 + seq2 + seq3 + seq4)
            }
        }
        return listenerRef
    }

    // TODO Not sure if this is worth having?
    internal fun haveListeners(): Boolean {
        return tableViewListeners.size > 0 ||
                columnViewListeners.size > 0 ||
                rowViewListeners.size > 0 ||
                //cellRangeViewListeners.size > 0 ||
                cellViewListeners.size > 0
    }

    // TODO Look at changing cells and buffers to use seqs
    internal fun publish(views: List<TableViewListenerEvent<Any>>) {
        val buffer = eventBuffer.get()

        if (buffer != null) {
            buffer.addAll(views)

            activeListener.get()?.let {
                activeListeners.get()?.add(it) ?: activeListeners.set(mutableSetOf(it))
            }

            return
        }

        eventBuffer.set(views.toMutableList())

        fun newValueVersion(newValue: Any) = when (newValue) {
            is TableView -> newValue.tableView.tableViewRef.get().version
            is ColumnView -> newValue.tableView.tableViewRef.get().version
            is RowView -> newValue.tableView.tableViewRef.get().version
            is CellView -> newValue.tableView.tableViewRef.get().version
            is DerivedCellView -> newValue.tableView.tableViewRef.get().version
            else -> throw SigblaAppException("Unsupported type: ${newValue.javaClass}")
        }

        fun valueColumn(value: Any) = when (value) {
            // TODO is TableView -> null
            is ColumnView -> value
            is RowView -> null
            is CellView -> value.columnView
            is DerivedCellView -> value.columnView
            else -> throw SigblaAppException("Unsupported type: ${value.javaClass}")
        }

        fun valueIndex(value: Any) = when (value) {
            // TODO is TableView -> null
            is ColumnView -> null
            is RowView -> value.index
            is CellView -> value.index
            is DerivedCellView -> value.index
            else -> throw SigblaAppException("Unsupported type: ${value.javaClass}")
        }

        try {
            while (true) {
                val batch = Collections.unmodifiableList(eventBuffer.get() ?: break)
                if (batch.isEmpty()) break

                eventBuffer.set(mutableListOf())

                tableViewListeners
                    .values
                    .forEach { listenerRef ->
                        synchronized(listenerRef) {
                            loopCheck(listenerRef.listenerReference)
                            listenerRef.listenerEvent(batch.asSequence().filter { newValueVersion(it.newValue) > listenerRef.version })
                            // TODO Do we really need to clear this here and below?
                            if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                        }
                    }

                columnViewListeners
                    .values
                    .forEach { listenerRef ->
                        val columnBatch = Collections.unmodifiableList(batch.filter {
                            val newValueColumn = valueColumn(it.newValue)
                            val oldValueColumn = valueColumn(it.oldValue)
                            return@filter newValueColumn == null || oldValueColumn == null
                                    || newValueColumn == listenerRef.listenerReference.columnView
                                    || newValueColumn.columnHeader == listenerRef.listenerReference.columnView.columnHeader // TODO Might not need this?
                                    || oldValueColumn == listenerRef.listenerReference.columnView
                                    || oldValueColumn.columnHeader == listenerRef.listenerReference.columnView.columnHeader // TODO Might not need this?
                        }.filter { newValueVersion(it.newValue) > listenerRef.version })

                        if (columnBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                loopCheck(listenerRef.listenerReference)
                                listenerRef.listenerEvent(columnBatch.asSequence())
                                if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                            }
                        }
                    }

                rowViewListeners
                    .values
                    .forEach { listenerRef ->
                        val rowBatch = Collections.unmodifiableList(batch.filter {
                            // TODO This filtering will need to take into account the index relation if supported?
                            val newValueIndex = valueIndex(it.newValue)
                            val oldValueIndex = valueIndex(it.oldValue)
                            return@filter newValueIndex == null || oldValueIndex == null
                                    || newValueIndex == listenerRef.listenerReference.rowView.index
                                    || oldValueIndex == listenerRef.listenerReference.rowView.index
                        }.filter { newValueVersion(it.newValue) > listenerRef.version })

                        if (rowBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                loopCheck(listenerRef.listenerReference)
                                listenerRef.listenerEvent(rowBatch.asSequence())
                                if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                            }
                        }
                    }

                // TODO cellRangeView .. rowRangeViewListeners..? columnRangeView?

                cellViewListeners
                    .values
                    .forEach { listenerRef ->
                        val cellBatch = Collections.unmodifiableList(batch.filter {
                            val newValueColumn = valueColumn(it.newValue)
                            val oldValueColumn = valueColumn(it.oldValue)
                            val newValueIndex = valueIndex(it.newValue)
                            val oldValueIndex = valueIndex(it.oldValue)
                            return@filter (newValueColumn == null || newValueIndex == null || oldValueColumn == null || oldValueIndex == null)
                                    || (listenerRef.listenerReference.cellView.index == newValueIndex
                                        && (listenerRef.listenerReference.cellView.columnView == newValueColumn
                                        || listenerRef.listenerReference.cellView.columnView.columnHeader == newValueColumn.columnHeader)) // TODO Is the columnHeader check really needed?
                                    || (listenerRef.listenerReference.cellView.index == oldValueIndex
                                        && (listenerRef.listenerReference.cellView.columnView == oldValueColumn
                                        || listenerRef.listenerReference.cellView.columnView.columnHeader == oldValueColumn.columnHeader)) // TODO Is the columnHeader check really needed?
                        }.filter { newValueVersion(it.newValue) > listenerRef.version })

                        if (cellBatch.isNotEmpty()) {
                            synchronized(listenerRef) {
                                loopCheck(listenerRef.listenerReference)
                                listenerRef.listenerEvent(cellBatch.asSequence())
                                if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                            }
                        }
                    }

                derivedCellViewListeners
                    .values
                    .forEach { listenerRef ->
                        val cellBatch = Collections.unmodifiableList(batch.filter {
                            val newValueColumn = valueColumn(it.newValue)
                            val oldValueColumn = valueColumn(it.oldValue)
                            val newValueIndex = valueIndex(it.newValue)
                            val oldValueIndex = valueIndex(it.oldValue)
                            return@filter (newValueColumn == null || newValueIndex == null || oldValueColumn == null || oldValueIndex == null)
                                    || (listenerRef.listenerReference.derivedCellView.index == newValueIndex
                                    && (listenerRef.listenerReference.derivedCellView.columnView == newValueColumn
                                    || listenerRef.listenerReference.derivedCellView.columnView.columnHeader == newValueColumn.columnHeader)) // TODO Is the columnHeader check really needed?
                                    || (listenerRef.listenerReference.derivedCellView.index == oldValueIndex
                                    && (listenerRef.listenerReference.derivedCellView.columnView == oldValueColumn
                                    || listenerRef.listenerReference.derivedCellView.columnView.columnHeader == oldValueColumn.columnHeader)) // TODO Is the columnHeader check really needed?
                        }.filter { newValueVersion(it.newValue) > listenerRef.version })

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

    private fun loopCheck(listenerReference: TableViewListenerReference) {
        if (listenerReference.allowLoop) return
        if (activeListeners.get()?.contains(listenerReference) == true) throw ListenerLoopException(listenerReference)
        activeListener.set(listenerReference)
    }

    internal fun shutdown() {
        tableViewListeners.clear()
    }

    companion object {
        private val idGenerator = AtomicLong()
        private val eventBuffer = ThreadLocal<MutableList<TableViewListenerEvent<Any>>?>()
        private val activeListener = ThreadLocal<TableViewListenerReference?>()
        private val activeListeners = ThreadLocal<MutableSet<TableViewListenerReference>?>()
    }
}
