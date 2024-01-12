/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

// NOTE: It's important that this does not import anything from BasicMath!
//       Otherwise, some of the math ops below will use what is inside
//       BasicMath rather than the native Kotlin operations.
//       Never do "import sigbla.app.*" in this class!

import org.junit.Assert.*
import org.junit.Test
import org.junit.After
import sigbla.app.Precision
import sigbla.app.IndexRelation
import sigbla.app.Table
import sigbla.app.columnOf
import sigbla.app.columnsOf
import sigbla.app.exceptions.InvalidCellException
import sigbla.app.remove
import sigbla.app.valueOf
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.test.assertFailsWith

// NOTE: It's important that this does not import anything from BasicMath!
//       Otherwise, some of the math ops below will use what is inside
//       BasicMath rather than the native Kotlin operations.
//       Never do "import sigbla.app.*" in this class!

class BasicTableTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `basic table ops 1`() {
        val name = object {}.javaClass.enclosingMethod.name
        val t = Table[name]
        assertTrue(Table.names.contains(name))

        t["A"][1] = "String"
        t["B"][2] = 123L
        t["C"][3] = 123.0
        t["D"][4] = BigInteger.ONE
        t["E"][5] = BigDecimal.TEN

        assertEquals("String", valueOf<Any>(t["A"][1]))
        assertEquals(123L, valueOf<Any>(t["B"][2]))
        assertEquals(123.0, valueOf<Any>(t["C"][3]))
        assertEquals(BigInteger.ONE, valueOf<Any>(t["D"][4]))
        assertEquals(BigDecimal.TEN, valueOf<Any>(t["E"][5]))

        assertEquals(Unit, valueOf<Any>(t["A"][2]))
        assertEquals(Unit, valueOf<Any>(t["A"][3]))
        assertEquals(Unit, valueOf<Any>(t["A"][4]))
        assertEquals(Unit, valueOf<Any>(t["A"][5]))

        assertEquals(Unit, valueOf<Any>(t["B"][1]))
        assertEquals(Unit, valueOf<Any>(t["B"][3]))
        assertEquals(Unit, valueOf<Any>(t["B"][4]))
        assertEquals(Unit, valueOf<Any>(t["B"][5]))

        assertEquals(Unit, valueOf<Any>(t["C"][1]))
        assertEquals(Unit, valueOf<Any>(t["C"][2]))
        assertEquals(Unit, valueOf<Any>(t["C"][4]))
        assertEquals(Unit, valueOf<Any>(t["C"][5]))

        assertEquals(Unit, valueOf<Any>(t["D"][1]))
        assertEquals(Unit, valueOf<Any>(t["D"][2]))
        assertEquals(Unit, valueOf<Any>(t["D"][3]))
        assertEquals(Unit, valueOf<Any>(t["D"][5]))

        assertEquals(Unit, valueOf<Any>(t["E"][1]))
        assertEquals(Unit, valueOf<Any>(t["E"][2]))
        assertEquals(Unit, valueOf<Any>(t["E"][3]))
        assertEquals(Unit, valueOf<Any>(t["E"][4]))

        columnsOf(t).forEach { remove(it) }

        assertEquals(Unit, valueOf<Any>(t["A"][1]))
        assertEquals(Unit, valueOf<Any>(t["A"][2]))
        assertEquals(Unit, valueOf<Any>(t["A"][3]))
        assertEquals(Unit, valueOf<Any>(t["A"][4]))
        assertEquals(Unit, valueOf<Any>(t["A"][5]))

        assertEquals(Unit, valueOf<Any>(t["B"][1]))
        assertEquals(Unit, valueOf<Any>(t["B"][2]))
        assertEquals(Unit, valueOf<Any>(t["B"][3]))
        assertEquals(Unit, valueOf<Any>(t["B"][4]))
        assertEquals(Unit, valueOf<Any>(t["B"][5]))

        assertEquals(Unit, valueOf<Any>(t["C"][1]))
        assertEquals(Unit, valueOf<Any>(t["C"][2]))
        assertEquals(Unit, valueOf<Any>(t["C"][3]))
        assertEquals(Unit, valueOf<Any>(t["C"][4]))
        assertEquals(Unit, valueOf<Any>(t["C"][5]))

        assertEquals(Unit, valueOf<Any>(t["D"][1]))
        assertEquals(Unit, valueOf<Any>(t["D"][2]))
        assertEquals(Unit, valueOf<Any>(t["D"][3]))
        assertEquals(Unit, valueOf<Any>(t["D"][4]))
        assertEquals(Unit, valueOf<Any>(t["D"][5]))

        assertEquals(Unit, valueOf<Any>(t["E"][1]))
        assertEquals(Unit, valueOf<Any>(t["E"][2]))
        assertEquals(Unit, valueOf<Any>(t["E"][3]))
        assertEquals(Unit, valueOf<Any>(t["E"][4]))
        assertEquals(Unit, valueOf<Any>(t["E"][5]))

