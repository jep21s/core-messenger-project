package org.jep21s.messenger.core.lib.cor.handler

import org.jep21s.messenger.core.lib.cor.ICorExec
import org.jep21s.messenger.core.lib.cor.dsl.ICorExecDsl

abstract class AbstractCorExec<T>(
  override val title: String,
  override val description: String = "",
  private val blockOn: suspend T.() -> Boolean = { true },
  private val blockExcept: suspend T.(Throwable) -> T = { throw it },
) : ICorExec<T> {
  protected abstract suspend fun handle(context: T): T

  private suspend fun on(context: T): Boolean = context.blockOn()

  private suspend fun except(context: T, ex: Throwable): T =
    context.blockExcept(ex)

  override suspend fun exec(context: T): T = runCatching {
    if (!on(context)) return context
    handle(context)
  }.getOrElse { except(context, it) }
}

abstract class AbstractCorExecDsl<T> : ICorExecDsl<T> {
  override var title: String = ""

  override var description: String = ""

  protected var blockOn: suspend T.() -> Boolean = { true }

  protected var blockExcept: suspend T.(Throwable) -> T = { throw it }

  override suspend fun on(blockOn: suspend T.() -> Boolean) {
    this.blockOn = blockOn
  }

  override suspend fun except(blockExcept: suspend T.(Throwable) -> T) {
    this.blockExcept = blockExcept
  }
}