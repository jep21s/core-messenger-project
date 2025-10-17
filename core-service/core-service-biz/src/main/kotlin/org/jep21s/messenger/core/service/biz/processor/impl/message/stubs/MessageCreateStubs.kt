package org.jep21s.messenger.core.service.biz.processor.impl.message.stubs

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
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation

fun ICorChainDsl<CSContext<MessageCreation, Message?>>.stubsMessageCreation() {
  chain {
    this.title = "Обработка стабов создания сообщения"
    on { workMode is CSWorkMode.Stub && state == CSContextState.Running }
    stubSuccessMessageCreation()
  }
}

private fun ICorChainDsl<CSContext<MessageCreation, Message?>>.stubSuccessMessageCreation() = worker {
  this.title = "Кейс успеха создания сообщения"
  on { workMode.isStubSuccess() && state.isRunning() }
  handle {
    putModelResp(
      Message(
        id = modelReq.id ?: UUID.fromString("00000000-0000-0000-0000-000000000001"),
        chatId = modelReq.chatId,
        messageType = modelReq.messageType,
        status = "CREATED",
        sentDate = modelReq.sentDate,
        createdAt = Instant.ofEpochSecond(1),
        updatedAt = null,
        body = "body",
        externalId = modelReq.externalId,
        payload = modelReq.payload,
      )
    )
  }
}