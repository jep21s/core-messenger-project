package org.jep21s.messenger.core.service.common.model.chat

import java.time.Instant
import java.util.UUID

data class ChatLatestActivityUpdation(
  val chatId: UUID,
  val communicationType: String,
  val latestMessageDate: Instant,
)
