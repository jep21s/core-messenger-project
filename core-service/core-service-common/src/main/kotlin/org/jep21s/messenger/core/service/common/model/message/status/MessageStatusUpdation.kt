package org.jep21s.messenger.core.service.common.model.message.status

data class MessageStatusUpdation(
  val ids: List<java.util.UUID>,
  val communicationType: String,
  val newStatus: String
)
