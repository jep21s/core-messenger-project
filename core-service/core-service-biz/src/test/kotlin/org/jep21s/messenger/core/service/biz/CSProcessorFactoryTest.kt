package org.jep21s.messenger.core.service.biz

import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.jep21s.messenger.core.service.biz.processor.impl.chat.CSChatSearchProcessor
import org.jep21s.messenger.core.service.biz.processor.CSProcessorFactory
import org.jep21s.messenger.core.service.biz.processor.getCSProcessor
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSContextCommand
import org.jep21s.messenger.core.service.common.context.CSContextState
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.junit.jupiter.api.assertAll

class CSProcessorFactoryTest {

  @Test
  fun `got expected processor from factory`() = runBlocking {
    //Given
    val unixTime: Long = Instant.now().toEpochMilli()
    val expectedInstant = Instant.ofEpochMilli(unixTime)
    val chatIds: List<UUID> = listOf(UUID.randomUUID())
    val chatSearchContext = CSContext<ChatSearch, List<Chat>?>(
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
      modelResp = null
    )

    //When
    val processor = CSProcessorFactory.getCSProcessor(chatSearchContext)

    //Then
    assertAll(
      {
        assertTrue("Got expected processor [${CSChatSearchProcessor::class.simpleName}]") {
          processor::class == CSChatSearchProcessor::class
        }
      },
    )

  }


}