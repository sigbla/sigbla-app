package sigbla.app

import sigbla.app.exceptions.InvalidSequenceException
import java.util.*

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

internal val newTableRefs = Collections.synchronizedMap(WeakHashMap<Sequence<TableListenerEvent<*, *>>, Table>())
internal val oldTableRefs = Collections.synchronizedMap(WeakHashMap<Sequence<TableListenerEvent<*, *>>, Table>())

// TODO See if val Sequence<TableListenerEvent<*, *>>.newTable: Table by lazy { .. } is a better option? Check lifecycle..
val Sequence<TableListenerEvent<*, *>>.newTable: Table
    get() {
        val table = newTableRefs[this]
        if (table != null) return table

        this.firstOrNull()?.let {
            val table = it.newValue.column.table
            newTableRefs[this] = table
            return table
        } ?: throw InvalidSequenceException()
    }

val Sequence<TableListenerEvent<*, *>>.oldTable: Table
    get() {
        val table = oldTableRefs[this]
        if (table != null) return table

        this.firstOrNull()?.let {
            val table = it.oldValue.column.table
            oldTableRefs[this] = table
            return table
        } ?: throw InvalidSequenceException()
    }
