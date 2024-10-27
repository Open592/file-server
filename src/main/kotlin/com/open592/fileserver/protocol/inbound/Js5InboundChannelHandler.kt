package com.open592.fileserver.protocol.inbound

import com.github.michaelbull.logging.InlineLogger
import com.open592.fileserver.configuration.ServerConfiguration
import com.open592.fileserver.net.js5.Js5Client
import com.open592.fileserver.net.js5.Js5Service
import com.open592.fileserver.protocol.outbound.Js5OutboundMessage
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent
import jakarta.inject.Inject

class Js5InboundChannelHandler
@Inject
constructor(
    private val service: Js5Service,
    private val serverConfiguration: ServerConfiguration,
) : SimpleChannelInboundHandler<Js5InboundMessage>(Js5InboundMessage::class.java) {
  private lateinit var client: Js5Client

  override fun handlerAdded(ctx: ChannelHandlerContext) {
    client = Js5Client(ctx.read())
  }

  override fun channelRead0(ctx: ChannelHandlerContext, message: Js5InboundMessage) {
    when (message) {
      is Js5InboundMessage.InitializeJs5RemoteConnection ->
          handleInitializeJs5RemoteConnection(ctx, message)
      is Js5InboundMessage.RequestGroup -> service.push(client, message)
      is Js5InboundMessage.ExchangeObfuscationKey -> handleExchangeObfuscationKey(message)
      is Js5InboundMessage.RequestConnectionDisconnect -> ctx.close()
      else -> Unit
    }
  }

  private fun handleInitializeJs5RemoteConnection(
      ctx: ChannelHandlerContext,
      message: Js5InboundMessage.InitializeJs5RemoteConnection
  ) {
    if (message.build != serverConfiguration.getBuildNumber()) {
      ctx.write(Js5OutboundMessage.ClientIsOutOfDate)
    } else {
      ctx.write(Js5OutboundMessage.Ok)
    }
  }

  private fun handleExchangeObfuscationKey(message: Js5InboundMessage.ExchangeObfuscationKey) {
    logger.info { "Handle Exchange Obfuscation Key with value = ${message.key}" }
  }

  override fun channelReadComplete(ctx: ChannelHandlerContext) {
    service.readIfNotFull(client)
    ctx.flush()
  }

  override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
    if (ctx.channel().isWritable) {
      service.notifyIfNotEmpty(client)
    }
  }

  override fun userEventTriggered(ctx: ChannelHandlerContext, event: Any) {
    if (event is IdleStateEvent) {
      ctx.close()
    }
  }

  private companion object {
    private val logger = InlineLogger()
  }
}
