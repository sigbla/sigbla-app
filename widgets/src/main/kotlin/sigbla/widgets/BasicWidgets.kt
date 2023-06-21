package sigbla.widgets

import sigbla.app.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.html.InputType
import kotlinx.html.input
import kotlinx.html.label
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.absoluteValue

private val widgetCounter = AtomicLong()

class Button(
    val source: CellView,
    text: String
) {
    internal var modified: Boolean = false
        private set

    var text: String = text
        set(value) {
            field = value
            modified = true
        }
}

fun button(
    text: String,
    action: Button.() -> Unit = {}
) = button(
    "button-${widgetCounter.getAndIncrement()}-${ThreadLocalRandom.current().nextInt().absoluteValue}",
    text,
    action
)

internal fun button(
    id: String,
    text: String,
    action: Button.() -> Unit = {}
): CellView.() -> Unit = {
    // Remove all existing properties on cellview
    val cellView = this
    clear(cellView)

    val callback = "sigbla/widgets/button/$id-${widgetCounter.getAndIncrement()}"

    val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
        if (call.request.httpMethod == HttpMethod.Post) {
            val b = Button(cellView, text)
            b.action()
            if (b.modified) {
                cellView { button(id, b.text, action) }
                call.respondText { "false" }
            } else {
                call.respondText { "true" }
            }
        }
    }

    this.tableView[Resources] {
        this + listOf(
            (callback to handler),
            ("sigbla/widgets.css" to cssResource("/widgets.css")),
            ("sigbla/widgets.js" to jsResource("/widgets.js"))
        )
    }

    val transformer = div("sigbla-widgets") {
        input {
            type = InputType.button
            value = text
            attributes["id"] = id
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

class CheckBox(
    val source: CellView,
    text: String,
    checked: Boolean
) {
    internal var modified: Boolean = false
        private set

    var text: String = text
        set(value) {
            field = value
            modified = true
        }

    var checked: Boolean = checked
        set(value) {
            field = value
            modified = true
        }
}

fun checkBox(
    text: String = "",
    checked: Boolean = false,
    action: CheckBox.() -> Unit = {}
) = checkBox(
    "check-box-${widgetCounter.getAndIncrement()}-${ThreadLocalRandom.current().nextInt().absoluteValue}",
    text,
    checked,
    action
)

internal fun checkBox(
    id: String,
    text: String = "",
    checked: Boolean = false,
    action: CheckBox.() -> Unit
): CellView.() -> Unit = {
    // Remove all existing properties on cellview
    val cellView = this
    clear(cellView)

    val callback = "sigbla/widgets/button/$id-${widgetCounter.getAndIncrement()}"

    val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
        if (call.request.httpMethod == HttpMethod.Post) {
            val newChecked = call.receiveText() == "true"
            val cb = CheckBox(cellView, text, newChecked)
            cb.action()
            if (cb.modified || newChecked != checked) {
                cellView { checkBox(id, cb.text, cb.checked, action) }
                call.respondText { "false" }
            } else {
                call.respondText { "true" }
            }
        }
    }

    this.tableView[Resources] {
        this + listOf(
            (callback to handler),
            ("sigbla/widgets.css" to cssResource("/widgets.css")),
            ("sigbla/widgets.js" to jsResource("/widgets.js"))
        )
    }

    val transformer = div("sigbla-widgets") {
        input {
            type = InputType.checkBox
            attributes["id"] = id
            attributes["callback"] = callback
            if (checked) attributes["checked"] = "checked"
        }
        label {
            attributes["for"] = id
            +text
        }
    }
    this[CellTransformer] = transformer

    this[CellTopics] = "sigbla-widgets-checkbox"

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