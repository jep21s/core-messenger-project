package org.jep21s.messenger.core.service.app.kafka.config

data class KafkaProperties(
  val hosts: List<String> = KAFKA_HOSTS,
) {
  companion object {
    private const val KAFKA_HOST_VAR = "KAFKA_HOSTS"
    private val KAFKA_HOSTS by lazy { (System.getenv(KAFKA_HOST_VAR) ?: "").split("\\s*[,; ]\\s*") }
  }
}