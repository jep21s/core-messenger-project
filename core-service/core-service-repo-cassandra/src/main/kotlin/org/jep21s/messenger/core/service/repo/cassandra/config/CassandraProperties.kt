package org.jep21s.messenger.core.service.repo.cassandra.config

data class CassandraProperties(
  val keyspaceName: String = "core_service",
  val host: String = "localhost",
  val port: Int = 9042,
  val user: String = "cassandra",
  val pass: String = "cassandra",
  val datacenter: String = "dc1",
  )
