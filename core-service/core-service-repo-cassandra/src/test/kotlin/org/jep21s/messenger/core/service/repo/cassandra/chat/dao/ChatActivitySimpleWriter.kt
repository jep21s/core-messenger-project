package org.jep21s.messenger.core.service.repo.cassandra.chat.dao

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatActivityEntity
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletionStage
import kotlinx.coroutines.delay

class ChatActivitySimpleWriter(private val session: CqlSession) {

  suspend fun insertChatActivities(
    chatActivities: List<ChatActivityEntity>,
  ): List<CompletionStage<AsyncResultSet>> = chatActivities.map {
    /**
     * Делаем задержку чтобы не было ошибок от касандры при запросах
     * с минимальной паузой между ними
     */
    delay(20L)
    insertChatActivity(it)
  }

  suspend fun insertChatActivity(chatActivity: ChatActivityEntity): CompletionStage<AsyncResultSet> {
    val query = buildInsertQuery(chatActivity)
    val statement = SimpleStatement.newInstance(query)

    return runCatching {
      session.executeAsync(statement)
    }
      .onSuccess {
        println("Successfully inserted chat activity for chat_id: ${chatActivity.chatId} in bucket: ${chatActivity.bucketDay}")
      }
      .onFailure {
        System.err.println("Failed to insert chat activity for chat_id: ${chatActivity.chatId} in bucket: ${chatActivity.bucketDay}")
        it.printStackTrace()
      }
      .getOrThrow()
  }

  private fun buildInsertQuery(chatActivity: ChatActivityEntity): String {
    return """
            INSERT INTO ${ChatActivityEntity.TABLE_NAME} 
            (
                ${ChatActivityEntity.COLUMN_BUCKET_DAY}, 
                ${ChatActivityEntity.COLUMN_COMMUNICATION_TYPE}, 
                ${ChatActivityEntity.COLUMN_LATEST_ACTIVITY}, 
                ${ChatActivityEntity.COLUMN_CHAT_ID}
            )
            VALUES 
            (
                '${escapeString(chatActivity.bucketDay)}',
                '${escapeString(chatActivity.communicationType)}',
                ${formatInstant(chatActivity.latestActivity)},
                ${formatUuid(chatActivity.chatId)}
            )
        """.trimIndent()
  }

  private fun formatUuid(uuid: UUID): String {
    return uuid.toString()
  }

  private fun formatInstant(instant: Instant): String {
    return "'$instant'"
  }

  private fun escapeString(value: String): String {
    return value.replace("'", "''")
  }
}