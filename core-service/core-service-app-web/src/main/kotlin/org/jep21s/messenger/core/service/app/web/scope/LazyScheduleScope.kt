package org.jep21s.messenger.core.service.app.web.scope

import io.ktor.server.application.Application
import java.io.Serializable
import kotlinx.coroutines.CoroutineScope

class LazyScheduleScope(
  initializer: Application.() -> CoroutineScope,
  lock: Any? = null,
) : Serializable {
  private var initializer: (Application.() -> CoroutineScope)? = initializer

  @Volatile
  private var _value: Any? = UNINITIALIZED_VALUE

  private val lock = lock ?: this

  fun value(app: Application): CoroutineScope {
    val _v1 = _value
    if (_v1 !== UNINITIALIZED_VALUE) {
      @Suppress("UNCHECKED_CAST")
      return _v1 as CoroutineScope
    }

    return synchronized(lock) {
      val _v2 = _value
      if (_v2 !== UNINITIALIZED_VALUE) {
        @Suppress("UNCHECKED_CAST") (_v2 as CoroutineScope)
      } else {
        val typedValue = initializer!!(app)
        _value = typedValue
        initializer = null
        typedValue
      }
    }
  }

  fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

  override fun toString(): String =
    if (isInitialized()) _value.toString() else "Lazy value not initialized yet."

  private object UNINITIALIZED_VALUE
}

fun lazyScheduleScope(
  initializer: Application.() -> CoroutineScope,
) = LazyScheduleScope(initializer)