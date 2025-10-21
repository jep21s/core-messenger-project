package org.jep21s.messenger.core.service.biz.processor.impl.message.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion

suspend fun ICorChainDsl<CSContext<MessageDeletion, MessageDeleted?>>.messageIdsInChat() = worker {
  //TODO проверить в БД что все удаляемые сообщения относятся к переданному чату
}

suspend fun ICorChainDsl<CSContext<MessageDeletion, MessageDeleted?>>.validCommunicationType() = worker {
  //TODO проверить что у чата в БД корректный тип коммуникации
}