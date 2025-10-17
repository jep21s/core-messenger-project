package org.jep21s.messenger.core.service.biz.processor.impl.message

import org.jep21s.messenger.core.service.biz.extention.putModelResp
import org.jep21s.messenger.core.service.biz.processor.CSProcessor
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion

object CSMessageDeleteProcessor :
  CSProcessor<MessageDeletion, MessageDeleted?>() {
  override suspend fun exec(
    context: CSContext<MessageDeletion, MessageDeleted?>,
  ): CSContext<MessageDeletion, MessageDeleted?> {
    val request: MessageDeletion = context.modelReq
    return context.putModelResp(
      MessageDeleted(
        ids = request.ids,
        communicationType = request.communicationType,
      )
    )
  }
}