package org.jep21s.messenger.core.lib.cor.handler

import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.jep21s.messenger.core.lib.cor.TestContext
import org.junit.jupiter.api.Assertions.*

class CorWorkerTest {
  @Test
  fun `worker should execute handle`() = runTest {
    val worker = CorWorker<TestContext>(
      title = "w1",
      blockHandle = {
        copy(history = history + "w1; ")
      }
    )
    val ctx = TestContext()
    val resultCtx = worker.exec(ctx)
    assertEquals("w1; ", resultCtx.history)
  }

  @Test
  fun `worker should not execute when off`() = runTest {
    val worker = CorWorker<TestContext>(
      title = "w1",
      blockOn = { status == TestContext.CorStatuses.ERROR },
      blockHandle = {
        copy(history = history + "w1; ")
      }
    )
    val ctx = TestContext()
    val resultCtx = worker.exec(ctx)
    assertEquals("", resultCtx.history)
  }

  @Test
  fun `worker should handle exception`() = runTest {
    val worker = CorWorker<TestContext>(
      title = "w1",
      blockHandle = { throw RuntimeException("some error") },
      blockExcept = { e ->
        copy(history = history + e.message)
      }
    )
    val ctx = TestContext()
    val resultCtx = worker.exec(ctx)
    assertEquals("some error", resultCtx.history)
  }
}
