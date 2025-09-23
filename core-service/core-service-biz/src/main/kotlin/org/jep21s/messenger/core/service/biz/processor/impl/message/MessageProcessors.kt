package org.jep21s.messenger.core.service.biz.processor.impl.message

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.biz.extention.putModelResp
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion
import org.jep21s.messenger.core.service.common.model.message.MessageSearch

object CSMessageCreateProcessor :
  CSProcessor<MessageCreation, Message?>() {
  override suspend fun exec(
    context: CSContext<MessageCreation, Message?>,
  ): CSContext<MessageCreation, Message?> {
    val request: MessageCreation = context.modelReq
    return context.putModelResp(
      Message(
        id = request.id ?: UUID.fromString("00000000-0000-0000-0000-000000000001"),
        chatId = request.chatId,
        messageType = request.messageType,
        status = "CREATED",
        sentDate = request.sentDate,
        createdAt = Instant.ofEpochSecond(1),
        updatedAt = null,
        body = "body",
        externalId = request.externalId,
        payload = request.payload,
      )
    )
  }
}

object CSMessageSearchProcessor :
  CSProcessor<MessageSearch, List<Message>?>() {
  override suspend fun exec(
    context: CSContext<MessageSearch, List<Message>?>,
  ): CSContext<MessageSearch, List<Message>?> {
    val messageFilter: MessageSearch.MessageFilter? = context.modelReq.messageFilter
    val chatFilter: MessageSearch.ChatFilter = context.modelReq.chatFilter
    return context.putModelResp(
      listOf(
        Message(
          id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
          chatId = chatFilter.id ?: UUID.fromString("00000000-0000-0000-0000-000000000002"),
          messageType = messageFilter?.messageTypes?.first() ?: "simple",
          status = "CREATED",
          sentDate = Instant.ofEpochSecond(1),
          createdAt = Instant.ofEpochSecond(1),
          updatedAt = null,
          body = "body",
          externalId = null,
          payload = null,
        )
      )
    )
  }
}

object CSMessageDeleteProcessor :
  CSProcessor<MessageDeletion, MessageDeleted?>() {
  override suspend fun exec(
    context: CSContext<MessageDeletion, MessageDeleted?>,
  ): CSContext<MessageDeletion, MessageDeleted?> {
    val request: MessageDeletion = context.modelReq
    return context.putModelResp(
      MessageDeleted(
        ids = request.ids,
        communicationType = request.communicationType,
      )
    )
  }
}