package sigbla.app

// TODO Example sum below..

fun sum(
    cellRange: CellRange,
    init: Number? = null,
    name: String? = null,
    order: Long = 0L
): DestinationOsmosis<Cell<*>>.() -> Unit = {
    cellRange.on<Any, Number> {
        this.name = name
        this.order = order

        if (init != null)
            destination `=` init
        else
            destination `=` null

        var sum: Number? = null

        source.table[cellRange]
            .asSequence()
            .filter { it.isNumeric() }
            .forEach { sum = it + (sum ?: 0) }

        if (sum != null) destination.table[destination] = sum!!
    }
}