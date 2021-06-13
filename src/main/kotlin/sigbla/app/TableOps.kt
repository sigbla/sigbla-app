package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.internals.Registry
import sigbla.app.internals.refAction
import java.util.concurrent.atomic.AtomicReference
import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.collection.TreeMap as PTreeMap
import kotlin.reflect.KClass

// TODO Refactor TableOps.kt into various files, like for column ops, rows ops, events, etc..?

// TODO Implement something similar for moving/copying rows around, like move(t[1] after t[2]), etc
// TODO Implement something for moving rows around within a column, like move(t["A", 1] after t["A", 2]), etc?

fun move(columnToColumnAction: ColumnToColumnAction, withName: ColumnHeader) {
    fun columnMove(left: Column, right: Column, order: ColumnActionOrder, withName: ColumnHeader, refUpdate: TableRef.() -> TableRef): (ref: TableRef) -> TableRef = { inRef ->
        val ref = inRef.refUpdate()

        val changedColumns = ref
            .columnsMap
            .values()
            .asSequence()
            .sortedBy { it.columnOrder }
            .dropWhile { it != right }
            .filter { it != left }
            .let {
                if (order == ColumnActionOrder.AFTER) it.drop(1)
                else it
            }

        val firstChangedColumn = changedColumns.firstOrNull()

        val unchangedColumns = ref
            .columnsMap
            .values()
            .asSequence()
            .filter { it != left }
            .sortedBy { it.columnOrder }
            .takeWhile { firstChangedColumn == null || firstChangedColumn != it }

        val newColumn = sequenceOf(BaseColumn(left.table, withName, left.table.tableRef, left.columnOrder))

        val remainingColumns = changedColumns.filter { it.columnHeader != withName }.let { columns ->
            if (order == ColumnActionOrder.TO) columns.filter { it.columnHeader != right.columnHeader }
            else columns
        }

        val allColumns = unchangedColumns + newColumn + remainingColumns

        val columnOrders = ref
            .columnsMap
            .asSequence()
            .map { it.component2().columnOrder }
            .sorted() zip allColumns

        // Use sequence of columnOrder as it already exists, and just reassign accordingly to the new sequence..
        val newColumnMap = columnOrders.fold(PHashMap<ColumnHeader, Column>()) { acc, (columnOrder, column) ->
            acc.put(column.columnHeader, BaseColumn(column.table, column.columnHeader, left.table.tableRef, columnOrder))
        }

        ref.copy(
            columnsMap = newColumnMap,
            version = ref.version + 1L
        )
    }

    val left = columnToColumnAction.left
    val right = columnToColumnAction.right
    val order = columnToColumnAction.order

    if (left.table === right.table) {
        // Internal move
        val newLeft = BaseColumn(left.table, withName, left.table.tableRef)
        val (oldRef, newRef) = left.table.tableRef.refAction(
            (::columnMove)(left, right, order, withName) {
                copy(
                    columnsMap = this.columnsMap.put(withName, newLeft),
                    columnCellMap = this.columnCellMap.put(newLeft, this.columnCellMap[left] ?: PTreeMap())
                )
            }
        )

        // TODO Events
    } else {
        // Move between tables
        val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
            ref.copy(
                columnsMap = ref.columnsMap.remove(left.columnHeader),
                columnCellMap = ref.columnCellMap.remove(left),
                version = ref.version + 1L
            )
        }

        val newLeft = BaseColumn(right.table, withName, right.table.tableRef)
        val (oldRef2, newRef2) = right.table.tableRef.refAction(
            (::columnMove)(newLeft, right, order, withName) {
                copy(
                    columnsMap = this.columnsMap.put(withName, newLeft),
                    columnCellMap = this.columnCellMap.put(newLeft, oldRef1.columnCellMap[left] ?: PTreeMap())
                )
            }
        )

        // TODO Events
    }
}

fun move(columnToColumnAction: ColumnToColumnAction, vararg withName: String) = move(columnToColumnAction, ColumnHeader(*withName))

fun move(columnToColumnAction: ColumnToColumnAction) = move(columnToColumnAction, if (columnToColumnAction.order == ColumnActionOrder.TO) columnToColumnAction.right.columnHeader else columnToColumnAction.left.columnHeader)

