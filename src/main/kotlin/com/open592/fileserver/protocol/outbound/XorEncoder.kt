package com.open592.fileserver.protocol.outbound

import com.open592.fileserver.buffer.xor
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder

class XorEncoder : MessageToMessageEncoder<ByteBuf>(ByteBuf::class.java) {
  var key: Int = 0

  override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
    out += msg.xor(key)
  }
}
