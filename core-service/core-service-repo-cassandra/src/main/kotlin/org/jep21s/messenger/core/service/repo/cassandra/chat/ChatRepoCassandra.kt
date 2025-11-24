package org.jep21s.messenger.core.service.repo.cassandra.chat

import com.datastax.oss.driver.api.core.cql.AsyncResultSet
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
import org.jep21s.messenger.core.service.repo.cassandra.chat.mapper.ChatEntityMapper
import org.jep21s.messenger.core.service.repo.cassandra.chat.mapper.ChatEntityMapperImpl
import org.jep21s.messenger.core.service.repo.cassandra.chat.provider.ChatSearchService

class ChatRepoCassandra(
  private val chatDao: ChatDao,
  private val chatActivityDao: ChatActivityDao,
  private val chatEntityMapper: ChatEntityMapper = ChatEntityMapperImpl,
  private val chatSearchService: ChatSearchService = ChatSearchService(
    chatDao = chatDao,
    chatActivityDao = chatActivityDao,
    chatEntityMapper = chatEntityMapper
  ),
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
    val activityEntity: ChatActivityEntity = chatEntityMapper
      .mapToActivityEntity(chatCreation, entity)
    chatActivityDao.create(activityEntity)
      .await()
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

  override suspend fun search(chatSearch: ChatSearch): List<Chat> =
    chatSearchService.search(chatSearch)
}