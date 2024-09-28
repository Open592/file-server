package com.open592.fileserver.configuration

import com.google.inject.AbstractModule
import java.nio.file.FileSystem
import java.nio.file.FileSystems

object ServerConfigurationModule : AbstractModule() {
  override fun configure() {
    bind(FileSystem::class.java).toInstance(FileSystems.getDefault())
  }
}
