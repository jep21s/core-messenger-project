package org.jep21s.messenger.core.service.repo.common.chat

import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

abstract class ChatSearchPaginationTest {
  abstract val chatRepo: AChatRepoInitializable

  private val now: Instant = Instant.now()

  @Test
  fun `search should respect max pagination limit`() = runTest {
    // Given - add more chats to test limit
    (1..1500).map { i ->
      val chatId = UUID.randomUUID()
      Chat(
        id = chatId,
        externalId = "ext_many_$i",
        chatType = "PRIVATE",
        communicationType = "WHATSAPP",
        latestMessageDate = now.plusSeconds(i.toLong()),
        createdAt = now.plusSeconds(i.toLong()),
        updatedAt = null,
        payload = null,
      )
    }.also { chatRepo.addTestData(it) }

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
    assertAll(
      { assertEquals(30, result.size) } // Should be limited to maxPaginationLimit
    )
  }


  @Test
  fun `search should use default pagination limit when not specified`() = runTest {
    // Given - add more chats to test default limit
    (1..100).map { i ->
      val chatId = UUID.randomUUID()
      Chat(
        id = chatId,
        externalId = "ext_default_$i",
        chatType = "PRIVATE",
        communicationType = "WHATSAPP",
        latestMessageDate = now.plusSeconds(i.toLong()),
        createdAt = now.plusSeconds(i.toLong()),
        updatedAt = null,
        payload = null,
      )
    }.also { chatRepo.addTestData(it) }

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
    assertAll(
      { assertEquals(10, result.size) } // Should use defaultPaginationLimit
    )
  }
}