package org.jep21s.messenger.core.service.repo.inmemory.message

import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.repo.common.message.AMessageRepoInitializable
import org.jep21s.messenger.core.service.repo.common.message.MessageSearchTest
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.inmemory.message.mapper.MessageEntityMapper
import org.jep21s.messenger.core.service.repo.inmemory.message.mapper.MessageEntityMapperImpl

class MessageRepoSearchInmemoryTest : MessageSearchTest() {
  private val messageEntityMapper: MessageEntityMapper = MessageEntityMapperImpl
  private val db: MutableMap<UUID, EntityWrapper<MessageEntity>> = mutableMapOf()

  override val chatId1: UUID = UUID.randomUUID()
  override val chatId2: UUID = UUID.randomUUID()
  override val messageId1: UUID = UUID.randomUUID()
  override val messageId2: UUID = UUID.randomUUID()
  override val messageId3: UUID = UUID.randomUUID()
  override val now: Instant = Instant.now()

  private val messageEntity1 = Message(
    id = messageId1,
    chatId = chatId1,
    communicationType = "WHATSAPP",
    messageType = "TEXT",
    sentDate = now.minusSeconds(3600),
    createdAt = now.minusSeconds(3600),
    updatedAt = null,
    body = "Hello, world!",
    externalId = "ext1",
    payload = null
  )

  private val messageEntity2 = Message(
    id = messageId2,
    chatId = chatId1,
    communicationType = "WHATSAPP",
    messageType = "IMAGE",
    sentDate = now.minusSeconds(1800),
    createdAt = now.minusSeconds(1800),
    updatedAt = null,
    body = "Check this image",
    externalId = "ext2",
    payload = mapOf("url" to "http://example.com/image.jpg")
  )

  private val messageEntity3 = Message(
    id = messageId3,
    chatId = chatId2,
    communicationType = "TELEGRAM",
    messageType = "TEXT",
    sentDate = now.minusSeconds(900),
    createdAt = now.minusSeconds(900),
    updatedAt = null,
    body = "Telegram message",
    externalId = "ext3",
    payload = null
  )

  override val messageRepo: AMessageRepoInitializable = object : AMessageRepoInitializable(
    MessageRepoInmemory(db, messageEntityMapper),
    listOf(messageEntity1, messageEntity2, messageEntity3)
  ) {
    override fun addTestData(messages: List<Message>) =
      messages.forEach { message ->
        val entity = messageEntityMapper.mapToEntity(message)
        db[entity.id] = EntityWrapper(entity)
      }

    override fun clearDB() = db.clear()
  }
}