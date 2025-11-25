package org.jep21s.messenger.core.service.repo.inmemory.message

import java.util.UUID
import kotlin.collections.forEach
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.repo.common.message.AMessageRepoInitializable
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.inmemory.message.mapper.MessageEntityMapper
import org.jep21s.messenger.core.service.repo.inmemory.message.mapper.MessageEntityMapperImpl

object MessageTestRepoProvider {
  private val messageEntityMapper: MessageEntityMapper = MessageEntityMapperImpl
  private val db: MutableMap<UUID, EntityWrapper<MessageEntity>> = mutableMapOf()

  fun getMessageRepoTest() = object : AMessageRepoInitializable(
    MessageRepoInmemory(db, messageEntityMapper),
    emptyList()
  ) {
    override suspend fun addTestData(messages: List<Message>) =
      messages.forEach { message ->
        val entity = messageEntityMapper.mapToEntity(message)
        db[entity.id] = EntityWrapper(entity)
      }

    override suspend fun clearDB() = db.clear()
  }

}