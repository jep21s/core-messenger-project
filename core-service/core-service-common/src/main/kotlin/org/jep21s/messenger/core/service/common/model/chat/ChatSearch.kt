package org.jep21s.messenger.core.service.common.model.chat

import java.util.UUID
import kotlin.properties.Delegates
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch.ChatSearchFilter
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch.ChatSearchSort
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch.ChatSearchSort.SortField

data class ChatSearch(
  val filter: ChatSearchFilter,
  val sort: ChatSearchSort?,
  val limit: Int?,
) {
  data class ChatSearchFilter(
    val ids: List<UUID>?,
    val externalIds: List<String>?,
    val communicationType: String,
    val chatTypes: List<String>?,
    val latestMessageDate: ComparableFilter?,
  )

  data class ChatSearchSort(
    val sortField: SortField,
    val order: OrderType,
  ) {
    enum class SortField {
      LATEST_MESSAGE_DATE,
      ;
    }
  }
}

@DslMarker
private annotation class ChatSearchDslMarker

fun chatSearch(block: ChatSearchDsl.() -> Unit): ChatSearch {
  val dsl = ChatSearchDsl().apply(block)
  return dsl.build()
}

@ChatSearchDslMarker
class ChatSearchDsl {
  private lateinit var _filter: ChatSearchFilter
  private var _sort: ChatSearchSort? = null
  var limit: Int? = null

  fun filter(block: ChatSearchFilterDsl.() -> Unit) {
    val dsl = ChatSearchFilterDsl()
    dsl.block()
    _filter = dsl.build()
  }

  fun sort(block: ChatSearchSortDsl.() -> ChatSearchSort?) {
    val dsl = ChatSearchSortDsl()
    dsl.block()
    _sort = dsl.build()
  }

  fun build(): ChatSearch = ChatSearch(
    filter = this._filter,
    sort = this._sort,
    limit = this.limit
  )
}

@ChatSearchDslMarker
class ChatSearchFilterDsl {
  private var _ids: List<UUID>? = null
  private var _externalIds: List<String>? = null
  private var _communicationType: String by Delegates.notNull()
  private var _chatTypes: List<String>? = null
  private var _latestMessageDate: ComparableFilter? = null

  fun ids(block: () -> List<UUID>?) {
    _ids = block()
  }

  fun id(block: () -> UUID?) {
    val id: UUID = block() ?: return
    ids { listOf(id) }
  }

  fun externalIds(block: () -> List<String>?) {
    _externalIds = block()
  }

  fun communicationType(block: () -> String) {
    _communicationType = block()
  }

  fun chatTypes(block: () -> List<String>?) {
    _chatTypes = block()
  }

  fun latestMessageDate(block: () -> ComparableFilter?) {
    _latestMessageDate = block()
  }

  fun build() = ChatSearchFilter(
    ids = _ids,
    externalIds = _externalIds,
    communicationType = _communicationType,
    chatTypes = _chatTypes,
    latestMessageDate = _latestMessageDate
  )
}

@ChatSearchDslMarker
class ChatSearchSortDsl {
  private var sortField: SortField by Delegates.notNull()
  private var order: OrderType by Delegates.notNull()

  fun build() = ChatSearchSort(
    sortField = sortField,
    order = order,
  )
}