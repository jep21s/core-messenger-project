package org.jep21s.messenger.core.service.biz.processor.impl.chat

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.chat.stubs.stubsChatDeletion
import org.jep21s.messenger.core.service.biz.processor.impl.chat.validation.validCommunicationType
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion
import org.jep21s.messenger.core.service.common.repo.IChatRepo

object CSChatDeleteProcessor :
  CSProcessor<ChatDeletion, Chat?>() {
  override suspend fun exec(
    context: CSContext<ChatDeletion, Chat?>,
  ): CSContext<ChatDeletion, Chat?> = runChain(context) {
    stubsChatDeletion()
    validation {
      validCommunicationType()
    }
    chain {
      deleteChatFromDB()
    }
  }

  private suspend fun ICorChainDsl<CSContext<ChatDeletion, Chat?>>.deleteChatFromDB() {
    worker {
      title = "Удаление чата из БД"
      onRunning()
      handle {
        val chatRepo: IChatRepo = CSCorSettings.chatRepo(workMode)
        val deletedChat: Chat? = chatRepo.delete(this.modelReq)
        this.copy(
          state = CSContextState.Finishing,
          modelResp = deletedChat,
        )
      }
    }
  }
}
