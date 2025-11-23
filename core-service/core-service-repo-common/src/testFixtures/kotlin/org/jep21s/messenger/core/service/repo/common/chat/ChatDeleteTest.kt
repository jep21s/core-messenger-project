package org.jep21s.messenger.core.service.repo.common.chat

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.UUID

abstract class ChatDeleteTest {
  abstract val chatRepo: AChatRepoInitializable

  abstract val existingChatId: UUID
  abstract val nonExistentChatId: UUID
  abstract val whatsappCommunicationType: String
  abstract val telegramCommunicationType: String

  abstract val existingWhatsappChat: Chat
  abstract val existingTelegramChat: Chat

  @BeforeEach
  fun setUp() = runTest {
    chatRepo.initDB()
  }

  @AfterEach
  fun tearDown() = runTest {
    chatRepo.clearDB()
  }

  @Test
  fun `delete should remove existing chat and return it`() = runTest {
    // Given
    val chatDeletion = ChatDeletion(
      id = existingChatId,
      communicationType = whatsappCommunicationType
    )

    // When
    val result = chatRepo.delete(chatDeletion)

    // Then
    assertAll(
      { assertThat(result).isNotNull },
      { assertThat(result?.id).isEqualTo(existingChatId) },
      { assertThat(result?.communicationType).isEqualTo(whatsappCommunicationType) }
    )

    // Verify chat is actually removed
    val searchResult = chatRepo.search(
      org.jep21s.messenger.core.service.common.model.chat.ChatSearch(
        filter = org.jep21s.messenger.core.service.common.model.chat.ChatSearch.ChatSearchFilter(
          ids = listOf(existingChatId),
          externalIds = null,
          latestMessageDate = null,
          chatTypes = null,
          communicationType = whatsappCommunicationType
        ),
        sort = null,
        limit = null
      )
    )
    assertThat(searchResult).isEmpty()
  }

  @Test
  fun `delete should return null when chat does not exist`() = runTest {
    // Given
    val chatDeletion = ChatDeletion(
      id = nonExistentChatId,
      communicationType = whatsappCommunicationType
    )

    // When
    val result = chatRepo.delete(chatDeletion)

    // Then
    assertThat(result).isNull()
  }

  @Test
  fun `delete should return null when communication type does not match`() = runTest {
    // Given - Try to delete WhatsApp chat with Telegram communication type
    val chatDeletion = ChatDeletion(
      id = existingChatId,
      communicationType = telegramCommunicationType // Different from chat's actual type
    )

    // When
    val result = chatRepo.delete(chatDeletion)

    // Then
    assertThat(result).isNull()

    // Verify chat is NOT removed
    val searchResult = chatRepo.search(
      org.jep21s.messenger.core.service.common.model.chat.ChatSearch(
        filter = org.jep21s.messenger.core.service.common.model.chat.ChatSearch.ChatSearchFilter(
          ids = listOf(existingChatId),
          externalIds = null,
          latestMessageDate = null,
          chatTypes = null,
          communicationType = whatsappCommunicationType
        ),
        sort = null,
        limit = null
      )
    )
    assertThat(searchResult).hasSize(1)
    assertThat(searchResult[0].id).isEqualTo(existingChatId)
  }

  @Test
  fun `delete should not affect other chats`() = runTest {
    // Given
    val chatDeletion = ChatDeletion(
      id = existingChatId,
      communicationType = whatsappCommunicationType
    )

    // When
    val result = chatRepo.delete(chatDeletion)

    // Then
    assertThat(result).isNotNull

    // Verify other chat still exists
    val searchResult = chatRepo.search(
      org.jep21s.messenger.core.service.common.model.chat.ChatSearch(
        filter = org.jep21s.messenger.core.service.common.model.chat.ChatSearch.ChatSearchFilter(
          ids = listOf(existingTelegramChat.id),
          externalIds = null,
          latestMessageDate = null,
          chatTypes = null,
          communicationType = telegramCommunicationType
        ),
        sort = null,
        limit = null
      )
    )
    assertAll(
      { assertThat(searchResult).hasSize(1) },
      { assertThat(searchResult[0].id).isEqualTo(existingTelegramChat.id) },
      { assertThat(searchResult[0].communicationType).isEqualTo(telegramCommunicationType) }
    )
  }

  @Test
  fun `delete should return chat with all original fields`() = runTest {
    // Given
    val chatDeletion = ChatDeletion(
      id = existingChatId,
      communicationType = whatsappCommunicationType
    )

    // When
    val result = chatRepo.delete(chatDeletion)

    // Then
    assertAll(
      { assertThat(result?.id).isEqualTo(existingChatId) },
      { assertThat(result?.externalId).isEqualTo(existingWhatsappChat.externalId) },
      { assertThat(result?.communicationType).isEqualTo(whatsappCommunicationType) },
      { assertThat(result?.chatType).isEqualTo(existingWhatsappChat.chatType) },
      { assertThat(result?.payload).isEqualTo(existingWhatsappChat.payload) },
      { assertThat(result?.createdAt).isEqualTo(existingWhatsappChat.createdAt) },
      { assertThat(result?.updatedAt).isEqualTo(existingWhatsappChat.updatedAt) },
      { assertThat(result?.latestMessageDate).isEqualTo(existingWhatsappChat.latestMessageDate) }
    )
  }

  @Test
  fun `multiple delete calls for same chat should work correctly`() = runTest {
    // Given
    val chatDeletion = ChatDeletion(
      id = existingChatId,
      communicationType = whatsappCommunicationType
    )

    // When
    val firstResult = chatRepo.delete(chatDeletion)
    val secondResult = chatRepo.delete(chatDeletion)

    // Then
    assertAll(
      { assertThat(firstResult).isNotNull },
      { assertThat(secondResult).isNull() } // Already deleted
    )
  }

  @Test
  fun `delete should work with chats having null fields`() = runTest {
    // Given - Add a chat with null fields
    val chatWithNullFields = Chat(
      id = UUID.randomUUID(),
      externalId = null,
      communicationType = whatsappCommunicationType,
      chatType = "PRIVATE",
      payload = null,
      createdAt = java.time.Instant.now(),
      updatedAt = null,
      latestMessageDate = null
    )
    chatRepo.addTestData(listOf(chatWithNullFields))

    val chatDeletion = ChatDeletion(
      id = chatWithNullFields.id,
      communicationType = whatsappCommunicationType
    )

    // When
    val result = chatRepo.delete(chatDeletion)

    // Then
    assertAll(
      { assertThat(result).isNotNull },
      { assertThat(result?.id).isEqualTo(chatWithNullFields.id) },
      { assertThat(result?.externalId).isNull() },
      { assertThat(result?.payload).isNull() },
      { assertThat(result?.latestMessageDate).isNull() }
    )
  }
}