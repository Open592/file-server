package com.open592.fileserver.js5

import io.netty.buffer.ByteBuf

sealed class Js5Response {
  object Ok : Js5Response()

  object ClientOutOfDate : Js5Response()

  object ServerFull : Js5Response()

  object IpLimit : Js5Response()

  data class Group(val prefetch: Boolean, val archive: Int, val group: Int, val data: ByteBuf) :
      Js5Response()
}
