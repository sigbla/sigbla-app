/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import sigbla.app.exceptions.ListenerLoopException
import org.junit.After
import org.junit.Assert
import org.junit.Test
import sigbla.app.exceptions.InvalidCellException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CellsListenerTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    // TODO Ensure we test this subscribe and unsubscribe test below on all onAny functions
    @Test
    fun `subscribe and unsubscribe 1`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        val ref = on(t1["A", 1] or t1["A", 2] or t1["B", 3] or t1["C", 4]) {
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
    fun `subscribe and unsubscribe 2`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        val cells: List<Iterable<Cell<*>>> = listOf(t1["A", 1], t1["A", 2], t1["B", 3], t1["C", 4])
        val ref = on(Cells(cells)) {
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
    fun `expected duplicates on empty table`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        on(t1["A", 1] or t1["A"] or t1[1]) {
            events {
                eventCount += count()
            }
        }

        t1["A", 1] = "A"

        assertEquals(3, eventCount)

        t1["A", 1] = "B"

        assertEquals(6, eventCount)
    }

    @Test
    fun `expected duplicates on prefilled table`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        t1["A", 1] = "A"

        on(t1["A", 1] or t1["A"] or t1[1]) {
            events {
                eventCount += count()
            }
        }

        assertEquals(3, eventCount)

        t1["A", 1] = "B"

        assertEquals(6, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        t1["A", 1] = "A"

        val ref = on(t1["A", 1] or t1["A", 2] or t1["B", 3] or t1["C", 4]) {
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
    fun `subscribe and instant unsubscribe 1`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        on(t1["A", 1]) {
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
    fun `subscribe and instant unsubscribe 2`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        on(t1["A", 1] or t1["A", 0]) {
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
    fun `subscribe to filled table and instant unsubscribe 1`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        t1["A", 1] = "A"

        on(t1["A", 1]) {
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
    fun `subscribe to filled table and instant unsubscribe 2`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        t1["A", 1] = "A"

        on(t1["A", 1] or t1["A", 0]) {
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
    fun `listener ref with name and order 1`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]
        val ref = on(t["A", 0]) {
            name = "Name A"
            order = 123
        }

        assertEquals("Name A", ref.name)
        assertEquals(123L, ref.order)

        off(ref)
    }

    @Test
    fun `listener ref with name and order 2`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]
        val ref = on(t["A", 0] or t["A", 1]) {
            name = "Name A"
            order = 123
        }

        assertEquals("Name A", ref.name)
        assertEquals(123L, ref.order)

        off(ref)
    }

    @Test
    fun `listener ref without name and order 1`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]
        val ref = on(t["A", 1]) {}

        assertNull(ref.name)
        assertEquals(0L, ref.order)

        off(ref)
    }

    @Test
    fun `listener ref without name and order 2`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]
        val ref = on(t["A", 0] or t["A", 1]) {}

        assertNull(ref.name)
        assertEquals(0L, ref.order)

        off(ref)
    }

    @Test
    fun `listener loop support`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        val ref1 = on(t["A", 0] or t["A", 1]) {
            events {
                t["A", 0] = 1
            }
        }

        assertFailsWith(ListenerLoopException::class) {
            t["A", 0] = 0
        }

        off(ref1)

        t["A", 0] = null

        val ref2 = on(t["A", 1] or t["A", 0]) {
            allowLoop = true

            events {
                forEach { _ ->
                    if (t["A", 1].isNumeric && valueOf<Number>(t["A", 1])?.toLong() ?: 1000 < 1000)
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

        var cells1 = Cells(t1["A", 0])

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                cells1 = cells1 or t1[c][r]
            }
        }

        on(cells1) {
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

        var cells2 = Cells(t2["A", 0])

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                cells2 = cells2 or t2[c][r]
            }
        }

        // We divide by 2 because we overwrite cells above,
        // but when adding a listener we only reply current values
        var expectedT2EventCount = expectedT1EventCount / 2

        on(cells2) {
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
    fun `table events with old and new snapshots 1`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 1] = 1

        var change: Number = 0

        on(t["A", 1]) {
            skipHistory = true

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
    fun `table events with old and new snapshots 2`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 1] = 1

        var change: Number = 0

        on(t["A", 1] or t["A", -1]) {
            skipHistory = true

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
    fun `listener ordering 1`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        val generator = AtomicInteger()

        var id1: Int? = null
        var id2: Int? = null
        var id3: Int? = null

        on(t["A", 0]) {
            order = 3
            skipHistory = true

            events {
                if (any() && id1 == null) {
                    id1 = generator.getAndIncrement()
                }
            }
        }

        on(t["A", 0]) {
            order = 2
            skipHistory = true

            events {
                if (any() && id2 == null) {
                    id2 = generator.getAndIncrement()
                }
            }
        }

        on(t["A", 0]) {
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
    fun `listener ordering 2`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        val generator = AtomicInteger()

        var id1: Int? = null
        var id2: Int? = null
        var id3: Int? = null

        on(t["A", 0] or t["A", 1]) {
            order = 3
            skipHistory = true

            events {
                if (any() && id1 == null) {
                    id1 = generator.getAndIncrement()
                }
            }
        }

        on(t["A", 0] or t["A", 1]) {
            order = 2
            skipHistory = true

            events {
                if (any() && id2 == null) {
                    id2 = generator.getAndIncrement()
                }
            }
        }

        on(t["A", 0] or t["A", 1]) {
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
    fun `listener order difference propagation 1`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        var v1Old: Any? = null
        var v2Old: Any? = null
        var v3Old: Any? = null

        var v1New: Any? = null
        var v2New: Any? = null
        var v3New: Any? = null

        on(t["A", 0]) {
            order = 2

            events {
                v2Old = valueOf<Any>(oldTable["A", 0])
                v2New = valueOf<Any>(newTable["A", 0])

                assertEquals(t["A", 0], source.table["A", 0])

                if (newTable["A", 0].isNumeric)
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric)
                    oldTable["A", 0] = oldTable["A", 0] - 1
            }
        }

        on(t["A", 0]) {
            order = 3

            events {
                v3Old = valueOf<Any>(oldTable["A", 0])
                v3New = valueOf<Any>(newTable["A", 0])

                assertEquals(t["A", 0], source.table["A", 0])

                if (newTable["A", 0].isNumeric)
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric)
                    oldTable["A", 0] = oldTable["A", 0] - 1
            }
        }

        on(t["A", 0]) {
            order = 1

            events {
                v1Old = valueOf<Any>(oldTable["A", 0])
                v1New = valueOf<Any>(newTable["A", 0])

                assertEquals(t["A", 0], source.table["A", 0])

                if (newTable["A", 0].isNumeric)
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric)
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

    @Test
    fun `listener order difference propagation 2`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        var v1Old: Any? = null
        var v2Old: Any? = null
        var v3Old: Any? = null

        var v1New: Any? = null
        var v2New: Any? = null
        var v3New: Any? = null

        on(t["A", 0] or t["A", 1]) {
            order = 2

            events {
                v2Old = valueOf<Any>(oldTable["A", 0])
                v2New = valueOf<Any>(newTable["A", 0])

                assertEquals(t["A", 0], source.table["A", 0])

                if (newTable["A", 0].isNumeric)
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric)
                    oldTable["A", 0] = oldTable["A", 0] - 1
            }
        }

        on(t["A", 0] or t["A", 1]) {
            order = 3

            events {
                v3Old = valueOf<Any>(oldTable["A", 0])
                v3New = valueOf<Any>(newTable["A", 0])

                assertEquals(t["A", 0], source.table["A", 0])

                if (newTable["A", 0].isNumeric)
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric)
                    oldTable["A", 0] = oldTable["A", 0] - 1
            }
        }

        on(t["A", 0] or t["A", 1]) {
            order = 1

            events {
                v1Old = valueOf<Any>(oldTable["A", 0])
                v1New = valueOf<Any>(newTable["A", 0])

                assertEquals(t["A", 0], source.table["A", 0])

                if (newTable["A", 0].isNumeric)
                    newTable["A", 0] = newTable["A", 0] + 1
                if (oldTable["A", 0].isNumeric)
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

    @Test
    fun `old table is an empty clone of source table on first pass 1`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = 100

        var count = 0

        on(t["A", 0]) {
            events {
                assertEquals(0, oldTable.iterator().asSequence().count())
                assertEquals(1, newTable.iterator().asSequence().count())
                count += count()
            }

            off(this)
        }

        assertEquals(1, count)

        on(t["A", 0]) {
            skipHistory = true

            events {
                assertEquals(1, oldTable.iterator().asSequence().count())
                assertEquals(1, newTable.iterator().asSequence().count())
                count += count()
            }
        }

        t["A", 0] = 200

        assertEquals(2, count)
    }

    @Test
    fun `old table is an empty clone of source table on first pass 2`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = 100

        var count = 0

        on(t["A", 0] or t["B", 1]) {
            events {
                assertEquals(0, oldTable.iterator().asSequence().count())
                assertEquals(1, newTable.iterator().asSequence().count())
                count += count()
            }

            off(this)
        }

        assertEquals(1, count)

        on(t["A", 0] or t["B", 1]) {
            skipHistory = true

            events {
                assertEquals(1, oldTable.iterator().asSequence().count())
                assertEquals(1, newTable.iterator().asSequence().count())
                count += count()
            }
        }

        t["A", 0] = 200

        assertEquals(2, count)
    }

    @Test
    fun `old and new table is a clone of source table 1`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = 100

        var count = 0

        on(t["A", 0]) {
            events {
                oldTable["A", 0] = source.table["A", 0] + 200
                newTable["A", 0] = source.table["A", 0] + 300

                assertEquals<Any>(source.table["A", 0] + 200, (oldTable["A", 0].asLong ?: throw InvalidCellException("")))
                assertEquals<Any>(source.table["A", 0] + 300, (newTable["A", 0].asLong ?: throw InvalidCellException("")))

                count += count()
            }
        }

        // The second listener is executed after the first listener, and its
        // old/new table should reflect changes introduced by the first listener.
        on(t["A", 0]) {
            skipHistory = true

            events {
                assertEquals<Any>(source.table["A", 0] + 200, (oldTable["A", 0].asLong ?: throw InvalidCellException("")))
                assertEquals<Any>(source.table["A", 0] + 300, (newTable["A", 0].asLong ?: throw InvalidCellException("")))

                count += count()
            }
        }

        assertEquals<Any>(100L, (t["A", 0].asLong ?: throw InvalidCellException("")))

        t["A", 0] = 50

        assertEquals<Any>(50L, (t["A", 0].asLong ?: throw InvalidCellException("")))

        assertEquals(3, count)
    }

    @Test
    fun `old and new table is a clone of source table 2`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = 100

        var count = 0

        on(t["A", 0] or t["A", 1]) {
            events {
                oldTable["A", 0] = source.table["A", 0] + 200
                newTable["A", 0] = source.table["A", 0] + 300

                assertEquals<Any>(source.table["A", 0] + 200, (oldTable["A", 0].asLong ?: throw InvalidCellException("")))
                assertEquals<Any>(source.table["A", 0] + 300, (newTable["A", 0].asLong ?: throw InvalidCellException("")))

                count += count()
            }
        }

        // The second listener is executed after the first listener, and its
        // old/new table should reflect changes introduced by the first listener.
        on(t["A", 0] or t["A", 1]) {
            skipHistory = true

            events {
                assertEquals<Any>(source.table["A", 0] + 200, (oldTable["A", 0].asLong ?: throw InvalidCellException("")))
                assertEquals<Any>(source.table["A", 0] + 300, (newTable["A", 0].asLong ?: throw InvalidCellException("")))

                count += count()
            }
        }

        assertEquals<Any>(100L, (t["A", 0].asLong ?: throw InvalidCellException("")))

        t["A", 0] = 50

        assertEquals<Any>(50L, (t["A", 0].asLong ?: throw InvalidCellException("")))

        assertEquals(3, count)
    }

    @Test
    fun `type filtering for subscriptions`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount1 = 0
        var eventCount2 = 0
        var eventCount3 = 0
        var eventCount4 = 0
        var eventCount5 = 0
        var eventCount6 = 0

        on<Any, String>(t1["A", 0] or t1["A", 1]) events {
            eventCount1 += count()
        }

        on<Any, Long>(t1["A", 0] or t1["A", 1]) events {
            eventCount2 += count()
        }

        on<String, Any>(t1["A", 0] or t1["A", 1]) events {
            eventCount3 += count()
        }

        on<Long, Any>(t1["A", 0] or t1["A", 1]) events {
            eventCount4 += count()
        }

        on<String, Long>(t1["A", 0] or t1["A", 1]) events {
            eventCount5 += count()
        }

        on<Long, String>(t1["A", 0] or t1["A", 1]) events {
            eventCount6 += count()
        }

        t1["A", 0] = "String 1"

        assertEquals(1, eventCount1)
        assertEquals(0, eventCount2)
        assertEquals(0, eventCount3)
        assertEquals(0, eventCount4)
        assertEquals(0, eventCount5)
        assertEquals(0, eventCount6)

        t1["A", 1] = 100L

        assertEquals(1, eventCount1)
        assertEquals(1, eventCount2)
        assertEquals(0, eventCount3)
        assertEquals(0, eventCount4)
        assertEquals(0, eventCount5)
        assertEquals(0, eventCount6)

        t1["A", 0] = "String 2"

        assertEquals(2, eventCount1)
        assertEquals(1, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(0, eventCount4)
        assertEquals(0, eventCount5)
        assertEquals(0, eventCount6)

        t1["A", 1] = 200 // Auto converted to Long

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(0, eventCount5)
        assertEquals(0, eventCount6)

        t1["A", 0] = 300L

        assertEquals(2, eventCount1)
        assertEquals(3, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(0, eventCount6)

        t1["A", 1] = "String 3"

        assertEquals(3, eventCount1)
        assertEquals(3, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(2, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(1, eventCount6)
    }

    @Test
    fun `type filtering for chained subscriptions 1`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount1 = 0
        var eventCount2 = 0

        on<Any, String>(t1["A", 1] or t1["A", 2], name = "Listener 1") events {
            eventCount1 += count()
            forEach {
                oldTable[it.oldValue] { it.oldValue.value.toString().toLongOrNull() }
                newTable[it.newValue] = it.newValue.value.toLong()
            }
        }

        on<Long, Long>(t1["A", 1] or t1["A", 2], name = "Listener 2") events {
            eventCount2 += count()
            forEach {
                t1["B", it.newValue.index] = it.newValue - it.oldValue
            }
        }

        t1["A", 1] = "100"
        t1["A", 2] = "110"

        t1["A", 1] = "105"
        t1["A", 2] = "120"

        assertEquals(4, eventCount1)
        assertEquals(2, eventCount2)

        assertEquals(5L, t1["B", 1].asLong)
        assertEquals(10L, t1["B", 2].asLong)
    }

    @Test
    fun `type filtering for chained subscriptions without type filter 1`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount1 = 0
        var eventCount2 = 0

        on(t1["A", 1] or t1["A", 2], name = "Listener 1") events {
            eventCount1 += count()
            forEach {
                oldTable[it.oldValue] = "L1 old"
                newTable[it.newValue] = "L1 new"
            }
        }

        on(t1["A", 1] or t1["A", 2], name = "Listener 2") events {
            eventCount2 += count()
            forEach {
                assertEquals("L1 old", it.oldValue.value)
                assertEquals("L1 new", it.newValue.value)
            }
        }

        t1["A", 1] = "Original value A1"
        t1["A", 2] = "Original value A2"

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)

        assertEquals("Original value A1", t1["A", 1].value)
        assertEquals("Original value A2", t1["A", 2].value)
    }

    @Test
    fun `type filtering for chained subscriptions 2`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount1 = 0
        var eventCount2 = 0

        on<Any, String>(t1["A", 1], name = "Listener 1") events {
            eventCount1 += count()
            forEach {
                oldTable[it.oldValue] { it.oldValue.value.toString().toLongOrNull() }
                newTable[it.newValue] = it.newValue.value.toLong()
            }
        }

        on<Long, Long>(t1["A", 1], name = "Listener 2") events {
            eventCount2 += count()
            forEach {
                t1["B", it.newValue.index] = it.newValue - it.oldValue
            }
        }

        t1["A", 1] = "100"

        t1["A", 1] = "105"

        assertEquals(2, eventCount1)
        assertEquals(1, eventCount2)

        assertEquals(5L, t1["B", 1].asLong)
    }

    @Test
    fun `type filtering for chained subscriptions without type filter 2`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name]

        var eventCount1 = 0
        var eventCount2 = 0

        on(t1["A", 1], name = "Listener 1") events {
            eventCount1 += count()
            forEach {
                oldTable[it.oldValue] = "L1 old"
                newTable[it.newValue] = "L1 new"
            }
        }

        on(t1["A", 1], name = "Listener 2") events {
            eventCount2 += count()
            forEach {
                assertEquals("L1 old", it.oldValue.value)
                assertEquals("L1 new", it.newValue.value)
            }
        }

        t1["A", 1] = "Original value A1"

        assertEquals(1, eventCount1)
        assertEquals(1, eventCount2)

        assertEquals("Original value A1", t1["A", 1].value)
    }

    // TODO Add more combinations between cells, table, columns, rows, cellrange, etc..
}
