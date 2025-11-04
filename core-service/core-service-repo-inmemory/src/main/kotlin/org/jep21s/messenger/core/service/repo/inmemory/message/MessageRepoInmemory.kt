package org.jep21s.messenger.core.service.repo.inmemory.message

import java.util.UUID
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion
import org.jep21s.messenger.core.service.common.model.message.MessageSearch
import org.jep21s.messenger.core.service.common.repo.IMessageRepo
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.inmemory.message.mapper.MessageEntityMapper
import org.jep21s.messenger.core.service.repo.inmemory.message.mapper.MessageEntityMapperImpl
import org.jep21s.messenger.core.service.repo.inmemory.wrap

class MessageRepoInmemory(
  private val db: MutableMap<UUID, EntityWrapper<MessageEntity>>,
  private val messageEntityMapper: MessageEntityMapper = MessageEntityMapperImpl,
) : IMessageRepo {

  override suspend fun save(messageCreation: MessageCreation): Message {
    val entity = messageEntityMapper.mapToEntity(messageCreation)
    db.put(entity.id, entity.wrap())
    return messageEntityMapper.mapToModel(entity)
  }

  override suspend fun delete(messageDeletion: MessageDeletion) {
    db.entries.removeAll { entry ->
      val entity = entry.value.entity
      messageDeletion.ids.contains(entity.id) &&
          entity.chatId == messageDeletion.chatId &&
          entity.communicationType == messageDeletion.communicationType
    }
  }

  override suspend fun search(messageSearch: MessageSearch): List<Message> {
    val limit = (messageSearch.limit ?: defaultPaginationLimit).let {
      if (it > maxPaginationLimit) maxPaginationLimit else it
    }

    val entities = db.asSequence()
      .map { (id, messageWrapper) ->
        object : Map.Entry<UUID, MessageEntity> {
          override val key: UUID = id
          override val value: MessageEntity = messageWrapper.entity
        }
      }
      .filterByMessageIds(messageSearch)
      .filterBySentDate(messageSearch)
      .filterByChatId(messageSearch)
      .filterByMessageTypes(messageSearch)
      .filterByCommunicationType(messageSearch)
      .filterByPartOfBody(messageSearch)
      .sort(messageSearch)
      .map { it.value }
      .take(limit)
      .toList()

    return entities.map { messageEntityMapper.mapToModel(it) }
  }
}
