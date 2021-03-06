package sigbla.app

import sigbla.app.internals.refAction
import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.collection.TreeMap as PTreeMap
import kotlin.reflect.KClass

// TODO Refactor TableOps.kt into various files, like for column ops, rows ops, events, etc..?

// TODO Implement move/copy(t["A"] to t["B"]) and move/copy(t["A"] to t["B"], "C")
// TODO Implement something similar for moving/copying rows around, like move(t[1] after t[2]), etc
// TODO Implement something for moving rows around within a column, like move(t["A", 1] after t["A", 2]), etc

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

//fun rename(column: Column, withName: ColumnHeader): Unit = column.rename(withName)

//fun rename(column: Column, vararg withName: String): Unit = TODO()

// TODO Delete, clear

// TODO: Need a table/row clear like we have on columns..

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
