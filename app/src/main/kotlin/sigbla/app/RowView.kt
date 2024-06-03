/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidCellHeightException
import sigbla.app.exceptions.InvalidRowException
import sigbla.app.pds.kollection.ImmutableSet as PSet
import sigbla.app.pds.kollection.immutableSetOf
import sigbla.app.pds.kollection.toImmutableSet
import java.util.*

class RowView internal constructor(
    val tableView: TableView,
    val index: Long
) : Iterable<DerivedCellView> {
    operator fun get(row: RowTransformer.Companion): RowTransformer<*> {
        val ref = tableView.tableViewRef.get()
        val function = ref.rowTransformers[index] ?: return UnitRowTransformer(this)
        return FunctionRowTransformer(this, function)
    }

    operator fun set(row: RowTransformer.Companion, rowTransformer: Unit?) {
        setRowTransformer(null)
    }

    operator fun set(row: RowTransformer.Companion, rowTransformer: RowTransformer<*>?) {
        setRowTransformer(rowTransformer?.function as? (Row.() -> Unit)?)
    }

    operator fun set(row: RowTransformer.Companion, rowTransformer: (Row.() -> Unit)?) {
        setRowTransformer(rowTransformer)
    }

    private fun setRowTransformer(transformer: (Row.() -> Unit)?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                it.copy(
                    rowTransformers = if (transformer == null) it.rowTransformers.remove(index) else it.rowTransformers.put(index, transformer),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index][RowTransformer]
            val new = newView[index][RowTransformer]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<RowTransformer<*>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellHeight: CellHeight.Companion): CellHeight<RowView, *> {
        val ref = tableView.tableViewRef.get()
        return when (val height = ref.rowViews[index]?.cellHeight) {
            is Long -> PixelCellHeight(this, height)
            else -> UnitCellHeight(this)
        }
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Unit?) {
        setCellHeight(null)
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Long?) {
        setCellHeight(height)
    }

    operator fun set(cellHeight: CellHeight.Companion, height: Number?) {
        when (height) {
            is Int -> setCellHeight(height.toLong())
            is Long -> setCellHeight(height)
            null -> setCellHeight(null)
            else -> throw InvalidCellHeightException("Unsupported type: ${height::class}")
        }
    }

    operator fun set(cellHeight: CellHeight.Companion, height: CellHeight<*, *>?) {
        setCellHeight(height?.asLong)
    }

    private fun setCellHeight(height: Long?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.rowViews[index]
                val viewMeta = oldMeta?.copy(cellHeight = height) ?: ViewMeta(cellHeight = height)

                it.copy(
                    rowViews = it.rowViews.put(index, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index][CellHeight]
            val new = newView[index][CellHeight]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellHeight<RowView, *>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellClasses: CellClasses.Companion): CellClasses<RowView> {
        val ref = tableView.tableViewRef.get()
        return CellClasses(this, ref.rowViews[index]?.cellClasses ?: immutableSetOf())
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
                val oldMeta = it.rowViews[index]
                val viewMeta = oldMeta?.copy(cellClasses = if (classes?.isEmpty() == true) null else classes) ?: ViewMeta(
                    cellClasses = if (classes?.isEmpty() == true) null else classes
                )

                it.copy(
                    rowViews = it.rowViews.put(index, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index][CellClasses]
            val new = newView[index][CellClasses]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellClasses<RowView>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellTopics: CellTopics.Companion): CellTopics<RowView> {
        val ref = tableView.tableViewRef.get()
        return CellTopics(this, ref.rowViews[index]?.cellTopics ?: immutableSetOf())
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
                val oldMeta = it.rowViews[index]
                val viewMeta = oldMeta?.copy(cellTopics = if (topics?.isEmpty() == true) null else topics) ?: ViewMeta(
                    cellTopics = if (topics?.isEmpty() == true) null else topics
                )

                it.copy(
                    rowViews = it.rowViews.put(index, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index][CellTopics]
            val new = newView[index][CellTopics]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellTopics<RowView>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(position: Position.Companion): Position.Vertical<*> {
        val ref = tableView.tableViewRef.get()
        return when (ref.rowViews[index]?.positionValue) {
            null -> Position.Vertical(this, Unit)
            Position.Value.TOP -> Position.Top(this)
            Position.Value.BOTTOM -> Position.Bottom(this)
            else -> throw InvalidRowException("Unsupported position type: ${ref.rowViews[index]?.positionValue}")
        }
    }

    operator fun set(position: Position.Companion, newPosition: Unit?) {
        setRowPosition(null)
    }

    operator fun set(position: Position.Companion, newPosition: Position.Vertical<*>?) {
        setRowPosition(newPosition?.asValue)
    }

    operator fun set(position: Position.Companion, newPosition: Position.Top.Companion?) {
        if (newPosition == null) setRowPosition(null) else setRowPosition(Position.Value.TOP)
    }

    operator fun set(position: Position.Companion, newPosition: Position.Bottom.Companion?) {
        if (newPosition == null) setRowPosition(null) else setRowPosition(Position.Value.BOTTOM)
    }

    private fun setRowPosition(position: Position.Value?) {
        synchronized(tableView.eventProcessor) {
            val (oldRef, newRef) = tableView.tableViewRef.refAction {
                val oldMeta = it.rowViews[index]
                val viewMeta = oldMeta?.copy(positionValue = position) ?: ViewMeta(positionValue = position)

                it.copy(
                    rowViews = it.rowViews.put(index, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!tableView.eventProcessor.haveListeners()) return

            val oldView = tableView.makeClone(ref = oldRef)
            val newView = tableView.makeClone(ref = newRef)

            val old = oldView[index][Position]
            val new = newView[index][Position]

            tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<Position.Vertical<*>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun invoke(newValue: RowView?): RowView? {
        tableView[this] = newValue
        return newValue
    }

    operator fun invoke(newValue: CellHeight<*, *>?): CellHeight<*, *>? {
        tableView[this][CellHeight] = newValue
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

    operator fun invoke(newValue: RowTransformer<*>?): RowTransformer<*>? {
        tableView[this][RowTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: Position.Vertical<*>?): Position.Vertical<*>? {
        tableView[this][Position] = newValue
        return newValue
    }

    // TODO Other invoke for position?

    operator fun invoke(newValue: Unit?): Unit? {
        tableView[this] = Unit
        return newValue
    }

    // Note: cellViews return the defined CellViews, while the RowView iterator
    // returns the calculated cell views for current cells
    val cellViews: Sequence<CellView>
        get() = tableView.tableViewRef.get()
            .cellViews
            .keys()
            .filter { it.second == index }
            .asSequence()
            .map {
                CellView(ColumnView(this.tableView, it.first), it.second)
            }

    val derived: DerivedRowView
        get() = createDerivedRowView(this)

    operator fun get(vararg header: String): CellView = tableView[Header(*header), index]

    operator fun get(header: Header): CellView = tableView[header, index]

    operator fun get(columnView: ColumnView): CellView = tableView[columnView.header, index]

    operator fun get(column: Column): CellView = tableView[column.header, index]

    operator fun set(vararg header: String, view: Unit?) { tableView[Header(*header), index] = view }

    operator fun set(header: Header, view: Unit?) { tableView[header, index] = view }

    operator fun set(columnView: ColumnView, view: Unit?) { tableView[columnView.header, index] = view }

    operator fun set(column: Column, view: Unit?) { tableView[column.header, index] = view }

    operator fun set(vararg header: String, view: CellView?) { tableView[Header(*header), index] = view }

    operator fun set(header: Header, view: CellView?) { tableView[header, index] = view }

    operator fun set(columnView: ColumnView, view: CellView?) { tableView[columnView.header, index] = view }

    operator fun set(column: Column, view: CellView?) { tableView[column.header, index] = view }

    operator fun set(vararg header: String, function: CellView.() -> Unit) { tableView[Header(*header), index].function() }

    operator fun set(header: Header, function: CellView.() -> Unit) { tableView[header, index].function() }

    operator fun set(columnView: ColumnView, function: CellView.() -> Unit) { tableView[columnView.header, index].function() }

    operator fun set(column: Column, function: CellView.() -> Unit) { tableView[column.header, index].function() }

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableView.tableViewRef.get()
        val table = ref.table
            ?: return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val rowIterator = table[IndexRelation.AT, index].iterator()

        return object : Iterator<DerivedCellView> {
            override fun hasNext() = rowIterator.hasNext()
            override fun next(): DerivedCellView {
                val cell = rowIterator.next()
                val columnView = ColumnView(this@RowView.tableView, cell.column.header)
                return createDerivedCellViewFromRef(ref, columnView, cell.index)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RowView

        if (tableView != other.tableView) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int = index.hashCode()

    override fun toString(): String = "RowView[$index]"
}

abstract class RowTransformer<T>(source: RowView, function: T): Transformer<RowView, T>(source, function) {
    operator fun invoke(newValue: RowTransformer<*>?): RowTransformer<*>? {
        source[RowTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: (Row.() -> Unit)?): (Row.() -> Unit)? {
        source[RowTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        source[RowTransformer] = newValue
        return newValue
    }

    operator fun contains(other: RowTransformer<*>) = function == other.function
    operator fun contains(other: Row.() -> Unit) = function == other
    operator fun contains(other: Unit) = function == other

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RowTransformer<*>

        if (source != other.source) return false
        if (function != other.function) return false

        return true
    }

    override fun hashCode() = Objects.hashCode(this.function)

    companion object
}

class UnitRowTransformer internal constructor(
    source: RowView
): RowTransformer<Unit>(source, Unit) {
    override fun toString() = "UnitRowTransformer"
}

class FunctionRowTransformer internal constructor(
    source: RowView,
    function: Row.() -> Unit
): RowTransformer<Row.() -> Unit>(source, function) {
    override fun toString() = "FunctionRowTransformer[$function]"
}

class DerivedRowView internal constructor(
    val rowView: RowView,
    val cellHeight: Long,
    classes: PSet<String>,
    topics: PSet<String>
) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = rowView.tableView

    val index: Long
        get() = rowView.index

    val cellClasses: CellClasses<DerivedRowView> by lazy {
        CellClasses(this, classes)
    }

    val cellTopics: CellTopics<DerivedRowView> by lazy {
        CellTopics(this, topics)
    }

    override fun iterator() = rowView.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DerivedRowView

        if (rowView != other.rowView) return false

        return true
    }

    override fun hashCode() = rowView.hashCode()

    override fun toString() = "DerivedRowView[${rowView.index}]"
}

internal fun createDerivedRowView(rowView: RowView): DerivedRowView {
    val ref = rowView.tableView.tableViewRef.get()
    val defaultCellViewMeta = ref.defaultCellView
    val rowViewMeta = ref.rowViews[rowView.index]

    val height = rowViewMeta?.cellHeight
        ?: defaultCellViewMeta.cellHeight
        ?: DEFAULT_CELL_HEIGHT

    val classes = (rowViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET)

    val topics = (rowViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET)

    return DerivedRowView(rowView, height, classes, topics)
}