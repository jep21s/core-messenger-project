package org.jep21s.messenger.core.service.repo.cassandra.config.liquibase

import java.sql.Connection
import java.sql.DriverManager
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jep21s.messenger.core.service.repo.cassandra.config.CassandraProperties

class LiquibaseConfig(
  private val properties: CassandraProperties,
  private val changeLogFilePath: String = "db/changelog/master.xml",
) {
  fun runMigrations() {
    val url: String = "jdbc:cassandra://" +
        "${properties.host}:" +
        "${properties.port}/" +
        "${properties.keyspaceName}?" +
        "localDatacenter=${properties.datacenter}&" +
        "consistency=ALL"
    val username = properties.user
    val password = properties.pass

    DriverManager.getConnection(url, username, password).use { connection: Connection ->
      val database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(JdbcConnection(connection))
      Liquibase(
        changeLogFilePath,
        ClassLoaderResourceAccessor(),
        database
      )
        .use { liquibase ->
          liquibase.update()
        }
    }
  }
}