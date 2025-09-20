package org.jep21s.messenger.core.service.app.web.extention

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.common.ICMLogWrapper
import org.jep21s.messenger.core.lib.logging.logback.mpLoggerLogback

inline fun <reified T> T.logger(): ICMLogWrapper =
  CMLoggerProvider { className: String -> mpLoggerLogback(className) }
    .logger(T::class)