/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.internals.RefHolder
import sigbla.app.internals.Registry
import kotlin.reflect.KClass

inline fun <reified T> valueOf(cell: Cell<*>): T? = valueOf(cell, T::class) as T?

fun valueOf(cell: Cell<*>, typeFilter: KClass<*>): Any? = if (typeFilter.isInstance(cell.value)) cell.value else null

inline fun <reified T> valueOf(noinline source: Cell<*>.() -> Any?): T? = valueOf(source, T::class) as T?

fun valueOf(source: Cell<*>.() -> Any?, typeFilter: KClass<*>): Any? {
    val table = BaseTable(null, null, false, RefHolder(TableRef())) as Table
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

fun headersOf(cells: Iterable<Cell<*>>) = cells
    .asSequence()
    .map { it.column }
    .toSortedSet()
    .asSequence()
    .map { it.header }

fun columnOf(cell: Cell<*>) = cell.column

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
