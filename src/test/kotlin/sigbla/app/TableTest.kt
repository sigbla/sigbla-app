package sigbla.app

// NOTE: It's important that this does not import anything from BasicMath!

import sigbla.app.internals.Registry
import sigbla.app.Table.Companion.deleteTable
import org.junit.Assert.*
import org.junit.Test
import sigbla.app.Table.Companion.newTable
import org.junit.After
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

// NOTE: It's important that this does not import anything from BasicMath!

class TableTest {
    @After
    fun cleanup() {
        Registry.tableNames().forEach { Registry.deleteTable(it) }
    }

    @Test
    fun basicTableOps1() {
        val name = "basicTableOps1"
        val t = newTable(name)
        assertTrue(Registry.tableNames().contains(name))

        t["A"][1] = "String"
        t["B"][2] = 123L
        t["C"][3] = 123.0
        t["D"][4] = BigInteger.ONE
        t["E"][5] = BigDecimal.TEN

        assertEquals("String", t["A"][1].value)
        assertEquals(123L, t["B"][2].value)
        assertEquals(123.0, t["C"][3].value)
        assertEquals(BigInteger.ONE, t["D"][4].value)
        assertEquals(BigDecimal.TEN, t["E"][5].value)

        assertEquals(Unit, t["A"][2].value)
        assertEquals(Unit, t["A"][3].value)
        assertEquals(Unit, t["A"][4].value)
        assertEquals(Unit, t["A"][5].value)

        assertEquals(Unit, t["B"][1].value)
        assertEquals(Unit, t["B"][3].value)
        assertEquals(Unit, t["B"][4].value)
        assertEquals(Unit, t["B"][5].value)

        assertEquals(Unit, t["C"][1].value)
        assertEquals(Unit, t["C"][2].value)
        assertEquals(Unit, t["C"][4].value)
        assertEquals(Unit, t["C"][5].value)

        assertEquals(Unit, t["D"][1].value)
        assertEquals(Unit, t["D"][2].value)
        assertEquals(Unit, t["D"][3].value)
        assertEquals(Unit, t["D"][5].value)

        assertEquals(Unit, t["E"][1].value)
        assertEquals(Unit, t["E"][2].value)
        assertEquals(Unit, t["E"][3].value)
        assertEquals(Unit, t["E"][4].value)

        t.headers.forEach { t.remove(it) }

        assertEquals(Unit, t["A"][1].value)
        assertEquals(Unit, t["A"][2].value)
        assertEquals(Unit, t["A"][3].value)
        assertEquals(Unit, t["A"][4].value)
        assertEquals(Unit, t["A"][5].value)

        assertEquals(Unit, t["B"][1].value)
        assertEquals(Unit, t["B"][2].value)
        assertEquals(Unit, t["B"][3].value)
        assertEquals(Unit, t["B"][4].value)
        assertEquals(Unit, t["B"][5].value)

        assertEquals(Unit, t["C"][1].value)
        assertEquals(Unit, t["C"][2].value)
        assertEquals(Unit, t["C"][3].value)
        assertEquals(Unit, t["C"][4].value)
        assertEquals(Unit, t["C"][5].value)

        assertEquals(Unit, t["D"][1].value)
        assertEquals(Unit, t["D"][2].value)
        assertEquals(Unit, t["D"][3].value)
        assertEquals(Unit, t["D"][4].value)
        assertEquals(Unit, t["D"][5].value)

        assertEquals(Unit, t["E"][1].value)
        assertEquals(Unit, t["E"][2].value)
        assertEquals(Unit, t["E"][3].value)
        assertEquals(Unit, t["E"][4].value)
        assertEquals(Unit, t["E"][5].value)

        deleteTable(name)
        assertFalse(Registry.tableNames().contains(name))
    }

