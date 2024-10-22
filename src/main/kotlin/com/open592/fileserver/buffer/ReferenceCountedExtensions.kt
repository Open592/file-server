package com.open592.fileserver.buffer

import io.netty.util.ReferenceCounted

// https://github.com/openrs2/openrs2/blob/master/buffer/src/main/kotlin/org/openrs2/buffer/ReferenceCountedExtensions.kt
//
// Author: Graham
inline fun <T : ReferenceCounted?, R> T.use(block: (T) -> R): R {
  try {
    return block(this)
  } finally {
    this?.release()
  }
}
