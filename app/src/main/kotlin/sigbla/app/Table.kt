package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.RefHolder
import sigbla.app.internals.Registry
import sigbla.app.internals.TableEventProcessor
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.atomic.AtomicLong
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

    internal abstract val tableRef: RefHolder<TableRef>

    internal abstract val eventProcessor: TableEventProcessor

    abstract operator fun get(header: ColumnHeader): Column

    operator fun get(vararg header: String): Column = get(
        ColumnHeader(
            *header
        )
    )

    operator fun get(index: Long): Row = get(IndexRelation.AT, index)

    operator fun get(indexRelation: IndexRelation, index: Long): Row = BaseRow(this, indexRelation, index)

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

    operator fun get(cell: Cell<*>): Cell<*> = this[cell.column.header][cell.index]

    operator fun get(column: Column): Column = this[column.header]

    operator fun get(cellRange: CellRange): CellRange = CellRange(this[cellRange.start], this[cellRange.endInclusive], cellRange.order)

    operator fun get(column: Column, index: Long) = this[column][index]

    operator fun get(column: Column, index: Int) = this[column][index]

    operator fun get(columnHeader: ColumnHeader, index: Long) = this[columnHeader][index]

    operator fun get(columnHeader: ColumnHeader, index: Int) = this[columnHeader][index]

    operator fun get(index: Long, column: Column) = this[column][index]

    operator fun get(index: Int, column: Column) = this[column][index]

    operator fun get(index: Long, columnHeader: ColumnHeader) = this[columnHeader][index]

    operator fun get(index: Int, columnHeader: ColumnHeader) = this[columnHeader][index]

    operator fun get(column: Column, row: Row) = this[column][row]

    operator fun get(columnHeader: ColumnHeader, row: Row) = this[columnHeader][row]

    operator fun get(row: Row, column: Column) = this[column][row]

    operator fun get(row: Row, columnHeader: ColumnHeader) = this[columnHeader][row]

    // TODO Check for missing shorthand convenience functions on get/set

    // TODO Consider if get(table: Table) and set(..: Table, ..) should be included for symmetry?

    // TODO Include set as well, which will, like with TableView, copy over all the data

    // TODO Add a set(column: Column) = Column to copy over a whole column efficiently

    operator fun get(table: Table.Companion): Table {
        return this
    }

    // -----

    operator fun set(column: Column, index: Long, value: Cell<*>?) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Long, value: String) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Long, value: Long) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Long, value: Double) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Long, value: BigInteger) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Long, value: BigDecimal) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Long, value: Number) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Long, init: Cell<*>.() -> Any?) = this[column][index] { init() }

    // -----

    operator fun set(column: Column, index: Int, value: Cell<*>?) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Int, value: String) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Int, value: Long) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Int, value: Double) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Int, value: BigInteger) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Int, value: BigDecimal) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Int, value: Number) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Int, init: Cell<*>.() -> Any?) = this[column][index] { init() }

    // -----

    operator fun set(columnHeader: ColumnHeader, index: Long, value: Cell<*>?) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Long, value: String) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Long, value: Long) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Long, value: Double) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Long, value: BigInteger) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Long, value: BigDecimal) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Long, value: Number) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Long, init: Cell<*>.() -> Any?) = this[columnHeader][index] { init() }

    // -----

    operator fun set(columnHeader: ColumnHeader, index: Int, value: Cell<*>?) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Int, value: String) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Int, value: Long) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Int, value: Double) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Int, value: BigInteger) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Int, value: BigDecimal) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Int, value: Number) {
        this[columnHeader][index] = value
    }

    operator fun set(columnHeader: ColumnHeader, index: Int, init: Cell<*>.() -> Any?) = this[columnHeader][index] { init() }

    // -----

    operator fun set(column: Column, row: Row, value: Cell<*>?) {
        this[column][row] = value
    }

    operator fun set(column: Column, row: Row, value: String) {
        this[column][row] = value
    }

    operator fun set(column: Column, row: Row, value: Long) {
        this[column][row] = value
    }

    operator fun set(column: Column, row: Row, value: Double) {
        this[column][row] = value
    }

    operator fun set(column: Column, row: Row, value: BigInteger) {
        this[column][row] = value
    }

    operator fun set(column: Column, row: Row, value: BigDecimal) {
        this[column][row] = value
    }

    operator fun set(column: Column, row: Row, value: Number) {
        this[column][row] = value
    }

    operator fun set(column: Column, row: Row, init: Cell<*>.() -> Any?) = this[column][row] { init() }

    // -----

    operator fun set(columnHeader: ColumnHeader, row: Row, value: Cell<*>?) {
        this[columnHeader][row] = value
    }

    operator fun set(columnHeader: ColumnHeader, row: Row, value: String) {
        this[columnHeader][row] = value
    }

    operator fun set(columnHeader: ColumnHeader, row: Row, value: Long) {
        this[columnHeader][row] = value
    }

    operator fun set(columnHeader: ColumnHeader, row: Row, value: Double) {
        this[columnHeader][row] = value
    }

    operator fun set(columnHeader: ColumnHeader, row: Row, value: BigInteger) {
        this[columnHeader][row] = value
    }

    operator fun set(columnHeader: ColumnHeader, row: Row, value: BigDecimal) {
        this[columnHeader][row] = value
    }

    operator fun set(columnHeader: ColumnHeader, row: Row, value: Number) {
        this[columnHeader][row] = value
    }

    operator fun set(columnHeader: ColumnHeader, row: Row, init: Cell<*>.() -> Any?) = this[columnHeader][row] { init() }

    // -----

    operator fun set(index: Long, column: Column, value: Cell<*>?) {
        this[column][index] = value
    }

    operator fun set(index: Long, column: Column, value: String) {
        this[column][index] = value
    }

    operator fun set(index: Long, column: Column, value: Long) {
        this[column][index] = value
    }

    operator fun set(index: Long, column: Column, value: Double) {
        this[column][index] = value
    }

    operator fun set(index: Long, column: Column, value: BigInteger) {
        this[column][index] = value
    }

    operator fun set(index: Long, column: Column, value: BigDecimal) {
        this[column][index] = value
    }

    operator fun set(index: Long, column: Column, value: Number) {
        this[column][index] = value
    }

    operator fun set(index: Long, column: Column, init: Cell<*>.() -> Any?) = this[column][index] { init() }

    // -----

    operator fun set(index: Int, column: Column, value: Cell<*>?) {
        this[column][index] = value
    }

    operator fun set(index: Int, column: Column, value: String) {
        this[column][index] = value
    }

    operator fun set(index: Int, column: Column, value: Long) {
        this[column][index] = value
    }

    operator fun set(index: Int, column: Column, value: Double) {
        this[column][index] = value
    }

    operator fun set(index: Int, column: Column, value: BigInteger) {
        this[column][index] = value
    }

    operator fun set(index: Int, column: Column, value: BigDecimal) {
        this[column][index] = value
    }

    operator fun set(index: Int, column: Column, value: Number) {
        this[column][index] = value
    }

    operator fun set(index: Int, column: Column, init: Cell<*>.() -> Any?) = this[column][index] { init() }

    // -----

    operator fun set(index: Long, columnHeader: ColumnHeader, value: Cell<*>?) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Long, columnHeader: ColumnHeader, value: String) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Long, columnHeader: ColumnHeader, value: Long) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Long, columnHeader: ColumnHeader, value: Double) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Long, columnHeader: ColumnHeader, value: BigInteger) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Long, columnHeader: ColumnHeader, value: BigDecimal) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Long, columnHeader: ColumnHeader, value: Number) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Long, columnHeader: ColumnHeader, init: Cell<*>.() -> Any?) = this[columnHeader][index] { init() }

    // -----

    operator fun set(index: Int, columnHeader: ColumnHeader, value: Cell<*>?) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Int, columnHeader: ColumnHeader, value: String) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Int, columnHeader: ColumnHeader, value: Long) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Int, columnHeader: ColumnHeader, value: Double) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Int, columnHeader: ColumnHeader, value: BigInteger) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Int, columnHeader: ColumnHeader, value: BigDecimal) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Int, columnHeader: ColumnHeader, value: Number) {
        this[columnHeader][index] = value
    }

    operator fun set(index: Int, columnHeader: ColumnHeader, init: Cell<*>.() -> Any?) = this[columnHeader][index] { init() }

    // -----

    operator fun set(row: Row, column: Column, value: Cell<*>?) {
        this[column][row] = value
    }

    operator fun set(row: Row, column: Column, value: String) {
        this[column][row] = value
    }

    operator fun set(row: Row, column: Column, value: Long) {
        this[column][row] = value
    }

    operator fun set(row: Row, column: Column, value: Double) {
        this[column][row] = value
    }

    operator fun set(row: Row, column: Column, value: BigInteger) {
        this[column][row] = value
    }

    operator fun set(row: Row, column: Column, value: BigDecimal) {
        this[column][row] = value
    }

    operator fun set(row: Row, column: Column, value: Number) {
        this[column][row] = value
    }

    operator fun set(row: Row, column: Column, init: Cell<*>.() -> Any?) = this[column][row] { init() }

    // -----

    operator fun set(row: Row, columnHeader: ColumnHeader, value: Cell<*>?) {
        this[columnHeader][row] = value
    }

    operator fun set(row: Row, columnHeader: ColumnHeader, value: String) {
        this[columnHeader][row] = value
    }

    operator fun set(row: Row, columnHeader: ColumnHeader, value: Long) {
        this[columnHeader][row] = value
    }

    operator fun set(row: Row, columnHeader: ColumnHeader, value: Double) {
        this[columnHeader][row] = value
    }

    operator fun set(row: Row, columnHeader: ColumnHeader, value: BigInteger) {
        this[columnHeader][row] = value
    }

    operator fun set(row: Row, columnHeader: ColumnHeader, value: BigDecimal) {
        this[columnHeader][row] = value
    }

    operator fun set(row: Row, columnHeader: ColumnHeader, value: Number) {
        this[columnHeader][row] = value
    }

    operator fun set(row: Row, columnHeader: ColumnHeader, init: Cell<*>.() -> Any?) = this[columnHeader][row] { init() }

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

    infix fun at(index: Long) = get(IndexRelation.AT, index)

    infix fun atOrBefore(index: Long) = get(IndexRelation.AT_OR_BEFORE, index)

    infix fun atOrAfter(index: Long) = get(IndexRelation.AT_OR_AFTER, index)

    infix fun before(index: Long) = get(IndexRelation.BEFORE, index)

    infix fun after(index: Long) = get(IndexRelation.AFTER, index)

    infix fun at(index: Int) = get(IndexRelation.AT, index)

    infix fun atOrBefore(index: Int) = get(IndexRelation.AT_OR_BEFORE, index)

    infix fun atOrAfter(index: Int) = get(IndexRelation.AT_OR_AFTER, index)

    infix fun before(index: Int) = get(IndexRelation.BEFORE, index)

    infix fun after(index: Int) = get(IndexRelation.AFTER, index)

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

    abstract operator fun <R> invoke(batch: Table.() -> R): R

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

// TODO Consider if this shouldn't be a data class?
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
    override val tableRef: RefHolder<TableRef> = RefHolder(TableRef()),
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

    override operator fun <R> invoke(batch: Table.() -> R): R {
        synchronized(eventProcessor) {
            if (eventProcessor.pauseEvents()) {
                try {
                    tableRef.useLocal()
                    val r = this.batch()
                    eventProcessor.publish(true)
                    tableRef.commitLocal()
                    return r
                } finally {
                    eventProcessor.clearBuffer()
                    tableRef.clearLocal()
                }
            } else {
                return this.batch()
            }
        }
    }

    override fun makeClone(name: String?, onRegistry: Boolean, ref: TableRef): Table = BaseTable(name, this, onRegistry, RefHolder(ref))

    companion object
}
