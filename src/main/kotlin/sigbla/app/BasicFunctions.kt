package sigbla.app

import java.util.concurrent.atomic.AtomicInteger

// TODO Introduce a predicate on all these functions..

fun Iterable<Cell<*>>.sum(): Number? = asSequence()
    .filter { it.isNumeric() }
    .fold(null as Number?) { sum, num -> num + (sum ?: 0) }

fun Iterable<Cell<*>>.max(): Number? = asSequence()
    .filter { it.isNumeric() }
    .fold(null as Number?) { max, num -> if (max == null || num > max) num.toNumber() else max }

fun Iterable<Cell<*>>.min(): Number? = asSequence()
    .filter { it.isNumeric() }
    .fold(null as Number?) { min, num -> if (min == null || num < min) num.toNumber() else min }

fun sum(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = cellFunction<Any, Number>(cellRange, init, empty, name, order) { sum() }

fun max(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = cellFunction<Any, Number>(cellRange, init, empty, name, order) { max() }

fun min(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = cellFunction<Any, Number>(cellRange, init, empty, name, order) { min() }

fun count(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    predicate: (Cell<*>) -> Boolean = { it !is UnitCell }
): DestinationOsmosis<Cell<*>>.() -> Unit = cellFunction<Any, Any>(cellRange, init, empty, name, order) { count(predicate) }

private inline fun <reified O, reified N> cellFunction(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    crossinline calc: Iterable<Cell<*>>.() -> Number?
): DestinationOsmosis<Cell<*>>.() -> Unit = {
    cellRange.on<O, N> {
        this.name = name
        this.order = order

        if (init != null) destination `=` init else destination `=` null

        val destinationCount = AtomicInteger()

        events {
            if (any()) {
                destinationCount.incrementAndGet()
                val newValue = newTable[cellRange].calc()

                when {
                    newValue != null -> destination `=` newValue
                    empty != null -> destination `=` empty
                    else -> destination `=` null
                }
            }
        }

        this.let { target ->
            destination.onAny {
                this.skipHistory = true
                this.name = "Unsubscriber for $name"
                this.order = order

                events {
                    if (any() && destinationCount.decrementAndGet() < 0) {
                        // Something else is interactive with destination,
                        // so let's remove this function.
                        target.reference.unsubscribe()
                        reference.unsubscribe()
                    }
                }
            }
        }
    }
}