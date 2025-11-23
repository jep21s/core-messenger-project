package org.jep21s.messenger.core.service.repo.inmemory.chat

import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatSearchTest

class ChatRepoSearchInmemoryTest: ChatSearchTest() {
  override val chatRepo: AChatRepoInitializable =
    ChatTestRepoProvider.getChatRepoTest()
}