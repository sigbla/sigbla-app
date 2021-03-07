package sigbla.app.test

import sigbla.app.*
import sigbla.app.exceptions.ListenerLoopException
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TableListenerTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    // TODO Ensure we test this subscribe and unsubscribe test below on all onAny functions
    @Test
    fun `subscribe and unsubscribe`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        val ref = on(t1) {
            events {
                eventCount += count()
            }
        }

        t1["A", 1] = "A"

        assertEquals(1, eventCount)

        t1["A", 1] = "B"

        assertEquals(2, eventCount)

        t1["A", 2] = "C"
        t1["B", 3] = "D"

        assertEquals(4, eventCount)

        off(ref)

        t1["B", 3] = "E"
        t1["C", 4] = "F"

        assertEquals(4, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        t1["A", 1] = "A"

        val ref = on(t1) {
            events {
                eventCount += count()
            }
        }

        assertEquals(1, eventCount)

        t1["A", 1] = "B"

        assertEquals(2, eventCount)

        t1["A", 2] = "C"
        t1["B", 3] = "D"

        assertEquals(4, eventCount)

        off(ref)

        t1["B", 3] = "E"
        t1["C", 4] = "F"

        assertEquals(4, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        on(t1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(0, eventCount)

        t1["A", 1] = "A"

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        t1["A", 1] = "A"

        on(t1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(1, eventCount)

        t1["A", 1] = "B"

        assertEquals(1, eventCount)
    }

    @Test
    fun `listener ref with name and order`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]
        val ref = on(t) {
            name = "Name A"
            order = 123
        }

        assertEquals("Name A", ref.name)
        assertEquals(123L, ref.order)

        off(ref)
    }

    @Test
    fun `listener ref without name and order`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]
        val ref = on(t) {}

        assertNull(ref.name)
        assertEquals(0L, ref.order)

        off(ref)
    }

    @Test
    fun `listener loop support`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        val ref1 = on(t) {
            events {
                t["A", 0] = 1
            }
        }

        assertFailsWith(ListenerLoopException::class) {
            t["A", 0] = 0
        }

        off(ref1)

        val ref2 = on(t) {
            allowLoop = true

            events {
                forEach { _ ->
                    if (t["A", 1].isNumeric() && valueOf<Number>(t["A", 1])?.toLong() ?: 1000 < 1000)
                        t["A", 1] = t["A", 1] + 1
                }
            }
        }

        t["A", 1] = 0

        assertEquals(1000L, valueOf<Long>(t["A", 1]))

        off(ref2)
    }

    @Test
    fun `table clone and events`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var t1EventCount = 0
        var t2EventCount = 0

        on(t1) {
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

        val t2 = clone(t1, "tableClone2")

        // We divide by 2 because we overwrite cells above,
        // but when adding a listener we only reply current values
        var expectedT2EventCount = expectedT1EventCount / 2

        on(t2) {
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

        Assert.assertEquals(expectedT1EventCount, t1EventCount)
        Assert.assertEquals(expectedT2EventCount, t2EventCount)
        Assert.assertTrue(expectedT1EventCount > expectedT2EventCount)
        Assert.assertTrue(expectedT2EventCount > 0)
    }

    @Test
    fun `table events with old and new snapshots`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 1] = 1

        var change: Number = 0

        on(t) {
            events {
                change = newTable["A", 1] - oldTable["A", 1]
            }
        }

        t["A", 1] = 2
        Assert.assertEquals(1L, change)

        t["A", 1] = 4
        Assert.assertEquals(2L, change)
    }

    @Test
    fun `listener ordering`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        val generator = AtomicInteger()

        var id1: Int? = null
        var id2: Int? = null
        var id3: Int? = null

        on(t) {
            order = 3
            skipHistory = true

            events {
                if (any() && id1 == null) {
                    id1 = generator.getAndIncrement()
                }
            }
        }

        on(t) {
            order = 2
            skipHistory = true

            events {
                if (any() && id2 == null) {
                    id2 = generator.getAndIncrement()
                }
            }
        }

        on(t) {
            order = 1
            skipHistory = true

            events {
                if (any() && id3 == null) {
                    id3 = generator.getAndIncrement()
                }
            }
        }

        assertNull(id1)
        assertNull(id2)
        assertNull(id3)

        t["A", 0] = 0

        assertTrue(id1 ?: Int.MIN_VALUE > id2 ?: Int.MAX_VALUE)
        assertTrue(id2 ?: Int.MIN_VALUE > id3 ?: Int.MAX_VALUE)
    }

    @Test
    fun `listener order difference propagation`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        var v1Old: Any? = null
        var v2Old: Any? = null
        var v3Old: Any? = null

        var v1New: Any? = null
        var v2New: Any? = null
        var v3New: Any? = null

        on(t) {
            order = 2

            events {
                v2Old = valueOf<Any>(oldTable["A", 0])
                v2New = valueOf<Any>(newTable["A", 0])

                assertEquals(t["A", 0], source["A", 0])

                if (newTable["A", 0].isNumeric())
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric())
                    oldTable["A", 0] = oldTable["A", 0] - 1
            }
        }

        on(t) {
            order = 3

            events {
                v3Old = valueOf<Any>(oldTable["A", 0])
                v3New = valueOf<Any>(newTable["A", 0])

                assertEquals(t["A", 0], source["A", 0])

                if (newTable["A", 0].isNumeric())
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric())
                    oldTable["A", 0] = oldTable["A", 0] - 1
            }
        }

        on(t) {
            order = 1

            events {
                v1Old = valueOf<Any>(oldTable["A", 0])
                v1New = valueOf<Any>(newTable["A", 0])

                assertEquals(t["A", 0], source["A", 0])

                if (newTable["A", 0].isNumeric())
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric())
                    oldTable["A", 0] = oldTable["A", 0] - 1
            }
        }

        assertNull(v1New)
        assertNull(v1Old)

        assertNull(v2New)
        assertNull(v2Old)

        assertNull(v3New)
        assertNull(v3Old)

        t["A", 0] = 100

        assertEquals(Unit, v1Old)
        assertEquals(Unit, v2Old)
        assertEquals(Unit, v3Old)

        assertEquals(100L, v1New)
        assertEquals(101L, v2New)
        assertEquals(102L, v3New)

        assertTrue(100L in t["A", 0])

        t["A", 0] = 200

        assertEquals(100L, v1Old)
        assertEquals(99L, v2Old)
        assertEquals(98L, v3Old)

        assertEquals(200L, v1New)
        assertEquals(201L, v2New)
        assertEquals(202L, v3New)

        assertTrue(200L in t["A", 0])
    }

    // TODO Test type filters cases like on<A, B> etc..

}