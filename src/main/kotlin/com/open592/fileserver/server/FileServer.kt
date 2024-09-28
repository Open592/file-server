package com.open592.fileserver.server

import com.github.michaelbull.logging.InlineLogger
import com.open592.fileserver.configuration.ServerConfiguration
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FileServer @Inject constructor(private val serverConfiguration: ServerConfiguration) {
  fun start() {
    logger.info {
      "Starting server on ports ${serverConfiguration.getPrimaryPort()} / ${serverConfiguration.getSecondaryPort()}..."
    }
  }

  private companion object {
    private val logger = InlineLogger()
  }
}
