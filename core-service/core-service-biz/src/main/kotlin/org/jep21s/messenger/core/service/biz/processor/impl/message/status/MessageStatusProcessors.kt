package org.jep21s.messenger.core.service.biz.processor.impl.message.status

import org.jep21s.messenger.core.service.biz.extention.putModelResp
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdated
import org.jep21s.messenger.core.service.common.model.message.status.MessageStatusUpdation

object CSMessageStatusUpdateProcessor :
  CSProcessor<MessageStatusUpdation, MessageStatusUpdated?>() {
  override suspend fun exec(
    context: CSContext<MessageStatusUpdation, MessageStatusUpdated?>,
  ): CSContext<MessageStatusUpdation, MessageStatusUpdated?> {
    val request: MessageStatusUpdation = context.modelReq
    return context.putModelResp(
      MessageStatusUpdated(
        ids = request.ids,
        communicationType = request.communicationType,
        newStatus = request.newStatus
      )
    )
  }
}