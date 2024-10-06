package com.open592.fileserver.js5

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.EncoderException
import io.netty.handler.codec.MessageToByteEncoder
import kotlin.math.min

@ChannelHandler.Sharable
object Js5ResponseEncoder : MessageToByteEncoder<Js5Response>(Js5Response::class.java) {
  override fun encode(ctx: ChannelHandlerContext, msg: Js5Response, out: ByteBuf) {
    when (msg) {
      is Js5Response.Ok -> {
        out.writeByte(0)
      }
      is Js5Response.Group -> encodeGroup(ctx, msg, out)
      is Js5Response.ClientOutOfDate -> {
        out.writeByte(6)
      }
      is Js5Response.ServerFull -> {
        out.writeByte(7)
      }
      is Js5Response.IpLimit -> {
        out.writeByte(9)
      }
      else -> throw EncoderException("Unimplemented")
    }
  }

  override fun allocateBuffer(
      ctx: ChannelHandlerContext,
      msg: Js5Response,
      preferDirect: Boolean
  ): ByteBuf {
    return when (msg) {
      is Js5Response.Group -> groupAllocateBuffer(ctx, msg, preferDirect)
      else -> simpleAllocateBuffer(ctx, msg, preferDirect)
    }
  }

  private fun encodeGroup(ctx: ChannelHandlerContext, msg: Js5Response.Group, out: ByteBuf) {
    out.writeByte(msg.archive)
    out.writeByte(msg.group)

    if (!msg.data.isReadable) {
      throw EncoderException("Missing compression byte")
    }

    var compression = msg.data.readUnsignedByte().toInt()

    if (msg.prefetch) {
      compression = compression or 0x80
    }

    out.writeByte(compression)

    out.writeBytes(msg.data, min(msg.data.readableBytes(), 508))

    while (msg.data.isReadable) {
      out.writeByte(0xFF)
      out.writeBytes(msg.data, min(msg.data.readableBytes(), 511))
    }
  }

  private fun groupAllocateBuffer(
      ctx: ChannelHandlerContext,
      msg: Js5Response.Group,
      preferDirect: Boolean
  ): ByteBuf {
    val dataLength = msg.data.readableBytes()

    // See:
    // https://github.com/openrs2/openrs2/blob/f809ba1929bb0bb65c909499aab37e92e07f0c6f/protocol/src/main/kotlin/org/openrs2/protocol/js5/downstream/Js5ResponseEncoder.kt#L38-L101
    val length = 2 + dataLength + (512 + dataLength) / 511

    return allocateBuffer(ctx, preferDirect, length)
  }

  private fun simpleAllocateBuffer(
      ctx: ChannelHandlerContext,
      msg: Js5Response,
      preferDirect: Boolean
  ): ByteBuf {
    return allocateBuffer(ctx, preferDirect, length = 4)
  }

  private fun allocateBuffer(
      ctx: ChannelHandlerContext,
      preferDirect: Boolean,
      length: Int
  ): ByteBuf {
    return if (preferDirect) {
      ctx.alloc().ioBuffer(length, length)
    } else {
      ctx.alloc().heapBuffer(length, length)
    }
  }
}
