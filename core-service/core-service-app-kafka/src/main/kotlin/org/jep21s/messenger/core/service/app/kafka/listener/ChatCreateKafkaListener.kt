package org.jep21s.messenger.core.service.app.kafka.listener

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jep21s.messenger.core.service.api.v1.mapper.ChatMapperImpl
import org.jep21s.messenger.core.service.api.v1.models.CSResponse
import org.jep21s.messenger.core.service.api.v1.models.ChatCreateReq
import org.jep21s.messenger.core.service.api.v1.models.ChatResp
import org.jep21s.messenger.core.service.app.kafka.config.KafkaConsumerProperties
import org.jep21s.messenger.core.service.app.kafka.config.KafkaListener
import org.jep21s.messenger.core.service.app.kafka.config.KafkaProperties
import org.jep21s.messenger.core.service.app.kafka.constants.Topic
import org.jep21s.messenger.core.service.app.kafka.extention.processRequest
import org.jep21s.messenger.core.service.app.kafka.service.KafkaSender
import org.jep21s.messenger.core.service.common.model.chat.Chat

class ChatCreateKafkaListener(
  private val kafkaProperties: KafkaProperties,
  kafkaConsumerProperties: KafkaConsumerProperties,
) : AutoCloseable {
  private val kafkaListener: KafkaListener = KafkaListener(
    kafkaProperties,
    kafkaConsumerProperties
  )

  suspend fun listen() = withContext(Dispatchers.IO) {
    kafkaListener.listen { message ->
      message.processRequest("chat create kafka") {
        mapRequestToModel { request: ChatCreateReq ->
          ChatMapperImpl.mapToModel(request)
        }
        mapResultToResponse { result: Chat ->
          ChatMapperImpl.mapToResponse(result)
        }
        respond { response: CSResponse ->
          KafkaSender.send(
            hosts = kafkaProperties.hosts,
            topic = Topic.CHAT_CREATE_RESP,
            key = (response.content as ChatResp).id?.toString(),
            value = response,
          )
        }
      }
    }
  }

  override fun close() {
    kafkaListener.close()
  }
}