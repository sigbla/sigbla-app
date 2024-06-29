/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import sigbla.app.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import java.io.File
import java.time.LocalTime
import kotlin.concurrent.thread

fun main() {
    val table = Table["Resource_Example"]
    val tableView = TableView[table]

    thread {
        while (true) {
            Thread.sleep(1000)
            table["A", 0] = LocalTime.now().toString()
        }
    }

    fun getHandler(): suspend PipelineContext<*, ApplicationCall>.() -> Unit {
        return {
            call.respondText(text = "Works!")
        }
    }

    Resource["/"] = {
        call.respondText(text = "Root routing works!")
    }
    Resource["/more-root"] = {
        call.respondText(text = "More stuff at root..")
    }

    tableView[Resource["test-resource/1"]] = getHandler()

    tableView[Resource["static-file/sigbla.txt"]].apply {
        val tmpFile = File.createTempFile("sigbla", "txt")
        tmpFile.deleteOnExit()
        tmpFile.writeText("sigbla data app file resource")
        this(staticFile(tmpFile))
    }

    val url = show(tableView)
    println(url)

    tableView[Resource["static-resource/magpie.jpg"]] = staticResource("/test-folder/magpie.jpg")
    tableView[Resource["js/test.js"]] = jsResource("/test-folder/test.js")
    tableView[Resource["css/test.css"]] = cssResource("/test-folder/test.css")

    tableView["A", 0][CellTopics] = "resourceTopic1"

    println("END")
}
