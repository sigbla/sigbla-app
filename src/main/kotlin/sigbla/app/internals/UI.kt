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
import kotlin.collections.filter
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.mutableListOf
import kotlin.collections.mutableSetOf
import kotlin.collections.set
import kotlin.collections.toSet
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

                            while (true) {
                                when (val frame = incoming.receive()) {
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

    private fun areaContent(view: TableView, x: Long, y: Long, h: Long, w: Long, dims: Dimensions, dirtyCells: Set<Cell<*>>? = null): List<PositionedContent> {
        // TODO Consider using a stable snapshot ref for view/table
        val table = view.table ?: return emptyList()

        val dirtyColumnHeaders = dirtyCells?.map { it.column.columnHeader }?.toSet()
        val dirtyRowIndices = dirtyCells?.map { it.index }?.toSet()

        val applicableColumns = mutableListOf<Pair<Column, Long>>()
        var runningWidth = 0L
        var maxHeaderOffset = 0L
        var maxHeaderCells = 0

        val headerWidth = view[ColumnHeader()].width

        for (column in table.columns) {
            if (x <= runningWidth && runningWidth <= x + w) applicableColumns.add(Pair(column, runningWidth))
            runningWidth += view[column].width

            val yOffset = column.columnHeader.header.mapIndexed { i, _ -> view[(-(column.columnHeader.header.size) + i).toLong()].height }.sum()
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
            if (column.columnHeader.header.size > maxHeaderCells) maxHeaderCells = column.columnHeader.header.size
        }

        val colHeaderZ = Integer.MAX_VALUE.toLong()
        val rowHeaderZ = Integer.MAX_VALUE.toLong()

        val output = mutableListOf<PositionedContent>()

        // This is for the column headers
        for ((applicableColumn, applicableX) in applicableColumns) {
            if (dirtyColumnHeaders != null && !dirtyColumnHeaders.contains(applicableColumn.columnHeader)) continue

            for (idx in 0 until maxHeaderCells) {
                val headerText = applicableColumn.columnHeader[idx]
                val yOffset = applicableColumn.columnHeader.header.mapIndexed { i, _ -> if (i < idx) view[(-maxHeaderCells + i).toLong()].height else 0L }.sum()

                output.add(PositionedContent(
                    applicableColumn.columnHeader,
                    (-maxHeaderCells + idx).toLong(),
                    headerText,
                    view[(-maxHeaderCells + idx).toLong()].height,
                    view[applicableColumn].width,
                    colHeaderZ,
                    ml = applicableX + headerWidth,
                    cw = dims.maxX,
                    ch = dims.maxY,
                    className = "ch",
                    x = null,
                    y = yOffset
                ))
            }
        }

        val applicableRows = mutableListOf<Pair<Long, Long>>()
        var runningHeight = maxHeaderOffset

        val lastKey = table.tableRef.get().indicesMap.last()?.component1() ?: -1
        for (row in 0..lastKey) {
            if (y <= runningHeight && runningHeight <= y + h) applicableRows.add(Pair(row, runningHeight))

            runningHeight += view[row].height

            if (runningHeight > y + h || runningHeight < 0L) break
        }

        // This is for the row headers
        for ((applicableRow, applicableY) in applicableRows) {
            if (dirtyRowIndices != null && !dirtyRowIndices.contains(applicableRow)) continue

            output.add(PositionedContent(
                emptyColumnHeader,
                applicableRow,
                applicableRow.toString(),
                view[applicableRow].height,
                headerWidth,
                rowHeaderZ,
                mt = applicableY,
                cw = dims.maxX,
                ch = dims.maxY,
                className = "rh",
                x = 0,
                y = null
            ))
        }

        // This is for the cells
        for ((applicableColumn, applicableX) in applicableColumns) {
            if (dirtyColumnHeaders != null && !dirtyColumnHeaders.contains(applicableColumn.columnHeader)) continue
            for ((applicableRow, applicableY) in applicableRows) {
                if (dirtyRowIndices != null && !dirtyRowIndices.contains(applicableRow)) continue
                // TODO Can probably skip empty cells here..
                // TODO PositionedCell will need it's height and width..
                //if (applicableColumn[applicableRow] is UnitCell) continue

                val cell = applicableColumn[applicableRow]

                output.add(PositionedContent(
                    applicableColumn.columnHeader,
                    applicableRow,
                    cell.toString(),
                    view[applicableRow].height,
                    view[applicableColumn].width,
                    className = if (cell is WebCell) "hc c" else "c",
                    x = applicableX + headerWidth,
                    y = applicableY
                ))
            }
        }

        return output
    }

    private fun dims(view:TableView): Dimensions {
        // TODO Consider using a stable snapshot ref for view/table
        val table = view.table ?: return Dimensions(0, 0, 0, 0)

        val headerHeight = view[-1].height
        val headerWidth = view[ColumnHeader()].width

        var maxHeaderOffset = headerHeight
        var runningWidth = headerWidth

        for (column in table.columns) {
            runningWidth += view[column].width

            val yOffset = column.columnHeader.header.mapIndexed { i, _ -> view[(-(column.columnHeader.header.size) + i).toLong()].height }.sum()
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
        }

        var runningHeight = maxHeaderOffset

        val lastKey = table.tableRef.get().indicesMap.last()?.component1() ?: -1
        for (row in 0..lastKey) {
            runningHeight += view[row].height
        }

        return Dimensions(headerWidth, maxHeaderOffset, runningWidth, runningHeight)
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
            client.contentState.clear()

            val jsonParser = Klaxon()
            val clientPackage = ClientPackage()

            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEvent("clear")))

            Registry.getView(client.ref)?.let { view ->
                val dims = dims(view)
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

            val dims = dims(view)
            val clientEventDims = ClientEventDims(dims.cornerX, dims.cornerY, dims.maxX, dims.maxY)

            client.dims = dims

            val clientPackage = ClientPackage(outgoing = mutableListOf(Klaxon().toJsonString(clientEventDims)))
            client.publish(clientPackage)
        }
    }

    private suspend fun handleTiles(client: SigblaClient, scroll: ClientEventScroll) {
        client.mutex.withLock {
            val view = Registry.getView(client.ref) ?: return
            val currentRegion = client.contentState.updateTiles(scroll)

            val dims = client.dims
            val clientPackage = ClientPackage()

            val addedIds = mutableSetOf<String>()

            val addBatch = mutableListOf<ClientEventAddContent>()

            val x = currentRegion.cornerX
            val y = currentRegion.cornerY
            val w = currentRegion.maxX - currentRegion.cornerX
            val h = currentRegion.maxY - currentRegion.cornerY

            val jsonParser = Klaxon()

            areaContent(view, x, y, h, w, dims).forEach { content ->
                val existingId = client.contentState.withIdFor(
                    content.x ?: content.ml ?: throw Exception(),
                    content.y ?: content.mt ?: throw Exception()
                )
                val existingContent = if (existingId == null) null else client.contentState.withPCFor(existingId)

                if (existingContent == null) {
                    val cellId = client.contentState.addIdFor(
                        content.x ?: content.ml ?: throw Exception(),
                        content.y ?: content.mt ?: throw Exception(),
                        content
                    )

                    val addContent = ClientEventAddContent(
                        cellId,
                        content.className,
                        content.x,
                        content.y,
                        content.h,
                        content.w,
                        content.z,
                        content.mt,
                        content.ml,
                        content.ch,
                        content.cw,
                        content.content
                    )

                    addBatch.add(addContent)

                    if (addBatch.size > 25) {
                        clientPackage.outgoing.add(jsonParser.toJsonString(addBatch))
                        addBatch.clear()
                    }

                    addedIds.add(cellId)
                } else if (existingId != null) {
                    addedIds.add(existingId)
                }
            }

            if (addBatch.isNotEmpty()) clientPackage.outgoing.add(jsonParser.toJsonString(addBatch))
            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventAddCommit(UUID.randomUUID().toString())))

            val removeBatch = mutableListOf<ClientEventRemoveContent>()

            client.contentState.withRemainingIds(addedIds).forEach { contentId ->
                val removeContent = ClientEventRemoveContent(contentId)
                removeBatch.add(removeContent)

                if (removeBatch.size > 25) {
                    clientPackage.outgoing.add(Klaxon().toJsonString(removeBatch))
                    removeBatch.clear()
                }

                client.contentState.removeId(contentId)
            }

            if (removeBatch.isNotEmpty()) clientPackage.outgoing.add(jsonParser.toJsonString(removeBatch))

            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventUpdateEnd(UUID.randomUUID().toString())))

            client.publish(clientPackage)
        }
    }

    private suspend fun handleDirty(client: SigblaClient, dirtyCells: Set<Cell<*>>) {
        client.mutex.withLock {
            val view = Registry.getView(client.ref) ?: return
            val currentDims = client.contentState.existingDims

            val clientPackage = ClientPackage()

            val batch = mutableListOf<ClientEventAddContent>()

            val x = currentDims.cornerX
            val y = currentDims.cornerY
            val w = currentDims.maxX - currentDims.cornerX
            val h = currentDims.maxY - currentDims.cornerY

            val jsonParser = Klaxon()

            areaContent(view, x, y, h, w, client.dims, dirtyCells).forEach { content ->
                val existingId = client.contentState.withIdFor(content.x ?: content.ml ?: throw Exception(), content.y ?: content.mt ?: throw Exception())
                val existingContent = if (existingId == null) null else client.contentState.withPCFor(existingId)

                if (existingContent != content) {
                    val cellId = client.contentState.addIdFor(
                        content.x ?: content.ml ?: throw Exception(),
                        content.y ?: content.mt ?: throw Exception(),
                        content
                    )
                    val addContent = ClientEventAddContent(
                        cellId,
                        content.className,
                        content.x,
                        content.y,
                        content.h,
                        content.w,
                        content.z,
                        content.mt,
                        content.ml,
                        content.ch,
                        content.cw,
                        content.content
                    )
                    batch.add(addContent)

                    if (batch.size > 25) {
                        clientPackage.outgoing.add(jsonParser.toJsonString(batch))
                        batch.clear()
                    }
                }
            }

            if (batch.isNotEmpty()) clientPackage.outgoing.add(jsonParser.toJsonString(batch))
            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventAddCommit(UUID.randomUUID().toString())))

            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventUpdateEnd(UUID.randomUUID().toString())))

            client.publish(clientPackage, isDirty = true)
        }
    }

    private fun handleResize(client: SigblaClient, resize: ClientEventResize) {
        // No lock here as we're not sending data
        val view = Registry.getView(client.ref) ?: return
        val target = client.contentState.withPCFor(resize.target) ?: return

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
    val contentState: ContentState = ContentState(),
    val mutex: Mutex = Mutex(),
    @Volatile var dims: Dimensions = Dimensions(0, 0, 0, 0)
) {
    private val outgoingPackages = mutableListOf<ClientPackage>()

    private var clearId: UUID? = null

    suspend fun publish(clientPackage: ClientPackage? = null, isDirty: Boolean = false) {
        if (!mutex.isLocked) throw SigblaAppException("Mutex not locked!")

        if (clearId != null && isDirty) return

        if (clientPackage != null) outgoingPackages.add(clientPackage)

        val overflow = outgoingPackages.size > Companion.MAX_PACKAGES

        if (overflow) {
            outgoingPackages.clear()
            contentState.clear()

            val jsonParser = Klaxon()
            val clearClientPackage = ClientPackage()

            clearClientPackage.outgoing.add(jsonParser.toJsonString(ClientEvent("clear")))

            val clientEventDims = ClientEventDims(dims.cornerX, dims.cornerY, dims.maxX, dims.maxY)

            clearClientPackage.outgoing.add(jsonParser.toJsonString(clientEventDims))

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

    companion object {
        private const val MAX_PACKAGES = 10
    }
}

internal data class ClientSession(val id: String)

@TypeFor(field = "type", adapter = ClientEventAdapter::class)
internal open class ClientEvent(val type: String)
internal data class ClientEventScroll(val x: Long, val y: Long, val h: Long, val w: Long): ClientEvent("scroll")
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

internal data class Coordinate(val x: Long, val y: Long)

internal val idGenerator = AtomicLong()

internal class ContentState(val maxDistance: Int = 2000, val tileSize: Int = 1000) {
    @Volatile
    var existingDims: Dimensions = Dimensions(0, 0, 0, 0)

    private val coordinateIds: ConcurrentMap<Coordinate, String> = ConcurrentHashMap()
    private val idsContent: ConcurrentMap<String, PositionedContent> = ConcurrentHashMap()

    fun updateTiles(scroll: ClientEventScroll): Dimensions {
        val x1 = scroll.x - (scroll.x % tileSize)
        val y1 = scroll.y - (scroll.y % tileSize)
        val x2 = x1 + scroll.w - (scroll.w % tileSize) + tileSize
        val y2 = y1 + scroll.h - (scroll.h % tileSize) + tileSize

        existingDims = Dimensions(x1 - maxDistance, y1 - maxDistance, x2 + maxDistance, y2 + maxDistance)

        return existingDims
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

    fun withRemainingIds(ids: Set<String>) = idsContent.keys.filter { !ids.contains(it) }.toSet()

    fun removeId(id: String) = idsContent.remove(id)

    fun clear() {
        existingDims = Dimensions(0, 0, 0, 0)
        coordinateIds.clear()
        idsContent.clear()
    }
}