fun move(left: Column, actionOrder: ColumnActionOrder, right: Column, withName: ColumnHeader) = move(ColumnToColumnAction(left, right, actionOrder), withName)

fun move(left: Column, actionOrder: ColumnActionOrder, right: Column, vararg withName: String) = move(ColumnToColumnAction(left, right, actionOrder), *withName)

fun move(left: Column, actionOrder: ColumnActionOrder, right: Column) = move(ColumnToColumnAction(left, right, actionOrder))

fun move(columnToTableAction: ColumnToTableAction, withName: ColumnHeader) {
    fun columnMove(left: Column, table: Table, withName: ColumnHeader, refUpdate: TableRef.() -> TableRef): (ref: TableRef) -> TableRef = { inRef ->
        val ref = inRef.refUpdate()

        val otherColumns = ref
            .columnsMap
            .values()
            .asSequence()
            .filter { it != left }
            .sortedBy { it.columnOrder }

        val newColumn = sequenceOf(BaseColumn(table, withName, table.tableRef, left.columnOrder))

        val allColumns = otherColumns + newColumn

        val columnOrders = ref
            .columnsMap
            .asSequence()
            .map { it.component2().columnOrder }
            .sorted() zip allColumns

        // Use sequence of columnOrder as it already exists, and just reassign accordingly to the new sequence..
        val newColumnMap = columnOrders.fold(PHashMap<ColumnHeader, Column>()) { acc, (columnOrder, column) ->
            acc.put(column.columnHeader, BaseColumn(column.table, column.columnHeader, table.tableRef, columnOrder))
        }

        ref.copy(
            columnsMap = newColumnMap,
            version = ref.version + 1L
        )
    }

    val left = columnToTableAction.left
    val table = columnToTableAction.table

    if (left.table === table) {
        // Internal move
        val newLeft = BaseColumn(table, withName, table.tableRef)
        val (oldRef, newRef) = table.tableRef.refAction(
            (::columnMove)(newLeft, table, withName) {
                copy(
                    columnsMap = this.columnsMap.remove(left.columnHeader).put(withName, newLeft),
                    columnCellMap = this.columnCellMap.remove(left).put(newLeft, this.columnCellMap[left] ?: PTreeMap()),
                )
            }
        )
        // TODO Events
    } else {
        // Move between tables
        val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
            ref.copy(
                columnsMap = ref.columnsMap.remove(left.columnHeader),
                columnCellMap = ref.columnCellMap.remove(left),
                version = ref.version + 1L
            )
        }

        val newLeft = BaseColumn(table, withName, table.tableRef)
        val (oldRef2, newRef2) = table.tableRef.refAction(
            (::columnMove)(newLeft, table, withName) {
                copy(
                    columnsMap = this.columnsMap.put(withName, newLeft),
                    columnCellMap = this.columnCellMap.put(newLeft, oldRef1.columnCellMap[left] ?: PTreeMap()),
                )
            }
        )
        // TODO Events
    }
}

fun move(columnToTableAction: ColumnToTableAction, vararg withName: String) = move(columnToTableAction, ColumnHeader(*withName))

fun move(columnToTableAction: ColumnToTableAction) = move(columnToTableAction, columnToTableAction.left.columnHeader)

fun move(left: Column, table: Table, withName: ColumnHeader) = move(ColumnToTableAction(left, table), withName)

fun move(left: Column, table: Table, vararg withName: String) = move(ColumnToTableAction(left, table), *withName)

fun move(left: Column, table: Table) = move(ColumnToTableAction(left, table), left.columnHeader)

