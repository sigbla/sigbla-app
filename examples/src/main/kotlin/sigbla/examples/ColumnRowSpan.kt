/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples

import sigbla.app.*
import java.net.URL

fun showColumnAndRowSpan(table: Table, viewConfig: ViewConfig): URL {
    val tableView = TableView[table]

    // Used to calculate the width of the cell we want to span across 2 columns
    fun calcColspanWidth(): Long {
        // Take into account the left and right margin and padding between the two columns
        val spacing = viewConfig.marginLeft + viewConfig.marginRight + viewConfig.paddingLeft + viewConfig.paddingRight
        // Take current width of column A + B + spacing as target cell width
        return tableView["A"].derived.cellWidth + tableView["B"].derived.cellWidth + spacing
    }

    // Used to calculate the height of the cell we want to span across 2 rows
    fun calcRowspanHeight(): Long {
        // Take into account the top and bottom margin and padding between the two rows
        val spacing = viewConfig.marginTop + viewConfig.marginBottom + viewConfig.paddingTop + viewConfig.paddingBottom
        // Take current height of row 0 + 1 + spacing as target cell height
        return tableView[0].derived.cellHeight + tableView[1].derived.cellHeight + spacing
    }

    // colSpan and rowSpan below react to any changed to the cell sizes so that we can update accordingly
    val colSpan: TableViewEventReceiver<ColumnView, Any>.() -> Unit = {
        var lastWidth = 0L

        events {
            val newWidth = calcColspanWidth()

            // Only update if different to avoid infinite listener looping
            if (newWidth != lastWidth) {
                tableView["A", 1][CellWidth] = newWidth
                lastWidth = newWidth
            }
        }
    }

    val rowSpan: TableViewEventReceiver<RowView, Any>.() -> Unit = {
        var lastHeight = 0L

        events {
            val newHeight = calcRowspanHeight()

            // Only update if different to avoid infinite listener looping
            if (newHeight != lastHeight) {
                tableView["D", 0][CellHeight] = newHeight
                lastHeight = newHeight
            }
        }
    }

    // We'll use a batch here as best practice to ensure we don't miss any updates
    // while initializing the current height and width on our two target cells
    batch(tableView) {
        // Initiate the height and width
        tableView["A", 1][CellWidth] = calcColspanWidth()
        tableView["D", 1][CellHeight] = calcRowspanHeight()

        // Add view listener on column A and B
        // We need allowLoop = true because we update cells within the receiver
        on(tableView["A"], allowLoop = true, receiver = colSpan)
        on(tableView["B"], allowLoop = true, receiver = colSpan)

        // Add view listener on row 0 and 1
        // We need allowLoop = true because we update cells within the receiver
        on(tableView[0], allowLoop = true, receiver = rowSpan)
        on(tableView[1], allowLoop = true, receiver = rowSpan)
    }

    return show(tableView, ref = "column-and-row-span", config = viewConfig)
}

fun main() {
    TableView[Port] = 8080

    val table = Table[null]

    table["A", 0] = "A0"
    table["B", 0] = "B0"
    table["C", 0] = "C0"
    table["C", 1] = "C1"

    // We want the cell at D0 to span from D0 to D1 (row span)
    table["D", 0] = "D0 to D1"

    // We want the cell at A1 to span from A1 to B1 (column span)
    table["A", 1] = "A1 to B1"

    // Pick on of the two default view configs
    val viewConfig = spaciousViewConfig(title = "Demo of column and row span")
    //val viewConfig = compactViewConfig(title = "Demo of column and row span")

    val url = showColumnAndRowSpan(table, viewConfig)
    println(url)
}