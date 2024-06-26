/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
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
import kotlinx.html.tabIndex
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

// TODO Would be good to be able to easily tie this to cells, for example button(table["..", ..]) { .. }
//      where the value is fetched from and subscribed to the cell given. Same for other widgets..

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
    val cellView = this

    batch(cellView.tableView) {
        val callback = "sigbla/widgets/$id-${widgetCounter.getAndIncrement()}"

        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            if (call.request.httpMethod == HttpMethod.Post) {
                val b = Button(cellView, text)
                b.action()
                if (b.modified) {
                    cellView.(button(id, b.text, action))()
                    // TODO Look at return values
                    call.respondText { "false" }
                } else {
                    call.respondText { "true" }
                }
            }
        }

        cellView.tableView[Resource[callback]] = handler
        cellView.tableView[Resource["sigbla/widgets.css"]] = cssResource("/widgets.css")
        cellView.tableView[Resource["sigbla/widgets.js"]] = jsResource("/widgets.js")

        val transformer = div("sigbla-widgets") {
            input {
                type = InputType.button
                value = text
                tabIndex = "-1"
                attributes["id"] = id
                attributes["callback"] = callback
            }
        }
        cellView[CellTransformer] = transformer

        cellView[CellTopics].apply { this(this + "sigbla-widgets-button") }

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
    val cellView = this

    batch(cellView.tableView) {
        val callback = "sigbla/widgets/$id-${widgetCounter.getAndIncrement()}"

        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            if (call.request.httpMethod == HttpMethod.Post) {
                val newChecked = call.receiveText() == "true"
                val cb = CheckBox(cellView, text, newChecked)
                cb.action()
                if (cb.modified || newChecked != checked) {
                    cellView.(checkBox(id, cb.text, cb.checked, action))()
                    call.respondText { "false" }
                } else {
                    call.respondText { "true" }
                }
            }
        }

        cellView.tableView[Resource[callback]] = handler
        cellView.tableView[Resource["sigbla/widgets.css"]] = cssResource("/widgets.css")
        cellView.tableView[Resource["sigbla/widgets.js"]] = jsResource("/widgets.js")

        val transformer = div("sigbla-widgets") {
            input {
                type = InputType.checkBox
                tabIndex = "-1"
                attributes["id"] = id
                attributes["callback"] = callback
                if (checked) attributes["checked"] = "checked"
            }
            label {
                attributes["for"] = id
                +text
            }
        }
        cellView[CellTransformer] = transformer

        cellView[CellTopics].apply { this(this + "sigbla-widgets-checkbox") }

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

class Radio(
    val source: CellView,
    text: String,
    selected: Boolean
) {
    internal var modified: Boolean = false
        private set

    var text: String = text
        set(value) {
            field = value
            modified = true
        }

    var selected: Boolean = selected
        set(value) {
            field = value
            modified = true
        }
}

fun radio(
    text: String = "",
    selected: Boolean = false,
    action: Radio.() -> Unit = {}
) = radio(
    "radio-${widgetCounter.getAndIncrement()}-${ThreadLocalRandom.current().nextInt().absoluteValue}",
    text,
    selected,
    action
)

internal fun radio(
    id: String,
    text: String = "",
    selected: Boolean = false,
    action: Radio.() -> Unit
): CellView.() -> Unit = {
    val cellView = this

    batch(cellView.tableView) {
        val callback = "sigbla/widgets/$id-${widgetCounter.getAndIncrement()}"

        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            if (call.request.httpMethod == HttpMethod.Post) {
                val newSelected = call.receiveText() == "true"
                val r = Radio(cellView, text, newSelected)
                r.action()
                if (r.modified || newSelected != selected) {
                    cellView.(radio(id, r.text, r.selected, action))()
                    call.respondText { "false" }
                } else {
                    call.respondText { "true" }
                }
            }
        }

        cellView.tableView[Resource[callback]] = handler
        cellView.tableView[Resource["sigbla/widgets.css"]] = cssResource("/widgets.css")
        cellView.tableView[Resource["sigbla/widgets.js"]] = jsResource("/widgets.js")

        val transformer = div("sigbla-widgets") {
            input {
                type = InputType.radio
                tabIndex = "-1"
                attributes["id"] = id
                attributes["callback"] = callback
                if (selected) attributes["checked"] = "checked"
            }
            label {
                attributes["for"] = id
                +text
            }
        }
        cellView[CellTransformer] = transformer

        cellView[CellTopics].apply { this(this + "sigbla-widgets-radio") }

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

// TODO Create a radioGroup function that makes it easy to create a group of radio buttons

class TextField(
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

fun textField(
    text: String = "",
    action: TextField.() -> Unit = {}
) = textField(
    "textfield-${widgetCounter.getAndIncrement()}-${ThreadLocalRandom.current().nextInt().absoluteValue}",
    text,
    action
)

internal fun textField(
    id: String,
    text: String,
    action: TextField.() -> Unit = {}
): CellView.() -> Unit = {
    val cellView = this

    batch(this.tableView) {
        val callback = "sigbla/widgets/$id-${widgetCounter.getAndIncrement()}"

        val handler: suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
            if (call.request.httpMethod == HttpMethod.Post) {
                val newText = call.receiveText()
                val tf = TextField(cellView, newText)
                tf.action()
                if (tf.modified || newText != text) {
                    cellView.(textField(id, tf.text, action))()
                    call.respondText { "false" }
                } else {
                    call.respondText { "true" }
                }
            }
        }

        cellView.tableView[Resource[callback]] = handler
        cellView.tableView[Resource["sigbla/widgets.css"]] = cssResource("/widgets.css")
        cellView.tableView[Resource["sigbla/widgets.js"]] = jsResource("/widgets.js")

        val transformer = div("sigbla-widgets") {
            input {
                type = InputType.text
                tabIndex = "-1"
                value = text
                attributes["id"] = id
                attributes["callback"] = callback
            }
        }

        cellView[CellTransformer] = transformer

        cellView[CellTopics].apply { this(this + "sigbla-widgets-textfield") }

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
