# Charts

Like with functions, Sigbla comes with charting capabilities. As is with a limited set of two chart types, but this will
expand in the near future.

Unlike functions, as described in the previous chapter, charts live within their own package `sigbla.charts`.
To use charts make sure to do `import sigbla.charts.*` in addition to the usual `import sigbla.app.*`.

## Line chart

The next example shows how to create a line chart with static data.

``` kotlin
import sigbla.app.*
import sigbla.charts.*

fun main() {
    TableView[Port] = 8080

    val table = Table["chart"]
    val tableView = TableView[table]

    val title = "Chart title"
    val xLabels = listOf("X1", "X2", "X3")
    val series1 = "Series 1" to listOf(1.2, 2.3, 1.9)
    val series2 = "Series 2" to listOf(1.5, 2.1, 2.2)
    val series3 = "Series 3" to listOf(2.3, 2.7, 0.8)

    // Define the line chart, title is optional
    tableView["Chart", 0] = line(
        title,
        xLabels,
        series1, series2, series3
    )

    tableView[0][CellHeight] = 250
    tableView["Chart"][CellWidth] = 550

    val url = show(tableView)
    println(url)
}
```

Loading this, we'll find the line chart as expected.

![Line chart example](img/charts_line_chart.png)

Instead of using static data, we can also link the chart to cell data. That would cause the chart to automatically
update when the underlying data is updated.

``` kotlin
import sigbla.app.*
import sigbla.charts.*

fun main() {
    TableView[Port] = 8080

    val dataTable = Table["data"]

    dataTable["Title", 0] = "Chart title"

    dataTable["X Labels", 0] = "X1"
    dataTable["X Labels", 1] = "X2"
    dataTable["X Labels", 2] = "X3"

    dataTable["Series 1", 0] = 1.2
    dataTable["Series 1", 1] = 2.3
    dataTable["Series 1", 2] = 1.9

    dataTable["Series 2", 0] = 1.5
    dataTable["Series 2", 1] = 2.1
    dataTable["Series 2", 2] = 2.2

    dataTable["Series 3", 0] = 2.3
    dataTable["Series 3", 1] = 2.7
    dataTable["Series 3", 2] = 0.8

    val chartTable = Table["chart"]
    val tableView = TableView[chartTable]

    // Define the line chart using data from cells
    tableView["Chart", 0] = line(
        dataTable["Title", 0],
        dataTable["X Labels", 0]..dataTable["X Labels", 2],
        "Series 1" to dataTable["Series 1", 0]..dataTable["Series 1", 2],
        "Series 2" to dataTable["Series 2", 0]..dataTable["Series 2", 2],
        "Series 3" to dataTable["Series 3", 0]..dataTable["Series 3", 2],
    )

    tableView[0][CellHeight] = 250
    tableView["Chart"][CellWidth] = 550

    val url = show(tableView)
    println(url)
}
```

## Bar chart

The parameters for a bar chart follow that of the line chart. To get a bar chart, simply replace `line` with `bar`:

``` kotlin
tableView["Chart", 0] = bar(
    title,
    xLabels,
    series1, series2, series3
)
```

``` kotlin
tableView["Chart", 0] = bar(
    dataTable["Title", 0],
    dataTable["X Labels", 0]..dataTable["X Labels", 2],
    "Series 1" to dataTable["Series 1", 0]..dataTable["Series 1", 2],
    "Series 2" to dataTable["Series 2", 0]..dataTable["Series 2", 2],
    "Series 3" to dataTable["Series 3", 0]..dataTable["Series 3", 2],
)
```

This will produce a bar chart like so:

![Bar chart example](img/charts_bar_chart.png)
