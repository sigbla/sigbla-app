package sigbla.app.internals

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import sigbla.app.*
import sigbla.app.exceptions.SigblaAppException
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
import kotlin.concurrent.thread
import kotlin.reflect.KClass

internal object SigblaBackend {
    private val engine: ApplicationEngine
    private val blockingThread: Thread
    val port: Int

    private val listeners: ConcurrentMap<WebSocketSession, SigblaClient> = ConcurrentHashMap()

    init {
        val (engine, port) = start(10)
        this.engine = engine
        this.port = port
        this.blockingThread = thread(name="UI") {
            Thread.sleep(Long.MAX_VALUE)
        }
    }

    private fun start(n: Int): Pair<ApplicationEngine, Int> {
        return try {
            val port = ThreadLocalRandom.current().nextInt(1024, 65535)
            val engine = embeddedServer(Netty, port) {
                install(WebSockets)

                routing {
                    staticResources("/_static", "_static")
                    route("/t/{ref}", HttpMethod.Get) {
                        handle {
                            val ref = call.parameters["ref"]

                            if (ref == null || ref.isBlank()) {
                                call.respondText(status = HttpStatusCode.NotFound, text = "Not found")
                                return@handle
                            }

                            val view = Registry.getView(ref)
                            if (view == null) {
                                call.respondText(status = HttpStatusCode.NotFound, text = "Not found")
                                return@handle
                            }

                            call.respondRedirect("/t/$ref/", permanent = true)
                        }
                    }
                    route("/t/{ref}/", HttpMethod.Get) {
                        handle {
                            val ref = call.parameters["ref"]

                            if (ref == null || ref.isBlank()) {
                                call.respondText(status = HttpStatusCode.NotFound, text = "Not found")
                                return@handle
                            }

                            val view = Registry.getView(ref)
                            if (view == null) {
                                call.respondText(status = HttpStatusCode.NotFound, text = "Not found")
                                return@handle
                            }

                            call.respondText(
                                ContentType.Text.Html,
                                HttpStatusCode.OK
                            ) {
                                this.javaClass.getResource("/table/table.html").readText().replace("\${title}", ref)
                            }
                        }
                    }
                    webSocket("/t/{ref}/socket") {
                        val ref = call.parameters["ref"]

                        if (ref == null || ref.isBlank()) {
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No ref"))
                            return@webSocket
                        }

                        val view = Registry.getView(ref)
                        if (view == null) {
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No ref"))
                        }

                        val client = addListener(this, ref)

                        handleDims(client)

                        try {
                            val jsonParser = Klaxon()

                            while (true) {
                                when (val frame = incoming.receive()) {
                                    is Frame.Text -> {
                                        val text = frame.readText()
                                        val event = jsonParser.parse<ClientEvent>(text)
                                        if (event != null) handleEvent(this, event)
                                    }
                                    else -> { /* ignore */ }
                                }
                            }
                        } catch (ex: Exception) {
                            println("Closing listener due to: " + ex.message)
                            removeListener(this)
                        }
                    }
                    route("/t/{ref}/{resource...}") {
                        handle {
                            val ref = call.parameters["ref"]

                            if (ref == null || ref.isBlank()) {
                                call.respondText(status = HttpStatusCode.NotFound, text = "Not found")
                                return@handle
                            }

                            val view = Registry.getView(ref)
                            if (view == null) {
                                call.respondText(status = HttpStatusCode.NotFound, text = "Not found")
                                return@handle
                            }

                            val resource = call.parameters.getAll("resource")?.joinToString("/")
                            val handler = if (resource == null) null else view[Resources]._resources[resource]
                            if (handler == null) {
                                call.respondText(status = HttpStatusCode.NotFound, text = "Not found")
                                return@handle
                            }

                            handler()
                        }
                    }
                }
            }.start(wait = false)

            return Pair(engine, port)
        } catch (ex: Exception) {
            if (n - 1 > 0) start(n - 1)
            else throw ex
        }
    }

