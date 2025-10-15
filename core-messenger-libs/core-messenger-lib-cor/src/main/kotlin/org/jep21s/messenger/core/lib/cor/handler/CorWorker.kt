package org.jep21s.messenger.core.lib.cor.handler

import org.jep21s.messenger.core.lib.cor.ICorExec
import org.jep21s.messenger.core.lib.cor.dsl.CorDslMarker
import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.dsl.ICorWorkerDsl

class CorWorker<T>(
  title: String,
  description: String = "",
  blockOn: suspend T.() -> Boolean = { true },
  blockExcept: suspend T.(Throwable) -> T = { throw it },
  private val blockHandle: suspend T.() -> T = { this },
) : AbstractCorExec<T>(title, description, blockOn, blockExcept) {
  override suspend fun handle(context: T): T = context.blockHandle()
}

@CorDslMarker
class CorWorkerDsl<T> : AbstractCorExecDsl<T>(), ICorWorkerDsl<T> {
  private var blockHandle: suspend T.() -> T = { this }

  override fun handle(blockHandle: suspend T.() -> T) {
    this.blockHandle = blockHandle
  }

  override fun build(): ICorExec<T> = CorWorker<T>(
    title = this.title,
    description = this.description,
    blockOn = this.blockOn,
    blockExcept = this.blockExcept,
    blockHandle = this.blockHandle
  )
}

fun <T> ICorChainDsl<T>.worker(block: ICorWorkerDsl<T>.() -> Unit) {
  add(CorWorkerDsl<T>().apply(block))
}