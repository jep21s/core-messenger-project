package org.jep21s.messenger.core.service.biz.processor.impl.message.stubs

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.chainStub
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.context.isRunning
import org.jep21s.messenger.core.service.common.context.isStubSuccess
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion

fun ICorChainDsl<CSContext<MessageDeletion, MessageDeleted?>>.stubsMessageDeletion() {
  chainStub {
    this.title = "Обработка стабов удаления сообщения"
    on { workMode is CSWorkMode.Stub && state.isRunning() }
    stubSuccessMessageDeletion()
  }
}

private fun ICorChainDsl<CSContext<MessageDeletion, MessageDeleted?>>.stubSuccessMessageDeletion() = worker {
  this.title = "Кейс успеха удаления сообщения"
  on { workMode.isStubSuccess() && state.isRunning() }
  handle {
    copy(
      modelResp = MessageDeleted(
        ids = modelReq.ids,
        communicationType = modelReq.communicationType,
      ),
      state = CSContextState.Finishing,
    )
  }
}