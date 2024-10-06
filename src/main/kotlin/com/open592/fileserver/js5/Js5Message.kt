package com.open592.fileserver.js5

import com.open592.fileserver.protocol.Packet

sealed class Js5Message : Packet {
  data class InitializeJs5RemoteConnection(val build: Int) : Js5Message()

  data class RequestGroup(
      val archive: Int,
      val group: Int,
      val isPrefetch: Boolean,
  ) : Js5Message()

  object InformUserLoggedIn : Js5Message()

  object InformUserLoggedOut : Js5Message()

  object InformClientReady : Js5Message()

  object RequestConnectionDisconnect : Js5Message()

  data class ExchangeObfuscationKey(val key: Int) : Js5Message()
}
