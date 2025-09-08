package com.open592.fileserver.net

import com.github.michaelbull.logging.InlineLogger
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class ExceptionHandler : ChannelInboundHandlerAdapter() {

  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    logger.error { "Caught exception: ${cause.message}" }
  }

  private companion object {
    private val logger = InlineLogger()
  }
}
