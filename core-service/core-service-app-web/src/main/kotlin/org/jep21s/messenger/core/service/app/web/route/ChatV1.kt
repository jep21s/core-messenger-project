package org.jep21s.messenger.core.service.app.web.route

import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jep21s.messenger.core.service.api.v1.mapper.ChatMapper
import org.jep21s.messenger.core.service.api.v1.mapper.ChatMapperImpl
import org.jep21s.messenger.core.service.api.v1.models.ChatCreateReq
import org.jep21s.messenger.core.service.api.v1.models.ChatDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReq
import org.jep21s.messenger.core.service.app.web.extention.processRequest
import org.jep21s.messenger.core.service.common.model.chat.Chat

private val chatMapper: ChatMapper = ChatMapperImpl

fun Route.chatV1() {
  route("/v1/chat") {
    post("/create") {
      processRequest("chat create") {
        mapRequestToModel { request: ChatCreateReq ->
          chatMapper.mapToModel(request)
        }
        mapResultToResponse { result: Chat ->
          chatMapper.mapToResponse(result)
        }
      }
    }

    post("/delete") {
      processRequest("chat delete") {
        mapRequestToModel { request: ChatDeleteReq ->
          chatMapper.mapToModel(request)
        }
        mapResultToResponse { result: Chat ->
          chatMapper.mapToResponse(result)
        }
      }
    }

    post("/search") {
      processRequest("chat search") {
        mapRequestToModel { request: ChatSearchReq ->
          chatMapper.mapToModel(request)
        }
        mapResultToResponse { result: List<Chat> ->
          result.map { chatMapper.mapToResponse(it) }
        }
      }
    }
  }
}

