package com.open592.fileserver.protocol.inbound

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.DecoderException

class Js5InboundMessageDecoder : ByteToMessageDecoder() {
  override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, output: MutableList<Any>) {
    if (input.readableBytes() < 4) {
      return
    }

    val opcode = input.readUnsignedByte().toInt()
    val message = decodeOpcode(input, opcode)

    output += message
  }

  private fun decodeOpcode(input: ByteBuf, opcode: Int): Js5InboundMessage {
    return when (opcode) {
      0 -> decodeRequestGroupPacket(input, isPrefetch = true)
      1 -> decodeRequestGroupPacket(input, isPrefetch = false)
      2 -> decodeInformUserIsLoggedInPacket(input)
      3 -> decodeInformUserIsLoggedOutPacket(input)
      4 -> decodeExchangeObfuscationKeyPacket(input)
      6 -> decodeInformClientIsReadyPacket(input)
      7 -> decodeRequestConnectionDisconnectPacket(input)
      15 -> decodeInitializeJs5RemoteConnectionPacket(input)
      else -> throw DecoderException("Unknown Js5 inbound message opcode: $opcode")
    }
  }

  private fun decodeRequestGroupPacket(
      input: ByteBuf,
      isPrefetch: Boolean
  ): Js5InboundMessage.RequestGroup {
    val archive = input.readUnsignedByte().toInt()
    val group = input.readUnsignedShort().toInt()

    return Js5InboundMessage.RequestGroup(archive, group, isPrefetch)
  }

  private fun decodeInformUserIsLoggedInPacket(
      input: ByteBuf
  ): Js5InboundMessage.InformUserIsLoggedIn {
    // Skip padding bytes
    input.skipBytes(3)

    return Js5InboundMessage.InformUserIsLoggedIn
  }

  private fun decodeInformUserIsLoggedOutPacket(
      input: ByteBuf
  ): Js5InboundMessage.InformUserIsLoggedOut {
    // Skip padding bytes
    input.skipBytes(3)

    return Js5InboundMessage.InformUserIsLoggedOut
  }

  private fun decodeRequestConnectionDisconnectPacket(
      input: ByteBuf
  ): Js5InboundMessage.RequestConnectionDisconnect {
    // Skip padding bytes
    input.skipBytes(3)

    return Js5InboundMessage.RequestConnectionDisconnect
  }

  private fun decodeExchangeObfuscationKeyPacket(
      input: ByteBuf
  ): Js5InboundMessage.ExchangeObfuscationKey {
    val key = input.readUnsignedByte().toInt()

    return Js5InboundMessage.ExchangeObfuscationKey(key)
  }

  private fun decodeInformClientIsReadyPacket(
      input: ByteBuf
  ): Js5InboundMessage.InformClientIsReady {
    // Skip padding bytes
    // NOTE: The client usually sends along padding bytes with `0` as the value,
    // but for this message it's using `p3(3)`.
    input.skipBytes(3)

    return Js5InboundMessage.InformClientIsReady
  }

  private fun decodeInitializeJs5RemoteConnectionPacket(
      input: ByteBuf
  ): Js5InboundMessage.InitializeJs5RemoteConnection {
    val build = input.readInt()

    return Js5InboundMessage.InitializeJs5RemoteConnection(build)
  }
}
