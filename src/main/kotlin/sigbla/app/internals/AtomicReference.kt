package sigbla.app.internals

import java.util.concurrent.atomic.AtomicReference

fun <V> AtomicReference<V>.refAction(updateFunction: (prev: V) -> V): Pair<V, V> {
    var prev: V = get()
    var next: V = updateFunction(prev)

    while (true) {
        if (weakCompareAndSetVolatile(prev, next)) return Pair(prev, next)

        val newPrev = get()
        if (prev !== newPrev) next = updateFunction(newPrev.also { prev = it })
    }
}
