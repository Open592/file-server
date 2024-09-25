package com.open592.fileserver.cmd

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main

fun main(args: Array<String>): Unit = FileServerCommand().main(args)

class FileServerCommand : CliktCommand() {
  override fun run() {
    echo("Hello world!")
  }
}
