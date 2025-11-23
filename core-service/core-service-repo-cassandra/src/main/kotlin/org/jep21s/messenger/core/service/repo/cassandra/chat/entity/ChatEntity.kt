package org.jep21s.messenger.core.service.repo.cassandra.chat.entity

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn
import com.datastax.oss.driver.api.mapper.annotations.CqlName
import com.datastax.oss.driver.api.mapper.annotations.Entity
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey
import java.time.Instant
import java.util.UUID

@Entity
data class ChatEntity(
  @field:PartitionKey(0)
  @field:CqlName(COLUMN_ID)
  val id: UUID,

  @field:PartitionKey(1)
  @field:CqlName(COLUMN_COMMUNICATION_TYPE)
  val communicationType: String,

  @field:ClusteringColumn(0)
  @field:CqlName(COLUMN_CHAT_TYPE)
  val chatType: String,

  @Deprecated("field will be removed")
  @field:CqlName(COLUMN_EXTERNAL_ID)
  val externalId: String?,

  @field:CqlName(COLUMN_PAYLOAD)
  val payload: Map<String, Any?>?,

  @field:CqlName(COLUMN_CREATED_AT)
  val createdAt: Instant,

  @field:CqlName(COLUMN_UPDATED_AT)
  val updatedAt: Instant?,
) {
  companion object {
    const val TABLE_NAME = "chats"

    const val COLUMN_ID = "id"
    const val COLUMN_COMMUNICATION_TYPE = "communication_type"
    const val COLUMN_CHAT_TYPE = "chat_type"
    const val COLUMN_EXTERNAL_ID = "external_id"
    const val COLUMN_PAYLOAD = "payload"
    const val COLUMN_CREATED_AT = "created_at"
    const val COLUMN_UPDATED_AT = "updated_at"
  }
}
