package org.jep21s.messenger.core.service.repo.cassandra.chat.provider

import java.util.UUID
import kotlinx.coroutines.future.await
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.jep21s.messenger.core.service.repo.cassandra.chat.ChatActivityBucketCalculator
import org.jep21s.messenger.core.service.repo.cassandra.chat.dao.ChatActivityDao
import org.jep21s.messenger.core.service.repo.cassandra.chat.dao.ChatDao
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatActivityEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.filter.ChatActivityEntityFilter
import org.jep21s.messenger.core.service.repo.cassandra.chat.mapper.ChatEntityMapper
import org.jep21s.messenger.core.service.repo.cassandra.chat.mapper.ChatEntityMapperImpl
import org.jep21s.messenger.core.service.repo.common.Pagination

class ChatSearchService(
  private val chatDao: ChatDao,
  private val chatActivityDao: ChatActivityDao,
  private val chatEntityMapper: ChatEntityMapper = ChatEntityMapperImpl,
) {
  //TODO переписать на рекурсивную довыборку до необходимо лимита
  suspend fun search(
    chatSearch: ChatSearch,
  ): List<Chat> = getChatActivities(chatSearch, 100)
    .getUniqueByLatestActivity()
    .findChats(chatSearch)

  private suspend fun getChatActivities(
    chatSearch: ChatSearch,
    limit: Int,
  ): List<ChatActivityEntity> {
    val activities = chatActivityDao.search(
      ChatActivityEntityFilter(
        communicationType = chatSearch.filter.communicationType,
        bucketDay = ChatActivityBucketCalculator
          .calculateBucketDay(chatSearch.filter.latestMessageDate?.value),
        limit = limit,
        order = Pagination.getChatOrder(chatSearch),
        sourceFilter = chatSearch,
      )
    ).await()
    val ids: List<UUID>? = chatSearch.filter.ids
    if (ids.isNullOrEmpty()) return activities
    //TODO переписать на отдельную таблицу для запросов по chatId
    return activities.filter { ids.contains(it.chatId) }
  }

  private suspend fun List<ChatActivityEntity>.findChats(
    chatSearch: ChatSearch,
  ): List<Chat> = when (Pagination.getChatOrder(chatSearch)) {
    OrderType.DESC -> sortedByDescending { it.latestActivity }
    OrderType.ASC -> sortedBy { it.latestActivity }
  }
    .mapNotNull { chatActivity ->
      val chatEntity = chatDao.findById(
        chatActivity.chatId,
        chatSearch.filter.communicationType
      )
        .await() ?: return@mapNotNull null
      chatActivity to chatEntity
    }
    .filterByChatTypes(chatSearch)
    .filterByExternalIds(chatSearch)
    .take(Pagination.getValidChatLimit(chatSearch.limit))
    .map { (chatActivityEntity, chatEntity) ->
      chatEntityMapper.mapToModel(chatEntity, chatActivityEntity.latestActivity)
    }


  private fun List<Pair<ChatActivityEntity, ChatEntity>>.filterByChatTypes(
    chatSearch: ChatSearch,
  ): List<Pair<ChatActivityEntity, ChatEntity>> = filter { (_, chatEntity) ->
    chatSearch.filter.chatTypes
      .takeIf { chatTypes -> !chatTypes.isNullOrEmpty() }
      ?.contains(chatEntity.chatType)
      ?: true
  }

  private fun List<Pair<ChatActivityEntity, ChatEntity>>.filterByExternalIds(
    chatSearch: ChatSearch,
  ): List<Pair<ChatActivityEntity, ChatEntity>> = filter { (_, chatEntity) ->
    chatSearch.filter.externalIds
      .takeIf { externalIds ->
        !externalIds.isNullOrEmpty() &&
            chatEntity.externalId != null
      }
      ?.contains(chatEntity.externalId)
      ?: true
  }

  fun List<ChatActivityEntity>.getUniqueByLatestActivity(): List<ChatActivityEntity> {
    val latestByChatId = mutableMapOf<UUID, ChatActivityEntity>()

    for (entity in this) {
      val current = latestByChatId[entity.chatId]
      if (current == null || entity.latestActivity > current.latestActivity) {
        latestByChatId[entity.chatId] = entity
      }
    }

    return latestByChatId.values
      .toList()
  }
}