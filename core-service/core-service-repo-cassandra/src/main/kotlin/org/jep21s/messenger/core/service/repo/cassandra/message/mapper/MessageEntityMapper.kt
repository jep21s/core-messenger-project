package org.jep21s.messenger.core.service.repo.cassandra.message.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konvert
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.Mapping
import java.util.UUID
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.MessageSearch
import org.jep21s.messenger.core.service.repo.cassandra.message.entity.MessageEntity
import org.jep21s.messenger.core.service.repo.cassandra.message.filter.MessageEntityFilter


@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface MessageEntityMapper {
  fun mapToModel(messageEntity: MessageEntity): Message

  fun mapToEntity(messageCreation: MessageCreation): MessageEntity

  fun mapToMessageEntityFilter(
    messageSearch: MessageSearch,
    messageId: UUID?,
  ) = MessageEntityFilter(
    chatId = messageSearch.chatFilter.id,
    sentDate = messageSearch.messageFilter.sentDate,
    id = messageId,
    order = messageSearch.order,
    limit = messageSearch.limit
  )
}