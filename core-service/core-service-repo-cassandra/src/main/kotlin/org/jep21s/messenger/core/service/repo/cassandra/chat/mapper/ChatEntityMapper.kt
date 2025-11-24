package org.jep21s.messenger.core.service.repo.cassandra.chat.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import java.time.Instant
import java.util.UUID
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.jep21s.messenger.core.service.repo.cassandra.chat.ChatActivityBucketCalculator
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatActivityEntity
import org.jep21s.messenger.core.service.repo.cassandra.chat.entity.ChatEntity

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface ChatEntityMapper {
  fun mapToEntity(chatCreation: ChatCreation) = ChatEntity(
    id = UUID.randomUUID(),
    communicationType = chatCreation.communicationType,
    chatType = chatCreation.chatType,
    externalId = chatCreation.externalId,
    payload = chatCreation.payload,
    createdAt = Instant.now(),
    updatedAt = null,
  )

  fun mapToActivityEntity(
    chatCreation: ChatCreation,
    chatEntity: ChatEntity,
  ) = ChatActivityEntity(
    bucketDay = ChatActivityBucketCalculator
      .calculateBucketDay(chatEntity.createdAt)
      .toString(),
    communicationType = chatCreation.communicationType,
    latestActivity = chatEntity.createdAt,
    chatId = chatEntity.id
  )

  fun mapToEntity(chat: Chat): ChatEntity

  fun mapToModel(
    @Konverter.Source entity: ChatEntity,
    latestMessageDate: Instant?,
  ): Chat
}