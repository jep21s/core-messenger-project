package org.jep21s.messenger.core.service.common.model.chat

import java.util.UUID

data class ChatDeleted(
  val id: UUID,
  val externalId: String?,
  val communicationType: String,
)
