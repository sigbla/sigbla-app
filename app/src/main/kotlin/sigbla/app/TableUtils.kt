/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app

import java.io.Writer

// TODO Look at adding print methods for column/row ranges too

fun print(table: Table) {
    print(table, System.out.writer())
}

fun print(table: Table, writer: Writer) {
    val table = clone(table)

    val headers = table.headers
    val indexes = table.indexes

    var maxCellWidth = 0
    val headerTable = Table[null].let { headerTable ->
        for ((index, header) in headers.withIndex()) {
            headerTable[index.toString()].let { headerColumn ->
                for ((index, headerCell) in header.labels.withIndex()) {
                    headerColumn[index] = headerCell
                    if (headerCell.length > maxCellWidth) maxCellWidth = headerCell.length
                }
            }
        }
        headerTable
    }

    table.indexes.map { it.toString() }.map { it.length }.forEach {
        if (it > maxCellWidth) maxCellWidth = it
    }

    table.map { it.value?.toString() ?: "" }.map { it.length }.forEach {
        if (it > maxCellWidth) maxCellWidth = it
    }

    fun write(input: String, width: Int) {
        writer.append(input)
        for (i in input.length .. width) {
            writer.append(" ")
        }
    }

    for (index in headerTable.indexes) {
        write("", maxCellWidth)
        for (header in headerTable.headers) {
            write("|${headerTable[index][header]}", maxCellWidth + 1)
        }
        writer.append(System.lineSeparator())
    }

    for (index in indexes) {
        write(index.toString(), maxCellWidth)
        for (header in headers) {
            write("|${table[index][header]}", maxCellWidth + 1)
        }
        writer.append(System.lineSeparator())
    }

    writer.flush()
}
