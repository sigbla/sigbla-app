package sigbla.app.internals

import sigbla.app.exceptions.InvalidTableException
import sigbla.app.BaseTable
import sigbla.app.Column
import sigbla.app.Table
import sigbla.app.TableView
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap

internal object Registry {
    private val tables: SortedMap<String, Table> = ConcurrentSkipListMap()
    private val views: SortedMap<String, TableView> = ConcurrentSkipListMap()

    fun setTable(name: String, table: Table) = deleteTable(tables.put(name, table))

    fun getTable(name: String): Table? = tables[name]

    fun tableNames(): SortedSet<String> = Collections.unmodifiableSortedSet(tables.keys.toSortedSet())

    fun deleteTable(name: String) = deleteTable(tables.remove(name))

    private fun deleteTable(table: Table?) {
        if (table == null) return

        table.closed = true;

        if (table !is BaseTable) throw InvalidTableException()

        table.eventProcessor.shutdown()

        val columns = ArrayList<Column>()

        while (!table.columnsMap.isEmpty()) {
            val (columnHeader, column) = table.columnsMap.entries.firstOrNull() ?: continue
            if (table.columnsMap.remove(columnHeader, column))
                columns.add(column)
        }

        table.indicesMap.clear()

        columns.forEach(Column::clear)

        views.filter { it.value.table == table }.map { it.key }.forEach { views.remove(it) }
    }

    fun setView(name: String, view: TableView) = deleteView(views.put(name, view))

    fun getView(name: String): TableView? = views[name]

    fun viewNames(): SortedSet<String> = Collections.unmodifiableSortedSet(views.keys.toSortedSet())

    fun deleteView(name: String) = deleteView(views.remove(name))

    private fun deleteView(view: TableView?) {
        if (view == null) return

        // TODO Any cleanups?
    }
}