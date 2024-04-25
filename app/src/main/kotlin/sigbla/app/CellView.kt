/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidCellException
import sigbla.app.exceptions.InvalidCellHeightException
import sigbla.app.exceptions.InvalidCellWidthException
import sigbla.app.pds.kollection.ImmutableSet as PSet
import sigbla.app.pds.kollection.immutableSetOf
import sigbla.app.pds.kollection.toImmutableSet
import java.util.*

class CellView(
    val columnView: ColumnView,
    val index: Long
) : Iterable<DerivedCellView> {
    operator fun get(cellHeight: CellHeight.Companion): CellHeight<CellView, *> {
        val ref = tableView.tableViewRef.get()
        return when (val height = ref.cellViews[Pair(columnView.header, index)]?.cellHeight) {
            is Long -> PixelCellHeight(this, height)
            else -> UnitCellHeight(this)
        }
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

    operator fun set(cellHeight: CellHeight.Companion, height: Unit?) {
        setCellHeight(null)
    }

    private fun setCellHeight(height: Long?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellHeight = height) ?: ViewMeta(cellHeight = height)

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.header][index][CellHeight]
            val new = newView[columnView.header][index][CellHeight]

            columnView.tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellHeight<CellView, *>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellWidth: CellWidth.Companion): CellWidth<CellView, *> {
        val ref = tableView.tableViewRef.get()
        return when (val width = ref.cellViews[Pair(columnView.header, index)]?.cellWidth) {
            is Long -> PixelCellWidth(this, width)
            else -> UnitCellWidth(this)
        }
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

    operator fun set(cellWidth: CellWidth.Companion, width: Unit?) {
        setCellWidth(null)
    }

    private fun setCellWidth(width: Long?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellWidth = width) ?: ViewMeta(cellWidth = width)

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.header][index][CellWidth]
            val new = newView[columnView.header][index][CellWidth]

            columnView.tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellWidth<CellView, *>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellClasses: CellClasses.Companion): CellClasses<CellView> {
        val ref = tableView.tableViewRef.get()
        return CellClasses(this, ref.cellViews[Pair(columnView.header, index)]?.cellClasses ?: immutableSetOf())
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

    operator fun set(cellClasses: CellClasses.Companion, classes: Unit?) {
        setCellClasses(null)
    }

    private fun setCellClasses(classes: PSet<String>?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellClasses = if (classes?.isEmpty() == true) null else classes) ?: ViewMeta(
                    cellClasses = if (classes?.isEmpty() == true) null else classes
                )

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.header][index][CellClasses]
            val new = newView[columnView.header][index][CellClasses]

            columnView.tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellClasses<CellView>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cellTopics: CellTopics.Companion): CellTopics<CellView> {
        val ref = tableView.tableViewRef.get()
        return CellTopics(this, ref.cellViews[Pair(columnView.header, index)]?.cellTopics ?: immutableSetOf())
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

    operator fun set(cellTopics: CellTopics.Companion, topics: Unit?) {
        setCellTopics(null)
    }

    private fun setCellTopics(topics: PSet<String>?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)
                val oldMeta = it.cellViews[key]
                val viewMeta = oldMeta?.copy(cellTopics = if (topics?.isEmpty() == true) null else topics) ?: ViewMeta(
                    cellTopics = if (topics?.isEmpty() == true) null else topics
                )

                it.copy(
                    cellViews = it.cellViews.put(key, viewMeta),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.header][index][CellTopics]
            val new = newView[columnView.header][index][CellTopics]

            columnView.tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellTopics<CellView>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun get(cell: CellTransformer.Companion): CellTransformer<*> {
        val ref = tableView.tableViewRef.get()
        val function = ref.cellTransformers[Pair(columnView.header, index)] ?: return UnitCellTransformer(this)
        return FunctionCellTransformer(this, function)
    }

    operator fun set(cell: CellTransformer.Companion, cellTransformer: CellTransformer<*>?) {
        setCellTransformer(cellTransformer?.function as? (Cell<*>.() -> Unit)?)
    }

    operator fun set(cell: CellTransformer.Companion, cellTransformer: Unit?) {
        setCellTransformer(null)
    }

    operator fun set(cell: CellTransformer.Companion, cellTransformer: (Cell<*>.() -> Unit)?) {
        setCellTransformer(cellTransformer)
    }

    private fun setCellTransformer(transformer: (Cell<*>.() -> Unit)?) {
        synchronized(columnView.tableView.eventProcessor) {
            val (oldRef, newRef) = columnView.tableView.tableViewRef.refAction {
                val key = Pair(columnView.header, index)

                it.copy(
                    cellTransformers = if (transformer == null) it.cellTransformers.remove(key) else it.cellTransformers.put(key, transformer),
                    version = it.version + 1L
                )
            }

            if (!columnView.tableView.eventProcessor.haveListeners()) return

            val oldView = columnView.tableView.makeClone(ref = oldRef)
            val newView = columnView.tableView.makeClone(ref = newRef)

            val old = oldView[columnView.header][index][CellTransformer]
            val new = newView[columnView.header][index][CellTransformer]

            columnView.tableView.eventProcessor.publish(listOf(
                TableViewListenerEvent<CellTransformer<*>>(
                    old,
                    new
                )
            ) as List<TableViewListenerEvent<Any>>)
        }
    }

    operator fun invoke(newValue: CellView?): CellView? {
        tableView[this] = newValue
        return newValue
    }

    operator fun invoke(newValue: CellHeight<*, *>?): CellHeight<*, *>? {
        tableView[this][CellHeight] = newValue
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

    operator fun invoke(newValue: CellTransformer<*>?): CellTransformer<*>? {
        tableView[this][CellTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        tableView[this] = newValue
        return newValue
    }

    val tableView: TableView
        get() = columnView.tableView

    val cell: Cell<*>?
        get() = tableView[Table]?.let { return it[columnView.header][index] }

    val derived: DerivedCellView
        get() = createDerivedCellViewFromRef(this.tableView.tableViewRef.get(), columnView, index)

    override fun iterator(): Iterator<DerivedCellView> {
        val ref = tableView.tableViewRef.get()
        if (ref.table == null || ref.table.tableRef.get().columnCells.get(this.columnView.header)?.get(this.index) == null)
            return object : Iterator<DerivedCellView> {
                override fun hasNext() = false
                override fun next() = throw NoSuchElementException()
            }

        val derivedCellView = createDerivedCellViewFromRef(ref, columnView, index)

        return listOf(derivedCellView).iterator()
    }

    override fun toString() = "CellView[${columnView.header.labels.joinToString(limit = 30)}, $index]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellView

        if (columnView != other.columnView) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = columnView.hashCode()
        result = 31 * result + index.hashCode()
        return result
    }
}

abstract class CellTransformer<T>(source: CellView, function: T): Transformer<CellView, T>(source, function) {
    operator fun invoke(newValue: CellTransformer<*>?): CellTransformer<*>? {
        source[CellTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: (Cell<*>.() -> Unit)?): (Cell<*>.() -> Unit)? {
        source[CellTransformer] = newValue
        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        source[CellTransformer] = newValue
        return newValue
    }

    operator fun contains(other: CellTransformer<*>) = function == other.function
    operator fun contains(other: Cell<*>.() -> Unit) = function == other
    operator fun contains(other: Unit) = function == other

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellTransformer<*>

        if (source != other.source) return false
        if (function != other.function) return false

        return true
    }

    override fun hashCode() = Objects.hashCode(this.function)

    companion object
}

class UnitCellTransformer internal constructor(
    source: CellView
): CellTransformer<Unit>(source, Unit) {
    override fun toString() = "UnitCellTransformer"
}

class FunctionCellTransformer internal constructor(
    source: CellView,
    function: Cell<*>.() -> Unit
): CellTransformer<Cell<*>.() -> Unit>(source, function) {
    override fun toString() = "FunctionCellTransformer[$function]"
}

class DerivedCellView internal constructor(
    val columnView: ColumnView,
    val index: Long,
    val cellHeight: Long,
    val cellWidth: Long,
    classes: PSet<String>,
    topics: PSet<String>
) : Iterable<DerivedCellView> {
    val tableView: TableView
        get() = columnView.tableView

    val cellView: CellView
        get() = columnView[index]

    // Note: This is assigned on init to preserve the derived nature of the class
    // TODO Should assign null on negative index?
    private val _cell: Cell<*>? = if (columnView.header.labels.isEmpty()) null
        else tableView[Table].let { it[columnView.header][index] }

    val cell: Cell<*>
        get() = _cell ?: throw InvalidCellException("Cell not available")

    val cellClasses: CellClasses<DerivedCellView> by lazy {
        CellClasses(this, classes)
    }

    val cellTopics: CellTopics<DerivedCellView> by lazy {
        CellTopics(this, topics)
    }

    override fun iterator(): Iterator<DerivedCellView> = cellView.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DerivedCellView

        if (columnView != other.columnView) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = columnView.hashCode()
        result = 31 * result + index.hashCode()
        return result
    }

    override fun toString() = "DerivedCellView[${columnView.header.labels.joinToString(limit = 30)}, $index]"
}

