package org.jep21s.messenger.core.service.api.v1.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konvert
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.Mapping
import org.jep21s.messenger.core.service.api.v1.models.ChatDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.ChatDeleteResp

import org.jep21s.messenger.core.service.api.v1.models.ChatResp
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReq
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqFilter
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqSort
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatDeleted
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface ChatMapper {
  fun mapToResponse(chat: Chat): ChatResp

  fun mapToModel(request: ChatDeleteReq): ChatDeletion

  fun mapToResponse(model: ChatDeleted): ChatDeleteResp

  fun mapToModel(request: ChatSearchReq): ChatSearch

  fun mapToModel(request: ChatSearchReqFilter): ChatSearch.ChatSearchFilter

  fun mapToModel(request: ChatSearchReqSort): ChatSearch.ChatSearchSort
}
