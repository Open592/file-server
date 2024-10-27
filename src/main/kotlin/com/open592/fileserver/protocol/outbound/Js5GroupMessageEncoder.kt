package com.open592.fileserver.protocol.outbound

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.EncoderException
import io.netty.handler.codec.MessageToByteEncoder
import kotlin.math.min

@ChannelHandler.Sharable
class Js5GroupMessageEncoder :
    MessageToByteEncoder<Js5OutboundMessage.Group>(Js5OutboundMessage.Group::class.java) {
  override fun encode(
      ctx: ChannelHandlerContext,
      message: Js5OutboundMessage.Group,
      output: ByteBuf
  ) {
    output.writeByte(message.archive)
    output.writeShort(message.group)

    if (!message.data.isReadable) {
      throw EncoderException("Missing compression byte")
    }

    var compression = message.data.readUnsignedByte().toInt()

    if (message.isPrefetch) {
      compression = compression or 0x80
    }

    output.writeByte(compression)

    output.writeBytes(message.data, min(message.data.readableBytes(), 508))

    while (message.data.isReadable) {
      output.writeByte(0xFF)
      output.writeBytes(message.data, min(message.data.readableBytes(), 511))
    }
  }

  override fun allocateBuffer(
      ctx: ChannelHandlerContext,
      message: Js5OutboundMessage.Group,
      preferDirect: Boolean
  ): ByteBuf {
    val dataLength = message.data.readableBytes()
    val bufferLength = 2 + dataLength + (512 + dataLength) / 511

    return if (preferDirect) {
      ctx.alloc().ioBuffer(bufferLength, bufferLength)
    } else {
      ctx.alloc().heapBuffer(bufferLength, bufferLength)
    }
  }
}
