package org.jep21s.messenger.core.service.app.kafka.test

import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.utility.DockerImageName

object KafkaContainer {
  private val kafka: ConfluentKafkaContainer =
    ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.1"))
      .apply { start() }

  val bootstrapServers: String = kafka.bootstrapServers
}