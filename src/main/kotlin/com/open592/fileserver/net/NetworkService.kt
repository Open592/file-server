package com.open592.fileserver.net

import com.github.michaelbull.logging.InlineLogger
import com.google.common.util.concurrent.AbstractService
import com.open592.fileserver.configuration.ServerConfiguration
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
import java.util.concurrent.CompletableFuture

@Singleton
class NetworkService
@Inject
constructor(
    private val allocator: ByteBufAllocator,
    private val networkChannelInitializer: NetworkChannelInitializer,
    private val serverConfiguration: ServerConfiguration,
) : AbstractService() {
  private lateinit var eventLoopGroup: MultithreadEventLoopGroup

  override fun doStart() {
    val (group, serverSocketChannelClass) = resolveGroupAndChannel()

    eventLoopGroup = group

    val serverBootstrap =
        ServerBootstrap()
            .group(eventLoopGroup)
            .channel(serverSocketChannelClass)
            .childHandler(networkChannelInitializer)
            .childOption(ChannelOption.ALLOCATOR, allocator)
            .childOption(ChannelOption.AUTO_READ, false)
            .childOption(ChannelOption.TCP_NODELAY, true)

    val primaryPort = serverConfiguration.getPrimaryPort()
    val secondaryPort = serverConfiguration.getSecondaryPort()

    logger.info { "Binding to primary port: $primaryPort" }

    val primaryFuture =
        serverBootstrap.bind(serverConfiguration.getPrimaryPort()).asCompletableFuture()

    logger.info { "Binding to secondary port: $secondaryPort" }

    val secondaryFuture =
        serverBootstrap.bind(serverConfiguration.getSecondaryPort()).asCompletableFuture()

    CompletableFuture.allOf(primaryFuture, secondaryFuture).handle { _, ex ->
      if (ex != null) {
        group.shutdownGracefully()
        notifyFailed(ex)
      } else {
        notifyStarted()
      }
    }
  }

  override fun doStop() {
    logger.info { "Shutting down network service." }

    eventLoopGroup.shutdownGracefully().addListener { future ->
      if (future.isSuccess) {
        notifyStopped()
      } else {
        notifyFailed(future.cause())
      }
    }
  }

  private fun resolveGroupAndChannel():
      Pair<MultithreadEventLoopGroup, Class<out ServerSocketChannel>> {
    return when {
      Epoll.isAvailable() -> Pair(EpollEventLoopGroup(), EpollServerSocketChannel::class.java)
      KQueue.isAvailable() -> Pair(KQueueEventLoopGroup(), KQueueServerSocketChannel::class.java)
      else -> Pair(NioEventLoopGroup(), NioServerSocketChannel::class.java)
    }
  }

  private companion object {
    private val logger = InlineLogger()
  }
}
