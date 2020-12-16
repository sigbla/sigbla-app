package sigbla.app

import java.util.concurrent.atomic.AtomicInteger

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
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = cellFunction(cellRange, init, name, order) { sum() }

fun max(
    cellRange: CellRange,
    init: Number? = null,
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = cellFunction(cellRange, init, name, order) { max() }

fun min(
    cellRange: CellRange,
    init: Number? = null,
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = cellFunction(cellRange, init, name, order) { min() }

private fun cellFunction(
    cellRange: CellRange,
    init: Number? = null,
    name: String? = null,
    order: Long = 0L,
    calc: Iterable<Cell<*>>.() -> Number?
): DestinationOsmosis<Cell<*>>.() -> Unit = {
    cellRange.on<Any, Number> {
        this.name = name
        this.order = order

        if (init != null) destination `=` init else destination `=` null

        val destinationCount = AtomicInteger()

        events {
            if (any()) {
                destinationCount.incrementAndGet()

                destination `=` (newTable[cellRange].calc() ?: return@events)
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