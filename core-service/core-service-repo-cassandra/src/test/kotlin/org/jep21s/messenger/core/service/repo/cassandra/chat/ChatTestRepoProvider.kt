package org.jep21s.messenger.core.service.repo.cassandra.chat

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.logback.cmLoggerLogback
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.repo.cassandra.chat.dao.ChatActivitySimpleWriter
import org.jep21s.messenger.core.service.repo.cassandra.chat.dao.ChatSimpleWriter
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatActivityEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.mapper.ChatEntityMapperImpl
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraMapper
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraProperties
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraSessionProvider
import org.jep21s.messenger.core.service.repo.cassandra.config.liquibase.LiquibaseConfig
import org.jep21s.messenger.core.service.repo.cassandra.extention.awaitAll
import org.jep21s.messenger.core.service.repo.cassandra.test.dao.SimpleCleaner
import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable

object ChatTestRepoProvider {
  init {
    runCatching {
      CSCorSettings.initialize(
        loggerProvider = CMLoggerProvider { clazz -> cmLoggerLogback(clazz) }
      )
    }
  }

  private val chatEntityMapper = ChatEntityMapperImpl
  private val properties = CassandraProperties()
  private val sessionProvider = CassandraSessionProvider(properties)
    .also { LiquibaseConfig(properties).runMigrations() }
  private val chatDao = CassandraMapper.getInstant(sessionProvider.session)
    .getChatDao(
      properties.keyspaceName,
      ChatEntity.TABLE_NAME,
    )
  private val chatActivityDao = CassandraMapper.getInstant(sessionProvider.session)
    .getChatActivityDao(
      properties.keyspaceName,
      ChatActivityEntity.TABLE_NAME,
    )

  private val chatRepoCassandra = ChatRepoCassandra(
    chatDao = chatDao,
    chatActivityDao = chatActivityDao,
    chatEntityMapper = chatEntityMapper
  )

  fun getChatRepoTest(
    initChats: List<Chat> = emptyList(),
  ): AChatRepoInitializable = object : AChatRepoInitializable(
    chatRepoCassandra,
    initChats
  ) {
    private val chatSimpleWriter = ChatSimpleWriter(sessionProvider.session)
    private val chatActivitySimpleWriter = ChatActivitySimpleWriter(sessionProvider.session)
    private val simpleCleaner = SimpleCleaner(sessionProvider.session)

    override suspend fun addTestData(chats: List<Chat>) {
      if (chats.isEmpty()) return
      val entities = chats.map { chatEntityMapper.mapToEntity(it) }
      chatSimpleWriter.insertChats(entities)
        .awaitAll()
      val activities = chats.map { chat ->
        ChatActivityEntity(
          bucketDay = ChatActivityBucketCalculator
            .calculateBucketDay(chat.latestMessageDate)
            .toString(),
          communicationType = chat.communicationType,
          latestActivity = (chat.latestMessageDate ?: chat.createdAt),
          chatId = chat.id
        )
      }
      chatActivitySimpleWriter.insertChatActivities(activities)
    }

    override suspend fun clearDB() {
      simpleCleaner.truncateTable(ChatEntity.TABLE_NAME)
      simpleCleaner.truncateTable(ChatActivityEntity.TABLE_NAME)
    }
  }

}