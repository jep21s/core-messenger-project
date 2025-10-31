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
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.ChatRepoInmemory
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.inmemory.message.MessageRepoInmemory
import org.jep21s.messenger.core.service.repo.inmemory.message.entity.MessageEntity
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
}

suspend fun Application.initInmemoryRepos() {
  initChatInmemoryRepo(scheduleScope.value(this))
  initMessageInmemoryRepo(scheduleScope.value(this))
}

private suspend fun initChatInmemoryRepo(scope: CoroutineScope) {
  val db = mutableMapOf<UUID, EntityWrapper<ChatEntity>>()
  CSCorSettings.initialize(
    chatRepo = ChatRepoInmemory(db)
  )
  DBCleaner(db, scope).runScheduleDeleteOldRowsTask()
  println("----- ChatDBCleaner started")
}

private suspend fun initMessageInmemoryRepo(scope: CoroutineScope) {
  val db = mutableMapOf<UUID, EntityWrapper<MessageEntity>>()
  CSCorSettings.initialize(
    messageRepo = MessageRepoInmemory(db)
  )
  DBCleaner(db, scope).runScheduleDeleteOldRowsTask()
  println("----- MessageDBCleaner started")
}
