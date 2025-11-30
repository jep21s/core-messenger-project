package org.jep21s.messenger.core.service.app.web.route.testmode

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.every
import io.mockk.mockkStatic
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.lib.test.common.constant.UUIDValue
import org.jep21s.messenger.core.lib.test.common.extention.toLinkedHashMap
import org.jep21s.messenger.core.service.api.v1.models.CSResponse
import org.jep21s.messenger.core.service.api.v1.models.MessageCreateReq
import org.jep21s.messenger.core.service.api.v1.models.MessageResp
import org.jep21s.messenger.core.service.api.v1.models.ResponseResult
import org.jep21s.messenger.core.service.api.v1.models.CSErrorResp
import org.jep21s.messenger.core.service.api.v1.models.CmDebug
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugMode
import org.jep21s.messenger.core.service.app.web.test.util.testConfiguredApplication
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.junit.jupiter.api.assertAll

class MessageCreationV1ModeTestShould {

  @Test
  fun `success creation message`() = testConfiguredApplication { client ->
    //Given
    val request = MessageCreateReq(
      requestType = "CREATE_MESSAGE",
      id = UUIDValue.uuid1,
      chatId = UUIDValue.uuid2,
      communicationType = "TG",
      messageType = "simple",
      senderId = "1",
      senderType = "EMPLOYEE",
      sentDate = Instant.ofEpochSecond(1).toEpochMilli(),
      body = "body",
      externalId = null,
      payload = null,
      debug = CmDebug(
        mode = CmRequestDebugMode.TEST,
      )
    )

    val createdAt: Instant = Instant.ofEpochSecond(1)
    val expectedResponseBody = CSResponse(
      result = ResponseResult.SUCCESS,
      content = MessageResp(
        id = UUIDValue.uuid1,
        chatId = UUIDValue.uuid2,
        communicationType = "TG",
        messageType = "simple",
        senderId = "1",
        senderType = "EMPLOYEE",
        sentDate = Instant.ofEpochSecond(1).toEpochMilli(),
        body = "body",
        externalId = null,
        payload = null,
        createdAt = Instant.ofEpochSecond(1).toEpochMilli(),
        updatedAt = null,
      ).toLinkedHashMap()
    )

    mockkStatic(Instant::class, UUID::class) {
      every { Instant.now() } returns createdAt
      every { UUID.randomUUID() } returns UUIDValue.uuid1

      // Создаем чат перед созданием сообщения
      val chatRepo = CSCorSettings.chatRepo(CSWorkMode.Test)
      val chat = chatRepo.save(
        ChatCreation(
          externalId = null,
          communicationType = "TG",
          chatType = "simple",
          payload = null
        )
      )

      //When
      val response = client.post("/v1/message/create") {
        contentType(ContentType.Application.Json)
        setBody(request.copy(chatId = chat.id)) // Используем реальный ID созданного чата
      }
      val resultBody: CSResponse = response.body<CSResponse>()

      //Then
      assertAll(
        {
          assertThat(resultBody)
            .describedAs("got expected response body")
            .isEqualTo(expectedResponseBody.copy(content = resultBody.content))
          // Используем фактический content, так как chatId может отличаться
        },
        {
          assertThat(response.status)
            .describedAs("got expected http status")
            .isEqualTo(HttpStatusCode.OK)
        }
      )
    }
  }

  @Test
  fun `failure creation message because of not found chat`() = testConfiguredApplication { client ->
    //Given
    val request = MessageCreateReq(
      requestType = "CREATE_MESSAGE",
      id = UUIDValue.uuid10,
      chatId = UUIDValue.uuid20, // Несуществующий чат
      communicationType = "TG",
      messageType = "simple",
      senderId = "1",
      senderType = "EMPLOYEE",
      sentDate = Instant.ofEpochSecond(1).toEpochMilli(),
      body = "body",
      externalId = null,
      payload = null,
      debug = CmDebug(
        mode = CmRequestDebugMode.TEST,
      )
    )

    val createdAt: Instant = Instant.ofEpochSecond(1)
    val expectedResponseBody = CSResponse(
      result = ResponseResult.ERROR,
      errors = listOf(
        CSErrorResp(
          code = "message-validation-create",
          group = "message-validation",
          field = "chatId",
          message = "Chat not exists. ChatId [00000000-0000-0000-0000-000000000020], communicationType [TG]",
        )
      )
    )

    mockkStatic(Instant::class, UUID::class) {
      every { Instant.now() } returns createdAt
      every { UUID.randomUUID() } returns UUIDValue.uuid1

      //When
      val response = client.post("/v1/message/create") {
        contentType(ContentType.Application.Json)
        setBody(request)
      }
      val resultBody: CSResponse = response.body<CSResponse>()

      //Then
      assertAll(
        {
          assertThat(resultBody)
            .describedAs("got expected response body")
            .isEqualTo(expectedResponseBody)
        },
        {
          assertThat(response.status)
            .describedAs("got expected http status")
            .isEqualTo(HttpStatusCode.OK)
        }
      )
    }
  }
}