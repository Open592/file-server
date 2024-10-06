package com.open592.fileserver.js5

import com.open592.fileserver.configuration.ServerConfiguration
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import jakarta.inject.Inject

class Js5ChannelHandler @Inject constructor(
  private val serverConfiguration: ServerConfiguration
) : SimpleChannelInboundHandler<Js5Message>() {
  override fun channelActive(ctx: ChannelHandlerContext) {
    ctx.read()
  }

  override fun channelRead0(ctx: ChannelHandlerContext, message: Js5Message) {
    when (message) {
      is Js5Message.InitializeJs5RemoteConnection -> handleInitializeJs5RemoteConnection(ctx, message)
      else -> Unit
    }
  }

  private fun handleInitializeJs5RemoteConnection(ctx: ChannelHandlerContext, message: Js5Message.InitializeJs5RemoteConnection) {
    if (message.build != serverConfiguration.getBuildNumber()) {
      ctx.write(6)
    } else {
      ctx.write(0)
    }
  }
}
