package org.jep21s.messenger.core.service.biz.processor.impl.message

import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.stubs.stubsMessageDeletion
import org.jep21s.messenger.core.service.biz.processor.impl.message.validation.messageIdsInChat
import org.jep21s.messenger.core.service.biz.processor.impl.message.validation.existChat
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

object CSMessageDeleteProcessor :
  CSProcessor<MessageDeletion, MessageDeleted?>() {
  override suspend fun exec(
    context: CSContext<MessageDeletion, MessageDeleted?>,
  ): CSContext<MessageDeletion, MessageDeleted?> = runChain(context) {
    stubsMessageDeletion()
    validation {
      existChat()
      messageIdsInChat()
    }
    chain {
      worker {
        title = "Удаление сообщений из чата"
        onRunning()
        handle {
          val messageRepo: IMessageRepo = CSCorSettings.messageRepo(workMode)
          messageRepo.delete(modelReq)
          this.copy(
            modelResp = MessageDeleted(
              ids = modelReq.ids.toList(),
              chatId = modelReq.chatId,
              communicationType = modelReq.communicationType
            )
          )
        }
      }
    }
  }
}