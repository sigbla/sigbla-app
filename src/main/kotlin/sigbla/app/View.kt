package sigbla.app

import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.Registry
import sigbla.app.internals.SigblaBackend
import sigbla.app.internals.ViewEventProcessor
import sigbla.app.internals.refAction
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass
import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.collection.Map as PMap

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

    internal abstract val eventProcessor: ViewEventProcessor

    abstract operator fun get(type: DEFAULT_COLUMN_STYLE): DefaultColumnStyle

    abstract operator fun set(type: DEFAULT_COLUMN_STYLE, init: DefaultColumnStyleBuilder.() -> Unit)

    abstract operator fun set(type: DEFAULT_COLUMN_STYLE, defaultColumnStyle: DefaultColumnStyle)

    abstract operator fun get(type: DEFAULT_ROW_STYLE): DefaultRowStyle

    abstract operator fun set(type: DEFAULT_ROW_STYLE, init: DefaultRowStyleBuilder.() -> Unit)

    abstract operator fun set(type: DEFAULT_ROW_STYLE, defaultRowStyle: DefaultRowStyle)

    abstract operator fun get(type: DEFAULT_CELL_STYLE): DefaultCellStyle

    abstract operator fun set(type: DEFAULT_CELL_STYLE, init: DefaultCellStyleBuilder.() -> Unit)

    abstract operator fun set(type: DEFAULT_CELL_STYLE, defaultCellStyle: DefaultCellStyle)

    abstract operator fun get(columnHeader: ColumnHeader): ColumnStyle

    operator fun get(column: Column) = get(column.columnHeader)

    abstract operator fun get(row: Long): RowStyle

    operator fun get(row: Row) = get(row.index)

    abstract operator fun get(columnHeader: ColumnHeader, row: Long): CellStyle

    operator fun get(column: Column, row: Long) = get(column.columnHeader, row)

    operator fun get(cell: Cell<*>) = get(cell.column, cell.index)

    abstract operator fun set(columnHeader: ColumnHeader, init: ColumnStyleBuilder.() -> Unit)

    operator fun set(column: Column, init: ColumnStyleBuilder.() -> Unit) = set(column.columnHeader, init)

    abstract operator fun set(columnHeader: ColumnHeader, columnStyle: ColumnStyle?)

    operator fun set(column: Column, columnStyle: ColumnStyle?) = set(column.columnHeader, columnStyle)

    abstract operator fun set(row: Long, init: RowStyleBuilder.() -> Unit)

    abstract operator fun set(row: Long, rowStyle: RowStyle?)

    operator fun set(row: Row, rowStyle: RowStyle?) = set(row.index, rowStyle)

    abstract operator fun set(columnHeader: ColumnHeader, row: Long, init: CellStyleBuilder.() -> Unit)

    operator fun set(column: Column, row: Long, init: CellStyleBuilder.() -> Unit) = set(column.columnHeader, row, init)

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

    inline fun <reified T> on(noinline init: TableViewEventReceiver<TableView, T>.() -> Unit): TableViewListenerReference {
        return on(T::class, init as TableViewEventReceiver<TableView, Any>.() -> Unit)
    }

    fun onAny(init: TableViewEventReceiver<TableView, Any>.() -> Unit): TableViewListenerReference {
        return on(Any::class, init)
    }

    fun on(type: KClass<*> = Any::class, init: TableViewEventReceiver<TableView, Any>.() -> Unit): TableViewListenerReference {
        val eventReceiver = when {
            type == Any::class -> TableViewEventReceiver<TableView, Any>(
                this
            ) { this }
            else -> TableViewEventReceiver(this) {
                this.filter {
                    type.isInstance(it.oldValue) && type.isInstance(it.newValue)
                }
            }
        }
        return eventProcessor.subscribe(this, eventReceiver, init)
    }

    abstract fun clone(): TableView

    abstract fun clone(name: String): TableView

    internal abstract fun makeClone(name: String = this.name, onRegistry: Boolean = false, ref: ViewRef = viewRef.get()!!): TableView

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
    val defaultColumnStyle: DefaultColumnStyle,
    val defaultRowStyle: DefaultRowStyle,
    val defaultCellStyle: DefaultCellStyle,
    val columnStyles: PMap<ColumnHeader, ColumnStyle> = PHashMap(),
    val rowStyles: PMap<Long, RowStyle> = PHashMap(),
    val cellStyles: PMap<Pair<ColumnHeader, Long>, CellStyle> = PHashMap()
)

