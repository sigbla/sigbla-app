package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.Registry
import sigbla.app.internals.TableEventProcessor
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import com.github.andrewoma.dexx.collection.HashMap as PHashMap
import com.github.andrewoma.dexx.collection.Map as PMap
import com.github.andrewoma.dexx.collection.SortedMap as PSortedMap
import com.github.andrewoma.dexx.collection.TreeMap as PTreeMap

abstract class Table(val name: String) : Iterable<Cell<*>> {
    @Volatile
    var closed: Boolean = false
        internal set

    val table: Table
        get() = this

    internal val columnCounter = AtomicInteger()

    abstract val headers: Collection<ColumnHeader>

    abstract val columns: Collection<Column>

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

    operator fun get(indexRelation: IndexRelation, index: Int): Row =
        BaseRow(this, indexRelation, index.toLong())

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
        return this[cell.column.columnHeader][cell.index]
    }

    operator fun get(column: Column): Column {
        return this[column.columnHeader]
    }

    operator fun get(cellRange: CellRange): CellRange {
        return CellRange(this[cellRange.start], this[cellRange.endInclusive], cellRange.order)
    }

    // TODO Row related

    operator fun get(table: Table): Table {
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

    operator fun set(cell: Cell<*>, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(cell).init()

    // -----

    operator fun set(header1: String, index: Long, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1][index]).init()

    operator fun set(header1: String, header2: String, index: Long, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1, header2][index]).init()

    operator fun set(header1: String, header2: String, header3: String, index: Long, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1, header2, header3][index]).init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1, header2, header3, header4][index]).init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1, header2, header3, header4, header5][index]).init()

    // -----

    operator fun set(header1: String, index: Int, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1][index]).init()

    operator fun set(header1: String, header2: String, index: Int, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1, header2][index]).init()

    operator fun set(header1: String, header2: String, header3: String, index: Int, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1, header2, header3][index]).init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1, header2, header3, header4][index]).init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, init: DestinationOsmosis<Cell<*>>.() -> Unit) = DestinationOsmosis(this[header1, header2, header3, header4, header5][index]).init()

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
        // TODO This needs to work with the ref snapshot
        return object : Iterator<Cell<*>> {
            private val columnIterator = columns.iterator()
            private var cellIterator = nextCellIterator()

            private fun nextCellIterator(): Iterator<Cell<*>> {
                while (columnIterator.hasNext()) {
                    val itr = columnIterator.next().iterator()
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

    internal abstract fun makeClone(name: String = table.name, onRegistry: Boolean = false, ref: TableRef = tableRef.get()!!): Table

    override fun toString(): String {
        return "Table[$name]"
    }

    companion object {
        // TODO Consider option for anonymous tables with no name?
        operator fun get(name: String): Table = BaseTable(name)

        fun fromRegistry(name: String): Table = Registry.getTable(name) ?: throw InvalidTableException("No table by name $name")

        fun fromStorage(storage: Storage, name: String): Table {
            TODO()
        }

        fun fromStorageAs(storage: Storage, name: String, newName: String): Table {
            TODO()
        }

        fun fromStorageRange(storage: Storage, name: String, fromIndex: Long, toIndex: Long): Table {
            TODO()
        }

        fun fromStorageRangeAs(storage: Storage, name: String, fromIndex: Long, toIndex: Long, newName: String): Table {
            TODO()
        }

        val names: SortedSet<String> get() = Registry.tableNames()

        fun delete(name: String) = Registry.deleteTable(name)
    }
}

internal data class TableRef(
    val columnsMap: PMap<ColumnHeader, Column> = PHashMap(),
    val columnCellMap: PMap<Column, PSortedMap<Long, CellValue<*>>> = PHashMap(),
    val version: Long = Long.MIN_VALUE
)

class BaseTable internal constructor(
    name: String,
    onRegistry: Boolean = true,
    override val tableRef: AtomicReference<TableRef> = AtomicReference(TableRef()),
    override val eventProcessor: TableEventProcessor = TableEventProcessor()
) : Table(name) {
    init {
        if (onRegistry) Registry.setTable(name, this)
    }

    override val headers: Collection<ColumnHeader>
        get() = tableRef.get().columnsMap.asSequence()
            .sortedBy { it.component2().columnOrder }
            .map { it.component1() }
            .toList()

    override val columns: Collection<Column>
        get() = tableRef.get().columnsMap.values().asSequence()
            .sortedBy { it.columnOrder }
            .toList()

    // TODO Column add event
    override fun get(header: ColumnHeader): Column {
        if (closed) throw InvalidTableException("Table is closed")
        if (header.header.isEmpty()) throw InvalidColumnException("Empty header")

        return tableRef.get().columnsMap[header] ?: tableRef.updateAndGet {
            if (it.columnsMap.containsKey(header)) return@updateAndGet it

            val column = BaseColumn(this, header, tableRef)

            it.copy(
                columnsMap = it.columnsMap.put(header, column),
                columnCellMap = it.columnCellMap.put(column, PTreeMap()),
                version = it.version + 1L
            )
        }.columnsMap[header] ?: throw InvalidColumnException(header)
    }

    override fun contains(header: ColumnHeader): Boolean = tableRef.get().columnsMap.containsKey(header)

    override fun makeClone(name: String, onRegistry: Boolean, ref: TableRef): Table {
        val newTableRef = AtomicReference(ref)
        val tableClone = BaseTable(name, onRegistry, newTableRef)

        // TODO Consider if we can optimize this by making it lazy or something else?
        //      It might be fine doing it like this if we also offer the batch update feature
        val newColumnsMap = ref.columnsMap.fold(PHashMap<ColumnHeader, Column>()) { acc, chc ->
            acc.put(chc.component1(), BaseColumn(tableClone, chc.component1(), newTableRef))
        }

        // TODO Consider if we can optimize this by making it lazy or something else?
        //      It might be fine doing it like this if we also offer the batch update feature
        val newColumnCellMap = ref.columnCellMap.fold(PHashMap<Column, PSortedMap<Long, CellValue<*>>>()) { acc, ccm ->
            acc.put(newColumnsMap[ccm.component1().columnHeader] ?: throw InvalidColumnException(ccm.component1()), ccm.component2())
        }

        newTableRef.set(TableRef(
            newColumnsMap,
            newColumnCellMap,
            ref.version
        ))

        return tableClone
    }

    companion object
}
