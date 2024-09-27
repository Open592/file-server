plugins {
  base
  application
  alias(libs.plugins.kotlin.jvm)

  alias(libs.plugins.ktfmt.gradle)
}

application {
  mainClass.set("com.open592.fileserver.cmd.FileServerCommandKt")
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
  implementation(libs.clikt)
  implementation(libs.guice)
  implementation(libs.guava)
  implementation(libs.kotlin.inline.logger)
  implementation(libs.kotlinx.coroutines.core)
  runtimeOnly(libs.logback.classic)
  implementation(libs.netty.all)

  testImplementation(kotlin("test"))
  testImplementation(platform(libs.junit.bom))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation(libs.mockk)
  testImplementation(libs.jimfs)
}

tasks.test {
  useJUnitPlatform()
}
