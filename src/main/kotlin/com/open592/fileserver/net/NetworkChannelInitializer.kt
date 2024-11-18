package com.open592.fileserver.net

import com.github.michaelbull.logging.InlineLogger
import com.open592.fileserver.protocol.inbound.Js5InboundChannelHandler
import com.open592.fileserver.protocol.inbound.Js5InboundMessageDecoder
import com.open592.fileserver.protocol.outbound.Js5OutboundGroupMessageEncoder
import com.open592.fileserver.protocol.outbound.Js5OutboundStatusMessageEncoder
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.handler.timeout.IdleStateHandler
import jakarta.inject.Inject
import jakarta.inject.Provider
import jakarta.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class NetworkChannelInitializer
@Inject
constructor(private val js5InboundChannelHandler: Provider<Js5InboundChannelHandler>) :
    ChannelInitializer<Channel>() {
  override fun initChannel(channel: Channel) {
    channel
        .pipeline()
        .addLast(
            IdleStateHandler(
                true, TIMEOUT_SECONDS, TIMEOUT_SECONDS, TIMEOUT_SECONDS, TimeUnit.SECONDS),
            Js5InboundMessageDecoder(),
            Js5OutboundStatusMessageEncoder(),
            Js5OutboundGroupMessageEncoder(),
            js5InboundChannelHandler.get(),
        )
  }

  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    logger.error { "Caught exception: ${cause.message}" }
  }

  private companion object {
    private const val TIMEOUT_SECONDS: Long = 30
    private val logger = InlineLogger()
  }
}
