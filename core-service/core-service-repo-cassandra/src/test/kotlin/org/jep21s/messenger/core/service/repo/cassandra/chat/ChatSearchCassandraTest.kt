package org.jep21s.messenger.core.service.repo.cassandra.chat

import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatSearchTest

class ChatSearchCassandraTest : ChatSearchTest() {
  override val chatRepo: AChatRepoInitializable =
    ChatTestRepoProvider.getChatRepoTest()
}