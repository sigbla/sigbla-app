package sigbla.app

import sigbla.app.exceptions.InvalidCellException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.exceptions.InvalidValueException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.util.*
import kotlin.math.min
import kotlin.math.max
import kotlin.reflect.KClass

internal fun String.toCell(column: Column, index: Long) =
    StringCell(column, index, this)
internal fun Long.toCell(column: Column, index: Long) =
    LongCell(column, index, this)
internal fun Double.toCell(column: Column, index: Long) =
    DoubleCell(column, index, this)
internal fun BigInteger.toCell(column: Column, index: Long): BigIntegerCell =
    BigIntegerCell(column, index, this)
internal fun BigDecimal.toCell(column: Column, index: Long): BigDecimalCell =
    BigDecimalCell(column, index, this)
internal fun WebContent.toCell(column: Column, index: Long): WebCell =
    WebCell(column, index, this)

internal class CellValue<T>(private val value: T) {
    fun toCell(column: Column, index: Long): Cell<*> {
        return when (value) {
            is String -> value.toCell(column, index)
            is Long -> value.toCell(column, index)
            is Double -> value.toCell(column, index)
            is BigInteger -> value.toCell(column, index)
            is BigDecimal -> value.toCell(column, index)
            is WebContent -> value.toCell(column, index)
            else -> throw UnsupportedOperationException("Unable to convert to cell: $value")
        }
    }
}

enum class CellRangeOrder { COLUMN, ROW }

// TODO change order input so it can be t[][]..t[][] by ORDER
class CellRange(override val start: Cell<*>, override val endInclusive: Cell<*>, val order: CellRangeOrder = CellRangeOrder.COLUMN) : ClosedRange<Cell<*>>, Iterable<Cell<*>> {
    init {
        if (start.column.table != endInclusive.column.table) {
            throw InvalidTableException("Cell range much be within same table")
        }
    }

    val table: Table
        get() = start.table

    override fun iterator(): Iterator<Cell<*>> {
        // TODO: This iterator implementation, as with the ColumnRange one could be more efficient!

        val columns = (start.column..endInclusive.column).toList()
        val rows = if (start.index <= endInclusive.index) {
            (start.index..endInclusive.index).toList()
        } else {
            (start.index downTo endInclusive.index).toList()
        }

        val output = mutableListOf<Cell<*>>()

        if (order == CellRangeOrder.COLUMN) {
            rows.forEach { row ->
                columns.forEach { column ->
                    output.add(column[row])
                }
            }
        } else {
            columns.forEach { column ->
                rows.forEach { row ->
                    output.add(column[row])
                }
            }
        }

        return output.iterator()
    }

    operator fun contains(value: Any): Boolean {
        iterator().forEach {
            return it.value == value
        }

        return false
    }

    override fun contains(value: Cell<*>): Boolean {
        if (value.index < min(start.index, endInclusive.index) || value.index > max(start.index, endInclusive.index)) {
            return false
        }

        return if (start.column.columnOrder <= endInclusive.column.columnOrder) {
            (start.column..endInclusive.column).contains(value.column)
        } else {
            (endInclusive.column..start.column).contains(value.column)
        }
    }

    override fun isEmpty(): Boolean = false

    inline fun <reified O, reified N> on(noinline init: TableEventReceiver<CellRange, O, N>.() -> Unit): TableListenerReference {
        return on(O::class, N::class, init as TableEventReceiver<CellRange, Any, Any>.() -> Unit)
    }

    fun onAny(init: TableEventReceiver<CellRange, Any, Any>.() -> Unit): TableListenerReference {
        return on(Any::class, Any::class, init)
    }

