package com.open592.fileserver.net

import com.open592.fileserver.js5.Js5ChannelHandler
import com.open592.fileserver.js5.Js5RequestDecoder
import com.open592.fileserver.js5.Js5ResponseEncoder
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.timeout.IdleStateHandler
import jakarta.inject.Inject
import jakarta.inject.Provider
import jakarta.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class NetworkChannelInitializer
@Inject
constructor(
    private val handlerProvider: Provider<Js5ChannelHandler>,
) : ChannelInitializer<Channel>() {
  override fun initChannel(channel: Channel) {
    channel
        .pipeline()
        .addLast(
            IdleStateHandler(
                true, TIMEOUT_SECONDS, TIMEOUT_SECONDS, TIMEOUT_SECONDS, TimeUnit.SECONDS),
            Js5RequestDecoder(),
            Js5ResponseEncoder,
            handlerProvider.get(),
        )
  }

  private companion object {
    private const val TIMEOUT_SECONDS: Long = 30
  }
}
