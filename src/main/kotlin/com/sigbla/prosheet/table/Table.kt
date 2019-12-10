package com.sigbla.prosheet.table

import com.sigbla.prosheet.exceptions.InvalidTableException
import com.sigbla.prosheet.internals.Registry
import com.sigbla.prosheet.storage.Storage
import java.util.*
import java.util.concurrent.ConcurrentNavigableMap
import java.util.concurrent.ConcurrentSkipListMap

// Look at this wrt Table, Column, Row: https://kotlinlang.org/docs/reference/operator-overloading.html

abstract class Table(val name: String) {
    @Volatile
    var closed: Boolean = false
        internal set

    abstract val headers: Collection<ColumnHeader>

    abstract operator fun get(header: ColumnHeader): Column

    operator fun get(vararg header: String): Column = get(ColumnHeader(*header))

    operator fun get(index: Long): Row = get(IndexRelation.AT, index)

    operator fun get(indexRelation: IndexRelation, index: Long): Row = BaseRow(this, indexRelation, index)

    operator fun get(header1: String, index: Long): Cell<*> = this[header1][index]

    operator fun get(header1: String, header2: String, index: Long): Cell<*> = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Long): Cell<*> = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Long): Cell<*> = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long): Cell<*> = this[header1, header2, header3, header4, header5][index]

    operator fun get(header1: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1][indexRelation, index]

    operator fun get(header1: String, header2: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2, header3][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2, header3, header4][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2, header3, header4, header5][indexRelation, index]

    operator fun get(index: Int): Row = get(IndexRelation.AT, index)

    operator fun get(indexRelation: IndexRelation, index: Int): Row = BaseRow(this, indexRelation, index.toLong())

    operator fun get(header1: String, index: Int): Cell<*> = this[header1][index]

    operator fun get(header1: String, header2: String, index: Int): Cell<*> = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Int): Cell<*> = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Int): Cell<*> = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int): Cell<*> = this[header1, header2, header3, header4, header5][index]

    operator fun get(header1: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1][indexRelation, index]

    operator fun get(header1: String, header2: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2, header3][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2, header3, header4][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2, header3, header4, header5][indexRelation, index]

    // TODO: set methods like above get methods

    abstract operator fun contains(header: ColumnHeader): Boolean

    fun contains(vararg header: String): Boolean = contains(ColumnHeader(*header))

    abstract fun remove(header: ColumnHeader)

    fun rename(existing: ColumnHeader, vararg newName: String) = rename(existing, ColumnHeader(*newName))

    abstract fun rename(existing: ColumnHeader, newName: ColumnHeader)

    fun remove(vararg header: String) = remove(ColumnHeader(*header))

    fun remove(index: Long) = this.headers.forEach { c -> this[c].remove(index) }

    companion object {
        fun newTable(name: String): Table = BaseTable(name)

        fun fromRegistry(name: String): Table = Registry.getTable(name) ?: throw InvalidTableException("No table by name $name")

        fun fromStorage(storage: Storage, name: String): Table {
            TODO()
        }

        fun fromStorageAs(storage: Storage, name: String, newName: String): Table {
            TODO()
        }

        fun fromStorageRange(storage: Storage, name: String, fromIndex: Long, toIndex: Long): Table {
            TODO()
        }

        fun fromStorageRangeAs(storage: Storage, name: String, fromIndex: Long, toIndex: Long, newName: String): Table {
            TODO()
        }

        fun registryTableNames(): SortedSet<String> = Registry.tableNames()

        fun deleteTable(name: String) = Registry.deleteTable(name)
    }
}

class BaseTable internal constructor(
    name: String,
    internal val columns: ConcurrentNavigableMap<ColumnHeader, Column> = ConcurrentSkipListMap(),
    internal val indices: ConcurrentNavigableMap<Long, Int> = ConcurrentSkipListMap()
) : Table(name) {
    init {
        Registry.setTable(name, this)
    }

    override val headers: Collection<ColumnHeader>
        get() = Collections.unmodifiableCollection(columns.keys.toList())

    override fun get(header: ColumnHeader): Column = columns.computeIfAbsent(header) {
        if (closed)
            throw InvalidTableException("Table is closed")

        BaseColumnOnHeap(this, header, indices)
    }

    override fun contains(header: ColumnHeader): Boolean = columns.containsKey(header)

    override fun remove(header: ColumnHeader) {
        columns.remove(header)?.clear()
    }

    override fun rename(existing: ColumnHeader, newName: ColumnHeader) {
        val column = columns[existing] ?: return

        columns.put(newName, column)?.clear()
        columns.remove(existing, column)
    }

    companion object
}