internal fun createDerivedCellViewFromRef(ref: TableViewRef, columnView: ColumnView, index: Long): DerivedCellView {
    val columnHeader = columnView.header
    val cellViewMeta = ref.cellViews[Pair(columnHeader, index)]
    val defaultCellViewMeta = ref.defaultCellView
    val columnViewMeta = ref.columnViews[columnHeader]
    val rowViewMeta = ref.rowViews[index]

    val height = cellViewMeta?.cellHeight
        ?: rowViewMeta?.cellHeight
        ?: defaultCellViewMeta.cellHeight
        ?: DEFAULT_CELL_HEIGHT

    val width = cellViewMeta?.cellWidth
        ?: columnViewMeta?.cellWidth
        ?: defaultCellViewMeta.cellWidth
        ?: DEFAULT_CELL_WIDTH

    val classes = (cellViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (columnViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (rowViewMeta?.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellClasses ?: EMPTY_IMMUTABLE_STRING_SET)

    val topics = (cellViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (columnViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (rowViewMeta?.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET) +
            (defaultCellViewMeta.cellTopics ?: EMPTY_IMMUTABLE_STRING_SET)

    return DerivedCellView(columnView, index, height, width, classes, topics)
}

class CellClasses<S> internal constructor(
    val source: S,
    internal val _classes: PSet<String>
) : Iterable<String> {
    val classes: SortedSet<String> by lazy {
        _classes.toSortedSet()
    }

    operator fun plus(topic: String): SortedSet<String> = (_classes + topic).toSortedSet()
    operator fun plus(topics: Collection<String>): SortedSet<String> = (topics.fold(this._classes) { acc, topic -> acc + topic }).toSortedSet()
    operator fun minus(topic: String): SortedSet<String> = (_classes - topic).toSortedSet()
    operator fun minus(topics: Collection<String>): SortedSet<String> = (topics.fold(this._classes) { acc, topic -> acc - topic }).toSortedSet()
    override fun iterator(): Iterator<String> = classes.iterator()

    operator fun invoke(newValue: Collection<String>?): Collection<String>? {
        when (source) {
            is TableView -> source[CellClasses] = newValue
            is ColumnView -> source[CellClasses] = newValue
            is RowView -> source[CellClasses] = newValue
            is CellView -> source[CellClasses] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: String?): String? {
        when (source) {
            is TableView -> source[CellClasses] = newValue
            is ColumnView -> source[CellClasses] = newValue
            is RowView -> source[CellClasses] = newValue
            is CellView -> source[CellClasses] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: CellClasses<*>?): CellClasses<*>? {
        when (source) {
            is TableView -> source[CellClasses] = newValue
            is ColumnView -> source[CellClasses] = newValue
            is RowView -> source[CellClasses] = newValue
            is CellView -> source[CellClasses] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        when (source) {
            is TableView -> source[CellClasses] = newValue
            is ColumnView -> source[CellClasses] = newValue
            is RowView -> source[CellClasses] = newValue
            is CellView -> source[CellClasses] = newValue
        }

        return newValue
    }

    operator fun contains(other: CellClasses<*>) = _classes.containsAll(other._classes)
    operator fun contains(other: String) = _classes.contains(other)
    operator fun contains(other: Collection<*>) = _classes.containsAll(other)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellClasses<*>

        if (source != other.source) return false
        if (_classes != other._classes) return false

        return true
    }

    override fun hashCode() = Objects.hash(this._classes)

    override fun toString() = "CellClasses[${classes.joinToString(limit = 30)}]"

    companion object
}

class CellTopics<S> internal constructor(
    val source: S,
    internal val _topics: PSet<String>
) : Iterable<String> {
    val topics: SortedSet<String> by lazy {
        _topics.toSortedSet()
    }

    operator fun plus(topic: String): SortedSet<String> = (_topics + topic).toSortedSet()
    operator fun plus(topics: Collection<String>): SortedSet<String> = (topics.fold(this._topics) { acc, topic -> acc + topic }).toSortedSet()
    operator fun minus(topic: String): SortedSet<String> = (_topics - topic).toSortedSet()
    operator fun minus(topics: Collection<String>): SortedSet<String> = (topics.fold(this._topics) { acc, topic -> acc - topic }).toSortedSet()
    override fun iterator(): Iterator<String> = topics.iterator()

    operator fun invoke(newValue: Collection<String>?): Collection<String>? {
        when (source) {
            is TableView -> source[CellTopics] = newValue
            is ColumnView -> source[CellTopics] = newValue
            is RowView -> source[CellTopics] = newValue
            is CellView -> source[CellTopics] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: String?): String? {
        when (source) {
            is TableView -> source[CellTopics] = newValue
            is ColumnView -> source[CellTopics] = newValue
            is RowView -> source[CellTopics] = newValue
            is CellView -> source[CellTopics] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: CellTopics<*>?): CellTopics<*>? {
        when (source) {
            is TableView -> source[CellTopics] = newValue
            is ColumnView -> source[CellTopics] = newValue
            is RowView -> source[CellTopics] = newValue
            is CellView -> source[CellTopics] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        when (source) {
            is TableView -> source[CellTopics] = newValue
            is ColumnView -> source[CellTopics] = newValue
            is RowView -> source[CellTopics] = newValue
            is CellView -> source[CellTopics] = newValue
        }

        return newValue
    }

    operator fun contains(other: CellTopics<*>) = _topics.containsAll(other._topics)
    operator fun contains(other: String) = _topics.contains(other)
    operator fun contains(other: Collection<*>) = _topics.containsAll(other)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellTopics<*>

        if (source != other.source) return false
        if (_topics != other._topics) return false

        return true
    }

    override fun hashCode() = Objects.hash(this._topics)

    override fun toString() = "CellTopics[${topics.joinToString(limit = 30)}]"

    companion object
}

sealed class CellHeight<S, T> {
    abstract val source: S
    abstract val height: T

    open val isNumeric: Boolean = false
    open val asLong: Long? = null

    operator fun plus(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> plus(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun plus(that: Number): Number {
        return when (that) {
            is Int -> plus(that)
            is Long -> plus(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun plus(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun plus(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun minus(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> minus(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun minus(that: Number): Number {
        return when (that) {
            is Int -> minus(that)
            is Long -> minus(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun minus(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun minus(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun times(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> times(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun times(that: Number): Number {
        return when (that) {
            is Int -> minus(that)
            is Long -> minus(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun times(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun times(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun div(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> div(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun div(that: Number): Number {
        return when (that) {
            is Int -> div(that)
            is Long -> div(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun div(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun div(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun rem(that: CellHeight<*, *>): Number {
        return when (that.height) {
            is Long -> rem(that.asLong ?: 0L)
            else -> throw InvalidCellHeightException("CellHeight not numeric at ${that.source}")
        }
    }

    operator fun rem(that: Number): Number {
        return when (that) {
            is Int -> rem(that)
            is Long -> rem(that)
            else -> throw InvalidCellHeightException("Unsupported type: ${that::class}")
        }
    }

    open operator fun rem(that: Int): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")
    open operator fun rem(that: Long): Number = throw InvalidCellHeightException("CellHeight not numeric at $source")

    operator fun invoke(newValue: CellHeight<*, *>?): CellHeight<*, *>? {
        when (val source = source) {
            is TableView -> source[CellHeight] = newValue
            is RowView -> source[CellHeight] = newValue
            is CellView -> source[CellHeight] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: Long?): Long? {
        when (val source = source) {
            is TableView -> source[CellHeight] = newValue
            is RowView -> source[CellHeight] = newValue
            is CellView -> source[CellHeight] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        when (val source = source) {
            is TableView -> source[CellHeight] = newValue
            is RowView -> source[CellHeight] = newValue
            is CellView -> source[CellHeight] = newValue
        }

        return newValue
    }

    operator fun contains(other: CellHeight<*, *>) = height == other.height
    operator fun contains(other: Int) = (height as? Long) == other.toLong()
    operator fun contains(other: Long) = (height as? Long) == other
    operator fun contains(other: Unit) = (height as? Unit) == other

    override fun hashCode() = Objects.hash(this.height)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellHeight<*, *>

        if (source != other.source) return false
        if (height != other.height) return false

        return true
    }

    companion object
}

class UnitCellHeight<S> internal constructor(
    override val source: S
) : CellHeight<S, Unit>() {
    override val height = Unit

    override fun toString() = "UnitCellHeight"
}

class PixelCellHeight<S> internal constructor(
    override val source: S,
    override val height: Long
) : CellHeight<S, Long>() {
    override val isNumeric = true

    override val asLong = height

    override fun plus(that: Int) = height + that

    override fun plus(that: Long) = height + that

    override fun minus(that: Int) = height - that

    override fun minus(that: Long) = height - that

    override fun times(that: Int) = height * that

    override fun times(that: Long) = height * that

    override fun div(that: Int) = height / that

    override fun div(that: Long) = height / that

    override fun rem(that: Int) = height % that

    override fun rem(that: Long) = height % that

    override fun toString() = "PixelCellHeight[$height]"
}

sealed class CellWidth<S, T> {
    abstract val source: S
    abstract val width: T

    open val isNumeric: Boolean = false
    open val asLong: Long? = null

    operator fun plus(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> plus(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun plus(that: Number): Number {
        return when (that) {
            is Int -> plus(that)
            is Long -> plus(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun plus(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun plus(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun minus(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> minus(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun minus(that: Number): Number {
        return when (that) {
            is Int -> minus(that)
            is Long -> minus(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun minus(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun minus(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun times(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> times(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun times(that: Number): Number {
        return when (that) {
            is Int -> minus(that)
            is Long -> minus(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun times(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun times(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun div(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> div(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun div(that: Number): Number {
        return when (that) {
            is Int -> div(that)
            is Long -> div(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun div(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun div(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun rem(that: CellWidth<*, *>): Number {
        return when (that.width) {
            is Long -> rem(that.asLong ?: 0L)
            else -> throw InvalidCellWidthException("CellWidth not numeric at ${that.source}")
        }
    }

    operator fun rem(that: Number): Number {
        return when (that) {
            is Int -> rem(that)
            is Long -> rem(that)
            else -> throw InvalidCellWidthException("Unsupported type: ${that::class}")
        }
    }

    open operator fun rem(that: Int): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")
    open operator fun rem(that: Long): Number = throw InvalidCellWidthException("CellWidth not numeric at $source")

    operator fun invoke(newValue: CellWidth<*, *>?): CellWidth<*, *>? {
        when (val source = source) {
            is TableView -> source[CellWidth] = newValue
            is ColumnView -> source[CellWidth] = newValue
            is CellView -> source[CellWidth] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: Long?): Long? {
        when (val source = source) {
            is TableView -> source[CellWidth] = newValue
            is ColumnView -> source[CellWidth] = newValue
            is CellView -> source[CellWidth] = newValue
        }

        return newValue
    }

    operator fun invoke(newValue: Unit?): Unit? {
        when (val source = source) {
            is TableView -> source[CellWidth] = newValue
            is ColumnView -> source[CellWidth] = newValue
            is CellView -> source[CellWidth] = newValue
        }

        return newValue
    }

    operator fun contains(other: CellWidth<*, *>) = width == other.width
    operator fun contains(other: Int) = (width as? Long) == other.toLong()
    operator fun contains(other: Long) = (width as? Long) == other
    operator fun contains(other: Unit) = (width as? Unit) == other

    override fun hashCode() = Objects.hash(this.width)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CellWidth<*, *>

        if (source != other.source) return false
        if (width != other.width) return false

        return true
    }

    companion object
}

class UnitCellWidth<S> internal constructor(
    override val source: S
) : CellWidth<S, Unit>() {
    override val width = Unit

    override fun toString() = "UnitCellWidth"
}

class PixelCellWidth<S> internal constructor(
    override val source: S,
    override val width: Long
) : CellWidth<S, Long>() {
    override val isNumeric = true

    override val asLong = width

    override fun plus(that: Int) = width + that

    override fun plus(that: Long) = width + that

    override fun minus(that: Int) = width - that

    override fun minus(that: Long) = width - that

    override fun times(that: Int) = width * that

    override fun times(that: Long) = width * that

    override fun div(that: Int) = width / that

    override fun div(that: Long) = width / that

    override fun rem(that: Int) = width % that

    override fun rem(that: Long) = width % that

    override fun toString() = "PixelCellWidth[$width]"
}