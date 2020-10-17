package sigbla.app

import sigbla.app.internals.Registry
import sigbla.app.Table.Companion.newTable
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

class BasicMathTest {
    @After
    fun cleanup() {
        Registry.tableNames().forEach { Registry.deleteTable(it) }
    }

    @Test
    fun basicTableMath() {
        // Testing math between number and cell
        val t = newTable("basicTableMath")

        fun typeValue(clazz: KClass<*>): Int {
            return when (clazz) {
                BigDecimal::class -> 6
                BigInteger::class -> 5
                Double::class -> 4
                Float::class -> 3
                Long::class -> 2
                Int::class -> 1
                else -> throw UnsupportedOperationException(clazz.toString())
            }
        }

        fun typePref(class1: KClass<*>, class2: KClass<*>): KClass<*> {
            val (class3, class4) = listOf(class1, class2).sortedWith(Comparator { o1, o2 -> typeValue(o1).compareTo(typeValue(o2)) })

            return when (class4) {
                BigInteger::class -> when (class3) {
                    Int::class -> BigInteger::class
                    Long::class -> BigInteger::class
                    Float::class -> BigDecimal::class
                    Double::class -> BigDecimal::class
                    BigInteger::class -> BigInteger::class
                    BigDecimal::class -> BigDecimal::class
                    else -> throw UnsupportedOperationException("$class3 $class4")
                }
                BigDecimal::class -> BigDecimal::class
                else -> class4
            }
        }

        val values = listOf(1, 2L, 3F, 3.0, BigInteger.TWO, BigDecimal.TEN)

        var idx = 0
        for (val2 in values) {
            t["Val2"][idx] = val2 as Number
            idx++
        }

        for (i in 0 until idx) {
            assertEquals(typePref(1::class, t["Val2"][i].value!!::class), (1 + t["Val2"][i])::class)
            assertEquals(typePref(1::class, t["Val2"][i].value!!::class), (1 - t["Val2"][i])::class)
            assertEquals(typePref(1::class, t["Val2"][i].value!!::class), (1 * t["Val2"][i])::class)
            assertEquals(typePref(1::class, t["Val2"][i].value!!::class), (1 / t["Val2"][i])::class)
            assertEquals(typePref(1::class, t["Val2"][i].value!!::class), (1 % t["Val2"][i])::class)

            assertEquals(typePref(2L::class, t["Val2"][i].value!!::class), (2L + t["Val2"][i])::class)
            assertEquals(typePref(2L::class, t["Val2"][i].value!!::class), (2L - t["Val2"][i])::class)
            assertEquals(typePref(2L::class, t["Val2"][i].value!!::class), (2L * t["Val2"][i])::class)
            assertEquals(typePref(2L::class, t["Val2"][i].value!!::class), (2L / t["Val2"][i])::class)
            assertEquals(typePref(2L::class, t["Val2"][i].value!!::class), (2L % t["Val2"][i])::class)

            assertEquals(typePref(3F::class, t["Val2"][i].value!!::class), (3F + t["Val2"][i])::class)
            assertEquals(typePref(3F::class, t["Val2"][i].value!!::class), (3F - t["Val2"][i])::class)
            assertEquals(typePref(3F::class, t["Val2"][i].value!!::class), (3F * t["Val2"][i])::class)
            assertEquals(typePref(3F::class, t["Val2"][i].value!!::class), (3F / t["Val2"][i])::class)
            assertEquals(typePref(3F::class, t["Val2"][i].value!!::class), (3F % t["Val2"][i])::class)

            assertEquals(typePref(3.0::class, t["Val2"][i].value!!::class), (3.0 + t["Val2"][i])::class)
            assertEquals(typePref(3.0::class, t["Val2"][i].value!!::class), (3.0 - t["Val2"][i])::class)
            assertEquals(typePref(3.0::class, t["Val2"][i].value!!::class), (3.0 * t["Val2"][i])::class)
            assertEquals(typePref(3.0::class, t["Val2"][i].value!!::class), (3.0 / t["Val2"][i])::class)
            assertEquals(typePref(3.0::class, t["Val2"][i].value!!::class), (3.0 % t["Val2"][i])::class)

            assertEquals(typePref(BigInteger.TWO::class, t["Val2"][i].value!!::class), (BigInteger.TWO + t["Val2"][i])::class)
            assertEquals(typePref(BigInteger.TWO::class, t["Val2"][i].value!!::class), (BigInteger.TWO - t["Val2"][i])::class)
            assertEquals(typePref(BigInteger.TWO::class, t["Val2"][i].value!!::class), (BigInteger.TWO * t["Val2"][i])::class)
            assertEquals(typePref(BigInteger.TWO::class, t["Val2"][i].value!!::class), (BigInteger.TWO / t["Val2"][i])::class)
            assertEquals(typePref(BigInteger.TWO::class, t["Val2"][i].value!!::class), (BigInteger.TWO % t["Val2"][i])::class)

            assertEquals(typePref(BigDecimal.TEN::class, t["Val2"][i].value!!::class), (BigDecimal.TEN + t["Val2"][i])::class)
            assertEquals(typePref(BigDecimal.TEN::class, t["Val2"][i].value!!::class), (BigDecimal.TEN - t["Val2"][i])::class)
            assertEquals(typePref(BigDecimal.TEN::class, t["Val2"][i].value!!::class), (BigDecimal.TEN * t["Val2"][i])::class)
            assertEquals(typePref(BigDecimal.TEN::class, t["Val2"][i].value!!::class), (BigDecimal.TEN / t["Val2"][i])::class)
            assertEquals(typePref(BigDecimal.TEN::class, t["Val2"][i].value!!::class), (BigDecimal.TEN % t["Val2"][i])::class)
        }

        for (valAny in values) {
            val val1: Number = valAny as Number

            for (i in 0 until idx) {
                assertEquals(typePref(val1::class, t["Val2"][i].value!!::class), (val1 + t["Val2"][i])::class)
                assertEquals(typePref(val1::class, t["Val2"][i].value!!::class), (val1 - t["Val2"][i])::class)
                assertEquals(typePref(val1::class, t["Val2"][i].value!!::class), (val1 * t["Val2"][i])::class)
                assertEquals(typePref(val1::class, t["Val2"][i].value!!::class), (val1 / t["Val2"][i])::class)
                assertEquals(typePref(val1::class, t["Val2"][i].value!!::class), (val1 % t["Val2"][i])::class)
            }
        }

        listOf(1, 2L, 3F, 3.0, BigInteger.TWO, BigDecimal.TEN)

        for (valAny in values) {
            val val1: Number = valAny as Number

            idx = 0

            assertEquals(LongCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + t["Val2"][idx].toLong(), val1 + t["Val2"][idx])
            assertEquals(val1 - t["Val2"][idx].toLong(), val1 - t["Val2"][idx])
            assertEquals(val1 * t["Val2"][idx].toLong(), val1 * t["Val2"][idx])
            assertEquals(val1 / t["Val2"][idx].toLong(), val1 / t["Val2"][idx])
            assertEquals(val1 % t["Val2"][idx].toLong(), val1 % t["Val2"][idx])

            idx = 1

            assertEquals(LongCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + t["Val2"][idx].toLong(), val1 + t["Val2"][idx])
            assertEquals(val1 - t["Val2"][idx].toLong(), val1 - t["Val2"][idx])
            assertEquals(val1 * t["Val2"][idx].toLong(), val1 * t["Val2"][idx])
            assertEquals(val1 / t["Val2"][idx].toLong(), val1 / t["Val2"][idx])
            assertEquals(val1 % t["Val2"][idx].toLong(), val1 % t["Val2"][idx])

            idx = 2

            assertEquals(DoubleCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + t["Val2"][idx].toDouble(), val1 + t["Val2"][idx])
            assertEquals(val1 - t["Val2"][idx].toDouble(), val1 - t["Val2"][idx])
            assertEquals(val1 * t["Val2"][idx].toDouble(), val1 * t["Val2"][idx])
            assertEquals(val1 / t["Val2"][idx].toDouble(), val1 / t["Val2"][idx])
            assertEquals(val1 % t["Val2"][idx].toDouble(), val1 % t["Val2"][idx])

            idx = 3

            assertEquals(DoubleCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + t["Val2"][idx].toDouble(), val1 + t["Val2"][idx])
            assertEquals(val1 - t["Val2"][idx].toDouble(), val1 - t["Val2"][idx])
            assertEquals(val1 * t["Val2"][idx].toDouble(), val1 * t["Val2"][idx])
            assertEquals(val1 / t["Val2"][idx].toDouble(), val1 / t["Val2"][idx])
            assertEquals(val1 % t["Val2"][idx].toDouble(), val1 % t["Val2"][idx])

            idx = 4

            assertEquals(BigIntegerCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + t["Val2"][idx].toBigInteger(), val1 + t["Val2"][idx])
            assertEquals(val1 - t["Val2"][idx].toBigInteger(), val1 - t["Val2"][idx])
            assertEquals(val1 * t["Val2"][idx].toBigInteger(), val1 * t["Val2"][idx])
            assertEquals(val1 / t["Val2"][idx].toBigInteger(), val1 / t["Val2"][idx])
            assertEquals(val1 % t["Val2"][idx].toBigInteger(), val1 % t["Val2"][idx])

            idx = 5

            assertEquals(BigDecimalCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + t["Val2"][idx].toBigDecimal(), val1 + t["Val2"][idx])
            assertEquals(val1 - t["Val2"][idx].toBigDecimal(), val1 - t["Val2"][idx])
            assertEquals(val1 * t["Val2"][idx].toBigDecimal(), val1 * t["Val2"][idx])
            assertEquals(val1 / t["Val2"][idx].toBigDecimal(), val1 / t["Val2"][idx])
            assertEquals(val1 % t["Val2"][idx].toBigDecimal(), val1 % t["Val2"][idx])
        }
    }
}