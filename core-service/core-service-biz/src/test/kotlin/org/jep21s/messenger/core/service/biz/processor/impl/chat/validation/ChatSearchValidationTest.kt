package org.jep21s.messenger.core.service.biz.processor.impl.chat.validation

import kotlin.intArrayOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.biz.cor.runChain
import org.jep21s.messenger.core.service.biz.cor.validation
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextCommand
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ChatSearchValidationTest {
  @ParameterizedTest
  @ValueSource(ints = [0, 51])
  fun `should return failed context on incorrect limit`(incorrectLimit: Int) = runTest {
    //Given
    val context: CSContext<ChatSearch, List<Chat>?> = CSContext(
      command = CSContextCommand.SEARCH_MESSAGE,
      state = CSContextState.Running,
      workMode = CSWorkMode.Test,
      modelResp = null,
      modelReq = ChatSearch(
        sort = null,
        limit = incorrectLimit,
        filter = ChatSearch.ChatSearchFilter(
          ids = null,
          externalIds = null,
          communicationType = "TG",
          chatTypes = null,
          latestMessageDate = null
        ),
      )
    )
    val expectedError = CSError(
      code = "validation-search",
      group = "validation",
      field = ChatSearch::limit.name,
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
  fun `should return failed context on correct limit`(correctLimit: Int) = runTest {
    //Given
    val context: CSContext<ChatSearch, List<Chat>?> = CSContext(
      command = CSContextCommand.SEARCH_MESSAGE,
      state = CSContextState.Running,
      workMode = CSWorkMode.Test,
      modelResp = null,
      modelReq = ChatSearch(
        sort = null,
        limit = correctLimit,
        filter = ChatSearch.ChatSearchFilter(
          ids = null,
          externalIds = null,
          communicationType = "TG",
          chatTypes = null,
          latestMessageDate = null
        ),
      )
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