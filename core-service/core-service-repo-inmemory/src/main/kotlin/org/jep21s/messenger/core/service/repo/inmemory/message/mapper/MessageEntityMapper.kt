package org.jep21s.messenger.core.service.repo.inmemory.message.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konvert
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.Mapping
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.repo.inmemory.message.entity.MessageEntity

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface MessageEntityMapper {
  fun mapToModel(messageEntity: MessageEntity): Message

  @Konvert(
    mappings = [
      Mapping(target = "id", ignore = true),
    ]
  )
  fun mapToEntity(messageCreation: MessageCreation): MessageEntity

  fun mapToEntity(message: Message): MessageEntity
}