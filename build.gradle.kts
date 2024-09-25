plugins {
  base
  application
  alias(libs.plugins.kotlin.jvm)

  alias(libs.plugins.ktfmt.gradle)
}

application {
  mainClass.set("com.open592.fileserver.cmd.Main")
}

kotlin {
  jvmToolchain(21)
}

group = "com.open592"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
