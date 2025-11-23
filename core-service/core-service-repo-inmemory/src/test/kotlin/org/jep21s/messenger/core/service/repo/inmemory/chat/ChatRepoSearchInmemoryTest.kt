package org.jep21s.messenger.core.service.repo.inmemory.chat

import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapper
import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatSearchTest
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapperImpl
import org.jep21s.messenger.core.service.repo.inmemory.wrap

class ChatRepoSearchInmemoryTest: ChatSearchTest() {

  override val chatId1: UUID = UUID.randomUUID()
  override val chatId2: UUID = UUID.randomUUID()
  override val chatId3: UUID = UUID.randomUUID()
  override val now: Instant = Instant.now()

  private val db: MutableMap<UUID, EntityWrapper<ChatEntity>> = mutableMapOf()
  private val chatEntityMapper: ChatEntityMapper = ChatEntityMapperImpl

  private val chatEntity1 = ChatEntity(
    id = chatId1,
    externalId = "ext1",
    chatType = "PRIVATE",
    communicationType = "WHATSAPP",
    latestMessageDate = now.minusSeconds(3600),
    createdAt = now.minusSeconds(3600),
    updatedAt = null,
    payload = null,
  )

  override val chatModel1 = Chat(
    id = chatEntity1.id,
    externalId = chatEntity1.externalId,
    communicationType = chatEntity1.communicationType,
    chatType = chatEntity1.chatType,
    payload = chatEntity1.payload,
    createdAt = chatEntity1.createdAt,
    updatedAt = chatEntity1.updatedAt,
    latestMessageDate = chatEntity1.latestMessageDate,
  )

  private val chatEntity2 = ChatEntity(
    id = chatId2,
    externalId = "ext2",
    chatType = "GROUP",
    communicationType = "WHATSAPP",
    latestMessageDate = now.minusSeconds(1800),
    createdAt = now.minusSeconds(1800),
    updatedAt = null,
    payload = null,
  )

  override val chatModel2 = Chat(
    id = chatEntity2.id,
    externalId = chatEntity2.externalId,
    communicationType = chatEntity2.communicationType,
    chatType = chatEntity2.chatType,
    payload = chatEntity2.payload,
    createdAt = chatEntity2.createdAt,
    updatedAt = chatEntity2.updatedAt,
    latestMessageDate = chatEntity2.latestMessageDate,
  )

  private val chatEntity3 = ChatEntity(
    id = chatId3,
    externalId = "ext3",
    chatType = "PRIVATE",
    communicationType = "TELEGRAM",
    latestMessageDate = now.minusSeconds(900),
    createdAt = now.minusSeconds(900),
    updatedAt = null,
    payload = null,
  )

  override val chatModel3 = Chat(
    id = chatEntity3.id,
    externalId = chatEntity3.externalId,
    communicationType = chatEntity3.communicationType,
    chatType = chatEntity3.chatType,
    payload = chatEntity3.payload,
    createdAt = chatEntity3.createdAt,
    updatedAt = chatEntity3.updatedAt,
    latestMessageDate = chatEntity3.latestMessageDate,
  )

  override val chatRepo: AChatRepoInitializable = object: AChatRepoInitializable(
    chatRepo = ChatRepoInmemory(db, chatEntityMapper),
    initChats = listOf(chatModel1, chatModel2, chatModel3),
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