    private fun areaContent(view: TableView, x: Long, y: Long, h: Long, w: Long, dims: Dimensions, dirtyCells: List<Cell<*>>? = null): List<PositionedContent> {
        val table = view[Table]

        val dirtyColumnHeaders = dirtyCells?.map { it.column.columnHeader }?.toSet()
        val dirtyRowIndices = dirtyCells?.map { it.index }?.toSet()

        val applicableColumns = mutableListOf<Pair<Column, Long>>()
        var runningWidth = 0L
        var maxHeaderOffset = 0L
        var maxHeaderCells = 0

        val headerWidth = view[emptyColumnHeader].derived.cellWidth

        for (column in table.columns) {
            if (x <= runningWidth && runningWidth <= x + w) applicableColumns.add(Pair(column, runningWidth))
            runningWidth += view[column].derived.cellWidth

            val yOffset = column.columnHeader.header.mapIndexed { i, _ -> view[(-(column.columnHeader.header.size) + i).toLong()].derived.cellHeight }.sum()
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
                val yOffset = applicableColumn.columnHeader.header.mapIndexed { i, _ -> if (i < idx) view[(-maxHeaderCells + i).toLong()].derived.cellHeight else 0L }.sum()

                output.add(PositionedContent(
                    applicableColumn.columnHeader,
                    (-maxHeaderCells + idx).toLong(),
                    headerText,
                    view[(-maxHeaderCells + idx).toLong()].derived.cellHeight,
                    view[applicableColumn].derived.cellWidth,
                    colHeaderZ,
                    ml = applicableX + headerWidth,
                    cw = dims.maxX,
                    ch = dims.maxY,
                    // TODO This needs to use the derived column to get tableView level classes and topics
                    className = ("ch " + view[applicableColumn].derived.cellClasses.joinToString(separator = " ")).trim(),
                    topics = view[applicableColumn].derived.cellTopics.toList(),
                    x = null,
                    y = yOffset
                ))
            }
        }

        val applicableRows = mutableListOf<Pair<Long, Long>>()
        var runningHeight = maxHeaderOffset

        val lastKey = table.tableRef.get().columnCells.values().map { it.last()?.component1() ?: -1 }.maxOrNull() ?: -1
        for (row in 0..lastKey) {
            if (y <= runningHeight && runningHeight <= y + h) applicableRows.add(Pair(row, runningHeight))

            runningHeight += view[row].derived.cellHeight

            if (runningHeight > y + h || runningHeight < 0L) break
        }

        // This is for the row headers
        for ((applicableRow, applicableY) in applicableRows) {
            if (dirtyRowIndices != null && !dirtyRowIndices.contains(applicableRow)) continue

            output.add(PositionedContent(
                emptyColumnHeader,
                applicableRow,
                applicableRow.toString(),
                view[applicableRow].derived.cellHeight,
                headerWidth,
                rowHeaderZ,
                mt = applicableY,
                cw = dims.maxX,
                ch = dims.maxY,
                // TODO This needs to use the derived row to get tableView level classes and topics
                className = ("rh " + view[applicableRow].derived.cellClasses.joinToString(separator = " ")).trim(),
                topics = view[applicableRow].derived.cellTopics.toList(),
                x = 0,
                y = null
            ))
        }

        // This is for the cells
        for ((applicableColumn, applicableX) in applicableColumns) {
            if (dirtyColumnHeaders != null && !dirtyColumnHeaders.contains(applicableColumn.columnHeader)) continue
            for ((applicableRow, applicableY) in applicableRows) {
                if (dirtyRowIndices != null && !dirtyRowIndices.contains(applicableRow)) continue

                val cell = applicableColumn[applicableRow]

                // TODO Consider option to skip empty cells here..
                //if (cell is UnitCell) continue

                output.add(PositionedContent(
                    applicableColumn.columnHeader,
                    applicableRow,
                    if (cell is UnitCell) null else cell.toString(),
                    view[applicableRow].derived.cellHeight,
                    view[applicableColumn].derived.cellWidth,
                    className = ((if (cell is WebCell) "hc c " else "c ") + view[applicableColumn, applicableRow].derived.cellClasses.joinToString(separator = " ")).trim(),
                    topics = view[applicableColumn, applicableRow].derived.cellTopics.toList(),
                    x = applicableX + headerWidth,
                    y = applicableY
                ))
            }
        }

