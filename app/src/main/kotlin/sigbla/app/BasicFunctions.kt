package sigbla.app

import java.util.concurrent.atomic.AtomicInteger

fun sum(
    vararg cells: Iterable<Cell<*>>,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    predicate: (Cell<*>) -> Boolean = { true }
): Cell<*>.() -> Unit = cellFunction<Any, Number>(cells = cells, init, empty, name, order) {
    filter { it.isNumeric() && predicate(it) }
        .fold(null as Number?) { sum, num -> num + (sum ?: 0) }
}

fun max(
    vararg cells: Iterable<Cell<*>>,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    predicate: (Cell<*>) -> Boolean = { true }
): Cell<*>.() -> Unit = cellFunction<Any, Number>(cells = cells, init, empty, name, order) {
    filter { it.isNumeric() && predicate(it) }
        .fold(null as Number?) { max, num -> if (max == null || num > max) num.toNumber() else max }
}

fun min(
    vararg cells: Iterable<Cell<*>>,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    predicate: (Cell<*>) -> Boolean = { true }
): Cell<*>.() -> Unit = cellFunction<Any, Number>(cells = cells, init, empty, name, order) {
    filter { it.isNumeric() && predicate(it) }
        .fold(null as Number?) { min, num -> if (min == null || num < min) num.toNumber() else min }
}

fun count(
    vararg cells: Iterable<Cell<*>>,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    predicate: (Cell<*>) -> Boolean = { it !is UnitCell }
): Cell<*>.() -> Unit = cellFunction<Any, Any>(cells = cells, init, empty, name, order) { count(predicate) }

private inline fun <reified O, reified N> cellFunction(
    vararg cells: Iterable<Cell<*>>,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    crossinline calc: Sequence<Cell<*>>.() -> Number?
): Cell<*>.() -> Unit = {
    val cells = Cells(*cells)
    val cell = this

    on<O, N>(cells) {
        val unsubscribeOuter = { off(this) }

        this.name = name
        this.order = order

        cell { init }

        val destinationCount = AtomicInteger()

        events {
            if (cell.table.closed) { unsubscribeOuter() }
            else if (any()) {
                destinationCount.incrementAndGet()
                val newValue = cells.asSequence().map { newTable[it] }.calc()

                when {
                    newValue != null -> cell { newValue }
                    empty != null -> cell { empty }
                    else -> cell { null }
                }
            }
        }

        on(cell) {
            val unsubscribeInner = { off(this) }

            this.skipHistory = true
            this.name = "Unsubscriber for $name"
            this.order = order

            events {
                if (any() && destinationCount.decrementAndGet() < 0) {
                    // Something else is interacting with destination,
                    // so let's remove this function.
                    unsubscribeOuter()
                    unsubscribeInner()
                }
            }
        }
    }
}