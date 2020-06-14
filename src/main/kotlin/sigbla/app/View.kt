package sigbla.app

import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.Registry
import sigbla.app.internals.SigblaBackend
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

// TODO..

// A table view is associated with one table, and holds meta data related on how to view a table.
// This includes among other things column widths, row heights, individual cell dimensions, styling, etc..

abstract class TableView(val name: String) {
    abstract var table: Table?

    abstract var defaultColumnStyle: ColumnStyle

    abstract var defaultRowStyle: RowStyle

    abstract var defaultCellStyle: CellStyle

    abstract operator fun get(columnHeader: ColumnHeader): ColumnStyle

    operator fun get(column: Column) = get(column.columnHeader)

    abstract operator fun get(row: Long): RowStyle

    operator fun get(row: Row) = get(row.index)

    abstract operator fun get(columnHeader: ColumnHeader, row: Long): CellStyle

    operator fun get(column: Column, row: Long) = get(column.columnHeader, row)

    operator fun get(cell: Cell<*>) = get(cell.column, cell.index)

    abstract operator fun set(columnHeader: ColumnHeader, columnStyle: ColumnStyle?)

    operator fun set(column: Column, columnStyle: ColumnStyle?) = set(column.columnHeader, columnStyle)

    abstract operator fun set(row: Long, rowStyle: RowStyle?)

    operator fun set(row: Row, rowStyle: RowStyle?) = set(row.index, rowStyle)

    abstract operator fun set(columnHeader: ColumnHeader, row: Long, cellStyle: CellStyle?)

    operator fun set(column: Column, row: Long, cellStyle: CellStyle?) = set(column.columnHeader, row, cellStyle)

    operator fun set(cell: Cell<*>, cellStyle: CellStyle?) = set(cell.column, cell.index, cellStyle)

    abstract fun remove(columnHeader: ColumnHeader)

    fun remove(column: Column) = remove(column.columnHeader)

    abstract fun remove(row: Long)

    fun remove(row: Row) = remove(row.index)

    abstract fun remove(columnHeader: ColumnHeader, row: Long)

    fun remove(column: Column, row: Long) = remove(column.columnHeader, row)

    fun remove(cell: Cell<*>) = remove(cell.column, cell.index)

    fun show() = SigblaBackend.openView(this)

    internal abstract fun areaCells(x: Int, y: Int, h: Int, w: Int): List<PositionedCell>

    companion object {
        fun newTableView(name: String): TableView = BaseTableView(name)

        fun newTableView(table: Table): TableView = BaseTableView(table)

        fun newTableView(name: String, table: Table): TableView = BaseTableView(name, table)

        fun fromRegistry(name: String): TableView = Registry.getView(name) ?: throw InvalidTableException("No table view by name $name")

        fun fromStorage(storage: Storage, name: String): TableView {
            TODO()
        }

        fun fromStorageAs(storage: Storage, name: String, newName: String): TableView {
            TODO()
        }

        fun fromStorageRange(storage: Storage, name: String, fromIndex: Long, toIndex: Long): TableView {
            TODO()
        }

        fun fromStorageRangeAs(storage: Storage, name: String, fromIndex: Long, toIndex: Long, newName: String): TableView {
            TODO()
        }

        fun registryTableViewNames(): SortedSet<String> = Registry.viewNames()

        fun deleteView(name: String) = Registry.deleteView(name)
    }
}

