/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidSequenceException
import java.util.*
import kotlin.reflect.KClass

// TODO Should likely be (partially) internal?
abstract class TableViewListenerReference {
    abstract val name: String?
    abstract val order: Long
    abstract val allowLoop: Boolean
    abstract fun unsubscribe()

    override fun toString() = "TableViewListenerReference[${name}, order=${order}, allowLoop=${allowLoop}]"
}

class TableViewListenerEvent<T>(val oldValue: T, val newValue: T) {
    val tableView: TableView by lazy { tableViewFromViewRelated(newValue) }

    val columnView: ColumnView? by lazy { columnViewFromViewRelated(newValue) }

    val index: Long? by lazy { indexFromViewRelated(newValue) }

    override fun toString() = "TableViewListenerEvent[$oldValue -> $newValue]"
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

    private var processor: (Sequence<TableViewListenerEvent<T>>.() -> Unit) = {}

    fun events(processor: Sequence<TableViewListenerEvent<out T>>.() -> Unit) {
        this.processor = processor
    }

    internal operator fun invoke(events: Sequence<TableViewListenerEvent<Any>>) {
        val seq = events.typeFilter()
        if (seq.any()) seq.processor()
    }
}

internal val newViewRefs = Collections.synchronizedMap(WeakHashMap<Sequence<TableViewListenerEvent<*>>, TableView>())
internal val oldViewRefs = Collections.synchronizedMap(WeakHashMap<Sequence<TableViewListenerEvent<*>>, TableView>())

val Sequence<TableViewListenerEvent<*>>.newView: TableView
    get() {
        val view = newViewRefs[this]
        if (view != null) return view

        this.firstOrNull()?.let {
            val view = tableViewFromViewRelated(it.newValue)
            newViewRefs[this] = view
            return view
        } ?: throw InvalidSequenceException("No event in sequence")
    }

val Sequence<TableViewListenerEvent<*>>.oldView: TableView
    get() {
        val view = oldViewRefs[this]
        if (view != null) return view

        this.firstOrNull()?.let {
            val view = tableViewFromViewRelated(it.oldValue)
            oldViewRefs[this] = view
            return view
        } ?: throw InvalidSequenceException("No event in sequence")
    }
