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

internal class ChartConfig(
    val type: String,
    val data: Data,
    val options: Options = Options()
) {
    internal class Data(
        val labels: List<String>,
        val datasets: List<Dataset>
    ) {
        internal class Dataset(
            val label: String,
            val data: List<Double>,
            val borderWidth: Int = 1
        )
    }

    internal class Options(
        val scales: Scales = Scales(),
        val responsive: Boolean = true,
        val maintainAspectRatio: Boolean = false,
        val animation: Animation = Animation(),
        val plugins: Plugins = Plugins()
    ) {
        internal class Scales(
            val y: Y = Y()
        ) {
            internal class Y(
                val beginAtZero: Boolean = true
            )
        }

        internal class Animation(
            val duration: Int = 0
        )

        internal class Plugins(
            val title: Title = Title()
        ) {
            internal class Title(
                val display: Boolean = false,
                val text: String = ""
            )
        }
    }
}

fun line(
    labels: CellRange,
    vararg datasets: Pair<String, CellRange>
): CellView.() -> Unit = line(null, labels, *datasets)

fun line(
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

            line(titleString, labelStrings, *datasetValues.toTypedArray())()
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
    vararg datasets: Pair<String, List<Double>>
): CellView.() -> Unit = line(null, labels, *datasets)

fun line(
    title: String?,
    labels: List<String>,
    vararg datasets: Pair<String, List<Double>>
): CellView.() -> Unit = {
    val chartConfig = ChartConfig(
        type = "line",
        options = ChartConfig.Options(
            plugins = ChartConfig.Options.Plugins(
                title = ChartConfig.Options.Plugins.Title(
                    display = !title.isNullOrBlank(),
                    text = title ?: ""
                )
            )
        ),
        data = ChartConfig.Data(
            labels = labels,
            datasets = datasets.map {
                ChartConfig.Data.Dataset(
                    label = it.first,
                    data = it.second
                )
            }
        )
    )

    clear(this)

    val callback = "sigbla/charts/${chartCounter.getAndIncrement()}"

    val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
        if (call.request.httpMethod == HttpMethod.Get) {
            call.respondText { Klaxon().toJsonString(chartConfig) }
        }
    }

    this.tableView[Resources] {
        this + listOf(
            (callback to handler),
            ("chartjs/charts.js" to jsResource("/chartjs/chart.js")),
            ("sigbla/charts.css" to cssResource("/sigbla/charts.css")),
            ("sigbla/charts.js" to jsResource("/sigbla/charts.js"))
        )
    }

    val transformer = div("sigbla-charts") {
        attributes["callback"] = callback
        canvas {}
    }

    this[CellTransformer] = transformer

    this[CellTopics] = "sigbla-charts"

    on(this) {
        val unsubscribe = { off(this) }
        skipHistory = true
        events {
            if (any() && source.tableView[source][CellTransformer].function != transformer) {
                // clean up
                unsubscribe()
                source.tableView[Resources] = source.tableView[Resources] - callback
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
    val chartConfig = ChartConfig(
        type = "bar",
        options = ChartConfig.Options(
            plugins = ChartConfig.Options.Plugins(
                title = ChartConfig.Options.Plugins.Title(
                    display = !title.isNullOrBlank(),
                    text = title ?: ""
                )
            )
        ),
        data = ChartConfig.Data(
            labels = labels,
            datasets = datasets.map {
                ChartConfig.Data.Dataset(
                    label = it.first,
                    data = it.second
                )
            }
        )
    )

    clear(this)

    val callback = "sigbla/charts/${chartCounter.getAndIncrement()}"

    val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
        if (call.request.httpMethod == HttpMethod.Get) {
            call.respondText { Klaxon().toJsonString(chartConfig) }
        }
    }

    this.tableView[Resources] {
        this + listOf(
            (callback to handler),
            ("chartjs/charts.js" to jsResource("/chartjs/chart.js")),
            ("sigbla/charts.css" to cssResource("/sigbla/charts.css")),
            ("sigbla/charts.js" to jsResource("/sigbla/charts.js"))
        )
    }

    val transformer = div("sigbla-charts") {
        attributes["callback"] = callback
        canvas {}
    }

    this[CellTransformer] = transformer

    this[CellTopics] = "sigbla-charts"

    on(this) {
        val unsubscribe = { off(this) }
        skipHistory = true
        events {
            if (any() && source.tableView[source][CellTransformer].function != transformer) {
                // clean up
                unsubscribe()
                source.tableView[Resources] = source.tableView[Resources] - callback
            }
        }
    }
}
