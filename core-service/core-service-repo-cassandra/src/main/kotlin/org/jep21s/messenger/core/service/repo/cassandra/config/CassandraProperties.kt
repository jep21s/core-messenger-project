package org.jep21s.messenger.core.service.repo.cassandra.config

data class CassandraProperties(
  val keyspaceName: String = "core_service",
  val host: String = System.getenv("CASSANDRA_HOST") ?: "localhost",
  val port: Int = System.getenv("CASSANDRA_PORT")?.toIntOrNull() ?: 9042,
  val user: String = System.getenv("CASSANDRA_USER") ?: "cassandra",
  val pass: String = System.getenv("CASSANDRA_PASSWORD") ?: "cassandra",
  val datacenter: String = System.getenv("CASSANDRA_DATACENTER") ?: "dc1",
  )
