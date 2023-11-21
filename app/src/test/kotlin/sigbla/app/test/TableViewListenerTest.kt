/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import org.junit.After
import org.junit.Assert
import org.junit.Test
import sigbla.app.*
import sigbla.app.exceptions.ListenerLoopException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TableViewListenerTest {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    // TODO Ensure we test this subscribe and unsubscribe test below on all onAny functions
    @Test
    fun `subscribe and unsubscribe`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        val ref = on(tv1) {
            events {
                eventCount += map { it.newValue }
                    .filter { it is CellWidth<*, *> || it is CellHeight<*, *> }
                    .mapNotNull { if (it is CellWidth<*, *>) it.source else if (it is CellHeight<*, *>) it.source else null }
                    .filterIsInstance<CellView>()
                    .count()
            }
        }

        tv1["A", 1][CellHeight] = 25
        tv1["A", 1][CellWidth] = 25

        assertEquals(2, eventCount)

        tv1["A", 1][CellHeight] = 50
        tv1["A", 1][CellWidth] = 50

        assertEquals(4, eventCount)

        tv1["A", 2][CellHeight] = 100
        tv1["A", 2][CellWidth] = 100

        tv1["B", 3][CellHeight] = 125
        tv1["B", 3][CellWidth] = 125

        assertEquals(8, eventCount)

        off(ref)

        tv1["B", 3][CellHeight] = 150
        tv1["B", 3][CellWidth] = 150

        tv1["C", 4][CellHeight] = 175
        tv1["C", 4][CellWidth] = 175

        assertEquals(8, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        tv1["A", 1][CellHeight] = 25
        tv1["A", 1][CellWidth] = 25

        val ref = on(tv1) {
            events {
                eventCount += count()
            }
        }

        assertEquals(2, eventCount)

        tv1["A", 1][CellHeight] = 50
        tv1["A", 1][CellWidth] = 50

        assertEquals(4, eventCount)

        tv1["A", 2][CellHeight] = 75
        tv1["A", 2][CellWidth] = 75

        tv1["B", 3][CellHeight] = 100
        tv1["B", 3][CellWidth] = 100

        assertEquals(8, eventCount)

        off(ref)

        tv1["B", 3][CellHeight] = 125
        tv1["B", 3][CellWidth] = 125

        tv1["C", 4][CellHeight] = 150
        tv1["C", 4][CellWidth] = 150

        assertEquals(8, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(0, eventCount)

        tv1["A", 1][CellHeight] = 25
        tv1["A", 1][CellWidth] = 25

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        tv1["A", 1][CellHeight] = 25
        tv1["A", 1][CellWidth] = 25

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(2, eventCount)

        tv1["A", 1][CellHeight] = 50
        tv1["A", 1][CellWidth] = 50

        assertEquals(2, eventCount)
    }

    @Test
    fun `listener ref with name and order`() {
        val t = TableView[object {}.javaClass.enclosingMethod.name]
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
        val t = TableView[object {}.javaClass.enclosingMethod.name]
        val ref = on(t) {}

        assertNull(ref.name)
        assertEquals(0L, ref.order)

        off(ref)
    }

    @Test
    fun `listener loop support`() {
        val t = TableView[object {}.javaClass.enclosingMethod.name]

        val ref1 = on(t, skipHistory = true) {
            events {
                t["A", 0][CellWidth] = 25
            }
        }

        assertFailsWith(ListenerLoopException::class) {
            t["A", 0][CellWidth] = 0
        }

        off(ref1)

        val ref2 = on(t) {
            allowLoop = true

            events {
                forEach { _ ->
                    if (t["A", 1].derived.cellWidth < 1000)
                        t["A", 1][CellWidth] = t["A", 1].derived.cellWidth + 1
                }
            }
        }

        t["A", 1][CellWidth] = 0

        assertEquals(1000L, t["A", 1][CellWidth].toLong())

        off(ref2)
    }

    @Test
    fun `table clone and events`() {
        val t1 = TableView[object {}.javaClass.enclosingMethod.name]

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
                t1[c][r][CellHeight] = c.first().toByte().toLong()
                t1[c][r][CellWidth] = r.toLong()

                expectedT1EventCount += 2
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r][CellHeight] = c.first().toByte().toLong()
                t1[c][r][CellWidth] = r.toLong()

                expectedT1EventCount += 2
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
                t1[c][r][CellHeight] = c.first().toByte().toLong()
                t1[c][r][CellWidth] = r.toLong() + 100

                expectedT1EventCount += 2
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t2[c][r][CellHeight] = c.first().toByte().toLong()
                t2[c][r][CellWidth] = r.toLong() + 100

                expectedT2EventCount += 2
            }
        }

        Assert.assertEquals(expectedT1EventCount, t1EventCount)
        Assert.assertEquals(expectedT2EventCount, t2EventCount)
        Assert.assertTrue(expectedT1EventCount > expectedT2EventCount)
        Assert.assertTrue(expectedT2EventCount > 0)
    }

    @Test
    fun `table events with old and new snapshots`() {
        val t = TableView[object {}.javaClass.enclosingMethod.name]

        t["A", 1][CellHeight] = 20
        t["A", 1][CellWidth] = 25

        var heightChange: Number = 0
        var widthChange: Number = 0

        on(t) {
            skipHistory = true

            events {
                heightChange = newView["A", 1].derived.cellHeight - oldView["A", 1].derived.cellHeight
                widthChange = newView["A", 1].derived.cellWidth - oldView["A", 1].derived.cellWidth
            }
        }

        t["A", 1][CellHeight] = 40

        Assert.assertEquals(20L, heightChange)
        Assert.assertEquals(0L, widthChange)

        t["A", 1][CellWidth] = 50

        Assert.assertEquals(0L, heightChange)
        Assert.assertEquals(25L, widthChange)

        t["A", 1][CellHeight] = 110

        Assert.assertEquals(70L, heightChange)
        Assert.assertEquals(0L, widthChange)

        t["A", 1][CellWidth] = 100

        Assert.assertEquals(0L, heightChange)
        Assert.assertEquals(50L, widthChange)
    }

    @Test
    fun `listener ordering`() {
        val t = TableView[object {}.javaClass.enclosingMethod.name]

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

        t["A", 0][CellHeight] = 150

        assertTrue(id1 ?: Int.MIN_VALUE > id2 ?: Int.MAX_VALUE)

        id1 = null
        id2 = null
        id3 = null

        t["A", 0][CellWidth] = 150

        assertTrue(id2 ?: Int.MIN_VALUE > id3 ?: Int.MAX_VALUE)
    }

    @Test
    fun `listener order difference propagation`() {
        val t = TableView[object {}.javaClass.enclosingMethod.name]

        var v1Old: Long? = null
        var v2Old: Long? = null
        var v3Old: Long? = null

        var v1New: Long? = null
        var v2New: Long? = null
        var v3New: Long? = null

        on(t) {
            skipHistory = true
            order = 2

            events {
                v2Old = oldView["A", 0][CellWidth].toLong()
                v2New = newView["A", 0][CellWidth].toLong()

                assertEquals(t["A", 0][CellWidth], source["A", 0][CellWidth])

                newView["A", 0][CellWidth] = newView["A", 0].derived.cellWidth + 1
                oldView["A", 0][CellWidth] = oldView["A", 0].derived.cellWidth - 1
            }
        }

        on(t) {
            skipHistory = true
            order = 3

            events {
                v3Old = oldView["A", 0][CellWidth].toLong()
                v3New = newView["A", 0][CellWidth].toLong()

                assertEquals(t["A", 0][CellWidth], source["A", 0][CellWidth])

                newView["A", 0][CellWidth] = newView["A", 0].derived.cellWidth + 1
                oldView["A", 0][CellWidth] = oldView["A", 0].derived.cellWidth - 1
            }
        }

        on(t) {
            skipHistory = true
            order = 1

            events {
                v1Old = oldView["A", 0][CellWidth].toLong()
                v1New = newView["A", 0][CellWidth].toLong()

                assertEquals(t["A", 0][CellWidth], source["A", 0][CellWidth])

                newView["A", 0][CellWidth] = newView["A", 0].derived.cellWidth + 1
                oldView["A", 0][CellWidth] = oldView["A", 0].derived.cellWidth - 1
            }
        }

        assertNull(v1New)
        assertNull(v1Old)

        assertNull(v2New)
        assertNull(v2Old)

        assertNull(v3New)
        assertNull(v3Old)

        t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH * 2

        assertEquals(null, v1Old)
        assertEquals(DEFAULT_CELL_WIDTH - 1, v2Old)
        assertEquals(DEFAULT_CELL_WIDTH - 2, v3Old)

        assertEquals(DEFAULT_CELL_WIDTH * 2, v1New)
        assertEquals(DEFAULT_CELL_WIDTH * 2 + 1, v2New)
        assertEquals(DEFAULT_CELL_WIDTH * 2 + 2, v3New)

        assertEquals<Any?>(DEFAULT_CELL_WIDTH * 2, t["A", 0][CellWidth].width)

        t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH * 3

        assertEquals(DEFAULT_CELL_WIDTH * 2, v1Old)
        assertEquals(DEFAULT_CELL_WIDTH * 2 - 1, v2Old)
        assertEquals(DEFAULT_CELL_WIDTH * 2 - 2, v3Old)

        assertEquals(DEFAULT_CELL_WIDTH * 3, v1New)
        assertEquals(DEFAULT_CELL_WIDTH * 3 + 1, v2New)
        assertEquals(DEFAULT_CELL_WIDTH * 3 + 2, v3New)

        assertEquals<Any?>(DEFAULT_CELL_WIDTH * 3, t["A", 0][CellWidth].width)
    }

    @Test
    fun `old table is an empty clone of source table on first pass`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]
        val tv = TableView[t]

        t["A", 0] = 100

        var count = 0

        on(tv) {
            events {
                assertEquals(0, oldView.iterator().asSequence().count())
                assertEquals(1, newView.iterator().asSequence().count())
                count += count()
            }

            off(this)
        }

        assertEquals(1, count)

        on(tv) {
            skipHistory = true

            events {
                assertEquals(1, oldView.iterator().asSequence().count())
                assertEquals(1, newView.iterator().asSequence().count())
                count += count()
            }
        }

        tv["A", 0][CellWidth] = 200

        assertEquals(2, count)
    }

    @Test
    fun `old and new table is a clone of source table`() {
        val t = TableView[object {}.javaClass.enclosingMethod.name]

        t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH

        var count = 0

        on(t) {
            events {
                oldView["A", 0][CellWidth] = source["A", 0].derived.cellWidth + 200
                newView["A", 0][CellWidth] = source["A", 0].derived.cellWidth + 300

                assertEquals(source["A", 0].derived.cellWidth + 200, oldView["A", 0][CellWidth].width)
                assertEquals(source["A", 0].derived.cellWidth + 300, newView["A", 0][CellWidth].width)

                count += map { it.newValue }
                    .filterIsInstance<CellWidth<*,*>>()
                    .map { it.source }
                    .filterIsInstance<CellView>()
                    .count()
            }
        }

        // The second listener is executed after the first listener, and its
        // old/new table should reflect changes introduced by the first listener.
        on(t) {
            skipHistory = true

            events {
                assertEquals(source["A", 0].derived.cellWidth + 200, oldView["A", 0][CellWidth].width)
                assertEquals(source["A", 0].derived.cellWidth + 300, newView["A", 0][CellWidth].width)

                count += count()
            }
        }

        assertEquals(DEFAULT_CELL_WIDTH, t["A", 0][CellWidth].width)

        t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH / 2

        assertEquals(DEFAULT_CELL_WIDTH / 2, t["A", 0][CellWidth].width)

        assertEquals(3, count)
    }

    // TODO CellClasses and CellTopics

    // TODO Test type filters cases like on<A> etc..
}
