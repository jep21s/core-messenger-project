package org.jep21s.messenger.core.service.api.v1.mapper

import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReq
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfFilter
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfSort
import org.jep21s.messenger.core.service.api.v1.models.CmDebug
import org.jep21s.messenger.core.service.api.v1.models.CmRequestDebugMode
import org.jep21s.messenger.core.service.api.v1.models.ComparableFilterReq
import org.jep21s.messenger.core.service.api.v1.models.ConditionTypeDto
import org.jep21s.messenger.core.service.api.v1.models.OrderTypeDto
import org.junit.jupiter.api.Assertions.*

class ChatMapperTest {
  private val chatMapper: ChatMapper = ChatMapperImpl

  @Test
  fun `throw on error by map incorrect ChatSearchReq to Context`() {
    val incorrectCommunicationType = null
    val request = ChatSearchReq(
      requestType = "SEARCH_CHAT",
      debug = CmDebug(
        mode = CmRequestDebugMode.PROD,
        stub = null,
      ),
      filter = ChatSearchReqAllOfFilter(
        ids = listOf(UUID.randomUUID()),
        externalIds = null,
        communicationType = incorrectCommunicationType,
        chatTypes = listOf("SIMPLE_MESSAGE", "IMAGE"),
        latestMessageDate = ComparableFilterReq(
          value = Instant.now().toEpochMilli(),
          direction = ConditionTypeDto.GREATER,
        )
      ),
      sort = ChatSearchReqAllOfSort(
        sortField = ChatSearchReqAllOfSort.SortField.LATEST_MESSAGE_DATE,
        order = OrderTypeDto.DESC
      ),
      limit = 10,
    )

    //When
    //Then
    assertThrows(Exception::class.java) {
      chatMapper.mapToModel(request)
    }
  }
}