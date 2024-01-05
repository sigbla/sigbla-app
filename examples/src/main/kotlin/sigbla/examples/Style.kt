/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
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

    tableView[CellHeight] = 100
    tableView[CellHeight] = tableView[CellHeight] + 100
    val cellHeight = tableView[CellHeight]
    tableView[CellWidth]
    tableView[CellClasses] = "  "
    tableView[CellClasses] = setOf("a", "b")
    tableView[CellClasses] = listOf("a", "b")
    tableView[CellClasses] = tableView[CellClasses] + "c"
    tableView[CellClasses] = tableView[CellClasses] + setOf("d", "e")
    tableView[CellClasses] = tableView[CellClasses] - "e"
    tableView[CellClasses] = tableView[CellClasses] - setOf("a", "d")
    tableView[CellClasses] = null
    tableView["A"][CellWidth]
    tableView[1][CellHeight]
    tableView["A", 1][CellHeight]
    tableView["A", 1][CellWidth]
    tableView["A"].tableView
    table["A", 1] = 1
    table["A", 1] = {
        this { 10 }
        //this.table[this] = 10
        //10
    }
    table["A", 1] {
        10
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
    tableView[Header["A", "B"]] = {

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
    tableView[Header["A", "B"]][1] = {

    }
    tableView[Header["A", "B"]][1L] = {

    }
    tableView[1]["A"] = {

    }
    tableView[1L]["A"] = {

    }
    tableView[1]["A", "B"] = {

    }
    tableView[1L]["A", "B"] = {

    }
    tableView[1][Header["A", "B"]] = {

    }
    tableView[1L][Header["A", "B"]] = {

    }
    tableView["A", 1] = {

    }
    tableView["A", 1L] = {

    }
    tableView["A", "B", 1] = {

    }
    tableView["A", "B", 1L] = {

    }
    tableView[Header["A", "B"], 1] = {

    }
    tableView[Header["A", "B"], 1L] = {

    }
     */

    tableView["A"].let {

    }
    tableView["A", "B"].let {

    }
    tableView[Header["A", "B"]].let {

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
    tableView[Header["A", "B"]][1].let {

    }
    tableView[Header["A", "B"]][1L].let {

    }
    tableView[1]["A"].let {

    }
    tableView[1L]["A"].let {

    }
    tableView[1]["A", "B"].let {

    }
    tableView[1L]["A", "B"].let {

    }
    tableView[1][Header["A", "B"]].let {

    }
    tableView[1L][Header["A", "B"]].let {

    }
    tableView["A", 1].let {

    }
    tableView["A", 1L].let {

    }
    tableView["A", "B", 1].let {

    }
    tableView["A", "B", 1L].let {

    }
    tableView[Header["A", "B"], 1].let {

    }
    tableView[Header["A", "B"], 1L].let {

    }

    /*
    tableView[Header["A"], 1] = {
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
    tableView[Header["A"], 1][CellHeight] = null
    tableView[Header["A"], 1][CellWidth] = 100

    // TODO?
    /*
    tableView["A"] = tableView["A", 1]
    tableView[1] = tableView["A", 1]
    tableView["A", 1] = tableView["A"]
     */

    val cv = tableView["A", 1]

    cv[CellWidth] = 100
    cv[CellHeight]

    tableView[CellHeight] = 100
    tableView[CellHeight] = null
    tableView[CellHeight] = 100L
    val height = tableView[CellHeight] + 100
    tableView[CellHeight] = height

    /*
    tableView[Header["A"]] = {
        cellWidth = 100
    }
     */

    val url = show(tableView)
    println(url)

    on(tableView, allowLoop = true) events {
        forEach {
            println("onAny: " + it)
        }
    }

    on<CellView>(tableView) events {
        forEach {
            println("on: " + it)
        }
    }

    on(tableView, allowLoop = true) {
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
