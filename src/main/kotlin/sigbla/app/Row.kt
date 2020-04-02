package sigbla.app

import java.math.BigDecimal
import java.math.BigInteger

abstract class Row {
    abstract val table: Table

    abstract val indexRelation: IndexRelation

    abstract val index: Long

    val headers: Collection<ColumnHeader>
        get() = table.headers

    operator fun get(header: ColumnHeader): Cell<*> = table[header][indexRelation, index]

    operator fun get(vararg columnHeader: String): Cell<*> = get(
        ColumnHeader(
            *columnHeader
        )
    )

    // TODO Unsure if it is correct to remove based on indexRelation. Needs to work relative to set below as well..
    // TODO Compare to column behaviour?
    fun remove(header: ColumnHeader) = table[header].remove(table[header][indexRelation, index].index)

    fun remove(vararg header: String) = remove(ColumnHeader(*header))

    // TODO Float and Int
    operator fun set(header: ColumnHeader, value: Cell<*>?) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: String) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Double) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Long) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: BigInteger) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: BigDecimal) = table[header].set(index, value)
    operator fun set(header: ColumnHeader, value: Number) = table[header].set(index, value)

    // TODO Float and Int
    operator fun set(vararg header: String, value: Cell<*>?) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: String) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Double) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Long) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: BigInteger) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: BigDecimal) = table[ColumnHeader(*header)].set(index, value)
    operator fun set(vararg header: String, value: Number) = table[ColumnHeader(*header)].set(index, value)

    inline fun <reified O, reified N> subscribe(crossinline listener: (eventReceiver: ListenerEventReceiver<Row, O, N>) -> Unit): ListenerReference {
        return subscribeAny { receiver ->
            val events = receiver.events.filter {
                it.oldValue.value is O && it.newValue.value is N
            } as List<ListenerEvent<out O, out N>>
            if (events.isNotEmpty()) listener.invoke(ListenerEventReceiver(this, receiver.listenerReference, events))
        }
    }

    fun subscribeAny(listener: (eventReceiver: ListenerEventReceiver<Row, *, *>) -> Unit): ListenerReference {
        return table.eventProcessor.subscribe(this) {
            if (it.events.isNotEmpty()) listener.invoke(ListenerEventReceiver(this, it.listenerReference, it.events))
        }
    }

    // TODO: Row range
}

class BaseRow internal constructor(override val table: Table, override val indexRelation: IndexRelation, override val index: Long) : Row()
