package sigbla.app

import kotlinx.html.DIV
import kotlinx.html.consumers.delayed
import kotlinx.html.consumers.onFinalizeMap
import kotlinx.html.div
import kotlinx.html.stream.HTMLStreamBuilder
import sigbla.app.exceptions.InvalidCellException
import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.exceptions.InvalidValueException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.util.*
import kotlin.math.min
import kotlin.math.max

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

internal class CellValue<T>(val value: T) {
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

    override fun toString(): String {
        return "CellValue(value=$value)"
    }
}

enum class CellOrder { COLUMN, ROW }

class CellRange(override val start: Cell<*>, override val endInclusive: Cell<*>, val order: CellOrder = CellOrder.COLUMN) : ClosedRange<Cell<*>>, Iterable<Cell<*>> {
    init {
        if (start.column.table !== endInclusive.column.table) {
            throw InvalidTableException("Cell range much be within same table")
        }
    }

    val table: Table
        get() = start.table

    override fun iterator(): Iterator<Cell<*>> {
        val ref = table.tableRef.get()

        // Because columns might move around, get the latest order
        val currentStart = ref.columns[start.column.header]?.columnOrder ?: throw InvalidColumnException("Unable to find column $start")
        val currentEnd = ref.columns[endInclusive.column.header]?.columnOrder ?: throw InvalidColumnException("Unable to find column $endInclusive")

        val minOrder = min(currentStart, currentEnd)
        val maxOrder = max(currentStart, currentEnd)

        val columns = ref
            .headers
            .filter { it.second.columnOrder in minOrder..maxOrder }
            .let {
                if (currentStart > currentEnd) it.toList().reversed().asSequence() else it
            }
            .map { BaseColumn(table, it.first, it.second.columnOrder) }
            .toList()

        val rows = if (start.index <= endInclusive.index) {
            (start.index..endInclusive.index)
        } else {
            (start.index downTo endInclusive.index)
        }

        return if (order == CellOrder.COLUMN) {
            columns.asSequence()
                .map { c ->
                    rows.asSequence().map { r ->
                        c to r
                    }
                }
        } else {
            rows.asSequence()
                .map { r ->
                    columns.asSequence().map { c ->
                        c to r
                    }
                }
        }
            .flatten()
            .map {
                ref.columnCells[it.first.header]?.get(it.second)?.toCell(it.first, it.second)
            }
            .filterNotNull()
            .iterator()
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

        // TODO Because columns might move around, get the latest order
        //      See iterator above.. There also no need to check if start is <= endm just use contains from columnrange..
        return if (start.column.order <= endInclusive.column.order) {
            (start.column..endInclusive.column).contains(value.column)
        } else {
            (endInclusive.column..start.column).contains(value.column)
        }
    }

    override fun isEmpty(): Boolean = false

    // TODO: Implement assignment ops?
}

infix fun CellRange.by(cellOrder: CellOrder) = CellRange(this.start, this.endInclusive, cellOrder)

// TODO Implement something that allows us to navigate relative to a cell?
//      Ex: table["A", 1] up 1 returns table["A", 0], or
//          table["A", 1] left 1 returns table["B", 1] etc..
//      But it would need to be able to cope with no-existing columns..
sealed class Cell<T>(val column: Column, val index: Long) : Comparable<Any?>, Iterable<Cell<*>> {
    abstract val value: T

    // TODO Shouldn't this be internal with tableOf(..) function available?
    val table: Table
        get() = column.table

    internal abstract fun toCell(column: Column, index: Long): Cell<T>

    internal fun toCellValue() = CellValue(value)

    // TODO Look at making these internal and introduce a CellOps
    open fun isNumeric(): Boolean = false
    open fun isText(): Boolean = false

    // TODO? Look at making these internal and introduce a CellOps
    // TODO Make these return null instead of throwing exception
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
                    is Long -> v.compareTo(0L)
                    is Double -> v.compareTo(0.0)
                    is BigInteger -> v.compareTo(BigInteger.ZERO)
                    is BigDecimal -> v.compareTo(BigDecimal.ZERO)
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

    // TODO Can remove this after adding iterator functionality
    fun asSequence(): Sequence<Cell<*>> = sequenceOf(this)

    operator fun <T> invoke(function: Cell<*>.() -> T): T {
        return when (val value = function()) {
            is BigDecimal -> { table[this] = value; value }
            is BigInteger -> { table[this] = value; value }
            is Double -> { table[this] = value; value }
            is Long -> { table[this] = value; value }
            is Number -> { table[this] = value; value }
            is String -> { table[this] = value; value }
            is Cell<*> -> { table[this] = value; value }
            is Unit -> { /* no assignment */ Unit as T }
            is Function1<*, *> -> { invoke(value as Cell<*>.() -> T?) as T }
            null -> { table[this] = null; null as T }
            else -> throw InvalidValueException("Unsupported type: ${value!!::class}")
        }
    }

