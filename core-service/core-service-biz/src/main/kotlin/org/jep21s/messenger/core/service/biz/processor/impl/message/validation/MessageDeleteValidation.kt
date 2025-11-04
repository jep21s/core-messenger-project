package org.jep21s.messenger.core.service.biz.processor.impl.message.validation

import java.util.UUID
import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.handler.worker
import org.jep21s.messenger.core.service.biz.cor.fail
import org.jep21s.messenger.core.service.biz.cor.onRunning
import org.jep21s.messenger.core.service.common.CSCorSettings
import org.jep21s.messenger.core.service.common.context.CSContext
import org.jep21s.messenger.core.service.common.context.CSError
import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.chatSearch
import org.jep21s.messenger.core.service.common.model.message.MessageDeleted
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion
import org.jep21s.messenger.core.service.common.model.message.messageSearch
import org.jep21s.messenger.core.service.common.repo.IChatRepo
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

suspend fun ICorChainDsl<CSContext<MessageDeletion, MessageDeleted?>>.existChat() = worker {
  title = "Проверка существует ли чат, при удалении сообщения"
  onRunning {
    val chatRepo: IChatRepo = CSCorSettings.chatRepo(workMode)
    val chat: Chat? = chatRepo.search(chatSearch {
      filter {
        id { modelReq.chatId }
        communicationType { modelReq.communicationType }
      }
      limit = 1
    }
    ).firstOrNull()

    return@onRunning chat == null
  }
  handle {
    fail(
      CSError(
        code = "message-validation-delete",
        group = "message-validation",
        field = MessageDeletion::chatId.name,
        message = "Chat not exists. ChatId [${modelReq.chatId}], " +
            "communicationType [${modelReq.communicationType}]"
      )
    )

  }

}

suspend fun ICorChainDsl<CSContext<MessageDeletion, MessageDeleted?>>.messageIdsInChat() = worker {
  title = "Проверка что все удаляемые сообщения относятся к переданному чату"
  onRunning {
    val messageRepo: IMessageRepo = CSCorSettings.messageRepo(workMode)
    val messageIds: List<UUID> = messageRepo.search(messageSearch {
      chatFilter {
        id { modelReq.chatId }
        communicationType { modelReq.communicationType }
      }
      messageFilter {
        ids { modelReq.ids.toList() }
      }
    }).map { it.id }
    val invalidMessageIds = messageIds - modelReq.ids
    if (invalidMessageIds.isEmpty()) return@onRunning false
    this[InvalidChatMessageIds::class] = InvalidChatMessageIds(invalidMessageIds)
    return@onRunning true
  }
  handle {
    val invalidMessageIds: List<UUID> = this.pop(InvalidChatMessageIds::class)
      ?.invalidMessageIds
      .orEmpty()
    fail(
      CSError(
        code = "message-validation-delete",
        group = "message-validation",
        field = MessageDeletion::ids.name,
        message = "MessageIds doesn't in chat. ChatId [${modelReq.chatId}], " +
            "messageIds [$invalidMessageIds]"
      )
    )
  }
}

private data class InvalidChatMessageIds(
  val invalidMessageIds: List<UUID>,
)
