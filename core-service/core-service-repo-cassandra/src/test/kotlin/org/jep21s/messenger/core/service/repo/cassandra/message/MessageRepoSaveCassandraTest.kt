package org.jep21s.messenger.core.service.repo.cassandra.message

import org.jep21s.messenger.core.service.repo.cassandra.test.extention.CassandraTestExtension
import org.jep21s.messenger.core.service.repo.common.message.AMessageRepoInitializable
import org.jep21s.messenger.core.service.repo.common.message.MessageSaveTest
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CassandraTestExtension::class)
class MessageRepoSaveCassandraTest : MessageSaveTest() {
  override val messageRepo: AMessageRepoInitializable =
    MessageTestRepoProvider.getMessageRepoTest()
}
