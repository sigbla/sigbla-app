package sigbla.app

import sigbla.app.internals.RefHolder
import sigbla.app.internals.Registry
import java.io.Writer
import kotlin.reflect.KClass

// TODO Any iteration below needs to operate on a clone

inline fun <reified T> valueOf(cell: Cell<*>): T? = valueOf(cell, T::class) as T?

fun valueOf(cell: Cell<*>, typeFilter: KClass<*>): Any? = if (typeFilter.isInstance(cell.value)) cell.value else null

inline fun <reified T> valueOf(noinline source: Cell<*>.() -> Unit): T? = valueOf(source, T::class) as T?

fun valueOf(source: Cell<*>.() -> Unit, typeFilter: KClass<*>): Any? {
    val table = BaseTable("", null, false, RefHolder(TableRef())) as Table
    table["valueOf", 0L].source()
    val value = valueOf(table["valueOf", 0L], typeFilter)
    Registry.deleteTable(table) // Clean up
    return value
}

inline fun <reified T> valuesOf(cells: Iterable<Cell<*>>): Sequence<T> = valuesOf(cells, T::class) as Sequence<T>

fun valuesOf(cells: Iterable<Cell<*>>, typeFilter: KClass<*>): Sequence<Any> = cells
    .asSequence()
    .mapNotNull { valueOf(it, typeFilter) }

fun headerOf(cell: Cell<*>) = cell.column.header

fun headerOf(column: Column) = column.header

fun headersOf(row: Row) = row.headers.asSequence()

fun headersOf(cells: Iterable<Cell<*>>) = cells
    .asSequence()
    .map { it.column }
    .toSortedSet()
    .asSequence()
    .map { it.header }

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

// TODO Look at adding print methods for column/row ranges too

fun print(table: Table) {
    print(table, System.out.writer())
}

fun print(table: Table, writer: Writer) {
    val table = clone(table)

    val headers = table.headers
    val indexes = table.indexes

    var maxCellWidth = 0
    val headerTable = Table[null].let { headerTable ->
        for ((index, header) in headers.withIndex()) {
            headerTable[index.toString()].let { headerColumn ->
                for ((index, headerCell) in header.labels.withIndex()) {
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

    fun write(input: String, width: Int) {
        writer.append(input)
        for (i in input.length .. width) {
            writer.append(" ")
        }
    }

    for (index in headerTable.indexes) {
        write("", maxCellWidth)
        for (header in headerTable.headers) {
            write("|${headerTable[index][header]}", maxCellWidth + 1)
        }
        writer.append(System.lineSeparator())
    }

    for (index in indexes) {
        write(index.toString(), maxCellWidth)
        for (header in headers) {
            write("|${table[index][header]}", maxCellWidth + 1)
        }
        writer.append(System.lineSeparator())
    }

    writer.flush()
}
