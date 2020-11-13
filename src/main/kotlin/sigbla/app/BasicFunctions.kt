package sigbla.app

// TODO Example sum below.. needs test

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
                destination `=` (source.table[cellRange]
                    .asSequence()
                    .filter { it.isNumeric() }
                    .fold(null as Number?) { sum, num -> num + (sum ?: 0) } ?: return@events)
            }
        }
    }
}