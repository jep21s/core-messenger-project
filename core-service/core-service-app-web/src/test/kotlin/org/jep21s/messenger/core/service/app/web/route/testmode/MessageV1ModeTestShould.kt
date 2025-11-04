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
import org.jep21s.messenger.core.service.api.v1.models.CSResponse
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteRespAllOfContent
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReq
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReqAllOfChatFilter
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReqAllOfMessageFilter
import org.jep21s.messenger.core.service.api.v1.models.OrderTypeDto
import org.jep21s.messenger.core.service.api.v1.models.ResponseResult
import org.jep21s.messenger.core.lib.test.common.extention.toLinkedHashMap
import org.jep21s.messenger.core.service.api.v1.models.CmDebug
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugMode
import org.jep21s.messenger.core.service.app.web.test.util.testConfiguredApplication
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.junit.jupiter.api.assertAll

class MessageV1ModeTestShould {

  @Test
  fun `success delete message`() = testConfiguredApplication { client ->
    //Given
    val uuid = UUIDValue.uuid1
    val communicationType = "TG"
    val instant = Instant.ofEpochSecond(1)

    mockkStatic(Instant::class, UUID::class) {
      every { Instant.now() } returns instant
      every { UUID.randomUUID() } returns uuid

      // Создаем чат и сообщение для удаления
      val chatRepo = CSCorSettings.chatRepo(CSWorkMode.Test)
      val messageRepo = CSCorSettings.messageRepo(CSWorkMode.Test)

      val chat = chatRepo.save(
        ChatCreation(
          externalId = null,
          communicationType = communicationType,
          chatType = "simple",
          payload = null
        )
      )

      val message = messageRepo.save(
        MessageCreation(
          id = null,
          chatId = chat.id,
          communicationType = communicationType,
          messageType = "simple",
          sentDate = Instant.ofEpochSecond(1),
          body = "test message",
          externalId = null,
          payload = null,
        )
      )

      val ids = listOf(message.id)
      val request = MessageDeleteReq(
        requestType = "DELETE_MESSAGE",
        ids = ids,
        chatId = chat.id,
        communicationType = communicationType,
        debug = CmDebug(
          mode = CmRequestDebugMode.TEST,
        )
      )

      val expectedResponseBody = CSResponse(
        result = ResponseResult.SUCCESS,
        content = MessageDeleteRespAllOfContent(
          ids = ids,
          chatId = chat.id,
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
  }

  @Test
  fun `success search message`() = testConfiguredApplication { client ->
    //Given
    val communicationType = "TG"
    val instant = Instant.ofEpochSecond(1)
    val uuid = UUIDValue.uuid1

    mockkStatic(Instant::class, UUID::class) {
      every { Instant.now() } returns instant
      every { UUID.randomUUID() } returns uuid

      // Создаем чат и сообщение для поиска
      val chatRepo = CSCorSettings.chatRepo(CSWorkMode.Test)
      val messageRepo = CSCorSettings.messageRepo(CSWorkMode.Test)

      val chat = chatRepo.save(
        ChatCreation(
          externalId = null,
          communicationType = communicationType,
          chatType = "simple",
          payload = null
        )
      )

      val message = messageRepo.save(
        MessageCreation(
          id = null,
          chatId = chat.id,
          communicationType = communicationType,
          messageType = "simple",
          sentDate = Instant.ofEpochSecond(1),
          body = "test message body",
          externalId = null,
          payload = null
        )
      )

      val request = MessageSearchReq(
        requestType = "SEARCH_MESSAGE",
        chatFilter = MessageSearchReqAllOfChatFilter(
          id = chat.id,
          communicationType = communicationType
        ),
        messageFilter = MessageSearchReqAllOfMessageFilter(
          ids = listOf(message.id),
          messageTypes = listOf("simple"),
        ),
        order = OrderTypeDto.DESC,
        limit = 10,
        debug = CmDebug(
          mode = CmRequestDebugMode.TEST,
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
          assertThat(resultBody.result)
            .describedAs("got success result")
            .isEqualTo(ResponseResult.SUCCESS)
        },
        {
          assertThat(resultBody.content)
            .describedAs("content is not empty")
            .isNotNull
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