package com.open592.fileserver.configuration

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.NoSuchFileException

@Singleton
class ServerConfiguration @Inject constructor(private val fileSystem: FileSystem) {
  private enum class ConfigurationKeys(val value: String) {
    CACHE_PATH("cachePath"),
    PRIMARY_PORT("primaryPort"),
    SECONDARY_PORT("secondaryPort"),
    BUILD_NUMBER("buildNumber"),
  }

  private data class Configuration(
      var cachePath: String = "etc/cache",
      var primaryPort: Int = 43594,
      var secondaryPort: Int = 443,
      var buildNumber: Int = 592,
  )

  private val configuration = Configuration()

  init {
    try {
      resolveConfigurationFile()
    } catch (_: NoSuchFileException) {
      logger.info { "No configuration file found. Proceeding with defaults." }
    } catch (_: Throwable) {
      logger.error { "Failed to open configuration file. Some values may be their defaults." }
    }
  }

  fun getCachePath(): String {
    return configuration.cachePath
  }

  fun getPrimaryPort(): Int {
    return configuration.primaryPort
  }

  fun getSecondaryPort(): Int {
    return configuration.secondaryPort
  }

  fun getBuildNumber(): Int {
    return configuration.buildNumber
  }

  private fun resolveConfigurationFile() {
    Files.newBufferedReader(fileSystem.getPath(DEFAULT_CONFIGURATION_PATH)).forEachLine { line ->
      val (key, value) =
          line.takeIf { !it.startsWith("#") }?.split("=", limit = 2)?.takeIf { it.size == 2 }
              ?: return@forEachLine

      val configurationKey =
          ConfigurationKeys.entries.firstOrNull { it.value == key } ?: return@forEachLine

      when (configurationKey) {
        ConfigurationKeys.CACHE_PATH -> setCachePathFromConfiguration(value)
        ConfigurationKeys.PRIMARY_PORT -> setPrimaryPortFromConfiguration(value)
        ConfigurationKeys.SECONDARY_PORT -> setSecondaryPortFromConfiguration(value)
        ConfigurationKeys.BUILD_NUMBER -> setBuildNumberFromConfiguration(value)
      }
    }
  }

  private fun setCachePathFromConfiguration(path: String) {
    if (path.isEmpty()) {
      logger.error {
        "Value for ${ConfigurationKeys.CACHE_PATH.value} is empty. Proceeding with default."
      }

      return
    }

    configuration.cachePath = path
  }

  private fun setPrimaryPortFromConfiguration(port: String) {
    try {
      configuration.primaryPort = port.toInt()
    } catch (_: NumberFormatException) {
      logger.error {
        "Failed to parse ${ConfigurationKeys.PRIMARY_PORT.value}. Proceeding with default."
      }
    }
  }

  private fun setSecondaryPortFromConfiguration(port: String) {
    try {
      configuration.secondaryPort = port.toInt()
    } catch (_: NumberFormatException) {
      logger.error {
        "Failed to parse ${ConfigurationKeys.SECONDARY_PORT.value}. Proceeding with default."
      }
    }
  }

  private fun setBuildNumberFromConfiguration(buildNumber: String) {
    try {
      configuration.buildNumber = buildNumber.toInt()
    } catch (_: NumberFormatException) {
      logger.error {
        "Failed to parse ${ConfigurationKeys.BUILD_NUMBER.value}. Proceeding with default."
      }
    }
  }

  private companion object {
    private const val DEFAULT_CONFIGURATION_PATH = "etc/file-server.ini"
    private val logger = InlineLogger()
  }
}
