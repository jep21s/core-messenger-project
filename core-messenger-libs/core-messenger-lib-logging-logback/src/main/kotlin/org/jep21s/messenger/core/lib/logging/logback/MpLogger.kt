package org.jep21s.messenger.core.lib.logging.logback

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

import kotlin.reflect.KClass
import org.jep21s.messenger.core.lib.logging.common.ICMLogWrapper

/**
 * Generate internal MpLogContext logger
 *
 * @param logger Logback instance from [LoggerFactory.getLogger()]
 */
fun cmLoggerLogback(logger: Logger): ICMLogWrapper = CMLogWrapperLogback(
    logger = logger,
    loggerId = logger.name,
)

fun cmLoggerLogback(clazz: KClass<*>): ICMLogWrapper = cmLoggerLogback(LoggerFactory.getLogger(clazz.java) as Logger)

@Suppress("unused")
fun cmLoggerLogback(loggerId: String): ICMLogWrapper = cmLoggerLogback(LoggerFactory.getLogger(loggerId) as Logger)
