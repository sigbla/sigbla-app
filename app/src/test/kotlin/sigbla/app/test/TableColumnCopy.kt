/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.After

class TableColumnCopy {
    @After
    fun cleanup() {
        Table.names.forEach { Table.delete(it) }
    }

    @Test
    fun `copy columns within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "First"
        t["B", 0] = "Middle"
        t["C", 0] = "Last"

        var events = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t) {
            skipHistory = true

            events {
                events = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t[0].map { valueOf<Any>(it) })

        // Internal copy with same name is just a move
        copy(t["A"] after t["B"])

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals("First", valueOf<Any>(events.first().oldValue))
        assertEquals("First", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("First", "Middle", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Middle", "First", "Last"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("Middle", "First", "Last"), t[0].map { valueOf<Any>(it) })

        copy(t["A"] before t["B"])

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals("First", valueOf<Any>(events.first().oldValue))
        assertEquals("First", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("Middle", "First", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First", "Middle", "Last"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy A to A2 after B
        copy(t["A"] after t["B"], "A2")

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("A2"), listOf("C")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("First", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A2"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A2"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("First", "Middle", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First", "Middle", "First", "Last"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("A2"), listOf("C")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "First", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy B to B2 last
        copy(t["B"] after t["C"], "B2")

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("A2"), listOf("C")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("A2"), listOf("C"), listOf("B2")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("Middle", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("B2"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("B2"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("First", "Middle", "First", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First", "Middle", "First", "Last", "Middle"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("A2"), listOf("C"), listOf("B2")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "First", "Last", "Middle"), t[0].map { valueOf<Any>(it) })

        // Copy C to C2 first
        copy(t["C"] before t["A"], "C2")

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("A2"), listOf("C"), listOf("B2")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("C2"), listOf("A"), listOf("B"), listOf("A2"), listOf("C"), listOf("B2")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("Last", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("C2"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("C2"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("First", "Middle", "First", "Last", "Middle"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Last", "First", "Middle", "First", "Last", "Middle"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("C2"), listOf("A"), listOf("B"), listOf("A2"), listOf("C"), listOf("B2")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("Last", "First", "Middle", "First", "Last", "Middle"), t[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `overwrite columns within same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A0", 0] = "First"
        t["B", 0] = "Middle 1"
        t["C", 0] = "Middle 2"
        t["D", 0] = "Last"