    fun on(old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<CellRange, Any, Any>.() -> Unit): TableListenerReference {
        val eventReceiver = when {
            old == Any::class && new == Any::class -> TableEventReceiver<CellRange, Any, Any>(
                this
            ) { this }
            old == Any::class -> TableEventReceiver(this) {
                this.filter {
                    new.isInstance(it.newValue.value)
                }
            }
            new == Any::class -> TableEventReceiver(this) {
                this.filter {
                    old.isInstance(it.oldValue.value)
                }
            }
            else -> TableEventReceiver(this) {
                this.filter {
                    old.isInstance(it.oldValue.value) && new.isInstance(it.newValue.value)
                }
            }
        }
        return start.column.table.eventProcessor.subscribe(this, eventReceiver, init)
    }

    // TODO: Implement assignment ops?
}

sealed class Cell<T>(val column: Column, val index: Long) : Comparable<Any?> {
    abstract val value: T

    val table: Table
        get() = column.table

    internal abstract fun toCell(column: Column, index: Long): Cell<T>

    internal fun toCellValue() = CellValue(value)

    open fun isNumeric(): Boolean = false
    open fun isText(): Boolean = false

    open fun toLong(): Long = throw InvalidCellException("Cell not numeric at $column:$index")
    open fun toDouble(): Double = throw InvalidCellException("Cell not numeric at $column:$index")
    open fun toBigInteger(): BigInteger = throw InvalidCellException("Cell not numeric at $column:$index")
    open fun toBigDecimal(): BigDecimal = toBigDecimal(DefaultBigDecimalPrecision.mathContext)
    open fun toBigDecimal(mathContext: MathContext): BigDecimal = throw InvalidCellException("Cell not numeric at $column:$index")
    open fun toNumber(): Number = throw InvalidCellException("Cell not numeric at $column:$index")

