package com.open592.fileserver.net.js5

import com.open592.fileserver.protocol.inbound.Js5InboundMessage
import io.netty.channel.ChannelHandlerContext

class Js5Client(val ctx: ChannelHandlerContext) {
  private val urgent = ArrayDeque<Js5InboundMessage.RequestGroup>()
  private val prefetch = ArrayDeque<Js5InboundMessage.RequestGroup>()

  fun push(request: Js5InboundMessage.RequestGroup) {
    if (request.isPrefetch) {
      prefetch += request
    } else {
      urgent += request
      prefetch -= request
    }
  }

  fun pop(): Js5InboundMessage.RequestGroup? {
    val request = urgent.removeFirstOrNull()

    if (request != null) {
      return request
    }

    return prefetch.removeFirstOrNull()
  }

  fun isNotFull(): Boolean {
    return urgent.size < MAX_QUEUE_SIZE && prefetch.size < MAX_QUEUE_SIZE
  }

  fun isNotEmpty(): Boolean {
    return urgent.isNotEmpty() || prefetch.isNotEmpty()
  }

  fun isReady(): Boolean {
    return ctx.channel().isWritable && isNotEmpty()
  }

  private companion object {
    private const val MAX_QUEUE_SIZE = 20
  }
}