fun copy(columnToColumnAction: ColumnToColumnAction, withName: ColumnHeader) {
    fun columnCopy(left: Column, right: Column, order: ColumnActionOrder, withName: ColumnHeader, refUpdate: TableRef.() -> TableRef): (ref: TableRef) -> TableRef = { inRef ->
        val ref = inRef.refUpdate()

        val changedColumns = ref
            .columnsMap
            .values()
            .asSequence()
            .sortedBy { it.columnOrder }
            .dropWhile { it != right }
            .filter { left.columnHeader != withName || it != left }
            .let {
                if (order == ColumnActionOrder.AFTER) it.drop(1)
                else it
            }

        val firstChangedColumn = changedColumns.firstOrNull()

        val unchangedColumns = ref
            .columnsMap
            .values()
            .asSequence()
            .filter { left.columnHeader != withName || it != left }
            .sortedBy { it.columnOrder }
            .takeWhile { firstChangedColumn == null || firstChangedColumn != it }

        val newColumn = sequenceOf(BaseColumn(left.table, withName, left.table.tableRef, left.columnOrder))

        val remainingColumns = changedColumns.filter { it.columnHeader != withName }.let { columns ->
            if (order == ColumnActionOrder.TO) columns.filter { it.columnHeader != right.columnHeader }
            else columns
        }

        val allColumns = unchangedColumns + newColumn + remainingColumns

        val columnOrders = ref
            .columnsMap
            .asSequence()
            .map { it.component2().columnOrder }
            .sorted() zip allColumns

        // Use sequence of columnOrder as it already exists, and just reassign accordingly to the new sequence..
        val newColumnMap = columnOrders.fold(PHashMap<ColumnHeader, Column>()) { acc, (columnOrder, column) ->
            acc.put(column.columnHeader, BaseColumn(column.table, column.columnHeader, left.table.tableRef, columnOrder))
        }

        ref.copy(
            columnsMap = newColumnMap,
            version = ref.version + 1L
        )
    }

    val left = columnToColumnAction.left
    val right = columnToColumnAction.right
    val order = columnToColumnAction.order

    if (left.table === right.table) {
        // Internal copy
        val newLeft = BaseColumn(left.table, withName, left.table.tableRef)
        val (oldRef, newRef) = left.table.tableRef.refAction(
            (::columnCopy)(left, right, order, withName) {
                copy(
                    columnsMap = this.columnsMap.put(withName, newLeft),
                    columnCellMap = this.columnCellMap.put(newLeft, this.columnCellMap[left] ?: PTreeMap())
                )
            }
        )

        // TODO Events
    } else {
        // Copy between tables
        val newLeft = BaseColumn(right.table, withName, right.table.tableRef)
        val (oldRef, newRef) = right.table.tableRef.refAction(
            (::columnCopy)(newLeft, right, order, withName) {
                copy(
                    columnsMap = this.columnsMap.put(withName, newLeft),
                    columnCellMap = this.columnCellMap.put(newLeft, left.table.tableRef.get().columnCellMap[left] ?: PTreeMap())
                )
            }
        )

        // TODO Events
    }
}

fun copy(columnToColumnAction: ColumnToColumnAction, vararg withName: String) = copy(columnToColumnAction, ColumnHeader(*withName))

fun copy(columnToColumnAction: ColumnToColumnAction) = copy(columnToColumnAction, if (columnToColumnAction.order == ColumnActionOrder.TO) columnToColumnAction.right.columnHeader else columnToColumnAction.left.columnHeader)

fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column, withName: ColumnHeader) = copy(ColumnToColumnAction(left, right, actionOrder), withName)

fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column, vararg withName: String) = copy(ColumnToColumnAction(left, right, actionOrder), ColumnHeader(*withName))

fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column) = copy(ColumnToColumnAction(left, right, actionOrder))

