package org.jep21s.messenger.core.service.common

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.service.common.repo.IChatRepo
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

object CSCorSettings {
  private var _loggerProvider: CMLoggerProvider? = null
  private val loggerProvider: CMLoggerProvider
    get() = _loggerProvider ?: notInit(CSCorSettings::loggerProvider.name)

  private var _chatRepoMock: IChatRepo? = null
  private val chatRepoMock: IChatRepo
    get() = _chatRepoMock ?: notInit(CSCorSettings::chatRepoMock.name)

  private var _messageRepoMock: IMessageRepo? = null
  private val messageRepoMock: IMessageRepo
    get() = _messageRepoMock ?: notInit(CSCorSettings::messageRepoMock.name)

  fun initialize(
    loggerProvider: CMLoggerProvider? = null,
    chatRepo: IChatRepo? = null,
    messageRepo: IMessageRepo? = null,
  ) {
    loggerProvider?.let {
      if (_loggerProvider != null) alreadyInit(CSCorSettings::loggerProvider.name)
      _loggerProvider = loggerProvider
      successInit(CSCorSettings::loggerProvider.name)
    }
    chatRepo?.let {
      if (_chatRepoMock != null) alreadyInit(CSCorSettings::chatRepoMock.name)
      _chatRepoMock = chatRepo
      successInit(CSCorSettings::chatRepoMock.name)
    }
    messageRepo?.let {
      if (_messageRepoMock != null) alreadyInit(CSCorSettings::messageRepoMock.name)
      successInit(CSCorSettings::messageRepoMock.name)
    }
  }
}

private fun notInit(fieldName: String): Nothing =
  error("$fieldName not initialized")

private fun alreadyInit(fieldName: String): Nothing =
  error("$fieldName already initialized")

private fun successInit(fieldName: String) =
  println("----- $fieldName success initialized -----")