    operator fun plus(that: Cell<*>): Number {
        return when (that.value) {
            is Long -> plus(that.toLong())
            is Double -> plus(that.toDouble())
            is BigInteger -> plus(that.toBigInteger())
            is BigDecimal -> plus(that.toBigDecimal())
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun plus(that: Number): Number {
        return when (that) {
            is Int -> plus(that.toLong())
            is Long -> plus(that)
            is Float -> plus(that.toDouble())
            is Double -> plus(that)
            is BigInteger -> plus(that)
            is BigDecimal -> plus(that)
            else -> throw InvalidValueException("Unsupported type: ${that::class}")
        }
    }

    open operator fun plus(that: Int): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun plus(that: Long): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun plus(that: Float): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun plus(that: Double): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun plus(that: BigInteger): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun plus(that: BigDecimal): Number = throw InvalidCellException("Cell not numeric at $column:$index")

    operator fun minus(that: Cell<*>): Number {
        return when (that.value) {
            is Long -> minus(that.toLong())
            is Double -> minus(that.toDouble())
            is BigInteger -> minus(that.toBigInteger())
            is BigDecimal -> minus(that.toBigDecimal())
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun minus(that: Number): Number {
        return when (that) {
            is Int -> minus(that.toLong())
            is Long -> minus(that)
            is Float -> minus(that.toDouble())
            is Double -> minus(that)
            is BigInteger -> minus(that)
            is BigDecimal -> minus(that)
            else -> throw InvalidValueException("Unsupported type: ${that::class}")
        }
    }

    open operator fun minus(that: Int): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun minus(that: Long): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun minus(that: Float): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun minus(that: Double): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun minus(that: BigInteger): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun minus(that: BigDecimal): Number = throw InvalidCellException("Cell not numeric at $column:$index")

    operator fun times(that: Cell<*>): Number {
        return when (that.value) {
            is Long -> times(that.toLong())
            is Double -> times(that.toDouble())
            is BigInteger -> times(that.toBigInteger())
            is BigDecimal -> times(that.toBigDecimal())
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun times(that: Number): Number {
        return when (that) {
            is Int -> times(that.toLong())
            is Long -> times(that)
            is Float -> times(that.toDouble())
            is Double -> times(that)
            is BigInteger -> times(that)
            is BigDecimal -> times(that)
            else -> throw InvalidValueException("Unsupported type: ${that::class}")
        }
    }

    open operator fun times(that: Int): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun times(that: Long): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun times(that: Float): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun times(that: Double): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun times(that: BigInteger): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun times(that: BigDecimal): Number = throw InvalidCellException("Cell not numeric at $column:$index")

    operator fun div(that: Cell<*>): Number {
        return when (that.value) {
            is Long -> div(that.toLong())
            is Double -> div(that.toDouble())
            is BigInteger -> div(that.toBigInteger())
            is BigDecimal -> div(that.toBigDecimal())
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun div(that: Number): Number {
        return when (that) {
            is Int -> div(that.toLong())
            is Long -> div(that)
            is Float -> div(that.toDouble())
            is Double -> div(that)
            is BigInteger -> div(that)
            is BigDecimal -> div(that)
            else -> throw InvalidValueException("Unsupported type: ${that::class}")
        }
    }

    open operator fun div(that: Int): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun div(that: Long): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun div(that: Float): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun div(that: Double): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun div(that: BigInteger): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun div(that: BigDecimal): Number = throw InvalidCellException("Cell not numeric at $column:$index")

    operator fun rem(that: Cell<*>): Number {
        return when (that.value) {
            is Long -> rem(that.toLong())
            is Double -> rem(that.toDouble())
            is BigInteger -> rem(that.toBigInteger())
            is BigDecimal -> rem(that.toBigDecimal())
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun rem(that: Number): Number {
        return when (that) {
            is Int -> rem(that.toLong())
            is Long -> rem(that)
            is Float -> rem(that.toDouble())
            is Double -> rem(that)
            is BigInteger -> rem(that)
            is BigDecimal -> rem(that)
            else -> throw InvalidValueException("Unsupported type: ${that::class}")
        }
    }

    open operator fun rem(that: Int): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun rem(that: Long): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun rem(that: Float): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun rem(that: Double): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun rem(that: BigInteger): Number = throw InvalidCellException("Cell not numeric at $column:$index")
    open operator fun rem(that: BigDecimal): Number = throw InvalidCellException("Cell not numeric at $column:$index")

    override fun compareTo(other: Any?): Int {
        val value = this.value

        // Rules:
        // Null is less than everything else
        // Numbers are compared to each other
        // A non-number is greater than a number
        // Everything else compared as strings

        return when {
            // Cell case
            other is Cell<*> -> compareTo(other.value)

            // Null cases
            other == null -> if (value == null) 0 else 1
            value == null -> -1

            // Number case
            other is Number -> if (value is Number) {
                when (val v = this - other) {
                    is Long -> v.compareTo(0)
                    is Double -> v.compareTo(0)
                    is BigDecimal -> v.compareTo(BigDecimal.ZERO)
                    is BigInteger -> v.compareTo(BigInteger.ZERO)
                    else -> throw InvalidValueException("Unsupported type: ${v::class}")
                }
            } else 1 // If this is not a number, make it greater than other number

            // Compare values as strings
            else -> value.toString().compareTo(other.toString())
        }
    }

    operator fun rangeTo(that: Cell<*>): CellRange {
        return CellRange(this, that)
    }

    operator fun contains(that: Any): Boolean {
        return compareTo(that) == 0
    }

    inline fun <reified O, reified N> on(noinline init: TableEventReceiver<Cell<*>, O, N>.() -> Unit): TableListenerReference {
        return on(O::class, N::class, init as TableEventReceiver<Cell<*>, Any, Any>.() -> Unit)
    }

    fun onAny(init: TableEventReceiver<Cell<*>, Any, Any>.() -> Unit): TableListenerReference {
        return on(Any::class, Any::class, init)
    }

    fun on(old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Cell<*>, Any, Any>.() -> Unit): TableListenerReference {
        val eventReceiver = when {
            old == Any::class && new == Any::class -> TableEventReceiver<Cell<*>, Any, Any>(
                this
            ) { this }
            old == Any::class -> TableEventReceiver<Cell<*>, Any, Any>(this) {
                this.filter {
                    new.isInstance(
                        it.newValue.value
                    )
                }
            }
            new == Any::class -> TableEventReceiver<Cell<*>, Any, Any>(this) {
                this.filter {
                    old.isInstance(
                        it.oldValue.value
                    )
                }
            }
            else -> TableEventReceiver<Cell<*>, Any, Any>(this) {
                this.filter {
                    old.isInstance(
                        it.oldValue.value
                    ) && new.isInstance(it.newValue.value)
                }
            }
        }
        return column.table.eventProcessor.subscribe(this, eventReceiver, init)
    }

    fun asSequence(): Sequence<Cell<*>> = sequenceOf(this)

    infix fun `=`(value: BigDecimal) {
        table[this] = value
    }

    infix fun `=`(value: BigInteger) {
        table[this] = value
    }

    infix fun `=`(value: Double) {
        table[this] = value
    }

    infix fun `=`(value: Long) {
        table[this] = value
    }

    infix fun `=`(value: Number) {
        table[this] = value
    }

    infix fun `=`(value: String) {
        table[this] = value
    }

    infix fun `=`(value: Cell<*>?) {
        table[this] = value
    }

    override fun equals(other: Any?): Boolean {
        return this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(this.value)
    }

    override fun toString(): String {
        return this.value.toString()
    }
}

class UnitCell(column: Column, index: Long) : Cell<Unit>(column, index) {
    override val value = Unit

    override fun toCell(column: Column, index: Long): Cell<Unit> =
        UnitCell(column, index)

    override fun toString() = ""
}

class StringCell(column: Column, index: Long, override val value: String) : Cell<String>(column, index) {
    override fun toCell(column: Column, index: Long): Cell<String> =
        StringCell(column, index, this.value)

    override fun isText() = true
}

class LongCell(column: Column, index: Long, override val value: Long) : Cell<Long>(column, index) {
    override fun toCell(column: Column, index: Long): Cell<Long> =
        LongCell(column, index, this.value)

    override fun isNumeric() = true

    override fun toLong() = value

    override fun toDouble() = value.toDouble()

    override fun toBigInteger() = value.toBigInteger()

    override fun toBigDecimal(mathContext: MathContext) = value.toBigDecimal(mathContext)

    override fun toNumber(): Number = value

    override fun plus(that: Int) = plus(that.toLong())

    override fun plus(that: Long) = this.value + that

    override fun plus(that: Float) = plus(that.toDouble())

    override fun plus(that: Double) = this.value + that

    override fun plus(that: BigInteger) = this.value.toBigInteger().add(that)!!

    override fun plus(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).add(that)!!

    override fun minus(that: Int) = minus(that.toLong())

    override fun minus(that: Long) = this.value - that

    override fun minus(that: Float) = minus(that.toDouble())

    override fun minus(that: Double) = this.value - that

    override fun minus(that: BigInteger) = this.value.toBigInteger().subtract(that)!!

    override fun minus(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).subtract(that)!!

    override fun times(that: Int) = times(that.toLong())

    override fun times(that: Long) = this.value * that

    override fun times(that: Float) = times(that.toDouble())

    override fun times(that: Double) = this.value * that

    override fun times(that: BigInteger) = this.value.toBigInteger().multiply(that)!!

    override fun times(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).multiply(that)!!

    override fun div(that: Int) = div(that.toLong())

    override fun div(that: Long) = this.value / that

    override fun div(that: Float) = div(that.toDouble())

    override fun div(that: Double) = this.value / that

    override fun div(that: BigInteger) = this.value.toBigInteger().divide(that)!!

    override fun div(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).divide(that, DefaultBigDecimalPrecision.divRoundingMode)!!

    override fun rem(that: Int) = rem(that.toLong())

    override fun rem(that: Long) = this.value % that

    override fun rem(that: Float) = rem(that.toDouble())

    override fun rem(that: Double) = this.value % that

    override fun rem(that: BigInteger) = this.value.toBigInteger().remainder(that)!!

    override fun rem(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).remainder(that)!!
}

class DoubleCell(column: Column, index: Long, override val value: Double) : Cell<Double>(column, index) {
    override fun toCell(column: Column, index: Long): Cell<Double> =
        DoubleCell(column, index, this.value)

    override fun isNumeric() = true

    override fun toLong() = value.toLong()

    override fun toDouble() = value

    override fun toBigInteger(): BigInteger = BigInteger.valueOf(value.toLong())

    override fun toBigDecimal(mathContext: MathContext) = value.toBigDecimal(mathContext)

    override fun toNumber(): Number = value

    override fun plus(that: Int) = plus(that.toLong())

    override fun plus(that: Long) = this.value + that

    override fun plus(that: Float) = plus(that.toDouble())

    override fun plus(that: Double) = this.value + that

    override fun plus(that: BigInteger) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).add(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext))!!

    override fun plus(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).add(that)!!

    override fun minus(that: Int) = minus(that.toLong())

    override fun minus(that: Long) = this.value - that

    override fun minus(that: Float) = minus(that.toDouble())

    override fun minus(that: Double) = this.value - that

    override fun minus(that: BigInteger) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).subtract(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext))!!

    override fun minus(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).subtract(that)!!

    override fun times(that: Int) = times(that.toLong())

    override fun times(that: Long) = this.value * that

    override fun times(that: Float) = times(that.toDouble())

    override fun times(that: Double) = this.value * that

    override fun times(that: BigInteger) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).multiply(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext))!!

    override fun times(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).multiply(that)!!

    override fun div(that: Int) = div(that.toLong())

    override fun div(that: Long) = this.value / that

    override fun div(that: Float) = div(that.toDouble())

    override fun div(that: Double) = this.value / that

    override fun div(that: BigInteger) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).divide(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext), DefaultBigDecimalPrecision.divRoundingMode)!!

    override fun div(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).divide(that, DefaultBigDecimalPrecision.divRoundingMode)!!

    override fun rem(that: Int) = rem(that.toLong())

    override fun rem(that: Long) = this.value % that

    override fun rem(that: Float) = rem(that.toDouble())

    override fun rem(that: Double) = this.value % that

    override fun rem(that: BigInteger) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).remainder(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext))!!

