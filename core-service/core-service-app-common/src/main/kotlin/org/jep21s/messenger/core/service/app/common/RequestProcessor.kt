package org.jep21s.messenger.core.service.app.common

import arrow.core.Either
import arrow.core.getOrElse
import org.jep21s.messenger.core.lib.logging.common.ICMLogWrapper
import org.jep21s.messenger.core.service.api.v1.asCSErrorResp
import org.jep21s.messenger.core.service.api.v1.mapper.CSContextMapper
import org.jep21s.messenger.core.service.api.v1.mapper.CSContextMapperImpl
import org.jep21s.messenger.core.service.api.v1.mapper.helper.MappingNullError
import org.jep21s.messenger.core.service.api.v1.mapper.mapToContext
import org.jep21s.messenger.core.service.api.v1.models.CSErrorResp
import org.jep21s.messenger.core.service.api.v1.models.CSResponse
import org.jep21s.messenger.core.service.api.v1.models.IRequest
import org.jep21s.messenger.core.service.api.v1.models.ResponseResult
import org.jep21s.messenger.core.service.biz.processor.CSProcessorFactory
import org.jep21s.messenger.core.service.biz.processor.getCSProcessor
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSError

val csContextMapper: CSContextMapper = CSContextMapperImpl

suspend inline fun <
    reified Req : IRequest,
    reified Resp,
    reified MReq,
    reified MResp,
    > processRequest(
  actionName: String,
  mapRequestToModel: (Req) -> Either<MappingNullError, MReq>,
  mapResultToResponse: (MResp) -> Resp,
  mapErrorToResponse: (CSError) -> CSErrorResp,
  receive: () -> Req,
  respond: (CSResponse) -> Unit,
  logger: ICMLogWrapper,
) {
  try {
    logger.info(
      msg = "[$actionName] request started",
      marker = "ROUTE",
    )
    val request: Req = receive()
    val modelRequest: MReq = mapRequestToModel(request).getOrElse {
      throw MappingNullException(it)
    }
    val context: CSContext<MReq, MResp?> = csContextMapper.mapToContext(
      request = request,
      modelReq = modelRequest
    )
    logger.info(
      msg = "Got [$actionName] context [$context]",
      marker = "ROUTE",
    )
    val processor = CSProcessorFactory
      .getCSProcessor(context)
    logger.info("Got processor [${processor::class.simpleName}] for [$actionName]")
    val resultContext: CSContext<MReq, MResp?> = processor.exec(context)
    logger.info(
      msg = "Got [$actionName] result context [$resultContext]",
      marker = "ROUTE",
    )

    val responseWrapper = when (resultContext.state) {
      is CSContextState.Finishing -> {
        val response: Resp? = resultContext.modelResp?.let { mapResultToResponse(it) }
        CSResponse(
          result = ResponseResult.SUCCESS,
          content = response,
        )
      }

      is CSContextState.Failing -> CSResponse(
        result = ResponseResult.ERROR,
        errors = (resultContext.state as CSContextState.Failing).errors
          .map { mapErrorToResponse(it) }
      )

      is CSContextState.None -> error("Context had not started")
      is CSContextState.Running -> error("Context had not finished")
    }

    logger.info(
      msg = "Got [$actionName] response result: [$responseWrapper]",
      marker = "ROUTE",
    )
    respond(responseWrapper)
  } catch (ex: MappingNullException) {
    logger.error(
      msg = "Error in trying handle [$actionName] request, " +
          "because of null fields: [${ex.error.getAllFieldPaths()}]",
      marker = "ROUTE",
      ex = ex,
    )
    respond(
      CSResponse(
        result = ResponseResult.ERROR,
        errors = listOf(
          ex.asCSErrorResp(
            code = "required-field",
            group = "required-field",
            message = "Required fields: [${
              ex.error
                .getAllFieldPaths()
                .joinToString(", ")
            }]"
          )
        ),
      )
    )
  } catch (ex: Throwable) {
    logger.error(
      msg = "Error in trying handle [$actionName] request",
      marker = "ROUTE",
      ex = ex,
    )
    respond(
      CSResponse(
        result = ResponseResult.ERROR,
        errors = listOf(ex.asCSErrorResp()),
      )
    )
  }

}

class ProcessRequestDsl<Req : IRequest, Resp, MReq, MResp> {
  private lateinit var _mapRequestToModel: (Req) -> Either<MappingNullError, MReq>
  private lateinit var _mapResultToResponse: (MResp) -> Resp
  private lateinit var _mapErrorToResponse: (CSError) -> CSErrorResp

  fun mapRequestToModel(block: (Req) -> Either<MappingNullError, MReq>) {
    _mapRequestToModel = block
  }

  fun mapResultToResponse(block: (MResp) -> Resp) {
    _mapResultToResponse = block
  }

  fun mapErrorToResponse(block: (CSError) -> CSErrorResp) {
    _mapErrorToResponse = block
  }

  fun build(): ProcessRequestConfig<Req, Resp, MReq, MResp> {
    return ProcessRequestConfig(
      mapRequestToModel = _mapRequestToModel,
      mapResultToResponse = _mapResultToResponse,
      mapErrorToResponse = _mapErrorToResponse
    )
  }
}

data class ProcessRequestConfig<Req : IRequest, Resp, MReq, MResp>(
  val mapRequestToModel: (Req) -> Either<MappingNullError, MReq>,
  val mapResultToResponse: (MResp) -> Resp,
  val mapErrorToResponse: (CSError) -> CSErrorResp,
)

suspend inline fun <
    reified Req : IRequest,
    reified Resp,
    reified MReq,
    reified MResp,
    > processRequest(
  actionName: String,
  crossinline config: ProcessRequestDsl<Req, Resp, MReq, MResp>.() -> Unit,
  receive: () -> Req,
  respond: (CSResponse) -> Unit,
  logger: ICMLogWrapper,
) {
  val dsl = ProcessRequestDsl<Req, Resp, MReq, MResp>()
    .apply(config)
  val configObj = dsl.build()

  processRequest<Req, Resp, MReq, MResp>(
    actionName = actionName,
    mapRequestToModel = configObj.mapRequestToModel,
    mapResultToResponse = configObj.mapResultToResponse,
    mapErrorToResponse = configObj.mapErrorToResponse,
    receive = receive,
    respond = respond,
    logger = logger,
  )
}

data class MappingNullException(val error: MappingNullError) : Exception()