/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import sigbla.app.*
import sigbla.app.TableRef
import sigbla.app.exceptions.InvalidTableException
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap

internal object Registry {
    private val _tables: SortedMap<String, Table> = ConcurrentSkipListMap()
    private val _views: SortedMap<String, TableView> = ConcurrentSkipListMap()

    fun setTable(name: String, table: Table) = _tables.put(name, table)?.also { shutdownTable(it, false) }

    fun getTable(name: String): Table? = _tables[name]

    fun getTable(name: String, init: (String) -> Table): Table = _tables.computeIfAbsent(name, init)

    val tables: Set<Table> get() = Collections.unmodifiableSet(_tables.values.toSet())

    val tableNames: SortedSet<String> get() = Collections.unmodifiableSortedSet(_tables.keys.toSortedSet())

    fun shutdownTable(table: Table, remove: Boolean) {
        if (table !is BaseTable) throw InvalidTableException("Unsupported table type")

        synchronized(table.eventProcessor) {
            if (remove && table.name != null) _tables.remove(table.name, table)
            table.tableRef.closed = true
        }
    }

    fun setView(name: String, view: TableView) = _views.put(name, view)?.also { shutdownView(it, false) }

    fun getView(name: String): TableView? = _views[name]

    fun getView(name: String, init: (String) -> TableView): TableView = _views.computeIfAbsent(name, init)

    val views: Set<TableView> get() = Collections.unmodifiableSet(_views.values.toSet())

    val viewNames: SortedSet<String> get() = Collections.unmodifiableSortedSet(_views.keys.toSortedSet())

    fun shutdownView(view: TableView, remove: Boolean) {
        synchronized(view.eventProcessor) {
            if (remove && view.name != null) _views.remove(view.name, view)
            view.tableViewRef.closed = true
        }
    }
}
