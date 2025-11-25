package org.jep21s.messenger.core.service.repo.common.message

import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

abstract class AMessageRepoInitializable(
  protected val messageRepo: IMessageRepo,
  private val initMessages: List<Message>,
) : IMessageRepo by messageRepo {
  abstract suspend fun addTestData(messages: List<Message>)

  abstract suspend fun clearDB()

  suspend fun initDB() = addTestData(initMessages)
}