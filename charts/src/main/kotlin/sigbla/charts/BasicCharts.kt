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
import com.beust.klaxon.Klaxon
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


class LineChartConfig(
    var type: String,
    var data: Data,
    var options: Options = Options()
) {
    class Data(
        var labels: List<String>,
        var datasets: List<Dataset>
    ) {
        class Dataset(
            var label: String,
            var data: List<Double>,
            var borderWidth: Int = 1
        )
    }

    class Options(
        var scales: Scales = Scales(),
        var responsive: Boolean = true,
        var maintainAspectRatio: Boolean = false,
        var animation: Animation = Animation(),
        var plugins: Plugins = Plugins()
    ) {
        class Scales(
            var y: Y = Y()
        ) {
            class Y(
                var beginAtZero: Boolean = true
            )
        }

        class Animation(
            var duration: Int = 0
        )

        class Plugins(
            var title: Title = Title()
        ) {
            class Title(
                var display: Boolean = false,
                var text: String = ""
            )
        }
    }
}

class BarChartConfig(
    var type: String,
    var data: Data,
    var options: Options = Options()
) {
    class Data(
        var labels: List<String>,
        var datasets: List<Dataset>
    ) {
        class Dataset(
            var label: String,
            var data: List<Double>,
            var borderWidth: Int = 1
        )
    }

    class Options(
        var scales: Scales = Scales(),
        var responsive: Boolean = true,
        var maintainAspectRatio: Boolean = false,
        var animation: Animation = Animation(),
        var plugins: Plugins = Plugins()
    ) {
        class Scales(
            var y: Y = Y()
        ) {
            class Y(
                var beginAtZero: Boolean = true
            )
        }

        class Animation(
            var duration: Int = 0
        )

        class Plugins(
            var title: Title = Title()
        ) {
            class Title(
                var display: Boolean = false,
                var text: String = ""
            )
        }
    }
}

fun line(
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>,
    configurator: LineChartConfig.() -> Unit = {}
): CellView.() -> Unit = line(null, labels, *datasets, configurator = configurator)

fun line(
    title: Cell<*>?,
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>,
    configurator: LineChartConfig.() -> Unit = {}
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val labelStrings = labels.table[labels].map(Cell<*>::toString)

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].mapNotNull(Cell<*>::asDouble)
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
    labels: List<String>,
    vararg datasets: Pair<String, List<Double>>,
    configurator: LineChartConfig.() -> Unit = {}
): CellView.() -> Unit = line(null, labels, *datasets, configurator = configurator)

fun line(
    title: String?,
    labels: List<String>,
    vararg datasets: Pair<String, List<Double>>,
    configurator: LineChartConfig.() -> Unit = {}
): CellView.() -> Unit = {
    val cellView = this

    batch (cellView.tableView) {
        val chartConfig = LineChartConfig(
            type = "line",
            options = LineChartConfig.Options(
                plugins = LineChartConfig.Options.Plugins(
                    title = LineChartConfig.Options.Plugins.Title(
                        display = !title.isNullOrBlank(),
                        text = title ?: ""
                    )
                )
            ),
            data = LineChartConfig.Data(
                labels = labels,
                datasets = datasets.map {
                    LineChartConfig.Data.Dataset(
                        label = it.first,
                        data = it.second
                    )
                }
            )
        )

        configurator(chartConfig)
        val chartConfigJson = Klaxon().toJsonString(chartConfig)

        val callback = "sigbla/charts/${chartCounter.getAndIncrement()}"

        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            if (call.request.httpMethod == HttpMethod.Get) {
                call.respondText { chartConfigJson }
            }
        }

        cellView.tableView[Resource[callback]] = handler
        cellView.tableView[Resource["chartjs/charts.js"]] = jsResource("/chartjs/chart.js")
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

fun bar(
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>
): CellView.() -> Unit = bar(null, labels, *datasets)

fun bar(
    title: Cell<*>?,
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>
): CellView.() -> Unit = {
    val listenerRefs = mutableListOf<TableListenerReference>()

    fun update() {
        synchronized(listenerRefs) {
            val titleString = if (title == null) null else title.table[title].let {
                if (it is UnitCell) null else it.toString()
            }

            val labelStrings = labels.table[labels].map(Cell<*>::toString)

            val datasetValues = datasets.map {
                it.first to it.second.table[it.second].mapNotNull(Cell<*>::asDouble)
            }

            bar(titleString, labelStrings, *datasetValues.toTypedArray())()
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
    labels: List<String>,
    vararg datasets: Pair<String, List<Double>>
): CellView.() -> Unit = bar(null, labels, *datasets)

fun bar(
    title: String?,
    labels: List<String>,
    vararg datasets: Pair<String, List<Double>>
): CellView.() -> Unit = {
    val cellView = this

    batch(cellView.tableView) {
        val chartConfig = BarChartConfig(
            type = "bar",
            options = BarChartConfig.Options(
                plugins = BarChartConfig.Options.Plugins(
                    title = BarChartConfig.Options.Plugins.Title(
                        display = !title.isNullOrBlank(),
                        text = title ?: ""
                    )
                )
            ),
            data = BarChartConfig.Data(
                labels = labels,
                datasets = datasets.map {
                    BarChartConfig.Data.Dataset(
                        label = it.first,
                        data = it.second
                    )
                }
            )
        )

        val callback = "sigbla/charts/${chartCounter.getAndIncrement()}"

        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            if (call.request.httpMethod == HttpMethod.Get) {
                call.respondText { Klaxon().toJsonString(chartConfig) }
            }
        }

        cellView.tableView[Resource[callback]] = handler
        cellView.tableView[Resource["chartjs/charts.js"]] = jsResource("/chartjs/chart.js")
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
