/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import java.io.OutputStream

class NoCloseOutputStream(private val outputStream: OutputStream): OutputStream() {
    override fun write(b: Int) {
        outputStream.write(b)
    }

    override fun write(b: ByteArray) {
        outputStream.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        outputStream.write(b, off, len)
    }

    override fun close() {
        flush()
    }

    override fun flush() {
        outputStream.flush()
    }
}
