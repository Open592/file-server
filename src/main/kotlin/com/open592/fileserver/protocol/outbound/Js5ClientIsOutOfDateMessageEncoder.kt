package com.open592.fileserver.protocol.outbound

import io.netty.channel.ChannelHandler

@ChannelHandler.Sharable
class Js5ClientIsOutOfDateMessageEncoder :
    SimpleOpcodeMessageToByteEncoder<Js5OutboundMessage.ClientIsOutOfDate>(
        opcode = 6,
        klass = Js5OutboundMessage.ClientIsOutOfDate::class.java,
    )
