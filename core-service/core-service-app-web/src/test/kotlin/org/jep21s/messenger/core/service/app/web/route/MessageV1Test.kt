package org.jep21s.messenger.core.service.app.web.route

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
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteRespAllOfContent
import org.jep21s.messenger.core.service.api.v1.models.MessageResp
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReq
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReqAllOfChatFilter
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReqAllOfMessageFilter
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateReq
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateRespAllOfContent
import org.jep21s.messenger.core.service.api.v1.models.OrderTypeDto
import org.jep21s.messenger.core.service.api.v1.models.ResponseResult
import org.jep21s.messenger.core.lib.test.common.extention.toLinkedHashMap
import org.jep21s.messenger.core.service.api.v1.models.CmDebug
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugMode
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugStubs
import org.jep21s.messenger.core.service.app.web.test.util.testConfiguredApplication
import org.junit.jupiter.api.assertAll

class MessageV1Test {
  @Test
  fun `success delete message`() = testConfiguredApplication { client ->
    //Given
    val ids = listOf(UUIDValue.uuid1, UUIDValue.uuid2)
    val chatId = UUIDValue.uuid10
    val communicationType = "TG"
    val request = MessageDeleteReq(
      requestType = "DELETE_MESSAGE",
      ids = ids,
      chatId = chatId,
      communicationType = communicationType,
      debug = CmDebug(
        mode = CmRequestDebugMode.STUB,
        stub = CmRequestDebugStubs.SUCCESS,
      )
    )

    val expectedResponseBody = CSResponse(
      result = ResponseResult.SUCCESS,
      content = MessageDeleteRespAllOfContent(
        ids = ids,
        chatId = chatId,
        communicationType = communicationType,
      ).toLinkedHashMap()
    )

    //When
    val response = client.post("/v1/message/delete") {
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
  fun `success search message`() = testConfiguredApplication { client ->
    //Given
    val request = MessageSearchReq(
      requestType = "SEARCH_MESSAGE",
      chatFilter = MessageSearchReqAllOfChatFilter(
        id = UUIDValue.uuid2,
        communicationType = "TG"
      ),
      messageFilter = MessageSearchReqAllOfMessageFilter(
        ids = listOf(UUIDValue.uuid1),
        messageTypes = listOf("simple"),
      ),
      order = OrderTypeDto.DESC,
      limit = 10,
      debug = CmDebug(
        mode = CmRequestDebugMode.STUB,
        stub = CmRequestDebugStubs.SUCCESS,
      )
    )

    val expectedResponseBody = CSResponse(
      result = ResponseResult.SUCCESS,
      content = listOf(
        MessageResp(
          id = UUIDValue.uuid1,
          chatId = UUIDValue.uuid2,
          messageType = "simple",
          communicationType = "TG",
          sentDate = Instant.ofEpochSecond(1).toEpochMilli(),
          body = "body",
          externalId = null,
          payload = null,
          createdAt = Instant.ofEpochSecond(1).toEpochMilli(),
          updatedAt = null,
        ).toLinkedHashMap()
      )
    )

    //When
    val response = client.post("/v1/message/search") {
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
  fun `success update message status`() = testConfiguredApplication { client ->
    //Given
    val ids = listOf(UUIDValue.uuid1, UUIDValue.uuid2)
    val chatId = UUIDValue.uuid10
    val communicationType = "TG"
    val status = "status"
    val request = MessageStatusUpdateReq(
      requestType = "UPDATE_STATUS_MESSAGE",
      ids = ids,
      chatId = chatId,
      communicationType = communicationType,
      newStatus = status,
      debug = CmDebug(
        mode = CmRequestDebugMode.STUB,
        stub = CmRequestDebugStubs.SUCCESS,
      )
    )

    val expectedResponseBody = CSResponse(
      result = ResponseResult.SUCCESS,
      content = MessageStatusUpdateRespAllOfContent(
        ids = ids,
        chatId = chatId,
        communicationType = communicationType,
        newStatus = status
      ).toLinkedHashMap()
    )


    //When
    val response = client.post("/v1/message/status/update") {
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