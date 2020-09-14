package sigbla.app

import sigbla.app.exceptions.InvalidSequenceException

abstract class ListenerReference {
    abstract val name: String?
    abstract val order: Long
    abstract val allowLoop: Boolean
    abstract fun unsubscribe()

    override fun toString(): String {
        return "ListenerReference(name=${name}, order=${order}, allowLoop=${allowLoop})"
    }
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
    var allowLoop: Boolean = false
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
        return it.newValue.column.table
    } ?: throw InvalidSequenceException()

val Sequence<ListenerEvent<*, *>>.oldTable: Table
    get() = this.firstOrNull()?.let {
        return it.oldValue.column.table
    } ?: throw InvalidSequenceException()