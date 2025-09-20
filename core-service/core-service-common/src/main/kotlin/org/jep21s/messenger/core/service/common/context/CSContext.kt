package org.jep21s.messenger.core.service.common.context

import java.time.Instant

data class CSContext<Req, Resp>(
  val command: CSContextCommand,
  val state: CSContextState,
  val workMode: CSWorkMode,
  val modelReq: Req,
  val modelResp: Resp,
  val timeStart: Instant = Instant.now(),
)

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

