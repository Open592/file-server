package com.open592.fileserver.protocol.outbound

import io.netty.channel.ChannelHandler

@ChannelHandler.Sharable
class Js5ServerFullMessageEncoder :
    SimpleOpcodeMessageToByteEncoder<Js5OutboundMessage.ServerIsFull>(
        opcode = 7, klass = Js5OutboundMessage.ServerIsFull::class.java)
