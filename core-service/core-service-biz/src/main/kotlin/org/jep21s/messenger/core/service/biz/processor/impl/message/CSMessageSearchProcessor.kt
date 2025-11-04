package org.jep21s.messenger.core.service.biz.processor.impl.message

import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.stubs.stubsMessageSearch
import org.jep21s.messenger.core.service.biz.processor.impl.message.validation.validLimit
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageSearch
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

object CSMessageSearchProcessor :
  CSProcessor<MessageSearch, List<Message>?>() {
  override suspend fun exec(
    context: CSContext<MessageSearch, List<Message>?>,
  ): CSContext<MessageSearch, List<Message>?> = runChain(context) {
    stubsMessageSearch()
    validation {
      validLimit()
    }
    chain {
      worker {
        title = "Динамический поиск сообщений"
        onRunning()
        handle {
          val messageRepo: IMessageRepo = CSCorSettings.messageRepo(workMode)
          val result: List<Message> = messageRepo.search(modelReq)
          this.copy(
            state = CSContextState.Finishing,
            modelResp = result,
          )
        }
      }
    }
  }
}