package org.jep21s.messenger.core.service.biz.cor

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.context.CSWorkMode

suspend fun <MReq, MResp> ICorChainDsl<CSContext<MReq, MResp>>.stubNoCase() = worker {
  title = "Запрошенный кейс не поддерживается"
  onRunning { workMode is CSWorkMode.Stub }
  handle {
    copy(
      state = CSContextState.Failing(
        listOf(
          CSError(
            code = "stub-no-case",
            group = "stub",
            field = "stub",
            message = "Кейс в стабах не поддерживается",
          )
        )
      )
    )
  }
}

suspend inline fun <MReq, MResp> ICorChainDsl<CSContext<MReq, MResp>>.chainStub(
  crossinline block: suspend ICorChainDsl<CSContext<MReq, MResp>>.() -> Unit,
) = chain {
  block()
  stubNoCase()
}