        return output
    }

    private fun dims(view: TableView): Dimensions {
        // TODO Consider using a stable snapshot ref for view/table
        val table = view[Table] ?: return Dimensions(0, 0, 0, 0)

        val headerHeight = view[-1].derived.cellHeight
        val headerWidth = view[emptyColumnHeader].derived.cellWidth

        var maxHeaderOffset = headerHeight
        var runningWidth = headerWidth

        for (column in table.columns) {
            runningWidth += view[column].derived.cellWidth

            val yOffset = column.columnHeader.header.mapIndexed { i, _ -> view[(-(column.columnHeader.header.size) + i).toLong()].derived.cellHeight }.sum()
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
        }

        var runningHeight = maxHeaderOffset

        val lastKey = table.tableRef.get().columnCells.values().map { it.last()?.component1() ?: -1 }.maxOrNull() ?: -1
        for (row in 0..lastKey) {
            runningHeight += view[row].derived.cellHeight
        }

        return Dimensions(headerWidth, maxHeaderOffset, runningWidth, runningHeight)
    }

    private suspend fun handleEvent(socket: WebSocketSession, event: ClientEvent) {
        val client = listeners[socket] ?: return

        when {
            event.type == ClientEventType.CLEAR.type -> handleClear(client)
            event is ClientEventScroll -> handleTiles(client, event)
            event is ClientEventResize -> handleResize(client, event)
            event is ClientEventPackageEnd -> handlePackageEnd(client, event)
        }
    }

    private suspend fun handleClear(client: SigblaClient) {
        client.mutex.withLock {
            client.contentState.clear()

            val jsonParser = Klaxon()
            val clientPackage = ClientPackage(client.nextEventId())

            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEvent(ClientEventType.CLEAR.type)))

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

            val clientPackage = ClientPackage(client.nextEventId(), outgoing = mutableListOf(Klaxon().toJsonString(clientEventDims)))
            client.publish(clientPackage)
        }
    }

    private suspend fun handleTiles(client: SigblaClient, scroll: ClientEventScroll) {
        client.mutex.withLock {
            val view = clone(Registry.getView(client.ref) ?: return)
            val currentRegion = client.contentState.updateTiles(scroll)

            val dims = client.dims
            val clientPackage = ClientPackage(client.nextEventId())

            val addedIds = mutableSetOf<Long>()

            val addBatch = mutableListOf<ClientEventAddContent>()

            val x = currentRegion.cornerX
            val y = currentRegion.cornerY
            val w = currentRegion.maxX - currentRegion.cornerX
            val h = currentRegion.maxY - currentRegion.cornerY

            val jsonParser = Klaxon()

            val cssUrls = view[Resources]._resources.filter { cssHandlers.contains(it.component2()) }.map { it.component1() }.toList()
            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventLoadCSS(cssUrls)))

            val jsUrls = view[Resources]._resources.filter { jsHandlers.contains(it.component2()) }.map { it.component1() }.toList()
            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventLoadJavaScript(jsUrls)))

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
                        "c$cellId",
                        content.className,
                        content.topics,
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

            if (clientPackage.outgoing.isNotEmpty()) {
                clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventAddCommit(client.nextEventId().toString())))
            }

            val removeBatch = mutableListOf<ClientEventRemoveContent>()

            client.contentState.withRemainingIds(addedIds).forEach { contentId ->
                val removeContent = ClientEventRemoveContent("c$contentId")
                removeBatch.add(removeContent)

                if (removeBatch.size > 25) {
                    clientPackage.outgoing.add(jsonParser.toJsonString(removeBatch))
                    removeBatch.clear()
                }

                client.contentState.removeId(contentId)
            }

            if (removeBatch.isNotEmpty()) clientPackage.outgoing.add(jsonParser.toJsonString(removeBatch))

            // TODO Consider if there are some edge cases where we still want to send this on empty outgoing?
            if (clientPackage.outgoing.isNotEmpty()) {
                clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventUpdateEnd(client.nextEventId().toString())))
            }

            client.publish(clientPackage)
        }
    }

    private suspend fun handleDirty(client: SigblaClient, dirtyCells: List<Cell<*>>) {
        client.mutex.withLock {
            val view = clone(Registry.getView(client.ref) ?: return)
            val currentDims = client.contentState.existingDims

            val clientPackage = ClientPackage(client.nextEventId())

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
                        "c$cellId",
                        content.className,
                        content.topics,
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

            // TODO Consider if there are some edge cases where we still want to send this on empty outgoing?
            if (clientPackage.outgoing.isNotEmpty()) {
                clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventAddCommit(client.nextEventId().toString())))

                clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventUpdateEnd(client.nextEventId().toString())))
            }

            client.publish(clientPackage, isDirty = true)
        }
    }

    private fun handleResize(client: SigblaClient, resize: ClientEventResize) {
        // No lock here as we're not sending data
        val view = Registry.getView(client.ref) ?: return
        // Note that resize.target is the client side element id
        val targetId = resize.target.substring(1).toLong()
        val target = client.contentState.withPCFor(targetId) ?: return

        if (resize.sizeChangeX != 0L) {
            val columnView = view[target.contentHeader]
            view[target.contentHeader][CellWidth] = 10L.coerceAtLeast(columnView.derived.cellWidth + resize.sizeChangeX)
        }

        if (resize.sizeChangeY != 0L) {
            val rowView = view[target.contentRow]
            view[target.contentRow][CellHeight] = 10L.coerceAtLeast(rowView.derived.cellHeight + resize.sizeChangeY)
        }
    }

    private suspend fun handlePackageEnd(client: SigblaClient, packageEnd: ClientEventPackageEnd) {
        client.mutex.withLock {
            client.purge(packageEnd.id.toLong())
        }
    }

    fun openView(view: TableView) {
        // TODO It's possible for a view to be replaced by another view on same name.
        //      We'll need to ensure we remove this listener and add a new listener in that case.
        on(view) {
            skipHistory = true
            order = Long.MAX_VALUE
            name = "UI"

            events {
                if (any()) {
                    // TODO: Might be able to just do as below for table events and not clear if we just extract cells?
                    var clear = false
                    val dirtyCells = mutableListOf<Cell<*>>()
                    for (event in this) {
                        when (val value = cellOrFalseFromViewRelated(event.newValue)) {
                            false -> {
                                clear = true
                                break
                            }
                            is Cell<*> -> dirtyCells.add(value)
                        }
                    }

                    if (clear) {
                        println("force clear")
                        listeners.values.forEach { client ->
                            if (client.ref == source.name) {
                                runBlocking {
                                    handleClear(client)
                                }
                            }
                        }
                    } else {
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
        }

        // TODO As with the view, the underlying table might also change, so we need to remove
        //      and add a new listener when this happens..
        view.tableViewRef.get().table?.apply {
            on(this) {
                skipHistory = true
                order = Long.MAX_VALUE
                name = "UI"

                events {
                    if (any()) {
                        val dirtyCells = map { it.newValue }.toList()
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
        listeners.remove(socket)
    }
}

internal data class ClientPackage(
    val id: Long,
    val outgoing: MutableList<String> = mutableListOf(),
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

    private var clearId: Long? = null

    private val idGenerator = AtomicLong()
    internal fun nextEventId() = idGenerator.getAndIncrement()

    suspend fun publish(clientPackage: ClientPackage? = null, isDirty: Boolean = false) {
        if (!mutex.isLocked) throw SigblaAppException("Mutex not locked!")

        if (clearId != null && isDirty) return

        if (clientPackage != null) outgoingPackages.add(clientPackage)

        val overflow = outgoingPackages.size > MAX_PACKAGES

        if (overflow) {
            println("overflow")

            outgoingPackages.clear()
            contentState.clear()

            val jsonParser = Klaxon()
            val clearClientPackage = ClientPackage(nextEventId())

            clearClientPackage.outgoing.add(jsonParser.toJsonString(ClientEvent(ClientEventType.CLEAR.type)))

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

    suspend fun purge(id: Long) {
        if (!mutex.isLocked) throw SigblaAppException("Mutex not locked!")
        outgoingPackages.removeIf { it.id == id }
        if (clearId == id) clearId = null
        publish()
    }

    companion object {
        private const val MAX_PACKAGES = 100
    }
}

internal enum class ClientEventType(val type: Int) {
    CLEAR(0),
    SCROLL(1),
    ADD_CONTENT(2),
    ADD_COMMIT(3),
    REMOVE_CONTENT(4),
    UPDATE_END(5),
    PACKAGE_END(6),
    DIMS(7),
    RESIZE(8),
    LOAD_CSS(9),
    LOAD_JS(10)
}

@TypeFor(field = "type", adapter = ClientEventAdapter::class)
internal open class ClientEvent(
    val type: Int
)

internal data class ClientEventScroll(
    val x: Long,
    val y: Long,
    val h: Long,
    val w: Long
): ClientEvent(ClientEventType.SCROLL.type)

internal data class ClientEventAddContent(
    val id: String,
    val classes: String,
    val topics: List<String>?,
    val x: Long?,
    val y: Long?,
    val h: Long,
    val w: Long,
    val z: Long?,
    val mt: Long?,
    val ml: Long?,
    val ch: Long?,
    val cw: Long?,
    val content: String?
): ClientEvent(ClientEventType.ADD_CONTENT.type)

internal data class ClientEventAddCommit(
    val id: String
): ClientEvent(ClientEventType.ADD_COMMIT.type)

internal data class ClientEventRemoveContent(
    val id: String
): ClientEvent(ClientEventType.REMOVE_CONTENT.type)

internal data class ClientEventUpdateEnd(
    val id: String
): ClientEvent(ClientEventType.UPDATE_END.type)

internal data class ClientEventPackageEnd(
    val id: String
): ClientEvent(ClientEventType.PACKAGE_END.type)

internal data class ClientEventDims(
    val cornerX: Long,
    val cornerY: Long,
    val maxX: Long,
    val maxY: Long
): ClientEvent(ClientEventType.DIMS.type)

internal data class ClientEventResize(
    val target: String,
    val sizeChangeX: Long,
    val sizeChangeY: Long
): ClientEvent(ClientEventType.RESIZE.type)

internal data class ClientEventLoadCSS(
    val urls: List<String>
): ClientEvent(ClientEventType.LOAD_CSS.type)

internal data class ClientEventLoadJavaScript(
    val urls: List<String>
): ClientEvent(ClientEventType.LOAD_JS.type)

internal class ClientEventAdapter: TypeAdapter<ClientEvent> {
    override fun classFor(type: Any): KClass<out ClientEvent> = when(type as Int) {
        ClientEventType.CLEAR.type -> ClientEvent::class
        ClientEventType.SCROLL.type -> ClientEventScroll::class
        ClientEventType.RESIZE.type -> ClientEventResize::class
        ClientEventType.PACKAGE_END.type -> ClientEventPackageEnd::class
        else -> throw IllegalArgumentException("Unknown type: $type")
    }
}

internal data class Coordinate(val x: Long, val y: Long)

internal data class PositionedContent(
    val contentHeader: ColumnHeader,
    val contentRow: Long,
    val content: String?,
    val h: Long,
    val w: Long,
    val z: Long? = null,
    val mt: Long? = null, // Margin top
    val ml: Long? = null, // Margin left
    val cw: Long? = null, // Container width
    val ch: Long? = null, // Container height
    val className: String = "",
    val topics: List<String>?,
    val x: Long?,
    val y: Long?
)

internal class Dimensions(val cornerX: Long, val cornerY: Long, val maxX: Long, val maxY: Long)

internal class ContentState(val maxDistance: Int = 2000, val tileSize: Int = 1000) {
    @Volatile
    var existingDims: Dimensions = Dimensions(0, 0, 0, 0)

    private val contentIDGenerator = AtomicLong()
    private val coordinateIds: ConcurrentMap<Coordinate, Long> = ConcurrentHashMap()
    private val idsContent: ConcurrentMap<Long, PositionedContent> = ConcurrentHashMap()

    fun updateTiles(scroll: ClientEventScroll): Dimensions {
        val x1 = scroll.x - (scroll.x % tileSize)
        val y1 = scroll.y - (scroll.y % tileSize)
        val x2 = x1 + scroll.w - (scroll.w % tileSize) + tileSize
        val y2 = y1 + scroll.h - (scroll.h % tileSize) + tileSize

        existingDims = Dimensions(x1 - maxDistance, y1 - maxDistance, x2 + maxDistance, y2 + maxDistance)

        return existingDims
    }

    fun addIdFor(x: Long, y: Long, pc: PositionedContent): Long {
        val id = coordinateIds.computeIfAbsent(Coordinate(x, y)) {
            contentIDGenerator.getAndIncrement()
        }

        idsContent[id] = pc

        return id
    }

    fun withIdFor(x: Long, y: Long) = coordinateIds[Coordinate(x, y)]

    fun withPCFor(id: Long) = idsContent[id]

    fun withRemainingIds(ids: Set<Long>) = idsContent.keys.filter { !ids.contains(it) }.toSet()

    fun removeId(id: Long) = idsContent.remove(id)

    fun clear() {
        existingDims = Dimensions(0, 0, 0, 0)
        coordinateIds.clear()
        idsContent.clear()
    }
}