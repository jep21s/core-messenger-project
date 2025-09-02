package org.jep21s.messenger.core.service.api.v1.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteResp
import org.jep21s.messenger.core.service.api.v1.models.MessageResp
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReq
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReqChatFilter
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReqMessageFilter
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion
import org.jep21s.messenger.core.service.common.model.message.MessageSearch

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface MessageMapper {
  fun mapToResponse(model: Message): MessageResp

  fun mapToModel(request: MessageDeleteReq): MessageDeletion

  fun mapToResponse(model: MessageDeleted): MessageDeleteResp

  fun mapToModel(request: MessageSearchReq): MessageSearch

  fun mapToModel(request: MessageSearchReqMessageFilter): MessageSearch.MessageFilter

  fun mapToModel(request: MessageSearchReqChatFilter): MessageSearch.ChatFilter
}
