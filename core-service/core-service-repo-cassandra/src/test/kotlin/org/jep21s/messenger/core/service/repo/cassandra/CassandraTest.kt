package org.jep21s.messenger.core.service.repo.cassandra

import java.io.File
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.logback.cmLoggerLogback
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.messageSearch
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraMapper
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraProperties
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraSessionProvider
import org.jep21s.messenger.core.service.repo.cassandra.config.liquibase.LiquibaseConfig
import org.jep21s.messenger.core.service.repo.cassandra.message.MessageDao
import org.jep21s.messenger.core.service.repo.cassandra.message.MessageRepoCassandra
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.cassandra.test.extention.CassandraTestExtension
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.junit.jupiter.Container

@ExtendWith(CassandraTestExtension::class)
class CassandraTest {

  @Test
  fun checkVersions() {
    println("Testcontainers version: ${org.testcontainers.utility.TestcontainersConfiguration.getInstance().userProperties}")

    val client = org.testcontainers.DockerClientFactory.lazyClient()
    try {
      val version = client.versionCmd().exec()
      println("Docker API Version: ${version.apiVersion}")
      println("Docker Server Version: ${version.version}")
    } catch (e: Exception) {
      println("Error getting Docker version: ${e.message}")
    }
  }

  @Test
  fun test() = runTest {
    CSCorSettings.initialize(
      loggerProvider = CMLoggerProvider { clazz -> cmLoggerLogback(clazz) }
    )
    val messageRepo = messageRepository()
    val messageCreation = MessageCreation(
      id = UUID.randomUUID(),
      chatId = UUID.randomUUID(),
      communicationType = "TG",
      messageType = "simple",
      sentDate = Instant.now(),
      body = "bla",
      externalId = null,
      payload = mapOf("myField" to "myValue")
    )
    val message = messageRepo.save(messageCreation)
    assertThat(message.id)
      .isEqualTo(messageCreation.id)
//    messageRepo.save(messageCreation)

    val result = messageRepo.search(messageSearch {
      chatFilter {
        id { message.chatId }
        communicationType { message.communicationType }
      }
      messageFilter {
        id { message.id }
        sentDate {
          ComparableFilter(
            value = message.sentDate,
            direction = ConditionType.EQUAL,
          )
        }
      }
    })

    assertAll(
      {
        assertThat(result.size)
          .isEqualTo(1)
      },
      {
        assertThat(result.first().payload)
          .isEqualTo(mapOf("myField" to "myValue"))
      }
    )
  }

  //  @Ignore
  companion object {
    private const val CS_SERVICE = "cassandra"
    private val properties = CassandraProperties()

    val logger: Logger = LoggerFactory.getLogger(ComposeContainer::class.java)

    val resDc = this::class.java.classLoader.getResource("docker-compose-cs.yml")
      ?: throw Exception("No resource found")
    val fileDc = File(resDc.toURI())
    val logConsumer = Slf4jLogConsumer(logger)

    @Container
    private val container: ComposeContainer = ComposeContainer(fileDc)
        .withExposedService(CS_SERVICE, properties.port)
        .withStartupTimeout(Duration.ofMinutes(10))
        .withLogConsumer(CS_SERVICE, logConsumer)


    fun messageRepository(): MessageRepoCassandra {
      val properties = CassandraProperties()
      val sessionProvider = CassandraSessionProvider(properties)
      LiquibaseConfig(properties).runMigrations()
      val messageDao: MessageDao = CassandraMapper.getInstant(sessionProvider.session).getMessageDAO(
        properties.keyspaceName,
        MessageEntity.TABLE_NAME,
      )
      return MessageRepoCassandra(messageDao)
//        .apply { clear() }
    }

    @JvmStatic
    @BeforeAll
    fun start() {
      container.start()
    }

    @JvmStatic
    @AfterAll
    fun finish() {
      container.stop()
    }
  }

}