package sigbla.app.internals

import sigbla.app.*
import sigbla.app.TableRef
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.exceptions.InvalidTableViewException
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap

object Registry {
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
        table.tableRef.set(TableRef())
    }

    fun setView(name: String, view: TableView) = deleteView(views.put(name, view))

    fun getView(name: String): TableView? = views[name]

    fun viewNames(): SortedSet<String> = Collections.unmodifiableSortedSet(views.keys.toSortedSet())

    fun deleteView(name: String) = deleteView(views.remove(name))

    private fun deleteView(view: TableView?) {
        if (view == null) return

        if (view !is BaseTableView) throw InvalidTableViewException()

        view.eventProcessor.shutdown()
        view.tableViewRef.set(TableViewRef(
            DefaultColumnView(),
            DefaultRowView()
        ))
    }
}