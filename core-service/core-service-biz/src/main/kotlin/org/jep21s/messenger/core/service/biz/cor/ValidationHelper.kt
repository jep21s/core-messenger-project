package org.jep21s.messenger.core.service.biz.cor

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.isRunning

suspend fun <MReq, MResp> ICorChainDsl<CSContext<MReq, MResp>>.validation(
  block: suspend ICorChainDsl<CSContext<MReq, MResp>>.() -> Unit,
) = chain {
  title = "Валидация"
  block()
  on { state.isRunning() }
}

