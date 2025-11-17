package org.jep21s.messenger.core.service.repo.cassandra.config

import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.mapper.entity.EntityHelper
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.BiConsumer

class AsyncFetcher<T>(
  private val entityHelper: EntityHelper<T>
) : BiConsumer<AsyncResultSet?, Throwable?> {
  private val buffer = mutableListOf<T>()
  private val future = CompletableFuture<List<T>>()
  val stage: CompletionStage<List<T>> = future

  override fun accept(resultSet: AsyncResultSet?, t: Throwable?) {
    when {
      t != null -> future.completeExceptionally(t)
      resultSet == null -> future.completeExceptionally(IllegalStateException("ResultSet should not be null"))
      else -> {
        buffer.addAll(resultSet.currentPage().map { entityHelper.get(it, false) })
        if (resultSet.hasMorePages())
          resultSet.fetchNextPage().whenComplete(this)
        else
          future.complete(buffer)
      }
    }
  }
}