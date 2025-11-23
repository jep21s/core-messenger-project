package org.jep21s.messenger.core.service.repo.cassandra.chat.filter

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import org.jep21s.messenger.core.service.common.model.ComparableFilter

class ChatActivityEntityFilter(
  val communicationType: String,
  val latestMessageDate: ComparableFilter?,
  val limit: Int?,
) {
  val bucketDay: LocalDate = ChatActivityBucketCalculator
    .calculateBucketDay(latestMessageDate?.value)

  val bucketDayStr: String = bucketDay.toString()
}

object ChatActivityBucketCalculator {
  fun calculateBucketDay(latestMessageDate: Instant?): LocalDate {
    if (latestMessageDate == null) return LocalDate.now(ZoneOffset.UTC)
    return LocalDate.ofInstant(latestMessageDate, ZoneOffset.UTC)

  }
}
