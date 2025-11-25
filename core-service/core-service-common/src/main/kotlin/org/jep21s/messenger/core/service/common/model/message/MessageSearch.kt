package org.jep21s.messenger.core.service.common.model.message

import java.util.UUID
import kotlin.properties.Delegates
import org.jep21s.messenger.core.service.common.model.ComparableFilter
import org.jep21s.messenger.core.service.common.model.OrderType

data class MessageSearch(
  val chatFilter: ChatFilter,
  val messageFilter: MessageFilter,
  val order: OrderType?,
  val limit: Int?,
) {
  data class ChatFilter(
    val id: UUID,
    val communicationType: String,
  )

  data class MessageFilter(
    val ids: List<UUID>?,
    val messageTypes: List<String>?,
    val partOfBody: String?,
    val sentDate: ComparableFilter,
  )
}

@DslMarker
private annotation class MessageSearchDslMarker

fun messageSearch(block: MessageSearchDsl.() -> Unit): MessageSearch {
  val dsl = MessageSearchDsl().apply(block)
  return dsl.build()
}

@MessageSearchDslMarker
class MessageSearchDsl {
  private lateinit var _chatFilter: MessageSearch.ChatFilter
  private lateinit var _messageFilter: MessageSearch.MessageFilter
  private var _order: OrderType? = null
  var limit: Int? = null

  fun chatFilter(block: ChatFilterDsl.() -> Unit) {
    val dsl = ChatFilterDsl()
    dsl.block()
    _chatFilter = dsl.build()
  }

  fun messageFilter(block: MessageFilterDsl.() -> Unit) {
    val dsl = MessageFilterDsl()
    dsl.block()
    _messageFilter = dsl.build()
  }

  fun order(block: () -> OrderType?) {
    _order = block()
  }

  fun build(): MessageSearch = MessageSearch(
    chatFilter = this._chatFilter,
    messageFilter = this._messageFilter,
    order = this._order,
    limit = this.limit
  )
}

@MessageSearchDslMarker
class ChatFilterDsl {
  private lateinit var _id: UUID
  private var _communicationType: String by Delegates.notNull()

  fun id(block: () -> UUID) {
    _id = block()
  }

  fun communicationType(block: () -> String) {
    _communicationType = block()
  }

  fun build() = MessageSearch.ChatFilter(
    id = _id,
    communicationType = _communicationType
  )
}

@MessageSearchDslMarker
class MessageFilterDsl {
  private var _ids: List<UUID>? = null
  private var _messageTypes: List<String>? = null
  private var _partOfBody: String? = null
  private lateinit var _sentDate: ComparableFilter

  fun ids(block: () -> List<UUID>?) {
    _ids = block()
  }

  fun id(block: () -> UUID?) {
    val id: UUID = block() ?: return
    ids { listOf(id) }
  }

  fun messageTypes(block: () -> List<String>?) {
    _messageTypes = block()
  }

  fun messageType(block: () -> String?) {
    val messageType: String = block() ?: return
    messageTypes { listOf(messageType) }
  }

  fun partOfBody(block: () -> String?) {
    _partOfBody = block()
  }

  fun sentDate(block: () -> ComparableFilter) {
    _sentDate = block()
  }

  fun build() = MessageSearch.MessageFilter(
    ids = _ids,
    messageTypes = _messageTypes,
    partOfBody = _partOfBody,
    sentDate = _sentDate
  )
}