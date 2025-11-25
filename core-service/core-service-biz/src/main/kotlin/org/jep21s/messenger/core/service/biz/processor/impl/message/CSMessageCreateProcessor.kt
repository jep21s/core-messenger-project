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
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.model.chat.ChatLatestActivityUpdation
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.repo.IChatRepo
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
            modelResp = message,
          )
        }
      }
      worker {
        title = "Обновление даты последнего сообщения чата"
        onRunning()
        handle {
          val message: Message = modelResp ?: return@handle this.copy(
            state = CSContextState.Failing(
              errors = listOf(
                CSError(
                  code = "can't find saved message",
                  group = "biz-logic",
                  field = "modelResp",
                  message = "couldn't get saved message in trying update " +
                      "chat latest message date",
                )
              )
            )
          )
          val chatRepo: IChatRepo = CSCorSettings.chatRepo(workMode)
          chatRepo.updateLatestMessageDate(
            ChatLatestActivityUpdation(
              chatId = message.chatId,
              communicationType = message.communicationType,
              latestMessageDate = message.sentDate,
            )
          )
          this.copy(
            state = CSContextState.Finishing,
          )
        }
      }
    }
  }
}
