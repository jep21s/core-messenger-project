package org.jep21s.messenger.core.service.repo.cassandra.config

import com.datastax.oss.driver.api.core.CqlSession
import java.net.InetAddress
import java.net.InetSocketAddress
import com.datastax.oss.driver.internal.core.type.codec.registry.DefaultCodecRegistry
import org.jep21s.messenger.core.service.repo.cassandra.config.type.JsonMapCodec

class CassandraSessionProvider(
  properties: CassandraProperties,
) {
  val session = CqlSession.builder()
    .addContactPoints(parseAddresses(properties.host, properties.port))
    .withLocalDatacenter(properties.datacenter)
    .withAuthCredentials(properties.user, properties.pass)
    .withKeyspace(properties.keyspaceName)
    .withCodecRegistry(DefaultCodecRegistry("default").apply {
      register(JsonMapCodec())
    })
    .build()

  private fun parseAddresses(
    hosts: String,
    port: Int,
  ): Collection<InetSocketAddress> = hosts
    .split(Regex("""\s*,\s*"""))
    .map { InetSocketAddress(InetAddress.getByName(it), port) }
}