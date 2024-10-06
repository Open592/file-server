package com.open592.fileserver.js5

import com.google.common.util.concurrent.AbstractExecutionThreadService
import com.open592.fileserver.collections.UniqueQueue
import io.netty.buffer.ByteBufAllocator
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class Js5Service @Inject constructor(
  private val allocator: ByteBufAllocator
): AbstractExecutionThreadService() {
  private val lock = Object()
  private val clients = UniqueQueue<Js5Client>()

  override fun run() {
    while (true) {
      var client: Js5Client
      var request: Js5Message.RequestGroup

      synchronized(lock) {
        while (isRunning) {
          val nextClient = clients.removeFirstOrNull()

          if (nextClient == null) {
            lock.wait()

            continue
          }

          client = nextClient
          request = nextClient.pop() ?: continue

          break
        }
      }
    }
  }

  fun serve(client: Js5Client, request: Js5Message.RequestGroup) {
    val context = client.getContext()

    if (!context.channel().isActive) {
      return
    }


  }

  override fun shutDown() {
  }
}
