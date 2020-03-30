package sigbla.app.internals

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KClass

class SigblaApp {
    fun start() {
        println("http://127.0.0.1:${SigblaBackend.port}/init/${SigblaBackend.accessToken}")
    }

    fun stop() {
        TODO()
    }
}

object SigblaBackend {
    val engine: ApplicationEngine
    val port: Int
    val accessToken: String

    val listeners: ConcurrentMap<WebSocketSession, SigblaClient> = ConcurrentHashMap()

    init {
        val (engine, port, accessToken) = start(10)
        this.engine = engine
        this.port = port
        this.accessToken = accessToken
    }

    private fun start(n: Int): Triple<ApplicationEngine, Int, String> {
        return try {
            val jsonParser = Klaxon()

            val port = ThreadLocalRandom.current().nextInt(1024, 65535)
            val accessToken = UUID.randomUUID().toString()
            val engine = embeddedServer(Netty, port) {
                install(DefaultHeaders)
                install(Sessions) {
                    cookie<ClientSession>("SESSION")
                }
                install(WebSockets)

                routing {
                    get("/init/$accessToken") {
                        call.sessions.set("SESSION", ClientSession(UUID.randomUUID().toString()))
                        call.respondRedirect("/t/test/")
                    }
                    static("/t/{ref}") {
                        resources("table")
                        defaultResource("index.html", "table")
                    }
                    webSocket("/t/{ref}/socket") {
                        val session = call.sessions.get<ClientSession>()
                        val ref = call.parameters["ref"]

                        if (session == null) {
                            println("Close")
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                            return@webSocket
                        }

                        if (ref == null || ref.isBlank()) {
                            println("Close")
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No ref"))
                            return@webSocket
                        }

                        addListener(this, session.id, ref)

                        println("post add listener on ${session.id}:$ref")

                        try {
                            for (frame in incoming) {
                                when (frame) {
                                    is Frame.Text -> {
                                        val text = frame.readText()
                                        val event = jsonParser.parse<ClientEvent>(text)
                                        if (event != null) handleEvent(this, event)
                                    }
                                }
                            }
                        } catch (ex: Exception) {
                            removeListener(this)
                        }
                    }
                }
            }.start(wait = false)

            return Triple(engine, port, accessToken)
        } catch (ex: Exception) {
            if (n - 1 > 0) start(n - 1)
            else throw ex
        }
    }

    private suspend fun handleEvent(socket: WebSocketSession, event: ClientEvent) {
        val client = listeners[socket] ?: return

        when (event) {
            is ClientEventScroll -> {
                client.scroll = event
                handleTiles(client)
            }
        }
    }

    // TODO Check synchronized and suspend
    @Synchronized
    private suspend fun handleTiles(client: SigblaClient) {
        val scroll = client.scroll ?: return
        val update = client.tileState.updateTiles(scroll)

        val jsonParser = Klaxon()

        //val tileStep = (client.tileState.tileSize/2);
        val tileStep = 50

        update.addedTiles
            .sortedBy { tile ->
                // Sort so that those tiles closest to the current view point are updated first
                sqrt((scroll.x - tile.x).toFloat().pow(2) + (scroll.y - tile.y).toFloat().pow(2))
            }
            .forEach { tile ->
                val batch = mutableListOf<ClientEventAddCell>()

                for (x in tile.x..(tile.x+client.tileState.tileSize) step tileStep) {
                    for (y in tile.y..(tile.y+client.tileState.tileSize) step tileStep) {
                        val addCell = ClientEventAddCell("c-$x-$y", "", x, y, tileStep, tileStep, "C")
                        batch.add(addCell)

                        if (batch.size > 25) {
                            client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(batch)))
                            batch.clear()
                        }
                    }
                }
                client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(batch)))
                client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(ClientEventAddCommit(UUID.randomUUID().toString()))))
            }
        update.removedTiles.forEach { tile ->
            val batch = mutableListOf<ClientEventRemoveCell>()

            for (x in tile.x..(tile.x+client.tileState.tileSize) step tileStep) {
                for (y in tile.y..(tile.y+client.tileState.tileSize) step tileStep) {
                    val removeCell = ClientEventRemoveCell("c-$x-$y")
                    batch.add(removeCell)

                    if (batch.size > 25) {
                        client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(batch)))
                        batch.clear()
                    }
                }
            }
            client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(batch)))
        }
        client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(ClientEventUpdateEnd(UUID.randomUUID().toString()))))
    }

    private fun addListener(
        socket: WebSocketSession,
        session: String,
        ref: String
    ) {
        println("add listener")
        listeners.put(socket, SigblaClient(socket, session, ref))
    }

    private fun removeListener(socket: WebSocketSession) {
        println("remove listener")
        listeners.remove(socket)
        // TODO Close socket?
    }
}

data class SigblaClient(
    val socket: WebSocketSession,
    val session: String,
    val ref: String,
    @Volatile var scroll: ClientEventScroll? = null,
    val tileState: TileState = TileState()
)

data class ClientSession(val id: String)

@TypeFor(field = "type", adapter = ClientEventAdapter::class)
open class ClientEvent(val type: String)
data class ClientEventScroll(val x: Int, val y: Int, val h: Int, val w: Int): ClientEvent("scroll")
data class ClientEventAddCell(val id: String, val classes: String, val x: Int, val y: Int, val h: Int, val w: Int, val content: String): ClientEvent("add")
data class ClientEventAddCommit(val id: String): ClientEvent("add-commit")
data class ClientEventRemoveCell(val id: String): ClientEvent("rm")
data class ClientEventUpdateEnd(val id: String): ClientEvent("update-end")

class ClientEventAdapter: TypeAdapter<ClientEvent> {
    override fun classFor(type: Any): KClass<out ClientEvent> = when(type as String) {
        "scroll" -> ClientEventScroll::class
        else -> throw IllegalArgumentException("Unknown type: $type")
    }
}

data class Tile(val x: Int, val y: Int)
data class TileUpdate(val removedTiles: Set<Tile>, val addedTiles: Set<Tile>)

class TileState(val maxDistance: Int = 2000, val tileSize: Int = 1000) {
    var existingTiles = emptySet<Tile>()

    fun updateTiles(scroll: ClientEventScroll): TileUpdate {
        println(scroll)
        val x1 = scroll.x - (scroll.x % tileSize)
        val y1 = scroll.y - (scroll.y % tileSize)
        val x2 = x1 + scroll.w - (scroll.w % tileSize) + tileSize
        val y2 = y1 + scroll.h - (scroll.h % tileSize) + tileSize

        val tiles = mutableSetOf<Tile>()
        for (x in (Math.max(x1-maxDistance, 0))..(x2+maxDistance) step tileSize) {
            for (y in (Math.max(y1-maxDistance, 0))..(y2+maxDistance) step tileSize) {
                tiles.add(Tile(x, y))
            }
        }

        val removedTiles = existingTiles.filter { !tiles.contains(it) }.toSet()
        val addedTiles = tiles.filter { !existingTiles.contains(it) }.toSet()

        existingTiles = tiles

        return TileUpdate(removedTiles, addedTiles)
    }
}