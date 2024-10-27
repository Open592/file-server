package com.open592.fileserver.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator

abstract class FixedLengthPacketCodec<T : Packet>(packet: T, private val length: Int) :
    PacketCodec<T>(packet) {
  override fun allocateBuffer(alloc: ByteBufAllocator, input: T, preferDirect: Boolean): ByteBuf {
    val length = 1 + length

    return if (preferDirect) {
      alloc.ioBuffer(length, length)
    } else {
      alloc.heapBuffer(length, length)
    }
  }
}