        var events = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t) {
            skipHistory = true

            events {
                events = toList()
            }
        }

        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), t[0].map { valueOf<Any>(it) })

        // No-op
        copy(t["A0"] to t["A0"])

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals("First", valueOf<Any>(events.first().oldValue))
        assertEquals("First", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A0"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A0"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), t[0].map { valueOf<Any>(it) })

        // Rename
        copy(t["A0"] to t["A0"], "A")

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals(listOf(listOf("A0"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals("First", valueOf<Any>(events.first().oldValue))
        assertEquals(Unit, valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A0"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A0"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(Unit, valueOf<Any>(events.last().oldValue))
        assertEquals("First", valueOf<Any>(events.last().newValue))
        assertEquals(listOf("A"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.last().newValue).labels)
        assertEquals(0, indexOf(events.last().oldValue))
        assertEquals(0, indexOf(events.last().newValue))

        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy to in between
        copy(t["A"] to t["C"])

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals("Middle 2", valueOf<Any>(events.first().oldValue))
        assertEquals("First", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("C"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("C"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("First", "Middle 1", "Middle 2", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First", "Middle 1", "First", "Last"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle 1", "First", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy to last
        copy(t["B"] to t["D"], "D2")

        assertEquals(2, events.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D2")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")), headersOf(events.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D2")), headersOf(events.last().newValue.table).toList().map { it.labels })

        assertEquals("Last", valueOf<Any>(events.first().oldValue))
        assertEquals(Unit, valueOf<Any>(events.first().newValue))
        assertEquals(listOf("D"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("D"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(Unit, valueOf<Any>(events.last().oldValue))
        assertEquals("Middle 1", valueOf<Any>(events.last().newValue))
        assertEquals(listOf("D2"), headerOf(events.last().oldValue).labels)
        assertEquals(listOf("D2"), headerOf(events.last().newValue).labels)
        assertEquals(0, indexOf(events.last().oldValue))
        assertEquals(0, indexOf(events.last().newValue))

        assertEquals(listOf("First", "Middle 1", "First", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First", "Middle 1", "First", "Middle 1"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D2")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle 1", "First", "Middle 1"), t[0].map { valueOf<Any>(it) })

        // Copy to C, replace first
        t["E", 0] = "New First"

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D2")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D2"), listOf("E")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("New First", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("E"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("E"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("First", "Middle 1", "First", "Middle 1"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First", "Middle 1", "First", "Middle 1", "New First"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        copy(t["E"] to t["C"], "A")

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("D2"), listOf("E")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("Middle 1", "New First", "Middle 1", "New First"), t[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `copy columns between tables with columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First"
        t1["B", 0] = "Middle"
        t1["C", 0] = "Last"

        t2["T2", 0] = "T2 cell"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        // Copy to after T2
        copy(t1["A"] after t2["T2"])

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("T2")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("T2"), listOf("A")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("First", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("T2 cell"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("T2 cell", "First"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("A")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("T2 cell", "First"), t2[0].map { valueOf<Any>(it) })

        // Copy to before T2
        copy(t1["B"] before t2["T2"], "B2")

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("T2"), listOf("A")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("B2"), listOf("T2"), listOf("A")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("Middle", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("B2"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("B2"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("T2 cell", "First"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Middle", "T2 cell", "First"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B2"), listOf("T2"), listOf("A")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("Middle", "T2 cell", "First"), t2[0].map { valueOf<Any>(it) })

        // Copy to in between
        copy(t1["C"] after t2["B2"], "C2")

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("B2"), listOf("T2"), listOf("A")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("B2"), listOf("C2"), listOf("T2"), listOf("A")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("Last", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("C2"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("C2"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("Middle", "T2 cell", "First"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Middle", "Last", "T2 cell", "First"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B2"), listOf("C2"), listOf("T2"), listOf("A")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("Middle", "Last", "T2 cell", "First"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `overwrite columns between tables with columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First 1"
        t1["B", 0] = "Middle 1"
        t1["C", 0] = "Last 1"

        t2["A", 0] = "First 2"
        t2["B", 0] = "Middle 2"
        t2["C", 0] = "Last 2"

        var events1 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t1) {
            skipHistory = true

            events {
                events1 = toList()
            }
        }

        var events2 = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t2) {
            skipHistory = true

            events {
                events2 = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("First 2", "Middle 2", "Last 2"), t2[0].map { valueOf<Any>(it) })

        // Copy T1["A"] to T2
        copy(t1["A"] to t2["A"])

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals("First 2", valueOf<Any>(events2.first().oldValue))
        assertEquals("First 1", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("First 2", "Middle 2", "Last 2"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First 1", "Middle 2", "Last 2"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("First 1", "Middle 2", "Last 2"), t2[0].map { valueOf<Any>(it) })

        // Copy T1["C"] to T2 with new name
        copy(t1["C"] to t2["C"], "C2")

        assertEquals(0, events1.size)

        assertEquals(2, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(events2.last().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headersOf(events2.last().newValue.table).toList().map { it.labels })

        assertEquals("Last 2", valueOf<Any>(events2.first().oldValue))
        assertEquals(Unit, valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("C"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("C"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(Unit, valueOf<Any>(events2.last().oldValue))
        assertEquals("Last 1", valueOf<Any>(events2.last().newValue))
        assertEquals(listOf("C2"), headerOf(events2.last().oldValue).labels)
        assertEquals(listOf("C2"), headerOf(events2.last().newValue).labels)
        assertEquals(0, indexOf(events2.last().oldValue))
        assertEquals(0, indexOf(events2.last().newValue))

        assertEquals(listOf("First 1", "Middle 2", "Last 2"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First 1", "Middle 2", "Last 1"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        assertEquals(listOf("First 1", "Middle 2", "Last 2"), events2.last().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First 1", "Middle 2", "Last 1"), events2.last().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("First 1", "Middle 2", "Last 1"), t2[0].map { valueOf<Any>(it) })

        // Copy T1["B"] to T2 with same name
        copy(t1["B"] to t2["B"], "B")

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals("Middle 2", valueOf<Any>(events2.first().oldValue))
        assertEquals("Middle 1", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("B"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("First 1", "Middle 2", "Last 1"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C2")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("First 1", "Middle 1", "Last 1"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `copy columns to end of same table`() {
        val t = Table[object {}.javaClass.enclosingMethod.name]

        t["A", 0] = "First"
        t["B", 0] = "Middle"
        t["C", 0] = "Last"

        var events = emptyList<TableListenerEvent<out Any, out Any>>()

        on(t) {
            skipHistory = true

            events {
                events = toList()
            }
        }

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy first
        copy(t["A"] to t)

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("B"), listOf("C"), listOf("A")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals("First", valueOf<Any>(events.first().oldValue))
        assertEquals("First", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("A"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("First", "Middle", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Middle", "Last", "First"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("B"), listOf("C"), listOf("A")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("Middle", "Last", "First"), t[0].map { valueOf<Any>(it) })

        // Copy last
        copy(t["C"] to t)

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("B"), listOf("C"), listOf("A")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals("Last", valueOf<Any>(events.first().oldValue))
        assertEquals("Last", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("C"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("C"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("Middle", "Last", "First"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Middle", "First", "Last"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("Middle", "First", "Last"), t[0].map { valueOf<Any>(it) })

        // Copy middle
        copy(t["B"] to t, "B2")

        assertEquals(1, events.size)
        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headersOf(events.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C"), listOf("B2")), headersOf(events.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events.first().oldValue))
        assertEquals("Middle", valueOf<Any>(events.first().newValue))
        assertEquals(listOf("B2"), headerOf(events.first().oldValue).labels)
        assertEquals(listOf("B2"), headerOf(events.first().newValue).labels)
        assertEquals(0, indexOf(events.first().oldValue))
        assertEquals(0, indexOf(events.first().newValue))

        assertEquals(listOf("Middle", "First", "Last"), events.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Middle", "First", "Last", "Middle"), events.first().newValue.table[0].map { valueOf<Any>(it) })

        events = emptyList()

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C"), listOf("B2")), headersOf(t).toList().map { it.labels })
        assertEquals(listOf("Middle", "First", "Last", "Middle"), t[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `copy columns to end of different table with columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First"
        t1["B", 0] = "Middle"
        t1["C", 0] = "Last"

        t2["T2", 0] = "T2 cell"

        // TODO Not testing copy between tables where column name exists in target below.. (also check other copy/move tests)

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

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        // Copy middle to T2
        copy(t1["B"] to t2)

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("T2")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("T2"), listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("Middle", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("B"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("T2 cell"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("T2 cell", "Middle"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("T2 cell", "Middle"), t2[0].map { valueOf<Any>(it) })

        // Copy first to T2
        copy(t1["A"] to t2)

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("T2"), listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("T2"), listOf("B"), listOf("A")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("First", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("T2 cell", "Middle"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("T2 cell", "Middle", "First"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("B"), listOf("A")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("T2 cell", "Middle", "First"), t2[0].map { valueOf<Any>(it) })

        // Copy last to T2
        copy(t1["C"] to t2)

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("T2"), listOf("B"), listOf("A")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("T2"), listOf("B"), listOf("A"), listOf("C")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("Last", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("C"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("C"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("T2 cell", "Middle", "First"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("T2 cell", "Middle", "First", "Last"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("T2"), listOf("B"), listOf("A"), listOf("C")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("T2 cell", "Middle", "First", "Last"), t2[0].map { valueOf<Any>(it) })
    }

    @Test
    fun `copy columns to end of different table without columns`() {
        val t1 = Table[object {}.javaClass.enclosingMethod.name + " 1"]
        val t2 = Table[object {}.javaClass.enclosingMethod.name + " 2"]

        t1["A", 0] = "First"
        t1["B", 0] = "Middle"
        t1["C", 0] = "Last"

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

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        // Copy middle to T2
        copy(t1["B"] to t2)

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(emptyList<List<String>>(), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("B")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("Middle", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("B"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("B"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(emptyList<String>(), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Middle"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("Middle"), t2[0].map { valueOf<Any>(it) })

        // Copy first to T2
        copy(t1["A"] to t2)

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("B")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("B"), listOf("A")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("First", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("A"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("A"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("Middle"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Middle", "First"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B"), listOf("A")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("Middle", "First"), t2[0].map { valueOf<Any>(it) })

        // Copy last to T2
        copy(t1["C"] to t2)

        assertEquals(0, events1.size)

        assertEquals(1, events2.size)
        assertEquals(listOf(listOf("B"), listOf("A")), headersOf(events2.first().oldValue.table).toList().map { it.labels })
        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headersOf(events2.first().newValue.table).toList().map { it.labels })

        assertEquals(Unit, valueOf<Any>(events2.first().oldValue))
        assertEquals("Last", valueOf<Any>(events2.first().newValue))
        assertEquals(listOf("C"), headerOf(events2.first().oldValue).labels)
        assertEquals(listOf("C"), headerOf(events2.first().newValue).labels)
        assertEquals(0, indexOf(events2.first().oldValue))
        assertEquals(0, indexOf(events2.first().newValue))

        assertEquals(listOf("Middle", "First"), events2.first().oldValue.table[0].map { valueOf<Any>(it) })
        assertEquals(listOf("Middle", "First", "Last"), events2.first().newValue.table[0].map { valueOf<Any>(it) })

        events2 = emptyList()

        assertEquals(listOf(listOf("A"), listOf("B"), listOf("C")), headersOf(t1).toList().map { it.labels })
        assertEquals(listOf("First", "Middle", "Last"), t1[0].map { valueOf<Any>(it) })

        assertEquals(listOf(listOf("B"), listOf("A"), listOf("C")), headersOf(t2).toList().map { it.labels })
        assertEquals(listOf("Middle", "First", "Last"), t2[0].map { valueOf<Any>(it) })
    }

    // TODO Test events with multiple rows/columns, i.e., multiple events
}
