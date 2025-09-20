package org.jep21s.messenger.core.service.app.web.extention

import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.logback.mpLoggerLogback
import org.jep21s.messenger.core.service.api.v1.asCSErrorResp
import org.jep21s.messenger.core.service.api.v1.mapper.CSContextMapperImpl
import org.jep21s.messenger.core.service.api.v1.mapper.mapToContext
import org.jep21s.messenger.core.service.api.v1.models.CSResponse
import org.jep21s.messenger.core.service.api.v1.models.IRequest
import org.jep21s.messenger.core.service.api.v1.models.ResponseResult
import org.jep21s.messenger.core.service.biz.processor.CSProcessorFactory
import org.jep21s.messenger.core.service.biz.processor.getCSProcessor
import org.jep21s.messenger.core.service.common.context.CSContext

val log = CMLoggerProvider { className: String -> mpLoggerLogback(className) }
  .logger(RoutingContext::class)

suspend inline fun <
    reified Req : IRequest,
    reified Resp,
    reified MReq,
    reified MResp,
    > RoutingContext.processRequest(
  actionName: String,
  mapRequestToModel: (Req) -> MReq,
  mapResultToResponse: (MResp) -> Resp,
) {
  try {
    log.info(
      msg = "[$actionName] request started",
      marker = "ROUTE",
    )
    val request: Req = call.receive()
    val modelRequest: MReq = mapRequestToModel(request)
    val context: CSContext<MReq, MResp?> = CSContextMapperImpl.mapToContext(
      request = request,
      modelReq = modelRequest
    )
    log.info(
      msg = "Got [$actionName] context [$context]",
      marker = "ROUTE",
    )
    val processor = CSProcessorFactory
      .getCSProcessor(context)
    log.info("Got processor [${processor::class.simpleName}] for [$actionName]")
    val resultContext: CSContext<MReq, MResp?> = processor.exec(context)
    log.info(
      msg = "Got [$actionName] result context [$resultContext]",
      marker = "ROUTE",
    )
    val response: Resp = mapResultToResponse(requireNotNull(resultContext.modelResp))
    val responseWrapper = CSResponse(
      result = ResponseResult.SUCCESS,
      content = response,
    )
    log.info(
      msg = "Got [$actionName] response result: [$responseWrapper]",
      marker = "ROUTE",
    )
    call.respond(responseWrapper)
  } catch (ex: Throwable) {
    log.error(
      msg = "Error in trying handle [$actionName] request: [${call.receiveText()}]",
      marker = "ROUTE",
      ex = ex,
    )
    call.respond(
      CSResponse(
        result = ResponseResult.ERROR,
        errors = listOf(ex.asCSErrorResp()),
      )
    )
  }

}

class ProcessRequestDsl<Req : IRequest, Resp, MReq, MResp> {
  private lateinit var _mapRequestToModel: (Req) -> MReq
  private lateinit var _mapResultToResponse: (MResp) -> Resp

  fun mapRequestToModel(block: (Req) -> MReq) {
    _mapRequestToModel = block
  }

  fun mapResultToResponse(block: (MResp) -> Resp) {
    _mapResultToResponse = block
  }

  fun build(): ProcessRequestConfig<Req, Resp, MReq, MResp> {
    return ProcessRequestConfig(
      mapRequestToModel = _mapRequestToModel,
      mapResultToResponse = _mapResultToResponse
    )
  }
}

data class ProcessRequestConfig<Req : IRequest, Resp, MReq, MResp>(
  val mapRequestToModel: (Req) -> MReq,
  val mapResultToResponse: (MResp) -> Resp,
)

suspend inline fun <
    reified Req : IRequest,
    reified Resp,
    reified MReq,
    reified MResp,
    > RoutingContext.processRequest(
  actionName: String,
  crossinline config: ProcessRequestDsl<Req, Resp, MReq, MResp>.() -> Unit,
) {
  val dsl = ProcessRequestDsl<Req, Resp, MReq, MResp>()
    .apply(config)
  val configObj = dsl.build()

  processRequest<Req, Resp, MReq, MResp>(
    actionName = actionName,
    mapRequestToModel = configObj.mapRequestToModel,
    mapResultToResponse = configObj.mapResultToResponse
  )
}