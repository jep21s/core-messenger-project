package org.jep21s.messenger.core.service.repo.inmemory.chat

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatSearchPaginationTest
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapperImpl
import org.jep21s.messenger.core.service.repo.inmemory.wrap

class ChatSearchPaginationInmemoryTest : ChatSearchPaginationTest() {
  private val db: MutableMap<UUID, EntityWrapper<ChatEntity>> = mutableMapOf()
  private val chatEntityMapper: ChatEntityMapper = ChatEntityMapperImpl

  override val now: Instant = Instant.now()
  override val chatRepo: AChatRepoInitializable = object : AChatRepoInitializable(
    chatRepo = ChatRepoInmemory(db, chatEntityMapper),
    initChats = emptyList()
  ) {
    override fun addTestData(chats: List<Chat>) {

      chats.forEach {
        val entity = ChatEntityMapperImpl.mapToEntity(it)
        db.put(it.id, entity.wrap())
      }
    }

    override fun clearDB() {
      db.clear()
    }
  }
}