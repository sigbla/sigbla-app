/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import sigbla.app.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class CellViewBatchListenerTest {
    @Test
    fun `subscribe and unsubscribe cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        val ref = on(tv1["A", 1]) {
            events {
                val c = count()
                assertEquals(1, c)
                eventCount += c
            }
        }

        batch(tv1) {
            tv1["A", 1][CellHeight] = 25
            tv1["A", 1][CellWidth] = 25
            tv1["A", 1][CellClasses] = "cell-classes-1"
            tv1["A", 1][CellTopics] = "cell-topics-1"
            tv1["A", 1][CellTransformer] = {}

            assertEquals(0, eventCount)

            tv1["A", 1][CellHeight] = 50
            tv1["A", 1][CellWidth] = 50
            tv1["A", 1][CellClasses] = "cell-classes-2"
            tv1["A", 1][CellTopics] = "cell-topics-2"
            tv1["A", 1][CellTransformer] = {}

            assertEquals(0, eventCount)

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

            assertEquals(0, eventCount)

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

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and unsubscribe columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        val ref = on(tv1["A", 1]) {
            events {
                val c = count()
                assertEquals(1, c)
                eventCount += c
            }
        }

        batch(tv1) {
            //tv1["A"][CellHeight] = 25
            tv1["A"][CellWidth] = 25
            tv1["A"][CellClasses] = "cell-classes-1"
            tv1["A"][CellTopics] = "cell-topics-1"
            tv1["A"][Position] = Position.Left
            tv1["A"][Visibility] = Visibility.Show
            //tv1["A"][CellTransformer] = {}

            assertEquals(0, eventCount)

            //tv1["A"][CellHeight] = 50
            tv1["A"][CellWidth] = 50
            tv1["A"][CellClasses] = "cell-classes-2"
            tv1["A"][CellTopics] = "cell-topics-2"
            tv1["A"][Position] = Position.Right
            tv1["A"][Visibility] = Visibility.Hide
            //tv1["A"][CellTransformer] = {}

            assertEquals(0, eventCount)

            //tv1["A"][CellHeight] = 100
            tv1["A"][CellWidth] = 100
            tv1["A"][CellClasses] = "cell-classes-3"
            tv1["A"][CellTopics] = "cell-topics-3"
            tv1["A"][Position] = Unit
            tv1["A"][Visibility] = Unit
            //tv1["A"][CellTransformer] = {}

            //tv1["B"][CellHeight] = 125
            tv1["B"][CellWidth] = 125
            tv1["B"][CellClasses] = "cell-classes-4"
            tv1["B"][CellTopics] = "cell-topics-4"
            tv1["B"][Position] = Position.Right
            tv1["B"][Visibility] = Visibility.Show
            //tv1["B"][CellTransformer] = {}

            assertEquals(0, eventCount)

            off(ref)

            //tv1["B"][CellHeight] = 150
            tv1["B"][CellWidth] = 150
            tv1["B"][CellClasses] = "cell-classes-5"
            tv1["B"][CellTopics] = "cell-topics-5"
            tv1["B"][Position] = Unit
            tv1["B"][Visibility] = Unit
            //tv1["B"][CellTransformer] = {}

            //tv1["C"][CellHeight] = 175
            tv1["C"][CellWidth] = 175
            tv1["C"][CellClasses] = "cell-classes-6"
            tv1["C"][CellTopics] = "cell-topics-6"
            tv1["C"][Position] = Position.Left
            tv1["C"][Visibility] = Visibility.Show
            //tv1["C"][CellTransformer] = {}

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and unsubscribe rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        val ref = on(tv1["A", 1]) {
            events {
                val c = count()
                assertEquals(1, c)
                eventCount += c
            }
        }

        batch(tv1) {
            tv1[1][CellHeight] = 25
            //tv1[1][CellWidth] = 25
            tv1[1][CellClasses] = "cell-classes-1"
            tv1[1][CellTopics] = "cell-topics-1"
            tv1[1][Position] = Position.Top
            tv1[1][Visibility] = Visibility.Show
            //tv1[1][CellTransformer] = {}

            assertEquals(0, eventCount)

            tv1[1][CellHeight] = 50
            //tv1[1][CellWidth] = 50
            tv1[1][CellClasses] = "cell-classes-2"
            tv1[1][CellTopics] = "cell-topics-2"
            tv1[1][Position] = Position.Bottom
            tv1[1][Visibility] = Visibility.Hide
            //tv1[1][CellTransformer] = {}

            assertEquals(0, eventCount)

            tv1[2][CellHeight] = 100
            //tv1[2][CellWidth] = 100
            tv1[2][CellClasses] = "cell-classes-3"
            tv1[2][CellTopics] = "cell-topics-3"
            tv1[2][Position] = Unit
            tv1[2][Visibility] = Unit
            //tv1[2][CellTransformer] = {}

            tv1[3][CellHeight] = 125
            //tv1[3][CellWidth] = 125
            tv1[3][CellClasses] = "cell-classes-4"
            tv1[3][CellTopics] = "cell-topics-4"
            tv1[3][Position] = Position.Top
            tv1[3][Visibility] = Visibility.Show
            //tv1[3][CellTransformer] = {}

            assertEquals(0, eventCount)

            off(ref)

            tv1[3][CellHeight] = 150
            //tv1[3][CellWidth] = 150
            tv1[3][CellClasses] = "cell-classes-5"
            tv1[3][CellTopics] = "cell-topics-5"
            tv1[3][Position] = Unit
            tv1[3][Visibility] = Unit
            //tv1[3][CellTransformer] = {}

            tv1[1][CellHeight] = 175
            //tv1[1][CellWidth] = 175
            tv1[1][CellClasses] = "cell-classes-6"
            tv1[1][CellTopics] = "cell-topics-6"
            tv1[1][Position] = Position.Top
            tv1[1][Visibility] = Visibility.Show
            //tv1[1][CellTransformer] = {}

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and unsubscribe tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        val ref = on(tv1["A", 1]) {
            events {
                val c = count()
                assertEquals(1, c)
                eventCount += c
            }
        }

        batch(tv1) {
            tv1[CellHeight] = 25
            tv1[CellWidth] = 25
            tv1[CellClasses] = "cell-classes-1"
            tv1[CellTopics] = "cell-topics-1"
            //tv1[CellTransformer] = {}
            tv1[Resource["a"]] = {}
            tv1[Table] = Table[null]

            assertEquals(0, eventCount)

            tv1[CellHeight] = 50
            tv1[CellWidth] = 50
            tv1[CellClasses] = "cell-classes-2"
            tv1[CellTopics] = "cell-topics-2"
            //tv1[CellTransformer] = {}
            tv1[Resource["b"]] = {}
            tv1[Table] = Table[null]

            assertEquals(0, eventCount)

            off(ref)

            tv1[CellHeight] = 150
            tv1[CellWidth] = 150
            tv1[CellClasses] = "cell-classes-3"
            tv1[CellTopics] = "cell-topics-3"
            //tv1[CellTransformer] = {}
            tv1[Resource["c"]] = {}
            tv1[Table] = Table[null]

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            tv1["A", 1][CellHeight] = 25
            tv1["A", 1][CellWidth] = 25
            tv1["A", 1][CellClasses] = "cell-classes-1"
            tv1["A", 1][CellTopics] = "cell-topics-1"
            tv1["A", 1][CellTransformer] = {}
        }

        val ref = on(tv1["A", 1]) {
            events {
                eventCount += count()
            }
        }

        batch(tv1) {
            assertEquals(5, eventCount)

            tv1["A", 1][CellHeight] = 50
            tv1["A", 1][CellWidth] = 50
            tv1["A", 1][CellClasses] = "cell-classes-2"
            tv1["A", 1][CellTopics] = "cell-topics-2"
            tv1["A", 1][CellTransformer] = {}

            assertEquals(5, eventCount)

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

            assertEquals(5, eventCount)

            off(ref)

            tv1["B", 3][CellHeight] = 125
            tv1["B", 3][CellWidth] = 125
            tv1["B", 3][CellClasses] = "cell-classes-5"
            tv1["B", 3][CellTopics] = "cell-topics-5"
            tv1["B", 3][CellTransformer] = {}

            tv1["A", 1][CellHeight] = 150
            tv1["A", 1][CellWidth] = 150
            tv1["A", 1][CellClasses] = "cell-classes-6"
            tv1["A", 1][CellTopics] = "cell-topics-6"
            tv1["A", 1][CellTransformer] = {}

            assertEquals(5, eventCount)
        }

        assertEquals(5, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            //tv1["A"][CellHeight] = 25
            tv1["A"][CellWidth] = 25
            tv1["A"][CellClasses] = "cell-classes-1"
            tv1["A"][CellTopics] = "cell-topics-1"
            tv1["A"][Position] = Position.Left
            tv1["A"][Visibility] = Visibility.Show
            //tv1["A"][CellTransformer] = {}
        }

        val ref = on(tv1["A", 1]) {
            events {
                eventCount += count()
            }
        }

        batch(tv1) {
            assertEquals(0, eventCount)

            //tv1["A"][CellHeight] = 50
            tv1["A"][CellWidth] = 50
            tv1["A"][CellClasses] = "cell-classes-2"
            tv1["A"][CellTopics] = "cell-topics-2"
            tv1["A"][Position] = Position.Right
            tv1["A"][Visibility] = Visibility.Hide
            //tv1["A"][CellTransformer] = {}

            assertEquals(0, eventCount)

            //tv1["A"][CellHeight] = 75
            tv1["A"][CellWidth] = 75
            tv1["A"][CellClasses] = "cell-classes-3"
            tv1["A"][CellTopics] = "cell-topics-3"
            tv1["A"][Position] = Unit
            tv1["A"][Visibility] = Unit
            //tv1["A"][CellTransformer] = {}

            //tv1["B"][CellHeight] = 100
            tv1["B"][CellWidth] = 100
            tv1["B"][CellClasses] = "cell-classes-4"
            tv1["B"][CellTopics] = "cell-topics-4"
            tv1["B"][Position] = Position.Left
            tv1["B"][Visibility] = Visibility.Show
            //tv1["B"][CellTransformer] = {}

            assertEquals(0, eventCount)

            off(ref)

            //tv1["B"][CellHeight] = 125
            tv1["B"][CellWidth] = 125
            tv1["B"][CellClasses] = "cell-classes-5"
            tv1["B"][CellTopics] = "cell-topics-5"
            tv1["B"][Position] = Unit
            tv1["B"][Visibility] = Unit
            //tv1["B"][CellTransformer] = {}

            //tv1["A"][CellHeight] = 150
            tv1["A"][CellWidth] = 150
            tv1["A"][CellClasses] = "cell-classes-6"
            tv1["A"][CellTopics] = "cell-topics-6"
            tv1["A"][Position] = Position.Left
            tv1["A"][Visibility] = Visibility.Show
            //tv1["A"][CellTransformer] = {}

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            tv1[1][CellHeight] = 25
            //tv1[1][CellWidth] = 25
            tv1[1][CellClasses] = "cell-classes-1"
            tv1[1][CellTopics] = "cell-topics-1"
            tv1[1][Position] = Position.Top
            tv1[1][Visibility] = Visibility.Show
            //tv1[1][CellTransformer] = {}
        }

        val ref = on(tv1["A", 1]) {
            events {
                eventCount += count()
            }
        }

        batch(tv1) {
            assertEquals(0, eventCount)

            tv1[1][CellHeight] = 50
            //tv1[1][CellWidth] = 50
            tv1[1][CellClasses] = "cell-classes-2"
            tv1[1][CellTopics] = "cell-topics-2"
            tv1[1][Position] = Position.Bottom
            tv1[1][Visibility] = Visibility.Hide
            //tv1[1][CellTransformer] = {}

            assertEquals(0, eventCount)

            tv1[2][CellHeight] = 75
            //tv1[2][CellWidth] = 75
            tv1[2][CellClasses] = "cell-classes-3"
            tv1[2][CellTopics] = "cell-topics-3"
            tv1[2][Position] = Position.Top
            tv1[2][Visibility] = Visibility.Show
            //tv1[2][CellTransformer] = {}

            tv1[3][CellHeight] = 100
            //tv1[3][CellWidth] = 100
            tv1[3][CellClasses] = "cell-classes-4"
            tv1[3][CellTopics] = "cell-topics-4"
            tv1[3][Position] = Position.Top
            tv1[3][Visibility] = Visibility.Show
            //tv1[3][CellTransformer] = {}

            assertEquals(0, eventCount)

            off(ref)

            tv1[3][CellHeight] = 125
            //tv1[3][CellWidth] = 125
            tv1[3][CellClasses] = "cell-classes-5"
            tv1[3][CellTopics] = "cell-topics-5"
            tv1[3][Position] = Unit
            tv1[3][Visibility] = Unit
            //tv1[3][CellTransformer] = {}

            tv1[1][CellHeight] = 150
            //tv1[1][CellWidth] = 150
            tv1[1][CellClasses] = "cell-classes-6"
            tv1[1][CellTopics] = "cell-topics-6"
            tv1[1][Position] = Position.Top
            tv1[1][Visibility] = Visibility.Show
            //tv1[1][CellTransformer] = {}

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and unsubscribe tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            tv1[CellHeight] = 25
            tv1[CellWidth] = 25
            tv1[CellClasses] = "cell-classes-1"
            tv1[CellTopics] = "cell-topics-1"
            //tv1[CellTransformer] = {}
            tv1[Resource["a"]] = {}
            tv1[Table] = Table[null]
        }

        val ref = on(tv1["A", 1]) {
            events {
                eventCount += count()
            }
        }

        batch(tv1) {
            assertEquals(0, eventCount)

            tv1[CellHeight] = 50
            tv1[CellWidth] = 50
            tv1[CellClasses] = "cell-classes-2"
            tv1[CellTopics] = "cell-topics-2"
            //tv1[CellTransformer] = {}
            tv1[Resource["b"]] = {}
            tv1[Table] = Table[null]

            assertEquals(0, eventCount)

            off(ref)

            tv1[CellHeight] = 125
            tv1[CellWidth] = 125
            tv1[CellClasses] = "cell-classes-5"
            tv1[CellTopics] = "cell-topics-5"
            //tv1[CellTransformer] = {}
            tv1[Resource["c"]] = {}
            tv1[Table] = Table[null]

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            on(tv1["A", 1]) {
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

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            on(tv1["A", 1]) {
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
            tv1["A"][Position] = Position.Left
            tv1["A"][Visibility] = Visibility.Show
            //tv1["A"][CellTransformer] = {}

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            on(tv1["A", 1]) {
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
            tv1[1][Position] = Position.Top
            tv1[1][Visibility] = Visibility.Show
            //tv1[1][CellTransformer] = {}

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe and instant unsubscribe tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            on(tv1["A", 1]) {
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
            tv1[Resource["a"]] = {}
            tv1[Table] = Table[null]

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            tv1["A", 1][CellHeight] = 25
            tv1["A", 1][CellWidth] = 25
            tv1["A", 1][CellClasses] = "cell-classes-1"
            tv1["A", 1][CellTopics] = "cell-topics-1"
            tv1["A", 1][CellTransformer] = {}
        }

        batch(tv1) {
            on(tv1["A", 1]) {
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

        assertEquals(5, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            //tv1["A"][CellHeight] = 25
            tv1["A"][CellWidth] = 25
            tv1["A"][CellClasses] = "cell-classes-1"
            tv1["A"][CellTopics] = "cell-topics-1"
            tv1["A"][Position] = Position.Left
            tv1["A"][Visibility] = Visibility.Show
            //tv1["A"][CellTransformer] = {}
        }

        batch(tv1) {
            on(tv1["A", 1]) {
                off(this)

                events {
                    eventCount += count()
                }
            }

            assertEquals(0, eventCount)

            //tv1["A"][CellHeight] = 50
            tv1["A"][CellWidth] = 50
            tv1["A"][CellClasses] = "cell-classes-2"
            tv1["A"][CellTopics] = "cell-topics-2"
            tv1["A"][Position] = Position.Right
            tv1["A"][Visibility] = Visibility.Hide
            //tv1["A"][CellTransformer] = {}

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            tv1[1][CellHeight] = 25
            //tv1[1][CellWidth] = 25
            tv1[1][CellClasses] = "cell-classes-1"
            tv1[1][CellTopics] = "cell-topics-1"
            tv1[1][Position] = Position.Top
            tv1[1][Visibility] = Visibility.Show
            //tv1[1][CellTransformer] = {}
        }

        batch(tv1) {
            on(tv1["A", 1]) {
                off(this)

                events {
                    eventCount += count()
                }
            }

            assertEquals(0, eventCount)

            tv1[1][CellHeight] = 50
            //tv1[1][CellWidth] = 50
            tv1[1][CellClasses] = "cell-classes-2"
            tv1[1][CellTopics] = "cell-topics-2"
            tv1[1][Position] = Position.Bottom
            tv1[1][Visibility] = Visibility.Hide
            //tv1[1][CellTransformer] = {}

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `subscribe to filled table and instant unsubscribe tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount = 0

        batch(tv1) {
            tv1[CellHeight] = 25
            tv1[CellWidth] = 25
            tv1[CellClasses] = "cell-classes-1"
            tv1[CellTopics] = "cell-topics-1"
            //tv1[CellTransformer] = {}
            tv1[Resource["a"]] = {}
            tv1[Table] = Table[null]
        }

        batch(tv1) {
            on(tv1["A", 1]) {
                off(this)

                events {
                    eventCount += count()
                }
            }

            assertEquals(0, eventCount)

            tv1[CellHeight] = 50
            tv1[CellWidth] = 50
            tv1[CellClasses] = "cell-classes-2"
            tv1[CellTopics] = "cell-topics-2"
            //tv1[CellTransformer] = {}
            tv1[Resource["b"]] = {}
            tv1[Table] = Table[null]

            assertEquals(0, eventCount)
        }

        assertEquals(0, eventCount)
    }

    @Test
    fun `listener ref with name and order`() {
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        batch(t) {
            val ref = on(t["A", 1]) {
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
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        batch(t) {
            val ref = on(t["A", 1]) {}

            assertNull(ref.name)
            assertEquals(0L, ref.order)

            off(ref)
        }
    }

    @Test
    fun `listener loop support`() {
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ref2 = batch(t) {
            val ref1 = on(t["A", 0]) {
                events {
                    t["A", 0][CellWidth] = 25
                }
            }

            // No exception expected from this as event not yet produced
            t["A", 0][CellWidth] = 0

            off(ref1)

            val ref2 = on(t["A", 1]) {
                allowLoop = true

                events {
                    forEach { _ ->
                        if (t["A", 1].derived.cellWidth < 1000)
                            t["A", 1][CellWidth] = t["A", 1].derived.cellWidth + 1
                    }
                }
            }

            t["A", 1][CellWidth] = 0

            return@batch ref2
        }

        assertEquals(1000L, t["A", 1][CellWidth].asLong)

        off(ref2)
    }

    @Test
    fun `table clone and events`() {
        val t1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var t1EventCount = 0
        var t2EventCount = 0

        on(t1["A", 1]) {
            events {
                t1EventCount += count()
            }
        }

        var expectedT1EventCount = 0

        val t2 = batch(t1) {
            for (c in listOf("A", "B", "C", "D")) {
                for (r in 1..100) {
                    t1[c][r][CellHeight] = c.first().code.toLong()
                    t1[c][r][CellWidth] = r.toLong()

                    // these will be overwritten expectedT1EventCount += 2
                }
            }

            for (c in listOf("A", "B", "C", "D")) {
                for (r in 1..100) {
                    t1[c][r][CellHeight] = c.first().code.toLong()
                    t1[c][r][CellWidth] = r.toLong()

                    if (c == "A" && r == 1) expectedT1EventCount += 2
                }
            }

            return@batch clone(t1, "tableClone2")
        }

        var expectedT2EventCount = expectedT1EventCount

        on(t2["A", 1]) {
            events {
                t2EventCount += count()
            }
        }

        // Testing event separation between t1/t2
        t1["A"][1][CellHeight] = "A".toCharArray().first().code.toLong()
        t1["A"][1][CellWidth] = 1.toLong() + 100
        expectedT1EventCount += 2

        batch(t1) {
            batch(t2) {
                for (c in listOf("A", "B", "C", "D")) {
                    for (r in 1..100) {
                        t1[c][r][CellHeight] = c.first().code.toLong()
                        t1[c][r][CellWidth] = r.toLong() + 100

                        if (c == "A" && r == 1) expectedT1EventCount += 2
                    }
                }

                for (c in listOf("A", "B", "C", "D")) {
                    for (r in 1..100) {
                        t2[c][r][CellHeight] = c.first().code.toLong()
                        t2[c][r][CellWidth] = r.toLong() + 100

                        if (c == "A" && r == 1) expectedT2EventCount += 2
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
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        batch(t) {
            t["A", 1][CellHeight] = 20
            t["A", 1][CellWidth] = 25
        }

        var heightChange: Number = 0
        var widthChange: Number = 0

        batch(t) {
            on(t["A", 1]) {
                skipHistory = true

                events {
                    heightChange = newView["A", 1].derived.cellHeight - oldView["A", 1].derived.cellHeight
                    widthChange = newView["A", 1].derived.cellWidth - oldView["A", 1].derived.cellWidth
                }
            }

            t["A", 1][CellHeight] = 40
            t["A", 1][CellWidth] = 50

            t["A", 1][CellHeight] = 110
            t["A", 1][CellWidth] = 100

            Assert.assertEquals(0, heightChange)
            Assert.assertEquals(0, widthChange)
        }

        Assert.assertEquals(90L, heightChange)
        Assert.assertEquals(75L, widthChange)
    }

    @Test
    fun `recursive batching`() {
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        batch(t) {
            t["A", 1][CellHeight] = 20
            t["A", 1][CellWidth] = 25
        }

        var heightChange: Number = 0
        var widthChange: Number = 0

        batch(t) {
            on(t["A", 1]) {
                skipHistory = true

                events {
                    heightChange = newView["A", 1].derived.cellHeight - oldView["A", 1].derived.cellHeight
                    widthChange = newView["A", 1].derived.cellWidth - oldView["A", 1].derived.cellWidth
                }
            }

            t["A", 1][CellHeight] = 40
            t["A", 1][CellWidth] = 50

            batch(t) {
                t["A", 1][CellHeight] = 110
                t["A", 1][CellWidth] = 100
            }

            Assert.assertEquals(0, heightChange)
            Assert.assertEquals(0, widthChange)
        }

        Assert.assertEquals(90L, heightChange)
        Assert.assertEquals(75L, widthChange)
    }

    @Test
    fun `listener ordering`() {
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val generator = AtomicInteger()

        var id1: Int? = null
        var id2: Int? = null
        var id3: Int? = null

        batch(t) {
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

            t["A", 0][CellHeight] = 150
            t["A", 0][CellWidth] = 150

            assertNull(id1)
            assertNull(id2)
            assertNull(id3)
        }

        assertTrue((id1 ?: Int.MIN_VALUE) > (id2 ?: Int.MAX_VALUE))
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

        batch(t) {
            on(t["A", 0]) {
                skipHistory = true
                order = 2

                events {
                    v2Old = oldView["A", 0][CellWidth].asLong
                    v2New = newView["A", 0][CellWidth].asLong

                    assertEquals(t["A", 0][CellWidth], source[CellWidth])

                    newView["A", 0][CellWidth] = newView["A", 0].derived.cellWidth + 1
                    oldView["A", 0][CellWidth] = oldView["A", 0].derived.cellWidth - 1
                }
            }

            on(t["A", 0]) {
                skipHistory = true
                order = 3

                events {
                    v3Old = oldView["A", 0][CellWidth].asLong
                    v3New = newView["A", 0][CellWidth].asLong

                    assertEquals(t["A", 0][CellWidth], source[CellWidth])

                    newView["A", 0][CellWidth] = newView["A", 0].derived.cellWidth + 1
                    oldView["A", 0][CellWidth] = oldView["A", 0].derived.cellWidth - 1
                }
            }

            on(t["A", 0]) {
                skipHistory = true
                order = 1

                events {
                    v1Old = oldView["A", 0][CellWidth].asLong
                    v1New = newView["A", 0][CellWidth].asLong

                    assertEquals(t["A", 0][CellWidth], source[CellWidth])

                    newView["A", 0][CellWidth] = newView["A", 0].derived.cellWidth + 1
                    oldView["A", 0][CellWidth] = oldView["A", 0].derived.cellWidth - 1
                }
            }

            t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH * 2

            assertNull(v1New)
            assertNull(v1Old)

            assertNull(v2New)
            assertNull(v2Old)

            assertNull(v3New)
            assertNull(v3Old)
        }

        batch(t) {
            assertEquals(DEFAULT_CELL_WIDTH * 2, t["A", 0][CellWidth].width)

            t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH * 3

            assertEquals(null, v1Old)
            assertEquals(DEFAULT_CELL_WIDTH - 1, v2Old)
            assertEquals(DEFAULT_CELL_WIDTH - 2, v3Old)

            assertEquals(DEFAULT_CELL_WIDTH * 2, v1New)
            assertEquals(DEFAULT_CELL_WIDTH * 2 + 1, v2New)
            assertEquals(DEFAULT_CELL_WIDTH * 2 + 2, v3New)
        }

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

        batch(t) {
            t["A", 0] = 100
        }

        var count = 0

        batch(t) {
            on(tv["A", 0]) {
                events {
                    assertEquals(0, oldView.iterator().asSequence().count())
                    assertEquals(1, newView.iterator().asSequence().count())
                    count += count()
                }

                off(this)
            }

            assertEquals(0, count)

            on(tv["A", 0]) {
                skipHistory = true

                events {
                    assertEquals(1, oldView.iterator().asSequence().count())
                    assertEquals(1, newView.iterator().asSequence().count())
                    count += count()
                }
            }

            tv["A", 0][CellWidth] = 200
        }

        assertEquals(1, count)
    }

    @Test
    fun `old and new table is a clone of source table`() {
        val t = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        batch(t) {
            t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH
        }

        var count = 0

        batch(t) {
            on(t["A", 0]) {
                events {
                    oldView["A", 0][CellWidth] = source.derived.cellWidth + 200
                    newView["A", 0][CellWidth] = source.derived.cellWidth + 300

                    assertEquals(source.derived.cellWidth + 200, oldView["A", 0][CellWidth].width)
                    assertEquals(source.derived.cellWidth + 300, newView["A", 0][CellWidth].width)

                    count += count()
                }
            }

            // The second listener is executed after the first listener, and its
            // old/new table should reflect changes introduced by the first listener.
            on(t["A", 0]) {
                skipHistory = true

                events {
                    assertEquals(source.derived.cellWidth + 200, oldView["A", 0][CellWidth].width)
                    assertEquals(source.derived.cellWidth + 300, newView["A", 0][CellWidth].width)

                    count += count()
                }
            }

            assertEquals(DEFAULT_CELL_WIDTH, t["A", 0][CellWidth].width)

            t["A", 0][CellWidth] = DEFAULT_CELL_WIDTH / 2
        }

        assertEquals(DEFAULT_CELL_WIDTH / 2, t["A", 0][CellWidth].width)

        assertEquals(3, count)
    }

    @Test
    fun `event properties`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        batch(tv1) {
            tv1["A", 1][CellHeight] = 100
        }

        batch(tv1) {
            on(tv1["A", 1]) events {
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
    }

    @Test
    fun `event values cellview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ct1: Cell<*>.() -> Unit = {}
        val ct2: Cell<*>.() -> Unit = {}

        batch(tv1) {
            tv1["A", 1][CellHeight] = 25
            tv1["A", 1][CellWidth] = 30
            tv1["A", 1][CellClasses] = "cell-classes-1"
            tv1["A", 1][CellTopics] = "cell-topics-1"
            tv1["A", 1][CellTransformer] = ct1
        }

        var init = true

        batch(tv1) {
            on(tv1["A", 1]) events {
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

            batch(tv1) {
                tv1["A", 1][CellHeight] = 55
                tv1["A", 1][CellWidth] = 60
                tv1["A", 1][CellClasses] = "cell-classes-2"
                tv1["A", 1][CellTopics] = "cell-topics-2"
                tv1["A", 1][CellTransformer] = ct2
            }
        }
    }

    @Test
    fun `event values columnview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val ct1: Column.() -> Unit = {}
        val ct2: Column.() -> Unit = {}

        batch(tv1) {
            //tv1["A"][CellHeight] = 25
            tv1["A"][CellWidth] = 30
            tv1["A"][CellClasses] = "cell-classes-1"
            tv1["A"][CellTopics] = "cell-topics-1"
            tv1["A"][Position] = Position.Left
            tv1["A"][Visibility] = Visibility.Show
            tv1["A"][ColumnTransformer] = ct1
        }

        var init = true
        var count = 0

        batch(tv1) {
            on(tv1["A", 1]) events {
                count += count()

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

                            is Position<*, *> -> {
                                assertTrue(it.oldValue is Position.Horizontal<*>)
                                assertFalse(it.oldValue is Position.Left)
                                assertFalse(it.oldValue is Position.Right)
                                assertEquals(Unit, (it.oldValue as Position<*, *>).position)
                                assertTrue(it.newValue is Position.Left)
                                assertEquals(Position.Value.LEFT, (it.newValue as Position<*, *>).position)
                                assertEquals(oldView["A"], (it.oldValue as Position<*, *>).source)
                                assertEquals(newView["A"], (it.newValue as Position<*, *>).source)
                            }

                            is Visibility<*, *> -> {
                                assertTrue(it.oldValue is Visibility.Undefined<*>)
                                assertFalse(it.oldValue is Visibility.Show<*>)
                                assertFalse(it.oldValue is Visibility.Hide<*>)
                                assertEquals(Unit, (it.oldValue as Visibility<*, *>).visibility)
                                assertTrue(it.newValue is Visibility.Show<*>)
                                assertEquals(Visibility.Value.SHOW, (it.newValue as Visibility<*, *>).visibility)
                                assertEquals(oldView["A"], (it.oldValue as Visibility<*, *>).source)
                                assertEquals(newView["A"], (it.newValue as Visibility<*, *>).source)
                            }

                            is ColumnTransformer<*> -> {
                                assertEquals(Unit, (it.oldValue as ColumnTransformer<*>).function)
                                assertEquals(ct1, (it.newValue as ColumnTransformer<*>).function)
                                assertEquals(oldView["A"], (it.oldValue as ColumnTransformer<*>).source)
                                assertEquals(newView["A"], (it.newValue as ColumnTransformer<*>).source)
                            }
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

                            is Position<*, *> -> {
                                assertTrue(it.oldValue is Position.Left)
                                assertTrue(it.newValue is Position.Right)
                                assertEquals(Position.Value.LEFT, (it.oldValue as Position<*, *>).position)
                                assertEquals(Position.Value.RIGHT, (it.newValue as Position<*, *>).position)
                                assertEquals(oldView["A"], (it.oldValue as Position<*, *>).source)
                                assertEquals(newView["A"], (it.newValue as Position<*, *>).source)
                            }

                            is Visibility<*, *> -> {
                                assertTrue(it.oldValue is Visibility.Show<*>)
                                assertTrue(it.newValue is Visibility.Hide<*>)
                                assertEquals(Visibility.Value.SHOW, (it.oldValue as Visibility<*, *>).visibility)
                                assertEquals(Visibility.Value.HIDE, (it.newValue as Visibility<*, *>).visibility)
                                assertEquals(oldView["A"], (it.oldValue as Visibility<*, *>).source)
                                assertEquals(newView["A"], (it.newValue as Visibility<*, *>).source)
                            }

                            is ColumnTransformer<*> -> {
                                assertEquals(ct1, (it.oldValue as ColumnTransformer<*>).function)
                                assertEquals(ct2, (it.newValue as ColumnTransformer<*>).function)
                                assertEquals(oldView["A"], (it.oldValue as ColumnTransformer<*>).source)
                                assertEquals(newView["A"], (it.newValue as ColumnTransformer<*>).source)
                            }
                            else -> assertTrue(false)
                        }
                    }
                }
            }

            init = false

            batch(tv1) {
                //tv1["A"][CellHeight] = 55
                tv1["A"][CellWidth] = 60
                tv1["A"][CellClasses] = "cell-classes-2"
                tv1["A"][CellTopics] = "cell-topics-2"
                tv1["A"][Position] = Position.Right
                tv1["A"][Visibility] = Visibility.Hide
                tv1["A"][ColumnTransformer] = ct2
            }
        }

        assertEquals(0, count)
    }

    @Test
    fun `event values rowview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val rt1: Row.() -> Unit = {}
        val rt2: Row.() -> Unit = {}

        batch(tv1) {
            tv1[1][CellHeight] = 25
            //tv1[1][CellWidth] = 30
            tv1[1][CellClasses] = "cell-classes-1"
            tv1[1][CellTopics] = "cell-topics-1"
            tv1[1][Position] = Position.Top
            tv1[1][Visibility] = Visibility.Show
            tv1[1][RowTransformer] = rt1
        }

        var init = true
        var count = 0

        batch(tv1) {
            on(tv1["A", 1]) events {
                count += count()

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

                            is Position<*, *> -> {
                                assertTrue(it.oldValue is Position.Vertical<*>)
                                assertFalse(it.oldValue is Position.Top)
                                assertFalse(it.oldValue is Position.Bottom)
                                assertEquals(Unit, (it.oldValue as Position<*, *>).position)
                                assertTrue(it.newValue is Position.Top)
                                assertEquals(Position.Value.TOP, (it.newValue as Position<*, *>).position)
                                assertEquals(oldView[1], (it.oldValue as Position<*, *>).source)
                                assertEquals(newView[1], (it.newValue as Position<*, *>).source)
                            }

                            is Visibility<*, *> -> {
                                assertTrue(it.oldValue is Visibility.Undefined<*>)
                                assertFalse(it.oldValue is Visibility.Show<*>)
                                assertFalse(it.oldValue is Visibility.Hide<*>)
                                assertEquals(Unit, (it.oldValue as Visibility<*, *>).visibility)
                                assertTrue(it.newValue is Visibility.Show<*>)
                                assertEquals(Visibility.Value.SHOW, (it.newValue as Visibility<*, *>).visibility)
                                assertEquals(oldView[1], (it.oldValue as Visibility<*, *>).source)
                                assertEquals(newView[1], (it.newValue as Visibility<*, *>).source)
                            }

                            is RowTransformer<*> -> {
                                assertEquals(Unit, (it.oldValue as RowTransformer<*>).function)
                                assertEquals(rt1, (it.newValue as RowTransformer<*>).function)
                                assertEquals(oldView[1], (it.oldValue as RowTransformer<*>).source)
                                assertEquals(newView[1], (it.newValue as RowTransformer<*>).source)
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

                            is Position<*, *> -> {
                                assertTrue(it.oldValue is Position.Top)
                                assertTrue(it.newValue is Position.Bottom)
                                assertEquals(Position.Value.TOP, (it.oldValue as Position<*, *>).position)
                                assertEquals(Position.Value.BOTTOM, (it.newValue as Position<*, *>).position)
                                assertEquals(oldView[1], (it.oldValue as Position<*, *>).source)
                                assertEquals(newView[1], (it.newValue as Position<*, *>).source)
                            }

                            is Visibility<*, *> -> {
                                assertTrue(it.oldValue is Visibility.Show<*>)
                                assertTrue(it.newValue is Visibility.Hide<*>)
                                assertEquals(Visibility.Value.SHOW, (it.oldValue as Visibility<*, *>).visibility)
                                assertEquals(Visibility.Value.HIDE, (it.newValue as Visibility<*, *>).visibility)
                                assertEquals(oldView[1], (it.oldValue as Visibility<*, *>).source)
                                assertEquals(newView[1], (it.newValue as Visibility<*, *>).source)
                            }

                            is RowTransformer<*> -> {
                                assertEquals(rt1, (it.oldValue as RowTransformer<*>).function)
                                assertEquals(rt2, (it.newValue as RowTransformer<*>).function)
                                assertEquals(oldView[1], (it.oldValue as RowTransformer<*>).source)
                                assertEquals(newView[1], (it.newValue as RowTransformer<*>).source)
                            }
                            else -> assertTrue(false)
                        }
                    }
                }
            }

            init = false

            batch(tv1) {
                tv1[1][CellHeight] = 55
                //tv1[1][CellWidth] = 60
                tv1[1][CellClasses] = "cell-classes-2"
                tv1[1][CellTopics] = "cell-topics-2"
                tv1[1][Position] = Position.Bottom
                tv1[1][Visibility] = Visibility.Hide
                tv1[1][RowTransformer] = rt2
            }
        }

        assertEquals(0, count)
    }

    @Test
    fun `event values tableview`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        val t1 = Table[null]
        val t2 = Table[null]

        val tt1: Table.() -> Unit = {}
        val tt2: Table.() -> Unit = {}

        val r1: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> = "a" to {}
        val r2: Pair<String, suspend PipelineContext<*, ApplicationCall>.() -> Unit> = "a" to {}

        batch(tv1) {
            tv1[CellHeight] = 25
            tv1[CellWidth] = 30
            tv1[CellClasses] = "cell-classes-1"
            tv1[CellTopics] = "cell-topics-1"
            tv1[TableTransformer] = tt1
            tv1[Resource[r1.first]] = r1.second
            tv1[Table] = t1
        }

        var init = true
        var count = 0

        batch(tv1) {
            on(tv1["A", 1]) events {
                count += count()

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
                            is TableTransformer<*> -> {
                                assertEquals(Unit, (it.oldValue as TableTransformer<*>).function)
                                assertEquals(tt1, (it.newValue as TableTransformer<*>).function)
                                assertEquals(oldView, (it.oldValue as TableTransformer<*>).source)
                                assertEquals(newView, (it.newValue as TableTransformer<*>).source)
                            }
                            is Resource<*, *> -> {
                                assertTrue(it.oldValue is UnitResource<*>)
                                assertTrue(it.newValue is HandlerResource<*>)
                                assertEquals(oldView, (it.oldValue as Resource<*, *>).source)
                                assertEquals(newView, (it.newValue as Resource<*, *>).source)
                                assertEquals("a", (it.oldValue as Resource<*, *>).path)
                                assertEquals(r1.first, (it.newValue as Resource<*, *>).path)
                                assertEquals(Unit, (it.oldValue as Resource<*, *>).handler)
                                assertEquals(r1.second, (it.newValue as Resource<*, *>).handler)
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
                            is TableTransformer<*> -> {
                                assertEquals(tt1, (it.oldValue as TableTransformer<*>).function)
                                assertEquals(tt2, (it.newValue as TableTransformer<*>).function)
                                assertEquals(oldView, (it.oldValue as TableTransformer<*>).source)
                                assertEquals(newView, (it.newValue as TableTransformer<*>).source)
                            }
                            is Resource<*, *> -> {
                                assertTrue(it.oldValue is HandlerResource<*>)
                                assertTrue(it.newValue is HandlerResource<*>)
                                assertEquals(oldView, (it.oldValue as Resource<*, *>).source)
                                assertEquals(newView, (it.newValue as Resource<*, *>).source)
                                assertEquals(r1.first, (it.oldValue as Resource<*, *>).path)
                                assertEquals(r2.first, (it.newValue as Resource<*, *>).path)
                                assertEquals(r1.second, (it.oldValue as Resource<*, *>).handler)
                                assertEquals(r2.second, (it.newValue as Resource<*, *>).handler)
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

            batch(tv1) {
                tv1[CellHeight] = 55
                tv1[CellWidth] = 60
                tv1[CellClasses] = "cell-classes-2"
                tv1[CellTopics] = "cell-topics-2"
                tv1[TableTransformer] = tt2
                tv1[Resource[r2.first]] = r2.second
                tv1[Table] = t2
            }
        }

        assertEquals(0, count)
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

        batch(tv1) {
            tv1["A", 0][CellHeight] = 25
            tv1["A", 0][CellWidth] = 30
            tv1["A", 0][CellClasses] = "cell-classes-1"
            tv1["A", 0][CellTopics] = "cell-topics-1"
            tv1["A", 0][CellTransformer] = ct1
            tv1[Resource[r1.first]] = r1.second
            tv1[Table] = t1
        }

        var eventCount1 = 0
        var eventCount2 = 0
        var eventCount3 = 0
        var eventCount4 = 0
        var eventCount5 = 0
        var eventCount6 = 0
        var eventCount7 = 0
        var eventCount8 = 0
        var eventCount9 = 0

        on<CellHeight<*, *>>(tv1["A", 0]) events {
            eventCount1 += count()
        }

        on<CellWidth<*, *>>(tv1["A", 0]) events {
            eventCount2 += count()
        }

        on<CellClasses<*>>(tv1["A", 0]) events {
            eventCount3 += count()
        }

        on<CellTopics<*>>(tv1["A", 0]) events {
            eventCount4 += count()
        }

        on<CellTransformer<*>>(tv1["A", 0]) events {
            eventCount5 += count()
        }

        on<Resource<*, *>>(tv1["A", 0]) events {
            eventCount6 += count()
        }

        on<SourceTable>(tv1["A", 0]) events {
            eventCount7 += count()
        }

        on<Position<*, *>>(tv1["A", 0]) events {
            eventCount8 += count()
        }

        on<Visibility<*, *>>(tv1["A", 0]) events {
            eventCount9 += count()
        }

        assertEquals(1, eventCount1)
        assertEquals(1, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(0, eventCount6)
        assertEquals(0, eventCount7)
        assertEquals(0, eventCount8)
        assertEquals(0, eventCount9)

        batch(tv1) {
            tv1["A", 0][CellHeight] = 45
        }

        assertEquals(2, eventCount1)
        assertEquals(1, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(0, eventCount6)
        assertEquals(0, eventCount7)
        assertEquals(0, eventCount8)
        assertEquals(0, eventCount9)

        batch(tv1) {
            tv1["A", 0][CellWidth] = 60
        }

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(1, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(0, eventCount6)
        assertEquals(0, eventCount7)
        assertEquals(0, eventCount8)
        assertEquals(0, eventCount9)

        batch(tv1) {
            tv1["A", 0][CellClasses] = "cell-classes-2"
        }

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(1, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(0, eventCount6)
        assertEquals(0, eventCount7)
        assertEquals(0, eventCount8)
        assertEquals(0, eventCount9)

        batch(tv1) {
            tv1["A", 0][CellTopics] = "cell-topics-2"
        }

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(2, eventCount4)
        assertEquals(1, eventCount5)
        assertEquals(0, eventCount6)
        assertEquals(0, eventCount7)
        assertEquals(0, eventCount8)
        assertEquals(0, eventCount9)

        batch(tv1) {
            tv1["A", 0][CellTransformer] = ct2
        }

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(2, eventCount4)
        assertEquals(2, eventCount5)
        assertEquals(0, eventCount6)
        assertEquals(0, eventCount7)
        assertEquals(0, eventCount8)
        assertEquals(0, eventCount9)

        batch(tv1) {
            tv1[Resource[r2.first]] = r2.second
        }

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(2, eventCount4)
        assertEquals(2, eventCount5)
        assertEquals(0, eventCount6)
        assertEquals(0, eventCount7)
        assertEquals(0, eventCount8)
        assertEquals(0, eventCount9)

        batch(tv1) {
            tv1[Table] = t2
        }

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
        assertEquals(2, eventCount3)
        assertEquals(2, eventCount4)
        assertEquals(2, eventCount5)
        assertEquals(0, eventCount6)
        assertEquals(0, eventCount7)
        assertEquals(0, eventCount8)
        assertEquals(0, eventCount9)
    }

    @Test
    fun `type filtering for chained subscriptions`() {
        val tv1 = TableView["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        var eventCount1 = 0
        var eventCount2 = 0

        on<CellWidth<*,*>>(tv1["A", 1], name = "Listener 1") events {
            eventCount1 += count()
            forEach {
                oldView[it.columnView!!, it.index!!][CellWidth](100)
                newView[it.columnView!!, it.index!!][CellWidth] = 200
            }
        }

        on<CellWidth<*,*>>(tv1["A", 1], name = "Listener 2") events {
            eventCount2 += count()
            forEach {
                assertEquals(PixelCellWidth::class, it.oldValue::class)
                assertEquals(PixelCellWidth::class, it.newValue::class)
            }
        }

        batch(tv1) {
            tv1["A", 1][CellWidth] = 50
        }

        assertEquals(1, eventCount1)
        assertEquals(1, eventCount2)

        batch(tv1) {
            tv1["A", 1][CellWidth] = Unit
        }

        assertEquals(2, eventCount1)
        assertEquals(2, eventCount2)
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
