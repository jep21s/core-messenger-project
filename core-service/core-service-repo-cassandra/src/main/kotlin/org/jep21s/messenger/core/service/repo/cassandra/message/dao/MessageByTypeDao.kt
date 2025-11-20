package org.jep21s.messenger.core.service.repo.cassandra.message.dao

import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes
import java.util.concurrent.CompletionStage
import org.jep21s.messenger.core.service.repo.cassandra.message.providers.MessageByTypeCassandraSearchProvider
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageByTypeEntity
import org.jep21s.messenger.core.service.repo.cassandra.message.filter.MessageByTypeEntityFilter

@Dao
interface MessageByTypeDao {
  @QueryProvider(providerClass = MessageByTypeCassandraSearchProvider::class,
    entityHelpers = [MessageByTypeEntity::class])
  @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
  fun search(filter: MessageByTypeEntityFilter): CompletionStage<List<MessageByTypeEntity>>
}