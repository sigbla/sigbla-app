package sigbla.app

import sigbla.app.exceptions.InvalidSequenceException

abstract class TableViewListenerReference {
    abstract val name: String?
    abstract val order: Long
    abstract val allowLoop: Boolean
    abstract fun unsubscribe()

    override fun toString(): String {
        return "ViewListenerReference(name=${name}, order=${order}, allowLoop=${allowLoop})"
    }
}

data class TableViewListenerEvent<T>(val oldValue: Area<T>, val newValue: Area<T>)

class TableViewEventReceiver<S, T>(
    val source: S,
    private val typeFilter: Sequence<TableViewListenerEvent<Any>>.() -> Sequence<TableViewListenerEvent<Any>>
) {
    lateinit var reference: TableViewListenerReference
        internal set

    var name: String? = null
    var order: Long = 0
    var allowLoop: Boolean = false
    var skipHistory: Boolean = false

    private var process: (Sequence<TableViewListenerEvent<T>>.() -> Unit) = {}

    fun events(process: Sequence<TableViewListenerEvent<out T>>.() -> Unit) {
        this.process = process
    }

    internal operator fun invoke(events: Sequence<TableViewListenerEvent<Any>>) {
        val seq = (events.typeFilter() as Sequence<TableViewListenerEvent<T>>)
        if (seq.any()) seq.process()
    }
}

val Sequence<TableViewListenerEvent<*>>.newView: TableView
    get() = this.firstOrNull()?.let {
        return it.newValue.view
    } ?: throw InvalidSequenceException()

val Sequence<TableViewListenerEvent<*>>.oldView: TableView
    get() = this.firstOrNull()?.let {
        return it.oldValue.view
    } ?: throw InvalidSequenceException()
