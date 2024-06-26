/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import sigbla.app.exceptions.InvalidStorageException
import sigbla.app.pds.kollection.ImmutableSet as PSet
import sigbla.app.pds.kollection.toImmutableSet
import sigbla.app.*
import sigbla.app.ViewMeta
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.io.BufferedOutputStream
import java.nio.channels.Channels
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream
import kotlin.io.path.deleteIfExists

/*

High level storage format description (see code for all the details):

<magic><version><options><seed><index location>
<... content ...>
<... index ...>

<magic> = 0x519b1b (3 bytes) - notice the 'b' at the end, compared to an 'a' for table data
<version> = 0x1 (1 byte)
<options> = 0x1 indicates content is compressed, otherwise 0x0
<index location> = offset for first byte of index (8 bytes)

content is sequence of view sections and view meta, with this prefixed by 8 bytes describing length
<8 bytes for length (long) in bytes>[sequence of bytes for view sections + view meta]

index contains the location for the 4 types of view meta, hence the index is of fixed length

If compression is enabled, each of the 4 types of content is individually compressed

The point of seed is to remove predictability around the short hash for columns

 */

internal fun load1(
    resources: Pair<File, TableView>,
    extension: String
) {
    val file = resources.first.let {
        if (it.name.endsWith(".$extension", ignoreCase = true)) it else File(it.parent, it.name + ".$extension")
    }
    val tableView = resources.second

    Files.newByteChannel(file.toPath(), StandardOpenOption.READ).use { sbc ->
        val magic = "519b1b".chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val actualMagic = ByteArray(magic.size)
        sbc.read(ByteBuffer.wrap(actualMagic))

        if (!magic.contentEquals(actualMagic)) throw InvalidStorageException("Unsupported file type")

        val fileVersion = ByteArray(1).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read version")
            it
        }

        if (fileVersion[0] != 1.toByte()) throw InvalidStorageException("Table view file version ${fileVersion[0]} not supported, please upgrade Sigbla")

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

        val defaultCellViewPosition = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read default cell view position")
            SerializationUtils.toLong(it)
        }

        val columnViewsPosition = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read column views position")
            SerializationUtils.toLong(it)
        }

        val rowViewsPosition = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read row views position")
            SerializationUtils.toLong(it)
        }

        val cellViewsPosition = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read cell views position")
            SerializationUtils.toLong(it)
        }

        sbc.position(defaultCellViewPosition)

        val defaultCellViewLength = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read default cell view length")
            SerializationUtils.toLong(it)
        }

        if (compressed) {
            InflaterInputStream(SeekableByteChannelInputStream(sbc, defaultCellViewLength))
        } else {
            SeekableByteChannelInputStream(sbc, defaultCellViewLength)
        }.use { inputStream ->
            val viewMeta = deserializeViewMeta(inputStream)
            tableView[CellHeight] = viewMeta.cellHeight
            tableView[CellWidth] = viewMeta.cellWidth
            tableView[CellClasses] = viewMeta.cellClasses
            tableView[CellTopics] = viewMeta.cellTopics
        }

        sbc.position(columnViewsPosition)

        val columnViewsLength = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read column views length")
            SerializationUtils.toLong(it)
        }

        if (compressed) {
            InflaterInputStream(SeekableByteChannelInputStream(sbc, columnViewsLength))
        } else {
            SeekableByteChannelInputStream(sbc, columnViewsLength)
        }.use { inputStream ->
            val nonLeafSections = mutableMapOf<Long, ViewSection>()

            generateSequence { deserializeViewSection(inputStream) }.forEach { viewSection ->
                if (!viewSection.leaf) {
                    nonLeafSections[shortHash(viewSection.parent, viewSection.section ?: throw InvalidStorageException("Missing column section"))] = viewSection
                } else {
                    val headers = generateSequence(viewSection) { currentSection ->
                        nonLeafSections[currentSection.parent]
                    }.toList()

                    val header = Header(headers.map { it.section ?: throw InvalidStorageException("Missing column section") }.reversed())

                    val viewMeta = deserializeViewMeta(inputStream)
                    tableView[header][CellWidth] = viewMeta.cellWidth
                    tableView[header][CellClasses] = viewMeta.cellClasses
                    tableView[header][CellTopics] = viewMeta.cellTopics
                    when (viewMeta.positionValue) {
                        Position.Value.LEFT -> tableView[header][Position] = Position.Left
                        Position.Value.RIGHT -> tableView[header][Position] = Position.Right
                        else -> tableView[header][Position] = Unit
                    }
                    when (viewMeta.visibilityValue) {
                        Visibility.Value.HIDE -> tableView[header][Visibility] = Visibility.Hide
                        Visibility.Value.SHOW -> tableView[header][Visibility] = Visibility.Show
                        else -> tableView[header][Visibility] = Unit
                    }
                }
            }
        }

        sbc.position(rowViewsPosition)

        val rowViewsLength = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read row views length")
            SerializationUtils.toLong(it)
        }

        if (compressed) {
            InflaterInputStream(SeekableByteChannelInputStream(sbc, rowViewsLength))
        } else {
            SeekableByteChannelInputStream(sbc, rowViewsLength)
        }.use { inputStream ->
            var prevRow = Long.MIN_VALUE

            while (true) {
                val type = ByteArray(1).let {
                    val r = inputStream.readNBytes(it, 0, it.size)
                    if (r == 0) return@let null
                    if (r != it.size) throw InvalidStorageException("Invalid cell data")
                    it[0].toInt()
                } ?: break

                val row = when (val rowDiff = SerializationUtils.toType(type, inputStream)) {
                    is Byte -> prevRow + rowDiff
                    is Int -> prevRow + rowDiff
                    is Long -> rowDiff
                    else -> throw InvalidStorageException("Invalid cell row")
                }

                prevRow = row

                val viewMeta = deserializeViewMeta(inputStream)
                tableView[row][CellHeight] = viewMeta.cellHeight
                tableView[row][CellClasses] = viewMeta.cellClasses
                tableView[row][CellTopics] = viewMeta.cellTopics
                when (viewMeta.positionValue) {
                    Position.Value.TOP -> tableView[row][Position] = Position.Top
                    Position.Value.BOTTOM -> tableView[row][Position] = Position.Bottom
                    else -> tableView[row][Position] = Unit
                }
                when (viewMeta.visibilityValue) {
                    Visibility.Value.HIDE -> tableView[row][Visibility] = Visibility.Hide
                    Visibility.Value.SHOW -> tableView[row][Visibility] = Visibility.Show
                    else -> tableView[row][Visibility] = Unit
                }
            }
        }

        sbc.position(cellViewsPosition)

        val cellViewsLength = ByteArray(Long.SIZE_BYTES).let {
            if (sbc.read(ByteBuffer.wrap(it)) != it.size) throw InvalidStorageException("Unable to read column views length")
            SerializationUtils.toLong(it)
        }

        if (compressed) {
            InflaterInputStream(SeekableByteChannelInputStream(sbc, cellViewsLength))
        } else {
            SeekableByteChannelInputStream(sbc, cellViewsLength)
        }.use { inputStream ->
            val nonLeafSections = mutableMapOf<Long, ViewSection>()

            generateSequence { deserializeViewSection(inputStream) }.forEach { viewSection ->
                if (!viewSection.leaf) {
                    nonLeafSections[shortHash(viewSection.parent, viewSection.section ?: throw InvalidStorageException("Missing column section"))] = viewSection
                } else {
                    val headers = generateSequence(viewSection) { currentSection ->
                        nonLeafSections[currentSection.parent]
                    }.drop(1).toList()

                    val header = Header(headers.map { it.section ?: throw InvalidStorageException("Missing column section") }.reversed())
                    val row = viewSection.row ?: throw InvalidStorageException("Missing row section")

                    val viewMeta = deserializeViewMeta(inputStream)
                    tableView[header, row][CellHeight] = viewMeta.cellHeight
                    tableView[header, row][CellWidth] = viewMeta.cellWidth
                    tableView[header, row][CellClasses] = viewMeta.cellClasses
                    tableView[header, row][CellTopics] = viewMeta.cellTopics
                }
            }
        }
    }
}