fun copy(columnToTableAction: ColumnToTableAction, withName: ColumnHeader) {
    fun columnCopy(left: Column, table: Table, withName: ColumnHeader, refUpdate: TableRef.() -> TableRef): (ref: TableRef) -> TableRef = { inRef ->
        val ref = inRef.refUpdate()

        val otherColumns = ref
            .columnsMap
            .values()
            .asSequence()
            .filter { left.columnHeader != withName || it != left }
            .sortedBy { it.columnOrder }

        val newColumn = sequenceOf(BaseColumn(table, withName, table.tableRef, left.columnOrder))

        val allColumns = otherColumns + newColumn

        val columnOrders = ref
            .columnsMap
            .asSequence()
            .map { it.component2().columnOrder }
            .sorted() zip allColumns

        // Use sequence of columnOrder as it already exists, and just reassign accordingly to the new sequence..
        val newColumnMap = columnOrders.fold(PHashMap<ColumnHeader, Column>()) { acc, (columnOrder, column) ->
            acc.put(column.columnHeader, BaseColumn(column.table, column.columnHeader, table.tableRef, columnOrder))
        }

        ref.copy(
            columnsMap = newColumnMap,
            version = ref.version + 1L
        )
    }

    val left = columnToTableAction.left
    val table = columnToTableAction.table

    if (left.table === table) {
        // Internal copy
        val newLeft = BaseColumn(table, withName, table.tableRef)
        val (oldRef, newRef) = table.tableRef.refAction(
            (::columnCopy)(newLeft, table, withName) {
                copy(
                    columnsMap = this.columnsMap.put(withName, newLeft),
                    columnCellMap = this.columnCellMap.put(newLeft, this.columnCellMap[left] ?: PTreeMap()),
                )
            }
        )
        // TODO Events
    } else {
        // Copy between tables
        val newLeft = BaseColumn(table, withName, table.tableRef)
        val (oldRef, newRef) = table.tableRef.refAction(
            (::columnCopy)(newLeft, table, withName) {
                copy(
                    columnsMap = this.columnsMap.put(withName, newLeft),
                    columnCellMap = this.columnCellMap.put(newLeft, left.table.tableRef.get().columnCellMap[left] ?: PTreeMap()),
                )
            }
        )
        // TODO Events
    }
}

fun copy(columnToTableAction: ColumnToTableAction, vararg withName: String) = copy(columnToTableAction, ColumnHeader(*withName))

fun copy(columnToTableAction: ColumnToTableAction) = copy(columnToTableAction, columnToTableAction.left.columnHeader)

fun copy(left: Column, table: Table, withName: ColumnHeader) = copy(ColumnToTableAction(left, table), withName)

fun copy(left: Column, table: Table, vararg withName: String) = copy(ColumnToTableAction(left, table), *withName)

fun copy(left: Column, table: Table) = copy(ColumnToTableAction(left, table), left.columnHeader)

// ---

fun move(rowToRowAction: RowToRowAction) {
    val left = rowToRowAction.left
    val right = rowToRowAction.right
    val order = rowToRowAction.order

    if (order == RowActionOrder.TO) {
        if (left.table === right.table) {
            // Internal move
            val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCellMap = ref.columnCellMap.fold(PHashMap()) { acc, ccm ->
                        acc.put(ccm.component1(), ccm.component2().remove(left.index).let {
                            val cell = ccm.component2().get(left.index)
                            if (cell != null) it.put(right.index, cell) else it
                        })
                    }
                )
            }

            // TODO Events
        } else {
            // Move between tables
            val (oldRef1, newRef1) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCellMap = ref.columnCellMap.fold(PHashMap()) { acc, ccm ->
                        acc.put(ccm.component1(), ccm.component2().remove(left.index))
                    }
                )
            }

            val (oldRef2, newRef2) = right.table.tableRef.refAction { ref ->
                val columnsMap = oldRef1
                    .columnsMap
                    .sortedBy { it.component2().columnOrder }
                    .map { it.component1() }
                    .fold(ref.columnsMap) { acc, c ->
                        if (acc.containsKey(c)) acc else acc.put(c, BaseColumn(right.table, c, right.table.tableRef))
                    }

                val columnCellMap = oldRef1.columnCellMap.fold(ref.columnCellMap) { acc, ccm ->
                    val cell = ccm.component2().get(left.index)
                    if (cell != null) {
                        val column = columnsMap.get(ccm.component1().columnHeader)
                            ?: throw InvalidColumnException(ccm.component1())
                        acc.put(column, (acc.get(column) ?: PTreeMap()).put(right.index, cell))
                    } else acc
                }

                ref.copy(
                    columnsMap = columnsMap,
                    columnCellMap = columnCellMap
                )
            }

            // TODO Events
        }
    } else {
        TODO()
    }
}

fun move(left: Row, actionOrder: RowActionOrder, right: Row) = move(RowToRowAction(left, right, actionOrder))

