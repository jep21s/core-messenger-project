package org.jep21s.messenger.core.lib.cor.dsl

import org.jep21s.messenger.core.lib.cor.ICorExec

@CorDslMarker
interface ICorExecDsl<T> {
  var title: String
  var description: String

  suspend fun on(blockOn: suspend T.() -> Boolean)

  suspend fun except(blockExcept: suspend T.(Throwable) -> T)

  suspend fun build(): ICorExec<T>
}

@CorDslMarker
interface ICorChainDsl<T>: ICorExecDsl<T> {
  suspend fun add(worker: ICorExecDsl<T>)
}

interface ICorWorkerDsl<T>: ICorExecDsl<T> {
  suspend fun handle(blockHandle: suspend T.() -> T)
}