package com.open592.fileserver.js5

import com.displee.cache.CacheLibrary
import com.github.michaelbull.logging.InlineLogger
import com.google.common.util.concurrent.AbstractExecutionThreadService
import com.open592.fileserver.buffer.use
import com.open592.fileserver.collections.UniqueQueue
import io.netty.buffer.ByteBufAllocator
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.math.log

@Singleton
class Js5Service
@Inject
constructor(
    private val allocator: ByteBufAllocator,
    private val cacheLibrary: CacheLibrary,
) : AbstractExecutionThreadService() {
  private val lock = Object()
  private val clients = UniqueQueue<Js5Client>()

  override fun run() {
    while (true) {
      var client: Js5Client
      var request: Js5Message.RequestGroup

      synchronized(lock) {
        while (true) {
          if (!isRunning) {
            return
          }

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

      serve(client, request)
    }
  }

  override fun triggerShutdown() {
    synchronized(lock) { lock.notifyAll() }
  }

  private fun serve(client: Js5Client, request: Js5Message.RequestGroup) {
    val context = client.getContext()

    if (!context.channel().isActive) {
      return
    }

    logger.info { "Processing request - Group: ${request.group} :: Archive: ${request.archive}" }

    val buf =
        if (request.archive == ARCHIVE_SET && request.group == ARCHIVE_SET) {
          allocator.buffer().use { buffer ->
            val masterIndex = cacheLibrary.generateUkeys(false)

            buffer.writeBytes(masterIndex)
          }
        } else {
          try {
            val data = cacheLibrary.data(request.archive, request.group)

            allocator.buffer().use { buffer ->
              buffer.writeBytes(data)
            }
          } catch (_: Exception) {
            context.close()

            return
          }
        }

    val response = Js5Response.Group(request.isPrefetch, request.archive, request.group, buf)

    context.writeAndFlush(response, context.voidPromise())

    synchronized(lock) {
      client.push(request)

      if (client.isReady()) {
        clients.add(client)

        lock.notifyAll()
      }

      if (client.isNotFull()) {
        client.getContext().read()
      }
    }
  }

  fun push(client: Js5Client, request: Js5Message.RequestGroup) {
    synchronized(lock) {
      client.push(request)

      if (client.isReady()) {
        clients.add(client)

        lock.notifyAll()
      }

      if (client.isNotFull()) {
        client.getContext().read()
      }
    }
  }

  fun readIfNotFull(client: Js5Client) {
    synchronized(lock) {
      if (client.isNotFull()) {
        client.getContext().read()
      }
    }
  }

  fun notifyIfNotEmpty(client: Js5Client) {
    synchronized(lock) {
      if (client.isNotEmpty()) {
        lock.notifyAll()
      }
    }
  }

  private companion object {
    private val logger = InlineLogger()
    private const val ARCHIVE_SET: Int = 255
  }
}
