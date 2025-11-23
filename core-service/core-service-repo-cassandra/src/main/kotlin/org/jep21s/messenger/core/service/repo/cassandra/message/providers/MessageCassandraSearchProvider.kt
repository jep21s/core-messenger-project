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
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.cassandra.message.filter.MessageEntityFilter
import org.jep21s.messenger.core.service.repo.common.Pagination

class MessageCassandraSearchProvider(
  private val context: MapperContext,
  private val entityHelper: EntityHelper<MessageEntity>,
) {
  fun search(filter: MessageEntityFilter): CompletionStage<List<MessageEntity>> {
    val select: Select = entityHelper.selectStart()
      .allowFiltering()
      .applyChatId(filter.chatId)
      .applySentDate(filter.sentDate)
      .applyMessageId(filter.id)
      .applyPartOfBody(filter.partOfBody)
      .withSorting(filter)
      .withLimit(filter.limit)

    val asyncFetcher = AsyncFetcher<MessageEntity>(entityHelper)

    context.session
      .executeAsync(select.build())
      .whenComplete(asyncFetcher)

    return asyncFetcher.stage
  }

  private fun Select.applyChatId(
    chatId: UUID,
  ): Select = whereColumn(MessageEntity.COLUMN_CHAT_ID)
    .isEqualTo(
      QueryBuilder.literal(
        chatId,
        context.session.context.codecRegistry
      )
    )

  private fun Select.applySentDate(
    sentDateFilter: ComparableFilter,
  ): Select {
    val literal = QueryBuilder.literal(
      sentDateFilter.value,
      context.session.context.codecRegistry
    )

    return whereColumn(MessageEntity.COLUMN_SENT_DATE)
      .let {
        when (sentDateFilter.direction) {
          ConditionType.EQUAL -> it.isEqualTo(literal)
          ConditionType.LESS -> it.isLessThan(literal)
          ConditionType.GREATER -> it.isGreaterThan(literal)
        }
      }
  }

  private fun Select.applyMessageId(
    messageId: UUID?,
  ): Select {
    if (messageId == null) return this
    return whereColumn(MessageEntity.COLUMN_ID)
      .isEqualTo(
        QueryBuilder.literal(
          messageId,
          context.session.context.codecRegistry
        )
      )
  }

  private fun Select.applyPartOfBody(
    partOfBody: String?,
  ): Select {
    if (partOfBody.isNullOrBlank()) return this
    return whereColumn(MessageEntity.COLUMN_BODY)
      .like(QueryBuilder.literal("%$partOfBody%"))
  }

  private fun Select.withSorting(filter: MessageEntityFilter): Select {
    // отключаем сортировку, если требуется поиск по части тела сообщения,
    // так как Cassandra не поддерживает и то и другое одновременно в данном кейсе
    if (!filter.partOfBody.isNullOrBlank()) return this

    val direction = when (filter.order ?: Pagination.defaultMessageSortDirection) {
      OrderType.DESC -> ClusteringOrder.DESC
      OrderType.ASC -> ClusteringOrder.ASC
    }

    return orderBy(
      MessageEntity.COLUMN_SENT_DATE,
      direction,
    )
  }

  private fun Select.withLimit(limit: Int?): Select =
    limit(Pagination.getValidMessageLimit(limit))
}

