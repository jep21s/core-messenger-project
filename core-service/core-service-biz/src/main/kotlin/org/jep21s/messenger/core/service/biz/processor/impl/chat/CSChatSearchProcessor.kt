package org.jep21s.messenger.core.service.biz.processor.impl.chat

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.impl.chat.stubs.stubsChatSearch
import org.jep21s.messenger.core.service.biz.processor.impl.chat.validation.validLimit
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.jep21s.messenger.core.service.common.repo.IChatRepo

object CSChatSearchProcessor :
  CSProcessor<ChatSearch, List<Chat>?>() {
  override suspend fun exec(
    context: CSContext<ChatSearch, List<Chat>?>,
  ): CSContext<ChatSearch, List<Chat>?> = runChain(context) {
    stubsChatSearch()
    validation {
      validLimit()
    }
    chain {
      searchChatInDB()
    }
  }

  private suspend fun ICorChainDsl<CSContext<ChatSearch, List<Chat>?>>.searchChatInDB() {
    worker {
      title = "Поиск чатов в БД"
      onRunning()
      handle {
        val chatRepo: IChatRepo = CSCorSettings.chatRepo(workMode)
        val resultList: List<Chat> = chatRepo.search(this.modelReq)
        this.copy(
          state = CSContextState.Finishing,
          modelResp = resultList,
        )
      }
    }
  }
}