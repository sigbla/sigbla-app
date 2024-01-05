/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import sigbla.app.*
import sigbla.app.exceptions.InvalidListenerException
import sigbla.app.exceptions.ListenerLoopException
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass

internal class TableViewEventProcessor {
    private var eventBuffer: MutableList<TableViewListenerEvent<Any>>? = null

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
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerColumnViewRef>>,
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
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerRowViewRef>>,
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
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerCellViewRef>>,
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
        private val listeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerDerivedCellViewRef>>,
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

    @Synchronized
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

        val key = ListenerId(eventReceiver.order)
        tableViewListeners[key] = listenerRefEvent
        listenerRef.key = key

        if (!eventReceiver.skipHistory) {
            val ref = tableView.tableViewRef.get()
            val oldTableView = tableView.makeClone(ref = TableViewRef())
            val newTableView = tableView.makeClone(ref = ref)
            listenerRefEvent.version = ref.version

            val tableEvents = listOfNotNull(
                if (ref.table == null) null else TableViewListenerEvent(SourceTable(oldTableView, null), SourceTable(newTableView, ref.table)),
                if (ref.resources.isEmpty) null else TableViewListenerEvent(oldTableView[Resources], newTableView[Resources]),
                if (ref.defaultCellView.cellClasses?.isEmpty() != false) null else TableViewListenerEvent(oldTableView[CellClasses], newTableView[CellClasses]),
                if (ref.defaultCellView.cellHeight == null) null else TableViewListenerEvent(oldTableView[CellHeight], newTableView[CellHeight]),
                if (ref.defaultCellView.cellTopics?.isEmpty() != false) null else TableViewListenerEvent(oldTableView[CellTopics], newTableView[CellTopics]),
                if (ref.defaultCellView.cellWidth == null) null else TableViewListenerEvent(oldTableView[CellWidth], newTableView[CellWidth])
            ).asSequence()

            val columnEvents = ref.columnViews.asSequence().map {
                val columnHeader = it.component1()
                val viewMeta = it.component2()

                val oldColumnView = oldTableView[columnHeader]
                val newColumnView = newTableView[columnHeader]

                listOfNotNull(
                    if (viewMeta.cellClasses?.isEmpty() != false) null else TableViewListenerEvent(oldColumnView[CellClasses], newColumnView[CellClasses]),
                    if (viewMeta.cellTopics?.isEmpty() != false) null else TableViewListenerEvent(oldColumnView[CellTopics], newColumnView[CellTopics]),
                    if (viewMeta.cellWidth == null) null else TableViewListenerEvent(oldColumnView[CellWidth], newColumnView[CellWidth])
                )
            }.flatten()

            val rowEvents = ref.rowViews.asSequence().map {
                val index = it.component1()
                val viewMeta = it.component2()

                val oldRowView = oldTableView[index]
                val newRowView = newTableView[index]

                listOfNotNull(
                    if (viewMeta.cellClasses?.isEmpty() != false) null else TableViewListenerEvent(oldRowView[CellClasses], newRowView[CellClasses]),
                    if (viewMeta.cellHeight == null) null else TableViewListenerEvent(oldRowView[CellHeight], newRowView[CellHeight]),
                    if (viewMeta.cellTopics?.isEmpty() != false) null else TableViewListenerEvent(oldRowView[CellTopics], newRowView[CellTopics])
                )
            }.flatten()

            val cellEvents = ref.cellViews.asSequence().map {
                val chi = it.component1()
                val viewMeta = it.component2()

                val oldCellView = oldTableView[chi.first, chi.second]
                val newCellView = newTableView[chi.first, chi.second]

                listOfNotNull(
                    if (viewMeta.cellClasses?.isEmpty() != false) null else TableViewListenerEvent(oldCellView[CellClasses], newCellView[CellClasses]),
                    if (viewMeta.cellHeight == null) null else TableViewListenerEvent(oldCellView[CellHeight], newCellView[CellHeight]),
                    if (viewMeta.cellTopics?.isEmpty() != false) null else TableViewListenerEvent(oldCellView[CellTopics], newCellView[CellTopics]),
                    if (viewMeta.cellWidth == null) null else TableViewListenerEvent(oldCellView[CellWidth], newCellView[CellWidth])
                )
            }.flatten()

            val cellTransformerEvents = ref.cellTransformers.keys().asSequence().map {
                val oldCellView = oldTableView[it.first, it.second]
                val newCellView = newTableView[it.first, it.second]

                TableViewListenerEvent(oldCellView[CellTransformer], newCellView[CellTransformer])
            }

            listenerRefEvent.listenerEvent((tableEvents + columnEvents + rowEvents + cellEvents + cellTransformerEvents) as Sequence<TableViewListenerEvent<Any>>)
        }

