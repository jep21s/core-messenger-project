package org.jep21s.messenger.core.service.api.v1.mapper

import arrow.core.Either
import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import org.jep21s.messenger.core.service.api.v1.mapper.helper.buildEitherResult
import org.jep21s.messenger.core.service.api.v1.mapper.helper.getNotNull
import org.jep21s.messenger.core.service.api.v1.mapper.helper.getOrThrow
import org.jep21s.messenger.core.service.api.v1.mapper.helper.MappingNullError
import org.jep21s.messenger.core.service.api.v1.models.ChatCreateReq
import org.jep21s.messenger.core.service.api.v1.models.ChatDeleteReq
import org.jep21s.messenger.core.service.api.v1.models.ChatDeleteRespAllOfContent
import org.jep21s.messenger.core.service.api.v1.models.ChatResp
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReq
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfFilter
import org.jep21s.messenger.core.service.api.v1.models.ChatSearchReqAllOfSort
import org.jep21s.messenger.core.service.common.model.OrderType
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.jep21s.messenger.core.service.common.model.chat.ChatDeleted
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface ChatMapper {

  fun mapToResponse(chat: Chat): ChatResp

  fun mapToDeleteContentResponse(model: ChatDeleted): ChatDeleteRespAllOfContent

  fun mapToModel(
    request: ChatCreateReq?,
    fieldName: String = "",
  ): Either<MappingNullError, ChatCreation> {
    val communicationTypeKP = ChatCreateReq::communicationType
    val chatTypeKP = ChatCreateReq::chatType

    return buildEitherResult(request, fieldName, communicationTypeKP, chatTypeKP)
    { nonNullRequest: ChatCreateReq ->
      modelReq {
        ChatCreation(
          externalId = nonNullRequest.externalId,
          communicationType = communicationTypeKP.getNotNull(nonNullRequest),
          chatType = chatTypeKP.getNotNull(nonNullRequest),
          payload = nonNullRequest.payload
        )
      }
    }
  }

  fun mapToModel(
    request: ChatDeleteReq?,
    fieldName: String = "",
  ): Either<MappingNullError, ChatDeletion> {
    val idKP = ChatDeleteReq::id
    val communicationTypeKP = ChatDeleteReq::communicationType

    return buildEitherResult(request, fieldName, idKP, communicationTypeKP)
    { nonNullRequest: ChatDeleteReq ->
      modelReq {
        ChatDeletion(
          id = idKP.getNotNull(nonNullRequest),
          communicationType = communicationTypeKP.getNotNull(nonNullRequest)
        )
      }
    }
  }

  fun mapToModel(
    request: ChatSearchReq?,
    fieldName: String = "",
  ): Either<MappingNullError, ChatSearch> {
    val filterKP = ChatSearchReq::filter
    val sortKP = ChatSearchReq::sort

    return buildEitherResult(request, fieldName, filterKP, sortKP)
    { nonNullRequest: ChatSearchReq ->
      val filterEither: Either<MappingNullError, ChatSearch.ChatSearchFilter> =
        mapToModel(filterKP.get(nonNullRequest), filterKP.name)
      val sortEither: Either<MappingNullError, ChatSearch.ChatSearchSort>? = nonNullRequest.sort
        ?.let { mapToModel(sortKP.get(nonNullRequest), sortKP.name) }

      checkInnerFields { listOf(filterEither, sortEither) }
      modelReq {
        ChatSearch(
          filter = filterEither.getOrThrow(),
          sort = sortEither?.getOrThrow(),
          limit = nonNullRequest.limit
        )
      }
    }
  }

  fun mapToModel(
    request: ChatSearchReqAllOfFilter?,
    fieldName: String = "",
  ): Either<MappingNullError, ChatSearch.ChatSearchFilter> {
    val communicationTypeKP = ChatSearchReqAllOfFilter::communicationType

    return buildEitherResult(request, fieldName, communicationTypeKP)
    { nonNullRequest: ChatSearchReqAllOfFilter ->
      modelReq {
        ChatSearch.ChatSearchFilter(
          ids = nonNullRequest.ids,
          externalIds = nonNullRequest.externalIds,
          communicationType = communicationTypeKP.getNotNull(nonNullRequest),
          chatTypes = nonNullRequest.chatTypes,
          latestMessageDate = nonNullRequest.latestMessageDate
            ?.let { ComparableFilterMapperImpl.mapToModel(it) },
        )
      }
    }
  }

  fun mapToModel(
    request: ChatSearchReqAllOfSort?,
    fieldName: String = "",
  ): Either<MappingNullError, ChatSearch.ChatSearchSort> {
    val sortFieldKP = ChatSearchReqAllOfSort::sortField
    val orderKP = ChatSearchReqAllOfSort::order

    return buildEitherResult(request, fieldName, sortFieldKP, orderKP)
    { nonNullRequest: ChatSearchReqAllOfSort ->
      modelReq {
        ChatSearch.ChatSearchSort(
          sortField = ChatSearch.ChatSearchSort.SortField.valueOf(
            sortFieldKP.getNotNull(nonNullRequest).name
          ),
          order = OrderType.valueOf(orderKP.getNotNull(nonNullRequest).name),
        )
      }
    }
  }
}
