package org.jep21s.messenger.core.service.api.v1.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import org.jep21s.messenger.core.service.api.v1.models.ChatCreateReq
import org.jep21s.messenger.core.service.api.v1.models.ChatDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.ChatDeleteRespAllOfContent
import org.jep21s.messenger.core.service.api.v1.models.ChatResp
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReq
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfFilter
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfSort
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
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
  fun mapToModel(request: ChatCreateReq): ChatCreation

  fun mapToResponse(chat: Chat): ChatResp

  fun mapToModel(request: ChatDeleteReq): ChatDeletion

  fun mapToDeleteContentResponse(model: ChatDeleted): ChatDeleteRespAllOfContent

  fun mapToModel(request: ChatSearchReq): ChatSearch

  fun mapToModel(request: ChatSearchReqAllOfFilter): ChatSearch.ChatSearchFilter

  fun mapToModel(request: ChatSearchReqAllOfSort): ChatSearch.ChatSearchSort
}
