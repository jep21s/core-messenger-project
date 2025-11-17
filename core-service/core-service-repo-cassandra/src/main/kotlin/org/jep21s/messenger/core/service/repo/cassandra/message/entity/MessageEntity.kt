package org.jep21s.messenger.core.service.repo.cassandra.message.entity

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn
import com.datastax.oss.driver.api.mapper.annotations.CqlName
import com.datastax.oss.driver.api.mapper.annotations.Entity
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey
import java.time.Instant
import java.util.UUID

@Entity
data class MessageEntity(
  @field:PartitionKey
  @field:CqlName(COLUMN_CHAT_ID)
  val chatId: UUID,

  @field:ClusteringColumn(0)
  @field:CqlName(COLUMN_SENT_DATE)
  val sentDate: Instant,

  @field:ClusteringColumn(1)
  @field:CqlName(COLUMN_ID)
  val id: UUID = UUID.randomUUID(),

  @field:CqlName(COLUMN_MESSAGE_TYPE)
  val messageType: String,

  @field:CqlName(COLUMN_EXTERNAL_ID)
  val externalId: String?,

  @field:CqlName(COLUMN_COMMUNICATION_TYPE)
  val communicationType: String,

  @field:CqlName(COLUMN_CREATED_AT)
  val createdAt: Instant = Instant.now(),

  @field:CqlName(COLUMN_UPDATED_AT)
  val updatedAt: Instant?,

  @field:CqlName(COLUMN_BODY)
  val body: String?,

  @field:CqlName(COLUMN_PAYLOAD)
  val payload: Map<String, Any?>?,
) {
  companion object {
    const val TABLE_NAME = "messages"

    const val COLUMN_CHAT_ID = "chat_id"
    const val COLUMN_SENT_DATE = "sent_date"
    const val COLUMN_ID = "id"
    const val COLUMN_MESSAGE_TYPE = "message_type"
    const val COLUMN_EXTERNAL_ID = "external_id"
    const val COLUMN_COMMUNICATION_TYPE = "communication_type"
    const val COLUMN_CREATED_AT = "created_at"
    const val COLUMN_UPDATED_AT = "updated_at"
    const val COLUMN_BODY = "body"
    const val COLUMN_PAYLOAD = "payload"
  }
}