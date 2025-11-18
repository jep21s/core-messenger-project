package org.jep21s.messenger.core.service.repo.common.message

import org.jep21s.messenger.core.service.common.model.message.Message
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

abstract class AMessageRepoInitializable(
  private val messageRepo: IMessageRepo,
  private val initMessages: List<Message>,
) : IMessageRepo by messageRepo {
  abstract fun addTestData(messages: List<Message>)

  abstract fun clearDB()

  fun initDB() = addTestData(initMessages)
}