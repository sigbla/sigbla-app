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

    internal abstract fun areaCells(x: Int, y: Int, h: Int, w: Int, dims: Dimensions): List<PositionedCell>

    internal abstract fun dims(): Dimensions

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
    override fun areaCells(x: Int, y: Int, h: Int, w: Int, dims: Dimensions): List<PositionedCell> {
        val table = this.table ?: return emptyList()

        val applicableColumns = mutableListOf<Pair<Column, Long>>()
        var runningWidth = 0L
        var maxHeaderOffset = 0L
        var maxHeaderCells = 0

        for (column in table.columns) {
            if (x <= runningWidth && runningWidth <= x + w) applicableColumns.add(Pair(column, runningWidth))
            runningWidth += this[column].width

            val yOffset = column.columnHeader.header.size * defaultRowStyle.height.toLong()
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
            if (column.columnHeader.header.size > maxHeaderCells) maxHeaderCells = column.columnHeader.header.size
        }

        val colHeaderZ = Integer.MAX_VALUE.toLong()
        val rowHeaderZ = Integer.MAX_VALUE.toLong()

        val output = mutableListOf<PositionedCell>()

        // This is for the column headers
        for ((applicableColumn, applicableX) in applicableColumns) {
            for (idx in 0 until maxHeaderCells) {
                val headerText = applicableColumn.columnHeader[idx]
                val yOffset = idx.toLong() * defaultRowStyle.height.toLong()

                output.add(PositionedCell(
                    headerText,
                    null,
                    yOffset,
                    defaultRowStyle.height.toLong(),
                    this[applicableColumn].width.toLong(),
                    colHeaderZ,
                    ch = dims.maxY,
                    cw = dims.maxX,
                    ml = applicableX + 100,
                    className = "ch"
                ))
            }
        }

        val applicableRows = mutableListOf<Pair<Long, Long>>()
        var runningHeight = maxHeaderOffset

        for (row in 0..(table.indicesMap.lastKey())) {
            if (y <= runningHeight && runningHeight <= y + h) applicableRows.add(Pair(row, runningHeight))

            runningHeight += this[row].height

            if (runningHeight > y + h || runningHeight < 0L) break
        }

        // This is for the row headers
        for ((applicableRow, applicableY) in applicableRows) {
            output.add(PositionedCell(applicableRow.toString(),
                0,
                null,
                this[applicableRow].height.toLong(),
                100,
                rowHeaderZ,
                ch = dims.maxY,
                cw = dims.maxX,
                mt = applicableY,
                className = "rh"
            ))
        }

        // This is for the cells
        for ((applicableColumn, applicableX) in applicableColumns) {
            for ((applicableRow, applicableY) in applicableRows) {
                // TODO Can probably skip empty cells here..
                // TODO PositionedCell will need it's height and width..
                //if (applicableColumn[applicableRow] is UnitCell) continue

                output.add(PositionedCell(applicableColumn[applicableRow].toString(), applicableX + 100, applicableY, this[applicableRow].height.toLong(), this[applicableColumn].width.toLong(), className = "c"))
            }
        }

        return output
    }

    @Synchronized
    override fun dims(): Dimensions {
        val table = this.table ?: return Dimensions(0, 0, 0, 0)

        var runningWidth = defaultColumnStyle.width.toLong()
        var maxHeaderOffset = 0L

        for (column in table.columns) {
            runningWidth += this[column].width

            val yOffset = column.columnHeader.header.size * defaultRowStyle.height.toLong()
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
        }

        var runningHeight = maxHeaderOffset

        for (row in 0..(table.indicesMap.lastKey())) {
            runningHeight += this[row].height
        }

        return Dimensions(defaultColumnStyle.width.toLong(), maxHeaderOffset, runningWidth, runningHeight)
    }
}

class ColumnStyle internal constructor(val width: Int)

class ColumnStyleBuilder(var width: Int = 100)

fun columnStyle(init: ColumnStyleBuilder.() -> Unit): ColumnStyle {
    val builder = ColumnStyleBuilder()
    builder.init()
    return ColumnStyle(width = builder.width)
}

class RowStyle internal constructor(val height: Int)

class RowStyleBuilder(var height: Int = 20)

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

internal class PositionedCell(
    val content: String,
    val x: Long?,
    val y: Long?,
    val h: Long,
    val w: Long,
    val z: Long? = null,
    val mt: Long? = null, // Margin top
    val ml: Long? = null, // Margin left
    val cw: Long? = null, // Container width
    val ch: Long? = null, // Container height
    val className: String = ""
)

internal class Dimensions(val cornerX: Long, val cornerY: Long, val maxX: Long, val maxY: Long)