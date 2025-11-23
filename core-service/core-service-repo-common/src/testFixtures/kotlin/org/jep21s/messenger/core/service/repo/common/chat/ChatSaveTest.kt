package org.jep21s.messenger.core.service.repo.common.chat

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.UUID
import kotlin.test.assertNotNull

abstract class ChatSaveTest {
  abstract val chatRepo: AChatRepoInitializable

  abstract val existingChatId: UUID
  abstract val newChatExternalId: String
  abstract val communicationType: String
  abstract val chatType: String

  abstract val existingChat: Chat

  @BeforeEach
  fun setUp() = runTest {
    chatRepo.initDB()
  }

  @AfterEach
  fun tearDown() = runTest {
    chatRepo.clearDB()
  }

  @Test
  fun `save should create new chat with all fields`() = runTest {
    // Given
    val chatCreation = ChatCreation(
      externalId = newChatExternalId,
      communicationType = communicationType,
      chatType = chatType,
      payload = mapOf("key1" to "value1", "key2" to 123)
    )

    // When
    val result = chatRepo.save(chatCreation)

    // Then
    assertAll(
      { assertNotNull(result.id) },
      { assertThat(result.externalId).isEqualTo(newChatExternalId) },
      { assertThat(result.communicationType).isEqualTo(communicationType) },
      { assertThat(result.chatType).isEqualTo(chatType) },
      { assertThat(result.payload).isEqualTo(mapOf("key1" to "value1", "key2" to 123)) },
      { assertNotNull(result.createdAt) },
      { assertThat(result.updatedAt).isNull() },
      { assertThat(result.latestMessageDate).isNull() }
    )
  }

  @Test
  fun `save should create chat with null externalId`() = runTest {
    // Given
    val chatCreation = ChatCreation(
      externalId = null,
      communicationType = communicationType,
      chatType = chatType,
      payload = null
    )

    // When
    val result = chatRepo.save(chatCreation)

    // Then
    assertAll(
      { assertNotNull(result.id) },
      { assertThat(result.externalId).isNull() },
      { assertThat(result.communicationType).isEqualTo(communicationType) },
      { assertThat(result.chatType).isEqualTo(chatType) },
      { assertThat(result.payload).isNull() },
      { assertNotNull(result.createdAt) }
    )
  }

  @Test
  fun `save should create chat with null payload`() = runTest {
    // Given
    val chatCreation = ChatCreation(
      externalId = newChatExternalId,
      communicationType = communicationType,
      chatType = chatType,
      payload = null
    )

    // When
    val result = chatRepo.save(chatCreation)

    // Then
    assertAll(
      { assertNotNull(result.id) },
      { assertThat(result.externalId).isEqualTo(newChatExternalId) },
      { assertThat(result.communicationType).isEqualTo(communicationType) },
      { assertThat(result.chatType).isEqualTo(chatType) },
      { assertThat(result.payload).isNull() },
      { assertNotNull(result.createdAt) }
    )
  }

  @Test
  fun `save should generate unique ID for each chat`() = runTest {
    // Given
    val chatCreation1 = ChatCreation(
      externalId = "ext1",
      communicationType = communicationType,
      chatType = chatType,
      payload = null
    )
    val chatCreation2 = ChatCreation(
      externalId = "ext2",
      communicationType = communicationType,
      chatType = chatType,
      payload = null
    )

    // When
    val result1 = chatRepo.save(chatCreation1)
    val result2 = chatRepo.save(chatCreation2)

    // Then
    assertAll(
      { assertThat(result1.id).isNotEqualTo(result2.id) },
      { assertThat(result1.externalId).isEqualTo("ext1") },
      { assertThat(result2.externalId).isEqualTo("ext2") }
    )
  }

  @Test
  fun `save should set createdAt timestamp`() = runTest {
    // Given
    val chatCreation = ChatCreation(
      externalId = newChatExternalId,
      communicationType = communicationType,
      chatType = chatType,
      payload = null
    )

    // When
    val result = chatRepo.save(chatCreation)

    // Then
    assertAll(
      { assertNotNull(result.createdAt) },
      { assertThat(result.createdAt).isBeforeOrEqualTo(java.time.Instant.now()) }
    )
  }

  @Test
  fun `saved chat should be retrievable via search`() = runTest {
    // Given
    val chatCreation = ChatCreation(
      externalId = newChatExternalId,
      communicationType = communicationType,
      chatType = chatType,
      payload = mapOf("test" to "data")
    )

    // When
    val savedChat = chatRepo.save(chatCreation)

    // Then - search by externalId
    val searchResult = chatRepo.search(
      org.jep21s.messenger.core.service.common.model.chat.ChatSearch(
        filter = org.jep21s.messenger.core.service.common.model.chat.ChatSearch.ChatSearchFilter(
          ids = null,
          externalIds = listOf(newChatExternalId),
          latestMessageDate = null,
          chatTypes = null,
          communicationType = communicationType
        ),
        sort = null,
        limit = null
      )
    )

    assertAll(
      { assertThat(searchResult).hasSize(1) },
      { assertThat(searchResult[0].id).isEqualTo(savedChat.id) },
      { assertThat(searchResult[0].externalId).isEqualTo(newChatExternalId) },
      { assertThat(searchResult[0].communicationType).isEqualTo(communicationType) },
      { assertThat(searchResult[0].chatType).isEqualTo(chatType) },
      { assertThat(searchResult[0].payload).isEqualTo(mapOf("test" to "data")) }
    )
  }

  @Test
  fun `save should handle different communication types`() = runTest {
    // Given
    val communicationTypes = listOf("WHATSAPP", "TELEGRAM", "VIBER", "INSTAGRAM")
    val savedChats = mutableListOf<Chat>()

    // When
    communicationTypes.forEach { commType ->
      val chatCreation = ChatCreation(
        externalId = "ext_$commType",
        communicationType = commType,
        chatType = chatType,
        payload = null
      )
      savedChats.add(chatRepo.save(chatCreation))
    }

    // Then
    assertAll(
      { assertThat(savedChats).hasSize(communicationTypes.size) },
      {
        communicationTypes.forEach { commType ->
          assertThat(savedChats).anyMatch { it.communicationType == commType }
        }
      }
    )
  }

  @Test
  fun `save should handle different chat types`() = runTest {
    // Given
    val chatTypes = listOf("PRIVATE", "GROUP", "CHANNEL", "BROADCAST")
    val savedChats = mutableListOf<Chat>()

    // When
    chatTypes.forEach { cType ->
      val chatCreation = ChatCreation(
        externalId = "ext_$cType",
        communicationType = communicationType,
        chatType = cType,
        payload = null
      )
      savedChats.add(chatRepo.save(chatCreation))
    }

    // Then
    assertAll(
      { assertThat(savedChats).hasSize(chatTypes.size) },
      {
        chatTypes.forEach { cType ->
          assertThat(savedChats).anyMatch { it.chatType == cType }
        }
      }
    )
  }
}