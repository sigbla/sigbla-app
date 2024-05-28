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
        val ref = view.tableViewRef.get()
        val marginTop = viewRef.config.marginTop
        val marginBottom = viewRef.config.marginBottom
        val marginLeft = viewRef.config.marginLeft
        val marginRight = viewRef.config.marginRight
        val paddingTop = viewRef.config.paddingTop
        val paddingBottom = viewRef.config.paddingBottom
        val paddingLeft = viewRef.config.paddingLeft
        val paddingRight = viewRef.config.paddingRight
        val topSeparatorHeight = viewRef.config.topSeparatorHeight
        val bottomSeparatorHeight = viewRef.config.bottomSeparatorHeight
        val leftSeparatorWidth = viewRef.config.leftSeparatorWidth
        val rightSeparatorWidth = viewRef.config.rightSeparatorWidth

        val table = view[Table]

        val dirtyColumnHeaders = dirtyCells?.map { it.column.header }?.toSet()
        val dirtyRowIndices = dirtyCells?.map { it.index }?.toSet()

        val applicableColumns = mutableListOf<Pair<Column, Pair<Long, Long>>>()
        var runningLeft = marginRight + marginLeft // This is margin right for row header + margin left for first cell column
        var runningRight = 0L
        var maxHeaderOffset = 0L
        var maxHeaderCells = 0

        val headerWidth = view[EMPTY_HEADER].derived.cellWidth + paddingLeft + paddingRight

        val leftRest = table.columns.partition { ref.columnPositions[it.header] == Position.PositionValue.LEFT }

        for (column in leftRest.first) {
            applicableColumns.add(Pair(column, Pair(runningLeft, 0)))
            runningLeft += view[column].derived.cellWidth + marginLeft + marginRight + paddingLeft + paddingRight

            val yOffset = column.header.labels.mapIndexed { i, _ ->
                view[(-(column.header.labels.size) + i).toLong()].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
            }.sum()
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
            if (column.header.labels.size > maxHeaderCells) maxHeaderCells = column.header.labels.size
        }

        if (leftRest.first.isNotEmpty())
            runningLeft += leftSeparatorWidth

        val rightRest = leftRest.second.partition { ref.columnPositions[it.header] == Position.PositionValue.RIGHT }

        for (column in rightRest.second) {
            if (x <= runningLeft && runningLeft <= x + w) applicableColumns.add(Pair(column, Pair(runningLeft, 0)))
            runningLeft += view[column].derived.cellWidth + marginLeft + marginRight + paddingLeft + paddingRight

            val yOffset = column.header.labels.mapIndexed { i, _ ->
                view[(-(column.header.labels.size) + i).toLong()].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
            }.sum()
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
            if (column.header.labels.size > maxHeaderCells) maxHeaderCells = column.header.labels.size
        }

        if (rightRest.first.isNotEmpty())
            runningLeft += rightSeparatorWidth

        for (column in rightRest.first.reversed()) {
            applicableColumns.add(Pair(column, Pair(runningLeft, runningRight)))
            runningLeft += view[column].derived.cellWidth + marginLeft + marginRight + paddingLeft + paddingRight
            runningRight += view[column].derived.cellWidth + marginLeft + marginRight + paddingLeft + paddingRight

            val yOffset = column.header.labels.mapIndexed { i, _ ->
                view[(-(column.header.labels.size) + i).toLong()].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
            }.sum()
            if (yOffset > maxHeaderOffset) maxHeaderOffset = yOffset
            if (column.header.labels.size > maxHeaderCells) maxHeaderCells = column.header.labels.size
        }

        val output = mutableListOf<PositionedContent>()

        // This is for the column headers
        for ((applicableColumn, applicableLeftRight) in applicableColumns) {
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

                val stickyLeft = ref.columnPositions[applicableColumn.header] == Position.PositionValue.LEFT
                val stickyRight = ref.columnPositions[applicableColumn.header] == Position.PositionValue.RIGHT

                output.add(PositionedContent(
                    contentHeader = applicableColumn.header,
                    contentRow = (-maxHeaderCells + idx).toLong(),
                    marker = false,
                    resize = true,
                    text = headerText,
                    h = derivedCellView.cellHeight + paddingTop + paddingBottom,
                    w = derivedCellView.cellWidth + paddingLeft + paddingRight,
                    ch = dims.maxY,
                    cw = dims.maxX,
                    cellClasses = listOfNotNull(
                        "ch",
                        if (stickyLeft) "chl" else null,
                        if (stickyRight) "chr" else null
                    ).joinToString(separator = " "),
                    contentClasses = ("cc " + derivedCellView.cellClasses.joinToString(separator = " ")).trim(),
                    topics = derivedCellView.cellTopics.toList(),
                    left = applicableLeftRight.first + headerWidth,
                    right = applicableLeftRight.second + derivedCellView.cellWidth + paddingLeft + paddingRight,
                    top = yOffset
                ))
            }
        }

        val applicableRows = mutableListOf<Pair<Long, Pair<Long, Long>>>()
        var runningHeight = maxHeaderOffset
        var topHeight = 0L
        var runningBottom = 0L

        val lastKey = table.tableRef.get().columnCells.values().map { it.last()?.component1() ?: -1 }.maxOrNull() ?: -1

        for (row in ref.rowPositions.filter { it.component2() == Position.PositionValue.TOP }.map { it.component1() }.sorted()) {
            if (!table.indexes.contains(row)) continue

            applicableRows.add(Pair(row, Pair(runningHeight, 0)))

            runningHeight += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
            topHeight += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
        }

        if (topHeight > 0)
            runningHeight += topSeparatorHeight

        for (row in 0..lastKey) {
            if (ref.rowPositions[row] == Position.PositionValue.TOP) continue
            if (ref.rowPositions[row] == Position.PositionValue.BOTTOM) continue

            if (y <= runningHeight && runningHeight <= y + h) applicableRows.add(Pair(row, Pair(runningHeight, 0)))

            runningHeight += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
        }

        for (row in ref.rowPositions.filter { it.component2() == Position.PositionValue.BOTTOM }.map { it.component1() }.sortedDescending()) {
            if (!table.indexes.contains(row)) continue

            if (runningBottom == 0L) {
                runningHeight += bottomSeparatorHeight
            }

            applicableRows.add(Pair(row, Pair(runningHeight, runningBottom)))

            runningHeight += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
            runningBottom += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
        }

        // This is for the row headers
        for ((applicableRow, applicableTopBottom) in applicableRows) {
            if (dirtyRowIndices != null && !dirtyRowIndices.contains(applicableRow)) continue

            val cellView = view[EMPTY_HEADER, applicableRow]
            val derivedCellView = cellView.derived

            val stickyTop = ref.rowPositions[applicableRow] == Position.PositionValue.TOP
            val stickyBottom = ref.rowPositions[applicableRow] == Position.PositionValue.BOTTOM

            output.add(PositionedContent(
                contentHeader = EMPTY_HEADER,
                contentRow = applicableRow,
                marker = false,
                resize = true,
                text = applicableRow.toString(),
                h = derivedCellView.cellHeight + paddingTop + paddingBottom,
                w = derivedCellView.cellWidth + paddingLeft + paddingRight,
                ch = dims.maxY,
                cw = dims.maxX,
                cellClasses = listOfNotNull(
                    "rh",
                    if (stickyTop) "rht" else null,
                    if (stickyBottom) "rhb" else null
                ).joinToString(separator = " "),
                contentClasses = ("cc " + derivedCellView.cellClasses.joinToString(separator = " ")).trim(),
                topics = derivedCellView.cellTopics.toList(),
                left = 0,
                top = applicableTopBottom.first,
                bottom = applicableTopBottom.second + derivedCellView.cellHeight + paddingTop + paddingBottom
            ))
        }

        // This is for the cells
        for ((applicableColumn, applicableLeftRight) in applicableColumns) {
            if (dirtyColumnHeaders != null && !dirtyColumnHeaders.contains(applicableColumn.header)) continue

            val stickyLeft = ref.columnPositions[applicableColumn.header] == Position.PositionValue.LEFT
            val stickyRight = ref.columnPositions[applicableColumn.header] == Position.PositionValue.RIGHT

            for ((applicableRow, applicableTopBottom) in applicableRows) {
                if (dirtyRowIndices != null && !dirtyRowIndices.contains(applicableRow)) continue

                val stickyTop = ref.rowPositions[applicableRow] == Position.PositionValue.TOP
                val stickyBottom = ref.rowPositions[applicableRow] == Position.PositionValue.BOTTOM

                val cell = applicableColumn[applicableRow]
                val cellView = view[cell]
                val derivedCellView = cellView.derived

                // TODO Consider option to skip empty cells here..
                //if (cell is UnitCell) continue
                val text = if (cell is UnitCell) null else if (cell !is WebCell) cell.toString() else null
                val html = if (cell is WebCell) cell.toString() else null

                output.add(PositionedContent(
                    contentHeader = applicableColumn.header,
                    contentRow = applicableRow,
                    marker = true,
                    resize = false,
                    text = text,
                    html = html,
                    h = derivedCellView.cellHeight + paddingTop + paddingBottom,
                    w = derivedCellView.cellWidth + paddingLeft + paddingRight,
                    ch = dims.maxY,
                    cw = dims.maxX,
                    cellClasses = listOfNotNull(
                        "c",
                        if (stickyLeft) "cl" else null,
                        if (stickyRight) "cr" else null,
                        if (stickyTop) "ct" else null,
                        if (stickyBottom) "cb" else null
                    ).joinToString(separator = " "),
                    contentClasses = ("cc " + derivedCellView.cellClasses.joinToString(separator = " ")).trim(),
                    topics = derivedCellView.cellTopics.toList(),
                    left = applicableLeftRight.first + headerWidth,
                    right = applicableLeftRight.second + derivedCellView.cellWidth + paddingLeft + paddingRight,
                    top = applicableTopBottom.first,
                    bottom = applicableTopBottom.second + derivedCellView.cellHeight + paddingTop + paddingBottom
                ))
            }
        }

        return output
    }

    private fun dims(view: TableView, viewRef: UIViewRef): Dimensions {
        val ref = view.tableViewRef.get()
        val marginTop = viewRef.config.marginTop
        val marginBottom = viewRef.config.marginBottom
        val marginLeft = viewRef.config.marginLeft
        val marginRight = viewRef.config.marginRight
        val paddingTop = viewRef.config.paddingTop
        val paddingBottom = viewRef.config.paddingBottom
        val paddingLeft = viewRef.config.paddingLeft
        val paddingRight = viewRef.config.paddingRight
        val topSeparatorHeight = viewRef.config.topSeparatorHeight
        val bottomSeparatorHeight = viewRef.config.bottomSeparatorHeight
        val leftSeparatorWidth = viewRef.config.leftSeparatorWidth
        val rightSeparatorWidth = viewRef.config.rightSeparatorWidth

        val table = view[Table]

        val headerHeight = view[-1].derived.cellHeight + paddingTop + paddingBottom
        val headerWidth = view[EMPTY_HEADER].derived.cellWidth + paddingLeft + paddingRight

        var maxHeaderOffset = headerHeight
        var runningLeft = headerWidth
        var fixedLeft = 0L
        var runningRight = 0L

        for (column in table.columns) {
            runningLeft += view[column].derived.cellWidth + marginLeft + marginRight + paddingLeft + paddingRight
            if (ref.columnPositions[column.header] == Position.PositionValue.LEFT) {
                fixedLeft += view[column].derived.cellWidth + marginLeft + marginRight + paddingLeft + paddingRight
            }
            if (ref.columnPositions[column.header] == Position.PositionValue.RIGHT) {
                runningRight += view[column].derived.cellWidth + marginLeft + marginRight + paddingLeft + paddingRight
            }

            val yOffset = column.header.labels.mapIndexed { i, _ ->
                view[(-(column.header.labels.size) + i).toLong()].derived.cellHeight + paddingTop + paddingBottom
            }.sum()
            val topMargins = (column.header.labels.size - 1) * marginTop
            val bottomMargins = (column.header.labels.size - 1) * marginBottom
            val combinedOffset = yOffset + topMargins + bottomMargins
            if (combinedOffset > maxHeaderOffset) maxHeaderOffset = combinedOffset
        }

        if (fixedLeft > 0) {
            runningLeft += leftSeparatorWidth
        }
        if (runningRight > 0) {
            runningLeft += rightSeparatorWidth
        }

        var fixedTop = 0L
        var runningHeight = maxHeaderOffset
        var runningBottom = 0L

        val lastKey = table.tableRef.get().columnCells.values().map { it.last()?.component1() ?: -1 }.maxOrNull() ?: -1
        for (row in 0..lastKey) {
            if (ref.rowPositions[row] == Position.PositionValue.TOP) {
                fixedTop += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
            }
            if (ref.rowPositions[row] == Position.PositionValue.BOTTOM) {
                runningBottom += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
            }
            runningHeight += view[row].derived.cellHeight + marginTop + marginBottom + paddingTop + paddingBottom
        }

        if (fixedTop > 0) {
            runningHeight += topSeparatorHeight
        }
        if (runningBottom > 0) {
            runningHeight += bottomSeparatorHeight
        }

        return Dimensions(
            headerWidth,
            maxHeaderOffset,
            marginLeft + marginRight,
            marginTop + marginBottom,
            runningLeft,
            runningHeight,
            if (fixedLeft == 0L) 0 else headerWidth + marginLeft + marginRight + fixedLeft + leftSeparatorWidth,
            if (runningRight == 0L) 0 else runningRight + rightSeparatorWidth,
            if (fixedTop == 0L) 0 else fixedTop + maxHeaderOffset + marginTop + marginBottom + topSeparatorHeight,
            if (runningBottom == 0L) 0 else runningBottom + bottomSeparatorHeight
        )
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
            val clientEventDims = ClientEventDims(
                dims.cornerX,
                dims.cornerY,
                dims.cornerRightMargin,
                dims.cornerBottomMargin,
                dims.maxX,
                dims.maxY,
                dims.topBannerLeft,
                dims.topBannerRight,
                dims.leftBannerTop,
                dims.leftBannerBottom
            )

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
            val clientEventDims = ClientEventDims(
                dims.cornerX,
                dims.cornerY,
                dims.cornerRightMargin,
                dims.cornerBottomMargin,
                dims.maxX,
                dims.maxY,
                dims.topBannerLeft,
                dims.topBannerRight,
                dims.leftBannerTop,
                dims.leftBannerBottom
            )

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
                    content.left,
                    content.top
                )
                val existingContent = if (existingId == null) null else client.contentState.withPCFor(existingId)

                if (existingContent == null) {
                    val cellId = client.contentState.addIdFor(
                        content.left,
                        content.top,
                        content
                    )

                    val addContent = ClientEventAddContent(
                        "c$cellId",
                        content.cellClasses,
                        content.contentClasses,
                        content.topics,
                        content.marker,
                        content.resize,
                        content.left,
                        content.right,
                        content.top,
                        content.bottom,
                        content.h,
                        content.w,
                        content.ch,
                        content.cw,
                        content.text,
                        content.html
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
                val existingId = client.contentState.withIdFor(content.left, content.top)
                val existingContent = if (existingId == null) null else client.contentState.withPCFor(existingId)

                if (existingContent != content) {
                    val cellId = client.contentState.addIdFor(
                        content.left,
                        content.top,
                        content
                    )
                    val addContent = ClientEventAddContent(
                        "c$cellId",
                        content.cellClasses,
                        content.contentClasses,
                        content.topics,
                        content.marker,
                        content.resize,
                        content.left,
                        content.right,
                        content.top,
                        content.bottom,
                        content.h,
                        content.w,
                        content.ch,
                        content.cw,
                        content.text,
                        content.html
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
    @Volatile var dims: Dimensions = Dimensions(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
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

            val clientEventDims = ClientEventDims(
                dims.cornerX,
                dims.cornerY,
                dims.cornerBottomMargin,
                dims.cornerRightMargin,
                dims.maxX,
                dims.maxY,
                dims.topBannerLeft,
                dims.topBannerRight,
                dims.leftBannerTop,
                dims.leftBannerBottom,
            )

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
    val cellClasses: String?,
    val contentClasses: String?,
    val topics: List<String>?,
    val marker: Boolean,
    val resize: Boolean,
    val left: Long,
    val right: Long?,
    val top: Long,
    val bottom: Long?,
    val h: Long,
    val w: Long,
    val ch: Long,
    val cw: Long,
    val text: String?,
    val html: String?
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
    val maxY: Long,
    val topBannerLeft: Long,
    val topBannerRight: Long,
    val leftBannerTop: Long,
    val leftBannerBottom: Long
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
    val marker: Boolean,
    val resize: Boolean,
    val text: String? = null,
    val html: String? = null,
    val h: Long,
    val w: Long,
    val ch: Long, // Container height
    val cw: Long, // Container width
    val cellClasses: String?,
    val contentClasses: String?,
    val topics: List<String>?,
    val left: Long,
    val right: Long? = null,
    val top: Long,
    val bottom: Long? = null
)

internal class Region(val cornerX: Long, val cornerY: Long, val maxX: Long, val maxY: Long)

internal class Dimensions(
    val cornerX: Long,
    val cornerY: Long,
    val cornerRightMargin: Long,
    val cornerBottomMargin: Long,
    val maxX: Long,
    val maxY: Long,
    val topBannerLeft: Long,
    val topBannerRight: Long,
    val leftBannerTop: Long,
    val leftBannerBottom: Long
)

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
