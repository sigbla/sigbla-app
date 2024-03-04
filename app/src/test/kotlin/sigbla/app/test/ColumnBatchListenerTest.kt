/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import sigbla.app.exceptions.InvalidCellException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ColumnBatchListenerTest {
    // TODO Ensure we test this subscribe and unsubscribe test below on all onAny functions
    @Test
    fun `subscribe and unsubscribe`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        val ref = on(t1["A"]) {
            events {
                eventCount += count()
            }
        }

        batch(t1) {
            t1["A", 1] = "A"

            assertEquals(0, eventCount)

            t1["A", 1] = "B"

            assertEquals(0, eventCount)

            t1["A", 2] = "C"
            t1["B", 3] = "D"

            assertEquals(0, eventCount)

            off(ref)

            t1["A", 3] = "E"
            t1["C", 4] = "F"

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        t1["A", 1] = "A"

        val ref = on(t1["A"]) {
            events {
                eventCount += count()
            }
        }

        batch(t1) {
            assertEquals(1, eventCount)

            t1["A", 1] = "B"

            assertEquals(1, eventCount)

            t1["A", 2] = "C"
            t1["B", 3] = "D"

            assertEquals(1, eventCount)

            off(ref)

            t1["A", 3] = "E"
            t1["C", 4] = "F"

            assertEquals(1, eventCount)
        }

        assertEquals(1, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(t1) {
            on(t1["A"]) {
                off(this)

                events {
                    eventCount += count()
                }
            }

            assertEquals(0, eventCount)

            t1["A", 1] = "A"

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        t1["A", 1] = "A"

        batch(t1) {
            on(t1["A"]) {
                off(this)

                events {
                    eventCount += count()
                }
            }

            assertEquals(1, eventCount)

            t1["A", 1] = "B"

            assertEquals(1, eventCount)
        }

        assertEquals(1, eventCount)
    }

    @Test
    fun `listener ref with name and order`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        batch(t) {
            val ref = on(t["A"]) {
                name = "Name A"
                order = 123
            }

            assertEquals("Name A", ref.name)
            assertEquals(123L, ref.order)

            off(ref)
        }
    }

    @Test
    fun `listener ref without name and order`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        batch(t) {
            val ref = on(t["A"]) {}

            assertNull(ref.name)
            assertEquals(0L, ref.order)

            off(ref)
        }
    }

    @Test
    fun `listener loop support`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ref2 = batch(t) {
            val ref1 = on(t["A"]) {
                events {
                    t["A", 0] = 1
                }
            }

            // No exception expected from this as event not yet produced
            t["A", 0] = 0

            off(ref1)

            t["A", 0] = null

            val ref2 = on(t["A"]) {
                allowLoop = true

                events {
                    forEach { _ ->
                        if (t["A", 1].isNumeric && valueOf<Number>(t["A", 1])?.toLong() ?: 1000 < 1000)
                            t["A", 1] = t["A", 1] + 1
                    }
                }
            }

            t["A", 1] = 0

            return@batch ref2
        }

        assertEquals(1000L, valueOf<Long>(t["A", 1]))

        off(ref2)
    }

    @Test
    fun `table clone and events`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var t1EventCount = 0
        var t2EventCount = 0

        on(t1["A"]) {
            events {
                t1EventCount += count()
            }
        }

        var expectedT1EventCount = 0

        val t2 = batch(t1) {
            for (c in listOf("A", "B", "C", "D")) {
                for (r in 1..100) {
                    t1[c][r] = "$c$r A1"
                    // these will be overwritten if (c == "A") expectedT1EventCount++
                }
            }

            for (c in listOf("A", "B", "C", "D")) {
                for (r in 1..100) {
                    t1[c][r] = "$c$r A1"
                    if (c == "A") expectedT1EventCount++
                }
            }

            return@batch clone(t1, "tableClone2")
        }

        var expectedT2EventCount = expectedT1EventCount

        on(t2["A"]) {
            events {
                t2EventCount += count()
            }
        }

        // Testing event separation between t1/t2
        t1["A", 1] = t1["A", 1]
        expectedT1EventCount++

        batch(t1) {
            batch(t2) {
                for (c in listOf("A", "B", "C", "D")) {
                    for (r in 1..100) {
                        t1[c][r] = "$c$r A2"
                        if (c == "A") expectedT1EventCount++
                    }
                }

                for (c in listOf("A", "B", "C", "D")) {
                    for (r in 1..100) {
                        t2[c][r] = "$c$r B1"
                        if (c == "A") expectedT2EventCount++
                    }
                }
            }
        }

        Assert.assertEquals(expectedT1EventCount, t1EventCount)
        Assert.assertEquals(expectedT2EventCount, t2EventCount)
        Assert.assertTrue(expectedT1EventCount > expectedT2EventCount)
        Assert.assertTrue(expectedT2EventCount > 0)
    }

    @Test
    fun `table events with old and new snapshots`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = 1

        var change: Number = 0

        on(t["A"]) {
            skipHistory = true

            events {
                change = newTable["A", 1] - oldTable["A", 1]
            }
        }

        batch(t) {
            t["A", 1] = 2
            t["A", 1] = 4
        }

        Assert.assertEquals(3L, change)
    }

    @Test
    fun `recursive batching`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 1] = 1

        var change: Number = 0

        on(t["A"]) {
            skipHistory = true

            events {
                change = newTable["A", 1] - oldTable["A", 1]
            }
        }

        batch(t) {
            t["A", 1] = 2

            batch(t) {
                t["A", 1] = 4
            }
        }

        Assert.assertEquals(3L, change)
    }

    @Test
    fun `listener ordering`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val generator = AtomicInteger()

        var id1: Int? = null
        var id2: Int? = null
        var id3: Int? = null

        batch(t) {
            on(t["A"]) {
                order = 3
                skipHistory = true

                events {
                    if (any() && id1 == null) {
                        id1 = generator.getAndIncrement()
                    }
                }
            }

            on(t["A"]) {
                order = 2
                skipHistory = true

                events {
                    if (any() && id2 == null) {
                        id2 = generator.getAndIncrement()
                    }
                }
            }

            on(t["A"]) {
                order = 1
                skipHistory = true

                events {
                    if (any() && id3 == null) {
                        id3 = generator.getAndIncrement()
                    }
                }
            }

            t["A", 0] = 0

            assertNull(id1)
            assertNull(id2)
            assertNull(id3)
        }

        assertTrue(id1 ?: Int.MIN_VALUE > id2 ?: Int.MAX_VALUE)
        assertTrue(id2 ?: Int.MIN_VALUE > id3 ?: Int.MAX_VALUE)
    }

    @Test
    fun `listener order difference propagation`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var v1Old: Any? = null
        var v2Old: Any? = null
        var v3Old: Any? = null

        var v1New: Any? = null
        var v2New: Any? = null
        var v3New: Any? = null

        batch(t) {
            on(t["A"]) {
                order = 2

                events {
                    v2Old = valueOf<Any>(oldTable["A", 0])
                    v2New = valueOf<Any>(newTable["A", 0])

                    assertEquals(t["A", 0], source[0])

                    if (newTable["A", 0].isNumeric)
                        newTable["A", 0] = newTable["A", 0] + 1
                    if (oldTable["A", 0].isNumeric)
                        oldTable["A", 0] = oldTable["A", 0] - 1
                }
            }

            on(t["A"]) {
                order = 3

                events {
                    v3Old = valueOf<Any>(oldTable["A", 0])
                    v3New = valueOf<Any>(newTable["A", 0])

                    assertEquals(t["A", 0], source[0])

                    if (newTable["A", 0].isNumeric)
                        newTable["A", 0] = newTable["A", 0] + 1
                    if (oldTable["A", 0].isNumeric)
                        oldTable["A", 0] = oldTable["A", 0] - 1
                }
            }

            on(t["A"]) {
                order = 1

                events {
                    v1Old = valueOf<Any>(oldTable["A", 0])
                    v1New = valueOf<Any>(newTable["A", 0])

                    assertEquals(t["A", 0], source[0])

                    if (newTable["A", 0].isNumeric)
                        newTable["A", 0] = newTable["A", 0] + 1
                    if (oldTable["A", 0].isNumeric)
                        oldTable["A", 0] = oldTable["A", 0] - 1
                }
            }

            t["A", 0] = 100

            assertNull(v1New)
            assertNull(v1Old)

            assertNull(v2New)
            assertNull(v2Old)

            assertNull(v3New)
            assertNull(v3Old)
        }

        batch(t) {
            assertTrue(100L in t["A", 0])

            t["A", 0] = 200

            assertEquals(Unit, v1Old)
            assertEquals(Unit, v2Old)
            assertEquals(Unit, v3Old)

            assertEquals(100L, v1New)
            assertEquals(101L, v2New)
            assertEquals(102L, v3New)
        }

        assertEquals(100L, v1Old)
        assertEquals(99L, v2Old)
        assertEquals(98L, v3Old)

        assertEquals(200L, v1New)
        assertEquals(201L, v2New)
        assertEquals(202L, v3New)

        assertTrue(200L in t["A", 0])
    }

    @Test
    fun `old table is an empty clone of source table on first pass`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = 100

        var count = 0

        batch(t) {
            on(t["A"]) {
                events {
                    assertEquals(0, oldTable.iterator().asSequence().count())
                    assertEquals(1, newTable.iterator().asSequence().count())
                    count += count()
                }

                off(this)
            }

            assertEquals(1, count)

            on(t["A"]) {
                skipHistory = true

                events {
                    assertEquals(1, oldTable.iterator().asSequence().count())
                    assertEquals(1, newTable.iterator().asSequence().count())
                    count += count()
                }
            }

            t["A", 0] = 200
        }

        assertEquals(2, count)
    }

    @Test
    fun `old and new table is a clone of source table`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0] = 100

        var count = 0

        batch(t) {
            on(t["A"]) {
                events {
                    oldTable["A", 0] = source[0] + 200
                    newTable["A", 0] = source[0] + 300

                    assertEquals<Any>(source[0] + 200, (oldTable["A", 0].asLong ?: throw InvalidCellException("")))
                    assertEquals<Any>(source[0] + 300, (newTable["A", 0].asLong ?: throw InvalidCellException("")))

                    count += count()
                }
            }

            // The second listener is executed after the first listener, and its
            // old/new table should reflect changes introduced by the first listener.
            on(t["A"]) {
                skipHistory = true

                events {
                    assertEquals<Any>(source[0] + 200, (oldTable["A", 0].asLong ?: throw InvalidCellException("")))
                    assertEquals<Any>(source[0] + 300, (newTable["A", 0].asLong ?: throw InvalidCellException("")))

                    count += count()
                }
            }

            assertEquals<Any>(100L, (t["A", 0].asLong ?: throw InvalidCellException("")))

            t["A", 0] = 50
        }

        assertEquals<Any>(50L, (t["A", 0].asLong ?: throw InvalidCellException("")))

        assertEquals(3, count)
    }

    @Test
    fun `type filtering for subscriptions`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount1 = 0
        var eventCount2 = 0
        var eventCount3 = 0
        var eventCount4 = 0
        var eventCount5 = 0
        var eventCount6 = 0

        on<Any, String>(t1["A"]) events {
            eventCount1 += count()
        }

        on<Any, Long>(t1["A"]) events {
            eventCount2 += count()
        }

        on<String, Any>(t1["A"]) events {
            eventCount3 += count()
        }

        on<Long, Any>(t1["A"]) events {
            eventCount4 += count()
        }

        on<String, Long>(t1["A"]) events {
            eventCount5 += count()
        }

        on<Long, String>(t1["A"]) events {
            eventCount6 += count()
        }

        batch(t1) { t1["A", 0] = "String 1" }

        assertEquals(1, eventCount1)
        assertEquals(0, eventCount2)
        assertEquals(0, eventCount3)
        assertEquals(0, eventCount4)
        assertEquals(0, eventCount5)
        assertEquals(0, eventCount6)

        batch(t1) { t1["A", 1] = 100L }

        assertEquals(1, eventCount1)
        assertEquals(1, eventCount2)
        assertEquals(0, eventCount3)
        assertEquals(0, eventCount4)
        assertEquals(0, eventCount5)
        assertEquals(0, eventCount6)

        batch(t1) { t1["A", 0] = "String 2" }

        assertEquals(2, eventCount1)
        assertEquals(1, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(0, eventCount4)
        assertEquals(0, eventCount5)
        assertEquals(0, eventCount6)

        batch(t1) { t1["A", 1] = 200 } // Auto converted to Long

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(0, eventCount5)
        assertEquals(0, eventCount6)

        batch(t1) { t1["A", 0] = 300L }

        assertEquals(2, eventCount1)
        assertEquals(3, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(0, eventCount6)

        batch(t1) { t1["A", 1] = "String 3" }

        assertEquals(3, eventCount1)
        assertEquals(3, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(2, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(1, eventCount6)
    }

    @Test
    fun `type filtering for chained subscriptions`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount1 = 0
        var eventCount2 = 0

        on<Any, String>(t1["A"], name = "Listener 1") events {
            eventCount1 += count()
            forEach {
                oldTable[it.oldValue] { it.oldValue.value.toString().toLongOrNull() }
                newTable[it.newValue] = it.newValue.value.toLong()
            }
        }

        on<Long, Long>(t1["A"], name = "Listener 2") events {
            eventCount2 += count()
            forEach {
                t1["B", it.newValue.index] = it.newValue - it.oldValue
            }
        }

        batch(t1) {
            t1["A", 1] = "100"
            t1["A", 2] = "110"
        }

        batch(t1) {
            t1["A", 1] = "105"
            t1["A", 2] = "120"
        }

        assertEquals(4, eventCount1)
        assertEquals(2, eventCount2)

        assertEquals(5L, t1["B", 1].asLong)
        assertEquals(10L, t1["B", 2].asLong)
    }

    @Test
    fun `type filtering for chained subscriptions without type filter`() {
        val t1 = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount1 = 0
        var eventCount2 = 0

        on(t1["A"], name = "Listener 1") events {
            eventCount1 += count()
            forEach {
                oldTable[it.oldValue] = "L1 old"
                newTable[it.newValue] = "L1 new"
            }
        }

        on(t1["A"], name = "Listener 2") events {
            eventCount2 += count()
            forEach {
                assertEquals("L1 old", it.oldValue.value)
                assertEquals("L1 new", it.newValue.value)
            }
        }

        batch(t1) {
            t1["A", 1] = "Original value A1"
            t1["A", 2] = "Original value A2"
        }

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)

        assertEquals("Original value A1", t1["A", 1].value)
        assertEquals("Original value A2", t1["A", 2].value)
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.names.filter { it.startsWith(Companion::class.java.declaringClass.simpleName) }.forEach { Table.delete(it) }
        }
    }
}
