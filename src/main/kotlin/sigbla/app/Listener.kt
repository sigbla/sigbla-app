package sigbla.app

import sigbla.app.exceptions.InvalidSequenceException

interface ListenerReference {
    val name: String?
    val order: Long
    fun unsubscribe()
}

data class ListenerEvent<O, N>(val oldValue: Cell<O>, val newValue: Cell<N>)

class EventReceiver<S, O, N>(
    val source: S,
    private val typeFilter: Sequence<ListenerEvent<Any, Any>>.() -> Sequence<ListenerEvent<Any, Any>>
) {
    lateinit var reference: ListenerReference
        internal set

    var name: String? = null
    var order: Long = 0
    var skipHistory: Boolean = false

    private var process: (Sequence<ListenerEvent<O, N>>.() -> Unit) = {}

    fun events(process: Sequence<ListenerEvent<out O, out N>>.() -> Unit) {
        this.process = process
    }

    internal operator fun invoke(events: Sequence<ListenerEvent<Any, Any>>) {
        val seq = (events.typeFilter() as Sequence<ListenerEvent<O, N>>)
        if (seq.any()) seq.process()
    }
}

val Sequence<ListenerEvent<*, *>>.newTable: Table
    get() = this.firstOrNull()?.let {
        // TODO Needs to be the table clone for new value
        //      Let event processors modify the newTable and oldTable,
        //      but since both of these are clones, it does not affect the
        //      actual table. The reason for letting them modify these
        //      are so that later event processors see what they passed on.
        return it.newValue.column.table
    } ?: throw InvalidSequenceException()

val Sequence<ListenerEvent<*, *>>.oldTable: Table
    get() = this.firstOrNull()?.let {
        // TODO Needs to be the table clone for old value
        return it.oldValue.column.table
    } ?: throw InvalidSequenceException()