    override fun rem(that: BigDecimal) = this.value.toBigDecimal(DefaultBigDecimalPrecision.mathContext).remainder(that)!!
}

class BigIntegerCell(column: Column, index: Long, override val value: BigInteger) : Cell<BigInteger>(column, index) {
    override fun toCell(column: Column, index: Long): Cell<BigInteger> =
        BigIntegerCell(column, index, this.value)

    override fun isNumeric() = true

    override fun toLong() = value.toLong()

    override fun toDouble() = value.toDouble()

    override fun toBigInteger() = value

    override fun toBigDecimal(mathContext: MathContext) = value.toBigDecimal(mathContext = mathContext)

    override fun toNumber(): Number = value

    override fun plus(that: Int) = plus(that.toLong())

    override fun plus(that: Long) = plus(that.toBigInteger())

    override fun plus(that: Float) = plus(that.toDouble())

    override fun plus(that: Double) = plus(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun plus(that: BigInteger) = this.value.add(that)!!

    override fun plus(that: BigDecimal) = this.value.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext).add(that)!!

    override fun minus(that: Int) = minus(that.toLong())

    override fun minus(that: Long) = minus(that.toBigInteger())

    override fun minus(that: Float) = minus(that.toDouble())

    override fun minus(that: Double) = plus(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun minus(that: BigInteger) = this.value.subtract(that)!!

    override fun minus(that: BigDecimal) = this.value.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext).subtract(that)!!

