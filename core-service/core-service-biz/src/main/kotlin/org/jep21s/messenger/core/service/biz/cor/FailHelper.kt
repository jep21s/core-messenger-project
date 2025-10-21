package org.jep21s.messenger.core.service.biz.cor

import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSError

fun <MReq, MResp> CSContext<MReq, MResp>.fail(
  csError: CSError,
): CSContext<MReq, MResp> = fail(listOf(csError))

fun <MReq, MResp> CSContext<MReq, MResp>.fail(
  csErrors: List<CSError>,
): CSContext<MReq, MResp> = when (state) {
  is CSContextState.Failing -> {
    val currentErrors = (state as CSContextState.Failing).errors
    val updatedState = CSContextState.Failing(currentErrors + csErrors)
    copy(state = updatedState)
  }

  else -> copy(state = CSContextState.Failing(csErrors))
}