package org.jep21s.messenger.core.service.repo.cassandra.message.filter

import java.util.UUID
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.OrderType

data class MessageEntityFilter(
  val chatId: UUID,
  val sentDate: ComparableFilter,
  val id: UUID?,
  val order: OrderType?,
  val limit: Int?,
)