    override fun iterator(): Iterator<Cell<*>> = if (this is UnitCell) emptyList<Cell<*>>().iterator() else listOf(this).iterator()

    // TODO Add functionality to make this symmetric? Add to BasicMath?
    override fun equals(other: Any?) = this.compareTo(other) == 0

    override fun hashCode() = Objects.hash(this.value)

    override fun toString() = this.value.toString()
}

class UnitCell internal constructor(column: Column, index: Long) : Cell<Unit>(column, index) {
    override val value = Unit

    override fun toCell(column: Column, index: Long): Cell<Unit> =
        UnitCell(column, index)

    override fun toString() = ""
}

class StringCell internal constructor(column: Column, index: Long, override val value: String) : Cell<String>(column, index) {
    override fun toCell(column: Column, index: Long): Cell<String> =
        StringCell(column, index, this.value)

    override fun isText() = true
}

class LongCell internal constructor(column: Column, index: Long, override val value: Long) : Cell<Long>(column, index) {
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

class DoubleCell internal constructor(column: Column, index: Long, override val value: Double) : Cell<Double>(column, index) {
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

class BigIntegerCell internal constructor(column: Column, index: Long, override val value: BigInteger) : Cell<BigInteger>(column, index) {
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

    override fun minus(that: Double) = minus(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

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

class BigDecimalCell internal constructor(column: Column, index: Long, override val value: BigDecimal) : Cell<BigDecimal>(column, index) {
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

    override fun minus(that: Double) = minus(that.toBigDecimal(DefaultBigDecimalPrecision.mathContext))

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

class WebContent internal constructor(val content: String) {
    override fun toString() = content
}

internal fun String.toWebContent() = WebContent(this)

class WebCell internal constructor(column: Column, index: Long, override val value: WebContent) : Cell<WebContent>(column, index) {
    override fun toCell(column: Column, index: Long): Cell<WebContent> =
        WebCell(column, index, this.value)
}

// TODO Other types of cells:

// Boolean cell
// Time cell
// Blob cell for storing blobs of data?
// Table cell for storing a table in a cell?
// Chart cell for rendering a chart..
//   - Having looked at options I don't find anything I like in the JVM world..
//   - Maybe use D3 in a web frame.. https://www.d3-graph-gallery.com/

// Chart cell to be done as a WebCell instead..

fun div(
    classes : String? = null, block : DIV.() -> Unit = {}
): Cell<*>.() -> WebCell = {
    val builder = HTMLStreamBuilder(StringBuilder(256), prettyPrint = false, xhtmlCompatible = false)
        .onFinalizeMap { sb, _ -> sb.toString() }
        .delayed()

    WebCell(
        column,
        index,
        builder.div(classes, block).toWebContent()
    )
}

class Cells(sources: List<Iterable<Cell<*>>>): Iterable<Cell<*>> {
    constructor(vararg sources: Iterable<Cell<*>>) : this(sources.toList())

    val sources: List<Iterable<Cell<*>>> = sources.flatMap {
        if (it is Cells) it.sources
        else when (it) {
            is Cell<*> -> listOf(it)
            is Row -> listOf(it)
            is Column -> listOf(it)
            is CellRange -> listOf(it)
            is Table -> listOf(it)
            else -> it.toCollection(mutableListOf()) // make a copy
        }
    }

    val table = this.sources.first().let {
        when (it) {
            is Cell<*> -> it.table
            is Row -> it.table
            is Column -> it.table
            is CellRange -> it.table
            is Table -> it
            else -> throw InvalidValueException("Unsupported source type: ${it::class}")
        }
    }

    init {
        if (sources.isEmpty()) throw InvalidValueException("At least one source needed")

        val tables = sources
            .flatMap { if (it is Cells) it.sources else listOf(it) }
            .map { iterable ->
                when (iterable) {
                    is Cell<*> -> listOf(iterable.table)
                    is Row -> listOf(iterable.table)
                    is Column -> listOf(iterable.table)
                    is CellRange -> listOf(iterable.table)
                    is Table -> listOf(iterable)
                    else -> iterable.map { it.table }.toSet()
                }
            }
            .flatten()
            .toSet()

        if (tables.isEmpty()) throw InvalidValueException("At least one source needed")

        if (tables.size > 1) {
            val t1 = tables.first()
            tables.forEach {
                if (t1 !== it) throw InvalidTableException("Only a single source table supported")
            }
        }
    }

    override fun iterator(): Iterator<Cell<*>> = sources.asSequence().flatten().iterator()
}

infix fun Iterable<Cell<*>>.or(source: Iterable<Cell<*>>): Cells {
    return when {
        this is Cells && source is Cells -> this.sources.toMutableList().apply {
            addAll(source.sources)
        }.let { Cells(it) }
        this is Cells -> this.sources.toMutableList().apply {
            add(source)
        }.let { Cells(it) }
        source is Cells -> source or this
        else -> Cells(this, source)
    }
}