        return listenerRef
    }

    @Synchronized
    fun subscribe(
        columnView: ColumnView,
        eventReceiver: TableViewEventReceiver<ColumnView, Any>,
        init: TableViewEventReceiver<ColumnView, Any>.() -> Unit
    ): TableViewListenerReference {
        val listenerRef = ListenerColumnViewRef(
            columnViewListeners,
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

        val key = ListenerId(eventReceiver.order)
        columnViewListeners[key] = listenerRefEvent
        listenerRef.key = key

        if (!eventReceiver.skipHistory) {
            val ref = columnView.tableView.tableViewRef.get()
            val oldTableView = columnView.tableView.makeClone(ref = TableViewRef())
            val newTableView = columnView.tableView.makeClone(ref = ref)
            listenerRefEvent.version = ref.version

            val columnEvents = ref.columnViews[columnView.header]?.let { viewMeta ->
                val oldColumnView = oldTableView[columnView.header]
                val newColumnView = newTableView[columnView.header]

                listOf(
                    if (viewMeta.cellClasses?.isEmpty() != false) null else TableViewListenerEvent(oldColumnView[CellClasses], newColumnView[CellClasses]),
                    if (viewMeta.cellTopics?.isEmpty() != false) null else TableViewListenerEvent(oldColumnView[CellTopics], newColumnView[CellTopics]),
                    if (viewMeta.cellWidth == null) null else TableViewListenerEvent(oldColumnView[CellWidth], newColumnView[CellWidth])
                ).asSequence()
            } ?: emptySequence()

            val cellEvents = ref.cellViews.asSequence()
                .filter { it.component1().first == columnView.header }
                .map {
                    val chi = it.component1()
                    val viewMeta = it.component2()

                    val oldCellView = oldTableView[chi.first, chi.second]
                    val newCellView = newTableView[chi.first, chi.second]

                    listOfNotNull(
                        if (viewMeta.cellClasses?.isEmpty() != false) null else TableViewListenerEvent(oldCellView[CellClasses], newCellView[CellClasses]),
                        if (viewMeta.cellHeight == null) null else TableViewListenerEvent(oldCellView[CellHeight], newCellView[CellHeight]),
                        if (viewMeta.cellTopics?.isEmpty() != false) null else TableViewListenerEvent(oldCellView[CellTopics], newCellView[CellTopics]),
                        if (viewMeta.cellWidth == null) null else TableViewListenerEvent(oldCellView[CellWidth], newCellView[CellWidth])
                    )
                }.flatten()

            val cellTransformerEvents = ref.cellTransformers.keys().asSequence()
                .filter { it.first == columnView.header }
                .map {
                    val oldCellView = oldTableView[it.first, it.second]
                    val newCellView = newTableView[it.first, it.second]

                    TableViewListenerEvent(oldCellView[CellTransformer], newCellView[CellTransformer])
                }

            listenerRefEvent.listenerEvent((columnEvents + cellEvents + cellTransformerEvents) as Sequence<TableViewListenerEvent<Any>>)
        }

        return listenerRef
    }

    @Synchronized
    fun subscribe(
        rowView: RowView,
        eventReceiver: TableViewEventReceiver<RowView, Any>,
        init: TableViewEventReceiver<RowView, Any>.() -> Unit
    ): TableViewListenerReference {
        val listenerRef = ListenerRowViewRef(
            rowViewListeners,
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

        val key = ListenerId(eventReceiver.order)
        rowViewListeners[key] = listenerRefEvent
        listenerRef.key = key

        if (!eventReceiver.skipHistory) {
            val ref = rowView.tableView.tableViewRef.get()
            val oldTableView = rowView.tableView.makeClone(ref = TableViewRef())
            val newTableView = rowView.tableView.makeClone(ref = ref)
            listenerRefEvent.version = ref.version

            val rowEvents = ref.rowViews[rowView.index]?.let { viewMeta ->
                val oldRowView = oldTableView[rowView.index]
                val newRowView = newTableView[rowView.index]

                listOfNotNull(
                    if (viewMeta.cellClasses?.isEmpty() != false) null else TableViewListenerEvent(oldRowView[CellClasses], newRowView[CellClasses]),
                    if (viewMeta.cellHeight == null) null else TableViewListenerEvent(oldRowView[CellHeight], newRowView[CellHeight]),
                    if (viewMeta.cellTopics?.isEmpty() != false) null else TableViewListenerEvent(oldRowView[CellTopics], newRowView[CellTopics])
                ).asSequence()
            } ?: emptySequence()

            val cellEvents = ref.cellViews.asSequence()
                .filter { it.component1().second == rowView.index }
                .map {
                    val chi = it.component1()
                    val viewMeta = it.component2()

                    val oldCellView = oldTableView[chi.first, chi.second]
                    val newCellView = newTableView[chi.first, chi.second]

                    listOfNotNull(
                        if (viewMeta.cellClasses?.isEmpty() != false) null else TableViewListenerEvent(oldCellView[CellClasses], newCellView[CellClasses]),
                        if (viewMeta.cellHeight == null) null else TableViewListenerEvent(oldCellView[CellHeight], newCellView[CellHeight]),
                        if (viewMeta.cellTopics?.isEmpty() != false) null else TableViewListenerEvent(oldCellView[CellTopics], newCellView[CellTopics]),
                        if (viewMeta.cellWidth == null) null else TableViewListenerEvent(oldCellView[CellWidth], newCellView[CellWidth])
                    )
                }.flatten()

            val cellTransformerEvents = ref.cellTransformers.keys().asSequence()
                .filter { it.second == rowView.index }
                .map {
                    val oldCellView = oldTableView[it.first, it.second]
                    val newCellView = newTableView[it.first, it.second]

                    TableViewListenerEvent(oldCellView[CellTransformer], newCellView[CellTransformer])
                }

            listenerRefEvent.listenerEvent((rowEvents + cellEvents + cellTransformerEvents) as Sequence<TableViewListenerEvent<Any>>)
        }

        return listenerRef
    }

    // TODO CellRange

    @Synchronized
    fun subscribe(
        cellView: CellView,
        eventReceiver: TableViewEventReceiver<CellView, Any>,
        init: TableViewEventReceiver<CellView, Any>.() -> Unit
    ): TableViewListenerReference {
        val listenerRef = ListenerCellViewRef(
            cellViewListeners,
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

        val key = ListenerId(eventReceiver.order)
        cellViewListeners[key] = listenerRefEvent
        listenerRef.key = key

        if (!eventReceiver.skipHistory) {
            val ref = cellView.tableView.tableViewRef.get()
            val oldTableView = cellView.tableView.makeClone(ref = TableViewRef())
            val newTableView = cellView.tableView.makeClone(ref = ref)
            listenerRefEvent.version = ref.version

            val cellEvents = ref.cellViews[cellView.columnView.header to cellView.index]?.let { viewMeta ->
                val oldCellView = oldTableView[cellView]
                val newCellView = newTableView[cellView]

                listOfNotNull(
                    if (viewMeta.cellClasses?.isEmpty() != false) null else TableViewListenerEvent(oldCellView[CellClasses], newCellView[CellClasses]),
                    if (viewMeta.cellHeight == null) null else TableViewListenerEvent(oldCellView[CellHeight], newCellView[CellHeight]),
                    if (viewMeta.cellTopics?.isEmpty() != false) null else TableViewListenerEvent(oldCellView[CellTopics], newCellView[CellTopics]),
                    if (viewMeta.cellWidth == null) null else TableViewListenerEvent(oldCellView[CellWidth], newCellView[CellWidth])
                ).asSequence()
            } ?: emptySequence()

            val cellTransformerEvents = if (ref.cellTransformers.containsKey(cellView.columnView.header to cellView.index)) {
                val oldCellView = oldTableView[cellView]
                val newCellView = newTableView[cellView]

                listOf(TableViewListenerEvent(oldCellView[CellTransformer], newCellView[CellTransformer])).asSequence()
            } else emptySequence()

            listenerRefEvent.listenerEvent((cellEvents + cellTransformerEvents) as Sequence<TableViewListenerEvent<Any>>)
        }

        return listenerRef
    }

    internal fun haveListeners(): Boolean {
        return tableViewListeners.size > 0 ||
                columnViewListeners.size > 0 ||
                rowViewListeners.size > 0 ||
                //cellRangeViewListeners.size > 0 ||
                cellViewListeners.size > 0
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
    internal fun publish(views: List<TableViewListenerEvent<Any>>) {
        val buffer = eventBuffer

        if (buffer != null) {
            buffer.addAll(views)

            activeListener.get()?.let {
                activeListeners.get()?.add(it) ?: activeListeners.set(mutableSetOf(it))
            }

            return
        }

        eventBuffer = views.toMutableList()

        publish(false)
    }

    @Synchronized
    internal fun publish(rebase: Boolean) {
        if (rebase) {
            eventBuffer?.apply {
                if (this.isEmpty()) return@apply

                val oldView = tableViewFromViewRelated(this.first().oldValue)
                val newView = tableViewFromViewRelated(this.last().newValue)

                val locations = LinkedHashMap<Triple<Header?, Long?, KClass<*>>, Any>()

                this.forEach {
                    val location = Triple(columnViewFromViewRelated(it.newValue)?.header, indexFromViewRelated(it.newValue), it.newValue::class)
                    if (locations[location] != null) locations.remove(location)
                    locations[location] = it.newValue
                }

                val updated = mutableListOf<TableViewListenerEvent<Any>>()

                locations.values.forEach {
                    updated.add(TableViewListenerEvent(relatedFromViewRelated(oldView, it), relatedFromViewRelated(newView, it)) as TableViewListenerEvent<Any>)
                }

                eventBuffer = updated
            }
        }

        try {
            while (true) {
                val batch = Collections.unmodifiableList(eventBuffer ?: break)
                if (batch.isEmpty()) break

                eventBuffer = mutableListOf()

                tableViewListeners
                    .values
                    .forEach { listenerRef ->
                        loopCheck(listenerRef.listenerReference)
                        listenerRef.listenerEvent(batch.asSequence().filter { refVersionFromViewRelated(it.newValue) > listenerRef.version })
                        // TODO Do we really need to clear this here and below?
                        if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                    }

                columnViewListeners
                    .values
                    .forEach { listenerRef ->
                        val columnBatch = Collections.unmodifiableList(batch.filter {
                            val newValueColumn = columnViewFromViewRelated(it.newValue)
                            val oldValueColumn = columnViewFromViewRelated(it.oldValue)
                            return@filter newValueColumn?.header == listenerRef.listenerReference.columnView.header
                                    || oldValueColumn?.header == listenerRef.listenerReference.columnView.header
                        }.filter { refVersionFromViewRelated(it.newValue) > listenerRef.version })

                        if (columnBatch.isNotEmpty()) {
                            loopCheck(listenerRef.listenerReference)
                            listenerRef.listenerEvent(columnBatch.asSequence())
                            if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                        }
                    }

                rowViewListeners
                    .values
                    .forEach { listenerRef ->
                        val rowBatch = Collections.unmodifiableList(batch.filter {
                            // TODO This filtering will need to take into account the index relation if supported?
                            val newValueIndex = indexFromViewRelated(it.newValue)
                            val oldValueIndex = indexFromViewRelated(it.oldValue)
                            return@filter newValueIndex == listenerRef.listenerReference.rowView.index
                                    || oldValueIndex == listenerRef.listenerReference.rowView.index
                        }.filter { refVersionFromViewRelated(it.newValue) > listenerRef.version })

                        if (rowBatch.isNotEmpty()) {
                            loopCheck(listenerRef.listenerReference)
                            listenerRef.listenerEvent(rowBatch.asSequence())
                            if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
                        }
                    }

                // TODO cellRangeView .. rowRangeViewListeners..? columnRangeView?

                cellViewListeners
                    .values
                    .forEach { listenerRef ->
                        val cellBatch = Collections.unmodifiableList(batch.filter {
                            val newValueColumn = columnViewFromViewRelated(it.newValue)
                            val oldValueColumn = columnViewFromViewRelated(it.oldValue)
                            val newValueIndex = indexFromViewRelated(it.newValue)
                            val oldValueIndex = indexFromViewRelated(it.oldValue)
                            return@filter (listenerRef.listenerReference.cellView.index == newValueIndex
                                        && listenerRef.listenerReference.cellView.columnView.header == newValueColumn?.header)
                                    || (listenerRef.listenerReference.cellView.index == oldValueIndex
                                        && listenerRef.listenerReference.cellView.columnView.header == oldValueColumn?.header)
                        }.filter { refVersionFromViewRelated(it.newValue) > listenerRef.version })

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

    private fun loopCheck(listenerReference: TableViewListenerReference) {
        if (listenerReference.allowLoop) return
        if (activeListeners.get()?.contains(listenerReference) == true) throw ListenerLoopException(listenerReference)
        activeListener.set(listenerReference)
    }

    internal fun shutdown() {
        tableViewListeners.clear()
        columnViewListeners.clear()
        rowViewListeners.clear()
        // TODO cellRangeViewListeners.clear()
        cellViewListeners.clear()
        //derivedCellViewListeners.clear()
    }

    companion object {
        private val idGenerator = AtomicLong()
        private val activeListener = ThreadLocal<TableViewListenerReference?>()
        private val activeListeners = ThreadLocal<MutableSet<TableViewListenerReference>?>()
    }
}
