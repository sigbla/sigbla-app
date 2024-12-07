package sigbla.data.test

import org.junit.AfterClass
import org.junit.Test
import sigbla.app.*
import sigbla.data.*
import java.io.File
import java.io.FileReader
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.apache.commons.csv.CSVFormat as ApacheCSVFormat

class CSVTest {
    @Test
    fun `csv import`() {
        val table = Table[null]

        import(csv(FileReader(this::class.java.classLoader.getResource("test1.csv").file)) to table)

        val expectedHeaders = listOf("Index", "Customer Id", "First Name", "Last Name", "Company", "City", "Country", "Phone 1", "Phone 2", "Email", "Subscription Date", "Website")
        val expectedFirstRow = listOf("1", "DD37Cf93aecA6Dc", "Sheryl", "Baxter", "Rasmussen Group", "East Leonard", "Chile", "229.077.5154", "397.884.0519x718", "zunigavanessa@smith.info", "2020-08-24", "http://www.stephenson.com/")
        val expectedLastRow = listOf("100", "2354a0E336A91A1", "Clarence", "Haynes", "Le, Nash and Cross", "Judymouth", "Honduras", "(753)813-6941", "783.639.1472", "colleen91@faulkner.biz", "2020-03-11", "http://www.hatfield-saunders.net/")

        assertEquals(expectedHeaders, table.headers.map { it.labels.joinToString() }.toList())
        assertEquals(100, table.indexes.count())
        assertEquals(expectedFirstRow, table[0].map { it.toString() }.toList())
        assertEquals(expectedLastRow, table[99].map { it.toString() }.toList())
    }

    @Test
    fun `csv import with header`() {
        val table = Table[null]

        import(csv(FileReader(this::class.java.classLoader.getResource("test1.csv").file), withHeader = true) to table)

        val expectedHeaders = listOf("Index", "Customer Id", "First Name", "Last Name", "Company", "City", "Country", "Phone 1", "Phone 2", "Email", "Subscription Date", "Website")
        val expectedFirstRow = listOf("1", "DD37Cf93aecA6Dc", "Sheryl", "Baxter", "Rasmussen Group", "East Leonard", "Chile", "229.077.5154", "397.884.0519x718", "zunigavanessa@smith.info", "2020-08-24", "http://www.stephenson.com/")
        val expectedLastRow = listOf("100", "2354a0E336A91A1", "Clarence", "Haynes", "Le, Nash and Cross", "Judymouth", "Honduras", "(753)813-6941", "783.639.1472", "colleen91@faulkner.biz", "2020-03-11", "http://www.hatfield-saunders.net/")

        assertEquals(expectedHeaders, table.headers.map { it.labels.joinToString() }.toList())
        assertEquals(100, table.indexes.count())
        assertEquals(expectedFirstRow, table[0].map { it.toString() }.toList())
        assertEquals(expectedLastRow, table[99].map { it.toString() }.toList())
    }

    @Test
    fun `csv import without header`() {
        val table = Table[null]

        import(csv(FileReader(this::class.java.classLoader.getResource("test1.csv").file), withHeader = false) to table)

        val expectedFirstRow = listOf("Index", "Customer Id", "First Name", "Last Name", "Company", "City", "Country", "Phone 1", "Phone 2", "Email", "Subscription Date", "Website")
        val expectedSecondRow = listOf("1", "DD37Cf93aecA6Dc", "Sheryl", "Baxter", "Rasmussen Group", "East Leonard", "Chile", "229.077.5154", "397.884.0519x718", "zunigavanessa@smith.info", "2020-08-24", "http://www.stephenson.com/")
        val expectedLastRow = listOf("100", "2354a0E336A91A1", "Clarence", "Haynes", "Le, Nash and Cross", "Judymouth", "Honduras", "(753)813-6941", "783.639.1472", "colleen91@faulkner.biz", "2020-03-11", "http://www.hatfield-saunders.net/")

        assertEquals((0..11).map { it.toString() }.toList(), table.headers.map { it.labels.joinToString() }.toList())
        assertEquals(101, table.indexes.count())
        assertEquals(expectedFirstRow, table[0].map { it.toString() }.toList())
        assertEquals(expectedSecondRow, table[1].map { it.toString() }.toList())
        assertEquals(expectedLastRow, table[100].map { it.toString() }.toList())
    }

    @Test
    fun `csv import with filter`() {
        val table = Table[null]

        import(csv(FileReader(this::class.java.classLoader.getResource("test1.csv").file)) to table) {
            remove(this["Index"].column)
        }

        val expectedHeaders = listOf("Customer Id", "First Name", "Last Name", "Company", "City", "Country", "Phone 1", "Phone 2", "Email", "Subscription Date", "Website")
        val expectedFirstRow = listOf("DD37Cf93aecA6Dc", "Sheryl", "Baxter", "Rasmussen Group", "East Leonard", "Chile", "229.077.5154", "397.884.0519x718", "zunigavanessa@smith.info", "2020-08-24", "http://www.stephenson.com/")
        val expectedLastRow = listOf("2354a0E336A91A1", "Clarence", "Haynes", "Le, Nash and Cross", "Judymouth", "Honduras", "(753)813-6941", "783.639.1472", "colleen91@faulkner.biz", "2020-03-11", "http://www.hatfield-saunders.net/")

        assertEquals(expectedHeaders, table.headers.map { it.labels.joinToString() }.toList())
        assertEquals(100, table.indexes.count())
        assertEquals(expectedFirstRow, table[0].map { it.toString() }.toList())
        assertEquals(expectedLastRow, table[99].map { it.toString() }.toList())
    }

