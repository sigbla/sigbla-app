package sigbla.app

import sigbla.app.internals.Registry
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

// TODO Any iteration below needs to operate on a clone

inline fun <reified T> valueOf(cell: Cell<*>): T? = valueOf(cell, T::class) as T?

fun valueOf(cell: Cell<*>, typeFilter: KClass<*>): Any? = if (typeFilter.isInstance(cell.value)) cell.value else null

inline fun <reified T> valueOf(noinline source: DestinationOsmosis<Cell<*>>.() -> Unit): T? = valueOf(source, T::class) as T?

fun valueOf(source: DestinationOsmosis<Cell<*>>.() -> Unit, typeFilter: KClass<*>): Any? {
    val table = BaseTable("", false, AtomicReference(TableRef())) as Table
    table["valueOf", 0L] = source // Subscribe
    val value = valueOf(table["valueOf", 0L], typeFilter)
    table["valueOf", 0L] = null // Unsubscribe
    Registry.deleteTable(table) // Clean up
    return value
}

inline fun <reified T> valuesOf(cells: Iterable<Cell<*>>): Sequence<T> = valuesOf(cells, T::class) as Sequence<T>

fun valuesOf(cells: Iterable<Cell<*>>, typeFilter: KClass<*>): Sequence<Any> = cells
    .asSequence()
    .mapNotNull { valueOf(it, typeFilter) }

fun headerOf(cell: Cell<*>) = cell.column.columnHeader

fun headerOf(column: Column) = column.columnHeader

fun headersOf(row: Row) = row.headers.asSequence()

fun headersOf(cells: Iterable<Cell<*>>) = cells
    .asSequence()
    .map { it.column }
    .toSortedSet()
    .asSequence()
    .map { it.columnHeader }

fun columnOf(cell: Cell<*>) = cell.column

fun columnsOf(row: Row) = row.headers.asSequence().map { row.table[it] }

fun columnsOf(cells: Iterable<Cell<*>>) = cells
    .asSequence()
    .map { it.column }
    .toSortedSet()
    .asSequence()

fun indexOf(cell: Cell<*>) = cell.index

fun indexesOf(cells: Iterable<Cell<*>>) = cells
    .asSequence()
    .map { it.index }
    .toSortedSet()
    .asSequence()

// TODO We want specifics of header/column/indexOf for column/row/range, for efficiency

fun print(table: Table) {
    val table = clone(table)

    val headers = table.headers
    val indexes = table.indexes

    var maxCellWidth = 0
    val headerTable = Table[null].let { headerTable ->
        for ((index, header) in headers.withIndex()) {
            headerTable[index.toString()].let { headerColumn ->
                for ((index, headerCell) in header.header.withIndex()) {
                    headerColumn[index] = headerCell
                    if (headerCell.length > maxCellWidth) maxCellWidth = headerCell.length
                }
            }
        }
        headerTable
    }

    table.indexes.map { it.toString() }.map { it.length }.forEach {
        if (it > maxCellWidth) maxCellWidth = it
    }

    table.map { it.value?.toString() ?: "" }.map { it.length }.forEach {
        if (it > maxCellWidth) maxCellWidth = it
    }

    fun padding(input: String): String {
        var output = input
        while (output.length < maxCellWidth) output += " "
        return output
    }

    for (index in headerTable.indexes) {
        print(padding(""))
        for (header in headerTable.headers) {
            print("\t" + padding(headerTable[index][header].toString()))
        }
        println()
    }

    for (index in indexes) {
        print(padding(index.toString()))
        for (header in headers) {
            print("\t" + padding(table[index][header].toString()))
        }
        println()
    }
}
