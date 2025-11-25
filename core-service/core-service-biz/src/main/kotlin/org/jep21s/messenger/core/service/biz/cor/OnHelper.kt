package org.jep21s.messenger.core.service.biz.cor

import org.jep21s.messenger.core.lib.cor.dsl.ICorWorkerDsl
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.isRunning

suspend inline fun <MReq, MResp> ICorWorkerDsl<CSContext<MReq, MResp>>.onRunning() {
  onRunning { true }
}

suspend inline fun <MReq, MResp> ICorWorkerDsl<CSContext<MReq, MResp>>.onRunning(
  crossinline block: suspend CSContext<MReq, MResp>.() -> Boolean,
) {
  on {
    if (!state.isRunning()) return@on false
    return@on block()
  }
}