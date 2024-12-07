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

### Formats

Internally, Sigbla relies on the Apache Commons CSV library for reading and writing CSV. Multiple formats are supported
which can be specified with the `format` parameter:

``` kotlin
import(csv(reader, format = CSVFormat.DEFAULT) to table)
export(table to csv(writer, format = CSVFormat.DEFAULT))
```

List of support formats, in addition to [DEFAULT](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#DEFAULT),
includes [EXCEL](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#EXCEL),
[INFORMIX_UNLOAD](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#INFORMIX_UNLOAD),
[INFORMIX_UNLOAD_CSV](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#INFORMIX_UNLOAD_CSV),
[MONGO_CSV](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#MONGODB_CSV),
[MONGO_TSV](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#MONGODB_TSV),
[MYSQL](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#ORACLE),
[ORACLE](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#ORACLE),
[POSTGRESQL_CSV](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#POSTGRESQL_CSV),
[POSTGRESQL_TEXT](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#POSTGRESQL_TEXT),
[RFC4180](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#RFC4180), and
[TDF](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#TDF).
