package org.jep21s.messenger.core.service.biz.processor.impl.message.stubs

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.chainStub
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.context.isRunning
import org.jep21s.messenger.core.service.common.context.isStubSuccess
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageSearch

suspend fun ICorChainDsl<CSContext<MessageSearch, List<Message>?>>.stubsMessageSearch() {
  chainStub {
    this.title = "Обработка стабов поиска сообщений"
    on { workMode is CSWorkMode.Stub && state.isRunning() }
    stubSuccessMessageSearch()
  }
}

private suspend fun ICorChainDsl<CSContext<MessageSearch, List<Message>?>>.stubSuccessMessageSearch() = worker {
  this.title = "Кейс успеха поиска сообщений"
  onRunning { workMode.isStubSuccess() }
  handle {
    val messageFilter: MessageSearch.MessageFilter? = modelReq.messageFilter
    val chatFilter: MessageSearch.ChatFilter = modelReq.chatFilter
    copy(
      modelResp = listOf(
        Message(
          id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
          chatId = chatFilter.id ?: UUID.fromString("00000000-0000-0000-0000-000000000002"),
          communicationType = "TG",
          messageType = messageFilter?.messageTypes?.first() ?: "simple",
          sentDate = Instant.ofEpochSecond(1),
          createdAt = Instant.ofEpochSecond(1),
          updatedAt = null,
          body = "body",
          externalId = null,
          payload = null,
        )
      ),
      state = CSContextState.Finishing,
    )
  }
}