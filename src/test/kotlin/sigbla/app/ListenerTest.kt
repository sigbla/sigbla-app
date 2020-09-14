package sigbla.app

import org.junit.After
import org.junit.Test
import sigbla.app.exceptions.ListenerLoopException
import sigbla.app.internals.Registry
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ListenerTest {
    @After
    fun cleanup() {
        Registry.tableNames().forEach { Registry.deleteTable(it) }
    }

    // TODO Ensure we test this subscribe and unsubscribe test below on all onAny functions
    @Test
    fun subscribeAndUnsubscribe() {
        val t1 = Table.newTable("subscribeAndUnsubscribe")

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
    fun subscribeFilledTableAndUnsubscribe() {
        val t1 = Table.newTable("subscribeAndUnsubscribe")

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
    fun subscribeAndInstantUnsubscribe() {
        val t1 = Table.newTable("subscribeAndInstantUnsubscribe")

        var eventCount = 0

        t1.onAny {
            this.reference.unsubscribe()

            events {
                eventCount += count()
            }
        }

        assertEquals(0, eventCount)

        t1["A", 1] = "A"

        assertEquals(0, eventCount)
    }

    @Test
    fun subscribeFilledTableAndInstantUnsubscribe() {
        val t1 = Table.newTable("subscribeFilledTableAndInstantUnsubscribe")

        var eventCount = 0

        t1["A", 1] = "A"

        t1.onAny {
            this.reference.unsubscribe()

            events {
                eventCount += count()
            }
        }

        assertEquals(1, eventCount)

        t1["A", 1] = "B"

        assertEquals(1, eventCount)
    }

    @Test
    fun listenerRefWithNameOrder() {
        val t = Table.newTable("listenerRefWithNameOrder")
        val ref = t.onAny {
            name = "Name A"
            order = 123
        }

        assertEquals("Name A", ref.name)
        assertEquals(123L, ref.order)

        ref.unsubscribe()
    }

    @Test
    fun listenerRefWithoutNameOrder() {
        val t = Table.newTable("listenerRefWithoutNameOrder")
        val ref = t.onAny {}

        assertNull(ref.name)
        assertEquals(0L, ref.order)

        ref.unsubscribe()
    }

    @Test
    fun listenerLoop() {
        val t = Table.newTable("listenerLoop")

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

    // TODO Test type filters cases like on<A, B> etc..
}