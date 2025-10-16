package org.jep21s.messenger.core.service.biz.processor.impl.chat

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.biz.extention.putModelResp
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation

object CSChatCreateProcessor :
  CSProcessor<ChatCreation, Chat?>() {
  override suspend fun exec(
    context: CSContext<ChatCreation, Chat?>,
  ): CSContext<ChatCreation, Chat?> {
    val request: ChatCreation = context.modelReq
    return context.putModelResp(
      Chat(
        id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        externalId = request.externalId,
        communicationType = request.communicationType,
        chatType = request.chatType,
        payload = request.payload,
        createdAt = Instant.ofEpochSecond(1),
        updatedAt = null,
        latestMessageDate = null,
      )
    )
  }
}