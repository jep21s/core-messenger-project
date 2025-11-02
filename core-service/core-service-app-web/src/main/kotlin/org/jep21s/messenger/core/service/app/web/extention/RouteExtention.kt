package org.jep21s.messenger.core.service.app.web.extention

import arrow.core.Either
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import org.jep21s.messenger.core.service.api.v1.mapper.CSErrorMapperImpl
import org.jep21s.messenger.core.service.api.v1.mapper.helper.MappingNullError
import org.jep21s.messenger.core.service.api.v1.models.IRequest
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.app.common.ProcessRequestDsl
import org.jep21s.messenger.core.service.app.common.processRequest

val logger = lazy { CSCorSettings.loggerProvider.logger(RoutingContext::class) }

suspend inline fun <
    reified Req : IRequest,
    reified Resp,
    reified MReq,
    reified MResp,
    > RoutingContext.processRequest(
  actionName: String,
  crossinline config: RouteProcessRequestDsl<Req, Resp, MReq, MResp>.() -> Unit,
) {
  val dsl = RouteProcessRequestDsl<Req, Resp, MReq, MResp>()
    .apply(config)

  processRequest<Req, Resp, MReq, MResp>(
    actionName = actionName,
    config = dsl.getConfig(),
    receive = { call.receive() },
    respond = { cSResponse -> call.respond(cSResponse) },
    logger = logger.value,
  )
}

class RouteProcessRequestDsl<Req : IRequest, Resp, MReq, MResp> {
  private lateinit var _rMapRequestToModel: (Req) -> Either<MappingNullError, MReq>
  private lateinit var _rMapResultToResponse: (MResp) -> Resp

  fun mapRequestToModel(block: (Req) -> Either<MappingNullError, MReq>) {
    _rMapRequestToModel = block
  }

  fun mapResultToResponse(block: (MResp) -> Resp) {
    _rMapResultToResponse = block
  }

  fun getConfig(): ProcessRequestDsl<Req, Resp, MReq, MResp>.() -> Unit = {
    mapRequestToModel { _rMapRequestToModel(it) }
    mapResultToResponse { _rMapResultToResponse(it) }
    mapErrorToResponse { CSErrorMapperImpl.mapToResponse(it) }
  }
}
