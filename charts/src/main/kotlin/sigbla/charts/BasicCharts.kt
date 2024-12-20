/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.charts

import sigbla.app.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.html.canvas
import java.util.concurrent.atomic.AtomicLong

private val chartCounter = AtomicLong()

fun chart(
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = {
    val cellView = this

    batch (cellView.tableView) {
        val chartConfigJson = StringBuffer().let {
            var model = ChartModel()
            configurator(model)
            model.serialize(it)
            it.toString()
        }

        val callback = "sigbla/charts/${chartCounter.getAndIncrement()}"

        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            if (call.request.httpMethod == HttpMethod.Get) {
                call.respondText { chartConfigJson }
            }
        }

        cellView.tableView[Resource[callback]] = handler

        cellView.tableView[Resource["chartjs/charts.js"]] = jsResource("/chartjs/chart.js")
        cellView.tableView[Resource["chartjs/chartjs-adapter-date-fns.bundle.min.js"]] = jsResource("/chartjs/chartjs-adapter-date-fns.bundle.min.js")

        cellView.tableView[Resource["sigbla/charts.css"]] = cssResource("/sigbla/charts.css")
        cellView.tableView[Resource["sigbla/charts.js"]] = jsResource("/sigbla/charts.js")

        val transformer = div("sigbla-charts") {
            attributes["callback"] = callback
            canvas {}
        }

        cellView[CellTransformer] = transformer

        cellView[CellTopics].apply { this(this + "sigbla-charts") }

        on(cellView) {
            val unsubscribe = { off(this) }
            skipHistory = true
            events {
                if (any() && source.tableView[source][CellTransformer].function != transformer) {
                    // clean up
                    unsubscribe()
                    source.tableView[Resource[callback]] = Unit
                }
            }
        }
    }
}

fun line(
    title: Cell<*>?,
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val labelStrings = labels.table[labels].map(Cell<*>::toString)

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].map(Cell<*>::asDouble)
            }

            line(titleString, labelStrings, *datasetValues.toTypedArray(), configurator = configurator)()
        }
    }

    synchronized(listenerRefs) {
        if (title != null) listenerRefs += on(title) {
            skipHistory = true
            events { if (any()) update() }
        }

        listenerRefs += on(labels) {
            skipHistory = true
            events { if (any()) update() }
        }

        datasets.forEach {
            listenerRefs += on(it.second) {
                skipHistory = true
                events { if (any()) update() }
            }
        }

        update()
    }
}

fun line(
    title: String?,
    labels: List<String>,
    vararg datasets: Pair<String, List<Double?>>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = chart {
    type = ChartType.Line
    data = ChartModel.Data(
        labels = Strings(labels),
        datasets = ChartModel.Data.Datasets(
            datasets.map {
                ChartModel.Data.Datasets.Dataset(
                    label = Text(it.first),
                    data = Numbers(it.second)
                )
            }
        )
    )
    options = ChartModel.Options(
        responsive = Bool.True,
        plugins = ChartModel.Options.Plugins(
            title = if (title != null) ChartModel.Options.Plugins.Title(
                display = Bool.True,
                text = Text(title)
            ) else null
        ),
        animation = ChartModel.Options.Animation(
            duration = Numeric(0)
        )
    )

    configurator()
}

fun bar(
    title: Cell<*>?,
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val labelStrings = labels.table[labels].map(Cell<*>::toString)

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].map(Cell<*>::asDouble)
            }

            bar(titleString, labelStrings, *datasetValues.toTypedArray(), configurator = configurator)()
        }
    }

    synchronized(listenerRefs) {
        if (title != null) listenerRefs += on(title) {
            skipHistory = true
            events { if (any()) update() }
        }

        listenerRefs += on(labels) {
            skipHistory = true
            events { if (any()) update() }
        }

        datasets.forEach {
            listenerRefs += on(it.second) {
                skipHistory = true
                events { if (any()) update() }
            }
        }

        update()
    }
}

fun bar(
    title: String?,
    labels: List<String>,
    vararg datasets: Pair<String, List<Double?>>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = chart {
    type = ChartType.Bar
    data = ChartModel.Data(
        labels = Strings(labels),
        datasets = ChartModel.Data.Datasets(
            datasets.map {
                ChartModel.Data.Datasets.Dataset(
                    label = Text(it.first),
                    data = Numbers(it.second)
                )
            }
        )
    )
    options = ChartModel.Options(
        responsive = Bool.True,
        plugins = ChartModel.Options.Plugins(
            title = if (title != null) ChartModel.Options.Plugins.Title(
                display = Bool.True,
                text = Text(title)
            ) else null
        ),
        animation = ChartModel.Options.Animation(
            duration = Numeric(0)
        )
    )

    configurator()
}

fun bubble(
    title: Cell<*>?,
    vararg datasets: Pair<String, CellRange>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].map(Cell<*>::asDouble)
            }

            bubble(titleString, *datasetValues.toTypedArray(), configurator = configurator)()
        }
    }

    synchronized(listenerRefs) {
        if (title != null) listenerRefs += on(title) {
            skipHistory = true
            events { if (any()) update() }
        }

        datasets.forEach {
            listenerRefs += on(it.second) {
                skipHistory = true
                events { if (any()) update() }
            }
        }

        update()
    }
}

