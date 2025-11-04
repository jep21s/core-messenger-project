package org.jep21s.messenger.core.service.biz.processor.impl.chat.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.fail
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.jep21s.messenger.core.service.common.model.chat.chatSearch
import org.jep21s.messenger.core.service.common.repo.IChatRepo

suspend fun ICorChainDsl<CSContext<ChatCreation, Chat?>>.notExistExternalId() = worker {
  title = "Проверка отсутствия поля ${ChatCreation::externalId.name}"
  onRunning {
    val externalId: String = this.modelReq.externalId ?: return@onRunning false
    val chatRepo: IChatRepo = CSCorSettings.chatRepo(this.workMode)
    val existsChat: Chat? = chatRepo.search(
      chatSearch {
        filter {
          externalIds { listOf(externalId) }
          communicationType { modelReq.communicationType }
        }
        limit = 1
      }
    ).firstOrNull()

    return@onRunning existsChat != null
  }
  handle {
    fail(
      CSError(
        code = "chat-validation-create",
        group = "chat-validation",
        field = ChatCreation::externalId.name,
        message = "Chat already exists by externalId [${modelReq.externalId}]",
      )
    )
  }
}