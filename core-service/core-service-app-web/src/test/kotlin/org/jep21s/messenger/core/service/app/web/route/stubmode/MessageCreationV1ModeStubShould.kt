package org.jep21s.messenger.core.service.app.web.route.stubmode

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.time.Instant
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.lib.test.common.constant.UUIDValue
import org.jep21s.messenger.core.service.api.v1.models.CSResponse
import org.jep21s.messenger.core.service.api.v1.models.MessageCreateReq
import org.jep21s.messenger.core.service.api.v1.models.MessageResp
import org.jep21s.messenger.core.service.api.v1.models.ResponseResult
import org.jep21s.messenger.core.lib.test.common.extention.toLinkedHashMap
import org.jep21s.messenger.core.service.api.v1.models.CSErrorResp
import org.jep21s.messenger.core.service.api.v1.models.CmDebug
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugMode
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugStubs
import org.jep21s.messenger.core.service.app.web.test.util.testConfiguredApplication
import org.junit.jupiter.api.assertAll

class MessageCreationV1ModeStubShould {
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
        mode = CmRequestDebugMode.STUB,
        stub = CmRequestDebugStubs.SUCCESS,
      )
    )

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

  @Test
  fun `failure creation message because of not found chat`() = testConfiguredApplication { client ->
    //Given
    val request = MessageCreateReq(
      requestType = "CREATE_MESSAGE",
      id = UUIDValue.uuid10,
      chatId = UUIDValue.uuid20,
      communicationType = "TG",
      messageType = "simple",
      senderId = "1",
      senderType = "EMPLOYEE",
      sentDate = Instant.ofEpochSecond(1).toEpochMilli(),
      body = "body",
      externalId = null,
      payload = null,
      debug = CmDebug(
        mode = CmRequestDebugMode.STUB,
        stub = CmRequestDebugStubs.NOT_FOUND,
      )
    )

    val expectedResponseBody = CSResponse(
      result = ResponseResult.ERROR,
      errors = listOf(
        CSErrorResp(
          code = "not-found-chat-for-message-creation",
          group = "not-found",
          field = mapOf("chatId" to request.chatId.toString()).toString(),
          message = "Ошибка при попытке сохранить сообщение. Чат не найден",
        )
      )
    )

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

  @Test
  fun `failure creation message because of database error`() = testConfiguredApplication { client ->
    //Given
    val request = MessageCreateReq(
      requestType = "CREATE_MESSAGE",
      id = UUIDValue.uuid2,
      chatId = UUIDValue.uuid10,
      communicationType = "TG",
      messageType = "simple",
      senderId = "1",
      senderType = "EMPLOYEE",
      sentDate = Instant.ofEpochSecond(1).toEpochMilli(),
      body = "body",
      externalId = null,
      payload = null,
      debug = CmDebug(
        mode = CmRequestDebugMode.STUB,
        stub = CmRequestDebugStubs.DB_ERROR,
      )
    )

    val expectedResponseBody = CSResponse(
      result = ResponseResult.ERROR,
      errors = listOf(
        CSErrorResp(
          code = "internal-db-error",
          group = "internal",
          field = buildMap {
            put("chatId", request.chatId.toString())
            request.id?.let { put("messageId", it.toString()) }
          }.toString(),
          message = "Ошибка при попытке сохранить сообщение. База данных недоступна",
        )
      )
    )

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