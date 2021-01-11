package sigbla.app.test

import org.junit.After
import org.junit.Assert
import org.junit.Test
import sigbla.app.Table
import sigbla.app.exceptions.ListenerLoopException
import sigbla.app.newTable
import sigbla.app.oldTable
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
        val t1 = Table["subscribeAndUnsubscribe"]

        var eventCount = 0

        val ref = t1.onAny {
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

        ref.unsubscribe()

        t1["B", 3] = "E"
        t1["C", 4] = "F"

        assertEquals(4, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe`() {
        val t1 = Table["subscribeAndUnsubscribe"]

        var eventCount = 0

        t1["A", 1] = "A"

        val ref = t1.onAny {
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

        ref.unsubscribe()

        t1["B", 3] = "E"
        t1["C", 4] = "F"

        assertEquals(4, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe`() {
        val t1 = Table["subscribeAndInstantUnsubscribe"]

        var eventCount = 0

        t1.onAny {
            reference.unsubscribe()

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
        val t1 = Table["subscribeFilledTableAndInstantUnsubscribe"]

        var eventCount = 0

        t1["A", 1] = "A"

        t1.onAny {
            reference.unsubscribe()

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
        val t = Table["listenerRefWithNameOrder"]
        val ref = t.onAny {
            name = "Name A"
            order = 123
        }

        assertEquals("Name A", ref.name)
        assertEquals(123L, ref.order)

        ref.unsubscribe()
    }

    @Test
    fun `listener ref without name and order`() {
        val t = Table["listenerRefWithoutNameOrder"]
        val ref = t.onAny {}

        assertNull(ref.name)
        assertEquals(0L, ref.order)

        ref.unsubscribe()
    }

    @Test
    fun `listener loop support`() {
        val t = Table["listenerLoop"]

        val ref1 = t.onAny {
            events {
                t["A", 0] = 1
            }
        }

        assertFailsWith(ListenerLoopException::class) {
            t["A", 0] = 0
        }

        ref1.unsubscribe()

        val ref2 = t.onAny {
            allowLoop = true

            events {
                forEach { _ ->
                    if (t["A", 1].isNumeric() && (t["A", 1].value as Number).toLong() < 1000)
                        t["A", 1] = t["A", 1] + 1
                }
            }
        }

        t["A", 1] = 0

        assertEquals(1000L, t["A", 1].value)

        ref2.unsubscribe()
    }

    @Test
    fun `table clone and events`() {
        val t1 = Table["tableCloneEvents"]

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

        Assert.assertEquals(expectedT1EventCount, t1EventCount)
        Assert.assertEquals(expectedT2EventCount, t2EventCount)
        Assert.assertTrue(expectedT1EventCount > expectedT2EventCount)
        Assert.assertTrue(expectedT2EventCount > 0)
    }

    @Test
    fun `table events with old and new snapshots`() {
        val t = Table["tableEventSnapshots"]

        t["A", 1] = 1

        var change: Number = 0

        t.onAny {
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
        val t = Table["listenerOrdering"]

        val generator = AtomicInteger()

        var id1: Int? = null
        var id2: Int? = null
        var id3: Int? = null

        t.onAny {
            order = 3
            skipHistory = true

            events {
                if (any() && id1 == null) {
                    id1 = generator.getAndIncrement()
                }
            }
        }

        t.onAny {
            order = 2
            skipHistory = true

            events {
                if (any() && id2 == null) {
                    id2 = generator.getAndIncrement()
                }
            }
        }

        t.onAny {
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
        val t = Table["listerOrderDiffPropagation"]

        var v1Old: Any? = null
        var v2Old: Any? = null
        var v3Old: Any? = null

        var v1New: Any? = null
        var v2New: Any? = null
        var v3New: Any? = null

        t.onAny {
            order = 2

            events {
                v2Old = oldTable["A", 0].value
                v2New = newTable["A", 0].value

                assertEquals(t["A", 0], source["A", 0])

                if (newTable["A", 0].isNumeric())
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric())
                    oldTable["A", 0] = oldTable["A", 0] - 1
            }
        }

        t.onAny {
            order = 3

            events {
                v3Old = oldTable["A", 0].value
                v3New = newTable["A", 0].value

                assertEquals(t["A", 0], source["A", 0])

                if (newTable["A", 0].isNumeric())
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric())
                    oldTable["A", 0] = oldTable["A", 0] - 1
            }
        }

        t.onAny {
            order = 1

            events {
                v1Old = oldTable["A", 0].value
                v1New = newTable["A", 0].value

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