internal fun save1(
    resources: Pair<TableView, File>,
    extension: String,
    compress: Boolean
) {
    val tableViewRef = resources.first.tableViewRef.get()
    val file = resources.second.let {
        if (it.name.endsWith(".$extension", ignoreCase = true)) it else File(it.parent, it.name + ".$extension")
    }

    file.parentFile?.mkdirs()

    val tmpFilePath = File(file.parent, file.name + ".tmp").toPath()
    tmpFilePath.deleteIfExists()

    Files.newByteChannel(tmpFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE).use { sbc ->
        val magic = "519b1b01".chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        if (sbc.write(ByteBuffer.wrap(magic)) != magic.size) throw InvalidStorageException("Unable to write magic")

        val options = if (compress) byteArrayOf(0x1) else byteArrayOf(0x0)
        if (sbc.write(ByteBuffer.wrap(options)) != options.size) throw InvalidStorageException("Unable to write options")

        val seed = ThreadLocalRandom.current().nextLong()
        val seedBuffer = SerializationUtils.fromLong(seed)
        if (sbc.write(ByteBuffer.wrap(seedBuffer)) != seedBuffer.size) throw InvalidStorageException("Unable to write seed")

        // Placeholder for index location
        val indexLocation = sbc.position()
        if (sbc.write(ByteBuffer.wrap(ByteArray(Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write index location")

        val viewSections = TreeSet<ViewSection> { o1, o2 ->
            // Put non-leafs first
            if (o1.leaf != o2.leaf) {
                if (o2.leaf) -1 else 1
            }
            // If both leaf or not, sort by section next
            else if (o1.section != o2.section) {
                val s1 = o1.section
                val s2 = o2.section

                when {
                    s1 == null -> -1
                    s2 == null -> 1
                    else -> s1.compareTo(s2)
                }
            }
            // If section the same, sort by row next
            else if (o1.row != o2.row) {
                val r1 = o1.row
                val r2 = o2.row

                when {
                    r1 == null -> -1
                    r2 == null -> 1
                    else -> r1.compareTo(r2)
                }
            }
            // Finally sort by parent hash
            else o1.parent.compareTo(o2.parent)
        }

        // Save default cell view first
        val defaultCellViewPosition = sbc.position()
        tableViewRef.defaultCellView.let { defaultCellView ->
            // Placeholder for content length
            if (sbc.write(ByteBuffer.wrap(ByteArray(Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")

            Channels.newOutputStream(sbc).let {
                if (compress) DeflaterOutputStream(BufferedOutputStream(NoCloseOutputStream(it))) else BufferedOutputStream(NoCloseOutputStream(it))
            }.use { outputStream ->
                outputStream.write(serializeViewMeta(defaultCellView))
            }

            val end = sbc.position()

            if (sbc.position(defaultCellViewPosition).write(ByteBuffer.wrap(SerializationUtils.fromLong(end - defaultCellViewPosition - Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")
            sbc.position(end)
        }

        // Save column views next
        tableViewRef.columnViews.forEach { (columnHeader, viewMeta) ->
            val head = columnHeader.labels.dropLast(1)
            val tail = columnHeader.labels.last()

            var parent = seed

            head.forEach {
                viewSections.add(ViewSection(it, parent, false))
                parent = shortHash(parent, it)
            }

            viewSections.add(ViewSection(tail, parent, true, viewMeta = viewMeta))
        }

        val columnViewsPosition = sbc.position()

        // Placeholder for content length
        if (sbc.write(ByteBuffer.wrap(ByteArray(Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")

        Channels.newOutputStream(sbc).let {
            if (compress) DeflaterOutputStream(BufferedOutputStream(NoCloseOutputStream(it))) else BufferedOutputStream(NoCloseOutputStream(it))
        }.use { outputStream ->
            viewSections.forEach { viewSection ->
                outputStream.write(serializeViewSection(viewSection))
                if (viewSection.leaf) outputStream.write(serializeViewMeta(viewSection.viewMeta ?: throw InvalidStorageException("Unable to write view meta")))
            }
        }

        val columnViewsPositionEnd = sbc.position()

        if (sbc.position(columnViewsPosition).write(ByteBuffer.wrap(SerializationUtils.fromLong(columnViewsPositionEnd - columnViewsPosition - Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")
        sbc.position(columnViewsPositionEnd)

        // Save row views next
        val rowViewsPosition = sbc.position()
        tableViewRef.rowViews.let { rowViews ->
            // Placeholder for content length
            if (sbc.write(ByteBuffer.wrap(ByteArray(Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")

            Channels.newOutputStream(sbc).let {
                if (compress) DeflaterOutputStream(BufferedOutputStream(NoCloseOutputStream(it))) else BufferedOutputStream(NoCloseOutputStream(it))
            }.use { outputStream ->
                var prevRow = Long.MIN_VALUE
                rowViews.sortedBy { it.component1() }.forEach { (row, viewMeta) ->
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
                    outputStream.write(serializeViewMeta(viewMeta))
                }
            }

            val end = sbc.position()

            if (sbc.position(rowViewsPosition).write(ByteBuffer.wrap(SerializationUtils.fromLong(end - rowViewsPosition - Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")
            sbc.position(end)
        }

        // Save cell views next
        viewSections.clear()
        tableViewRef.cellViews.forEach { (columnHeaderRow, viewMeta) ->
            val (columnHeader, row) = columnHeaderRow

            var parent = seed

            columnHeader.labels.forEach {
                viewSections.add(ViewSection(it, parent, false))
                parent = shortHash(parent, it)
            }

            viewSections.add(ViewSection(null, parent, true, row, viewMeta = viewMeta))
        }

        val cellViewsPosition =  sbc.position()

        // Placeholder for content length
        if (sbc.write(ByteBuffer.wrap(ByteArray(Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")

        Channels.newOutputStream(sbc).let {
            if (compress) DeflaterOutputStream(BufferedOutputStream(NoCloseOutputStream(it))) else BufferedOutputStream(NoCloseOutputStream(it))
        }.use { outputStream ->
            viewSections.forEach { viewSection ->
                outputStream.write(serializeViewSection(viewSection))
                if (viewSection.leaf) outputStream.write(serializeViewMeta(viewSection.viewMeta ?: throw InvalidStorageException("Unable to write view meta")))
            }
        }

        val cellViewsPositionEnd = sbc.position()

        if (sbc.position(cellViewsPosition).write(ByteBuffer.wrap(SerializationUtils.fromLong(cellViewsPositionEnd - cellViewsPosition - Long.SIZE_BYTES))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write content length")
        sbc.position(cellViewsPositionEnd)

        val start = sbc.position()

        if (sbc.position(indexLocation).write(ByteBuffer.wrap(SerializationUtils.fromLong(start))) != Long.SIZE_BYTES) throw InvalidStorageException("Unable to write index location")
        sbc.position(start)

        // Write the 4 index positions
        sbc.write(ByteBuffer.wrap(SerializationUtils.fromLong(defaultCellViewPosition)))
        sbc.write(ByteBuffer.wrap(SerializationUtils.fromLong(columnViewsPosition)))
        sbc.write(ByteBuffer.wrap(SerializationUtils.fromLong(rowViewsPosition)))
        sbc.write(ByteBuffer.wrap(SerializationUtils.fromLong(cellViewsPosition)))
    }

    Files.move(tmpFilePath, file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
}

private class ViewSection(
    val section: String? = null,
    val parent: Long,
    val leaf: Boolean,
    val row: Long? = null,
    val viewMeta: ViewMeta? = null
)

private fun serializeViewSection(viewSection: ViewSection): ByteArray {
    ByteArrayOutputStream().use { baos ->
        if (viewSection.leaf) {
            baos.write(if (viewSection.row == null) 1 else 2)

            if (viewSection.section != null) SerializationUtils.fromString(viewSection.section).apply {
                baos.write(SerializationUtils.fromInt(this.size))
                if (this.isNotEmpty()) baos.write(this)
            } else baos.write(SerializationUtils.fromInt(-1))

            baos.write(SerializationUtils.fromLong(viewSection.parent))

            if (viewSection.row != null) baos.write(SerializationUtils.fromLong(viewSection.row))
        } else {
            baos.write(0)

            if (viewSection.section != null) SerializationUtils.fromString(viewSection.section).apply {
                baos.write(SerializationUtils.fromInt(this.size))
                if (this.isNotEmpty()) baos.write(this)
            } else baos.write(SerializationUtils.fromInt(-1))

            baos.write(SerializationUtils.fromLong(viewSection.parent))
        }

        return baos.toByteArray()
    }
}

private fun deserializeViewSection(inputStream: InputStream): ViewSection? {
    val type = ByteArray(1).let {
        val r = inputStream.readNBytes(it, 0, it.size)
        if (r == 0) return null
        if (r != it.size) throw InvalidStorageException("Unable to read view section type")
        if (it[0] != 0.toByte() && it[0] != 1.toByte() && it[0] != 2.toByte()) throw InvalidStorageException("Unsupported view section type: ${it[0]}")
        it[0]
    }

    val sectionLength = ByteArray(Int.SIZE_BYTES).let {
        if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view section length")
        SerializationUtils.toInt(it)
    }

    val section = when (sectionLength) {
        -1 -> null
        0 -> ""
        else -> ByteArray(sectionLength).let {
            val r = inputStream.readNBytes(it, 0, it.size)
            if (r != it.size) throw InvalidStorageException("Unable to read view section")
            SerializationUtils.toString(it)
        }
    }

    val parent = ByteArray(Long.SIZE_BYTES).let {
        if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view section parent")
        SerializationUtils.toLong(it)
    }

    if (type == 0.toByte()) {
        return ViewSection(section, parent, false)
    }

    val row = if (type == 2.toByte()) ByteArray(Long.SIZE_BYTES).let {
        if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view section location")
        SerializationUtils.toLong(it)
    } else null

    return ViewSection(section, parent, true, row)
}

private fun serializeViewMeta(viewMeta: ViewMeta): ByteArray {
    ByteArrayOutputStream().use { baos ->
        val haveHeight = if (viewMeta.cellHeight == null) 0 else          0b10000000
        val haveWidth = if (viewMeta.cellWidth == null) 0 else            0b01000000
        val haveClasses = if (viewMeta.cellClasses == null) 0 else        0b00100000
        val haveTopics = if (viewMeta.cellTopics == null) 0 else          0b00010000
        val havePosition = if (viewMeta.positionValue == null) 0 else     0b00001000
        val haveVisibility = if (viewMeta.visibilityValue == null) 0 else 0b00000100

        val options = haveHeight or haveWidth or haveClasses or haveTopics or havePosition or haveVisibility

        baos.write(SerializationUtils.fromInt(options))

        if (viewMeta.cellHeight != null) baos.write(SerializationUtils.fromLong(viewMeta.cellHeight))
        if (viewMeta.cellWidth != null) baos.write(SerializationUtils.fromLong(viewMeta.cellWidth))
        if (viewMeta.cellClasses != null) {
            baos.write(SerializationUtils.fromInt(viewMeta.cellClasses.size))
            viewMeta.cellClasses.forEach {
                SerializationUtils.fromString(it).apply {
                    baos.write(SerializationUtils.fromInt(this.size))
                    if (this.isNotEmpty()) baos.write(this)
                }
            }
        }
        if (viewMeta.cellTopics != null) {
            baos.write(SerializationUtils.fromInt(viewMeta.cellTopics.size))
            viewMeta.cellTopics.forEach {
                SerializationUtils.fromString(it).apply {
                    baos.write(SerializationUtils.fromInt(this.size))
                    if (this.isNotEmpty()) baos.write(this)
                }
            }
        }
        if (viewMeta.positionValue != null) {
            when (viewMeta.positionValue) {
                Position.Value.BOTTOM -> baos.write(SerializationUtils.fromInt(0))
                Position.Value.LEFT -> baos.write(SerializationUtils.fromInt(1))
                Position.Value.RIGHT -> baos.write(SerializationUtils.fromInt(2))
                Position.Value.TOP -> baos.write(SerializationUtils.fromInt(3))
            }
        }
        if (viewMeta.visibilityValue != null) {
            when (viewMeta.visibilityValue) {
                Visibility.Value.HIDE -> baos.write(SerializationUtils.fromInt(0))
                Visibility.Value.SHOW -> baos.write(SerializationUtils.fromInt(1))
            }
        }

        return baos.toByteArray()
    }
}

private fun deserializeViewMeta(inputStream: InputStream): ViewMeta {
    val haveHeight =     0b10000000
    val haveWidth =      0b01000000
    val haveClasses =    0b00100000
    val haveTopics =     0b00010000
    val havePosition =   0b00001000
    val haveVisibility = 0b00000100

    val options = ByteArray(Int.SIZE_BYTES).let {
        if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta options")
        SerializationUtils.toInt(it)
    }

    val cellHeight = if ((options and haveHeight) == haveHeight) {
        ByteArray(Long.SIZE_BYTES).let {
            if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta height")
            SerializationUtils.toLong(it)
        }
    } else null

    val cellWidth = if ((options and haveWidth) == haveWidth) {
        ByteArray(Long.SIZE_BYTES).let {
            if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta width")
            SerializationUtils.toLong(it)
        }
    } else null

    val cellClasses: PSet<String>? = if ((options and haveClasses) == haveClasses) {
        val classesLength = ByteArray(Int.SIZE_BYTES).let {
            if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta classes length")
            SerializationUtils.toInt(it)
        }
        val classes = mutableSetOf<String>()
        for (i in 0 until classesLength) {
            val classLength = ByteArray(Int.SIZE_BYTES).let {
                if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta classes length")
                SerializationUtils.toInt(it)
            }
            classes += ByteArray(classLength).let {
                if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta section")
                SerializationUtils.toString(it)
            }
        }
        classes.toImmutableSet()
    } else null

    val cellTopics: PSet<String>? = if ((options and haveTopics) == haveTopics) {
        val topicsLength = ByteArray(Int.SIZE_BYTES).let {
            if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta topics length")
            SerializationUtils.toInt(it)
        }
        val topics = mutableSetOf<String>()
        for (i in 0 until topicsLength) {
            val topicLength = ByteArray(Int.SIZE_BYTES).let {
                if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta topics length")
                SerializationUtils.toInt(it)
            }
            topics += ByteArray(topicLength).let {
                if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta section")
                SerializationUtils.toString(it)
            }
        }
        topics.toImmutableSet()
    } else null

    val positionValue: Position.Value? = if ((options and havePosition) == havePosition) {
        ByteArray(Int.SIZE_BYTES).let {
            if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta position value")
            when (SerializationUtils.toInt(it)) {
                0 -> Position.Value.BOTTOM
                1 -> Position.Value.LEFT
                2 -> Position.Value.RIGHT
                3 -> Position.Value.TOP
                else -> throw InvalidStorageException("Unsupported view meta position value")
            }
        }
    } else null

    val visibilityValue: Visibility.Value? = if ((options and haveVisibility) == haveVisibility) {
        ByteArray(Int.SIZE_BYTES).let {
            if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta visibility value")
            when (SerializationUtils.toInt(it)) {
                0 -> Visibility.Value.HIDE
                1 -> Visibility.Value.SHOW
                else -> throw InvalidStorageException("Unsupported view meta visibility value")
            }
        }
    } else null

    return ViewMeta(cellHeight, cellWidth, cellClasses, cellTopics, positionValue, visibilityValue)
}
