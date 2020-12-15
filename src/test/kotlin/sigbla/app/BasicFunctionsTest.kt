package sigbla.app

import org.junit.After
import org.junit.Test
import kotlin.test.assertTrue

class BasicFunctionsTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `sum`() {
        val t = Table["sum"]

        t["A", 0] = 100
        t["A", 1] = 200

        t["Sum", 0] = sum(t["A", 0]..t["B", 10])

        assertTrue(300 in t["Sum", 0])

        t["A", 0] = 300

        assertTrue(500 in t["Sum", 0])

        t["B", 10] = 1

        assertTrue(501 in t["Sum", 0])

        t["Sum", 0] = null

        assertTrue(t["Sum", 0] is UnitCell)

        t["A", 0] = 200

        assertTrue(t["Sum", 0] is UnitCell)
    }

    @Test
    fun `max`() {
        val t = Table["max"]

        t["B", 0] = 100
        t["A", 1] = 200

        t["Max", 0] = max(t["A", 0]..t["B", 10])

        assertTrue(200 in t["Max", 0])

        t["B", 0] = 300

        assertTrue(300 in t["Max", 0])

        t["B", 10] = 1

        assertTrue(300 in t["Max", 0])

        t["Max", 0] = null

        assertTrue(t["Max", 0] is UnitCell)

        t["A", 0] = 10000

        assertTrue(t["Max", 0] is UnitCell)
    }

    @Test
    fun `min`() {
        val t = Table["min"]

        t["B", 0] = 200
        t["A", 1] = 100

        t["Min", 0] = min(t["A", 0]..t["B", 10])

        assertTrue(100 in t["Min", 0])

        t["B", 0] = 50

        assertTrue(50 in t["Min", 0])

        t["B", 10] = 300

        assertTrue(50 in t["Min", 0])

        t["Min", 0] = null

        assertTrue(t["Min", 0] is UnitCell)

        t["A", 0] = 0

        assertTrue(t["Min", 0] is UnitCell)
    }
}