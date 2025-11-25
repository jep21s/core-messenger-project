package org.jep21s.messenger.core.service.repo.inmemory.message.entity

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.repo.inmemory.Entity

data class MessageEntity(
  val id: UUID = UUID.randomUUID(),
  val chatId: UUID,
  val communicationType: String,
  val messageType: String,
  val sentDate: Instant,
  val createdAt: Instant = Instant.now(),
  val updatedAt: Instant?,
  val body: String?,
  val externalId: String?,
  val payload: Map<String, Any?>?,
): Entity