package org.jep21s.messenger.core.service.common.repo

import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.model.message.MessageCreation
import org.jep21s.messenger.core.service.common.model.message.MessageDeletion
import org.jep21s.messenger.core.service.common.model.message.MessageSearch

interface IMessageRepo {
  val defaultPaginationLimit: Int
    get() = 30

  val maxPaginationLimit: Int
    get() = 100

  suspend fun save(messageCreation: MessageCreation): Message

  suspend fun delete(messageDeletion: MessageDeletion)

  suspend fun search(messageSearch: MessageSearch): List<Message>
}