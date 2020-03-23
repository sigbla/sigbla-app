package sigbla.app.timeseries

import sigbla.app.Storage
import sigbla.app.Column
import sigbla.app.ColumnHeader
import sigbla.app.Table
import java.time.*

// TODO table date/time getters..

fun Table.Companion.fromStorageRange(storage: Storage, name: String, fromLocalDate: LocalDate, fromLocalTime: LocalTime, toLocalDate: LocalDate, toLocalTime: LocalTime, zoneId: ZoneId): Table {
    return Table.fromStorageRange(storage, name, fromLocalDate.atTime(fromLocalTime), toLocalDate.atTime(toLocalTime), zoneId)
}

fun Table.Companion.fromStorageRangeAs(storage: Storage, name: String, fromLocalDate: LocalDate, fromLocalTime: LocalTime, toLocalDate: LocalDate, toLocalTime: LocalTime, zoneId: ZoneId, newName: String): Table {
    return Table.fromStorageRangeAs(storage, name, fromLocalDate.atTime(fromLocalTime), toLocalDate.atTime(toLocalTime), zoneId, newName)
}

fun Table.Companion.fromStorageRange(storage: Storage, name: String, fromLocalDateTime: LocalDateTime, toLocalDateTime: LocalDateTime, zoneId: ZoneId): Table {
    return Table.fromStorageRange(storage, name, fromLocalDateTime.atZone(zoneId), toLocalDateTime.atZone(zoneId))
}

fun Table.Companion.fromStorageRangeAs(storage: Storage, name: String, fromLocalDateTime: LocalDateTime, toLocalDateTime: LocalDateTime, zoneId: ZoneId, newName: String): Table {
    return Table.fromStorageRangeAs(storage, name, fromLocalDateTime.atZone(zoneId), toLocalDateTime.atZone(zoneId), newName)
}

fun Table.Companion.fromStorageRange(storage: Storage, name: String, fromZonedDateTime: ZonedDateTime, toZonedDateTime: ZonedDateTime): Table {
    return Table.fromStorageRange(storage, name, fromZonedDateTime.toInstant().toEpochMilli(), toZonedDateTime.toInstant().toEpochMilli())
}

fun Table.Companion.fromStorageRangeAs(storage: Storage, name: String, fromZonedDateTime: ZonedDateTime, toZonedDateTime: ZonedDateTime, newName: String): Table {
    return Table.fromStorageRangeAs(storage, name, fromZonedDateTime.toInstant().toEpochMilli(), toZonedDateTime.toInstant().toEpochMilli(), newName)
}

// TODO This should probably not be here
class RowLockTable internal constructor(private val table: Table, private val index: Long) : Table(table.name) {
    override val headers: Collection<ColumnHeader>
        get() = table.headers

    override fun get(header: ColumnHeader): Column = RowLockColumn(table[header], index)

    override fun contains(header: ColumnHeader): Boolean = table.contains(header)

    override fun remove(header: ColumnHeader) = table.remove(header)

    override fun rename(existing: ColumnHeader, newName: ColumnHeader) = table.rename(existing, newName)
}
