package org.jep21s.messenger.core.service.app.kafka.config

import java.time.Duration
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.errors.WakeupException
import org.jep21s.messenger.core.lib.logging.logback.logger

class KafkaListener(
  kafkaProperties: KafkaProperties,
  private val kafkaConsumerProperties: KafkaConsumerProperties,
) : AutoCloseable {
  private val log = logger()
  private val process = atomic(true)
  private val consumer: Consumer<String, String> = KafkaFactory
    .createKafkaConsumer(kafkaProperties, kafkaConsumerProperties)

  suspend fun listen(
    processMessage: suspend (message: ConsumerRecord<String, String>) -> Unit,
  ) {
    process.value = true
    try {
      start(processMessage)
    } catch (ex: WakeupException) {
      // ignore for shutdown
    } catch (ex: RuntimeException) {
      // exception handling
      withContext(NonCancellable) {
        throw ex
      }
    } finally {
      withContext(NonCancellable) {
        try {
          consumer.commitSync()
        } finally {
          consumer.close()
        }
      }
    }
  }

  private suspend fun start(
    processMessage: suspend (message: ConsumerRecord<String, String>) -> Unit,
  ) {
    consumer.subscribe(listOf(kafkaConsumerProperties.topic))
    while (process.value) {
      val records: ConsumerRecords<String, String> = withContext(Dispatchers.IO) {
        consumer.poll(Duration.ofSeconds(1))
      }
      if (!records.isEmpty) {
        log.debug("Receive ${records.count()} messages")
      }
      records.forEach { record: ConsumerRecord<String, String> ->
        runCatching {
          processMessage(record)
          consumer.commitAsync()
        }
          .onFailure { log.error("error", ex = it) }
      }
    }
  }

  override fun close() {
    process.value = false
  }
}

fun KafkaListener(
  hosts: List<String>,
  groupId: String,
  topic: String,
) = KafkaListener(
  kafkaProperties = KafkaProperties(hosts),
  kafkaConsumerProperties = KafkaConsumerProperties(
    groupId = groupId,
    topic = topic
  )
)