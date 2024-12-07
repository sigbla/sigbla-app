# Data

Within the `sigbla.data` package you'll find utility functions for importing and exporting data to and from tables.

## CSV

Importing a CSV file can be done like shown below:

``` kotlin
import sigbla.app.*
import sigbla.data.*
import java.io.*

fun main() {
    val table = Table["csv"]
    val reader = File("path/to/data.csv").bufferedReader()

    import(csv(reader) to table)
}
```

By default, the import function assumes the CSV file contains a header row, and will use that to define the table
headers. If that's not the case, specify `withHeader = false`:

``` kotlin
val table = Table["csv"]
val reader = File("path/to/data.csv").bufferedReader()

import(csv(reader, withHeader = false) to table)
```

When `false`, default header values are set, named according to the column position ranging from zero onwards.

It's also possible to apply filtering to the rows being read, handy if you need to apply formatting or remove/add
columns:

``` kotlin
val table = Table["csv"]
val reader = File("path/to/data.csv").bufferedReader()

import(csv(reader) to table) {
    remove(this["Index"].column) // Remove column with name "Index"
}
```

Exporting to CSV follows a similar pattern:

``` kotlin
val writer = File("path/to/data.csv").bufferedWriter()
export(table to csv(writer))
```

Again, if you don't want headers to be exported, specify `withHeader = false`:

``` kotlin
val writer = File("path/to/data.csv").bufferedWriter()
export(table to csv(writer, withHeader = false))
```
