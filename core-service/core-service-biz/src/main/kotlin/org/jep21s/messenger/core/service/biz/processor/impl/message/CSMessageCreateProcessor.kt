package org.jep21s.messenger.core.service.biz.processor.impl.message

import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.stubs.stubsMessageCreation
import org.jep21s.messenger.core.service.biz.processor.impl.message.validation.existChat
import org.jep21s.messenger.core.service.biz.processor.impl.message.validation.notExistId
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

object CSMessageCreateProcessor :
  CSProcessor<MessageCreation, Message?>() {
  override suspend fun exec(
    context: CSContext<MessageCreation, Message?>,
  ): CSContext<MessageCreation, Message?> = runChain(context) {
    stubsMessageCreation()
    validation {
      existChat()
      notExistId()
    }
    chain {
      worker {
        title = "Создание нового сообщения"
        onRunning()
        handle {
          val messageRepo: IMessageRepo = CSCorSettings.messageRepo(workMode)
          val message: Message = messageRepo.save(modelReq)
          this.copy(
            state = CSContextState.Finishing,
            modelResp = message,
          )
        }
      }
    }
  }
}
