package sigbla.app.exceptions

import sigbla.app.Column
import sigbla.app.ColumnHeader

// TODO Look at other exceptions and change to below format?
class InvalidColumnException : SigblaAppException {
    internal constructor(message: String) : super(message)
    internal constructor(column: Column) : super(column.toString())
    internal constructor(columnHeader: ColumnHeader) : super(columnHeader.toString())
}