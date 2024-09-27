package com.open592.fileserver.server

import com.github.michaelbull.logging.InlineLogger
import com.google.common.util.concurrent.Service
import com.google.common.util.concurrent.ServiceManager
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FileServer @Inject constructor(
    services: Set<Service>
) {
  private val serviceManager = ServiceManager(services)

  fun run(startMillis: Long) {
    val shutdownHook = Thread(::stop, "FileServerShutdownHook")

    serviceManager.startAsync()
    runtime.addShutdownHook(shutdownHook)

    try {
      serviceManager.awaitHealthy()
    } catch (e: Throwable) {
      serviceManager.stopAsync().awaitStopped()
      runtime.removeShutdownHook(shutdownHook)

      throw e
    }

    val elapsedMillis = System.currentTimeMillis() - startMillis

    logger.info { "Started file server in $elapsedMillis milliseconds." }

    serviceManager.awaitStopped()
    runtime.removeShutdownHook(shutdownHook)
  }

  private fun stop() {
    logger.info { "Stopping file server" }

    serviceManager.stopAsync().awaitStopped()

    logger.info { "Stopped file server" }
  }

  private companion object {
    private val logger = InlineLogger()
    private val runtime = Runtime.getRuntime()
  }
}
