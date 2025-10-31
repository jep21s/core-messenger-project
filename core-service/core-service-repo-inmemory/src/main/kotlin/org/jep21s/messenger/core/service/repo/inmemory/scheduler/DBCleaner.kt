package org.jep21s.messenger.core.service.repo.inmemory.scheduler

import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper

class DBCleaner<ID, E>(
  private val db: MutableMap<ID, EntityWrapper<E>>,
  private val scope: CoroutineScope,
  private val rowExpiryTimeMinutes: Long = 5L,
  private val scheduleRunTaskTimeMs: Long = 60_000L,
) {
  private var job: Job? = null

  suspend fun runScheduleDeleteOldRowsTask() {
    if (job != null) return
    scope.launch {
      while (isActive) {
        try {
          deleteOldRows()
          delay(scheduleRunTaskTimeMs)
        } catch (e: CancellationException) {
          throw e
        } catch (e: Throwable) {
          e.printStackTrace()
        }
      }
      job = null
    }.also { job = it }
  }

  private suspend fun CoroutineScope.deleteOldRows() {
    val iterator = db.iterator()
    val expiryTime = Instant.now().minus(rowExpiryTimeMinutes, ChronoUnit.MINUTES)
    while (iterator.hasNext() && isActive) {
      val (_, entityWrapper) = iterator.next()
      if (entityWrapper.creationTime.isBefore(expiryTime)) {
        iterator.remove()
      }
    }
  }
}

