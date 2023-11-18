# Widgets

Any app would sooner or later need some way of letting the user provide some input. Sigbla allows for this using a set
of widgets currently supporting buttons, check boxes, radio buttons, and text fields.

Like for charts, widgets are placed under a separate `sigbla.widgets` package, so do an `import sigbla.widgets.*` in
addition to the usual `import sigbla.app.*` to make use of them.

## Button

Below is an example of how we'd create two buttons, where their actions increments or decrements a cell value.

``` kotlin
import sigbla.app.*
import sigbla.widgets.*

fun main() {
    TableView[Port] = 8080

    val table = Table["widgets"]
    val tableView = TableView[table]

    tableView["Increment", 0] = button("+") {
        table["Value", 0] = table["Value", 0] + 1
    }

    table["Value", 0] = 0

    tableView["Decrement", 0] = button("-") {
        table["Value", 0] = table["Value", 0] - 1
    }

    show(tableView)
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

    show(tableView)
}
```

## Radio button

Radio buttons can be checked, but not unchecked unless we check another related radio button. In the next example
we're creating two radio buttons, and linking them so that one unchecks the other.

``` kotlin
import sigbla.app.*
import sigbla.widgets.*

fun main() {
    TableView[Port] = 8080

    val table = Table["widgets"]
    val tableView = TableView[table]

    fun initButtons(selected: Int = 0) {
        tableView["Radio 1", 0] = radio("Button 1", selected = selected == 1) {
            initButtons(1)
        }
        tableView["Radio 2", 0] = radio("Button 2", selected = selected == 2) {
            initButtons(2)
        }
    }

    initButtons()

    show(tableView)
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

    show(tableView)
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

    show(tableView)
}
```
