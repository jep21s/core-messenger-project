package org.jep21s.messenger.core.service.repo.cassandra.message.dao

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import kotlinx.coroutines.future.await
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity

class MessageSimpleCleaner(private val session: CqlSession) {
  suspend fun truncateTable() {
    val query = "TRUNCATE TABLE ${MessageEntity.TABLE_NAME}"
    val statement = SimpleStatement.newInstance(query)

    try {
      println("Starting truncate of table: ${MessageEntity.TABLE_NAME}")
      session.executeAsync(statement).await()
      println("Successfully truncated table: ${MessageEntity.TABLE_NAME}")
    } catch (e: Exception) {
      System.err.println("Failed to truncate table: ${MessageEntity.TABLE_NAME}")
      e.printStackTrace()
      throw e
    }
  }
}