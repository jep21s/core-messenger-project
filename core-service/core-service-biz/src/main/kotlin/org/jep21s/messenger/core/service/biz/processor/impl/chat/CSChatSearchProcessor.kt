package org.jep21s.messenger.core.service.biz.processor.impl.chat

import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.biz.processor.impl.chat.stubs.stubsChatSearch
import org.jep21s.messenger.core.service.biz.processor.impl.chat.validation.validLimit
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch

object CSChatSearchProcessor :
  CSProcessor<ChatSearch, List<Chat>?>() {
  override suspend fun exec(
    context: CSContext<ChatSearch, List<Chat>?>,
  ): CSContext<ChatSearch, List<Chat>?> = runChain(context) {
    stubsChatSearch()
    validation {
      validLimit()
    }
  }
}