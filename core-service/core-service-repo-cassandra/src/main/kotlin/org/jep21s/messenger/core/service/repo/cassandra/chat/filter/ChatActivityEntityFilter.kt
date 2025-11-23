package org.jep21s.messenger.core.service.repo.cassandra.chat.filter

import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID
import org.jep21s.messenger.core.service.common.model.ComparableFilter

class ChatActivityEntityFilter(
  val communicationType: String,
  val latestMessageDate: ComparableFilter?,
  val limit: Int?,
  val chatId: UUID? = null,
) {
  val bucketDay: String = calculateBucketDay()
  
  private fun calculateBucketDay(): String {
    if (latestMessageDate == null) return LocalDate.now().toString()
    return LocalDate.ofInstant(latestMessageDate.value, ZoneOffset.UTC)
      .toString()
  }
}
