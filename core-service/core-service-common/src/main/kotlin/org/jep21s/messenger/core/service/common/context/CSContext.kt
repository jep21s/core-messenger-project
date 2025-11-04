package org.jep21s.messenger.core.service.common.context

import java.time.Instant
import kotlin.reflect.KClass

data class CSContext<Req, Resp>(
  val command: CSContextCommand,
  val state: CSContextState,
  val workMode: CSWorkMode,
  val modelReq: Req,
  val modelResp: Resp,
  val timeStart: Instant = Instant.now(),
  private val innerContext: MutableMap<KClass<*>, Any> = mutableMapOf()
) {
  @Suppress("UNCHECKED_CAST")
  operator fun<T: Any> get(valClass: KClass<T>): T? =
    innerContext[valClass] as? T

  operator fun <T: Any> set(valClass: KClass<T>, value: T) {
    innerContext.put(valClass, value as Any)
  }
}

enum class CSContextCommand {
  CREATE_CHAT,
  DELETE_CHAT,
  SEARCH_CHAT,
  CREATE_MESSAGE,
  DELETE_MESSAGE,
  SEARCH_MESSAGE,
  UPDATE_STATUS_MESSAGE,
  ;
}

