package org.jep21s.messenger.core.service.repo.inmemory

import java.time.Instant

data class EntityWrapper<E>(
  val entity: E,
) {
  val creationTime: Instant = Instant.now()
}

fun <E : Entity> E.wrap() = EntityWrapper<E>(this)
