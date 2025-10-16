package org.jep21s.messenger.core.service.biz.processor.impl.chat

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.lib.logging.common.LogLevel
import org.jep21s.messenger.core.service.biz.extention.putModelResp
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.context.isRunning
import org.jep21s.messenger.core.service.common.context.isStubSuccess
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch

fun ICorChainDsl<CSContext<ChatSearch, List<Chat>?>>.chatStubs() {
  chain {
    this.title = "Обработка стабов чата"
    on { workMode is CSWorkMode.Stub && state == CSContextState.Running }
    stubChatSearchSuccess()
  }
}

private fun ICorChainDsl<CSContext<ChatSearch, List<Chat>?>>.stubChatSearchSuccess() = worker {
  this.title = "Имитация успешной обработки"
  this.description = "Кейс успеха для создания чата"
  on { workMode.isStubSuccess() && state.isRunning() }
  val logger = CSCorSettings.loggerProvider.logger("stubChatSearchSuccess")
  handle {
    logger.doWithLogging(level = LogLevel.DEBUG) {
      val request: ChatSearch.ChatSearchFilter = modelReq.filter
      putModelResp(
        listOf(
          Chat(
            id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            externalId = request.externalIds?.firstOrNull(),
            communicationType = request.communicationType,
            chatType = "simple",
            payload = null,
            createdAt = Instant.ofEpochSecond(1),
            updatedAt = null,
            latestMessageDate = null
          )
        )
      )
    }
  }
}