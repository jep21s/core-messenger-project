package org.jep21s.messenger.core.lib.logging.common

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Инициализирует выбранный логер
 *
 * ```kotlin
 * // Обычно логер вызывается вот так
 * val logger = LoggerFactory.getLogger(this::class.java)
 * // Мы создаем экземпляр логер-провайдера вот так
 * val loggerProvider = MkpLoggerProvider { clazz -> mpLoggerLogback(clazz) }
 *
 * // В дальнейшем будем использовать этот экземпляр вот так:
 * val logger = loggerProvider.logger(this::class)
 * logger.info("My log")
 * ```
 */
class CMLoggerProvider(
    private val provider: (String) -> ICMLogWrapper = { ICMLogWrapper.DEFAULT }
) {
    /**
     * Инициализирует и возвращает экземпляр логера
     */
    fun logger(loggerId: String): ICMLogWrapper = provider(loggerId)

    /**
     * Инициализирует и возвращает экземпляр логера
     */
    fun logger(clazz: KClass<*>): ICMLogWrapper = provider(clazz.qualifiedName ?: clazz.simpleName ?: "(unknown)")

    /**
     * Инициализирует и возвращает экземпляр логера
     */
    fun logger(function: KFunction<*>): ICMLogWrapper = provider(function.name)
}