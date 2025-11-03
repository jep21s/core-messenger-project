package org.jep21s.messenger.core.service.repo.inmemory.chat

import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatSaveTest
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapperImpl
import org.jep21s.messenger.core.service.repo.inmemory.wrap
import java.time.Instant
import java.util.UUID

class ChatRepoSaveInmemoryTest : ChatSaveTest() {

  override val existingChatId: UUID = UUID.randomUUID()
  override val newChatExternalId: String = "new_external_id"
  override val communicationType: String = "WHATSAPP"
  override val chatType: String = "PRIVATE"

  private val db: MutableMap<UUID, EntityWrapper<ChatEntity>> = mutableMapOf()

  override val existingChat = Chat(
    id = existingChatId,
    externalId = "existing_ext",
    communicationType = communicationType,
    chatType = chatType,
    payload = mapOf("existing" to "data"),
    createdAt = Instant.now().minusSeconds(3600),
    updatedAt = null,
    latestMessageDate = null
  )

  override val chatRepo: AChatRepoInitializable = object : AChatRepoInitializable(
    chatRepo = ChatRepoInmemory(db, ChatEntityMapperImpl),
    initChats = listOf(existingChat)
  ) {
    override fun addTestData(chats: List<Chat>) {
      chats.forEach {
        val entity = ChatEntityMapperImpl.mapToEntity(it)
        db.put(entity.id, entity.wrap())
      }
    }

    override fun clearDB() {
      db.clear()
    }
  }
}