package org.jep21s.messenger.core.service.biz.processor.impl.message

import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.stubs.stubsMessageSearch
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageSearch

object CSMessageSearchProcessor :
  CSProcessor<MessageSearch, List<Message>?>() {
  override suspend fun exec(
    context: CSContext<MessageSearch, List<Message>?>,
  ): CSContext<MessageSearch, List<Message>?> = runChain(context) {
    stubsMessageSearch()
  }
}