class BaseTableView internal constructor(
    name: String,
    table: Table?,
    onRegistry: Boolean = true,
    viewRef: AtomicReference<ViewRef>? = null,
    override val eventProcessor: ViewEventProcessor = ViewEventProcessor()
) : TableView(name) {
    constructor(table: Table) : this(table.name, table)
    constructor(name: String) : this(name, Registry.getTable(name))

    override val viewRef: AtomicReference<ViewRef> = viewRef ?: AtomicReference(ViewRef(
        DefaultColumnStyle(view = this),
        DefaultRowStyle(view = this),
        DefaultCellStyle(view = this)
    ))

    init {
        if (onRegistry) Registry.setView(name, this)
    }

    override var table = table
        @Synchronized
        set(table) {
            val oldTable = field
            field = table

            if (!eventProcessor.haveListeners()) return

            eventProcessor.publish(listOf(
                TableViewListenerEvent(
                    oldTable,
                    table
                )
            ) as List<TableViewListenerEvent<Any>>)
        }

    override operator fun get(type: DEFAULT_COLUMN_STYLE): DefaultColumnStyle = viewRef.get().defaultColumnStyle

    override operator fun set(type: DEFAULT_COLUMN_STYLE, init: DefaultColumnStyleBuilder.() -> Unit) {
        val defaultColumnStyleBuilder = DefaultColumnStyleBuilder()
        defaultColumnStyleBuilder.init()
        set(type, defaultColumnStyleBuilder.build(this))
    }

    override operator fun set(type: DEFAULT_COLUMN_STYLE, defaultColumnStyle: DefaultColumnStyle) {
        val (oldRef, newRef) = viewRef.refAction {
            it.copy(
                defaultColumnStyle = defaultColumnStyle.ensureStyle(this)
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[DEFAULT_COLUMN_STYLE]
        val new = newView[DEFAULT_COLUMN_STYLE]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                old,
                new
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    override operator fun get(type: DEFAULT_ROW_STYLE): DefaultRowStyle = viewRef.get().defaultRowStyle

    override operator fun set(type: DEFAULT_ROW_STYLE, init: DefaultRowStyleBuilder.() -> Unit) {
        val defaultRowStyleBuilder = DefaultRowStyleBuilder()
        defaultRowStyleBuilder.init()
        set(type, defaultRowStyleBuilder.build(this))
    }

    override operator fun set(type: DEFAULT_ROW_STYLE, defaultRowStyle: DefaultRowStyle) {
        val (oldRef, newRef) = viewRef.refAction {
            it.copy(
                defaultRowStyle = defaultRowStyle.ensureStyle(this)
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[DEFAULT_ROW_STYLE]
        val new = newView[DEFAULT_ROW_STYLE]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                old,
                new
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    override operator fun get(type: DEFAULT_CELL_STYLE): DefaultCellStyle = viewRef.get().defaultCellStyle

    override operator fun set(type: DEFAULT_CELL_STYLE, init: DefaultCellStyleBuilder.() -> Unit) {
        val defaultCellStyleBuilder = DefaultCellStyleBuilder()
        defaultCellStyleBuilder.init()
        set(type, defaultCellStyleBuilder.build(this))
    }

    override operator fun set(type: DEFAULT_CELL_STYLE, defaultCellStyle: DefaultCellStyle) {
        val (oldRef, newRef) = viewRef.refAction {
            it.copy(
                defaultCellStyle = defaultCellStyle.ensureStyle(this)
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[DEFAULT_CELL_STYLE]
        val new = newView[DEFAULT_CELL_STYLE]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                old,
                new
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    override fun get(columnHeader: ColumnHeader): ColumnStyle = viewRef.get().columnStyles[columnHeader] ?: this[DEFAULT_COLUMN_STYLE].toColumnStyle(columnHeader)

    override fun get(row: Long): RowStyle = viewRef.get().rowStyles[row] ?: this[DEFAULT_ROW_STYLE].toRowStyle(row)

    override fun get(columnHeader: ColumnHeader, row: Long): CellStyle = viewRef.get().cellStyles[Pair(columnHeader, row)] ?: this[DEFAULT_CELL_STYLE].toCellStyle(columnHeader, row)

    override fun set(columnHeader: ColumnHeader, init: ColumnStyleBuilder.() -> Unit) {
        val columnStyleBuilder = ColumnStyleBuilder()
        columnStyleBuilder.init()
        set(columnHeader, columnStyleBuilder.build(this, columnHeader))
    }

    @Synchronized
    override fun set(columnHeader: ColumnHeader, columnStyle: ColumnStyle?) {
        if (columnStyle == null) {
            remove(columnHeader)
        } else {
            val (oldRef, newRef) = viewRef.refAction {
                it.copy(
                    columnStyles = it.columnStyles.put(columnHeader, columnStyle.ensureStyle(this, columnHeader))
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val old = oldView[columnHeader]
            val new = newView[columnHeader]

            eventProcessor.publish(listOf(
                TableViewListenerEvent(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    override fun set(row: Long, init: RowStyleBuilder.() -> Unit) {
        val rowStyleBuilder = RowStyleBuilder()
        rowStyleBuilder.init()
        set(row, rowStyleBuilder.build(this, row))
    }

    @Synchronized
    override fun set(row: Long, rowStyle: RowStyle?) {
        if (rowStyle == null) {
            remove(row)
        } else {
            val (oldRef, newRef) = viewRef.refAction {
                it.copy(
                    rowStyles = it.rowStyles.put(row, rowStyle.ensureStyle(this, row))
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val old = oldView[row]
            val new = newView[row]

            eventProcessor.publish(listOf(
                TableViewListenerEvent(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    override fun set(columnHeader: ColumnHeader, row: Long, init: CellStyleBuilder.() -> Unit) {
        val cellStyleBuilder = CellStyleBuilder()
        cellStyleBuilder.init()
        set(columnHeader, row, cellStyleBuilder.build(this, columnHeader, row))
    }

    @Synchronized
    override fun set(columnHeader: ColumnHeader, row: Long, cellStyle: CellStyle?) {
        if (cellStyle == null) {
            remove(columnHeader, row)
        } else {
            val (oldRef, newRef) = viewRef.refAction {
                it.copy(
                    cellStyles = it.cellStyles.put(Pair(columnHeader, row), cellStyle.ensureStyle(this, columnHeader, row))
                )
            }

            if (!eventProcessor.haveListeners()) return

            val oldView = makeClone(ref = oldRef)
            val newView = makeClone(ref = newRef)

            val old = oldView[columnHeader, row]
            val new = newView[columnHeader, row]

            eventProcessor.publish(listOf(
                TableViewListenerEvent(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    @Synchronized
    override fun remove(columnHeader: ColumnHeader) {
        val (oldRef, newRef) = viewRef.refAction {
            it.copy(
                columnStyles = it.columnStyles.remove(columnHeader)
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[columnHeader]
        val new = newView[columnHeader]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                old,
                new
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    @Synchronized
    override fun remove(row: Long) {
        val (oldRef, newRef) = viewRef.refAction {
            it.copy(
                rowStyles = it.rowStyles.remove(row)
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[row]
        val new = newView[row]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                old,
                new
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    @Synchronized
    override fun remove(columnHeader: ColumnHeader, row: Long) {
        val (oldRef, newRef) = viewRef.refAction {
            it.copy(
                cellStyles = it.cellStyles.remove(Pair(columnHeader, row))
            )
        }

        if (!eventProcessor.haveListeners()) return

        val oldView = makeClone(ref = oldRef)
        val newView = makeClone(ref = newRef)

        val old = oldView[columnHeader, row]
        val new = newView[columnHeader, row]

        eventProcessor.publish(listOf(
            TableViewListenerEvent(
                old,
                new
            )
        ) as List<TableViewListenerEvent<Any>>)
    }

    @Synchronized
    override fun areaContent(x: Int, y: Int, h: Int, w: Int, dims: Dimensions): List<PositionedContent> {
        val table = this.table ?: return emptyList()

        val applicableColumns = mutableListOf<Pair<Column, Long>>()
        var runningWidth = 0L
        var maxHeaderOffset = 0L
        var maxHeaderCells = 0

        val defaultRowStyle = this[DEFAULT_ROW_STYLE]

        for (column in table.columns) {
            if (x <= runningWidth && runningWidth <= x + w) applicableColumns.add(Pair(column, runningWidth))
            runningWidth += this[column].width

            val yOffset = column.columnHeader.header.size * defaultRowStyle.height
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
                val yOffset = idx.toLong() * defaultRowStyle.height

                output.add(PositionedContent(
                    applicableColumn.columnHeader,
                    (-1 - idx).toLong(),
                    headerText,
                    defaultRowStyle.height,
                    this[applicableColumn].width,
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

        val defaultColumnStyle = this[DEFAULT_COLUMN_STYLE]
        val defaultRowStyle = this[DEFAULT_ROW_STYLE]

        var runningWidth = defaultColumnStyle.width
        var maxHeaderOffset = 0L

        for (column in table.columns) {
            runningWidth += this[column].width

            val yOffset = column.columnHeader.header.size * defaultRowStyle.height
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
        }

        var runningHeight = maxHeaderOffset

        val lastKey = table.tableRef.get().indicesMap.last()?.component1() ?: -1
        for (row in 0..lastKey) {
            runningHeight += this[row].height
        }

        return Dimensions(defaultColumnStyle.width, maxHeaderOffset, runningWidth, runningHeight)
    }

    override fun clone(): TableView {
        return makeClone()
    }

    override fun clone(name: String): TableView {
        return makeClone(name, true)
    }

    override fun makeClone(name: String, onRegistry: Boolean, ref: ViewRef): TableView {
        val newViewRef = AtomicReference<ViewRef>(ref)
        val tableViewClone = BaseTableView(name, table, onRegistry, newViewRef)

        val newDefaultColumnStyle = ref.defaultColumnStyle.ensureStyle(tableViewClone)
        val newDefaultRowStyle = ref.defaultRowStyle.ensureStyle(tableViewClone)
        val newDefaultCellStyle = ref.defaultCellStyle.ensureStyle(tableViewClone)

        // TODO: Like with table clone, something more efficient than this would be nice
        val newColumnsStyles = ref.columnStyles.fold(PHashMap<ColumnHeader, ColumnStyle>()) { acc, chc ->
            acc.put(chc.component1(), ColumnStyle(chc.component2().width, tableViewClone, chc.component1()))
        }

        // TODO: Like with table clone, something more efficient than this would be nice
        val newRowStyles = ref.rowStyles.fold(PHashMap<Long, RowStyle>()) { acc, lrs ->
            acc.put(lrs.component1(), RowStyle(lrs.component2().height, tableViewClone, lrs.component1()))
        }

        // TODO: Like with table clone, something more efficient than this would be nice
        val newCellStyles = ref.cellStyles.fold(PHashMap<Pair<ColumnHeader, Long>, CellStyle>()) { acc, chlcs ->
            acc.put(chlcs.component1(), CellStyle(chlcs.component2().height, chlcs.component2().width, tableViewClone, chlcs.component1().first, chlcs.component1().second))
        }

        newViewRef.set(ViewRef(
            newDefaultColumnStyle,
            newDefaultRowStyle,
            newDefaultCellStyle,
            newColumnsStyles,
            newRowStyles,
            newCellStyles
        ))

        return tableViewClone
    }
}

private const val STANDARD_WIDTH = 100L
private const val STANDARD_HEIGHT = 20L

sealed class Style(val view: TableView)

object DEFAULT_COLUMN_STYLE
object DEFAULT_ROW_STYLE
object DEFAULT_CELL_STYLE

class ColumnStyle internal constructor(val width: Long = STANDARD_WIDTH, view: TableView, val columnHeader: ColumnHeader) : Style(view) {
    internal fun ensureStyle(view: TableView, columnHeader: ColumnHeader) = ColumnStyle(width, view, columnHeader)

    override fun toString(): String {
        return "ColumnStyle(width=$width, columnHeader=$columnHeader)"
    }
}

class DefaultColumnStyle internal constructor(val width: Long = STANDARD_WIDTH, view: TableView) : Style(view) {
    internal fun toColumnStyle(columnHeader: ColumnHeader) = ColumnStyle(width, view, columnHeader)
    internal fun ensureStyle(view: TableView) = DefaultColumnStyle(width, view)

    override fun toString(): String {
        return "DefaultColumnStyle(width=$width)"
    }
}

class RowStyle internal constructor(val height: Long = STANDARD_HEIGHT, view: TableView, val index: Long) : Style(view) {
    internal fun ensureStyle(view: TableView, index: Long) = RowStyle(height, view, index)

    override fun toString(): String {
        return "RowStyle(height=$height, index=$index)"
    }
}

class DefaultRowStyle internal constructor(val height: Long = STANDARD_HEIGHT, view: TableView) : Style(view) {
    internal fun toRowStyle(index: Long) = RowStyle(height, view, index)
    internal fun ensureStyle(view: TableView) = DefaultRowStyle(height, view)

    override fun toString(): String {
        return "DefaultRowStyle(height=$height)"
    }
}

class CellStyle internal constructor(val height: Long = STANDARD_HEIGHT, val width: Long = STANDARD_WIDTH, view: TableView, val columnHeader: ColumnHeader, val index: Long) : Style(view) {
    internal fun ensureStyle(view: TableView, columnHeader: ColumnHeader, index: Long) = CellStyle(height, width, view, columnHeader, index)

    override fun toString(): String {
        return "CellStyle(height=$height, width=$width, columnHeader=$columnHeader, index=$index)"
    }
}

class DefaultCellStyle internal constructor(val height: Long = STANDARD_HEIGHT, val width: Long = STANDARD_WIDTH, view: TableView) : Style(view) {
    internal fun toCellStyle(columnHeader: ColumnHeader, index: Long) = CellStyle(height, width, view, columnHeader, index)
    internal fun ensureStyle(view: TableView) = DefaultCellStyle(height, width, view)

    override fun toString(): String {
        return "DefaultCellStyle(height=$height, width=$width)"
    }
}

class ColumnStyleBuilder(var width: Long = STANDARD_WIDTH) {
    internal fun build(view: TableView, columnHeader: ColumnHeader): ColumnStyle = ColumnStyle(width, view, columnHeader)
}

class RowStyleBuilder(var height: Long = STANDARD_HEIGHT) {
    internal fun build(view: TableView, index: Long): RowStyle = RowStyle(height, view, index)
}

class CellStyleBuilder(var height: Long = STANDARD_HEIGHT, var width: Long = STANDARD_WIDTH) {
    internal fun build(view: TableView, columnHeader: ColumnHeader, index: Long): CellStyle = CellStyle(height, width, view, columnHeader, index)
}

class DefaultColumnStyleBuilder(var width: Long = STANDARD_WIDTH) {
    internal fun build(view: TableView): DefaultColumnStyle = DefaultColumnStyle(width, view)
}

class DefaultRowStyleBuilder(var height: Long = STANDARD_HEIGHT) {
    internal fun build(view: TableView): DefaultRowStyle = DefaultRowStyle(height, view)
}

class DefaultCellStyleBuilder(var height: Long = STANDARD_HEIGHT, var width: Long = STANDARD_WIDTH) {
    internal fun build(view: TableView): DefaultCellStyle = DefaultCellStyle(height, width, view)
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