package sigbla.app

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import java.io.File

fun staticFile(file: File): suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
    call.respondFile(file)
}

fun staticResource(resource: String): suspend PipelineContext<*, ApplicationCall>.() -> Unit = {
    call.respondOutputStream(ContentType.defaultForFilePath(resource), HttpStatusCode.OK) {
        this.javaClass.getResourceAsStream(resource).buffered().transferTo(this)
    }
}