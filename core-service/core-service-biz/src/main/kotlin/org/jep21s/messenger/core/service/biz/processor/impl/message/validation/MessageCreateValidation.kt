package org.jep21s.messenger.core.service.biz.processor.impl.message.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.fail
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.chatSearch
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.messageSearch
import org.jep21s.messenger.core.service.common.repo.IChatRepo
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.existChat() = worker {
  title = "Проверка существования чата, перед записью в него сообщения"
  onRunning {
    val chatRepo: IChatRepo = CSCorSettings.chatRepo(workMode)
    val chat: Chat? = chatRepo.search(
      chatSearch {
        filter {
          communicationType { modelReq.communicationType }
          id { modelReq.chatId }
        }
        limit = 1
      }
    ).firstOrNull()
    return@onRunning chat == null
  }
  handle {
    fail(
      CSError(
        code = "message-validation-create",
        group = "message-validation",
        field = MessageCreation::chatId.name,
        message = "Chat not exists. ChatId [${modelReq.chatId}], communicationType [${modelReq.communicationType}]",
      )
    )
  }
}

suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.notExistId() = worker {
  title = "Проверка что сообщение с подобным externalId еще не записано"
  onRunning {
    val messageId = modelReq.id
    if (messageId == null) return@onRunning false

    val chatRepo: IMessageRepo = CSCorSettings.messageRepo(workMode)
    val message: Message? = chatRepo.search(
      messageSearch {
        chatFilter {
          communicationType { modelReq.communicationType }
          id { modelReq.chatId }
        }
        messageFilter {
          id { messageId }
          sentDate {
            ComparableFilter(
              value = modelReq.sentDate,
              direction = ConditionType.EQUAL,
            )
          }
        }
        limit = 1
      }
    ).firstOrNull()

    return@onRunning message != null
  }
  handle {
    fail(
      CSError(
        code = "message-validation-create",
        group = "message-validation",
        field = MessageCreation::id.name,
        message = "Message already exists. ChatId [${modelReq.chatId}], " +
            "communicationType [${modelReq.communicationType}], " +
            "messageId [${modelReq.id}]",
      )
    )

  }
}