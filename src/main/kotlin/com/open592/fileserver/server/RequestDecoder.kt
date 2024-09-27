package com.open592.fileserver.server

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class RequestDecoder : ByteToMessageDecoder() {
  override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: List<Any>) {
  }
}
