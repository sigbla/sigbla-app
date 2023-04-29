package sigbla.examples

import kotlinx.html.style
import sigbla.app.*
import java.util.concurrent.ThreadLocalRandom

fun main() {
    //val table = Table["Table A"]
    val table = Table["test"]

    for (col in listOf("A", "B", "C", "D", "E", "F")) {
        val withExtra = ThreadLocalRandom.current().nextBoolean()
        for (row in 0..100) {
            if (withExtra)
                table[col, "E1", row] = "$col $row"
            else
                table[col, row] = "$col $row"
        }
    }

    table["G", 0] = div("myCustomClass1 myCustomClass2") {
        style = "color: red"
        +"This is red text"
    }

    val tableView = TableView[table]

    tableView.cellHeight = 100
    tableView.cellWidth
    tableView["A"].cellWidth
    tableView[1].cellHeight
    tableView["A", 1].cellHeight
    tableView["A", 1].cellWidth
    table["A"].table.table.table
    tableView["A"].tableView.tableView
    table["A", 1] = 1
    table["A", 1] = {
        destination `=` 10
        destination.table[destination] = 10
    }
    on(table["A", 1]) {
        events {
        }
    }

    for (dcv in tableView) {

    }
    for (dcv in tableView["A"]) {

    }
    for (dcv in tableView[1]) {

    }

    /*
    tableView["A"] = {

    }
    tableView["A", "B"] = {

    }
    tableView[ColumnHeader("A", "B")] = {

    }
    tableView[1] = {

    }
    tableView[1L] = {

    }
    tableView["A"][1] = {

    }
    tableView["A"][1L] = {

    }
    tableView["A", "B"][1] = {

    }
    tableView["A", "B"][1L] = {

    }
    tableView[ColumnHeader("A", "B")][1] = {

    }
    tableView[ColumnHeader("A", "B")][1L] = {

    }
    tableView[1]["A"] = {

    }
    tableView[1L]["A"] = {

    }
    tableView[1]["A", "B"] = {

    }
    tableView[1L]["A", "B"] = {

    }
    tableView[1][ColumnHeader("A", "B")] = {

    }
    tableView[1L][ColumnHeader("A", "B")] = {

    }
    tableView["A", 1] = {

    }
    tableView["A", 1L] = {

    }
    tableView["A", "B", 1] = {

    }
    tableView["A", "B", 1L] = {

    }
    tableView[ColumnHeader("A", "B"), 1] = {

    }
    tableView[ColumnHeader("A", "B"), 1L] = {

    }
     */

    tableView["A"].let {

    }
    tableView["A", "B"].let {

    }
    tableView[ColumnHeader("A", "B")].let {

    }
    tableView[1].let {

    }
    tableView[1L].let {

    }
    tableView["A"][1].let {

    }
    tableView["A"][1L].let {

    }
    tableView["A", "B"][1].let {

    }
    tableView["A", "B"][1L].let {

    }
    tableView[ColumnHeader("A", "B")][1].let {

    }
    tableView[ColumnHeader("A", "B")][1L].let {

    }
    tableView[1]["A"].let {

    }
    tableView[1L]["A"].let {

    }
    tableView[1]["A", "B"].let {

    }
    tableView[1L]["A", "B"].let {

    }
    tableView[1][ColumnHeader("A", "B")].let {

    }
    tableView[1L][ColumnHeader("A", "B")].let {

    }
    tableView["A", 1].let {

    }
    tableView["A", 1L].let {

    }
    tableView["A", "B", 1].let {

    }
    tableView["A", "B", 1L].let {

    }
    tableView[ColumnHeader("A", "B"), 1].let {

    }
    tableView[ColumnHeader("A", "B"), 1L].let {

    }

    /*
    tableView[ColumnHeader("A"), 1] = {
        cellHeight = null
        cellWidth = 100
        /*
        // TODO
        transformer = {
            destination.cell?.let {
                // TODO it `=` ..
                it.table[it] = div {
                    style = "color: red"
                    +it.toString()
                }
            }
        }
         */
    }
     */
    tableView[ColumnHeader("A"), 1].cellHeight = null
    tableView[ColumnHeader("A"), 1].cellWidth = 100

    // TODO?
    /*
    tableView["A"] = tableView["A", 1]
    tableView[1] = tableView["A", 1]
    tableView["A", 1] = tableView["A"]
     */

    val cv = tableView["A", 1]

    cv.cellWidth = 100
    cv.cellHeight

    /*
    tableView[ColumnHeader("A")] = {
        cellWidth = 100
    }
     */

    show(tableView)

    on(tableView) {
        events {
            forEach {
                println("onAny: " + it)
            }
        }
    }

    on<CellView>(tableView) {
        events {
            forEach {
                println("on: " + it)
            }
        }
    }

    on(tableView) {
        events {
            forEach {
                println("on: " + it)
            }
        }
    }

    on<TableView>(tableView) {
        events {
            forEach {
                println("on: " + it)
            }
        }
    }

    println("END")
}