    @Test
    fun basicTableOps2() {
        val name = "basicTableOps2"
        val t = newTable(name)
        assertTrue(Registry.tableNames().contains(name))

        t["A", 1] = "String"
        t["B", 2] = 123L
        t["C", 3] = 123.0
        t["D", 4] = BigInteger.ONE
        t["E", 5] = BigDecimal.TEN

        assertEquals("String", t["A"][1].value)
        assertEquals(123L, t["B"][2].value)
        assertEquals(123.0, t["C"][3].value)
        assertEquals(BigInteger.ONE, t["D"][4].value)
        assertEquals(BigDecimal.TEN, t["E"][5].value)

        assertEquals(Unit, t["A"][2].value)
        assertEquals(Unit, t["A"][3].value)
        assertEquals(Unit, t["A"][4].value)
        assertEquals(Unit, t["A"][5].value)

        assertEquals(Unit, t["B"][1].value)
        assertEquals(Unit, t["B"][3].value)
        assertEquals(Unit, t["B"][4].value)
        assertEquals(Unit, t["B"][5].value)

        assertEquals(Unit, t["C"][1].value)
        assertEquals(Unit, t["C"][2].value)
        assertEquals(Unit, t["C"][4].value)
        assertEquals(Unit, t["C"][5].value)

        assertEquals(Unit, t["D"][1].value)
        assertEquals(Unit, t["D"][2].value)
        assertEquals(Unit, t["D"][3].value)
        assertEquals(Unit, t["D"][5].value)

        assertEquals(Unit, t["E"][1].value)
        assertEquals(Unit, t["E"][2].value)
        assertEquals(Unit, t["E"][3].value)
        assertEquals(Unit, t["E"][4].value)

        t.headers.forEach { t.remove(it) }

        assertEquals(Unit, t["A"][1].value)
        assertEquals(Unit, t["A"][2].value)
        assertEquals(Unit, t["A"][3].value)
        assertEquals(Unit, t["A"][4].value)
        assertEquals(Unit, t["A"][5].value)

        assertEquals(Unit, t["B"][1].value)
        assertEquals(Unit, t["B"][2].value)
        assertEquals(Unit, t["B"][3].value)
        assertEquals(Unit, t["B"][4].value)
        assertEquals(Unit, t["B"][5].value)

        assertEquals(Unit, t["C"][1].value)
        assertEquals(Unit, t["C"][2].value)
        assertEquals(Unit, t["C"][3].value)
        assertEquals(Unit, t["C"][4].value)
        assertEquals(Unit, t["C"][5].value)

        assertEquals(Unit, t["D"][1].value)
        assertEquals(Unit, t["D"][2].value)
        assertEquals(Unit, t["D"][3].value)
        assertEquals(Unit, t["D"][4].value)
        assertEquals(Unit, t["D"][5].value)

        assertEquals(Unit, t["E"][1].value)
        assertEquals(Unit, t["E"][2].value)
        assertEquals(Unit, t["E"][3].value)
        assertEquals(Unit, t["E"][4].value)
        assertEquals(Unit, t["E"][5].value)
    }

    fun typeValue(clazz: KClass<*>): Int {
        return when (clazz) {
            BigDecimal::class -> 4
            BigInteger::class -> 3
            Double::class -> 2
            Long::class -> 1
            else -> throw UnsupportedOperationException(clazz.toString())
        }
    }

