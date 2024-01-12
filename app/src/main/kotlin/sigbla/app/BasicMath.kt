/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidCellException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

object Precision {
    internal var mathContext: MathContext = MathContext.DECIMAL64
}

object Math {
    operator fun set(precision: Precision, mathContext: MathContext) {
        Precision.mathContext = mathContext
    }

    operator fun get(precision: Precision): MathContext {
        return Precision.mathContext
    }
}

operator fun Int.plus(that: BigInteger): Number {
    return this.toBigInteger().add(that)
}

operator fun Int.plus(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).add(that)
}

operator fun Int.plus(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this + v
        is Double -> this + v
        is BigInteger -> this + v
        is BigDecimal -> this + v
        // TODO Update exception text to include column here and below
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Int.minus(that: BigInteger): Number {
    return this.toBigInteger().subtract(that)
}

operator fun Int.minus(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).subtract(that)
}

operator fun Int.minus(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this - v
        is Double -> this - v
        is BigInteger -> this - v
        is BigDecimal -> this - v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Int.times(that: BigInteger): Number {
    return this.toBigInteger().multiply(that)
}

operator fun Int.times(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).multiply(that)
}

operator fun Int.times(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this * v
        is Double -> this * v
        is BigInteger -> this * v
        is BigDecimal -> this * v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Int.div(that: BigInteger): Number {
    return this.toBigInteger().divide(that)
}

operator fun Int.div(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).divide(that,
        Precision.mathContext.roundingMode
    )
}

operator fun Int.div(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this / v
        is Double -> this / v
        is BigInteger -> this / v
        is BigDecimal -> this / v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Int.rem(that: BigInteger): Number {
    return this.toBigInteger().remainder(that)
}

operator fun Int.rem(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).remainder(that)
}

operator fun Int.rem(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this % v
        is Double -> this % v
        is BigInteger -> this % v
        is BigDecimal -> this % v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Long.plus(that: BigInteger): Number {
    return this.toBigInteger().add(that)
}

operator fun Long.plus(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).add(that)
}

operator fun Long.plus(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this + v
        is Double -> this + v
        is BigInteger -> this + v
        is BigDecimal -> this + v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Long.minus(that: BigInteger): Number {
    return this.toBigInteger().subtract(that)
}

operator fun Long.minus(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).subtract(that)
}

operator fun Long.minus(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this - v
        is Double -> this - v
        is BigInteger -> this - v
        is BigDecimal -> this - v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Long.times(that: BigInteger): Number {
    return this.toBigInteger().multiply(that)
}

operator fun Long.times(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).multiply(that)
}

operator fun Long.times(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this * v
        is Double -> this * v
        is BigInteger -> this * v
        is BigDecimal -> this * v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Long.div(that: BigInteger): Number {
    return this.toBigInteger().divide(that)
}

operator fun Long.div(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).divide(that,
        Precision.mathContext.roundingMode
    )
}

operator fun Long.div(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this / v
        is Double -> this / v
        is BigInteger -> this / v
        is BigDecimal -> this / v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Long.rem(that: BigInteger): Number {
    return this.toBigInteger().remainder(that)
}

operator fun Long.rem(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).remainder(that)
}

