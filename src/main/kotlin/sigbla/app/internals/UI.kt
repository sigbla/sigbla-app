package sigbla.app.internals

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
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
import sigbla.app.Dimensions
import sigbla.app.PositionedContent
import sigbla.app.TableView
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KClass

internal class SigblaApp {
    fun start() {
        println("http://127.0.0.1:${SigblaBackend.port}/init/${SigblaBackend.accessToken}")
    }

    fun stop() {
        TODO()
    }
}

internal object SigblaBackend {
    private val engine: ApplicationEngine
    val port: Int
    val accessToken: String

    private val listeners: ConcurrentMap<WebSocketSession, SigblaClient> = ConcurrentHashMap()

    init {
        val (engine, port, accessToken) = start(10)
        this.engine = engine
        this.port = port
        this.accessToken = accessToken
    }

    private fun start(n: Int): Triple<ApplicationEngine, Int, String> {
        return try {
            val port = ThreadLocalRandom.current().nextInt(1024, 65535)
            val accessToken = UUID.randomUUID().toString()
            val engine = embeddedServer(Netty, port) {
                install(DefaultHeaders)
                install(Sessions) {
                    cookie<ClientSession>("SESSION")
                }
                install(WebSockets)

                routing {
                    static("/t/{ref}") {
                        resources("table")
                        defaultResource("index.html", "table")
                    }
                    webSocket("/t/{ref}/socket") {
                        val ref = call.parameters["ref"]

                        if (ref == null || ref.isBlank()) {
                            println("Close")
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No ref"))
                            return@webSocket
                        }

                        val client = addListener(this, ref)

                        handleDims(client)

                        println("post add listener on $ref")

                        try {
                            val jsonParser = Klaxon()

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
            is ClientEventScroll -> handleTiles(client, event)
            is ClientEventResize -> handleResize(client, event)
        }
    }

    // TODO Check synchronized and suspend
    @Synchronized
    private suspend fun handleDims(client: SigblaClient) {
        val view = Registry.getView(client.ref) ?: return

        val jsonParser = Klaxon()

        val dims = view.dims()
        val clientEventDims = ClientEventDims(dims.cornerX, dims.cornerY, dims.maxX, dims.maxY)

        client.dims = dims
        client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(clientEventDims)))
    }

    // TODO Check synchronized and suspend
    @Synchronized
    private suspend fun handleTiles(client: SigblaClient, scroll: ClientEventScroll) {
        val view = Registry.getView(client.ref) ?: return
        val update = client.tileState.updateTiles(scroll)

        val jsonParser = Klaxon()

        update.addedTiles
            .sortedBy { tile ->
                // Sort so that those tiles closest to the current view point are updated first
                sqrt((scroll.x - tile.x).toFloat().pow(2) + (scroll.y - tile.y).toFloat().pow(2))
            }
            .forEach { tile ->
                val batch = mutableListOf<ClientEventAddContent>()

                val x = tile.x
                val y = tile.y
                val h = client.tileState.tileSize
                val w = client.tileState.tileSize

                view.areaContent(x, y, h, w, client.dims).forEach { content ->
                    val cellId = client.tileState.addIdFor(content.x ?: content.ml ?: throw Exception(), content.y ?: content.mt ?: throw Exception(), content)
                    val addContent = ClientEventAddContent(cellId, content.className, content.x, content.y, content.h, content.w, content.z, content.mt, content.ml, content.ch, content.cw, content.content)
                    batch.add(addContent)

                    if (batch.size > 25) {
                        client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(batch)))
                        batch.clear()
                    }
                }

                if (batch.isNotEmpty()) client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(batch)))
                client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(ClientEventAddCommit(UUID.randomUUID().toString()))))
            }

        update.removedTiles.forEach { tile ->
            val batch = mutableListOf<ClientEventRemoveContent>()

            val x = tile.x
            val y = tile.y
            val h = client.tileState.tileSize
            val w = client.tileState.tileSize

            view.areaContent(x, y, h, w, client.dims).forEach { cell ->
                val contentId = client.tileState.withIdFor(cell.x ?: cell.ml ?: throw Exception(), cell.y ?: cell.mt ?: throw Exception())

                if (contentId != null) {
                    val removeContent = ClientEventRemoveContent(contentId)
                    batch.add(removeContent)
                }

                if (batch.size > 25) {
                    client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(batch)))
                    batch.clear()
                }
            }

            if (batch.isNotEmpty()) client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(batch)))
        }

        client.socket.outgoing.send(Frame.Text(jsonParser.toJsonString(ClientEventUpdateEnd(UUID.randomUUID().toString()))))
    }

    // TODO Check synchronized and suspend
    @Synchronized
    private suspend fun handleResize(client: SigblaClient, resize: ClientEventResize) {
        val view = Registry.getView(client.ref) ?: return
        val target = client.tileState.withPCFor(resize.target) ?: return

        if (resize.sizeChangeX != 0L) {
            val columnStyle = view[target.contentHeader]
            view[target.contentHeader] = columnStyle.copy(width = 10L.coerceAtLeast(columnStyle.width + resize.sizeChangeX))
        }

        if (resize.sizeChangeY != 0L) {
            val rowStyle = view[target.contentRow]
            view[target.contentRow] = rowStyle.copy(height = 10L.coerceAtLeast(rowStyle.height + resize.sizeChangeY))
        }
    }

    fun openView(view: TableView) {
        // TODO
        //println("http://127.0.0.1:${SigblaBackend.port}/init/${SigblaBackend.accessToken}")
        println("http://127.0.0.1:$port/t/${view.name}/")
    }

    private fun addListener(
        socket: WebSocketSession,
        ref: String
    ): SigblaClient {
        val client = SigblaClient(socket, ref)
        listeners[socket] = client
        return client
    }

    private fun removeListener(socket: WebSocketSession) {
        println("remove listener")
        listeners.remove(socket)
        // TODO Close socket?
    }
}

