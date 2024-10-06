package com.open592.fileserver.js5

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.DecoderException

class Js5RequestDecoder : ByteToMessageDecoder() {
  override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
    if (input.readableBytes() < 4) {
      return
    }

    val opcode = input.readUnsignedByte().toInt()

    out +=
        when (opcode) {
          0 -> decodeRequestGroupPacket(input, isPrefetch = true)
          1 -> decodeRequestGroupPacket(input, isPrefetch = false)
          2 -> decodeInformUserLoggedInPacket(input)
          3 -> decodeInformUserLoggedOutPacket(input)
          4 -> decodeExchangeObfuscationKeyPacket(input)
          6 -> decodeInformClientReadyPacket(input)
          7 -> decodeRequestConnectionDisconnectPacket(input)
          15 -> decodeInitializeJs5RemoteConnectionPacket(input)
          else -> throw DecoderException("Unknown Js5 opcode: $opcode")
        }
  }

  private fun decodeRequestGroupPacket(
      input: ByteBuf,
      isPrefetch: Boolean
  ): Js5Message.RequestGroup {
    val archive = input.readUnsignedByte().toInt()
    val group = input.readUnsignedShort().toInt()

    return Js5Message.RequestGroup(archive, group, isPrefetch)
  }

  private fun decodeInformUserLoggedInPacket(input: ByteBuf): Js5Message.InformUserLoggedIn {
    // Skip padding bytes
    input.skipBytes(3)

    return Js5Message.InformUserLoggedIn
  }

  private fun decodeInformUserLoggedOutPacket(input: ByteBuf): Js5Message.InformUserLoggedOut {
    // Skip padding bytes
    input.skipBytes(3)

    return Js5Message.InformUserLoggedOut
  }

  private fun decodeRequestConnectionDisconnectPacket(
      input: ByteBuf
  ): Js5Message.RequestConnectionDisconnect {
    // Skip padding bytes
    input.skipBytes(3)

    return Js5Message.RequestConnectionDisconnect
  }

  private fun decodeExchangeObfuscationKeyPacket(
      input: ByteBuf
  ): Js5Message.ExchangeObfuscationKey {
    val key = input.readUnsignedByte().toInt()

    // Skip padding bytes
    input.skipBytes(2)

    return Js5Message.ExchangeObfuscationKey(key)
  }

  private fun decodeInformClientReadyPacket(input: ByteBuf): Js5Message.InformClientReady {
    // Skip padding bytes
    // NOTE: The client usually sends along padding bytes with `0` as the value,
    // but for this message it's using `p3(3)`.
    input.skipBytes(3)

    return Js5Message.InformClientReady
  }

  private fun decodeInitializeJs5RemoteConnectionPacket(
      input: ByteBuf
  ): Js5Message.InitializeJs5RemoteConnection {
    val build = input.readInt()

    return Js5Message.InitializeJs5RemoteConnection(build)
  }
}
