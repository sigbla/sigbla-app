package sigbla.app

import java.util.concurrent.atomic.AtomicInteger

// TODO Support more than just CellRange

fun sum(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    predicate: (Cell<*>) -> Boolean = { true }
): Cell<*>.() -> Unit = cellFunction<Any, Number>(cellRange, init, empty, name, order) {
    asSequence()
        .filter { it.isNumeric() && predicate(it) }
        .fold(null as Number?) { sum, num -> num + (sum ?: 0) }
}

fun max(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    predicate: (Cell<*>) -> Boolean = { true }
): Cell<*>.() -> Unit = cellFunction<Any, Number>(cellRange, init, empty, name, order) {
    asSequence()
        .filter { it.isNumeric() && predicate(it) }
        .fold(null as Number?) { max, num -> if (max == null || num > max) num.toNumber() else max }
}

fun min(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    predicate: (Cell<*>) -> Boolean = { true }
): Cell<*>.() -> Unit = cellFunction<Any, Number>(cellRange, init, empty, name, order) {
    asSequence()
        .filter { it.isNumeric() && predicate(it) }
        .fold(null as Number?) { min, num -> if (min == null || num < min) num.toNumber() else min }
}

fun count(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    predicate: (Cell<*>) -> Boolean = { it !is UnitCell }
): Cell<*>.() -> Unit = cellFunction<Any, Any>(cellRange, init, empty, name, order) { count(predicate) }

private inline fun <reified O, reified N> cellFunction(
    cellRange: CellRange,
    init: Number? = null,
    empty: Number? = null,
    name: String? = null,
    order: Long = 0L,
    crossinline calc: Iterable<Cell<*>>.() -> Number?
): Cell<*>.() -> Unit = {
    val cell = this

    on<O, N>(cellRange) {
        val unsubscribeOuter = { off(this) }

        this.name = name
        this.order = order

        cell { init }

        val destinationCount = AtomicInteger()

        events {
            if (cell.table.closed) { unsubscribeOuter() }
            else if (any()) {
                destinationCount.incrementAndGet()
                val newValue = newTable[cellRange].calc()

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