    @Test
    fun `csv export`() {
        val table = Table[null]

        table["AB", "A", 0] = "AB 1"
        table["AB", "B", 0] = "AB 2"
        table["C", 1] = "Test with \"quote\""
        table["C", 2] = "Test with \""

        val buffer = StringWriter()
        export(table to csv(buffer))

        assertEquals(FileReader(this::class.java.classLoader.getResource("test2.csv").file).readText(), buffer.toString())
    }

    @Test
    fun `csv export with header`() {
        val table = Table[null]

        table["AB", "A", 0] = "AB 1"
        table["AB", "B", 0] = "AB 2"
        table["C", 1] = "Test with \"quote\""
        table["C", 2] = "Test with \""

        val buffer = StringWriter()
        export(table to csv(buffer, withHeader = true))

        assertEquals(FileReader(this::class.java.classLoader.getResource("test2.csv").file).readText(), buffer.toString())
    }

    @Test
    fun `csv export without header`() {
        val table = Table[null]

        table["AB", "A", 0] = "AB 1"
        table["AB", "B", 0] = "AB 2"
        table["C", 1] = "Test with \"quote\""
        table["C", 2] = "Test with \""

        val buffer = StringWriter()
        export(table to csv(buffer, withHeader = false))

        println("1..")
        println(this::class.java.classLoader.getResource("test3.csv").file)
        println(File(this::class.java.classLoader.getResource("test3.csv").file).exists())
        println()
        println(FileReader(this::class.java.classLoader.getResource("test3.csv").file).readText())
        println()
        println(buffer.toString())

        assertEquals(FileReader(this::class.java.classLoader.getResource("test3.csv").file).readText(), buffer.toString())
    }

    @Test
    fun `csv formats`() {
        for (format in CSVFormat.entries) {
            val csvImport = csv(Reader.nullReader(), format)
            when (format) {
                CSVFormat.DEFAULT -> assertEquals(ApacheCSVFormat.DEFAULT, csvImport.format.format)
                CSVFormat.EXCEL -> assertEquals(ApacheCSVFormat.EXCEL, csvImport.format.format)
                CSVFormat.INFORMIX_UNLOAD -> assertEquals(ApacheCSVFormat.INFORMIX_UNLOAD, csvImport.format.format)
                CSVFormat.INFORMIX_UNLOAD_CSV -> assertEquals(ApacheCSVFormat.INFORMIX_UNLOAD_CSV, csvImport.format.format)
                CSVFormat.MONGO_CSV -> assertEquals(ApacheCSVFormat.MONGODB_CSV, csvImport.format.format)
                CSVFormat.MONGO_TSV -> assertEquals(ApacheCSVFormat.MONGODB_TSV, csvImport.format.format)
                CSVFormat.MYSQL -> assertEquals(ApacheCSVFormat.MYSQL, csvImport.format.format)
                CSVFormat.ORACLE -> assertEquals(ApacheCSVFormat.ORACLE, csvImport.format.format)
                CSVFormat.POSTGRESQL_CSV -> assertEquals(ApacheCSVFormat.POSTGRESQL_CSV, csvImport.format.format)
                CSVFormat.POSTGRESQL_TEXT -> assertEquals(ApacheCSVFormat.POSTGRESQL_TEXT, csvImport.format.format)
                CSVFormat.RFC4180 -> assertEquals(ApacheCSVFormat.RFC4180, csvImport.format.format)
                CSVFormat.TDF -> assertEquals(ApacheCSVFormat.TDF, csvImport.format.format)
                else -> assertTrue(false)
            }
        }

        for (format in CSVFormat.entries) {
            val csvOutput = csv(Writer.nullWriter(), format)
            when (format) {
                CSVFormat.DEFAULT -> assertEquals(ApacheCSVFormat.DEFAULT, csvOutput.format.format)
                CSVFormat.EXCEL -> assertEquals(ApacheCSVFormat.EXCEL, csvOutput.format.format)
                CSVFormat.INFORMIX_UNLOAD -> assertEquals(ApacheCSVFormat.INFORMIX_UNLOAD, csvOutput.format.format)
                CSVFormat.INFORMIX_UNLOAD_CSV -> assertEquals(ApacheCSVFormat.INFORMIX_UNLOAD_CSV, csvOutput.format.format)
                CSVFormat.MONGO_CSV -> assertEquals(ApacheCSVFormat.MONGODB_CSV, csvOutput.format.format)
                CSVFormat.MONGO_TSV -> assertEquals(ApacheCSVFormat.MONGODB_TSV, csvOutput.format.format)
                CSVFormat.MYSQL -> assertEquals(ApacheCSVFormat.MYSQL, csvOutput.format.format)
                CSVFormat.ORACLE -> assertEquals(ApacheCSVFormat.ORACLE, csvOutput.format.format)
                CSVFormat.POSTGRESQL_CSV -> assertEquals(ApacheCSVFormat.POSTGRESQL_CSV, csvOutput.format.format)
                CSVFormat.POSTGRESQL_TEXT -> assertEquals(ApacheCSVFormat.POSTGRESQL_TEXT, csvOutput.format.format)
                CSVFormat.RFC4180 -> assertEquals(ApacheCSVFormat.RFC4180, csvOutput.format.format)
                CSVFormat.TDF -> assertEquals(ApacheCSVFormat.TDF, csvOutput.format.format)
                else -> assertTrue(false)
            }
        }
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
            TableView.views.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}