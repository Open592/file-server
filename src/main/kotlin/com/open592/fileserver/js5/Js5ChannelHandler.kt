package com.open592.fileserver.js5

import com.open592.fileserver.configuration.ServerConfiguration
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateHandler
import jakarta.inject.Inject

class Js5ChannelHandler
@Inject
constructor(
    private val js5Service: Js5Service,
    private val serverConfiguration: ServerConfiguration,
) : SimpleChannelInboundHandler<Js5Message>(Js5Message::class.java) {
  private lateinit var client: Js5Client

  override fun channelActive(ctx: ChannelHandlerContext) {
    ctx.read()
  }

  override fun handlerAdded(ctx: ChannelHandlerContext) {
    client = Js5Client(ctx.read())
  }

  override fun channelRead0(ctx: ChannelHandlerContext, message: Js5Message) {
    when (message) {
      is Js5Message.RequestGroup -> js5Service.push(client, message)
      is Js5Message.InitializeJs5RemoteConnection ->
          handleInitializeJs5RemoteConnection(ctx, message)
      is Js5Message.InformClientReady -> handleInformClientReady(ctx)
      is Js5Message.RequestConnectionDisconnect -> handleRequestConnectionDisconnect(ctx)
      else -> Unit
    }
  }

  private fun handleInitializeJs5RemoteConnection(
      ctx: ChannelHandlerContext,
      message: Js5Message.InitializeJs5RemoteConnection
  ) {
    if (message.build != serverConfiguration.getBuildNumber()) {
      ctx.write(Js5Response.ClientOutOfDate)
    } else {
      ctx.write(Js5Response.Ok)
    }
  }

  private fun handleInformClientReady(ctx: ChannelHandlerContext) {
    println("Hello world")
  }

  private fun handleRequestConnectionDisconnect(ctx: ChannelHandlerContext) {
    ctx.close()
  }

  override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
    if (ctx.channel().isWritable) {
      js5Service.notifyIfNotEmpty(client)
    }
  }

  override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
    if (evt is IdleStateHandler) {
      ctx.close()
    }
  }

  override fun channelReadComplete(ctx: ChannelHandlerContext) {
    js5Service.readIfNotFull(client)
    ctx.flush()
  }
}
