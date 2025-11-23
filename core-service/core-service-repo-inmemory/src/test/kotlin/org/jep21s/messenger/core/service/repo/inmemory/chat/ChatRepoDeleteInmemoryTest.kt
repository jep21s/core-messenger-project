package org.jep21s.messenger.core.service.repo.inmemory.chat

import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatDeleteTest

class ChatRepoDeleteInmemoryTest : ChatDeleteTest() {
  override val chatRepo: AChatRepoInitializable =
    ChatTestRepoProvider.getChatRepoTest()
}