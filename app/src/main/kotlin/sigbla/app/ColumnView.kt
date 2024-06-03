/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidCellWidthException
import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidRowException
import sigbla.app.pds.kollection.ImmutableSet as PSet
import sigbla.app.pds.kollection.immutableSetOf
import sigbla.app.pds.kollection.toImmutableSet
import java.util.*

class ColumnView internal constructor(
    val tableView: TableView,
    val header: Header
) : Iterable<DerivedCellView> {
    init {
        // Best efforts to help columns appear in the order they are referenced,
        // but ultimately this is controlled by the underlying table.
        if (header != EMPTY_HEADER) tableView.tableViewRef.get().table?.get(header)
    }

    operator fun get(column: ColumnTransformer.Companion): ColumnTransformer<*> {
        val ref = tableView.tableViewRef.get()
        val function = ref.columnTransformers[header] ?: return UnitColumnTransformer(this)
        return FunctionColumnTransformer(this, function)
    }

    operator fun set(column: ColumnTransformer.Companion, columnTransformer: Unit?) {
        setColumnTransformer(null)
    }

    operator fun set(column: ColumnTransformer.Companion, columnTransformer: ColumnTransformer<*>?) {
        setColumnTransformer(columnTransformer?.function as? (Column.() -> Unit)?)
    }

    operator fun set(column: ColumnTransformer.Companion, columnTransformer: (Column.() -> Unit)?) {
        setColumnTransformer(columnTransformer)
    }

    private fun setColumnTransformer(transformer: (Column.() -> Unit)?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                it.copy(
                    columnTransformers = if (transformer == null) it.columnTransformers.remove(header) else it.columnTransformers.put(header, transformer),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[header][ColumnTransformer]
            val new = newView[header][ColumnTransformer]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<ColumnTransformer<*>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellWidth: CellWidth.Companion): CellWidth<ColumnView, *> {
        val ref = tableView.tableViewRef.get()
        return when (val width = ref.columnViews[header]?.cellWidth) {
            is Long -> PixelCellWidth(this, width)
            else -> UnitCellWidth(this)
        }
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Unit?) {
        setCellWidth(null)
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Long?) {
        setCellWidth(width)
    }

    operator fun set(cellWidth: CellWidth.Companion, width: Number?) {
        when (width) {
            is Int -> setCellWidth(width.toLong())
            is Long -> setCellWidth(width)
            null -> setCellWidth(null)
            else -> throw InvalidCellWidthException("Unsupported type: ${width::class}")
        }
    }

    operator fun set(cellWidth: CellWidth.Companion, width: CellWidth<*, *>?) {
        setCellWidth(width?.asLong)
    }

    private fun setCellWidth(width: Long?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[header]
                val viewMeta = oldMeta?.copy(cellWidth = width) ?: ViewMeta(cellWidth = width)

                it.copy(
                    columnViews = it.columnViews.put(header, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[header][CellWidth]
            val new = newView[header][CellWidth]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellWidth<ColumnView, *>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellClasses: CellClasses.Companion): CellClasses<ColumnView> {
        val ref = tableView.tableViewRef.get()
        return CellClasses(this, ref.columnViews[header]?.cellClasses ?: immutableSetOf())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: Unit?) {
        setCellClasses(null)
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: String?) {
        if (classes == null) setCellClasses(null) else setCellClasses(immutableSetOf(classes))
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: Collection<String>?) {
        setCellClasses(classes?.toImmutableSet())
    }

    operator fun set(cellClasses: CellClasses.Companion, classes: CellClasses<*>?) {
        setCellClasses(classes?._classes)
    }

    private fun setCellClasses(classes: PSet<String>?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[header]
                val viewMeta = oldMeta?.copy(cellClasses = if (classes?.isEmpty() == true) null else classes) ?: ViewMeta(
                    cellClasses = if (classes?.isEmpty() == true) null else classes
                )

                it.copy(
                    columnViews = it.columnViews.put(header, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[header][CellClasses]
            val new = newView[header][CellClasses]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellClasses<ColumnView>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellTopics: CellTopics.Companion): CellTopics<ColumnView> {
        val ref = tableView.tableViewRef.get()
        return CellTopics(this, ref.columnViews[header]?.cellTopics ?: immutableSetOf())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: Unit?) {
        setCellTopics(null)
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: String?) {
        if (topics == null) setCellTopics(null) else setCellTopics(immutableSetOf(topics))
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: Collection<String>?) {
        setCellTopics(topics?.toImmutableSet())
    }

    operator fun set(cellTopics: CellTopics.Companion, topics: CellTopics<*>?) {
        setCellTopics(topics?._topics)
    }

    private fun setCellTopics(topics: PSet<String>?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[header]
                val viewMeta = oldMeta?.copy(cellTopics = if (topics?.isEmpty() == true) null else topics) ?: ViewMeta(
                    cellTopics = if (topics?.isEmpty() == true) null else topics
                )

                it.copy(
                    columnViews = it.columnViews.put(header, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[header][CellTopics]
            val new = newView[header][CellTopics]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellTopics<ColumnView>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(position: Position.Companion): Position.Horizontal<*> {
        val ref = tableView.tableViewRef.get()
        return when (ref.columnViews[header]?.positionValue) {
            null -> Position.Horizontal(this, Unit)
            Position.Value.LEFT -> Position.Left(this)
            Position.Value.RIGHT -> Position.Right(this)
            else -> throw InvalidColumnException("Unsupported position type: ${ref.columnViews[header]?.positionValue}")
        }
    }

    operator fun set(position: Position.Companion, newPosition: Unit?) {
        setColumnPosition(null)
    }

    operator fun set(position: Position.Companion, newPosition: Position.Horizontal<*>?) {
        setColumnPosition(newPosition?.asValue)
    }

    operator fun set(position: Position.Companion, newPosition: Position.Left.Companion?) {
        if (newPosition == null) setColumnPosition(null) else setColumnPosition(Position.Value.LEFT)
    }

    operator fun set(position: Position.Companion, newPosition: Position.Right.Companion?) {
        if (newPosition == null) setColumnPosition(null) else setColumnPosition(Position.Value.RIGHT)
    }

    private fun setColumnPosition(position: Position.Value?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.columnViews[header]
                val viewMeta = oldMeta?.copy(positionValue = position) ?: ViewMeta(positionValue = position)

                it.copy(
                    columnViews = it.columnViews.put(header, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[header][Position]
            val new = newView[header][Position]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<Position.Horizontal<*>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun invoke(newValue: ColumnView?): ColumnView? {
        tableView[this] = newValue
        return newValue
    }

    operator fun invoke(newValue: CellWidth<*, *>?): CellWidth<*, *>? {
        tableView[this][CellWidth] = newValue
        return newValue
    }

    operator fun invoke(newValue: CellClasses<*>?): CellClasses<*>? {
        tableView[this][CellClasses] = newValue
        return newValue
    }

    operator fun invoke(newValue: CellTopics<*>?): CellTopics<*>? {
        tableView[this][CellTopics] = newValue
        return newValue
    }

    operator fun invoke(newValue: ColumnTransformer<*>?): ColumnTransformer<*>? {
        tableView[this][ColumnTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: Position.Horizontal<*>?): Position.Horizontal<*>? {
        tableView[this][Position] = newValue
        return newValue
    }

    // TODO Other invoke for position?

    operator fun invoke(newValue: Unit?): Unit? {
        tableView[this] = newValue
        return newValue
    }

    // Note: cellViews return the defined CellViews, while the ColumnView iterator
    // returns the calculated cell views for current cells
    val cellViews: Sequence<CellView>
        get() = tableView.tableViewRef.get()
            .cellViews
            .keys()
            .filter { it.first == header }
            .asSequence()
            .map {
                CellView(ColumnView(this.tableView, it.first), it.second)
            }

    val derived: DerivedColumnView
        get() = createDerivedColumnView(this)

    operator fun get(index: Long): CellView = tableView[header, index]

    operator fun get(index: Int) = get(index.toLong())

    operator fun get(row: Row): CellView {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in get: $row")
        return get(row.index)
    }

    operator fun get(rowView: RowView) = get(rowView.index)

    operator fun set(index: Long, view: Unit?) { tableView[header, index] = view }

    operator fun set(index: Int, view: Unit?) { this[index.toLong()] = view }

    operator fun set(row: Row, view: Unit?) {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        this[row.index] = view
    }

    operator fun set(rowView: RowView, view: Unit?) { this[rowView.index] = view }

    operator fun set(index: Long, view: CellView?) { tableView[header, index] = view }

    operator fun set(index: Int, view: CellView?) { this[index.toLong()] = view }

    operator fun set(row: Row, view: CellView?) {
        if (row.indexRelation != IndexRelation.AT) throw InvalidRowException("Only IndexRelation.AT supported in set: $row")
        this[row.index] = view
    }

    operator fun set(rowView: RowView, view: CellView?) { this[rowView.index] = view }

    operator fun set(index: Long, function: CellView.() -> Unit) { this[index].function() }

    operator fun set(index: Int, function: CellView.() -> Unit) { this[index].function() }

    operator fun set(row: Row, function: CellView.() -> Unit) {
        // No IR.AT check here because this is actually a get before set
        this[row].function()
    }

    operator fun set(rowView: RowView, function: CellView.() -> Unit) { this[rowView.index].function() }

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableView.tableViewRef.get()
        val table = ref.table
            ?: return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val tableRef = table.tableRef.get()
        val columnMeta = tableRef.columns[header]
            ?: return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val values = tableRef.columnCells[header] ?: throw InvalidColumnException("Unable to find column cells for header $header")
        val columnIterator = values.asSequence().map { it.component2().toCell(
            Column(
                table,
                header,
                columnMeta.columnOrder
            ), it.component1()) }.iterator()

        return object : Iterator<DerivedCellView> {
            override fun hasNext() = columnIterator.hasNext()
            override fun next(): DerivedCellView {
                val cell = columnIterator.next()
                val columnView = ColumnView(this@ColumnView.tableView, cell.column.header)
                return createDerivedCellViewFromRef(ref, columnView, cell.index)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColumnView

        if (tableView != other.tableView) return false
        if (header != other.header) return false

        return true
    }

    override fun hashCode() = header.hashCode()

    override fun toString() = "ColumnView[${header.labels.joinToString(limit = 30)}]"
}

abstract class ColumnTransformer<T>(source: ColumnView, function: T): Transformer<ColumnView, T>(source, function) {
    operator fun invoke(newValue: ColumnTransformer<*>?): ColumnTransformer<*>? {
        source[ColumnTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: (Column.() -> Unit)?): (Column.() -> Unit)? {
        source[ColumnTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        source[ColumnTransformer] = newValue
        return newValue
    }

    operator fun contains(other: ColumnTransformer<*>) = function == other.function
    operator fun contains(other: Column.() -> Unit) = function == other
    operator fun contains(other: Unit) = function == other

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColumnTransformer<*>

        if (source != other.source) return false
        if (function != other.function) return false

        return true
    }

    override fun hashCode() = Objects.hashCode(this.function)

    companion object
}

class UnitColumnTransformer internal constructor(
    source: ColumnView
): ColumnTransformer<Unit>(source, Unit) {
    override fun toString() = "UnitColumnTransformer"
}

class FunctionColumnTransformer internal constructor(
    source: ColumnView,
    function: Column.() -> Unit
): ColumnTransformer<Column.() -> Unit>(source, function) {
    override fun toString() = "FunctionColumnTransformer[$function]"
}

class DerivedColumnView internal constructor(
    val columnView: ColumnView,
    val cellWidth: Long,
    classes: PSet<String>,
    topics: PSet<String>
) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = columnView.tableView

    val header: Header
        get() = columnView.header

    val cellClasses: CellClasses<DerivedColumnView> by lazy {
        CellClasses(this, classes)
    }

    val cellTopics: CellTopics<DerivedColumnView> by lazy {
        CellTopics(this, topics)
    }

    override fun iterator() = columnView.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DerivedColumnView

        if (columnView != other.columnView) return false

        return true
    }

    override fun hashCode() = columnView.hashCode()

    override fun toString() = "DerivedColumnView[${columnView.header.labels.joinToString(limit = 30)}]"
}

internal fun createDerivedColumnView(columnView: ColumnView): DerivedColumnView {
    val ref = columnView.tableView.tableViewRef.get()
    val defaultCellViewMeta = ref.defaultCellView
    val columnViewMeta = ref.columnViews[columnView.header]

    val width = columnViewMeta?.cellWidth
        ?: defaultCellViewMeta.cellWidth
        ?: DEFAULT_CELL_WIDTH

    val classes = (columnViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET)

    val topics = (columnViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET)

    return DerivedColumnView(columnView, width, classes, topics)
}