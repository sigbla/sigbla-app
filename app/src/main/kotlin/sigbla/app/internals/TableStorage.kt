/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import sigbla.app.*
import sigbla.app.CellValue
import sigbla.app.exceptions.InvalidStorageException
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream
import kotlin.io.path.deleteIfExists

/*

High level storage format description (see code for all the details):

<magic><version><options><seed><index location>
<... content ...>
<... index ...>

<magic> = 0x519b1a (3 bytes)
<version> = 0x1 (1 byte)
<options> = 0x1 indicates content is compressed, otherwise 0x0
<index location> = offset for first byte of index (8 bytes)

content is sequence of X bytes for row offset followed by cell content, with this prefixed by 8 bytes describing length
<8 bytes for length (long) in bytes>[sequence of bytes for row + cell content]

row content is <byte for type><row value (absolute or relative depending on type)>
cell content is <byte for type><type content>

index is sequence of header sections with this prefixed by 8 bytes describing length

If compression is enabled, each part of content/index is individually compressed

The point of seed is to remove predictability around the short hash for columns

 */

internal fun load1(
    resources: Pair<File, Table>,
    extension: String,
    filter: Column.() -> Unit
) {
    val file = resources.first.let {
        if (it.name.endsWith(".$extension", ignoreCase = true)) it else File(it.parent, it.name + ".$extension")
    }
    val table = resources.second

    Files.newByteChannel(file.toPath(), StandardOpenOption.READ).use { sbc ->
        val magic = "519b1a".chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val actualMagic = ByteArray(magic.size)
        sbc.read(ByteBuffer.wrap(actualMagic))

        if (!magic.contentEquals(actualMagic)) throw InvalidStorageException("Unsupported file type")

        val fileVersion = ByteArray(1).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read version")
            it
        }

        if (fileVersion[0] != 1.toByte()) throw InvalidStorageException("Table file version ${fileVersion[0]} not supported, please upgrade Sigbla")

        val options = ByteArray(1).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read options")
            it
        }

        val compressed = options[0] == 1.toByte()

        val seed = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read seed")
            SerializationUtils.toLong(it)
        }

        val indexLocation = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read index location")
            SerializationUtils.toLong(it)
        }

        sbc.position(indexLocation)

        val indexLength = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read index length")
            SerializationUtils.toLong(it)
        }

        val nonLeafSections = mutableMapOf<Long, HeaderSection>()

        if (compressed) {
            InflaterInputStream(SeekableByteChannelInputStream(sbc, indexLength))
        } else {
            SeekableByteChannelInputStream(sbc, indexLength)
        }.use { inputStream ->
            generateSequence { deserializeHeaderSection(inputStream) }.forEach { headerSection ->
                if (!headerSection.leaf) {
                    nonLeafSections[shortHash(headerSection.parent, headerSection.section)] = headerSection
                } else {
                    val headers = generateSequence(headerSection) { currentSection ->
                        nonLeafSections[currentSection.parent]
                    }.toList()

                    val columnLocation = headers.first().location ?: throw InvalidStorageException("Missing column location")
                    val header = Header(headers.map { it.section }.reversed())
                    val column = Table(null, table)[header]

                    fillColumn(file, compressed, column, columnLocation)
                    column.filter()

                    if (header in column.table) {
                        // Do this to ensure prenatal status is kept if applicable
                        table[column]

                        // Merge any cells
                        column.forEach { table[it] = it } // TODO Should batch this?
                    }
                }
            }
        }
    }
}

private fun fillColumn(file: File, compressed: Boolean, column: Column, location: Long) {
    Files.newByteChannel(file.toPath(), StandardOpenOption.READ).use { sbc ->
        sbc.position(location)

        val columnLength = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read column length")
            SerializationUtils.toLong(it)
        }

        if (compressed) {
            InflaterInputStream(SeekableByteChannelInputStream(sbc, columnLength))
        } else {
            SeekableByteChannelInputStream(sbc, columnLength)
        }.use { inputStream ->
            var prevRow = Long.MIN_VALUE
            while(true) {
                val (row, cellValue) = readCell(prevRow, inputStream) ?: break
                prevRow = row
                column[row] = CellValue(cellValue).toCell(column, row)
            }
        }
    }
}

