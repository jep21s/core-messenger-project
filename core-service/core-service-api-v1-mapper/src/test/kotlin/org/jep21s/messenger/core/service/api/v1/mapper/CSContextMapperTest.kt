package org.jep21s.messenger.core.service.api.v1.mapper

import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReq
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfFilter
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfSort
import org.jep21s.messenger.core.service.api.v1.models.CmDebug
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugMode
import org.jep21s.messenger.core.service.api.v1.models.ComparableFilterReq
import org.jep21s.messenger.core.service.api.v1.models.ConditionTypeDto
import org.jep21s.messenger.core.service.api.v1.models.OrderTypeDto
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextCommand
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.junit.jupiter.api.Assertions.*

class CSContextMapperTest {
  private val csContextMapper: CSContextMapper = CSContextMapperImpl
  private val chatMapper: ChatMapper = ChatMapperImpl

  @Test
  fun `success map ChatSearchReq to Context`() {
    val unixTime: Long = Instant.now().toEpochMilli()
    val expectedInstant = Instant.ofEpochMilli(unixTime)
    val chatIds: List<UUID> = listOf(UUID.randomUUID())
    val request = ChatSearchReq(
      requestType = "SEARCH_CHAT",
      debug = CmDebug(
        mode = CmRequestDebugMode.PROD,
        stub = null,
      ),
      filter = ChatSearchReqAllOfFilter(
        ids = chatIds,
        externalIds = null,
        communicationType = "TELEGRAM",
        chatTypes = listOf("SIMPLE_MESSAGE", "IMAGE"),
        latestMessageDate = ComparableFilterReq(
          value = unixTime,
          direction = ConditionTypeDto.GREATER,
        )
      ),
      sort = ChatSearchReqAllOfSort(
        sortField = ChatSearchReqAllOfSort.SortField.LATEST_MESSAGE_DATE,
        order = OrderTypeDto.DESC
      ),
      limit = 10,
    )
    val timeStart = Instant.now()
    val expectedContext = CSContext<ChatSearch, List<Chat>?>(
      command = CSContextCommand.SEARCH_CHAT,
      state = CSContextState.None,
      workMode = CSWorkMode.Prod,
      modelReq = ChatSearch(
        filter = ChatSearch.ChatSearchFilter(
          ids = chatIds,
          externalIds = null,
          communicationType = "TELEGRAM",
          chatTypes = listOf("SIMPLE_MESSAGE", "IMAGE"),
          latestMessageDate = ComparableFilter(
            value = expectedInstant,
            direction = ConditionType.GREATER,
          )
        ),
        sort = ChatSearch.ChatSearchSort(
          sortField = ChatSearch.ChatSearchSort.SortField.LATEST_MESSAGE_DATE,
          order = OrderType.DESC
        ),
        limit = 10
      ),
      modelResp = null,
      timeStart = timeStart,
    )

    //When
    val model: ChatSearch = chatMapper.mapToModel(request)
    val context: CSContext<ChatSearch, List<Chat>?> = csContextMapper.mapToContext(
      request = request,
      modelReq = model,
      timeStart = timeStart,
    )

    //Then
    assertAll(
      { assertThat(context).isEqualTo(expectedContext) }
    )
  }
}