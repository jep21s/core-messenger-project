package org.jep21s.messenger.core.service.repo.cassandra.test.dao

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import kotlinx.coroutines.future.await

class SimpleCleaner(private val session: CqlSession) {
  suspend fun truncateTable(tableName: String) {
    val query = "TRUNCATE TABLE $tableName"
    val statement = SimpleStatement.newInstance(query)

    try {
      println("Starting truncate of table: $tableName")
      session.executeAsync(statement).await()
      println("Successfully truncated table: $tableName")
    } catch (e: Exception) {
      System.err.println("Failed to truncate table: $tableName")
      e.printStackTrace()
      throw e
    }
  }
}