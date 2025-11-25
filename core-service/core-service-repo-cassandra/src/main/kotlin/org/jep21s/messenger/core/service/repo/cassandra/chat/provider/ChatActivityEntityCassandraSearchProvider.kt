package org.jep21s.messenger.core.service.repo.cassandra.chat.provider

import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder
import com.datastax.oss.driver.api.mapper.MapperContext
import com.datastax.oss.driver.api.mapper.entity.EntityHelper
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.select.Select
import java.util.concurrent.CompletionStage
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatActivityEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.filter.ChatActivityEntityFilter
import org.jep21s.messenger.core.service.repo.cassandra.config.AsyncFetcher
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.common.Pagination

class ChatActivityEntityCassandraSearchProvider(
  private val context: MapperContext,
  private val entityHelper: EntityHelper<ChatActivityEntity>,
) {
  fun search(
    filter: ChatActivityEntityFilter,
  ): CompletionStage<List<ChatActivityEntity>> {
    val select: Select = entityHelper.selectStart()
      .applyBucketDay(filter)
      .applyCommunicationType(filter)
      .applyLatestMessageDate(filter)
      .sortByLatestMessage(filter)
      .withLimit(filter)

    val asyncFetcher: AsyncFetcher<ChatActivityEntity> = AsyncFetcher(entityHelper)

    context.session
      .executeAsync(select.build())
      .whenComplete(asyncFetcher)

    return asyncFetcher.stage
  }

  private fun Select.applyBucketDay(
    filter: ChatActivityEntityFilter,
  ): Select = whereColumn(ChatActivityEntity.COLUMN_BUCKET_DAY)
    .isEqualTo(
      QueryBuilder.literal(
        filter.bucketDayStr,
        context.session.context.codecRegistry
      )
    )

  private fun Select.applyCommunicationType(
    filter: ChatActivityEntityFilter,
  ): Select = whereColumn(ChatActivityEntity.COLUMN_COMMUNICATION_TYPE)
    .isEqualTo(
      QueryBuilder.literal(
        filter.communicationType,
        context.session.context.codecRegistry
      )
    )

  private fun Select.applyLatestMessageDate(
    filter: ChatActivityEntityFilter
  ): Select {
    val latestMessageDateFilter = filter.sourceFilter
      .filter
      .latestMessageDate
    if (latestMessageDateFilter == null) return this

    val literal = QueryBuilder.literal(
      latestMessageDateFilter.value,
      context.session.context.codecRegistry
    )

    return whereColumn(ChatActivityEntity.COLUMN_LATEST_ACTIVITY)
      .let {
        when (latestMessageDateFilter.direction) {
          ConditionType.EQUAL -> it.isEqualTo(literal)
          ConditionType.LESS -> it.isLessThan(literal)
          ConditionType.GREATER -> it.isGreaterThan(literal)
        }
      }
  }

  private fun Select.sortByLatestMessage(
    filter: ChatActivityEntityFilter,
  ): Select {
    val direction = when (filter.order) {
      OrderType.DESC -> ClusteringOrder.DESC
      OrderType.ASC -> ClusteringOrder.ASC
    }

    return orderBy(
      ChatActivityEntity.COLUMN_LATEST_ACTIVITY,
      direction,
    )
  }

  private fun Select.withLimit(
    filter: ChatActivityEntityFilter,
  ): Select = limit(Pagination.getValidChatLimit(filter.limit))

}