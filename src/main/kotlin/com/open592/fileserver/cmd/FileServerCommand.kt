package com.open592.fileserver.cmd

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = FileServerCommand().main(args)

class FileServerCommand : CliktCommand() {
  override fun run() = runBlocking { logger.info { "Hello world!" } }

  private companion object {
    private val logger = InlineLogger()
  }
}
