package org.jep21s.messenger.core.service.repo.inmemory.chat

import kotlinx.coroutines.test.runTest
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.jep21s.messenger.core.service.repo.inmemory.EntityWrapper
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.repo.inmemory.chat.mapper.ChatEntityMapperImpl

class ChatRepoInmemoryTest {

  private lateinit var chatRepo: ChatRepoInmemory
  private lateinit var db: MutableMap<UUID, EntityWrapper<ChatEntity>>
  private val chatEntityMapper: ChatEntityMapper = ChatEntityMapperImpl

  private val chatId1 = UUID.randomUUID()
  private val chatId2 = UUID.randomUUID()
  private val chatId3 = UUID.randomUUID()
  private val now = Instant.now()

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

  @BeforeEach
  fun setUp() {
    db = mutableMapOf(
      chatId1 to EntityWrapper(chatEntity1),
      chatId2 to EntityWrapper(chatEntity2),
      chatId3 to EntityWrapper(chatEntity3)
    )

    chatRepo = ChatRepoInmemory(db, chatEntityMapper)
  }

  @Test
  fun `search should return all WHATSAPP chats when no filters applied`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(2, result.size)
  }

  @Test
  fun `search should filter by chat ids`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = listOf(chatId1, chatId2),
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(2, result.size)
    assertTrue(result.any { it.id == chatId1 })
    assertTrue(result.any { it.id == chatId2 })
    assertFalse(result.any { it.id == chatId3 })
  }

  @Test
  fun `search should filter by external ids`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = listOf("ext1", "ext2"),
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(2, result.size)
    assertTrue(result.any { it.externalId == "ext1" })
    assertTrue(result.any { it.externalId == "ext2" })
    assertFalse(result.any { it.externalId == "ext3" })
  }

  @Test
  fun `search should filter by latest message date less than`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = ComparableFilter(now.minusSeconds(2000), ConditionType.LESS),
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertEquals(chatId1, result.first().id)
    assertTrue(result.first().latestMessageDate!!.isBefore(now.minusSeconds(2000)))
  }

  @Test
  fun `search should filter by latest message date greater than`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = ComparableFilter(now.minusSeconds(2000), ConditionType.GREATER),
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertTrue(result.all { it.latestMessageDate!!.isAfter(now.minusSeconds(2000)) })
  }

  @Test
  fun `search should filter by chat types`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = null,
        chatTypes = listOf("PRIVATE"),
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(1, result.size)
    assertTrue(result.all { it.chatType == "PRIVATE" })
    assertFalse(result.any { it.chatType == "GROUP" })
  }

  @Test
  fun `search should filter by communication type`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(2, result.size)
    assertTrue(result.all { it.communicationType == "WHATSAPP" })
    assertFalse(result.any { it.communicationType == "TELEGRAM" })
  }

  @Test
  fun `search should sort by latest message date ascending`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = ChatSearch.ChatSearchSort(
        sortField = ChatSearch.ChatSearchSort.SortField.LATEST_MESSAGE_DATE,
        order = OrderType.ASC
      ),
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(2, result.size)
    assertEquals(chatId1, result[0].id) // Older first
    assertEquals(chatId2, result[1].id)
  }

  @Test
  fun `search should sort by latest message date descending`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = ChatSearch.ChatSearchSort(
        sortField = ChatSearch.ChatSearchSort.SortField.LATEST_MESSAGE_DATE,
        order = OrderType.DESC
      ),
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(2, result.size)
    assertEquals(chatId2, result[0].id) // Newer first
    assertEquals(chatId1, result[1].id)
  }

  @Test
  fun `search should handle null latest message date when sorting`() = runTest {
    // Given - create chat with null latestMessageDate
    val chatId4 = UUID.randomUUID()
    val chatEntity4 = ChatEntity(
      id = chatId4,
      externalId = "ext4",
      chatType = "PRIVATE",
      communicationType = "WHATSAPP",
      latestMessageDate = null, // Null date
      createdAt = now,
      updatedAt = null,
      payload = null,
    )
    db[chatId4] = EntityWrapper(chatEntity4)

    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = listOf(chatId1, chatId4), // One with date, one without
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = ChatSearch.ChatSearchSort(
        sortField = ChatSearch.ChatSearchSort.SortField.LATEST_MESSAGE_DATE,
        order = OrderType.ASC
      ),
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then - should not throw exception and return results
    assertEquals(2, result.size)
  }

  @Test
  fun `search should apply limit`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = 2
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(2, result.size)
  }

  @Test
  fun `search should respect max pagination limit`() = runTest {
    // Given - add more chats to test limit
    (1..1500).map { i ->
      val chatId = UUID.randomUUID()
      val chatEntity = ChatEntity(
        id = chatId,
        externalId = "ext_many_$i",
        chatType = "PRIVATE",
        communicationType = "WHATSAPP",
        latestMessageDate = now.plusSeconds(i.toLong()),
        createdAt = now.plusSeconds(i.toLong()),
        updatedAt = null,
        payload = null,
      )
      db[chatId] = EntityWrapper(chatEntity)
      chatEntity
    }

    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = 1500 // More than max limit
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(30, result.size) // Should be limited to maxPaginationLimit
  }

  @Test
  fun `search should use default pagination limit when not specified`() = runTest {
    // Given - add more chats to test default limit
    (1..100).map { i ->
      val chatId = UUID.randomUUID()
      val chatEntity = ChatEntity(
        id = chatId,
        externalId = "ext_default_$i",
        chatType = "PRIVATE",
        communicationType = "WHATSAPP",
        latestMessageDate = now.plusSeconds(i.toLong()),
        createdAt = now.plusSeconds(i.toLong()),
        updatedAt = null,
        payload = null,
      )
      db[chatId] = EntityWrapper(chatEntity)
      chatEntity
    }

    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = null,
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = null // Not specified
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(10, result.size) // Should use defaultPaginationLimit
  }

  @Test
  fun `search should combine multiple filters`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = listOf(chatId1, chatId2, chatId3),
        externalIds = listOf("ext1", "ext2"),
        latestMessageDate = ComparableFilter(now.minusSeconds(4000), ConditionType.GREATER),
        chatTypes = listOf("PRIVATE", "GROUP"),
        communicationType = "WHATSAPP"
      ),
      sort = ChatSearch.ChatSearchSort(
        sortField = ChatSearch.ChatSearchSort.SortField.LATEST_MESSAGE_DATE,
        order = OrderType.DESC
      ),
      limit = 10
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertEquals(2, result.size)
    // Should only return chats that match all filters:
    // - chatEntity1, chatEntity2 and chatEntity3 (from ids filter)
    // - chatEntity1 and chatEntity2 (from externalIds filter)
    // - latestMessageDate > threshold (chatEntity1 and chatEntity2)
    // - chatTypes PRIVATE/GROUP (both)
    // - chatEntity1, chatEntity2 communicationType WHATSAPP (both)
    assertTrue(result.any { it.id == chatId1 })
    assertTrue(result.any { it.id == chatId2 })
    assertFalse(result.any { it.id == chatId3 })
  }

  @Test
  fun `search should return empty list when no chats match filters`() = runTest {
    // Given
    val search = ChatSearch(
      filter = ChatSearch.ChatSearchFilter(
        ids = listOf(UUID.randomUUID()), // Non-existent chat
        externalIds = null,
        latestMessageDate = null,
        chatTypes = null,
        communicationType = "WHATSAPP"
      ),
      sort = null,
      limit = null
    )

    // When
    val result = chatRepo.search(search)

    // Then
    assertTrue(result.isEmpty())
  }

}