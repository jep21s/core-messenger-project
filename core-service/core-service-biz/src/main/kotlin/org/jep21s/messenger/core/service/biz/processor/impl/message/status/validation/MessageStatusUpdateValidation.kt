package org.jep21s.messenger.core.service.biz.processor.impl.message.status.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdated
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdation

suspend fun ICorChainDsl<CSContext<MessageStatusUpdation, MessageStatusUpdated?>>.messageIdsInChat() = worker {
  //TODO проверить в БД что все статусы обновляемых сообщений относятся к переданному чату
}

suspend fun ICorChainDsl<CSContext<MessageStatusUpdation, MessageStatusUpdated?>>.validCommunicationType() = worker {
  //TODO проверить что у чата в БД корректный тип коммуникации
}