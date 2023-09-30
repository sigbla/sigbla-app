package sigbla.app.internals

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.MessageDigest

internal enum class SerializationType(val type: Int) {
    NULL(0),
    BOOL(1),
    BYTE(2),
    //SHORT(3),
    INT(4),
    LONG(5),
    //FLOAT(6),
    DOUBLE(7),
    //CHAR(8),
    STRING(9),
    BIGINTEGER(10),
    BIGDECIMAL(11)
}

internal object SerializationUtils {
    private const val LONG_SIZE = java.lang.Long.BYTES
    private const val INT_SIZE = Integer.BYTES

    fun fromLong(value: Long): ByteArray {
        val buffer = ByteBuffer.wrap(ByteArray(LONG_SIZE))
        buffer.putLong(value)
        return buffer.array()
    }

    fun toLong(value: ByteArray): Long {
        return ByteBuffer.wrap(value).getLong()
    }

    fun fromInt(value: Int): ByteArray {
        val buffer = ByteBuffer.wrap(ByteArray(INT_SIZE))
        buffer.putInt(value)
        return buffer.array()
    }

    fun toInt(value: ByteArray): Int {
        return ByteBuffer.wrap(value).getInt()
    }

    fun fromString(value: String): ByteArray {
        return value.toByteArray(charset = Charsets.UTF_8)
    }

    fun toString(value: ByteArray): String {
        return String(value, Charsets.UTF_8)
    }

    fun fromBoolean(value: Boolean): ByteArray {
        return ByteArray(1) { if (value) 1 else 0 }
    }

    fun toBoolean(value: ByteArray): Boolean {
        return value[0] == 1.toByte()
    }

    fun fromType(value: Any?): ByteArray {
        ByteArrayOutputStream().use { baos ->
            when (value) {
                null -> {
                    baos.write(SerializationType.NULL.type)
                }
                is Boolean -> {
                    baos.write(SerializationType.BOOL.type)
                    baos.write(if (value) 1 else 0)
                }

                is Byte -> {
                    baos.write(SerializationType.BYTE.type)
                    baos.write(value.toInt())
                }

                is Int -> {
                    baos.write(SerializationType.INT.type)
                    val buffer = ByteBuffer.wrap(ByteArray(INT_SIZE))
                    buffer.putInt(value)
                    baos.writeBytes(buffer.array())
                }

                is Long -> {
                    baos.write(SerializationType.LONG.type)
                    val buffer = ByteBuffer.wrap(ByteArray(LONG_SIZE))
                    buffer.putLong(value)
                    baos.writeBytes(buffer.array())
                }

                is Double -> {
                    baos.write(SerializationType.DOUBLE.type)
                    val buffer = ByteBuffer.wrap(ByteArray(LONG_SIZE))
                    buffer.putLong(value.toRawBits())
                    baos.writeBytes(buffer.array())
                }

                is String -> {
                    baos.write(SerializationType.STRING.type)
                    val bytes = value.toByteArray(Charsets.UTF_8)
                    baos.write(fromInt(bytes.size))
                    baos.writeBytes(bytes)
                }

                is BigInteger -> {
                    baos.write(SerializationType.BIGINTEGER.type)
                    val bytes = value.toByteArray()
                    baos.write(fromInt(bytes.size))
                    baos.writeBytes(bytes)
                }

                is BigDecimal -> {
                    baos.write(SerializationType.BIGDECIMAL.type)
                    val bytes = value.unscaledValue().toByteArray()
                    val scale = fromInt(value.scale())
                    baos.write(fromInt(bytes.size))
                    baos.writeBytes(bytes)
                    baos.writeBytes(scale)
                }

                else -> throw UnsupportedOperationException("Not supported: ${value.javaClass}")
            }

            return baos.toByteArray()
        }
    }

    fun toType(value: ByteArray): Any? {
        ByteArrayInputStream(value).use { baos ->
            val type = ByteArray(1).let {
                if (baos.read(it) != it.size) throw IllegalArgumentException()
                it[0].toInt()
            }
            return toType(type, baos)
        }
    }

    fun toType(type: Int, inputStream: InputStream): Any? {
        when (type) {
            SerializationType.NULL.type -> return null
            SerializationType.BOOL.type -> {
                val buffer = ByteArray(1).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    ByteBuffer.wrap(it)
                }
                return buffer.get() == 1.toByte()
            }
            SerializationType.BYTE.type -> {
                val buffer = ByteArray(1).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    ByteBuffer.wrap(it)
                }
                return buffer.get()
            }
            SerializationType.INT.type -> {
                val buffer = ByteArray(Int.SIZE_BYTES).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    ByteBuffer.wrap(it)
                }
                return buffer.getInt()
            }
            SerializationType.LONG.type -> {
                val buffer = ByteArray(Long.SIZE_BYTES).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    ByteBuffer.wrap(it)
                }
                return buffer.getLong()
            }
            SerializationType.DOUBLE.type -> {
                val buffer = ByteArray(Long.SIZE_BYTES).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    ByteBuffer.wrap(it)
                }
                return Double.fromBits(buffer.getLong())
            }
            SerializationType.STRING.type -> {
                val buffer = ByteArray(Int.SIZE_BYTES).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    ByteBuffer.wrap(it)
                }
                val length = buffer.getInt()
                if (length == 0) return ""
                val string = ByteArray(length).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    it
                }
                return String(string, Charsets.UTF_8)
            }
            SerializationType.BIGINTEGER.type -> {
                val buffer = ByteArray(Int.SIZE_BYTES).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    ByteBuffer.wrap(it)
                }
                val length = buffer.getInt()
                val bigint = ByteArray(length).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    it
                }
                return BigInteger(bigint)
            }
            SerializationType.BIGDECIMAL.type -> {
                val buffer1 = ByteArray(Int.SIZE_BYTES).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    ByteBuffer.wrap(it)
                }
                val length = buffer1.getInt()
                val bigdecimal = ByteArray(length).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    it
                }
                val buffer2 = ByteArray(Int.SIZE_BYTES).let {
                    if (inputStream.readNBytes(it, 0, it.size) != it.size) throw IllegalArgumentException()
                    ByteBuffer.wrap(it)
                }
                val scale = buffer2.getInt()
                return BigDecimal(BigInteger(bigdecimal), scale)
            }
            else -> throw IllegalArgumentException("Unsupported type $type")
        }
    }
}

internal fun shortHash(a: Long, b: String): Long {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(SerializationUtils.fromLong(a))
    val hash = digest.digest(SerializationUtils.fromString(b))
    return SerializationUtils.toLong(hash)
}
