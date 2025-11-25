package org.jep21s.messenger.core.service.repo.cassandra.message.entity

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn
import com.datastax.oss.driver.api.mapper.annotations.CqlName
import com.datastax.oss.driver.api.mapper.annotations.Entity
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey
import java.time.Instant
import java.util.UUID

@Entity
data class MessageByTypeEntity(
  @field:PartitionKey(0)
  @field:CqlName(COLUMN_CHAT_ID)
  val chatId: UUID,

  @field:PartitionKey(1)
  @field:CqlName(COLUMN_MESSAGE_TYPE)
  val messageType: String,

  @field:ClusteringColumn(0)
  @field:CqlName(COLUMN_SENT_DATE)
  val sentDate: Instant,

  @field:ClusteringColumn(1)
  @field:CqlName(COLUMN_ID)
  val id: UUID = UUID.randomUUID(),
) {
  companion object {
    const val TABLE_NAME = "messages_by_message_type"

    const val COLUMN_CHAT_ID = "chat_id"
    const val COLUMN_SENT_DATE = "sent_date"
    const val COLUMN_ID = "id"
    const val COLUMN_MESSAGE_TYPE = "message_type"
  }
}
