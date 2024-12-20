/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidSequenceException
import java.util.*

// TODO Should likely be (partially) internal?
abstract class TableListenerReference {
    abstract val name: String?
    abstract val order: Long
    abstract val allowLoop: Boolean
    abstract val skipHistory: Boolean
    internal abstract fun unsubscribe()

    override fun toString() = "TableListenerReference[${name}, order=${order}, allowLoop=${allowLoop}]"
}

class TableListenerEvent<O, N>(val oldValue: Cell<O>, val newValue: Cell<N>) {
    val table: Table
        get() = newValue.table

    val column: Column
        get() = newValue.column

    val index: Long
        get() = newValue.index

    override fun toString() = "TableListenerEvent[$oldValue -> $newValue]"
}

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

    private var processor: (Sequence<TableListenerEvent<O, N>>.() -> Unit) = {}

    fun events(processor: Sequence<TableListenerEvent<out O, out N>>.() -> Unit) {
        this.processor = processor
    }

    // TODO Shouldn't events be Sequence<TableListenerEvent<out Any, out Any>> ?
    internal operator fun invoke(events: Sequence<TableListenerEvent<Any, Any>>) {
        val seq = (events.typeFilter() as Sequence<TableListenerEvent<O, N>>)
        if (seq.any()) seq.processor()
    }
}

internal val newTableRefs = Collections.synchronizedMap(WeakHashMap<Sequence<TableListenerEvent<*, *>>, Table>())
internal val oldTableRefs = Collections.synchronizedMap(WeakHashMap<Sequence<TableListenerEvent<*, *>>, Table>())

val Sequence<TableListenerEvent<*, *>>.newTable: Table
    get() {
        val table = newTableRefs[this]
        if (table != null) return table

        this.firstOrNull()?.let {
            val table = it.newValue.column.table
            newTableRefs[this] = table
            return table
        } ?: throw InvalidSequenceException("No event in sequence")
    }

val Sequence<TableListenerEvent<*, *>>.oldTable: Table
    get() {
        val table = oldTableRefs[this]
        if (table != null) return table

        this.firstOrNull()?.let {
            val table = it.oldValue.column.table
            oldTableRefs[this] = table
            return table
        } ?: throw InvalidSequenceException("No event in sequence")
    }
