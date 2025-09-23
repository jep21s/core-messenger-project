package org.jep21s.messenger.core.service.api.v1

import com.fasterxml.jackson.databind.JsonNode
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.api.v1.models.ChatResp
import org.jep21s.messenger.core.service.api.v1.test.util.Given
import org.jep21s.messenger.core.service.api.v1.test.util.Then
import org.jep21s.messenger.core.service.api.v1.test.util.When
import org.jep21s.messenger.core.service.api.v1.test.util.readResource
import org.jep21s.messenger.core.service.api.v1.test.util.runDynamicTest
import org.junit.jupiter.api.TestFactory

class ApiV1MapperTest {
  @TestFactory
  fun `ApiV1Mapper test factory`() = runDynamicTest {
    val chatResp = ChatResp(
      id = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
      externalId = "ext-chat-12345",
      communicationType = "WHATSAPP",
      chatType = "GROUP",
      payload = mapOf(
        "participants" to listOf("user1", "user2", "user3"),
        "topic" to "Project Discussion",
        "isArchived" to false,
        "metadata" to mapOf(
          "priority" to "high",
          "tags" to listOf("urgent", "important")
        )
      ),
      createdAt = 1672531200000L,
      updatedAt = 1672617600000L,
      latestMessageDate = 1672617600000L,
    )
    val chatRespJsonNode: JsonNode = readResource("json/chat_resp_json.json")

    Given("Success deserialize ${ChatResp::class.simpleName}") {
      val chatRespJson: String = chatRespJsonNode.toPrettyString()
      When("apiV1Mapper start deserialize") {
        val result: ChatResp = ApiV1Mapper.deserialize(chatRespJson)
        Then("got expected result") {
          assertThat(result).isEqualTo(chatResp)
        }
      }
    }

    Given("Success serialize ${ChatResp::class.simpleName}") {
      When("apiV1Mapper start serialize") {
        val result: String = ApiV1Mapper.serialize(chatResp)
        Then("got expected result") {
          val resultJsonNode: JsonNode = ApiV1Mapper.jacksonMapper.readTree(result)
          assertThat(resultJsonNode).isEqualTo(chatRespJsonNode)
        }
      }
    }
  }
}
