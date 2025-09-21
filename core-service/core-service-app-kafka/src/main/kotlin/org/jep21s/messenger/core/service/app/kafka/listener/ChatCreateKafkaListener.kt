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
import org.jep21s.messenger.core.service.app.kafka.extention.processRequest
import org.jep21s.messenger.core.service.app.kafka.service.KafkaSender
import org.jep21s.messenger.core.service.common.model.chat.Chat

object ChatCreateKafkaListener {
  suspend fun listen(
    kafkaProperties: KafkaProperties,
    kafkaConsumerProperties: KafkaConsumerProperties,
  ) = withContext(Dispatchers.IO) {
      KafkaListener(kafkaProperties, kafkaConsumerProperties).listen { message ->
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
              topic = "chat-create-resp",
              key = (response.content as ChatResp).id?.toString(),
              value = response,
            )
          }
        }
      }
    }
}