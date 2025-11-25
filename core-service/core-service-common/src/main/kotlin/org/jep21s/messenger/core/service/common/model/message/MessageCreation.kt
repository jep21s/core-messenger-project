package org.jep21s.messenger.core.service.common.model.message

import java.time.Instant
import java.util.UUID

data class MessageCreation(
  val id: UUID?,
  val chatId: UUID,
  val communicationType: String,
  val messageType: String,
  val sentDate: Instant,
  val body: String?,
  val externalId: String?,
  val payload: Map<String, Any?>?,
) {
  fun getIdOrGenerate(): UUID = id ?: UUID.randomUUID()
}
