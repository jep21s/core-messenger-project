package org.jep21s.messenger.core.service.app.kafka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jep21s.messenger.core.service.app.kafka.config.KafkaConsumerProperties
import org.jep21s.messenger.core.service.app.kafka.config.KafkaProperties
import org.jep21s.messenger.core.service.app.kafka.constants.GroupId
import org.jep21s.messenger.core.service.app.kafka.constants.Topic
import org.jep21s.messenger.core.service.app.kafka.listener.ChatCreateKafkaListener

fun main(): Unit = runBlocking {
  val kafkaProperties = KafkaProperties(
    hosts = listOf("localhost:9092")
  )
  launch(Dispatchers.IO) {
    ChatCreateKafkaListener(
      kafkaProperties,
      KafkaConsumerProperties(
        groupId = GroupId.CORE_SERVICE_CHAT_CREATE_CONSUMER,
        topic = Topic.CHAT_CREATE_REQ,
      )
    ).listen()
  }
}