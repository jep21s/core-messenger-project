package org.jep21s.messenger.core.service.common.mapper

import org.jep21s.messenger.core.service.common.context.CSError

fun Throwable.asCSError(
  code: String = "unknown",
  group: String = "exceptions",
  message: String = this.message ?: "",
) = CSError(
  code = code,
  group = group,
  field = "",
  message = message,
  exception = this,
)