package com.open592.fileserver.protocol.outbound

import io.netty.channel.ChannelHandler

@ChannelHandler.Sharable
class Js5OkMessageEncoder :
    SimpleOpcodeMessageToByteEncoder<Js5OutboundMessage.Ok>(
        opcode = 0, klass = Js5OutboundMessage.Ok::class.java)
