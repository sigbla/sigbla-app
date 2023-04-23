package sigbla.app.exceptions

import sigbla.app.TableListenerReference
import sigbla.app.TableViewListenerReference

class ListenerLoopException : SigblaAppException {
    constructor(listenerReference: TableListenerReference) : this("Listener loop detected on table listener: $listenerReference")
    constructor(listenerReference: TableViewListenerReference) : this("Listener loop detected on view listener: $listenerReference")
    constructor(message: String) : super(message)
}