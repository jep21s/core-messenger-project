package org.jep21s.messenger.core.lib.cor.dsl

import org.jep21s.messenger.core.lib.cor.ICorExec

@CorDslMarker
interface ICorExecDsl<T> {
  var title: String
  var description: String

  fun on(blockOn: suspend T.() -> Boolean)

  fun except(blockExcept: suspend T.(Throwable) -> T)

  fun build(): ICorExec<T>
}

@CorDslMarker
interface ICorChainDsl<T>: ICorExecDsl<T> {
  fun add(worker: ICorExecDsl<T>)
}

interface ICorWorkerDsl<T>: ICorExecDsl<T> {
  fun handle(blockHandle: suspend T.() -> T)
}