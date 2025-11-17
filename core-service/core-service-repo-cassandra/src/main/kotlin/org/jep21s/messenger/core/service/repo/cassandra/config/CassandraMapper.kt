package org.jep21s.messenger.core.service.repo.cassandra.config

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace
import com.datastax.oss.driver.api.mapper.annotations.DaoTable
import com.datastax.oss.driver.api.mapper.annotations.Mapper
import org.jep21s.messenger.core.service.repo.cassandra.message.MessageDao

@Mapper
interface CassandraMapper {
    @DaoFactory
    fun getMessageDAO(@DaoKeyspace keyspace: String, @DaoTable tableName: String): MessageDao

    companion object {
        fun getInstant(session: CqlSession) = CassandraMapperBuilder(session)
            .build()
    }
}