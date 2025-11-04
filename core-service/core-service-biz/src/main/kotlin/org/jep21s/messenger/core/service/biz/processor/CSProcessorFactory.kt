package org.jep21s.messenger.core.service.biz.processor

import kotlin.reflect.KType
import kotlin.reflect.typeOf
import org.jep21s.messenger.core.service.biz.processor.impl.chat.CSChatCreateProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.chat.CSChatDeleteProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.chat.CSChatSearchProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.CSMessageCreateProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.CSMessageDeleteProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.CSMessageSearchProcessor
import org.jep21s.messenger.core.service.biz.processor.impl.message.status.CSMessageStatusUpdateProcessor
import org.jep21s.messenger.core.service.common.context.CSContext

object CSProcessorFactory {
  val processorMap: Map<KType, CSProcessor<out Any, out Any?>> = listOf(
    //chat
    CSChatCreateProcessor,
    CSChatDeleteProcessor,
    CSChatSearchProcessor,

    //message
    CSMessageCreateProcessor,
    CSMessageSearchProcessor,
    CSMessageDeleteProcessor,

    //message status
    CSMessageStatusUpdateProcessor,
  ).associateBy { it.contextType }

  @Suppress("UNCHECKED_CAST")
  suspend fun <MReq, MResp> getCSProcessor(
    contextKType: KType,
  ): CSProcessor<MReq, MResp> {
    val processor = processorMap[contextKType]
      ?: error("Can't find processor for $contextKType")
    return processor as CSProcessor<MReq, MResp>
  }
}

suspend inline fun <reified MReq, reified MResp>
    CSProcessorFactory.getCSProcessor(
  context: CSContext<MReq, MResp>,
): CSProcessor<MReq, MResp> {
  return getCSProcessor( context.kType())
}

inline fun <reified T> T.kType(): KType {
  return typeOf<T>()
}
