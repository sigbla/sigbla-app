package sigbla.app

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.min
import kotlin.math.max
import kotlin.reflect.KClass

abstract class Row : Comparable<Row> {
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

    operator fun set(header: ColumnHeader, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(table[header][index]).init()
    operator fun set(vararg header: String, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(table[ColumnHeader(*header)][index]).init()

    operator fun rangeTo(other: Row): RowRange {
        return RowRange(this, other)
    }

    inline fun <reified O, reified N> on(noinline init: TableEventReceiver<Row, O, N>.() -> Unit): TableListenerReference {
        return on(O::class, N::class, init as TableEventReceiver<Row, Any, Any>.() -> Unit)
    }

    fun onAny(init: TableEventReceiver<Row, Any, Any>.() -> Unit): TableListenerReference {
        return on(Any::class, Any::class, init)
    }

    fun on(old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Row, Any, Any>.() -> Unit): TableListenerReference {
        val eventReceiver = when {
            old == Any::class && new == Any::class -> TableEventReceiver<Row, Any, Any>(
                this
            ) { this }
            old == Any::class -> TableEventReceiver(this) {
                this.filter {
                    new.isInstance(
                        it.newValue.value
                    )
                }
            }
            new == Any::class -> TableEventReceiver(this) {
                this.filter {
                    old.isInstance(
                        it.oldValue.value
                    )
                }
            }
            else -> TableEventReceiver(this) {
                this.filter {
                    old.isInstance(it.oldValue.value) && new.isInstance(
                        it.newValue.value
                    )
                }
            }
        }
        return table.eventProcessor.subscribe(this, eventReceiver, init)
    }

    override fun compareTo(other: Row): Int {
        return index.compareTo(other.index)
    }

    override fun toString(): String {
        return index.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Row

        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        return index.hashCode()
    }
}

class BaseRow internal constructor(override val table: Table, override val indexRelation: IndexRelation, override val index: Long) : Row()

class RowRange(override val start: Row, override val endInclusive: Row) : ClosedRange<Row>, Iterable<Row> {
    val table: Table
        get() = start.table

    override fun iterator(): Iterator<Row> {
        return if (start.index <= endInclusive.index) {
            ((start.index)..(endInclusive.index))
                .asSequence()
                .map { table[it] }
                .iterator()
        } else {
            ((endInclusive.index)..(start.index))
                .asSequence()
                .map { table[it] }
                .iterator()
        }
    }

    override fun contains(value: Row): Boolean {
        if (value.index < min(start.index, endInclusive.index) || value.index > max(start.index, endInclusive.index)) {
            return false
        }

        return true
    }

    override fun isEmpty() = false

    override fun toString(): String {
        return "$start..$endInclusive"
    }
}
