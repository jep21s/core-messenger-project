package org.jep21s.messenger.core.service.repo.inmemory.chat

import java.util.UUID
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.jep21s.messenger.core.service.common.repo.IChatRepo
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapperImpl
import org.jep21s.messenger.core.service.repo.inmemory.wrap

class ChatRepoInmemory(
  private val db: MutableMap<UUID, EntityWrapper<ChatEntity>>,
  private val chatEntityMapper: ChatEntityMapper = ChatEntityMapperImpl,
) : IChatRepo {

  override suspend fun save(chatCreation: ChatCreation): Chat {
    val entity = chatEntityMapper.mapToEntity(chatCreation)
    db.put(entity.id, entity.wrap())
    return chatEntityMapper.mapToModel(entity)
  }

  override suspend fun delete(chatDeletion: ChatDeletion): Chat? {
    val entity: ChatEntity? = db[chatDeletion.id]?.entity
      ?.takeIf { it.communicationType == chatDeletion.communicationType }
      ?.also { db.remove(it.id) }
    return entity?.let { chatEntityMapper.mapToModel(it) }
  }

  override suspend fun search(chatSearch: ChatSearch): List<Chat> {
    val limit = (chatSearch.limit ?: defaultPaginationLimit).let {
      if (it > maxPaginationLimit) maxPaginationLimit else it
    }

    val entities = db.asSequence()
      .map { (id, chatWrapper) ->
        object : Map.Entry<UUID, ChatEntity> {
          override val key: UUID = id
          override val value: ChatEntity = chatWrapper.entity
        }
      }
      .filterByIds(chatSearch)
      .filterByExternalIds(chatSearch)
      .filterByLatestMessageDate(chatSearch)
      .filterByChatTypes(chatSearch)
      .filterByCommunicationType(chatSearch)
      .sort(chatSearch)
      .map { it.value }
      .take(limit)
      .toList()

    return entities.map { chatEntityMapper.mapToModel(it) }
  }
}