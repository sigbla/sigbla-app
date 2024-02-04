/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.exceptions

import sigbla.app.TableListenerReference
import sigbla.app.TableViewListenerReference

class ListenerLoopException : SigblaAppException {
    internal constructor(listenerReference: TableListenerReference) : this("Listener loop detected on table listener: $listenerReference")
    internal constructor(listenerReference: TableViewListenerReference) : this("Listener loop detected on view listener: $listenerReference")
    internal constructor(message: String) : super(message)
}
