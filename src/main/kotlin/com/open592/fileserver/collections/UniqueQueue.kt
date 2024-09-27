package com.open592.fileserver.collections

class UniqueQueue<T> {
  private val queue = ArrayDeque<T>()
  private val set = mutableSetOf<T>()

  fun add(element: T): Boolean {
    if (set.add(element)) {
      queue.addLast(element)

      return true
    }

    return false
  }

  fun removeFirstOrNull(): T? {
    val element = queue.removeFirstOrNull()

    if (element != null) {
      set.remove(element)

      return element
    }

    return null
  }

  fun clear() {
    queue.clear()
    set.clear()
  }
}
