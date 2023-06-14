package sigbla.app

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import sigbla.app.exceptions.InvalidValueException
import java.io.File
import java.util.*

fun staticFile(file: File): suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
    call.respondFile(file)
}

fun staticResource(resource: String): suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
    call.respondOutputStream(ContentType.defaultForFilePath(resource), HttpStatusCode.OK) {
        this.javaClass.getResourceAsStream(resource).buffered().transferTo(this)
    }
}

internal val jsHandlers = Collections.newSetFromMap(Collections.synchronizedMap(WeakHashMap<suspend PipelineContext<*, ApplicationCall>.() -> Unit, Boolean>()))

fun jsFile(file: File): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
    if (file.extension != "js") throw InvalidValueException("File extension must be .js")
    val handler = staticFile(file)
    jsHandlers.add(handler)
    return handler
}

fun jsResource(resource: String): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
    if (!resource.endsWith(".js")) throw InvalidValueException("File extension must be .js")
    val handler = staticResource(resource)
    jsHandlers.add(handler)
    return handler
}

internal val cssHandlers = Collections.newSetFromMap(Collections.synchronizedMap(WeakHashMap<suspend PipelineContext<*, ApplicationCall>.() -> Unit, Boolean>()))

fun cssFile(file: File): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
    if (file.extension != "css") throw InvalidValueException("File extension must be .css")
    val handler = staticFile(file)
    cssHandlers.add(handler)
    return handler
}

fun cssResource(resource: String): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
    if (!resource.endsWith(".css")) throw InvalidValueException("File extension must be .css")
    val handler = staticResource(resource)
    cssHandlers.add(handler)
    return handler
}

fun tableViewFromViewRelated(value: Any?): TableView = when (value) {
    is TableView -> value
    is ColumnView -> value.tableView
    is RowView -> value.tableView
    is CellView -> value.tableView
    is DerivedCellView -> value.tableView
    is CellHeight<*, *> -> tableViewFromViewRelated(value.source)
    is CellWidth<*, *> -> tableViewFromViewRelated(value.source)
    is CellClasses<*> -> tableViewFromViewRelated(value.source)
    is CellTopics<*> -> tableViewFromViewRelated(value.source)
    is CellTransformer<*> -> tableViewFromViewRelated(value.source)
    is Resources -> tableViewFromViewRelated(value.source)
    else -> throw InvalidValueException("Unknown type: ${value?.javaClass}")
}

fun columnViewFromViewRelated(value: Any?): ColumnView? = when (value) {
    is TableView -> null
    is ColumnView -> value
    is RowView -> null
    is CellView -> value.columnView
    is DerivedCellView -> value.columnView
    is CellHeight<*, *> -> columnViewFromViewRelated(value.source)
    is CellWidth<*, *> -> columnViewFromViewRelated(value.source)
    is CellClasses<*> -> columnViewFromViewRelated(value.source)
    is CellTopics<*> -> columnViewFromViewRelated(value.source)
    is CellTransformer<*> -> columnViewFromViewRelated(value.source)
    is Resources -> columnViewFromViewRelated(value.source)
    else -> throw InvalidValueException("Unsupported type: ${value?.javaClass}")
}

fun indexFromViewRelated(value: Any?): Long? = when (value) {
    is TableView -> null
    is ColumnView -> null
    is RowView -> value.index
    is CellView -> value.index
    is DerivedCellView -> value.index
    is CellHeight<*, *> -> indexFromViewRelated(value.source)
    is CellWidth<*, *> -> indexFromViewRelated(value.source)
    is CellClasses<*> -> indexFromViewRelated(value.source)
    is CellTopics<*> -> indexFromViewRelated(value.source)
    is CellTransformer<*> -> indexFromViewRelated(value.source)
    is Resources -> indexFromViewRelated(value.source)
    else -> throw InvalidValueException("Unsupported type: ${value?.javaClass}")
}

internal fun refVersionFromViewRelated(value: Any?): Long = when (value) {
    is TableView -> value.tableViewRef.get().version
    is ColumnView -> value.tableView.tableViewRef.get().version
    is RowView -> value.tableView.tableViewRef.get().version
    is CellView -> value.tableView.tableViewRef.get().version
    is DerivedCellView -> value.tableView.tableViewRef.get().version
    is CellHeight<*, *> -> refVersionFromViewRelated(value.source)
    is CellWidth<*, *> -> refVersionFromViewRelated(value.source)
    is CellClasses<*> -> refVersionFromViewRelated(value.source)
    is CellTopics<*> -> refVersionFromViewRelated(value.source)
    is CellTransformer<*> -> refVersionFromViewRelated(value.source)
    is Resources -> refVersionFromViewRelated(value.source)
    else -> throw InvalidValueException("Unsupported type: ${value?.javaClass}")
}

internal fun cellOrFalseFromViewRelated(value: Any?): Any? = when (value) {
    is CellView -> value.cell
    is CellHeight<*, *> -> cellOrFalseFromViewRelated(value.source)
    is CellWidth<*, *> -> cellOrFalseFromViewRelated(value.source)
    is CellClasses<*> -> cellOrFalseFromViewRelated(value.source)
    is CellTopics<*> -> cellOrFalseFromViewRelated(value.source)
    else -> false
}
