/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import org.junit.AfterClass
import org.junit.Test
import sigbla.app.*
import java.io.FileReader
import java.io.StringWriter
import kotlin.test.assertEquals

class TableUtilsTest {
    @Test
    fun `print table`() {
        val t = Table["${this.javaClass.simpleName} ${object {}.javaClass.enclosingMethod.name}"]

        for (column1 in (0..10).map { i -> "C1_$i" }) {
            for (column2 in (0..10).map { i -> "C2_$i" }) {
                for (column3 in (0..10).map { i -> "C3_$i" }) {
                    for (row in 0..100) {
                        t[column1, column2, column3, row] = "$column1:$column2:$column3:$row"
                    }
                }
            }
        }

        val stringWriter = StringWriter()
        print(t, stringWriter)

        val testSample = FileReader(this::class.java.classLoader.getResource("print_table_test.txt").file)

        val testLines = testSample.readLines()
        val actualLines = stringWriter.buffer.lines()

        assertEquals(105, actualLines.size)

        for (line in 0..103) {
            assertEquals(testLines[line], actualLines[line])
        }
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup(): Unit {
            Table.tables.filter { it.name?.startsWith(Companion::class.java.declaringClass.simpleName) == true }.forEach { remove(it) }
        }
    }
}