fun copy(rowToRowAction: RowToRowAction) {
    val left = rowToRowAction.left
    val right = rowToRowAction.right
    val order = rowToRowAction.order

    if (order == RowActionOrder.TO) {
        if (left.table === right.table) {
            // Internal copy
            val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCellMap = ref.columnCellMap.fold(PHashMap()) { acc, ccm ->
                        acc.put(ccm.component1(), ccm.component2().let {
                            val cell = ccm.component2().get(left.index)
                            if (cell != null) it.put(right.index, cell) else it
                        })
                    }
                )
            }

            // TODO Events
        } else {
            // Copy between tables
            val (oldRef, newRef) = right.table.tableRef.refAction { ref ->
                val leftRef = left.table.tableRef.get()

                val columnsMap = leftRef
                    .columnsMap
                    .sortedBy { it.component2().columnOrder }
                    .map { it.component1() }
                    .fold(ref.columnsMap) { acc, c ->
                        if (acc.containsKey(c)) acc else acc.put(c, BaseColumn(right.table, c, right.table.tableRef))
                    }

                val columnCellMap = leftRef.columnCellMap.fold(ref.columnCellMap) { acc, ccm ->
                    val cell = ccm.component2().get(left.index)
                    if (cell != null) {
                        val column = columnsMap.get(ccm.component1().columnHeader)
                            ?: throw InvalidColumnException(ccm.component1())
                        acc.put(column, (acc.get(column) ?: PTreeMap()).put(right.index, cell))
                    } else acc
                }

                ref.copy(
                    columnsMap = columnsMap,
                    columnCellMap = columnCellMap
                )
            }

            // TODO Events
        }
    } else {
        if (left.table === right.table) {
            // Internal copy
            val (oldRef, newRef) = left.table.tableRef.refAction { ref ->
                ref.copy(
                    columnCellMap = ref.columnCellMap.fold(PHashMap()) { acc, ccm ->
                        val newIndex = if (order == RowActionOrder.AFTER) right.index + 1 else right.index - 1

                        val headCells = ccm.component2().to(newIndex, order != RowActionOrder.AFTER)
                        val tailCells = ccm.component2().from(newIndex, order == RowActionOrder.AFTER)

                        val cells = if (order == RowActionOrder.AFTER) {
                            tailCells.fold(headCells) { acc2, cell ->
                                // Shift down
                                acc2.put(cell.component1() + 1, cell.component2())
                            }
                        } else {
                            headCells.fold(tailCells) { acc2, cell ->
                                // Shift up
                                acc2.put(cell.component1() - 1, cell.component2())
                            }
                        }

                        acc.put(ccm.component1(), cells.let {
                            val cellValue = ccm.component2().get(left.index)
                            if (cellValue != null) it.put(newIndex, cellValue) else it
                        })
                    }
                )
            }

            // TODO Events
        } else {
            // Copy between tables
            val (oldRef, newRef) = right.table.tableRef.refAction { ref ->
                val leftRef = left.table.tableRef.get()

                val columnsMap = leftRef
                    .columnsMap
                    .sortedBy { it.component2().columnOrder }
                    .map { it.component1() }
                    .fold(ref.columnsMap) { acc, c ->
                        if (acc.containsKey(c)) acc else acc.put(c, BaseColumn(right.table, c, right.table.tableRef))
                    }

                val columnCellMap = leftRef.columnCellMap.fold(ref.columnCellMap) { acc, ccm ->
                    val column = columnsMap.get(ccm.component1().columnHeader)
                        ?: throw InvalidColumnException(ccm.component1())

                    val cm = acc.get(column) ?: PTreeMap()

                    val newIndex = if (order == RowActionOrder.AFTER) right.index + 1 else right.index - 1

                    val headCells = cm.to(newIndex, order != RowActionOrder.AFTER)
                    val tailCells = cm.from(newIndex, order == RowActionOrder.AFTER)

                    val cells = if (order == RowActionOrder.AFTER) {
                        tailCells.fold(headCells) { acc2, cell ->
                            // Shift down
                            acc2.put(cell.component1() + 1, cell.component2())
                        }
                    } else {
                        headCells.fold(tailCells) { acc2, cell ->
                            // Shift up
                            acc2.put(cell.component1() - 1, cell.component2())
                        }
                    }

                    acc.put(column, cells.let {
                        val cellValue = ccm.component2().get(left.index)
                        if (cellValue != null) it.put(newIndex, cellValue) else it
                    })
                }

                ref.copy(
                    columnsMap = columnsMap,
                    columnCellMap = columnCellMap
                )
            }

            // TODO Events
        }
    }
}

