package org.jep21s.messenger.core.service.api.v1.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateReq
import org.jep21s.messenger.core.service.api.v1.models.MessageStatusUpdateResp
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

  fun mapToResponse(model: MessageStatusUpdated ): MessageStatusUpdateResp
}