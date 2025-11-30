package org.jep21s.messenger.core.service.repo.common.message

import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion
import org.jep21s.messenger.core.service.common.model.message.messageSearch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertAll

abstract class MessageDeleteTest {
  abstract val messageRepo: AMessageRepoInitializable

  @BeforeEach
  fun setUp() = runTest {
    messageRepo.initDB()
  }

  @AfterEach
  fun tearDown() = runTest {
    messageRepo.clearDB()
  }

  @Test
  fun `should success delete message`() = runTest {
    //Given
    val messageId = UUID.randomUUID()
    val instant = Instant.ofEpochMilli(1000)
    val messageCreation = MessageCreation(
      id = messageId,
      chatId = UUID.randomUUID(),
      communicationType = "TG",
      messageType = "simple",
      senderId = "1",
      senderType = "EMPLOYEE",
      sentDate = instant,
      body = "bla-bla",
      externalId = null,
      payload = mapOf(
        "myField" to "myValues",
        "mySecondField" to 5,
        "nullField" to null,
        "objectField" to mapOf(
          "innerField" to 42,
          "innerNullField" to null,
        )
      )
    )
    messageRepo.save(messageCreation)
    val existedMessage = requireNotNull(
      findMessage(
        chatId = messageCreation.chatId,
        communicationType = messageCreation.communicationType,
        messageId = messageId,
        sentDate = messageCreation.sentDate
      )
    )

    val deleteReq = MessageDeletion(
      ids = setOf(existedMessage.id),
      chatId = existedMessage.chatId,
      sentDate = existedMessage.sentDate,
      communicationType = existedMessage.communicationType
    )

    //When
    messageRepo.delete(deleteReq)

    //Then
    val result = findMessage(
      chatId = messageCreation.chatId,
      communicationType = messageCreation.communicationType,
      messageId = messageId,
      sentDate = messageCreation.sentDate
    )

    assertAll(
      {
        assertThat(result)
          .describedAs("message success deleted")
          .isNull()
      }
    )
  }

  private suspend fun findMessage(
    chatId: UUID,
    communicationType: String,
    messageId: UUID,
    sentDate: Instant,
  ): Message? = messageRepo.search(messageSearch {
    chatFilter {
      id { chatId }
      communicationType { communicationType }
    }
    messageFilter {
      id { messageId }
      sentDate {
        ComparableFilter(
          sentDate,
          ConditionType.EQUAL
        )
      }
    }
  }).firstOrNull()
}