package com.open592.fileserver.protocol

import io.netty.buffer.ByteBuf

abstract class PacketDecoder<T : Packet>(val type: Class<T>, val opcode: Int) {
  init {
    require(opcode in 0 until 256)
  }

  abstract fun decode(input: ByteBuf): T
}
