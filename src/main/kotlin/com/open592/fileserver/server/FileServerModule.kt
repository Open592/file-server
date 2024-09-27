package com.open592.fileserver.server

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import com.open592.fileserver.js5.Js5Service

object FileServerModule : AbstractModule() {
  override fun configure() {
    val binder = Multibinder.newSetBinder(binder(), Service::class.java)
    binder.addBinding().to(Js5Service::class.java)
  }
}
