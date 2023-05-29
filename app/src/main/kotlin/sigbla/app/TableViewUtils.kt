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
