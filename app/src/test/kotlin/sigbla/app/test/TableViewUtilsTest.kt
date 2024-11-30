package sigbla.app.test

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.junit.AfterClass
import org.junit.Test
import sigbla.app.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TableViewUtilsTest {
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

        // Cell to cell, column, table
        link(
            tableView["E", 0][CellWidth],
            tableView["A", 0][CellWidth], tableView["B"][CellWidth], tableView[CellWidth], tableView["C", 0][CellWidth], tableView["D"][CellWidth],
            config = viewConfig
        )

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

        // Column to cell, column, table
        link(
            tableView["E"][CellWidth],
            tableView["A", 0][CellWidth], tableView["B"][CellWidth], tableView[CellWidth], tableView["C", 0][CellWidth], tableView["D"][CellWidth],
            config = viewConfig
        )

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

        // Table to cell, column, table
        link(
            tableView1[CellWidth],
            tableView2["A", 0][CellWidth], tableView2["B"][CellWidth], tableView2[CellWidth], tableView2["C", 0][CellWidth], tableView2["D"][CellWidth],
            config = viewConfig
        )

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
    }

    // TODO Test self dep check
    // TODO Unsubscribe

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}