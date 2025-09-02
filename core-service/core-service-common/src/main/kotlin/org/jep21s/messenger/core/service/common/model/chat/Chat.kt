package org.jep21s.messenger.core.service.common.model.chat

import java.time.Instant
import java.util.UUID

data class Chat(
  val id: UUID,
  val externalId: String?,
  val communicationType: String,
  val chatType: String,
  val payload: Map<String, Any?>?,
  val createdAt: Instant,
  val updatedAt: Instant?,
  val latestMessageDate: Instant?,
)