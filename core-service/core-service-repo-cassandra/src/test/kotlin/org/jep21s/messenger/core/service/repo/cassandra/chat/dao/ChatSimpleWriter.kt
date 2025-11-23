package org.jep21s.messenger.core.service.repo.cassandra.chat.dao

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatEntity
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletionStage
import kotlinx.coroutines.delay

class ChatSimpleWriter(private val session: CqlSession) {
  private val jacksonMapper = jacksonObjectMapper()

  suspend fun insertChats(
    chats: List<ChatEntity>,
  ): List<CompletionStage<AsyncResultSet>> = chats.map {
    /**
     * Делаем задержку чтобы не было ошибок от касандры при запросах
     * с минимальной паузой между ними
     */
    delay(20L)
    insertChat(it)
  }

  suspend fun insertChat(chat: ChatEntity): CompletionStage<AsyncResultSet> {
    val query = buildInsertQuery(chat)
    val statement = SimpleStatement.newInstance(query)

    return runCatching {
      session.executeAsync(statement)
    }
      .onSuccess {
        println("Successfully inserted chat with id: ${chat.id} and communication type: ${chat.communicationType}")
      }
      .onFailure {
        System.err.println("Failed to insert chat with id: ${chat.id} and communication type: ${chat.communicationType}")
        it.printStackTrace()
      }
      .getOrThrow()
  }

  private fun buildInsertQuery(chat: ChatEntity): String {
    return """
            INSERT INTO ${ChatEntity.TABLE_NAME} 
            (
                ${ChatEntity.COLUMN_ID}, 
                ${ChatEntity.COLUMN_COMMUNICATION_TYPE}, 
                ${ChatEntity.COLUMN_CHAT_TYPE}, 
                ${ChatEntity.COLUMN_EXTERNAL_ID}, 
                ${ChatEntity.COLUMN_PAYLOAD}, 
                ${ChatEntity.COLUMN_CREATED_AT}, 
                ${ChatEntity.COLUMN_UPDATED_AT}
            )
            VALUES 
            (
                ${formatUuid(chat.id)},
                '${escapeString(chat.communicationType)}',
                '${escapeString(chat.chatType)}',
                ${formatNullableString(chat.externalId)},
                ${formatPayload(chat.payload)},
                ${formatInstant(chat.createdAt)},
                ${formatNullableInstant(chat.updatedAt)}
            )
        """.trimIndent()
  }

  private fun formatUuid(uuid: UUID): String {
    return uuid.toString()
  }

  private fun formatInstant(instant: Instant): String {
    return "'$instant'"
  }

  private fun formatNullableInstant(instant: Instant?): String {
    return instant?.let { "'$it'" } ?: "null"
  }

  private fun formatNullableString(value: String?): String {
    return value?.let { "'${escapeString(it)}'" } ?: "null"
  }

  private fun formatPayload(payload: Map<String, Any?>?): String {
    return if (payload != null) {
      val jsonString = jacksonMapper.writeValueAsString(payload)
      "'${escapeString(jsonString)}'"
    } else {
      "null"
    }
  }

  private fun escapeString(value: String): String {
    return value.replace("'", "''")
  }
}