package org.jep21s.messenger.core.service.api.v1.mapper

import arrow.core.Either
import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import org.jep21s.messenger.core.service.api.v1.mapper.helper.MappingNullError
import org.jep21s.messenger.core.service.api.v1.mapper.helper.buildEitherResult
import org.jep21s.messenger.core.service.api.v1.mapper.helper.getNotNull
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateReq
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateRespAllOfContent
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdated
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdation

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface MessageStatusMapper {
  fun mapToUpdateResponse(
    model: MessageStatusUpdated,
  ): MessageStatusUpdateRespAllOfContent

  fun mapToModel(
    request: MessageStatusUpdateReq?,
    fieldName: String = "",
  ): Either<MappingNullError, MessageStatusUpdation> {
    val idsKP = MessageStatusUpdateReq::ids
    val chatIdKP = MessageStatusUpdateReq::chatId
    val communicationTypeKP = MessageStatusUpdateReq::communicationType
    val newStatusKP = MessageStatusUpdateReq::newStatus

    return buildEitherResult(
      request, fieldName,
      idsKP, chatIdKP, communicationTypeKP, newStatusKP
    ) { nonNullRequest: MessageStatusUpdateReq ->
      modelReq {
        MessageStatusUpdation(
          ids = idsKP.getNotNull(nonNullRequest),
          chatId = chatIdKP.getNotNull(nonNullRequest),
          communicationType = communicationTypeKP.getNotNull(nonNullRequest),
          newStatus = newStatusKP.getNotNull(nonNullRequest)
        )
      }
    }
  }
}