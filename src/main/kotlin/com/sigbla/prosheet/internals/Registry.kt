package com.sigbla.prosheet.internals

import com.sigbla.prosheet.exceptions.InvalidTableException
import com.sigbla.prosheet.table.BaseTable
import com.sigbla.prosheet.table.Column
import com.sigbla.prosheet.table.Table
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap

internal object Registry {
    private val tables: SortedMap<String, Table> = ConcurrentSkipListMap()

    fun setTable(name: String, table: Table) = deleteTable(tables.put(name, table))

    fun getTable(name: String): Table? = tables[name]

    // TODO Make sure its immutable
    fun tableNames() = tables.keys.toSortedSet()

    fun deleteTable(name: String) {
        deleteTable(tables.remove(name))
    }

    private fun deleteTable(table: Table?) {
        if (table == null) return

        table.closed = true;

        if (table !is BaseTable) throw InvalidTableException()

        val columns = ArrayList<Column>()

        while (!table.columns.isEmpty()) {
            val (columnHeader, column) = table.columns.firstEntry() ?: continue
            if (table.columns.remove(columnHeader, column))
                columns.add(column)
        }

        table.indices.clear()

        columns.forEach(Column::clear)
    }
}