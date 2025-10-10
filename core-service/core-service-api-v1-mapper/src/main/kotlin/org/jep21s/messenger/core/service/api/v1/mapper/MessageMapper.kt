package org.jep21s.messenger.core.service.api.v1.mapper

import arrow.core.Either
import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import java.time.Instant
import org.jep21s.messenger.core.service.api.v1.mapper.helper.MappingNullError
import org.jep21s.messenger.core.service.api.v1.mapper.helper.buildEitherResult
import org.jep21s.messenger.core.service.api.v1.mapper.helper.getNotNull
import org.jep21s.messenger.core.service.api.v1.mapper.helper.getOrThrow
import org.jep21s.messenger.core.service.api.v1.models.MessageCreateReq
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.MessageDeleteRespAllOfContent
import org.jep21s.messenger.core.service.api.v1.models.MessageResp
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReq
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReqAllOfChatFilter
import org.jep21s.messenger.core.service.api.v1.models.MessageSearchReqAllOfMessageFilter
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion
import org.jep21s.messenger.core.service.common.model.message.MessageSearch

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail"),
  ]
)
interface MessageMapper {

  fun mapToResponse(model: Message): MessageResp

  fun mapToDeleteResponse(model: MessageDeleted): MessageDeleteRespAllOfContent

  fun mapToModel(
    request: MessageCreateReq?,
    fieldName: String = "",
  ): Either<MappingNullError, MessageCreation> {
    val chatIdKP = MessageCreateReq::chatId
    val messageTypeKP = MessageCreateReq::messageType
    val sentDateKP = MessageCreateReq::sentDate

    return buildEitherResult(
      request, fieldName,
      chatIdKP, messageTypeKP, sentDateKP
    ) { nonNullRequest: MessageCreateReq ->
      modelReq {
        MessageCreation(
          id = nonNullRequest.id,
          chatId = chatIdKP.getNotNull(nonNullRequest),
          messageType = messageTypeKP.getNotNull(nonNullRequest),
          sentDate = Instant.ofEpochMilli(sentDateKP.getNotNull(nonNullRequest)),
          body = nonNullRequest.body,
          externalId = nonNullRequest.externalId,
          payload = nonNullRequest.payload
        )
      }
    }
  }

  fun mapToModel(
    request: MessageDeleteReq?,
    fieldName: String = "",
  ): Either<MappingNullError, MessageDeletion> {
    val idsKP = MessageDeleteReq::ids
    val communicationTypeKP = MessageDeleteReq::communicationType

    return buildEitherResult(
      request, fieldName,
      idsKP, communicationTypeKP,
    ) { nonNullRequest: MessageDeleteReq ->
      modelReq {
        MessageDeletion(
          ids = idsKP.getNotNull(nonNullRequest),
          communicationType = communicationTypeKP.getNotNull(nonNullRequest),
        )
      }
    }
  }

  fun mapToModel(
    request: MessageSearchReq?,
    fieldName: String = "",
  ): Either<MappingNullError, MessageSearch> {
    val chatFilterKP = MessageSearchReq::chatFilter

    return buildEitherResult(request, fieldName, chatFilterKP)
    { nonNullRequest: MessageSearchReq ->
      val messageFilterKP = MessageSearchReq::messageFilter
      val chatFilterEither =
        mapToModel(chatFilterKP.get(nonNullRequest), chatFilterKP.name)
      val messageFilterEither = nonNullRequest.messageFilter
        ?.let { mapToModel(messageFilterKP.get(nonNullRequest), messageFilterKP.name) }

      checkInnerFields { listOf(chatFilterEither, messageFilterEither) }
      modelReq {
        MessageSearch(
          chatFilter = chatFilterEither.getOrThrow(),
          messageFilter = messageFilterEither?.getOrThrow(),
          order = nonNullRequest.order?.name?.let { OrderType.valueOf(it) },
          limit = nonNullRequest.limit
        )
      }
    }
  }

  fun mapToModel(
    request: MessageSearchReqAllOfChatFilter?,
    fieldName: String = "",
  ): Either<MappingNullError, MessageSearch.ChatFilter> {
    val communicationTypeKP = MessageSearchReqAllOfChatFilter::communicationType

    return buildEitherResult(request, fieldName, communicationTypeKP)
    { nonNullRequest: MessageSearchReqAllOfChatFilter ->
      modelReq {
        MessageSearch.ChatFilter(
          id = nonNullRequest.id,
          communicationType = communicationTypeKP.getNotNull(nonNullRequest),
        )
      }

    }
  }

  fun mapToModel(
    request: MessageSearchReqAllOfMessageFilter?,
    fieldName: String = "",
  ): Either<MappingNullError, MessageSearch.MessageFilter> = buildEitherResult(request, fieldName)
  { nonNullRequest: MessageSearchReqAllOfMessageFilter ->
    modelReq {
      MessageSearch.MessageFilter(
        ids = nonNullRequest.ids,
        messageTypes = nonNullRequest.messageTypes,
        partOfBody = nonNullRequest.partOfBody,
        sentDate = nonNullRequest.sentDate
          ?.let { ComparableFilterMapperImpl.mapToModel(it) }
      )

    }
  }
}
