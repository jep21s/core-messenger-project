package org.jep21s.messenger.core.service.repo.inmemory.message

import org.jep21s.messenger.core.service.repo.common.message.AMessageRepoInitializable
import org.jep21s.messenger.core.service.repo.common.message.MessageSearchTest

class MessageRepoSearchInmemoryTest : MessageSearchTest() {
  override val messageRepo: AMessageRepoInitializable =
    MessageTestRepoProvider.getMessageRepoTest()
}
