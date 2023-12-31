/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.exceptions

class InvalidTableException : SigblaAppException {
    constructor() : super()
    constructor(message: String) : super(message)
}
