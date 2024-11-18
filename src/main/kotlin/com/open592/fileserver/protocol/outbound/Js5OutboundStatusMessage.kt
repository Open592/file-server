package com.open592.fileserver.protocol.outbound

sealed class Js5OutboundStatusMessage(val opcode: Int) : Js5OutboundMessage {
  object Ok : Js5OutboundStatusMessage(opcode = 0)

  object ClientIsOutOfDate : Js5OutboundStatusMessage(opcode = 6)

  object ServerIsFull : Js5OutboundStatusMessage(opcode = 7)

  object IpIsLimited : Js5OutboundStatusMessage(opcode = 9)
}
