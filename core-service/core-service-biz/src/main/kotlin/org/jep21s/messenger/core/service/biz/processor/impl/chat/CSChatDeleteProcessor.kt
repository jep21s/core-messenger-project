package org.jep21s.messenger.core.service.biz.processor.impl.chat

import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.chat.stubs.stubsChatDeletion
import org.jep21s.messenger.core.service.biz.processor.impl.chat.validation.validCommunicationType
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion

object CSChatDeleteProcessor :
  CSProcessor<ChatDeletion, Chat?>() {
  override suspend fun exec(
    context: CSContext<ChatDeletion, Chat?>,
  ): CSContext<ChatDeletion, Chat?> = runChain(context) {
    stubsChatDeletion()
    validation {
      validCommunicationType()
    }
  }
}
