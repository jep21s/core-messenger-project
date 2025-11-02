package org.jep21s.messenger.core.service.common.model.message

import java.util.UUID

data class MessageDeletion(
  val ids: Set<UUID>,
  val chadId: UUID,
  val communicationType: String
)
