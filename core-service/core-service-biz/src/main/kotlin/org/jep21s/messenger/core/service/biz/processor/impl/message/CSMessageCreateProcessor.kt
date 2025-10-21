package org.jep21s.messenger.core.service.biz.processor.impl.message

import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.stubs.stubsMessageCreation
import org.jep21s.messenger.core.service.biz.processor.impl.message.validation.existChat
import org.jep21s.messenger.core.service.biz.processor.impl.message.validation.validCommunicationType
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation

object CSMessageCreateProcessor :
  CSProcessor<MessageCreation, Message?>() {
  override suspend fun exec(
    context: CSContext<MessageCreation, Message?>,
  ): CSContext<MessageCreation, Message?> = runChain(context) {
    stubsMessageCreation()
    validation {
      existChat()
      validCommunicationType()
    }
  }
}
