/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import sigbla.app.*
import sigbla.app.exceptions.ListenerLoopException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TableViewListenerTest {
    @Test
    fun `subscribe and unsubscribe cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        val ref = on(tv1) {
            events {
                val c = count()
                assertEquals(1, c)
                eventCount += c
            }
        }

        tv1["A", 1][CellHeight] = 25
        tv1["A", 1][CellWidth] = 25
        tv1["A", 1][CellClasses] = "cell-classes-1"
        tv1["A", 1][CellTopics] = "cell-topics-1"
        tv1["A", 1][CellTransformer] = {}

        assertEquals(5, eventCount)

        tv1["A", 1][CellHeight] = 50
        tv1["A", 1][CellWidth] = 50
        tv1["A", 1][CellClasses] = "cell-classes-2"
        tv1["A", 1][CellTopics] = "cell-topics-2"
        tv1["A", 1][CellTransformer] = {}

        assertEquals(10, eventCount)

        tv1["A", 2][CellHeight] = 100
        tv1["A", 2][CellWidth] = 100
        tv1["A", 2][CellClasses] = "cell-classes-3"
        tv1["A", 2][CellTopics] = "cell-topics-3"
        tv1["A", 2][CellTransformer] = {}

        tv1["B", 3][CellHeight] = 125
        tv1["B", 3][CellWidth] = 125
        tv1["B", 3][CellClasses] = "cell-classes-4"
        tv1["B", 3][CellTopics] = "cell-topics-4"
        tv1["B", 3][CellTransformer] = {}

        assertEquals(20, eventCount)

        off(ref)

        tv1["B", 3][CellHeight] = 150
        tv1["B", 3][CellWidth] = 150
        tv1["B", 3][CellClasses] = "cell-classes-5"
        tv1["B", 3][CellTopics] = "cell-topics-5"
        tv1["B", 3][CellTransformer] = {}

        tv1["C", 4][CellHeight] = 175
        tv1["C", 4][CellWidth] = 175
        tv1["C", 4][CellClasses] = "cell-classes-6"
        tv1["C", 4][CellTopics] = "cell-topics-6"
        tv1["C", 4][CellTransformer] = {}

        assertEquals(20, eventCount)
    }

    @Test
    fun `subscribe and unsubscribe columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        val ref = on(tv1) {
            events {
                val c = count()
                assertEquals(1, c)
                eventCount += c
            }
        }

        //tv1["A"][CellHeight] = 25
        tv1["A"][CellWidth] = 25
        tv1["A"][CellClasses] = "cell-classes-1"
        tv1["A"][CellTopics] = "cell-topics-1"
        //tv1["A"][CellTransformer] = {}

        assertEquals(3, eventCount)

        //tv1["A"][CellHeight] = 50
        tv1["A"][CellWidth] = 50
        tv1["A"][CellClasses] = "cell-classes-2"
        tv1["A"][CellTopics] = "cell-topics-2"
        //tv1["A"][CellTransformer] = {}

        assertEquals(6, eventCount)

        //tv1["A"][CellHeight] = 100
        tv1["A"][CellWidth] = 100
        tv1["A"][CellClasses] = "cell-classes-3"
        tv1["A"][CellTopics] = "cell-topics-3"
        //tv1["A"][CellTransformer] = {}

        //tv1["B"][CellHeight] = 125
        tv1["B"][CellWidth] = 125
        tv1["B"][CellClasses] = "cell-classes-4"
        tv1["B"][CellTopics] = "cell-topics-4"
        //tv1["B"][CellTransformer] = {}

        assertEquals(12, eventCount)

        off(ref)

        //tv1["B"][CellHeight] = 150
        tv1["B"][CellWidth] = 150
        tv1["B"][CellClasses] = "cell-classes-5"
        tv1["B"][CellTopics] = "cell-topics-5"
        //tv1["B"][CellTransformer] = {}

        //tv1["C"][CellHeight] = 175
        tv1["C"][CellWidth] = 175
        tv1["C"][CellClasses] = "cell-classes-6"
        tv1["C"][CellTopics] = "cell-topics-6"
        //tv1["C"][CellTransformer] = {}

        assertEquals(12, eventCount)
    }

    @Test
    fun `subscribe and unsubscribe rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        val ref = on(tv1) {
            events {
                val c = count()
                assertEquals(1, c)
                eventCount += c
            }
        }

        tv1[1][CellHeight] = 25
        //tv1[1][CellWidth] = 25
        tv1[1][CellClasses] = "cell-classes-1"
        tv1[1][CellTopics] = "cell-topics-1"
        //tv1[1][CellTransformer] = {}

        assertEquals(3, eventCount)

        tv1[1][CellHeight] = 50
        //tv1[1][CellWidth] = 50
        tv1[1][CellClasses] = "cell-classes-2"
        tv1[1][CellTopics] = "cell-topics-2"
        //tv1[1][CellTransformer] = {}

        assertEquals(6, eventCount)

        tv1[2][CellHeight] = 100
        //tv1[2][CellWidth] = 100
        tv1[2][CellClasses] = "cell-classes-3"
        tv1[2][CellTopics] = "cell-topics-3"
        //tv1[2][CellTransformer] = {}

        tv1[3][CellHeight] = 125
        //tv1[3][CellWidth] = 125
        tv1[3][CellClasses] = "cell-classes-4"
        tv1[3][CellTopics] = "cell-topics-4"
        //tv1[3][CellTransformer] = {}

        assertEquals(12, eventCount)

        off(ref)

        tv1[3][CellHeight] = 150
        //tv1[3][CellWidth] = 150
        tv1[3][CellClasses] = "cell-classes-5"
        tv1[3][CellTopics] = "cell-topics-5"
        //tv1[3][CellTransformer] = {}

        tv1[4][CellHeight] = 175
        //tv1[4][CellWidth] = 175
        tv1[4][CellClasses] = "cell-classes-6"
        tv1[4][CellTopics] = "cell-topics-6"
        //tv1[4][CellTransformer] = {}

        assertEquals(12, eventCount)
    }

    @Test
    fun `subscribe and unsubscribe tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        val ref = on(tv1) {
            events {
                val c = count()
                assertEquals(1, c)
                eventCount += c
            }
        }

        tv1[CellHeight] = 25
        tv1[CellWidth] = 25
        tv1[CellClasses] = "cell-classes-1"
        tv1[CellTopics] = "cell-topics-1"
        //tv1[CellTransformer] = {}
        tv1[Resources] = ("a" to {})
        tv1[Table] = Table[null]

        assertEquals(6, eventCount)

        tv1[CellHeight] = 50
        tv1[CellWidth] = 50
        tv1[CellClasses] = "cell-classes-2"
        tv1[CellTopics] = "cell-topics-2"
        //tv1[CellTransformer] = {}
        tv1[Resources] = ("b" to {})
        tv1[Table] = Table[null]

        assertEquals(12, eventCount)

        off(ref)

        tv1[CellHeight] = 150
        tv1[CellWidth] = 150
        tv1[CellClasses] = "cell-classes-3"
        tv1[CellTopics] = "cell-topics-3"
        //tv1[CellTransformer] = {}
        tv1[Resources] = ("c" to {})
        tv1[Table] = Table[null]

        assertEquals(12, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        tv1["A", 1][CellHeight] = 25
        tv1["A", 1][CellWidth] = 25
        tv1["A", 1][CellClasses] = "cell-classes-1"
        tv1["A", 1][CellTopics] = "cell-topics-1"
        tv1["A", 1][CellTransformer] = {}

        val ref = on(tv1) {
            events {
                eventCount += count()
            }
        }

        assertEquals(5, eventCount)

        tv1["A", 1][CellHeight] = 50
        tv1["A", 1][CellWidth] = 50
        tv1["A", 1][CellClasses] = "cell-classes-2"
        tv1["A", 1][CellTopics] = "cell-topics-2"
        tv1["A", 1][CellTransformer] = {}

        assertEquals(10, eventCount)

        tv1["A", 2][CellHeight] = 75
        tv1["A", 2][CellWidth] = 75
        tv1["A", 2][CellClasses] = "cell-classes-3"
        tv1["A", 2][CellTopics] = "cell-topics-3"
        tv1["A", 2][CellTransformer] = {}

        tv1["B", 3][CellHeight] = 100
        tv1["B", 3][CellWidth] = 100
        tv1["B", 3][CellClasses] = "cell-classes-4"
        tv1["B", 3][CellTopics] = "cell-topics-4"
        tv1["B", 3][CellTransformer] = {}

        assertEquals(20, eventCount)

        off(ref)

        tv1["B", 3][CellHeight] = 125
        tv1["B", 3][CellWidth] = 125
        tv1["B", 3][CellClasses] = "cell-classes-5"
        tv1["B", 3][CellTopics] = "cell-topics-5"
        tv1["B", 3][CellTransformer] = {}

        tv1["C", 4][CellHeight] = 150
        tv1["C", 4][CellWidth] = 150
        tv1["C", 4][CellClasses] = "cell-classes-6"
        tv1["C", 4][CellTopics] = "cell-topics-6"
        tv1["C", 4][CellTransformer] = {}

        assertEquals(20, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        //tv1["A"][CellHeight] = 25
        tv1["A"][CellWidth] = 25
        tv1["A"][CellClasses] = "cell-classes-1"
        tv1["A"][CellTopics] = "cell-topics-1"
        //tv1["A"][CellTransformer] = {}

        val ref = on(tv1) {
            events {
                eventCount += count()
            }
        }

        assertEquals(3, eventCount)

        //tv1["A"][CellHeight] = 50
        tv1["A"][CellWidth] = 50
        tv1["A"][CellClasses] = "cell-classes-2"
        tv1["A"][CellTopics] = "cell-topics-2"
        //tv1["A"][CellTransformer] = {}

        assertEquals(6, eventCount)

        //tv1["A"][CellHeight] = 75
        tv1["A"][CellWidth] = 75
        tv1["A"][CellClasses] = "cell-classes-3"
        tv1["A"][CellTopics] = "cell-topics-3"
        //tv1["A"][CellTransformer] = {}

        //tv1["B"][CellHeight] = 100
        tv1["B"][CellWidth] = 100
        tv1["B"][CellClasses] = "cell-classes-4"
        tv1["B"][CellTopics] = "cell-topics-4"
        //tv1["B"][CellTransformer] = {}

        assertEquals(12, eventCount)

        off(ref)

        //tv1["B"][CellHeight] = 125
        tv1["B"][CellWidth] = 125
        tv1["B"][CellClasses] = "cell-classes-5"
        tv1["B"][CellTopics] = "cell-topics-5"
        //tv1["B"][CellTransformer] = {}

        //tv1["C"][CellHeight] = 150
        tv1["C"][CellWidth] = 150
        tv1["C"][CellClasses] = "cell-classes-6"
        tv1["C"][CellTopics] = "cell-topics-6"
        //tv1["C"][CellTransformer] = {}

        assertEquals(12, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        tv1[1][CellHeight] = 25
        //tv1[1][CellWidth] = 25
        tv1[1][CellClasses] = "cell-classes-1"
        tv1[1][CellTopics] = "cell-topics-1"
        //tv1[1][CellTransformer] = {}

        val ref = on(tv1) {
            events {
                eventCount += count()
            }
        }

        assertEquals(3, eventCount)

        tv1[1][CellHeight] = 50
        //tv1[1][CellWidth] = 50
        tv1[1][CellClasses] = "cell-classes-2"
        tv1[1][CellTopics] = "cell-topics-2"
        //tv1[1][CellTransformer] = {}

        assertEquals(6, eventCount)

        tv1[2][CellHeight] = 75
        //tv1[2][CellWidth] = 75
        tv1[2][CellClasses] = "cell-classes-3"
        tv1[2][CellTopics] = "cell-topics-3"
        //tv1[2][CellTransformer] = {}

        tv1[3][CellHeight] = 100
        //tv1[3][CellWidth] = 100
        tv1[3][CellClasses] = "cell-classes-4"
        tv1[3][CellTopics] = "cell-topics-4"
        //tv1[3][CellTransformer] = {}

        assertEquals(12, eventCount)

        off(ref)

        tv1[3][CellHeight] = 125
        //tv1[3][CellWidth] = 125
        tv1[3][CellClasses] = "cell-classes-5"
        tv1[3][CellTopics] = "cell-topics-5"
        //tv1[3][CellTransformer] = {}

        tv1[4][CellHeight] = 150
        //tv1[4][CellWidth] = 150
        tv1[4][CellClasses] = "cell-classes-6"
        tv1[4][CellTopics] = "cell-topics-6"
        //tv1[4][CellTransformer] = {}

        assertEquals(12, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        tv1[CellHeight] = 25
        tv1[CellWidth] = 25
        tv1[CellClasses] = "cell-classes-1"
        tv1[CellTopics] = "cell-topics-1"
        //tv1[CellTransformer] = {}
        tv1[Resources] = ("a" to {})
        tv1[Table] = Table[null]

        val ref = on(tv1) {
            events {
                eventCount += count()
            }
        }

        assertEquals(6, eventCount)

        tv1[CellHeight] = 50
        tv1[CellWidth] = 50
        tv1[CellClasses] = "cell-classes-2"
        tv1[CellTopics] = "cell-topics-2"
        //tv1[CellTransformer] = {}
        tv1[Resources] = ("b" to {})
        tv1[Table] = Table[null]

        assertEquals(12, eventCount)

        off(ref)

        tv1[CellHeight] = 125
        tv1[CellWidth] = 125
        tv1[CellClasses] = "cell-classes-5"
        tv1[CellTopics] = "cell-topics-5"
        //tv1[CellTransformer] = {}
        tv1[Resources] = ("c" to {})
        tv1[Table] = Table[null]

        assertEquals(12, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        tv1["A", 1][CellClasses] = "cell-classes-1"
        tv1["A", 1][CellTopics] = "cell-topics-1"
        tv1["A", 1][CellTransformer] = {}

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(0, eventCount)

        //tv1["A"][CellHeight] = 25
        tv1["A"][CellWidth] = 25
        tv1["A"][CellClasses] = "cell-classes-1"
        tv1["A"][CellTopics] = "cell-topics-1"
        //tv1["A"][CellTransformer] = {}

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(0, eventCount)

        tv1[1][CellHeight] = 25
        //tv1[1][CellWidth] = 25
        tv1[1][CellClasses] = "cell-classes-1"
        tv1[1][CellTopics] = "cell-topics-1"
        //tv1[1][CellTransformer] = {}

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(0, eventCount)

        tv1[CellHeight] = 25
        tv1[CellWidth] = 25
        tv1[CellClasses] = "cell-classes-1"
        tv1[CellTopics] = "cell-topics-1"
        //tv1[CellTransformer] = {}
        tv1[Resources] = ("a" to {})
        tv1[Table] = Table[null]

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        tv1["A", 1][CellHeight] = 25
        tv1["A", 1][CellWidth] = 25
        tv1["A", 1][CellClasses] = "cell-classes-1"
        tv1["A", 1][CellTopics] = "cell-topics-1"
        tv1["A", 1][CellTransformer] = {}

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(5, eventCount)

        tv1["A", 1][CellHeight] = 50
        tv1["A", 1][CellWidth] = 50
        tv1["A", 1][CellClasses] = "cell-classes-2"
        tv1["A", 1][CellTopics] = "cell-topics-2"
        tv1["A", 1][CellTransformer] = {}

        assertEquals(5, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        //tv1["A"][CellHeight] = 25
        tv1["A"][CellWidth] = 25
        tv1["A"][CellClasses] = "cell-classes-1"
        tv1["A"][CellTopics] = "cell-topics-1"
        //tv1["A"][CellTransformer] = {}

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(3, eventCount)

        //tv1["A"][CellHeight] = 50
        tv1["A"][CellWidth] = 50
        tv1["A"][CellClasses] = "cell-classes-2"
        tv1["A"][CellTopics] = "cell-topics-2"
        //tv1["A"][CellTransformer] = {}

        assertEquals(3, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        tv1[1][CellHeight] = 25
        //tv1[1][CellWidth] = 25
        tv1[1][CellClasses] = "cell-classes-1"
        tv1[1][CellTopics] = "cell-topics-1"
        //tv1[1][CellTransformer] = {}

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(3, eventCount)

        tv1[1][CellHeight] = 50
        //tv1[1][CellWidth] = 50
        tv1[1][CellClasses] = "cell-classes-2"
        tv1[1][CellTopics] = "cell-topics-2"
        //tv1[1][CellTransformer] = {}

        assertEquals(3, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        tv1[CellHeight] = 25
        tv1[CellWidth] = 25
        tv1[CellClasses] = "cell-classes-1"
        tv1[CellTopics] = "cell-topics-1"
        //tv1[CellTransformer] = {}
        tv1[Resources] = ("a" to {})
        tv1[Table] = Table[null]

        on(tv1) {
            off(this)

            events {
                eventCount += count()
            }
        }

        assertEquals(6, eventCount)

        tv1[CellHeight] = 50
        tv1[CellWidth] = 50
        tv1[CellClasses] = "cell-classes-2"
        tv1[CellTopics] = "cell-topics-2"
        //tv1[CellTransformer] = {}
        tv1[Resources] = ("b" to {})
        tv1[Table] = Table[null]

        assertEquals(6, eventCount)
    }

    @Test
    fun `listener ref with name and order`() {
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
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
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
        val ref = on(t) {}

        assertNull(ref.name)
        assertEquals(0L, ref.order)

        off(ref)
    }

    @Test
    fun `listener loop support`() {
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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

        assertEquals(1000L, t["A", 1][CellWidth].asLong)

        off(ref2)
    }

    @Test
    fun `table clone and events`() {
        val t1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
                t1[c][r][CellHeight] = c.first().code.toLong()
                t1[c][r][CellWidth] = r.toLong()

                expectedT1EventCount += 2
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t1[c][r][CellHeight] = c.first().code.toLong()
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
                t1[c][r][CellHeight] = c.first().code.toLong()
                t1[c][r][CellWidth] = r.toLong() + 100

                expectedT1EventCount += 2
            }
        }

        for (c in listOf("A", "B", "C", "D")) {
            for (r in 1..100) {
                t2[c][r][CellHeight] = c.first().code.toLong()
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
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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

        assertTrue((id1 ?: Int.MIN_VALUE) > (id2 ?: Int.MAX_VALUE))

        id1 = null
        id2 = null
        id3 = null

        t["A", 0][CellWidth] = 150

        assertTrue((id2 ?: Int.MIN_VALUE) > (id3 ?: Int.MAX_VALUE))
    }

    @Test
    fun `listener order difference propagation`() {
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

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
                v2Old = oldView["A", 0][CellWidth].asLong
                v2New = newView["A", 0][CellWidth].asLong

                assertEquals(t["A", 0][CellWidth], source["A", 0][CellWidth])

                newView["A", 0][CellWidth] = newView["A", 0].derived.cellWidth + 1
                oldView["A", 0][CellWidth] = oldView["A", 0].derived.cellWidth - 1
            }
        }

        on(t) {
            skipHistory = true
            order = 3

            events {
                v3Old = oldView["A", 0][CellWidth].asLong
                v3New = newView["A", 0][CellWidth].asLong

                assertEquals(t["A", 0][CellWidth], source["A", 0][CellWidth])

                newView["A", 0][CellWidth] = newView["A", 0].derived.cellWidth + 1
                oldView["A", 0][CellWidth] = oldView["A", 0].derived.cellWidth - 1
            }
        }

        on(t) {
            skipHistory = true
            order = 1

            events {
                v1Old = oldView["A", 0][CellWidth].asLong
                v1New = newView["A", 0][CellWidth].asLong

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

        assertEquals(DEFAULT_CELL_WIDTH * 2, t["A", 0][CellWidth].width)

        t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH * 3

        assertEquals(DEFAULT_CELL_WIDTH * 2, v1Old)
        assertEquals(DEFAULT_CELL_WIDTH * 2 - 1, v2Old)
        assertEquals(DEFAULT_CELL_WIDTH * 2 - 2, v3Old)

        assertEquals(DEFAULT_CELL_WIDTH * 3, v1New)
        assertEquals(DEFAULT_CELL_WIDTH * 3 + 1, v2New)
        assertEquals(DEFAULT_CELL_WIDTH * 3 + 2, v3New)

        assertEquals(DEFAULT_CELL_WIDTH * 3, t["A", 0][CellWidth].width)
    }

    @Test
    fun `old table is an empty clone of source table on first pass`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]
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
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH

        var count = 0

        on(t) {
            events {
                oldView["A", 0][CellWidth] = source["A", 0].derived.cellWidth + 200
                newView["A", 0][CellWidth] = source["A", 0].derived.cellWidth + 300

                assertEquals(source["A", 0].derived.cellWidth + 200, oldView["A", 0][CellWidth].width)
                assertEquals(source["A", 0].derived.cellWidth + 300, newView["A", 0][CellWidth].width)

                count += map { it.newValue }
                    .filterIsInstance<CellWidth<*, *>>()
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

    @Test
    fun `event properties`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        tv1["A", 1][CellHeight] = 100

        on(tv1) events {
            forEach {
                assertNotNull(it.columnView)
                assertNotNull(it.index)

                assertEquals(it.tableView, tableViewFromViewRelated(it.newValue))
                assertEquals(it.columnView, columnViewFromViewRelated(it.newValue))
                assertEquals(it.index, indexFromViewRelated(it.newValue))

                assertNotEquals(it.tableView, tableViewFromViewRelated(it.oldValue))
                assertNotEquals(it.columnView, columnViewFromViewRelated(it.oldValue))
                assertEquals(it.columnView!!.header, columnViewFromViewRelated(it.oldValue)!!.header)
                assertEquals(it.index, indexFromViewRelated(it.oldValue))
            }
        }

        tv1["A", 1][CellHeight] = 200
    }

    @Test
    fun `event values cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ct1: Cell<*>.() -> Unit = {}
        val ct2: Cell<*>.() -> Unit = {}

        tv1["A", 1][CellHeight] = 25
        tv1["A", 1][CellWidth] = 30
        tv1["A", 1][CellClasses] = "cell-classes-1"
        tv1["A", 1][CellTopics] = "cell-topics-1"
        tv1["A", 1][CellTransformer] = ct1

        var init = true

        on(tv1) events {
            if (init) {
                forEach {
                    when (it.newValue) {
                        is CellHeight<*, *> -> {
                            assertEquals(Unit, (it.oldValue as CellHeight<*, *>).height)
                            assertEquals(25L, (it.newValue as CellHeight<*, *>).height)
                            assertEquals(oldView["A", 1], (it.oldValue as CellHeight<*, *>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellHeight<*, *>).source)
                        }

                        is CellWidth<*, *> -> {
                            assertEquals(Unit, (it.oldValue as CellWidth<*, *>).width)
                            assertEquals(30L, (it.newValue as CellWidth<*, *>).width)
                            assertEquals(oldView["A", 1], (it.oldValue as CellWidth<*, *>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellWidth<*, *>).source)
                        }

                        is CellClasses<*> -> {
                            assertEquals(emptySet(), (it.oldValue as CellClasses<*>).classes)
                            assertEquals(setOf("cell-classes-1"), (it.newValue as CellClasses<*>).classes)
                            assertEquals(oldView["A", 1], (it.oldValue as CellClasses<*>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellClasses<*>).source)
                        }

                        is CellTopics<*> -> {
                            assertEquals(emptySet(), (it.oldValue as CellTopics<*>).topics)
                            assertEquals(setOf("cell-topics-1"), (it.newValue as CellTopics<*>).topics)
                            assertEquals(oldView["A", 1], (it.oldValue as CellTopics<*>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellTopics<*>).source)
                        }

                        is CellTransformer<*> -> {
                            assertEquals(Unit, (it.oldValue as CellTransformer<*>).function)
                            assertEquals(ct1, (it.newValue as CellTransformer<*>).function)
                            assertEquals(oldView["A", 1], (it.oldValue as CellTransformer<*>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellTransformer<*>).source)
                        }

                        else -> assertTrue(false)
                    }
                }
            } else {
                forEach {
                    when (it.newValue) {
                        is CellHeight<*, *> -> {
                            assertEquals(25L, (it.oldValue as CellHeight<*, *>).height)
                            assertEquals(55L, (it.newValue as CellHeight<*, *>).height)
                            assertEquals(oldView["A", 1], (it.oldValue as CellHeight<*, *>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellHeight<*, *>).source)
                        }

                        is CellWidth<*, *> -> {
                            assertEquals(30L, (it.oldValue as CellWidth<*, *>).width)
                            assertEquals(60L, (it.newValue as CellWidth<*, *>).width)
                            assertEquals(oldView["A", 1], (it.oldValue as CellWidth<*, *>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellWidth<*, *>).source)
                        }

                        is CellClasses<*> -> {
                            assertEquals(setOf("cell-classes-1"), (it.oldValue as CellClasses<*>).classes)
                            assertEquals(setOf("cell-classes-2"), (it.newValue as CellClasses<*>).classes)
                            assertEquals(oldView["A", 1], (it.oldValue as CellClasses<*>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellClasses<*>).source)
                        }

                        is CellTopics<*> -> {
                            assertEquals(setOf("cell-topics-1"), (it.oldValue as CellTopics<*>).topics)
                            assertEquals(setOf("cell-topics-2"), (it.newValue as CellTopics<*>).topics)
                            assertEquals(oldView["A", 1], (it.oldValue as CellTopics<*>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellTopics<*>).source)
                        }

                        is CellTransformer<*> -> {
                            assertEquals(ct1, (it.oldValue as CellTransformer<*>).function)
                            assertEquals(ct2, (it.newValue as CellTransformer<*>).function)
                            assertEquals(oldView["A", 1], (it.oldValue as CellTransformer<*>).source)
                            assertEquals(newView["A", 1], (it.newValue as CellTransformer<*>).source)
                        }

                        else -> assertTrue(false)
                    }
                }
            }
        }

        init = false

        tv1["A", 1][CellHeight] = 55
        tv1["A", 1][CellWidth] = 60
        tv1["A", 1][CellClasses] = "cell-classes-2"
        tv1["A", 1][CellTopics] = "cell-topics-2"
        tv1["A", 1][CellTransformer] = ct2
    }

    @Test
    fun `event values columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ct1: Cell<*>.() -> Unit = {}
        val ct2: Cell<*>.() -> Unit = {}

        //tv1["A"][CellHeight] = 25
        tv1["A"][CellWidth] = 30
        tv1["A"][CellClasses] = "cell-classes-1"
        tv1["A"][CellTopics] = "cell-topics-1"
        //tv1["A"][CellTransformer] = ct1

        var init = true

        on(tv1) events {
            if (init) {
                forEach {
                    when (it.newValue) {
                        is CellWidth<*, *> -> {
                            assertEquals(Unit, (it.oldValue as CellWidth<*, *>).width)
                            assertEquals(30L, (it.newValue as CellWidth<*, *>).width)
                            assertEquals(oldView["A"], (it.oldValue as CellWidth<*, *>).source)
                            assertEquals(newView["A"], (it.newValue as CellWidth<*, *>).source)
                        }

                        is CellClasses<*> -> {
                            assertEquals(emptySet(), (it.oldValue as CellClasses<*>).classes)
                            assertEquals(setOf("cell-classes-1"), (it.newValue as CellClasses<*>).classes)
                            assertEquals(oldView["A"], (it.oldValue as CellClasses<*>).source)
                            assertEquals(newView["A"], (it.newValue as CellClasses<*>).source)
                        }

                        is CellTopics<*> -> {
                            assertEquals(emptySet(), (it.oldValue as CellTopics<*>).topics)
                            assertEquals(setOf("cell-topics-1"), (it.newValue as CellTopics<*>).topics)
                            assertEquals(oldView["A"], (it.oldValue as CellTopics<*>).source)
                            assertEquals(newView["A"], (it.newValue as CellTopics<*>).source)
                        }
                        /*
                        is CellTransformer<*> -> {
                            assertEquals(Unit, (it.oldValue as CellTransformer<*>).function)
                            assertEquals(ct1, (it.newValue as CellTransformer<*>).function)
                            assertEquals(oldView["A"], (it.oldValue as CellTransformer<*>).source)
                            assertEquals(newView["A"], (it.newValue as CellTransformer<*>).source)
                        }
                         */
                        else -> assertTrue(false)
                    }
                }
            } else {
                forEach {
                    when (it.newValue) {
                        is CellWidth<*, *> -> {
                            assertEquals(30L, (it.oldValue as CellWidth<*, *>).width)
                            assertEquals(60L, (it.newValue as CellWidth<*, *>).width)
                            assertEquals(oldView["A"], (it.oldValue as CellWidth<*, *>).source)
                            assertEquals(newView["A"], (it.newValue as CellWidth<*, *>).source)
                        }

                        is CellClasses<*> -> {
                            assertEquals(setOf("cell-classes-1"), (it.oldValue as CellClasses<*>).classes)
                            assertEquals(setOf("cell-classes-2"), (it.newValue as CellClasses<*>).classes)
                            assertEquals(oldView["A"], (it.oldValue as CellClasses<*>).source)
                            assertEquals(newView["A"], (it.newValue as CellClasses<*>).source)
                        }

                        is CellTopics<*> -> {
                            assertEquals(setOf("cell-topics-1"), (it.oldValue as CellTopics<*>).topics)
                            assertEquals(setOf("cell-topics-2"), (it.newValue as CellTopics<*>).topics)
                            assertEquals(oldView["A"], (it.oldValue as CellTopics<*>).source)
                            assertEquals(newView["A"], (it.newValue as CellTopics<*>).source)
                        }
                        /*
                        is CellTransformer<*> -> {
                            assertEquals(ct1, (it.oldValue as CellTransformer<*>).function)
                            assertEquals(ct2, (it.newValue as CellTransformer<*>).function)
                            assertEquals(oldView["A"], (it.oldValue as CellTransformer<*>).source)
                            assertEquals(newView["A"], (it.newValue as CellTransformer<*>).source)
                        }
                         */
                        else -> assertTrue(false)
                    }
                }
            }
        }

        init = false

        //tv1["A"][CellHeight] = 55
        tv1["A"][CellWidth] = 60
        tv1["A"][CellClasses] = "cell-classes-2"
        tv1["A"][CellTopics] = "cell-topics-2"
        //tv1["A"][CellTransformer] = ct2
    }

    @Test
    fun `event values rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ct1: Cell<*>.() -> Unit = {}
        val ct2: Cell<*>.() -> Unit = {}

        tv1[1][CellHeight] = 25
        //tv1[1][CellWidth] = 30
        tv1[1][CellClasses] = "cell-classes-1"
        tv1[1][CellTopics] = "cell-topics-1"
        //tv1[1][CellTransformer] = ct1

        var init = true

        on(tv1) events {
            if (init) {
                forEach {
                    when (it.newValue) {
                        is CellHeight<*, *> -> {
                            assertEquals(Unit, (it.oldValue as CellHeight<*, *>).height)
                            assertEquals(25L, (it.newValue as CellHeight<*, *>).height)
                            assertEquals(oldView[1], (it.oldValue as CellHeight<*, *>).source)
                            assertEquals(newView[1], (it.newValue as CellHeight<*, *>).source)
                        }

                        is CellClasses<*> -> {
                            assertEquals(emptySet(), (it.oldValue as CellClasses<*>).classes)
                            assertEquals(setOf("cell-classes-1"), (it.newValue as CellClasses<*>).classes)
                            assertEquals(oldView[1], (it.oldValue as CellClasses<*>).source)
                            assertEquals(newView[1], (it.newValue as CellClasses<*>).source)
                        }

                        is CellTopics<*> -> {
                            assertEquals(emptySet(), (it.oldValue as CellTopics<*>).topics)
                            assertEquals(setOf("cell-topics-1"), (it.newValue as CellTopics<*>).topics)
                            assertEquals(oldView[1], (it.oldValue as CellTopics<*>).source)
                            assertEquals(newView[1], (it.newValue as CellTopics<*>).source)
                        }
                        /*
                        is CellTransformer<*> -> {
                            assertEquals(Unit, (it.oldValue as CellTransformer<*>).function)
                            assertEquals(ct1, (it.newValue as CellTransformer<*>).function)
                            assertEquals(oldView[1], (it.oldValue as CellTransformer<*>).source)
                            assertEquals(newView[1], (it.newValue as CellTransformer<*>).source)
                        }
                         */
                        else -> assertTrue(false)
                    }
                }
            } else {
                forEach {
                    when (it.newValue) {
                        is CellHeight<*, *> -> {
                            assertEquals(25L, (it.oldValue as CellHeight<*, *>).height)
                            assertEquals(55L, (it.newValue as CellHeight<*, *>).height)
                            assertEquals(oldView[1], (it.oldValue as CellHeight<*, *>).source)
                            assertEquals(newView[1], (it.newValue as CellHeight<*, *>).source)
                        }

                        is CellClasses<*> -> {
                            assertEquals(setOf("cell-classes-1"), (it.oldValue as CellClasses<*>).classes)
                            assertEquals(setOf("cell-classes-2"), (it.newValue as CellClasses<*>).classes)
                            assertEquals(oldView[1], (it.oldValue as CellClasses<*>).source)
                            assertEquals(newView[1], (it.newValue as CellClasses<*>).source)
                        }

                        is CellTopics<*> -> {
                            assertEquals(setOf("cell-topics-1"), (it.oldValue as CellTopics<*>).topics)
                            assertEquals(setOf("cell-topics-2"), (it.newValue as CellTopics<*>).topics)
                            assertEquals(oldView[1], (it.oldValue as CellTopics<*>).source)
                            assertEquals(newView[1], (it.newValue as CellTopics<*>).source)
                        }
                        /*
                        is CellTransformer<*> -> {
                            assertEquals(ct1, (it.oldValue as CellTransformer<*>).function)
                            assertEquals(ct2, (it.newValue as CellTransformer<*>).function)
                            assertEquals(oldView[1], (it.oldValue as CellTransformer<*>).source)
                            assertEquals(newView[1], (it.newValue as CellTransformer<*>).source)
                        }
                         */
                        else -> assertTrue(false)
                    }
                }
            }
        }

        init = false

        tv1[1][CellHeight] = 55
        //tv1[1][CellWidth] = 60
        tv1[1][CellClasses] = "cell-classes-2"
        tv1[1][CellTopics] = "cell-topics-2"
        //tv1[1][CellTransformer] = ct2
    }

    @Test
    fun `event values tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val t1 = Table[null]
        val t2 = Table[null]

        val ct1: Cell<*>.() -> Unit = {}
        val ct2: Cell<*>.() -> Unit = {}

        val r1: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> = "a" to {}
        val r2: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> = "b" to {}

        tv1[CellHeight] = 25
        tv1[CellWidth] = 30
        tv1[CellClasses] = "cell-classes-1"
        tv1[CellTopics] = "cell-topics-1"
        //tv1[CellTransformer] = ct1
        tv1[Resources] = r1
        tv1[Table] = t1

        var init = true

        on(tv1) events {
            if (init) {
                forEach {
                    when (it.newValue) {
                        is CellHeight<*, *> -> {
                            assertEquals(Unit, (it.oldValue as CellHeight<*, *>).height)
                            assertEquals(25L, (it.newValue as CellHeight<*, *>).height)
                            assertEquals(oldView, (it.oldValue as CellHeight<*, *>).source)
                            assertEquals(newView, (it.newValue as CellHeight<*, *>).source)
                        }

                        is CellWidth<*, *> -> {
                            assertEquals(Unit, (it.oldValue as CellWidth<*, *>).width)
                            assertEquals(30L, (it.newValue as CellWidth<*, *>).width)
                            assertEquals(oldView, (it.oldValue as CellWidth<*, *>).source)
                            assertEquals(newView, (it.newValue as CellWidth<*, *>).source)
                        }

                        is CellClasses<*> -> {
                            assertEquals(emptySet(), (it.oldValue as CellClasses<*>).classes)
                            assertEquals(setOf("cell-classes-1"), (it.newValue as CellClasses<*>).classes)
                            assertEquals(oldView, (it.oldValue as CellClasses<*>).source)
                            assertEquals(newView, (it.newValue as CellClasses<*>).source)
                        }

                        is CellTopics<*> -> {
                            assertEquals(emptySet(), (it.oldValue as CellTopics<*>).topics)
                            assertEquals(setOf("cell-topics-1"), (it.newValue as CellTopics<*>).topics)
                            assertEquals(oldView, (it.oldValue as CellTopics<*>).source)
                            assertEquals(newView, (it.newValue as CellTopics<*>).source)
                        }
                        /*
                        is CellTransformer<*> -> {
                            assertEquals(Unit, (it.oldValue as CellTransformer<*>).function)
                            assertEquals(ct1, (it.newValue as CellTransformer<*>).function)
                            assertEquals(oldView, (it.oldValue as CellTransformer<*>).source)
                            assertEquals(newView, (it.newValue as CellTransformer<*>).source)
                        }
                         */
                        is Resources -> {
                            assertEquals(emptyMap(), (it.oldValue as Resources).resources)
                            assertEquals(mapOf(r1), (it.newValue as Resources).resources)
                            assertEquals(oldView, (it.oldValue as Resources).source)
                            assertEquals(newView, (it.newValue as Resources).source)
                        }

                        is SourceTable -> {
                            assertEquals(null, (it.oldValue as SourceTable).table)
                            assertEquals(t1, (it.newValue as SourceTable).table)
                            assertEquals(oldView, (it.oldValue as SourceTable).source)
                            assertEquals(newView, (it.newValue as SourceTable).source)
                        }

                        else -> assertTrue(false)
                    }
                }
            } else {
                forEach {
                    when (it.newValue) {
                        is CellHeight<*, *> -> {
                            assertEquals(25L, (it.oldValue as CellHeight<*, *>).height)
                            assertEquals(55L, (it.newValue as CellHeight<*, *>).height)
                            assertEquals(oldView, (it.oldValue as CellHeight<*, *>).source)
                            assertEquals(newView, (it.newValue as CellHeight<*, *>).source)
                        }

                        is CellWidth<*, *> -> {
                            assertEquals(30L, (it.oldValue as CellWidth<*, *>).width)
                            assertEquals(60L, (it.newValue as CellWidth<*, *>).width)
                            assertEquals(oldView, (it.oldValue as CellWidth<*, *>).source)
                            assertEquals(newView, (it.newValue as CellWidth<*, *>).source)
                        }

                        is CellClasses<*> -> {
                            assertEquals(setOf("cell-classes-1"), (it.oldValue as CellClasses<*>).classes)
                            assertEquals(setOf("cell-classes-2"), (it.newValue as CellClasses<*>).classes)
                            assertEquals(oldView, (it.oldValue as CellClasses<*>).source)
                            assertEquals(newView, (it.newValue as CellClasses<*>).source)
                        }

                        is CellTopics<*> -> {
                            assertEquals(setOf("cell-topics-1"), (it.oldValue as CellTopics<*>).topics)
                            assertEquals(setOf("cell-topics-2"), (it.newValue as CellTopics<*>).topics)
                            assertEquals(oldView, (it.oldValue as CellTopics<*>).source)
                            assertEquals(newView, (it.newValue as CellTopics<*>).source)
                        }
                        /*
                        is CellTransformer<*> -> {
                            assertEquals(ct1, (it.oldValue as CellTransformer<*>).function)
                            assertEquals(ct2, (it.newValue as CellTransformer<*>).function)
                            assertEquals(oldView, (it.oldValue as CellTransformer<*>).source)
                            assertEquals(newView, (it.newValue as CellTransformer<*>).source)
                        }
                         */
                        is Resources -> {
                            assertEquals(mapOf(r1), (it.oldValue as Resources).resources)
                            assertEquals(mapOf(r2), (it.newValue as Resources).resources)
                            assertEquals(oldView, (it.oldValue as Resources).source)
                            assertEquals(newView, (it.newValue as Resources).source)
                        }

                        is SourceTable -> {
                            assertEquals(t1, (it.oldValue as SourceTable).table)
                            assertEquals(t2, (it.newValue as SourceTable).table)
                            assertEquals(oldView, (it.oldValue as SourceTable).source)
                            assertEquals(newView, (it.newValue as SourceTable).source)
                        }

                        else -> assertTrue(false)
                    }
                }
            }
        }

        init = false

        tv1[CellHeight] = 55
        tv1[CellWidth] = 60
        tv1[CellClasses] = "cell-classes-2"
        tv1[CellTopics] = "cell-topics-2"
        //tv1[CellTransformer] = ct2
        tv1[Resources] = r2
        tv1[Table] = t2
    }

    @Test
    fun `type filtering for subscriptions`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val t1 = Table[null]
        val t2 = Table[null]

        val ct1: Cell<*>.() -> Unit = {}
        val ct2: Cell<*>.() -> Unit = {}

        val r1: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> = "a" to {}
        val r2: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> = "b" to {}

        tv1[CellHeight] = 25
        tv1[CellWidth] = 30
        tv1[CellClasses] = "cell-classes-1"
        tv1[CellTopics] = "cell-topics-1"
        //tv1[CellTransformer] = ct1
        tv1[Resources] = r1
        tv1[Table] = t1

        var eventCount1 = 0
        var eventCount2 = 0
        var eventCount3 = 0
        var eventCount4 = 0
        var eventCount5 = 0
        var eventCount6 = 0

        on<CellHeight<*, *>>(tv1) events {
            eventCount1 += count()
        }

        on<CellWidth<*, *>>(tv1) events {
            eventCount2 += count()
        }

        on<CellClasses<*>>(tv1) events {
            eventCount3 += count()
        }

        on<CellTopics<*>>(tv1) events {
            eventCount4 += count()
        }

        on<Resources>(tv1) events {
            eventCount5 += count()
        }

        on<SourceTable>(tv1) events {
            eventCount6 += count()
        }

        assertEquals(1, eventCount1)
        assertEquals(1, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(1, eventCount6)

        tv1[CellHeight] = 45

        assertEquals(2, eventCount1)
        assertEquals(1, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(1, eventCount6)

        tv1[CellWidth] = 60

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(1, eventCount6)

        tv1[CellClasses] = "cell-classes-2"

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(1, eventCount6)

        tv1[CellTopics] = "cell-topics-2"

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(2, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(1, eventCount6)

        tv1[Resources] = r2

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(2, eventCount4)
        assertEquals(2, eventCount5)
        assertEquals(1, eventCount6)

        tv1[Table] = t2

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(2, eventCount4)
        assertEquals(2, eventCount5)
        assertEquals(2, eventCount6)
    }

    @Test
    fun `type filtering for chained subscriptions`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount1 = 0
        var eventCount2 = 0

        on<CellWidth<*,*>>(tv1, name = "Listener 1") events {
            eventCount1 += count()
            forEach {
                oldView[it.columnView!!, it.index!!][CellWidth] { 100 }
                newView[it.columnView!!, it.index!!][CellWidth] = 200
            }
        }

        on<CellWidth<*,*>>(tv1, name = "Listener 2") events {
            eventCount2 += count()
            forEach {
                assertEquals(PixelCellWidth::class, it.oldValue::class)
                assertEquals(PixelCellWidth::class, it.newValue::class)
            }
        }

        tv1["A", 1][CellWidth] = 50

        assertEquals(1, eventCount1)
        assertEquals(1, eventCount2)

        tv1["A", 1][CellWidth] = null

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
    }

    @Test
    fun `columnview on header`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ct: Column.() -> Unit = {}
        var count = 0

        on(tv1["B"], skipHistory = true) events {
            count += count()

            forEach {
                when (it.newValue) {
                    is CellClasses<*> -> {
                        val old = it.oldValue as CellClasses<*>
                        val new = it.newValue as CellClasses<*>
                        assertEquals(emptySet(), old.classes)
                        assertEquals(setOf("cc-1"), new.classes)
                    }
                    is CellTopics<*> -> {
                        val old = it.oldValue as CellTopics<*>
                        val new = it.newValue as CellTopics<*>
                        assertEquals(emptySet(), old.topics)
                        assertEquals(setOf("ct-1"), new.topics)
                    }
                    is ColumnTransformer<*> -> {
                        val old = it.oldValue as ColumnTransformer<*>
                        val new = it.newValue as ColumnTransformer<*>
                        assertEquals(Unit, old.function)
                        assertEquals(ct, new.function)
                    }
                    is CellWidth<*, *> -> {
                        val old = it.oldValue as CellWidth<*, *>
                        val new = it.newValue as CellWidth<*, *>
                        assertEquals(Unit, old.width)
                        assertEquals(1000L, new.width)
                    }
                    else -> assertTrue(false)
                }
            }
        }

        tv1["A"][CellClasses] = "cc-1"
        tv1["A"][CellTopics] = "ct-1"
        tv1["A"][ColumnTransformer] = ct
        tv1["A"][CellWidth] = 1000

        assertEquals(0, count)
        tv1["B"] = tv1["A"]
        assertEquals(4, count)
    }

    @Test
    fun `rowview on row`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val rt: Row.() -> Unit = {}
        var count = 0

        on(tv1[2], skipHistory = true) events {
            count += count()

            forEach {
                when (it.newValue) {
                    is CellClasses<*> -> {
                        val old = it.oldValue as CellClasses<*>
                        val new = it.newValue as CellClasses<*>
                        assertEquals(emptySet(), old.classes)
                        assertEquals(setOf("cc-1"), new.classes)
                    }
                    is CellTopics<*> -> {
                        val old = it.oldValue as CellTopics<*>
                        val new = it.newValue as CellTopics<*>
                        assertEquals(emptySet(), old.topics)
                        assertEquals(setOf("ct-1"), new.topics)
                    }
                    is RowTransformer<*> -> {
                        val old = it.oldValue as RowTransformer<*>
                        val new = it.newValue as RowTransformer<*>
                        assertEquals(Unit, old.function)
                        assertEquals(rt, new.function)
                    }
                    is CellHeight<*, *> -> {
                        val old = it.oldValue as CellHeight<*, *>
                        val new = it.newValue as CellHeight<*, *>
                        assertEquals(Unit, old.height)
                        assertEquals(1000L, new.height)
                    }
                    else -> assertTrue(false)
                }
            }
        }

        tv1[1][CellClasses] = "cc-1"
        tv1[1][CellTopics] = "ct-1"
        tv1[1][RowTransformer] = rt
        tv1[1][CellHeight] = 1000

        assertEquals(0, count)
        tv1[2] = tv1[1]
        assertEquals(4, count)
    }

    @Test
    fun `cellview on cell`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var count = 0

        val ct: Cell<*>.() -> Unit = {}

        on(tv1["B", 2], skipHistory = true) events {
            count += count()

            forEach {
                when (it.newValue) {
                    is CellClasses<*> -> {
                        val old = it.oldValue as CellClasses<*>
                        val new = it.newValue as CellClasses<*>
                        assertEquals(emptySet(), old.classes)
                        assertEquals(setOf("cc-1"), new.classes)
                    }
                    is CellTopics<*> -> {
                        val old = it.oldValue as CellTopics<*>
                        val new = it.newValue as CellTopics<*>
                        assertEquals(emptySet(), old.topics)
                        assertEquals(setOf("ct-1"), new.topics)
                    }
                    is CellTransformer<*> -> {
                        val old = it.oldValue as CellTransformer<*>
                        val new = it.newValue as CellTransformer<*>
                        assertEquals(Unit, old.function)
                        assertEquals(ct, new.function)
                    }
                    is CellHeight<*, *> -> {
                        val old = it.oldValue as CellHeight<*, *>
                        val new = it.newValue as CellHeight<*, *>
                        assertEquals(Unit, old.height)
                        assertEquals(1000L, new.height)
                    }
                    is CellWidth<*, *> -> {
                        val old = it.oldValue as CellWidth<*, *>
                        val new = it.newValue as CellWidth<*, *>
                        assertEquals(Unit, old.width)
                        assertEquals(2000L, new.width)
                    }
                    else -> assertTrue(false)
                }
            }
        }

        tv1["A", 1][CellClasses] = "cc-1"
        tv1["A", 1][CellTopics] = "ct-1"
        tv1["A", 1][CellTransformer] = ct
        tv1["A", 1][CellHeight] = 1000
        tv1["A", 1][CellWidth] = 2000

        assertEquals(0, count)
        tv1["B", 2] = tv1["A", 1]
        assertEquals(5, count)
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
            TableView.views.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}
