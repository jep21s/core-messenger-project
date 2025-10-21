package org.jep21s.messenger.core.lib.cor.dsl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlinx.coroutines.test.runTest
import org.jep21s.messenger.core.lib.cor.TestContext
import org.jep21s.messenger.core.lib.cor.handler.chain
import org.jep21s.messenger.core.lib.cor.handler.rootChain
import org.jep21s.messenger.core.lib.cor.handler.worker

class CorDslTest {
  private suspend fun execute(dsl: ICorExecDsl<TestContext>): TestContext {
    val ctx = TestContext()
    val resCtx = dsl.build().exec(ctx)
    return resCtx
  }

  @Test
  fun `handle should execute`() = runTest {
    val chain = rootChain<TestContext> {
      worker {
        handle { copy(history = history + "w1; ") }
      }
    }
    val ctx = execute(chain)
    assertEquals("w1; ", ctx.history)
  }

  @Test
  fun `on should check condition`() = runTest {
    val chain = rootChain<TestContext> {
      worker {
        on { status == TestContext.CorStatuses.ERROR }
        handle { copy(history = history + "w1; ") }
      }
      worker {
        on { status == TestContext.CorStatuses.NONE }
        handle {
          copy(
            history = history + "w2; ",
            status = TestContext.CorStatuses.FAILING,
          )
        }
      }
      worker {
        on { status == TestContext.CorStatuses.FAILING }
        handle { copy(history = history + "w3; ") }
      }
    }
    assertEquals("w2; w3; ", execute(chain).history)
  }

  @Test
  fun `except should execute when exception`() = runTest {
    val chain = rootChain<TestContext> {
      worker {
        handle { throw RuntimeException("some error") }
        except { copy(history = history + it.message) }
      }
    }
    assertEquals("some error", execute(chain).history)
  }

  @Test
  fun `should throw when exception and no except`() = runTest {
    val chain = rootChain<TestContext> {
      worker() {
        title = "throw"
        handle { throw RuntimeException("some error") }
      }
    }
    assertFails {
      execute(chain)
    }
  }

  @Test
  fun `complex chain example`() = runTest {
    val chain = rootChain<TestContext> {
      worker {
        title = "Инициализация статуса"
        description = "При старте обработки цепочки, статус еще не установлен. Проверяем его"

        on { status == TestContext.CorStatuses.NONE }
        handle { copy(status = TestContext.CorStatuses.RUNNING) }
        except { copy(status = TestContext.CorStatuses.ERROR) }
      }

      chain {
        on { status == TestContext.CorStatuses.RUNNING }

        worker {
          title = "Лямбда обработчик"
          description = "Пример использования обработчика в виде лямбды"

          this@chain.worker { title = "xxx" }
          handle { copy(some = some + 4) }
        }
      }
      printResult()
    }.build()

    val ctx = TestContext()
    chain.exec(ctx)
    println("Complete: $ctx")
  }

  private suspend fun ICorChainDsl<TestContext>.printResult() = worker {
    title = "Print example"
    handle {
      println("some = $some")
      this
    }
  }
}