internal data class SigblaClient(
    val socket: WebSocketSession,
    val ref: String,
    val tileState: TileState = TileState(),
    @Volatile var dims: Dimensions = Dimensions(0, 0, 0, 0)
)

internal data class ClientSession(val id: String)

@TypeFor(field = "type", adapter = ClientEventAdapter::class)
internal open class ClientEvent(val type: String)
internal data class ClientEventScroll(val x: Int, val y: Int, val h: Int, val w: Int): ClientEvent("scroll")
internal data class ClientEventAddContent(val id: String, val classes: String, val x: Long?, val y: Long?, val h: Long, val w: Long, val z: Long?, val mt: Long?, val ml: Long?, val ch: Long?, val cw: Long?, val content: String): ClientEvent("add")
internal data class ClientEventAddCommit(val id: String): ClientEvent("add-commit")
internal data class ClientEventRemoveContent(val id: String): ClientEvent("rm")
internal data class ClientEventUpdateEnd(val id: String): ClientEvent("update-end")
internal data class ClientEventDims(val cornerX: Long, val cornerY: Long, val maxX: Long, val maxY: Long): ClientEvent("dims")
internal data class ClientEventResize(val target: String, val sizeChangeX: Long, val sizeChangeY: Long): ClientEvent("resize")

internal class ClientEventAdapter: TypeAdapter<ClientEvent> {
    override fun classFor(type: Any): KClass<out ClientEvent> = when(type as String) {
        "scroll" -> ClientEventScroll::class
        "resize" -> ClientEventResize::class
        else -> throw IllegalArgumentException("Unknown type: $type")
    }
}

internal data class Tile(val x: Int, val y: Int)
internal data class TileUpdate(val removedTiles: Set<Tile>, val addedTiles: Set<Tile>)
internal data class Coordinate(val x: Long, val y: Long)

internal val idGenerator = AtomicLong()

internal class TileState(val maxDistance: Int = 2000, val tileSize: Int = 1000) {
    @Volatile
    var existingTiles = emptySet<Tile>()

    private val coordinateIds: ConcurrentMap<Coordinate, String> = ConcurrentHashMap()
    private val idsContent: ConcurrentMap<String, PositionedContent> = ConcurrentHashMap()

    fun updateTiles(scroll: ClientEventScroll): TileUpdate {
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

    fun addIdFor(x: Long, y: Long, pc: PositionedContent): String {
        val id = coordinateIds.computeIfAbsent(Coordinate(x, y)) {
            "c${idGenerator.getAndIncrement()}"
        }

        idsContent[id] = pc

        return id
    }

    fun withIdFor(x: Long, y: Long) = coordinateIds[Coordinate(x, y)]

    fun withPCFor(id: String) = idsContent[id]
}