/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
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

fun staticText(contentType: ContentType, text: String): suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
    call.respondText(contentType = contentType, text = text)
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

fun js(resource: () -> String): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
    val handler = staticText(ContentType.Text.JavaScript, resource())
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

fun css(resource: () -> String): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
    val handler = staticText(ContentType.Text.CSS, resource())
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
    is SourceTable -> value.source
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
    is SourceTable -> null
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
    is SourceTable -> null
    else -> throw InvalidValueException("Unsupported type: ${value?.javaClass}")
}

fun sourceFromViewEventRelated(value: Any?): Any = when (value) {
    is CellHeight<*, *> -> value.source!!
    is CellWidth<*, *> -> value.source!!
    is CellClasses<*> -> value.source!!
    is CellTopics<*> -> value.source!!
    is CellTransformer<*> -> value.source
    is Resources -> value.source
    is SourceTable -> value.source
    else -> throw InvalidValueException("Unsupported type: ${value?.javaClass}")
}

internal fun relatedFromViewRelated(tableView: TableView, value: Any?): Any = when (value) {
    is TableView -> tableView
    is ColumnView -> tableView[value]
    is RowView -> tableView[value]
    is CellView -> tableView[value]
    is DerivedCellView -> tableView[value]
    is CellHeight<*, *> -> when (val source = value.source) {
        is TableView -> tableView[CellHeight]
        is RowView -> tableView[source][CellHeight]
        is CellView -> tableView[source][CellHeight]
        else -> throw InvalidValueException("Unsupported source: ${source?.javaClass}")
    }
    is CellWidth<*, *> -> when (val source = value.source) {
        is TableView -> tableView[CellWidth]
        is ColumnView -> tableView[source][CellWidth]
        is CellView -> tableView[source][CellWidth]
        else -> throw InvalidValueException("Unsupported source: ${source?.javaClass}")
    }
    is CellClasses<*> -> when (val source = value.source) {
        is TableView -> tableView[CellClasses]
        is ColumnView -> tableView[source][CellClasses]
        is RowView -> tableView[source][CellClasses]
        is CellView -> tableView[source][CellClasses]
        else -> throw InvalidValueException("Unsupported source: ${source?.javaClass}")
    }
    is CellTopics<*> -> when (val source = value.source) {
        is TableView -> tableView[CellTopics]
        is ColumnView -> tableView[source][CellTopics]
        is RowView -> tableView[source][CellTopics]
        is CellView -> tableView[source][CellTopics]
        else -> throw InvalidValueException("Unsupported source: ${source?.javaClass}")
    }
    is CellTransformer<*> -> tableView[value.source][CellTransformer]
    is Resources -> tableView[Resources]
    is SourceTable -> SourceTable(tableView, tableView.tableViewRef.get().table)
    else -> throw InvalidValueException("Unknown type: ${value?.javaClass}")
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
    is SourceTable -> refVersionFromViewRelated(value.source)
    else -> throw InvalidValueException("Unsupported type: ${value?.javaClass}")
}

internal fun cellOrResourceOrSourceTableOrFalseFromViewRelated(value: Any?): Any? = when (value) {
    is CellView -> value.cell
    is CellHeight<*, *> -> cellOrResourceOrSourceTableOrFalseFromViewRelated(value.source)
    is CellWidth<*, *> -> cellOrResourceOrSourceTableOrFalseFromViewRelated(value.source)
    is CellClasses<*> -> cellOrResourceOrSourceTableOrFalseFromViewRelated(value.source)
    is CellTopics<*> -> cellOrResourceOrSourceTableOrFalseFromViewRelated(value.source)
    is CellTransformer<*> -> cellOrResourceOrSourceTableOrFalseFromViewRelated(value.source)
    is SourceTable -> value
    is Resources -> value
    else -> false
}
