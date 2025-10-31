package org.jep21s.messenger.core.service.repo.inmemory.chat.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konvert
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.Mapping
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.jep21s.messenger.core.service.repo.inmemory.chat.entity.ChatEntity

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface ChatEntityMapper {
  @Konvert(
    mappings = [
      Mapping(target = "id", ignore = true),
      Mapping(target = "createdAt", ignore = true),
    ]
  )
  fun mapToEntity(chatCreation: ChatCreation): ChatEntity

  fun mapToModel(chatEntity: ChatEntity): Chat
}