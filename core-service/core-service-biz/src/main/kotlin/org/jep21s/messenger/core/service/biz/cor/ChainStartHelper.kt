package org.jep21s.messenger.core.service.biz.cor

import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.rootChain
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState

suspend fun <MReq, MResp> ICorChainDsl<CSContext<MReq, MResp>>.initStatus() = worker {
  this.title = "Инициализация стартового статуса обработки"
  this.description = "Этот обработчик устанавливает стартовый статус обработки. " +
      "Запускается только в случае не заданного статуса."
  on { state == CSContextState.None }
  handle { copy(state = CSContextState.Running) }
}

suspend inline fun <MReq, MResp> runChain(
  context: CSContext<MReq, MResp>,
  crossinline block: suspend ICorChainDsl<CSContext<MReq, MResp>>.() -> Unit,
): CSContext<MReq, MResp> = rootChain {
  initStatus()
  block()
}
  .build()
  .exec(context)
