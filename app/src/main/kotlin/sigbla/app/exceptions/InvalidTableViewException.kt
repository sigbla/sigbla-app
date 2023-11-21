/* Copyright 2019-2023, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.exceptions

class InvalidTableViewException : SigblaAppException {
    constructor() : super()
    constructor(message: String) : super(message)
}
