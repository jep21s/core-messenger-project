package org.jep21s.messenger.core.service.common.model.chat

data class ChatCreation(
  val externalId: String?,
  val communicationType: String,
  val chatType: String,
  val payload: Map<String, Any?>?
)
