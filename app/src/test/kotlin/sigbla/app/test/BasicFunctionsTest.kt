/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.AfterClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BasicFunctionsTest {
    @Test
    fun `sum with defaults`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = 100
        t["A", 1] = 200

        t["Sum", 0] = sum(t["A", 0]..t["B", 10])

        assertTrue(300 in t["Sum", 0])

        t["A", 0] = 300

        assertTrue(500 in t["Sum", 0])

        t["B", 10] = 1

        assertTrue(501 in t["Sum", 0])

        t["Sum", 0] = Unit

        assertTrue(t["Sum", 0] is UnitCell)

        t["A", 0] = 200

        assertTrue(t["Sum", 0] is UnitCell)
    }

    @Test
    fun `sum with predicate`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = 100
        t["A", 1] = 200

        t["Sum", 0] = sum(t["A", 0]..t["B", 10]) { it > 100 }

        assertTrue(200 in t["Sum", 0])

        t["A", 0] = 300

        assertTrue(500 in t["Sum", 0])

        t["B", 10] = 1

        assertTrue(500 in t["Sum", 0])

        t["Sum", 0] = Unit

        assertTrue(t["Sum", 0] is UnitCell)

        t["A", 0] = 200

        assertTrue(t["Sum", 0] is UnitCell)
    }

    @Test
    fun `sum with init and empty`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["Sum", 0] = sum(t["A"], init = 333, empty = 0)

        assertTrue(333 in t["Sum", 0])

        t["A", 0] = 100
        t["A", 1] = 200

        assertTrue(300 in t["Sum", 0])

        clear(t["A"])

        assertTrue(0L in t["Sum", 0])
    }

    @Test
    fun `sum with valueOf`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = 100
        t["A", 1] = 200

        assertEquals(300L, valueOf<Long>(sum(t["A", 0]..t["A", 1])))
    }

    @Test
    fun `max with defaults`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["B", 0] = 100
        t["A", 1] = 200

        t["Max", 0] = max(t["A", 0]..t["B", 10])

        assertTrue(200 in t["Max", 0])

        t["B", 0] = 300

        assertTrue(300 in t["Max", 0])

        t["B", 10] = 1

        assertTrue(300 in t["Max", 0])

        t["Max", 0] = Unit

        assertTrue(t["Max", 0] is UnitCell)

        t["A", 0] = 10000

        assertTrue(t["Max", 0] is UnitCell)
    }

    @Test
    fun `max with predicate`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["B", 0] = 100
        t["A", 1] = 200

        t["Max", 0] = max(t["A", 0]..t["B", 10]) { it < 300 }

        assertTrue(200 in t["Max", 0])

        t["B", 0] = 300

        assertTrue(200 in t["Max", 0])

        t["B", 10] = 1

        assertTrue(200 in t["Max", 0])

        t["Max", 0] = Unit

        assertTrue(t["Max", 0] is UnitCell)

        t["A", 0] = 10000

        assertTrue(t["Max", 0] is UnitCell)
    }

    @Test
    fun `max with init and empty`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["Max", 0] = max(t["A"], init = 333, empty = 0)

        assertTrue(333 in t["Max", 0])

        t["A", 0] = 100
        t["A", 1] = 200

        assertTrue(200 in t["Max", 0])

        clear(t["A"])

        assertTrue(0L in t["Max", 0])
    }


    @Test
    fun `max with valueOf`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = 100
        t["A", 1] = 200

        assertEquals(200L, valueOf<Long>(max(t["A", 0]..t["A", 1])))
    }

    @Test
    fun `min with defaults`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["B", 0] = 200
        t["A", 1] = 100

        t["Min", 0] = min(t["A", 0]..t["B", 10])

        assertTrue(100 in t["Min", 0])

        t["B", 0] = 50

        assertTrue(50 in t["Min", 0])

        t["B", 10] = 300

        assertTrue(50 in t["Min", 0])

        t["Min", 0] = Unit

        assertTrue(t["Min", 0] is UnitCell)

        t["A", 0] = 0

        assertTrue(t["Min", 0] is UnitCell)
    }

    @Test
    fun `min with predicate`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["B", 0] = 200
        t["A", 1] = 100

        t["Min", 0] = min(t["A", 0]..t["B", 10]) { it > 100 }

        assertTrue(200 in t["Min", 0])

        t["B", 0] = 50

        assertTrue(t["Min", 0] is UnitCell)

        t["B", 10] = 150

        assertTrue(150 in t["Min", 0])

        t["Min", 0] = Unit

        assertTrue(t["Min", 0] is UnitCell)

        t["A", 0] = 0

        assertTrue(t["Min", 0] is UnitCell)
    }

    @Test
    fun `min with init and empty`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["Min", 0] = min(t["A"], init = 333, empty = 0)

        assertTrue(333 in t["Min", 0])

        t["A", 0] = 100
        t["A", 1] = 200

        assertTrue(100 in t["Min", 0])

        clear(t["A"])

        assertTrue(0L in t["Min", 0])
    }

    @Test
    fun `min with valueOf`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = 100
        t["A", 1] = 200

        assertEquals(100L, valueOf<Long>(min(t["A", 0]..t["A", 1])))
    }

    @Test
    fun `count with defaults`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["B", 0] = 200
        t["A", 1] = 100

        t["Count", 0] = count(t["A", 0]..t["B", 10])

        assertTrue(2 in t["Count", 0])

        t["B", 0] = 50

        assertTrue(2 in t["Count", 0])

        t["B", 10] = 300

        assertTrue(3 in t["Count", 0])

        t["Count", 0] = Unit

        assertTrue(t["Count", 0] is UnitCell)

        t["A", 0] = 0

        assertTrue(t["Count", 0] is UnitCell)
    }

    @Test
    fun `count with predicate`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["B", 0] = 200
        t["A", 1] = 100

        t["Count", 0] = count(t["A", 0]..t["B", 10]) { it.isText }

        assertTrue(0 in t["Count", 0])

        t["B", 0] = "50"

        assertTrue(1 in t["Count", 0])

        t["B", 10] = "300"

        assertTrue(2 in t["Count", 0])

        t["Count", 0] = Unit

        assertTrue(t["Count", 0] is UnitCell)

        t["A", 0] = 0

        assertTrue(t["Count", 0] is UnitCell)
    }

    @Test
    fun `count with init and empty`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["Count", 0] = count(t["A"], init = 333, empty = 0)

        assertTrue(333 in t["Count", 0])

        t["A", 0] = 100
        t["A", 1] = 200

        assertTrue(2 in t["Count", 0])

        clear(t["A"])

        assertTrue(0L in t["Count", 0])
    }

    @Test
    fun `count with valueOf`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = 100
        t["A", 1] = 200

        assertEquals(2L, valueOf<Long>(count(t["A", 0]..t["A", 1])))
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}
