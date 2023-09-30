package sigbla.app.internals

import sigbla.app.exceptions.InvalidStorageException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.io.path.deleteIfExists
import com.github.andrewoma.dexx.kollection.ImmutableSet as PSet
import com.github.andrewoma.dexx.kollection.toImmutableSet
import sigbla.app.*
import sigbla.app.ViewMeta
import java.io.BufferedOutputStream
import java.nio.channels.Channels
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

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
        val magic = "519b1b01".chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val actualMagic = ByteArray(magic.size)
        sbc.read(ByteBuffer.wrap(actualMagic))

        if (!magic.contentEquals(actualMagic)) throw InvalidStorageException("Unsupported file type")

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
            if (viewMeta.cellHeight == null) tableView[CellHeight] = null else tableView[CellHeight] = viewMeta.cellHeight
            if (viewMeta.cellWidth == null) tableView[CellWidth] = null else tableView[CellWidth] = viewMeta.cellWidth
            if (viewMeta.cellClasses == null) tableView[CellClasses] = null else tableView[CellClasses] = viewMeta.cellClasses
            if (viewMeta.cellTopics == null) tableView[CellTopics] = null else tableView[CellTopics] = viewMeta.cellTopics
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

                    val header = ColumnHeader(headers.map { it.section ?: throw InvalidStorageException("Missing column section") }.reversed())

                    val viewMeta = deserializeViewMeta(inputStream)
                    if (viewMeta.cellWidth == null) tableView[header][CellWidth] = null else tableView[header][CellWidth] = viewMeta.cellWidth
                    if (viewMeta.cellClasses == null) tableView[header][CellClasses] = null else tableView[header][CellClasses] = viewMeta.cellClasses
                    if (viewMeta.cellTopics == null) tableView[header][CellTopics] = null else tableView[header][CellTopics] = viewMeta.cellTopics
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
                if (viewMeta.cellHeight == null) tableView[row][CellHeight] = null else tableView[row][CellHeight] = viewMeta.cellHeight
                if (viewMeta.cellClasses == null) tableView[row][CellClasses] = null else tableView[row][CellClasses] = viewMeta.cellClasses
                if (viewMeta.cellTopics == null) tableView[row][CellTopics] = null else tableView[row][CellTopics] = viewMeta.cellTopics
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

                    val header = ColumnHeader(headers.map { it.section ?: throw InvalidStorageException("Missing column section") }.reversed())
                    val row = viewSection.row ?: throw InvalidStorageException("Missing row section")

                    val viewMeta = deserializeViewMeta(inputStream)
                    if (viewMeta.cellHeight == null) tableView[header, row][CellHeight] = null else tableView[header, row][CellHeight] = viewMeta.cellHeight
                    if (viewMeta.cellWidth == null) tableView[header, row][CellWidth] = null else tableView[header, row][CellWidth] = viewMeta.cellWidth
                    if (viewMeta.cellClasses == null) tableView[header, row][CellClasses] = null else tableView[header, row][CellClasses] = viewMeta.cellClasses
                    if (viewMeta.cellTopics == null) tableView[header, row][CellTopics] = null else tableView[header, row][CellTopics] = viewMeta.cellTopics
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
            val head = columnHeader.header.dropLast(1)
            val tail = columnHeader.header.last()

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

            columnHeader.header.forEach {
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
        val haveHeight = if (viewMeta.cellHeight == null) 0 else   0b10000000
        val haveWidth = if (viewMeta.cellWidth == null) 0 else     0b01000000
        val haveClasses = if (viewMeta.cellClasses == null) 0 else 0b00100000
        val haveTopics = if (viewMeta.cellTopics == null) 0 else   0b00010000

        val options = haveHeight or haveWidth or haveClasses or haveTopics

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

        return baos.toByteArray()
    }
}

private fun deserializeViewMeta(inputStream: InputStream): ViewMeta {
    val haveHeight =  0b10000000
    val haveWidth =   0b01000000
    val haveClasses = 0b00100000
    val haveTopics =  0b00010000

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
            if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta classes length")
            SerializationUtils.toInt(it)
        }
        val topics = mutableSetOf<String>()
        for (i in 0 until topicsLength) {
            val topicLength = ByteArray(Int.SIZE_BYTES).let {
                if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta classes length")
                SerializationUtils.toInt(it)
            }
            topics += ByteArray(topicLength).let {
                if (inputStream.readNBytes(it, 0, it.size) != it.size) throw InvalidStorageException("Unable to read view meta section")
                SerializationUtils.toString(it)
            }
        }
        topics.toImmutableSet()
    } else null

    return ViewMeta(cellHeight, cellWidth, cellClasses, cellTopics)
}
