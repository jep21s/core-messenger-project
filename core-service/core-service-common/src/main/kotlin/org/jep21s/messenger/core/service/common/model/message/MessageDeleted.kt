package org.jep21s.messenger.core.service.common.model.message

import java.util.UUID

data class MessageDeleted(
  val ids: List<UUID>,
  val chatId: UUID,
  val communicationType: String
)