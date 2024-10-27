package com.open592.fileserver.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.handler.codec.ByteToMessageCodec

abstract class PacketCodec<T : Packet>(private val packet: T) : ByteToMessageCodec<T>() {
  init {
    packet.validateOpcode()
  }

  fun getOpcode(): Int {
    return packet.opcode
  }

  fun writeOutOpcode(out: ByteBuf) {
    out.writeByte(packet.opcode)
  }

  abstract fun allocateBuffer(alloc: ByteBufAllocator, input: T, preferDirect: Boolean): ByteBuf
}
