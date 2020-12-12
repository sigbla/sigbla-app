package sigbla.app

// TODO sum, min, max, etc on CellRange (and similar)
//      Look at reified stuff, like kotlin.collections.Iterable<kotlin.Int>.sum(): kotlin.Int
//      We can reuse those below..

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

        events {
            if (any()) {
                destination `=` (newTable[cellRange]
                    .asSequence()
                    .filter { it.isNumeric() }
                    .fold(null as Number?) { sum, num -> num + (sum ?: 0) } ?: return@events)
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

        events {
            if (any()) {
                destination `=` (newTable[cellRange]
                    .asSequence()
                    .filter { it.isNumeric() }
                    .fold(null as Number?) { max, num -> if (max == null || num > max) num + 0L else max } ?: return@events)
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

        events {
            if (any()) {
                destination `=` (newTable[cellRange]
                    .asSequence()
                    .filter { it.isNumeric() }
                    .fold(null as Number?) { min, num -> if (min == null || num < min) num - 0L else min } ?: return@events)
            }
        }
    }
}