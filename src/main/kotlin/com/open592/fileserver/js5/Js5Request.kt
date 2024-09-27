package com.open592.fileserver.js5

sealed class Js5Request {
  data class Group(val isPrefetch: Boolean, val archive: Int, val group: Int) : Js5Request()
  data class Initialize(val build: Int) : Js5Request()
  object LoggedIn : Js5Request()
  object LoggedOut : Js5Request()
  data class KeyExchange(val key: Int) : Js5Request()
  object Connected : Js5Request()
  object Disconnected : Js5Request()
}
