package sigbla.app.exceptions

class InvalidTableException : SigblaAppException {
    constructor() : super()
    constructor(message: String) : super(message)
}