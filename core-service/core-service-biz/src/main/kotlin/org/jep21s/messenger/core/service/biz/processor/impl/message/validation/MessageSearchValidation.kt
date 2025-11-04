package org.jep21s.messenger.core.service.biz.processor.impl.message.validation

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.fail
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageSearch

suspend fun ICorChainDsl<CSContext<MessageSearch, List<Message>?>>.validLimit() = worker {
  title = "Проверка корректно переданного лимита при поиске сообщений"
  on {
    modelReq.limit
      ?.let { it < 1 || it > 50 }
      ?: false
  }
  handle {
    fail(
      CSError(
        code = "validation-search",
        group = "validation",
        field = MessageSearch::limit.name,
        message = "incorrect limit value [${modelReq.limit}]",
      )
    )
  }
}