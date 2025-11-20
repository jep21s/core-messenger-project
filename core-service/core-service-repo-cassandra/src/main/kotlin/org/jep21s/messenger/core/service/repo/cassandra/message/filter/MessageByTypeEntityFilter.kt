package org.jep21s.messenger.core.service.repo.cassandra.message.filter

import java.util.UUID
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.OrderType

data class MessageByTypeEntityFilter(
  val chatId: UUID,
  val messageTypes: List<String>,
  val sentDate: ComparableFilter,
  val ids: List<UUID>?,
  val order: OrderType?,
  val limit: Int?,
)