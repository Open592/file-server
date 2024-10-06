package com.open592.fileserver.collections

class UniqueQueue<T> {
  private val queue = ArrayDeque<T>()
  private val set = mutableSetOf<T>()

  fun add(value: T): Boolean {
    if (set.add(value)) {
      queue.addLast(value)

      return true
    }

    return false
  }

  fun removeFirstOrNull(): T? {
    val value = queue.removeFirstOrNull()

    if (value != null) {
      set.remove(value)

      return value
    }

    return null
  }

  fun clear() {
    queue.clear()
    set.clear()
  }
}
