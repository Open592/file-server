package com.open592.fileserver.server

import com.google.common.util.concurrent.AbstractService
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelOption
import io.netty.channel.MultithreadEventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class NetworkService @Inject constructor(
    private val allocator: ByteBufAllocator
) : AbstractService() {
  private lateinit var eventLoopGroup: MultithreadEventLoopGroup

  override fun doStart() {
    val (group, channelClass) = resolveGroupAndChannel()

    eventLoopGroup = group

    val primaryServerBootstrap = ServerBootstrap()
        .group(group)
        .channel(channelClass)
        .option(ChannelOption.ALLOCATOR, allocator)
        .childOption(ChannelOption.ALLOCATOR, allocator)
        .childOption(ChannelOption.AUTO_READ, false)
        .childOption(ChannelOption.TCP_NODELAY, true)

    val secondaryServerBootstrap = ServerBootstrap()
        .group(group)
        .channel(channelClass)
        .option(ChannelOption.ALLOCATOR, allocator)
        .childOption(ChannelOption.ALLOCATOR, allocator)
        .childOption(ChannelOption.AUTO_READ, false)
        .childOption(ChannelOption.TCP_NODELAY, true)


    primaryServerBootstrap.bind(PRIMARY_PORT)
    secondaryServerBootstrap.bind(SECONDARY_PORT)
  }

  override fun doStop() {
    eventLoopGroup.shutdownGracefully().addListener { future ->
      if (future.isSuccess) {
        notifyStopped()
      } else {
        notifyFailed(future.cause())
      }
    }
  }

  private fun resolveGroupAndChannel(): Pair<MultithreadEventLoopGroup, Class<out ServerSocketChannel>> {
    return when {
      Epoll.isAvailable() -> Pair(EpollEventLoopGroup(), EpollServerSocketChannel::class.java)
      KQueue.isAvailable() -> Pair(KQueueEventLoopGroup(), KQueueServerSocketChannel::class.java)
      else -> Pair(NioEventLoopGroup(), NioServerSocketChannel::class.java)
    }
  }

  private companion object {
    private const val PRIMARY_PORT = 43594
    private const val SECONDARY_PORT = 443
  }
}
