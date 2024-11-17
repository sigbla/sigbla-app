/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.examples.charts

import sigbla.app.*
import sigbla.charts.*
import sigbla.charts.Position

fun main() {
    TableView[Port] = 8080

    val table = Table["AllChartExamples"]
    val tableView = TableView[table]

    val url = show(tableView, ref = "all-chart-examples", config = spaciousViewConfig(title = "All chart examples"))
    println(url)

    tableView[EMPTY_HEADER][CellWidth] = 20
    tableView[-1][CellHeight] = 20

    tableView[CellHeight] = 400
    tableView[CellWidth] = 800

    val allColumns = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    var columns = allColumns.toMutableList()
    var row = 0

    // Bar chart from scratch
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Bar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 1"),
                    data = Numbers(-34.39986283, -65.76646091, 16.24142661, 36.31515775, 33.96090535, -47.61145405, 60.73388203),
                    borderColor = Color.RGBA(0, 0, 255, 0.9),
                    backgroundColor = Color.RGBA(0, 0, 255, 1.0),
                    borderWidth = Numeric(2)
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Bar chart title")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Bar chart, two datasets
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Bar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 1"),
                    data = Numbers(-34.39986283, -65.76646091, 16.24142661, 36.31515775, 33.96090535, -47.61145405, 60.73388203),
                    borderWidth = Numeric(2)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 2"),
                    data = Numbers(-90.38580247, 20.01714678, -94.47702332, 27.8600823, 85.79389575, 37.34910837, 55.36522634),
                    borderWidth = Numeric(2)
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Bar chart with two dataset")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Bar chart with floating bars
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Bar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 1"),
                    data = NumberPairs(-5.5384087791495205 to -70.47582304526749, -53.3659122085048 to 85.914780521262, -64.36213991769547 to 10.000857338820296, 60.23834019204389 to -80.93364197530865, 78.460219478738 to 0.765603566529478, -36.85699588477366 to 35.34550754458161, -9.17009602194787 to -48.79886831275721)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 2"),
                    data = NumberPairs(-36.0099451303155 to -86.23542524005487, -33.425925925925924 to -52.27280521262003, 52.90294924554183 to 92.59516460905351, 69.89026063100135 to 91.57836076817557, 12.5977366255144 to 13.812585733882017, -86.87585733882031 to 9.915123456790127, 62.82750342935529 to 0.8736282578875318)
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Bar chart with floating bars")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Bar chart, horizontal
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Bar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 1"),
                    data = Numbers(-34.39986283, -65.76646091, 16.24142661, 36.31515775, 33.96090535, -47.61145405, 60.73388203),
                    borderWidth = Numeric(2)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 2"),
                    data = Numbers(-90.38580247, 20.01714678, -94.47702332, 27.8600823, 85.79389575, 37.34910837, 55.36522634),
                    borderWidth = Numeric(2)
                )
            )
        )
        options = ChartModel.Options(
            indexAxis = Text("y"),
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Horizontal bar chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Bar chart, stacked
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Bar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 1"),
                    data = Numbers(-34.39986283, -65.76646091, 16.24142661, 36.31515775, 33.96090535, -47.61145405, 60.73388203),
                    borderWidth = Numeric(2)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 2"),
                    data = Numbers(-90.38580247, 20.01714678, -94.47702332, 27.8600823, 85.79389575, 37.34910837, 55.36522634),
                    borderWidth = Numeric(2)
                )
            )
        )
        options = ChartModel.Options(
            scales = ChartModel.Options.Scales(
                "x" to ChartModel.Options.Scales.Scale(
                    stacked = Bool.True
                ),
                "y" to ChartModel.Options.Scales.Scale(
                    stacked = Bool.True
                )
            ),
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Stacked bar chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Bar chart, stacked groups
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Bar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 1"),
                    data = Numbers(-76.71124829, -28.24245542, 34.83539095, 77.95438957, -74.19410151, 17.89609053, 97.43484225),
                    backgroundColor = Color.RGBA(255, 99, 132, 1.0),
                    stack = Text("Stack 0")
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 2"),
                    data = Numbers(33.5579561, -23.11728395, 48.39677641, -38.31961591, -21.53806584, -2.49314129, -23.16015089),
                    backgroundColor = Color.RGBA(54, 162, 235, 1.0),
                    stack = Text("Stack 0")
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 3"),
                    data = Numbers(89.30041152, 11.43175583, -54.29698217, -23.93518519, -43.16186557, -65.06344307, -94.5781893),
                    backgroundColor = Color.RGBA(75, 192, 192, 1.0),
                    stack = Text("Stack 1")
                )
            )
        )
        options = ChartModel.Options(
            scales = ChartModel.Options.Scales(
                "x" to ChartModel.Options.Scales.Scale(
                    stacked = Bool.True
                ),
                "y" to ChartModel.Options.Scales.Scale(
                    stacked = Bool.True
                )
            ),
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Stacked by group bar chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    columns = allColumns.toMutableList()
    row++

    // Line chart from scratch
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Line 1"),
                    data = Numbers(-34.39986283, -65.76646091, 16.24142661, 36.31515775, 33.96090535, -47.61145405, 60.73388203),
                    borderColor = Color.RGBA(255, 0, 0, 0.5),
                    backgroundColor = Color.RGBA(255, 0, 0, 0.9),
                    borderWidth = Numeric(2)
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Line chart title")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Line chart with point styling
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Line 1"),
                    data = Numbers(-34.39986283, -65.76646091, 16.24142661, 36.31515775, 33.96090535, -47.61145405, 60.73388203),
                    borderColor = Color.RGBA(255, 99, 132, 0.5),
                    backgroundColor = Color.RGBA(255, 99, 132, 0.9),
                    pointStyle = PointStyle.Circle,
                    pointRadius = Numeric(10),
                    pointHoverRadius = Numeric(15)
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Line chart with point styling")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Line chart with interpolation
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Cubic interpolation (monotone)"),
                    data = Numbers(0, 20, 20, 60, 60, 120, null, 180, 120, 125, 105, 110, 170),
                    borderColor = Color.RGBA(255, 99, 132, 1.0),
                    fill = Bool.False,
                    cubicInterpolationMode = CubicInterpolationMode.Monotone,
                    tension = Numeric(0.4)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Cubic interpolation"),
                    data = Numbers(0, 20, 20, 60, 60, 120, null, 180, 120, 125, 105, 110, 170),
                    borderColor = Color.RGBA(54, 162, 235, 1.0),
                    fill = Bool.False,
                    cubicInterpolationMode = null,
                    tension = Numeric(0.4)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Cubic interpolation (default)"),
                    data = Numbers(0, 20, 20, 60, 60, 120, null, 180, 120, 125, 105, 110, 170),
                    borderColor = Color.RGBA(75, 192, 192, 1.0),
                    fill = Bool.False,
                    cubicInterpolationMode = null,
                    tension = null
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Line chart with cubic interpolation mode")
                )
            ),
            scales = ChartModel.Options.Scales(
                "x" to ChartModel.Options.Scales.Scale(
                    display = Bool.True,
                    title = ChartModel.Options.Scales.Scale.Title(
                        display = Bool.True
                    )
                ),
                "y" to ChartModel.Options.Scales.Scale(
                    display = Bool.True,
                    title = ChartModel.Options.Scales.Scale.Title(
                        display = Bool.True,
                        text = Text("Value")
                    ),
                    suggestedMin = Numeric(-10),
                    suggestedMax = Numeric(200)
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Line chart, multi axis
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(86.01165981, -98.44135802, -21.37174211, 30.80075446, -80.19547325, -55.59499314, 62.62688615),
                    yAxisID = Text("y1")
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 2"),
                    data = Numbers(47.30967078, -25.00342936, 48.15672154, -49.25925926, -62.93038409, 54.0569273, 96.76440329),
                    yAxisID = Text("y2")
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Line chart with multiple axes")
                )
            ),
            scales = ChartModel.Options.Scales(
                "y1" to ChartModel.Options.Scales.Scale(
                    type = ScaleType.Linear,
                    display = Bool.True,
                    position = Position.Left
                ),
                "y2" to ChartModel.Options.Scales.Scale(
                    type = ScaleType.Linear,
                    display = Bool.True,
                    position = Position.Right,
                    grid = ChartModel.Options.Scales.Scale.Grid(
                        drawOnChartArea = Bool.False
                    )
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Line chart with stepped option enabled
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("Day 1", "Day 2", "Day 3", "Day 4", "Day 5", "Day 6"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset"),
                    data = Numbers(66.47805213, 28.52709191, -46.66666667, 95.21776406, -98.90603567, -33.97633745),
                    fill = Bool.False,
                    stepped = Bool.True
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            interaction = ChartModel.Options.Interaction(
                intersect = Bool.False,
                axis = InteractionAxis.X
            ),
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Stepped line chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Line chart with styling and scaling
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Unfilled"),
                    fill = Bool.False,
                    backgroundColor = Color.RGBA(54, 162, 235, 1.0),
                    borderColor = Color.RGBA(54, 162, 235, 1.0),
                    data = Numbers(96.82441701, 83.74314129, 61.82098765, 12.52914952, 54.80109739, 73.09156379, 69.91426612)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dashed"),
                    fill = Bool.False,
                    backgroundColor = Color.RGB(75, 192, 192),
                    borderColor = Color.RGB(75, 192, 192),
                    borderDash = Numbers(5, 5),
                    data = Numbers(93.69170096, 82.73662551, 17.17249657, 4.11179698, 62.63888889, 95.9122085, 81.54492455)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Filled"),
                    fill = Bool.True,
                    backgroundColor = Color.RGB(255, 99, 132),
                    borderColor = Color.RGB(255, 99, 132),
                    data = Numbers(0.05144033, 27.19993141, 71.94101509, 27.99897119, 85.49725652, 91.36659808, 80.12345679)
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Line chart with styling and scaling")
                )
            ),
            interaction = ChartModel.Options.Interaction(
                mode = InteractionMode.Index,
                intersect = Bool.False
            ),
            scales = ChartModel.Options.Scales(
                "y" to ChartModel.Options.Scales.Scale(
                    display = Bool.True,
                    title = ChartModel.Options.Scales.Scale.Title(
                        display = Bool.True,
                        text = Text("Value")
                    ),
                    min = Numeric(10),
                    max = Numeric(100),
                    suggestedMin = Numeric(30),
                    suggestedMax = Numeric(50),
                    ticks = ChartModel.Options.Scales.Scale.Ticks(
                        stepSize = Numeric(50)
                    ),
                    type = ScaleType.Logarithmic
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Line and bar chart with time based scale
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("2024-09-01T20:39:02.823Z", "2024-09-02T20:39:02.823Z", "2024-09-03T20:39:02.823Z", "2024-09-04T20:39:02.823Z", "2024-09-05T20:39:02.823Z", "2024-09-06T20:39:02.823Z", "2024-09-07T20:39:02.823Z"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("My first dataset"),
                    fill = Bool.False,
                    data = Numbers(12.71090535, 10.19633059, 65.60356653, 32.01903292, 14.38100137, 80.59070645, 39.29012346),
                    type = ChartType.Bar
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("My second dataset"),
                    fill = Bool.False,
                    data = Numbers(84.30641289, 5.76303155, 67.85751029, 53.30589849, 74.45387517, 51.05452675, 54.7127915)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset with point data"),
                    fill = Bool.False,
                    data = Complex(
                        mapOf("x" to Text("2024-09-01"), "y" to Numeric(19.99657064471879)),
                        mapOf("x" to Text("2024-09-06"), "y" to Numeric(9.235682441700959)),
                        mapOf("x" to Text("2024-09-08"), "y" to Numeric(22.214506172839506)),
                        mapOf("x" to Text("2024-09-11"), "y" to Numeric(38.254029492455416)),
                    )
                )
            )
        )
        options = ChartModel.Options(
            spanGaps = Numeric( 1000 * 60 * 60 * 24 * 2), // 2 days
            interaction = ChartModel.Options.Interaction(
                mode = InteractionMode.Nearest
            ),
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    text = Text("Time scale"),
                    display = Bool.True
                )
            ),
            scales = ChartModel.Options.Scales(
                "x" to ChartModel.Options.Scales.Scale(
                    type = ScaleType.Time,
                    time = ChartModel.Options.Scales.Scale.Time(
                        //tooltipFormat = Text("DD T"),
                        unit = TimeUnit.Day
                    ),
                    title = ChartModel.Options.Scales.Scale.Title(
                        text = Text("Date"),
                        display = Bool.True
                    ),
                    ticks = ChartModel.Options.Scales.Scale.Ticks(
                        autoSkip = Bool.False,
                        maxRotation = Numeric(0),
                        major = ChartModel.Options.Scales.Scale.Ticks.Major(
                            enabled = Bool.True
                        ),
                        font = Font(
                            weight = FontWeight.Bold
                        ),
                        source = Text("data")
                    ),
                    offset = Bool.True
                ),
                "y" to ChartModel.Options.Scales.Scale(
                    display = Bool.True,
                    title = ChartModel.Options.Scales.Scale.Title(
                        display = Bool.True,
                        text = Text("Value")
                    )
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Chart with point styling, and title, subtitle styling
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(-13.17558299, 90.02572016, 47.34224966, -72.09019204, 82.34567901, 31.63751715, 89.36556927),
                    fill = Bool.False,
                    borderWidth = Numeric(1),
                    pointStyle = PointStyle.RectRot,
                    pointRadius = Numeric(5),
                    pointBorderColor = Color.RGB(0, 0, 0)
                )
            )
        )
        options = ChartModel.Options(
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    labels = ChartModel.Options.Plugins.Legend.Labels(
                        usePointStyle = Bool.True,
                    ),
                    align = Align.End,
                    title = ChartModel.Options.Plugins.Legend.Title(
                        position = Align.Start
                    )
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    align = Align.Start,
                    text = Text("Chart title"),
                    font = Font(
                        size = Numeric(14),
                        family = Text("tahoma"),
                        weight = FontWeight.Bold
                    )
                ),
                subtitle = ChartModel.Options.Plugins.Subtitle(
                    display = Bool.True,
                    text = Text("Chart subtitle"),
                    color = Color.RGB(0, 0, 250),
                    font = Font(
                        size = Numeric(12),
                        family = Text("tahoma"),
                        weight = FontWeight.Normal,
                        style = Text("italic")
                    ),
                    padding = Padding.TopLeftBottomRight(
                        bottom = 10
                    )
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    columns = allColumns.toMutableList()
    row++

    // Combined line and bar chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Bar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(-54.47187929, 70.95164609, -16.17626886, -46.71982167, -70.0617284, 34.78566529, -18.59739369),
                    order = Numeric(1)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 2"),
                    data = Numbers(31.51748971, 83.8957476, 96.56207133, 42.35596708, -2.17935528, -34.57475995, 29.12037037),
                    type = ChartType.Line,
                    order = Numeric(0)
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Combined line and bar chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Stacked line and bar chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(14.66906722, 53.71827846, 88.96090535, 36.44633059, 41.85356653, 58.26903292, 90.63100137),
                    stack = Text("combined"),
                    type = ChartType.Bar
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 2"),
                    data = Numbers(6.84070645, 15.54012346, 10.55641289, 82.01303155, 94.10751029, 29.55589849, 0.70387517),
                    stack = Text("combined"),
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Stacked line and bar chart")
                )
            ),
            scales = ChartModel.Options.Scales(
                "y" to ChartModel.Options.Scales.Scale(
                    stacked = Bool.True
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Chart with line boundaries
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July", "August"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset"),
                    data = Numbers(-74.37, -18.49, 96.44, -97.48, 91.11, -31.61, -55.78, -45.58),
                    fill = Text("origin") // false, origin, start, end, but also -1, +1, -2, +2, etc..
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                filler = ChartModel.Options.Plugins.Filler(
                    propagate = Bool.False
                ),
                legend = ChartModel.Options.Plugins.Legend(
                    display = Bool.False
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Line boundaries")
                )
            ),
            interaction = ChartModel.Options.Interaction(
                intersect = Bool.False
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Chart with stacked scales
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Line
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(10, 30, 50, 20, 25, 15, -10)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 2"),
                    data = Strings("ON", "ON", "OFF", "ON", "OFF", "OFF", "ON"),
                    stepped = Bool.True,
                    yAxisID = Text("y2")
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Stacked scales")
                )
            ),
            scales = ChartModel.Options.Scales(
                "y" to ChartModel.Options.Scales.Scale(
                    type = ScaleType.Linear,
                    position = Position.Left,
                    stack = Text("demo"),
                    stackWeight = Numeric(2),
                    border = ChartModel.Options.Scales.Scale.Border(
                        color = Color.RGB(54, 162, 235)
                    )
                ),
                "y2" to ChartModel.Options.Scales.Scale(
                    type = ScaleType.Category,
                    labels = Strings("ON", "OFF"),
                    offset = Bool.True,
                    position = Position.Left,
                    stack = Text("demo"),
                    stackWeight = Numeric(1),
                    border = ChartModel.Options.Scales.Scale.Border(
                        color = Color.RGB(255, 99, 132)
                    )
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    columns = allColumns.toMutableList()
    row++

    // Doughnut chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Doughnut
        data = ChartModel.Data(
            labels = Strings("Red", "Orange", "Yellow", "Green", "Blue"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(31.62208505, 9.14866255, 69.28840878, 79.9425583, 49.75308642),
                    backgroundColor = Colors(Color.RGB(255, 99, 132), Color.RGB(255, 159, 64), Color.RGB(255, 205, 86), Color.RGB(75, 192, 192), Color.RGB(54, 162, 235), Color.RGB(153, 102, 255), Color.RGB(201, 203, 207))
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Doughnut chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Pie chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Pie
        data = ChartModel.Data(
            labels = Strings("Red", "Orange", "Yellow", "Green", "Blue"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(31.62208505, 9.14866255, 69.28840878, 79.9425583, 49.75308642),
                    backgroundColor = Colors(Color.RGB(255, 99, 132), Color.RGB(255, 159, 64), Color.RGB(255, 205, 86), Color.RGB(75, 192, 192), Color.RGB(54, 162, 235), Color.RGB(153, 102, 255), Color.RGB(201, 203, 207))
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Pie chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Pie chart with multiple groups
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Pie
        data = ChartModel.Data(
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Overall"),
                    backgroundColor = Colors(Color.Hex("aaa"), Color.Hex("777")),
                    data = Numbers(21, 79),
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Group A"),
                    backgroundColor = Colors(Color.HSL(0, 100, 60), Color.HSL(0, 100, 35)),
                    data = Numbers(33, 67)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Group B"),
                    backgroundColor = Colors(Color.HSL(100, 100, 60), Color.HSL(100, 100, 35)),
                    data = Numbers(20, 80)
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Group C"),
                    backgroundColor = Colors(Color.HSL(180, 100, 60), Color.HSL(180, 100, 35)),
                    data = Numbers(10, 90)
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Pie chart with multiple groups")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Polar area chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.PolarArea
        data = ChartModel.Data(
            labels = Strings("Red", "Orange", "Yellow", "Green", "Blue"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(31.62208505, 9.14866255, 69.28840878, 79.9425583, 49.75308642),
                    backgroundColor = Colors(Color.RGB(255, 99, 132), Color.RGB(255, 159, 64), Color.RGB(255, 205, 86), Color.RGB(75, 192, 192), Color.RGB(54, 162, 235), Color.RGB(153, 102, 255), Color.RGB(201, 203, 207))
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Polar area chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Polar area chart with centered point labels
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.PolarArea
        data = ChartModel.Data(
            labels = Strings("Red", "Orange", "Yellow", "Green", "Blue"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(31.62208505, 9.14866255, 69.28840878, 79.9425583, 49.75308642),
                    backgroundColor = Colors(Color.RGB(255, 99, 132), Color.RGB(255, 159, 64), Color.RGB(255, 205, 86), Color.RGB(75, 192, 192), Color.RGB(54, 162, 235), Color.RGB(153, 102, 255), Color.RGB(201, 203, 207))
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            scales = ChartModel.Options.Scales(
                "r" to ChartModel.Options.Scales.Scale(
                    pointLabels = ChartModel.Options.Scales.Scale.PointLabels(
                        display = Bool.True,
                        centerPointLabels = Bool.True,
                        font = Font(
                            size = Numeric(18)
                        )
                    )
                )
            ),
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    display = Bool.False
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Polar area chart with centered point labels")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Radar chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Radar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Numbers(62.89266118, 2.7649177, 32.65775034, 64.91683813, 19.29526749, 67.39797668, 43.79286694),
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 2"),
                    data = Numbers(23.78858025, 67.87894376, 32.85408093, 49.5781893, 67.43398491, 65.43381344, 29.99742798),
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Radar chart")
                )
            )
        )
    }

    // Radar chart with skipped points
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Radar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Skip first"),
                    data = Numbers(null, 64.74022634, 79.81824417, 8.37362826, 36.82613169, 3.4473594, 9.47187929),
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Skip mid"),
                    data = Numbers(96.875, 92.81721536, 20.7553155, null, 81.2611454, 75.55727023, 14.19495885),
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Skip last"),
                    data = Numbers(2.11248285, 7.21107682, 38.13271605, 88.70456104, 49.05006859, 83.36676955, null),
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Radar chart with skipped points")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    columns = allColumns.toMutableList()
    row++

    // Bubble chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Bubble
        data = ChartModel.Data(
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Complex(
                        mapOf("x" to Numeric(96.85185185), "y" to Numeric(4.86711248),  "r" to Numeric(5.660665294924554)),
                        mapOf("x" to Numeric(78.90517833), "y" to Numeric(62.74348422), "r" to Numeric(11.961119684499314)),
                        mapOf("x" to Numeric(59.62105624), "y" to Numeric(13.1095679),  "r" to Numeric(12.487397119341564)),
                        mapOf("x" to Numeric(36.53034979), "y" to Numeric(49.79252401), "r" to Numeric(7.393818587105624)),
                        mapOf("x" to Numeric(25.6824417),  "y" to Numeric(62.91580933), "r" to Numeric(12.019890260631001)),
                        mapOf("x" to Numeric(72.75634431), "y" to Numeric(16.67695473), "r" to Numeric(9.112525720164609)),
                        mapOf("x" to Numeric(30.83847737), "y" to Numeric(93.7920096),  "r" to Numeric(7.714934842249657))
                    )
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 2"),
                    data = Complex(
                        mapOf("x" to Numeric(37.22179355), "y" to Numeric(89.73293896), "r" to Numeric(7.979552469135802)),
                        mapOf("x" to Numeric(57.90680727), "y" to Numeric(83.77186214), "r" to Numeric(9.930727023319616)),
                        mapOf("x" to Numeric(56.26671811), "y" to Numeric(67.83136145), "r" to Numeric(7.80525548696845)),
                        mapOf("x" to Numeric(37.23979767), "y" to Numeric(54.25711591), "r" to Numeric(8.794495884773664)),
                        mapOf("x" to Numeric(68.72728052), "y" to Numeric(62.80221193), "r" to Numeric(9.719435871056241)),
                        mapOf("x" to Numeric(59.37114198), "y" to Numeric(65.07158779), "r" to Numeric(12.586248285322359)),
                        mapOf("x" to Numeric(2.99854252),  "y" to Numeric(95.63314472), "r" to Numeric(6.808513374485597))
                    )
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Bubble chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Scatter chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Scatter
        data = ChartModel.Data(
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Complex(
                        mapOf("x" to Numeric(96.85185185), "y" to Numeric(4.86711248),  "r" to Numeric(1)),
                        mapOf("x" to Numeric(78.90517833), "y" to Numeric(62.74348422), "r" to Numeric(1)),
                        mapOf("x" to Numeric(59.62105624), "y" to Numeric(13.1095679),  "r" to Numeric(1)),
                        mapOf("x" to Numeric(36.53034979), "y" to Numeric(49.79252401), "r" to Numeric(1)),
                        mapOf("x" to Numeric(25.6824417),  "y" to Numeric(62.91580933), "r" to Numeric(1)),
                        mapOf("x" to Numeric(72.75634431), "y" to Numeric(16.67695473), "r" to Numeric(1)),
                        mapOf("x" to Numeric(30.83847737), "y" to Numeric(93.7920096),  "r" to Numeric(1))
                    )
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 2"),
                    data = Complex(
                        mapOf("x" to Numeric(37.22179355), "y" to Numeric(89.73293896)),
                        mapOf("x" to Numeric(57.90680727), "y" to Numeric(83.77186214)),
                        mapOf("x" to Numeric(56.26671811), "y" to Numeric(67.83136145)),
                        mapOf("x" to Numeric(37.23979767), "y" to Numeric(54.25711591)),
                        mapOf("x" to Numeric(68.72728052), "y" to Numeric(62.80221193)),
                        mapOf("x" to Numeric(59.37114198), "y" to Numeric(65.07158779)),
                        mapOf("x" to Numeric(2.99854252),  "y" to Numeric(95.63314472))
                    )
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 3"),
                    data = NumberPairs(
                        70.65283427 to 43.76352722,
                        23.00932049 to 5.6104037,
                        66.89053925 to 13.19205772,
                        21.17961317 to 46.89946685,
                        85.56491301 to 69.50978456,
                        65.99178533 to 27.61810902,
                        80.39923983 to 69.35712784
                    )
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Scatter chart")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Multi axis scatter chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Scatter
        data = ChartModel.Data(
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Complex(
                        mapOf("x" to Numeric(96.85185185), "y" to Numeric(4.86711248),  "r" to Numeric(1)),
                        mapOf("x" to Numeric(78.90517833), "y" to Numeric(62.74348422), "r" to Numeric(1)),
                        mapOf("x" to Numeric(59.62105624), "y" to Numeric(13.1095679),  "r" to Numeric(1)),
                        mapOf("x" to Numeric(36.53034979), "y" to Numeric(49.79252401), "r" to Numeric(1)),
                        mapOf("x" to Numeric(25.6824417),  "y" to Numeric(62.91580933), "r" to Numeric(1)),
                        mapOf("x" to Numeric(72.75634431), "y" to Numeric(16.67695473), "r" to Numeric(1)),
                        mapOf("x" to Numeric(30.83847737), "y" to Numeric(93.7920096),  "r" to Numeric(1))
                    ),
                    yAxisID = Text("y1")
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 2"),
                    data = Complex(
                        mapOf("x" to Numeric(37.22179355), "y" to Numeric(89.73293896)),
                        mapOf("x" to Numeric(57.90680727), "y" to Numeric(83.77186214)),
                        mapOf("x" to Numeric(56.26671811), "y" to Numeric(67.83136145)),
                        mapOf("x" to Numeric(37.23979767), "y" to Numeric(54.25711591)),
                        mapOf("x" to Numeric(68.72728052), "y" to Numeric(62.80221193)),
                        mapOf("x" to Numeric(59.37114198), "y" to Numeric(65.07158779)),
                        mapOf("x" to Numeric(2.99854252),  "y" to Numeric(95.63314472))
                    ),
                    yAxisID = Text("y2")
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Multi axis scatter chart")
                )
            ),
            scales = ChartModel.Options.Scales(
                "y1" to ChartModel.Options.Scales.Scale(
                    type = ScaleType.Linear,
                    position = Position.Left,
                    ticks = ChartModel.Options.Scales.Scale.Ticks(
                        color = Color.RGB(54, 162, 235)
                    )
                ),
                "y2" to ChartModel.Options.Scales.Scale(
                    type = ScaleType.Linear,
                    position = Position.Right,
                    ticks = ChartModel.Options.Scales.Scale.Ticks(
                        color = Color.RGB(255, 99, 132)
                    ),
                    grid = ChartModel.Options.Scales.Scale.Grid(
                        drawOnChartArea = Bool.False
                    )
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    // Axis center scatter chart
    tableView[columns.removeAt(0), row] = chart {
        type = ChartType.Scatter
        data = ChartModel.Data(
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 1"),
                    data = Complex(
                        mapOf("x" to Numeric(-14.61248285), "y" to Numeric(-84.91083676)),
                        mapOf("x" to Numeric(93.11556927), "y" to Numeric(43.80486968)),
                        mapOf("x" to Numeric(-28.99176955), "y" to Numeric(-64.72222222)),
                        mapOf("x" to Numeric(-82.16906722), "y" to Numeric(43.82887517)),
                        mapOf("x" to Numeric(91.60836763),  "y" to Numeric(16.37174211)),
                        mapOf("x" to Numeric(65.18004115), "y" to Numeric(47.968107))
                    ),
                    fill = Bool.False,
                ),
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Dataset 2"),
                    data = Complex(
                        mapOf("x" to Numeric(-29.28326475), "y" to Numeric(85.60356653)),
                        mapOf("x" to Numeric(75.97565158), "y" to Numeric(-77.03875171)),
                        mapOf("x" to Numeric(69.91769547), "y" to Numeric(-91.73868313)),
                        mapOf("x" to Numeric(-37.58058985), "y" to Numeric(6.93587106)),
                        mapOf("x" to Numeric(89.28326475), "y" to Numeric(43.67626886)),
                        mapOf("x" to Numeric(67.79320988), "y" to Numeric(57.98868313))
                    ),
                    fill = Bool.False
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                legend = ChartModel.Options.Plugins.Legend(
                    position = Position.Top
                ),
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Axis center scatter chart")
                )
            ),
            scales = ChartModel.Options.Scales(
                "x" to ChartModel.Options.Scales.Scale(
                    min = Numeric(-100),
                    max = Numeric(100),
                    position = Position.Bottom
                ),
                "y" to ChartModel.Options.Scales.Scale(
                    min = Numeric(-100),
                    max = Numeric(100),
                    position = Position.Left,
                    border = ChartModel.Options.Scales.Scale.Border(
                        display = Bool.True
                    ),
                    grid = ChartModel.Options.Scales.Scale.Grid(
                        display = Bool.True,
                        drawOnChartArea = Bool.True,
                        drawTicks = Bool.True
                    ),
                    title = ChartModel.Options.Scales.Scale.Title(
                        display = Bool.True,
                        text = Text("Value"),
                        color = Color.Hex("#191"),
                        font = Font(
                            family = Text("Times"),
                            size = Numeric(20),
                            style = Text("normal"),
                            lineHeight = Numeric(1.2)
                        ),
                        padding = Padding.TopLeftBottomRight(
                            top = 30,
                            left = 0,
                            right = 0,
                            bottom = 0
                        )
                    )
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }

    columns = allColumns.toMutableList()
    row++

    tableView[Resource["custom-chart-parser.js"]] = js {
        """
            window.customChartParser = function(config) {
                const cfn = function colorize() {
                      return (ctx) => {
                            // Change bar color based on its value
                            const v = ctx.parsed.y;
                            const c = v < 0 ? '#D60000' : '#00D600';
                            return c;
                      };
                }

                config.options.elements = { bar: { backgroundColor: cfn() } };
            }
        """.trimIndent()
    }

    // Bar chart with custom parser
    tableView[columns.removeAt(0), row] = chart {
        parser = "window.customChartParser"
        type = ChartType.Bar
        data = ChartModel.Data(
            labels = Strings("January", "February", "March", "April", "May", "June", "July"),
            datasets = ChartModel.Data.Datasets(
                ChartModel.Data.Datasets.Dataset(
                    label = Text("Bar 1"),
                    data = Numbers(-34.39986283, -65.76646091, 16.24142661, 36.31515775, 33.96090535, -47.61145405, 60.73388203)
                )
            )
        )
        options = ChartModel.Options(
            responsive = Bool.True,
            plugins = ChartModel.Options.Plugins(
                title = ChartModel.Options.Plugins.Title(
                    display = Bool.True,
                    text = Text("Bar chart with custom config parser")
                )
            ),
            animation = ChartModel.Options.Animation(
                duration = Numeric(0)
            )
        )
    }
}
