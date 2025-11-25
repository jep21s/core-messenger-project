package org.jep21s.messenger.core.service.repo.cassandra.chat

import org.jep21s.messenger.core.service.repo.cassandra.test.extention.CassandraTestExtension
import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatSearchTest
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CassandraTestExtension::class)
class ChatSearchCassandraTest : ChatSearchTest() {
  override val chatRepo: AChatRepoInitializable =
    ChatTestRepoProvider.getChatRepoTest()
}