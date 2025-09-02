package org.jep21s.messenger.core.service.common.context

data class CSContext<Req, Resp>(
  val command: CSContextCommand,
  val state: CSContextState,
  val workMode: CSWorkMode,
  val request: Req,
  val response: Resp,
)

enum class CSContextCommand {
  NONE,
  CREATE,
  READ,
  UPDATE,
  DELETE,
  SEARCH,
  OFFERS,
  INIT,
  FINISH,
}

