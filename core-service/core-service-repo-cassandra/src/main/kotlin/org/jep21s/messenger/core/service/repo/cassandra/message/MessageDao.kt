package org.jep21s.messenger.core.service.repo.cassandra.message

import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.mapper.annotations.CqlName
import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Delete
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletionStage
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.cassandra.message.filter.MessageEntityFilter

@Dao
interface MessageDao {
  @Insert(ifNotExists = true)
  @StatementAttributes(consistencyLevel = "QUORUM")
  fun create(message: MessageEntity): CompletionStage<MessageEntity?>

  @Delete(entityClass = [MessageEntity::class])
  @StatementAttributes(consistencyLevel = "QUORUM")
  fun delete(
    @CqlName(MessageEntity.COLUMN_CHAT_ID) chatId: UUID,
    @CqlName(MessageEntity.COLUMN_SENT_DATE) sentDate: Instant,
    @CqlName(MessageEntity.COLUMN_ID) messageId: UUID
  ): CompletionStage<AsyncResultSet>

  @QueryProvider(providerClass = MessageCassandraSearchProvider::class, entityHelpers = [MessageEntity::class])
  @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
  fun search(filter: MessageEntityFilter): CompletionStage<List<MessageEntity>>
}