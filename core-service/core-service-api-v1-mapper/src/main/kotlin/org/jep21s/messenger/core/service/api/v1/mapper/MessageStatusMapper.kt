package org.jep21s.messenger.core.service.api.v1.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konvert
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.Mapping
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateReq
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateResp
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
  fun mapToModel(request: MessageStatusUpdateReq): MessageStatusUpdation

  @Konvert(
    mappings = [
      Mapping(
        target = "result",
        constant = "org.jep21s.messenger.core.service.api.v1.models.ResponseResult.SUCCESS"
      ),
      Mapping(target = "errors", ignore = true),
      Mapping(target = "content", expression = "mapToUpdateContentResponse(model)"),
    ]
  )
  fun mapToResponse(model: MessageStatusUpdated): MessageStatusUpdateResp

  fun mapToUpdateContentResponse(
    model: MessageStatusUpdated,
  ): MessageStatusUpdateRespAllOfContent
}