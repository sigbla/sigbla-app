# Widgets

Any app would sooner or later need some way of letting the user provide some input. Sigbla allows for this using a set
of widgets currently supporting buttons, check boxes, radio buttons, and text fields.

Like for charts, widgets are placed under a separate `sigbla.widgets` package, so do an `import sigbla.widgets.*` in
addition to the usual `import sigbla.app.*` to make use of them.

Unless you double click a selected cell, you'll not have direct access to the widget with a mouse pointer. But a
selected cell with a widget in it will receive keyboard inputs. The space bar can therefore be used to "click" the
button, check box, or ratio button.

## Button

Below is an example of how we'd create two buttons, where their actions increments or decrements a cell value.

``` kotlin
import sigbla.app.*
import sigbla.widgets.*

fun main() {
    TableView[Port] = 8080

    val table = Table[null]
    val tableView = TableView[table]

    // Create a spacer to give the buttons some breathing room on the UI
    // Also assign a Unit to it to avoid the column being prenatal and hidden
    val spacer = table[" ", 0].also { it(Unit) }
    tableView[spacer.column][CellWidth] = 20

    // Create reference to relevant cells and init their layout order
    val incButton = table["Increment", 1]
    val value = table["Value", 1]
    val decButton = table["Decrement", 1]

    // Init the starting value
    table[value] = 0

    // Set up increment button and its action
    tableView[incButton] = button("+") {
        table[value] = table[value] + 1
    }

    // Set up decrement button and its action
    tableView[decButton] = button("-") {
        table[value] = table[value] - 1
    }

    val url = show(tableView, ref = "buttons", config = spaciousViewConfig(title = "Buttons"))
    println(url)
}
```

![Example of two buttons](img/widgets_buttons.png)

## Check box

Next we're showing an example of a check box, where we access its checked value from within the action.

``` kotlin
import sigbla.app.*
import sigbla.widgets.*

fun main() {
    TableView[Port] = 8080

    val table = Table["widgets"]
    val tableView = TableView[table]

    tableView["Checkbox", 0] = checkBox("Check me") {
        table["Result", 0] = if (this.checked) "Checked" else "Not checked"
    }

    val url = show(tableView)
    println(url)
}
```

## Radio button

Radio buttons can be checked, and should uncheck any other related radio button in the process. In the next example
we're creating two radio buttons, and linking them so that one unchecks the other.

``` kotlin
import sigbla.app.*
import sigbla.widgets.*

fun main() {
    TableView[Port] = 8080

    val table = Table[null]
    val tableView = TableView[table]

    // Create a spacer to give the buttons some breathing room on the UI
    // Also assign a Unit to it to avoid the column being prenatal and hidden
    val spacer = table[" ", 0].also { it(Unit) }
    tableView[spacer.column][CellWidth] = 20

    // Create reference to relevant cells and init their layout order
    val radio1 = tableView["Radio 1", 1]
    val radio2 = tableView["Radio 2", 1]

    fun initButtons(selected: Int = 0) {
        tableView[radio1] = radio("Button 1", selected = selected == 1) {
            initButtons(1)
        }
        tableView[radio2] = radio("Button 2", selected = selected == 2) {
            initButtons(2)
        }
    }

    initButtons()

    val url = show(tableView, ref = "radio", config = spaciousViewConfig(title = "Radio buttons"))
    println(url)
}
```

![Example of two radio buttons](img/widgets_radio_buttons.png)

This example is showing something which is typical in Sigbla: Values are typically immutable. When we want to
change the selected status of our two radio buttons, we do so by reassigning them to the table view.

## Text field

The text field widget allows the user to enter text which we can then access within the text field action function
through `this.text`. The action will fire when the input field loses focus.

``` kotlin
import sigbla.app.*
import sigbla.widgets.*

fun main() {
    TableView[Port] = 8080

    val table = Table["widgets"]
    val tableView = TableView[table]

    tableView["Input", 0 ] = textField("Existing text") {
        println("Text update: ${this.text}")
    }

    val url = show(tableView)
    println(url)
}
```

## Updates within action function

The action function will have access to properties that can be modified. If any of these are modified, the relevant
widget will be reassigned automatically making use of the updated values.

We can use this to update the text of a button, or change the selected state of a check box, etc. Here's an example
that updates the button text each time we click it:

``` kotlin
import sigbla.app.*
import sigbla.widgets.*

fun main() {
    TableView[Port] = 8080

    val table = Table["widgets"]
    val tableView = TableView[table]

    tableView["Button", 0] = button("0") {
        this.text = (this.text.toLong() + 1).toString()
    }

    val url = show(tableView)
    println(url)
}
```
