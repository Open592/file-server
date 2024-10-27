package com.open592.fileserver.protocol.outbound

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

abstract class SimpleOpcodeMessageToByteEncoder<T : Js5OutboundMessage>(
    protected val opcode: Int,
    klass: Class<T>
) : MessageToByteEncoder<T>(klass) {
  override fun encode(ctx: ChannelHandlerContext, message: T, output: ByteBuf) {
    output.writeByte(opcode)
  }

  override fun allocateBuffer(ctx: ChannelHandlerContext, msg: T, preferDirect: Boolean): ByteBuf {
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
