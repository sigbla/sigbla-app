/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import sigbla.app.exceptions.InvalidRefException

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

    fun refAction(updateFunction: (prev: V) -> V): Pair<V, V> {
        // We assume this is synchronized externally
        val prev = get()
        val next = updateFunction(prev)

        // Dev testing
        //if (prev is sigbla.app.TableRef && next is sigbla.app.TableRef && prev.version >= next.version) throw sigbla.app.exceptions.SigblaAppException()
        // TODO Consider if we want some kind of testing mode for the above..?
        //      Look at using assert() and enable this when running tests, unless there's some performance impact overall also when disabled?

        set(next)
        return Pair(prev, next)
    }

    fun updateAndGet(action: (V) -> V): V {
        // We assume this is synchronized externally
        set(action(get()))
        return get()
    }
}
