package com.open592.fileserver.server

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import com.open592.fileserver.buffer.BufferModule
import com.open592.fileserver.configuration.ServerConfigurationModule
import com.open592.fileserver.net.NetworkService

object FileServerModule : AbstractModule() {
  override fun configure() {
    install(BufferModule)
    install(ServerConfigurationModule)

    val binder = Multibinder.newSetBinder(binder(), Service::class.java)
    binder.addBinding().to(NetworkService::class.java)
  }
}