fun copy(left: Row, actionOrder: RowActionOrder, right: Row) = copy(RowToRowAction(left, right, actionOrder))

// ---

fun rename(column: Column, withName: ColumnHeader): Unit = move(column to column, withName)

fun rename(column: Column, vararg withName: String): Unit = move(column to column, *withName)

fun remove(table: Table) = Registry.deleteTable(table)

fun remove(column: Column) {
    // TODO Column remove event
    column.table.tableRef.updateAndGet {
        it.copy(
            columnsMap = it.columnsMap.remove(column.columnHeader),
            columnCellMap = it.columnCellMap.remove(column),
            version = it.version + 1L
        )
    }
}

// Note: We don't have remove(row) because that would imply shifting rows below up, same for cells
// TODO When move(t[1] after|before t[2]) is available, should have remove(row) too

fun clear(table: Table): Unit = TODO()

fun clear(column: Column): Unit = TODO()

fun clear(row: Row): Unit = TODO()

fun clear(cell: Cell<*>) = cell `=` null

fun clone(table: Table): Table = table.makeClone()

fun clone(table: Table, withName: String): Table = table.makeClone(withName, true)

// TODO Any iteration below needs to operate on a clone?

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

inline fun <reified T> valueOf(cells: Iterable<Cell<*>>): Sequence<T> = valueOf(cells, T::class) as Sequence<T>

fun valueOf(cells: Iterable<Cell<*>>, typeFilter: KClass<*>): Sequence<Any> = cells
    .asSequence()
    .mapNotNull { valueOf(it, typeFilter) }

fun headerOf(cell: Cell<*>) = cell.column.columnHeader

fun headerOf(column: Column) = column.columnHeader

fun headerOf(row: Row) = row.headers.asSequence()

fun headerOf(cells: Iterable<Cell<*>>) = cells
    .asSequence()
    .map { it.column }
    .toSortedSet()
    .asSequence()
    .map { it.columnHeader }

fun columnOf(cell: Cell<*>) = cell.column

fun columnOf(row: Row) = row.headers.asSequence().map { row.table[it] }

fun columnOf(cells: Iterable<Cell<*>>) = cells
    .asSequence()
    .map { it.column }
    .toSortedSet()
    .asSequence()

fun indexOf(cell: Cell<*>) = cell.index

fun indexOf(cells: Iterable<Cell<*>>) = cells
    .asSequence()
    .map { it.index }
    .toSortedSet()
    .asSequence()

// TODO We want specifics of header/column/indexOf for column/row/range, for efficiency

// ---

inline fun <reified O, reified N> on(table: Table, noinline init: TableEventReceiver<Table, O, N>.() -> Unit): TableListenerReference {
    return on(table, O::class, N::class, init as TableEventReceiver<Table, Any, Any>.() -> Unit)
}

fun on(table: Table, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Table, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Table, Any, Any>(
            table
        ) { this }
        old == Any::class -> TableEventReceiver(table) {
            this.filter {
                new.isInstance(
                    it.newValue.value
                )
            }
        }
        new == Any::class -> TableEventReceiver(table) {
            this.filter {
                old.isInstance(
                    it.oldValue.value
                )
            }
        }
        else -> TableEventReceiver(table) {
            this.filter {
                old.isInstance(it.oldValue.value) && new.isInstance(
                    it.newValue.value
                )
            }
        }
    }
    return table.eventProcessor.subscribe(table, eventReceiver, init)
}

// ---

