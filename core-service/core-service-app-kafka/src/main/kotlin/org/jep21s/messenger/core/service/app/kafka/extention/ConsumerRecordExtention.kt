package org.jep21s.messenger.core.service.app.kafka.extention

import arrow.core.Either
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.logback.mpLoggerLogback
import org.jep21s.messenger.core.service.api.v1.ApiV1Mapper
import org.jep21s.messenger.core.service.api.v1.mapper.helper.MappingNullError
import org.jep21s.messenger.core.service.api.v1.models.CSResponse
import org.jep21s.messenger.core.service.api.v1.models.IRequest
import org.jep21s.messenger.core.service.app.common.ProcessRequestDsl
import org.jep21s.messenger.core.service.app.common.processRequest
import org.jep21s.messenger.core.service.app.kafka.config.KafkaListener

val log = CMLoggerProvider { className: String -> mpLoggerLogback(className) }
  .logger(KafkaListener::class)

suspend inline fun <
    reified Req : IRequest,
    reified Resp,
    reified MReq,
    reified MResp,
    > ConsumerRecord<String, String>.processRequest(
  actionName: String,
  crossinline kconfig: KafkaProcessRequestDsl<Req, Resp, MReq, MResp>.() -> Unit,
) {
  val dsl = KafkaProcessRequestDsl<Req, Resp, MReq, MResp>()
    .apply(kconfig)
  processRequest<Req, Resp, MReq, MResp>(
    actionName = actionName,
    config = dsl.getConfig(),
    receive = { ApiV1Mapper.jacksonMapper.readValue(value()) },
    respond = dsl.getRespond(),
    log = log,
  )
}

class KafkaProcessRequestDsl<Req : IRequest, Resp, MReq, MResp> {
  private lateinit var _kMapRequestToModel: (Req) -> Either<MappingNullError, MReq>
  private lateinit var _kMapResultToResponse: (MResp) -> Resp
  private lateinit var _respond: (CSResponse) -> Unit

  fun mapRequestToModel(block: (Req) -> Either<MappingNullError, MReq>) {
    _kMapRequestToModel = block
  }

  fun mapResultToResponse(block: (MResp) -> Resp) {
    _kMapResultToResponse = block
  }

  fun respond(block: (CSResponse) -> Unit) {
    _respond = block
  }

  fun getConfig(): ProcessRequestDsl<Req, Resp, MReq, MResp>.() -> Unit = {
    mapRequestToModel { _kMapRequestToModel(it) }
    mapResultToResponse { _kMapResultToResponse(it) }
  }

  fun getRespond() = _respond
}
