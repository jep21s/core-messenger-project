package org.jep21s.messenger.core.service.app.kafka.listener

import java.util.UUID
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.api.v1.models.ChatCreateReq
import org.jep21s.messenger.core.service.app.kafka.config.KafkaConsumerProperties
import org.jep21s.messenger.core.service.app.kafka.config.KafkaListener
import org.jep21s.messenger.core.service.app.kafka.config.KafkaProperties
import org.jep21s.messenger.core.service.app.kafka.constants.GroupId
import org.jep21s.messenger.core.service.app.kafka.constants.Topic
import org.jep21s.messenger.core.service.app.kafka.service.KafkaSender
import org.junit.jupiter.api.Assertions.*
import org.slf4j.LoggerFactory

class ChatCreateKafkaListenerTest {
  @Test
  fun `success handle chat creation message from kafka`() = runBlocking {
    //необходимо для инициализации логера, чтобы не выбрасывалось исключение ошибки кастинга,
    //так как логер junit перетирает логер из кастомной библиотеки
    LoggerFactory.getILoggerFactory()
    //Given
    val kafkaProperties = KafkaProperties(
      hosts = listOf("localhost:9092")
    )
    val chatCreateKafkaListener = ChatCreateKafkaListener(
      kafkaProperties,
      KafkaConsumerProperties(
        groupId = GroupId.CORE_SERVICE_CHAT_CREATE_CONSUMER,
        topic = Topic.CHAT_CREATE_REQ,
      )
    )
    launch(Dispatchers.IO) {
      chatCreateKafkaListener.listen()
    }
    val responses = mutableListOf<String>()
    val responseKafkaListener = KafkaListener(
      kafkaProperties,
      KafkaConsumerProperties(
        groupId = UUID.randomUUID().toString(),
        topic = Topic.CHAT_CREATE_RESP
      )
    )
    launch(Dispatchers.IO) {
      responseKafkaListener.listen { responses.add(it.value()) }
    }
    delay(1000)
    val request = ChatCreateReq(
      requestType = "CREATE_CHAT",
      communicationType = "TG",
      chatType = "simple",
    )

    //When
    KafkaSender.send(kafkaProperties.hosts, Topic.CHAT_CREATE_REQ, null, request)
    delay(1000)

    //Then
    assertAll(
      { assertThat(responses).isNotEmpty }
    )

    chatCreateKafkaListener.close()
    responseKafkaListener.close()
  }

}
