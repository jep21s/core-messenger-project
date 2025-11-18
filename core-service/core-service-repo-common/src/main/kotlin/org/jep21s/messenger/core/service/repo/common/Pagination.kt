package org.jep21s.messenger.core.service.repo.common

import org.jep21s.messenger.core.service.common.model.OrderType

object Pagination {
  const val DEFAULT_MESSAGE_LIMIT = 50
  const val MAX_MESSAGE_LIMIT = 100
  val defaultMessageSortDirection = OrderType.DESC

  fun getValidMessageLimit(limit: Int?): Int = when {
    limit == null -> DEFAULT_MESSAGE_LIMIT
    limit < 0 -> DEFAULT_MESSAGE_LIMIT
    limit > MAX_MESSAGE_LIMIT -> MAX_MESSAGE_LIMIT
    else -> limit
  }
}