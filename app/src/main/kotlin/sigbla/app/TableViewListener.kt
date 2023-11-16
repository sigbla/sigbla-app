package sigbla.app

import sigbla.app.exceptions.InvalidSequenceException
import java.util.*
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

class TableViewListenerEvent<T>(val oldValue: T, val newValue: T) {
    override fun toString(): String {
        return "TableViewListenerEvent(oldValue=$oldValue, newValue=$newValue)"
    }
}

class TableViewEventReceiver<S, T>(
    val source: S,
    val type: KClass<*> = Any::class,
    var name: String? = null,
    var order: Long = 0,
    var allowLoop: Boolean = false,
    var skipHistory: Boolean = false,
    private val typeFilter: Sequence<TableViewListenerEvent<Any>>.() -> Sequence<TableViewListenerEvent<T>>
) {
    lateinit var reference: TableViewListenerReference
        internal set

    private var process: (Sequence<TableViewListenerEvent<T>>.() -> Unit) = {}

    fun events(process: Sequence<TableViewListenerEvent<out T>>.() -> Unit) {
        this.process = process
    }

    internal operator fun invoke(events: Sequence<TableViewListenerEvent<Any>>) {
        val seq = events.typeFilter()
        if (seq.any()) seq.process()
    }
}

internal val newViewRefs = Collections.synchronizedMap(WeakHashMap<Sequence<TableViewListenerEvent<*>>, TableView>())
internal val oldViewRefs = Collections.synchronizedMap(WeakHashMap<Sequence<TableViewListenerEvent<*>>, TableView>())

// TODO See if val Sequence<TableListenerEvent<*, *>>.newView: TableView by lazy { .. } is a better option? Check lifecycle..
val Sequence<TableViewListenerEvent<*>>.newView: TableView
    get() {
        val view = newViewRefs[this]
        if (view != null) return view

        this.firstOrNull()?.let {
            val view = tableViewFromViewRelated(it.newValue)
            newViewRefs[this] = view
            return view
        } ?: throw InvalidSequenceException()
    }

val Sequence<TableViewListenerEvent<*>>.oldView: TableView
    get() {
        val view = oldViewRefs[this]
        if (view != null) return view

        this.firstOrNull()?.let {
            val view = tableViewFromViewRelated(it.oldValue)
            oldViewRefs[this] = view
            return view
        } ?: throw InvalidSequenceException()
    }