private fun readCell(prevRow: Long, inputStream: InputStream): Pair<Long, Any?>? {
    val type1 = ByteArray(1).let {
        val r = inputStream.readNBytes(it, 0, it.size)
        if (r == 0) return null
        if (r != it.size) throw InvalidStorageException("Invalid cell data")
        it[0].toInt()
    }

    val row = when (val rowDiff = SerializationUtils.toType(type1, inputStream)) {
        is Byte -> prevRow + rowDiff
        is Int -> prevRow + rowDiff
        is Long -> rowDiff
        else -> throw InvalidStorageException("Invalid cell row")
    }

    val type2 = ByteArray(1).let {
        val r = inputStream.readNBytes(it, 0, it.size)
        if (r != it.size) throw InvalidStorageException("Invalid cell data")
        it[0].toInt()
    }

    val cellValue = SerializationUtils.toType(type2, inputStream)

    return row to cellValue
}

internal fun save1(
    resources: Pair<Table, File>,
    extension: String,
    compress: Boolean
) {
    val tableRef = resources.first.tableRef.get()
    val file = resources.second.let {
        if (it.name.endsWith(".$extension", ignoreCase = true)) it else File(it.parent, it.name + ".$extension")
    }

    file.parentFile?.mkdirs()

    val tmpFilePath = File(file.parent, file.name + ".tmp").toPath()
    tmpFilePath.deleteIfExists()

    Files.newByteChannel(tmpFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE).use { sbc ->
        val magic = "519b1a01".chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        if (sbc.write(ByteBuffer.wrap(magic)) != magic.size) throw InvalidStorageException("Unable to write magic")

        val options = if (compress) byteArrayOf(0x1) else byteArrayOf(0x0)
        if (sbc.write(ByteBuffer.wrap(options)) != options.size) throw InvalidStorageException("Unable to write options")

        val seed = ThreadLocalRandom.current().nextLong()
        val seedBuffer = SerializationUtils.fromLong(seed)
        if (sbc.write(ByteBuffer.wrap(seedBuffer)) != seedBuffer.size) throw InvalidStorageException("Unable to write seed")

        // Placeholder for index location
        val indexLocation = sbc.position()
        if (sbc.write(ByteBuffer.wrap(ByteArray(Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write index location")

        val headerSections = TreeSet<HeaderSection> { o1, o2 ->
            // Put non-leafs first
            if (o1.leaf != o2.leaf) {
                if (o2.leaf) -1 else 1
            }
            // If both leaf or not, sort by order
            else if (o1.order != o2.order) {
                o1.order.compareTo(o2.order)
            }
            // If order the same, sort by section next
            else if (o1.section != o2.section) {
                o1.section.compareTo(o2.section)
            }
            // Finally sort by parent hash
            else o1.parent.compareTo(o2.parent)
        }

        tableRef.columns.sortedBy { it.component2().columnOrder }.forEach { (columnHeader, columnMeta) ->
            val head = columnHeader.labels.dropLast(1)
            val tail = columnHeader.labels.last()

            var parent = seed

            head.forEach {
                headerSections.add(HeaderSection(it, parent, false))
                parent = shortHash(parent, it)
            }

            val start = sbc.position()

            headerSections.add(HeaderSection(tail, parent, true, start, columnMeta.columnOrder))

            // Placeholder for content length
            if (sbc.write(ByteBuffer.wrap(ByteArray(Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")

            Channels.newOutputStream(sbc).let {
                if (compress) DeflaterOutputStream(BufferedOutputStream(NoCloseOutputStream(it))) else BufferedOutputStream(NoCloseOutputStream(it))
            }.use { outputStream ->
                var prevRow = Long.MIN_VALUE
                tableRef.columnCells[columnHeader]?.forEach { (row, cellValue) ->
                    val rowDiff = row - prevRow

                    val rowValue = when {
                        // TODO prevRow + rowDiff != row -> row // Overflow case?
                        row == 0L -> row // Special case when row index is zero
                        rowDiff >= Byte.MIN_VALUE.toLong() && rowDiff <= Byte.MAX_VALUE.toLong() -> rowDiff.toByte()
                        rowDiff >= Int.MIN_VALUE.toLong() && rowDiff <= Int.MAX_VALUE.toLong() -> rowDiff.toInt()
                        else -> row
                    }

                    prevRow = row

                    outputStream.write(SerializationUtils.fromType(rowValue))
                    outputStream.write(SerializationUtils.fromType(cellValue.value))
                }
            }

            val end = sbc.position()

            if (sbc.position(start).write(ByteBuffer.wrap(SerializationUtils.fromLong(end - start - Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")
            sbc.position(end)
        }

        val start = sbc.position()

        if (sbc.position(indexLocation).write(ByteBuffer.wrap(SerializationUtils.fromLong(start))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write index location")
        sbc.position(start)

        // Placeholder for index length
        if (sbc.write(ByteBuffer.wrap(ByteArray(Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write index length")

        Channels.newOutputStream(sbc).let {
            if (compress) DeflaterOutputStream(BufferedOutputStream(NoCloseOutputStream(it))) else BufferedOutputStream(NoCloseOutputStream(it))
        }.use { outputStream ->
            headerSections.forEach { headerSection ->
                outputStream.write(serializeHeaderSection(headerSection))
            }
        }

        val end = sbc.position()

        if (sbc.position(start).write(ByteBuffer.wrap(SerializationUtils.fromLong(end - start - Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write index length")
        sbc.position(end)
    }

    Files.move(tmpFilePath, file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
}

private class HeaderSection(
    val section: String,
    val parent: Long,
    val leaf: Boolean,
    val location: Long? = null,
    val order: Long = Long.MIN_VALUE
)

private fun serializeHeaderSection(headerSection: HeaderSection): ByteArray {
    ByteArrayOutputStream().use { baos ->
        if (headerSection.leaf) {
            baos.write(1)
            SerializationUtils.fromString(headerSection.section).apply {
                baos.write(SerializationUtils.fromInt(this.size))
                if (this.isNotEmpty()) baos.write(this)
            }
            baos.write(SerializationUtils.fromLong(headerSection.parent))
            baos.write(SerializationUtils.fromLong(headerSection.location ?: throw InvalidStorageException("Missing header section location")))
        } else {
            baos.write(0)
            SerializationUtils.fromString(headerSection.section).apply {
                baos.write(SerializationUtils.fromInt(this.size))
                if (this.isNotEmpty()) baos.write(this)
            }
            baos.write(SerializationUtils.fromLong(headerSection.parent))
        }

        return baos.toByteArray()
    }
}

private fun deserializeHeaderSection(inputStream: InputStream): HeaderSection? {
    val type = ByteArray(1).let {
        val r = inputStream.readNBytes(it, 0, it.size)
        if (r == 0) return null
        if (r != it.size) throw InvalidStorageException("Unable to read header section type")
        if (it[0] != 0.toByte() && it[0] != 1.toByte()) throw InvalidStorageException("Unsupported header section type: ${it[0]}")
        it[0]
    }

    val sectionLength = ByteArray(Int.SIZE_BYTES).let {
        if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read header section length")
        SerializationUtils.toInt(it)
    }

    val section = if (sectionLength == 0) "" else ByteArray(sectionLength).let {
        if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read header section")
        SerializationUtils.toString(it)
    }

    val parent = ByteArray(Long.SIZE_BYTES).let {
        if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read header section parent")
        SerializationUtils.toLong(it)
    }

    if (type == 0.toByte()) {
        return HeaderSection(section, parent, false)
    }

    val location = ByteArray(Long.SIZE_BYTES).let {
        if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read header section location")
        SerializationUtils.toLong(it)
    }

    return HeaderSection(section, parent, true, location)
}
