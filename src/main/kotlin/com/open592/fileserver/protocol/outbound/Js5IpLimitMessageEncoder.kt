package com.open592.fileserver.protocol.outbound

import io.netty.channel.ChannelHandler

@ChannelHandler.Sharable
class Js5IpLimitMessageEncoder :
    SimpleOpcodeMessageToByteEncoder<Js5OutboundMessage.IpLimit>(
        opcode = 9, klass = Js5OutboundMessage.IpLimit::class.java)
