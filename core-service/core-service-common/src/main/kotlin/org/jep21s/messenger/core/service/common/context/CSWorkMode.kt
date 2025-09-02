package org.jep21s.messenger.core.service.common.context

sealed class CSWorkMode {
  abstract val type: CSWorkModeType

  class Prod : CSWorkMode() {
    override val type = CSWorkModeType.PROD
  }

  class Test : CSWorkMode() {
    override val type = CSWorkModeType.TEST
  }

  data class Stub(
    val stubCase: CSStubs,
  ) : CSWorkMode() {
    override val type = CSWorkModeType.STUB
  }
}

enum class CSWorkModeType {
  PROD,
  TEST,
  STUB,
}

enum class CSStubs {
  NONE,
  SUCCESS,
  NOT_FOUND,
  DB_ERROR,
}
