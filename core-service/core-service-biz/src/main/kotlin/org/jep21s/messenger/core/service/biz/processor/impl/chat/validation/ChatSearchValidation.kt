package org.jep21s.messenger.core.service.biz.processor.impl.chat.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.fail
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch

suspend fun ICorChainDsl<CSContext<ChatSearch, List<Chat>?>>.validLimit() = worker {
  title = "Проверка валидности переданного лимита"
  onRunning {
    modelReq.limit
      ?.let { it < 1 || it > 50 }
      ?: false
  }
  handle {
    fail(
      CSError(
        code = "chat-validation-search",
        group = "chat-validation",
        field = ChatSearch::limit.name,
        message = "incorrect limit value [${modelReq.limit}]",
      )
    )
  }
}