inline fun <reified O, reified N> on(column: Column, noinline init: TableEventReceiver<Column, O, N>.() -> Unit): TableListenerReference {
    return on(column, O::class, N::class, init as TableEventReceiver<Column, Any, Any>.() -> Unit)
}

fun on(column: Column, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Column, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Column, Any, Any>(
            column
        ) { this }
        old == Any::class -> TableEventReceiver(column) {
            this.filter {
                new.isInstance(
                    it.newValue.value
                )
            }
        }
        new == Any::class -> TableEventReceiver(column) {
            this.filter {
                old.isInstance(
                    it.oldValue.value
                )
            }
        }
        else -> TableEventReceiver(column) {
            this.filter {
                old.isInstance(it.oldValue.value) && new.isInstance(
                    it.newValue.value
                )
            }
        }
    }
    return column.table.eventProcessor.subscribe(column, eventReceiver, init)
}

// ---

inline fun <reified O, reified N> on(row: Row, noinline init: TableEventReceiver<Row, O, N>.() -> Unit): TableListenerReference {
    return on(row, O::class, N::class, init as TableEventReceiver<Row, Any, Any>.() -> Unit)
}

fun on(row: Row, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Row, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Row, Any, Any>(
            row
        ) { this }
        old == Any::class -> TableEventReceiver(row) {
            this.filter {
                new.isInstance(
                    it.newValue.value
                )
            }
        }
        new == Any::class -> TableEventReceiver(row) {
            this.filter {
                old.isInstance(
                    it.oldValue.value
                )
            }
        }
        else -> TableEventReceiver(row) {
            this.filter {
                old.isInstance(it.oldValue.value) && new.isInstance(
                    it.newValue.value
                )
            }
        }
    }
    return row.table.eventProcessor.subscribe(row, eventReceiver, init)
}

// ---

inline fun <reified O, reified N> on(cellRange: CellRange, noinline init: TableEventReceiver<CellRange, O, N>.() -> Unit): TableListenerReference {
    return on(cellRange, O::class, N::class, init as TableEventReceiver<CellRange, Any, Any>.() -> Unit)
}

fun on(cellRange: CellRange, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<CellRange, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<CellRange, Any, Any>(
            cellRange
        ) { this }
        old == Any::class -> TableEventReceiver(cellRange) {
            this.filter {
                new.isInstance(it.newValue.value)
            }
        }
        new == Any::class -> TableEventReceiver(cellRange) {
            this.filter {
                old.isInstance(it.oldValue.value)
            }
        }
        else -> TableEventReceiver(cellRange) {
            this.filter {
                old.isInstance(it.oldValue.value) && new.isInstance(it.newValue.value)
            }
        }
    }
    return cellRange.start.column.table.eventProcessor.subscribe(cellRange, eventReceiver, init)
}

// ---

inline fun <reified O, reified N> on(cell: Cell<*>, noinline init: TableEventReceiver<Cell<*>, O, N>.() -> Unit): TableListenerReference {
    return on(cell, O::class, N::class, init as TableEventReceiver<Cell<*>, Any, Any>.() -> Unit)
}

fun on(cell: Cell<*>, old: KClass<*> = Any::class, new: KClass<*> = Any::class, init: TableEventReceiver<Cell<*>, Any, Any>.() -> Unit): TableListenerReference {
    val eventReceiver = when {
        old == Any::class && new == Any::class -> TableEventReceiver<Cell<*>, Any, Any>(
            cell
        ) { this }
        old == Any::class -> TableEventReceiver(cell) {
            this.filter {
                new.isInstance(
                    it.newValue.value
                )
            }
        }
        new == Any::class -> TableEventReceiver(cell) {
            this.filter {
                old.isInstance(
                    it.oldValue.value
                )
            }
        }
        else -> TableEventReceiver(cell) {
            this.filter {
                old.isInstance(
                    it.oldValue.value
                ) && new.isInstance(it.newValue.value)
            }
        }
    }
    return cell.column.table.eventProcessor.subscribe(cell, eventReceiver, init)
}

// ---

fun off(reference: TableListenerReference) = reference.unsubscribe()

fun off(tableEventReceiver: TableEventReceiver<*, *, *>) = off(tableEventReceiver.reference)
