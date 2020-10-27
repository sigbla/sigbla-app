package sigbla.app

import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.collection.Map as PMap
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.Registry
import sigbla.app.internals.SigblaBackend
import sigbla.app.internals.refAction
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicReference

// A table view is associated with one table, and holds meta data related on how to view a table.
// This includes among other things column widths, row heights, individual cell dimensions, styling, etc..

// TODO Introduce a concept of a view transformer. As with styles, this can be on column, row or cell.
//      Some priority ordering might be needed between these, but the transformer would take a value
//      and convert this into another value. This only affects the view, and is done on demand (i.e.,
//      when the cell is being rendered), and is used to perform things like formatting etc..
//
//      Look at having this in the form of view["column"] = { .. } where this is given a cell and
//      expected to return a cell.

// TODO Events on a table view should be much like on a table, with view.onAny or view.on<ColumnStyle>.
//      Note that we only have one generic type, as we can't have one type of style convert to another.
//
//      A listener can, as with table, call back into the view to change the style if wanted. And we
//      use ordering to fire off listeners in the right order. This allows for the final UI listener
//      to fire after any earlier modifications. A loop detector is also included.

abstract class TableView(val name: String) {
    abstract var table: Table?

    internal abstract val viewRef: AtomicReference<ViewRef>

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

    internal abstract fun areaContent(x: Int, y: Int, h: Int, w: Int, dims: Dimensions): List<PositionedContent>

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

// TODO Introduce a ViewRef here like we have TableRef
internal data class ViewRef(
    val columnStyles: PMap<ColumnHeader, ColumnStyle> = PHashMap(),
    val rowStyles: PMap<Long, RowStyle> = PHashMap(),
    val cellStyles: PMap<Pair<ColumnHeader, Long>, CellStyle> = PHashMap()
)

class BaseTableView internal constructor(
    name: String,
    table: Table?,
    override val viewRef: AtomicReference<ViewRef> = AtomicReference(ViewRef())
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

            // TODO event
        }

    override var defaultColumnStyle: ColumnStyle = columnStyle {}
        @Synchronized
        set(columnStyle) {
            field = columnStyle

            // TODO event
        }

    override var defaultRowStyle: RowStyle = rowStyle {}
        @Synchronized
        set(rowStyle) {
            field = rowStyle

            // TODO event
        }

    override var defaultCellStyle: CellStyle = cellStyle {}
        @Synchronized
        set(cellStyle) {
            field = cellStyle

            // TODO event
        }

    override fun get(columnHeader: ColumnHeader): ColumnStyle = viewRef.get().columnStyles[columnHeader] ?: defaultColumnStyle

    override fun get(row: Long): RowStyle = viewRef.get().rowStyles[row] ?: defaultRowStyle

    override fun get(columnHeader: ColumnHeader, row: Long): CellStyle = viewRef.get().cellStyles[Pair(columnHeader, row)] ?: defaultCellStyle

    @Synchronized
    override fun set(columnHeader: ColumnHeader, columnStyle: ColumnStyle?) {
        if (columnStyle == null) {
            remove(columnHeader)
        } else {
            val (oldRef, newRef) = viewRef.refAction {
                it.copy(
                    columnStyles = it.columnStyles.put(columnHeader, columnStyle)
                )
            }

            // TODO event
        }
    }

    @Synchronized
    override fun set(row: Long, rowStyle: RowStyle?) {
        if (rowStyle == null) {
            remove(row)
        } else {
            val (oldRef, newRef) = viewRef.refAction {
                it.copy(
                    rowStyles = it.rowStyles.put(row, rowStyle)
                )
            }

            // TODO event
        }
    }

    @Synchronized
    override fun set(columnHeader: ColumnHeader, row: Long, cellStyle: CellStyle?) {
        if (cellStyle == null) {
            remove(columnHeader, row)
        } else {
            val (oldRef, newRef) = viewRef.refAction {
                it.copy(
                    cellStyles = it.cellStyles.put(Pair(columnHeader, row), cellStyle)
                )
            }

            // TODO event
        }
    }

    @Synchronized
    override fun remove(columnHeader: ColumnHeader) {
        val (oldRef, newRef) = viewRef.refAction {
            it.copy(
                columnStyles = it.columnStyles.remove(columnHeader)
            )
        }

        // TODO event
    }

    @Synchronized
    override fun remove(row: Long) {
        val (oldRef, newRef) = viewRef.refAction {
            it.copy(
                rowStyles = it.rowStyles.remove(row)
            )
        }

        // TODO event
    }

