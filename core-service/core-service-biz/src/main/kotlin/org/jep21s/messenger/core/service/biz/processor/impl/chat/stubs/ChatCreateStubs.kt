package org.jep21s.messenger.core.service.biz.processor.impl.chat.stubs

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.extention.putModelResp
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.context.isRunning
import org.jep21s.messenger.core.service.common.context.isStubSuccess
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation

fun ICorChainDsl<CSContext<ChatCreation, Chat?>>.stubsChatCreation() {
  chain {
    this.title = "Обработка стабов создания чата"
    on { workMode is CSWorkMode.Stub && state == CSContextState.Running }
    stubSuccessChatCreation()
  }
}

private fun ICorChainDsl<CSContext<ChatCreation, Chat?>>.stubSuccessChatCreation() = worker {
  this.title = "Кейс успеха для создания чата"
  on { workMode.isStubSuccess() && state.isRunning() }
  handle {
    val request: ChatCreation = modelReq
    putModelResp(
      Chat(
        id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        externalId = request.externalId,
        communicationType = request.communicationType,
        chatType = request.chatType,
        payload = request.payload,
        createdAt = Instant.ofEpochSecond(1),
        updatedAt = null,
        latestMessageDate = null,
      )
    )
  }
}
