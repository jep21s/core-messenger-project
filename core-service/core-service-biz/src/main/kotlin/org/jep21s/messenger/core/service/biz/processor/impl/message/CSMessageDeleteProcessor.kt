package org.jep21s.messenger.core.service.biz.processor.impl.message

import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.stubs.stubsMessageDeletion
import org.jep21s.messenger.core.service.biz.processor.impl.message.validation.messageIdsInChat
import org.jep21s.messenger.core.service.biz.processor.impl.message.validation.existChat
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion

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
  }
}