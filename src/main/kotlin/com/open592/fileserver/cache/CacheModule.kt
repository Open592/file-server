package com.open592.fileserver.cache

import com.displee.cache.CacheLibrary
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.open592.fileserver.configuration.ServerConfigurationModule

object CacheModule : AbstractModule() {
  override fun configure() {
    install(ServerConfigurationModule)

    bind(CacheLibrary::class.java).toProvider(CacheProvider::class.java).`in`(Scopes.SINGLETON)
  }
}
