package org.jep21s.messenger.core.service.app.kafka.config

import java.util.Properties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

object KafkaFactory {
  fun createKafkaProducer(properties: KafkaProperties): KafkaProducer<String, String> {
    val props = Properties().apply {
      put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.hosts)
      put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
      put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
    }
    return KafkaProducer<String, String>(props)
  }

  fun createKafkaConsumer(
    kafkaProperties: KafkaProperties,
    kafkaConsumerProperties: KafkaConsumerProperties,
  ): KafkaConsumer<String, String> {
    val props = Properties().apply {
      put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.hosts)
      put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.groupId)
      put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
      put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
      put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
    }
    return KafkaConsumer<String, String>(props)
  }
}