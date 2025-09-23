package org.jep21s.messenger.core.service.api.v1.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugMode
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugStubs
import org.jep21s.messenger.core.service.api.v1.models.IRequest
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextCommand
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSStub
import org.jep21s.messenger.core.service.common.context.CSWorkMode

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface CSContextMapper {
  fun mapToModel(
    request: IRequest,
  ): CSWorkMode = when (request.debug?.mode) {
    CmRequestDebugMode.PROD -> CSWorkMode.Prod
    CmRequestDebugMode.TEST -> CSWorkMode.Test
    CmRequestDebugMode.STUB -> CSWorkMode.Stub(
      mapToModel(requireNotNull(request.debug?.stub))
    )

    null -> CSWorkMode.Prod
  }

  fun mapToModel(request: CmRequestDebugStubs): CSStub

  fun mapToModel(requestType: String?): CSContextCommand =
    CSContextCommand.valueOf(requireNotNull(requestType))
}

fun <MReq, MResp> CSContextMapper.mapToContext(
  request: IRequest,
  modelReq: MReq,
): CSContext<MReq, MResp?> = CSContext(
  command = mapToModel(request.requestType),
  state = CSContextState.None,
  workMode = mapToModel(request),
  modelReq = modelReq,
  modelResp = null
)
