package org.jep21s.messenger.core.service.repo.cassandra.chat

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object ChatActivityBucketCalculator {
  fun calculateBucketDay(latestActivity: Instant?): LocalDate {
    if (latestActivity == null) return LocalDate.now(ZoneOffset.UTC)
    return LocalDate.ofInstant(latestActivity, ZoneOffset.UTC)

  }
}