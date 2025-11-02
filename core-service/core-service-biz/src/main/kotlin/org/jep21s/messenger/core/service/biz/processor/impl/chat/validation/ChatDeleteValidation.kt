package org.jep21s.messenger.core.service.biz.processor.impl.chat.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion

suspend fun ICorChainDsl<CSContext<ChatDeletion, Chat?>>.validCommunicationType() = worker {
  //TODO проверить что у чата в БД корректный тип коммуникации
}