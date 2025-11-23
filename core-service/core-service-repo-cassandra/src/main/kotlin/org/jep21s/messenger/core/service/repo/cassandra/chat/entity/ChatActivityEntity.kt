package org.jep21s.messenger.core.service.repo.cassandra.chat.entity

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn
import com.datastax.oss.driver.api.mapper.annotations.CqlName
import com.datastax.oss.driver.api.mapper.annotations.Entity
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey
import java.time.Instant
import java.util.UUID

@Entity
data class ChatActivityEntity(
  @field:PartitionKey(0)
  @field:CqlName(COLUMN_BUCKET_DAY)
  val bucketDay: String,

  @field:PartitionKey(1)
  @field:CqlName(COLUMN_COMMUNICATION_TYPE)
  val communicationType: String,

  @field:ClusteringColumn(0)
  @field:CqlName(COLUMN_LATEST_ACTIVITY)
  val latestActivity: Instant,

  @field:ClusteringColumn(1)
  @field:CqlName(COLUMN_CHAT_ID)
  val chatId: UUID,
) {
  companion object {
    const val TABLE_NAME = "chat_activities"

    const val COLUMN_BUCKET_DAY = "bucket_day"
    const val COLUMN_CHAT_ID = "chat_id"
    const val COLUMN_COMMUNICATION_TYPE = "communication_type"
    const val COLUMN_LATEST_ACTIVITY = "latest_activity"
  }
}