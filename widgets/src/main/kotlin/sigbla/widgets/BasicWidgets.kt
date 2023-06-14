package sigbla.widgets

import sigbla.app.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.html.InputType
import kotlinx.html.input
import java.util.concurrent.atomic.AtomicLong

private val widgetCounter = AtomicLong()

fun button(
    text: String,
    action: CellView.() -> Unit = {}
): CellView.() -> Unit = {
    // Remove all existing properties on cellview
    clear(this)

    val callback = "sigbla/widgets/button/${widgetCounter.getAndIncrement()}"

    val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
        if (call.request.httpMethod == HttpMethod.Post) {
            call.respondText { "" }
            action()
        }
    }

    this.tableView[Resources] {
        this + listOf(
            (callback to handler),
            ("sigbla/widgets.css" to cssResource("/widgets.css")),
            ("sigbla/widgets.js" to jsResource("/widgets.js"))
        )
    }

    val transformer = div(classes = "sigbla-widgets") {
        input {
            type = InputType.button
            value = text
            attributes["callback"] = callback
        }
    }
    this[CellTransformer] = transformer

    this[CellTopics] = "sigbla-widgets-button"

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