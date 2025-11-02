package org.jep21s.messenger.core.lib.cor.handler

import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.jep21s.messenger.core.lib.cor.TestContext
import org.junit.jupiter.api.Assertions.*

class CorChainTest {
  @Test
  fun `chain should execute workers`() = runTest {
    val createWorker = { title: String ->
      CorWorker<TestContext>(
        title = title,
        blockOn = { status == TestContext.CorStatuses.NONE },
        blockHandle = {
          copy(history = "$history$title; ")
        }
      )
    }
    val chain = CorChain(
      execs = listOf(createWorker("w1"), createWorker("w2")),
      title = "chain",
    )
    val ctx = TestContext()
    val resultContext = chain.exec(ctx)
    assertEquals("w1; w2; ", resultContext.history)
  }
}