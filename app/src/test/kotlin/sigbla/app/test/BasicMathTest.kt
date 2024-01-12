/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import sigbla.app.exceptions.InvalidCellException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlin.reflect.KClass

class BasicMathTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `basic table math`() {
        // Testing math between number and cell
        val t = Table[object {}.javaClass.enclosingMethod.name]

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
            val (class3, class4) = listOf(class1, class2).sortedWith { o1, o2 -> typeValue(o1).compareTo(typeValue(o2)) }

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

        val values = listOf(1, 2L, 3F, 4.0, BigInteger.TWO, BigDecimal.TEN)

        var idx = 0
        for (val2 in values) {
            t["Val2"][idx] = val2 as Number
            idx++
        }

        for (i in 0 until idx) {
            assertEquals(typePref(1::class, valueOf<Any>(t["Val2"][i])!!::class), (1 + t["Val2"][i])::class)
            assertEquals(typePref(1::class, valueOf<Any>(t["Val2"][i])!!::class), (1 - t["Val2"][i])::class)
            assertEquals(typePref(1::class, valueOf<Any>(t["Val2"][i])!!::class), (1 * t["Val2"][i])::class)
            assertEquals(typePref(1::class, valueOf<Any>(t["Val2"][i])!!::class), (1 / t["Val2"][i])::class)
            assertEquals(typePref(1::class, valueOf<Any>(t["Val2"][i])!!::class), (1 % t["Val2"][i])::class)

            assertEquals(typePref(2L::class, valueOf<Any>(t["Val2"][i])!!::class), (2L + t["Val2"][i])::class)
            assertEquals(typePref(2L::class, valueOf<Any>(t["Val2"][i])!!::class), (2L - t["Val2"][i])::class)
            assertEquals(typePref(2L::class, valueOf<Any>(t["Val2"][i])!!::class), (2L * t["Val2"][i])::class)
            assertEquals(typePref(2L::class, valueOf<Any>(t["Val2"][i])!!::class), (2L / t["Val2"][i])::class)
            assertEquals(typePref(2L::class, valueOf<Any>(t["Val2"][i])!!::class), (2L % t["Val2"][i])::class)

            assertEquals(typePref(3F::class, valueOf<Any>(t["Val2"][i])!!::class), (3F + t["Val2"][i])::class)
            assertEquals(typePref(3F::class, valueOf<Any>(t["Val2"][i])!!::class), (3F - t["Val2"][i])::class)
            assertEquals(typePref(3F::class, valueOf<Any>(t["Val2"][i])!!::class), (3F * t["Val2"][i])::class)
            assertEquals(typePref(3F::class, valueOf<Any>(t["Val2"][i])!!::class), (3F / t["Val2"][i])::class)
            assertEquals(typePref(3F::class, valueOf<Any>(t["Val2"][i])!!::class), (3F % t["Val2"][i])::class)

            assertEquals(typePref(4.0::class, valueOf<Any>(t["Val2"][i])!!::class), (3.0 + t["Val2"][i])::class)
            assertEquals(typePref(4.0::class, valueOf<Any>(t["Val2"][i])!!::class), (3.0 - t["Val2"][i])::class)
            assertEquals(typePref(4.0::class, valueOf<Any>(t["Val2"][i])!!::class), (3.0 * t["Val2"][i])::class)
            assertEquals(typePref(4.0::class, valueOf<Any>(t["Val2"][i])!!::class), (3.0 / t["Val2"][i])::class)
            assertEquals(typePref(4.0::class, valueOf<Any>(t["Val2"][i])!!::class), (3.0 % t["Val2"][i])::class)

            assertEquals(typePref(BigInteger.TWO::class, valueOf<Any>(t["Val2"][i])!!::class), (BigInteger.TWO + t["Val2"][i])::class)
            assertEquals(typePref(BigInteger.TWO::class, valueOf<Any>(t["Val2"][i])!!::class), (BigInteger.TWO - t["Val2"][i])::class)
            assertEquals(typePref(BigInteger.TWO::class, valueOf<Any>(t["Val2"][i])!!::class), (BigInteger.TWO * t["Val2"][i])::class)
            assertEquals(typePref(BigInteger.TWO::class, valueOf<Any>(t["Val2"][i])!!::class), (BigInteger.TWO / t["Val2"][i])::class)
            assertEquals(typePref(BigInteger.TWO::class, valueOf<Any>(t["Val2"][i])!!::class), (BigInteger.TWO % t["Val2"][i])::class)

            assertEquals(typePref(BigDecimal.TEN::class, valueOf<Any>(t["Val2"][i])!!::class), (BigDecimal.TEN + t["Val2"][i])::class)
            assertEquals(typePref(BigDecimal.TEN::class, valueOf<Any>(t["Val2"][i])!!::class), (BigDecimal.TEN - t["Val2"][i])::class)
            assertEquals(typePref(BigDecimal.TEN::class, valueOf<Any>(t["Val2"][i])!!::class), (BigDecimal.TEN * t["Val2"][i])::class)
            assertEquals(typePref(BigDecimal.TEN::class, valueOf<Any>(t["Val2"][i])!!::class), (BigDecimal.TEN / t["Val2"][i])::class)
            assertEquals(typePref(BigDecimal.TEN::class, valueOf<Any>(t["Val2"][i])!!::class), (BigDecimal.TEN % t["Val2"][i])::class)
        }

        for (valAny in values) {
            val val1: Number = valAny as Number

            for (i in 0 until idx) {
                assertEquals(typePref(val1::class, valueOf<Any>(t["Val2"][i])!!::class), (val1 + t["Val2"][i])::class)
                assertEquals(typePref(val1::class, valueOf<Any>(t["Val2"][i])!!::class), (val1 - t["Val2"][i])::class)
                assertEquals(typePref(val1::class, valueOf<Any>(t["Val2"][i])!!::class), (val1 * t["Val2"][i])::class)
                assertEquals(typePref(val1::class, valueOf<Any>(t["Val2"][i])!!::class), (val1 / t["Val2"][i])::class)
                assertEquals(typePref(val1::class, valueOf<Any>(t["Val2"][i])!!::class), (val1 % t["Val2"][i])::class)
            }
        }

