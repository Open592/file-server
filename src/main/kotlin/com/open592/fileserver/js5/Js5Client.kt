package com.open592.fileserver.js5

import io.netty.channel.ChannelHandlerContext

class Js5Client(private val context: ChannelHandlerContext) {
  private val urgentRequests = ArrayDeque<Js5Message.RequestGroup>()
  private val prefetchRequests = ArrayDeque<Js5Message.RequestGroup>()

  fun getContext(): ChannelHandlerContext {
    return context
  }

  fun push(request: Js5Message.RequestGroup) {
    if (request.isPrefetch) {
      prefetchRequests += request
    } else {
      prefetchRequests -= request
      urgentRequests += request
    }
  }

  fun pop(): Js5Message.RequestGroup? {
    val request = urgentRequests.removeFirstOrNull()

    if (request != null) {
      return request
    }

    return prefetchRequests.removeFirstOrNull()
  }

  fun isNotFull(): Boolean {
    return urgentRequests.size < MAX_QUEUE_SIZE && prefetchRequests.size < MAX_QUEUE_SIZE
  }

  fun isNotEmpty(): Boolean {
    return urgentRequests.isNotEmpty() || prefetchRequests.isNotEmpty()
  }

  fun isReady(): Boolean {
    val isNotEmpty = isNotEmpty()
    val isWriteable = context.channel().isWritable

    return isNotEmpty && isWriteable
  }

  private companion object {
    private const val MAX_QUEUE_SIZE: Int = 20
  }
}
