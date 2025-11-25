package org.jep21s.messenger.core.service.repo.cassandra.message

import java.util.UUID
import kotlinx.coroutines.future.await
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion
import org.jep21s.messenger.core.service.common.model.message.MessageSearch
import org.jep21s.messenger.core.service.common.repo.IMessageRepo
import org.jep21s.messenger.core.service.repo.cassandra.extention.awaitAll
import org.jep21s.messenger.core.service.repo.cassandra.message.dao.MessageByTypeDao
import org.jep21s.messenger.core.service.repo.cassandra.message.dao.MessageDao
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageByTypeEntity
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.cassandra.message.filter.MessageEntityFilter
import org.jep21s.messenger.core.service.repo.cassandra.message.mapper.MessageEntityMapper
import org.jep21s.messenger.core.service.repo.cassandra.message.mapper.MessageEntityMapperImpl

class MessageRepoCassandra(
  private val messageDao: MessageDao,
  private val messageByTypeDao: MessageByTypeDao,
  private val messageEntityMapper: MessageEntityMapper = MessageEntityMapperImpl,
) : IMessageRepo {
  private val logger = CSCorSettings.loggerProvider
    .logger(this::class)

  override suspend fun save(messageCreation: MessageCreation): Message {
    val entity = messageEntityMapper.mapToEntity(messageCreation)
    val existedMessageBeforeSave: MessageEntity? = messageDao.create(entity)
      .await()
    if (existedMessageBeforeSave != null) {
      logger.warn(
        "message by id [${existedMessageBeforeSave.id}] " +
            "is already existed before"
      )
    }
    return messageEntityMapper.mapToModel(entity)
  }

  override suspend fun delete(messageDeletion: MessageDeletion) {
    messageDeletion.ids
      .map { id ->
        id to messageDao.delete(
          chatId = messageDeletion.chatId,
          sentDate = messageDeletion.sentDate,
          messageId = id
        )
      }
      .map { (id, completionStage) ->
        id to completionStage.await()
      }
      .forEach { (id, result) ->
        if (!result.wasApplied()) {
          logger.warn("message [$id] is not deleted")
          //TODO реализовать логику возврата результата и его обработку
          // если сообщение не было удалено
        }
      }
  }

  override suspend fun search(
    messageSearch: MessageSearch,
  ): List<Message> {
    val ids: List<UUID?> = messageSearch.messageFilter.ids
      .let { ids: List<UUID>? ->
        if (ids.isNullOrEmpty()) {
          listOf(null)
        } else ids
      }

    val messages: List<MessageEntity> = ids.map { messageId: UUID? ->
      val filter: MessageEntityFilter = messageEntityMapper
        .mapToMessageEntityFilter(messageSearch, messageId)
      messageDao.search(filter)
    }
      .awaitAll()
      .flatten()

    val messageTypes: List<String>? = messageSearch.messageFilter.messageTypes
    val result = if (!messageTypes.isNullOrEmpty()) {
      //TODO реализовать логику рекурсивного поиска с использованием mat view
//      if (messages.size < (messageSearch.limit ?: Pagination.DEFAULT_MESSAGE_LIMIT)) {
      messages.filter { messageTypes.contains(it.messageType) }
//      } else {
//      }
    } else {
      messages
    }

    return result.map { messageEntityMapper.mapToModel(it) }
  }

  private suspend fun getMessageByTypes(
    messageSearch: MessageSearch,
    messageTypes: List<String>,
    messageIds: List<UUID?>?,
  ): List<MessageByTypeEntity>? {
    val typeFilter = messageEntityMapper.mapToMessageByTypeEntityFilter(
      messageSearch = messageSearch,
      messageIds = messageIds?.mapNotNull { it }.orEmpty(),
      messageTypes = messageTypes
    )
    return messageByTypeDao.search(typeFilter).await()
  }
}