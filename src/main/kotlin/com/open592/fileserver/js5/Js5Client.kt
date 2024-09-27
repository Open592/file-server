package com.open592.fileserver.js5

import io.netty.channel.ChannelHandlerContext

class Js5Client (
    private val ctx: ChannelHandlerContext
) {
  private val urgent = ArrayDeque<String>()
  private val prefetch = ArrayDeque<String>()
}
