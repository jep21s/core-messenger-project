package org.jep21s.messenger.core.service.repo.cassandra.chat.dao

import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes
import java.util.concurrent.CompletionStage
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatActivityEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.filter.ChatActivityEntityFilter
import org.jep21s.messenger.core.service.repo.cassandra.chat.provider.ChatActivityEntityCassandraSearchProvider

@Dao
interface ChatActivityDao {

  @QueryProvider(providerClass = ChatActivityEntityCassandraSearchProvider::class, entityHelpers = [ChatActivityEntity::class])
  @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
  fun search(filter: ChatActivityEntityFilter): CompletionStage<List<ChatActivityEntity>>
}