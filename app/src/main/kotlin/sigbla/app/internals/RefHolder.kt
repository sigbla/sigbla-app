/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import sigbla.app.TableRef
import sigbla.app.TableViewRef
import sigbla.app.exceptions.InvalidRefException
import java.lang.UnsupportedOperationException

internal class RefHolder<V>(@Volatile private var ref: V) {
    private val local = ThreadLocal<V>()
    var closed: Boolean = false

    fun useLocal() {
        local.set(get())
    }

    fun commitLocal() {
        ref = local.get() ?: throw InvalidRefException("No local")
        local.remove()
    }

    fun clearLocal() {
        local.remove()
    }

    fun get(): V {
        return local.get() ?: ref
    }

    fun set(ref: V) {
        if (closed) throw InvalidRefException("Reference is closed")
        if (local.get() != null) local.set(ref)
        else this.ref = ref
    }

    private fun refActionAssert(prev: V, next: V) {
        if (prev === next) return

        val versionIncrease = when (prev) {
            is TableRef -> prev.version < (next as TableRef).version
            is TableViewRef -> prev.version < (next as TableViewRef).version
            else -> throw UnsupportedOperationException(prev!!::class.toString())
        }

        if (!versionIncrease) {
            throw AssertionError("Version not increased")
        }
    }

    fun refAction(updateFunction: (prev: V) -> V): Pair<V, V> {
        // We assume this is synchronized externally
        val prev = get()
        val next = updateFunction(prev)

        // Run with java -Dsigbla.assert=true to enable this while testing
        if (enableAssert) {
            refActionAssert(prev, next)
        }

        set(next)
        return Pair(prev, next)
    }

    fun updateAndGet(action: (V) -> V): V {
        // We assume this is synchronized externally
        set(action(get()))
        return get()
    }
}
