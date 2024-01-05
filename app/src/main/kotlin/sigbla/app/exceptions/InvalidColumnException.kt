/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.exceptions

import sigbla.app.Column
import sigbla.app.Header

class InvalidColumnException : SigblaAppException {
    internal constructor(message: String) : super(message)
    internal constructor(column: Column) : super(column.toString())
    internal constructor(header: Header) : super(header.toString())
}
