package org.jep21s.messenger.core.service.repo.cassandra.message.dao

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletionStage
import kotlinx.coroutines.delay

class MessageSimpleWriter(private val session: CqlSession) {
  private val jacksonMapper = jacksonObjectMapper()

  suspend fun insertMessages(
    messages: List<MessageEntity>,
  ): List<CompletionStage<AsyncResultSet>> = messages.map {
    /**
     * Делаем задержку чтобы не было ошибок от касандры при запросах
     * с минимальной паузой между ними
     */
    delay(20L)
    insertMessage(it)
  }

  suspend fun insertMessage(message: MessageEntity): CompletionStage<AsyncResultSet> {
    val query = buildInsertQuery(message)
    val statement = SimpleStatement.newInstance(query)

    return runCatching {
      session.executeAsync(statement)
    }
      .onSuccess {
        println("Successfully inserted message with id: ${message.id}")
      }
      .onFailure {
        System.err.println("Failed to insert message with id: ${message.id}")
        it.printStackTrace()
      }
      .getOrThrow()
  }

  private fun buildInsertQuery(message: MessageEntity): String {
    return """
            INSERT INTO ${MessageEntity.TABLE_NAME} 
            (
                ${MessageEntity.COLUMN_CHAT_ID}, 
                ${MessageEntity.COLUMN_SENT_DATE}, 
                ${MessageEntity.COLUMN_ID}, 
                ${MessageEntity.COLUMN_MESSAGE_TYPE}, 
                ${MessageEntity.COLUMN_SENDER_ID}, 
                ${MessageEntity.COLUMN_SENDER_TYPE}, 
                ${MessageEntity.COLUMN_EXTERNAL_ID}, 
                ${MessageEntity.COLUMN_COMMUNICATION_TYPE}, 
                ${MessageEntity.COLUMN_CREATED_AT}, 
                ${MessageEntity.COLUMN_UPDATED_AT}, 
                ${MessageEntity.COLUMN_BODY}, 
                ${MessageEntity.COLUMN_PAYLOAD}
            )
            VALUES 
            (
                ${formatUuid(message.chatId)},
                ${formatInstant(message.sentDate)},
                ${formatUuid(message.id)},
                '${escapeString(message.messageType)}',
                '${escapeString(message.senderId)}',
                '${escapeString(message.senderType)}',
                ${formatNullableString(message.externalId)},
                '${escapeString(message.communicationType)}',
                ${formatInstant(message.createdAt)},
                ${formatNullableInstant(message.updatedAt)},
                ${formatNullableString(message.body)},
                ${formatPayload(message.payload)}
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