package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.Registry
import sigbla.app.internals.TableEventProcessor
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.collection.Map as PMap
import com.github.andrewoma.dexx.collection.SortedMap as PSortedMap
import com.github.andrewoma.dexx.collection.TreeMap as PTreeMap

// TODO Should this be sealed rather than abstract? Or just a normal class with no BaseTable?
abstract class Table(val name: String?, internal val source: Table?) : Iterable<Cell<*>> {
    @Volatile
    var closed: Boolean = false
        internal set

    abstract val headers: Sequence<ColumnHeader>

    abstract val columns: Sequence<Column>

    abstract val indexes: Sequence<Long>

    internal abstract val tableRef: AtomicReference<TableRef>

    internal abstract val eventProcessor: TableEventProcessor

    abstract operator fun get(header: ColumnHeader): Column

    operator fun get(vararg header: String): Column = get(
        ColumnHeader(
            *header
        )
    )

    operator fun get(index: Long): Row = get(IndexRelation.AT, index)

    operator fun get(indexRelation: IndexRelation, index: Long): Row =
        BaseRow(this, indexRelation, index)

    operator fun get(header1: String, index: Long): Cell<*> = this[header1][index]

    operator fun get(header1: String, header2: String, index: Long): Cell<*> = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Long): Cell<*> = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Long): Cell<*> = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long): Cell<*> = this[header1, header2, header3, header4, header5][index]

    operator fun get(header1: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1][indexRelation, index]

    operator fun get(header1: String, header2: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2, header3][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2, header3, header4][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, indexRelation: IndexRelation, index: Long): Cell<*> = this[header1, header2, header3, header4, header5][indexRelation, index]

    operator fun get(index: Int): Row = get(IndexRelation.AT, index)

    operator fun get(row: Row): Row = get(row.indexRelation, row.index)

    operator fun get(indexRelation: IndexRelation, index: Int): Row = BaseRow(this, indexRelation, index.toLong())

    operator fun get(header1: String, index: Int): Cell<*> = this[header1][index]

    operator fun get(header1: String, header2: String, index: Int): Cell<*> = this[header1, header2][index]

    operator fun get(header1: String, header2: String, header3: String, index: Int): Cell<*> = this[header1, header2, header3][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, index: Int): Cell<*> = this[header1, header2, header3, header4][index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int): Cell<*> = this[header1, header2, header3, header4, header5][index]

    operator fun get(header1: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1][indexRelation, index]

    operator fun get(header1: String, header2: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2, header3][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2, header3, header4][indexRelation, index]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, indexRelation: IndexRelation, index: Int): Cell<*> = this[header1, header2, header3, header4, header5][indexRelation, index]

    // -----

    operator fun get(cell: Cell<*>): Cell<*> {
        return this[cell.column.header][cell.index]
    }

    operator fun get(column: Column): Column {
        return this[column.header]
    }

    operator fun get(cellRange: CellRange): CellRange {
        return CellRange(this[cellRange.start], this[cellRange.endInclusive], cellRange.order)
    }

    // TODO Row related

    // TODO get(columnHeader/column, index)

    // TODO get(index, columnHeader/column)

    // TODO Consider if get(table: Table) and set(..: Table, ..) should be included for symmetry?

    // TODO Include set as well, which will, like with TableView, copy over all the data

    // TODO Add a set(column: Column) = Column to copy over a whole column efficiently

    operator fun get(table: Table.Companion): Table {
        return this
    }

    // -----

    operator fun set(cell: Cell<*>, value: Cell<*>?) {
        this[cell.column][cell.index] = value
    }

    operator fun set(cell: Cell<*>, value: String) {
        this[cell.column][cell.index] = value
    }

    operator fun set(cell: Cell<*>, value: Long) {
        this[cell.column][cell.index] = value
    }

    operator fun set(cell: Cell<*>, value: Double) {
        this[cell.column][cell.index] = value
    }

    operator fun set(cell: Cell<*>, value: BigInteger) {
        this[cell.column][cell.index] = value
    }

    operator fun set(cell: Cell<*>, value: BigDecimal) {
        this[cell.column][cell.index] = value
    }

    operator fun set(cell: Cell<*>, value: Number) {
        this[cell.column][cell.index] = value
    }

    operator fun set(cell: Cell<*>, init: Cell<*>.() -> Any?) = this[cell.column][cell.index] { init() }

    // -----

    // TODO Can't we just replace these and similar with vararg?
    operator fun set(header1: String, index: Long, init: Cell<*>.() -> Any?) = this[header1][index] { init() }

    operator fun set(header1: String, header2: String, index: Long, init: Cell<*>.() -> Any?) = this[header1, header2][index] { init() }

    operator fun set(header1: String, header2: String, header3: String, index: Long, init: Cell<*>.() -> Any?) = this[header1, header2, header3][index] { init() }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, init: Cell<*>.() -> Any?) = this[header1, header2, header3, header4][index] { init() }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, init: Cell<*>.() -> Any?) = this[header1, header2, header3, header4, header5][index] { init() }

    // -----

    operator fun set(header1: String, index: Int, init: Cell<*>.() -> Any?) = this[header1][index] { init() }

    operator fun set(header1: String, header2: String, index: Int, init: Cell<*>.() -> Any?) = this[header1, header2][index] { init() }

    operator fun set(header1: String, header2: String, header3: String, index: Int, init: Cell<*>.() -> Any?) = this[header1, header2, header3][index] { init() }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, init: Cell<*>.() -> Any?) = this[header1, header2, header3, header4][index] { init() }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, init: Cell<*>.() -> Any?) = this[header1, header2, header3, header4, header5][index] { init() }

    // -----

    operator fun set(header1: String, index: Long, value: Cell<*>?) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: Cell<*>?) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: Cell<*>?) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: Cell<*>?) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: Cell<*>?) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: String) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: String) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: String) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: String) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: String) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: Long) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: Long) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: Long) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: Long) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: Long) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: Double) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: Double) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: Double) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: Double) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: Double) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: BigInteger) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: BigInteger) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: BigInteger) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: BigInteger) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: BigInteger) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: BigDecimal) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: BigDecimal) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: BigDecimal) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: BigDecimal) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: BigDecimal) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Long, value: Number) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: Number) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: Number) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: Number) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: Number) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: Cell<*>?) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: Cell<*>?) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: Cell<*>?) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: Cell<*>?) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: Cell<*>?) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: String) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: String) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: String) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: String) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: String) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: Long) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: Long) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: Long) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: Long) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: Long) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: Double) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: Double) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: Double) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: Double) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: Double) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: BigInteger) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: BigInteger) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: BigInteger) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: BigInteger) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: BigInteger) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: BigDecimal) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: BigDecimal) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: BigDecimal) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: BigDecimal) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: BigDecimal) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    operator fun set(header1: String, index: Int, value: Number) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: Number) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: Number) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: Number) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: Number) {
        this[header1, header2, header3, header4, header5][index] = value
    }

    // -----

    // TODO Consider if we want this
    abstract operator fun contains(header: ColumnHeader): Boolean

    fun contains(vararg header: String): Boolean = contains(ColumnHeader(*header))

    override fun iterator(): Iterator<Cell<*>> {
        val ref = tableRef.get()
        val columnIterator = ref.headers.map { BaseColumn(this, it.first, it.second.columnOrder) }.iterator()

        return object : Iterator<Cell<*>> {
            private var cellIterator = nextCellIterator()

            private fun nextCellIterator(): Iterator<Cell<*>> {
                while (columnIterator.hasNext()) {
                    val column = columnIterator.next()
                    val values = ref.columnCells[column.header] ?: throw InvalidColumnException(column.header)
                    val itr = values.asSequence().map { it.component2().toCell(column, it.component1()) }.iterator()
                    if (itr.hasNext()) return itr
                }

                return emptyList<Cell<*>>().iterator()
            }

            override fun hasNext(): Boolean {
                if (cellIterator.hasNext()) return true
                cellIterator = nextCellIterator()
                return cellIterator.hasNext()
            }

            override fun next(): Cell<*> = cellIterator.next()
        }
    }

    internal abstract fun makeClone(name: String? = this.name, onRegistry: Boolean = false, ref: TableRef = tableRef.get()!!): Table

    override fun toString(): String {
        return "Table[$name]"
    }

    override fun equals(other: Any?): Boolean {
        // Implemented to ensure expected equality check to always just be a reference compare, that's what we want
        return this === other
    }

    companion object {
        operator fun get(name: String?): Table = BaseTable(name, null)

        fun fromRegistry(name: String): Table = Registry.getTable(name) ?: throw InvalidTableException("No table by name $name")

        fun fromRegistry(name: String, init: (name: String) -> Table) = Registry.getTable(name, init)

        val names: SortedSet<String> get() = Registry.tableNames()

        fun delete(name: String) = Registry.deleteTable(name)
    }
}

