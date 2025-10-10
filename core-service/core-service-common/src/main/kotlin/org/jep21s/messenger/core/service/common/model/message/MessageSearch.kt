package org.jep21s.messenger.core.service.common.model.message

import java.util.UUID
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.OrderType

data class MessageSearch(
  val chatFilter: ChatFilter,
  val messageFilter: MessageFilter?,
  val order: OrderType?,
  val limit: Int?,
) {
  data class ChatFilter(
    val id: UUID?,
    val communicationType: String,
  )

  data class MessageFilter(
    val ids: List<UUID>?,
    val messageTypes: List<String>?,
    val partOfBody: String?,
    val sentDate: ComparableFilter?,
  )
}