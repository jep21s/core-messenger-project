package org.jep21s.messenger.core.service.biz.processor.impl.message.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation

suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.existChat() = worker {
  //TODO проверить существование чата в БД

}

suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.validCommunicationType() = worker {
  //TODO проверить что у чата в БД корректный тип коммуникации
}