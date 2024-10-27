package com.open592.fileserver.protocol

import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class Protocol @Inject constructor(codecs: Set<PacketCodec<*>>) {
  private val registry = mutableMapOf<Int, PacketCodec<out Packet>>()

  init {
    for (codec in codecs) {
      registry[codec.getOpcode()] = codec
    }
  }

  fun getDecoder(opcode: Int): PacketCodec<out Packet>? {
    return registry[opcode]
  }

  fun <T : Packet> getEncoder(packet: T): PacketCodec<out Packet>? {
    return registry[packet.opcode]
  }
}
