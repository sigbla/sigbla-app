package sigbla.app.exceptions

import sigbla.app.TableListenerReference
import sigbla.app.TableViewListenerReference

class ListenerLoopException : SigblaAppException {
    constructor() : super()

    constructor(listenerReference: TableListenerReference) : this("Listener loop detected on table listener: $listenerReference")

    constructor(listenerReference: TableViewListenerReference) : this("Listener loop detected on view listener: $listenerReference")

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)

    protected constructor(
        message: String,
        cause: Throwable,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(message, cause, enableSuppression, writableStackTrace)
}