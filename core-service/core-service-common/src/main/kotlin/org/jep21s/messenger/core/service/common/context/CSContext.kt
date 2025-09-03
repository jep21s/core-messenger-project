package org.jep21s.messenger.core.service.common.context

data class CSContext<Req, Resp>(
  val command: CSContextCommand,
  val state: CSContextState,
  val workMode: CSWorkMode,
  val modelReq: Req,
  val modelResp: Resp,
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

