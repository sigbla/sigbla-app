package sigbla.app.test

import org.junit.After
import org.junit.Assert
import org.junit.Test
import sigbla.app.DEFAULT_CELL_WIDTH
import sigbla.app.Table
import sigbla.app.TableView
import sigbla.app.clone
import sigbla.app.exceptions.ListenerLoopException
import sigbla.app.newView
import sigbla.app.off
import sigbla.app.oldView
import sigbla.app.on
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
                eventCount += count()
            }
        }

        tv1["A", 1].cellHeight = 25
        tv1["A", 1].cellWidth = 25

        assertEquals(2, eventCount)

        tv1["A", 1].cellHeight = 50
        tv1["A", 1].cellWidth = 50

        assertEquals(4, eventCount)

        tv1["A", 2].cellHeight = 100
        tv1["A", 2].cellWidth = 100

        tv1["B", 3].cellHeight = 125
        tv1["B", 3].cellWidth = 125

        assertEquals(8, eventCount)

        off(ref)

        tv1["B", 3].cellHeight = 150
        tv1["B", 3].cellWidth = 150

        tv1["C", 4].cellHeight = 175
        tv1["C", 4].cellWidth = 175

        assertEquals(8, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        tv1["A", 1].cellHeight = 25
        tv1["A", 1].cellWidth = 25

        val ref = on(tv1) {
            events {
                eventCount += count()
            }
        }

        assertEquals(1, eventCount)

        tv1["A", 1].cellHeight = 50
        tv1["A", 1].cellWidth = 50

        assertEquals(3, eventCount)

        tv1["A", 2].cellHeight = 75
        tv1["A", 2].cellWidth = 75

        tv1["B", 3].cellHeight = 100
        tv1["B", 3].cellWidth = 100

        assertEquals(7, eventCount)

        off(ref)

        tv1["B", 3].cellHeight = 125
        tv1["B", 3].cellWidth = 125

        tv1["C", 4].cellHeight = 150
        tv1["C", 4].cellWidth = 150

        assertEquals(7, eventCount)
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

        tv1["A", 1].cellHeight = 25
        tv1["A", 1].cellWidth = 25

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe`() {
        val tv1 = TableView[object {}.javaClass.enclosingMethod.name]

        var eventCount = 0

        tv1["A", 1].cellHeight = 25
        tv1["A", 1].cellWidth = 25

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(1, eventCount)

        tv1["A", 1].cellHeight = 50
        tv1["A", 1].cellWidth = 50

        assertEquals(1, eventCount)
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

        val ref1 = on(t) {
            events {
                t["A", 0].cellWidth = 25
            }
        }

        assertFailsWith(ListenerLoopException::class) {
            t["A", 0].cellWidth = 0
        }

        off(ref1)

        val ref2 = on(t) {
            allowLoop = true

            events {
                forEach { _ ->
                    if (t["A", 1].derived.cellWidth < 1000)
                        t["A", 1].cellWidth = t["A", 1].derived.cellWidth + 1
                }
            }
        }

        t["A", 1].cellWidth = 0

        assertEquals(1000L, (t["A", 1].cellWidth))

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
                t1[c][r].cellHeight = c.first().toByte().toLong()
                t1[c][r].cellWidth = r.toLong()

                expectedT1EventCount += 2
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r].cellHeight = c.first().toByte().toLong()
                t1[c][r].cellWidth = r.toLong()

                expectedT1EventCount += 2
            }
        }

        val t2 = clone(t1, "tableClone2")

        // We divide by 4 because we overwrite cells above,
        // but when adding a listener we only reply current values
        var expectedT2EventCount = expectedT1EventCount / 4

        on(t2) {
            events {
                t2EventCount += count()
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r].cellHeight = c.first().toByte().toLong()
                t1[c][r].cellWidth = r.toLong() + 100

                expectedT1EventCount += 2
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t2[c][r].cellHeight = c.first().toByte().toLong()
                t2[c][r].cellWidth = r.toLong() + 100

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

        t["A", 1].cellHeight = 20
        t["A", 1].cellWidth = 25

        var heightChange: Number = 0
        var widthChange: Number = 0

        on(t) {
            skipHistory = true

            events {
                heightChange = newView["A", 1].derived.cellHeight - oldView["A", 1].derived.cellHeight
                widthChange = newView["A", 1].derived.cellWidth - oldView["A", 1].derived.cellWidth
            }
        }

        t["A", 1].cellHeight = 40

        Assert.assertEquals(20L, heightChange)
        Assert.assertEquals(0L, widthChange)

        t["A", 1].cellWidth = 50

        Assert.assertEquals(0L, heightChange)
        Assert.assertEquals(25L, widthChange)

        t["A", 1].cellHeight = 110

        Assert.assertEquals(70L, heightChange)
        Assert.assertEquals(0L, widthChange)

        t["A", 1].cellWidth = 100

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

        t["A", 0].cellHeight = 150

        assertTrue(id1 ?: Int.MIN_VALUE > id2 ?: Int.MAX_VALUE)

        id1 = null
        id2 = null
        id3 = null

        t["A", 0].cellWidth = 150

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
                v2Old = oldView["A", 0].cellWidth
                v2New = newView["A", 0].cellWidth

                assertEquals(t["A", 0].cellWidth, source["A", 0].cellWidth)

                newView["A", 0].cellWidth = newView["A", 0].derived.cellWidth + 1
                oldView["A", 0].cellWidth = oldView["A", 0].derived.cellWidth - 1
            }
        }

        on(t) {
            skipHistory = true
            order = 3

            events {
                v3Old = oldView["A", 0].cellWidth
                v3New = newView["A", 0].cellWidth

                assertEquals(t["A", 0].cellWidth, source["A", 0].cellWidth)

                newView["A", 0].cellWidth = newView["A", 0].derived.cellWidth + 1
                oldView["A", 0].cellWidth = oldView["A", 0].derived.cellWidth - 1
            }
        }

        on(t) {
            skipHistory = true
            order = 1

            events {
                v1Old = oldView["A", 0].cellWidth
                v1New = newView["A", 0].cellWidth

                assertEquals(t["A", 0].cellWidth, source["A", 0].cellWidth)

                newView["A", 0].cellWidth = newView["A", 0].derived.cellWidth + 1
                oldView["A", 0].cellWidth = oldView["A", 0].derived.cellWidth - 1
            }
        }

        assertNull(v1New)
        assertNull(v1Old)

        assertNull(v2New)
        assertNull(v2Old)

        assertNull(v3New)
        assertNull(v3Old)

        t["A", 0].cellWidth = DEFAULT_CELL_WIDTH * 2

        assertEquals(null, v1Old)
        assertEquals(DEFAULT_CELL_WIDTH - 1, v2Old)
        assertEquals(DEFAULT_CELL_WIDTH - 2, v3Old)

        assertEquals(DEFAULT_CELL_WIDTH * 2, v1New)
        assertEquals(DEFAULT_CELL_WIDTH * 2 + 1, v2New)
        assertEquals(DEFAULT_CELL_WIDTH * 2 + 2, v3New)

        assertEquals(DEFAULT_CELL_WIDTH * 2, t["A", 0].cellWidth)

        t["A", 0].cellWidth = DEFAULT_CELL_WIDTH * 3

        assertEquals(DEFAULT_CELL_WIDTH * 2, v1Old)
        assertEquals(DEFAULT_CELL_WIDTH * 2 - 1, v2Old)
        assertEquals(DEFAULT_CELL_WIDTH * 2 - 2, v3Old)

        assertEquals(DEFAULT_CELL_WIDTH * 3, v1New)
        assertEquals(DEFAULT_CELL_WIDTH * 3 + 1, v2New)
        assertEquals(DEFAULT_CELL_WIDTH * 3 + 2, v3New)

        assertEquals(DEFAULT_CELL_WIDTH * 3, t["A", 0].cellWidth)
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

        tv["A", 0].cellWidth = 200

        assertEquals(2, count)
    }

    @Test
    fun `old and new table is a clone of source table`() {
        val t = TableView[object {}.javaClass.enclosingMethod.name]

        t["A", 0].cellWidth = DEFAULT_CELL_WIDTH

        var count = 0

        on(t) {
            events {
                oldView["A", 0].cellWidth = source["A", 0].derived.cellWidth + 200
                newView["A", 0].cellWidth = source["A", 0].derived.cellWidth + 300

                assertEquals(source["A", 0].derived.cellWidth + 200, oldView["A", 0].cellWidth)
                assertEquals(source["A", 0].derived.cellWidth + 300, newView["A", 0].cellWidth)

                count += count()
            }
        }

        // The second listener is executed after the first listener, and its
        // old/new table should reflect changes introduced by the first listener.
        on(t) {
            skipHistory = true

            events {
                assertEquals(source["A", 0].derived.cellWidth + 200, oldView["A", 0].cellWidth)
                assertEquals(source["A", 0].derived.cellWidth + 300, newView["A", 0].cellWidth)

                count += count()
            }
        }

        assertEquals(DEFAULT_CELL_WIDTH, t["A", 0].cellWidth)

        t["A", 0].cellWidth = DEFAULT_CELL_WIDTH / 2

        assertEquals(DEFAULT_CELL_WIDTH / 2, t["A", 0].cellWidth)

        assertEquals(3, count)
    }

    // TODO Test type filters cases like on<A> etc..
}