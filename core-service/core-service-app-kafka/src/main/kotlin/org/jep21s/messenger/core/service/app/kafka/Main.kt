package org.jep21s.messenger.core.service.app.kafka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jep21s.messenger.core.service.app.kafka.config.KafkaConsumerProperties
import org.jep21s.messenger.core.service.app.kafka.config.KafkaProperties
import org.jep21s.messenger.core.service.app.kafka.listener.ChatCreateKafkaListener

fun main(): Unit = runBlocking {
  val kafkaProperties = KafkaProperties(
    hosts = listOf("localhost:9092")
  )
  launch(Dispatchers.IO) {
    ChatCreateKafkaListener.listen(
      kafkaProperties,
      KafkaConsumerProperties(
        groupId = "core-service-chat-create-consumer",
        topic = "chat-create-req",
      )
    )
  }
}