package org.jep21s.messenger.core.service.app.kafka.config

data class KafkaConsumerProperties(
  val groupId: String,
  val topic: String,
)

