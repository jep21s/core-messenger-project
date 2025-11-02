package org.jep21s.messenger.core.service.common.model.message.status

import java.util.UUID

data class MessageStatusUpdation(
  val ids: List<UUID>,
  val chatId: UUID,
  val communicationType: String,
  val newStatus: String
)
