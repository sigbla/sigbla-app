package sigbla.app

import java.util.concurrent.atomic.AtomicInteger

// TODO sum, min, max, etc on CellRange (and similar)
//      Look at reified stuff, like kotlin.collections.Iterable<kotlin.Int>.sum(): kotlin.Int
//      We can reuse those below..

// TODO Look at refactoring below functions into a generic helper function..

fun sum(
    cellRange: CellRange,
    init: Number? = null,
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = {
    cellRange.on<Any, Number> {
        this.name = name
        this.order = order

        if (init != null) destination `=` init else destination `=` null

        val destinationCount = AtomicInteger()

        events {
            if (any()) {
                destinationCount.incrementAndGet()

                destination `=` (newTable[cellRange]
                    .asSequence()
                    .filter { it.isNumeric() }
                    .fold(null as Number?) { sum, num -> num + (sum ?: 0) } ?: return@events)
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

fun max(
    cellRange: CellRange,
    init: Number? = null,
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = {
    cellRange.on<Any, Number> {
        this.name = name
        this.order = order

        if (init != null) destination `=` init else destination `=` null

        val destinationCount = AtomicInteger()

        events {
            if (any()) {
                destinationCount.incrementAndGet()

                destination `=` (newTable[cellRange]
                    .asSequence()
                    .filter { it.isNumeric() }
                    .fold(null as Number?) { max, num -> if (max == null || num > max) num + 0L else max } ?: return@events)
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

fun min(
    cellRange: CellRange,
    init: Number? = null,
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = {
    cellRange.on<Any, Number> {
        this.name = name
        this.order = order

        if (init != null) destination `=` init else destination `=` null

        val destinationCount = AtomicInteger()

        events {
            if (any()) {
                destinationCount.incrementAndGet()

                destination `=` (newTable[cellRange]
                    .asSequence()
                    .filter { it.isNumeric() }
                    .fold(null as Number?) { min, num -> if (min == null || num < min) num - 0L else min } ?: return@events)
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