package org.jep21s.messenger.core.service.app.web.module

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import java.util.UUID
import java.util.concurrent.ScheduledThreadPoolExecutor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import org.jep21s.messenger.core.service.app.web.scope.lazyScheduleScope
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.repo.cassandra.chat.ChatRepoCassandra
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatActivityEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatEntity as ChatEntityCassandra
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraMapper
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraProperties
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraSessionProvider
import org.jep21s.messenger.core.service.repo.cassandra.config.liquibase.LiquibaseConfig
import org.jep21s.messenger.core.service.repo.cassandra.message.MessageRepoCassandra
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageByTypeEntity
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity as MessageEntityCassandra
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.ChatRepoInmemory
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity as ChatEntityInmemory
import org.jep21s.messenger.core.service.repo.inmemory.message.MessageRepoInmemory
import org.jep21s.messenger.core.service.repo.inmemory.message.entity.MessageEntity as MessageEntityInmemory
import org.jep21s.messenger.core.service.repo.inmemory.scheduler.DBCleaner

private val scheduleScope = lazyScheduleScope {
  CoroutineScope(
    ScheduledThreadPoolExecutor(3)
      .asCoroutineDispatcher() +
        SupervisorJob() +
        CoroutineExceptionHandler { _, ex ->
          println("Error. DBCleaner tasks was interrupt")
          ex.printStackTrace()
        }
  ).also {
    monitor.subscribe(ApplicationStopping) {
      println("Application is stopping...")
      it.cancel()
      println("Schedule scope cancelled")
    }
  }
}

suspend fun Application.initializeRepos() {
  initInmemoryRepos()
  initCassandraTestRepos()
  initCassandraProdRepos()
}

suspend fun Application.initInmemoryRepos() {
  initChatInmemoryRepo(scheduleScope.value(this))
  initMessageInmemoryRepo(scheduleScope.value(this))
}

private suspend fun initChatInmemoryRepo(scope: CoroutineScope) {
  val db = mutableMapOf<UUID, EntityWrapper<ChatEntityInmemory>>()
  CSCorSettings.initialize(
    chatRepoStub = ChatRepoInmemory(db)
  )
  DBCleaner(db, scope).runScheduleDeleteOldRowsTask()
  println("----- ChatDBCleaner started")
}

private suspend fun initMessageInmemoryRepo(scope: CoroutineScope) {
  val db = mutableMapOf<UUID, EntityWrapper<MessageEntityInmemory>>()
  CSCorSettings.initialize(
    messageRepoStub = MessageRepoInmemory(db)
  )
  DBCleaner(db, scope).runScheduleDeleteOldRowsTask()
  println("----- MessageDBCleaner started")
}

suspend fun Application.initCassandraTestRepos() {
  val properties = CassandraProperties()
  val sessionProvider = CassandraSessionProvider(properties)
  LiquibaseConfig(properties).runMigrations()
  println("----- Liquibase migrations executed")
  initChatCassandraTestRepo(properties, sessionProvider)
  println("----- Chat Cassandra Repo init")
  initMessageCassandraTestRepo(properties, sessionProvider)
  println("----- Message Cassandra Repo init")
}

private suspend fun initChatCassandraTestRepo(
  properties: CassandraProperties,
  sessionProvider: CassandraSessionProvider,
) {
  val chatDao = CassandraMapper.getInstant(sessionProvider.session)
    .getChatDao(
      properties.keyspaceName,
      ChatEntityCassandra.TABLE_NAME
    )
  val chatActivityDao = CassandraMapper.getInstant(sessionProvider.session)
    .getChatActivityDao(
      properties.keyspaceName,
      ChatActivityEntity.TABLE_NAME,
    )

  CSCorSettings.initialize(
    chatRepoTest = ChatRepoCassandra(chatDao, chatActivityDao)
  )
}

private suspend fun initMessageCassandraTestRepo(
  properties: CassandraProperties,
  sessionProvider: CassandraSessionProvider,
) {
  val messageDao = CassandraMapper.getInstant(sessionProvider.session)
    .getMessageDAO(
      properties.keyspaceName,
      MessageEntityCassandra.TABLE_NAME
    )
  val messageByTypeDao = CassandraMapper.getInstant(sessionProvider.session)
    .getMessageByTypeDAO(
      properties.keyspaceName,
      MessageByTypeEntity.TABLE_NAME,
    )

  CSCorSettings.initialize(
    messageRepoTest = MessageRepoCassandra(messageDao, messageByTypeDao)
  )
}

suspend fun Application.initCassandraProdRepos() {

}
