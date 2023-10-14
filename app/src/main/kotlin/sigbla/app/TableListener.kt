package sigbla.app

import sigbla.app.exceptions.InvalidSequenceException

abstract class TableListenerReference {
    abstract val name: String?
    abstract val order: Long
    abstract val allowLoop: Boolean
    internal abstract fun unsubscribe()

    override fun toString(): String {
        return "TableListenerReference(name=${name}, order=${order}, allowLoop=${allowLoop})"
    }
}

data class TableListenerEvent<O, N>(val oldValue: Cell<O>, val newValue: Cell<N>)

class TableEventReceiver<S, O, N>(
    val source: S,
    var name: String? = null,
    var order: Long = 0,
    var allowLoop: Boolean = false,
    var skipHistory: Boolean = false,
    private val typeFilter: Sequence<TableListenerEvent<Any, Any>>.() -> Sequence<TableListenerEvent<Any, Any>>
) {
    lateinit var reference: TableListenerReference
        internal set

    private var process: (Sequence<TableListenerEvent<O, N>>.() -> Unit) = {}

    fun events(process: Sequence<TableListenerEvent<out O, out N>>.() -> Unit) {
        this.process = process
    }

    // TODO Shouldn't events be Sequence<TableListenerEvent<out Any, out Any>> ?
    internal operator fun invoke(events: Sequence<TableListenerEvent<Any, Any>>) {
        val seq = (events.typeFilter() as Sequence<TableListenerEvent<O, N>>)
        if (seq.any()) seq.process()
    }
}

// TODO Can these be lazy? I.e., reuse same value?
//      Can probably do something with WeakHashMap..
val Sequence<TableListenerEvent<*, *>>.newTable: Table
    get() = this.firstOrNull()?.let {
        return it.newValue.column.table
    } ?: throw InvalidSequenceException()

val Sequence<TableListenerEvent<*, *>>.oldTable: Table
    get() = this.firstOrNull()?.let {
        return it.oldValue.column.table
    } ?: throw InvalidSequenceException()