package org.jep21s.messenger.core.service.repo.cassandra.message

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.logback.cmLoggerLogback
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraMapper
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraProperties
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraSessionProvider
import org.jep21s.messenger.core.service.repo.cassandra.config.liquibase.LiquibaseConfig
import org.jep21s.messenger.core.service.repo.cassandra.extention.awaitAll
import org.jep21s.messenger.core.service.repo.cassandra.message.dao.MessageSimpleCleaner
import org.jep21s.messenger.core.service.repo.cassandra.message.dao.MessageSimpleWriter
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.cassandra.message.mapper.MessageEntityMapperImpl
import org.jep21s.messenger.core.service.repo.cassandra.test.extention.CassandraTestExtension
import org.jep21s.messenger.core.service.repo.common.message.AMessageRepoInitializable
import org.jep21s.messenger.core.service.repo.common.message.MessageSearchTest
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CassandraTestExtension::class)
class MessageRepoSearchCassandraTest : MessageSearchTest() {
  init {
    runCatching {
      CSCorSettings.initialize(
        loggerProvider = CMLoggerProvider { clazz -> cmLoggerLogback(clazz) }
      )
    }
  }

  private val messageEntityMapper = MessageEntityMapperImpl
  private val properties = CassandraProperties()
  private val sessionProvider = CassandraSessionProvider(properties)
    .also { LiquibaseConfig(properties).runMigrations() }
  private val messageDao: MessageDao = CassandraMapper.getInstant(sessionProvider.session).getMessageDAO(
    properties.keyspaceName,
    MessageEntity.TABLE_NAME,
  )
  private val messageRepoCassandra = MessageRepoCassandra(messageDao)

  override val messageRepo: AMessageRepoInitializable = object : AMessageRepoInitializable(
    messageRepoCassandra,
    emptyList()
  ) {
    private val messageSimpleWriter = MessageSimpleWriter(sessionProvider.session)
    private val messageSimpleCleaner = MessageSimpleCleaner(sessionProvider.session)

    override suspend fun addTestData(messages: List<Message>) {
      if (messages.isEmpty()) return
      val entities = messages.map { messageEntityMapper.mapToEntity(it) }
      messageSimpleWriter.insertMessages(entities)
        .awaitAll()
    }

    override suspend fun clearDB() {
      messageSimpleCleaner.truncateTable()
    }
  }
}