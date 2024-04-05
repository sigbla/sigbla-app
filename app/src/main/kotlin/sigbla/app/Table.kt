/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import sigbla.app.exceptions.InvalidColumnException
import sigbla.app.exceptions.InvalidTableException
import sigbla.app.internals.RefHolder
import sigbla.app.internals.Registry
import sigbla.app.internals.TableEventProcessor
import sigbla.app.pds.collection.HashMap as PHashMap
import sigbla.app.pds.collection.Map as PMap
import sigbla.app.pds.collection.SortedMap as PSortedMap
import sigbla.app.pds.collection.TreeMap as PTreeMap
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class Table internal constructor(
    val name: String?,
    val source: Table?,
    internal val tableRef: RefHolder<TableRef> = RefHolder(TableRef()),
    internal val eventProcessor: TableEventProcessor = TableEventProcessor()
) : Iterable<Cell<*>> {
    val closed: Boolean get() = tableRef.closed

    val headers: Sequence<Header>
        get() = tableRef.get().headers.filter { !it.second.prenatal }.map { it.first }

    val columns: Sequence<Column>
        get() = tableRef.get().headers.filter { !it.second.prenatal }.map { Column(this, it.first, it.second.columnOrder) }

    val indexes: Sequence<Long>
        get() = tableRef.get().indexes

    operator fun get(header: Header): Column {
        if (header.labels.isEmpty()) throw InvalidColumnException("Empty header")

        val columnMeta = tableRef.get().columns[header] ?: tableRef.updateAndGet {
            if (it.columns.containsKey(header)) return@updateAndGet it

            val columnMeta = ColumnMeta(tableRef.get().columnCounter.getAndIncrement(), true)

            it.copy(
                columns = it.columns.put(header, columnMeta),
                columnCells = it.columnCells.put(header, PTreeMap()),
                version = it.version + 1L
            )
        }.columns[header] ?: throw InvalidColumnException("Unable to find column meta for header $header")

        return Column(this, header, columnMeta.columnOrder)
    }

    operator fun get(vararg header: String): Column = get(
        Header(
            *header
        )
    )

    operator fun get(index: Long): Row = get(IndexRelation.AT, index)

    operator fun get(indexRelation: IndexRelation, index: Long): Row = Row(this, indexRelation, index)

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

    operator fun get(indexRelation: IndexRelation, index: Int): Row = Row(this, indexRelation, index.toLong())

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

    operator fun get(header1: String, row: Row): Cell<*> = this[header1][row]

    operator fun get(header1: String, header2: String, row: Row): Cell<*> = this[header1, header2][row]

    operator fun get(header1: String, header2: String, header3: String, row: Row): Cell<*> = this[header1, header2, header3][row]

    operator fun get(header1: String, header2: String, header3: String, header4: String, row: Row): Cell<*> = this[header1, header2, header3, header4][row]

    operator fun get(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row): Cell<*> = this[header1, header2, header3, header4, header5][row]

    // -----

    operator fun get(cell: Cell<*>): Cell<*> = this[cell.column.header][cell.index]

    operator fun get(column: Column): Column = this[column.header]

    operator fun get(cellRange: CellRange): CellRange {
        val startColumn = Column(this, cellRange.start.column.header)
        val endInclusiveColumn = Column(this, cellRange.endInclusive.column.header)
        return CellRange(startColumn[cellRange.start.index], endInclusiveColumn[cellRange.endInclusive.index], cellRange.order)
    }

    operator fun get(column: Column, index: Long) = this[column][index]

    operator fun get(column: Column, index: Int) = this[column][index]

    operator fun get(header: Header, index: Long) = this[header][index]

    operator fun get(header: Header, index: Int) = this[header][index]

    operator fun get(column: Column, indexRelation: IndexRelation, index: Long) = this[column][indexRelation, index]

    operator fun get(column: Column, indexRelation: IndexRelation, index: Int) = this[column][indexRelation, index]

    operator fun get(header: Header, indexRelation: IndexRelation, index: Long) = this[header][indexRelation, index]

    operator fun get(header: Header, indexRelation: IndexRelation, index: Int) = this[header][indexRelation, index]

    operator fun get(column: Column, row: Row) = this[column][row]

    operator fun get(header: Header, row: Row) = this[header][row]

    // TODO Consider if get(table: Table) and set(..: Table, ..) should be included for symmetry?

    // TODO Include set as well, which will, like with TableView, copy over all the data

    // TODO Add a set(column: Column) = Column to copy over a whole column efficiently

    /*
    // TODO?
    operator fun get(table: Table.Companion): Table {
        return this
    }
     */

    // -----

    operator fun set(column: Column, index: Long, value: Cell<*>?) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Long, value: Boolean) {
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

    operator fun set(column: Column, index: Long, init: Cell<*>.() -> Unit) = this[column][index].init()

    // -----

    operator fun set(column: Column, index: Int, value: Cell<*>?) {
        this[column][index] = value
    }

    operator fun set(column: Column, index: Int, value: Boolean) {
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

    operator fun set(column: Column, index: Int, init: Cell<*>.() -> Unit) = this[column][index].init()

    // -----

    operator fun set(header: Header, index: Long, value: Cell<*>?) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Long, value: Boolean) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Long, value: String) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Long, value: Long) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Long, value: Double) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Long, value: BigInteger) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Long, value: BigDecimal) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Long, value: Number) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Long, init: Cell<*>.() -> Unit) = this[header][index].init()

    // -----

    operator fun set(header: Header, index: Int, value: Cell<*>?) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Int, value: Boolean) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Int, value: String) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Int, value: Long) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Int, value: Double) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Int, value: BigInteger) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Int, value: BigDecimal) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Int, value: Number) {
        this[header][index] = value
    }

    operator fun set(header: Header, index: Int, init: Cell<*>.() -> Unit) = this[header][index].init()

    // -----

    operator fun set(column: Column, row: Row, value: Cell<*>?) {
        this[column][row] = value
    }

    operator fun set(column: Column, row: Row, value: Boolean) {
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

    operator fun set(column: Column, row: Row, init: Cell<*>.() -> Unit) = this[column][row].init()

    // -----

    operator fun set(header: Header, row: Row, value: Cell<*>?) {
        this[header][row] = value
    }

    operator fun set(header: Header, row: Row, value: Boolean) {
        this[header][row] = value
    }

    operator fun set(header: Header, row: Row, value: String) {
        this[header][row] = value
    }

    operator fun set(header: Header, row: Row, value: Long) {
        this[header][row] = value
    }

    operator fun set(header: Header, row: Row, value: Double) {
        this[header][row] = value
    }

    operator fun set(header: Header, row: Row, value: BigInteger) {
        this[header][row] = value
    }

    operator fun set(header: Header, row: Row, value: BigDecimal) {
        this[header][row] = value
    }

    operator fun set(header: Header, row: Row, value: Number) {
        this[header][row] = value
    }

    operator fun set(header: Header, row: Row, init: Cell<*>.() -> Unit) = this[header][row].init()

    // -----

    operator fun set(cell: Cell<*>, value: Cell<*>?) {
        this[cell.column][cell.index] = value
    }

    operator fun set(cell: Cell<*>, value: Boolean) {
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

    operator fun set(cell: Cell<*>, init: Cell<*>.() -> Unit) = this[cell.column][cell.index].init()

    // -----

    operator fun set(header1: String, index: Long, init: Cell<*>.() -> Unit) = this[header1][index].init()

    operator fun set(header1: String, header2: String, index: Long, init: Cell<*>.() -> Unit) = this[header1, header2][index].init()

    operator fun set(header1: String, header2: String, header3: String, index: Long, init: Cell<*>.() -> Unit) = this[header1, header2, header3][index].init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, init: Cell<*>.() -> Unit) = this[header1, header2, header3, header4][index].init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, init: Cell<*>.() -> Unit) = this[header1, header2, header3, header4, header5][index].init()

    // -----

    operator fun set(header1: String, index: Int, init: Cell<*>.() -> Unit) = this[header1][index].init()

    operator fun set(header1: String, header2: String, index: Int, init: Cell<*>.() -> Unit) = this[header1, header2][index].init()

    operator fun set(header1: String, header2: String, header3: String, index: Int, init: Cell<*>.() -> Unit) = this[header1, header2, header3][index].init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, init: Cell<*>.() -> Unit) = this[header1, header2, header3, header4][index].init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, init: Cell<*>.() -> Unit) = this[header1, header2, header3, header4, header5][index].init()

    // -----

    operator fun set(header1: String, row: Row, init: Cell<*>.() -> Unit) = this[header1][row].init()

    operator fun set(header1: String, header2: String, row: Row, init: Cell<*>.() -> Unit) = this[header1, header2][row].init()

    operator fun set(header1: String, header2: String, header3: String, row: Row, init: Cell<*>.() -> Unit) = this[header1, header2, header3][row].init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, init: Cell<*>.() -> Unit) = this[header1, header2, header3, header4][row].init()

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, init: Cell<*>.() -> Unit) = this[header1, header2, header3, header4, header5][row].init()

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

    operator fun set(header1: String, index: Long, value: Boolean) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Long, value: Boolean) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Long, value: Boolean) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Long, value: Boolean) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Long, value: Boolean) {
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

    operator fun set(header1: String, index: Int, value: Boolean) {
        this[header1][index] = value
    }

    operator fun set(header1: String, header2: String, index: Int, value: Boolean) {
        this[header1, header2][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, index: Int, value: Boolean) {
        this[header1, header2, header3][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, index: Int, value: Boolean) {
        this[header1, header2, header3, header4][index] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, index: Int, value: Boolean) {
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

    operator fun set(header1: String, row: Row, value: Cell<*>?) {
        this[header1][row] = value
    }

    operator fun set(header1: String, header2: String, row: Row, value: Cell<*>?) {
        this[header1, header2][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, value: Cell<*>?) {
        this[header1, header2, header3][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, value: Cell<*>?) {
        this[header1, header2, header3, header4][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, value: Cell<*>?) {
        this[header1, header2, header3, header4, header5][row] = value
    }

    // -----

    operator fun set(header1: String, row: Row, value: Boolean) {
        this[header1][row] = value
    }

    operator fun set(header1: String, header2: String, row: Row, value: Boolean) {
        this[header1, header2][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, value: Boolean) {
        this[header1, header2, header3][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, value: Boolean) {
        this[header1, header2, header3, header4][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, value: Boolean) {
        this[header1, header2, header3, header4, header5][row] = value
    }

    // -----

    operator fun set(header1: String, row: Row, value: String) {
        this[header1][row] = value
    }

    operator fun set(header1: String, header2: String, row: Row, value: String) {
        this[header1, header2][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, value: String) {
        this[header1, header2, header3][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, value: String) {
        this[header1, header2, header3, header4][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, value: String) {
        this[header1, header2, header3, header4, header5][row] = value
    }

    // -----

    operator fun set(header1: String, row: Row, value: Long) {
        this[header1][row] = value
    }

    operator fun set(header1: String, header2: String, row: Row, value: Long) {
        this[header1, header2][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, value: Long) {
        this[header1, header2, header3][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, value: Long) {
        this[header1, header2, header3, header4][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, value: Long) {
        this[header1, header2, header3, header4, header5][row] = value
    }

    // -----

    operator fun set(header1: String, row: Row, value: Double) {
        this[header1][row] = value
    }

    operator fun set(header1: String, header2: String, row: Row, value: Double) {
        this[header1, header2][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, value: Double) {
        this[header1, header2, header3][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, value: Double) {
        this[header1, header2, header3, header4][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, value: Double) {
        this[header1, header2, header3, header4, header5][row] = value
    }

    // -----

    operator fun set(header1: String, row: Row, value: BigInteger) {
        this[header1][row] = value
    }

    operator fun set(header1: String, header2: String, row: Row, value: BigInteger) {
        this[header1, header2][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, value: BigInteger) {
        this[header1, header2, header3][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, value: BigInteger) {
        this[header1, header2, header3, header4][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, value: BigInteger) {
        this[header1, header2, header3, header4, header5][row] = value
    }

    // -----

    operator fun set(header1: String, row: Row, value: BigDecimal) {
        this[header1][row] = value
    }

    operator fun set(header1: String, header2: String, row: Row, value: BigDecimal) {
        this[header1, header2][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, value: BigDecimal) {
        this[header1, header2, header3][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, value: BigDecimal) {
        this[header1, header2, header3, header4][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, value: BigDecimal) {
        this[header1, header2, header3, header4, header5][row] = value
    }

    // -----

    operator fun set(header1: String, row: Row, value: Number) {
        this[header1][row] = value
    }

    operator fun set(header1: String, header2: String, row: Row, value: Number) {
        this[header1, header2][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, row: Row, value: Number) {
        this[header1, header2, header3][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, row: Row, value: Number) {
        this[header1, header2, header3, header4][row] = value
    }

    operator fun set(header1: String, header2: String, header3: String, header4: String, header5: String, row: Row, value: Number) {
        this[header1, header2, header3, header4, header5][row] = value
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

    operator fun contains(header: Header): Boolean = tableRef.get().columns[header]?.prenatal == false

    operator fun contains(column: Column): Boolean = contains(column.header)

    operator fun contains(that: Number): Boolean = any { that in it }
    operator fun contains(that: Boolean): Boolean = any { that in it }
    operator fun contains(that: String): Boolean = any { that in it }
    operator fun contains(that: Cell<*>?): Boolean = any { that in it }

    // -----
    override fun iterator(): Iterator<Cell<*>> = iterator(this, tableRef.get())

    internal fun iterator(table: Table, ref: TableRef): Iterator<Cell<*>> {
        val columnIterator = ref.headers.map { Column(table, it.first, it.second.columnOrder) }.iterator()

        return object : Iterator<Cell<*>> {
            private var cellIterator = nextCellIterator()

            private fun nextCellIterator(): Iterator<Cell<*>> {
                while (columnIterator.hasNext()) {
                    val column = columnIterator.next()
                    // We want to throw this exception because ref should contain columnCells
                    val values = ref.columnCells[column.header] ?: throw InvalidColumnException("Unable to find column cells for header ${column.header}")
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

            override fun next(): Cell<*> {
                if (!hasNext()) throw NoSuchElementException()
                return cellIterator.next()
            }
        }
    }

    internal fun makeClone(name: String? = this.name, ref: TableRef = tableRef.get()): Table = Table(name, this, RefHolder(ref))

    override fun toString() = "Table[$name]"

    override fun equals(other: Any?): Boolean {
        // Implemented to ensure expected equality check to always just be a reference compare, that's what we want
        return this === other
    }

    override fun hashCode(): Int {
        // Clustered by name
        return Objects.hashCode(name)
    }

    companion object {
        operator fun get(name: String?): Table {
            return if (name == null) Table(null, null) // This will not be on the registry
            else get(name) { Table(null, null) } // This will be on the registry
        }

        operator fun get(name: String, init: (name: String) -> Table): Table = Registry.getTable(name, init)

        val names: SortedSet<String> get() = Registry.tableNames

        val tables: Set<Table> get() = Registry.tables
    }
}

internal data class ColumnMeta(
    val columnOrder: Long,
    val prenatal: Boolean
)

internal data class TableRef(
    val columns: PMap<Header, ColumnMeta> = PHashMap(),
    val columnCells: PMap<Header, PSortedMap<Long, CellValue<*>>> = PHashMap(),
    val version: Long = Long.MIN_VALUE,
    val columnCounter: AtomicLong = AtomicLong(Long.MIN_VALUE)
) {
    val headers: Sequence<Pair<Header, ColumnMeta>> by lazy {
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
            .fold(TreeSet<Long>()) { acc, header ->
                acc.addAll(columnCells[header]?.keys() ?: throw InvalidColumnException("Unable to find column cells for header $header"))
                acc
            }
            .asSequence()
    }
}

infix fun Table.by(columnRange: ColumnRange) = TableByColumnRangeAction(this, columnRange)
infix fun Table.by(rowRange: RowRange) = TableByRowRangeAction(this, rowRange)

class TableByColumnRangeAction internal constructor(val table: Table, val columnRange: ColumnRange)
class TableByRowRangeAction internal constructor(val table: Table, val rowRange: RowRange)
