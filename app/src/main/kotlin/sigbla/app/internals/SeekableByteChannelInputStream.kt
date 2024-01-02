/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class SeekableByteChannelInputStream(private val sbc: SeekableByteChannel, length: Long) : InputStream() {
    private val defaultBufferSize = 8192
    private val buffer = ByteBuffer.allocate(defaultBufferSize).limit(0)
    private val maxPosition = sbc.position() + length

    private fun preload(): Int {
        if (buffer.position() == buffer.limit()) {
            val newLimit = (maxPosition - sbc.position()).coerceAtMost(buffer.capacity().toLong())
            buffer.clear().limit(newLimit.toInt())
            val r = sbc.read(buffer)
            if (r <= 0) return -1 else buffer.flip()
        }
        if (buffer.position() == buffer.limit()) {
            return -1
        }

        return buffer.remaining()
    }

    override fun read(): Int {
        if (preload() < 0) return -1
        return buffer.get().toUByte().toInt()
    }

    override fun read(b: ByteArray): Int {
        return read(b, 0, b.size)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (preload() < 0) return -1
        val actualLength = buffer.remaining().coerceAtMost(len)
        buffer.get(b, off, actualLength)
        return actualLength
    }
}
