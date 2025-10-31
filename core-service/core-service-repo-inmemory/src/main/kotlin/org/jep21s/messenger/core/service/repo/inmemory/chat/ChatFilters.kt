package org.jep21s.messenger.core.service.repo.inmemory.chat

import java.util.UUID
import kotlin.collections.component1
import kotlin.collections.component2
import org.jep21s.messenger.core.service.common.model.ConditionType
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity
import org.jep21s.messenger.core.service.repo.inmemory.extention.doFilterIfNotNull

fun Sequence<Map.Entry<UUID, ChatEntity>>.filterByIds(
  chatSearch: ChatSearch,
): Sequence<Map.Entry<UUID, ChatEntity>> =
  doFilterIfNotNull(chatSearch.filter.ids) { ids ->
    { (_, chat) -> ids.contains(chat.id) }
  }

fun Sequence<Map.Entry<UUID, ChatEntity>>.filterByExternalIds(
  chatSearch: ChatSearch,
): Sequence<Map.Entry<UUID, ChatEntity>> =
  doFilterIfNotNull(chatSearch.filter.externalIds) { ids ->
    { (_, chat) -> ids.contains(chat.externalId) }
  }

fun Sequence<Map.Entry<UUID, ChatEntity>>.filterByLatestMessageDate(
  chatSearch: ChatSearch,
): Sequence<Map.Entry<UUID, ChatEntity>> =
  doFilterIfNotNull(chatSearch.filter.latestMessageDate) { comparableFilter ->
    { (_, chat) ->
      when (comparableFilter.direction) {
        ConditionType.LESS ->
          comparableFilter.value > chat.latestMessageDate

        ConditionType.GREATER ->
          comparableFilter.value < chat.latestMessageDate
      }
    }
  }

fun Sequence<Map.Entry<UUID, ChatEntity>>.filterByChatTypes(
  chatSearch: ChatSearch,
): Sequence<Map.Entry<UUID, ChatEntity>> =
  doFilterIfNotNull(chatSearch.filter.chatTypes) { chatTypes ->
    { (_, chat) -> chatTypes.contains(chat.chatType) }
  }

fun Sequence<Map.Entry<UUID, ChatEntity>>.filterByCommunicationType(
  chatSearch: ChatSearch,
): Sequence<Map.Entry<UUID, ChatEntity>> =
  filter { (_, chat) -> chat.communicationType == chatSearch.filter.communicationType }

fun Sequence<Map.Entry<UUID, ChatEntity>>.sort(
  chatSearch: ChatSearch,
): Sequence<Map.Entry<UUID, ChatEntity>> = let { seq ->
  if (chatSearch.sort == null) return@let seq
  seq.sortedWith(Comparator { o1, o2 ->
    when (chatSearch.sort!!.sortField) {
      ChatSearch.ChatSearchSort.SortField.LATEST_MESSAGE_DATE ->
        when (chatSearch.sort!!.order) {
          OrderType.ASC -> {
            o1.value.latestMessageDate?.compareTo(o2.value.latestMessageDate)
          }

          OrderType.DESC -> o2.value.latestMessageDate?.compareTo(o1.value.latestMessageDate)
        }
    } ?: 0
  }
  )
}