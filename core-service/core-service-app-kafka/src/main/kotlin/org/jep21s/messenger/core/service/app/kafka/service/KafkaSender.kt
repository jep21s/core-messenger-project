package org.jep21s.messenger.core.service.app.kafka.service

import org.apache.kafka.clients.producer.KafkaProducer
import org.jep21s.messenger.core.service.api.v1.ApiV1Mapper
import org.jep21s.messenger.core.service.app.kafka.config.KafkaFactory
import org.jep21s.messenger.core.service.app.kafka.config.KafkaProperties
import org.jep21s.messenger.core.service.app.kafka.contructor.ProducerRecord

object KafkaSender {
  private val producerMap: MutableMap<List<String>, KafkaProducer<String, String>> = mutableMapOf()

  fun send(
    hosts: List<String>,
    topic: String,
    key: String?,
    value: Any,
  ) {
    val producer = producerMap.computeIfAbsent(hosts) { hosts ->
      KafkaFactory.createKafkaProducer(KafkaProperties(hosts))
    }
    producer.send(
      ProducerRecord(
        topic = topic,
        key = key,
        value = ApiV1Mapper.jacksonMapper.writeValueAsString(value)
      )
    )
  }
}