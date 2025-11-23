package org.jep21s.messenger.core.service.repo.inmemory.chat

import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatDeleteTest
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapperImpl
import org.jep21s.messenger.core.service.repo.inmemory.wrap
import java.time.Instant
import java.util.UUID

class ChatRepoDeleteInmemoryTest : ChatDeleteTest() {

  override val existingChatId: UUID = UUID.randomUUID()
  override val nonExistentChatId: UUID = UUID.randomUUID()
  override val whatsappCommunicationType: String = "WHATSAPP"
  override val telegramCommunicationType: String = "TELEGRAM"

  private val db: MutableMap<UUID, EntityWrapper<ChatEntity>> = mutableMapOf()

  override val existingWhatsappChat = Chat(
    id = existingChatId,
    externalId = "whatsapp_ext_1",
    communicationType = whatsappCommunicationType,
    chatType = "PRIVATE",
    payload = mapOf("platform" to "whatsapp", "priority" to "high"),
    createdAt = Instant.now().minusSeconds(3600),
    updatedAt = Instant.now().minusSeconds(1800),
    latestMessageDate = Instant.now().minusSeconds(300)
  )

  override val existingTelegramChat = Chat(
    id = UUID.randomUUID(),
    externalId = "telegram_ext_1",
    communicationType = telegramCommunicationType,
    chatType = "GROUP",
    payload = mapOf("members_count" to 150),
    createdAt = Instant.now().minusSeconds(7200),
    updatedAt = null,
    latestMessageDate = Instant.now().minusSeconds(600)
  )

  override val chatRepo: AChatRepoInitializable = object : AChatRepoInitializable(
    chatRepo = ChatRepoInmemory(db, ChatEntityMapperImpl),
    initChats = listOf(existingWhatsappChat, existingTelegramChat)
  ) {
    override suspend fun addTestData(chats: List<Chat>) {
      chats.forEach {
        val entity = ChatEntityMapperImpl.mapToEntity(it)
        db.put(entity.id, entity.wrap())
      }
    }

    override suspend fun clearDB() {
      db.clear()
    }
  }
}