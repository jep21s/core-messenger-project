package org.jep21s.messenger.core.service.repo.cassandra.config

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace
import com.datastax.oss.driver.api.mapper.annotations.DaoTable
import com.datastax.oss.driver.api.mapper.annotations.Mapper
import org.jep21s.messenger.core.service.repo.cassandra.chat.dao.ChatActivityDao
import org.jep21s.messenger.core.service.repo.cassandra.chat.dao.ChatDao
import org.jep21s.messenger.core.service.repo.cassandra.message.dao.MessageByTypeDao
import org.jep21s.messenger.core.service.repo.cassandra.message.dao.MessageDao

@Mapper
interface CassandraMapper {
  @DaoFactory
  fun getMessageDAO(
    @DaoKeyspace keyspace: String,
    @DaoTable tableName: String,
  ): MessageDao

  @DaoFactory
  fun getMessageByTypeDAO(
    @DaoKeyspace keyspace: String,
    @DaoTable tableName: String,
  ): MessageByTypeDao

  @DaoFactory
  fun getChatDao(
    @DaoKeyspace keyspace: String,
    @DaoTable tableName: String,
  ): ChatDao

  @DaoFactory
  fun getChatActivityDao(
    @DaoKeyspace keyspace: String,
    @DaoTable tableName: String,
  ): ChatActivityDao

  companion object {
    fun getInstant(session: CqlSession) = CassandraMapperBuilder(session)
      .build()
  }
}