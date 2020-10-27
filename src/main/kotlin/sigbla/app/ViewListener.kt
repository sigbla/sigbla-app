package sigbla.app

abstract class TableViewListenerReference {
    abstract val name: String?
    abstract val order: Long
    abstract val allowLoop: Boolean
    abstract fun unsubscribe()

    override fun toString(): String {
        return "ViewListenerReference(name=${name}, order=${order}, allowLoop=${allowLoop})"
    }
}

data class TableViewListenerEvent<T>(val oldValue: T, val newValue: T)

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

// TODO
/*
val Sequence<TableListenerEvent<*, *>>.newTable: Table
    get() = this.firstOrNull()?.let {
        return it.newValue.column.table
    } ?: throw InvalidSequenceException()

val Sequence<TableListenerEvent<*, *>>.oldTable: Table
    get() = this.firstOrNull()?.let {
        return it.oldValue.column.table
    } ?: throw InvalidSequenceException()
*/