package org.jep21s.messenger.core.service.repo.inmemory.message

import org.jep21s.messenger.core.service.repo.common.message.AMessageRepoInitializable
import org.jep21s.messenger.core.service.repo.common.message.MessageSaveTest

class MessageRepoSaveInmemoryTest : MessageSaveTest() {
  override val messageRepo: AMessageRepoInitializable =
    MessageTestRepoProvider.getMessageRepoTest()
}