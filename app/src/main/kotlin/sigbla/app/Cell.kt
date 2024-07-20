/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount
import java.util.*
import kotlin.math.min
import kotlin.math.max

internal fun Unit.toCell(column: Column, index: Long) =
    UnitCell(column, index)
internal fun Boolean.toCell(column: Column, index: Long) =
    BooleanCell(column, index, this)
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
internal fun LocalDate.toCell(column: Column, index: Long): LocalDateCell =
    LocalDateCell(column, index, this)
internal fun LocalTime.toCell(column: Column, index: Long): LocalTimeCell =
    LocalTimeCell(column, index, this)
internal fun LocalDateTime.toCell(column: Column, index: Long): LocalDateTimeCell =
    LocalDateTimeCell(column, index, this)
internal fun ZonedDateTime.toCell(column: Column, index: Long): ZonedDateTimeCell =
    ZonedDateTimeCell(column, index, this)
internal fun WebContent.toCell(column: Column, index: Long): WebCell =
    WebCell(column, index, this)

internal class CellValue<T>(val value: T) {
    fun toCell(column: Column, index: Long): Cell<*> {
        return when (value) {
            is Boolean -> value.toCell(column, index)
            is String -> value.toCell(column, index)
            is Long -> value.toCell(column, index)
            is Double -> value.toCell(column, index)
            is BigInteger -> value.toCell(column, index)
            is BigDecimal -> value.toCell(column, index)
            is LocalDate -> value.toCell(column, index)
            is LocalTime -> value.toCell(column, index)
            is LocalDateTime -> value.toCell(column, index)
            is ZonedDateTime -> value.toCell(column, index)
            is WebContent -> value.toCell(column, index)
            else -> throw UnsupportedOperationException("Unable to convert to cell: $value")
        }
    }

    override fun toString(): String {
        return "CellValue(value=$value)"
    }
}

enum class CellOrder { COLUMN, ROW }

class CellRange internal constructor(override val start: Cell<*>, override val endInclusive: Cell<*>, val order: CellOrder = CellOrder.COLUMN) : ClosedRange<Cell<*>>, Iterable<Cell<*>> {
    init {
        if (start.column.table !== endInclusive.column.table) {
            throw InvalidCellException("CellRange much be within same table")
        }
    }

    val table: Table
        get() = start.table

    override fun iterator(): Iterator<Cell<*>> = iterator(table, table.tableRef.get())

    internal fun iterator(table: Table, ref: TableRef): Iterator<Cell<*>> {
        // Because columns might move around, get the latest order
        // It might also mean the column is no longer available
        val currentStart = ref.columns[start.column.header]?.columnOrder ?: return Collections.emptyIterator()
        val currentEnd = ref.columns[endInclusive.column.header]?.columnOrder ?: return Collections.emptyIterator()

        val minOrder = min(currentStart, currentEnd)
        val maxOrder = max(currentStart, currentEnd)

        val columns = ref
            .headers
            .filter { it.second.columnOrder in minOrder..maxOrder }
            .let {
                if (currentStart > currentEnd) it.toList().reversed().asSequence() else it
            }
            .map { Column(table, it.first, it.second.columnOrder) }
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
                // We want to throw this exception because ref should contain columnCells
                val values = ref.columnCells[it.first.header] ?: throw InvalidColumnException("Unable to find column cells for header ${it.first.header}")
                values[it.second]?.toCell(it.first, it.second)
            }
            .filterNotNull()
            .iterator()
    }

    operator fun contains(that: Number): Boolean = any { that in it }
    operator fun contains(that: Temporal): Boolean = any { that in it }
    operator fun contains(that: Boolean): Boolean = any { that in it }
    operator fun contains(that: String): Boolean = any { that in it }

    override fun contains(value: Cell<*>): Boolean {
        if (value.index < min(start.index, endInclusive.index) || value.index > max(start.index, endInclusive.index)) {
            return false
        }

        return ((start.column)..(endInclusive.column)).contains(value.column)
    }

    override fun isEmpty(): Boolean = !iterator().hasNext()

    // TODO: Implement assignment ops?
}

infix fun CellRange.by(cellOrder: CellOrder) = CellRange(this.start, this.endInclusive, cellOrder)

