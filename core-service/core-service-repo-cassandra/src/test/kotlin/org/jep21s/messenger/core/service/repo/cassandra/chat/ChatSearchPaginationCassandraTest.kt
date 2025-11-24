package org.jep21s.messenger.core.service.repo.cassandra.chat

import org.jep21s.messenger.core.service.repo.cassandra.test.extention.CassandraTestExtension
import org.jep21s.messenger.core.service.repo.common.chat.AChatRepoInitializable
import org.jep21s.messenger.core.service.repo.common.chat.ChatSearchPaginationTest
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CassandraTestExtension::class)
class ChatSearchPaginationCassandraTest : ChatSearchPaginationTest() {
  override val chatRepo: AChatRepoInitializable =
    ChatTestRepoProvider.getChatRepoTest()
}