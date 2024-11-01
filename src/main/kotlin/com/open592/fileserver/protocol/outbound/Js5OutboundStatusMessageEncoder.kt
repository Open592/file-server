package com.open592.fileserver.protocol.outbound

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class Js5OutboundStatusMessageEncoder :
    MessageToByteEncoder<Js5OutboundStatusMessage>(Js5OutboundStatusMessage::class.java) {
  override fun encode(
      ctx: ChannelHandlerContext,
      message: Js5OutboundStatusMessage,
      output: ByteBuf
  ) {
    output.writeByte(message.opcode)
  }

  override fun allocateBuffer(
      ctx: ChannelHandlerContext,
      msg: Js5OutboundStatusMessage,
      preferDirect: Boolean
  ): ByteBuf {
    return if (preferDirect) {
      ctx.alloc().ioBuffer(BUFFER_LENGTH, BUFFER_LENGTH)
    } else {
      ctx.alloc().heapBuffer(BUFFER_LENGTH, BUFFER_LENGTH)
    }
  }

  private companion object {
    private const val BUFFER_LENGTH = 4
  }
}