        Table.delete(name)
        assertFalse(Table.names.contains(name))
    }

    @Test
    fun `basic table ops 2`() {
        val name = object {}.javaClass.enclosingMethod.name
        val t = Table[name]
        assertTrue(Table.names.contains(name))

        t["A"][1] = "String"
        t["B"][2] = 123L
        t["C"][3] = 123.0
        t["D"][4] = BigInteger.ONE
        t["E"][5] = BigDecimal.TEN

        assertEquals("String", valueOf<Any>(t["A"][1]))
        assertEquals(123L, valueOf<Any>(t["B"][2]))
        assertEquals(123.0, valueOf<Any>(t["C"][3]))
        assertEquals(BigInteger.ONE, valueOf<Any>(t["D"][4]))
        assertEquals(BigDecimal.TEN, valueOf<Any>(t["E"][5]))

        assertEquals(Unit, valueOf<Any>(t["A"][2]))
        assertEquals(Unit, valueOf<Any>(t["A"][3]))
        assertEquals(Unit, valueOf<Any>(t["A"][4]))
        assertEquals(Unit, valueOf<Any>(t["A"][5]))

        assertEquals(Unit, valueOf<Any>(t["B"][1]))
        assertEquals(Unit, valueOf<Any>(t["B"][3]))
        assertEquals(Unit, valueOf<Any>(t["B"][4]))
        assertEquals(Unit, valueOf<Any>(t["B"][5]))

        assertEquals(Unit, valueOf<Any>(t["C"][1]))
        assertEquals(Unit, valueOf<Any>(t["C"][2]))
        assertEquals(Unit, valueOf<Any>(t["C"][4]))
        assertEquals(Unit, valueOf<Any>(t["C"][5]))

        assertEquals(Unit, valueOf<Any>(t["D"][1]))
        assertEquals(Unit, valueOf<Any>(t["D"][2]))
        assertEquals(Unit, valueOf<Any>(t["D"][3]))
        assertEquals(Unit, valueOf<Any>(t["D"][5]))

        assertEquals(Unit, valueOf<Any>(t["E"][1]))
        assertEquals(Unit, valueOf<Any>(t["E"][2]))
        assertEquals(Unit, valueOf<Any>(t["E"][3]))
        assertEquals(Unit, valueOf<Any>(t["E"][4]))

        t.forEach {
            remove(columnOf(it))
        }

        assertEquals(Unit, valueOf<Any>(t["A"][1]))
        assertEquals(Unit, valueOf<Any>(t["A"][2]))
        assertEquals(Unit, valueOf<Any>(t["A"][3]))
        assertEquals(Unit, valueOf<Any>(t["A"][4]))
        assertEquals(Unit, valueOf<Any>(t["A"][5]))

        assertEquals(Unit, valueOf<Any>(t["B"][1]))
        assertEquals(Unit, valueOf<Any>(t["B"][2]))
        assertEquals(Unit, valueOf<Any>(t["B"][3]))
        assertEquals(Unit, valueOf<Any>(t["B"][4]))
        assertEquals(Unit, valueOf<Any>(t["B"][5]))

        assertEquals(Unit, valueOf<Any>(t["C"][1]))
        assertEquals(Unit, valueOf<Any>(t["C"][2]))
        assertEquals(Unit, valueOf<Any>(t["C"][3]))
        assertEquals(Unit, valueOf<Any>(t["C"][4]))
        assertEquals(Unit, valueOf<Any>(t["C"][5]))

        assertEquals(Unit, valueOf<Any>(t["D"][1]))
        assertEquals(Unit, valueOf<Any>(t["D"][2]))
        assertEquals(Unit, valueOf<Any>(t["D"][3]))
        assertEquals(Unit, valueOf<Any>(t["D"][4]))
        assertEquals(Unit, valueOf<Any>(t["D"][5]))

        assertEquals(Unit, valueOf<Any>(t["E"][1]))
        assertEquals(Unit, valueOf<Any>(t["E"][2]))
        assertEquals(Unit, valueOf<Any>(t["E"][3]))
        assertEquals(Unit, valueOf<Any>(t["E"][4]))
        assertEquals(Unit, valueOf<Any>(t["E"][5]))

        Table.delete(name)
        assertFalse(Table.names.contains(name))
    }

    @Test
    fun `basic table ops 3`() {
        val name = object {}.javaClass.enclosingMethod.name
        val t = Table[name]
        assertTrue(Table.names.contains(name))

        t["A", 1] = "String"
        t["B", 2] = 123L
        t["C", 3] = 123.0
        t["D", 4] = BigInteger.ONE
        t["E", 5] = BigDecimal.TEN

        assertEquals("String", valueOf<Any>(t["A"][1]))
        assertEquals(123L, valueOf<Any>(t["B"][2]))
        assertEquals(123.0, valueOf<Any>(t["C"][3]))
        assertEquals(BigInteger.ONE, valueOf<Any>(t["D"][4]))
        assertEquals(BigDecimal.TEN, valueOf<Any>(t["E"][5]))

        assertEquals(Unit, valueOf<Any>(t["A"][2]))
        assertEquals(Unit, valueOf<Any>(t["A"][3]))
        assertEquals(Unit, valueOf<Any>(t["A"][4]))
        assertEquals(Unit, valueOf<Any>(t["A"][5]))

        assertEquals(Unit, valueOf<Any>(t["B"][1]))
        assertEquals(Unit, valueOf<Any>(t["B"][3]))
        assertEquals(Unit, valueOf<Any>(t["B"][4]))
        assertEquals(Unit, valueOf<Any>(t["B"][5]))

        assertEquals(Unit, valueOf<Any>(t["C"][1]))
        assertEquals(Unit, valueOf<Any>(t["C"][2]))
        assertEquals(Unit, valueOf<Any>(t["C"][4]))
        assertEquals(Unit, valueOf<Any>(t["C"][5]))

        assertEquals(Unit, valueOf<Any>(t["D"][1]))
        assertEquals(Unit, valueOf<Any>(t["D"][2]))
        assertEquals(Unit, valueOf<Any>(t["D"][3]))
        assertEquals(Unit, valueOf<Any>(t["D"][5]))

        assertEquals(Unit, valueOf<Any>(t["E"][1]))
        assertEquals(Unit, valueOf<Any>(t["E"][2]))
        assertEquals(Unit, valueOf<Any>(t["E"][3]))
        assertEquals(Unit, valueOf<Any>(t["E"][4]))

        columnsOf(t).forEach { remove(it) }

        assertEquals(Unit, valueOf<Any>(t["A"][1]))
        assertEquals(Unit, valueOf<Any>(t["A"][2]))
        assertEquals(Unit, valueOf<Any>(t["A"][3]))
        assertEquals(Unit, valueOf<Any>(t["A"][4]))
        assertEquals(Unit, valueOf<Any>(t["A"][5]))

        assertEquals(Unit, valueOf<Any>(t["B"][1]))
        assertEquals(Unit, valueOf<Any>(t["B"][2]))
        assertEquals(Unit, valueOf<Any>(t["B"][3]))
        assertEquals(Unit, valueOf<Any>(t["B"][4]))
        assertEquals(Unit, valueOf<Any>(t["B"][5]))

        assertEquals(Unit, valueOf<Any>(t["C"][1]))
        assertEquals(Unit, valueOf<Any>(t["C"][2]))
        assertEquals(Unit, valueOf<Any>(t["C"][3]))
        assertEquals(Unit, valueOf<Any>(t["C"][4]))
        assertEquals(Unit, valueOf<Any>(t["C"][5]))

        assertEquals(Unit, valueOf<Any>(t["D"][1]))
        assertEquals(Unit, valueOf<Any>(t["D"][2]))
        assertEquals(Unit, valueOf<Any>(t["D"][3]))
        assertEquals(Unit, valueOf<Any>(t["D"][4]))
        assertEquals(Unit, valueOf<Any>(t["D"][5]))

        assertEquals(Unit, valueOf<Any>(t["E"][1]))
        assertEquals(Unit, valueOf<Any>(t["E"][2]))
        assertEquals(Unit, valueOf<Any>(t["E"][3]))
        assertEquals(Unit, valueOf<Any>(t["E"][4]))
        assertEquals(Unit, valueOf<Any>(t["E"][5]))
    }

    private fun typeValue(clazz: KClass<*>): Int {
        return when (clazz) {
            BigDecimal::class -> 4
            BigInteger::class -> 3
            Double::class -> 2
            Long::class -> 1
            else -> throw UnsupportedOperationException(clazz.toString())
        }
    }

    private fun typePref(class1: KClass<*>, class2: KClass<*>): KClass<*> {
        val (class3, class4) = listOf(class1, class2).sortedWith { o1, o2 -> typeValue(o1).compareTo(typeValue(o2)) }

        return when (class4) {
            BigInteger::class -> when (class3) {
                Long::class -> BigInteger::class
                Double::class -> BigDecimal::class
                BigInteger::class -> BigInteger::class
                BigDecimal::class -> BigDecimal::class
                else -> throw UnsupportedOperationException(class3.simpleName + " " + class4.simpleName)
            }
            BigDecimal::class -> BigDecimal::class
            else -> class4
        }
    }

    @Test
    fun `basic table math 1`() {
        // Testing math between cells
        val t = Table[object {}.javaClass.enclosingMethod.name]

        val values = listOf(1, 2L, 3F, 4.0, BigInteger.TWO, BigDecimal.TEN)

        var idx = 0
        val idxMapping = HashMap<Pair<Int, Int>, Int>()

        for ((idx1, val1) in values.withIndex()) {
            for ((idx2, val2) in values.withIndex()) {
                t["Val1"][idx] = val1 as Number
                t["Val2"][idx] = val2 as Number
                t["Plus"][idx] = t["Val1"][idx] + t["Val2"][idx]
                t["Minus"][idx] = t["Val1"][idx] - t["Val2"][idx]
                t["Times"][idx] = t["Val1"][idx] * t["Val2"][idx]
                t["Div"][idx] = t["Val1"][idx] / t["Val2"][idx]
                t["Rem"][idx] = t["Val1"][idx] % t["Val2"][idx]

                idxMapping[Pair(idx1, idx2)] = idx

                idx++
            }
        }

        for (i in 0 until idx) {
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Plus"][i])!!::class)
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Minus"][i])!!::class)
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Times"][i])!!::class)
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Div"][i])!!::class)
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Rem"][i])!!::class)
        }

        assertEquals(1L + 1L, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 0)]!!]))
        assertEquals(1L - 1L, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 0)]!!]))
        assertEquals(1L * 1L, valueOf<Any>(t["Times"][idxMapping[Pair(0, 0)]!!]))
        assertEquals(1L / 1L, valueOf<Any>(t["Div"][idxMapping[Pair(0, 0)]!!]))
        assertEquals(1L % 1L, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 0)]!!]))

        assertEquals(1L + 2L, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 1)]!!]))
        assertEquals(1L - 2L, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 1)]!!]))
        assertEquals(1L * 2L, valueOf<Any>(t["Times"][idxMapping[Pair(0, 1)]!!]))
        assertEquals(1L / 2L, valueOf<Any>(t["Div"][idxMapping[Pair(0, 1)]!!]))
        assertEquals(1L % 2L, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 1)]!!]))

        assertEquals(1L + 3.0, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 2)]!!]))
        assertEquals(1L - 3.0, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 2)]!!]))
        assertEquals(1L * 3.0, valueOf<Any>(t["Times"][idxMapping[Pair(0, 2)]!!]))
        assertEquals(1L / 3.0, valueOf<Any>(t["Div"][idxMapping[Pair(0, 2)]!!]))
        assertEquals(1L % 3.0, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 2)]!!]))

        assertEquals(1L + 4.0, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 3)]!!]))
        assertEquals(1L - 4.0, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 3)]!!]))
        assertEquals(1L * 4.0, valueOf<Any>(t["Times"][idxMapping[Pair(0, 3)]!!]))
        assertEquals(1L / 4.0, valueOf<Any>(t["Div"][idxMapping[Pair(0, 3)]!!]))
        assertEquals(1L % 4.0, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 3)]!!]))

        assertEquals(BigInteger.ONE + BigInteger.TWO, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 4)]!!]))
        assertEquals(BigInteger.ONE - BigInteger.TWO, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 4)]!!]))
        assertEquals(BigInteger.ONE * BigInteger.TWO, valueOf<Any>(t["Times"][idxMapping[Pair(0, 4)]!!]))
        assertEquals(BigInteger.ONE / BigInteger.TWO, valueOf<Any>(t["Div"][idxMapping[Pair(0, 4)]!!]))
        assertEquals(BigInteger.ONE % BigInteger.TWO, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 4)]!!]))

        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 5)]!!]))
        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 5)]!!]))
        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(0, 5)]!!]))
        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(0, 5)]!!]))
        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 5)]!!]))

        // --

        assertEquals(2L + 1L, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 0)]!!]))
        assertEquals(2L - 1L, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 0)]!!]))
        assertEquals(2L * 1L, valueOf<Any>(t["Times"][idxMapping[Pair(1, 0)]!!]))
        assertEquals(2L / 1L, valueOf<Any>(t["Div"][idxMapping[Pair(1, 0)]!!]))
        assertEquals(2L % 1L, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 0)]!!]))

        assertEquals(2L + 2L, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 1)]!!]))
        assertEquals(2L - 2L, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 1)]!!]))
        assertEquals(2L * 2L, valueOf<Any>(t["Times"][idxMapping[Pair(1, 1)]!!]))
        assertEquals(2L / 2L, valueOf<Any>(t["Div"][idxMapping[Pair(1, 1)]!!]))
        assertEquals(2L % 2L, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 1)]!!]))

        assertEquals((2L + 3F).toDouble(), valueOf<Any>(t["Plus"][idxMapping[Pair(1, 2)]!!]))
        assertEquals((2L - 3F).toDouble(), valueOf<Any>(t["Minus"][idxMapping[Pair(1, 2)]!!]))
        assertEquals((2L * 3F).toDouble(), valueOf<Any>(t["Times"][idxMapping[Pair(1, 2)]!!]))
        assertEquals(2L / 3.0, valueOf<Any>(t["Div"][idxMapping[Pair(1, 2)]!!]))
        assertEquals((2L % 3F).toDouble(), valueOf<Any>(t["Rem"][idxMapping[Pair(1, 2)]!!]))

        assertEquals(2L + 4.0, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 3)]!!]))
        assertEquals(2L - 4.0, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 3)]!!]))
        assertEquals(2L * 4.0, valueOf<Any>(t["Times"][idxMapping[Pair(1, 3)]!!]))
        assertEquals(2L / 4.0, valueOf<Any>(t["Div"][idxMapping[Pair(1, 3)]!!]))
        assertEquals(2L % 4.0, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 3)]!!]))

        assertEquals(BigInteger.TWO + BigInteger.TWO, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 4)]!!]))
        assertEquals(BigInteger.TWO - BigInteger.TWO, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 4)]!!]))
        assertEquals(BigInteger.TWO * BigInteger.TWO, valueOf<Any>(t["Times"][idxMapping[Pair(1, 4)]!!]))
        assertEquals(BigInteger.TWO / BigInteger.TWO, valueOf<Any>(t["Div"][idxMapping[Pair(1, 4)]!!]))
        assertEquals(BigInteger.TWO % BigInteger.TWO, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 4)]!!]))

        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(1, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(1, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 5)]!!]))

        // --

        assertEquals(3.0 + 1L, valueOf<Any>(t["Plus"][idxMapping[Pair(2, 0)]!!]))
        assertEquals(3.0 - 1L, valueOf<Any>(t["Minus"][idxMapping[Pair(2, 0)]!!]))
        assertEquals(3.0 * 1L, valueOf<Any>(t["Times"][idxMapping[Pair(2, 0)]!!]))
        assertEquals(3.0 / 1L, valueOf<Any>(t["Div"][idxMapping[Pair(2, 0)]!!]))
        assertEquals(3.0 % 1L, valueOf<Any>(t["Rem"][idxMapping[Pair(2, 0)]!!]))

        assertEquals(3.0 + 2L, valueOf<Any>(t["Plus"][idxMapping[Pair(2, 1)]!!]))
        assertEquals(3.0 - 2L, valueOf<Any>(t["Minus"][idxMapping[Pair(2, 1)]!!]))
        assertEquals(3.0 * 2L, valueOf<Any>(t["Times"][idxMapping[Pair(2, 1)]!!]))
        assertEquals(3.0 / 2L, valueOf<Any>(t["Div"][idxMapping[Pair(2, 1)]!!]))
        assertEquals(3.0 % 2L, valueOf<Any>(t["Rem"][idxMapping[Pair(2, 1)]!!]))

        assertEquals(3.0 + 3.0, valueOf<Any>(t["Plus"][idxMapping[Pair(2, 2)]!!]))
        assertEquals(3.0 - 3.0, valueOf<Any>(t["Minus"][idxMapping[Pair(2, 2)]!!]))
        assertEquals(3.0 * 3.0, valueOf<Any>(t["Times"][idxMapping[Pair(2, 2)]!!]))
        assertEquals(3.0 / 3.0, valueOf<Any>(t["Div"][idxMapping[Pair(2, 2)]!!]))
        assertEquals(3.0 % 3.0, valueOf<Any>(t["Rem"][idxMapping[Pair(2, 2)]!!]))

        assertEquals(3.0 + 4.0, valueOf<Any>(t["Plus"][idxMapping[Pair(2, 3)]!!]))
        assertEquals(3.0 - 4.0, valueOf<Any>(t["Minus"][idxMapping[Pair(2, 3)]!!]))
        assertEquals(3.0 * 4.0, valueOf<Any>(t["Times"][idxMapping[Pair(2, 3)]!!]))
        assertEquals(3.0 / 4.0, valueOf<Any>(t["Div"][idxMapping[Pair(2, 3)]!!]))
        assertEquals(3.0 % 4.0, valueOf<Any>(t["Rem"][idxMapping[Pair(2, 3)]!!]))

        assertEquals(BigDecimal("3.0") + BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Plus"][idxMapping[Pair(2, 4)]!!]))
        assertEquals(BigDecimal("3.0") - BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Minus"][idxMapping[Pair(2, 4)]!!]))
        assertEquals(BigDecimal("3.0") * BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Times"][idxMapping[Pair(2, 4)]!!]))
        assertEquals(BigDecimal("3.0") / BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Div"][idxMapping[Pair(2, 4)]!!]))
        assertEquals(BigDecimal("3.0") % BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Rem"][idxMapping[Pair(2, 4)]!!]))

        assertEquals(BigDecimal("3.0") + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(2, 5)]!!]))
        assertEquals(BigDecimal("3.0") - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(2, 5)]!!]))
        assertEquals(BigDecimal("3.0") * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(2, 5)]!!]))
        assertEquals(BigDecimal("3.0") / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(2, 5)]!!]))
        assertEquals(BigDecimal("3.0") % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(2, 5)]!!]))

        // --

        assertEquals(4.0 + 1L, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 0)]!!]))
        assertEquals(4.0 - 1L, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 0)]!!]))
        assertEquals(4.0 * 1L, valueOf<Any>(t["Times"][idxMapping[Pair(3, 0)]!!]))
        assertEquals(4.0 / 1L, valueOf<Any>(t["Div"][idxMapping[Pair(3, 0)]!!]))
        assertEquals(4.0 % 1L, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 0)]!!]))

        assertEquals(4.0 + 2L, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 1)]!!]))
        assertEquals(4.0 - 2L, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 1)]!!]))
        assertEquals(4.0 * 2L, valueOf<Any>(t["Times"][idxMapping[Pair(3, 1)]!!]))
        assertEquals(4.0 / 2L, valueOf<Any>(t["Div"][idxMapping[Pair(3, 1)]!!]))
        assertEquals(4.0 % 2L, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 1)]!!]))

        assertEquals(4.0 + 3.0, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 2)]!!]))
        assertEquals(4.0 - 3.0, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 2)]!!]))
        assertEquals(4.0 * 3.0, valueOf<Any>(t["Times"][idxMapping[Pair(3, 2)]!!]))
        assertEquals(4.0 / 3.0, valueOf<Any>(t["Div"][idxMapping[Pair(3, 2)]!!]))
        assertEquals(4.0 % 3.0, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 2)]!!]))

        assertEquals(4.0 + 4.0, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 3)]!!]))
        assertEquals(4.0 - 4.0, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 3)]!!]))
        assertEquals(4.0 * 4.0, valueOf<Any>(t["Times"][idxMapping[Pair(3, 3)]!!]))
        assertEquals(4.0 / 4.0, valueOf<Any>(t["Div"][idxMapping[Pair(3, 3)]!!]))
        assertEquals(4.0 % 4.0, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 3)]!!]))

        assertEquals(BigDecimal("4.0") + BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Plus"][idxMapping[Pair(3, 4)]!!]))
        assertEquals(BigDecimal("4.0") - BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Minus"][idxMapping[Pair(3, 4)]!!]))
        assertEquals(BigDecimal("4.0") * BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Times"][idxMapping[Pair(3, 4)]!!]))
        assertEquals(BigDecimal("4.0") / BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Div"][idxMapping[Pair(3, 4)]!!]))
        assertEquals(BigDecimal("4.0") % BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Rem"][idxMapping[Pair(3, 4)]!!]))

        assertEquals(BigDecimal("4.0") + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 5)]!!]))
        assertEquals(BigDecimal("4.0") - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 5)]!!]))
        assertEquals(BigDecimal("4.0") * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(3, 5)]!!]))
        assertEquals(BigDecimal("4.0") / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(3, 5)]!!]))
        assertEquals(BigDecimal("4.0") % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 5)]!!]))

        // --

        assertEquals(BigInteger.TWO + BigInteger.ONE, valueOf<Any>(t["Plus"][idxMapping[Pair(4, 0)]!!]))
        assertEquals(BigInteger.TWO - BigInteger.ONE, valueOf<Any>(t["Minus"][idxMapping[Pair(4, 0)]!!]))
        assertEquals(BigInteger.TWO * BigInteger.ONE, valueOf<Any>(t["Times"][idxMapping[Pair(4, 0)]!!]))
        assertEquals(BigInteger.TWO / BigInteger.ONE, valueOf<Any>(t["Div"][idxMapping[Pair(4, 0)]!!]))
        assertEquals(BigInteger.TWO % BigInteger.ONE, valueOf<Any>(t["Rem"][idxMapping[Pair(4, 0)]!!]))

        assertEquals(BigInteger.TWO + BigInteger.TWO, valueOf<Any>(t["Plus"][idxMapping[Pair(4, 1)]!!]))
        assertEquals(BigInteger.TWO - BigInteger.TWO, valueOf<Any>(t["Minus"][idxMapping[Pair(4, 1)]!!]))
        assertEquals(BigInteger.TWO * BigInteger.TWO, valueOf<Any>(t["Times"][idxMapping[Pair(4, 1)]!!]))
        assertEquals(BigInteger.TWO / BigInteger.TWO, valueOf<Any>(t["Div"][idxMapping[Pair(4, 1)]!!]))
        assertEquals(BigInteger.TWO % BigInteger.TWO, valueOf<Any>(t["Rem"][idxMapping[Pair(4, 1)]!!]))

        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal("3.0"), valueOf<Any>(t["Plus"][idxMapping[Pair(4, 2)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal("3.0"), valueOf<Any>(t["Minus"][idxMapping[Pair(4, 2)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal("3.0"), valueOf<Any>(t["Times"][idxMapping[Pair(4, 2)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal("3.0"), valueOf<Any>(t["Div"][idxMapping[Pair(4, 2)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal("3.0"), valueOf<Any>(t["Rem"][idxMapping[Pair(4, 2)]!!]))

        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal("4.0"), valueOf<Any>(t["Plus"][idxMapping[Pair(4, 3)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal("4.0"), valueOf<Any>(t["Minus"][idxMapping[Pair(4, 3)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal("4.0"), valueOf<Any>(t["Times"][idxMapping[Pair(4, 3)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal("4.0"), valueOf<Any>(t["Div"][idxMapping[Pair(4, 3)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal("4.0"), valueOf<Any>(t["Rem"][idxMapping[Pair(4, 3)]!!]))

        assertEquals(BigInteger.TWO + BigInteger.TWO, valueOf<Any>(t["Plus"][idxMapping[Pair(4, 4)]!!]))
        assertEquals(BigInteger.TWO - BigInteger.TWO, valueOf<Any>(t["Minus"][idxMapping[Pair(4, 4)]!!]))
        assertEquals(BigInteger.TWO * BigInteger.TWO, valueOf<Any>(t["Times"][idxMapping[Pair(4, 4)]!!]))
        assertEquals(BigInteger.TWO / BigInteger.TWO, valueOf<Any>(t["Div"][idxMapping[Pair(4, 4)]!!]))
        assertEquals(BigInteger.TWO % BigInteger.TWO, valueOf<Any>(t["Rem"][idxMapping[Pair(4, 4)]!!]))

        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(4, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(4, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(4, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(4, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(4, 5)]!!]))

        // --

        assertEquals(BigDecimal.TEN + BigDecimal.ONE, valueOf<Any>(t["Plus"][idxMapping[Pair(5, 0)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal.ONE, valueOf<Any>(t["Minus"][idxMapping[Pair(5, 0)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal.ONE, valueOf<Any>(t["Times"][idxMapping[Pair(5, 0)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal.ONE, valueOf<Any>(t["Div"][idxMapping[Pair(5, 0)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal.ONE, valueOf<Any>(t["Rem"][idxMapping[Pair(5, 0)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal("2"), valueOf<Any>(t["Plus"][idxMapping[Pair(5, 1)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal("2"), valueOf<Any>(t["Minus"][idxMapping[Pair(5, 1)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal("2"), valueOf<Any>(t["Times"][idxMapping[Pair(5, 1)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal("2"), valueOf<Any>(t["Div"][idxMapping[Pair(5, 1)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal("2"), valueOf<Any>(t["Rem"][idxMapping[Pair(5, 1)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal("3.0"), valueOf<Any>(t["Plus"][idxMapping[Pair(5, 2)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal("3.0"), valueOf<Any>(t["Minus"][idxMapping[Pair(5, 2)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal("3.0"), valueOf<Any>(t["Times"][idxMapping[Pair(5, 2)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal("3.0"), valueOf<Any>(t["Div"][idxMapping[Pair(5, 2)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal("3.0"), valueOf<Any>(t["Rem"][idxMapping[Pair(5, 2)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal("4.0"), valueOf<Any>(t["Plus"][idxMapping[Pair(5, 3)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal("4.0"), valueOf<Any>(t["Minus"][idxMapping[Pair(5, 3)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal("4.0"), valueOf<Any>(t["Times"][idxMapping[Pair(5, 3)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal("4.0"), valueOf<Any>(t["Div"][idxMapping[Pair(5, 3)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal("4.0"), valueOf<Any>(t["Rem"][idxMapping[Pair(5, 3)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal("2"), valueOf<Any>(t["Plus"][idxMapping[Pair(5, 4)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal("2"), valueOf<Any>(t["Minus"][idxMapping[Pair(5, 4)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal("2"), valueOf<Any>(t["Times"][idxMapping[Pair(5, 4)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal("2"), valueOf<Any>(t["Div"][idxMapping[Pair(5, 4)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal("2"), valueOf<Any>(t["Rem"][idxMapping[Pair(5, 4)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(5, 5)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(5, 5)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(5, 5)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(5, 5)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(5, 5)]!!]))
    }

    @Test
    fun `basic table math 2`() {
        // Testing math between cell and number
        val t = Table[object {}.javaClass.enclosingMethod.name]

        val values = listOf(1, 2L, 3F, 4.0, BigInteger.TWO, BigDecimal.TEN)

        var idx = 0

        for (val1 in values) {
            assertFailsWith<InvalidCellException> { t["Plus"][idx] = t["Val1"][idx] + val1 }
            assertFailsWith<InvalidCellException> { t["Minus"][idx] = t["Val1"][idx] - val1 }
            assertFailsWith<InvalidCellException> { t["Times"][idx] = t["Val1"][idx] * val1 }
            assertFailsWith<InvalidCellException> { t["Div"][idx] = t["Val1"][idx] / val1 }
            assertFailsWith<InvalidCellException> { t["Rem"][idx] = t["Val1"][idx] % val1 }

            idx++
        }

        idx = 0
        val idxMapping = HashMap<Pair<Int, Int>, Int>()

        for ((idx1, val1) in values.withIndex()) {
            for ((idx2, val2) in values.withIndex()) {
                t["Val1"][idx] = val1 as Number
                t["Val2"][idx] = val2 as Number
                t["Plus"][idx] = t["Val1"][idx] + val2
                t["Minus"][idx] = t["Val1"][idx] - val2
                t["Times"][idx] = t["Val1"][idx] * val2
                t["Div"][idx] = t["Val1"][idx] / val2
                t["Rem"][idx] = t["Val1"][idx] % val2

                idxMapping[Pair(idx1, idx2)] = idx

                idx++
            }
        }

        for (i in 0 until idx) {
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Plus"][i])!!::class)
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Minus"][i])!!::class)
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Times"][i])!!::class)
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Div"][i])!!::class)
            assertEquals(typePref(valueOf<Any>(t["Val1"][i])!!::class, valueOf<Any>(t["Val2"][i])!!::class), valueOf<Any>(t["Rem"][i])!!::class)
        }


        assertEquals(1L + 1L, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 0)]!!]))
        assertEquals(1L - 1L, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 0)]!!]))
        assertEquals(1L * 1L, valueOf<Any>(t["Times"][idxMapping[Pair(0, 0)]!!]))
        assertEquals(1L / 1L, valueOf<Any>(t["Div"][idxMapping[Pair(0, 0)]!!]))
        assertEquals(1L % 1L, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 0)]!!]))

        assertEquals(1L + 2L, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 1)]!!]))
        assertEquals(1L - 2L, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 1)]!!]))
        assertEquals(1L * 2L, valueOf<Any>(t["Times"][idxMapping[Pair(0, 1)]!!]))
        assertEquals(1L / 2L, valueOf<Any>(t["Div"][idxMapping[Pair(0, 1)]!!]))
        assertEquals(1L % 2L, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 1)]!!]))

        assertEquals((1L + 3F).toDouble(), valueOf<Any>(t["Plus"][idxMapping[Pair(0, 2)]!!]))
        assertEquals((1L - 3F).toDouble(), valueOf<Any>(t["Minus"][idxMapping[Pair(0, 2)]!!]))
        assertEquals((1L * 3F).toDouble(), valueOf<Any>(t["Times"][idxMapping[Pair(0, 2)]!!]))
        assertEquals((1L / 3F).toDouble(), valueOf<Any>(t["Div"][idxMapping[Pair(0, 2)]!!]))
        assertEquals((1L % 3F).toDouble(), valueOf<Any>(t["Rem"][idxMapping[Pair(0, 2)]!!]))

        assertEquals(1L + 4.0, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 3)]!!]))
        assertEquals(1L - 4.0, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 3)]!!]))
        assertEquals(1L * 4.0, valueOf<Any>(t["Times"][idxMapping[Pair(0, 3)]!!]))
        assertEquals(1L / 4.0, valueOf<Any>(t["Div"][idxMapping[Pair(0, 3)]!!]))
        assertEquals(1L % 4.0, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 3)]!!]))

        assertEquals(BigInteger.ONE + BigInteger.TWO, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 4)]!!]))
        assertEquals(BigInteger.ONE - BigInteger.TWO, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 4)]!!]))
        assertEquals(BigInteger.ONE * BigInteger.TWO, valueOf<Any>(t["Times"][idxMapping[Pair(0, 4)]!!]))
        assertEquals(BigInteger.ONE / BigInteger.TWO, valueOf<Any>(t["Div"][idxMapping[Pair(0, 4)]!!]))
        assertEquals(BigInteger.ONE % BigInteger.TWO, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 4)]!!]))

        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(0, 5)]!!]))
        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(0, 5)]!!]))
        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(0, 5)]!!]))
        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(0, 5)]!!]))
        assertEquals(BigInteger.ONE.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(0, 5)]!!]))

        // --

        assertEquals(2L + 1L, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 0)]!!]))
        assertEquals(2L - 1L, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 0)]!!]))
        assertEquals(2L * 1L, valueOf<Any>(t["Times"][idxMapping[Pair(1, 0)]!!]))
        assertEquals(2L / 1L, valueOf<Any>(t["Div"][idxMapping[Pair(1, 0)]!!]))
        assertEquals(2L % 1L, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 0)]!!]))

        assertEquals(2L + 2L, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 1)]!!]))
        assertEquals(2L - 2L, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 1)]!!]))
        assertEquals(2L * 2L, valueOf<Any>(t["Times"][idxMapping[Pair(1, 1)]!!]))
        assertEquals(2L / 2L, valueOf<Any>(t["Div"][idxMapping[Pair(1, 1)]!!]))
        assertEquals(2L % 2L, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 1)]!!]))

        assertEquals((2L + 3F).toDouble(), valueOf<Any>(t["Plus"][idxMapping[Pair(1, 2)]!!]))
        assertEquals((2L - 3F).toDouble(), valueOf<Any>(t["Minus"][idxMapping[Pair(1, 2)]!!]))
        assertEquals((2L * 3F).toDouble(), valueOf<Any>(t["Times"][idxMapping[Pair(1, 2)]!!]))
        assertEquals((2L / 3F).toDouble(), valueOf<Any>(t["Div"][idxMapping[Pair(1, 2)]!!]))
        assertEquals((2L % 3F).toDouble(), valueOf<Any>(t["Rem"][idxMapping[Pair(1, 2)]!!]))

        assertEquals(2L + 4.0, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 3)]!!]))
        assertEquals(2L - 4.0, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 3)]!!]))
        assertEquals(2L * 4.0, valueOf<Any>(t["Times"][idxMapping[Pair(1, 3)]!!]))
        assertEquals(2L / 4.0, valueOf<Any>(t["Div"][idxMapping[Pair(1, 3)]!!]))
        assertEquals(2L % 4.0, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 3)]!!]))

        assertEquals(BigInteger.TWO + BigInteger.TWO, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 4)]!!]))
        assertEquals(BigInteger.TWO - BigInteger.TWO, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 4)]!!]))
        assertEquals(BigInteger.TWO * BigInteger.TWO, valueOf<Any>(t["Times"][idxMapping[Pair(1, 4)]!!]))
        assertEquals(BigInteger.TWO / BigInteger.TWO, valueOf<Any>(t["Div"][idxMapping[Pair(1, 4)]!!]))
        assertEquals(BigInteger.TWO % BigInteger.TWO, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 4)]!!]))

        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(1, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(1, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(1, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(1, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(1, 5)]!!]))

        // --

        assertEquals((3F + 1L).toDouble(), valueOf<Any>(t["Plus"][idxMapping[Pair(2, 0)]!!]))
        assertEquals((3F - 1L).toDouble(), valueOf<Any>(t["Minus"][idxMapping[Pair(2, 0)]!!]))
        assertEquals((3F * 1L).toDouble(), valueOf<Any>(t["Times"][idxMapping[Pair(2, 0)]!!]))
        assertEquals((3F / 1L).toDouble(), valueOf<Any>(t["Div"][idxMapping[Pair(2, 0)]!!]))
        assertEquals((3F % 1L).toDouble(), valueOf<Any>(t["Rem"][idxMapping[Pair(2, 0)]!!]))

        assertEquals((3F + 2L).toDouble(), valueOf<Any>(t["Plus"][idxMapping[Pair(2, 1)]!!]))
        assertEquals((3F - 2L).toDouble(), valueOf<Any>(t["Minus"][idxMapping[Pair(2, 1)]!!]))
        assertEquals((3F * 2L).toDouble(), valueOf<Any>(t["Times"][idxMapping[Pair(2, 1)]!!]))
        assertEquals((3F / 2L).toDouble(), valueOf<Any>(t["Div"][idxMapping[Pair(2, 1)]!!]))
        assertEquals((3F % 2L).toDouble(), valueOf<Any>(t["Rem"][idxMapping[Pair(2, 1)]!!]))

        assertEquals((3F + 3F).toDouble(), valueOf<Any>(t["Plus"][idxMapping[Pair(2, 2)]!!]))
        assertEquals((3F - 3F).toDouble(), valueOf<Any>(t["Minus"][idxMapping[Pair(2, 2)]!!]))
        assertEquals((3F * 3F).toDouble(), valueOf<Any>(t["Times"][idxMapping[Pair(2, 2)]!!]))
        assertEquals((3F / 3F).toDouble(), valueOf<Any>(t["Div"][idxMapping[Pair(2, 2)]!!]))
        assertEquals((3F % 3F).toDouble(), valueOf<Any>(t["Rem"][idxMapping[Pair(2, 2)]!!]))

        assertEquals(3F + 4.0, valueOf<Any>(t["Plus"][idxMapping[Pair(2, 3)]!!]))
        assertEquals(3F - 4.0, valueOf<Any>(t["Minus"][idxMapping[Pair(2, 3)]!!]))
        assertEquals(3F * 4.0, valueOf<Any>(t["Times"][idxMapping[Pair(2, 3)]!!]))
        assertEquals(3F / 4.0, valueOf<Any>(t["Div"][idxMapping[Pair(2, 3)]!!]))
        assertEquals(3F % 4.0, valueOf<Any>(t["Rem"][idxMapping[Pair(2, 3)]!!]))

        assertEquals(BigDecimal("3.0") + BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Plus"][idxMapping[Pair(2, 4)]!!]))
        assertEquals(BigDecimal("3.0") - BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Minus"][idxMapping[Pair(2, 4)]!!]))
        assertEquals(BigDecimal("3.0") * BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Times"][idxMapping[Pair(2, 4)]!!]))
        assertEquals(BigDecimal("3.0") / BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Div"][idxMapping[Pair(2, 4)]!!]))
        assertEquals(BigDecimal("3.0") % BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Rem"][idxMapping[Pair(2, 4)]!!]))

        assertEquals(BigDecimal("3.0") + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(2, 5)]!!]))
        assertEquals(BigDecimal("3.0") - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(2, 5)]!!]))
        assertEquals(BigDecimal("3.0") * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(2, 5)]!!]))
        assertEquals(BigDecimal("3.0") / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(2, 5)]!!]))
        assertEquals(BigDecimal("3.0") % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(2, 5)]!!]))

        // --

        assertEquals(4.0 + 1L, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 0)]!!]))
        assertEquals(4.0 - 1L, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 0)]!!]))
        assertEquals(4.0 * 1L, valueOf<Any>(t["Times"][idxMapping[Pair(3, 0)]!!]))
        assertEquals(4.0 / 1L, valueOf<Any>(t["Div"][idxMapping[Pair(3, 0)]!!]))
        assertEquals(4.0 % 1L, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 0)]!!]))

        assertEquals(4.0 + 2L, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 1)]!!]))
        assertEquals(4.0 - 2L, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 1)]!!]))
        assertEquals(4.0 * 2L, valueOf<Any>(t["Times"][idxMapping[Pair(3, 1)]!!]))
        assertEquals(4.0 / 2L, valueOf<Any>(t["Div"][idxMapping[Pair(3, 1)]!!]))
        assertEquals(4.0 % 2L, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 1)]!!]))

        assertEquals(4.0 + 3.0, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 2)]!!]))
        assertEquals(4.0 - 3.0, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 2)]!!]))
        assertEquals(4.0 * 3.0, valueOf<Any>(t["Times"][idxMapping[Pair(3, 2)]!!]))
        assertEquals(4.0 / 3.0, valueOf<Any>(t["Div"][idxMapping[Pair(3, 2)]!!]))
        assertEquals(4.0 % 3.0, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 2)]!!]))

        assertEquals(4.0 + 4.0, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 3)]!!]))
        assertEquals(4.0 - 4.0, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 3)]!!]))
        assertEquals(4.0 * 4.0, valueOf<Any>(t["Times"][idxMapping[Pair(3, 3)]!!]))
        assertEquals(4.0 / 4.0, valueOf<Any>(t["Div"][idxMapping[Pair(3, 3)]!!]))
        assertEquals(4.0 % 4.0, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 3)]!!]))

        assertEquals(BigDecimal("4.0") + BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Plus"][idxMapping[Pair(3, 4)]!!]))
        assertEquals(BigDecimal("4.0") - BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Minus"][idxMapping[Pair(3, 4)]!!]))
        assertEquals(BigDecimal("4.0") * BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Times"][idxMapping[Pair(3, 4)]!!]))
        assertEquals(BigDecimal("4.0") / BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Div"][idxMapping[Pair(3, 4)]!!]))
        assertEquals(BigDecimal("4.0") % BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext), valueOf<Any>(t["Rem"][idxMapping[Pair(3, 4)]!!]))

        assertEquals(BigDecimal("4.0") + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(3, 5)]!!]))
        assertEquals(BigDecimal("4.0") - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(3, 5)]!!]))
        assertEquals(BigDecimal("4.0") * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(3, 5)]!!]))
        assertEquals(BigDecimal("4.0") / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(3, 5)]!!]))
        assertEquals(BigDecimal("4.0") % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(3, 5)]!!]))

        // --

        assertEquals(BigInteger.TWO + BigInteger.ONE, valueOf<Any>(t["Plus"][idxMapping[Pair(4, 0)]!!]))
        assertEquals(BigInteger.TWO - BigInteger.ONE, valueOf<Any>(t["Minus"][idxMapping[Pair(4, 0)]!!]))
        assertEquals(BigInteger.TWO * BigInteger.ONE, valueOf<Any>(t["Times"][idxMapping[Pair(4, 0)]!!]))
        assertEquals(BigInteger.TWO / BigInteger.ONE, valueOf<Any>(t["Div"][idxMapping[Pair(4, 0)]!!]))
        assertEquals(BigInteger.TWO % BigInteger.ONE, valueOf<Any>(t["Rem"][idxMapping[Pair(4, 0)]!!]))

        assertEquals(BigInteger.TWO + BigInteger.TWO, valueOf<Any>(t["Plus"][idxMapping[Pair(4, 1)]!!]))
        assertEquals(BigInteger.TWO - BigInteger.TWO, valueOf<Any>(t["Minus"][idxMapping[Pair(4, 1)]!!]))
        assertEquals(BigInteger.TWO * BigInteger.TWO, valueOf<Any>(t["Times"][idxMapping[Pair(4, 1)]!!]))
        assertEquals(BigInteger.TWO / BigInteger.TWO, valueOf<Any>(t["Div"][idxMapping[Pair(4, 1)]!!]))
        assertEquals(BigInteger.TWO % BigInteger.TWO, valueOf<Any>(t["Rem"][idxMapping[Pair(4, 1)]!!]))

        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal("3.0"), valueOf<Any>(t["Plus"][idxMapping[Pair(4, 2)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal("3.0"), valueOf<Any>(t["Minus"][idxMapping[Pair(4, 2)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal("3.0"), valueOf<Any>(t["Times"][idxMapping[Pair(4, 2)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal("3.0"), valueOf<Any>(t["Div"][idxMapping[Pair(4, 2)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal("3.0"), valueOf<Any>(t["Rem"][idxMapping[Pair(4, 2)]!!]))

        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal("4.0"), valueOf<Any>(t["Plus"][idxMapping[Pair(4, 3)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal("4.0"), valueOf<Any>(t["Minus"][idxMapping[Pair(4, 3)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal("4.0"), valueOf<Any>(t["Times"][idxMapping[Pair(4, 3)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal("4.0"), valueOf<Any>(t["Div"][idxMapping[Pair(4, 3)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal("4.0"), valueOf<Any>(t["Rem"][idxMapping[Pair(4, 3)]!!]))

        assertEquals(BigInteger.TWO + BigInteger.TWO, valueOf<Any>(t["Plus"][idxMapping[Pair(4, 4)]!!]))
        assertEquals(BigInteger.TWO - BigInteger.TWO, valueOf<Any>(t["Minus"][idxMapping[Pair(4, 4)]!!]))
        assertEquals(BigInteger.TWO * BigInteger.TWO, valueOf<Any>(t["Times"][idxMapping[Pair(4, 4)]!!]))
        assertEquals(BigInteger.TWO / BigInteger.TWO, valueOf<Any>(t["Div"][idxMapping[Pair(4, 4)]!!]))
        assertEquals(BigInteger.TWO % BigInteger.TWO, valueOf<Any>(t["Rem"][idxMapping[Pair(4, 4)]!!]))

        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(4, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(4, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(4, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(4, 5)]!!]))
        assertEquals(BigInteger.TWO.toBigDecimal(mathContext = Precision.mathContext) % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(4, 5)]!!]))

        // --

        assertEquals(BigDecimal.TEN + BigDecimal.ONE, valueOf<Any>(t["Plus"][idxMapping[Pair(5, 0)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal.ONE, valueOf<Any>(t["Minus"][idxMapping[Pair(5, 0)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal.ONE, valueOf<Any>(t["Times"][idxMapping[Pair(5, 0)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal.ONE, valueOf<Any>(t["Div"][idxMapping[Pair(5, 0)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal.ONE, valueOf<Any>(t["Rem"][idxMapping[Pair(5, 0)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal("2"), valueOf<Any>(t["Plus"][idxMapping[Pair(5, 1)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal("2"), valueOf<Any>(t["Minus"][idxMapping[Pair(5, 1)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal("2"), valueOf<Any>(t["Times"][idxMapping[Pair(5, 1)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal("2"), valueOf<Any>(t["Div"][idxMapping[Pair(5, 1)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal("2"), valueOf<Any>(t["Rem"][idxMapping[Pair(5, 1)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal("3.0"), valueOf<Any>(t["Plus"][idxMapping[Pair(5, 2)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal("3.0"), valueOf<Any>(t["Minus"][idxMapping[Pair(5, 2)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal("3.0"), valueOf<Any>(t["Times"][idxMapping[Pair(5, 2)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal("3.0"), valueOf<Any>(t["Div"][idxMapping[Pair(5, 2)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal("3.0"), valueOf<Any>(t["Rem"][idxMapping[Pair(5, 2)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal("4.0"), valueOf<Any>(t["Plus"][idxMapping[Pair(5, 3)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal("4.0"), valueOf<Any>(t["Minus"][idxMapping[Pair(5, 3)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal("4.0"), valueOf<Any>(t["Times"][idxMapping[Pair(5, 3)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal("4.0"), valueOf<Any>(t["Div"][idxMapping[Pair(5, 3)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal("4.0"), valueOf<Any>(t["Rem"][idxMapping[Pair(5, 3)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal("2"), valueOf<Any>(t["Plus"][idxMapping[Pair(5, 4)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal("2"), valueOf<Any>(t["Minus"][idxMapping[Pair(5, 4)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal("2"), valueOf<Any>(t["Times"][idxMapping[Pair(5, 4)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal("2"), valueOf<Any>(t["Div"][idxMapping[Pair(5, 4)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal("2"), valueOf<Any>(t["Rem"][idxMapping[Pair(5, 4)]!!]))

        assertEquals(BigDecimal.TEN + BigDecimal.TEN, valueOf<Any>(t["Plus"][idxMapping[Pair(5, 5)]!!]))
        assertEquals(BigDecimal.TEN - BigDecimal.TEN, valueOf<Any>(t["Minus"][idxMapping[Pair(5, 5)]!!]))
        assertEquals(BigDecimal.TEN * BigDecimal.TEN, valueOf<Any>(t["Times"][idxMapping[Pair(5, 5)]!!]))
        assertEquals(BigDecimal.TEN / BigDecimal.TEN, valueOf<Any>(t["Div"][idxMapping[Pair(5, 5)]!!]))
        assertEquals(BigDecimal.TEN % BigDecimal.TEN, valueOf<Any>(t["Rem"][idxMapping[Pair(5, 5)]!!]))
    }

    @Test
    fun `basic cell fetch`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]
        t["A", 11] = "A 11"
        t["A", 12] = "A 12"
        t["A", 13] = "A 13"

        t["B", 11] = "B 11"
        t["B", 13] = "B 13"

        // This is also covered in TableTest

        assertEquals("A 11", valueOf<Any>(t["A", 11]))
        assertEquals("A 11", valueOf<Any>(t["A"] at 11))
        assertEquals("A 11", valueOf<Any>(t["A", IndexRelation.AT, 11]))

        assertEquals("A 12", valueOf<Any>(t["A", 12]))
        assertEquals("A 12", valueOf<Any>(t["A"] atOrAfter 12))
        assertEquals("A 12", valueOf<Any>(t["A"] atOrBefore 12))
        assertEquals("A 12", valueOf<Any>(t["A"] after 11))
        assertEquals("A 12", valueOf<Any>(t["A"] before 13))
        assertEquals("A 12", valueOf<Any>(t["A", IndexRelation.AT_OR_AFTER, 12]))
        assertEquals("A 12", valueOf<Any>(t["A", IndexRelation.AT_OR_BEFORE, 12]))
        assertEquals("A 12", valueOf<Any>(t["A", IndexRelation.AFTER, 11]))
        assertEquals("A 12", valueOf<Any>(t["A", IndexRelation.BEFORE, 13]))

        assertEquals("B 11", valueOf<Any>(t["B"] before 12))
        assertEquals("B 11", valueOf<Any>(t["B", IndexRelation.BEFORE, 12]))
        assertEquals("B 13", valueOf<Any>(t["B"] after 12))
        assertEquals("B 13", valueOf<Any>(t["B", IndexRelation.AFTER, 12]))
    }
}
