package com.sigbla.prosheet.internals

import com.sigbla.prosheet.internals.bohmap.Binary;
import com.sigbla.prosheet.internals.serialization.SerializationUtils
import com.sigbla.prosheet.table.BaseTable
import com.sigbla.prosheet.table.Cell
import com.sigbla.prosheet.table.toCell
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.UnsupportedOperationException
import java.math.BigDecimal
import java.math.BigInteger

fun BaseTable.serialize(stream: OutputStream) {
    TODO()
}

fun BaseTable.Companion.deserialize(stream: InputStream): BaseTable {
    TODO()
}

// Cell type registry:

private const val STRING_CELL_TYPE = 1
private const val DOUBLE_CELL_TYPE = 2
private const val LONG_CELL_TYPE = 3
private const val BIGINTEGER_CELL_TYPE = 4
private const val BIGDECIMAL_CELL_TYPE = 5

internal fun Cell<*>.toBinary() : Binary {
    // TODO: Format should be:
    // 1 byte: Type of cell
    // X bytes (optional): length of value
    // X bytes: value

    // The length is one or more unsigned bytes, together summing up to the length of the content.
    // If the content happens to have a length exactly equal to the max value of one unsigned byte,
    // then a final zero is also written.

    ByteArrayOutputStream().use {
        baos ->
        when (val value = this.value) {
            is String -> {
                val bytes = value.toByteArray(Charsets.UTF_8)

                // Write the cell type
                baos.write(STRING_CELL_TYPE)

                // Write the length
                writeLength(baos, bytes)

                // Write content
                baos.writeBytes(bytes)
            }
            is Double -> {
                val bytes = SerializationUtils.fromLong(value.toRawBits())

                // Write the cell type
                baos.write(DOUBLE_CELL_TYPE)

                // Write content (fixed length)
                baos.writeBytes(bytes)
            }
            is Long -> {
                val bytes = SerializationUtils.fromLong(value)

                // Write the cell type
                baos.write(LONG_CELL_TYPE)

                // Write content (fixed length)
                baos.writeBytes(bytes)
            }
            is BigInteger -> {
                val bytes = value.toByteArray()

                // Write the cell type
                baos.write(BIGINTEGER_CELL_TYPE)

                // Write the length
                writeLength(baos, bytes)

                // Write content
                baos.writeBytes(bytes)
            }
            is BigDecimal -> {
                val bytes = value.unscaledValue().toByteArray()
                val scale = SerializationUtils.fromInt(value.scale())

                // Write the cell type
                baos.write(BIGDECIMAL_CELL_TYPE)

                // Write the length
                writeLength(baos, bytes)

                // Write content
                baos.writeBytes(bytes)
                baos.writeBytes(scale)
            }
            else -> throw UnsupportedOperationException("Not supported: ${this.javaClass}")
        }

        return Binary(baos.toByteArray())
    }
}

internal fun Binary.toCell(index: Long) : Cell<*> {
    ByteArrayInputStream(this.value).use {
        bais ->
        return when (val type = bais.read()) {
            STRING_CELL_TYPE -> {
                val length = readLength(bais)
                val bytes = ByteArray(length)
                bais.read(bytes)
                bytes.toString(Charsets.UTF_8).toCell(index)
            }
            DOUBLE_CELL_TYPE -> {
                val bytes = ByteArray(SerializationUtils.LONG_SIZE)
                bais.read(bytes)
                Double.fromBits(SerializationUtils.toLong(bytes)).toCell(index)
            }
            LONG_CELL_TYPE -> {
                val bytes = ByteArray(SerializationUtils.LONG_SIZE)
                bais.read(bytes)
                SerializationUtils.toLong(bytes).toCell(index)
            }
            BIGINTEGER_CELL_TYPE -> {
                val length = readLength(bais)
                val bytes = ByteArray(length)
                bais.read(bytes)
                BigInteger(bytes).toCell(index)
            }
            BIGDECIMAL_CELL_TYPE -> {
                val length = readLength(bais)
                val b1 = ByteArray(length)
                val b2 = ByteArray(SerializationUtils.INT_SIZE)

                bais.read(b1)
                bais.read(b2)

                BigDecimal(BigInteger(b1), SerializationUtils.toInt(b2)).toCell(index)
            }
            else -> throw UnsupportedOperationException("Unknown type: $type")
        }
    }
}

private fun writeLength(baos: ByteArrayOutputStream, bytes: ByteArray) {
    baos.write(SerializationUtils.fromInt(bytes.size))
}

private fun readLength(bais: ByteArrayInputStream): Int {
    val a = ByteArray(SerializationUtils.INT_SIZE)
    bais.read(a)
    return SerializationUtils.toInt(a)
}