    fun typePref(class1: KClass<*>, class2: KClass<*>): KClass<*> {
        val (class3, class4) = listOf(class1, class2).sortedWith(Comparator { o1, o2 -> typeValue(o1).compareTo(typeValue(o2)) })

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
    fun basicTableMath1() {
        // Testing math between cells
        val t = newTable("basicTableMath1")

        val values = listOf(1, 2L, 3F, 3.0, BigInteger.TWO, BigDecimal.TEN)

        var idx = 0
        var idxMapping = HashMap<Pair<Int, Int>, Int>()

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
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Plus"][i].value!!::class)
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Minus"][i].value!!::class)
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Times"][i].value!!::class)
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Div"][i].value!!::class)
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Rem"][i].value!!::class)
        }

        assertEquals(1L + 1L, t["Plus"][idxMapping[Pair(0, 0)]!!].value)
        assertEquals(1L - 1L, t["Minus"][idxMapping[Pair(0, 0)]!!].value)
        assertEquals(1L * 1L, t["Times"][idxMapping[Pair(0, 0)]!!].value)
        assertEquals(1L / 1L, t["Div"][idxMapping[Pair(0, 0)]!!].value)
        assertEquals(1L % 1L, t["Rem"][idxMapping[Pair(0, 0)]!!].value)

        assertEquals(2L + 1L, t["Plus"][idxMapping[Pair(1, 0)]!!].value)
        assertEquals(2L - 1L, t["Minus"][idxMapping[Pair(1, 0)]!!].value)
        assertEquals(2L * 1L, t["Times"][idxMapping[Pair(1, 0)]!!].value)
        assertEquals(2L / 1L, t["Div"][idxMapping[Pair(1, 0)]!!].value)
        assertEquals(2L % 1L, t["Rem"][idxMapping[Pair(1, 0)]!!].value)

        assertEquals(3.0 + 2L, t["Plus"][idxMapping[Pair(2, 1)]!!].value)
        assertEquals(3.0 - 2L, t["Minus"][idxMapping[Pair(2, 1)]!!].value)
        assertEquals(3.0 * 2L, t["Times"][idxMapping[Pair(2, 1)]!!].value)
        assertEquals(3.0 / 2L, t["Div"][idxMapping[Pair(2, 1)]!!].value)
        assertEquals(3.0 % 2L, t["Rem"][idxMapping[Pair(2, 1)]!!].value)

        assertEquals(3.0 + 2L, t["Plus"][idxMapping[Pair(3, 1)]!!].value)
        assertEquals(3.0 - 2L, t["Minus"][idxMapping[Pair(3, 1)]!!].value)
        assertEquals(3.0 * 2L, t["Times"][idxMapping[Pair(3, 1)]!!].value)
        assertEquals(3.0 / 2L, t["Div"][idxMapping[Pair(3, 1)]!!].value)
        assertEquals(3.0 % 2L, t["Rem"][idxMapping[Pair(3, 1)]!!].value)

        assertEquals(BigInteger.TWO + BigInteger.TWO, t["Plus"][idxMapping[Pair(4, 1)]!!].value)
        assertEquals(BigInteger.TWO - BigInteger.TWO, t["Minus"][idxMapping[Pair(4, 1)]!!].value)
        assertEquals(BigInteger.TWO * BigInteger.TWO, t["Times"][idxMapping[Pair(4, 1)]!!].value)
        assertEquals(BigInteger.TWO / BigInteger.TWO, t["Div"][idxMapping[Pair(4, 1)]!!].value)
        assertEquals(BigInteger.TWO % BigInteger.TWO, t["Rem"][idxMapping[Pair(4, 1)]!!].value)

        assertEquals(BigDecimal.TEN + BigDecimal.valueOf(2), t["Plus"][idxMapping[Pair(5, 1)]!!].value)
        assertEquals(BigDecimal.TEN - BigDecimal.valueOf(2), t["Minus"][idxMapping[Pair(5, 1)]!!].value)
        assertEquals(BigDecimal.TEN * BigDecimal.valueOf(2), t["Times"][idxMapping[Pair(5, 1)]!!].value)
        assertEquals(BigDecimal.TEN / BigDecimal.valueOf(2), t["Div"][idxMapping[Pair(5, 1)]!!].value)
        assertEquals(BigDecimal.TEN % BigDecimal.valueOf(2), t["Rem"][idxMapping[Pair(5, 1)]!!].value)
    }

    @Test
    fun basicTableMath2() {
        // Testing math between cell and number
        val t = newTable("basicTableMath1")

        val values = listOf(1, 2L, 3F, 3.0, BigInteger.TWO, BigDecimal.TEN)

        var idx = 0
        var idxMapping = HashMap<Pair<Int, Int>, Int>()

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
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Plus"][i].value!!::class)
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Minus"][i].value!!::class)
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Times"][i].value!!::class)
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Div"][i].value!!::class)
            assertEquals(typePref(t["Val1"][i].value!!::class, t["Val2"][i].value!!::class), t["Rem"][i].value!!::class)
        }

        assertEquals(1L + 1L, t["Plus"][idxMapping[Pair(0, 0)]!!].value)
        assertEquals(1L - 1L, t["Minus"][idxMapping[Pair(0, 0)]!!].value)
        assertEquals(1L * 1L, t["Times"][idxMapping[Pair(0, 0)]!!].value)
        assertEquals(1L / 1L, t["Div"][idxMapping[Pair(0, 0)]!!].value)
        assertEquals(1L % 1L, t["Rem"][idxMapping[Pair(0, 0)]!!].value)

        assertEquals(2L + 1L, t["Plus"][idxMapping[Pair(1, 0)]!!].value)
        assertEquals(2L - 1L, t["Minus"][idxMapping[Pair(1, 0)]!!].value)
        assertEquals(2L * 1L, t["Times"][idxMapping[Pair(1, 0)]!!].value)
        assertEquals(2L / 1L, t["Div"][idxMapping[Pair(1, 0)]!!].value)
        assertEquals(2L % 1L, t["Rem"][idxMapping[Pair(1, 0)]!!].value)

        assertEquals(3.0 + 2L, t["Plus"][idxMapping[Pair(2, 1)]!!].value)
        assertEquals(3.0 - 2L, t["Minus"][idxMapping[Pair(2, 1)]!!].value)
        assertEquals(3.0 * 2L, t["Times"][idxMapping[Pair(2, 1)]!!].value)
        assertEquals(3.0 / 2L, t["Div"][idxMapping[Pair(2, 1)]!!].value)
        assertEquals(3.0 % 2L, t["Rem"][idxMapping[Pair(2, 1)]!!].value)

