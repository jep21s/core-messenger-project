package org.jep21s.messenger.core.service.biz.processor

import kotlin.reflect.KType
import kotlin.reflect.full.createType
import org.jep21s.messenger.core.service.common.context.CSContext

abstract class CSProcessor<MReq, MResp> {
  val contextType: KType = this::class
    .supertypes
    .first { it.classifier == CSProcessor::class }
    .arguments
    .let { arguments ->
      CSContext::class.createType(
        listOf(arguments[0], arguments[1])
      )
    }

  abstract suspend fun exec(
    context: CSContext<MReq, MResp>,
  ): CSContext<MReq, MResp>
}
