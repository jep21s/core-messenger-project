package org.jep21s.messenger.core.service.common

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.service.common.repo.IChatRepo

object CSCorSettings {
  private var _loggerProvider: CMLoggerProvider? = null
  val loggerProvider: CMLoggerProvider
    get() = _loggerProvider ?: notInit(CSCorSettings::loggerProvider.name)

  private var _chatRepo: IChatRepo? = null
  val chatRepo: IChatRepo
    get() = _chatRepo ?: notInit(CSCorSettings::chatRepo.name)

  fun initialize(
    loggerProvider: CMLoggerProvider? = null,
    chatRepo: IChatRepo? = null,
  ) {
    loggerProvider?.let {
      if (_loggerProvider != null) alreadyInit(CSCorSettings::loggerProvider.name)
      _loggerProvider = loggerProvider
      successInit(CSCorSettings::loggerProvider.name)
    }
    chatRepo?.let {
      if (_chatRepo != null) alreadyInit(CSCorSettings::chatRepo.name)
      _chatRepo = chatRepo
      successInit(CSCorSettings::chatRepo.name)
    }
  }
}

private fun notInit(fieldName: String): Nothing =
  error("$fieldName not initialized")

private fun alreadyInit(fieldName: String): Nothing =
  error("$fieldName already initialized")

private fun successInit(fieldName: String) =
  println("----- $fieldName success initialized -----")