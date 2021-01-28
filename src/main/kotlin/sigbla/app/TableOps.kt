package sigbla.app

import sigbla.app.internals.refAction
import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.collection.TreeMap as PTreeMap
import kotlin.reflect.KClass

fun move(columnToColumnAction: ColumnToColumnAction) {
    fun columnMove(left: Column, right: Column): (ref: TableRef) -> TableRef = { ref ->
        val changedColumns = ref
            .columnsMap
            .values()
            .filter { it != left }
            .sortedBy { it.columnOrder }
            .dropWhile { it != right }
            .let {
                if (columnToColumnAction.order == ColumnActionOrder.AFTER) it.drop(1) else it
            }

        val unchangedColumns = ref
            .columnsMap
            .values()
            .filter { it != left }
            .sortedBy { it.columnOrder }
            .takeWhile { changedColumns.isEmpty() || it != changedColumns.first() }

        val columnOrders = ref
            .columnsMap
            .map { it.component2().columnOrder }
            .sorted() zip listOf(unchangedColumns, listOf(left), changedColumns).flatten()

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

    if (left.table === right.table) {
        // Internal move
        val (oldRef, newRef) = left.table.tableRef.refAction((::columnMove)(left, right))

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

        val (oldRef2, newRef2) = right.table.tableRef.refAction { ref ->
            val newLeft = BaseColumn(right.table, left.columnHeader, right.table.tableRef)
            columnMove(newLeft, right)(ref.copy(
                columnsMap = ref.columnsMap.put(left.columnHeader, newLeft),
                columnCellMap = ref.columnCellMap.put(newLeft, oldRef1.columnCellMap[left] ?: PTreeMap()),
                version = ref.version + 1L
            ))
        }

        // TODO Events
    }
}

fun move(columnToColumnAction: ColumnToColumnAction, withName: ColumnHeader): Unit = TODO()

fun move(columnToColumnAction: ColumnToColumnAction, vararg withName: String): Unit = TODO()

fun move(left: Column, actionOrder: ColumnActionOrder, right: Column): Unit = TODO()

fun move(left: Column, actionOrder: ColumnActionOrder, right: Column, withName: ColumnHeader): Unit = TODO()

fun move(left: Column, actionOrder: ColumnActionOrder, right: Column, vararg withName: String): Unit = TODO()

fun move(columnToTableAction: ColumnToTableAction): Unit = TODO()

fun move(columnToTableAction: ColumnToTableAction, withName: ColumnHeader): Unit = TODO()

fun move(columnToTableAction: ColumnToTableAction, vararg withName: String): Unit = TODO()

fun move(left: Column, right: Table): Unit = TODO()

fun move(left: Column, right: Table, withName: ColumnHeader): Unit = TODO()

fun move(left: Column, right: Table, vararg withName: String): Unit = TODO()

//fun copy(columnAction: ColumnAction): Unit = TODO()

fun copy(columnToColumnAction: ColumnToColumnAction, withName: ColumnHeader): Unit = TODO()

fun copy(columnToColumnAction: ColumnToColumnAction, vararg withName: String): Unit = TODO()

//fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column): Unit = TODO()

fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column, withName: ColumnHeader): Unit = TODO()

fun copy(left: Column, actionOrder: ColumnActionOrder, right: Column, vararg withName: String): Unit = TODO()

fun copy(columnToTableAction: ColumnToTableAction): Unit = TODO()

fun copy(columnToTableAction: ColumnToTableAction, withName: ColumnHeader): Unit = TODO()

fun copy(columnToTableAction: ColumnToTableAction, vararg withName: String): Unit = TODO()

fun copy(left: Column, right: Table): Unit = TODO()

fun copy(left: Column, right: Table, withName: ColumnHeader): Unit = TODO()

fun copy(left: Column, right: Table, vararg withName: String): Unit = TODO()

//fun rename(column: Column, withName: ColumnHeader): Unit = column.rename(withName)

//fun rename(column: Column, vararg withName: String): Unit = TODO()

// TODO Delete, clear

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
