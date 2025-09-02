package org.jep21s.messenger.core.service.common.model.chat

import java.util.UUID
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.OrderType

data class ChatSearch(
  val filter: ChatSearchFilter,
  val sort: ChatSearchSort?,
  val limit: Int,
) {
  data class ChatSearchFilter(
    val ids: List<UUID>?,
    val externalIds: List<String>?,
    val communicationType: String,
    val chatTypes: List<String>?,
    val latestMessageDate: ComparableFilter?,
  )

  data class ChatSearchSort(
    val sortField: SortField,
    val order: OrderType,
  ) {
    enum class SortField {
      LATEST_MESSAGE_DATE,
      ;
    }
  }
}