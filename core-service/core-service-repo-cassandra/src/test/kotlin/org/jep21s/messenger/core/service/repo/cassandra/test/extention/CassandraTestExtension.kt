package org.jep21s.messenger.core.service.repo.cassandra.test.extention

import java.io.File
import java.time.Duration
import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.logback.cmLoggerLogback
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraProperties
import org.jep21s.messenger.core.service.repo.cassandra.config.liquibase.LiquibaseConfig
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.junit.jupiter.Container

class CassandraTestExtension : BeforeAllCallback, AfterAllCallback {
  override fun beforeAll(context: ExtensionContext) {
    if (!containerStarted) {
      println("Starting Cassandra container for tests...")
      container.start()

      // Запуск миграций Liquibase
      LiquibaseConfig(properties).runMigrations()

      containerStarted = true
      println("Cassandra container started successfully")
    }
  }

  override fun afterAll(context: ExtensionContext) {
    // Контейнер будет остановлен только когда все тесты завершатся
    // JVM сама закроет контейнер при завершении
  }

  companion object {
    private var containerStarted = false

    private const val CS_SERVICE = "cassandra"
    private val properties = CassandraProperties()

    val logger: Logger = LoggerFactory.getLogger(ComposeContainer::class.java)
    val resDc = CassandraTestExtension::class.java.classLoader.getResource("docker-compose-cs.yml")
      ?: throw Exception("No resource found: docker-compose-cs.yml")
    val fileDc = File(resDc.toURI())
    val logConsumer = Slf4jLogConsumer(logger)

    @Container
    val container: ComposeContainer = ComposeContainer(fileDc)
      .withExposedService(CS_SERVICE, properties.port)
      .withStartupTimeout(Duration.ofMinutes(10))
      .withLogConsumer(CS_SERVICE, logConsumer)

    init {
      // Инициализация выполняется один раз при первом обращении
      runCatching {
        CSCorSettings.initialize(
          loggerProvider = CMLoggerProvider { clazz -> cmLoggerLogback(clazz) }
        )
      }
    }
  }
}