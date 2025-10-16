package org.jep21s.messenger.core.service.app.web.extention

import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
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
  crossinline config: ProcessRequestDsl<Req, Resp, MReq, MResp>.() -> Unit,
) = processRequest<Req, Resp, MReq, MResp>(
  actionName = actionName,
  config = config,
  receive = { call.receive() },
  respond = { cSResponse -> call.respond(cSResponse) },
  logger = logger.value,
)