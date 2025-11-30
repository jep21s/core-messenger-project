package org.jep21s.messenger.core.service.repo.common.message

import io.mockk.every
import io.mockk.mockkStatic
import java.time.Instant
import java.util.UUID
import kotlin.properties.Delegates
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.messageSearch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertAll

abstract class MessageSaveTest {
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
  fun `should success create message`() = runTest {
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
    val expectedResult = Message(
      id = messageId,
      chatId = messageCreation.chatId,
      communicationType = messageCreation.communicationType,
      messageType = messageCreation.messageType,
      senderId = "1",
      senderType = "EMPLOYEE",
      sentDate = messageCreation.sentDate,
      createdAt = instant,
      updatedAt = instant,
      body = messageCreation.body,
      externalId = messageCreation.externalId,
      payload = messageCreation.payload
    )

    var returnedResult by Delegates.notNull<Message>()
    mockkStatic( UUID::class) {
      every { UUID.randomUUID() } returns messageId

      //When
      returnedResult = messageRepo.save(messageCreation)
        .copy(createdAt = instant, updatedAt = instant)
    }
    val dbResult: Message? = messageRepo.search(messageSearch {
      chatFilter {
        id { expectedResult.chatId }
        communicationType { expectedResult.communicationType }
      }
      messageFilter {
        id { expectedResult.id }
        sentDate {
          ComparableFilter(
            expectedResult.sentDate,
            ConditionType.EQUAL
          )
        }
      }
    })
      .firstOrNull()
      ?.copy(createdAt = instant, updatedAt = instant)

    //Then
    assertAll(
      {
        assertThat(dbResult)
          .describedAs("expected result should be equal row in DB")
          .isEqualTo(expectedResult)
      },
      {
        assertThat(returnedResult)
          .describedAs("expected result should be equal method result")
          .isEqualTo(expectedResult)
      }
    )
  }


}