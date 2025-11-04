package org.jep21s.messenger.core.service.biz.processor.impl.chat.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.fail
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion
import org.jep21s.messenger.core.service.common.model.chat.chatSearch
import org.jep21s.messenger.core.service.common.repo.IChatRepo

suspend fun ICorChainDsl<CSContext<ChatDeletion, Chat?>>.validCommunicationType() = worker {
  title = "Проверка валидности переданного значения ${ChatDeletion::communicationType.name}"
  onRunning {
    val chatRepo: IChatRepo = CSCorSettings.chatRepo(workMode)
    val existsChat: Chat? = chatRepo.search(
      chatSearch {
        filter {
          id { modelReq.id }
          communicationType { modelReq.communicationType }
        }
      }
    ).firstOrNull()

    return@onRunning existsChat == null
  }

  handle {
    fail(
      CSError(
        code = "chat-validation-delete",
        group = "chat-validation",
        field = ChatDeletion::id.name,
        message = "Chat already exists by chatId [${modelReq.id}] " +
            "and communicationType [${modelReq.communicationType}]",
      )
    )
  }
}