        for (valAny in values) {
            val val1: Number = valAny as Number

            idx = 0

            assertEquals(LongCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 + t["Val2"][idx])
            assertEquals(val1 - (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 - t["Val2"][idx])
            assertEquals(val1 * (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 * t["Val2"][idx])
            assertEquals(val1 / (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 / t["Val2"][idx])
            assertEquals(val1 % (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 % t["Val2"][idx])

            idx = 1

            assertEquals(LongCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 + t["Val2"][idx])
            assertEquals(val1 - (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 - t["Val2"][idx])
            assertEquals(val1 * (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 * t["Val2"][idx])
            assertEquals(val1 / (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 / t["Val2"][idx])
            assertEquals(val1 % (t["Val2"][idx].asLong ?: throw InvalidCellException("")), val1 % t["Val2"][idx])

            idx = 2

            assertEquals(DoubleCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 + t["Val2"][idx])
            assertEquals(val1 - (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 - t["Val2"][idx])
            assertEquals(val1 * (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 * t["Val2"][idx])
            assertEquals(val1 / (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 / t["Val2"][idx])
            assertEquals(val1 % (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 % t["Val2"][idx])

            idx = 3

            assertEquals(DoubleCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 + t["Val2"][idx])
            assertEquals(val1 - (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 - t["Val2"][idx])
            assertEquals(val1 * (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 * t["Val2"][idx])
            assertEquals(val1 / (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 / t["Val2"][idx])
            assertEquals(val1 % (t["Val2"][idx].asDouble ?: throw InvalidCellException("")), val1 % t["Val2"][idx])

            idx = 4

            assertEquals(BigIntegerCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + (t["Val2"][idx].asBigInteger ?: throw InvalidCellException("")), val1 + t["Val2"][idx])
            assertEquals(val1 - (t["Val2"][idx].asBigInteger ?: throw InvalidCellException("")), val1 - t["Val2"][idx])
            assertEquals(val1 * (t["Val2"][idx].asBigInteger ?: throw InvalidCellException("")), val1 * t["Val2"][idx])
            assertEquals(val1 / (t["Val2"][idx].asBigInteger ?: throw InvalidCellException("")), val1 / t["Val2"][idx])
            assertEquals(val1 % (t["Val2"][idx].asBigInteger ?: throw InvalidCellException("")), val1 % t["Val2"][idx])

            idx = 5

            assertEquals(BigDecimalCell::class, t["Val2"][idx]::class)
            assertEquals(val1 + (t["Val2"][idx].asBigDecimal ?: throw InvalidCellException("")), val1 + t["Val2"][idx])
            assertEquals(val1 - (t["Val2"][idx].asBigDecimal ?: throw InvalidCellException("")), val1 - t["Val2"][idx])
            assertEquals(val1 * (t["Val2"][idx].asBigDecimal ?: throw InvalidCellException("")), val1 * t["Val2"][idx])
            assertEquals(val1 / (t["Val2"][idx].asBigDecimal ?: throw InvalidCellException("")), val1 / t["Val2"][idx])
            assertEquals(val1 % (t["Val2"][idx].asBigDecimal ?: throw InvalidCellException("")), val1 % t["Val2"][idx])
        }

        assertEquals((1L as Number) + (1 as Number), t["Val2"][0] + (1 as Number))
        assertEquals((1 as Number) + (1L as Number), (1 as Number) + t["Val2"][0])
        assertEquals((1L as Number) + (2L as Number), t["Val2"][0] + (2L as Number))
        assertEquals((1 as Number) + (2L as Number), (1 as Number) + t["Val2"][1])
        assertEquals((1L as Number) + (3F as Number), t["Val2"][0] + (3F as Number))
        assertEquals((1 as Number) + (3.0 as Number), (1 as Number) + t["Val2"][2])
        assertEquals((1L as Number) + (4.0 as Number), t["Val2"][0] + (4.0 as Number))
        assertEquals((1 as Number) + (4.0 as Number), (1 as Number) + t["Val2"][3])
        assertEquals((1 as Number) + (BigInteger.TWO as Number), t["Val2"][0] + (BigInteger.TWO as Number))
        assertEquals((1 as Number) + (BigInteger.TWO as Number), (1 as Number) + t["Val2"][4])
        assertEquals((1 as Number) + (BigDecimal.TEN as Number), t["Val2"][0] + (BigDecimal.TEN as Number))
        assertEquals((1 as Number) + (BigDecimal.TEN as Number), (1 as Number) + t["Val2"][5])

        assertEquals((2L as Number) + (1 as Number), t["Val2"][1] + (1 as Number))
        assertEquals((2L as Number) + (1 as Number), (2L as Number) + t["Val2"][0])
        assertEquals((2L as Number) + (2L as Number), t["Val2"][1] + (2L as Number))
        assertEquals((2L as Number) + (2L as Number), (2L as Number) + t["Val2"][1])
        assertEquals((2L as Number) + (3F as Number), t["Val2"][1] + (3F as Number))
        assertEquals((2L as Number) + (3.0 as Number), (2L as Number) + t["Val2"][2])
        assertEquals((2L as Number) + (4.0 as Number), t["Val2"][1] + (4.0 as Number))
        assertEquals((2L as Number) + (4.0 as Number), (2L as Number) + t["Val2"][3])
        assertEquals((2L as Number) + (BigInteger.TWO as Number), t["Val2"][1] + (BigInteger.TWO as Number))
        assertEquals((2L as Number) + (BigInteger.TWO as Number), (2L as Number) + t["Val2"][4])
        assertEquals((2L as Number) + (BigDecimal.TEN as Number), t["Val2"][1] + (BigDecimal.TEN as Number))
        assertEquals((2L as Number) + (BigDecimal.TEN as Number), (2L as Number) + t["Val2"][5])

        assertEquals((3.0 as Number) + (1 as Number), t["Val2"][2] + (1 as Number))
        assertEquals((3F as Number) + (1 as Number), (3F as Number) + t["Val2"][0])
        assertEquals((3.0 as Number) + (2L as Number), t["Val2"][2] + (2L as Number))
        assertEquals((3F as Number) + (2L as Number), (3F as Number) + t["Val2"][1])
        assertEquals((3.0 as Number) + (3F as Number), t["Val2"][2] + (3F as Number))
        assertEquals((3F as Number) + (3.0 as Number), (3F as Number) + t["Val2"][2])
        assertEquals((3F as Number) + (4.0 as Number), t["Val2"][2] + (4.0 as Number))
        assertEquals((3F as Number) + (4.0 as Number), (3F as Number) + t["Val2"][3])
        assertEquals((3F as Number) + (BigInteger.TWO as Number), t["Val2"][2] + (BigInteger.TWO as Number))
        assertEquals((3F as Number) + (BigInteger.TWO as Number), (3F as Number) + t["Val2"][4])
        assertEquals((3F as Number) + (BigDecimal.TEN as Number), t["Val2"][2] + (BigDecimal.TEN as Number))
        assertEquals((3F as Number) + (BigDecimal.TEN as Number), (3F as Number) + t["Val2"][5])

        assertEquals((4.0 as Number) + (1 as Number), t["Val2"][3] + (1 as Number))
        assertEquals((4.0 as Number) + (1 as Number), (4.0 as Number) + t["Val2"][0])
        assertEquals((4.0 as Number) + (2L as Number), t["Val2"][3] + (2L as Number))
        assertEquals((4.0 as Number) + (2L as Number), (4.0 as Number) + t["Val2"][1])
        assertEquals((4.0 as Number) + (3F as Number), t["Val2"][3] + (3F as Number))
        assertEquals((4.0 as Number) + (3F as Number), (4.0 as Number) + t["Val2"][2])
        assertEquals((4.0 as Number) + (4.0 as Number), t["Val2"][3] + (4.0 as Number))
        assertEquals((4.0 as Number) + (4.0 as Number), (4.0 as Number) + t["Val2"][3])
        assertEquals((4.0 as Number) + (BigInteger.TWO as Number), t["Val2"][3] + (BigInteger.TWO as Number))
        assertEquals((4.0 as Number) + (BigInteger.TWO as Number), (4.0 as Number) + t["Val2"][4])
        assertEquals((4.0 as Number) + (BigDecimal.TEN as Number), t["Val2"][3] + (BigDecimal.TEN as Number))
        assertEquals((4.0 as Number) + (BigDecimal.TEN as Number), (4.0 as Number) + t["Val2"][5])

        assertEquals((BigInteger.TWO as Number) + (1 as Number), t["Val2"][4] + (1 as Number))
        assertEquals((BigInteger.TWO as Number) + (1 as Number), (BigInteger.TWO as Number) + t["Val2"][0])
        assertEquals((BigInteger.TWO as Number) + (2L as Number), t["Val2"][4] + (2L as Number))
        assertEquals((BigInteger.TWO as Number) + (2L as Number), (BigInteger.TWO as Number) + t["Val2"][1])
        assertEquals((BigInteger.TWO as Number) + (3F as Number), t["Val2"][4] + (3F as Number))
        assertEquals((BigInteger.TWO as Number) + (3F as Number), (BigInteger.TWO as Number) + t["Val2"][2])
        assertEquals((BigInteger.TWO as Number) + (4.0 as Number), t["Val2"][4] + (4.0 as Number))
        assertEquals((BigInteger.TWO as Number) + (4.0 as Number), (BigInteger.TWO as Number) + t["Val2"][3])
        assertEquals((BigInteger.TWO as Number) + (BigInteger.TWO as Number), t["Val2"][4] + (BigInteger.TWO as Number))
        assertEquals((BigInteger.TWO as Number) + (BigInteger.TWO as Number), (BigInteger.TWO as Number) + t["Val2"][4])
        assertEquals((BigInteger.TWO as Number) + (BigDecimal.TEN as Number), t["Val2"][4] + (BigDecimal.TEN as Number))
        assertEquals((BigInteger.TWO as Number) + (BigDecimal.TEN as Number), (BigInteger.TWO as Number) + t["Val2"][5])

        assertEquals((BigDecimal.TEN as Number) + (1 as Number), t["Val2"][5] + (1 as Number))
        assertEquals((BigDecimal.TEN as Number) + (1 as Number), (BigDecimal.TEN as Number) + t["Val2"][0])
        assertEquals((BigDecimal.TEN as Number) + (2L as Number), t["Val2"][5] + (2L as Number))
        assertEquals((BigDecimal.TEN as Number) + (2L as Number), (BigDecimal.TEN as Number) + t["Val2"][1])
        assertEquals((BigDecimal.TEN as Number) + (3F as Number), t["Val2"][5] + (3F as Number))
        assertEquals((BigDecimal.TEN as Number) + (3F as Number), (BigDecimal.TEN as Number) + t["Val2"][2])
        assertEquals((BigDecimal.TEN as Number) + (4.0 as Number), t["Val2"][5] + (4.0 as Number))
        assertEquals((BigDecimal.TEN as Number) + (4.0 as Number), (BigDecimal.TEN as Number) + t["Val2"][3])
        assertEquals((BigDecimal.TEN as Number) + (BigInteger.TWO as Number), t["Val2"][5] + (BigInteger.TWO as Number))
        assertEquals((BigDecimal.TEN as Number) + (BigInteger.TWO as Number), (BigDecimal.TEN as Number) + t["Val2"][4])
        assertEquals((BigDecimal.TEN as Number) + (BigDecimal.TEN as Number), t["Val2"][5] + (BigDecimal.TEN as Number))
        assertEquals((BigDecimal.TEN as Number) + (BigDecimal.TEN as Number), (BigDecimal.TEN as Number) + t["Val2"][5])

        assertEquals((1L as Number) - (1 as Number), t["Val2"][0] - (1 as Number))
        assertEquals((1 as Number) - (1L as Number), (1 as Number) - t["Val2"][0])
        assertEquals((1 as Number) - (2L as Number), t["Val2"][0] - (2L as Number))
        assertEquals((1 as Number) - (2L as Number), (1 as Number) - t["Val2"][1])
        assertEquals((1 as Number) - (3F as Number), t["Val2"][0] - (3F as Number))
        assertEquals((1 as Number) - (3.0 as Number), (1 as Number) - t["Val2"][2])
        assertEquals((1 as Number) - (4.0 as Number), t["Val2"][0] - (4.0 as Number))
        assertEquals((1 as Number) - (4.0 as Number), (1 as Number) - t["Val2"][3])
        assertEquals((1 as Number) - (BigInteger.TWO as Number), t["Val2"][0] - (BigInteger.TWO as Number))
        assertEquals((1 as Number) - (BigInteger.TWO as Number), (1 as Number) - t["Val2"][4])
        assertEquals((1 as Number) - (BigDecimal.TEN as Number), t["Val2"][0] - (BigDecimal.TEN as Number))
        assertEquals((1 as Number) - (BigDecimal.TEN as Number), (1 as Number) - t["Val2"][5])

        assertEquals((2L as Number) - (1 as Number), t["Val2"][1] - (1 as Number))
        assertEquals((2L as Number) - (1 as Number), (2L as Number) - t["Val2"][0])
        assertEquals((2L as Number) - (2L as Number), t["Val2"][1] - (2L as Number))
        assertEquals((2L as Number) - (2L as Number), (2L as Number) - t["Val2"][1])
        assertEquals((2L as Number) - (3F as Number), t["Val2"][1] - (3F as Number))
        assertEquals((2L as Number) - (3.0 as Number), (2L as Number) - t["Val2"][2])
        assertEquals((2L as Number) - (4.0 as Number), t["Val2"][1] - (4.0 as Number))
        assertEquals((2L as Number) - (4.0 as Number), (2L as Number) - t["Val2"][3])
        assertEquals((2L as Number) - (BigInteger.TWO as Number), t["Val2"][1] - (BigInteger.TWO as Number))
        assertEquals((2L as Number) - (BigInteger.TWO as Number), (2L as Number) - t["Val2"][4])
        assertEquals((2L as Number) - (BigDecimal.TEN as Number), t["Val2"][1] - (BigDecimal.TEN as Number))
        assertEquals((2L as Number) - (BigDecimal.TEN as Number), (2L as Number) - t["Val2"][5])

        assertEquals((3.0 as Number) - (1 as Number), t["Val2"][2] - (1 as Number))
        assertEquals((3F as Number) - (1 as Number), (3F as Number) - t["Val2"][0])
        assertEquals((3.0 as Number) - (2L as Number), t["Val2"][2] - (2L as Number))
        assertEquals((3F as Number) - (2L as Number), (3F as Number) - t["Val2"][1])
        assertEquals((3.0 as Number) - (3F as Number), t["Val2"][2] - (3F as Number))
        assertEquals((3F as Number) - (3.0 as Number), (3F as Number) - t["Val2"][2])
        assertEquals((3F as Number) - (4.0 as Number), t["Val2"][2] - (4.0 as Number))
        assertEquals((3F as Number) - (4.0 as Number), (3F as Number) - t["Val2"][3])
        assertEquals((3F as Number) - (BigInteger.TWO as Number), t["Val2"][2] - (BigInteger.TWO as Number))
        assertEquals((3F as Number) - (BigInteger.TWO as Number), (3F as Number) - t["Val2"][4])
        assertEquals((3F as Number) - (BigDecimal.TEN as Number), t["Val2"][2] - (BigDecimal.TEN as Number))
        assertEquals((3F as Number) - (BigDecimal.TEN as Number), (3F as Number) - t["Val2"][5])

        assertEquals((4.0 as Number) - (1 as Number), t["Val2"][3] - (1 as Number))
        assertEquals((4.0 as Number) - (1 as Number), (4.0 as Number) - t["Val2"][0])
        assertEquals((4.0 as Number) - (2L as Number), t["Val2"][3] - (2L as Number))
        assertEquals((4.0 as Number) - (2L as Number), (4.0 as Number) - t["Val2"][1])
        assertEquals((4.0 as Number) - (3F as Number), t["Val2"][3] - (3F as Number))
        assertEquals((4.0 as Number) - (3F as Number), (4.0 as Number) - t["Val2"][2])
        assertEquals((4.0 as Number) - (4.0 as Number), t["Val2"][3] - (4.0 as Number))
        assertEquals((4.0 as Number) - (4.0 as Number), (4.0 as Number) - t["Val2"][3])
        assertEquals((4.0 as Number) - (BigInteger.TWO as Number), t["Val2"][3] - (BigInteger.TWO as Number))
        assertEquals((4.0 as Number) - (BigInteger.TWO as Number), (4.0 as Number) - t["Val2"][4])
        assertEquals((4.0 as Number) - (BigDecimal.TEN as Number), t["Val2"][3] - (BigDecimal.TEN as Number))
        assertEquals((4.0 as Number) - (BigDecimal.TEN as Number), (4.0 as Number) - t["Val2"][5])

        assertEquals((BigInteger.TWO as Number) - (1 as Number), t["Val2"][4] - (1 as Number))
        assertEquals((BigInteger.TWO as Number) - (1 as Number), (BigInteger.TWO as Number) - t["Val2"][0])
        assertEquals((BigInteger.TWO as Number) - (2L as Number), t["Val2"][4] - (2L as Number))
        assertEquals((BigInteger.TWO as Number) - (2L as Number), (BigInteger.TWO as Number) - t["Val2"][1])
        assertEquals((BigInteger.TWO as Number) - (3F as Number), t["Val2"][4] - (3F as Number))
        assertEquals((BigInteger.TWO as Number) - (3F as Number), (BigInteger.TWO as Number) - t["Val2"][2])
        assertEquals((BigInteger.TWO as Number) - (4.0 as Number), t["Val2"][4] - (4.0 as Number))
        assertEquals((BigInteger.TWO as Number) - (4.0 as Number), (BigInteger.TWO as Number) - t["Val2"][3])
        assertEquals((BigInteger.TWO as Number) - (BigInteger.TWO as Number), t["Val2"][4] - (BigInteger.TWO as Number))
        assertEquals((BigInteger.TWO as Number) - (BigInteger.TWO as Number), (BigInteger.TWO as Number) - t["Val2"][4])
        assertEquals((BigInteger.TWO as Number) - (BigDecimal.TEN as Number), t["Val2"][4] - (BigDecimal.TEN as Number))
        assertEquals((BigInteger.TWO as Number) - (BigDecimal.TEN as Number), (BigInteger.TWO as Number) - t["Val2"][5])

        assertEquals((BigDecimal.TEN as Number) - (1 as Number), t["Val2"][5] - (1 as Number))
        assertEquals((BigDecimal.TEN as Number) - (1 as Number), (BigDecimal.TEN as Number) - t["Val2"][0])
        assertEquals((BigDecimal.TEN as Number) - (2L as Number), t["Val2"][5] - (2L as Number))
        assertEquals((BigDecimal.TEN as Number) - (2L as Number), (BigDecimal.TEN as Number) - t["Val2"][1])
        assertEquals((BigDecimal.TEN as Number) - (3F as Number), t["Val2"][5] - (3F as Number))
        assertEquals((BigDecimal.TEN as Number) - (3F as Number), (BigDecimal.TEN as Number) - t["Val2"][2])
        assertEquals((BigDecimal.TEN as Number) - (4.0 as Number), t["Val2"][5] - (4.0 as Number))
        assertEquals((BigDecimal.TEN as Number) - (4.0 as Number), (BigDecimal.TEN as Number) - t["Val2"][3])
        assertEquals((BigDecimal.TEN as Number) - (BigInteger.TWO as Number), t["Val2"][5] - (BigInteger.TWO as Number))
        assertEquals((BigDecimal.TEN as Number) - (BigInteger.TWO as Number), (BigDecimal.TEN as Number) - t["Val2"][4])
        assertEquals((BigDecimal.TEN as Number) - (BigDecimal.TEN as Number), t["Val2"][5] - (BigDecimal.TEN as Number))
        assertEquals((BigDecimal.TEN as Number) - (BigDecimal.TEN as Number), (BigDecimal.TEN as Number) - t["Val2"][5])

        assertEquals((1L as Number) * (1 as Number), t["Val2"][0] * (1 as Number))
        assertEquals((1 as Number) * (1L as Number), (1 as Number) * t["Val2"][0])
        assertEquals((1 as Number) * (2L as Number), t["Val2"][0] * (2L as Number))
        assertEquals((1 as Number) * (2L as Number), (1 as Number) * t["Val2"][1])
        assertEquals((1 as Number) * (3F as Number), t["Val2"][0] * (3F as Number))
        assertEquals((1 as Number) * (3.0 as Number), (1 as Number) * t["Val2"][2])
        assertEquals((1 as Number) * (4.0 as Number), t["Val2"][0] * (4.0 as Number))
        assertEquals((1 as Number) * (4.0 as Number), (1 as Number) * t["Val2"][3])
        assertEquals((1 as Number) * (BigInteger.TWO as Number), t["Val2"][0] * (BigInteger.TWO as Number))
        assertEquals((1 as Number) * (BigInteger.TWO as Number), (1 as Number) * t["Val2"][4])
        assertEquals((1 as Number) * (BigDecimal.TEN as Number), t["Val2"][0] * (BigDecimal.TEN as Number))
        assertEquals((1 as Number) * (BigDecimal.TEN as Number), (1 as Number) * t["Val2"][5])

        assertEquals((2L as Number) * (1 as Number), t["Val2"][1] * (1 as Number))
        assertEquals((2L as Number) * (1 as Number), (2L as Number) * t["Val2"][0])
        assertEquals((2L as Number) * (2L as Number), t["Val2"][1] * (2L as Number))
        assertEquals((2L as Number) * (2L as Number), (2L as Number) * t["Val2"][1])
        assertEquals((2L as Number) * (3F as Number), t["Val2"][1] * (3F as Number))
        assertEquals((2L as Number) * (3.0 as Number), (2L as Number) * t["Val2"][2])
        assertEquals((2L as Number) * (4.0 as Number), t["Val2"][1] * (4.0 as Number))
        assertEquals((2L as Number) * (4.0 as Number), (2L as Number) * t["Val2"][3])
        assertEquals((2L as Number) * (BigInteger.TWO as Number), t["Val2"][1] * (BigInteger.TWO as Number))
        assertEquals((2L as Number) * (BigInteger.TWO as Number), (2L as Number) * t["Val2"][4])
        assertEquals((2L as Number) * (BigDecimal.TEN as Number), t["Val2"][1] * (BigDecimal.TEN as Number))
        assertEquals((2L as Number) * (BigDecimal.TEN as Number), (2L as Number) * t["Val2"][5])

        assertEquals((3.0 as Number) * (1 as Number), t["Val2"][2] * (1 as Number))
        assertEquals((3F as Number) * (1 as Number), (3F as Number) * t["Val2"][0])
        assertEquals((3.0 as Number) * (2L as Number), t["Val2"][2] * (2L as Number))
        assertEquals((3F as Number) * (2L as Number), (3F as Number) * t["Val2"][1])
        assertEquals((3.0 as Number) * (3F as Number), t["Val2"][2] * (3F as Number))
        assertEquals((3F as Number) * (3.0 as Number), (3F as Number) * t["Val2"][2])
        assertEquals((3F as Number) * (4.0 as Number), t["Val2"][2] * (4.0 as Number))
        assertEquals((3F as Number) * (4.0 as Number), (3F as Number) * t["Val2"][3])
        assertEquals((3F as Number) * (BigInteger.TWO as Number), t["Val2"][2] * (BigInteger.TWO as Number))
        assertEquals((3F as Number) * (BigInteger.TWO as Number), (3F as Number) * t["Val2"][4])
        assertEquals((3F as Number) * (BigDecimal.TEN as Number), t["Val2"][2] * (BigDecimal.TEN as Number))
        assertEquals((3F as Number) * (BigDecimal.TEN as Number), (3F as Number) * t["Val2"][5])

        assertEquals((4.0 as Number) * (1 as Number), t["Val2"][3] * (1 as Number))
        assertEquals((4.0 as Number) * (1 as Number), (4.0 as Number) * t["Val2"][0])
        assertEquals((4.0 as Number) * (2L as Number), t["Val2"][3] * (2L as Number))
        assertEquals((4.0 as Number) * (2L as Number), (4.0 as Number) * t["Val2"][1])
        assertEquals((4.0 as Number) * (3F as Number), t["Val2"][3] * (3F as Number))
        assertEquals((4.0 as Number) * (3F as Number), (4.0 as Number) * t["Val2"][2])
        assertEquals((4.0 as Number) * (4.0 as Number), t["Val2"][3] * (4.0 as Number))
        assertEquals((4.0 as Number) * (4.0 as Number), (4.0 as Number) * t["Val2"][3])
        assertEquals((4.0 as Number) * (BigInteger.TWO as Number), t["Val2"][3] * (BigInteger.TWO as Number))
        assertEquals((4.0 as Number) * (BigInteger.TWO as Number), (4.0 as Number) * t["Val2"][4])
        assertEquals((4.0 as Number) * (BigDecimal.TEN as Number), t["Val2"][3] * (BigDecimal.TEN as Number))
        assertEquals((4.0 as Number) * (BigDecimal.TEN as Number), (4.0 as Number) * t["Val2"][5])

        assertEquals((BigInteger.TWO as Number) * (1 as Number), t["Val2"][4] * (1 as Number))
        assertEquals((BigInteger.TWO as Number) * (1 as Number), (BigInteger.TWO as Number) * t["Val2"][0])
        assertEquals((BigInteger.TWO as Number) * (2L as Number), t["Val2"][4] * (2L as Number))
        assertEquals((BigInteger.TWO as Number) * (2L as Number), (BigInteger.TWO as Number) * t["Val2"][1])
        assertEquals((BigInteger.TWO as Number) * (3F as Number), t["Val2"][4] * (3F as Number))
        assertEquals((BigInteger.TWO as Number) * (3F as Number), (BigInteger.TWO as Number) * t["Val2"][2])
        assertEquals((BigInteger.TWO as Number) * (4.0 as Number), t["Val2"][4] * (4.0 as Number))
        assertEquals((BigInteger.TWO as Number) * (4.0 as Number), (BigInteger.TWO as Number) * t["Val2"][3])
        assertEquals((BigInteger.TWO as Number) * (BigInteger.TWO as Number), t["Val2"][4] * (BigInteger.TWO as Number))
        assertEquals((BigInteger.TWO as Number) * (BigInteger.TWO as Number), (BigInteger.TWO as Number) * t["Val2"][4])
        assertEquals((BigInteger.TWO as Number) * (BigDecimal.TEN as Number), t["Val2"][4] * (BigDecimal.TEN as Number))
        assertEquals((BigInteger.TWO as Number) * (BigDecimal.TEN as Number), (BigInteger.TWO as Number) * t["Val2"][5])

        assertEquals((BigDecimal.TEN as Number) * (1 as Number), t["Val2"][5] * (1 as Number))
        assertEquals((BigDecimal.TEN as Number) * (1 as Number), (BigDecimal.TEN as Number) * t["Val2"][0])
        assertEquals((BigDecimal.TEN as Number) * (2L as Number), t["Val2"][5] * (2L as Number))
        assertEquals((BigDecimal.TEN as Number) * (2L as Number), (BigDecimal.TEN as Number) * t["Val2"][1])
        assertEquals((BigDecimal.TEN as Number) * (3F as Number), t["Val2"][5] * (3F as Number))
        assertEquals((BigDecimal.TEN as Number) * (3F as Number), (BigDecimal.TEN as Number) * t["Val2"][2])
        assertEquals((BigDecimal.TEN as Number) * (4.0 as Number), t["Val2"][5] * (4.0 as Number))
        assertEquals((BigDecimal.TEN as Number) * (4.0 as Number), (BigDecimal.TEN as Number) * t["Val2"][3])
        assertEquals((BigDecimal.TEN as Number) * (BigInteger.TWO as Number), t["Val2"][5] * (BigInteger.TWO as Number))
        assertEquals((BigDecimal.TEN as Number) * (BigInteger.TWO as Number), (BigDecimal.TEN as Number) * t["Val2"][4])
        assertEquals((BigDecimal.TEN as Number) * (BigDecimal.TEN as Number), t["Val2"][5] * (BigDecimal.TEN as Number))
        assertEquals((BigDecimal.TEN as Number) * (BigDecimal.TEN as Number), (BigDecimal.TEN as Number) * t["Val2"][5])

        assertEquals((1L as Number) / (1 as Number), t["Val2"][0] / (1 as Number))
        assertEquals((1 as Number) / (1L as Number), (1 as Number) / t["Val2"][0])
        assertEquals((1 as Number) / (2L as Number), t["Val2"][0] / (2L as Number))
        assertEquals((1 as Number) / (2L as Number), (1 as Number) / t["Val2"][1])
        assertEquals((1 as Number) / (3F as Number), t["Val2"][0] / (3F as Number))
        assertEquals((1 as Number) / (3.0 as Number), (1 as Number) / t["Val2"][2])
        assertEquals((1 as Number) / (4.0 as Number), t["Val2"][0] / (4.0 as Number))
        assertEquals((1 as Number) / (4.0 as Number), (1 as Number) / t["Val2"][3])
        assertEquals((1 as Number) / (BigInteger.TWO as Number), t["Val2"][0] / (BigInteger.TWO as Number))
        assertEquals((1 as Number) / (BigInteger.TWO as Number), (1 as Number) / t["Val2"][4])
        assertEquals((1 as Number) / (BigDecimal.TEN as Number), t["Val2"][0] / (BigDecimal.TEN as Number))
        assertEquals((1 as Number) / (BigDecimal.TEN as Number), (1 as Number) / t["Val2"][5])

        assertEquals((2L as Number) / (1 as Number), t["Val2"][1] / (1 as Number))
        assertEquals((2L as Number) / (1 as Number), (2L as Number) / t["Val2"][0])
        assertEquals((2L as Number) / (2L as Number), t["Val2"][1] / (2L as Number))
        assertEquals((2L as Number) / (2L as Number), (2L as Number) / t["Val2"][1])
        assertEquals((2L as Number) / (3F as Number), t["Val2"][1] / (3F as Number))
        assertEquals((2L as Number) / (3.0 as Number), (2L as Number) / t["Val2"][2])
        assertEquals((2L as Number) / (4.0 as Number), t["Val2"][1] / (4.0 as Number))
        assertEquals((2L as Number) / (4.0 as Number), (2L as Number) / t["Val2"][3])
        assertEquals((2L as Number) / (BigInteger.TWO as Number), t["Val2"][1] / (BigInteger.TWO as Number))
        assertEquals((2L as Number) / (BigInteger.TWO as Number), (2L as Number) / t["Val2"][4])
        assertEquals((2L as Number) / (BigDecimal.TEN as Number), t["Val2"][1] / (BigDecimal.TEN as Number))
        assertEquals((2L as Number) / (BigDecimal.TEN as Number), (2L as Number) / t["Val2"][5])

        assertEquals((3.0 as Number) / (1 as Number), t["Val2"][2] / (1 as Number))
        assertEquals((3F as Number) / (1L as Number), (3F as Number) / t["Val2"][0])
        assertEquals((3.0 as Number) / (2L as Number), t["Val2"][2] / (2L as Number))
        assertEquals((3F as Number) / (2L as Number), (3F as Number) / t["Val2"][1])
        assertEquals((3.0 as Number) / (3F as Number), t["Val2"][2] / (3F as Number))
        assertEquals((3F as Number) / (3.0 as Number), (3F as Number) / t["Val2"][2])
        assertEquals((3F as Number) / (4.0 as Number), t["Val2"][2] / (4.0 as Number))
        assertEquals((3F as Number) / (4.0 as Number), (3F as Number) / t["Val2"][3])
        assertEquals((3F as Number) / (BigInteger.TWO as Number), t["Val2"][2] / (BigInteger.TWO as Number))
        assertEquals((3F as Number) / (BigInteger.TWO as Number), (3F as Number) / t["Val2"][4])
        assertEquals((3F as Number) / (BigDecimal.TEN as Number), t["Val2"][2] / (BigDecimal.TEN as Number))
        assertEquals((3F as Number) / (BigDecimal.TEN as Number), (3F as Number) / t["Val2"][5])

        assertEquals((4.0 as Number) / (1 as Number), t["Val2"][3] / (1 as Number))
        assertEquals((4.0 as Number) / (1 as Number), (4.0 as Number) / t["Val2"][0])
        assertEquals((4.0 as Number) / (2L as Number), t["Val2"][3] / (2L as Number))
        assertEquals((4.0 as Number) / (2L as Number), (4.0 as Number) / t["Val2"][1])
        assertEquals((4.0 as Number) / (3F as Number), t["Val2"][3] / (3F as Number))
        assertEquals((4.0 as Number) / (3F as Number), (4.0 as Number) / t["Val2"][2])
        assertEquals((4.0 as Number) / (4.0 as Number), t["Val2"][3] / (4.0 as Number))
        assertEquals((4.0 as Number) / (4.0 as Number), (4.0 as Number) / t["Val2"][3])
        assertEquals((4.0 as Number) / (BigInteger.TWO as Number), t["Val2"][3] / (BigInteger.TWO as Number))
        assertEquals((4.0 as Number) / (BigInteger.TWO as Number), (4.0 as Number) / t["Val2"][4])
        assertEquals((4.0 as Number) / (BigDecimal.TEN as Number), t["Val2"][3] / (BigDecimal.TEN as Number))
        assertEquals((4.0 as Number) / (BigDecimal.TEN as Number), (4.0 as Number) / t["Val2"][5])

        assertEquals((BigInteger.TWO as Number) / (1 as Number), t["Val2"][4] / (1 as Number))
        assertEquals((BigInteger.TWO as Number) / (1 as Number), (BigInteger.TWO as Number) / t["Val2"][0])
        assertEquals((BigInteger.TWO as Number) / (2L as Number), t["Val2"][4] / (2L as Number))
        assertEquals((BigInteger.TWO as Number) / (2L as Number), (BigInteger.TWO as Number) / t["Val2"][1])
        assertEquals((BigInteger.TWO as Number) / (3F as Number), t["Val2"][4] / (3F as Number))
        assertEquals((BigInteger.TWO as Number) / (3F as Number), (BigInteger.TWO as Number) / t["Val2"][2])
        assertEquals((BigInteger.TWO as Number) / (4.0 as Number), t["Val2"][4] / (4.0 as Number))
        assertEquals((BigInteger.TWO as Number) / (4.0 as Number), (BigInteger.TWO as Number) / t["Val2"][3])
        assertEquals((BigInteger.TWO as Number) / (BigInteger.TWO as Number), t["Val2"][4] / (BigInteger.TWO as Number))
        assertEquals((BigInteger.TWO as Number) / (BigInteger.TWO as Number), (BigInteger.TWO as Number) / t["Val2"][4])
        assertEquals((BigInteger.TWO as Number) / (BigDecimal.TEN as Number), t["Val2"][4] / (BigDecimal.TEN as Number))
        assertEquals((BigInteger.TWO as Number) / (BigDecimal.TEN as Number), (BigInteger.TWO as Number) / t["Val2"][5])

        assertEquals((BigDecimal.TEN as Number) / (1 as Number), t["Val2"][5] / (1 as Number))
        assertEquals((BigDecimal.TEN as Number) / (1 as Number), (BigDecimal.TEN as Number) / t["Val2"][0])
        assertEquals((BigDecimal.TEN as Number) / (2L as Number), t["Val2"][5] / (2L as Number))
        assertEquals((BigDecimal.TEN as Number) / (2L as Number), (BigDecimal.TEN as Number) / t["Val2"][1])
        assertEquals((BigDecimal.TEN as Number) / (3F as Number), t["Val2"][5] / (3F as Number))
        assertEquals((BigDecimal.TEN as Number) / (3F as Number), (BigDecimal.TEN as Number) / t["Val2"][2])
        assertEquals((BigDecimal.TEN as Number) / (4.0 as Number), t["Val2"][5] / (4.0 as Number))
        assertEquals((BigDecimal.TEN as Number) / (4.0 as Number), (BigDecimal.TEN as Number) / t["Val2"][3])
        assertEquals((BigDecimal.TEN as Number) / (BigInteger.TWO as Number), t["Val2"][5] / (BigInteger.TWO as Number))
        assertEquals((BigDecimal.TEN as Number) / (BigInteger.TWO as Number), (BigDecimal.TEN as Number) / t["Val2"][4])
        assertEquals((BigDecimal.TEN as Number) / (BigDecimal.TEN as Number), t["Val2"][5] / (BigDecimal.TEN as Number))
        assertEquals((BigDecimal.TEN as Number) / (BigDecimal.TEN as Number), (BigDecimal.TEN as Number) / t["Val2"][5])

        assertEquals((1L as Number) % (1 as Number), t["Val2"][0] % (1 as Number))
        assertEquals((1 as Number) % (1L as Number), (1 as Number) % t["Val2"][0])
        assertEquals((1L as Number) % (2L as Number), t["Val2"][0] % (2L as Number))
        assertEquals((1 as Number) % (2L as Number), (1 as Number) % t["Val2"][1])
        assertEquals((1 as Number) % (3F as Number), t["Val2"][0] % (3F as Number))
        assertEquals((1 as Number) % (3.0 as Number), (1 as Number) % t["Val2"][2])
        assertEquals((1 as Number) % (4.0 as Number), t["Val2"][0] % (4.0 as Number))
        assertEquals((1 as Number) % (4.0 as Number), (1 as Number) % t["Val2"][3])
        assertEquals((1 as Number) % (BigInteger.TWO as Number), t["Val2"][0] % (BigInteger.TWO as Number))
        assertEquals((1 as Number) % (BigInteger.TWO as Number), (1 as Number) % t["Val2"][4])
        assertEquals((1 as Number) % (BigDecimal.TEN as Number), t["Val2"][0] % (BigDecimal.TEN as Number))
        assertEquals((1 as Number) % (BigDecimal.TEN as Number), (1 as Number) % t["Val2"][5])

        assertEquals((2L as Number) % (1 as Number), t["Val2"][1] % (1 as Number))
        assertEquals((2L as Number) % (1 as Number), (2L as Number) % t["Val2"][0])
        assertEquals((2L as Number) % (2L as Number), t["Val2"][1] % (2L as Number))
        assertEquals((2L as Number) % (2L as Number), (2L as Number) % t["Val2"][1])
        assertEquals((2L as Number) % (3F as Number), t["Val2"][1] % (3F as Number))
        assertEquals((2L as Number) % (3.0 as Number), (2L as Number) % t["Val2"][2])
        assertEquals((2L as Number) % (4.0 as Number), t["Val2"][1] % (4.0 as Number))
        assertEquals((2L as Number) % (4.0 as Number), (2L as Number) % t["Val2"][3])
        assertEquals((2L as Number) % (BigInteger.TWO as Number), t["Val2"][1] % (BigInteger.TWO as Number))
        assertEquals((2L as Number) % (BigInteger.TWO as Number), (2L as Number) % t["Val2"][4])
        assertEquals((2L as Number) % (BigDecimal.TEN as Number), t["Val2"][1] % (BigDecimal.TEN as Number))
        assertEquals((2L as Number) % (BigDecimal.TEN as Number), (2L as Number) % t["Val2"][5])

        assertEquals((3.0 as Number) % (1 as Number), t["Val2"][2] % (1 as Number))
        assertEquals((3F as Number) % (1 as Number), (3F as Number) % t["Val2"][0])
        assertEquals((3.0 as Number) % (2L as Number), t["Val2"][2] % (2L as Number))
        assertEquals((3F as Number) % (2L as Number), (3F as Number) % t["Val2"][1])
        assertEquals((3.0 as Number) % (3F as Number), t["Val2"][2] % (3F as Number))
        assertEquals((3F as Number) % (3.0 as Number), (3F as Number) % t["Val2"][2])
        assertEquals((3F as Number) % (4.0 as Number), t["Val2"][2] % (4.0 as Number))
        assertEquals((3F as Number) % (4.0 as Number), (3F as Number) % t["Val2"][3])
        assertEquals((3F as Number) % (BigInteger.TWO as Number), t["Val2"][2] % (BigInteger.TWO as Number))
        assertEquals((3F as Number) % (BigInteger.TWO as Number), (3F as Number) % t["Val2"][4])
        assertEquals((3F as Number) % (BigDecimal.TEN as Number), t["Val2"][2] % (BigDecimal.TEN as Number))
        assertEquals((3F as Number) % (BigDecimal.TEN as Number), (3F as Number) % t["Val2"][5])

        assertEquals((4.0 as Number) % (1 as Number), t["Val2"][3] % (1 as Number))
        assertEquals((4.0 as Number) % (1 as Number), (4.0 as Number) % t["Val2"][0])
        assertEquals((4.0 as Number) % (2L as Number), t["Val2"][3] % (2L as Number))
        assertEquals((4.0 as Number) % (2L as Number), (4.0 as Number) % t["Val2"][1])
        assertEquals((4.0 as Number) % (3F as Number), t["Val2"][3] % (3F as Number))
        assertEquals((4.0 as Number) % (3F as Number), (4.0 as Number) % t["Val2"][2])
        assertEquals((4.0 as Number) % (4.0 as Number), t["Val2"][3] % (4.0 as Number))
        assertEquals((4.0 as Number) % (4.0 as Number), (4.0 as Number) % t["Val2"][3])
        assertEquals((4.0 as Number) % (BigInteger.TWO as Number), t["Val2"][3] % (BigInteger.TWO as Number))
        assertEquals((4.0 as Number) % (BigInteger.TWO as Number), (4.0 as Number) % t["Val2"][4])
        assertEquals((4.0 as Number) % (BigDecimal.TEN as Number), t["Val2"][3] % (BigDecimal.TEN as Number))
        assertEquals((4.0 as Number) % (BigDecimal.TEN as Number), (4.0 as Number) % t["Val2"][5])

        assertEquals((BigInteger.TWO as Number) % (1 as Number), t["Val2"][4] % (1 as Number))
        assertEquals((BigInteger.TWO as Number) % (1 as Number), (BigInteger.TWO as Number) % t["Val2"][0])
        assertEquals((BigInteger.TWO as Number) % (2L as Number), t["Val2"][4] % (2L as Number))
        assertEquals((BigInteger.TWO as Number) % (2L as Number), (BigInteger.TWO as Number) % t["Val2"][1])
        assertEquals((BigInteger.TWO as Number) % (3F as Number), t["Val2"][4] % (3F as Number))
        assertEquals((BigInteger.TWO as Number) % (3F as Number), (BigInteger.TWO as Number) % t["Val2"][2])
        assertEquals((BigInteger.TWO as Number) % (4.0 as Number), t["Val2"][4] % (4.0 as Number))
        assertEquals((BigInteger.TWO as Number) % (4.0 as Number), (BigInteger.TWO as Number) % t["Val2"][3])
        assertEquals((BigInteger.TWO as Number) % (BigInteger.TWO as Number), t["Val2"][4] % (BigInteger.TWO as Number))
        assertEquals((BigInteger.TWO as Number) % (BigInteger.TWO as Number), (BigInteger.TWO as Number) % t["Val2"][4])
        assertEquals((BigInteger.TWO as Number) % (BigDecimal.TEN as Number), t["Val2"][4] % (BigDecimal.TEN as Number))
        assertEquals((BigInteger.TWO as Number) % (BigDecimal.TEN as Number), (BigInteger.TWO as Number) % t["Val2"][5])

        assertEquals((BigDecimal.TEN as Number) % (1 as Number), t["Val2"][5] % (1 as Number))
        assertEquals((BigDecimal.TEN as Number) % (1 as Number), (BigDecimal.TEN as Number) % t["Val2"][0])
        assertEquals((BigDecimal.TEN as Number) % (2L as Number), t["Val2"][5] % (2L as Number))
        assertEquals((BigDecimal.TEN as Number) % (2L as Number), (BigDecimal.TEN as Number) % t["Val2"][1])
        assertEquals((BigDecimal.TEN as Number) % (3F as Number), t["Val2"][5] % (3F as Number))
        assertEquals((BigDecimal.TEN as Number) % (3F as Number), (BigDecimal.TEN as Number) % t["Val2"][2])
        assertEquals((BigDecimal.TEN as Number) % (4.0 as Number), t["Val2"][5] % (4.0 as Number))
        assertEquals((BigDecimal.TEN as Number) % (4.0 as Number), (BigDecimal.TEN as Number) % t["Val2"][3])
        assertEquals((BigDecimal.TEN as Number) % (BigInteger.TWO as Number), t["Val2"][5] % (BigInteger.TWO as Number))
        assertEquals((BigDecimal.TEN as Number) % (BigInteger.TWO as Number), (BigDecimal.TEN as Number) % t["Val2"][4])
        assertEquals((BigDecimal.TEN as Number) % (BigDecimal.TEN as Number), t["Val2"][5] % (BigDecimal.TEN as Number))
        assertEquals((BigDecimal.TEN as Number) % (BigDecimal.TEN as Number), (BigDecimal.TEN as Number) % t["Val2"][5])
    }

    @Test
    fun `basic number math`() {
        // Testing math between number and number
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
            val (class3, class4) = listOf(class1, class2).sortedWith { o1, o2 -> typeValue(o1).compareTo(typeValue(o2)) }

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

        val values = listOf(1, 2L, 3F, 4.0, BigInteger.TWO, BigDecimal.TEN)

        for (value1 in values) {
            for (value2 in values) {
                assertEquals(typePref(value1::class, value2::class), (value1 + value2)::class)
                assertEquals(typePref(value1::class, value2::class), (value1 - value2)::class)
                assertEquals(typePref(value1::class, value2::class), (value1 * value2)::class)
                assertEquals(typePref(value1::class, value2::class), (value1 / value2)::class)
                assertEquals(typePref(value1::class, value2::class), (value1 % value2)::class)
            }
        }

        assertEquals(2, (1 as Number) + (1 as Number))
        assertEquals(3L, (1 as Number) + (2L as Number))
        assertEquals(4F, (1 as Number) + (3F as Number))
        assertEquals(5.0, (1 as Number) + (4.0 as Number))
        assertEquals(BigInteger.valueOf(3), (1 as Number) + (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(11), (1 as Number) + (BigDecimal.TEN as Number))

        assertEquals(3L, (2L as Number) + (1 as Number))
        assertEquals(4L, (2L as Number) + (2L as Number))
        assertEquals(5F, (2L as Number) + (3F as Number))
        assertEquals(6.0, (2L as Number) + (4.0 as Number))
        assertEquals(BigInteger.valueOf(4), (2L as Number) + (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(12), (2L as Number) + (BigDecimal.TEN as Number))

        assertEquals(4F, (3F as Number) + (1 as Number))
        assertEquals(5F, (3F as Number) + (2L as Number))
        assertEquals(6F, (3F as Number) + (3F as Number))
        assertEquals(7.0, (3F as Number) + (4.0 as Number))
        assertEquals(BigDecimal("5.0"), (3F as Number) + (BigInteger.TWO as Number))
        assertEquals(BigDecimal("13.0"), (3F as Number) + (BigDecimal.TEN as Number))

        assertEquals(5.0, (4.0 as Number) + (1 as Number))
        assertEquals(6.0, (4.0 as Number) + (2L as Number))
        assertEquals(7.0, (4.0 as Number) + (3F as Number))
        assertEquals(8.0, (4.0 as Number) + (4.0 as Number))
        assertEquals(BigDecimal("6.0"), (4.0 as Number) + (BigInteger.TWO as Number))
        assertEquals(BigDecimal("14.0"), (4.0 as Number) + (BigDecimal.TEN as Number))

        assertEquals(BigInteger.valueOf(3), (BigInteger.TWO as Number) + (1 as Number))
        assertEquals(BigInteger.valueOf(4), (BigInteger.TWO as Number) + (2L as Number))
        assertEquals(BigDecimal("5.0"), (BigInteger.TWO as Number) + (3F as Number))
        assertEquals(BigDecimal("6.0"), (BigInteger.TWO as Number) + (4.0 as Number))
        assertEquals(BigInteger.valueOf(4), (BigInteger.TWO as Number) + (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(12), (BigInteger.TWO as Number) + (BigDecimal.TEN as Number))

        assertEquals(BigDecimal.valueOf(11), (BigDecimal.TEN as Number) + (1 as Number))
        assertEquals(BigDecimal.valueOf(12), (BigDecimal.TEN as Number) + (2L as Number))
        assertEquals(BigDecimal("13.0"), (BigDecimal.TEN as Number) + (3F as Number))
        assertEquals(BigDecimal("14.0"), (BigDecimal.TEN as Number) + (4.0 as Number))
        assertEquals(BigDecimal.valueOf(12), (BigDecimal.TEN as Number) + (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(20), (BigDecimal.TEN as Number) + (BigDecimal.TEN as Number))

        assertEquals(0, (1 as Number) - (1 as Number))
        assertEquals(-1L, (1 as Number) - (2L as Number))
        assertEquals(-2F, (1 as Number) - (3F as Number))
        assertEquals(-3.0, (1 as Number) - (4.0 as Number))
        assertEquals(BigInteger.valueOf(-1), (1 as Number) - (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(-9), (1 as Number) - (BigDecimal.TEN as Number))

        assertEquals(1L, (2L as Number) - (1 as Number))
        assertEquals(0L, (2L as Number) - (2L as Number))
        assertEquals(-1F, (2L as Number) - (3F as Number))
        assertEquals(-2.0, (2L as Number) - (4.0 as Number))
        assertEquals(BigInteger.ZERO, (2L as Number) - (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(-8), (2L as Number) - (BigDecimal.TEN as Number))

        assertEquals(2F, (3F as Number) - (1 as Number))
        assertEquals(1F, (3F as Number) - (2L as Number))
        assertEquals(0F, (3F as Number) - (3F as Number))
        assertEquals(-1.0, (3F as Number) - (4.0 as Number))
        assertEquals(BigDecimal("1.0"), (3F as Number) - (BigInteger.TWO as Number))
        assertEquals(BigDecimal("-7.0"), (3F as Number) - (BigDecimal.TEN as Number))

        assertEquals(3.0, (4.0 as Number) - (1 as Number))
        assertEquals(2.0, (4.0 as Number) - (2L as Number))
        assertEquals(1.0, (4.0 as Number) - (3F as Number))
        assertEquals(0.0, (4.0 as Number) - (4.0 as Number))
        assertEquals(BigDecimal("2.0"), (4.0 as Number) - (BigInteger.TWO as Number))
        assertEquals(BigDecimal("-6.0"), (4.0 as Number) - (BigDecimal.TEN as Number))

        assertEquals(BigInteger.ONE, (BigInteger.TWO as Number) - (1 as Number))
        assertEquals(BigInteger.ZERO, (BigInteger.TWO as Number) - (2L as Number))
        assertEquals(BigDecimal("-1.0"), (BigInteger.TWO as Number) - (3F as Number))
        assertEquals(BigDecimal("-2.0"), (BigInteger.TWO as Number) - (4.0 as Number))
        assertEquals(BigInteger.ZERO, (BigInteger.TWO as Number) - (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(-8), (BigInteger.TWO as Number) - (BigDecimal.TEN as Number))

        assertEquals(BigDecimal.valueOf(9), (BigDecimal.TEN as Number) - (1 as Number))
        assertEquals(BigDecimal.valueOf(8), (BigDecimal.TEN as Number) - (2L as Number))
        assertEquals(BigDecimal("7.0"), (BigDecimal.TEN as Number) - (3F as Number))
        assertEquals(BigDecimal("6.0"), (BigDecimal.TEN as Number) - (4.0 as Number))
        assertEquals(BigDecimal.valueOf(8), (BigDecimal.TEN as Number) - (BigInteger.TWO as Number))
        assertEquals(BigDecimal.ZERO, (BigDecimal.TEN as Number) - (BigDecimal.TEN as Number))

        assertEquals(1, (1 as Number) * (1 as Number))
        assertEquals(2L, (1 as Number) * (2L as Number))
        assertEquals(3F, (1 as Number) * (3F as Number))
        assertEquals(4.0, (1 as Number) * (4.0 as Number))
        assertEquals(BigInteger.TWO, (1 as Number) * (BigInteger.TWO as Number))
        assertEquals(BigDecimal.TEN, (1 as Number) * (BigDecimal.TEN as Number))

        assertEquals(2L, (2L as Number) * (1 as Number))
        assertEquals(4L, (2L as Number) * (2L as Number))
        assertEquals(6F, (2L as Number) * (3F as Number))
        assertEquals(8.0, (2L as Number) * (4.0 as Number))
        assertEquals(BigInteger.valueOf(4), (2L as Number) * (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(20), (2L as Number) * (BigDecimal.TEN as Number))

        assertEquals(3F, (3F as Number) * (1 as Number))
        assertEquals(6F, (3F as Number) * (2L as Number))
        assertEquals(9F, (3F as Number) * (3F as Number))
        assertEquals(12.0, (3F as Number) * (4.0 as Number))
        assertEquals(BigDecimal("6.0"), (3F as Number) * (BigInteger.TWO as Number))
        assertEquals(BigDecimal("30.0"), (3F as Number) * (BigDecimal.TEN as Number))

        assertEquals(4.0, (4.0 as Number) * (1 as Number))
        assertEquals(8.0, (4.0 as Number) * (2L as Number))
        assertEquals(12.0, (4.0 as Number) * (3F as Number))
        assertEquals(16.0, (4.0 as Number) * (4.0 as Number))
        assertEquals(BigDecimal("8.0"), (4.0 as Number) * (BigInteger.TWO as Number))
        assertEquals(BigDecimal("40.0"), (4.0 as Number) * (BigDecimal.TEN as Number))

        assertEquals(BigInteger.TWO, (BigInteger.TWO as Number) * (1 as Number))
        assertEquals(BigInteger.valueOf(4), (BigInteger.TWO as Number) * (2L as Number))
        assertEquals(BigDecimal("6.0"), (BigInteger.TWO as Number) * (3F as Number))
        assertEquals(BigDecimal("8.0"), (BigInteger.TWO as Number) * (4.0 as Number))
        assertEquals(BigInteger.valueOf(4), (BigInteger.TWO as Number) * (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(20), (BigInteger.TWO as Number) * (BigDecimal.TEN as Number))

        assertEquals(BigDecimal.TEN, (BigDecimal.TEN as Number) * (1 as Number))
        assertEquals(BigDecimal.valueOf(20), (BigDecimal.TEN as Number) * (2L as Number))
        assertEquals(BigDecimal("30.0"), (BigDecimal.TEN as Number) * (3F as Number))
        assertEquals(BigDecimal("40.0"), (BigDecimal.TEN as Number) * (4.0 as Number))
        assertEquals(BigDecimal.valueOf(20), (BigDecimal.TEN as Number) * (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(100), (BigDecimal.TEN as Number) * (BigDecimal.TEN as Number))

        assertEquals(1, (1 as Number) / (1 as Number))
        assertEquals(0L, (1 as Number) / (2L as Number))
        assertEquals(0.33333334F, (1 as Number) / (3F as Number))
        assertEquals(0.25, (1 as Number) / (4.0 as Number))
        assertEquals(BigInteger.ZERO, (1 as Number) / (BigInteger.TWO as Number))
        assertEquals(BigDecimal.ZERO, (1 as Number) / (BigDecimal.TEN as Number))

        assertEquals(2L, (2L as Number) / (1 as Number))
        assertEquals(1L, (2L as Number) / (2L as Number))
        assertEquals(0.6666667F, (2L as Number) / (3F as Number))
        assertEquals(0.5, (2L as Number) / (4.0 as Number))
        assertEquals(BigInteger.ONE, (2L as Number) / (BigInteger.TWO as Number))
        assertEquals(BigDecimal.ZERO, (2L as Number) / (BigDecimal.TEN as Number))

        assertEquals(3F, (3F as Number) / (1 as Number))
        assertEquals(1.5F, (3F as Number) / (2L as Number))
        assertEquals(1F, (3F as Number) / (3F as Number))
        assertEquals(0.75, (3F as Number) / (4.0 as Number))
        assertEquals(BigDecimal("1.5"), (3F as Number) / (BigInteger.TWO as Number))
        assertEquals(BigDecimal("0.3"), (3F as Number) / (BigDecimal.TEN as Number))

        assertEquals(4.0, (4.0 as Number) / (1 as Number))
        assertEquals(2.0, (4.0 as Number) / (2L as Number))
        assertEquals(1.3333333333333333, (4.0 as Number) / (3F as Number))
        assertEquals(1.0, (4.0 as Number) / (4.0 as Number))
        assertEquals(BigDecimal("2.0"), (4.0 as Number) / (BigInteger.TWO as Number))
        assertEquals(BigDecimal("0.4"), (4.0 as Number) / (BigDecimal.TEN as Number))

        assertEquals(BigInteger.TWO, (BigInteger.TWO as Number) / (1 as Number))
        assertEquals(BigInteger.ONE, (BigInteger.TWO as Number) / (2L as Number))
        assertEquals(BigDecimal("1"), (BigInteger.TWO as Number) / (3F as Number))
        assertEquals(BigDecimal("0"), (BigInteger.TWO as Number) / (4.0 as Number))
        assertEquals(BigInteger.ONE, (BigInteger.TWO as Number) / (BigInteger.TWO as Number))
        assertEquals(BigDecimal("0"), (BigInteger.TWO as Number) / (BigDecimal.TEN as Number))

        assertEquals(BigDecimal.TEN, (BigDecimal.TEN as Number) / (1 as Number))
        assertEquals(BigDecimal("5"), (BigDecimal.TEN as Number) / (2L as Number))
        assertEquals(BigDecimal("3"), (BigDecimal.TEN as Number) / (3F as Number))
        assertEquals(BigDecimal("2"), (BigDecimal.TEN as Number) / (4.0 as Number))
        assertEquals(BigDecimal("5"), (BigDecimal.TEN as Number) / (BigInteger.TWO as Number))
        assertEquals(BigDecimal.ONE, (BigDecimal.TEN as Number) / (BigDecimal.TEN as Number))

        assertEquals(0, (1 as Number) % (1 as Number))
        assertEquals(1L, (1 as Number) % (2L as Number))
        assertEquals(1F, (1 as Number) % (3F as Number))
        assertEquals(1.0, (1 as Number) % (4.0 as Number))
        assertEquals(BigInteger.ONE, (1 as Number) % (BigInteger.TWO as Number))
        assertEquals(BigDecimal.ONE, (1 as Number) % (BigDecimal.TEN as Number))

        assertEquals(0L, (2L as Number) % (1 as Number))
        assertEquals(0L, (2L as Number) % (2L as Number))
        assertEquals(2F, (2L as Number) % (3F as Number))
        assertEquals(2.0, (2L as Number) % (4.0 as Number))
        assertEquals(BigInteger.ZERO, (2L as Number) % (BigInteger.TWO as Number))
        assertEquals(BigDecimal("2"), (2L as Number) % (BigDecimal.TEN as Number))

        assertEquals(0F, (3F as Number) % (1 as Number))
        assertEquals(1F, (3F as Number) % (2L as Number))
        assertEquals(0F, (3F as Number) % (3F as Number))
        assertEquals(3.0, (3F as Number) % (4.0 as Number))
        assertEquals(BigDecimal("1.0"), (3F as Number) % (BigInteger.TWO as Number))
        assertEquals(BigDecimal("3.0"), (3F as Number) % (BigDecimal.TEN as Number))

        assertEquals(0.0, (4.0 as Number) % (1 as Number))
        assertEquals(0.0, (4.0 as Number) % (2L as Number))
        assertEquals(1.0, (4.0 as Number) % (3F as Number))
        assertEquals(0.0, (4.0 as Number) % (4.0 as Number))
        assertEquals(BigDecimal("0.0"), (4.0 as Number) % (BigInteger.TWO as Number))
        assertEquals(BigDecimal("4.0"), (4.0 as Number) % (BigDecimal.TEN as Number))

        assertEquals(BigInteger.ZERO, (BigInteger.TWO as Number) % (1 as Number))
        assertEquals(BigInteger.ZERO, (BigInteger.TWO as Number) % (2L as Number))
        assertEquals(BigDecimal.valueOf(2), (BigInteger.TWO as Number) % (3F as Number))
        assertEquals(BigDecimal.valueOf(2), (BigInteger.TWO as Number) % (4.0 as Number))
        assertEquals(BigInteger.ZERO, (BigInteger.TWO as Number) % (BigInteger.TWO as Number))
        assertEquals(BigDecimal.valueOf(2), (BigInteger.TWO as Number) % (BigDecimal.TEN as Number))

        assertEquals(BigDecimal.ZERO, (BigDecimal.TEN as Number) % (1 as Number))
        assertEquals(BigDecimal.valueOf(0), (BigDecimal.TEN as Number) % (2L as Number))
        assertEquals(BigDecimal("1.0"), (BigDecimal.TEN as Number) % (3F as Number))
        assertEquals(BigDecimal("2.0"), (BigDecimal.TEN as Number) % (4.0 as Number))
        assertEquals(BigDecimal.valueOf(0), (BigDecimal.TEN as Number) % (BigInteger.TWO as Number))
        assertEquals(BigDecimal.ZERO, (BigDecimal.TEN as Number) % (BigDecimal.TEN as Number))
    }

    @Test
    fun `unit cell as`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        assertNull(t["Val1", 0].asLong)
        assertNull(t["Val1", 0].asDouble)
        assertNull(t["Val1", 0].asBigInteger)
        assertNull(t["Val1", 0].asBigDecimal)
        assertNull(t["Val1", 0].asBigDecimal(MathContext.DECIMAL32))
        assertNull(t["Val1", 0].asNumber)
    }
}
