package com.open592.fileserver.protocol.inbound

import com.open592.fileserver.protocol.FixedLengthPacketCodec
import com.open592.fileserver.protocol.Packet
import com.open592.fileserver.protocol.inbound.InitJs5RemoteConnectionCodec.Companion.InitJs5RemoteConnection
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import jakarta.inject.Singleton

@Singleton
class InitJs5RemoteConnectionCodec :
    FixedLengthPacketCodec<InitJs5RemoteConnection>(InitJs5RemoteConnection(), length = 4) {
  override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, output: MutableList<Any>) {
    val build = input.readInt()

    output += InitJs5RemoteConnection()
  }

  companion object {
    data class InitJs5RemoteConnection(override val opcode: Int = 15) : Packet
  }
}
