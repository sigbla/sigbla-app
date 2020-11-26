package sigbla.app.internals

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import sigbla.app.*
import sigbla.app.exceptions.SigblaAppException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.MutableList
import kotlin.collections.Set
import kotlin.collections.emptySet
import kotlin.collections.filter
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.mutableListOf
import kotlin.collections.mutableSetOf
import kotlin.collections.set
import kotlin.collections.sortedBy
import kotlin.collections.toSet
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
            is ClientEventPackageEnd -> handlePackageEnd(client, event)
        }
    }

    private suspend fun handleClear(client: SigblaClient) {
        client.mutex.withLock {
            client.tileState.clear()

            val jsonParser = Klaxon()
            val clientPackage = ClientPackage()

            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEvent("clear")))

            Registry.getView(client.ref)?.let { view ->
                val dims = view.dims()
                val clientEventDims = ClientEventDims(dims.cornerX, dims.cornerY, dims.maxX, dims.maxY)

                client.dims = dims

                clientPackage.outgoing.add(jsonParser.toJsonString(clientEventDims))
            }

            client.publish(clientPackage)
        }
    }

    private suspend fun handleDims(client: SigblaClient) {
        client.mutex.withLock {
            val view = Registry.getView(client.ref) ?: return

            val dims = view.dims()
            val clientEventDims = ClientEventDims(dims.cornerX, dims.cornerY, dims.maxX, dims.maxY)

            client.dims = dims

            val clientPackage = ClientPackage(outgoing = mutableListOf(Klaxon().toJsonString(clientEventDims)))
            client.publish(clientPackage)
        }
    }

    private suspend fun handleTiles(client: SigblaClient, scroll: ClientEventScroll) {
        client.mutex.withLock {
            val view = Registry.getView(client.ref) ?: return
            val update = client.tileState.updateTiles(scroll)

            val jsonParser = Klaxon()
            val dims = client.dims
            val clientPackage = ClientPackage()

            val addedIds = mutableSetOf<String>()

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

                    view.areaContent(x, y, h, w, dims).forEach { content ->
                        val cellId = client.tileState.addIdFor(content.x ?: content.ml ?: throw Exception(), content.y ?: content.mt ?: throw Exception(), content)
                        val addContent = ClientEventAddContent(cellId, content.className, content.x, content.y, content.h, content.w, content.z, content.mt, content.ml, content.ch, content.cw, content.content)
                        batch.add(addContent)

                        if (batch.size > 25) {
                            clientPackage.outgoing.add(jsonParser.toJsonString(batch))
                            batch.clear()
                        }

                        addedIds.add(cellId)
                    }

                    if (batch.isNotEmpty()) clientPackage.outgoing.add(jsonParser.toJsonString(batch))
                    clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventAddCommit(UUID.randomUUID().toString())))
                }

            update.removedTiles.forEach { tile ->
                val batch = mutableListOf<ClientEventRemoveContent>()

                val x = tile.x
                val y = tile.y
                val h = client.tileState.tileSize
                val w = client.tileState.tileSize

                view.areaContent(x, y, h, w, dims).forEach { cell ->
                    val contentId = client.tileState.withIdFor(cell.x ?: cell.ml ?: throw Exception(), cell.y ?: cell.mt ?: throw Exception())

                    if (contentId != null && !addedIds.contains(contentId)) {
                        val removeContent = ClientEventRemoveContent(contentId)
                        batch.add(removeContent)
                    }

                    if (batch.size > 25) {
                        clientPackage.outgoing.add(jsonParser.toJsonString(batch))
                        batch.clear()
                    }
                }

                if (batch.isNotEmpty()) clientPackage.outgoing.add(jsonParser.toJsonString(batch))
            }

            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventUpdateEnd(UUID.randomUUID().toString())))

            client.publish(clientPackage)
        }
    }

    private suspend fun handleDirty(client: SigblaClient, dirtyCells: Set<Cell<*>>) {
        client.mutex.withLock {
            val view = Registry.getView(client.ref) ?: return

            val jsonParser = Klaxon()
            val clientPackage = ClientPackage()

            client.tileState.existingTiles.forEach { tile ->
                val batch = mutableListOf<ClientEventAddContent>()

                val x = tile.x
                val y = tile.y
                val h = client.tileState.tileSize
                val w = client.tileState.tileSize

                view.areaContent(x, y, h, w, client.dims, dirtyCells).forEach { content ->
                    val cellId = client.tileState.addIdFor(content.x ?: content.ml ?: throw Exception(), content.y ?: content.mt ?: throw Exception(), content)
                    val addContent = ClientEventAddContent(cellId, content.className, content.x, content.y, content.h, content.w, content.z, content.mt, content.ml, content.ch, content.cw, content.content)
                    batch.add(addContent)

                    if (batch.size > 25) {
                        clientPackage.outgoing.add(jsonParser.toJsonString(batch))
                        batch.clear()
                    }
                }

                if (batch.isNotEmpty()) clientPackage.outgoing.add(jsonParser.toJsonString(batch))
                clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventAddCommit(UUID.randomUUID().toString())))
            }


            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventUpdateEnd(UUID.randomUUID().toString())))

            client.publish(clientPackage, isDirty = true)
        }
    }

    private fun handleResize(client: SigblaClient, resize: ClientEventResize) {
        // No lock here as we're not sending data
        val view = Registry.getView(client.ref) ?: return
        val target = client.tileState.withPCFor(resize.target) ?: return

        if (resize.sizeChangeX != 0L) {
            val columnView = view[target.contentHeader]
            view[target.contentHeader] = ColumnView(10L.coerceAtLeast(columnView.width + resize.sizeChangeX), target.contentHeader)
        }

        if (resize.sizeChangeY != 0L) {
            val rowView = view[target.contentRow]
            view[target.contentRow] = RowView(10L.coerceAtLeast(rowView.height + resize.sizeChangeY), target.contentRow)
        }
    }

    private suspend fun handlePackageEnd(client: SigblaClient, packageEnd: ClientEventPackageEnd) {
        client.mutex.withLock {
            client.purge(UUID.fromString(packageEnd.id))
        }
    }

    fun openView(view: TableView) {
        // TODO It's possible for a view to be replaced by another view on same name.
        //      We'll need to ensure we remove this listener and add a new listener in that case.
        view.onAny {
            skipHistory = true
            order = Long.MAX_VALUE
            name = "UI"

            events {
                if (any()) {
                    listeners.values.forEach { client ->
                        if (client.ref == source.name) {
                            runBlocking {
                                handleClear(client)
                            }
                        }
                    }
                }
            }
        }

        // TODO As with the view, the underlying table might also change, so we need to remove
        //      and add a new listener when this happens..
        view.table?.onAny {
            skipHistory = true
            order = Long.MAX_VALUE
            name = "UI"

            events {
                if (any()) {
                    val dirtyCells = map { it.newValue }.toSet()
                    listeners.values.forEach { client ->
                        if (client.ref == view.name) {
                            runBlocking {
                                handleDims(client)
                                handleDirty(client, dirtyCells)
                            }
                        }
                    }
                }
            }
        }

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

internal data class ClientPackage(
    val outgoing: MutableList<String> = mutableListOf(),
    val id: UUID = UUID.randomUUID(),
    var published: Boolean = false
)

internal data class SigblaClient(
    val socket: WebSocketSession,
    val ref: String,
    val tileState: TileState = TileState(),
    val mutex: Mutex = Mutex(),
    @Volatile var dims: Dimensions = Dimensions(0, 0, 0, 0)
) {
    val MAX_PACKAGES = 10
    val outgoingPackages = mutableListOf<ClientPackage>()

    private var clearId: UUID? = null

    suspend fun publish(clientPackage: ClientPackage? = null, isDirty: Boolean = false) {
        if (!mutex.isLocked) throw SigblaAppException("Mutex not locked!")

        if (clearId != null && isDirty) return

        if (clientPackage != null) outgoingPackages.add(clientPackage)

        val overflow = outgoingPackages.size > MAX_PACKAGES

        if (overflow) {
            outgoingPackages.clear()
            tileState.clear()

            val jsonParser = Klaxon()
            val clearClientPackage = ClientPackage()

            clearClientPackage.outgoing.add(jsonParser.toJsonString(ClientEvent("clear")))

            Registry.getView(ref)?.let { view ->
                val dims = view.dims()
                val clientEventDims = ClientEventDims(dims.cornerX, dims.cornerY, dims.maxX, dims.maxY)

                this.dims = dims

                clearClientPackage.outgoing.add(jsonParser.toJsonString(clientEventDims))
            }

            outgoingPackages.add(clearClientPackage)

            clearId = clearClientPackage.id
        }

        if (outgoingPackages.firstOrNull { it.published } != null) return

        val selectedPackage = outgoingPackages.firstOrNull { !it.published } ?: return
        selectedPackage.published = true

        selectedPackage.outgoing.forEach {
            socket.outgoing.send(Frame.Text(it))
        }

        socket.outgoing.send(Frame.Text(Klaxon().toJsonString(ClientEventPackageEnd(selectedPackage.id.toString()))))
    }

    suspend fun purge(id: UUID) {
        if (!mutex.isLocked) throw SigblaAppException("Mutex not locked!")
        outgoingPackages.removeIf { it.id == id }
        if (clearId == id) clearId = null
        publish()
    }
}

internal data class ClientSession(val id: String)

@TypeFor(field = "type", adapter = ClientEventAdapter::class)
internal open class ClientEvent(val type: String)
internal data class ClientEventScroll(val x: Int, val y: Int, val h: Int, val w: Int): ClientEvent("scroll")
internal data class ClientEventAddContent(val id: String, val classes: String, val x: Long?, val y: Long?, val h: Long, val w: Long, val z: Long?, val mt: Long?, val ml: Long?, val ch: Long?, val cw: Long?, val content: String): ClientEvent("add")
internal data class ClientEventAddCommit(val id: String): ClientEvent("add-commit")
internal data class ClientEventRemoveContent(val id: String): ClientEvent("rm")
internal data class ClientEventUpdateEnd(val id: String): ClientEvent("update-end")
internal data class ClientEventPackageEnd(val id: String): ClientEvent("package-end")
internal data class ClientEventDims(val cornerX: Long, val cornerY: Long, val maxX: Long, val maxY: Long): ClientEvent("dims")
internal data class ClientEventResize(val target: String, val sizeChangeX: Long, val sizeChangeY: Long): ClientEvent("resize")

internal class ClientEventAdapter: TypeAdapter<ClientEvent> {
    override fun classFor(type: Any): KClass<out ClientEvent> = when(type as String) {
        "scroll" -> ClientEventScroll::class
        "resize" -> ClientEventResize::class
        "package-end" -> ClientEventPackageEnd::class
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
        val x1 = scroll.x - (scroll.x % tileSize) - tileSize
        val y1 = scroll.y - (scroll.y % tileSize) - tileSize
        val x2 = x1 + scroll.w - (scroll.w % tileSize) + tileSize
        val y2 = y1 + scroll.h - (scroll.h % tileSize) + tileSize

        val tiles = mutableSetOf<Tile>()
        for (x in ((x1-maxDistance))..(x2+maxDistance) step tileSize) {
            for (y in ((y1-maxDistance))..(y2+maxDistance) step tileSize) {
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

    fun clear() {
        existingTiles = emptySet()
        coordinateIds.clear()
        idsContent.clear()
    }
}