fun bubble(
    title: String?,
    vararg datasets: Pair<String, List<Double?>>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = chart {
    type = ChartType.Bubble
    data = ChartModel.Data(
        datasets = ChartModel.Data.Datasets(
            datasets.map { dataset ->
                ChartModel.Data.Datasets.Dataset(
                    label = Text(dataset.first),
                    data = Complex(dataset.second.chunked(3).map { v ->
                        listOfNotNull(
                            v.getOrNull(0)?.let { "x" to Numeric(it) },
                            v.getOrNull(1)?.let { "y" to Numeric(it) },
                            v.getOrNull(2)?.let { "r" to Numeric(it) }
                        ).toMap()
                    })
                )
            }
        )
    )
    options = ChartModel.Options(
        responsive = Bool.True,
        plugins = ChartModel.Options.Plugins(
            title = if (title != null) ChartModel.Options.Plugins.Title(
                display = Bool.True,
                text = Text(title)
            ) else null
        ),
        animation = ChartModel.Options.Animation(
            duration = Numeric(0)
        )
    )

    configurator()
}

fun scatter(
    title: Cell<*>?,
    vararg datasets: Pair<String, CellRange>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].map(Cell<*>::asDouble)
            }

            scatter(titleString, *datasetValues.toTypedArray(), configurator = configurator)()
        }
    }

    synchronized(listenerRefs) {
        if (title != null) listenerRefs += on(title) {
            skipHistory = true
            events { if (any()) update() }
        }

        datasets.forEach {
            listenerRefs += on(it.second) {
                skipHistory = true
                events { if (any()) update() }
            }
        }

        update()
    }
}

fun scatter(
    title: String?,
    vararg datasets: Pair<String, List<Double?>>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = chart {
    type = ChartType.Scatter
    data = ChartModel.Data(
        datasets = ChartModel.Data.Datasets(
            datasets.map { dataset ->
                ChartModel.Data.Datasets.Dataset(
                    label = Text(dataset.first),
                    data = Complex(dataset.second.chunked(2).map { v ->
                        listOfNotNull(
                            v.getOrNull(0)?.let { "x" to Numeric(it) },
                            v.getOrNull(1)?.let { "y" to Numeric(it) }
                        ).toMap()
                    })
                )
            }
        )
    )
    options = ChartModel.Options(
        responsive = Bool.True,
        plugins = ChartModel.Options.Plugins(
            title = if (title != null) ChartModel.Options.Plugins.Title(
                display = Bool.True,
                text = Text(title)
            ) else null
        ),
        animation = ChartModel.Options.Animation(
            duration = Numeric(0)
        )
    )

    configurator()
}

fun doughnut(
    title: Cell<*>?,
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val labelStrings = labels.table[labels].map(Cell<*>::toString)

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].map(Cell<*>::asDouble)
            }

            doughnut(titleString, labelStrings, *datasetValues.toTypedArray(), configurator = configurator)()
        }
    }

    synchronized(listenerRefs) {
        if (title != null) listenerRefs += on(title) {
            skipHistory = true
            events { if (any()) update() }
        }

        listenerRefs += on(labels) {
            skipHistory = true
            events { if (any()) update() }
        }

        datasets.forEach {
            listenerRefs += on(it.second) {
                skipHistory = true
                events { if (any()) update() }
            }
        }

        update()
    }
}

