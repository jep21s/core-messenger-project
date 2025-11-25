package org.jep21s.messenger.core.service.repo.common.chat

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

abstract class ChatSearchTest {
  abstract val chatRepo: AChatRepoInitializable

  private val chatId1: UUID = UUID.randomUUID()
  private val chatId2: UUID = UUID.randomUUID()
  private val chatId3: UUID = UUID.randomUUID()
  private val now: Instant = Instant.now()

  private val chatModel1 = Chat(
    id = chatId1,
    externalId = "ext1",
    communicationType = "WHATSAPP",
    chatType = "PRIVATE",
    payload = null,
    createdAt = now.minusSeconds(3600)
      .truncatedTo(ChronoUnit.MILLIS),
    updatedAt = null,
    latestMessageDate = now.minusSeconds(3600)
      .truncatedTo(ChronoUnit.MILLIS),
  )

  private val chatModel2 = Chat(
    id = chatId2,
    externalId = "ext2",
    communicationType = "WHATSAPP",
    chatType = "GROUP",
    payload = null,
    createdAt = now.minusSeconds(1800)
      .truncatedTo(ChronoUnit.MILLIS),
    updatedAt = null,
    latestMessageDate = now.minusSeconds(1800)
      .truncatedTo(ChronoUnit.MILLIS),
  )

  private val chatModel3 = Chat(
    id = chatId3,
    externalId = "ext3",
    communicationType = "TELEGRAM",
    chatType = "PRIVATE",
    payload = null,
    createdAt = now.minusSeconds(900)
      .truncatedTo(ChronoUnit.MILLIS),
    updatedAt = null,
    latestMessageDate = now.minusSeconds(900)
      .truncatedTo(ChronoUnit.MILLIS),
  )

  @BeforeEach
  fun setUp() = runTest {
    chatRepo.initDB()
    chatRepo.addTestData(listOf(chatModel1, chatModel2, chatModel3))
  }

  @AfterEach
  fun tearDown() = runTest {
    chatRepo.clearDB()
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
    val expectedResult = listOf(chatModel1, chatModel2)

    // When
    val result = chatRepo.search(search)

    // Then
    assertAll(
      {
        assertThat(result.size)
          .describedAs { "equals chat result size" }
          .isEqualTo(expectedResult.size)
      },
      {
        assertThat(result)
          .containsAll(expectedResult)
      }
    )
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
    assertAll(
      { assertEquals(2, result.size) },
      { assertTrue(result.any { it.id == chatId1 }) },
      { assertTrue(result.any { it.id == chatId2 }) },
      { assertFalse(result.any { it.id == chatId3 }) }
    )
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
    assertAll(
      { assertEquals(2, result.size) },
      { assertTrue(result.any { it.externalId == "ext1" }) },
      { assertTrue(result.any { it.externalId == "ext2" }) },
      { assertFalse(result.any { it.externalId == "ext3" }) },
    )
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
    assertAll(
      { assertEquals(1, result.size) },
      { assertEquals(chatId1, result.first().id) },
      {
        assertTrue(
          result.first().latestMessageDate!!
            .isBefore(now.minusSeconds(2000))
        )
      },
    )
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
    assertAll(
      { assertEquals(1, result.size) },
      {
        assertTrue(result.all {
          it.latestMessageDate!!
            .isAfter(now.minusSeconds(2000))
        })
      }
    )
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
    assertAll(
      { assertEquals(1, result.size) },
      { assertTrue(result.all { it.chatType == "PRIVATE" }) },
      { assertFalse(result.any { it.chatType == "GROUP" }) }
    )
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
    assertAll(
      { assertEquals(2, result.size) },
      { assertTrue(result.all { it.communicationType == "WHATSAPP" }) },
      { assertFalse(result.any { it.communicationType == "TELEGRAM" }) }
    )
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
    assertAll(
      { assertEquals(2, result.size) },
      { assertEquals(chatId1, result[0].id) }, // Older first
      { assertEquals(chatId2, result[1].id) }
    )
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
    assertAll(
      { assertEquals(2, result.size) },
      { assertEquals(chatId2, result[0].id) }, // Newer first
      { assertEquals(chatId1, result[1].id) }
    )
  }

  @Test
  fun `search should handle null latest message date when sorting`() = runTest {
    // Given - create chat with null latestMessageDate
    val chatId4 = UUID.randomUUID()
    chatModel1.copy(
      id = chatId4,
      externalId = "ext4",
      chatType = "PRIVATE",
      communicationType = "WHATSAPP",
      latestMessageDate = null, // Null date
      createdAt = now,
      updatedAt = null,
      payload = null,
    ).also { chatRepo.addTestData(listOf(it)) }

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
    assertAll(
      { assertEquals(2, result.size) }
    )
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
    assertAll(
      { assertEquals(2, result.size) }
    )
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
    assertAll(
      { assertEquals(2, result.size) },
      // Should only return chats that match all filters:
      // - chatEntity1, chatEntity2 and chatEntity3 (from ids filter)
      // - chatEntity1 and chatEntity2 (from externalIds filter)
      // - latestMessageDate > threshold (chatEntity1 and chatEntity2)
      // - chatTypes PRIVATE/GROUP (both)
      // - chatEntity1, chatEntity2 communicationType WHATSAPP (both)
      { assertTrue(result.any { it.id == chatId1 }) },
      { assertTrue(result.any { it.id == chatId2 }) },
      { assertFalse(result.any { it.id == chatId3 }) }
    )
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
    assertAll(
      { assertTrue(result.isEmpty()) }
    )
  }

}