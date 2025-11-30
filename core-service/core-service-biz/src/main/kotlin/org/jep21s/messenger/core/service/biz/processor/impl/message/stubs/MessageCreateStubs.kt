package org.jep21s.messenger.core.service.biz.processor.impl.message.stubs

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.chainStub
import org.jep21s.messenger.core.service.biz.cor.fail
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.context.isStubDbError
import org.jep21s.messenger.core.service.common.context.isStubNotFound
import org.jep21s.messenger.core.service.common.context.isStubSuccess
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation

suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.stubsMessageCreation() {
  chainStub {
    this.title = "Обработка стабов создания сообщения"
    on { workMode is CSWorkMode.Stub && state == CSContextState.Running }
    stubSuccessMessageCreation()
    stubMessageCreationNotFoundChat()
    stubMessageCreationDBError()
  }
}

private suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.stubSuccessMessageCreation() = worker {
  this.title = "Кейс успеха создания сообщения"
  onRunning { workMode.isStubSuccess() }
  handle {
    copy(
      modelResp = Message(
        id = modelReq.id ?: UUID.fromString("00000000-0000-0000-0000-000000000001"),
        chatId = modelReq.chatId,
        communicationType = "TG",
        messageType = modelReq.messageType,
        senderId = "1",
        senderType = "EMPLOYEE",
        sentDate = modelReq.sentDate,
        createdAt = Instant.ofEpochSecond(1),
        updatedAt = null,
        body = "body",
        externalId = modelReq.externalId,
        payload = modelReq.payload,
      ),
      state = CSContextState.Finishing,
    )
  }
}

private suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.stubMessageCreationNotFoundChat() = worker {
  this.title = "Кейс провала. Не найден чат для создания сообщения"
  onRunning {
    workMode.isStubNotFound()
        && modelReq.chatId == UUID.fromString("00000000-0000-0000-0000-000000000020")
  }
  handle {
    fail(
      CSError(
        code = "not-found-chat-for-message-creation",
        group = "not-found",
        field = mapOf("chatId" to this.modelReq.chatId.toString()).toString(),
        message = "Ошибка при попытке сохранить сообщение. Чат не найден",
      )
    )
  }
}

private suspend fun ICorChainDsl<CSContext<MessageCreation, Message?>>.stubMessageCreationDBError() = worker {
  this.title = "Кейс провала создания сообщения. База данных недоступна"
  onRunning { workMode.isStubDbError() }
  handle {
    fail(
      CSError(
        code = "internal-db-error",
        group = "internal",
        field = buildMap {
          put("chatId", modelReq.chatId.toString())
          modelReq.id?.let { put("messageId", it.toString()) }
        }.toString(),
        message = "Ошибка при попытке сохранить сообщение. База данных недоступна",
      )
    )
  }
}
