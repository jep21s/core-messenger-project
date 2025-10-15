package org.jep21s.messenger.core.lib.cor.handler

import org.jep21s.messenger.core.lib.cor.ICorExec
import org.jep21s.messenger.core.lib.cor.dsl.CorDslMarker
import org.jep21s.messenger.core.lib.cor.dsl.ICorChainDsl
import org.jep21s.messenger.core.lib.cor.dsl.ICorExecDsl

class CorChain<T>(
  title: String,
  description: String = "",
  blockOn: suspend T.() -> Boolean = { true },
  blockExcept: suspend T.(Throwable) -> T = { throw it },
  private val execs: List<ICorExec<T>>,
) : AbstractCorExec<T>(title, description, blockOn, blockExcept) {
  override suspend fun handle(context: T): T =
    execs.fold(context) { ctx: T, executor: ICorExec<T> ->
      executor.exec(ctx)
    }
}

@CorDslMarker
class CorChainDsl<T> : AbstractCorExecDsl<T>(), ICorChainDsl<T> {
  private val workers: MutableList<ICorExecDsl<T>> = mutableListOf()

  override fun add(worker: ICorExecDsl<T>) {
    workers.add(worker)
  }

  override fun build(): ICorExec<T> = CorChain(
    title = this.title,
    description = this.description,
    blockOn = this.blockOn,
    blockExcept = this.blockExcept,
    execs = this.workers.map { it.build() }
  )
}

fun <T> rootChain(block: ICorChainDsl<T>.() -> Unit): ICorChainDsl<T> =
  CorChainDsl<T>().apply(block)

fun <T> ICorChainDsl<T>.chain(block: ICorChainDsl<T>.() -> Unit) {
  add(CorChainDsl<T>().apply(block))
}
