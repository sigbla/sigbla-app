package sigbla.app

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

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

    inline fun <reified O, reified N> on(noinline init: EventReceiver<Row, O, N>.() -> Unit): ListenerReference {
        return on(O::class, N::class, init as EventReceiver<Row, Any, Any>.() -> Unit)
    }

    fun onAny(init: EventReceiver<Row, Any, Any>.() -> Unit): ListenerReference {
        return on(Any::class, Any::class, init)
    }

    fun on(old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: EventReceiver<Row, Any, Any>.() -> Unit): ListenerReference {
        val eventReceiver = when {
            old == Any::class && new == Any::class -> EventReceiver<Row, Any, Any>(
                this
            ) { this }
            old == Any::class -> EventReceiver(this) {
                this.filter {
                    new.isInstance(
                        it.newValue.value
                    )
                }
            }
            new == Any::class -> EventReceiver(this) {
                this.filter {
                    old.isInstance(
                        it.oldValue.value
                    )
                }
            }
            else -> EventReceiver(this) {
                this.filter {
                    old.isInstance(it.oldValue.value) && new.isInstance(
                        it.newValue.value
                    )
                }
            }
        }
        return table.eventProcessor.subscribe(this, eventReceiver, init)
    }

    // TODO: Row range
}

class BaseRow internal constructor(override val table: Table, override val indexRelation: IndexRelation, override val index: Long) : Row()
