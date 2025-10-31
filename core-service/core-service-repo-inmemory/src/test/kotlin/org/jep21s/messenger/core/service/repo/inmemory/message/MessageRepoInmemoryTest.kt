package org.jep21s.messenger.core.service.repo.inmemory.message

import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.message.MessageSearch
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.inmemory.message.mapper.MessageEntityMapper
import org.jep21s.messenger.core.service.repo.inmemory.message.mapper.MessageEntityMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MessageRepoInmemoryTest {

  private lateinit var messageRepo: MessageRepoInmemory
  private lateinit var db: MutableMap<UUID, EntityWrapper<MessageEntity>>
  private val messageEntityMapper: MessageEntityMapper = MessageEntityMapperImpl

  private val chatId1 = UUID.randomUUID()
  private val chatId2 = UUID.randomUUID()
  private val messageId1 = UUID.randomUUID()
  private val messageId2 = UUID.randomUUID()
  private val messageId3 = UUID.randomUUID()
  private val now = Instant.now()

  private val messageEntity1 = MessageEntity(
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

  private val messageEntity2 = MessageEntity(
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

  private val messageEntity3 = MessageEntity(
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

  @BeforeEach
  fun setUp() {
    db = mutableMapOf(
      messageId1 to EntityWrapper(messageEntity1),
      messageId2 to EntityWrapper(messageEntity2),
      messageId3 to EntityWrapper(messageEntity3)
    )

//    messageEntityMapper = mockk()
    messageRepo = MessageRepoInmemory(db, messageEntityMapper)
  }

  @Test
  fun `search should return messages filtered by chat id and communication type`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = null,
      order = null,
      limit = null
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(2, result.size)
    assertTrue(result.any { it.id == messageId1 })
    assertTrue(result.any { it.id == messageId2 })
    assertFalse(result.any { it.id == messageId3 })
  }

  @Test
  fun `search should return empty list when no messages match chat filter`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = UUID.randomUUID(), // Non-existent chat
        communicationType = "WHATSAPP"
      ),
      messageFilter = null,
      order = null,
      limit = null
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertTrue(result.isEmpty())
  }

  @Test
  fun `search should filter by message ids`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = MessageSearch.MessageFilter(
        ids = listOf(messageId1),
        messageTypes = null,
        partOfBody = null,
        sentDate = null
      ),
      order = null,
      limit = null
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertEquals(messageId1, result.first().id)
  }

  @Test
  fun `search should filter by message types`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = listOf("TEXT"),
        partOfBody = null,
        sentDate = null
      ),
      order = null,
      limit = null
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertEquals(messageId1, result.first().id)
    assertEquals("TEXT", result.first().messageType)
  }

  @Test
  fun `search should filter by part of body`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = null,
        partOfBody = "image",
        sentDate = null
      ),
      order = null,
      limit = null
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertEquals(messageId2, result.first().id)
    assertTrue(result.first().body!!.contains("image", ignoreCase = true))
  }

  @Test
  fun `search should filter by sent date less than`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = null,
        partOfBody = null,
        sentDate = ComparableFilter(now.minusSeconds(2700), ConditionType.LESS)
      ),
      order = null,
      limit = null
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertEquals(messageId1, result.first().id)
  }

  @Test
  fun `search should filter by sent date greater than`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = null,
        partOfBody = null,
        sentDate = ComparableFilter(now.minusSeconds(2700), ConditionType.GREATER)
      ),
      order = null,
      limit = null
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertEquals(messageId2, result.first().id)
  }

  @Test
  fun `search should sort by sent date ascending`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = null,
      order = OrderType.ASC,
      limit = null
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(2, result.size)
    assertEquals(messageId1, result[0].id) // Older first
    assertEquals(messageId2, result[1].id)
  }

  @Test
  fun `search should sort by sent date descending`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = null,
      order = OrderType.DESC,
      limit = null
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(2, result.size)
    assertEquals(messageId2, result[0].id) // Newer first
    assertEquals(messageId1, result[1].id)
  }

  @Test
  fun `search should apply limit`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = null,
      order = OrderType.ASC,
      limit = 1
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertEquals(messageId1, result.first().id)
  }

  @Test
  fun `search should respect max pagination limit`() = runTest {
    // Given - add more messages to test limit
    val manyMessages = (1..1500).map { i ->
      val messageId = UUID.randomUUID()
      val messageEntity = MessageEntity(
        id = messageId,
        chatId = chatId1,
        communicationType = "WHATSAPP",
        messageType = "TEXT",
        sentDate = now.plusSeconds(i.toLong()),
        createdAt = now.plusSeconds(i.toLong()),
        updatedAt = null,
        body = "Message $i",
        externalId = "ext$i",
        payload = null
      )
      db[messageId] = EntityWrapper(messageEntity)
      messageEntity
    }

    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = null,
      order = OrderType.ASC,
      limit = 1500 // More than max limit
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(100, result.size) // Should be limited to maxPaginationLimit
  }

  @Test
  fun `search should use default pagination limit when not specified`() = runTest {
    // Given - add more messages to test default limit
    val manyMessages = (1..100).map { i ->
      val messageId = UUID.randomUUID()
      val messageEntity = MessageEntity(
        id = messageId,
        chatId = chatId1,
        communicationType = "WHATSAPP",
        messageType = "TEXT",
        sentDate = now.plusSeconds(i.toLong()),
        createdAt = now.plusSeconds(i.toLong()),
        updatedAt = null,
        body = "Message $i",
        externalId = "ext$i",
        payload = null
      )
      db[messageId] = EntityWrapper(messageEntity)
      messageEntity
    }

    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = null,
      order = OrderType.ASC,
      limit = null // Not specified
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(30, result.size) // Should use defaultPaginationLimit
  }

  @Test
  fun `search should combine multiple filters`() = runTest {
    // Given
    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = MessageSearch.MessageFilter(
        ids = listOf(messageId1, messageId2),
        messageTypes = listOf("TEXT"),
        partOfBody = "Hello",
        sentDate = ComparableFilter(now.minusSeconds(3000), ConditionType.LESS)
      ),
      order = OrderType.ASC,
      limit = 10
    )

    // When
    val result = messageRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertEquals(messageId1, result.first().id)
  }
}