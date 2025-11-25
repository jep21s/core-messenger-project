package org.jep21s.messenger.core.service.repo.cassandra.chat.dao

import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.mapper.annotations.CqlName
import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Delete
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.Select
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes
import java.util.UUID
import java.util.concurrent.CompletionStage
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatEntity

@Dao
interface ChatDao {
  @Insert(ifNotExists = true)
  @StatementAttributes(consistencyLevel = "QUORUM")
  fun create(chatEntity: ChatEntity): CompletionStage<ChatEntity?>

  @Delete(entityClass = [ChatEntity::class])
  @StatementAttributes(consistencyLevel = "QUORUM")
  fun delete(
    @CqlName(ChatEntity.COLUMN_ID) id: UUID,
    @CqlName(ChatEntity.COLUMN_COMMUNICATION_TYPE)
    communicationType: String,
  ): CompletionStage<AsyncResultSet>

  @Select
  @StatementAttributes(consistencyLevel = "QUORUM")
  fun findById(id: UUID, communicationType: String): CompletionStage<ChatEntity?>
}