internal data class ColumnMeta(
    val columnOrder: Long,
    val prenatal: Boolean
)

internal data class TableRef(
    val columns: PMap<ColumnHeader, ColumnMeta> = PHashMap(),
    val columnCells: PMap<ColumnHeader, PSortedMap<Long, CellValue<*>>> = PHashMap(),
    val version: Long = Long.MIN_VALUE,
    val columnCounter: AtomicLong = AtomicLong(Long.MIN_VALUE)
) {
    val headers: Sequence<Pair<ColumnHeader, ColumnMeta>> by lazy {
        columns
            .asSequence()
            .filter { !it.component2().prenatal }
            .map { it.component1() to it.component2() }
            .sortedBy { it.component2().columnOrder }
    }

    val indexes: Sequence<Long> by lazy {
        columns
            .asSequence()
            .filter { !it.component2().prenatal }
            .map { it.component1() }
            .fold(TreeSet<Long>()) { acc, column ->
                acc.addAll(columnCells[column]?.keys() ?: emptyList())
                acc
            }
            .asSequence()
    }
}

class BaseTable internal constructor(
    name: String?,
    source: Table?,
    onRegistry: Boolean = true,
    override val tableRef: AtomicReference<TableRef> = AtomicReference(TableRef()),
    override val eventProcessor: TableEventProcessor = TableEventProcessor()
) : Table(name, source) {
    init {
        if (name != null && onRegistry) Registry.setTable(name, this)
    }

    override val headers: Sequence<ColumnHeader>
        get() = tableRef.get().headers.filter { !it.second.prenatal }.map { it.first }

    override val columns: Sequence<Column>
        get() = tableRef.get().headers.filter { !it.second.prenatal }.map { BaseColumn(this, it.first, it.second.columnOrder) }

    override val indexes: Sequence<Long>
        get() = tableRef.get().indexes

    override fun get(header: ColumnHeader): Column {
        if (closed) throw InvalidTableException("Table is closed")
        if (header.labels.isEmpty()) throw InvalidColumnException("Empty header")

        val columnMeta = tableRef.get().columns[header] ?: tableRef.updateAndGet {
            if (it.columns.containsKey(header)) return@updateAndGet it

            val columnMeta = ColumnMeta(tableRef.get().columnCounter.getAndIncrement(), true)

            it.copy(
                columns = it.columns.put(header, columnMeta),
                columnCells = it.columnCells.put(header, PTreeMap()),
                version = it.version + 1L
            )
        }.columns[header] ?: throw InvalidColumnException(header)

        return BaseColumn(this, header, columnMeta.columnOrder)
    }

    override fun contains(header: ColumnHeader): Boolean = tableRef.get().columns[header]?.prenatal == false

    override fun makeClone(name: String?, onRegistry: Boolean, ref: TableRef): Table = BaseTable(name, this, onRegistry, AtomicReference(ref))

    companion object
}
