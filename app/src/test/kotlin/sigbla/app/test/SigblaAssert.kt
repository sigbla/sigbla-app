/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import org.junit.Test
import org.junit.Assert.assertTrue
import sigbla.app.internals.enableAssert

class SigblaAssert {
    @Test
    fun `ensure sigbla assert`() {
        assertTrue("Not running with -Dsigbla.assert=true", enableAssert)
    }
}
