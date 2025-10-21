package org.jep21s.messenger.core.service.biz.processor.impl.message.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation

suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.validationMessageCreation() = validation {
  existChat()
}

private suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.existChat() = worker {
  //
}