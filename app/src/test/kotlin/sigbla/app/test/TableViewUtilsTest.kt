package sigbla.app.test

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.junit.AfterClass
import org.junit.Test
import sigbla.app.*
import sigbla.app.exceptions.InvalidValueException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class TableViewUtilsTest {
    @Test
    fun `link cell height`() {
        val tableView = TableView[null]

        val viewConfig = ViewConfig(
            title = "title",

            marginTop = 1,
            marginBottom = 2,
            marginLeft = 3,
            marginRight = 4,

            paddingTop = 5,
            paddingBottom = 6,
            paddingLeft = 7,
            paddingRight = 8,

            topSeparatorHeight = 9,
            bottomSeparatorHeight = 10,
            leftSeparatorWidth = 11,
            rightSeparatorWidth = 12,

            defaultColumnVisibility = Visibility.Show,
            defaultRowVisibility = Visibility.Show,

            tableHtml = {
                call.respondText(ContentType.Text.Html, HttpStatusCode.OK) {
                    this.javaClass.getResource("/table/table.html").readText().replace("\${title}", "title")
                }
            },
            tableScript = staticResource("/table/table.js"),
            tableStyle = staticResource("/table/spacious.css")
        )

        val name = "link(${tableView["E", 0][CellHeight]} with ${arrayOf(tableView["A", 0][CellHeight], tableView[1][CellHeight], tableView[CellHeight], tableView["C", 0][CellHeight], tableView[2][CellHeight]).contentToString()})"

        // Cell to cell, row, table
        val ref = link(
            tableView["E", 0][CellHeight],
            tableView["A", 0][CellHeight], tableView[1][CellHeight], tableView[CellHeight], tableView["C", 0][CellHeight], tableView[2][CellHeight],
            config = viewConfig
        )

        assertEquals(name, ref.name)
        assertEquals(0L, ref.order)
        assertFalse(ref.allowLoop)

        assertEquals(
            tableView["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[2].derived.cellHeight,
            tableView["E", 0][CellHeight].asLong
        )

        val old1 = tableView["E", 0][CellHeight].asLong ?: 0
        tableView["A", 0][CellHeight] = 200

        assertEquals(
            tableView["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[2].derived.cellHeight,
            tableView["E", 0][CellHeight].asLong
        )

        assertTrue((tableView["E", 0][CellHeight].asLong ?: 0) > old1)

        val old2 = tableView["E", 0][CellHeight].asLong ?: 0
        tableView[1][CellHeight] = 300

        assertEquals(
            tableView["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[2].derived.cellHeight,
            tableView["E", 0][CellHeight].asLong
        )

        assertTrue((tableView["E", 0][CellHeight].asLong ?: 0) > old2)

        val old3 = tableView["E", 0][CellHeight].asLong ?: 0
        tableView[CellHeight] = 400

        assertEquals(
            tableView["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[2].derived.cellHeight,
            tableView["E", 0][CellHeight].asLong
        )

        assertTrue((tableView["E", 0][CellHeight].asLong ?: 0) > old3)

        val old4 = tableView["E", 0][CellHeight].asLong ?: 0

        off(ref)

        tableView["A", 0][CellHeight] = 500
        tableView[1][CellHeight] = 600
        tableView[CellHeight] = 700

        assertEquals(tableView["E", 0][CellHeight].asLong, old4)
    }

    @Test
    fun `link column height`() {
        val tableView = TableView[null]

        val viewConfig = ViewConfig(
            title = "title",

            marginTop = 1,
            marginBottom = 2,
            marginLeft = 3,
            marginRight = 4,

            paddingTop = 5,
            paddingBottom = 6,
            paddingLeft = 7,
            paddingRight = 8,

            topSeparatorHeight = 9,
            bottomSeparatorHeight = 10,
            leftSeparatorWidth = 11,
            rightSeparatorWidth = 12,

            defaultColumnVisibility = Visibility.Show,
            defaultRowVisibility = Visibility.Show,

            tableHtml = {
                call.respondText(ContentType.Text.Html, HttpStatusCode.OK) {
                    this.javaClass.getResource("/table/table.html").readText().replace("\${title}", "title")
                }
            },
            tableScript = staticResource("/table/table.js"),
            tableStyle = staticResource("/table/spacious.css")
        )

        val name = "link(${tableView[3][CellHeight]} with ${arrayOf(tableView["A", 0][CellHeight], tableView[1][CellHeight], tableView[CellHeight], tableView["C", 0][CellHeight], tableView[2][CellHeight]).contentToString()})"

        // Row to cell, row, table
        val ref = link(
            tableView[3][CellHeight],
            tableView["A", 0][CellHeight], tableView[1][CellHeight], tableView[CellHeight], tableView["C", 0][CellHeight], tableView[2][CellHeight],
            config = viewConfig
        )

        assertEquals(name, ref.name)
        assertEquals(0L, ref.order)
        assertFalse(ref.allowLoop)

        assertEquals(
            tableView["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[2].derived.cellHeight,
            tableView[3][CellHeight].asLong
        )

        val old1 = tableView[3][CellHeight].asLong ?: 0
        tableView["A", 0][CellHeight] = 200

        assertEquals(
            tableView["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[2].derived.cellHeight,
            tableView[3][CellHeight].asLong
        )

        assertTrue((tableView[3][CellHeight].asLong ?: 0) > old1)

        val old2 = tableView[3][CellHeight].asLong ?: 0
        tableView[1][CellHeight] = 300

        assertEquals(
            tableView["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[2].derived.cellHeight,
            tableView[3][CellHeight].asLong
        )

        assertTrue((tableView[3][CellHeight].asLong ?: 0) > old2)

        val old3 = tableView[3][CellHeight].asLong ?: 0
        tableView[CellHeight] = 400

        assertEquals(
            tableView["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView[2].derived.cellHeight,
            tableView[3][CellHeight].asLong
        )

        assertTrue((tableView[3][CellHeight].asLong ?: 0) > old3)

        val old4 = tableView[3][CellHeight].asLong ?: 0

        off(ref)

        tableView["A", 0][CellHeight] = 500
        tableView[1][CellHeight] = 600
        tableView[CellHeight] = 700

        assertEquals(tableView[3][CellHeight].asLong, old4)
    }

    @Test
    fun `link table height`() {
        // Need two table views to avoid interference
        val tableView1 = TableView[null]
        val tableView2 = TableView[null]

        val viewConfig = ViewConfig(
            title = "title",

            marginTop = 1,
            marginBottom = 2,
            marginLeft = 3,
            marginRight = 4,

            paddingTop = 5,
            paddingBottom = 6,
            paddingLeft = 7,
            paddingRight = 8,

            topSeparatorHeight = 9,
            bottomSeparatorHeight = 10,
            leftSeparatorWidth = 11,
            rightSeparatorWidth = 12,

            defaultColumnVisibility = Visibility.Show,
            defaultRowVisibility = Visibility.Show,

            tableHtml = {
                call.respondText(ContentType.Text.Html, HttpStatusCode.OK) {
                    this.javaClass.getResource("/table/table.html").readText().replace("\${title}", "title")
                }
            },
            tableScript = staticResource("/table/table.js"),
            tableStyle = staticResource("/table/spacious.css")
        )

        val name = "link(${tableView1[CellHeight]} with ${arrayOf(tableView2["A", 0][CellHeight], tableView2[1][CellHeight], tableView2[CellHeight], tableView2["C", 0][CellHeight], tableView2[2][CellHeight]).contentToString()})"

        // Table to cell, column, table
        val ref = link(
            tableView1[CellHeight],
            tableView2["A", 0][CellHeight], tableView2[1][CellHeight], tableView2[CellHeight], tableView2["C", 0][CellHeight], tableView2[2][CellHeight],
            config = viewConfig
        )

        assertEquals(name, ref.name)
        assertEquals(0L, ref.order)
        assertFalse(ref.allowLoop)

        assertEquals(
            tableView2["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView2[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2[2].derived.cellHeight,
            tableView1[CellHeight].asLong
        )

        val old1 = tableView1[CellHeight].asLong ?: 0
        tableView2["A", 0][CellHeight] = 200

        assertEquals(
            tableView2["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView2[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2[2].derived.cellHeight,
            tableView1[CellHeight].asLong
        )

        assertTrue((tableView1[CellHeight].asLong ?: 0) > old1)

        val old2 = tableView1[CellHeight].asLong ?: 0
        tableView2[1][CellHeight] = 300

        assertEquals(
            tableView2["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView2[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2[2].derived.cellHeight,
            tableView1[CellHeight].asLong
        )

        assertTrue((tableView1[CellHeight].asLong ?: 0) > old2)

        val old3 = tableView1[CellHeight].asLong ?: 0
        tableView2[CellHeight] = 400

        assertEquals(
            tableView2["A", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2[1].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom

                    + (tableView2[CellHeight].asLong ?: 0) // No margin or padding contribution from tableView[CellHeight]

                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2["C", 0].derived.cellHeight

                    + viewConfig.marginBottom + viewConfig.paddingBottom
                    + viewConfig.marginTop + viewConfig.paddingTop

                    + tableView2[2].derived.cellHeight,
            tableView1[CellHeight].asLong
        )

        assertTrue((tableView1[CellHeight].asLong ?: 0) > old3)

        val old4 = tableView1[CellHeight].asLong ?: 0

        off(ref)

        tableView2["A", 0][CellHeight] = 500
        tableView2[1][CellHeight] = 600
        tableView2[CellHeight] = 700

        assertEquals(tableView1[CellHeight].asLong, old4)
    }

    @Test
    fun `link cell width`() {
        val tableView = TableView[null]

        val viewConfig = ViewConfig(
            title = "title",

            marginTop = 1,
            marginBottom = 2,
            marginLeft = 3,
            marginRight = 4,

            paddingTop = 5,
            paddingBottom = 6,
            paddingLeft = 7,
            paddingRight = 8,

            topSeparatorHeight = 9,
            bottomSeparatorHeight = 10,
            leftSeparatorWidth = 11,
            rightSeparatorWidth = 12,

            defaultColumnVisibility = Visibility.Show,
            defaultRowVisibility = Visibility.Show,

            tableHtml = {
                call.respondText(ContentType.Text.Html, HttpStatusCode.OK) {
                    this.javaClass.getResource("/table/table.html").readText().replace("\${title}", "title")
                }
            },
            tableScript = staticResource("/table/table.js"),
            tableStyle = staticResource("/table/spacious.css")
        )

        val name = "link(${tableView["E", 0][CellWidth]} with ${arrayOf(tableView["A", 0][CellWidth], tableView["B"][CellWidth], tableView[CellWidth], tableView["C", 0][CellWidth], tableView["D"][CellWidth]).contentToString()})"

        // Cell to cell, column, table
        val ref = link(
            tableView["E", 0][CellWidth],
            tableView["A", 0][CellWidth], tableView["B"][CellWidth], tableView[CellWidth], tableView["C", 0][CellWidth], tableView["D"][CellWidth],
            config = viewConfig
        )

        assertEquals(name, ref.name)
        assertEquals(0L, ref.order)
        assertFalse(ref.allowLoop)

        assertEquals(
            tableView["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["D"].derived.cellWidth,
            tableView["E", 0][CellWidth].asLong
        )

        val old1 = tableView["E", 0][CellWidth].asLong ?: 0
        tableView["A", 0][CellWidth] = 200

        assertEquals(
            tableView["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["D"].derived.cellWidth,
            tableView["E", 0][CellWidth].asLong
        )

        assertTrue((tableView["E", 0][CellWidth].asLong ?: 0) > old1)

        val old2 = tableView["E", 0][CellWidth].asLong ?: 0
        tableView["B"][CellWidth] = 300

        assertEquals(
            tableView["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["D"].derived.cellWidth,
            tableView["E", 0][CellWidth].asLong
        )

        assertTrue((tableView["E", 0][CellWidth].asLong ?: 0) > old2)

        val old3 = tableView["E", 0][CellWidth].asLong ?: 0
        tableView[CellWidth] = 400

        assertEquals(
            tableView["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["D"].derived.cellWidth,
            tableView["E", 0][CellWidth].asLong
        )

        assertTrue((tableView["E", 0][CellWidth].asLong ?: 0) > old3)

        val old4 = tableView["E", 0][CellWidth].asLong ?: 0

        off(ref)

        tableView["A", 0][CellWidth] = 500
        tableView["B"][CellWidth] = 600
        tableView[CellWidth] = 700

        assertEquals(tableView["E", 0][CellWidth].asLong, old4)
    }

    @Test
    fun `link column width`() {
        val tableView = TableView[null]

        val viewConfig = ViewConfig(
            title = "title",

            marginTop = 1,
            marginBottom = 2,
            marginLeft = 3,
            marginRight = 4,

            paddingTop = 5,
            paddingBottom = 6,
            paddingLeft = 7,
            paddingRight = 8,

            topSeparatorHeight = 9,
            bottomSeparatorHeight = 10,
            leftSeparatorWidth = 11,
            rightSeparatorWidth = 12,

            defaultColumnVisibility = Visibility.Show,
            defaultRowVisibility = Visibility.Show,

            tableHtml = {
                call.respondText(ContentType.Text.Html, HttpStatusCode.OK) {
                    this.javaClass.getResource("/table/table.html").readText().replace("\${title}", "title")
                }
            },
            tableScript = staticResource("/table/table.js"),
            tableStyle = staticResource("/table/spacious.css")
        )

        val name = "link(${tableView["E"][CellWidth]} with ${arrayOf(tableView["A", 0][CellWidth], tableView["B"][CellWidth], tableView[CellWidth], tableView["C", 0][CellWidth], tableView["D"][CellWidth]).contentToString()})"

        // Column to cell, column, table
        val ref = link(
            tableView["E"][CellWidth],
            tableView["A", 0][CellWidth], tableView["B"][CellWidth], tableView[CellWidth], tableView["C", 0][CellWidth], tableView["D"][CellWidth],
            config = viewConfig
        )

        assertEquals(name, ref.name)
        assertEquals(0L, ref.order)
        assertFalse(ref.allowLoop)

        assertEquals(
            tableView["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["D"].derived.cellWidth,
            tableView["E"][CellWidth].asLong
        )

        val old1 = tableView["E"][CellWidth].asLong ?: 0
        tableView["A", 0][CellWidth] = 200

        assertEquals(
            tableView["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["D"].derived.cellWidth,
            tableView["E"][CellWidth].asLong
        )

        assertTrue((tableView["E"][CellWidth].asLong ?: 0) > old1)

        val old2 = tableView["E"][CellWidth].asLong ?: 0
        tableView["B"][CellWidth] = 300

        assertEquals(
            tableView["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["D"].derived.cellWidth,
            tableView["E"][CellWidth].asLong
        )

        assertTrue((tableView["E"][CellWidth].asLong ?: 0) > old2)

        val old3 = tableView["E"][CellWidth].asLong ?: 0
        tableView[CellWidth] = 400

        assertEquals(
            tableView["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView["D"].derived.cellWidth,
            tableView["E"][CellWidth].asLong
        )

        assertTrue((tableView["E"][CellWidth].asLong ?: 0) > old3)

        val old4 = tableView["E"][CellWidth].asLong ?: 0

        off(ref)

        tableView["A", 0][CellWidth] = 500
        tableView["B"][CellWidth] = 600
        tableView[CellWidth] = 700

        assertEquals(tableView["E"][CellWidth].asLong, old4)
    }

    @Test
    fun `link table width`() {
        // Need two table views to avoid interference
        val tableView1 = TableView[null]
        val tableView2 = TableView[null]

        val viewConfig = ViewConfig(
            title = "title",

            marginTop = 1,
            marginBottom = 2,
            marginLeft = 3,
            marginRight = 4,

            paddingTop = 5,
            paddingBottom = 6,
            paddingLeft = 7,
            paddingRight = 8,

            topSeparatorHeight = 9,
            bottomSeparatorHeight = 10,
            leftSeparatorWidth = 11,
            rightSeparatorWidth = 12,

            defaultColumnVisibility = Visibility.Show,
            defaultRowVisibility = Visibility.Show,

            tableHtml = {
                call.respondText(ContentType.Text.Html, HttpStatusCode.OK) {
                    this.javaClass.getResource("/table/table.html").readText().replace("\${title}", "title")
                }
            },
            tableScript = staticResource("/table/table.js"),
            tableStyle = staticResource("/table/spacious.css")
        )

        val name = "link(${tableView1[CellWidth]} with ${arrayOf(tableView2["A", 0][CellWidth], tableView2["B"][CellWidth], tableView2[CellWidth], tableView2["C", 0][CellWidth], tableView2["D"][CellWidth]).contentToString()})"

        // Table to cell, column, table
        val ref = link(
            tableView1[CellWidth],
            tableView2["A", 0][CellWidth], tableView2["B"][CellWidth], tableView2[CellWidth], tableView2["C", 0][CellWidth], tableView2["D"][CellWidth],
            config = viewConfig
        )

        assertEquals(name, ref.name)
        assertEquals(0L, ref.order)
        assertFalse(ref.allowLoop)

        assertEquals(
            tableView2["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView2[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["D"].derived.cellWidth,
            tableView1[CellWidth].asLong
        )

        val old1 = tableView1[CellWidth].asLong ?: 0
        tableView2["A", 0][CellWidth] = 200

        assertEquals(
            tableView2["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView2[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["D"].derived.cellWidth,
            tableView1[CellWidth].asLong
        )

        assertTrue((tableView1[CellWidth].asLong ?: 0) > old1)

        val old2 = tableView1[CellWidth].asLong ?: 0
        tableView2["B"][CellWidth] = 300

        assertEquals(
            tableView2["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView2[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["D"].derived.cellWidth,
            tableView1[CellWidth].asLong
        )

        assertTrue((tableView1[CellWidth].asLong ?: 0) > old2)

        val old3 = tableView1[CellWidth].asLong ?: 0
        tableView2[CellWidth] = 400

        assertEquals(
            tableView2["A", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["B"].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight

                    + (tableView2[CellWidth].asLong ?: 0) // No margin or padding contribution from tableView[CellWidth]

                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["C", 0].derived.cellWidth

                    + viewConfig.marginRight + viewConfig.paddingRight
                    + viewConfig.marginLeft + viewConfig.paddingLeft

                    + tableView2["D"].derived.cellWidth,
            tableView1[CellWidth].asLong
        )

        assertTrue((tableView1[CellWidth].asLong ?: 0) > old3)

        val old4 = tableView1[CellWidth].asLong ?: 0

        off(ref)

        tableView2["A", 0][CellWidth] = 500
        tableView2["B"][CellWidth] = 600
        tableView2[CellWidth] = 700

        assertEquals(tableView1[CellWidth].asLong, old4)
    }

    @Test
    fun `self dependency check`() {
        val tableView = TableView[null]

        assertFailsWith<InvalidValueException> { link(tableView[CellHeight], tableView[CellHeight], config = compactViewConfig()) }
        assertFailsWith<InvalidValueException> { link(tableView[1][CellHeight], tableView[1][CellHeight], config = compactViewConfig()) }
        assertFailsWith<InvalidValueException> { link(tableView["A", 1][CellHeight], tableView["A", 1][CellHeight], config = compactViewConfig()) }

        assertFailsWith<InvalidValueException> { link(tableView[CellWidth], tableView[CellWidth], config = compactViewConfig()) }
        assertFailsWith<InvalidValueException> { link(tableView["A"][CellWidth], tableView["A"][CellWidth], config = compactViewConfig()) }
        assertFailsWith<InvalidValueException> { link(tableView["A", 1][CellWidth], tableView["A", 1][CellWidth], config = compactViewConfig()) }
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}