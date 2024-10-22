package com.open592.fileserver.collections

// https://github.com/openrs2/openrs2/blob/master/util/src/main/kotlin/org/openrs2/util/collect/UniqueQueue.kt
//
// Author: Graham
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
