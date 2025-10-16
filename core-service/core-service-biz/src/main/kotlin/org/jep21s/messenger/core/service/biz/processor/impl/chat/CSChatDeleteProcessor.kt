package org.jep21s.messenger.core.service.biz.processor.impl.chat

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.biz.extention.putModelResp
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion

object CSChatDeleteProcessor :
  CSProcessor<ChatDeletion, Chat?>() {
  override suspend fun exec(
    context: CSContext<ChatDeletion, Chat?>,
  ): CSContext<ChatDeletion, Chat?> {
    val request: ChatDeletion = context.modelReq
    return context.putModelResp(
      Chat(
        id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        externalId = null,
        communicationType = request.communicationType,
        chatType = "simple",
        payload = null,
        createdAt = Instant.ofEpochSecond(1),
        updatedAt = null,
        latestMessageDate = null,
      )

    )
  }
}
