package org.jep21s.messenger.core.service.repo.inmemory.message

import org.jep21s.messenger.core.service.repo.common.message.AMessageRepoInitializable
import org.jep21s.messenger.core.service.repo.common.message.MessageDeleteTest

class MessageRepoDeleteInmemoryTest : MessageDeleteTest() {
  override val messageRepo: AMessageRepoInitializable =
    MessageTestRepoProvider.getMessageRepoTest()
}