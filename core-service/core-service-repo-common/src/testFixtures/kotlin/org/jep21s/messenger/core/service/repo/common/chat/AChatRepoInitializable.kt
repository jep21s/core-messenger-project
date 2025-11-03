package org.jep21s.messenger.core.service.repo.common.chat

import org.jep21s.messenger.core.service.common.model.chat.Chat
import org.jep21s.messenger.core.service.common.repo.IChatRepo

abstract class AChatRepoInitializable(
  private val chatRepo: IChatRepo,
  private val initChats: List<Chat>
) : IChatRepo by chatRepo {
  abstract fun addTestData(chats: List<Chat>)

  abstract fun clearDB()

  fun initDB() = addTestData(initChats)
}