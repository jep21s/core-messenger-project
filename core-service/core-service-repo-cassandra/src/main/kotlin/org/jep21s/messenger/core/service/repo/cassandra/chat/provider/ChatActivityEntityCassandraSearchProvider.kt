package org.jep21s.messenger.core.service.repo.cassandra.chat.provider

import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder
import com.datastax.oss.driver.api.mapper.MapperContext
import com.datastax.oss.driver.api.mapper.entity.EntityHelper
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.select.Select
import java.util.concurrent.CompletionStage
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatActivityEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.filter.ChatActivityEntityFilter
import org.jep21s.messenger.core.service.repo.cassandra.config.AsyncFetcher
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
      .applyChatId(filter)
      .sortByLatestMessage()
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
        filter.bucketDay,
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

  private fun Select.applyChatId(
    filter: ChatActivityEntityFilter,
  ): Select {
    if (filter.chatId == null) return this
    return whereColumn(ChatActivityEntity.COLUMN_CHAT_ID)
      .isEqualTo(
        QueryBuilder.literal(
          filter.chatId,
          context.session.context.codecRegistry
        )
      )
  }

  private fun Select.sortByLatestMessage(): Select = orderBy(
    ChatActivityEntity.COLUMN_LATEST_ACTIVITY,
    ClusteringOrder.DESC,
  )

  private fun Select.withLimit(
    filter: ChatActivityEntityFilter,
  ): Select = limit(Pagination.getValidChatLimit(filter.limit))
}