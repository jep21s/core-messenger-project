package org.jep21s.messenger.core.service.repo.inmemory.chat.entity

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.repo.inmemory.Entity

data class ChatEntity(
  val id: UUID = UUID.randomUUID(),
  val externalId: String?,
  val communicationType: String,
  val chatType: String,
  val payload: Map<String, Any?>?,
  val createdAt: Instant = Instant.now(),
  val updatedAt: Instant?,
  val latestMessageDate: Instant?,
) : Entity
