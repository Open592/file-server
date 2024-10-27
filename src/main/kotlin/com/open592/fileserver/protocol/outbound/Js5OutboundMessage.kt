package com.open592.fileserver.protocol.outbound

import io.netty.buffer.ByteBuf

sealed class Js5OutboundMessage {
  object Ok : Js5OutboundMessage()

  object ClientIsOutOfDate : Js5OutboundMessage()

  object ServerIsFull : Js5OutboundMessage()

  object IpLimit : Js5OutboundMessage()

  data class Group(val isPrefetch: Boolean, val archive: Int, val group: Int, val data: ByteBuf) :
      Js5OutboundMessage()
}
