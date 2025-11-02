package org.jep21s.messenger.core.service.common.context

import org.jep21s.messenger.core.lib.logging.common.LogLevel

sealed class CSContextState {
  abstract val type: CSContextStateType

  object None : CSContextState() {
    override val type = CSContextStateType.NONE
  }

  object Running : CSContextState() {
    override val type = CSContextStateType.RUNNING
  }

  object Finishing : CSContextState() {
    override val type = CSContextStateType.FINISHING
  }

  data class Failing(
    val errors: List<CSError>,
  ) : CSContextState() {
    override val type = CSContextStateType.FAILING
  }
}

enum class CSContextStateType {
  NONE,
  RUNNING,
  FAILING,
  FINISHING,
}

data class CSError(
  val code: String,
  val group: String,
  val field: String,
  val message: String,
  val level: LogLevel = LogLevel.ERROR,
  val exception: Throwable? = null,
)

fun CSContextState.isNone(): Boolean = this is CSContextState.None
fun CSContextState.isRunning(): Boolean = this is CSContextState.Running
fun CSContextState.isFinishing(): Boolean = this is CSContextState.Finishing
fun CSContextState.isFailing(): Boolean = this is CSContextState.Failing
