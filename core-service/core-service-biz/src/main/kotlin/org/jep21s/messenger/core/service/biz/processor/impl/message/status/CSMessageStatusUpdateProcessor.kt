package org.jep21s.messenger.core.service.biz.processor.impl.message.status

import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.status.stubs.stubsMessageStatusUpdation
import org.jep21s.messenger.core.service.biz.processor.impl.message.status.validation.messageIdsInChat
import org.jep21s.messenger.core.service.biz.processor.impl.message.status.validation.validCommunicationType
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdated
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdation

object CSMessageStatusUpdateProcessor :
  CSProcessor<MessageStatusUpdation, MessageStatusUpdated?>() {
  override suspend fun exec(
    context: CSContext<MessageStatusUpdation, MessageStatusUpdated?>,
  ): CSContext<MessageStatusUpdation, MessageStatusUpdated?> = runChain(context) {
    stubsMessageStatusUpdation()
    validation {
      validCommunicationType()
      messageIdsInChat()
    }
  }
}