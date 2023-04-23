package sigbla.app.exceptions

class InvalidTableViewException : SigblaAppException {
    constructor() : super()
    constructor(message: String) : super(message)
}