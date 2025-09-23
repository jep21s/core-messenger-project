package org.jep21s.messenger.core.service.app.web.route

import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jep21s.messenger.core.service.api.v1.mapper.MessageMapper
import org.jep21s.messenger.core.service.api.v1.mapper.MessageMapperImpl
import org.jep21s.messenger.core.service.api.v1.mapper.MessageStatusMapper
import org.jep21s.messenger.core.service.api.v1.mapper.MessageStatusMapperImpl
import org.jep21s.messenger.core.service.api.v1.models.MessageCreateReq
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReq
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateReq
import org.jep21s.messenger.core.service.app.web.extention.processRequest
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdated

private val messageMapper: MessageMapper = MessageMapperImpl
private val messageStatusMapper: MessageStatusMapper = MessageStatusMapperImpl

fun Route.messageV1() {
  route("/v1/message") {
    post("/create") {
      processRequest("message create") {
        mapRequestToModel { request: MessageCreateReq ->
          messageMapper.mapToModel(request)
        }
        mapResultToResponse { result: Message ->
          messageMapper.mapToResponse(result)
        }
      }
    }

    post("/delete") {
      processRequest("message delete") {
        mapRequestToModel { request: MessageDeleteReq ->
          messageMapper.mapToModel(request)
        }
        mapResultToResponse { result: MessageDeleted ->
          messageMapper.mapToDeleteResponse(result)
        }
      }
    }

    post("/search") {
      processRequest("message search") {
        mapRequestToModel { request: MessageSearchReq ->
          messageMapper.mapToModel(request)
        }
        mapResultToResponse { result: List<Message> ->
          result.map { messageMapper.mapToResponse(it) }
        }
      }
    }

    post("/status/update") {
      processRequest("message status update") {
        mapRequestToModel { request: MessageStatusUpdateReq ->
          messageStatusMapper.mapToModel(request)
        }
        mapResultToResponse { result: MessageStatusUpdated ->
          messageStatusMapper.mapToUpdateResponse(result)
        }
      }
    }
  }
}