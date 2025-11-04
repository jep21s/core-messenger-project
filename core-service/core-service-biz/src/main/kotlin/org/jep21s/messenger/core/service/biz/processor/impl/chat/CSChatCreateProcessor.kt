package org.jep21s.messenger.core.service.biz.processor.impl.chat

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.chat.stubs.stubsChatCreation
import org.jep21s.messenger.core.service.biz.processor.impl.chat.validation.notExistExternalId
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.isRunning
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation

object CSChatCreateProcessor :
  CSProcessor<ChatCreation, Chat?>() {
  override suspend fun exec(
    context: CSContext<ChatCreation, Chat?>,
  ): CSContext<ChatCreation, Chat?> = runChain(context) {
    stubsChatCreation()
    validation {
      notExistExternalId()
    }
    chain {
      createNewChatInDb()
    }
  }

  private suspend fun ICorChainDsl<CSContext<ChatCreation, Chat?>>.createNewChatInDb() {
    worker {
      title = "Добавление нового чата в БД"
      on { state.isRunning() }
      handle {
        val chatRepo = CSCorSettings.chatRepo(workMode)
        val newChat = chatRepo.save(modelReq)
        this.copy(
          modelResp = newChat
        )
      }
    }
  }
}
