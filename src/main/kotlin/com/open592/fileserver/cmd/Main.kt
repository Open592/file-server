package com.open592.fileserver.cmd

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.open592.fileserver.server.FileServer
import com.open592.fileserver.server.FileServerModule

object Main {
  private val logger = InlineLogger()

  @JvmStatic
  fun main(args: Array<String>) {
    val fileServer = Guice.createInjector(FileServerModule).getInstance(FileServer::class.java)

    fileServer.start(System.currentTimeMillis())
  }
}
