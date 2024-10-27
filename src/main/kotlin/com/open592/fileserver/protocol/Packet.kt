package com.open592.fileserver.protocol

interface Packet {
  val opcode: Int

  fun validateOpcode() {
    require(opcode in MIN_OPCODE..MAX_OPCODE)
  }

  companion object {
    private const val MIN_OPCODE = 0
    private const val MAX_OPCODE = 255
  }
}
