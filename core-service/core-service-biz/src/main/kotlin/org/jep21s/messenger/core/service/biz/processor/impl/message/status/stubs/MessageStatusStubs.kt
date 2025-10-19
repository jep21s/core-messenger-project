package org.jep21s.messenger.core.service.biz.processor.impl.message.status.stubs

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.chainStub
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.context.isRunning
import org.jep21s.messenger.core.service.common.context.isStubSuccess
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdated
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdation

fun ICorChainDsl<CSContext<MessageStatusUpdation, MessageStatusUpdated?>>.stubsMessageStatusUpdation() {
  chainStub {
    this.title = "Обработка стабов изменения статусов сообщений"
    on { workMode is CSWorkMode.Stub && state.isRunning() }
    stubSuccessMessageStatusUpdation()
  }
}

private fun ICorChainDsl<CSContext<MessageStatusUpdation, MessageStatusUpdated?>>.stubSuccessMessageStatusUpdation() =
  worker {
    this.title = "Кейс успеха обновления статусов сообщений"
    on { workMode.isStubSuccess() && state.isRunning() }
    handle {
      copy(
        modelResp = MessageStatusUpdated(
          ids = modelReq.ids,
          communicationType = modelReq.communicationType,
          newStatus = modelReq.newStatus
        ),
        state = CSContextState.Finishing,
      )
    }
  }
