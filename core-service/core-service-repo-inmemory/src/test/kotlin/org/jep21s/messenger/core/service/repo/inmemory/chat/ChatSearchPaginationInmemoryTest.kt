package org.jep21s.messenger.core.service.repo.inmemory.chat

import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatSearchPaginationTest

class ChatSearchPaginationInmemoryTest : ChatSearchPaginationTest() {
  override val chatRepo: AChatRepoInitializable =
    ChatTestRepoProvider.getChatRepoTest()
}