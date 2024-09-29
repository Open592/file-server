package com.open592.fileserver.net

import io.netty.util.concurrent.Future
import java.util.concurrent.CompletableFuture

fun <T> Future<T>.asCompletableFuture(): CompletableFuture<T> {
  if (isDone) {
    return if (isSuccess) {
      CompletableFuture.completedFuture(now)
    } else {
      CompletableFuture.failedFuture(cause())
    }
  }

  val future = CompletableFuture<T>()

  addListener {
    if (isSuccess) {
      future.complete(now)
    } else {
      future.completeExceptionally(cause())
    }
  }

  return future
}
