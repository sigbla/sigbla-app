# Introduction to Sigbla

Sigbla is a framework for working with data in tables, using the Kotlin programming language.
It supports [various data types](https://sigbla.app/docs/cells/),
[reactive programming and events](https://sigbla.app/docs/table_subscriptions/),
[user input](https://sigbla.app/docs/widgets/), [charts](https://sigbla.app/docs/charts/), among other things.

We're probably all used to working with data in tables, using applications like Microsoft Excel or Google Sheets.
Those are great, and used by many for all sorts of reasons. But they've got some shortcomings, such as:

* Mixing code, data, and presentation into the same sheet
* Performance challenges when doing a lot of calculations
* Usability limitations for larger quantities of data

These issues make them difficult to maintain and understand, and limits how much we sanely want to do with them.

Sigbla fixes this by:

* Providing an easy to use table structure for data
* With programming residing in source code files
* And letting you view the data through views

Sigbla also provides other things, like extending and making easier how we can do math in Kotlin,
and, as mentioned, allows us to wire together relationships between cells through reactive programming.

## Getting started

You can make use of Sigbla in your Kotlin projects by declaring it as a dependency.

Include it in your Gradle build file like so:

```
repositories {
    maven { url = uri("https://mvn.sigbla.app/repository") }
}

dependencies {
    implementation("sigbla.app:sigbla-app-all:1.+")
}
```

Please also consult the [Sigbla sample project](https://github.com/sigbla/sigbla-app-sample) for a working Gradle example.

However, you don't need a full project setup and can also use a Kotlin REPL, such as [kscript](https://github.com/kscripting/kscript).

Assuming you have kscript installed, here's a quick example:

``` kotlin
#!/usr/bin/env kscript

@file:Repository("https://mvn.sigbla.app/repository")
@file:DependsOn("sigbla.app:sigbla-app-all:[1,)")

import sigbla.app.*

val table = Table["MyTable"]

table["Column A", 0] = 100
table["Column A", 1] = 200

// Instead of sum(table["Column A", 0], table["Column A", 1]), you can also do sum(table["Column A"])
// if you want to sum all "Column A" values. Other variants and combinations are also possible.
table["Sum", 2] = sum(table["Column A", 0], table["Column A", 1])

val tableView = TableView[table]

val url = show(tableView)
println("Table URL: $url")
```

It's a basic example that puts the values 100 and 200 into a cell each, before assigning the sum of these to a separate
column we label "Sum" at row 2.

Because of the reactive nature of `sum(..)`, if the values update, so will the sum.

Please see the [Sigbla documentation](https://sigbla.app/docs) for further details.
