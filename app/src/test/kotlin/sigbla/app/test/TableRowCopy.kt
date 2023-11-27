/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import sigbla.app.*

class TableRowCopy {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `overwrite rows within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 2] = "A2"
        t["A", 3] = "A3"

        t["B", 0] = "B0"
        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        var events = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t) {
            skipHistory = true

            events {
                events = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A2", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())

        // No-op
        copy(t[0] to t[0])

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().newValue.table).toList())

        assertEquals("A0", valueOf<Any>(events.first().oldValue))
        assertEquals("A0", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals("B0", valueOf<Any>(events.last().oldValue))
        assertEquals("B0", valueOf<Any>(events.last().newValue))
        assertEquals(listOf("B"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events.last().newValue).labels)
        assertEquals(0, indexOf(events.last().oldValue))
        assertEquals(0, indexOf(events.last().newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A2", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())

        // Copy to in between
        copy(t[0] to t[2])

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().newValue.table).toList())

        assertEquals("A2", valueOf<Any>(events.first().oldValue))
        assertEquals("A0", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(2, indexOf(events.first().oldValue))
        assertEquals(2, indexOf(events.first().newValue))

        assertEquals("B2", valueOf<Any>(events.last().oldValue))
        assertEquals("B0", valueOf<Any>(events.last().newValue))
        assertEquals(listOf("B"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events.last().newValue).labels)
        assertEquals(2, indexOf(events.last().oldValue))
        assertEquals(2, indexOf(events.last().newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A0", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B1", "B0", "B3"), valuesOf<Any>(t["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t[1] to t[4])

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("A1", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(4, indexOf(events.first().oldValue))
        assertEquals(4, indexOf(events.first().newValue))

        assertEquals(Unit, valueOf<Any>(events.last().oldValue))
        assertEquals("B1", valueOf<Any>(events.last().newValue))
        assertEquals(listOf("B"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events.last().newValue).labels)
        assertEquals(4, indexOf(events.last().oldValue))
        assertEquals(4, indexOf(events.last().newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B1", "B0", "B3", "B1"), valuesOf<Any>(t["B"]).toList())

        // Copy to first
        copy(t[3] to t[-1])

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events.first().oldValue.table).toList())
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(events.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events.last().oldValue.table).toList())
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(events.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("A3", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(-1, indexOf(events.first().oldValue))
        assertEquals(-1, indexOf(events.first().newValue))

        assertEquals(Unit, valueOf<Any>(events.last().oldValue))
        assertEquals("B3", valueOf<Any>(events.last().newValue))
        assertEquals(listOf("B"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events.last().newValue).labels)
        assertEquals(-1, indexOf(events.last().oldValue))
        assertEquals(-1, indexOf(events.last().newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A3", "A0", "A1", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B3", "B0", "B1", "B0", "B3", "B1"), valuesOf<Any>(t["B"]).toList())
    }

    @Test
    fun `overwrite sparse rows within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 3] = "A3"

        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        var events = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t) {
            skipHistory = true

            events {
                events = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())

        // No-op
        copy(t[0] to t[0])

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().newValue.table).toList())

        assertEquals("A0", valueOf<Any>(events.first().oldValue))
        assertEquals("A0", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(Unit, valueOf<Any>(events.last().oldValue))
        assertEquals(Unit, valueOf<Any>(events.last().newValue))
        assertEquals(listOf("B"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events.last().newValue).labels)
        assertEquals(0, indexOf(events.last().oldValue))
        assertEquals(0, indexOf(events.last().newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())

        // Copy to in between
        copy(t[0] to t[2])

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("A0", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(2, indexOf(events.first().oldValue))
        assertEquals(2, indexOf(events.first().newValue))

        assertEquals("B2", valueOf<Any>(events.last().oldValue))
        assertEquals(Unit, valueOf<Any>(events.last().newValue))
        assertEquals(listOf("B"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events.last().newValue).labels)
        assertEquals(2, indexOf(events.last().oldValue))
        assertEquals(2, indexOf(events.last().newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A0", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B1", "B3"), valuesOf<Any>(t["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t[1] to t[4])

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("A1", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(4, indexOf(events.first().oldValue))
        assertEquals(4, indexOf(events.first().newValue))

        assertEquals(Unit, valueOf<Any>(events.last().oldValue))
        assertEquals("B1", valueOf<Any>(events.last().newValue))
        assertEquals(listOf("B"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events.last().newValue).labels)
        assertEquals(4, indexOf(events.last().oldValue))
        assertEquals(4, indexOf(events.last().newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B1", "B3", "B1"), valuesOf<Any>(t["B"]).toList())

        // Copy to first
        copy(t[3] to t[-1])

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events.first().oldValue.table).toList())
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(events.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events.last().oldValue.table).toList())
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(events.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("A3", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(-1, indexOf(events.first().oldValue))
        assertEquals(-1, indexOf(events.first().newValue))

        assertEquals(Unit, valueOf<Any>(events.last().oldValue))
        assertEquals("B3", valueOf<Any>(events.last().newValue))
        assertEquals(listOf("B"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events.last().newValue).labels)
        assertEquals(-1, indexOf(events.last().oldValue))
        assertEquals(-1, indexOf(events.last().newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A3", "A0", "A1", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B3", "B1", "B3", "B1"), valuesOf<Any>(t["B"]).toList())
    }

    @Test
    fun `overwrite rows between tables with rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        t2["A", 0] = "A0_1"
        t2["A", 1] = "A1_1"
        t2["A", 2] = "A2_1"
        t2["A", 3] = "A3_1"

        t2["B", 0] = "B0_1"
        t2["B", 1] = "B1_1"
        t2["B", 2] = "B2_1"
        t2["B", 3] = "B3_1"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()
        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_1", "A1_1", "A2_1", "A3_1"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B2_1", "B3_1"), valuesOf<Any>(t2["B"]).toList())

        // Copy to in between
        copy(t1[0] to t2[2])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.last().newValue.table).toList())

        assertEquals("A2_1", valueOf<Any>(events2.first().oldValue))
        assertEquals("A0_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(2, indexOf(events2.first().oldValue))
        assertEquals(2, indexOf(events2.first().newValue))

        assertEquals("B2_1", valueOf<Any>(events2.last().oldValue))
        assertEquals("B0_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(2, indexOf(events2.last().oldValue))
        assertEquals(2, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_1", "A1_1", "A0_0", "A3_1"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B0_0", "B3_1"), valuesOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t1[1] to t2[4])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A1_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(4, indexOf(events2.first().oldValue))
        assertEquals(4, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B1_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(4, indexOf(events2.last().oldValue))
        assertEquals(4, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_1", "A1_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B0_0", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())

        // Copy to first
        copy(t1[3] to t2[-1])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A3_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(-1, indexOf(events2.first().oldValue))
        assertEquals(-1, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B3_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(-1, indexOf(events2.last().oldValue))
        assertEquals(-1, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A0_1", "A1_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B0_1", "B1_1", "B0_0", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `overwrite sparse rows between tables with rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 3] = "A3_0"

        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        t2["A", 1] = "A1_1"
        t2["A", 2] = "A2_1"
        t2["A", 3] = "A3_1"

        t2["B", 0] = "B0_1"
        t2["B", 1] = "B1_1"
        t2["B", 3] = "B3_1"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()
        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A1_1", "A2_1", "A3_1"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B3_1"), valuesOf<Any>(t2["B"]).toList())

        // Copy to in between
        copy(t1[0] to t2[2])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.last().newValue.table).toList())

        assertEquals("A2_1", valueOf<Any>(events2.first().oldValue))
        assertEquals("A0_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(2, indexOf(events2.first().oldValue))
        assertEquals(2, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals(Unit, valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(2, indexOf(events2.last().oldValue))
        assertEquals(2, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A1_1", "A0_0", "A3_1"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B3_1"), valuesOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t1[1] to t2[4])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A1_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(4, indexOf(events2.first().oldValue))
        assertEquals(4, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B1_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(4, indexOf(events2.last().oldValue))
        assertEquals(4, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A1_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())

        // Copy to first
        copy(t1[3] to t2[-1])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A3_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(-1, indexOf(events2.first().oldValue))
        assertEquals(-1, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B3_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(-1, indexOf(events2.last().oldValue))
        assertEquals(-1, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A1_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B0_1", "B1_1", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `overwrite rows between tables without rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()
        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        // Copy to in between
        copy(t1[0] to t2[2])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(emptyList<List<Any>>(), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(emptyList<List<Any>>(), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(emptyList<Long>(), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(2L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(emptyList<Long>(), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(2L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A0_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(2, indexOf(events2.first().oldValue))
        assertEquals(2, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B0_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(2, indexOf(events2.last().oldValue))
        assertEquals(2, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_0"), valuesOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t1[1] to t2[4])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(2L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(2L, 4L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(2L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(2L, 4L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A1_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(4, indexOf(events2.first().oldValue))
        assertEquals(4, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B1_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(4, indexOf(events2.last().oldValue))
        assertEquals(4, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0"), valuesOf<Any>(t2["B"]).toList())

        // Copy to first
        copy(t1[3] to t2[0])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(2L, 4L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(0L, 2L, 4L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(2L, 4L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(0L, 2L, 4L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A3_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B3_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(0, indexOf(events2.last().oldValue))
        assertEquals(0, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B0_0", "B1_0"), valuesOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `overwrite sparse rows between tables without rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 3] = "A3_0"

        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()
        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        // Copy to in between
        copy(t1[0] to t2[2])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(emptyList<List<Any>>(), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(emptyList<List<Any>>(), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(emptyList<Long>(), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(2L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(emptyList<Long>(), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(2L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A0_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(2, indexOf(events2.first().oldValue))
        assertEquals(2, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals(Unit, valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(2, indexOf(events2.last().oldValue))
        assertEquals(2, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_0"), valuesOf<Any>(t2["A"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t1[1] to t2[4])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(2L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(2L, 4L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(2L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(2L, 4L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A1_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(4, indexOf(events2.first().oldValue))
        assertEquals(4, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B1_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(4, indexOf(events2.last().oldValue))
        assertEquals(4, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B1_0"), valuesOf<Any>(t2["B"]).toList())

        // Copy to first
        copy(t1[3] to t2[0])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(2L, 4L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(0L, 2L, 4L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(2L, 4L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(0L, 2L, 4L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A3_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B3_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(0, indexOf(events2.last().oldValue))
        assertEquals(0, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B1_0"), valuesOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `copy rows within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 2] = "A2"
        t["A", 3] = "A3"

        t["B", 0] = "B0"
        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        var events = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t) {
            skipHistory = true

            events {
                events = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A2", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())

        // TODO Test copy/move after/before self: copy/move(t[0] after/before t[0])

        // Copy to after
        copy(t[0] after t[2])

        assertEquals(4, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.newValue.table).toList())
        }

        assertEquals("A3", valueOf<Any>(events[0].oldValue))
        assertEquals("A0", valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(3, indexOf(events[0].oldValue))
        assertEquals(3, indexOf(events[0].newValue))

        assertEquals(Unit, valueOf<Any>(events[1].oldValue))
        assertEquals("A3", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("A"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[1].newValue).labels)
        assertEquals(4, indexOf(events[1].oldValue))
        assertEquals(4, indexOf(events[1].newValue))

        assertEquals("B3", valueOf<Any>(events[2].oldValue))
        assertEquals("B0", valueOf<Any>(events[2].newValue))
        assertEquals(listOf("B"), headerOf(events[2].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[2].newValue).labels)
        assertEquals(3, indexOf(events[2].oldValue))
        assertEquals(3, indexOf(events[2].newValue))

        assertEquals(Unit, valueOf<Any>(events[3].oldValue))
        assertEquals("B3", valueOf<Any>(events[3].newValue))
        assertEquals(listOf("B"), headerOf(events[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[3].newValue).labels)
        assertEquals(4, indexOf(events[3].oldValue))
        assertEquals(4, indexOf(events[3].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A2", "A0", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B1", "B2", "B0", "B3"), valuesOf<Any>(t["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t[1] after t[4])

        assertEquals(2, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events[0].oldValue))
        assertEquals("A1", valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(5, indexOf(events[0].oldValue))
        assertEquals(5, indexOf(events[0].newValue))

        assertEquals(Unit, valueOf<Any>(events[1].oldValue))
        assertEquals("B1", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("B"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[1].newValue).labels)
        assertEquals(5, indexOf(events[1].oldValue))
        assertEquals(5, indexOf(events[1].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A2", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B1", "B2", "B0", "B3", "B1"), valuesOf<Any>(t["B"]).toList())

        // Copy to first
        copy(t[2] before t[0])

        assertEquals(2, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events[0].oldValue))
        assertEquals("A2", valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(-1, indexOf(events[0].oldValue))
        assertEquals(-1, indexOf(events[0].newValue))

        assertEquals(Unit, valueOf<Any>(events[1].oldValue))
        assertEquals("B2", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("B"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[1].newValue).labels)
        assertEquals(-1, indexOf(events[1].oldValue))
        assertEquals(-1, indexOf(events[1].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A2", "A0", "A1", "A2", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B2", "B0", "B1", "B2", "B0", "B3", "B1"), valuesOf<Any>(t["B"]).toList())

        // Copy to before
        copy(t[4] before t[1])

        assertEquals(6, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-2L, -1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events[0].oldValue))
        assertEquals("A2", valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(-2, indexOf(events[0].oldValue))
        assertEquals(-2, indexOf(events[0].newValue))

        assertEquals("A2", valueOf<Any>(events[1].oldValue))
        assertEquals("A0", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("A"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[1].newValue).labels)
        assertEquals(-1, indexOf(events[1].oldValue))
        assertEquals(-1, indexOf(events[1].newValue))

        assertEquals("A0", valueOf<Any>(events[2].oldValue))
        assertEquals("A3", valueOf<Any>(events[2].newValue))
        assertEquals(listOf("A"), headerOf(events[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[2].newValue).labels)
        assertEquals(0, indexOf(events[2].oldValue))
        assertEquals(0, indexOf(events[2].newValue))

        assertEquals(Unit, valueOf<Any>(events[3].oldValue))
        assertEquals("B2", valueOf<Any>(events[3].newValue))
        assertEquals(listOf("B"), headerOf(events[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[3].newValue).labels)
        assertEquals(-2, indexOf(events[3].oldValue))
        assertEquals(-2, indexOf(events[3].newValue))

        assertEquals("B2", valueOf<Any>(events[4].oldValue))
        assertEquals("B0", valueOf<Any>(events[4].newValue))
        assertEquals(listOf("B"), headerOf(events[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[4].newValue).labels)
        assertEquals(-1, indexOf(events[4].oldValue))
        assertEquals(-1, indexOf(events[4].newValue))

        assertEquals("B0", valueOf<Any>(events[5].oldValue))
        assertEquals("B3", valueOf<Any>(events[5].newValue))
        assertEquals(listOf("B"), headerOf(events[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[5].newValue).labels)
        assertEquals(0, indexOf(events[5].oldValue))
        assertEquals(0, indexOf(events[5].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A2", "A0", "A3", "A1", "A2", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B2", "B0", "B3", "B1", "B2", "B0", "B3", "B1"), valuesOf<Any>(t["B"]).toList())
    }

    @Test
    fun `copy sparse rows within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 3] = "A3"

        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        var events = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t) {
            skipHistory = true

            events {
                events = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())

        // TODO Test copy/move after/before self: copy/move(t[0] after/before t[0])

        // Copy to after
        copy(t[0] after t[2])

        assertEquals(4, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.newValue.table).toList())
        }

        assertEquals("A3", valueOf<Any>(events[0].oldValue))
        assertEquals("A0", valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(3, indexOf(events[0].oldValue))
        assertEquals(3, indexOf(events[0].newValue))

        assertEquals(Unit, valueOf<Any>(events[1].oldValue))
        assertEquals("A3", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("A"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[1].newValue).labels)
        assertEquals(4, indexOf(events[1].oldValue))
        assertEquals(4, indexOf(events[1].newValue))

        assertEquals("B3", valueOf<Any>(events[2].oldValue))
        assertEquals(Unit, valueOf<Any>(events[2].newValue))
        assertEquals(listOf("B"), headerOf(events[2].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[2].newValue).labels)
        assertEquals(3, indexOf(events[2].oldValue))
        assertEquals(3, indexOf(events[2].newValue))

        assertEquals(Unit, valueOf<Any>(events[3].oldValue))
        assertEquals("B3", valueOf<Any>(events[3].newValue))
        assertEquals(listOf("B"), headerOf(events[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[3].newValue).labels)
        assertEquals(4, indexOf(events[3].oldValue))
        assertEquals(4, indexOf(events[3].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A0", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t[1] after t[4])

        assertEquals(2, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events[0].oldValue))
        assertEquals("A1", valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(5, indexOf(events[0].oldValue))
        assertEquals(5, indexOf(events[0].newValue))

        assertEquals(Unit, valueOf<Any>(events[1].oldValue))
        assertEquals("B1", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("B"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[1].newValue).labels)
        assertEquals(5, indexOf(events[1].oldValue))
        assertEquals(5, indexOf(events[1].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B1", "B2", "B3", "B1"), valuesOf<Any>(t["B"]).toList())

        // Copy to first
        copy(t[2] before t[0])

        assertEquals(2, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events[0].oldValue))
        assertEquals(Unit, valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(-1, indexOf(events[0].oldValue))
        assertEquals(-1, indexOf(events[0].newValue))

        assertEquals(Unit, valueOf<Any>(events[1].oldValue))
        assertEquals("B2", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("B"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[1].newValue).labels)
        assertEquals(-1, indexOf(events[1].oldValue))
        assertEquals(-1, indexOf(events[1].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B2", "B1", "B2", "B3", "B1"), valuesOf<Any>(t["B"]).toList())

        // Copy to before
        copy(t[4] before t[1])

        assertEquals(6, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-2L, -1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events[0].oldValue))
        assertEquals(Unit, valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(-2, indexOf(events[0].oldValue))
        assertEquals(-2, indexOf(events[0].newValue))

        assertEquals(Unit, valueOf<Any>(events[1].oldValue))
        assertEquals("A0", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("A"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[1].newValue).labels)
        assertEquals(-1, indexOf(events[1].oldValue))
        assertEquals(-1, indexOf(events[1].newValue))

        assertEquals("A0", valueOf<Any>(events[2].oldValue))
        assertEquals("A3", valueOf<Any>(events[2].newValue))
        assertEquals(listOf("A"), headerOf(events[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[2].newValue).labels)
        assertEquals(0, indexOf(events[2].oldValue))
        assertEquals(0, indexOf(events[2].newValue))

        assertEquals(Unit, valueOf<Any>(events[3].oldValue))
        assertEquals("B2", valueOf<Any>(events[3].newValue))
        assertEquals(listOf("B"), headerOf(events[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[3].newValue).labels)
        assertEquals(-2, indexOf(events[3].oldValue))
        assertEquals(-2, indexOf(events[3].newValue))

        assertEquals("B2", valueOf<Any>(events[4].oldValue))
        assertEquals(Unit, valueOf<Any>(events[4].newValue))
        assertEquals(listOf("B"), headerOf(events[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[4].newValue).labels)
        assertEquals(-1, indexOf(events[4].oldValue))
        assertEquals(-1, indexOf(events[4].newValue))

        assertEquals(Unit, valueOf<Any>(events[5].oldValue))
        assertEquals("B3", valueOf<Any>(events[5].newValue))
        assertEquals(listOf("B"), headerOf(events[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[5].newValue).labels)
        assertEquals(0, indexOf(events[5].oldValue))
        assertEquals(0, indexOf(events[5].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A3", "A1", "A0", "A3", "A1"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B2", "B3", "B1", "B2", "B3", "B1"), valuesOf<Any>(t["B"]).toList())
    }

    @Test
    fun `copy rows within same table with overlap`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 2] = "A2"
        t["A", 3] = "A3"

        t["B", 0] = "B0"
        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        var events = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t) {
            skipHistory = true

            events {
                events = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A2", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())

        // Check copy|move(t[X] after t[Y]) with X > Y, and copy|move(t[X] before t[Y]) with X < Y

        // Copy to after
        copy(t[2] after t[0])

        assertEquals(8, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.newValue.table).toList())
        }

        assertEquals("A1", valueOf<Any>(events[0].oldValue))
        assertEquals("A2", valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(1, indexOf(events[0].oldValue))
        assertEquals(1, indexOf(events[0].newValue))

        assertEquals("A2", valueOf<Any>(events[1].oldValue))
        assertEquals("A1", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("A"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[1].newValue).labels)
        assertEquals(2, indexOf(events[1].oldValue))
        assertEquals(2, indexOf(events[1].newValue))

        assertEquals("A3", valueOf<Any>(events[2].oldValue))
        assertEquals("A2", valueOf<Any>(events[2].newValue))
        assertEquals(listOf("A"), headerOf(events[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[2].newValue).labels)
        assertEquals(3, indexOf(events[2].oldValue))
        assertEquals(3, indexOf(events[2].newValue))

        assertEquals(Unit, valueOf<Any>(events[3].oldValue))
        assertEquals("A3", valueOf<Any>(events[3].newValue))
        assertEquals(listOf("A"), headerOf(events[3].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[3].newValue).labels)
        assertEquals(4, indexOf(events[3].oldValue))
        assertEquals(4, indexOf(events[3].newValue))

        assertEquals("B1", valueOf<Any>(events[4].oldValue))
        assertEquals("B2", valueOf<Any>(events[4].newValue))
        assertEquals(listOf("B"), headerOf(events[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[4].newValue).labels)
        assertEquals(1, indexOf(events[4].oldValue))
        assertEquals(1, indexOf(events[4].newValue))

        assertEquals("B2", valueOf<Any>(events[5].oldValue))
        assertEquals("B1", valueOf<Any>(events[5].newValue))
        assertEquals(listOf("B"), headerOf(events[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[5].newValue).labels)
        assertEquals(2, indexOf(events[5].oldValue))
        assertEquals(2, indexOf(events[5].newValue))

        assertEquals("B3", valueOf<Any>(events[6].oldValue))
        assertEquals("B2", valueOf<Any>(events[6].newValue))
        assertEquals(listOf("B"), headerOf(events[6].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[6].newValue).labels)
        assertEquals(3, indexOf(events[6].oldValue))
        assertEquals(3, indexOf(events[6].newValue))

        assertEquals(Unit, valueOf<Any>(events[7].oldValue))
        assertEquals("B3", valueOf<Any>(events[7].newValue))
        assertEquals(listOf("B"), headerOf(events[7].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[7].newValue).labels)
        assertEquals(4, indexOf(events[7].oldValue))
        assertEquals(4, indexOf(events[7].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A2", "A1", "A2", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B2", "B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())
        // TODO indexOf test

        // Copy to before
        copy(t[1] before t[3])

        assertEquals(8, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events[0].oldValue))
        assertEquals("A0", valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(-1, indexOf(events[0].oldValue))
        assertEquals(-1, indexOf(events[0].newValue))

        assertEquals("A0", valueOf<Any>(events[1].oldValue))
        assertEquals("A2", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("A"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[1].newValue).labels)
        assertEquals(0, indexOf(events[1].oldValue))
        assertEquals(0, indexOf(events[1].newValue))

        assertEquals("A2", valueOf<Any>(events[2].oldValue))
        assertEquals("A1", valueOf<Any>(events[2].newValue))
        assertEquals(listOf("A"), headerOf(events[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[2].newValue).labels)
        assertEquals(1, indexOf(events[2].oldValue))
        assertEquals(1, indexOf(events[2].newValue))

        assertEquals("A1", valueOf<Any>(events[3].oldValue))
        assertEquals("A2", valueOf<Any>(events[3].newValue))
        assertEquals(listOf("A"), headerOf(events[3].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[3].newValue).labels)
        assertEquals(2, indexOf(events[3].oldValue))
        assertEquals(2, indexOf(events[3].newValue))

        assertEquals(Unit, valueOf<Any>(events[4].oldValue))
        assertEquals("B0", valueOf<Any>(events[4].newValue))
        assertEquals(listOf("B"), headerOf(events[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[4].newValue).labels)
        assertEquals(-1, indexOf(events[4].oldValue))
        assertEquals(-1, indexOf(events[4].newValue))

        assertEquals("B0", valueOf<Any>(events[5].oldValue))
        assertEquals("B2", valueOf<Any>(events[5].newValue))
        assertEquals(listOf("B"), headerOf(events[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[5].newValue).labels)
        assertEquals(0, indexOf(events[5].oldValue))
        assertEquals(0, indexOf(events[5].newValue))

        assertEquals("B2", valueOf<Any>(events[6].oldValue))
        assertEquals("B1", valueOf<Any>(events[6].newValue))
        assertEquals(listOf("B"), headerOf(events[6].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[6].newValue).labels)
        assertEquals(1, indexOf(events[6].oldValue))
        assertEquals(1, indexOf(events[6].newValue))

        assertEquals("B1", valueOf<Any>(events[7].oldValue))
        assertEquals("B2", valueOf<Any>(events[7].newValue))
        assertEquals(listOf("B"), headerOf(events[7].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[7].newValue).labels)
        assertEquals(2, indexOf(events[7].oldValue))
        assertEquals(2, indexOf(events[7].newValue))

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A2", "A1", "A2", "A2", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B0", "B2", "B1", "B2", "B2", "B3"), valuesOf<Any>(t["B"]).toList())
    }

    @Test
    fun `copy sparse rows within same table with overlap`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "A0"
        t["A", 1] = "A1"
        t["A", 3] = "A3"

        t["B", 1] = "B1"
        t["B", 2] = "B2"
        t["B", 3] = "B3"

        var events = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t) {
            skipHistory = true

            events {
                events = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())

        // Check copy|move(t[X] after t[Y]) with X > Y, and copy|move(t[X] before t[Y]) with X < Y

        // Copy to after
        copy(t[2] after t[0])

        assertEquals(8, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.newValue.table).toList())
        }

        assertEquals("A1", valueOf<Any>(events[0].oldValue))
        assertEquals(Unit, valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(1, indexOf(events[0].oldValue))
        assertEquals(1, indexOf(events[0].newValue))

        assertEquals(Unit, valueOf<Any>(events[1].oldValue))
        assertEquals("A1", valueOf<Any>(events[1].newValue))
        assertEquals(listOf("A"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[1].newValue).labels)
        assertEquals(2, indexOf(events[1].oldValue))
        assertEquals(2, indexOf(events[1].newValue))

        assertEquals("A3", valueOf<Any>(events[2].oldValue))
        assertEquals(Unit, valueOf<Any>(events[2].newValue))
        assertEquals(listOf("A"), headerOf(events[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[2].newValue).labels)
        assertEquals(3, indexOf(events[2].oldValue))
        assertEquals(3, indexOf(events[2].newValue))

        assertEquals(Unit, valueOf<Any>(events[3].oldValue))
        assertEquals("A3", valueOf<Any>(events[3].newValue))
        assertEquals(listOf("A"), headerOf(events[3].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[3].newValue).labels)
        assertEquals(4, indexOf(events[3].oldValue))
        assertEquals(4, indexOf(events[3].newValue))

        assertEquals("B1", valueOf<Any>(events[4].oldValue))
        assertEquals("B2", valueOf<Any>(events[4].newValue))
        assertEquals(listOf("B"), headerOf(events[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[4].newValue).labels)
        assertEquals(1, indexOf(events[4].oldValue))
        assertEquals(1, indexOf(events[4].newValue))

        assertEquals("B2", valueOf<Any>(events[5].oldValue))
        assertEquals("B1", valueOf<Any>(events[5].newValue))
        assertEquals(listOf("B"), headerOf(events[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[5].newValue).labels)
        assertEquals(2, indexOf(events[5].oldValue))
        assertEquals(2, indexOf(events[5].newValue))

        assertEquals("B3", valueOf<Any>(events[6].oldValue))
        assertEquals("B2", valueOf<Any>(events[6].newValue))
        assertEquals(listOf("B"), headerOf(events[6].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[6].newValue).labels)
        assertEquals(3, indexOf(events[6].oldValue))
        assertEquals(3, indexOf(events[6].newValue))

        assertEquals(Unit, valueOf<Any>(events[7].oldValue))
        assertEquals("B3", valueOf<Any>(events[7].newValue))
        assertEquals(listOf("B"), headerOf(events[7].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[7].newValue).labels)
        assertEquals(4, indexOf(events[7].oldValue))
        assertEquals(4, indexOf(events[7].newValue))

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B2", "B1", "B2", "B3"), valuesOf<Any>(t["B"]).toList())
        // TODO indexOf test

        // Copy to before
        copy(t[1] before t[3])

        assertEquals(8, events.size)
        for (event in events) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })

            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events[0].oldValue))
        assertEquals("A0", valueOf<Any>(events[0].newValue))
        assertEquals(listOf("A"), headerOf(events[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[0].newValue).labels)
        assertEquals(-1, indexOf(events[0].oldValue))
        assertEquals(-1, indexOf(events[0].newValue))

        assertEquals("A0", valueOf<Any>(events[1].oldValue))
        assertEquals(Unit, valueOf<Any>(events[1].newValue))
        assertEquals(listOf("A"), headerOf(events[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[1].newValue).labels)
        assertEquals(0, indexOf(events[1].oldValue))
        assertEquals(0, indexOf(events[1].newValue))

        assertEquals(Unit, valueOf<Any>(events[2].oldValue))
        assertEquals("A1", valueOf<Any>(events[2].newValue))
        assertEquals(listOf("A"), headerOf(events[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[2].newValue).labels)
        assertEquals(1, indexOf(events[2].oldValue))
        assertEquals(1, indexOf(events[2].newValue))

        assertEquals("A1", valueOf<Any>(events[3].oldValue))
        assertEquals(Unit, valueOf<Any>(events[3].newValue))
        assertEquals(listOf("A"), headerOf(events[3].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events[3].newValue).labels)
        assertEquals(2, indexOf(events[3].oldValue))
        assertEquals(2, indexOf(events[3].newValue))

        assertEquals(Unit, valueOf<Any>(events[4].oldValue))
        assertEquals(Unit, valueOf<Any>(events[4].newValue))
        assertEquals(listOf("B"), headerOf(events[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[4].newValue).labels)
        assertEquals(-1, indexOf(events[4].oldValue))
        assertEquals(-1, indexOf(events[4].newValue))

        assertEquals(Unit, valueOf<Any>(events[5].oldValue))
        assertEquals("B2", valueOf<Any>(events[5].newValue))
        assertEquals(listOf("B"), headerOf(events[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[5].newValue).labels)
        assertEquals(0, indexOf(events[5].oldValue))
        assertEquals(0, indexOf(events[5].newValue))

        assertEquals("B2", valueOf<Any>(events[6].oldValue))
        assertEquals("B1", valueOf<Any>(events[6].newValue))
        assertEquals(listOf("B"), headerOf(events[6].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[6].newValue).labels)
        assertEquals(1, indexOf(events[6].oldValue))
        assertEquals(1, indexOf(events[6].newValue))

        assertEquals("B1", valueOf<Any>(events[7].oldValue))
        assertEquals("B2", valueOf<Any>(events[7].newValue))
        assertEquals(listOf("B"), headerOf(events[7].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events[7].newValue).labels)
        assertEquals(2, indexOf(events[7].oldValue))
        assertEquals(2, indexOf(events[7].newValue))

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("A0", "A1", "A3"), valuesOf<Any>(t["A"]).toList())
        assertEquals(listOf("B2", "B1", "B2", "B2", "B3"), valuesOf<Any>(t["B"]).toList())
    }

    @Test
    fun `copy rows to different table with rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        t2["A", 0] = "A0_1"
        t2["A", 1] = "A1_1"
        t2["A", 2] = "A2_1"
        t2["A", 3] = "A3_1"

        t2["B", 0] = "B0_1"
        t2["B", 1] = "B1_1"
        t2["B", 2] = "B2_1"
        t2["B", 3] = "B3_1"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()
        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_1", "A1_1", "A2_1", "A3_1"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B2_1", "B3_1"), valuesOf<Any>(t2["B"]).toList())

        // Copy to after
        copy(t1[0] after t2[2])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(4, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.newValue.table).toList())
        }

        assertEquals("A3_1", valueOf<Any>(events2[0].oldValue))
        assertEquals("A0_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(3, indexOf(events2[0].oldValue))
        assertEquals(3, indexOf(events2[0].newValue))

        assertEquals(Unit, valueOf<Any>(events2[1].oldValue))
        assertEquals("A3_1", valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("A"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[1].newValue).labels)
        assertEquals(4, indexOf(events2[1].oldValue))
        assertEquals(4, indexOf(events2[1].newValue))

        assertEquals("B3_1", valueOf<Any>(events2[2].oldValue))
        assertEquals("B0_0", valueOf<Any>(events2[2].newValue))
        assertEquals(listOf("B"), headerOf(events2[2].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[2].newValue).labels)
        assertEquals(3, indexOf(events2[2].oldValue))
        assertEquals(3, indexOf(events2[2].newValue))

        assertEquals(Unit, valueOf<Any>(events2[3].oldValue))
        assertEquals("B3_1", valueOf<Any>(events2[3].newValue))
        assertEquals(listOf("B"), headerOf(events2[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[3].newValue).labels)
        assertEquals(4, indexOf(events2[3].oldValue))
        assertEquals(4, indexOf(events2[3].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_1", "A1_1", "A2_1", "A0_0", "A3_1"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B2_1", "B0_0", "B3_1"), valuesOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t1[1] after t2[4])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events2[0].oldValue))
        assertEquals("A1_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(5, indexOf(events2[0].oldValue))
        assertEquals(5, indexOf(events2[0].newValue))

        assertEquals(Unit, valueOf<Any>(events2[1].oldValue))
        assertEquals("B1_0", valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("B"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[1].newValue).labels)
        assertEquals(5, indexOf(events2[1].oldValue))
        assertEquals(5, indexOf(events2[1].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_1", "A1_1", "A2_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B2_1", "B0_0", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())

        // Copy to first
        copy(t1[3] before t2[0])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events2[0].oldValue))
        assertEquals("A3_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(-1, indexOf(events2[0].oldValue))
        assertEquals(-1, indexOf(events2[0].newValue))

        assertEquals(Unit, valueOf<Any>(events2[1].oldValue))
        assertEquals("B3_0", valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("B"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[1].newValue).labels)
        assertEquals(-1, indexOf(events2[1].oldValue))
        assertEquals(-1, indexOf(events2[1].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A0_1", "A1_1", "A2_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B0_1", "B1_1", "B2_1", "B0_0", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())

        // Copy to before
        copy(t1[2] before t2[1])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(6, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-2L, -1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events2[0].oldValue))
        assertEquals("A3_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(-2, indexOf(events2[0].oldValue))
        assertEquals(-2, indexOf(events2[0].newValue))

        assertEquals("A3_0", valueOf<Any>(events2[1].oldValue))
        assertEquals("A0_1", valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("A"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[1].newValue).labels)
        assertEquals(-1, indexOf(events2[1].oldValue))
        assertEquals(-1, indexOf(events2[1].newValue))

        assertEquals("A0_1", valueOf<Any>(events2[2].oldValue))
        assertEquals("A2_0", valueOf<Any>(events2[2].newValue))
        assertEquals(listOf("A"), headerOf(events2[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[2].newValue).labels)
        assertEquals(0, indexOf(events2[2].oldValue))
        assertEquals(0, indexOf(events2[2].newValue))

        assertEquals(Unit, valueOf<Any>(events2[3].oldValue))
        assertEquals("B3_0", valueOf<Any>(events2[3].newValue))
        assertEquals(listOf("B"), headerOf(events2[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[3].newValue).labels)
        assertEquals(-2, indexOf(events2[3].oldValue))
        assertEquals(-2, indexOf(events2[3].newValue))

        assertEquals("B3_0", valueOf<Any>(events2[4].oldValue))
        assertEquals("B0_1", valueOf<Any>(events2[4].newValue))
        assertEquals(listOf("B"), headerOf(events2[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[4].newValue).labels)
        assertEquals(-1, indexOf(events2[4].oldValue))
        assertEquals(-1, indexOf(events2[4].newValue))

        assertEquals("B0_1", valueOf<Any>(events2[5].oldValue))
        assertEquals("B2_0", valueOf<Any>(events2[5].newValue))
        assertEquals(listOf("B"), headerOf(events2[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[5].newValue).labels)
        assertEquals(0, indexOf(events2[5].oldValue))
        assertEquals(0, indexOf(events2[5].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A0_1", "A2_0", "A1_1", "A2_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B0_1", "B2_0", "B1_1", "B2_1", "B0_0", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `copy sparse rows to different table with rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 3] = "A3_0"

        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        t2["A", 1] = "A1_1"
        t2["A", 2] = "A2_1"
        t2["A", 3] = "A3_1"

        t2["B", 0] = "B0_1"
        t2["B", 1] = "B1_1"
        t2["B", 3] = "B3_1"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()
        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A1_1", "A2_1", "A3_1"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B3_1"), valuesOf<Any>(t2["B"]).toList())

        // Copy to after
        copy(t1[0] after t2[2])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(4, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(0L, 1L, 2L, 3L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.newValue.table).toList())
        }

        assertEquals("A3_1", valueOf<Any>(events2[0].oldValue))
        assertEquals("A0_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(3, indexOf(events2[0].oldValue))
        assertEquals(3, indexOf(events2[0].newValue))

        assertEquals(Unit, valueOf<Any>(events2[1].oldValue))
        assertEquals("A3_1", valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("A"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[1].newValue).labels)
        assertEquals(4, indexOf(events2[1].oldValue))
        assertEquals(4, indexOf(events2[1].newValue))

        assertEquals("B3_1", valueOf<Any>(events2[2].oldValue))
        assertEquals(Unit, valueOf<Any>(events2[2].newValue))
        assertEquals(listOf("B"), headerOf(events2[2].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[2].newValue).labels)
        assertEquals(3, indexOf(events2[2].oldValue))
        assertEquals(3, indexOf(events2[2].newValue))

        assertEquals(Unit, valueOf<Any>(events2[3].oldValue))
        assertEquals("B3_1", valueOf<Any>(events2[3].newValue))
        assertEquals(listOf("B"), headerOf(events2[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[3].newValue).labels)
        assertEquals(4, indexOf(events2[3].oldValue))
        assertEquals(4, indexOf(events2[3].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A1_1", "A2_1", "A0_0", "A3_1"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B3_1"), valuesOf<Any>(t2["B"]).toList())
        // TODO indexOf test

        // Copy to last
        copy(t1[1] after t2[4])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events2[0].oldValue))
        assertEquals("A1_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(5, indexOf(events2[0].oldValue))
        assertEquals(5, indexOf(events2[0].newValue))

        assertEquals(Unit, valueOf<Any>(events2[1].oldValue))
        assertEquals("B1_0", valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("B"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[1].newValue).labels)
        assertEquals(5, indexOf(events2[1].oldValue))
        assertEquals(5, indexOf(events2[1].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A1_1", "A2_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_1", "B1_1", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())

        // Copy to first
        copy(t1[3] before t2[0])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events2[0].oldValue))
        assertEquals("A3_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(-1, indexOf(events2[0].oldValue))
        assertEquals(-1, indexOf(events2[0].newValue))

        assertEquals(Unit, valueOf<Any>(events2[1].oldValue))
        assertEquals("B3_0", valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("B"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[1].newValue).labels)
        assertEquals(-1, indexOf(events2[1].oldValue))
        assertEquals(-1, indexOf(events2[1].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A1_1", "A2_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B0_1", "B1_1", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())

        // Copy to before
        copy(t1[2] before t2[1])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(6, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(-1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-2L, -1L, 0L, 1L, 2L, 3L, 4L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events2[0].oldValue))
        assertEquals("A3_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(-2, indexOf(events2[0].oldValue))
        assertEquals(-2, indexOf(events2[0].newValue))

        assertEquals("A3_0", valueOf<Any>(events2[1].oldValue))
        assertEquals(Unit, valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("A"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[1].newValue).labels)
        assertEquals(-1, indexOf(events2[1].oldValue))
        assertEquals(-1, indexOf(events2[1].newValue))

        assertEquals(Unit, valueOf<Any>(events2[2].oldValue))
        assertEquals(Unit, valueOf<Any>(events2[2].newValue))
        assertEquals(listOf("A"), headerOf(events2[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[2].newValue).labels)
        assertEquals(0, indexOf(events2[2].oldValue))
        assertEquals(0, indexOf(events2[2].newValue))

        assertEquals(Unit, valueOf<Any>(events2[3].oldValue))
        assertEquals("B3_0", valueOf<Any>(events2[3].newValue))
        assertEquals(listOf("B"), headerOf(events2[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[3].newValue).labels)
        assertEquals(-2, indexOf(events2[3].oldValue))
        assertEquals(-2, indexOf(events2[3].newValue))

        assertEquals("B3_0", valueOf<Any>(events2[4].oldValue))
        assertEquals("B0_1", valueOf<Any>(events2[4].newValue))
        assertEquals(listOf("B"), headerOf(events2[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[4].newValue).labels)
        assertEquals(-1, indexOf(events2[4].oldValue))
        assertEquals(-1, indexOf(events2[4].newValue))

        assertEquals("B0_1", valueOf<Any>(events2[5].oldValue))
        assertEquals("B2_0", valueOf<Any>(events2[5].newValue))
        assertEquals(listOf("B"), headerOf(events2[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[5].newValue).labels)
        assertEquals(0, indexOf(events2[5].oldValue))
        assertEquals(0, indexOf(events2[5].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A1_1", "A2_1", "A0_0", "A3_1", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B0_1", "B2_0", "B1_1", "B3_1", "B1_0"), valuesOf<Any>(t2["B"]).toList())
    }

    @Test
    fun `copy rows to different table without rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 2] = "A2_0"
        t1["A", 3] = "A3_0"

        t1["B", 0] = "B0_0"
        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()
        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf<Any>(), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf<Any>(), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf<Any>(), valuesOf<Any>(t2["B"]).toList())

        // Copy to after
        copy(t1[0] after t2[2])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(emptyList<List<Any>>(), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(emptyList<List<Any>>(), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(emptyList<Long>(), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(3L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(emptyList<Long>(), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(3L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A0_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(3, indexOf(events2.first().oldValue))
        assertEquals(3, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B0_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(3, indexOf(events2.last().oldValue))
        assertEquals(3, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_0"), valuesOf<Any>(t2["B"]).toList())
        assertEquals(listOf(3L), indexesOf(t2["A"]).toList())
        assertEquals(listOf(3L), indexesOf(t2["B"]).toList())

        // Copy to last
        copy(t1[1] after t2[4])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(3L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(3L, 5L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(3L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(3L, 5L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A1_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(5, indexOf(events2.first().oldValue))
        assertEquals(5, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B1_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(5, indexOf(events2.last().oldValue))
        assertEquals(5, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0"), valuesOf<Any>(t2["B"]).toList())
        assertEquals(listOf(3L, 5L), indexesOf(t2["A"]).toList())
        assertEquals(listOf(3L, 5L), indexesOf(t2["B"]).toList())

        // Copy to first
        copy(t1[3] before t2[0])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(3L, 5L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(-1L, 3L, 5L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(3L, 5L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(-1L, 3L, 5L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A3_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(-1, indexOf(events2.first().oldValue))
        assertEquals(-1, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B3_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(-1, indexOf(events2.last().oldValue))
        assertEquals(-1, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B0_0", "B1_0"), valuesOf<Any>(t2["B"]).toList())
        assertEquals(listOf(-1L, 3L, 5L), indexesOf(t2["A"]).toList())
        assertEquals(listOf(-1L, 3L, 5L), indexesOf(t2["B"]).toList())

        // Copy to before
        copy(t1[2] before t2[1])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(6, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(-1L, 3L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-2L, 0L, 3L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events2[0].oldValue))
        assertEquals("A3_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(-2, indexOf(events2[0].oldValue))
        assertEquals(-2, indexOf(events2[0].newValue))

        assertEquals("A3_0", valueOf<Any>(events2[1].oldValue))
        assertEquals(Unit, valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("A"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[1].newValue).labels)
        assertEquals(-1, indexOf(events2[1].oldValue))
        assertEquals(-1, indexOf(events2[1].newValue))

        assertEquals(Unit, valueOf<Any>(events2[2].oldValue))
        assertEquals("A2_0", valueOf<Any>(events2[2].newValue))
        assertEquals(listOf("A"), headerOf(events2[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[2].newValue).labels)
        assertEquals(0, indexOf(events2[2].oldValue))
        assertEquals(0, indexOf(events2[2].newValue))

        assertEquals(Unit, valueOf<Any>(events2[3].oldValue))
        assertEquals("B3_0", valueOf<Any>(events2[3].newValue))
        assertEquals(listOf("B"), headerOf(events2[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[3].newValue).labels)
        assertEquals(-2, indexOf(events2[3].oldValue))
        assertEquals(-2, indexOf(events2[3].newValue))

        assertEquals("B3_0", valueOf<Any>(events2[4].oldValue))
        assertEquals(Unit, valueOf<Any>(events2[4].newValue))
        assertEquals(listOf("B"), headerOf(events2[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[4].newValue).labels)
        assertEquals(-1, indexOf(events2[4].oldValue))
        assertEquals(-1, indexOf(events2[4].newValue))

        assertEquals(Unit, valueOf<Any>(events2[5].oldValue))
        assertEquals("B2_0", valueOf<Any>(events2[5].newValue))
        assertEquals(listOf("B"), headerOf(events2[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[5].newValue).labels)
        assertEquals(0, indexOf(events2[5].oldValue))
        assertEquals(0, indexOf(events2[5].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A2_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B0_0", "B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A2_0", "A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B2_0", "B0_0", "B1_0"), valuesOf<Any>(t2["B"]).toList())
        assertEquals(listOf(-2L, 0L, 3L, 5L), indexesOf(t2["A"]).toList())
        assertEquals(listOf(-2L, 0L, 3L, 5L), indexesOf(t2["B"]).toList())
    }

    @Test
    fun `copy sparse rows to different table without rows`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "A0_0"
        t1["A", 1] = "A1_0"
        t1["A", 3] = "A3_0"

        t1["B", 1] = "B1_0"
        t1["B", 2] = "B2_0"
        t1["B", 3] = "B3_0"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()
        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf<Any>(), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf<Any>(), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf<Any>(), valuesOf<Any>(t2["B"]).toList())

        // Copy to after
        copy(t1[0] after t2[2])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(emptyList<List<Any>>(), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(emptyList<List<Any>>(), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(emptyList<Long>(), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(3L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(emptyList<Long>(), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(3L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A0_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(3, indexOf(events2.first().oldValue))
        assertEquals(3, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals(Unit, valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(3, indexOf(events2.last().oldValue))
        assertEquals(3, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf(3L), indexesOf(t2["A"]).toList())

        // Copy to last
        copy(t1[1] after t2[4])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(3L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(3L, 5L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(3L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(3L, 5L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A1_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(5, indexOf(events2.first().oldValue))
        assertEquals(5, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B1_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(5, indexOf(events2.last().oldValue))
        assertEquals(5, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B1_0"), valuesOf<Any>(t2["B"]).toList())
        assertEquals(listOf(3L, 5L), indexesOf(t2["A"]).toList())
        assertEquals(listOf(5L), indexesOf(t2["B"]).toList())

        // Copy to first
        copy(t1[3] before t2[0])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals(listOf(3L, 5L), indexesOf(events2.first().oldValue.table).toList())
        assertEquals(listOf(-1L, 3L, 5L), indexesOf(events2.first().newValue.table).toList())
        assertEquals(listOf(3L, 5L), indexesOf(events2.last().oldValue.table).toList())
        assertEquals(listOf(-1L, 3L, 5L), indexesOf(events2.last().newValue.table).toList())

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("A3_0", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(-1, indexOf(events2.first().oldValue))
        assertEquals(-1, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("B3_0", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("B"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.last().newValue).labels)
        assertEquals(-1, indexOf(events2.last().oldValue))
        assertEquals(-1, indexOf(events2.last().newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B1_0"), valuesOf<Any>(t2["B"]).toList())
        assertEquals(listOf(-1L, 3L, 5L), indexesOf(t2["A"]).toList())
        assertEquals(listOf(-1L, 5L), indexesOf(t2["B"]).toList())

        // Copy to before
        copy(t1[2] before t2[1])

        assertEquals(0, events1.size)
        events1 = emptyList()

        assertEquals(6, events2.size)
        for (event in events2) {
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.oldValue.table).toList().map { it.labels })
            assertEquals(listOf(listOf("A"), listOf("B")), headersOf(event.newValue.table).toList().map { it.labels })
            assertEquals(listOf(-1L, 3L, 5L), indexesOf(event.oldValue.table).toList())
            assertEquals(listOf(-2L, 0L, 3L, 5L), indexesOf(event.newValue.table).toList())
        }

        assertEquals(Unit, valueOf<Any>(events2[0].oldValue))
        assertEquals("A3_0", valueOf<Any>(events2[0].newValue))
        assertEquals(listOf("A"), headerOf(events2[0].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[0].newValue).labels)
        assertEquals(-2, indexOf(events2[0].oldValue))
        assertEquals(-2, indexOf(events2[0].newValue))

        assertEquals("A3_0", valueOf<Any>(events2[1].oldValue))
        assertEquals(Unit, valueOf<Any>(events2[1].newValue))
        assertEquals(listOf("A"), headerOf(events2[1].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[1].newValue).labels)
        assertEquals(-1, indexOf(events2[1].oldValue))
        assertEquals(-1, indexOf(events2[1].newValue))

        assertEquals(Unit, valueOf<Any>(events2[2].oldValue))
        assertEquals(Unit, valueOf<Any>(events2[2].newValue))
        assertEquals(listOf("A"), headerOf(events2[2].oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2[2].newValue).labels)
        assertEquals(0, indexOf(events2[2].oldValue))
        assertEquals(0, indexOf(events2[2].newValue))

        assertEquals(Unit, valueOf<Any>(events2[3].oldValue))
        assertEquals("B3_0", valueOf<Any>(events2[3].newValue))
        assertEquals(listOf("B"), headerOf(events2[3].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[3].newValue).labels)
        assertEquals(-2, indexOf(events2[3].oldValue))
        assertEquals(-2, indexOf(events2[3].newValue))

        assertEquals("B3_0", valueOf<Any>(events2[4].oldValue))
        assertEquals(Unit, valueOf<Any>(events2[4].newValue))
        assertEquals(listOf("B"), headerOf(events2[4].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[4].newValue).labels)
        assertEquals(-1, indexOf(events2[4].oldValue))
        assertEquals(-1, indexOf(events2[4].newValue))

        assertEquals(Unit, valueOf<Any>(events2[5].oldValue))
        assertEquals("B2_0", valueOf<Any>(events2[5].newValue))
        assertEquals(listOf("B"), headerOf(events2[5].oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2[5].newValue).labels)
        assertEquals(0, indexOf(events2[5].oldValue))
        assertEquals(0, indexOf(events2[5].newValue))

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("A0_0", "A1_0", "A3_0"), valuesOf<Any>(t1["A"]).toList())
        assertEquals(listOf("B1_0", "B2_0", "B3_0"), valuesOf<Any>(t1["B"]).toList())

        assertEquals(listOf(listOf("A"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("A3_0", "A0_0", "A1_0"), valuesOf<Any>(t2["A"]).toList())
        assertEquals(listOf("B3_0", "B2_0", "B1_0"), valuesOf<Any>(t2["B"]).toList())
        assertEquals(listOf(-2L, 3L, 5L), indexesOf(t2["A"]).toList())
        assertEquals(listOf(-2L, 0L, 5L), indexesOf(t2["B"]).toList())
    }
}
