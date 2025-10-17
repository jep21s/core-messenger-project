package org.jep21s.messenger.core.service.app.web.route

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.HttpStatusCode
import java.time.Instant
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.lib.test.common.constant.UUIDValue
import org.jep21s.messenger.core.service.api.v1.models.CSResponse
import org.jep21s.messenger.core.service.api.v1.models.ChatCreateReq
import org.jep21s.messenger.core.service.api.v1.models.ChatDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.ChatResp
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReq
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfFilter
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfSort
import org.jep21s.messenger.core.service.api.v1.models.OrderTypeDto
import org.jep21s.messenger.core.service.api.v1.models.ResponseResult
import org.jep21s.messenger.core.service.app.web.test.util.testConfiguredApplication
import org.jep21s.messenger.core.lib.test.common.extention.toLinkedHashMap
import org.jep21s.messenger.core.service.api.v1.models.CmDebug
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugMode
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugStubs
import org.junit.jupiter.api.assertAll

class ChatV1Test {
  @Test
  fun `success creation chat`() = testConfiguredApplication { client ->
    //Given
    val request = ChatCreateReq(
      requestType = "CREATE_CHAT",
      communicationType = "TG",
      chatType = "simple",
      debug = CmDebug(
        mode = CmRequestDebugMode.STUB,
        stub = CmRequestDebugStubs.SUCCESS,
      )
    )

    val expectedResponseBody = CSResponse(
      result = ResponseResult.SUCCESS,
      content = ChatResp(
        id = UUIDValue.uuid1,
        externalId = request.externalId,
        communicationType = request.communicationType,
        chatType = request.chatType,
        payload = request.payload,
        createdAt = Instant.ofEpochSecond(1).toEpochMilli(),
        updatedAt = null,
        latestMessageDate = null
      ).toLinkedHashMap()
    )

    //When
    val response = client.post("/v1/chat/create") {
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
  fun `success delete chat`() = testConfiguredApplication { client ->
    //Given
    val request = ChatDeleteReq(
      requestType = "DELETE_CHAT",
      id = UUIDValue.uuid1,
      communicationType = "TG",
      debug = CmDebug(
        mode = CmRequestDebugMode.STUB,
        stub = CmRequestDebugStubs.SUCCESS,
      )
    )
    val expectedResponseBody = CSResponse(
      result = ResponseResult.SUCCESS,
      content = ChatResp(
        id = UUIDValue.uuid1,
        externalId = null,
        communicationType = request.communicationType,
        chatType = "simple",
        payload = null,
        createdAt = Instant.ofEpochSecond(1).toEpochMilli(),
        updatedAt = null,
        latestMessageDate = null
      ).toLinkedHashMap()
    )

    //When
    val response = client.post("/v1/chat/delete") {
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
  fun `success search chat`() = testConfiguredApplication { client ->
    //Given
    val request = ChatSearchReq(
      requestType = "SEARCH_CHAT",
      filter = ChatSearchReqAllOfFilter(
        ids = listOf(UUIDValue.uuid1),
        externalIds = null,
        communicationType = "TG",
        chatTypes = listOf("simple"),
        latestMessageDate = null,
      ),
      sort = ChatSearchReqAllOfSort(
        sortField = ChatSearchReqAllOfSort.SortField.LATEST_MESSAGE_DATE,
        order = OrderTypeDto.DESC,
      ),
      limit = 1,
      debug = CmDebug(
        mode = CmRequestDebugMode.STUB,
        stub = CmRequestDebugStubs.SUCCESS,
      )
    )

    val expectedResponseBody = CSResponse(
      result = ResponseResult.SUCCESS,
      content = listOf(
        ChatResp(
          id = UUIDValue.uuid1,
          externalId = null,
          communicationType = "TG",
          chatType = "simple",
          payload = null,
          createdAt = Instant.ofEpochSecond(1).toEpochMilli(),
          updatedAt = null,
          latestMessageDate = null
        )
      )
        .toLinkedHashMap()
    )

    //When
    val response = client.post("/v1/chat/search") {
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
