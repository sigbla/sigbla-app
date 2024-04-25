/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.internals

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sigbla.app.*
import sigbla.app.exceptions.InvalidTableViewException
import sigbla.app.exceptions.InvalidUIException
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.set
import kotlin.concurrent.thread
import kotlin.reflect.KClass

private val logger: Logger = LoggerFactory.getLogger("sigbla.app.internals.UI")

internal object SigblaBackend {
    private val engine: ApplicationEngine
    private val port: Int
    private val blockingThread: Thread

    // TODO Add a ping event regularly on all listeners
    private val listeners: ConcurrentMap<WebSocketSession, SigblaClient> = ConcurrentHashMap()
    private val viewRefs: ConcurrentMap<String, UIViewRef> = ConcurrentHashMap()

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
            val host = TableView[Host]
            val port = TableView[Port]
            val engine = embeddedServer(Netty, port, host) {
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

                            val view = viewRefs[ref]?.tableView
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

                            val viewRef = viewRefs[ref] ?: run {
                                call.respondText(status = HttpStatusCode.NotFound, text = "Not found")
                                return@handle
                            }

                            viewRef.config.tableHtml(this)
                        }
                    }
                    webSocket("/t/{ref}/socket") {
                        val ref = call.parameters["ref"]

                        if (ref == null || ref.isBlank()) {
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No ref"))
                            return@webSocket
                        }

                        val view = viewRefs[ref]?.tableView
                        if (view == null) {
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No ref"))
                            // TODO return@webSocket ?
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
                            logger.error("Closing listener on $ref due to: ${ex.message}", ex)
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

                            val viewRef = viewRefs[ref] ?: run {
                                call.respondText(status = HttpStatusCode.NotFound, text = "Not found")
                                return@handle
                            }

                            val view = viewRef.tableView
                            val handler = when (val resource = call.parameters.getAll("resource")?.joinToString("/")) {
                                null -> null
                                "table.js" -> viewRef.config.tableScript
                                "table.css" -> viewRef.config.tableStyle
                                else -> view[Resources]._resources[resource]?.second
                            }
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

    private fun areaContent(view: TableView, viewRef: UIViewRef, x: Long, y: Long, h: Long, w: Long, dims: Dimensions, dirtyCells: List<Cell<*>>? = null): List<PositionedContent> {
        val marginTop = viewRef.config.marginTop
        val marginBottom = viewRef.config.marginBottom
        val marginLeft = viewRef.config.marginLeft
        val marginRight = viewRef.config.marginRight
        val paddingTop = viewRef.config.paddingTop
        val paddingBottom = viewRef.config.paddingBottom
        val paddingLeft = viewRef.config.paddingLeft
        val paddingRight = viewRef.config.paddingRight

        val table = view[Table]

        val dirtyColumnHeaders = dirtyCells?.map { it.column.header }?.toSet()
        val dirtyRowIndices = dirtyCells?.map { it.index }?.toSet()

        val applicableColumns = mutableListOf<Pair<Column, Long>>()
        var runningWidth = marginRight + marginLeft // This is margin right for row header + margin left for first cell column
        var maxHeaderOffset = 0L
        var maxHeaderCells = 0

        val headerWidth = view[EMPTY_HEADER].derived.cellWidth + paddingLeft + paddingRight

        for (column in table.columns) {
            if (x <= runningWidth && runningWidth <= x + w) applicableColumns.add(Pair(column, runningWidth))
            runningWidth += view[column].derived.cellWidth + marginLeft + marginRight + paddingLeft + paddingRight

            val yOffset = column.header.labels.mapIndexed { i, _ ->
                view[(-(column.header.labels.size) + i).toLong()].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
            }.sum()
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
            if (column.header.labels.size > maxHeaderCells) maxHeaderCells = column.header.labels.size
        }

        val colHeaderZ = Integer.MAX_VALUE.toLong()
        val rowHeaderZ = Integer.MAX_VALUE.toLong()

        val output = mutableListOf<PositionedContent>()

        // This is for the column headers
        for ((applicableColumn, applicableX) in applicableColumns) {
            if (dirtyColumnHeaders != null && !dirtyColumnHeaders.contains(applicableColumn.header)) continue

            for (idx in 0 until maxHeaderCells) {
                val headerText = applicableColumn.header[idx] ?: ""
                val yOffset = applicableColumn.header.labels.mapIndexed { i, _ ->
                    if (i < idx)
                        view[(-maxHeaderCells + i).toLong()].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
                    else 0L
                }.sum()

                val cellView = view[applicableColumn, (-maxHeaderCells + idx).toLong()]
                val derivedCellView = cellView.derived

                output.add(PositionedContent(
                    applicableColumn.header,
                    (-maxHeaderCells + idx).toLong(),
                    headerText,
                    derivedCellView.cellHeight + paddingTop + paddingBottom,
                    derivedCellView.cellWidth + paddingLeft + paddingRight,
                    colHeaderZ,
                    ml = applicableX + headerWidth,
                    cw = dims.maxX,
                    ch = dims.maxY,
                    className = ("ch " + derivedCellView.cellClasses.joinToString(separator = " ")).trim(),
                    topics = derivedCellView.cellTopics.toList(),
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

            runningHeight += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom

            if (runningHeight > y + h || runningHeight < 0L) break
        }

        // This is for the row headers
        for ((applicableRow, applicableY) in applicableRows) {
            if (dirtyRowIndices != null && !dirtyRowIndices.contains(applicableRow)) continue

            val cellView = view[EMPTY_HEADER, applicableRow]
            val derivedCellView = cellView.derived

            output.add(PositionedContent(
                EMPTY_HEADER,
                applicableRow,
                applicableRow.toString(),
                derivedCellView.cellHeight + paddingTop + paddingBottom,
                derivedCellView.cellWidth + paddingLeft + paddingRight,
                rowHeaderZ,
                mt = applicableY,
                cw = dims.maxX,
                ch = dims.maxY,
                className = ("rh " + derivedCellView.cellClasses.joinToString(separator = " ")).trim(),
                topics = derivedCellView.cellTopics.toList(),
                x = 0,
                y = null
            ))
        }

        // This is for the cells
        for ((applicableColumn, applicableX) in applicableColumns) {
            if (dirtyColumnHeaders != null && !dirtyColumnHeaders.contains(applicableColumn.header)) continue
            for ((applicableRow, applicableY) in applicableRows) {
                if (dirtyRowIndices != null && !dirtyRowIndices.contains(applicableRow)) continue

                val cell = applicableColumn[applicableRow]
                val cellView = view[cell]
                val derivedCellView = cellView.derived

                // TODO Consider option to skip empty cells here..
                //if (cell is UnitCell) continue

                output.add(PositionedContent(
                    applicableColumn.header,
                    applicableRow,
                    if (cell is UnitCell) null else cell.toString(),
                    derivedCellView.cellHeight + paddingTop + paddingBottom,
                    derivedCellView.cellWidth + paddingLeft + paddingRight,
                    className = ((if (cell is WebCell) "hc cc " else "cc ") + derivedCellView.cellClasses.joinToString(separator = " ")).trim(),
                    topics = derivedCellView.cellTopics.toList(),
                    x = applicableX + headerWidth,
                    y = applicableY
                ))
            }
        }

        return output
    }

    private fun dims(view: TableView, viewRef: UIViewRef): Dimensions {
        val marginTop = viewRef.config.marginTop
        val marginBottom = viewRef.config.marginBottom
        val marginLeft = viewRef.config.marginLeft
        val marginRight = viewRef.config.marginRight
        val paddingTop = viewRef.config.paddingTop
        val paddingBottom = viewRef.config.paddingBottom
        val paddingLeft = viewRef.config.paddingLeft
        val paddingRight = viewRef.config.paddingRight

        val table = view[Table]

        val headerHeight = view[-1].derived.cellHeight + paddingTop + paddingBottom
        val headerWidth = view[EMPTY_HEADER].derived.cellWidth + paddingLeft + paddingRight

        var maxHeaderOffset = headerHeight
        var runningWidth = headerWidth

        for (column in table.columns) {
            runningWidth += view[column].derived.cellWidth + marginLeft + marginRight + paddingLeft + paddingRight

            val yOffset = column.header.labels.mapIndexed { i, _ ->
                view[(-(column.header.labels.size) + i).toLong()].derived.cellHeight + paddingTop + paddingBottom
            }.sum()
            val topMargins = (column.header.labels.size - 1) * marginTop
            val bottomMargins = (column.header.labels.size - 1) * marginBottom
            val combinedOffset = yOffset + topMargins + bottomMargins
            if (combinedOffset > maxHeaderOffset) maxHeaderOffset = combinedOffset
        }

        var runningHeight = maxHeaderOffset

        val lastKey = table.tableRef.get().columnCells.values().map { it.last()?.component1() ?: -1 }.maxOrNull() ?: -1
        for (row in 0..lastKey) {
            runningHeight += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
        }

        return Dimensions(headerWidth, maxHeaderOffset, marginLeft + marginRight, marginTop + marginBottom, runningWidth, runningHeight)
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

            val viewRef = viewRefs[client.ref] ?: return
            val view = clone(viewRef.tableView)

            val dims = dims(view, viewRef)
            val clientEventDims = ClientEventDims(dims.cornerX, dims.cornerY, dims.cornerRightMargin, dims.cornerBottomMargin, dims.maxX, dims.maxY)

            client.dims = dims

            clientPackage.outgoing.add(jsonParser.toJsonString(clientEventDims))

            client.publish(clientPackage)
        }
    }

    private suspend fun handleDims(client: SigblaClient) {
        client.mutex.withLock {
            val viewRef = viewRefs[client.ref] ?: return
            val view = clone(viewRef.tableView)

            val dims = dims(view, viewRef)
            val clientEventDims = ClientEventDims(dims.cornerX, dims.cornerY, dims.cornerRightMargin, dims.cornerBottomMargin, dims.maxX, dims.maxY)

            client.dims = dims

            val clientPackage = ClientPackage(client.nextEventId(), outgoing = mutableListOf(Klaxon().toJsonString(clientEventDims)))
            client.publish(clientPackage)
        }
    }

    private suspend fun handleTiles(client: SigblaClient, scroll: ClientEventScroll) {
        client.mutex.withLock {
            val viewRef = viewRefs[client.ref] ?: return
            val view = clone(viewRef.tableView)

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

            val cssUrls = view[Resources]._resources.filter { cssHandlers.contains(it.component2().second) }.sortedBy { it.component2().first }.map { it.component1() }.toList()
            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventLoadCSS(cssUrls)))

            val jsUrls = view[Resources]._resources.filter { jsHandlers.contains(it.component2().second) }.sortedBy { it.component2().first }.map { it.component1() }.toList()
            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventLoadJavaScript(jsUrls)))

            areaContent(view, viewRef, x, y, h, w, dims).forEach { content ->
                val existingId = client.contentState.withIdFor(
                    // TODO Use InvalidUIException?
                    content.x ?: content.ml ?: throw Exception(),
                    content.y ?: content.mt ?: throw Exception()
                )
                val existingContent = if (existingId == null) null else client.contentState.withPCFor(existingId)

                if (existingContent == null) {
                    val cellId = client.contentState.addIdFor(
                        // TODO Use InvalidUIException?
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

    private suspend fun handleDirty(client: SigblaClient, dirtyCells: List<Cell<*>>, dirtyResources: List<Resources>) {
        client.mutex.withLock {
            val viewRef = viewRefs[client.ref] ?: return
            val view = clone(viewRef.tableView)

            val currentDims = client.contentState.existingRegion

            val clientPackage = ClientPackage(client.nextEventId())

            val batch = mutableListOf<ClientEventAddContent>()

            val x = currentDims.cornerX
            val y = currentDims.cornerY
            val w = currentDims.maxX - currentDims.cornerX
            val h = currentDims.maxY - currentDims.cornerY

            val jsonParser = Klaxon()

            val cssUrls = dirtyResources.asSequence().map { it._resources }.flatten().filter { cssHandlers.contains(it.component2().second) }.sortedBy { it.component2().first }.map { it.component1() }.distinct().toList()
            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventLoadCSS(cssUrls, dirty = true)))

            val jsUrls = dirtyResources.asSequence().map { it._resources }.flatten().filter { jsHandlers.contains(it.component2().second) }.sortedBy { it.component2().first }.map { it.component1() }.distinct().toList()
            clientPackage.outgoing.add(jsonParser.toJsonString(ClientEventLoadJavaScript(jsUrls, dirty = true)))

            areaContent(view, viewRef, x, y, h, w, client.dims, dirtyCells).forEach { content ->
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
        // No view clone here as we're manipulating the view below
        val view = viewRefs[client.ref]?.tableView ?: return
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

    @Synchronized
    fun openView(view: TableView, ref: String, config: ViewConfig, urlGenerator: (engine: ApplicationEngine, view: TableView, ref: String) -> URL): URL {
        fun tableSubscription(table: Table?): TableListenerReference? {
            if (table == null) return null

            return on(table) {
                skipHistory = true
                order = Long.MAX_VALUE
                name = "UI"

                events {
                    if (any()) {
                        val dirtyCells = map { it.newValue }.toList()
                        listeners.values.forEach { client ->
                            if (client.ref == ref) {
                                runBlocking {
                                    handleDims(client)
                                    handleDirty(client, dirtyCells, emptyList())
                                }
                            }
                        }
                    }
                }
            }
        }

        val viewListener = on(view) {
            skipHistory = true
            order = Long.MAX_VALUE
            name = "UI"

            events {
                if (any()) {
                    // TODO: Might be able to just do as below for table events and not clear if we just extract cells?
                    var clear = false
                    val dirtyCells = mutableListOf<Cell<*>>()
                    val dirtyResources = mutableListOf<Resources>()
                    for (event in this) {
                        when (val value = cellOrResourceOrSourceTableOrFalseFromViewRelated(event.newValue)) {
                            false -> {
                                clear = true
                                break
                            }
                            is SourceTable -> {
                                synchronized(SigblaBackend) {
                                    val viewRef = viewRefs[ref] ?: throw InvalidTableViewException("No view ref on $ref")
                                    viewRef.tableListener?.apply { off(this) }
                                    if (!viewRefs.replace(ref, viewRef, viewRef.copy(
                                        tableListener = tableSubscription(value.table)
                                    ))) throw InvalidTableViewException("Unexpected view ref update")
                                }

                                clear = true
                                break
                            }
                            is Cell<*> -> dirtyCells.add(value)
                            is Resources -> dirtyResources.add(value)
                        }
                    }

                    if (clear) {
                        logger.debug("Forcing a clear on {}", ref)
                        listeners.values.forEach { client ->
                            if (client.ref == ref) {
                                runBlocking {
                                    handleClear(client)
                                }
                            }
                        }
                    } else {
                        listeners.values.forEach { client ->
                            if (client.ref == ref) {
                                runBlocking {
                                    handleDims(client)
                                    handleDirty(client, dirtyCells, dirtyResources)
                                }
                            }
                        }
                    }
                }
            }
        }

        val tableListener = tableSubscription(view.tableViewRef.get().table)

        viewRefs.put(ref, UIViewRef(view, viewListener, tableListener, config))?.apply {
            off(this.viewListener)
            this.tableListener?.apply { off(this) }

            logger.debug("Forcing a clear on {}", ref)
            listeners.values.forEach { client ->
                if (client.ref == ref) {
                    runBlocking {
                        handleClear(client)
                    }
                }
            }
        }

        // TODO Clean up listeners if a tableview is removed
        // TODO Look at shutting down ktor if all views are removed?

        return urlGenerator(engine, view, ref)
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

internal data class UIViewRef(
    val tableView: TableView,
    val viewListener: TableViewListenerReference,
    val tableListener: TableListenerReference?,
    val config: ViewConfig
)

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
    @Volatile var dims: Dimensions = Dimensions(0, 0, 0, 0, 0, 0)
) {
    private val outgoingPackages = mutableListOf<ClientPackage>()

    private var clearId: Long? = null

    private val idGenerator = AtomicLong()
    internal fun nextEventId() = idGenerator.getAndIncrement()

    suspend fun publish(clientPackage: ClientPackage? = null, isDirty: Boolean = false) {
        if (!mutex.isLocked) throw InvalidUIException("Mutex not locked!")

        if (clearId != null && isDirty) return

        if (clientPackage != null) outgoingPackages.add(clientPackage)

        val overflow = outgoingPackages.size > MAX_PACKAGES

        if (overflow) {
            logger.debug("View overflow on {}", ref)

            outgoingPackages.clear()
            contentState.clear()

            val jsonParser = Klaxon()
            val clearClientPackage = ClientPackage(nextEventId())

            clearClientPackage.outgoing.add(jsonParser.toJsonString(ClientEvent(ClientEventType.CLEAR.type)))

            val clientEventDims = ClientEventDims(dims.cornerX, dims.cornerY, dims.cornerBottomMargin, dims.cornerRightMargin, dims.maxX, dims.maxY)

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
        if (!mutex.isLocked) throw InvalidUIException("Mutex not locked!")
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
    val cornerRightMargin: Long,
    val cornerBottomMargin: Long,
    val maxX: Long,
    val maxY: Long
): ClientEvent(ClientEventType.DIMS.type)

internal data class ClientEventResize(
    val target: String,
    val sizeChangeX: Long,
    val sizeChangeY: Long
): ClientEvent(ClientEventType.RESIZE.type)

internal data class ClientEventLoadCSS(
    val urls: List<String>,
    val dirty: Boolean = false
): ClientEvent(ClientEventType.LOAD_CSS.type)

internal data class ClientEventLoadJavaScript(
    val urls: List<String>,
    val dirty: Boolean = false
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
    val contentHeader: Header,
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

internal class Region(val cornerX: Long, val cornerY: Long, val maxX: Long, val maxY: Long)
internal class Dimensions(val cornerX: Long, val cornerY: Long, val cornerRightMargin: Long, val cornerBottomMargin: Long, val maxX: Long, val maxY: Long)

internal class ContentState(val maxDistance: Int = 2000, val tileSize: Int = 1000) {
    @Volatile
    var existingRegion: Region = Region(0, 0, 0, 0)

    private val contentIDGenerator = AtomicLong()
    private val coordinateIds: ConcurrentMap<Coordinate, Long> = ConcurrentHashMap()
    private val idsContent: ConcurrentMap<Long, PositionedContent> = ConcurrentHashMap()

    fun updateTiles(scroll: ClientEventScroll): Region {
        val x1 = scroll.x - (scroll.x % tileSize)
        val y1 = scroll.y - (scroll.y % tileSize)
        val x2 = x1 + scroll.w - (scroll.w % tileSize) + tileSize
        val y2 = y1 + scroll.h - (scroll.h % tileSize) + tileSize

        existingRegion = Region(x1 - maxDistance, y1 - maxDistance, x2 + maxDistance, y2 + maxDistance)

        return existingRegion
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
        existingRegion = Region(0, 0, 0, 0)
        coordinateIds.clear()
        idsContent.clear()
    }
}
