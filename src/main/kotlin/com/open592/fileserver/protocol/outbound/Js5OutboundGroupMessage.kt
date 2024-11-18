package com.open592.fileserver.protocol.outbound

import io.netty.buffer.ByteBuf

data class Js5OutboundGroupMessage(
    val isPrefetch: Boolean,
    val archive: Int,
    val group: Int,
    val data: ByteBuf
) : Js5OutboundMessage