operator fun Long.rem(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this % v
        is Double -> this % v
        is BigInteger -> this % v
        is BigDecimal -> this % v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Float.plus(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).add(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun Float.plus(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).add(that)
}

operator fun Float.plus(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this + v
        is Double -> this + v
        is BigInteger -> this + v
        is BigDecimal -> this + v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Float.minus(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).subtract(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun Float.minus(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).subtract(that)
}

operator fun Float.minus(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this - v
        is Double -> this - v
        is BigInteger -> this - v
        is BigDecimal -> this - v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Float.times(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).multiply(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun Float.times(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).multiply(that)
}

operator fun Float.times(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this * v
        is Double -> this * v
        is BigInteger -> this * v
        is BigDecimal -> this * v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Float.div(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).divide(that.toBigDecimal(mathContext = Precision.mathContext),
        Precision.mathContext.roundingMode
    )
}

operator fun Float.div(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).divide(that,
        Precision.mathContext.roundingMode
    )
}

operator fun Float.div(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this / v
        is Double -> this / v
        is BigInteger -> this / v
        is BigDecimal -> this / v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Float.rem(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).remainder(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun Float.rem(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).remainder(that)
}

operator fun Float.rem(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this % v
        is Double -> this % v
        is BigInteger -> this % v
        is BigDecimal -> this % v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Double.plus(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).add(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun Double.plus(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).add(that)
}

operator fun Double.plus(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this + v
        is Double -> this + v
        is BigInteger -> this + v
        is BigDecimal -> this + v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Double.minus(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).subtract(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun Double.minus(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).subtract(that)
}

operator fun Double.minus(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this - v
        is Double -> this - v
        is BigInteger -> this - v
        is BigDecimal -> this - v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Double.times(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).multiply(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun Double.times(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).multiply(that)
}

operator fun Double.times(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this * v
        is Double -> this * v
        is BigInteger -> this * v
        is BigDecimal -> this * v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Double.div(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).divide(that.toBigDecimal(mathContext = Precision.mathContext),
        Precision.mathContext.roundingMode
    )
}

operator fun Double.div(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).divide(that,
        Precision.mathContext.roundingMode
    )
}

operator fun Double.div(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this / v
        is Double -> this / v
        is BigInteger -> this / v
        is BigDecimal -> this / v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Double.rem(that: BigInteger): Number {
    return this.toBigDecimal(Precision.mathContext).remainder(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun Double.rem(that: BigDecimal): Number {
    return this.toBigDecimal(Precision.mathContext).remainder(that)
}

operator fun Double.rem(that: Cell<*>): Number {
    return when (val v = that.value) {
        is Long -> this % v
        is Double -> this % v
        is BigInteger -> this % v
        is BigDecimal -> this % v
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigInteger.plus(that: BigDecimal): Number {
    return this.toBigDecimal(mathContext = Precision.mathContext).add(that)
}

operator fun BigInteger.plus(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this.add(that.asBigInteger)
        is Double -> this.toBigDecimal(mathContext = Precision.mathContext).add(that.asBigDecimal)
        is BigInteger -> this.add(that.asBigInteger)
        is BigDecimal -> this.toBigDecimal(mathContext = Precision.mathContext).add(that.asBigDecimal)
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigInteger.minus(that: BigDecimal): Number {
    return this.toBigDecimal(mathContext = Precision.mathContext).subtract(that)
}

operator fun BigInteger.minus(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this.subtract(that.asBigInteger)
        is Double -> this.toBigDecimal(mathContext = Precision.mathContext).subtract(that.asBigDecimal)
        is BigInteger -> this.subtract(that.asBigInteger)
        is BigDecimal -> this.toBigDecimal(mathContext = Precision.mathContext).subtract(that.asBigDecimal)
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigInteger.times(that: BigDecimal): Number {
    return this.toBigDecimal(mathContext = Precision.mathContext).multiply(that)
}

operator fun BigInteger.times(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this.multiply(that.asBigInteger)
        is Double -> this.toBigDecimal(mathContext = Precision.mathContext).multiply(that.asBigDecimal)
        is BigInteger -> this.multiply(that.asBigInteger)
        is BigDecimal -> this.toBigDecimal(mathContext = Precision.mathContext).multiply(that.asBigDecimal)
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigInteger.div(that: BigDecimal): Number {
    return this.toBigDecimal(mathContext = Precision.mathContext).divide(that,
        Precision.mathContext.roundingMode
    )
}

operator fun BigInteger.div(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this.divide(that.asBigInteger)
        is Double -> this.toBigDecimal(mathContext = Precision.mathContext).divide(that.asBigDecimal,
            Precision.mathContext.roundingMode
        )
        is BigInteger -> this.divide(that.asBigInteger)
        is BigDecimal -> this.toBigDecimal(mathContext = Precision.mathContext).divide(that.asBigDecimal,
            Precision.mathContext.roundingMode
        )
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigInteger.rem(that: BigDecimal): Number {
    return this.toBigDecimal(mathContext = Precision.mathContext).remainder(that)
}

operator fun BigInteger.rem(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this.remainder(that.asBigInteger)
        is Double -> this.toBigDecimal(mathContext = Precision.mathContext).remainder(that.asBigDecimal)
        is BigInteger -> this.remainder(that.asBigInteger)
        is BigDecimal -> this.toBigDecimal(mathContext = Precision.mathContext).remainder(that.asBigDecimal)
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigDecimal.plus(that: BigInteger): Number {
    return this.add(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun BigDecimal.plus(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this.add(that.asBigDecimal)
        is Double -> this.add(that.asBigDecimal)
        is BigInteger -> this.add(that.asBigDecimal)
        is BigDecimal -> this.add(that.asBigDecimal)
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigDecimal.minus(that: BigInteger): Number {
    return this.subtract(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun BigDecimal.minus(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this.subtract(that.asBigDecimal)
        is Double -> this.subtract(that.asBigDecimal)
        is BigInteger -> this.subtract(that.asBigDecimal)
        is BigDecimal -> this.subtract(that.asBigDecimal)
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigDecimal.times(that: BigInteger): Number {
    return this.multiply(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun BigDecimal.times(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this * (that.asBigInteger ?: throw InvalidCellException("Cell not numeric at ${that.index}"))
        is Double -> this * (that.asBigDecimal ?: throw InvalidCellException("Cell not numeric at ${that.index}"))
        is BigInteger -> this * (that.asBigInteger ?: throw InvalidCellException("Cell not numeric at ${that.index}"))
        is BigDecimal -> this * (that.asBigDecimal ?: throw InvalidCellException("Cell not numeric at ${that.index}"))
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigDecimal.div(that: BigInteger): Number {
    return this.divide(that.toBigDecimal(mathContext = Precision.mathContext),
        Precision.mathContext.roundingMode
    )
}

operator fun BigDecimal.div(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this.divide(that.asBigDecimal,
            Precision.mathContext.roundingMode
        )
        is Double -> this.divide(that.asBigDecimal,
            Precision.mathContext.roundingMode
        )
        is BigInteger -> this.divide(that.asBigDecimal,
            Precision.mathContext.roundingMode
        )
        is BigDecimal -> this.divide(that.asBigDecimal,
            Precision.mathContext.roundingMode
        )
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun BigDecimal.rem(that: BigInteger): Number {
    return this.remainder(that.toBigDecimal(mathContext = Precision.mathContext))
}

operator fun BigDecimal.rem(that: Cell<*>): Number {
    return when (that.value) {
        is Long -> this.remainder(that.asBigDecimal)
        is Double -> this.remainder(that.asBigDecimal)
        is BigInteger -> this.remainder(that.asBigDecimal)
        is BigDecimal -> this.remainder(that.asBigDecimal)
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Number.plus(that: Cell<*>): Number {
    return when (this) {
        is Int -> this + that
        is Long -> this + that
        is Float -> this + that
        is Double -> this + that
        is BigInteger -> this + that
        is BigDecimal -> this + that
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.plus(that: Int): Number {
    return when (this) {
        is Int -> this + that
        is Long -> this + that
        is Float -> this + that
        is Double -> this + that
        is BigInteger -> this.add(that.toBigInteger())
        is BigDecimal -> this.add(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.plus(that: Long): Number {
    return when (this) {
        is Int -> this + that
        is Long -> this + that
        is Float -> this + that
        is Double -> this + that
        is BigInteger -> this.add(that.toBigInteger())
        is BigDecimal -> this.add(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.plus(that: Float): Number {
    return when (this) {
        is Int -> this + that
        is Long -> this + that
        is Float -> this + that
        is Double -> this + that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).add(that.toBigDecimal(
            Precision.mathContext
        ))
        is BigDecimal -> this.add(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.plus(that: Double): Number {
    return when (this) {
        is Int -> this + that
        is Long -> this + that
        is Float -> this + that
        is Double -> this + that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).add(that.toBigDecimal(
            Precision.mathContext
        ))
        is BigDecimal -> this.add(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.plus(that: BigInteger): Number {
    return when (this) {
        is Int -> this + that
        is Long -> this + that
        is Float -> this + that
        is Double -> this + that
        is BigInteger -> this.add(that)
        is BigDecimal -> this.add(that.toBigDecimal(mathContext = Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.plus(that: BigDecimal): Number {
    return when (this) {
        is Int -> this + that
        is Long -> this + that
        is Float -> this + that
        is Double -> this + that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).add(that)
        is BigDecimal -> this.add(that)
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.plus(that: Number): Number {
    return when (that) {
        is Int -> this + that
        is Long -> this + that
        is Float -> this + that
        is Double -> this + that
        is BigInteger -> this + that
        is BigDecimal -> this + that
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.minus(that: Cell<*>): Number {
    return when (this) {
        is Int -> this - that
        is Long -> this - that
        is Float -> this - that
        is Double -> this - that
        is BigInteger -> this - that
        is BigDecimal -> this - that
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.minus(that: Int): Number {
    return when (this) {
        is Int -> this - that
        is Long -> this - that
        is Float -> this - that
        is Double -> this - that
        is BigInteger -> this.subtract(that.toBigInteger())
        is BigDecimal -> this.subtract(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.minus(that: Long): Number {
    return when (this) {
        is Int -> this - that
        is Long -> this - that
        is Float -> this - that
        is Double -> this - that
        is BigInteger -> this.subtract(that.toBigInteger())
        is BigDecimal -> this.subtract(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.minus(that: Float): Number {
    return when (this) {
        is Int -> this - that
        is Long -> this - that
        is Float -> this - that
        is Double -> this - that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).subtract(that.toBigDecimal(
            Precision.mathContext
        ))
        is BigDecimal -> this.subtract(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.minus(that: Double): Number {
    return when (this) {
        is Int -> this - that
        is Long -> this - that
        is Float -> this - that
        is Double -> this - that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).subtract(that.toBigDecimal(
            Precision.mathContext
        ))
        is BigDecimal -> this.subtract(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.minus(that: BigInteger): Number {
    return when (this) {
        is Int -> this - that
        is Long -> this - that
        is Float -> this - that
        is Double -> this - that
        is BigInteger -> this.subtract(that)
        is BigDecimal -> this.subtract(that.toBigDecimal(mathContext = Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.minus(that: BigDecimal): Number {
    return when (this) {
        is Int -> this - that
        is Long -> this - that
        is Float -> this - that
        is Double -> this - that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).subtract(that)
        is BigDecimal -> this.subtract(that)
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.minus(that: Number): Number {
    return when (that) {
        is Int -> this - that
        is Long -> this - that
        is Float -> this - that
        is Double -> this - that
        is BigInteger -> this - that
        is BigDecimal -> this - that
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.times(that: Cell<*>): Number {
    return when (this) {
        is Int -> this * that
        is Long -> this * that
        is Float -> this * that
        is Double -> this * that
        is BigInteger -> this * that
        is BigDecimal -> this * that
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Number.times(that: Int): Number {
    return when (this) {
        is Int -> this * that
        is Long -> this * that
        is Float -> this * that
        is Double -> this * that
        is BigInteger -> this.multiply(that.toBigInteger())
        is BigDecimal -> this.multiply(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.times(that: Long): Number {
    return when (this) {
        is Int -> this * that
        is Long -> this * that
        is Float -> this * that
        is Double -> this * that
        is BigInteger -> this.multiply(that.toBigInteger())
        is BigDecimal -> this.multiply(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.times(that: Float): Number {
    return when (this) {
        is Int -> this * that
        is Long -> this * that
        is Float -> this * that
        is Double -> this * that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).multiply(that.toBigDecimal(
            Precision.mathContext
        ))
        is BigDecimal -> this.multiply(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.times(that: Double): Number {
    return when (this) {
        is Int -> this * that
        is Long -> this * that
        is Float -> this * that
        is Double -> this * that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).multiply(that.toBigDecimal(
            Precision.mathContext
        ))
        is BigDecimal -> this.multiply(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.times(that: BigInteger): Number {
    return when (this) {
        is Int -> this * that
        is Long -> this * that
        is Float -> this * that
        is Double -> this * that
        is BigInteger -> this.multiply(that)
        is BigDecimal -> this.multiply(that.toBigDecimal(mathContext = Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.times(that: BigDecimal): Number {
    return when (this) {
        is Int -> this * that
        is Long -> this * that
        is Float -> this * that
        is Double -> this * that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).multiply(that)
        is BigDecimal -> this.multiply(that)
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.times(that: Number): Number {
    return when (that) {
        is Int -> this * that
        is Long -> this * that
        is Float -> this * that
        is Double -> this * that
        is BigInteger -> this * that
        is BigDecimal -> this * that
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.div(that: Cell<*>): Number {
    return when (this) {
        is Int -> this / that
        is Long -> this / that
        is Float -> this / that
        is Double -> this / that
        is BigInteger -> this / that
        is BigDecimal -> this / that
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Number.div(that: Int): Number {
    return when (this) {
        is Int -> this / that
        is Long -> this / that
        is Float -> this / that
        is Double -> this / that
        is BigInteger -> this.divide(that.toBigInteger())
        is BigDecimal -> this.divide(that.toBigDecimal(Precision.mathContext),
            Precision.mathContext.roundingMode
        )
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.div(that: Long): Number {
    return when (this) {
        is Int -> this / that
        is Long -> this / that
        is Float -> this / that
        is Double -> this / that
        is BigInteger -> this.divide(that.toBigInteger())
        is BigDecimal -> this.divide(that.toBigDecimal(Precision.mathContext),
            Precision.mathContext.roundingMode
        )
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.div(that: Float): Number {
    return when (this) {
        is Int -> this / that
        is Long -> this / that
        is Float -> this / that
        is Double -> this / that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).divide(that.toBigDecimal(
            Precision.mathContext
        ), Precision.mathContext.roundingMode
        )
        is BigDecimal -> this.divide(that.toBigDecimal(Precision.mathContext),
            Precision.mathContext.roundingMode
        )
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.div(that: Double): Number {
    return when (this) {
        is Int -> this / that
        is Long -> this / that
        is Float -> this / that
        is Double -> this / that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).divide(that.toBigDecimal(
            Precision.mathContext
        ), Precision.mathContext.roundingMode)
        is BigDecimal -> this.divide(that.toBigDecimal(Precision.mathContext),
            Precision.mathContext.roundingMode
        )
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.div(that: BigInteger): Number {
    return when (this) {
        is Int -> this / that
        is Long -> this / that
        is Float -> this / that
        is Double -> this / that
        is BigInteger -> this.divide(that)
        is BigDecimal -> this.divide(that.toBigDecimal(mathContext = Precision.mathContext),
            Precision.mathContext.roundingMode
        )
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.div(that: BigDecimal): Number {
    return when (this) {
        is Int -> this / that
        is Long -> this / that
        is Float -> this / that
        is Double -> this / that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).divide(that,
            Precision.mathContext.roundingMode
        )
        is BigDecimal -> this.divide(that,
            Precision.mathContext.roundingMode
        )
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.div(that: Number): Number {
    return when (that) {
        is Int -> this / that
        is Long -> this / that
        is Float -> this / that
        is Double -> this / that
        is BigInteger -> this / that
        is BigDecimal -> this / that
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.rem(that: Cell<*>): Number {
    return when (this) {
        is Int -> this % that
        is Long -> this % that
        is Float -> this % that
        is Double -> this % that
        is BigInteger -> this % that
        is BigDecimal -> this % that
        else -> throw InvalidCellException("Cell not numeric at ${that.index}")
    }
}

operator fun Number.rem(that: Int): Number {
    return when (this) {
        is Int -> this % that
        is Long -> this % that
        is Float -> this % that
        is Double -> this % that
        is BigInteger -> this.remainder(that.toBigInteger())
        is BigDecimal -> this.remainder(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.rem(that: Long): Number {
    return when (this) {
        is Int -> this % that
        is Long -> this % that
        is Float -> this % that
        is Double -> this % that
        is BigInteger -> this.remainder(that.toBigInteger())
        is BigDecimal -> this.remainder(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.rem(that: Float): Number {
    return when (this) {
        is Int -> this % that
        is Long -> this % that
        is Float -> this % that
        is Double -> this % that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).remainder(that.toBigDecimal(
            Precision.mathContext
        ))
        is BigDecimal -> this.remainder(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.rem(that: Double): Number {
    return when (this) {
        is Int -> this % that
        is Long -> this % that
        is Float -> this % that
        is Double -> this % that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).remainder(that.toBigDecimal(
            Precision.mathContext
        ))
        is BigDecimal -> this.remainder(that.toBigDecimal(Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.rem(that: BigInteger): Number {
    return when (this) {
        is Int -> this % that
        is Long -> this % that
        is Float -> this % that
        is Double -> this % that
        is BigInteger -> this.remainder(that)
        is BigDecimal -> this.remainder(that.toBigDecimal(mathContext = Precision.mathContext))
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.rem(that: BigDecimal): Number {
    return when (this) {
        is Int -> this % that
        is Long -> this % that
        is Float -> this % that
        is Double -> this % that
        is BigInteger -> this.toBigDecimal(mathContext = Precision.mathContext).remainder(that)
        is BigDecimal -> this.remainder(that)
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.rem(that: Number): Number {
    return when (that) {
        is Int -> this % that
        is Long -> this % that
        is Float -> this % that
        is Double -> this % that
        is BigInteger -> this % that
        is BigDecimal -> this % that
        else -> throw UnsupportedOperationException()
    }
}

operator fun Number.compareTo(other: Cell<*>): Int = other.compareTo(this) * -1
