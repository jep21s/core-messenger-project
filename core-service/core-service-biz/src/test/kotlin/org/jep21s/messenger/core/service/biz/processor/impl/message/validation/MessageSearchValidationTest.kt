package org.jep21s.messenger.core.service.biz.processor.impl.message.validation

import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextCommand
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageSearch
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class MessageSearchValidationTest {
  @ParameterizedTest
  @ValueSource(ints = [0, 51])
  fun `should return failed context on incorrect limit`(incorrectLimit: Int) = runTest {
    //Given
    val context: CSContext<MessageSearch, List<Message>?> = CSContext(
      command = CSContextCommand.SEARCH_MESSAGE,
      state = CSContextState.Running,
      workMode = CSWorkMode.Test,
      modelResp = null,
      modelReq = MessageSearch(
        messageFilter = MessageSearch.MessageFilter(
          ids = null,
          partOfBody = null,
          messageTypes = null,
          sentDate = ComparableFilter(
            direction = ConditionType.EQUAL,
            value = Instant.now(),
          )
        ),
        order = null,
        limit = incorrectLimit,
        chatFilter = MessageSearch.ChatFilter(
          id = UUID.randomUUID(),
          communicationType = "TG",
        ),
      ),
    )
    val expectedError = CSError(
      code = "validation-search",
      group = "validation",
      field = MessageSearch::limit.name,
      message = "incorrect limit value [$incorrectLimit]",
    )

    //When
    val resultContext = runChain(context) {
      validation {
        validLimit()
      }
    }

    //Then
    assertAll(
      {
        assertThat(resultContext.state)
          .isInstanceOf(CSContextState.Failing::class.java)
      },
      {
        val error = (resultContext.state as CSContextState.Failing)
          .errors
          .first()
        assertThat(error)
          .isEqualTo(expectedError)
      }
    )
  }

  @ParameterizedTest
  @ValueSource(ints = [1, 25, 50])
  fun `should return original context on correct limit`(correctLimit: Int) = runTest {
    //Given
    val context: CSContext<MessageSearch, List<Message>?> = CSContext(
      command = CSContextCommand.SEARCH_MESSAGE,
      state = CSContextState.Running,
      workMode = CSWorkMode.Test,
      modelResp = null,
      modelReq = MessageSearch(
        messageFilter = MessageSearch.MessageFilter(
          ids = null,
          partOfBody = null,
          messageTypes = null,
          sentDate = ComparableFilter(
            direction = ConditionType.EQUAL,
            value = Instant.now(),
          )
        ),
        order = null,
        limit = correctLimit,
        chatFilter = MessageSearch.ChatFilter(
          id = UUID.randomUUID(),
          communicationType = "TG",
        ),
      ),
    )

    //When
    val resultContext = runChain(context) {
      validation {
        validLimit()
      }
    }

    //Then
    assertAll(
      {
        assertThat(resultContext)
          .isEqualTo(context)
      },
    )
  }
}