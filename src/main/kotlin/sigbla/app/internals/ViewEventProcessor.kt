package sigbla.app.internals

import sigbla.app.*
import sigbla.app.exceptions.InvalidListenerException
import sigbla.app.exceptions.ListenerLoopException
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong

internal class ViewEventProcessor {
    private class ListenerReferenceEvent<R>(
        val listenerReference: R,
        val listenerEvent: (event: Sequence<TableViewListenerEvent<Any>>) -> Unit
    )

    private class ListenerId(val order: Long) : Comparable<ListenerId> {
        val id: Long = idGenerator.getAndIncrement()

        override fun compareTo(other: ListenerId): Int {
            val cmp = this.order.compareTo(other.order)
            if (cmp == 0) return this.id.compareTo(other.id)
            return cmp
        }
    }

    private val tableViewListeners: ConcurrentMap<ListenerId, ListenerReferenceEvent<ListenerTableViewRef>> = ConcurrentSkipListMap()

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
        val view: TableView
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

            // TODO Don't like the potential overlap between when we call clone and that the table might have
            //      been altered just before, but after we added to the tableViewListeners..
            //      It might the listener gets duplicates..
            if (!eventReceiver.skipHistory) {
                // TODO
                /*
                listenerRefEvent.listenerEvent(tableView.clone().asSequence().map {
                    TableViewListenerEvent(..UnitCell(it.column, it.index), it) as TableViewListenerEvent<Any>
                })
                 */
            }
        }
        return listenerRef
    }

    // TODO Not sure if this is worth having?
    internal fun haveListeners(): Boolean {
        return tableViewListeners.size > 0
    }

    internal fun publish(cells: List<TableViewListenerEvent<Any>>) {
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

                tableViewListeners
                    .values
                    .forEach { listenerRef ->
                        synchronized(listenerRef) {
                            loopCheck(listenerRef.listenerReference)
                            listenerRef.listenerEvent(batch.asSequence())
                            // TODO Do we really need to clear this here and below?
                            if (!listenerRef.listenerReference.allowLoop) activeListener.remove()
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
