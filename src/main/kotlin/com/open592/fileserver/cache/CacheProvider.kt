package com.open592.fileserver.cache

import com.displee.cache.CacheLibrary
import com.open592.fileserver.configuration.ServerConfiguration
import jakarta.inject.Inject
import jakarta.inject.Provider

class CacheProvider @Inject constructor(private val serverConfiguration: ServerConfiguration) :
    Provider<CacheLibrary> {
  override fun get(): CacheLibrary {
    return CacheLibrary.create(serverConfiguration.getCachePath())
  }
}
