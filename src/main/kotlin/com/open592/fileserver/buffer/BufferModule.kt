package com.open592.fileserver.buffer

import com.google.inject.AbstractModule
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.PooledByteBufAllocator

object BufferModule : AbstractModule() {
  override fun configure() {
    bind(ByteBufAllocator::class.java).toInstance(PooledByteBufAllocator.DEFAULT)
  }
}