    override fun times(that: Int) = times(that.toLong())

    override fun times(that: Long) = times(that.toBigInteger())

    override fun times(that: Float) = times(that.toDouble())

    override fun times(that: Double) = times(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun times(that: BigInteger) = this.value.multiply(that)!!

    override fun times(that: BigDecimal) = this.value.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext).multiply(that)!!

    override fun div(that: Int) = div(that.toLong())

    override fun div(that: Long) = div(that.toBigInteger())

    override fun div(that: Float) = div(that.toDouble())

    override fun div(that: Double) = div(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun div(that: BigInteger) = this.value.divide(that)!!

    override fun div(that: BigDecimal) = this.value.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext).divide(that, DefaultBigDecimalPrecision.divRoundingMode)!!

    override fun rem(that: Int) = rem(that.toLong())

    override fun rem(that: Long) = rem(that.toBigInteger())

    override fun rem(that: Float) = rem(that.toDouble())

    override fun rem(that: Double) = rem(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun rem(that: BigInteger) = this.value.remainder(that)!!

    override fun rem(that: BigDecimal) = this.value.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext).remainder(that)!!
}

class BigDecimalCell(column: Column, index: Long, override val value: BigDecimal) : Cell<BigDecimal>(column, index) {
    override fun toCell(column: Column, index: Long): Cell<BigDecimal> =
        BigDecimalCell(column, index, this.value)

