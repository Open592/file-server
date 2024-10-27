package com.open592.fileserver.protocol.inbound

sealed class Js5InboundMessage {
  data class InitializeJs5RemoteConnection(val build: Int) : Js5InboundMessage()

  data class RequestGroup(val archive: Int, val group: Int, val isPrefetch: Boolean) :
      Js5InboundMessage()

  object InformUserIsLoggedIn : Js5InboundMessage()

  object InformUserIsLoggedOut : Js5InboundMessage()

  object InformClientIsReady : Js5InboundMessage()

  object RequestConnectionDisconnect : Js5InboundMessage()

  data class ExchangeObfuscationKey(val key: Int) : Js5InboundMessage()
}