class BaseTableView internal constructor(
    name: String,
    table: Table?,
    private val columnStyles: ConcurrentMap<ColumnHeader, ColumnStyle> = ConcurrentHashMap(),
    private val rowStyles: ConcurrentMap<Long, RowStyle> = ConcurrentHashMap(),
    private val cellStyles: ConcurrentMap<Pair<ColumnHeader, Long>, CellStyle> = ConcurrentHashMap()
) : TableView(name) {
    constructor(table: Table) : this(table.name, table)
    constructor(name: String) : this(name, Registry.getTable(name))

    init {
        Registry.setView(name, this)
    }

    override var table = table
        @Synchronized
        set(table) {
            field = table
        }

    override var defaultColumnStyle: ColumnStyle = columnStyle {}
        @Synchronized
        set(columnStyle) {
            field = columnStyle
        }

    override var defaultRowStyle: RowStyle = rowStyle {}
        @Synchronized
        set(rowStyle) {
            field = rowStyle
        }

    override var defaultCellStyle: CellStyle = cellStyle {}
        @Synchronized
        set(cellStyle) {
            field = cellStyle
        }

    override fun get(columnHeader: ColumnHeader): ColumnStyle = columnStyles[columnHeader] ?: defaultColumnStyle

    override fun get(row: Long): RowStyle = rowStyles[row] ?: defaultRowStyle

    override fun get(columnHeader: ColumnHeader, row: Long): CellStyle = cellStyles[Pair(columnHeader, row)] ?: defaultCellStyle

    @Synchronized
    override fun set(columnHeader: ColumnHeader, columnStyle: ColumnStyle?) {
        if (columnStyle == null) {
            remove(columnHeader)
        } else {
            columnStyles[columnHeader] = columnStyle
            // TODO event
        }
    }

    @Synchronized
    override fun set(row: Long, rowStyle: RowStyle?) {
        if (rowStyle == null) {
            remove(row)
        } else {
            rowStyles[row] = rowStyle
            // TODO event
        }
    }

    @Synchronized
    override fun set(columnHeader: ColumnHeader, row: Long, cellStyle: CellStyle?) {
        if (cellStyle == null) {
            remove(columnHeader, row)
        } else {
            cellStyles[Pair(columnHeader, row)] = cellStyle
            // TODO event
        }
    }

    @Synchronized
    override fun remove(columnHeader: ColumnHeader) {
        columnStyles.remove(columnHeader)
        // TODO event
    }

    @Synchronized
    override fun remove(row: Long) {
        rowStyles.remove(row)
        // TODO event
    }

    @Synchronized
    override fun remove(columnHeader: ColumnHeader, row: Long) {
        cellStyles.remove(Pair(columnHeader, row))
        // TODO event
    }

    @Synchronized
    override fun areaCells(x: Int, y: Int, h: Int, w: Int): List<PositionedCell> {
        val table = this.table ?: return emptyList()

        val columnHeaderHeight = 25L

        val applicableColumns = mutableListOf<Pair<Column, Long>>()
        var runningWidth = 0L
        var maxHeaderOffset = 0L

        for (column in table.columns) {
            if (x <= runningWidth && runningWidth <= x + w) applicableColumns.add(Pair(column, runningWidth))
            runningWidth += this[column].width

            val yOffset = column.columnHeader.header.size * columnHeaderHeight
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
        }

        val output = mutableListOf<PositionedCell>()

        for ((applicableColumn, applicableX) in applicableColumns) {
            for (indexedColumnHeader in applicableColumn.columnHeader.header.withIndex()) {
                val headerText = indexedColumnHeader.value
                val yOffset = indexedColumnHeader.index.toLong() * columnHeaderHeight

                output.add(PositionedCell(headerText, applicableX + 100, yOffset, columnHeaderHeight, this[applicableColumn].width.toLong(), Int.MAX_VALUE.toLong(), "ch"))
            }
        }

        val applicableRows = mutableListOf<Pair<Long, Long>>()
        var runningHeight = maxHeaderOffset

        for (row in 0..(table.indicesMap.lastKey())) {
            if (y <= runningHeight && runningHeight <= y + h) applicableRows.add(Pair(row, runningHeight))

            runningHeight += this[row].height

            if (runningHeight > y + h || runningHeight < 0L) break
        }

        for ((applicableRow, applicableY) in applicableRows) {
            output.add(PositionedCell(applicableRow.toString(), 0, applicableY, columnHeaderHeight, 100, className = "rh"))
        }

        for ((applicableColumn, applicableX) in applicableColumns) {
            for ((applicableRow, applicableY) in applicableRows) {
                // TODO Can probably skip empty cells here..
                // TODO PositionedCell will need it's height and width..
                output.add(PositionedCell(applicableColumn[applicableRow].toString(), applicableX + 100, applicableY, this[applicableRow].height.toLong(), this[applicableColumn].width.toLong(), className = "c"))
            }
        }

        return output
    }
}

class ColumnStyle internal constructor(val width: Int)

class ColumnStyleBuilder(var width: Int = 65)

fun columnStyle(init: ColumnStyleBuilder.() -> Unit): ColumnStyle {
    val builder = ColumnStyleBuilder()
    builder.init()
    return ColumnStyle(width = builder.width)
}

class RowStyle internal constructor(val height: Int)

class RowStyleBuilder(var height: Int = 16)

fun rowStyle(init: RowStyleBuilder.() -> Unit): RowStyle {
    val builder = RowStyleBuilder()
    builder.init()
    return RowStyle(height = builder.height)
}

class CellStyle(val height: Int?, val width: Int?)

class CellStyleBuilder(var height: Int? = null, var width: Int? = null)

fun cellStyle(init: CellStyleBuilder.() -> Unit): CellStyle {
    val builder = CellStyleBuilder()
    builder.init()
    return CellStyle(height = builder.height, width = builder.width)
}

internal class PositionedCell(val content: String, val x: Long, val y: Long, val h: Long, val w: Long, val z: Long = 0L, val className: String = "")
