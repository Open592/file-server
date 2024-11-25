package com.open592.fileserver.net.js5

import com.displee.cache.CacheLibrary
import com.displee.compress.CompressionType
import com.displee.compress.compress
import com.displee.compress.type.EmptyCompressor
import com.github.michaelbull.logging.InlineLogger
import com.google.common.util.concurrent.AbstractExecutionThreadService
import com.open592.fileserver.buffer.use
import com.open592.fileserver.collections.UniqueQueue
import com.open592.fileserver.protocol.inbound.Js5InboundMessage
import com.open592.fileserver.protocol.outbound.Js5OutboundGroupMessage
import io.netty.buffer.ByteBufAllocator
import jakarta.inject.Inject
import jakarta.inject.Singleton

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
      var request: Js5InboundMessage.RequestGroup

      synchronized(lock) {
        while (true) {
          if (!isRunning) {
            return
          }

          val next = clients.removeFirstOrNull()

          if (next == null) {
            lock.wait()
            continue
          }

          client = next
          request = client.pop() ?: continue

          break
        }
      }

      serve(client, request)
    }
  }

  private fun serve(client: Js5Client, request: Js5InboundMessage.RequestGroup) {
    val ctx = client.ctx

    if (!ctx.channel().isActive) {
      return
    }

    val buf =
        if (request.archive == ARCHIVE_SET && request.group == ARCHIVE_SET) {
          allocator.buffer().use { buffer ->
            val masterIndex =
                cacheLibrary.generateUkeys(false).compress(CompressionType.NONE, EmptyCompressor)

            buffer.writeBytes(masterIndex)
            buffer.retain()
          }
        } else {
          allocator.buffer().use { buffer ->
            val archiveSector =
                if (request.group == 255) {
                  cacheLibrary.index255?.readArchiveSector(request.archive)
                } else {
                  cacheLibrary.index(request.group).readArchiveSector(request.archive)
                } ?: return

            buffer.writeBytes(archiveSector.data.compress(CompressionType.NONE, EmptyCompressor))
            buffer.retain()
          }
        }

    val response =
        Js5OutboundGroupMessage(request.isPrefetch, request.archive, request.group, data = buf)

    ctx.writeAndFlush(response, ctx.voidPromise())

    synchronized(lock) {
      if (client.isReady()) {
        clients.add(client)
      }

      if (client.isNotFull()) {
        ctx.read()
      }
    }
  }

  fun push(client: Js5Client, request: Js5InboundMessage.RequestGroup) {
    synchronized(lock) {
      client.push(request)

      if (client.isReady()) {
        clients.add(client)

        lock.notifyAll()
      }

      if (client.isNotFull()) {
        client.ctx.read()
      }
    }
  }

  fun readIfNotFull(client: Js5Client) {
    synchronized(lock) {
      if (client.isNotFull()) {
        client.ctx.read()
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

  override fun triggerShutdown() {
    synchronized(lock) { lock.notifyAll() }
  }

  private companion object {
    private const val ARCHIVE_SET = 255
    private val logger = InlineLogger()
  }
}
