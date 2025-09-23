package org.jep21s.messenger.core.service.common.model.chat

import java.util.UUID

data class ChatDeletion(
  val id: UUID,
  val communicationType: String
)