    override fun isNumeric() = true

    override fun toLong() = value.toLong()

    override fun toDouble() = value.toDouble()

    override fun toBigInteger(): BigInteger = value.toBigInteger()

    override fun toBigDecimal() = value

    override fun toBigDecimal(mathContext: MathContext) = value.round(mathContext)!!

    override fun toNumber(): Number = value

    override fun plus(that: Int) = plus(that.toLong())

    override fun plus(that: Long) = plus(that.toBigInteger())

    override fun plus(that: Float) = plus(that.toDouble())

    override fun plus(that: Double) = plus(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun plus(that: BigInteger) = this.value.add(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext))!!

    override fun plus(that: BigDecimal) = this.value.add(that)!!

    override fun minus(that: Int) = minus(that.toLong())

    override fun minus(that: Long) = minus(that.toBigInteger())

    override fun minus(that: Float) = minus(that.toDouble())

    override fun minus(that: Double) = plus(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun minus(that: BigInteger) = this.value.subtract(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext))!!

    override fun minus(that: BigDecimal) = this.value.subtract(that)!!

    override fun times(that: Int) = times(that.toLong())

    override fun times(that: Long) = times(that.toBigInteger())

    override fun times(that: Float) = times(that.toDouble())

    override fun times(that: Double) = times(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun times(that: BigInteger) = this.value.multiply(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext))!!

    override fun times(that: BigDecimal) = this.value.multiply(that)!!

    override fun div(that: Int) = div(that.toLong())

    override fun div(that: Long) = div(that.toBigInteger())

    override fun div(that: Float) = div(that.toDouble())

    override fun div(that: Double) = div(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun div(that: BigInteger) = this.value.divide(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext), DefaultBigDecimalPrecision.divRoundingMode)!!

    override fun div(that: BigDecimal) = this.value.divide(that, DefaultBigDecimalPrecision.divRoundingMode)!!

    override fun rem(that: Int) = rem(that.toLong())

    override fun rem(that: Long) = rem(that.toBigInteger())

    override fun rem(that: Float) = rem(that.toDouble())

    override fun rem(that: Double) = rem(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

    override fun rem(that: BigInteger) = this.value.remainder(that.toBigDecimal(mathContext = DefaultBigDecimalPrecision.mathContext))!!

    override fun rem(that: BigDecimal) = this.value.remainder(that)!!
}

class WebContent(val content: String) {
    override fun toString() = content
}

internal fun String.toWebContent() = WebContent(this)

class WebCell(column: Column, index: Long, override val value: WebContent) : Cell<WebContent>(column, index) {
    override fun toCell(column: Column, index: Long): Cell<WebContent> =
        WebCell(column, index, this.value)
}

// TODO Other types of cells:

// Time cell
// Blob cell for storing blobs of data?
// Table cell for storing a table in a cell?
// Chart cell for rendering a chart..
//   - Having looked at options I don't find anything I like in the JVM world..
//   - Maybe use D3 in a web frame.. https://www.d3-graph-gallery.com/

// Chart cell to be done as a WebCell instead..

// Cells also need meta data, for example related to how they are displayed?
