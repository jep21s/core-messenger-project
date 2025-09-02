package org.jep21s.messenger.core.service.common.model.message

import java.util.UUID

data class MessageDeletion(
  val ids: List<UUID>,
  val communicationType: String
)
