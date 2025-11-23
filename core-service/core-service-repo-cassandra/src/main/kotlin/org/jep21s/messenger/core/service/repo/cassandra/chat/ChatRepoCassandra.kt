package org.jep21s.messenger.core.service.repo.cassandra.chat

import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import java.util.UUID
import kotlinx.coroutines.future.await
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.jep21s.messenger.core.service.common.repo.IChatRepo
import org.jep21s.messenger.core.service.repo.cassandra.chat.dao.ChatActivityDao
import org.jep21s.messenger.core.service.repo.cassandra.chat.dao.ChatDao
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatActivityEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.filter.ChatActivityEntityFilter
import org.jep21s.messenger.core.service.repo.cassandra.chat.mapper.ChatEntityMapper
import org.jep21s.messenger.core.service.repo.cassandra.chat.mapper.ChatEntityMapperImpl

class ChatRepoCassandra(
  private val chatDao: ChatDao,
  private val chatActivityDao: ChatActivityDao,
  private val chatEntityMapper: ChatEntityMapper = ChatEntityMapperImpl,
) : IChatRepo {
  private val logger = CSCorSettings.loggerProvider
    .logger(this::class)

  override suspend fun save(chatCreation: ChatCreation): Chat {
    val entity: ChatEntity = chatEntityMapper.mapToEntity(chatCreation)
    val existedChatBeforeSave: ChatEntity? = chatDao.create(entity)
      .await()
    if (existedChatBeforeSave != null) {
      logger.warn(
        "chat by id [${existedChatBeforeSave.id}] " +
            "is already existed before"
      )
    }
    return chatEntityMapper.mapToModel(entity, null)
  }

  override suspend fun delete(chatDeletion: ChatDeletion): Chat? {
    val entity: ChatEntity? = chatDao.findById(
      chatDeletion.id,
      chatDeletion.communicationType
    )
      .await()
    val result: AsyncResultSet = chatDao.delete(
      chatDeletion.id,
      chatDeletion.communicationType
    )
      .await()
    if (!result.wasApplied()) {
      logger.warn("message [${chatDeletion.id}] is not deleted")
      //TODO реализовать логику возврата результата и его обработку
      // если чат не был удален
    }
    return entity?.let { chatEntityMapper.mapToModel(it, null) }
  }

  //TODO переписать на рекурсивную довыборку до необходимо лимита
  override suspend fun search(
    chatSearch: ChatSearch,
  ): List<Chat> = with(chatSearch.filter.ids) {
    when {
      isNullOrEmpty() -> getChatActivities(chatSearch, 100)
        .getUniqueByLatestActivity()
        .findChats(chatSearch)

      else -> map { id ->
        getChatActivities(chatSearch, 1, id)
      }
        .flatten()
        .findChats(chatSearch)
    }
  }

  private suspend fun getChatActivities(
    chatSearch: ChatSearch,
    limit: Int,
    chatId: UUID? = null,
  ): List<ChatActivityEntity> = chatActivityDao.search(
    ChatActivityEntityFilter(
      communicationType = chatSearch.filter.communicationType,
      latestMessageDate = chatSearch.filter.latestMessageDate,
      limit = limit,
      chatId = chatId,
    )
  ).await()

  private suspend fun List<ChatActivityEntity>.findChats(
    chatSearch: ChatSearch,
  ): List<Chat> = sortedByDescending { it.latestActivity }
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