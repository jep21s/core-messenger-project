package org.jep21s.messenger.core.service.repo.inmemory.message

import java.util.UUID
import kotlin.collections.component1
import kotlin.collections.component2
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.message.MessageSearch
import org.jep21s.messenger.core.service.repo.inmemory.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.inmemory.extention.doFilterIfNotNull

fun Sequence<Map.Entry<UUID, MessageEntity>>.filterByChatId(
  messageSearch: MessageSearch,
): Sequence<Map.Entry<UUID, MessageEntity>> =
  doFilterIfNotNull(messageSearch.chatFilter.id) { chatId ->
    { (_, message) -> chatId == message.chatId }
  }

fun Sequence<Map.Entry<UUID, MessageEntity>>.filterByCommunicationType(
  messageSearch: MessageSearch,
): Sequence<Map.Entry<UUID, MessageEntity>> =
  filter { (_, message) -> message.communicationType == messageSearch.chatFilter.communicationType }

fun Sequence<Map.Entry<UUID, MessageEntity>>.filterByMessageIds(
  messageSearch: MessageSearch,
): Sequence<Map.Entry<UUID, MessageEntity>> =
  doFilterIfNotNull(messageSearch.messageFilter.ids) { ids ->
    { (_, message) -> ids.contains(message.id) }
  }

fun Sequence<Map.Entry<UUID, MessageEntity>>.filterByMessageTypes(
  messageSearch: MessageSearch,
): Sequence<Map.Entry<UUID, MessageEntity>> =
  doFilterIfNotNull(messageSearch.messageFilter.messageTypes) { messageTypes ->
    { (_, message) -> messageTypes.contains(message.messageType) }
  }

fun Sequence<Map.Entry<UUID, MessageEntity>>.filterByPartOfBody(
  messageSearch: MessageSearch,
): Sequence<Map.Entry<UUID, MessageEntity>> =
  doFilterIfNotNull(messageSearch.messageFilter.partOfBody) { searchText ->
    { (_, message) ->
      message.body?.contains(searchText, ignoreCase = true) == true
    }
  }

fun Sequence<Map.Entry<UUID, MessageEntity>>.filterBySentDate(
  messageSearch: MessageSearch,
): Sequence<Map.Entry<UUID, MessageEntity>> =
  doFilterIfNotNull(messageSearch.messageFilter.sentDate) { comparableFilter ->
    { (_, message) ->
      when (comparableFilter.direction) {
        ConditionType.LESS -> message.sentDate < comparableFilter.value
        ConditionType.GREATER -> message.sentDate > comparableFilter.value
        ConditionType.EQUAL -> message.sentDate == comparableFilter.value
      }
    }
  }

fun Sequence<Map.Entry<UUID, MessageEntity>>.sort(
  messageSearch: MessageSearch,
): Sequence<Map.Entry<UUID, MessageEntity>> = let { seq ->
  if (messageSearch.order == null) return@let seq

  seq.sortedWith(Comparator { o1, o2 ->
    when (messageSearch.order!!) {
      OrderType.ASC -> o1.value.sentDate.compareTo(o2.value.sentDate)
      OrderType.DESC -> o2.value.sentDate.compareTo(o1.value.sentDate)
    }
  })
}