        assertEquals(3.0 + 2L, t["Plus"][idxMapping[Pair(3, 1)]!!].value)
        assertEquals(3.0 - 2L, t["Minus"][idxMapping[Pair(3, 1)]!!].value)
        assertEquals(3.0 * 2L, t["Times"][idxMapping[Pair(3, 1)]!!].value)
        assertEquals(3.0 / 2L, t["Div"][idxMapping[Pair(3, 1)]!!].value)
        assertEquals(3.0 % 2L, t["Rem"][idxMapping[Pair(3, 1)]!!].value)

        assertEquals(BigInteger.TWO + BigInteger.TWO, t["Plus"][idxMapping[Pair(4, 1)]!!].value)
        assertEquals(BigInteger.TWO - BigInteger.TWO, t["Minus"][idxMapping[Pair(4, 1)]!!].value)
        assertEquals(BigInteger.TWO * BigInteger.TWO, t["Times"][idxMapping[Pair(4, 1)]!!].value)
        assertEquals(BigInteger.TWO / BigInteger.TWO, t["Div"][idxMapping[Pair(4, 1)]!!].value)
        assertEquals(BigInteger.TWO % BigInteger.TWO, t["Rem"][idxMapping[Pair(4, 1)]!!].value)

        assertEquals(BigDecimal.TEN + BigDecimal.valueOf(2), t["Plus"][idxMapping[Pair(5, 1)]!!].value)
        assertEquals(BigDecimal.TEN - BigDecimal.valueOf(2), t["Minus"][idxMapping[Pair(5, 1)]!!].value)
        assertEquals(BigDecimal.TEN * BigDecimal.valueOf(2), t["Times"][idxMapping[Pair(5, 1)]!!].value)
        assertEquals(BigDecimal.TEN / BigDecimal.valueOf(2), t["Div"][idxMapping[Pair(5, 1)]!!].value)
        assertEquals(BigDecimal.TEN % BigDecimal.valueOf(2), t["Rem"][idxMapping[Pair(5, 1)]!!].value)
    }

    @Test
    fun tableCloneValues() {
        val t1 = newTable("tableClone1")

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r] = "$c$r A1"
            }
        }

        val t2 = t1.clone("tableClone2")

        for (c in listOf("A", "B", "C")) {
            for (r in 1..100) {
                t2[c][r] = "$c$r B1"
            }
        }

        for (c in listOf("D")) {
            for (r in 1..100) {
                t1[c][r] = "$c$r A2"
            }
        }

        for (c in listOf("A", "B", "C")) {
            for (r in 1..100) {
                assertEquals("$c$r A1", t1[c][r].value)
            }
        }

        for (c in listOf("A", "B", "C")) {
            for (r in 1..100) {
                assertEquals("$c$r B1", t2[c][r].value)
            }
        }

        for (c in listOf("D")) {
            for (r in 1..100) {
                assertEquals("$c$r A2", t1[c][r].value)
            }
        }

        for (c in listOf("D")) {
            for (r in 1..100) {
                assertEquals("$c$r A1", t2[c][r].value)
            }
        }
    }

    @Test
    fun tableCloneEvents() {
        val t1 = newTable("tableCloneEvents")

        var t1EventCount = 0
        var t2EventCount = 0

        t1.onAny {
            events {
                t1EventCount += count()
            }
        }

        var expectedT1EventCount = 0

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r] = "$c$r A1"
                expectedT1EventCount++
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r] = "$c$r A1"
                expectedT1EventCount++
            }
        }

        val t2 = t1.clone("tableClone2")

        // We divide by 2 because we overwrite cells above,
        // but when adding a listener we only reply current values
        var expectedT2EventCount = expectedT1EventCount / 2

        t2.onAny {
            events {
                t2EventCount += count()
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r] = "$c$r A2"
                expectedT1EventCount++
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t2[c][r] = "$c$r B1"
                expectedT2EventCount++
            }
        }

        assertEquals(expectedT1EventCount, t1EventCount)
        assertEquals(expectedT2EventCount, t2EventCount)
        assertTrue(expectedT1EventCount > expectedT2EventCount)
        assertTrue(expectedT2EventCount > 0)
    }

    @Test
    fun tableEventSnapshots() {
        val t = newTable("tableEventSnapshots")

        t["A", 1] = 1

        var change: Number = 0

        t.onAny {
            events {
                change = newTable["A", 1] - oldTable["A", 1]
            }
        }

        t["A", 1] = 2
        assertEquals(1L, change)

        t["A", 1] = 4
        assertEquals(2L, change)
    }

    // TODO: Listener ordering

    // TODO: Table compareTo/contains
}