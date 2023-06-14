package sigbla.app

import sigbla.app.exceptions.InvalidSequenceException
import sigbla.app.exceptions.SigblaAppException
import kotlin.reflect.KClass

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
    val type: KClass<*> = Any::class,
    private val typeFilter: Sequence<TableViewListenerEvent<Any>>.() -> Sequence<TableViewListenerEvent<T>>
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
        val seq = events.typeFilter()
        if (seq.any()) seq.process()
    }
}

// TODO Can these be lazy? I.e., reuse same value?
val Sequence<TableViewListenerEvent<*>>.newView: TableView
    get() = this.firstOrNull()?.let {
        tableViewFromViewRelated(it.newValue)
    } ?: throw InvalidSequenceException()

val Sequence<TableViewListenerEvent<*>>.oldView: TableView
    get() = this.firstOrNull()?.let {
        tableViewFromViewRelated(it.oldValue)
    } ?: throw InvalidSequenceException()
