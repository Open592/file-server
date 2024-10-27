package com.open592.fileserver.protocol

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

abstract class EmptyPacketCodec<T : Packet>(private val packet: T) :
    FixedLengthPacketCodec<T>(packet, length = 0) {
  override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
    out += packet
  }

  override fun encode(ctx: ChannelHandlerContext, input: T, out: ByteBuf) {
    // Empty
  }
}
