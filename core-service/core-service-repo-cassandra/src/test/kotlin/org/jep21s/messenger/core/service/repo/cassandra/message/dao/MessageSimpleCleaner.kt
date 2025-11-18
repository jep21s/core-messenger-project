package org.jep21s.messenger.core.service.repo.cassandra.message.dao

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.future.await
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity

class MessageSimpleCleaner(private val session: CqlSession) {
  private val logger = CSCorSettings.loggerProvider.logger(this::class)
  private val jacksonMapper = jacksonObjectMapper()

  suspend fun truncateTable() {
    val query = "TRUNCATE TABLE ${MessageEntity.TABLE_NAME}"
    val statement = SimpleStatement.newInstance(query)

    try {
      logger.info("Starting truncate of table: ${MessageEntity.TABLE_NAME}")
      session.executeAsync(statement).await()
      logger.info("Successfully truncated table: ${MessageEntity.TABLE_NAME}")
    } catch (e: Exception) {
      logger.error("Failed to truncate table: ${MessageEntity.TABLE_NAME}", ex = e)
      throw e
    }
  }
}