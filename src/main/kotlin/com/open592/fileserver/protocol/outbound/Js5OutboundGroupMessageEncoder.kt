package com.open592.fileserver.protocol.outbound

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.EncoderException
import io.netty.handler.codec.MessageToByteEncoder
import kotlin.math.min

@ChannelHandler.Sharable
class Js5OutboundGroupMessageEncoder :
    MessageToByteEncoder<Js5OutboundGroupMessage>(Js5OutboundGroupMessage::class.java) {
  override fun encode(
      ctx: ChannelHandlerContext,
      message: Js5OutboundGroupMessage,
      output: ByteBuf
  ) {
    output.writeByte(message.archive)
    output.writeShort(message.group)

    if (!message.data.isReadable) {
      throw EncoderException("Missing compression byte")
    }

    // Initialize flags with the compression byte from the data buffer
    var flags = message.data.readUnsignedByte().toInt()

    if (message.isPrefetch) {
      flags = flags or 0x80
    }

    output.writeByte(flags)

    output.writeBytes(message.data, min(message.data.readableBytes(), BLOCK_SIZE - HEADER_SIZE))

    while (message.data.isReadable) {
      output.writeByte(0xFF)
      output.writeBytes(message.data, min(message.data.readableBytes(), BLOCK_SIZE - 1))
    }
  }

  override fun allocateBuffer(
      ctx: ChannelHandlerContext,
      message: Js5OutboundGroupMessage,
      preferDirect: Boolean
  ): ByteBuf {
    val dataLength = message.data.readableBytes()
    val bufferLength = 2 + dataLength + (BLOCK_SIZE + dataLength) / 511

    return if (preferDirect) {
      ctx.alloc().ioBuffer(bufferLength, bufferLength)
    } else {
      ctx.alloc().heapBuffer(bufferLength, bufferLength)
    }
  }

  private companion object {
    private const val HEADER_SIZE = 4
    private const val BLOCK_SIZE = 512
  }
}
