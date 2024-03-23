package sigbla.examples

import sigbla.app.*
import java.lang.Math
import kotlinx.html.style

/**
 * Not your typical usage of Sigbla but a fun little example implementing Conway's Game of Life
 * (https://en.wikipedia.org/wiki/Conway's_Game_of_Life). It makes use of views, batching, transformers,
 * and the efficient ability to clone tables to obtain prior state.
 */

fun main() {
    // Board size and other parameters
    val size = 100L
    val loopSleep = 100L
    val randomStartCells = 500

    val conway = Table["conway"]

    // Fill table with false (blank) according to size
    for (i in 0 until size) {
        for (j in 0 until size) {
            conway[i.toString()][j] = false
        }
    }

    // Create view
    TableView[Port] = 8080
    val view = TableView[conway]

    view[CellWidth] = 10
    view[CellHeight] = 10

    view[TableTransformer] = {
        forEach {
            this[it] = if (true in it) div { style = "background-color: black; width: 100%; height: 100%" }
            else div { style = "background-color: white; width: 100%; height: 100%" }
        }
    }

    // Open this in your browser while running
    val url = show(view)
    println(url)

    val boards = LinkedHashSet<Int>()
    var lastBoardReset = 0L

    // Game loop
    for (loop in 0L..Long.MAX_VALUE) {
        batch(conway) {
            val prior = clone(conway)

            for (x in 0 until size) {
                for (y in 0 until size) {
                    // Find neighbors of current cell on x/y
                    val xLeft = Math.floorMod(x - 1, size)
                    val xRight = (x + 1) % size

                    val yUp = Math.floorMod(y - 1, size)
                    val yDown = (y + 1) % size

                    val neighbors = mutableListOf<Cell<*>>()
                    for (x1 in listOf(xLeft, x, xRight)) {
                        for (y1 in listOf(yUp, y, yDown)) {
                            if (x1 == x && y1 == y) continue
                            else neighbors.add(prior[x1.toString(), y1])
                        }
                    }

                    val liveCount = neighbors.count { true in it }

                    val cell = conway[x.toString(), y]

                    // Rules:
                    // Any live cell with fewer than two live neighbors dies, as if by underpopulation
                    if (liveCount < 2) conway[cell] = false

                    // Any live cell with two or three live neighbors lives on to the next generation
                    // (It's a rule we don't need to check for due to no state update)
                    //else if ((liveCount == 2 || liveCount == 3) && true in conway[cell]) conway[cell] = true

                    // Any dead cell with exactly three live neighbors become a live cell, as if by reproduction
                    else if (liveCount == 3 && false in conway[cell]) conway[cell] = true

                    // Any live cell with more than three live neighbors dies, as if by overpopulation
                    else if (liveCount > 3) conway[cell] = false
                }
            }

            // Add some logic to detect when we're stuck in a loop
            boards.add(conway.map { true in it }.toList().hashCode())

            if (loop - lastBoardReset > 10) {
                boards.remove(boards.first())

                if (boards.size < 5) {
                    // Stuck in loop, add some randomness
                    conway.shuffled().take(randomStartCells).forEach {
                        it.table[it] = true
                    }

                    boards.clear()
                    lastBoardReset = loop
                }
            }
        }

        Thread.sleep(loopSleep)
    }
}