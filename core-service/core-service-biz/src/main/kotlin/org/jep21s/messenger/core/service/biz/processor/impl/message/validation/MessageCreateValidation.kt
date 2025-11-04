package org.jep21s.messenger.core.service.biz.processor.impl.message.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.fail
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.context.isRunning
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.chatSearch
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.repo.IChatRepo

suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.existChat() = worker {
  title = "Проверка существования чата, перед записью в него сообщения"
  on {
    if (!state.isRunning()) return@on false
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
    return@on chat == null
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

suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.notId() = worker {

}

suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.notExistExternalId() = worker {
  title = "Проверка что сообщение с подобным externalId еще не записано"

  //TODO проверить что нет сообщения в БД с таким же externalId
}