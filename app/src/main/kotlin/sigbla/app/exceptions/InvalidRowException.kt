/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.exceptions

import sigbla.app.Row

class InvalidRowException : SigblaAppException {
    internal constructor(message: String) : super(message)
    internal constructor(row: Row) : super(row.toString())
}
