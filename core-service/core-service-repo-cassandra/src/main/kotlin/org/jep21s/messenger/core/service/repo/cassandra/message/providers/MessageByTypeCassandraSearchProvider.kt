package org.jep21s.messenger.core.service.repo.cassandra.message.providers

import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder
import com.datastax.oss.driver.api.mapper.MapperContext
import com.datastax.oss.driver.api.mapper.entity.EntityHelper
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.select.Select
import java.util.UUID
import java.util.concurrent.CompletionStage
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.repo.cassandra.config.AsyncFetcher
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageByTypeEntity
import org.jep21s.messenger.core.service.repo.cassandra.message.filter.MessageByTypeEntityFilter
import org.jep21s.messenger.core.service.repo.common.Pagination

class MessageByTypeCassandraSearchProvider(
  private val context: MapperContext,
  private val entityHelper: EntityHelper<MessageByTypeEntity>,
) {
  fun search(
    filter: MessageByTypeEntityFilter,
  ): CompletionStage<List<MessageByTypeEntity>> {
    val select: Select = entityHelper.selectStart()
      .allowFiltering()
      .applyChatId(context, filter.chatId)
      .applyMessageTypes(context, filter.messageTypes)
      .applySentDate(context, filter.sentDate)
      .applyMessageIds(context, filter.ids)
      .withSorting(filter)
      .withLimit(filter.limit)

    val asyncFetcher = AsyncFetcher<MessageByTypeEntity>(entityHelper)

    context.session
      .executeAsync(select.build())
      .whenComplete(asyncFetcher)

    return asyncFetcher.stage
  }

  private fun Select.applyChatId(
    context: MapperContext,
    chatId: UUID,
  ): Select = whereColumn(MessageByTypeEntity.COLUMN_CHAT_ID)
    .isEqualTo(
      QueryBuilder.literal(
        chatId,
        context.session.context.codecRegistry
      )
    )

  private fun Select.applyMessageTypes(
    context: MapperContext,
    messageTypes: List<String>,
  ): Select = whereColumn(MessageByTypeEntity.COLUMN_MESSAGE_TYPE)
    .`in`(
      *messageTypes.map { messageType ->
        QueryBuilder.literal(
          messageType,
          context.session.context.codecRegistry
        )
      }.toTypedArray()
    )

  private fun Select.applySentDate(
    context: MapperContext,
    sentDateFilter: ComparableFilter,
  ): Select {
    val literal = QueryBuilder.literal(
      sentDateFilter.value,
      context.session.context.codecRegistry
    )

    return whereColumn(MessageByTypeEntity.COLUMN_SENT_DATE)
      .let {
        when (sentDateFilter.direction) {
          ConditionType.EQUAL -> it.isEqualTo(literal)
          ConditionType.LESS -> it.isLessThan(literal)
          ConditionType.GREATER -> it.isGreaterThan(literal)
        }
      }
  }

  private fun Select.applyMessageIds(
    context: MapperContext,
    messageIds: List<UUID>?,
  ): Select {
    if (messageIds.isNullOrEmpty()) return this
    return whereColumn(MessageByTypeEntity.COLUMN_ID)
      .`in`(
        *messageIds.map { id ->
          QueryBuilder.literal(
            id,
            context.session.context.codecRegistry
          )
        }.toTypedArray()
      )
  }

  private fun Select.withSorting(filter: MessageByTypeEntityFilter): Select {
    val direction = when (filter.order ?: Pagination.defaultMessageSortDirection) {
      OrderType.DESC -> ClusteringOrder.DESC
      OrderType.ASC -> ClusteringOrder.ASC
    }

    return orderBy(
      MessageByTypeEntity.COLUMN_SENT_DATE,
      direction,
    )
  }

  private fun Select.withLimit(limit: Int?): Select =
    limit(Pagination.getValidMessageLimit(limit))
}