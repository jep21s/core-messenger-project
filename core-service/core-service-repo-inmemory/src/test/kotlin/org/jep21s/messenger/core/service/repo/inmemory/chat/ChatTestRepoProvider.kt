package org.jep21s.messenger.core.service.repo.inmemory.chat

import java.util.UUID
import kotlin.collections.forEach
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapperImpl
import org.jep21s.messenger.core.service.repo.inmemory.wrap

object ChatTestRepoProvider {
  private val db: MutableMap<UUID, EntityWrapper<ChatEntity>> = mutableMapOf()
  private val chatEntityMapper: ChatEntityMapper = ChatEntityMapperImpl

  fun getChatRepoTest(): AChatRepoInitializable = object : AChatRepoInitializable(
    chatRepo = ChatRepoInmemory(db, chatEntityMapper),
    initChats = emptyList()
  ) {
    override suspend fun addTestData(chats: List<Chat>) {

      chats.forEach {
        val entity = ChatEntityMapperImpl.mapToEntity(it)
        db.put(it.id, entity.wrap())
      }
    }

    override suspend fun clearDB() {
      db.clear()
    }
  }
}