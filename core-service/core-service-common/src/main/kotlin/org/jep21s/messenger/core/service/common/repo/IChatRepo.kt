package org.jep21s.messenger.core.service.common.repo

import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.model.chat.ChatCreation
import org.jep21s.messenger.core.service.common.model.chat.ChatDeletion
import org.jep21s.messenger.core.service.common.model.chat.ChatSearch

interface IChatRepo {
  val defaultPaginationLimit: Int
    get() = 10

  val maxPaginationLimit: Int
    get() = 50

  suspend fun save(chatCreation: ChatCreation): Chat

  suspend fun delete(chatDeletion: ChatDeletion): Chat?

  suspend fun search(chatSearch: ChatSearch): List<Chat>
}