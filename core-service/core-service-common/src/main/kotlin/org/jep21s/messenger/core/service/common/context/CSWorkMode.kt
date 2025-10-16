package org.jep21s.messenger.core.service.common.context

sealed class CSWorkMode {
  abstract val type: CSWorkModeType

  object Prod : CSWorkMode() {
    override val type = CSWorkModeType.PROD
  }

  object Test : CSWorkMode() {
    override val type = CSWorkModeType.TEST
  }

  data class Stub(
    val stubCase: CSStub,
  ) : CSWorkMode() {
    override val type = CSWorkModeType.STUB
  }
}

enum class CSWorkModeType {
  PROD,
  TEST,
  STUB,
}

enum class CSStub {
  SUCCESS,
  NOT_FOUND,
  DB_ERROR,
}

fun CSWorkMode.isStubSuccess() = this is CSWorkMode.Stub && this.stubCase == CSStub.SUCCESS
fun CSWorkMode.isStubNotFound() = this is CSWorkMode.Stub && this.stubCase == CSStub.NOT_FOUND
fun CSWorkMode.isStubDbError() = this is CSWorkMode.Stub && this.stubCase == CSStub.DB_ERROR
