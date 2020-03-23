package sigbla.app

class ListenerReference {
    fun unsubscribe() {}
}

data class ListenerEvent<T>(val subscriber: T, val listenerReference: ListenerReference, val cells: List<Cell<*>>)