// TODO Implement something that allows us to navigate relative to a cell?
//      Ex: table["A", 1] up 1 returns table["A", 0], or
//          table["A", 1] left 1 returns table["B", 1] etc..
//      But it would need to be able to cope with no-existing columns..
sealed class Cell<T>(val column: Column, val index: Long) : Comparable<Any?>, Iterable<Cell<*>> {
    abstract val value: T

    val table: Table
        get() = column.table

    internal fun toCellValue() = CellValue(value)

    open val isNumeric: Boolean = false
    open val isText: Boolean = false
    open val isTemporal: Boolean = false

    open val asLong: Long? = null
    open val asDouble: Double? = null
    open val asBigInteger: BigInteger? = null
    open val asBigDecimal: BigDecimal? by lazy { asBigDecimal(Precision.mathContext) }
    open fun asBigDecimal(mathContext: MathContext): BigDecimal? = null
    open val asNumber: Number? = null
    open val asString: String? = null
    open val asBoolean: Boolean? = null
    open val asLocalDate: LocalDate? = null
    open val asLocalTime: LocalTime? = null
    open val asLocalDateTime: LocalDateTime? = null
    open val asZonedDateTime: ZonedDateTime? = null

    operator fun plus(that: Cell<*>): Number {
        return when (val v = that.value) {
            is Long -> plus(v)
            is Double -> plus(v)
            is BigInteger -> plus(v)
            is BigDecimal -> plus(v)
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun plus(that: Number): Number {
        return when (that) {
            is Int -> plus(that)
            is Long -> plus(that)
            is Float -> plus(that)
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

    open operator fun plus(that: TemporalAmount): Temporal = throw InvalidCellException("Cell not temporal at $column:$index")

    operator fun minus(that: Cell<*>): Number {
        return when (val v = that.value) {
            is Long -> minus(v)
            is Double -> minus(v)
            is BigInteger -> minus(v)
            is BigDecimal -> minus(v)
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun minus(that: Number): Number {
        return when (that) {
            is Int -> minus(that)
            is Long -> minus(that)
            is Float -> minus(that)
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

    open operator fun minus(that: TemporalAmount): Temporal = throw InvalidCellException("Cell not temporal at $column:$index")

    operator fun times(that: Cell<*>): Number {
        return when (val v = that.value) {
            is Long -> times(v)
            is Double -> times(v)
            is BigInteger -> times(v)
            is BigDecimal -> times(v)
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun times(that: Number): Number {
        return when (that) {
            is Int -> times(that)
            is Long -> times(that)
            is Float -> times(that)
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
        return when (val v = that.value) {
            is Long -> div(v)
            is Double -> div(v)
            is BigInteger -> div(v)
            is BigDecimal -> div(v)
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun div(that: Number): Number {
        return when (that) {
            is Int -> div(that)
            is Long -> div(that)
            is Float -> div(that)
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
        return when (val v = that.value) {
            is Long -> rem(v)
            is Double -> rem(v)
            is BigInteger -> rem(v)
            is BigDecimal -> rem(v)
            else -> throw InvalidCellException("Cell not numeric at ${that.column}:${that.index}")
        }
    }

    operator fun rem(that: Number): Number {
        return when (that) {
            is Int -> rem(that)
            is Long -> rem(that)
            is Float -> rem(that)
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
        // Null/Unit is less than everything else
        // Numbers are compared to each other
        // A non-number is greater than a number
        // Everything else compared as strings

        return when {
            // Cell case
            other is Cell<*> -> compareTo(other.value)

            // Null/Unit cases
            other == null -> if (value == Unit) 0 else 1
            other == Unit -> if (value == Unit) 0 else 1
            value == Unit -> -1

            // Number case
            other is Number -> if (value is Number) {
                when (val v = this - other) {
                    is Int -> v.compareTo(0)
                    is Long -> v.compareTo(0L)
                    is Float -> v.compareTo(0F)
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

    operator fun contains(that: Number): Boolean = compareTo(that) == 0
    operator fun contains(that: Temporal): Boolean = compareTo(that) == 0
    operator fun contains(that: Boolean): Boolean = compareTo(that) == 0
    operator fun contains(that: String): Boolean = compareTo(that) == 0
    operator fun contains(that: Unit): Boolean = compareTo(that) == 0
    operator fun contains(that: Cell<*>): Boolean = compareTo(that) == 0

    operator fun invoke(newValue: BigDecimal?): BigDecimal? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: BigInteger?): BigInteger? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: Double?): Double? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: Long?): Long? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: Number?): Number? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: String?): String? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: Boolean?): Boolean? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: LocalDate?): LocalDate? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: LocalTime?): LocalTime? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: LocalDateTime?): LocalDateTime? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: ZonedDateTime?): ZonedDateTime? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: Cell<*>?): Cell<*>? {
        table[this] = newValue
        return newValue
    }
    operator fun invoke(newValue: Unit?): Unit? {
        table[this] = newValue
        return newValue
    }

    override fun iterator(): Iterator<Cell<*>> = iterator(table, table.tableRef.get())

    internal fun iterator(table: Table, ref: TableRef): Iterator<Cell<*>> {
        // Column might have been removed before we call iterator
        val meta = ref.columns[this.column.header] ?: return emptyList<Cell<*>>().iterator()
        if (meta.prenatal) return emptyList<Cell<*>>().iterator()

        // We want to throw this exception because ref should contain columnCells
        val values = ref.columnCells[this.column.header] ?: throw InvalidColumnException("Unable to find column cells for header ${this.column.header}")
        val cellValue = values[index] ?: return emptyList<Cell<*>>().iterator()
        val column = Column(table, this.column.header, meta.columnOrder)

        return listOf(cellValue.toCell(column, index)).iterator()
    }

    override fun toString() = this.value.toString()

    override fun hashCode() = Objects.hash(this.value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cell<*>

        if (column != other.column) return false
        if (index != other.index) return false
        if (value != other.value) return false

        return true
    }
}

class UnitCell internal constructor(column: Column, index: Long) : Cell<Unit>(column, index) {
    override val value = Unit

    override fun toString() = ""
}

class BooleanCell internal constructor(column: Column, index: Long, override val value: Boolean) : Cell<Boolean>(column, index) {
    override val asBoolean: Boolean = value
}

class StringCell internal constructor(column: Column, index: Long, override val value: String) : Cell<String>(column, index) {
    override val isText = true
    override val asString: String = value
}

class LongCell internal constructor(column: Column, index: Long, override val value: Long) : Cell<Long>(column, index) {
    override val isNumeric = true

    override val asLong: Long = value

    override val asDouble: Double by lazy { value.toDouble() }

    override val asBigInteger: BigInteger by lazy { value.toBigInteger() }

    override fun asBigDecimal(mathContext: MathContext): BigDecimal = value.toBigDecimal(mathContext)

    override val asNumber: Number = value

    override fun plus(that: Int) = this.value + that

    override fun plus(that: Long) = this.value + that

    override fun plus(that: Float) = this.value + that

    override fun plus(that: Double) = this.value + that

    override fun plus(that: BigInteger) = this.value.toBigInteger().add(that)!!

    override fun plus(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).add(that)!!

    override fun minus(that: Int) = this.value - that

    override fun minus(that: Long) = this.value - that

    override fun minus(that: Float) = this.value - that

    override fun minus(that: Double) = this.value - that

    override fun minus(that: BigInteger) = this.value.toBigInteger().subtract(that)!!

    override fun minus(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).subtract(that)!!

    override fun times(that: Int) = this.value * that

    override fun times(that: Long) = this.value * that

    override fun times(that: Float) = this.value * that

    override fun times(that: Double) = this.value * that

    override fun times(that: BigInteger) = this.value.toBigInteger().multiply(that)!!

    override fun times(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).multiply(that)!!

    override fun div(that: Int) = this.value / that

    override fun div(that: Long) = this.value / that

    override fun div(that: Float) = this.value / that

    override fun div(that: Double) = this.value / that

    override fun div(that: BigInteger) = this.value.toBigInteger().divide(that)!!

    override fun div(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).divide(that, Precision.mathContext.roundingMode)!!

    override fun rem(that: Int) = this.value % that

    override fun rem(that: Long) = this.value % that

    override fun rem(that: Float) = this.value % that

    override fun rem(that: Double) = this.value % that

    override fun rem(that: BigInteger) = this.value.toBigInteger().remainder(that)!!

    override fun rem(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).remainder(that)!!
}

class DoubleCell internal constructor(column: Column, index: Long, override val value: Double) : Cell<Double>(column, index) {
    override val isNumeric = true

    override val asLong: Long by lazy { value.toLong() }

    override val asDouble: Double = value

    override val asBigInteger: BigInteger by lazy { BigInteger.valueOf(value.toLong()) }

    override fun asBigDecimal(mathContext: MathContext): BigDecimal = value.toBigDecimal(mathContext)

    override val asNumber: Number = value

    override fun plus(that: Int) = this.value + that

    override fun plus(that: Long) = this.value + that

    override fun plus(that: Float) = this.value + that

    override fun plus(that: Double) = this.value + that

    override fun plus(that: BigInteger) = this.value.toBigDecimal(Precision.mathContext).add(that.toBigDecimal(mathContext = Precision.mathContext))!!

    override fun plus(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).add(that)!!

    override fun minus(that: Int) = this.value - that

    override fun minus(that: Long) = this.value - that

    override fun minus(that: Float) = this.value - that

    override fun minus(that: Double) = this.value - that

    override fun minus(that: BigInteger) = this.value.toBigDecimal(Precision.mathContext).subtract(that.toBigDecimal(mathContext = Precision.mathContext))!!

    override fun minus(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).subtract(that)!!

    override fun times(that: Int) = this.value * that

    override fun times(that: Long) = this.value * that

    override fun times(that: Float) = this.value * that

    override fun times(that: Double) = this.value * that

    override fun times(that: BigInteger) = this.value.toBigDecimal(Precision.mathContext).multiply(that.toBigDecimal(mathContext = Precision.mathContext))!!

    override fun times(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).multiply(that)!!

    override fun div(that: Int) = this.value / that

    override fun div(that: Long) = this.value / that

    override fun div(that: Float) = this.value / that

    override fun div(that: Double) = this.value / that

    override fun div(that: BigInteger) = this.value.toBigDecimal(Precision.mathContext).divide(that.toBigDecimal(mathContext = Precision.mathContext), Precision.mathContext.roundingMode)!!

    override fun div(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).divide(that, Precision.mathContext.roundingMode)!!

    override fun rem(that: Int) = this.value % that

    override fun rem(that: Long) = this.value % that

    override fun rem(that: Float) = this.value % that

    override fun rem(that: Double) = this.value % that

    override fun rem(that: BigInteger) = this.value.toBigDecimal(Precision.mathContext).remainder(that.toBigDecimal(mathContext = Precision.mathContext))!!

    override fun rem(that: BigDecimal) = this.value.toBigDecimal(Precision.mathContext).remainder(that)!!
}

class BigIntegerCell internal constructor(column: Column, index: Long, override val value: BigInteger) : Cell<BigInteger>(column, index) {
    override val isNumeric = true

    override val asLong: Long by lazy { value.toLong() }

    override val asDouble: Double by lazy { value.toDouble() }

    override val asBigInteger: BigInteger = value

    override fun asBigDecimal(mathContext: MathContext): BigDecimal = value.toBigDecimal(mathContext = mathContext)

    override val asNumber: Number = value

    override fun plus(that: Int) = plus(that.toLong())

    override fun plus(that: Long) = plus(that.toBigInteger())

    override fun plus(that: Float) = plus(that.toDouble())

    override fun plus(that: Double) = plus(that.toBigDecimal(Precision.mathContext))

    override fun plus(that: BigInteger) = this.value.add(that)!!

    override fun plus(that: BigDecimal) = this.value.toBigDecimal(mathContext = Precision.mathContext).add(that)!!

    override fun minus(that: Int) = minus(that.toLong())

    override fun minus(that: Long) = minus(that.toBigInteger())

    override fun minus(that: Float) = minus(that.toDouble())

    override fun minus(that: Double) = minus(that.toBigDecimal(Precision.mathContext))

    override fun minus(that: BigInteger) = this.value.subtract(that)!!

    override fun minus(that: BigDecimal) = this.value.toBigDecimal(mathContext = Precision.mathContext).subtract(that)!!

    override fun times(that: Int) = times(that.toLong())

    override fun times(that: Long) = times(that.toBigInteger())

    override fun times(that: Float) = times(that.toDouble())

    override fun times(that: Double) = times(that.toBigDecimal(Precision.mathContext))

    override fun times(that: BigInteger) = this.value.multiply(that)!!

    override fun times(that: BigDecimal) = this.value.toBigDecimal(mathContext = Precision.mathContext).multiply(that)!!

    override fun div(that: Int) = div(that.toLong())

    override fun div(that: Long) = div(that.toBigInteger())

    override fun div(that: Float) = div(that.toDouble())

    override fun div(that: Double) = div(that.toBigDecimal(Precision.mathContext))

    override fun div(that: BigInteger) = this.value.divide(that)!!

    override fun div(that: BigDecimal) = this.value.toBigDecimal(mathContext = Precision.mathContext).divide(that, Precision.mathContext.roundingMode)!!

    override fun rem(that: Int) = rem(that.toLong())

    override fun rem(that: Long) = rem(that.toBigInteger())

    override fun rem(that: Float) = rem(that.toDouble())

    override fun rem(that: Double) = rem(that.toBigDecimal(Precision.mathContext))

    override fun rem(that: BigInteger) = this.value.remainder(that)!!

    override fun rem(that: BigDecimal) = this.value.toBigDecimal(mathContext = Precision.mathContext).remainder(that)!!
}

class BigDecimalCell internal constructor(column: Column, index: Long, override val value: BigDecimal) : Cell<BigDecimal>(column, index) {
    override val isNumeric = true

    override val asLong: Long by lazy { value.toLong() }

    override val asDouble: Double by lazy { value.toDouble() }

    override val asBigInteger: BigInteger by lazy { value.toBigInteger() }

    override fun asBigDecimal(mathContext: MathContext): BigDecimal = value.round(mathContext)!!

    override val asNumber: Number = value

    override fun plus(that: Int) = plus(that.toLong())

    override fun plus(that: Long) = plus(that.toBigInteger())

    override fun plus(that: Float) = plus(that.toDouble())

    override fun plus(that: Double) = plus(that.toBigDecimal(Precision.mathContext))

    override fun plus(that: BigInteger) = this.value.add(that.toBigDecimal(mathContext = Precision.mathContext))!!

    override fun plus(that: BigDecimal) = this.value.add(that)!!

    override fun minus(that: Int) = minus(that.toLong())

    override fun minus(that: Long) = minus(that.toBigInteger())

    override fun minus(that: Float) = minus(that.toDouble())

    override fun minus(that: Double) = minus(that.toBigDecimal(Precision.mathContext))

    override fun minus(that: BigInteger) = this.value.subtract(that.toBigDecimal(mathContext = Precision.mathContext))!!

    override fun minus(that: BigDecimal) = this.value.subtract(that)!!

    override fun times(that: Int) = times(that.toLong())

    override fun times(that: Long) = times(that.toBigInteger())

    override fun times(that: Float) = times(that.toDouble())

    override fun times(that: Double) = times(that.toBigDecimal(Precision.mathContext))

    override fun times(that: BigInteger) = this.value.multiply(that.toBigDecimal(mathContext = Precision.mathContext))!!

    override fun times(that: BigDecimal) = this.value.multiply(that)!!

    override fun div(that: Int) = div(that.toLong())

    override fun div(that: Long) = div(that.toBigInteger())

    override fun div(that: Float) = div(that.toDouble())

    override fun div(that: Double) = div(that.toBigDecimal(Precision.mathContext))

    override fun div(that: BigInteger) = this.value.divide(that.toBigDecimal(mathContext = Precision.mathContext), Precision.mathContext.roundingMode)!!

    override fun div(that: BigDecimal) = this.value.divide(that, Precision.mathContext.roundingMode)!!

    override fun rem(that: Int) = rem(that.toLong())

    override fun rem(that: Long) = rem(that.toBigInteger())

    override fun rem(that: Float) = rem(that.toDouble())

    override fun rem(that: Double) = rem(that.toBigDecimal(Precision.mathContext))

    override fun rem(that: BigInteger) = this.value.remainder(that.toBigDecimal(mathContext = Precision.mathContext))!!

    override fun rem(that: BigDecimal) = this.value.remainder(that)!!
}

class LocalDateCell internal constructor(column: Column, index: Long, override val value: LocalDate) : Cell<LocalDate>(column, index) {
    override val isTemporal = true
    override val asLocalDate: LocalDate = value

    override fun plus(that: TemporalAmount): LocalDate = this.value + that
    override fun minus(that: TemporalAmount): LocalDate = this.value - that
}

class LocalTimeCell internal constructor(column: Column, index: Long, override val value: LocalTime) : Cell<LocalTime>(column, index) {
    override val isTemporal = true
    override val asLocalTime: LocalTime = value

    override fun plus(that: TemporalAmount): LocalTime = this.value + that
    override fun minus(that: TemporalAmount): LocalTime = this.value - that
}

class LocalDateTimeCell internal constructor(column: Column, index: Long, override val value: LocalDateTime) : Cell<LocalDateTime>(column, index) {
    override val isTemporal = true
    override val asLocalDateTime: LocalDateTime = value
    override val asLocalDate: LocalDate by lazy { value.toLocalDate() }
    override val asLocalTime: LocalTime by lazy { value.toLocalTime() }

    override fun plus(that: TemporalAmount): LocalDateTime = this.value + that
    override fun minus(that: TemporalAmount): LocalDateTime = this.value - that
}

class ZonedDateTimeCell internal constructor(column: Column, index: Long, override val value: ZonedDateTime) : Cell<ZonedDateTime>(column, index) {
    override val isTemporal = true
    override val asZonedDateTime: ZonedDateTime = value
    override val asLocalDateTime: LocalDateTime by lazy { value.toLocalDateTime() }
    override val asLocalDate: LocalDate by lazy { value.toLocalDate() }
    override val asLocalTime: LocalTime by lazy { value.toLocalTime() }

    override fun plus(that: TemporalAmount): ZonedDateTime = this.value + that
    override fun minus(that: TemporalAmount): ZonedDateTime = this.value - that
}

class WebContent internal constructor(val content: String) {
    override fun toString() = content

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WebContent

        return content == other.content
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }
}

internal fun String.toWebContent() = WebContent(this)

class WebCell internal constructor(column: Column, index: Long, override val value: WebContent) : Cell<WebContent>(column, index)

fun div(
    classes : String? = null, block : DIV.() -> Unit = {}
): Cell<*>.() -> Unit = {
    val builder = HTMLStreamBuilder(StringBuilder(256), prettyPrint = false, xhtmlCompatible = false)
        .onFinalizeMap { sb, _ -> sb.toString() }
        .delayed()

    table[this] = WebCell(
        column,
        index,
        builder.div(classes, block).toWebContent()
    )
}

class Cells internal constructor(sources: Collection<Iterable<Cell<*>>>): Iterable<Cell<*>> {
    internal constructor(vararg sources: Iterable<Cell<*>>) : this(sources.toList())

    val sources: List<Iterable<Cell<*>>>

    val table: Table

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

        this.sources = sources.flatMap {
            if (it is Cells) it.sources
            else when (it) {
                is Cell<*> -> listOf(it)
                is Row -> listOf(it)
                is Column -> listOf(it)
                is CellRange -> listOf(it)
                is Table -> listOf(it)
                else -> it.toCollection(mutableListOf()).toList() // make a copy
            }
        }

        this.table = tables.first()
    }

    override fun iterator(): Iterator<Cell<*>> {
        val ref = table.tableRef.get()

        fun refIterator(iterable: Iterable<Cell<*>>): Iterator<Cell<*>> {
            return when (iterable) {
                is Cell<*> -> iterable.iterator(table, ref)
                is Row -> iterable.iterator(table, ref)
                is Column -> iterable.iterator(table, ref)
                is CellRange -> iterable.iterator(table, ref)
                is Table -> iterable.iterator(table, ref)
                else -> iterable.asSequence().flatMap { it.iterator(table, ref).asSequence() }.iterator()
            }
        }

        return sources.asSequence().flatMap { refIterator(it).asSequence() }.iterator()
    }

    companion object {
        operator fun get(sources: Collection<Iterable<Cell<*>>>) = Cells(sources)
        operator fun get(vararg sources: Iterable<Cell<*>>) = Cells(sources.toList())
    }
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
