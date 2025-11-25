package org.jep21s.messenger.core.service.common.model.message

import java.time.Instant
import java.util.UUID

data class MessageDeletion(
  val ids: Set<UUID>,
  val chatId: UUID,
  val sentDate: Instant,
  val communicationType: String
)
