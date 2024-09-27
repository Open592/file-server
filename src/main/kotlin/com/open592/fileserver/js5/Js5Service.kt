package com.open592.fileserver.js5

import com.google.common.util.concurrent.AbstractExecutionThreadService
import com.open592.fileserver.collections.UniqueQueue
import jakarta.inject.Singleton

@Singleton
class Js5Service : AbstractExecutionThreadService() {
  private val lock = Object()
  private val clients = UniqueQueue<Js5Client>()

  override fun run() {
    while (true) {
      var client: Js5Client

      synchronized(lock) {
        while (true) {
          val next = clients.removeFirstOrNull()

          if (next == null) {
            lock.wait()

            continue
          }

          client = next

          break
        }
      }

      serve(client)
    }
  }

  private fun getNextClient() {

  }

  private fun serve(client: Js5Client) {
  }
}
