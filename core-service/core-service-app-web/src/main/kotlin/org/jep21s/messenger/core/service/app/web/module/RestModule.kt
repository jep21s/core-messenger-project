package org.jep21s.messenger.core.service.app.web.module

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jep21s.messenger.core.lib.logging.common.ICMLogWrapper
import org.jep21s.messenger.core.service.api.v1.ApiV1Mapper
import org.jep21s.messenger.core.service.api.v1.asCSErrorResp
import org.jep21s.messenger.core.service.api.v1.models.CSResponse
import org.jep21s.messenger.core.service.api.v1.models.ResponseResult
import org.jep21s.messenger.core.service.app.web.route.chatV1
import org.jep21s.messenger.core.service.app.web.route.messageV1
import org.jep21s.messenger.core.service.common.CSCorSettings

fun Application.restModule() {
  install(ContentNegotiation) {
    jackson {
      setConfig(ApiV1Mapper.jacksonMapper.serializationConfig)
      setConfig(ApiV1Mapper.jacksonMapper.deserializationConfig)
    }
  }
  install(StatusPages) {
    val logger: ICMLogWrapper = CSCorSettings.loggerProvider.logger(this::class)
    exception<Throwable> { call: ApplicationCall, ex: Throwable ->
      logger.error(
        msg = "Uncaught exception",
        ex = ex,
      )
      call.respond(
        message = CSResponse(
          result = ResponseResult.ERROR,
          errors = listOf(ex.asCSErrorResp()),
        ),
        status = HttpStatusCode.InternalServerError
      )
    }
  }
  install(DefaultHeaders)
  install(DoubleReceive)

  routing {
    get("/") {
      call.respondText("Hello World!")
    }
    chatV1()
    messageV1()
  }
}