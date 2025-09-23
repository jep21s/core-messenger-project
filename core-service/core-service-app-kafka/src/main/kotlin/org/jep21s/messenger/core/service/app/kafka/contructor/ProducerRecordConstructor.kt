package org.jep21s.messenger.core.service.app.kafka.contructor

fun <K, V> ProducerRecord(
  topic: String,
  key: K? = null,
  value: V,
) = org.apache.kafka.clients.producer.ProducerRecord<K, V>(
  /* topic = */ topic,
  /* key = */ key,
  /* value = */ value
)