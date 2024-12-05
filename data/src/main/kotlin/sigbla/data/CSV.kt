package sigbla.data

import sigbla.app.*
import java.io.*
import org.apache.commons.csv.CSVFormat as ApacheCSVFormat

enum class CSVFormat(internal val format: ApacheCSVFormat) {
    DEFAULT(ApacheCSVFormat.DEFAULT),
    EXCEL(ApacheCSVFormat.EXCEL),
    INFORMIX_UNLOAD(ApacheCSVFormat.INFORMIX_UNLOAD),
    INFORMIX_UNLOAD_CSV(ApacheCSVFormat.INFORMIX_UNLOAD_CSV),
    MONGO_CSV(ApacheCSVFormat.MONGODB_CSV),
    MONGO_TSV(ApacheCSVFormat.MONGODB_TSV),
    MYSQL(ApacheCSVFormat.MYSQL),
    ORACLE(ApacheCSVFormat.ORACLE),
    POSTGRESQL_CSV(ApacheCSVFormat.POSTGRESQL_CSV),
    POSTGRESQL_TEXT(ApacheCSVFormat.POSTGRESQL_TEXT),
    RFC4180(ApacheCSVFormat.RFC4180),
    TDF(ApacheCSVFormat.TDF)
}

class CSVInput internal constructor(
    val input: Reader,
    val format: CSVFormat,
    val withHeader: Boolean
)

class CSVOutput internal constructor(
    val output: Writer,
    val format: CSVFormat,
    val withHeader: Boolean
)

fun csv(
    input: Reader,
    format: CSVFormat = CSVFormat.DEFAULT,
    withHeader: Boolean = true
): CSVInput = CSVInput(input, format, withHeader)

fun csv(
    output: Writer,
    format: CSVFormat = CSVFormat.DEFAULT,
    withHeader: Boolean = true
): CSVOutput = CSVOutput(output, format, withHeader)

fun import(
    sourceDest: Pair<CSVInput, Table>,
    filter: Row.() -> Unit = { }
) {
    val csvInput = sourceDest.first
    val destTable = sourceDest.second

    val parser = csvInput.format.format.builder().let {
        if (csvInput.withHeader) {
            it.setHeader()
        }

        it.build()
    }

    val records = parser.parse(csvInput.input)
    val headers = records.headerNames
    val table = clone(destTable)

    batch(destTable) {
        records.forEachIndexed { index, record ->
            clear(table)

            if (index == 0) headers.forEach {
                table[it] // Define headers
            }

            record.toList().forEachIndexed { col, value ->
                val header = headers.getOrElse(col) { i -> i.toString() }
                table[header, index] = value
            }

            table[index].filter()

            table[index].forEach {
                destTable[it] = it
            }
        }
    }
}

fun export(
    sourceDest: Pair<Table, CSVOutput>
) {
    val sourceTable = sourceDest.first
    val csvOutput = sourceDest.second

    batch(sourceTable) {
        val headers = sourceTable.headers

        val printer = csvOutput.format.format.builder().let {
            if (csvOutput.withHeader) {
                val headersText = headers.map { header -> header.labels.joinToString() }.toList()
                it.setHeader(*headersText.toTypedArray())
            }

            it.build().print(csvOutput.output)
        }

        sourceTable.indexes.map { sourceTable[it] }.forEach { row ->
            val rowValues = headers.map { header -> sourceTable[header][row].toString() }.toList()
            printer.printRecord(rowValues)
        }

        printer.flush()
    }
}
