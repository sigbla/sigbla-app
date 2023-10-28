package sigbla.app.internals

import sigbla.app.exceptions.SigblaAppException

internal class RefHolder<V>(@Volatile private var ref: V) {
    private val local = ThreadLocal<V>()

    fun useLocal() {
        local.set(get())
    }

    fun commitLocal() {
        ref = local.get() ?: throw SigblaAppException("No local")
        local.remove()
    }

    fun clearLocal() {
        local.remove()
    }

    fun get(): V {
        return local.get() ?: ref
    }

    fun set(ref: V) {
        if (local.get() != null) local.set(ref)
        else this.ref = ref
    }

    fun refAction(updateFunction: (prev: V) -> V): Pair<V, V> {
        // We assume this is synchronized externally
        val prev = get()
        val next = updateFunction(prev)
        set(next)
        return Pair(prev, next)
    }

    fun updateAndGet(action: (V) -> V): V {
        // We assume this is synchronized externally
        set(action(get()))
        return get()
    }
}