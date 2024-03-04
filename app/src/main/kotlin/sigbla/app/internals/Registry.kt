/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import sigbla.app.*
import sigbla.app.TableRef
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.exceptions.InvalidTableViewException
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap

internal object Registry {
    private val tables: SortedMap<String, Table> = ConcurrentSkipListMap()
    private val views: SortedMap<String, TableView> = ConcurrentSkipListMap()

    fun setTable(name: String, table: Table) = deleteTable(tables.put(name, table))

    fun getTable(name: String): Table? = tables[name]

    fun getTable(name: String, init: (String) -> Table): Table = tables.computeIfAbsent(name, init)

    fun tableNames(): SortedSet<String> = Collections.unmodifiableSortedSet(tables.keys.toSortedSet())

    fun deleteTable(name: String) = deleteTable(tables.remove(name))

    fun deleteTable(table: Table?) {
        if (table == null) return

        if (table.name != null) tables.remove(table.name, table)

        table.closed = true

        if (table !is BaseTable) throw InvalidTableException("Unsupported table type")

        table.eventProcessor.shutdown()
        table.tableRef.set(TableRef(version = Long.MAX_VALUE))
    }

    fun setView(name: String, view: TableView) = deleteView(views.put(name, view))

    fun getView(name: String): TableView? = views[name]

    fun getView(name: String, init: (String) -> TableView): TableView = views.computeIfAbsent(name, init)

    fun viewNames(): SortedSet<String> = Collections.unmodifiableSortedSet(views.keys.toSortedSet())

    fun deleteView(name: String) = deleteView(views.remove(name))

    private fun deleteView(view: TableView?) {
        if (view == null) return

        view.eventProcessor.shutdown()
        view.tableViewRef.set(TableViewRef())
    }
}