fun doughnut(
    title: String?,
    labels: List<String>,
    vararg datasets: Pair<String, List<Double?>>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = chart {
    type = ChartType.Doughnut
    data = ChartModel.Data(
        labels = Strings(labels),
        datasets = ChartModel.Data.Datasets(
            datasets.map {
                ChartModel.Data.Datasets.Dataset(
                    label = Text(it.first),
                    data = Numbers(it.second)
                )
            }
        )
    )
    options = ChartModel.Options(
        responsive = Bool.True,
        plugins = ChartModel.Options.Plugins(
            title = if (title != null) ChartModel.Options.Plugins.Title(
                display = Bool.True,
                text = Text(title)
            ) else null
        ),
        animation = ChartModel.Options.Animation(
            duration = Numeric(0)
        )
    )

    configurator()
}

fun pie(
    title: Cell<*>?,
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val labelStrings = labels.table[labels].map(Cell<*>::toString)

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].map(Cell<*>::asDouble)
            }

            pie(titleString, labelStrings, *datasetValues.toTypedArray(), configurator = configurator)()
        }
    }

    synchronized(listenerRefs) {
        if (title != null) listenerRefs += on(title) {
            skipHistory = true
            events { if (any()) update() }
        }

        listenerRefs += on(labels) {
            skipHistory = true
            events { if (any()) update() }
        }

        datasets.forEach {
            listenerRefs += on(it.second) {
                skipHistory = true
                events { if (any()) update() }
            }
        }

        update()
    }
}

fun pie(
    title: String?,
    labels: List<String>,
    vararg datasets: Pair<String, List<Double?>>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = chart {
    type = ChartType.Pie
    data = ChartModel.Data(
        labels = Strings(labels),
        datasets = ChartModel.Data.Datasets(
            datasets.map {
                ChartModel.Data.Datasets.Dataset(
                    label = Text(it.first),
                    data = Numbers(it.second)
                )
            }
        )
    )
    options = ChartModel.Options(
        responsive = Bool.True,
        plugins = ChartModel.Options.Plugins(
            title = if (title != null) ChartModel.Options.Plugins.Title(
                display = Bool.True,
                text = Text(title)
            ) else null
        ),
        animation = ChartModel.Options.Animation(
            duration = Numeric(0)
        )
    )

    configurator()
}

fun polarArea(
    title: Cell<*>?,
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val labelStrings = labels.table[labels].map(Cell<*>::toString)

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].map(Cell<*>::asDouble)
            }

            polarArea(titleString, labelStrings, *datasetValues.toTypedArray(), configurator = configurator)()
        }
    }

    synchronized(listenerRefs) {
        if (title != null) listenerRefs += on(title) {
            skipHistory = true
            events { if (any()) update() }
        }

        listenerRefs += on(labels) {
            skipHistory = true
            events { if (any()) update() }
        }

        datasets.forEach {
            listenerRefs += on(it.second) {
                skipHistory = true
                events { if (any()) update() }
            }
        }

        update()
    }
}

fun polarArea(
    title: String?,
    labels: List<String>,
    vararg datasets: Pair<String, List<Double?>>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = chart {
    type = ChartType.PolarArea
    data = ChartModel.Data(
        labels = Strings(labels),
        datasets = ChartModel.Data.Datasets(
            datasets.map {
                ChartModel.Data.Datasets.Dataset(
                    label = Text(it.first),
                    data = Numbers(it.second)
                )
            }
        )
    )
    options = ChartModel.Options(
        responsive = Bool.True,
        plugins = ChartModel.Options.Plugins(
            title = if (title != null) ChartModel.Options.Plugins.Title(
                display = Bool.True,
                text = Text(title)
            ) else null
        ),
        animation = ChartModel.Options.Animation(
            duration = Numeric(0)
        )
    )

    configurator()
}

fun radar(
    title: Cell<*>?,
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val labelStrings = labels.table[labels].map(Cell<*>::toString)

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].map(Cell<*>::asDouble)
            }

            radar(titleString, labelStrings, *datasetValues.toTypedArray(), configurator = configurator)()
        }
    }

    synchronized(listenerRefs) {
        if (title != null) listenerRefs += on(title) {
            skipHistory = true
            events { if (any()) update() }
        }

        listenerRefs += on(labels) {
            skipHistory = true
            events { if (any()) update() }
        }

        datasets.forEach {
            listenerRefs += on(it.second) {
                skipHistory = true
                events { if (any()) update() }
            }
        }

        update()
    }
}

fun radar(
    title: String?,
    labels: List<String>,
    vararg datasets: Pair<String, List<Double?>>,
    configurator: ChartModel.() -> Unit = {}
): CellView.() -> Unit = chart {
    type = ChartType.Radar
    data = ChartModel.Data(
        labels = Strings(labels),
        datasets = ChartModel.Data.Datasets(
            datasets.map {
                ChartModel.Data.Datasets.Dataset(
                    label = Text(it.first),
                    data = Numbers(it.second)
                )
            }
        )
    )
    options = ChartModel.Options(
        responsive = Bool.True,
        plugins = ChartModel.Options.Plugins(
            title = if (title != null) ChartModel.Options.Plugins.Title(
                display = Bool.True,
                text = Text(title)
            ) else null
        ),
        animation = ChartModel.Options.Animation(
            duration = Numeric(0)
        )
    )

    configurator()
}
