package org.jep21s.messenger.core.service.repo.cassandra.chat.filter

import java.time.LocalDate
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch

class ChatActivityEntityFilter(
  val communicationType: String,
  val bucketDay: LocalDate,
  val limit: Int?,
  val order: OrderType,
  val sourceFilter: ChatSearch,
) {

  val bucketDayStr: String = bucketDay.toString()
}

