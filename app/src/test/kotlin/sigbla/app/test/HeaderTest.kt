/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import sigbla.app.*
import org.junit.Assert.*
import org.junit.Test
import sigbla.app.exceptions.InvalidColumnException
import kotlin.test.assertFailsWith

class HeaderTest {
    @Test
    fun `header constructors`() {
        // Internal constructors
        val h1 = Header("L1", "L2", "L3")
        val h2 = Header(listOf("L1", "L2"))

        // Public constructors
        val h3 = Header["L1", "L2"]
        val h4 = Header[listOf("L1", "L2", "L3")]

        assertEquals(h1, h4)
        assertEquals(h2, h3)
        assertNotEquals(h1, h2)
        assertNotEquals(h3, h4)
        assertNotEquals(h1, h3)
        assertNotEquals(h2, h4)
    }

    @Test
    fun `disallow null values`() {
        assertFailsWith<InvalidColumnException> {
            Header[listOf("A", null, "B") as List<String>]
        }
    }

    @Test
    fun `out of bounds returns null`() {
        val h = Header["L1", "L2"]

        assertEquals("L1", h[0])
        assertEquals("L2", h[1])
        assertNull(h[2])
        assertNull(h[3])
    }

    @Test
    fun destructuring() {
        val (a, b, c, d, e) = Header["A", "B", "C", "D", "E", "F"]

        assertEquals("A", a)
        assertEquals("B", b)
        assertEquals("C", c)
        assertEquals("D", d)
        assertEquals("E", e)
    }

    @Test
    fun `header sort`() {
        assertEquals(listOf(Header[""], Header["", ""], Header["", "", ""]), listOf(Header["", "", ""], Header[""], Header["", ""]).sorted())
        assertEquals(listOf(Header["C"], Header["C", "A"], Header["C", "B"]), listOf(Header["C"], Header["C", "B"], Header["C", "A"]).sorted())
    }
}