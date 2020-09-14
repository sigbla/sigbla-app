package sigbla.app.exceptions

import sigbla.app.ListenerReference

class ListenerLoopException : SigblaAppException {
    constructor() : super()

    constructor(listenerReference: ListenerReference) : this("Listener loop detected on listener: $listenerReference")

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