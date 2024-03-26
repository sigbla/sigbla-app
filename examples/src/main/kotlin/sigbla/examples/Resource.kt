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

    tableView[Resources] = "test-resource/1" to getHandler()
    tableView[Resources] = tableView[Resources] + ("test-resource/1" to getHandler())
    tableView[Resources] = tableView[Resources] + ("" to {
        call.respondText(text = "Works!")
    })

    tableView[Resources].apply {
        val tmpFile = File.createTempFile("sigbla", "txt")
        tmpFile.deleteOnExit()
        tmpFile.writeText("sigbla data app file resource")
        this(this + ("static-file/sigbla.txt" to staticFile(tmpFile)))
    }

    val url = show(tableView)
    println(url)

    tableView[Resources].apply {
        this(this + ("static-resource/magpie.jpg" to staticResource("/test-folder/magpie.jpg")))
    }

    tableView[Resources].apply {
        this(this + ("js/test.js" to jsResource("/test-folder/test.js")))
    }

    tableView[Resources].apply {
        this(this + ("css/test.css" to cssResource("/test-folder/test.css")))
    }

    tableView["A", 0][CellTopics] = "resourceTopic1"

    println("END")
}
