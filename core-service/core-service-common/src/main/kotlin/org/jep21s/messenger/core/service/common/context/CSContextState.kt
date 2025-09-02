package org.jep21s.messenger.core.service.common.context

sealed class CSContextState {
  abstract val type: CSContextStateType

  class None : CSContextState() {
    override val type = CSContextStateType.NONE
  }

  class Running : CSContextState() {
    override val type = CSContextStateType.RUNNING
  }

  class Finishing : CSContextState() {
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
//TODO  val level: CMLogLevel = CMLogLevel.ERROR,
  val exception: Throwable? = null,
)