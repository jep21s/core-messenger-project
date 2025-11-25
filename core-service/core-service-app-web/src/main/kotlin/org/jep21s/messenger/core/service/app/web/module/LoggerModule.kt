package org.jep21s.messenger.core.service.app.web.module

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.logback.cmLoggerLogback
import org.jep21s.messenger.core.service.common.CSCorSettings

suspend fun initializeLoggerProvider() {
  CSCorSettings.initialize(
    loggerProvider = CMLoggerProvider { clazz -> cmLoggerLogback(clazz) }
  )
}