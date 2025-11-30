package org.jep21s.messenger.core.service.common.model.message

import java.time.Instant
import java.util.UUID

data class Message(
  val id: UUID,
  val chatId: UUID,
  val communicationType: String,
  val messageType: String,
  val senderId: String,
  val senderType: String,
  val sentDate: Instant,
  val createdAt: Instant,
  val updatedAt: Instant?,
  val body: String?,
  val externalId: String?,
  val payload: Map<String, Any?>?,
)