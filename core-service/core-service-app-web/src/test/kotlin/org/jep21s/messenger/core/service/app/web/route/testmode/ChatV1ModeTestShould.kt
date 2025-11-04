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
import org.jep21s.messenger.core.service.api.v1.models.ChatCreateReq
import org.jep21s.messenger.core.service.api.v1.models.ChatResp
import org.jep21s.messenger.core.service.api.v1.models.CmDebug
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugMode
import org.jep21s.messenger.core.service.api.v1.models.ResponseResult
import org.jep21s.messenger.core.service.app.web.test.util.testConfiguredApplication
import org.junit.jupiter.api.assertAll

class ChatV1ModeTestShould {
  @Test
  fun `success creation chat`() = testConfiguredApplication { client ->
    //Given
    val request = ChatCreateReq(
      requestType = "CREATE_CHAT",
      communicationType = "TG",
      chatType = "simple",
      debug = CmDebug(
        mode = CmRequestDebugMode.TEST,
      )
    )

    val createdAt: Instant = Instant.ofEpochSecond(1)
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
    mockkStatic(Instant::class, UUID::class) {
      every { Instant.now() } returns createdAt
      every { UUID.randomUUID() } returns UUIDValue.uuid1

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
  }

}