package org.jep21s.messenger.core.service.common

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider

object CSCorSettings {
  private var _loggerProvider: CMLoggerProvider? = null
  val loggerProvider: CMLoggerProvider
    get() = _loggerProvider ?: notInit(CSCorSettings::loggerProvider.name)

  fun initialize(
    loggerProvider: CMLoggerProvider? = null,
  ) {
    loggerProvider?.let {
      if (_loggerProvider != null) alreadyInit(CSCorSettings::loggerProvider.name)
      _loggerProvider = loggerProvider
      successInit(CSCorSettings::loggerProvider.name)
    }
  }
}

private fun notInit(fieldName: String): Nothing =
  error("$fieldName not initialized")

private fun alreadyInit(fieldName: String): Nothing =
  error("$fieldName already initialized")

private fun successInit(fieldName: String) =
  println("----- $fieldName success initialized -----")