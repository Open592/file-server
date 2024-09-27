package com.open592.fileserver.cmd

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.open592.fileserver.server.FileServer
import com.open592.fileserver.server.FileServerModule
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = FileServerCommand().main(args)

class FileServerCommand : CliktCommand() {
  override fun run() = runBlocking {
    logger.info { "Starting file server..." }

    val fileServer = Guice.createInjector(FileServerModule).getInstance(FileServer::class.java)

    fileServer.run(System.currentTimeMillis())
  }

  private companion object {
    private val logger = InlineLogger()
  }
}
