package org.jep21s.messenger.core.service.repo.cassandra.chat.filter

import java.time.LocalDate

class ChatActivityEntityFilter(
  val communicationType: String,
  val bucketDay: LocalDate,
  val limit: Int?,
) {

  val bucketDayStr: String = bucketDay.toString()
}

