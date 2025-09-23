package org.jep21s.messenger.core.lib.logging.logback

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.common.ICMLogWrapper

inline fun <reified T> T.logger(): ICMLogWrapper =
  CMLoggerProvider { className: String -> mpLoggerLogback(className) }
    .logger(T::class)