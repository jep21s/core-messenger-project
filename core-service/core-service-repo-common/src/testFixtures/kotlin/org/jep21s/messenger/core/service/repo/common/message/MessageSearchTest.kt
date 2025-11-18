package org.jep21s.messenger.core.service.repo.common.message

import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageSearch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class MessageSearchTest {
  abstract val messageRepo: AMessageRepoInitializable

  protected abstract val chatId1: UUID
  protected abstract val chatId2: UUID
  protected abstract val messageId1: UUID
  protected abstract val messageId2: UUID
  protected abstract val messageId3: UUID
  protected abstract val now: Instant

  @BeforeEach
  fun setUp() {
    messageRepo.initDB()
  }

  @AfterEach
  fun tearDown() {
    messageRepo.clearDB()
  }

  @Test
  fun `search should return messages filtered by chat id and communication type`() = runTest {
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
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )
      ),
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
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = null,
        partOfBody = null,
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )
      ),
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
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )
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
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )

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
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )

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
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = null,
        partOfBody = null,
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )
      ),
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
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = null,
        partOfBody = null,
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )
      ),
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
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = null,
        partOfBody = null,
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )
      ),
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
      Message(
        id = UUID.randomUUID(),
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
    }
    messageRepo.addTestData(manyMessages)

    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = null,
        partOfBody = null,
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )
      ),
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
      Message(
        id = UUID.randomUUID(),
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
    }
    messageRepo.addTestData(manyMessages)

    val search = MessageSearch(
      chatFilter = MessageSearch.ChatFilter(
        id = chatId1,
        communicationType = "WHATSAPP"
      ),
      messageFilter = MessageSearch.MessageFilter(
        ids = null,
        messageTypes = null,
        partOfBody = null,
        sentDate = ComparableFilter(
          value = Instant.ofEpochSecond(1),
          direction = ConditionType.GREATER
        )
      ),
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