package org.jep21s.messenger.core.service.app.web.route

import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jep21s.messenger.core.service.api.v1.mapper.MessageMapperImpl
import org.jep21s.messenger.core.service.api.v1.mapper.MessageStatusMapperImpl
import org.jep21s.messenger.core.service.api.v1.models.MessageCreateReq
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReq
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateReq
import org.jep21s.messenger.core.service.app.web.extention.processRequest
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdated

fun Route.messageV1() {
  route("/v1/message") {
    post("/create") {
      processRequest("message create") {
        mapRequestToModel { request: MessageCreateReq ->
          MessageMapperImpl.mapToModel(request)
        }
        mapResultToResponse { result: Message ->
          MessageMapperImpl.mapToResponse(result)
        }
      }
    }

    post("/delete") {
      processRequest("message delete") {
        mapRequestToModel { request: MessageDeleteReq ->
          MessageMapperImpl.mapToModel(request)
        }
        mapResultToResponse { result: MessageDeleted ->
          MessageMapperImpl.mapToDeleteResponse(result)
        }
      }
    }

    post("/search") {
      processRequest("message search") {
        mapRequestToModel { request: MessageSearchReq ->
          MessageMapperImpl.mapToModel(request)
        }
        mapResultToResponse { result: List<Message> ->
          result.map { MessageMapperImpl.mapToResponse(it) }
        }
      }
    }

    post("/status/update") {
      processRequest("message status update") {
        mapRequestToModel { request: MessageStatusUpdateReq ->
          MessageStatusMapperImpl.mapToModel(request)
        }
        mapResultToResponse { result: MessageStatusUpdated ->
          MessageStatusMapperImpl.mapToUpdateResponse(result)
        }
      }
    }
  }
}