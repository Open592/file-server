package com.open592.fileserver.server

import com.google.inject.AbstractModule
import com.open592.fileserver.configuration.ServerConfigurationModule

object FileServerModule : AbstractModule() {
  override fun configure() {
    install(ServerConfigurationModule)
  }
}
