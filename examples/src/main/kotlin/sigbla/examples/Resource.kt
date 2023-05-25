package sigbla.examples

import sigbla.app.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import java.io.File

fun main() {
    val table = Table["test"]
    val tableView = TableView[table]

    fun getHandler(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
        return {
            call.respondText(text = "Works!")
        }
    }

    tableView[Resources] = "test-resource/1" to getHandler()
    tableView[Resources] = tableView[Resources] + ("test-resource/1" to getHandler())
    tableView[Resources] = tableView[Resources] + ("" to {
        call.respondText(text = "Works!")
    })

    tableView[Resources] {
        val tmpFile = File.createTempFile("sigbla", "txt")
        tmpFile.deleteOnExit()
        tmpFile.writeText("sigbla data app file resource")
        this + ("static-file/sigbla.txt" to staticFile(tmpFile))
    }

    tableView[Resources] {
        this + ("static-resource/magpie.jpg" to staticResource("/test-folder/magpie.jpg"))
    }

    show(tableView)

    println("END")
}
