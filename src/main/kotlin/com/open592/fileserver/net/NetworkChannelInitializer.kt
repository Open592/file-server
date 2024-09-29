package com.open592.fileserver.net

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.timeout.IdleStateHandler
import jakarta.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class NetworkChannelInitializer : ChannelInitializer<Channel>() {
  override fun initChannel(channel: Channel) {
    channel
        .pipeline()
        .addLast(
            IdleStateHandler(
                true, TIMEOUT_SECONDS, TIMEOUT_SECONDS, TIMEOUT_SECONDS, TimeUnit.SECONDS))
  }

  private companion object {
    private const val TIMEOUT_SECONDS: Long = 30
  }
}