    @Synchronized
    override fun remove(columnHeader: ColumnHeader, row: Long) {
        val (oldRef, newRef) = viewRef.refAction {
            it.copy(
                cellStyles = it.cellStyles.remove(Pair(columnHeader, row))
            )
        }

        // TODO event
    }

    @Synchronized
    override fun areaContent(x: Int, y: Int, h: Int, w: Int, dims: Dimensions): List<PositionedContent> {
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

        val output = mutableListOf<PositionedContent>()

        // This is for the column headers
        for ((applicableColumn, applicableX) in applicableColumns) {
            for (idx in 0 until maxHeaderCells) {
                val headerText = applicableColumn.columnHeader[idx]
                val yOffset = idx.toLong() * defaultRowStyle.height.toLong()

                output.add(PositionedContent(
                    applicableColumn.columnHeader,
                    (-1 - idx).toLong(),
                    headerText,
                    defaultRowStyle.height.toLong(),
                    this[applicableColumn].width.toLong(),
                    colHeaderZ,
                    ml = applicableX + 100,
                    cw = dims.maxX,
                    ch = dims.maxY,
                    className = "ch",
                    x = null,
                    y = yOffset
                ))
            }
        }

        val applicableRows = mutableListOf<Pair<Long, Long>>()
        var runningHeight = maxHeaderOffset

        val lastKey = table.tableRef.get().indicesMap.last()?.component1() ?: -1
        for (row in 0..lastKey) {
            if (y <= runningHeight && runningHeight <= y + h) applicableRows.add(Pair(row, runningHeight))

            runningHeight += this[row].height

            if (runningHeight > y + h || runningHeight < 0L) break
        }

        // This is for the row headers
        for ((applicableRow, applicableY) in applicableRows) {
            output.add(PositionedContent(
                emptyColumnHeader,
                applicableRow,
                applicableRow.toString(),
                this[applicableRow].height,
                100,
                rowHeaderZ,
                mt = applicableY,
                cw = dims.maxX,
                ch = dims.maxY,
                className = "rh",
                x = 0,
                y = null
            ))
        }

        // This is for the cells
        for ((applicableColumn, applicableX) in applicableColumns) {
            for ((applicableRow, applicableY) in applicableRows) {
                // TODO Can probably skip empty cells here..
                // TODO PositionedCell will need it's height and width..
                //if (applicableColumn[applicableRow] is UnitCell) continue

                val cell = applicableColumn[applicableRow]

                output.add(PositionedContent(
                    applicableColumn.columnHeader,
                    applicableRow,
                    cell.toString(),
                    this[applicableRow].height,
                    this[applicableColumn].width,
                    className = if (cell is WebCell) "hc c" else "c",
                    x = applicableX + 100,
                    y = applicableY
                ))
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

        val lastKey = table.tableRef.get().indicesMap.last()?.component1() ?: -1
        for (row in 0..lastKey) {
            runningHeight += this[row].height
        }

        return Dimensions(defaultColumnStyle.width.toLong(), maxHeaderOffset, runningWidth, runningHeight)
    }
}

data class ColumnStyle internal constructor(val width: Long)

class ColumnStyleBuilder(var width: Long = 100)

fun columnStyle(init: ColumnStyleBuilder.() -> Unit): ColumnStyle {
    val builder = ColumnStyleBuilder()
    builder.init()
    return ColumnStyle(width = builder.width)
}

data class RowStyle internal constructor(val height: Long)

class RowStyleBuilder(var height: Long = 20)

fun rowStyle(init: RowStyleBuilder.() -> Unit): RowStyle {
    val builder = RowStyleBuilder()
    builder.init()
    return RowStyle(height = builder.height)
}

class CellStyle(val height: Long?, val width: Long?)

class CellStyleBuilder(var height: Long? = null, var width: Long? = null)

fun cellStyle(init: CellStyleBuilder.() -> Unit): CellStyle {
    val builder = CellStyleBuilder()
    builder.init()
    return CellStyle(height = builder.height, width = builder.width)
}

internal class PositionedContent(
    val contentHeader: ColumnHeader,
    val contentRow: Long,
    val content: String,
    val h: Long,
    val w: Long,
    val z: Long? = null,
    val mt: Long? = null, // Margin top
    val ml: Long? = null, // Margin left
    val cw: Long? = null, // Container width
    val ch: Long? = null, // Container height
    val className: String = "",
    val x: Long?,
    val y: Long?
)

internal class Dimensions(val cornerX: Long, val cornerY: Long, val maxX: Long, val maxY: Long)