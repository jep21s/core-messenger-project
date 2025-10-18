package org.jep21s.messenger.core.service.biz.processor.impl.chat.stubs

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.context.isRunning
import org.jep21s.messenger.core.service.common.context.isStubSuccess
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion

fun ICorChainDsl<CSContext<ChatDeletion, Chat?>>.stubsChatDeletion() {
  chain {
    this.title = "Обработка стабов поиска чатов"
    on { workMode is CSWorkMode.Stub && state == CSContextState.Running }
    stubSuccessChatDeletion()
  }
}

private fun ICorChainDsl<CSContext<ChatDeletion, Chat?>>.stubSuccessChatDeletion() = worker {
  this.title = "Кейс успеха для удаления чата"
  on { workMode.isStubSuccess() && state.isRunning() }
  handle {
    copy(
      modelResp = Chat(
        id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        externalId = null,
        communicationType = modelReq.communicationType,
        chatType = "simple",
        payload = null,
        createdAt = Instant.ofEpochSecond(1),
        updatedAt = null,
        latestMessageDate = null,
      ),
      state = CSContextState.Finishing,
    )
  }
}
