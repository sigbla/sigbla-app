package com.sigbla.prosheet.timeseries

import com.sigbla.prosheet.exceptions.CellAlreadyExistsException
import com.sigbla.prosheet.exceptions.InvalidIndexException
import com.sigbla.prosheet.table.*
import java.time.*

operator fun Column.get(indexRelation: IndexRelation, localDate: LocalDate, localTime: LocalTime, zoneId: ZoneId): Cell<*> {
    return this[indexRelation, localDate.atTime(localTime), zoneId]
}

operator fun Column.get(indexRelation: IndexRelation, localDateTime: LocalDateTime, zoneId: ZoneId): Cell<*> {
    return this[indexRelation, localDateTime.atZone(zoneId)]
}

operator fun Column.get(indexRelation: IndexRelation, zonedDateTime: ZonedDateTime): Cell<*> {
    return this[indexRelation, zonedDateTime.toInstant().toEpochMilli()]
}

// TODO Setters..

// TODO Between time range

class RowLockColumn internal constructor(private val column: Column, private val index: Long) : Column(column.table, column.columnHeader) {
    @Volatile
    private var tid: Long? = null

    // TODO: Need to have access to some kind of context that we can use to both communicate lock status
    // and figure out if we are aborted..

    override fun get(indexRelation: IndexRelation, index: Long): Cell<*> {
        if (index < this.index)
            return column[indexRelation, index]

        if (index > this.index)
            return UnitCell(index)

        // TODO Can we do this with coroutines?
        if (tid == null) {
            if (indexRelation == IndexRelation.BEFORE)
                return this[IndexRelation.AT_OR_BEFORE, index - 1]

            synchronized(this) {
                while (tid == null) {
                    try {
                        (this as Object).wait(1000)
                    } catch (ex: InterruptedException) {}
                }
            }
        }

        return column[indexRelation, index]
    }

    override fun set(index: Long, cell: Cell<*>) {
        if (cell.index != index)
            throw InvalidIndexException("${cell.index} != $index")

        markThread()

        column[cell.index] = cell
    }

    override fun remove(index: Long): Cell<*> {
        if (index != this.index)
            throw InvalidIndexException("$index != ${this.index}")

        markThread()

        return column.remove(index)
    }

    override fun clear() {
        // TODO Should this be supported?
        remove(this.index)
    }

    @Synchronized
    private fun markThread() {
        if (tid != null)
            throw CellAlreadyExistsException()

        tid = Thread.currentThread().id;

        synchronized(this) {
            (this as Object).notifyAll()
        }
    }
}
